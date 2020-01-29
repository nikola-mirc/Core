package alfatec.dao.relationship;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.person.AuthorDAO;
import alfatec.dao.utils.Logging;
import alfatec.dao.utils.TableUtility;
import alfatec.model.relationship.ConferenceCall;
import database.Getter;
import database.DatabaseTable;
import util.BooleanUtil;

/**
 * DAO for table "conference_call".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class ConferenceCallDAO {

	private static ConferenceCallDAO instance;

	public static ConferenceCallDAO getInstance() {
		if (instance == null)
			synchronized (ConferenceCallDAO.class) {
				if (instance == null)
					instance = new ConferenceCallDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<ConferenceCall> getCall;

	private ConferenceCallDAO() {
		table = new TableUtility(new DatabaseTable("conference_call", "cc_id",
				new String[] { "conference_id", "first_call_answer", "second_call_answer", "third_call_answer",
						"interested", "first_call_sent", "second_call_sent", "third_call_sent", "author_id" }));
		getCall = (ResultSet rs) -> {
			ConferenceCall call = new ConferenceCall();
			try {
				call.setConferenceCallID(rs.getLong(table.getTable().getPrimaryKey()));
				call.setConferenceID(rs.getInt(table.getTable().getColumnName(1)));
				call.setFirstCallAnswer(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(2))));
				call.setSecondCallAnswer(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(3))));
				call.setThirdCallAnswer(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(4))));
				call.setInterested(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(5))));
				call.setFirstCallSent(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(6))));
				call.setSecondCallSent(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(7))));
				call.setThirdCallSent(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(8))));
				call.setAuthorID(rs.getLong(table.getTable().getColumnName(9)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return call;
		};
	}

	public ConferenceCall createEntry(int conferenceID, long authorID) {
		return table.create(new String[] {}, new int[] { conferenceID, 0, 0, 0, 1, 0, 0, 0 }, new long[] { authorID },
				getCall);
	}

	public void deleteEntry(ConferenceCall call) {
		table.delete(call.getConferenceCallID());
	}

	/**
	 * @return all data from the table
	 */
	public ObservableList<ConferenceCall> getAll() {
		return table.getAll(getCall);
	}

	/**
	 * @param authorID
	 * @return all data tied to specified author
	 */
	public ObservableList<ConferenceCall> getAuthorsAnswer(long authorID) {
		return table.findBy(authorID, 9, getCall);
	}

	/**
	 * Search for answers by primary key - id
	 */
	public ConferenceCall getByID(long id) {
		return table.findBy(id, getCall);
	}

	/**
	 * @return all data for current conference
	 */
	public ObservableList<ConferenceCall> getCurrent() {
		return getForConference(ConferenceDAO.getInstance().getCurrentConference().getConferenceID());
	}

	/**
	 * @param authorID
	 * @return answers for current conference from specified author
	 */
	public ConferenceCall getCurrentAnswer(long authorID) {
		try {
			return table
					.findWhere(new String[] { table.getTable().getColumnName(9), table.getTable().getColumnName(1) },
							new long[] { authorID,
									ConferenceDAO.getInstance().getCurrentConference().getConferenceID() },
							getCall)
					.get(0);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param conferenceID
	 * @return all data tied to specified conference
	 */
	public ObservableList<ConferenceCall> getForConference(int conferenceID) {
		return table.findBy(conferenceID, 1, getCall);
	}

	public void updateFirstCall(ConferenceCall call, boolean answer) {
		table.update(call.getConferenceCallID(), 2, BooleanUtil.parse(answer));
		call.setFirstCallAnswer(answer);
	}

	/**
	 * for current conference
	 * 
	 * @param authorID
	 * @param answer
	 */
	public void updateFirstCall(long authorID, boolean answer) {
		int call = getCurrentAnswer(authorID).getFirstCallAnswer();
		updateFirstCall(getCurrentAnswer(authorID), answer);
		Logging.getInstance().change("update", "Update first call answer from " + BooleanUtil.checkNumber(call) + " to "
				+ answer + " for " + AuthorDAO.getInstance().findAuthorByID(authorID));
	}

	public void updateSecondCall(ConferenceCall call, boolean answer) {
		table.update(call.getConferenceCallID(), 3, BooleanUtil.parse(answer));
		call.setSecondCallAnswer(answer);
	}

	/**
	 * for current conference
	 * 
	 * @param authorID
	 * @param answer
	 */
	public void updateSecondCall(long authorID, boolean answer) {
		int call = getCurrentAnswer(authorID).getSecondCallAnswer();
		updateSecondCall(getCurrentAnswer(authorID), answer);
		Logging.getInstance().change("update", "Update second call answer from " + BooleanUtil.checkNumber(call)
				+ " to " + answer + " for " + AuthorDAO.getInstance().findAuthorByID(authorID));
	}

	public void updateThirdCall(ConferenceCall call, boolean answer) {
		table.update(call.getConferenceCallID(), 4, BooleanUtil.parse(answer));
		call.setThirdCallAnswer(answer);
	}

	/**
	 * for current conference
	 * 
	 * @param authorID
	 * @param answer
	 */
	public void updateThirdCall(long authorID, boolean answer) {
		int call = getCurrentAnswer(authorID).getThirdCallAnswer();
		updateThirdCall(getCurrentAnswer(authorID), answer);
		Logging.getInstance().change("update", "Update third call answer from " + BooleanUtil.checkNumber(call) + " to "
				+ answer + " for " + AuthorDAO.getInstance().findAuthorByID(authorID));
	}

	public void updateInterested(ConferenceCall call, boolean isInterested) {
		table.update(call.getConferenceCallID(), 5, BooleanUtil.parse(isInterested));
		call.setIsInterested(isInterested);
	}

	/**
	 * for current conference
	 * 
	 * @param authorID
	 * @param isInterested is author interested in participating
	 */
	public void updateInterested(long authorID, boolean isInterested) {
		int call = getCurrentAnswer(authorID).getInterested();
		updateInterested(getCurrentAnswer(authorID), isInterested);
		Logging.getInstance().change("update",
				"Update is author interested in conference from " + BooleanUtil.checkNumber(call) + " to "
						+ isInterested + " for " + AuthorDAO.getInstance().findAuthorByID(authorID));
	}

	public void updateFirstCallSent(ConferenceCall call, boolean sent) {
		table.update(call.getConferenceCallID(), 6, BooleanUtil.parse(sent));
		call.setIsFirstCallSent(sent);
	}

	public void updateFirstCallSent(long authorID, boolean sent) {
		updateFirstCallSent(getCurrentAnswer(authorID), sent);
		Logging.getInstance().change("email", "Send first call to " + AuthorDAO.getInstance().findAuthorByID(authorID));
	}

	public void updateSecondCallSent(ConferenceCall call, boolean sent) {
		table.update(call.getConferenceCallID(), 7, BooleanUtil.parse(sent));
		call.setIsSecondCallSent(sent);
	}

	public void updateSecondCallSent(long authorID, boolean sent) {
		updateSecondCallSent(getCurrentAnswer(authorID), sent);
		Logging.getInstance().change("email",
				"Send second call to " + AuthorDAO.getInstance().findAuthorByID(authorID));
	}

	public void updateThirdCallSent(ConferenceCall call, boolean sent) {
		table.update(call.getConferenceCallID(), 8, BooleanUtil.parse(sent));
		call.setIsThirdCallSent(sent);
	}

	public void updateThirdCallSent(long authorID, boolean sent) {
		updateThirdCallSent(getCurrentAnswer(authorID), sent);
		Logging.getInstance().change("email", "Send third call to " + AuthorDAO.getInstance().findAuthorByID(authorID));
	}
}
