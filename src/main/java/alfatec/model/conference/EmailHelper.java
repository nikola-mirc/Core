package alfatec.model.conference;

import java.io.File;
import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import util.DateUtil;

public class EmailHelper {

	private final IntegerProperty emailID;
	private final IntegerProperty conferenceID;
	private final ObjectProperty<LocalDateTime> time;
	private final ObjectProperty<File> message;
	private final IntegerProperty sent;
	private final IntegerProperty invite;

	public EmailHelper() {
		this(0, 0, "", null, 0, 0);
	}

	public EmailHelper(int emailID, int conferenceID, String time, File message, int sent, int invite) {
		this.emailID = new SimpleIntegerProperty(emailID);
		this.conferenceID = new SimpleIntegerProperty(conferenceID);
		this.time = new SimpleObjectProperty<LocalDateTime>(DateUtil.parse(time));
		this.message = new SimpleObjectProperty<File>(message);
		this.sent = new SimpleIntegerProperty(sent);
		this.invite = new SimpleIntegerProperty(invite);
	}

	public IntegerProperty getEmailIDProperty() {
		return emailID;
	}

	public int getEmailID() {
		return emailID.get();
	}

	public void setEmailID(int id) {
		this.emailID.set(id);
	}

	public IntegerProperty getConferenceIDProperty() {
		return conferenceID;
	}

	public int getConferenceID() {
		return conferenceID.get();
	}

	public void setConferenceID(int id) {
		this.conferenceID.set(id);
	}

	public ObjectProperty<LocalDateTime> getTimeProperty() {
		return time;
	}

	public LocalDateTime getLocalDateTime() {
		return time.get();
	}

	public void setLocalDateTime(LocalDateTime ldt) {
		this.time.set(ldt);
	}

	public String getTime() {
		return DateUtil.format(getLocalDateTime());
	}

	public void setTime(String time) {
		setLocalDateTime(DateUtil.parse(time));
	}

	public ObjectProperty<File> getMessageProperty() {
		return message;
	}

	public File getFile() {
		return message.get();
	}

	public void setFile(File file) {
		this.message.set(file);
	}

	public IntegerProperty getSentProperty() {
		return sent;
	}

	public int getCount() {
		return sent.get();
	}

	public void setCount(int sent) {
		this.sent.set(sent);
	}

	public IntegerProperty getInviteProperty() {
		return invite;
	}

	public int getOrdinal() {
		return invite.get();
	}

	public void setOrdinal(int invite) {
		this.invite.set(invite);
	}

}
