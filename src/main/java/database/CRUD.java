package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import alfatec.model.enums.Institution;
import util.DateUtil;

/**
 * Class with SQL queries to create, read, update and delete rows in database
 * table
 * 
 * @author jelena
 *
 */
public class CRUD {

	public static long create(String tableName, String[] columnNames, String[] strings, int[] ints,
			Institution institution) throws SQLException {
		String column = getColumnNames(columnNames);
		String value = getQuestionMarks(strings, Arrays.stream(ints).mapToLong(i -> i).toArray(), new double[] { 1 });
		String query = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, column, value);
		ResultSet rs = prepareStatement(query, strings, ints, institution);
		return rs.next() ? rs.getInt(1) : 0;
	}

	/**
	 * "INSERT INTO" table
	 * 
	 * @param tableName
	 * @param columnNames - array of columns
	 * @param strings     - strings to be inserted
	 * @param ints        - integers to be inserted
	 * @param bigDecimal  - decimals to be inserted
	 * @return - generated key - id value in the table
	 * @throws SQLException
	 */
	public static long create(String tableName, String[] columnNames, String[] strings, int[] ints, double[] bigDecimal)
			throws SQLException {
		String column = getColumnNames(columnNames);
		String value = getQuestionMarks(strings, Arrays.stream(ints).mapToLong(i -> i).toArray(), bigDecimal);
		String query = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, column, value);
		ResultSet rs = prepareStatement(query, strings, ints, bigDecimal);
		return rs.next() ? rs.getInt(1) : 0;
	}

	/**
	 * "INSERT INTO" table
	 * 
	 * @param tableName
	 * @param columnNames - array of columns
	 * @param strings     - strings to be inserted
	 * @param ints        - integers to be inserted
	 * @return - generated key - id value in the table
	 * @throws SQLException
	 */
	public static long create(String tableName, String[] columnNames, String[] strings, int[] ints, long[] longs)
			throws SQLException {
		String column = getColumnNames(columnNames);
		String value = getQuestionMarks(strings, join(ints, longs), new double[] {});
		String query = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, column, value);
		ResultSet rs = prepareStatement(query, strings, ints, longs);
		return rs.next() ? rs.getInt(1) : 0;
	}

	/**
	 * Delete specified row from the table
	 * 
	 * @param tableName - name of the table
	 * @param idLabel   - name of the primary key - id - column
	 * @param id        - long value of primary key, id of the row
	 */
	public static void delete(String tableName, String idLabel, long id) {
		String query = String.format("DELETE FROM %s WHERE %s = %d", tableName, idLabel, id);
		DatabaseUtility.getInstance().executeUpdateQuery(query);
	}

	/**
	 * Select all rows in table where specified column holds number
	 * 
	 * @param tableName   - name of the table
	 * @param numberLabel - name of the column
	 * @param number      - long value being searched for
	 * @return - ResultSet
	 */
	public static ResultSet findByNumber(String tableName, String numberLabel, long number) {
		String query = String.format("SELECT * FROM %s WHERE %s = %d", tableName, numberLabel, number);
		return DatabaseUtility.getInstance().executeQuery(query);
	}

	/**
	 * Select all from table where specified column holds String
	 * 
	 * @param tableName   - name of the table
	 * @param stringLabel - name of the column
	 * @param string      - String value being searched for
	 * @return - ResultSet
	 */
	public static ResultSet findByString(String tableName, String stringLabel, String string) {
		String query = String.format("SELECT * FROM %s WHERE %s = '%s'", tableName, stringLabel, string);
		return DatabaseUtility.getInstance().executeQuery(query);
	}

	/**
	 * 
	 * @param columnNames
	 * @return String in format: column_name_1, column_name_2, ..., column_name_n
	 */
	private static String getColumnNames(String[] columnNames) {
		String column = "";
		for (String s : columnNames)
			column += s + ", ";
		return column.substring(0, column.lastIndexOf(','));
	}

	/**
	 * Create conjunction of elements in given array
	 * 
	 * @param match - array of conditions in format a = b
	 * @return String in format: m1 AND m2 AND ... AND mN
	 */
	private static String getCondition(String[] match) {
		String conditiion = "";
		for (String s : match)
			conditiion += s + " AND ";
		return conditiion.substring(0, conditiion.lastIndexOf(" AND "));
	}

	/**
	 * Create conjunction where columnName = value
	 * 
	 * @param columnNames - name of the columns
	 * @param values      - integer values in that columns
	 * @return String in format: c1=v1 AND c2=v2 AND ... AND cN=vN
	 */
	private static String getCondition(String[] columnNames, long[] values) {
		String[] match = new String[columnNames.length];
		for (int i = 0; i < columnNames.length; i++)
			match[i] = columnNames[i] + " = " + values[i];
		return getCondition(match);
	}

	/**
	 * Create conjunction where columnName = value
	 * 
	 * @param columnNames - name of the columns
	 * @param values      - String values in that columns
	 * @return String in format: c1=v1 AND c2=v2 AND ... AND cN=vN
	 */
	private static String getCondition(String[] columnNames, String[] values) {
		String[] match = new String[columnNames.length];
		for (int i = 0; i < columnNames.length; i++)
			match[i] = columnNames[i] + " = '" + values[i] + "'";
		return getCondition(match);
	}

	/**
	 * For all elements in given arrays, get question marks
	 * 
	 * @param strings    - array of some strings
	 * @param ints       - array of some integers
	 * @param bigDecimal - array of some floats
	 * @return String in format: ?,?,...,?
	 */
	private static String getQuestionMarks(String[] strings, long[] ints, double[] bigDecimal) {
		String value = "";
		for (int i = 0; i < strings.length + ints.length + bigDecimal.length; i++)
			value += "?,";
		return value.substring(0, value.lastIndexOf(','));
	}

	/**
	 * Update specific row of the table - insert file via FileInputStream
	 * 
	 * @param tableName  - name of the table
	 * @param columnName - name of the column that holds blob
	 * @param filePath   - path to the file
	 * @param idLabel    - name of the primary key (id) column
	 * @param id         - value of the primary key (where blob should be inserted)
	 * @throws FileNotFoundException
	 * @throws SQLException
	 */
	public static void insertBlob(String tableName, String columnName, String filePath, String idLabel, long id)
			throws FileNotFoundException, SQLException {
		File file = new File(filePath);
		FileInputStream inputStream = new FileInputStream(file);
		String query = String.format("INSERT INTO %s (%s) VALUES (?) WHERE %s = %d", tableName, columnName, idLabel,
				id);
		PreparedStatement statement = DatabaseUtility.getInstance().getConnection().prepareStatement(query);
		statement.setBlob(1, inputStream);
		statement.executeUpdate();
	}

	private static long[] join(int[] ints, long[] longs) {
		long[] result = new long[ints.length + longs.length];
		for (int i = 0; i < ints.length; i++)
			result[i] = ints[i];
		for (int i = ints.length; i < result.length; i++)
			result[i] = longs[i - ints.length];
		return result;
	}

	private static ResultSet prepareStatement(String query, String[] strings, int[] ints, Institution institution)
			throws SQLException {
		PreparedStatement preparedStatement = DatabaseUtility.getInstance().getConnection().prepareStatement(query,
				Statement.RETURN_GENERATED_KEYS);
		preparedStatement.setObject(1, institution.name());
		for (int i = 0; i < strings.length; i++)
			preparedStatement.setString(i + 2, strings[i]);
		for (int i = 0; i < ints.length; i++)
			preparedStatement.setInt(i + 2 + strings.length, ints[i]);
		preparedStatement.executeUpdate();
		return preparedStatement.getGeneratedKeys();
	}

	/**
	 * Prepare statement with generated keys
	 * 
	 * @param query      - query to be prepared
	 * @param strings    - array of strings to be inserted
	 * @param ints       - array of integers to be inserted
	 * @param bigDecimal - array of decimals to be inserted
	 * @return result set with generated keys
	 * @throws SQLException
	 */
	private static ResultSet prepareStatement(String query, String[] strings, int[] ints, double[] bigDecimal)
			throws SQLException {
		PreparedStatement preparedStatement = DatabaseUtility.getInstance().getConnection().prepareStatement(query,
				Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < strings.length; i++)
			preparedStatement.setString(i + 1, strings[i]);
		for (int i = 0; i < ints.length; i++)
			preparedStatement.setInt(i + 1 + strings.length, ints[i]);
		for (int i = 0; i < bigDecimal.length; i++)
			preparedStatement.setBigDecimal(i + 1 + strings.length + ints.length, BigDecimal.valueOf(bigDecimal[i]));
		preparedStatement.executeUpdate();
		return preparedStatement.getGeneratedKeys();
	}

	/**
	 * Prepare statement with generated keys
	 * 
	 * @param query   - query to be prepared
	 * @param strings - string to be inserted via query
	 * @param ints    - integers to be inserted via query
	 * @return generated keys
	 * @throws SQLException
	 */
	private static ResultSet prepareStatement(String query, String[] strings, int[] ints, long[] longs)
			throws SQLException {
		PreparedStatement preparedStatement = DatabaseUtility.getInstance().getConnection().prepareStatement(query,
				Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < strings.length; i++)
			preparedStatement.setString(i + 1, strings[i]);
		for (int i = 0; i < ints.length; i++)
			preparedStatement.setInt(i + 1 + strings.length, ints[i]);
		for (int i = 0; i < longs.length; i++)
			preparedStatement.setLong(i + 1 + strings.length + ints.length, longs[i]);
		preparedStatement.executeUpdate();
		return preparedStatement.getGeneratedKeys();
	}

	/**
	 * "SELECT * FROM" - get the entire table
	 * 
	 * @param tableName
	 * @return ResultSet to be processed further
	 */
	public static ResultSet read(String tableName) {
		String query = "SELECT * FROM " + tableName;
		return DatabaseUtility.getInstance().executeQuery(query);
	}

	/**
	 * FTC - Full text search in natural language mode: countryByName,
	 * AuthorByEmail, UserByEmail, ReviewerByEmail, etc
	 */
	public static ResultSet read(String tableName, String match, String against) {
		String query = String.format("SELECT * FROM %s WHERE MATCH(%s) AGAINST('%s' IN NATURAL LANGUAGE MODE)",
				tableName, match, against);
		return DatabaseUtility.getInstance().executeQuery(query);
	}

	public static ResultSet readColumn(String tableName, String columnName) {
		String query = "SELECT " + columnName + " FROM " + tableName;
		return DatabaseUtility.getInstance().executeQuery(query);
	}

	/**
	 * Select rows in table where specified column names have specified integer
	 * values respectively
	 * 
	 * @param tableName   - name of the table
	 * @param columnNames - names of the columns
	 * @param values      - integer values
	 */
	public static ResultSet read(String tableName, String[] columnNames, long[] values) {
		String query = String.format("SELECT * FROM %s WHERE %s", tableName, getCondition(columnNames, values));
		return DatabaseUtility.getInstance().executeQuery(query);
	}

	/**
	 * FTC - Full text search in boolean mode: countries(code, name),
	 * reviewers(email, firstName, lastName), authors(email, firstName, lastName),
	 * user(firstName, lastName), etc
	 */
	public static ResultSet read(String tableName, String[] match, String startTyping) {
		String matches = getColumnNames(match);
		String query = String.format("SELECT * FROM %s WHERE MATCH(%s) AGAINST('%s*' IN BOOLEAN MODE)", tableName,
				matches, startTyping);
		return DatabaseUtility.getInstance().executeQuery(query);
	}

	/**
	 * Select rows in table where specified column names have specified String
	 * values respectively
	 * 
	 * @param tableName   - name of the table
	 * @param columnNames - names of the columns
	 * @param values      - string values
	 */
	public static ResultSet read(String tableName, String[] columnNames, String[] values) {
		String query = String.format("SELECT * FROM %s WHERE %s", tableName, getCondition(columnNames, values));
		return DatabaseUtility.getInstance().executeQuery(query);
	}

	/**
	 * execute update query
	 */
	public static void update(String query) {
		DatabaseUtility.getInstance().executeUpdateQuery(query);
	}

	/**
	 * Execute update query - update float in specified row
	 * 
	 * @param tableName  - name of the table
	 * @param columnName - name of the column that holds value to be updated
	 * @param newValue   - value to be updated, in 2 decimal points
	 * @param idLabel    - name of the primary key - id - column
	 * @param idWhere    - value of primary key - row
	 */
	public static void update(String tableName, String columnName, double newValue, String idLabel, long idWhere) {
		String query = String.format("UPDATE %s SET %s = %f WHERE %s = %d", tableName, columnName,
				BigDecimal.valueOf(newValue), idLabel, idWhere);
		DatabaseUtility.getInstance().executeUpdateQuery(query);
	}

	/**
	 * Execute update query - update integer in specified row
	 * 
	 * @param tableName  - name of the table
	 * @param columnName - name of the column that holds value to be updated
	 * @param newValue   - value to be updated
	 * @param idLabel    - name of the primary key - id - column
	 * @param idWhere    - value of primary key - row
	 */
	public static void update(String tableName, String columnName, int newValue, String idLabel, long idWhere) {
		String query = String.format("UPDATE %s SET %s = %d WHERE %s = %d", tableName, columnName, newValue, idLabel,
				idWhere);
		DatabaseUtility.getInstance().executeUpdateQuery(query);
	}

	/**
	 * Execute update query - update LocalDate in specified row
	 * 
	 * @param tableName  - name of the table
	 * @param columnName - name of the column that holds value to be updated
	 * @param newValue   - value to be updated, will be formated as String
	 * @param idLabel    - name of the primary key - id - column
	 * @param idWhere    - value of primary key - row
	 */
	public static void update(String tableName, String columnName, LocalDate newValue, String idLabel, long idWhere) {
		String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", tableName, columnName,
				DateUtil.format(newValue), idLabel, idWhere);
		DatabaseUtility.getInstance().executeUpdateQuery(query);
	}

	/**
	 * Execute update query - update LocalDateTime in specified row
	 * 
	 * @param tableName  - name of the table
	 * @param columnName - name of the column that holds value to be updated
	 * @param newValue   - value to be updated, will be formated as String
	 * @param idLabel    - name of the primary key - id - column
	 * @param idWhere    - value of primary key - row
	 */
	public static void update(String tableName, String columnName, LocalDateTime newValue, String idLabel,
			long idWhere) {
		String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", tableName, columnName,
				DateUtil.format(newValue), idLabel, idWhere);
		DatabaseUtility.getInstance().executeUpdateQuery(query);
	}

	/**
	 * Execute update query - update String in specified row
	 * 
	 * @param tableName  - name of the table
	 * @param columnName - name of the column that holds value to be updated
	 * @param newValue   - value to be updated
	 * @param idLabel    - name of the primary key - id - column
	 * @param idWhere    - value of primary key - row
	 */
	public static void update(String tableName, String columnName, String newValue, String idLabel, long idWhere) {
		String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d", tableName, columnName, newValue, idLabel,
				idWhere);
		DatabaseUtility.getInstance().executeUpdateQuery(query);
	}

	/**
	 * Execute update query - update Blob in specified row
	 * 
	 * @param tableName  - name of the table
	 * @param columnName - name of the column that holds blob
	 * @param blobPath   - path to the file
	 * @param idLabel    - name of the primary key - id - column
	 * @param idWhere    - value of primary key - row
	 */
	public static void updateBlob(String tableName, String columnName, String blobPath, String idLabel, long idWhere)
			throws FileNotFoundException, SQLException {
		File file = new File(blobPath);
		FileInputStream inputStream = new FileInputStream(file);
		String query = String.format("UPDATE %s SET %s = ? WHERE %s = %d", tableName, columnName, idLabel, idWhere);
		PreparedStatement statement = DatabaseUtility.getInstance().getConnection().prepareStatement(query);
		statement.setBinaryStream(1, inputStream);
		statement.executeUpdate();
	}
}
