package util;

/**
 * Convert boolean value to integer and vice versa. MySQL stores tinyint 0 and 1
 * instead of false and true.
 * 
 * @author jelena
 *
 */
public class BooleanUtil {

	/**
	 * Parse integer value to boolean
	 * 
	 * @param number - integer to be converted
	 * @return false if number is 0, otherwise true
	 */
	public static boolean checkNumber(int number) {
		return number == 0 ? false : true;
	}

	/**
	 * Parse boolean value to integer
	 * 
	 * @param bool - boolean value
	 * @return 0 for false, 1 for true
	 */
	public static int parse(boolean bool) {
		return bool ? 1 : 0;
	}

}
