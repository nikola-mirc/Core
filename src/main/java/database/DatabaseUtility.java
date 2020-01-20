package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

/**
 * Connect application with database using jdbc driver. Database info is set in
 * database.properties file. Pattern: thread safe singleton, with double-checked
 * locking.
 * 
 * @author jelena
 *
 */
public class DatabaseUtility {

	private static final String DB_USERNAME = "db.username";
	private static final String DB_PASSWORD = "db.password";
	private static final String DB_URL = "db.url";
	private static final String DB_DRIVER_CLASS = "driver.class.name";

	private static Properties properties = null;
	private static DatabaseUtility instance;

	/**
	 * @return instance of the DatabaseUtility class
	 */
	public static DatabaseUtility getInstance() {
		if (instance == null)
			synchronized (DatabaseUtility.class) {
				if (instance == null)
					instance = new DatabaseUtility();
			}
		return instance;
	}

	private Connection connection;

	private DatabaseUtility() {
		try {
			properties = new Properties();
			properties.load(new FileInputStream("src/main/java/database/database.properties"));
			Class.forName(properties.getProperty(DB_DRIVER_CLASS));
			connection = DriverManager.getConnection(properties.getProperty(DB_URL),
					properties.getProperty(DB_USERNAME), properties.getProperty(DB_PASSWORD));
		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void databaseDisconnect() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Execute query should be use for "SELECT" (read)
	 * 
	 * @param query
	 * @return
	 */
	public ResultSet executeQuery(String query) {
		Statement statement = null;
		ResultSet resultSet = null;
		CachedRowSet crs = null;
		try {
			statement = getConnection().createStatement();
			resultSet = statement.executeQuery(query);
			crs = RowSetProvider.newFactory().createCachedRowSet();
			crs.populate(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return crs;
	}

	/**
	 * Execute update query should be used for create, update, delete
	 * 
	 * @param query
	 */
	public void executeUpdateQuery(String query) {
		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}
}
