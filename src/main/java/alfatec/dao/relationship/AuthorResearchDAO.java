package alfatec.dao.relationship;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import alfatec.dao.utils.TableUtility;
import alfatec.model.relationship.AuthorResearch;
import database.Getter;
import database.DatabaseTable;

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
		getAR = new Getter<AuthorResearch>() {
			@Override
			public AuthorResearch get(ResultSet rs) {
				AuthorResearch ar = new AuthorResearch();
				try {
					ar.setAuthorResearchID(rs.getLong(table.getTable().getPrimaryKey()));
					ar.setResearchID(rs.getLong(table.getTable().getColumnName(1)));
					ar.setAuthorID(rs.getLong(table.getTable().getColumnName(2)));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return ar;
			}
		};
	}

	public AuthorResearch createEntry(long authorID, long researchID) {
		return table.create(new String[] {}, new int[] {}, new long[] { researchID, authorID }, getAR);
	}

	public void deleteEntry(AuthorResearch ar) {
		table.delete(ar.getAuthorResearchID());
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

	public AuthorResearchDAO getInstance() {
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
