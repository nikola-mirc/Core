package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Methods to format LocalDate and LocalDateTime in MySQL acceptable format and
 * vice versa.
 * 
 * @author jelena
 *
 */
public class DateUtil {

	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

	/**
	 * @param date - LocalDate object
	 * @return MySQL acceptable String
	 */
	public static String format(LocalDate date) {
		if (date == null) {
			return null;
		}
		return DATE_FORMATTER.format(date);
	}

	/**
	 * @param dateTime - LocalDateTime object
	 * @return MySQL acceptable String
	 */
	public static String format(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return DATE_TIME_FORMATTER.format(dateTime);
	}

	/**
	 * @param dateTimeString
	 * @return LocalDateTime object
	 */
	public static LocalDateTime parse(String dateTimeString) {
		try {
			return DATE_TIME_FORMATTER.parse(dateTimeString, LocalDateTime::from);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	/**
	 * @param date
	 * @return LocalDate object
	 */
	public static LocalDate parseDate(String date) {
		try {
			return DATE_FORMATTER.parse(date, LocalDate::from);
		} catch (DateTimeParseException | NullPointerException e) {
			return null;
		}
	}

	/**
	 * @param date
	 * @return true if parameter is not null and is in MySQL acceptable format
	 */
	public static boolean validDate(String date) {
		return DateUtil.parseDate(date) != null;
	}

	/**
	 * @param dateTimeString
	 * @return true if parameter is not null and is in MySQL acceptable format
	 */
	public static boolean validDateTime(String dateTimeString) {
		return DateUtil.parse(dateTimeString) != null;
	}
}
