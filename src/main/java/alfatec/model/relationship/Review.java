package alfatec.model.relationship;

import alfatec.model.enums.Opinion;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import util.BooleanUtil;

/**
 * Model for database table "review".
 * 
 * Connection table for reviewer and research.
 * 
 * @author jelena
 *
 */
public class Review {

	private final LongProperty reviewID;
	private final ObjectProperty<Opinion> opinion;
	private final IntegerProperty reviewerID;
	private final LongProperty researchID;
	private final IntegerProperty authorInformed;

	public Review() {
		this(0, null, 0, 0, false);
	}

	public Review(long id, String opinion, int reviewerID, long researchID, boolean authorInformed) {
		this.reviewID = new SimpleLongProperty(id);
		this.opinion = new SimpleObjectProperty<Opinion>(Opinion.valueOf(opinion));
		this.reviewerID = new SimpleIntegerProperty(reviewerID);
		this.researchID = new SimpleLongProperty(researchID);
		this.authorInformed = new SimpleIntegerProperty(BooleanUtil.parse(authorInformed));
	}

	public String getOpinion() {
		return opinion.get().getOpinion();
	}

	public Opinion getOpinionName() {
		return opinion.get();
	}

	public ObjectProperty<Opinion> getOpinionProperty() {
		return opinion;
	}

	public long getResearchID() {
		return researchID.get();
	}

	public LongProperty getResearchIDProperty() {
		return researchID;
	}

	public int getReviewerID() {
		return reviewerID.get();
	}

	public IntegerProperty getReviewerIDProperty() {
		return reviewerID;
	}

	public long getReviewID() {
		return reviewID.get();
	}

	public LongProperty getReviewIDProperty() {
		return reviewID;
	}

	public void setOpinion(Opinion opinion) {
		this.opinion.set(opinion);
	}

	public void setOpinion(String opinion) {
		this.opinion.set(Opinion.valueOf(opinion));
	}

	public void setResearchID(long id) {
		this.researchID.set(id);
	}

	public void setReviewerID(int id) {
		this.reviewerID.set(id);
	}

	public void setReviewID(long id) {
		this.reviewID.set(id);
	}

	public IntegerProperty getAuthorInformedProperty() {
		return authorInformed;
	}

	public int getAuthorInformed() {
		return authorInformed.get();
	}

	public boolean isAuthorInformed() {
		return BooleanUtil.checkNumber(getAuthorInformed());
	}

	public void setAuthorInformed(int isInformed) {
		this.authorInformed.set(isInformed);
	}

	public void setIsAuthorInformed(boolean isInformed) {
		this.authorInformed.set(BooleanUtil.parse(isInformed));
	}

}
