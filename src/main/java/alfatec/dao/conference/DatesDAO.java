package alfatec.dao.conference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javafx.collections.ObservableList;
import alfatec.dao.utils.TableUtility;
import alfatec.model.conference.Dates;
import database.Getter;
import database.DatabaseTable;
import util.DateUtil;

/**
 * DAO for table "dates".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class DatesDAO {

	private static DatesDAO instance;
	public static DatesDAO getInstance() {
		if (instance == null)
			synchronized (DatesDAO.class) {
				if (instance == null)
					instance = new DatesDAO();
			}
		return instance;
	}
	private final TableUtility table;

	private Getter<Dates> getDate;

	private DatesDAO() {
		table = new TableUtility(new DatabaseTable("dates", "dates_id", new String[] { "start_date", "first_call_date",
				"second_call_date", "third_call_date", "end_date", "conference_id" }));
		getDate = new Getter<Dates>() {
			@Override
			public Dates get(ResultSet rs) {
				Dates dates = new Dates();
				try {
					dates.setDatesID(rs.getInt(table.getTable().getPrimaryKey()));
					dates.setStartDate(rs.getTimestamp(table.getTable().getColumnName(1)).toLocalDateTime());
					dates.setFirstCallDate(rs.getDate(table.getTable().getColumnName(2)).toLocalDate());
					dates.setSecondCallDate(rs.getDate(table.getTable().getColumnName(3)).toLocalDate());
					dates.setThirdCallDate(rs.getDate(table.getTable().getColumnName(4)).toLocalDate());
					dates.setEndDate(rs.getDate(table.getTable().getColumnName(5)).toLocalDate());
					dates.setConferenceID(rs.getInt(table.getTable().getColumnName(6)));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return dates;
			}
		};
	}

	/**
	 * DATE_PATTERN = "yyyy-MM-dd"
	 */
	public Dates create(String firstCallDate, String secondCallDate, String thirdCallDate) {
		String[] strings = { DateUtil.format(LocalDateTime.now()), firstCallDate, secondCallDate, thirdCallDate, null };
		int[] ints = { ConferenceDAO.getInstance().getCurrentConference().getConferenceID() };
		return table.create(strings, ints, new long[] {}, getDate);
	}

	public void delete(Dates date) {
		table.delete(date.getDatesID());
	}

	/**
	 * @return all dates from the table (all dates ever)
	 */
	public ObservableList<Dates> getAll() {
		return table.getAll(getDate);
	}

	/**
	 * Full text search in natural language
	 * 
	 * @return dates for current conference
	 */
	public Dates getCurrent() {
		return table.findByFulltext(null, getDate, 5);
	}

	/**
	 * Search table by primary key - id
	 */
	public Dates getDates(int datesID) {
		return table.findBy(datesID, getDate);
	}

	/**
	 * @param conferenceID
	 * @return all dates for specified conference
	 */
	public Dates getDatesForConference(int conferenceID) {
		return table.findBy(conferenceID, 6, getDate).get(0);
	}

	/**
	 * @param date DATE_PATTERN = "yyyy-MM-dd"
	 */
	public void updateFirstCall(String date) {
		table.update(getCurrent().getDatesID(), 2, date);
		getCurrent().setFirstCallDate(date);
	}

	/**
	 * @param date DATE_PATTERN = "yyyy-MM-dd"
	 */
	public void updateSecondCall(String date) {
		table.update(getCurrent().getDatesID(), 3, date);
		getCurrent().setSecondCallDate(date);
	}

	/**
	 * @param date DATE_PATTERN = "yyyy-MM-dd"
	 */
	public void updateThirdCall(String date) {
		table.update(getCurrent().getDatesID(), 4, date);
		getCurrent().setThirdCallDate(date);
	}

}
