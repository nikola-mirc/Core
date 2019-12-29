package alfatec.model.relationship;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * Model for database table "author_research".
 * 
 * Table is connection table for author and research.
 * 
 * @author jelena
 *
 */
public class AuthorResearch {

	private final LongProperty authorResearchID;
	private final LongProperty authorID;
	private final LongProperty researchID;

	public AuthorResearch() {
		this(0, 0, 0);
	}

	public AuthorResearch(long id, long authorID, long researchID) {
		this.authorResearchID = new SimpleLongProperty(id);
		this.authorID = new SimpleLongProperty(authorID);
		this.researchID = new SimpleLongProperty(researchID);
	}

	public long getAuthorID() {
		return authorID.get();
	}

	public LongProperty getAuthorIDProperty() {
		return authorID;
	}

	public long getAuthorResearchID() {
		return authorResearchID.get();
	}

	public LongProperty getAuthorResearchIDProperty() {
		return authorResearchID;
	}

	public long getResearchID() {
		return researchID.get();
	}

	public LongProperty getResearchIDProperty() {
		return researchID;
	}

	public void setAuthorID(long id) {
		this.authorID.set(id);
	}

	public void setAuthorResearchID(long id) {
		this.authorResearchID.set(id);
	}

	public void setResearchID(long id) {
		this.researchID.set(id);
	}

}
