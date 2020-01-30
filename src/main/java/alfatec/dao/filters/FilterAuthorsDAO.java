package alfatec.dao.filters;

import java.sql.ResultSet;
import java.util.ArrayList;

import alfatec.dao.country.CountryDAO;
import database.DatabaseUtility;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class FilterAuthorsDAO {

	private static FilterAuthorsDAO instance;

	public static FilterAuthorsDAO getInstance() {
		if (instance == null)
			synchronized (CountryDAO.class) {
				if (instance == null)
					instance = new FilterAuthorsDAO();
			}
		return instance;
	}

	private static String[] columnsForComboBoxes = { "author.institution", "author.institution_name",
			"author.country_id", "conference.conference_title", "field.field_name", "review.review_opinion" };
	private static String[] columnsForCheckBoxes = { "paperwork.for_collection", "paperwork.sent_to_review",
			"paperwork.submittet_work", "conference_call.first_call_answer", "conference_call.second_call_answer",
			"conference_call.third_call_answer", "conference_call.interested" };

	public static ResultSet createStatementForFilter(String condition) {
		String query = "SELECT author.author_id, author.country_id, author.author_first_name, author.author_last_name, author.author_email, author.institution, author.institution_name, author.note, author.validate_email FROM author \n"
				+ "LEFT OUTER JOIN conference_call ON author.author_id = conference_call.author_id\n"
				+ "LEFT OUTER JOIN conference ON conference_call.conference_id = conference.conference_id\n"
				+ "LEFT OUTER JOIN author_research ON author.author_id = author_research.author_id \n"
				+ "LEFT OUTER JOIN research ON author_research.research_id = research.research_id \n"
				+ "LEFT OUTER JOIN paperwork ON research.research_id = paperwork.research_id\n"
				+ "LEFT OUTER JOIN review ON research.research_id = review.research_id\n"
				+ "LEFT OUTER JOIN field ON conference.field_id = field.field_id ";

		if (!condition.isBlank())
			query += "WHERE";
		query += condition;
		query = query.replace("WHERE AND ", "WHERE ");
		return DatabaseUtility.getInstance().executeQuery(query);

	}

	private static String createConditionForComboBox(ComboBox<String> comboBox, String column) {
		if (comboBox.getSelectionModel().getSelectedItem() != null) {
			if (column.equals("author.country_id"))
				return " AND " + column + " = " + CountryDAO.getInstance()
						.getCountry(comboBox.getSelectionModel().getSelectedItem()).getCountryID();
			else
				return " AND " + column + " = \'" + comboBox.getSelectionModel().getSelectedItem() + "\'";
		} else
			return "";
	}

	private static String createConditionForCheckBox(CheckBox cb, String column) {
		if (cb.isSelected())
			return " AND " + column + " = 1";
		else
			return "";
	}

	public static ResultSet filterAuthors(ArrayList<ComboBox<String>> comboes, CheckBox[] checks) {
		String conditions = "";
		for (int i = 0; i < columnsForComboBoxes.length; i++) {
			conditions += createConditionForComboBox(comboes.get(i), columnsForComboBoxes[i]);
		}
		for (int i = 0; i < columnsForCheckBoxes.length; i++) {
			conditions += createConditionForCheckBox(checks[i], columnsForCheckBoxes[i]);
		}
		ResultSet filtered = createStatementForFilter(conditions);
		return filtered;
	}
}
