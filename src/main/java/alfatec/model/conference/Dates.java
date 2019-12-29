package alfatec.model.conference;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import util.DateUtil;

/**
 * Model for database table "dates".
 * 
 * Represent dates related to current conference.
 * 
 * @author jelena
 *
 */
public class Dates {

	private final IntegerProperty datesID;
	private final IntegerProperty conferenceID;
	private final ObjectProperty<LocalDateTime> startDate;
	private final ObjectProperty<LocalDate> firstCallDate;
	private final ObjectProperty<LocalDate> secondCallDate;
	private final ObjectProperty<LocalDate> thirdCallDate;
	private final ObjectProperty<LocalDate> endDate;

	public Dates() {
		this(0, 0, "", null, null, null, null);
	}

	public Dates(int datesID, int conferenceID, String startDate, String firstCallDate, String secondCallDate,
			String thirdCallDate, String endDate) {
		this.datesID = new SimpleIntegerProperty(datesID);
		this.conferenceID = new SimpleIntegerProperty(conferenceID);
		this.startDate = new SimpleObjectProperty<LocalDateTime>(DateUtil.parse(startDate));
		this.firstCallDate = new SimpleObjectProperty<LocalDate>(DateUtil.parseDate(firstCallDate));
		this.secondCallDate = new SimpleObjectProperty<LocalDate>(DateUtil.parseDate(secondCallDate));
		this.thirdCallDate = new SimpleObjectProperty<LocalDate>(DateUtil.parseDate(thirdCallDate));
		this.endDate = new SimpleObjectProperty<LocalDate>(DateUtil.parseDate(endDate));
	}

	public int getConferenceID() {
		return conferenceID.get();
	}

	public IntegerProperty getConferenceIDProperty() {
		return conferenceID;
	}

	public int getDatesID() {
		return datesID.get();
	}

	public IntegerProperty getDatesIDProperty() {
		return datesID;
	}

	public LocalDate getEndDate() {
		return endDate.get();
	}

	public ObjectProperty<LocalDate> getEndDateProperty() {
		return endDate;
	}

	public String getEndDateString() {
		return DateUtil.format(endDate.get());
	}

	public LocalDate getFirstCallDate() {
		return firstCallDate.get();
	}

	public ObjectProperty<LocalDate> getFirstCallDateProperty() {
		return firstCallDate;
	}

	public String getFirstCallString() {
		return DateUtil.format(firstCallDate.get());
	}

	public LocalDate getSecondCallDate() {
		return secondCallDate.get();
	}

	public ObjectProperty<LocalDate> getSecondCallDateProperty() {
		return secondCallDate;
	}

	public String getSecondCallString() {
		return DateUtil.format(secondCallDate.get());
	}

	public LocalDateTime getStartDate() {
		return startDate.get();
	}

	public ObjectProperty<LocalDateTime> getStartDateProperty() {
		return startDate;
	}

	public String getStartTimestamp() {
		return DateUtil.format(startDate.get());
	}

	public LocalDate getThirdCallDate() {
		return thirdCallDate.get();
	}

	public ObjectProperty<LocalDate> getThirdCallDateProperty() {
		return thirdCallDate;
	}

	public String getThirdCallString() {
		return DateUtil.format(thirdCallDate.get());
	}

	public void setConferenceID(int id) {
		this.conferenceID.set(id);
	}

	public void setDatesID(int id) {
		this.datesID.set(id);
	}

	public void setEndDate(LocalDate date) {
		this.endDate.set(date);
	}

	/**
	 * DATE_PATTERN = "yyyy-MM-dd";
	 * 
	 * @param date
	 */
	public void setEndDate(String date) {
		this.endDate.set(DateUtil.parseDate(date));
	}

	public void setFirstCallDate(LocalDate date) {
		this.firstCallDate.set(date);
	}

	/**
	 * DATE_PATTERN = "yyyy-MM-dd";
	 * 
	 * @param date
	 */
	public void setFirstCallDate(String date) {
		this.firstCallDate.set(DateUtil.parseDate(date));
	}

	public void setSecondCallDate(LocalDate date) {
		this.secondCallDate.set(date);
	}

	/**
	 * DATE_PATTERN = "yyyy-MM-dd";
	 * 
	 * @param date
	 */
	public void setSecondCallDate(String date) {
		this.secondCallDate.set(DateUtil.parseDate(date));
	}

	public void setStartDate(LocalDateTime ldt) {
		this.startDate.set(ldt);
	}

	/**
	 * DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	 * 
	 * @param dateTime
	 */
	public void setStartTimestamp(String dateTime) {
		this.startDate.set(DateUtil.parse(dateTime));
	}

	public void setThirdCallDate(LocalDate date) {
		this.thirdCallDate.set(date);
	}

	/**
	 * DATE_PATTERN = "yyyy-MM-dd";
	 * 
	 * @param date
	 */
	public void setThirdCallDate(String date) {
		this.thirdCallDate.set(DateUtil.parseDate(date));
	}

}
