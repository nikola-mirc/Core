package alfatec.model.user;

import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.DateUtil;

/**
 * Model for database table "user_audit".
 * 
 * History table - will be used to monitor user interaction with database.
 * 
 * @author jelena
 *
 */
public class UserAudit {

	private final LongProperty auditID;
	private final StringProperty eventType;
	private final StringProperty description;
	private final ObjectProperty<LocalDateTime> time;
	private final IntegerProperty loginID;

	public UserAudit() {
		this(0, null, null, "", 0);
	}

	public UserAudit(long auditID, String eventType, String description, String datetime, int loginID) {
		this.auditID = new SimpleLongProperty(auditID);
		this.eventType = new SimpleStringProperty(eventType);
		this.description = new SimpleStringProperty(description);
		this.time = new SimpleObjectProperty<LocalDateTime>(DateUtil.parse(datetime));
		this.loginID = new SimpleIntegerProperty(loginID);
	}

	public LongProperty getAuditIDProperty() {
		return auditID;
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty getDescriptionProperty() {
		return description;
	}

	public String getEventType() {
		return eventType.get();
	}

	public StringProperty getEventTypeProperty() {
		return eventType;
	}

	public int getLoginID() {
		return loginID.get();
	}

	public IntegerProperty getLoginIDProperty() {
		return loginID;
	}

	public ObjectProperty<LocalDateTime> getTimeProperty() {
		return time;
	}

	public LocalDateTime getTimestamp() {
		return time.get();
	}

	public String getTimestampString() {
		return DateUtil.format(time.get());
	}

	public long getUserAuditID() {
		return auditID.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public void setEventType(String eventType) {
		this.eventType.set(eventType);
	}

	public void setLoginID(int id) {
		this.loginID.set(id);
	}

	public void setTimestamp(LocalDateTime ldt) {
		this.time.set(ldt);
	}

	/**
	 * DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	 * 
	 * @param dateTime
	 */
	public void setTimestamp(String datetime) {
		this.time.set(DateUtil.parse(datetime));
	}

	public void setUserAuditID(long id) {
		this.auditID.set(id);
	}

}
