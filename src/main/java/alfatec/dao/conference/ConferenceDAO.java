package alfatec.dao.conference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;

import alfatec.dao.utils.Logging;
import alfatec.dao.utils.TableUtility;
import alfatec.model.conference.Conference;
import database.DatabaseTable;
import database.Getter;
import javafx.collections.ObservableList;
import util.BooleanUtil;
import util.Folder;

/**
 * DAO for table "conference".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class ConferenceDAO {

	private static ConferenceDAO instance;

	public static ConferenceDAO getInstance() {
		if (instance == null)
			synchronized (ConferenceDAO.class) {
				if (instance == null)
					instance = new ConferenceDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<Conference> getConference;

	private ConferenceDAO() {
		table = new TableUtility(new DatabaseTable("conference", "conference_id", new String[] { "conference_title",
				"conference_email", "email_password", "conference_bcc", "note", "report", "field_id", "realized" }));
		getConference = (ResultSet rs) -> {
			Conference conference = new Conference();
			try {
				conference.setConferenceID(rs.getInt(table.getTable().getPrimaryKey()));
				conference.setConferenceTitle(rs.getString(table.getTable().getColumnName(1)));
				conference.setConferenceEmail(rs.getString(table.getTable().getColumnName(2)));
				conference.readPasswordFromDB(rs.getString(table.getTable().getColumnName(3)));
				conference.setConferenceBcc(rs.getString(table.getTable().getColumnName(4)));
				conference.setNote(rs.getString(table.getTable().getColumnName(5)));
				Path path = Paths.get(Folder.getConferenceDirectory().getAbsolutePath() + File.separator
						+ conference.getConferenceTitle());
				if (Files.notExists(path)) {
					File file = new File(Folder.getConferenceDirectory().getAbsolutePath() + File.separator
							+ conference.getConferenceTitle());
					InputStream blob = rs.getBinaryStream(table.getTable().getColumnName(6));
					if (blob != null)
						FileUtils.copyInputStreamToFile(blob, file);
					conference.setReport(file);
					file.deleteOnExit();
				}
				conference.setFieldID(rs.getInt(table.getTable().getColumnName(7)));
				conference.setRealized(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(8))));
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			}
			return conference;
		};
	}

	public void deleteConference(Conference conference) {
		String past = conference.getConferenceTitle();
		table.delete(conference.getConferenceID());
		Logging.getInstance().change("delete", "Delete conference\n\t" + past);
	}

	public void endConference(Conference conference) {
		Logging.getInstance().change("end",
				"Marked conference\n\t" + conference.getConferenceTitle() + "\nas realized.");
		conference.setRealized(1);
		table.update(conference.getConferenceID(), 8, 1);
	}

	/**
	 * Search table by primary key - id
	 */
	public Conference findBy(int id) {
		return table.findBy(id, getConference);
	}

	/**
	 * Full text search in natural language by title
	 */
	public Conference findBy(String title) {
		return table.findByFulltext(title, getConference, 1);
	}

	/**
	 * @return all conferences
	 */
	public ObservableList<Conference> getAll() {
		return table.getAll(getConference);
	}

	/**
	 * @param fieldID
	 * @return all conferences tied to specified field
	 */
	public ObservableList<Conference> getAllForField(int fieldID) {
		return table.findBy(fieldID, 7, getConference);
	}

	/**
	 * @param field name of the field
	 * @return all conferences tied to specified field
	 */
	public ObservableList<Conference> getAllForField(String field) {
		return getAllForField(FieldDAO.getInstance().getField(field).getFieldID());
	}

	/**
	 * @return all realized - finished conferences
	 */
	public ObservableList<Conference> getAllRealized() {
		return table.findBy(1, 8, getConference);
	}

	/**
	 * Search table by primary key - id
	 */
	public Conference getConference(int id) {
		return table.findBy(id, getConference);
	}

	/**
	 * @return current conference
	 */
	public Conference getCurrentConference() {
		try {
			return table.findBy(0, 8, getConference).get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Conference startConference(String title, String field, String email, String password, String bcc,
			String note, String reportPath) {
		Conference conference;
		if (reportPath != null)
			if (field != null)
				conference = table.create(reportPath, 6, new String[] { title, email, password, bcc, note },
						new int[] { FieldDAO.getInstance().getField(field).getFieldID(), 0 }, new long[] {},
						getConference);
			else
				conference = table.create(reportPath, 6, new String[] { title, email, password, bcc, note, null },
						new int[] { 0 }, new long[] {}, getConference);
		else {
			if (field != null)
				conference = table.create(new String[] { title, email, password, bcc, note, null },
						new int[] { FieldDAO.getInstance().getField(field).getFieldID(), 0 }, new long[] {},
						getConference);
			else
				conference = table.create(new String[] { title, email, password, bcc, note, null, null },
						new int[] { 0 }, new long[] {}, getConference);
		}
		Logging.getInstance().change("create", "Start conference\n\t" + title);
		return conference;
	}

	public void updateConferenceEmail(Conference conference, String email) {
		String past = conference.getConferenceEmail();
		table.update(conference.getConferenceID(), 2, email);
		conference.setConferenceEmail(email);
		Logging.getInstance().change("update", "Update conference email from\n\t" + past + "\nto\n\t" + email);
	}

	public void updateConferenceField(Conference conference, int fieldID) {
		String past = FieldDAO.getInstance().getField(conference.getFieldID()) == null ? null
				: FieldDAO.getInstance().getField(conference.getFieldID()).getFieldName();
		table.update(conference.getConferenceID(), 7, fieldID);
		conference.setFieldID(fieldID);
		Logging.getInstance().change("update", "Update conference field from\n\t" + past + "\nto\n\t"
				+ FieldDAO.getInstance().getField(fieldID).getFieldName());
	}

	public void updateConferenceField(Conference conference, String field) {
		updateConferenceField(conference, FieldDAO.getInstance().getField(field).getFieldID());
	}

	public void updateConferenceNote(Conference conference, String note) {
		String past = conference.getNote();
		String pastNote = past == null || past.isBlank() || past.isEmpty() ? "->no note<-" : past;
		table.update(conference.getConferenceID(), 5, note);
		conference.setNote(note);
		Logging.getInstance().change("update", "Update conference note from\n\t" + pastNote + "\nto\n\t" + note);
	}

	public void updateConferencePassword(Conference conference, String password) {
		table.update(conference.getConferenceID(), 3, password);
		conference.setConferenceEmailPassword(password);
		Logging.getInstance().change("update", "Updated password for conference email.");
	}

	public void updateConferenceReport(Conference conference, String filePath) {
		table.updateBlob(conference.getConferenceID(), 6, filePath);
		conference.setReportFile(filePath);
		Logging.getInstance().change("update", "Updated conference file report.");
	}

	public void updateConferenceTitle(Conference conference, String title) {
		String past = conference.getConferenceTitle();
		table.update(conference.getConferenceID(), 1, title);
		conference.setConferenceTitle(title);
		Logging.getInstance().change("update", "Update conference title from\n\t" + past + "\nto\n\t" + title);
	}

	public void updateConferenceBcc(Conference conference, String conferenceBcc) {
		String past = conference.getConferenceBcc();
		String bcc = past == null || past.isBlank() || past.isEmpty() ? "->no bcc<-" : past;
		table.update(conference.getConferenceID(), 4, conferenceBcc);
		conference.setConferenceBcc(conferenceBcc);
		Logging.getInstance().change("update", "Update conference bcc from\n\t" + bcc + "\nto\n\t" + conferenceBcc);
	}

	public Conference getter(ResultSet rs) {
		return getConference.get(rs);
	}
}
