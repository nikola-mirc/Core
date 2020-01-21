package alfatec.dao.relationship;

import java.sql.ResultSet;
import java.sql.SQLException;

import alfatec.dao.person.AuthorDAO;
import alfatec.dao.research.ResearchDAO;
import alfatec.dao.utils.Logging;
import alfatec.dao.utils.TableUtility;
import alfatec.model.relationship.AuthorResearch;
import database.DatabaseTable;
import database.Getter;
import javafx.collections.ObservableList;

/**
 * DAO for table "author_research".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class AuthorResearchDAO {

	private static AuthorResearchDAO instance;
	private final TableUtility table;
	private Getter<AuthorResearch> getAR;

	private AuthorResearchDAO() {
		table = new TableUtility(new DatabaseTable("author_research", "author_research_id",
				new String[] { "research_id", "author_id" }));
		getAR = (ResultSet rs) -> {
			AuthorResearch ar = new AuthorResearch();
			try {
				ar.setAuthorResearchID(rs.getLong(table.getTable().getPrimaryKey()));
				ar.setResearchID(rs.getLong(table.getTable().getColumnName(1)));
				ar.setAuthorID(rs.getLong(table.getTable().getColumnName(2)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return ar;
		};
	}

	public AuthorResearch createEntry(long authorID, long researchID) {
		return table.create(new String[] {}, new int[] {}, new long[] { researchID, authorID }, getAR);
	}

	public void deleteEntry(AuthorResearch ar) {
		table.delete(ar.getAuthorResearchID());
		Logging.getInstance().change("delete",
				"Removed\n\t" + AuthorDAO.getInstance().findAuthorByID(ar.getAuthorID()).getAuthorEmail()
						+ "\nas author of\n\t"
						+ ResearchDAO.getInstance().getResearch(ar.getResearchID()).getResearchTitle());
	}

	/**
	 * @return all data from the table
	 */
	public ObservableList<AuthorResearch> getAllEntries() {
		return table.getAll(getAR);
	}

	/**
	 * @param authorID
	 * @return -> all researches by specified author
	 */
	public ObservableList<AuthorResearch> getAllEntriesForAuthor(long authorID) {
		return table.findBy(authorID, 2, getAR);
	}

	/**
	 * @param researchID
	 * @return -> all authors of specified research
	 */
	public ObservableList<AuthorResearch> getAllEntriesForResearch(long researchID) {
		return table.findBy(researchID, 1, getAR);
	}

	/**
	 * Search table by primary key - id
	 */
	public AuthorResearch getByID(long id) {
		return table.findBy(id, getAR);
	}

	public AuthorResearch getEntry(long researchID, long authorID) {
		try {
			return table
					.findWhere(new String[] { table.getTable().getColumnName(1), table.getTable().getColumnName(2) },
							new long[] { researchID, authorID }, getAR)
					.get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public static AuthorResearchDAO getInstance() {
		if (instance == null)
			synchronized (AuthorResearchDAO.class) {
				if (instance == null)
					instance = new AuthorResearchDAO();
			}
		return instance;
	}

	public void updateAuthorID(AuthorResearch ar, long authorID) {
		table.update(ar.getAuthorResearchID(), 2, authorID);
		ar.setAuthorID(authorID);
	}

	public void updateResearchID(AuthorResearch ar, long researchID) {
		table.update(ar.getAuthorResearchID(), 1, researchID);
		ar.setResearchID(researchID);
	}
}
