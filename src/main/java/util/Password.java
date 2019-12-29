package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Methods from BCrypt to hash password
 * 
 * @author jelena
 *
 */
public class Password {

	public static boolean checkPassword(String enteredPassword, String storedPassword) {
		return BCrypt.checkpw(enteredPassword, storedPassword);
	}

	public static String hashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

}
