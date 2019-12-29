package alfatec.dao.person;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import alfatec.dao.utils.Commons;
import alfatec.dao.utils.TableUtility;
import alfatec.model.person.Reviewer;
import database.DatabaseTable;
import database.Getter;

/**
 * DAO for table "reviewer".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class ReviewerDAO {

	private static ReviewerDAO instance;
	public static ReviewerDAO getInstance() {
		if (instance == null)
			synchronized (ReviewerDAO.class) {
				if (instance == null)
					instance = new ReviewerDAO();
			}
		return instance;
	}
	private final TableUtility table;

	private Getter<Reviewer> getReviewer;

	private ReviewerDAO() {
		table = new TableUtility(
				new DatabaseTable("reviewer", "reviewer_id", new String[] { "reviewer_name", "reviewer_last_name",
						"reviewer_email", "reviewer_telephone", "reviewer_institution", "note", "country_id" }));
		getReviewer = new Getter<Reviewer>() {
			@Override
			public Reviewer get(ResultSet rs) {
				Reviewer reviewer = new Reviewer();
				try {
					reviewer.setReviewerID(rs.getInt(table.getTable().getPrimaryKey()));
					reviewer.setReviewerFirstName(rs.getString(table.getTable().getColumnName(1)));
					reviewer.setReviewerLastName(rs.getString(table.getTable().getColumnName(2)));
					reviewer.setReviewerEmail(rs.getString(table.getTable().getColumnName(3)));
					reviewer.setContactTelephone(rs.getString(table.getTable().getColumnName(4)));
					reviewer.setInstitutionName(rs.getString(table.getTable().getColumnName(5)));
					reviewer.setNote(rs.getString(table.getTable().getColumnName(6)));
					reviewer.setCountryID(rs.getInt(table.getTable().getColumnName(7)));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return reviewer;
			}
		};
	}

	public Reviewer createReviewer(String firstName, String lastName, String email, String contactTelephone,
			String institution, String country, String note) {
		String[] strings = { firstName, lastName, email, contactTelephone, institution, note };
		int[] ints = { Commons.findCountryByName(country).getCountryID() };
		return table.create(strings, ints, new long[] {}, getReviewer);
	}

	public void deleteReviewer(Reviewer reviewer) {
		table.delete(reviewer.getReviewerID());
	}

	public ObservableList<Reviewer> getAllReviewers() {
		return table.getAll(getReviewer);
	}

	/**
	 * @param country name of the country
	 * @return all reviewers from specified country
	 */
	public ObservableList<Reviewer> getAllReviewersFrom(String country) {
		return table.getAllFrom(country, getReviewer, 7);
	}

	/**
	 * Search for reviewer by primary key - id
	 */
	public Reviewer getReviewer(int reviewerID) {
		return table.findBy(reviewerID, getReviewer);
	}

	/**
	 * Full text search in natural language
	 * 
	 * @param email
	 * @return reviewer with specified email address
	 */
	public Reviewer getReviewer(String email) {
		return table.findByFulltext(email, getReviewer, 3);
	}

	/**
	 * Full text search in boolean mode for reviewers with email, first and last
	 * name
	 * 
	 * @param startTyping
	 * @return all reviewers start with specified String
	 */
	public ObservableList<Reviewer> getReviewers(String startTyping) {
		String[] fulltext = { table.getTable().getColumnName(3), table.getTable().getColumnName(1),
				table.getTable().getColumnName(2) };
		return table.searchInBooleanMode(startTyping, fulltext, getReviewer);
	}

	public void updateCountry(Reviewer reviewer, String country) {
		table.updateCountry(reviewer.getReviewerID(), 7, country);
		reviewer.setCountryID(Commons.findCountryByName(country).getCountryID());
	}

	public void updateNote(Reviewer reviewer, String note) {
		table.update(reviewer.getReviewerID(), 6, note);
		reviewer.setNote(note);
	}

	public void updateReviewerEmail(Reviewer reviewer, String email) {
		table.update(reviewer.getReviewerID(), 3, email);
	}

	public void updateReviewerFirstName(Reviewer reviewer, String firstName) {
		table.update(reviewer.getReviewerID(), 1, firstName);
		reviewer.setReviewerFirstName(firstName);
	}

	public void updateReviewerInstitution(Reviewer reviewer, String institution) {
		table.update(reviewer.getReviewerID(), 5, institution);
		reviewer.setInstitutionName(institution);
	}

	public void updateReviewerLastName(Reviewer reviewer, String lastName) {
		table.update(reviewer.getReviewerID(), 2, lastName);
		reviewer.setReviewerLastName(lastName);
	}

	public void updateReviewerTelephone(Reviewer reviewer, String telephone) {
		table.update(reviewer.getReviewerID(), 4, telephone);
		reviewer.setContactTelephone(telephone);
	}

}
