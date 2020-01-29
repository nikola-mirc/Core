package alfatec.dao.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import alfatec.model.conference.RegistrationFee;
import alfatec.model.country.Country;
import alfatec.model.user.LoginData;
import database.CRUD;

/**
 * Some common methods, used by several classes.
 * 
 * @author jelena
 *
 */
public class Commons {

	public static Country findCountryByName(String name) {
		ResultSet rs = CRUD.findByString("country", "value", name);
		try {
			if (rs.next())
				return new Country(rs.getInt("country_id"), rs.getString("id"), name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Search login_data table in full text in natural language for email
	 * 
	 * @param email
	 * @return loginData object
	 */
	public static LoginData findEmail(String email) {
		ResultSet rs = CRUD.read("login_data", "user_email", email);
		try {
			if (rs.next()) {
				int id = rs.getInt("login_id");
				return new LoginData(id, email, rs.getString("password_hash"), rs.getInt("user_id"),
						rs.getInt("role_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param name         name of the registration_fee
	 * @param conferenceID id of conference for which the fee is defined
	 */
	public static RegistrationFee findRegistration(String name, int conferenceID) {
		ResultSet rs = CRUD.findByString("registration_fee", "registration_name", name);
		int id = 0;
		try {
			while (rs.next()) {
				int x = rs.getInt("for_conference");
				if (x == conferenceID) {
					id = x;
					break;
				}
			}
			return new RegistrationFee(id, conferenceID, name, rs.getBigDecimal("registration_price").doubleValue(),
					rs.getString("currency"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getLastRecordedIndex(ResultSet rs) {
		int max = 0;
		try {
			if (rs.next())
				max = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return max;
	}
}
