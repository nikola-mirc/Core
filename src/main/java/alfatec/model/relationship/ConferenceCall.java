package alfatec.model.relationship;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import util.BooleanUtil;

/**
 * Model for database table "conference_call".
 * 
 * Table is connection table for conference and author.
 * 
 * @author jelena
 *
 */
public class ConferenceCall {

	private final LongProperty conferenceCallID;
	private final LongProperty authorID;
	private final IntegerProperty conferenceID;
	private final IntegerProperty firstCallAnswer;
	private final IntegerProperty secondCallAnswer;
	private final IntegerProperty thirdCallAnswer;
	private final IntegerProperty interested;

	public ConferenceCall() {
		this(0, 0, 0, false, false, false, true);
	}

	public ConferenceCall(long id, long authorID, int conferenceID, boolean firstCallAnswer, boolean secondCallAnswer,
			boolean thirdCallAnswer, boolean interested) {
		this.conferenceCallID = new SimpleLongProperty(id);
		this.authorID = new SimpleLongProperty(authorID);
		this.conferenceID = new SimpleIntegerProperty(conferenceID);
		this.firstCallAnswer = new SimpleIntegerProperty(BooleanUtil.parse(firstCallAnswer));
		this.secondCallAnswer = new SimpleIntegerProperty(BooleanUtil.parse(secondCallAnswer));
		this.thirdCallAnswer = new SimpleIntegerProperty(BooleanUtil.parse(thirdCallAnswer));
		this.interested = new SimpleIntegerProperty(BooleanUtil.parse(interested));
	}

	public long getAuthorID() {
		return authorID.get();
	}

	public LongProperty getAuthorIDProperty() {
		return authorID;
	}

	public long getConferenceCallID() {
		return conferenceCallID.get();
	}

	public LongProperty getConferenceCallIDProperty() {
		return conferenceCallID;
	}

	public int getConferenceID() {
		return conferenceID.get();
	}

	public IntegerProperty getConferenceIDProperty() {
		return conferenceID;
	}

	public int getFirstCallAnswer() {
		return firstCallAnswer.get();
	}

	public IntegerProperty getFirstCallAnswerProperty() {
		return firstCallAnswer;
	}

	public int getSecondCallAnswer() {
		return secondCallAnswer.get();
	}

	public IntegerProperty getSecondCallAnswerProperty() {
		return secondCallAnswer;
	}

	public int getThirdCallAnswer() {
		return thirdCallAnswer.get();
	}

	public IntegerProperty getThirdCallAnswerProperty() {
		return thirdCallAnswer;
	}

	public boolean isFirstCallAnswered() {
		return BooleanUtil.checkNumber(firstCallAnswer.get());
	}

	public boolean isSecondCallAnswered() {
		return BooleanUtil.checkNumber(secondCallAnswer.get());
	}

	public boolean isThirdCallAnswered() {
		return BooleanUtil.checkNumber(thirdCallAnswer.get());
	}

	public void setAuthorID(long id) {
		this.authorID.set(id);
	}

	public void setConferenceCallID(long id) {
		this.conferenceCallID.set(id);
	}

	public void setConferenceID(int id) {
		this.conferenceID.set(id);
	}

	public void setFirstCallAnswer(boolean bool) {
		this.firstCallAnswer.set(BooleanUtil.parse(bool));
	}

	public void setFirstCallAnswer(int answer) {
		this.firstCallAnswer.set(answer);
	}

	public void setSecondCallAnswer(boolean bool) {
		this.secondCallAnswer.set(BooleanUtil.parse(bool));
	}

	public void setSecondCallAnswer(int answer) {
		this.secondCallAnswer.set(answer);
	}

	public void setThirdCallAnswer(boolean bool) {
		this.thirdCallAnswer.set(BooleanUtil.parse(bool));
	}

	public void setThirdCallAnswer(int answer) {
		this.thirdCallAnswer.set(answer);
	}

	public IntegerProperty getInterestedProperty() {
		return interested;
	}

	public int getInterested() {
		return interested.get();
	}

	public void setInterested(int isInterested) {
		this.interested.set(isInterested);
	}

	public void setIsInterested(boolean interested) {
		this.interested.set(BooleanUtil.parse(interested));
	}

	public boolean isInterested() {
		return BooleanUtil.checkNumber(getInterested());
	}

}
