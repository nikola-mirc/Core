package alfatec.model.country;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model for database table "country".
 * 
 * There are 255 countries with iso alpha 2 code inserted.
 * 
 * @author jelena
 *
 */
public class Country {

	private final IntegerProperty countryID;
	private final StringProperty isoAlpha2Code;
	private final StringProperty countryName;

	public Country() {
		this(0, null, null);
	}

	public Country(int countryID, String isoAlpha2Code, String countryName) {
		this.countryID = new SimpleIntegerProperty(countryID);
		this.isoAlpha2Code = new SimpleStringProperty(isoAlpha2Code);
		this.countryName = new SimpleStringProperty(countryName);
	}

	public int getCountryID() {
		return countryID.get();
	}

	public IntegerProperty getCountryIDProperty() {
		return countryID;
	}

	public String getCountryName() {
		return countryName.get();
	}

	public StringProperty getCountryNameProperty() {
		return countryName;
	}

	public String getIsoAlpha2Code() {
		return isoAlpha2Code.get();
	}

	public StringProperty getIsoAlpha2CodeProperty() {
		return isoAlpha2Code;
	}

	public void setCountryID(int id) {
		this.countryID.set(id);
	}

	public void setCountryName(String name) {
		this.countryName.set(name);
	}

	public void setIsoAlpha2Code(String code) {
		this.isoAlpha2Code.set(code);
	}
	
	@Override
		public String toString() {
			return getCountryName();
		}
}
