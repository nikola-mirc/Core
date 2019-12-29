package alfatec.model.user;

import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.DateUtil;

/**
 * Model for database table "user".
 * 
 * Represent basic info about users of the application.
 * 
 * @author jelena
 *
 */
public class User {

	private final IntegerProperty userID;
	private final StringProperty userFirstName;
	private final StringProperty userLastName;
	private final StringProperty contactTelephone;
	private final ObjectProperty<LocalDateTime> createdTime;

	public User() {
		this(0, "", "", null, "");
	}

	public User(int id, String firstName, String lastName, String contactTelephone, String createdTime) {
		this.userID = new SimpleIntegerProperty(id);
		this.userFirstName = new SimpleStringProperty(firstName);
		this.userLastName = new SimpleStringProperty(lastName);
		this.contactTelephone = new SimpleStringProperty(contactTelephone);
		this.createdTime = new SimpleObjectProperty<LocalDateTime>(DateUtil.parse(createdTime));
	}

	public String getContactTelephone() {
		return contactTelephone.get();
	}

	public StringProperty getContactTelephoneProperty() {
		return contactTelephone;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime.get();
	}

	public ObjectProperty<LocalDateTime> getCreatedTimeProperty() {
		return createdTime;
	}

	public String getCreatedTimeString() {
		return DateUtil.format(createdTime.get());
	}

	public String getUserFirstName() {
		return userFirstName.get();
	}

	public StringProperty getUserFirstNameProperty() {
		return userFirstName;
	}

	public int getUserID() {
		return userID.get();
	}

	public IntegerProperty getUserIdProperty() {
		return userID;
	}

	public String getUserLastName() {
		return userLastName.get();
	}

	public StringProperty getUserLastNameProperty() {
		return userLastName;
	}

	public void setContactTelephone(String number) {
		this.contactTelephone.set(number);
	}

	public void setCreatedTime(LocalDateTime ldt) {
		this.createdTime.set(ldt);
	}

	/**
	 * PATTERN = "yyyy-MM-dd HH:mm:ss";
	 * 
	 * @param dateTime
	 */
	public void setCreatedTime(String dateTime) {
		this.createdTime.set(DateUtil.parse(dateTime));
	}

	public void setUserFirstName(String firstName) {
		this.userFirstName.set(firstName);
	}

	public void setUserID(int id) {
		this.userID.set(id);
	}

	public void setUserLastName(String lastName) {
		this.userLastName.set(lastName);
	}

}
