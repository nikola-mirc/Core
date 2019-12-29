package alfatec.model.conference;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * Model for database table "special_issue".
 * 
 * @author jelena
 *
 */
public class SpecialIssue {

	private final IntegerProperty specialIssueID;
	private final LongProperty collectionID;

	public SpecialIssue() {
		this(0, 0);
	}

	public SpecialIssue(int specialIssueID, long collectionID) {
		this.specialIssueID = new SimpleIntegerProperty(specialIssueID);
		this.collectionID = new SimpleLongProperty(collectionID);
	}

	public long getCollectionID() {
		return collectionID.get();
	}

	public LongProperty getCollectionIDProperty() {
		return collectionID;
	}

	public int getSpecialIssueID() {
		return specialIssueID.get();
	}

	public IntegerProperty getSpecialIssueIDProperty() {
		return specialIssueID;
	}

	public void setCollectionID(long id) {
		this.collectionID.set(id);
	}

	public void setSpecialIssueID(int id) {
		this.specialIssueID.set(id);
	}

}
