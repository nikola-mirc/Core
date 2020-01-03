package alfatec.dao.utils;

import alfatec.dao.user.UserAuditDAO;
import alfatec.model.user.LoginData;

/**
 * Basic custom monitor class. All changes made during using application will be
 * saved in appropriate database table.
 * 
 * @author jelena
 *
 */
public class Logging {

	private LoginData user;
	private static Logging instance;

	private Logging() {
	}

	public static Logging getInstance() {
		if (instance == null)
			synchronized (Logging.class) {
				if (instance == null)
					instance = new Logging();
			}
		return instance;
	}

	/**
	 * monitor changes for current user
	 * 
	 * @param loginData
	 */
	public void setUser(LoginData loginData) {
		this.user = loginData;
	}

	/**
	 * event types:
	 * 
	 * CREATE - new entry in author, research, conference, reviewer tables
	 * 
	 * UPDATE - update entry in author, research, conference, reviewer tables
	 * 
	 * DELETE - delete entry in author, research, conference, reviewer tables
	 * 
	 * ADD - mark for collection or for special issue
	 * 
	 * REMOVE - remove from collection or special issue
	 * 
	 * EMAIL - record send email action
	 * 
	 * DATE - set up dates
	 */
	public void change(String type, String description) {
		UserAuditDAO.getInstance().createEntry(type, description, user.getLoginID());
	}
}
