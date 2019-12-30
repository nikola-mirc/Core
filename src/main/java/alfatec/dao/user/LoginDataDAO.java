package alfatec.dao.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import util.Password;
import alfatec.dao.utils.Commons;
import alfatec.dao.utils.TableUtility;
import alfatec.model.enums.RoleEnum;
import alfatec.model.user.LoginData;
import database.Getter;
import database.DatabaseTable;

/**
 * DAO for table "login_data".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class LoginDataDAO {

	private static LoginDataDAO instance;

	public static LoginDataDAO getInstance() {
		if (instance == null)
			synchronized (LoginDataDAO.class) {
				if (instance == null)
					instance = new LoginDataDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<LoginData> getLogin;

	private LoginDataDAO() {
		table = new TableUtility(new DatabaseTable("login_data", "login_id",
				new String[] { "user_email", "password_hash", "user_id", "role_id" }));
		getLogin = new Getter<LoginData>() {
			@Override
			public LoginData get(ResultSet rs) {
				LoginData login = new LoginData();
				try {
					login.setLoginID(rs.getInt(table.getTable().getPrimaryKey()));
					login.setUserEmail(rs.getString(table.getTable().getColumnName(1)));
					login.readPasswordFromDB(rs.getString(table.getTable().getColumnName(2)));
					login.setUserID(rs.getInt(table.getTable().getColumnName(3)));
					login.setRoleID(rs.getInt(table.getTable().getColumnName(4)));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return login;
			}
		};
	}

	/**
	 * Will save hashed password in db
	 * 
	 * @param email
	 * @param password regular password
	 * @param userID
	 * @param roleID
	 * @return
	 */
	public LoginData createLoginData(String email, String password, int userID, int roleID) {
		return table.create(new String[] { email, Password.hashPassword(password) }, new int[] { userID, roleID },
				new long[] {}, getLogin);
	}

	public void deleteLoginCredentials(LoginData data) {
		table.delete(data.getLoginID());
	}

	/**
	 * @return all data from the table
	 */
	public ObservableList<LoginData> getAll() {
		return table.getAll(getLogin);
	}

	/**
	 * Full text search in natural language by email
	 * 
	 * @param email
	 * @return matching LoginData object
	 */
	public LoginData getData(String email) {
		return Commons.findEmail(email);
	}

	/**
	 * @param roleID
	 * @return all data connected with specified role
	 */
	public ObservableList<LoginData> getDataForRole(int roleID) {
		return table.findBy(roleID, 4, getLogin);
	}

	/**
	 * @param userID
	 * @return all data for specified user
	 */
	public LoginData getDataForUser(int userID) {
		return table.findBy(userID, 3, getLogin).get(0);
	}

	public void updateEmail(LoginData data, String email) {
		table.update(data.getLoginID(), 1, email);
		data.setUserEmail(email);
	}

	/**
	 * Will save hashed password
	 * 
	 * @param data
	 * @param password "regular" password
	 */
	public void updatePassword(LoginData data, String password) {
		table.update(data.getLoginID(), 2, Password.hashPassword(password));
		data.setPassword(password);
	}

	public void updateRole(LoginData data, String role) {
		table.update(data.getLoginID(), 4, RoleEnum.valueOf(role).getRoleID());
		data.setRole(role);
	}

	public LoginData findUserDataByExactEmail(String email) {
		ObservableList<LoginData> search = table.findWhere(new String[] { table.getTable().getColumnName(1) },
				new String[] { email }, getLogin);
		return search.size() > 0 ? search.get(0) : null;
	}
	
	public LoginData getter(ResultSet rs) {
		return getLogin.get(rs);
	}
}
