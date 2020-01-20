package alfatec.dao.research;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;

import javafx.collections.ObservableList;
import util.Folder;
import alfatec.dao.utils.Logging;
import alfatec.dao.utils.TableUtility;
import alfatec.model.research.Research;
import database.Getter;
import database.DatabaseTable;

/**
 * DAO for table "research".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class ResearchDAO {

	private static ResearchDAO instance;

	public static ResearchDAO getInstance() {
		if (instance == null)
			synchronized (ResearchDAO.class) {
				if (instance == null)
					instance = new ResearchDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<Research> getResearch;

	private ResearchDAO() {
		table = new TableUtility(
				new DatabaseTable("research", "research_id", new String[] { "research_title", "paper", "note" }));
		getResearch = (ResultSet rs) -> {
			Research research = new Research();
			try {
				research.setResearchID(rs.getLong(table.getTable().getPrimaryKey()));
				research.setResearchTitle(rs.getString(table.getTable().getColumnName(1)));
				Path path = Paths.get(Folder.getResearchDirectory().getAbsolutePath() + File.separator
						+ research.getResearchID() + research.getResearchTitle());
				if (Files.notExists(path)) {
					File file = new File(Folder.getResearchDirectory().getAbsolutePath() + File.separator
							+ research.getResearchID() + research.getResearchTitle());
					InputStream blob = rs.getBinaryStream(table.getTable().getColumnName(2));
					if (blob != null)
						FileUtils.copyInputStreamToFile(blob, file);
					research.setPaperFile(file);
					file.deleteOnExit();
				}
				research.setNote(rs.getString(table.getTable().getColumnName(3)));
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			}
			return research;
		};
	}

	public Research createResearch(String title, String filePath, String note) {
		Research research = table.create(filePath, 2, new String[] { title, filePath, note }, new int[] {},
				new long[] {}, getResearch);
		Logging.getInstance().change("Create", "Add research " + title);
		return research;
	}

	public Research createResearch(String title, String note) {
		Research research = table.create(new String[] { title, null, note }, new int[] {}, new long[] {}, getResearch);
		Logging.getInstance().change("Create", "Add research " + title);
		return research;
	}

	public void deleteResearch(Research research) {
		table.delete(research.getResearchID());
		Logging.getInstance().change("Delete", "Delete research " + research.getResearchTitle());
	}

	/**
	 * @return all researches form the table
	 */
	public ObservableList<Research> getAllResearches() {
		return table.getAll(getResearch);
	}

	/**
	 * Find research by ID
	 * 
	 * @param researchID
	 * @return
	 */
	public Research getResearch(long researchID) {
		return table.findBy(researchID, getResearch);
	}

	/**
	 * Full text search in boolean mode - start typing - by title
	 * 
	 * @param title
	 * @return all researches start with "title"
	 */
	public ObservableList<Research> getResearch(String title) {
		return table.searchInBooleanMode(title, new String[] { table.getTable().getColumnName(1) }, getResearch);
	}

	public void updateNote(Research research, String note) {
		String past = research.getNote();
		String pastNote = past == null || past.isBlank() || past.isEmpty() ? "->no note<-" : past;
		table.update(research.getResearchID(), 3, note);
		research.setNote(note);
		Logging.getInstance().change("Update",
				"Update " + research.getResearchTitle() + " note from " + pastNote + " to " + note);
	}

	public void updatePaper(Research research, String filePath) {
		table.updateBlob(research.getResearchID(), 2, filePath);
		research.setPaperPath(filePath);
		Logging.getInstance().change("Update", "Updated research file for " + research.getResearchTitle());
	}

	public void updateTitle(Research research, String title) {
		String past = research.getResearchTitle();
		table.update(research.getResearchID(), 1, title);
		research.setResearchTitle(title);
		Logging.getInstance().change("Update", "Update research title from " + past + " to " + title);
	}
}
