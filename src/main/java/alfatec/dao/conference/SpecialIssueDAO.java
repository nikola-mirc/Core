package alfatec.dao.conference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import alfatec.dao.utils.TableUtility;
import alfatec.model.conference.Collection;
import alfatec.model.conference.SpecialIssue;
import database.DatabaseTable;
import database.Getter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * DAO for table "special_issue".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class SpecialIssueDAO {

	private static SpecialIssueDAO instance;

	public static SpecialIssueDAO getInstance() {
		if (instance == null)
			synchronized (SpecialIssueDAO.class) {
				if (instance == null)
					instance = new SpecialIssueDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<SpecialIssue> getSpecial;

	private SpecialIssueDAO() {
		table = new TableUtility(
				new DatabaseTable("special_issue", "special_issue_id", new String[] { "collection_id" }));
		getSpecial = new Getter<SpecialIssue>() {
			@Override
			public SpecialIssue get(ResultSet rs) {
				SpecialIssue si = new SpecialIssue();
				try {
					si.setSpecialIssueID(rs.getInt(table.getTable().getPrimaryKey()));
					si.setCollectionID(rs.getInt(table.getTable().getColumnName(1)));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return si;
			}
		};
	}

	public SpecialIssue createEntry(long collectionID) {
		return table.create(new String[] {}, new int[] {}, new long[] { collectionID }, getSpecial);
	}

	public void deleteEntry(SpecialIssue si) {
		table.delete(si.getSpecialIssueID());
	}

	/**
	 * @return all data from the table - all that were published in SI
	 */
	public ObservableList<SpecialIssue> getAll() {
		return table.getAll(getSpecial);
	}

	/**
	 * @param collectionID - foreign key of collection, point to particular research
	 */
	public SpecialIssue getByCollectionID(long collectionID) {
		return table.findBy(collectionID, 1, getSpecial).get(0);
	}

	/**
	 * Search table by primary key - id
	 */
	public SpecialIssue getByID(int id) {
		return table.findBy(id, getSpecial);
	}

	/**
	 * @return all that are tied to current conference
	 */
	public ObservableList<SpecialIssue> getCurrent() {
		return getFromConference(ConferenceDAO.getInstance().getCurrentConference().getConferenceID());
	}

	/**
	 * @param conferenceID
	 * @return all that are tied to specified conference
	 */
	public ObservableList<SpecialIssue> getFromConference(int conferenceID) {
		ObservableList<SpecialIssue> special = FXCollections.observableArrayList();
		List<Collection> forSI = CollectionDAO.getInstance().getAllForConference(conferenceID).stream()
				.filter(c -> c.getForSpecialIssue() == 1).collect(Collectors.toList());
		for (Collection c : forSI)
			special.add(getByCollectionID(c.getCollectionID()));
		return special;
	}

}
