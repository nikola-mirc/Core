package alfatec.model.person;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model for database table "reviewer".
 * 
 * Holds info about reviewers.
 * 
 * @author jelena
 *
 */
public class Reviewer extends Person {

	private final StringProperty contactTelephone;

	public Reviewer() {
		super();
		this.contactTelephone = new SimpleStringProperty(null);
	}

	public Reviewer(int reviewerID, int countryID, String firstName, String lastName, String email,
			String contactTelephone, String institutionName, String note) {
		super(reviewerID, countryID, firstName, lastName, email, institutionName, note);
		this.contactTelephone = new SimpleStringProperty(contactTelephone);
	}

	public String getContactTelephone() {
		return contactTelephone.get();
	}

	public StringProperty getContectTelephoneProperty() {
		return contactTelephone;
	}

	public String getReviewerEmail() {
		return getEmail();
	}

	public StringProperty getReviewerEmailProperty() {
		return getPersonEmailProperty();
	}

	public String getReviewerFirstName() {
		return getFirstName();
	}

	public StringProperty getReviewerFirstNameProperty() {
		return getPersonFirstNameProperty();
	}

	public int getReviewerID() {
		return (int) getPersonID();
	}

	public LongProperty getReviewerIDProperty() {
		return getPersonIDProperty();
	}

	public String getReviewerLastName() {
		return getLastName();
	}

	public StringProperty getReviewerLastNameProperty() {
		return getPersonLastNameProperty();
	}

	public void setContactTelephone(String number) {
		this.contactTelephone.set(number);
	}

	public void setReviewerEmail(String email) {
		setEmail(email);
	}

	public void setReviewerFirstName(String name) {
		setFirstName(name);
	}

	public void setReviewerID(int id) {
		setPersonID(id);
	}

	public void setReviewerLastName(String surname) {
		setLastName(surname);
	}
}
