package alfatec.model.enums;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Model for database enum in column "presentation" in "paperwork" table.
 * 
 * As requested, holds 3 values: ppt, video, live.
 * 
 * @author jelena
 *
 */
public enum Presentation {
	PPT, VIDEO, LIVE;

	private static final Map<String, Presentation> indexMap = new HashMap<>(Presentation.values().length);

	static {
		for (Presentation presentation : Presentation.values())
			indexMap.put(presentation.name(), presentation);
	}

	public static Presentation lookUpByName(String name) {
		return name == null ? null : indexMap.get(name.toUpperCase(Locale.ENGLISH));
	}

	public static String getName(Presentation presentation) {
		return presentation == null ? "" : presentation.name();
	}
}
