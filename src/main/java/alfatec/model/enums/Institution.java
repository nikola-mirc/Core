package alfatec.model.enums;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Model for database enum in column "institution" in "author" table.
 * 
 * As requested, holds 3 values: faculty, university and institute.
 * 
 * @author jelena
 *
 */
public enum Institution {
	FACULTY, UNIVERSITY, INSTITUTE;

	private static final Map<String, Institution> indexMap = new HashMap<>(Institution.values().length);

	static {
		for (Institution institution : Institution.values())
			indexMap.put(institution.name(), institution);
	}

	public static Institution lookUpByName(String name) {
		return name == null ? null : indexMap.get(name.toUpperCase(Locale.ENGLISH));
	}
}
