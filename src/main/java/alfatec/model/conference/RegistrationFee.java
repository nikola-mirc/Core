package alfatec.model.conference;

import java.math.BigDecimal;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import alfatec.model.enums.Currency;

/**
 * Model for database table "registration_fee".
 * 
 * @author jelena
 *
 */
public class RegistrationFee {

	private final IntegerProperty registrationFeeID;
	private final IntegerProperty conferenceID;
	private final StringProperty registrationName;
	private final ObjectProperty<BigDecimal> registrationPrice;
	private final ObjectProperty<Currency> currency;

	public RegistrationFee() {
		this(0, 0, null, 0, null);
	}

	public RegistrationFee(int registrationFeeID, int conferenceID, String registrationName, double registrationPrice,
			String currency) {
		this.registrationFeeID = new SimpleIntegerProperty(registrationFeeID);
		this.conferenceID = new SimpleIntegerProperty(conferenceID);
		this.registrationName = new SimpleStringProperty(registrationName);
		this.registrationPrice = new SimpleObjectProperty<BigDecimal>(BigDecimal.valueOf(registrationPrice));
		this.currency = new SimpleObjectProperty<Currency>(Currency.valueOf(currency.toUpperCase()));
	}

	public int getConferenceID() {
		return conferenceID.get();
	}

	public IntegerProperty getConferenceIDProperty() {
		return conferenceID;
	}

	public Currency getCurrency() {
		return currency.get();
	}

	public ObjectProperty<Currency> getCurrencyProperty() {
		return currency;
	}

	public String getCurrencyString() {
		return currency.get().name();
	}

	public int getRegistrationFeeID() {
		return registrationFeeID.get();
	}

	public IntegerProperty getRegistrationFeeIDProperty() {
		return registrationFeeID;
	}

	public String getRegistrationName() {
		return registrationName.get();
	}

	public StringProperty getRegistrationNameProperty() {
		return registrationName;
	}

	public BigDecimal getRegistrationPrice() {
		return registrationPrice.get();
	}

	public double getRegistrationPriceDouble() {
		return registrationPrice.get().doubleValue();
	}

	public ObjectProperty<BigDecimal> getRegistrationPriceProperty() {
		return registrationPrice;
	}

	public void setConferenceID(int id) {
		this.conferenceID.set(id);
	}

	public void setCurrency(Currency currency) {
		this.currency.set(currency);
	}

	public void setCurrency(String currency) {
		this.currency.set(Currency.valueOf(currency.toUpperCase()));
	}

	public void setRegistrationFeeID(int id) {
		this.registrationFeeID.set(id);
	}

	public void setRegistrationName(String name) {
		this.registrationName.set(name);
	}

	public void setRegistrationPrice(BigDecimal price) {
		this.registrationPrice.set(price);
	}

	public void setRegistrationPrice(double price) {
		this.registrationPrice.set(BigDecimal.valueOf(price));
	}

}
