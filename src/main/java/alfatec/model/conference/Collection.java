package alfatec.model.conference;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import util.BooleanUtil;

/**
 * Model for database table "collection".
 * 
 * @author jelena
 *
 */
public class Collection {

	private final LongProperty collectionID;
	private final LongProperty researchID;
	private final IntegerProperty fromConferenceID;
	private final IntegerProperty forSpecialIssue;

	public Collection() {
		this(0, 0, 0, false);
	}

	public Collection(long collectionID, long researchID, int conferenceID, boolean forSpecialIssue) {
		this.collectionID = new SimpleLongProperty(collectionID);
		this.researchID = new SimpleLongProperty(researchID);
		this.fromConferenceID = new SimpleIntegerProperty(conferenceID);
		this.forSpecialIssue = new SimpleIntegerProperty(BooleanUtil.parse(forSpecialIssue));
	}

	public long getCollectionID() {
		return collectionID.get();
	}

	public LongProperty getCollectionIDProperty() {
		return collectionID;
	}

	public int getConferenceID() {
		return fromConferenceID.get();
	}

	public int getForSpecialIssue() {
		return forSpecialIssue.get();
	}

	public IntegerProperty getForSpecialIssueProperty() {
		return forSpecialIssue;
	}

	public IntegerProperty getFromConferenceIDProperty() {
		return fromConferenceID;
	}

	public long getResearchID() {
		return researchID.get();
	}

	public LongProperty getResearchIDProperty() {
		return researchID;
	}

	public boolean isForSpecialIssue() {
		return BooleanUtil.checkNumber(forSpecialIssue.get());
	}

	public void setCollectionID(long id) {
		this.collectionID.set(id);
	}

	public void setConferenceID(int id) {
		this.fromConferenceID.set(id);
	}

	public void setForSpecialIssue(int value) {
		this.forSpecialIssue.set(value);
	}

	public void setIsForSpecialIssue(boolean bool) {
		this.forSpecialIssue.set(BooleanUtil.parse(bool));
	}

	public void setResearchID(long id) {
		this.researchID.set(id);
	}

}
