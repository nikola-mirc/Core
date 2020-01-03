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
				new String[] { "presentation", "presentation_date", "submittet_work", "sent_to_review", "conference_id",
						"registration_fee", "for_collection", "research_id" }));
		getPaperwork = new Getter<Paperwork>() {
			@Override
			public Paperwork get(ResultSet rs) {
				Paperwork work = new Paperwork();
				try {
					work.setPaperworkID(rs.getLong(table.getTable().getPrimaryKey()));
					work.setPresentation(rs.getString(table.getTable().getColumnName(1)));
					work.setPresentationTimestamp(rs.getObject(table.getTable().getColumnName(2), LocalDateTime.class));
					work.setConferenceID(rs.getInt(table.getTable().getColumnName(3)));
					work.setSubmittetWork(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(4))));
					work.setSentToReview(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(5))));
					work.setRegistrationFeeID(rs.getInt(table.getTable().getColumnName(6)));
					work.setForCollection(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(7))));
					work.setResearchID(rs.getLong(table.getTable().getColumnName(8)));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return work;
			}
		};
	}

	public Paperwork createPaperwork(long researchID) {
		int[] ints = { 0, 0, ConferenceDAO.getInstance().getCurrentConference().getConferenceID(), 0, 0 };
		return table.create(null, ints, new long[] { researchID }, getPaperwork);
	}

	public Paperwork createPaperwork(long researchID, boolean isSubmittet, String presentation, boolean isSentToReview,
			int registrationFeeID, boolean isForCollection, String presentationDate) {
		String[] strings = { presentation, presentationDate };
		int[] ints = { BooleanUtil.parse(isSubmittet), BooleanUtil.parse(isSentToReview),
				ConferenceDAO.getInstance().getCurrentConference().getConferenceID(), registrationFeeID,
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
		return table.findBy(researchID, 8, getPaperwork).get(0);
	}

	public void setIsForCollection(Paperwork work, boolean bool) {
		table.update(work.getPaperworkID(), 7, BooleanUtil.parse(bool));
		work.setIsForCollection(bool);
	}

	public void setPresentationDateTime(Paperwork work, LocalDateTime datetime) {
		table.update(work.getPaperworkID(), 2, datetime);
		work.setPresentationTimestamp(datetime);
		Logging.getInstance().change("Update",
				"Set presentation date for " + ResearchDAO.getInstance().getResearch(work.getResearchID()));
	}

	/**
	 * @param datetime - pattern: yyyy-MM-dd HH:mm:ss
	 */
	public void setPresentationDateTime(Paperwork work, String datetime) {
		table.update(work.getPaperworkID(), 2, datetime);
		work.setPresentationTime(datetime);
		Logging.getInstance().change("Update",
				"Set presentation date for " + ResearchDAO.getInstance().getResearch(work.getResearchID()));
	}

	public void setRegistrationFee(Paperwork work, int registrationFeeID) {
		table.update(work.getPaperworkID(), 6, registrationFeeID);
		work.setRegistrationFeeID(registrationFeeID);
		Logging.getInstance().change("Update",
				"Set registration fee for " + ResearchDAO.getInstance().getResearch(work.getResearchID()));
	}

	public void setRegistrationFee(Paperwork work, String registrationName) {
		int regID = Commons.findRegistration(registrationName, work.getConferenceID()).getRegistrationFeeID();
		table.update(work.getPaperworkID(), 6, regID);
		work.setRegistrationFeeID(regID);
		Logging.getInstance().change("Update",
				"Set registration fee for " + ResearchDAO.getInstance().getResearch(work.getResearchID()));
	}

	public void updateIsSentToReview(Paperwork work, boolean bool) {
		table.update(work.getPaperworkID(), 5, BooleanUtil.parse(bool));
		work.setIsSentToReview(bool);
	}

	public void updateIsSubmittetWork(Paperwork work, boolean bool) {
		table.update(work.getPaperworkID(), 4, BooleanUtil.parse(bool));
		work.setIsSubmittetWork(bool);
		Logging.getInstance().change("Update",
				"Sent " + ResearchDAO.getInstance().getResearch(work.getResearchID()) + " to review.");
	}

	public void updatePaperworkPresentationType(Paperwork work, String presentationType) {
		table.update(work.getPaperworkID(), 1, presentationType);
		work.setPresentation(presentationType);
		Logging.getInstance().change("Update",
				"Set presentation type for " + ResearchDAO.getInstance().getResearch(work.getResearchID()));
	}
}
