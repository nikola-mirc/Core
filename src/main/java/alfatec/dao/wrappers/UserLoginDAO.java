package alfatec.dao.wrappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import alfatec.dao.user.LoginDataDAO;
import alfatec.dao.user.UserDAO;
import alfatec.model.user.LoginData;
import alfatec.model.user.User;
import alfatec.view.wrappers.UserLoginConnection;
import database.DatabaseUtility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserLoginDAO {

	private static UserLoginDAO instance;

	public static UserLoginDAO getInstance() {
		if (instance == null)
			synchronized (UserLoginDAO.class) {
				if (instance == null)
					instance = new UserLoginDAO();
			}
		return instance;
	}

	public ObservableList<UserLoginConnection> getAllData() {
		String query = "SELECT * FROM login_data INNER JOIN user WHERE login_data.user_id = user.user_id";
		ResultSet resultSet = null;
		ObservableList<UserLoginConnection> dataList = FXCollections.observableArrayList();
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

	private static ObservableList<UserLoginConnection> getUserData(ResultSet rs)
			throws SQLException, ClassNotFoundException {
		ObservableList<UserLoginConnection> dataList = FXCollections.observableArrayList();
		while (rs.next()) {
			User user = UserDAO.getInstance().getter(rs);
			LoginData login = LoginDataDAO.getInstance().getter(rs);
			UserLoginConnection ul = new UserLoginConnection(user, login);
			dataList.add(ul);
		}
		return dataList;
	}
}
