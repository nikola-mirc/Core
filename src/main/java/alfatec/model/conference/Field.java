package alfatec.model.conference;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model for database table "field".
 * 
 * There are 3 fields inserted: Energetics, Knowledge Management, Information
 * Theory.
 * 
 * @author jelena
 *
 */
public class Field {

	private final IntegerProperty fieldID;
	private final StringProperty fieldName;

	public Field() {
		this(0, null);
	}

	public Field(int fieldID, String fieldName) {
		this.fieldID = new SimpleIntegerProperty(fieldID);
		this.fieldName = new SimpleStringProperty(fieldName);
	}

	public int getFieldID() {
		return fieldID.get();
	}

	public IntegerProperty getFieldIDProperty() {
		return fieldID;
	}

	public String getFieldName() {
		return fieldName.get();
	}

	public StringProperty getFieldNameProperty() {
		return fieldName;
	}

	public void setFieldID(int id) {
		this.fieldID.set(id);
	}

	public void setFieldName(String name) {
		this.fieldName.set(name);
	}

}
