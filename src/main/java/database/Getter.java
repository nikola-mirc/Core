package database;

import java.sql.ResultSet;

/**
 * Generic functional interface, represent method which will create convenient
 * object - read from database table represented with T.
 * 
 * @author jelena
 *
 * @param <T>
 */

@FunctionalInterface
public interface Getter<T> {
	public T get(ResultSet rs);
}
