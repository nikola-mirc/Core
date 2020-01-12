package alfatec.dao.relationship;

import java.sql.ResultSet;
import java.sql.SQLException;

import alfatec.dao.research.ResearchDAO;
import alfatec.dao.utils.Logging;
import alfatec.dao.utils.TableUtility;
import alfatec.model.relationship.Review;
import database.DatabaseTable;
import database.Getter;
import javafx.collections.ObservableList;
import util.BooleanUtil;

/**
 * DAO for table "review".
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class ReviewDAO {

	private static ReviewDAO instance;

	public static ReviewDAO getInstance() {
		if (instance == null)
			synchronized (ReviewDAO.class) {
				if (instance == null)
					instance = new ReviewDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<Review> getReview;

	private ReviewDAO() {
		table = new TableUtility(new DatabaseTable("review", "review_id",
				new String[] { "review_opinion", "reviewer_id", "author_informed", "research_id" }));
		getReview = (ResultSet rs) -> {
			Review review = new Review();
			try {
				review.setReviewID(rs.getLong(table.getTable().getPrimaryKey()));
				review.setOpinion(rs.getString(table.getTable().getColumnName(1)));
				review.setReviewerID(rs.getInt(table.getTable().getColumnName(2)));
				review.setAuthorInformed(BooleanUtil.parse(rs.getBoolean(table.getTable().getColumnName(3))));
				review.setResearchID(rs.getLong(table.getTable().getColumnName(4)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return review;
		};
	}

	public Review createReview(long researchID, int reviewerID, String opinion) {
		Review review = table.create(new String[] { opinion }, new int[] { reviewerID, 0 }, new long[] { researchID },
				getReview);
		Logging.getInstance().change("Create",
				"Add review to " + ResearchDAO.getInstance().getResearch(review.getResearchID()));
		return review;
	}

	public void deleteReview(Review review) {
		table.delete(review.getReviewID());
		Logging.getInstance().change("Delete",
				"Delete review for " + ResearchDAO.getInstance().getResearch(review.getResearchID()));
	}

	/**
	 * @return all reviews form the table
	 */
	public ObservableList<Review> getAll() {
		return table.getAll(getReview);
	}

	/**
	 * @param name - opinion
	 * @return all reviews with same opinion
	 */
	public ObservableList<Review> getAllByName(String name) {
		return table.findWhere(new String[] { table.getTable().getColumnName(1) }, new String[] { name }, getReview);
	}

	/**
	 * There should be max 2 reviews per research
	 * 
	 * @param researchID
	 * @return all reviews for specified research
	 */
	public ObservableList<Review> getAllFor(long researchID) {
		return table.findBy(researchID, 4, getReview);
	}

	/**
	 * @return all reviews that author is not informed about
	 */
	public ObservableList<Review> getAllThatShouldBeInformed() {
		return table.findWhere(new String[] { table.getTable().getColumnName(3) }, new long[] { 0 }, getReview);
	}

	/**
	 * @param reviewerID
	 * @return all reviews from specified reviewer
	 */
	public ObservableList<Review> getAllFrom(int reviewerID) {
		return table.findBy(reviewerID, 2, getReview);
	}

	/**
	 * Search for review with primary key - id
	 * 
	 * @param id
	 */
	public Review getReviewByID(long id) {
		return table.findBy(id, getReview);
	}

	public void updateOpinion(Review review, String opinion) {
		table.update(review.getReviewID(), 1, opinion);
		review.setOpinion(opinion);
		Logging.getInstance().change("Update",
				"Update opinion for " + ResearchDAO.getInstance().getResearch(review.getResearchID()));
	}

	public void updateReviewer(Review review, int reviewerID) {
		table.update(review.getReviewID(), 2, reviewerID);
		review.setReviewerID(reviewerID);
		Logging.getInstance().change("Update",
				"Update reviewer for " + ResearchDAO.getInstance().getResearch(review.getResearchID()));
	}

	public void updateIsAuthorInformed(Review review, boolean isInformed) {
		table.update(review.getReviewID(), 3, BooleanUtil.parse(isInformed));
		review.setIsAuthorInformed(isInformed);
		Logging.getInstance().change("Update",
				"Update author informed for " + ResearchDAO.getInstance().getResearch(review.getResearchID()));
	}
}
