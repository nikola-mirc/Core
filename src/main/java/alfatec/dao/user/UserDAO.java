package alfatec.dao.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javafx.collections.ObservableList;
import alfatec.dao.utils.Commons;
import alfatec.dao.utils.TableUtility;
import alfatec.model.user.User;
import database.Getter;
import database.DatabaseTable;
import util.DateUtil;

/**
 * DAO class for "user" table.
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class UserDAO {

	private static UserDAO instance;

	public static UserDAO getInstance() {
		if (instance == null)
			synchronized (UserDAO.class) {
				if (instance == null)
					instance = new UserDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<User> getUser;

	private UserDAO() {
		table = new TableUtility(new DatabaseTable("user", "user_id",
				new String[] { "user_first_name", "user_last_name", "contact_telephone", "created_time" }));
		getUser = new Getter<User>() {
			@Override
			public User get(ResultSet rs) {
				User user = new User();
				try {
					user.setUserID(rs.getInt(table.getTable().getPrimaryKey()));
					user.setUserFirstName(rs.getString(table.getTable().getColumnName(1)));
					user.setUserLastName(rs.getString(table.getTable().getColumnName(2)));
					user.setContactTelephone(rs.getString(table.getTable().getColumnName(3)));
					user.setCreatedTime(rs.getTimestamp(table.getTable().getColumnName(4)).toLocalDateTime());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return user;
			}
		};
	}

	public User createUser(String firstName, String lastName, String contactTelephone) {
		return table.create(
				new String[] { firstName, lastName, contactTelephone, DateUtil.format(LocalDateTime.now()) },
				new int[] {}, new long[] {}, getUser);
	}

	public void deleteUser(User user) {
		table.delete(user.getUserID());
	}

	/**
	 * Search for user by email
	 * 
	 * @param email to login with
	 * @return
	 */
	public User findUser(String email) {
		return getUser(Commons.findEmail(email).getUserID());
	}

	/**
	 * Full text search for users with first and last name in boolean mode
	 * 
	 * @param startTyping
	 * @return
	 */
	public ObservableList<User> findUsers(String startTyping) {
		return table.searchInBooleanMode(startTyping,
				new String[] { table.getTable().getColumnName(1), table.getTable().getColumnName(2) }, getUser);
	}

	public ObservableList<User> getAllUsers() {
		return table.getAll(getUser);
	}

	/**
	 * Search for user by ID, primary key
	 * 
	 * @param id
	 * @return
	 */
	public User getUser(int id) {
		return table.findBy(id, getUser);
	}

	public void updateUserFirstName(User user, String firstName) {
		table.update(user.getUserID(), 1, firstName);
		user.setUserFirstName(firstName);
	}

	public void updateUserLastName(User user, String lastName) {
		table.update(user.getUserID(), 2, lastName);
		user.setUserLastName(lastName);
	}

	public void updateUserTelephone(User user, String contact) {
		table.update(user.getUserID(), 3, contact);
		user.setContactTelephone(contact);
	}

	public User getter(ResultSet rs) {
		return getUser.get(rs);
	}
}
