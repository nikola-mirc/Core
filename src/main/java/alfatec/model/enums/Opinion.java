package alfatec.model.enums;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Model for database enum in column "review_opinion" in "review" table.
 * 
 * As requested, hold 4 values: accepted, small review, great review and
 * rejected.
 * 
 * @author jelena
 *
 */

public enum Opinion {
	ACCEPTED("accepted"), SMALL("small review"), GREAT("great review"), REJECTED("rejected");

	private String name;
	private static final Map<String, Opinion> indexMap = new HashMap<>(Opinion.values().length);

	static {
		for (Opinion opinion : Opinion.values())
			indexMap.put(opinion.name(), opinion);
	}

	private Opinion(String name) {
		this.name = name;
	}

	/**
	 * @return full name of the opinion
	 */
	public String getOpinion() {
		return Opinion.lookUpByName(name) == null ? null : name;
	}

	public static Opinion lookUpByName(String name) {
		if (name != null && name.equalsIgnoreCase("small review"))
			name = "small";
		if (name != null && name.equalsIgnoreCase("great review"))
			name = "great";
		return name == null ? null : indexMap.get(name.toUpperCase(Locale.ENGLISH));
	}

	@Override
	public String toString() {
		return getOpinion();
	}
}
