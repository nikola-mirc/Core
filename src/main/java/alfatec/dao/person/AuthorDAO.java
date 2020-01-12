package alfatec.dao.person;

import java.sql.ResultSet;
import java.sql.SQLException;

import alfatec.dao.country.CountryDAO;
import alfatec.dao.utils.Commons;
import alfatec.dao.utils.Logging;
import alfatec.dao.utils.TableUtility;
import alfatec.model.enums.Institution;
import alfatec.model.person.Author;
import database.DatabaseTable;
import database.Getter;
import javafx.collections.ObservableList;

/**
 * DAO for table "author"
 * 
 * Double-checked locking singleton.
 * 
 * @author jelena
 *
 */
public class AuthorDAO {

	private static AuthorDAO instance;

	public static AuthorDAO getInstance() {
		if (instance == null)
			synchronized (AuthorDAO.class) {
				if (instance == null)
					instance = new AuthorDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<Author> getAuthor;

	private AuthorDAO() {
		table = new TableUtility(new DatabaseTable("author", "author_id", new String[] { "institution",
				"author_first_name", "author_last_name", "author_email", "institution_name", "note", "country_id" }));
		getAuthor = (ResultSet rs) -> {
			Author author = new Author();
			try {
				author.setAuthorID(rs.getLong(table.getTable().getPrimaryKey()));
				author.setAuthorFirstName(rs.getString(table.getTable().getColumnName(2)));
				author.setAuthorLastName(rs.getString(table.getTable().getColumnName(3)));
				author.setAuthorEmail(rs.getString(table.getTable().getColumnName(4)));
				author.setInstitutionType(rs.getString(table.getTable().getColumnName(1)));
				author.setInstitutionName(rs.getString(table.getTable().getColumnName(5)));
				author.setNote(rs.getString(table.getTable().getColumnName(6)));
				author.setCountryID(rs.getInt(table.getTable().getColumnName(7)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return author;
		};
	}

	/**
	 * Note: Email must be unique in DB
	 */
	public Author createAuthor(String firstName, String lastName, String email, String country, String institutionType,
			String institutionName, String note) {
		int[] ints = { Commons.findCountryByName(country).getCountryID() };
		String[] strings = { institutionType, firstName, lastName, email, institutionName, note };
		return table.create(strings, ints, new long[] {}, getAuthor);
	}

	public void deleteAuthor(Author author) {
		String authorDetails = author.getAuthorEmail() + ", " + author.getAuthorFirstName() + " "
				+ author.getAuthorLastName();
		table.delete(author.getAuthorID());
		Logging.getInstance().change("Delete", "DELETE AUTHOR " + authorDetails);
	}

	public Author createAuthor(String firstName, String lastName, String email, String country,
			Institution institutionType, String institutionName, String note) {
		int[] ints = { Commons.findCountryByName(country).getCountryID() };
		String[] strings = { firstName, lastName, email, institutionName, note };
		return table.create(strings, ints, institutionType, getAuthor);
	}

	/**
	 * Search for authors by primary key - id
	 */
	public Author findAuthorByID(long id) {
		return table.findBy(id, getAuthor);
	}

	public ObservableList<Author> getAllAuthors() {
		return table.getAll(getAuthor);
	}

	/**
	 * Full text search in natural language
	 * 
	 * @param email
	 * @return author with specified email address
	 */
	public Author getAuthor(String email) {
		return table.findByFulltext(email, getAuthor, 4);
	}

	/**
	 * @param country name of the country
	 * @return all authors from specified country
	 */
	public ObservableList<Author> getAuthorsFrom(String country) {
		return table.getAllFrom(country, getAuthor, 7);
	}

	/**
	 * Full text search in boolean mode by email, first and last name
	 * 
	 * @param startTyping
	 * @return all authors that start with specified String
	 */
	public ObservableList<Author> searchForAuthors(String startTyping) {
		String[] fulltext = { table.getTable().getColumnName(2), table.getTable().getColumnName(3),
				table.getTable().getColumnName(4) };
		return table.searchInBooleanMode(startTyping, fulltext, getAuthor);
	}

	public void updateAuthorCountry(Author author, String country) {
		String past = CountryDAO.getInstance().getCountry(author.getCountryID()).getCountryName();
		table.updateCountry(author.getAuthorID(), 7, country);
		author.setCountryID(Commons.findCountryByName(country).getCountryID());
		Logging.getInstance().change("Update",
				"UPDATE AUTHOR " + author.getAuthorEmail() + " COUNTRY FROM: " + past + " TO: " + country);
	}

	/**
	 * Note: Email must be unique in DB
	 */
	public void updateAuthorEmail(Author author, String email) {
		String past = author.getAuthorEmail();
		table.update(author.getAuthorID(), 4, email);
		author.setAuthorEmail(email);
		Logging.getInstance().change("Update",
				"UPDATE AUTHOR " + author.getAuthorEmail() + " EMAIL FROM: " + past + " TO: " + email);
	}

	public void updateAuthorFirstName(Author author, String firstName) {
		String past = author.getAuthorFirstName();
		table.update(author.getAuthorID(), 2, firstName);
		author.setAuthorFirstName(firstName);
		Logging.getInstance().change("Update",
				"UPDATE AUTHOR " + author.getAuthorEmail() + " FIRST NAME FROM: " + past + " TO: " + firstName);
	}

	public void updateAuthorInstitution(Author author, String institutionType) {
		String past = author.getInstitution().name();
		table.update(author.getAuthorID(), 1, institutionType);
		author.setInstitutionType(institutionType);
		Logging.getInstance().change("Update", "UPDATE AUTHOR " + author.getAuthorEmail() + " INSTITUTION TYPE FROM: "
				+ past + " TO: " + institutionType);
	}

	public void updateAuthorInstitutionName(Author author, String institutionName) {
		String past = author.getInstitutionName();
		String pastName = past == null || past.isBlank() || past.isEmpty() ? "->no institution name<-" : past;
		table.update(author.getAuthorID(), 5, institutionName);
		author.setInstitutionName(institutionName);
		Logging.getInstance().change("Update", "UPDATE AUTHOR " + author.getAuthorEmail() + " INSTITUTION NAME FROM: "
				+ pastName + " TO: " + institutionName);
	}

	public void updateAuthorLastName(Author author, String lastName) {
		String past = author.getAuthorLastName();
		table.update(author.getAuthorID(), 3, lastName);
		author.setAuthorLastName(lastName);
		Logging.getInstance().change("Update",
				"UPDATE AUTHOR " + author.getAuthorEmail() + " LAST NAME FROM: " + past + " TO: " + lastName);
	}

	public void updateAuthorNote(Author author, String note) {
		String past = author.getNote();
		String pastNote = past == null || past.isBlank() || past.isEmpty() ? "->no note<-" : past;
		table.update(author.getAuthorID(), 6, note);
		author.setNote(note);
		Logging.getInstance().change("Update",
				"UPDATE AUTHOR " + author.getAuthorEmail() + " NOTE FROM: " + pastNote + " TO: " + note);
	}

	public Author findAuthorByExactEmail(String email) {
		ObservableList<Author> search = table.findWhere(new String[] { table.getTable().getColumnName(4) },
				new String[] { email }, getAuthor);
		return search.size() > 0 ? search.get(0) : null;
	}

}
