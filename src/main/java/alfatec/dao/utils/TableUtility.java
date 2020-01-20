package alfatec.dao.utils;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alfatec.model.enums.Institution;
import alfatec.model.person.Author;
import database.CRUD;
import database.DatabaseTable;
import database.Getter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Store generic methods that use CRUD, DatabaseTable model and generic
 * interface Getter to describe methods required for DAO.
 * 
 * @author jelena
 *
 */

public class TableUtility {

	private final DatabaseTable table;

	/**
	 * DI table
	 * 
	 * @param table DatabaseTable
	 */
	public TableUtility(DatabaseTable table) {
		this.table = table;
	}

	/**
	 * Insert in database object with blob
	 * 
	 * @param <T>       type of object - table that holds blob
	 * @param blobPath  file path to the file
	 * @param blobIndex index of column in the table that holds blob
	 * @param strings   String values of object
	 * @param ints      integer values of object
	 * @param get       matching functional interface
	 * @return object of type T
	 */
	public <T> T create(String blobPath, int blobIndex, String[] strings, int[] ints, long[] longs, Getter<T> get) {
		try {
			long id = CRUD.create(table.getTableName(), table.getColumnNames(), strings, ints, longs);
			CRUD.updateBlob(table.getTableName(), table.getColumnName(blobIndex), blobPath, table.getPrimaryKey(), id);
			return findBy(id, get);
		} catch (SQLException | FileNotFoundException e) {
			System.out.println("EXCEPTION " + e.getMessage());
			return null;
		}
	}

