package alfatec.dao.research;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javafx.collections.ObservableList;
import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.utils.Commons;
import alfatec.dao.utils.Logging;
import alfatec.dao.utils.TableUtility;
import alfatec.model.research.Paperwork;
import database.Getter;
import database.DatabaseTable;
import util.BooleanUtil;

/**
 * DAO for table "paperwork".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class PaperworkDAO {

	private static PaperworkDAO instance;

	public static PaperworkDAO getInstance() {
		if (instance == null)
			synchronized (PaperworkDAO.class) {
				if (instance == null)
					instance = new PaperworkDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<Paperwork> getPaperwork;

	private PaperworkDAO() {
		table = new TableUtility(new DatabaseTable("paperwork", "paperwork_id",
				new String[] { "presentation", "presentation_date", "registration_fee", "conference_id",
						"submittet_work", "sent_to_review", "fee_paid", "for_collection", "research_id" }));
		getPaperwork = (ResultSet rs) -> {
			Paperwork work = new Paperwork();
			try {
				work.setPaperworkID(rs.getLong(table.getTable().getPrimaryKey()));
				work.setPresentation(rs.getString(table.getTable().getColumnName(1)));
				work.setPresentationTimestamp(rs.getTimestamp(table.getTable().getColumnName(2)).toLocalDateTime());
				work.setRegistrationFeeID(rs.getInt(table.getTable().getColumnName(3)));
				work.setConferenceID(rs.getInt(table.getTable().getColumnName(4)));
				work.setSubmittetWork(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(5))));
				work.setSentToReview(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(6))));
				work.setFeePaid(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(7))));
				work.setForCollection(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(8))));
				work.setResearchID(rs.getLong(table.getTable().getColumnName(9)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return work;
		};
	}

	public Paperwork createPaperworkForCurrentConference(long researchID) {
		int[] ints = { ConferenceDAO.getInstance().getCurrentConference().getConferenceID(), 0, 0, 0 };
		return table.create(new String[] { null, "", null }, ints, new long[] { researchID }, getPaperwork);
	}

	public Paperwork createPaperwork(long researchID) {
		return table.create(new String[] { null, "", null, null }, new int[] { 0, 0, 0 }, new long[] { researchID },
				getPaperwork);
	}

	public Paperwork createPaperworkForCurrentConference(long researchID, boolean isSubmittet, String presentation,
			boolean isSentToReview, int registrationFeeID, boolean isFeePaid, boolean isForCollection,
			String presentationDate) {
		if (registrationFeeID == 0) {
			String[] strings = { presentation, presentationDate, null };
			int[] ints = { ConferenceDAO.getInstance().getCurrentConference().getConferenceID(),
					BooleanUtil.parse(isSubmittet), BooleanUtil.parse(isSentToReview), BooleanUtil.parse(isFeePaid),
					BooleanUtil.parse(isForCollection) };
			return table.create(strings, ints, new long[] { researchID }, getPaperwork);
		} else {
			String[] strings = { presentation, presentationDate };
			int[] ints = { registrationFeeID, ConferenceDAO.getInstance().getCurrentConference().getConferenceID(),
					BooleanUtil.parse(isSubmittet), BooleanUtil.parse(isSentToReview), BooleanUtil.parse(isFeePaid),
					BooleanUtil.parse(isForCollection) };
			return table.create(strings, ints, new long[] { researchID }, getPaperwork);
		}
	}

	public Paperwork createPaperwork(long researchID, boolean isSubmittet, String presentation, boolean isSentToReview,
			int registrationFeeID, boolean isFeePaid, boolean isForCollection, String presentationDate) {
		String[] strings = { presentation, presentationDate, null, null };
		int[] ints = { BooleanUtil.parse(isSubmittet), BooleanUtil.parse(isSentToReview), BooleanUtil.parse(isFeePaid),
				BooleanUtil.parse(isForCollection) };
		return table.create(strings, ints, new long[] { researchID }, getPaperwork);
	}

	/**
	 * @return all data from the table
	 */
	public ObservableList<Paperwork> getAllPaperwork() {
		return table.getAll(getPaperwork);
	}

	public Paperwork getPaperwork(long paperworkID) {
		return table.findBy(paperworkID, getPaperwork);
	}

	public Paperwork getPaperworkForResearch(long researchID) {
		try {
			return table.findBy(researchID, 9, getPaperwork).get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void setIsForCollection(Paperwork work, boolean bool) {
		table.update(work.getPaperworkID(), 8, BooleanUtil.parse(bool));
		work.setIsForCollection(bool);
	}

	public void setPresentationDateTime(Paperwork work, LocalDateTime datetime) {
		table.update(work.getPaperworkID(), 2, datetime);
		work.setPresentationTimestamp(datetime);
		Logging.getInstance().change("update", "Set presentation date for "
				+ ResearchDAO.getInstance().getResearch(work.getResearchID()).getResearchTitle());
	}

	/**
	 * @param datetime - pattern: yyyy-MM-dd HH:mm:ss
	 */
	public void setPresentationDateTime(Paperwork work, String datetime) {
		table.update(work.getPaperworkID(), 2, datetime);
		work.setPresentationTime(datetime);
		Logging.getInstance().change("update", "Set presentation date for "
				+ ResearchDAO.getInstance().getResearch(work.getResearchID()).getResearchTitle());
	}

	public void setRegistrationFee(Paperwork work, int registrationFeeID) {
		table.update(work.getPaperworkID(), 3, registrationFeeID);
		work.setRegistrationFeeID(registrationFeeID);
		Logging.getInstance().change("update", "Set registration fee for "
				+ ResearchDAO.getInstance().getResearch(work.getResearchID()).getResearchTitle());
	}

	public void setRegistrationFee(Paperwork work, String registrationName) {
		int regID = Commons.findRegistration(registrationName, work.getConferenceID()).getRegistrationFeeID();
		table.update(work.getPaperworkID(), 3, regID);
		work.setRegistrationFeeID(regID);
		Logging.getInstance().change("update", "Set registration fee for "
				+ ResearchDAO.getInstance().getResearch(work.getResearchID()).getResearchTitle());
	}

	public void updateIsSentToReview(Paperwork work, boolean bool) {
		table.update(work.getPaperworkID(), 6, BooleanUtil.parse(bool));
		work.setIsSentToReview(bool);
		Logging.getInstance().change("update", "Sent "
				+ ResearchDAO.getInstance().getResearch(work.getResearchID()).getResearchTitle() + " to review.");
	}

	public void updateIsSubmittetWork(Paperwork work, boolean bool) {
		table.update(work.getPaperworkID(), 5, BooleanUtil.parse(bool));
		work.setIsSubmittetWork(bool);
		Logging.getInstance().change("update", "Marked research\n\t"
				+ ResearchDAO.getInstance().getResearch(work.getResearchID()).getResearchTitle() + "\nas submittet.");
	}

	public void updateIsFeePaid(Paperwork work, boolean bool) {
		table.update(work.getPaperworkID(), 7, BooleanUtil.parse(bool));
		work.setIsFeePaid(bool);
		Logging.getInstance().change("update",
				"Registration fee for\n\t"
						+ ResearchDAO.getInstance().getResearch(work.getResearchID()).getResearchTitle() + "\nis paid: "
						+ bool);
	}

	public void updatePaperworkPresentationType(Paperwork work, String presentationType) {
		table.update(work.getPaperworkID(), 1, presentationType);
		work.setPresentation(presentationType);
		Logging.getInstance().change("update", "Set presentation type for\n\t"
				+ ResearchDAO.getInstance().getResearch(work.getResearchID()).getResearchTitle());
	}

}
