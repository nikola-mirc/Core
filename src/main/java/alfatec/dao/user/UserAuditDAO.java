package alfatec.dao.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javafx.collections.ObservableList;
import alfatec.dao.utils.TableUtility;
import alfatec.model.user.LoginData;
import alfatec.model.user.UserAudit;
import database.Getter;
import database.DatabaseTable;
import util.DateUtil;

/**
 * DAO for table "user_audit".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class UserAuditDAO {

	private static UserAuditDAO instance;

	public static UserAuditDAO getInstance() {
		if (instance == null)
			synchronized (UserAuditDAO.class) {
				if (instance == null)
					instance = new UserAuditDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<UserAudit> getAudit;

	private UserAuditDAO() {
		table = new TableUtility(new DatabaseTable("user_audit", "audit_id",
				new String[] { "event_type", "description", "time", "login_id" }));
		getAudit = (ResultSet rs) -> {
			UserAudit user = new UserAudit();
			try {
				user.setUserAuditID(rs.getInt(table.getTable().getPrimaryKey()));
				user.setEventType(rs.getString(table.getTable().getColumnName(1)));
				user.setDescription(rs.getString(table.getTable().getColumnName(2)));
				user.setTimestamp(rs.getTimestamp(table.getTable().getColumnName(3)).toLocalDateTime());
				user.setLoginID(rs.getInt(table.getTable().getColumnName(4)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return user;
		};
	}

	/**
	 * Insert entry in DB
	 * 
	 * @param eventType   - insert, update, delete - user action
	 * @param description - more detailed description of change
	 * @param loginID     - id of user / email that made change
	 * @return
	 */
	public UserAudit createEntry(String eventType, String description, int loginID) {
		return table.create(new String[] { eventType, description, DateUtil.format(LocalDateTime.now()) },
				new int[] { loginID }, new long[] {}, getAudit);
	}

	/**
	 * Delete entry from the table. Will be used with triggers.
	 * 
	 * @param audit
	 */
	public void deleteEntry(UserAudit audit) {
		table.delete(audit.getUserAuditID());
	}

	public ObservableList<UserAudit> getAll() {
		return table.getAll(getAudit);
	}

	/**
	 * Search table by login id
	 */
	public ObservableList<UserAudit> getAllFor(int loginID) {
		return table.findWhere(new String[] { table.getTable().getColumnName(4) }, new long[] { loginID }, getAudit);
	}

	/**
	 * @param data object of type LoginData
	 * @return all entries tied to given data
	 */
	public ObservableList<UserAudit> getAllFor(LoginData data) {
		return getAllFor(data.getLoginID());
	}
}
