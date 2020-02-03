package alfatec.dao.conference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.apache.commons.io.FileUtils;

import alfatec.dao.utils.Commons;
import alfatec.dao.utils.TableUtility;
import alfatec.model.conference.EmailHelper;
import database.CRUD;
import database.DatabaseTable;
import database.DatabaseUtility;
import database.Getter;
import javafx.collections.ObservableList;
import util.DateUtil;
import util.Folder;

public class EmailDAO {

	private static EmailDAO instance;
	private final TableUtility table;
	private Getter<EmailHelper> getHelp;

	private EmailDAO() {
		table = new TableUtility(new DatabaseTable("email_helper", "email_id",
				new String[] { "time", "message", "conference_id", "sent", "invite" }));
		getHelp = (ResultSet rs) -> {
			EmailHelper email = new EmailHelper();
			try {
				email.setEmailID(rs.getInt(table.getTable().getPrimaryKey()));
				email.setLocalDateTime(rs.getTimestamp(table.getTable().getColumnName(1)).toLocalDateTime());
				email.setFile(null);
				email.setConferenceID(rs.getInt(table.getTable().getColumnName(3)));
				email.setCount(rs.getInt(table.getTable().getColumnName(4)));
				email.setOrdinal(rs.getInt(table.getTable().getColumnName(5)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return email;
		};
	}

	public static EmailDAO getInstance() {
		if (instance == null)
			synchronized (EmailDAO.class) {
				if (instance == null)
					instance = new EmailDAO();
			}
		return instance;
	}

	public EmailHelper create(int invite, int sent) {
		return table.create(new String[] { DateUtil.format(LocalDateTime.now()), null },
				new int[] { ConferenceDAO.getInstance().getCurrentConference().getConferenceID(), sent, invite },
				new long[] {}, getHelp);
	}

	public EmailHelper create(int invite, int sent, String text) {
		try {
			String path = createFile(text, sent).getAbsolutePath();
			return table.create(path, 2, new String[] { DateUtil.format(LocalDateTime.now()), path },
					new int[] { ConferenceDAO.getInstance().getCurrentConference().getConferenceID(), sent, invite },
					new long[] {}, getHelp);
		} catch (IOException e) {
			return create(invite, sent);
		}
	}

	public ObservableList<EmailHelper> getAllRelevant() {
		try {
			return table.findBy(ConferenceDAO.getInstance().getCurrentConference().getConferenceID(), 3, getHelp);
		} catch (NullPointerException e) {
			return null;
		}
	}

	public EmailHelper getLastRecord() {
		ResultSet rs = CRUD.read(table.getTable().getTableName(), new String[] { table.getTable().getColumnName(3) },
				new long[] { ConferenceDAO.getInstance().getCurrentConference().getConferenceID() });
		return table.findBy(Commons.getLastRecordedIndex(rs), getHelp);
	}

	private File getBlob(EmailHelper email) {
		String query = String.format("SELECT * FROM email_helper WHERE email_id = %d", email.getEmailID());
		ResultSet rs = DatabaseUtility.getInstance().executeQuery(query);
		Path path;
		try {
			path = Paths.get(Folder.getConferenceDirectory().getAbsolutePath() + File.separator + email.getEmailID()
					+ table.getTable().getColumnName(2));
			if (Files.notExists(path)) {
				File file = new File(Folder.getConferenceDirectory().getAbsolutePath() + File.separator
						+ email.getEmailID() + table.getTable().getColumnName(2));
				rs.next();
				InputStream blob = rs.getBinaryStream(table.getTable().getColumnName(2));
				if (blob != null)
					FileUtils.copyInputStreamToFile(blob, file);
				email.setFile(file);
				file.deleteOnExit();
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		return email.getFile();
	}

	public String getMessage(EmailHelper email) {
		try {
			return FileUtils.readFileToString(getBlob(email), "UTF-8");
		} catch (IOException e) {
			return null;
		}
	}

	private File createFile(String text, int id) throws IOException {
		File file = new File(Folder.getConferenceDirectory().getAbsolutePath() + File.separator
				+ table.getTable().getColumnName(2) + id);
		FileUtils.writeStringToFile(file, text, "UTF-8");
		file.deleteOnExit();
		return file;
	}

	public void updateBlob(EmailHelper email, String text) {
		try {
			File file = createFile(text, email.getEmailID());
			table.updateBlob(email.getEmailID(), 2, file.getAbsolutePath());
			email.setFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
