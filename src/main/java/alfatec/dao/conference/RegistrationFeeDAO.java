package alfatec.dao.conference;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import alfatec.dao.utils.Logging;
import alfatec.dao.utils.TableUtility;
import alfatec.model.conference.RegistrationFee;
import database.Getter;
import database.DatabaseTable;

/**
 * DAO for table "registration_fee".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class RegistrationFeeDAO {

	private static RegistrationFeeDAO instance;

	public static RegistrationFeeDAO getInstance() {
		if (instance == null)
			synchronized (RegistrationFeeDAO.class) {
				if (instance == null)
					instance = new RegistrationFeeDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<RegistrationFee> getRegistration;

	private RegistrationFeeDAO() {
		table = new TableUtility(new DatabaseTable("registration_fee", "registration_id",
				new String[] { "registration_name", "currency", "for_conference", "registration_price" }));
		getRegistration = (ResultSet rs) -> {
			RegistrationFee fee = new RegistrationFee();
			try {
				fee.setRegistrationFeeID(rs.getInt(table.getTable().getPrimaryKey()));
				fee.setRegistrationName(rs.getString(table.getTable().getColumnName(1)));
				fee.setCurrency(rs.getString(table.getTable().getColumnName(2)));
				fee.setConferenceID(rs.getInt(table.getTable().getColumnName(3)));
				fee.setRegistrationPrice(rs.getBigDecimal(table.getTable().getColumnName(4)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return fee;
		};
	}

	public RegistrationFee create(String registrationName, double registrationPrice, String currency) {
		String[] strings = { registrationName, currency };
		int[] ints = { ConferenceDAO.getInstance().getCurrentConference().getConferenceID() };
		RegistrationFee fee = table.create(strings, ints, new double[] { registrationPrice }, getRegistration);
		Logging.getInstance().change("Create", "Add new registration fee " + registrationName);
		return fee;
	}

	public void deleteRegistration(RegistrationFee fee) {
		table.delete(fee.getRegistrationFeeID());
		Logging.getInstance().change("Delete", "Delete registration fee " + fee.getRegistrationName());
	}

	public RegistrationFee getRegistration(int registrationID) {
		return table.findBy(registrationID, getRegistration);
	}

	/**
	 * @return all entries from table (all entries ever)
	 */
	public ObservableList<RegistrationFee> getAll() {
		return table.getAll(getRegistration);
	}

	/**
	 * @param conferenceID
	 * @return all entries for specified conference
	 */
	public ObservableList<RegistrationFee> getAllForConference(int conferenceID) {
		return table.findBy(conferenceID, 3, getRegistration);
	}

	/**
	 * @return all fees for current conference
	 */
	public ObservableList<RegistrationFee> getCurrentFees() {
		try {
			return getAllForConference(ConferenceDAO.getInstance().getCurrentConference().getConferenceID());
		} catch (NullPointerException e) {
			return null;
		}
	}

	public void updateCurrency(RegistrationFee fee, String currency) {
		String past = fee.getCurrencyString();
		table.update(fee.getRegistrationFeeID(), 2, currency);
		fee.setCurrency(currency);
		Logging.getInstance().change("Update", "Update currency from " + past + " to " + currency);
	}

	public void updatePrice(RegistrationFee fee, double price) {
		String past = fee.getRegistrationPrice().toString();
		table.update(fee.getRegistrationFeeID(), 4, price);
		fee.setRegistrationPrice(price);
		Logging.getInstance().change("Update", "Update price from " + past + " to " + price);
	}

	public void updateRegistrationName(RegistrationFee fee, String name) {
		String past = fee.getRegistrationName();
		table.update(fee.getRegistrationFeeID(), 1, name);
		fee.setRegistrationName(name);
		Logging.getInstance().change("Update", "Update registration name from " + past + " to " + name);
	}

}
