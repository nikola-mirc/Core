package alfatec.model.person;

import alfatec.dao.country.CountryDAO;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Abstract class Person - for author and reviewer.
 * 
 * @author jelena
 *
 */
abstract class Person {

	private final LongProperty personID;
	private final IntegerProperty countryID;
	private final StringProperty personFirstName;
	private final StringProperty personLastName;
	private final StringProperty personEmail;
	private final StringProperty institutionName;
	private final StringProperty note;

	protected Person() {
		this(0, 0, null, null, null, null, null);
	}

	protected Person(long personID, int countryID, String firstName, String lastName, String email,
			String institutionName, String note) {
		this.personID = new SimpleLongProperty(personID);
		this.countryID = new SimpleIntegerProperty(countryID);
		this.personFirstName = new SimpleStringProperty(firstName);
		this.personLastName = new SimpleStringProperty(lastName);
		this.personEmail = new SimpleStringProperty(email);
		this.institutionName = new SimpleStringProperty(institutionName);
		this.note = new SimpleStringProperty(note);
	}

	public int getCountryID() {
		return countryID.get();
	}
	
	public StringProperty countryProperty() {
		return CountryDAO.getInstance().getCountry(getCountryID()).getCountryNameProperty();
	}

	public IntegerProperty getCountryIDProperty() {
		return countryID;
	}

	protected String getEmail() {
		return personEmail.get();
	}

	protected String getFirstName() {
		return personFirstName.get();
	}

	public String getInstitutionName() {
		return institutionName.get();
	}

	public StringProperty getInstitutionNameProperty() {
		return institutionName;
	}

	protected String getLastName() {
		return personLastName.get();
	}

	public String getNote() {
		return note.get();
	}

	public StringProperty getNoteProperty() {
		return note;
	}

	protected StringProperty getPersonEmailProperty() {
		return personEmail;
	}

	protected StringProperty getPersonFirstNameProperty() {
		return personFirstName;
	}

	protected long getPersonID() {
		return personID.get();
	}

	protected LongProperty getPersonIDProperty() {
		return personID;
	}

	protected StringProperty getPersonLastNameProperty() {
		return personLastName;
	}

	public void setCountryID(int id) {
		this.countryID.set(id);
	}

	protected void setEmail(String email) {
		this.personEmail.set(email);
	}

	protected void setFirstName(String name) {
		this.personFirstName.set(name);
	}

	public void setInstitutionName(String name) {
		this.institutionName.set(name);
	}

	protected void setLastName(String surname) {
		this.personLastName.set(surname);
	}

	public void setNote(String note) {
		this.note.set(note);
	}

	protected void setPersonID(long id) {
		this.personID.set(id);
	}
}
