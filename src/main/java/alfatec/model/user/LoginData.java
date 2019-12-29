package alfatec.model.user;

import alfatec.model.enums.RoleEnum;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.Password;

/**
 * Model for database table login_data.
 * 
 * Holds login data for users of the application.
 * 
 * @author jelena
 *
 */
public class LoginData {

	private final IntegerProperty loginID;
	private final StringProperty userEmail;
	private final StringProperty passwordHash;
	private final IntegerProperty userID;
	private final IntegerProperty roleID;

	public LoginData() {
		this(0, null, null, 0, 0);
	}

	public LoginData(int loginID, String email, String password, int userID, int roleID) {
		this.loginID = new SimpleIntegerProperty(loginID);
		this.userEmail = new SimpleStringProperty(email);
		this.passwordHash = new SimpleStringProperty(password);
		this.userID = new SimpleIntegerProperty(userID);
		this.roleID = new SimpleIntegerProperty(roleID);
	}

	public int getLoginID() {
		return loginID.get();
	}

	public IntegerProperty getLoginIDProperty() {
		return loginID;
	}

	public String getPasswordHash() {
		return passwordHash.get();
	}

	public StringProperty getPasswordHashProperty() {
		return passwordHash;
	}

	public int getRoleID() {
		return roleID.get();
	}

	public IntegerProperty getRoleIDProperty() {
		return roleID;
	}

	public String getRoleName() {
		return RoleEnum.getRoleName(getRoleID());
	}

	public String getUserEmail() {
		return userEmail.get();
	}

	public StringProperty getUserEmailProperty() {
		return userEmail;
	}

	public int getUserID() {
		return userID.get();
	}

	public IntegerProperty getUserIDProperty() {
		return userID;
	}

	public void setLoginID(int id) {
		this.loginID.set(id);
	}

	/**
	 * Method to set hashed password
	 * 
	 * @param password - "regular" password
	 */
	public void setPassword(String password) {
		this.passwordHash.set(Password.hashPassword(password));
	}

	/**
	 * only to read hashed password from db
	 * 
	 * @param password result set get string method
	 */
	public void readPasswordFromDB(String password) {
		this.passwordHash.set(password);
	}

	public void setRole(RoleEnum role) {
		this.roleID.set(role.getRoleID());
	}

	public void setRole(String roleName) {
		this.roleID.set(RoleEnum.valueOf(roleName.toUpperCase()).getRoleID());
	}

	public void setRoleID(int id) {
		this.roleID.set(id);
	}

	public void setUserEmail(String email) {
		this.userEmail.set(email);
	}

	public void setUserID(int id) {
		this.userID.set(id);
	}

}
