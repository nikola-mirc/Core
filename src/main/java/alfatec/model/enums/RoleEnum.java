package alfatec.model.enums;

/**
 * Model for database table "role".
 * 
 * There are 3 roles, fixed. Enum was chosen for simplicity.
 * 
 * @author jelena
 *
 */
public enum RoleEnum {

	USER(1), ADMIN(2), SUPERADMIN(3);

	/**
	 * @param roleID
	 * @return name of the role for the specified roleID
	 */
	public static String getRoleName(int roleID) {
		for (RoleEnum r : RoleEnum.values())
			if (r.getRoleID() == roleID)
				return r.name();
		return null;
	}

	private final int roleID;

	private RoleEnum(int roleID) {
		this.roleID = roleID;
	}

	/**
	 * get id of the role
	 */
	public int getRoleID() {
		return roleID;
	}
}
