package alfatec.dao.country;

import java.sql.ResultSet;
import java.sql.SQLException;

import alfatec.dao.utils.Commons;
import alfatec.dao.utils.TableUtility;
import alfatec.model.country.Country;
import database.DatabaseTable;
import database.Getter;
import javafx.collections.ObservableList;

/**
 * DAO for table "country".
 * 
 * Double-checked locking singleton.
 * 
 * @author jelena
 *
 */
public class CountryDAO {

	private static CountryDAO instance;

	public static CountryDAO getInstance() {
		if (instance == null)
			synchronized (CountryDAO.class) {
				if (instance == null)
					instance = new CountryDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<Country> getCountry;

	private CountryDAO() {
		table = new TableUtility(new DatabaseTable("country", "country_id", new String[] { "id", "value" }));
		getCountry = (ResultSet rs) -> {
			Country country = new Country();
			try {
				country.setCountryID(rs.getInt(table.getTable().getPrimaryKey()));
				country.setIsoAlpha2Code(rs.getString(table.getTable().getColumnName(1)));
				country.setCountryName(rs.getString(table.getTable().getColumnName(2)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return country;
		};
	}

	/**
	 * Note: values must be unique in DB
	 */
	public Country createCountry(String name, String isoAlpha2Code) {
		String[] strings = { isoAlpha2Code, name };
		return table.create(strings, new int[] {}, new long[] {}, getCountry);
	}

	public void deleteCountry(Country country) {
		table.delete(country.getCountryID());
	}

	/**
	 * Full text search in boolean mode by country name or iso alpha 2 code
	 * 
	 * @param startTyping
	 * @return all countries start with specified String
	 */
	public ObservableList<Country> findCountries(String startTyping) {
		return table.searchInBooleanMode(startTyping, new String[] { table.getTable().getColumnName(2) }, getCountry);
	}

	public ObservableList<Country> getAllCountries() {
		return table.getAll(getCountry);
	}

	public ObservableList<String> getAllCountryNames() {
		return table.getColumn(2);
	}

	/**
	 * Search for country by primary key - id
	 */
	public Country getCountry(int id) {
		return table.findBy(id, getCountry);
	}

	/**
	 * @param name name of the country
	 * @return country
	 */
	public Country getCountry(String name) {
		return Commons.findCountryByName(name);
	}

	public void updateCountry(Country country, String newName) {
		table.update(country.getCountryID(), 2, newName);
		country.setCountryName(newName);
	}
}
