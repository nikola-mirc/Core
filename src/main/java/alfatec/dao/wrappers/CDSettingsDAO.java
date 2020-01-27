package alfatec.dao.wrappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.conference.DatesDAO;
import alfatec.model.conference.Conference;
import alfatec.model.conference.Dates;
import alfatec.view.wrappers.ConferenceDateSettings;
import database.DatabaseUtility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Inner join table for Conference and their dates plan
 * 
 * @author jelena
 *
 */

public class CDSettingsDAO {

	private static CDSettingsDAO instance;

	private CDSettingsDAO() {
	}

	public static CDSettingsDAO getInstance() {
		if (instance == null)
			synchronized (CDSettingsDAO.class) {
				if (instance == null)
					instance = new CDSettingsDAO();
			}
		return instance;
	}

	public ObservableList<ConferenceDateSettings> getAllData() {
		String query = "SELECT * FROM dates INNER JOIN conference WHERE dates.conference_id = conference.conference_id";
		ResultSet resultSet = null;
		ObservableList<ConferenceDateSettings> dataList = FXCollections.observableArrayList();
		try {
			resultSet = DatabaseUtility.getInstance().executeQuery(query);
			dataList = getUserData(resultSet);
			return dataList;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dataList;
	}

	private static ObservableList<ConferenceDateSettings> getUserData(ResultSet rs)
			throws SQLException, ClassNotFoundException {
		ObservableList<ConferenceDateSettings> dataList = FXCollections.observableArrayList();
		while (rs.next()) {
			Conference conference = ConferenceDAO.getInstance().getter(rs);
			Dates dates = DatesDAO.getInstance().getter(rs);
			ConferenceDateSettings cd = new ConferenceDateSettings(conference, dates);
			dataList.add(cd);
		}
		return dataList;
	}
}
