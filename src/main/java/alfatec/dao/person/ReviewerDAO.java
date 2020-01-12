package alfatec.dao.person;

import java.sql.ResultSet;
import java.sql.SQLException;

import alfatec.dao.country.CountryDAO;
import alfatec.dao.utils.Commons;
import alfatec.dao.utils.Logging;
import alfatec.dao.utils.TableUtility;
import alfatec.model.person.Reviewer;
import database.DatabaseTable;
import database.Getter;
import javafx.collections.ObservableList;

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
		getReviewer = (ResultSet rs) -> {
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
		};
	}

	public Reviewer createReviewer(String firstName, String lastName, String email, String contactTelephone,
			String institution, String country, String note) {
		String[] strings = { firstName, lastName, email, contactTelephone, institution, note };
		int[] ints = { Commons.findCountryByName(country).getCountryID() };
		Reviewer reviewer = table.create(strings, ints, new long[] {}, getReviewer);
		Logging.getInstance().change("Create", "Add reviewer " + reviewer.getReviewerEmail() + ", "
				+ reviewer.getReviewerFirstName() + " " + reviewer.getReviewerLastName());
		return reviewer;
	}

	public void deleteReviewer(Reviewer reviewer) {
		table.delete(reviewer.getReviewerID());
		Logging.getInstance().change("Delete", "Delete reviewer " + reviewer.getReviewerEmail() + ", "
				+ reviewer.getReviewerFirstName() + " " + reviewer.getReviewerLastName());
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
		int id = reviewer.getCountryID();
		String past = id == 0 ? "->no country<-"
				: CountryDAO.getInstance().getCountry(reviewer.getCountryID()).getCountryName();
		table.updateCountry(reviewer.getReviewerID(), 7, country);
		reviewer.setCountryID(Commons.findCountryByName(country).getCountryID());
		Logging.getInstance().change("Update",
				"Update reviewer " + reviewer.getReviewerEmail() + " country from " + past + " to " + country);
	}

	public void updateNote(Reviewer reviewer, String note) {
		String past = reviewer.getNote();
		String pastNote = past == null || past.isBlank() || past.isEmpty() ? "->no note<-" : past;
		table.update(reviewer.getReviewerID(), 6, note);
		reviewer.setNote(note);
		Logging.getInstance().change("Update",
				"Update reviewer " + reviewer.getReviewerEmail() + " note from " + pastNote + " to " + note);
	}

	public void updateReviewerEmail(Reviewer reviewer, String email) {
		String past = reviewer.getReviewerEmail();
		table.update(reviewer.getReviewerID(), 3, email);
		reviewer.setReviewerEmail(email);
		Logging.getInstance().change("Update", "Update reviewer " + reviewer.getReviewerEmail() + " from " + past);
	}

	public void updateReviewerFirstName(Reviewer reviewer, String firstName) {
		String past = reviewer.getReviewerFirstName();
		table.update(reviewer.getReviewerID(), 1, firstName);
		reviewer.setReviewerFirstName(firstName);
		Logging.getInstance().change("Update", "Update reviewer first name from " + past + " to " + firstName);
	}

	public void updateReviewerInstitution(Reviewer reviewer, String institution) {
		String past = reviewer.getInstitutionName();
		String pastName = past == null || past.isBlank() || past.isEmpty() ? "->no institution name<-" : past;
		table.update(reviewer.getReviewerID(), 5, institution);
		reviewer.setInstitutionName(institution);
		Logging.getInstance().change("Update", "Update reviewer " + reviewer.getReviewerEmail() + " institution from "
				+ pastName + " to " + institution);
	}

	public void updateReviewerLastName(Reviewer reviewer, String lastName) {
		String past = reviewer.getReviewerLastName();
		table.update(reviewer.getReviewerID(), 2, lastName);
		reviewer.setReviewerLastName(lastName);
		Logging.getInstance().change("Update",
				"Update reviewer " + reviewer.getReviewerEmail() + " last name from " + past + " to " + lastName);
	}

	public void updateReviewerTelephone(Reviewer reviewer, String telephone) {
		String past = reviewer.getContactTelephone();
		String number = past == null || past.isBlank() || past.isEmpty() ? "->no telephone<-" : past;
		table.update(reviewer.getReviewerID(), 4, telephone);
		reviewer.setContactTelephone(telephone);
		Logging.getInstance().change("Update",
				"Update reviewer " + reviewer.getReviewerEmail() + " telephone from " + number + " to " + telephone);
	}

}
