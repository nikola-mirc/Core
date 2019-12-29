package database;

/**
 * Model of table in database
 * 
 * @author jelena
 *
 */
public class DatabaseTable {

	private final String tableName;
	private final String primaryKey;
	private final String[] columnNames;

	/**
	 * @param table   - name of the table
	 * @param key     - name of the column that holds primary key
	 * @param columns - names of the other columns
	 */
	public DatabaseTable(final String table, final String key, final String[] columns) {
		this.tableName = table;
		this.primaryKey = key;
		this.columnNames = columns;
	}

	/**
	 * columns don't include primary key, so count index from 1
	 * 
	 * @return name of the column with specified index
	 */
	public String getColumnName(int index) {
		return columnNames[index - 1];
	}

	/**
	 * @return array of names of the all columns except column that holds primary
	 *         key
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	/**
	 * @return name of the column that holds primary key
	 */
	public String getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @return name of the table
	 */
	public String getTableName() {
		return tableName;
	}

}
