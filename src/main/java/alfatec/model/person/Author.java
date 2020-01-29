package alfatec.model.person;

import alfatec.model.enums.Institution;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import util.BooleanUtil;

/**
 * Model for database table "author".
 * 
 * Holds info about authors.
 * 
 * @author jelena
 *
 */
public class Author extends Person {

	private final ObjectProperty<Institution> institution;
	private final IntegerProperty validateEmail;

	public Author() {
		super();
		this.institution = new SimpleObjectProperty<Institution>(Institution.UNIVERSITY);
		this.validateEmail = new SimpleIntegerProperty();
	}

	public Author(long authorID, int countryID, String firstName, String lastName, String email, String institutionType,
			String institutionName, String note, boolean validateEmail) {
		super(authorID, countryID, firstName, lastName, email, institutionName, note);
		this.institution = new SimpleObjectProperty<Institution>(Institution.lookUpByName(institutionType));
		this.validateEmail = new SimpleIntegerProperty(BooleanUtil.parse(validateEmail));
	}

	public String getAuthorEmail() {
		return getEmail();
	}

	public StringProperty getAuthorEmailProperty() {
		return getPersonEmailProperty();
	}

	public String getAuthorFirstName() {
		return getFirstName();
	}

	public StringProperty getAuthorFirstNameProperty() {
		return getPersonFirstNameProperty();
	}

	public long getAuthorID() {
		return getPersonID();
	}

	public LongProperty getAuthorIDProperty() {
		return getPersonIDProperty();
	}

	public String getAuthorLastName() {
		return getLastName();
	}

	public StringProperty getAuthorLastNameProperty() {
		return getPersonLastNameProperty();
	}

	public Institution getInstitution() {
		return institution.get();
	}

	public ObjectProperty<Institution> getInstitutionProperty() {
		return institution;
	}

	public String getInstitutionType() {
		return institution.getName();
	}

	public void setAuthorEmail(String email) {
		setEmail(email);
	}

	public void setAuthorFirstName(String name) {
		setFirstName(name);
	}

	public void setAuthorID(long id) {
		setPersonID(id);
	}

	public void setAuthorLastName(String surname) {
		setLastName(surname);
	}

	public void setInstitution(Institution institution) {
		this.institution.setValue(institution);
	}

	public void setInstitutionType(String type) {
		this.institution.setValue(Institution.lookUpByName(type));
	}

	public IntegerProperty getValidateEmailProperty() {
		return validateEmail;
	}

	public int getValidateEmail() {
		return validateEmail.get();
	}

	public boolean isValidEmail() {
		return BooleanUtil.checkNumber(getValidateEmail());
	}

	public void setValidateEmail(int valid) {
		this.validateEmail.set(valid);
	}

	public void setIsValidEmail(boolean valid) {
		this.validateEmail.set(BooleanUtil.parse(valid));
	}
}
