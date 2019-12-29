package alfatec.model.research;

import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import alfatec.model.enums.Presentation;
import util.BooleanUtil;
import util.DateUtil;

/**
 * Model for database table "paperwork".
 * 
 * Represent data related to the particular research and conference to which it
 * was sent.
 * 
 * @author jelena
 *
 */
public class Paperwork {

	private final LongProperty paperworkID;
	private final LongProperty researchID;
	private final ObjectProperty<Presentation> presentation;
	private final IntegerProperty submittetWork;
	private final IntegerProperty sentToReview;
	private final IntegerProperty conferenceID;
	private final IntegerProperty registrationFeeID;
	private final IntegerProperty forCollection;
	private final ObjectProperty<LocalDateTime> presentationDate;

	public Paperwork() {
		this(0, 0, null, false, false, 0, 0, false, null);
	}

	public Paperwork(int paperworkID, int researchID, String presentationType, boolean submittetWork,
			boolean sentToReview, int conferenceID, int registrationFeeID, boolean forCollection,
			String presentationTime) {
		this.paperworkID = new SimpleLongProperty(paperworkID);
		this.researchID = new SimpleLongProperty(researchID);
		this.presentation = new SimpleObjectProperty<Presentation>(
				Presentation.valueOf(presentationType.toUpperCase()));
		this.submittetWork = new SimpleIntegerProperty(BooleanUtil.parse(submittetWork));
		this.sentToReview = new SimpleIntegerProperty(BooleanUtil.parse(sentToReview));
		this.conferenceID = new SimpleIntegerProperty(conferenceID);
		this.registrationFeeID = new SimpleIntegerProperty(registrationFeeID);
		this.forCollection = new SimpleIntegerProperty(BooleanUtil.parse(forCollection));
		this.presentationDate = new SimpleObjectProperty<LocalDateTime>(DateUtil.parse(presentationTime));
	}

	public int getConferenceID() {
		return conferenceID.get();
	}

	public IntegerProperty getConferenceIDProperty() {
		return conferenceID;
	}

	public int getForCollection() {
		return forCollection.get();
	}

	public IntegerProperty getForCollectionProperty() {
		return forCollection;
	}

	public long getPaperworkID() {
		return paperworkID.get();
	}

	public LongProperty getPaperworkIDProperty() {
		return paperworkID;
	}

	public Presentation getPresentation() {
		return presentation.get();
	}

	public ObjectProperty<LocalDateTime> getPresentationDateProperty() {
		return presentationDate;
	}

	public String getPresentationName() {
		return presentation.get().name();
	}

	public ObjectProperty<Presentation> getPresentationProperty() {
		return presentation;
	}

	public String getPresentationTime() {
		return DateUtil.format(presentationDate.get());
	}

	public LocalDateTime getPresentationTimestamp() {
		return presentationDate.get();
	}

	public int getRegistrationFeeID() {
		return registrationFeeID.get();
	}

	public IntegerProperty getRegistrationFeeIDProperty() {
		return registrationFeeID;
	}

	public long getResearchID() {
		return researchID.get();
	}

	public LongProperty getResearchIDProperty() {
		return researchID;
	}

	public int getSentToReview() {
		return sentToReview.get();
	}

	public IntegerProperty getSentToReviewProperty() {
		return sentToReview;
	}

	public int getSubmittetWork() {
		return submittetWork.get();
	}

	public IntegerProperty getSubmittetWorkProperty() {
		return submittetWork;
	}

	public boolean isForCollection() {
		return BooleanUtil.checkNumber(forCollection.get());
	}

	public boolean isSentToReview() {
		return BooleanUtil.checkNumber(sentToReview.get());
	}

	public boolean isSubmittetWork() {
		return BooleanUtil.checkNumber(submittetWork.get());
	}

	public void setConferenceID(int id) {
		this.conferenceID.set(id);
	}

	public void setForCollection(int value) {
		this.forCollection.set(value);
	}

	public void setIsForCollection(boolean isForCollection) {
		this.forCollection.set(BooleanUtil.parse(isForCollection));
	}

	public void setIsSentToReview(boolean isSent) {
		this.sentToReview.set(BooleanUtil.parse(isSent));
	}

	public void setIsSubmittetWork(boolean isWorkSubmittet) {
		this.submittetWork.set(BooleanUtil.parse(isWorkSubmittet));
	}

	public void setPaperworkID(long id) {
		this.paperworkID.set(id);
	}

	public void setPresentation(Presentation p) {
		this.presentation.set(p);
	}

	public void setPresentation(String type) {
		this.presentation.set(Presentation.valueOf(type.toUpperCase()));
	}

	/**
	 * DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	 * 
	 * @param dateTime
	 */
	public void setPresentationTime(String datetime) {
		this.presentationDate.set(DateUtil.parse(datetime));
	}

	public void setPresentationTimestamp(LocalDateTime ldt) {
		this.presentationDate.set(ldt);
	}

	public void setRegistrationFeeID(int id) {
		this.registrationFeeID.set(id);
	}

	public void setResearchID(long id) {
		this.researchID.set(id);
	}

	public void setSentToReview(int isSent) {
		this.sentToReview.set(isSent);
	}

	public void setSubmittetWork(int isSubmittet) {
		this.submittetWork.set(isSubmittet);
	}

}
