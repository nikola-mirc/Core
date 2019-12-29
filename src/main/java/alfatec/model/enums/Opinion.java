package alfatec.model.enums;

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

	private Opinion(String name) {
		this.name = name;
	}

	/**
	 * @return full name of the opinion
	 */
	public String getOpinion() {
		return name;
	}
}