	public Author create(String[] strings, int[] ints, Institution institution, Getter<Author> get) {
		try {
			long id = CRUD.create(table.getTableName(), table.getColumnNames(), strings, ints, institution);
			return findBy(id, get);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Create - insert in database - object of type T
	 * 
	 * @param <T>        type of object
	 * @param strings    String values of object
	 * @param ints       integer values of object
	 * @param bigDecimal float values of object
	 * @param get        matching functional interface
	 * @return object of type T
	 */
	public <T> T create(String[] strings, int[] ints, double[] bigDecimal, Getter<T> get) {
		try {
			long id = CRUD.create(table.getTableName(), table.getColumnNames(), strings, ints, bigDecimal);
			return findBy(id, get);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Create - insert in database - object of type T
	 * 
	 * @param <T>     type of object
	 * @param strings String values of object
	 * @param ints    integer values of object
	 * @param get     matching functional interface
	 * @return object of type T
	 */
	public <T> T create(String[] strings, int[] ints, long[] longs, Getter<T> get) {
		try {
			long id = CRUD.create(table.getTableName(), table.getColumnNames(), strings, ints, longs);
			return findBy(id, get);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Delete object (row from database) with given primary key value
	 * 
	 * @param id primary key value
	 */
	public void delete(long id) {
		CRUD.delete(table.getTableName(), table.getPrimaryKey(), id);
	}

	/**
	 * Search by primary key
	 * 
	 * @param <T> type of object
	 * @param id  value of primary key
	 * @param get matching functional interface
	 * @return object of type T indexed with id
	 */
	public <T> T findBy(long id, Getter<T> get) {
		ResultSet rs = CRUD.findByNumber(table.getTableName(), table.getPrimaryKey(), id);
		try {
			if (rs.next())
				return get.get(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Search by some integer value
	 * 
	 * @param <T>        type of object
	 * @param foreignKey integer value to search
	 * @param column     index of column in table that holds observed integer
	 * @param get        matching functional interface
	 * @return observable list of objects type T
	 */
	public <T> ObservableList<T> findBy(long foreignKey, int column, Getter<T> get) {
		ObservableList<T> list = FXCollections.observableArrayList();
		ResultSet rs = CRUD.findByNumber(table.getTableName(), table.getColumnName(column), foreignKey);
		try {
			while (rs.next())
				list.add(get.get(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Full text search in natural language mode
	 * 
	 * @param <T>    type of object that represent given table
	 * @param string String to search for
	 * @param get    matching functional interface
	 * @param column index of column in table which will be searched
	 * @return object of type T that matches against given criteria
	 */
	public <T> T findByFulltext(String string, Getter<T> get, int column) {
		ResultSet rs = CRUD.read(table.getTableName(), table.getColumnName(column), string);
		try {
			if (rs.next())
				return get.get(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Conjunction search - where given column names have given integer values
	 * 
	 * @param <T>         type of object
	 * @param columnNames array of name of the columns
	 * @param values      array of desirable integers
	 * @param get         matching functional interface
	 * @return observable list of found objects
	 */
	public <T> ObservableList<T> findWhere(String[] columnNames, long[] values, Getter<T> get) {
		ObservableList<T> list = FXCollections.observableArrayList();
		ResultSet rs = CRUD.read(table.getTableName(), columnNames, values);
		try {
			while (rs.next())
				list.add(get.get(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Conjunction search - where given column names have given String values
	 * 
	 * @param <T>         type of object
	 * @param columnNames array of name of the columns
	 * @param values      array of desirable strings
	 * @param get         matching functional interface
	 * @return observable list of found objects
	 */
	public <T> ObservableList<T> findWhere(String[] columnNames, String[] values, Getter<T> get) {
		ObservableList<T> list = FXCollections.observableArrayList();
		ResultSet rs = CRUD.read(table.getTableName(), columnNames, values);
		try {
			while (rs.next())
				list.add(get.get(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Get all objects (rows) of type T (from DB table represented with T)
	 * 
	 * @param <T>     type of object
	 * @param gettter matching functional interface
	 * @return observable list of objects of type T
	 */
	public <T> ObservableList<T> getAll(Getter<T> gettter) {
		ObservableList<T> list = FXCollections.observableArrayList();
		ResultSet rs = CRUD.read(table.getTableName());
		try {
			while (rs.next()) {
				list.add(gettter.get(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ObservableList<String> getColumn(int index) {
		ObservableList<String> list = FXCollections.observableArrayList();
		ResultSet rs = CRUD.readColumn(table.getTableName(), table.getColumnName(index));
		try {
			while (rs.next())
				list.add(rs.getString(table.getColumnName(index)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param <T>          type of object
	 * @param country      name of the country
	 * @param get          matching functional interface
	 * @param countryLabel index of country column in the table
	 * @return observable list of objects of type T "from" specified country
	 */
	public <T> ObservableList<T> getAllFrom(String country, Getter<T> get, int countryLabel) {
		ObservableList<T> list = FXCollections.observableArrayList();
		int countryID = Commons.findCountryByName(country).getCountryID();
		ResultSet rs = CRUD.findByNumber(table.getTableName(), table.getColumnName(countryLabel), countryID);
		try {
			while (rs.next())
				list.add(get.get(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @return table
	 */
	public DatabaseTable getTable() {
		return table;
	}

	/**
	 * Use full text search in boolean mode to search table from DB
	 * 
	 * @param <T>         objects that represent given table
	 * @param startTyping starting criteria for the search
	 * @param fulltext    column names indexed in db table
	 * @param get         matching functional interface
	 * @return observable list of objects type T whose column starts with specified
	 *         String
	 */
	public <T> ObservableList<T> searchInBooleanMode(String startTyping, String[] fulltext, Getter<T> get) {
		ObservableList<T> list = FXCollections.observableArrayList();
		ResultSet rs = CRUD.read(table.getTableName(), fulltext, startTyping);
		try {
			while (rs.next())
				list.add(get.get(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Update row in the table
	 * 
	 * @param id     primary key id, which row to update
	 * @param column index of the column in the table that should be updated
	 * @param number double value to be inserted
	 */
	public void update(long id, int column, double number) {
		CRUD.update(table.getTableName(), table.getColumnName(column), number, table.getPrimaryKey(), id);
	}

	/**
	 * Update row in the table
	 * 
	 * @param id     primary key id, which row to update
	 * @param column index of the column in the table that should be updated
	 * @param ld     LocalDate value to be inserted
	 */
	public void update(long id, int column, LocalDate ld) {
		CRUD.update(table.getTableName(), table.getColumnName(column), ld, table.getPrimaryKey(), id);
	}

	/**
	 * Update row in the table
	 * 
	 * @param id     primary key id, which row to update
	 * @param column index of the column in the table that should be updated
	 * @param ldt    LocalDateTime value to be inserted
	 */
	public void update(long id, int column, LocalDateTime ldt) {
		CRUD.update(table.getTableName(), table.getColumnName(column), ldt, table.getPrimaryKey(), id);
	}

	/**
	 * Update row in the table
	 * 
	 * @param id       primary key id, which row to update
	 * @param column   index of the column in the table that should be updated
	 * @param newValue long value to be inserted
	 */
	public void update(long id, int column, long newValue) {
		CRUD.update(table.getTableName(), table.getColumnName(column), newValue, table.getPrimaryKey(), id);
	}

	/**
	 * Update row in table
	 * 
	 * @param id       primary key id, which row to update
	 * @param column   index of the column in the table that should be updated
	 * @param newValue String to be inserted
	 */
	public void update(long id, int column, String newValue) {
		CRUD.update(table.getTableName(), table.getColumnName(column), newValue, table.getPrimaryKey(), id);
	}

	/**
	 * Update row in the table with blob
	 * 
	 * @param id       primary key id, which row to update
	 * @param column   index of the column in the table that should be updated,
	 *                 column that holds blob
	 * @param filePath path to the file to be inserted
	 */
	public void updateBlob(long id, int column, String filePath) {
		try {
			CRUD.updateBlob(table.getTableName(), table.getColumnName(column), filePath, table.getPrimaryKey(), id);
		} catch (FileNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param id      primary key id, which row to update
	 * @param column  index of column that holds country
	 * @param country name of the country
	 */
	public void updateCountry(long id, int column, String country) {
		int countryID = Commons.findCountryByName(country).getCountryID();
		CRUD.update(table.getTableName(), table.getColumnName(column), countryID, table.getPrimaryKey(), id);
	}
}
