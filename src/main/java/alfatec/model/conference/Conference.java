package alfatec.model.conference;

import java.io.File;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.BooleanUtil;

/**
 * Model for database table "conference".
 * 
 * Holds basic info about conference.
 * 
 * @author jelena
 *
 */
public class Conference {

	private final IntegerProperty conferenceID;
	private final StringProperty conferenceTitle;
	private final IntegerProperty fieldID;
	private final StringProperty conferenceEmail;
	private final StringProperty emailPassword;
	private final StringProperty note;
	private final ObjectProperty<File> report;
	private final IntegerProperty realized;
	private final StringProperty conferenceBcc;

	public Conference() {
		this(0, null, 0, null, null, null, null, null, false);
	}

	public Conference(int conferenceID, String conferenceTitle, int fieldID, String conferenceEmail,
			String emailPassword, String conferenceBcc, String note, File report, boolean realized) {
		this.conferenceID = new SimpleIntegerProperty(conferenceID);
		this.conferenceTitle = new SimpleStringProperty(conferenceTitle);
		this.fieldID = new SimpleIntegerProperty(fieldID);
		this.conferenceEmail = new SimpleStringProperty(conferenceEmail);
		this.emailPassword = new SimpleStringProperty(emailPassword);
		this.conferenceBcc = new SimpleStringProperty(conferenceBcc);
		this.note = new SimpleStringProperty(note);
		this.report = new SimpleObjectProperty<File>(report);
		this.realized = new SimpleIntegerProperty(BooleanUtil.parse(realized));
	}

	public String getConferenceEmail() {
		return conferenceEmail.get();
	}

	public String getConferenceEmailPassword() {
		return emailPassword.get();
	}

	public StringProperty getConferenceEmailProperty() {
		return conferenceEmail;
	}

	public int getConferenceID() {
		return conferenceID.get();
	}

	public IntegerProperty getConferenceIDProperty() {
		return conferenceID;
	}

	public String getConferenceTitle() {
		return conferenceTitle.get();
	}

	public StringProperty getConferenceTitleProperty() {
		return conferenceTitle;
	}

	public StringProperty getEmailPasswordProperty() {
		return emailPassword;
	}

	public int getFieldID() {
		return fieldID.get();
	}

	public IntegerProperty getFieldIDProperty() {
		return fieldID;
	}

	public String getNote() {
		return note.get();
	}

	public StringProperty getNoteProperty() {
		return note;
	}

	public int getRealized() {
		return realized.get();
	}

	public IntegerProperty getRealizedProperty() {
		return realized;
	}

	public File getReport() {
		return report.get();
	}

	public ObjectProperty<File> getReportProperty() {
		return report;
	}

	public boolean isRealized() {
		return BooleanUtil.checkNumber(realized.get());
	}

	public void setConferenceEmail(String email) {
		this.conferenceEmail.set(email);
	}

	/**
	 * to do: hash password
	 * 
	 * @param regularPassword
	 */
	public void setConferenceEmailPassword(String regularPassword) {
		this.emailPassword.set(regularPassword);
	}

	/**
	 * will read hashed password from db and set it to the object
	 * 
	 * @param password result set get string method
	 */
	public void readPasswordFromDB(String password) {
		this.emailPassword.set(password);
	}

	public void setConferenceID(int id) {
		this.conferenceID.set(id);
	}

	public void setConferenceTitle(String title) {
		this.conferenceTitle.set(title);
	}

	public void setFieldID(int id) {
		this.fieldID.set(id);
	}

	public void setIsRealized(boolean bool) {
		this.realized.set(BooleanUtil.parse(bool));
	}

	public void setNote(String note) {
		this.note.set(note);
	}

	public void setRealized(int isRealized) {
		this.realized.set(isRealized);
	}

	public void setReport(File report) {
		this.report.set(report);
	}

	public void setReportFile(String path) {
		this.report.set(new File(path));
	}

	public StringProperty getConferenceBccProperty() {
		return conferenceBcc;
	}

	public String getConferenceBcc() {
		return conferenceBcc.get();
	}

	public void setConferenceBcc(String conferenceBcc) {
		this.conferenceBcc.set(conferenceBcc);
	}

}
