package alfatec.controller.conference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXButton;

import alfatec.dao.person.ReviewerDAO;
import alfatec.model.person.Reviewer;
import alfatec.view.utils.GUIUtils;
import alfatec.view.utils.Utility;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SearchReviewersController extends GUIUtils {

	@FXML
	private TableView<Reviewer> reviewerTableView;

	@FXML
	private TableColumn<Reviewer, String> reviewerNameColumn, reviewerLastNameColumn, institutionNameColumn,
			emailColumn, countryColumn;

	@FXML
	private TextField searchReviewerTextField;

	@FXML
	private JFXButton selectReveiwerButton;

	@FXML
	private Button closeButton;

	private Stage display;
	private ObservableList<Reviewer> reviewers;
	private Reviewer reviewer;

	@FXML
	void initialize() {
		reviewers = ReviewerDAO.getInstance().getAllReviewers();
		reviewer = null;
		populateTable();
		handleSearch();
	}

	@FXML
	void close(ActionEvent event) {
		display.close();
	}

	@FXML
	void selectReviewer(ActionEvent event) {
		reviewer = reviewerTableView.getSelectionModel().getSelectedItem();
		display.close();
	}

	public void setDisplayStage(Stage stage) {
		this.display = stage;
	}

	private void populateTable() {
		reviewerTableView.setPlaceholder(new Label("Database table \"reviewer\" is empty"));
		Utility.setUpStringCell(reviewerTableView);
		reviewerNameColumn.setCellValueFactory(cellData -> cellData.getValue().getReviewerFirstNameProperty());
		reviewerLastNameColumn.setCellValueFactory(cellData -> cellData.getValue().getReviewerLastNameProperty());
		emailColumn.setCellValueFactory(cellData -> cellData.getValue().getReviewerEmailProperty());
		institutionNameColumn.setCellValueFactory(cellData -> cellData.getValue().getInstitutionNameProperty());
		countryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
		reviewerTableView.setItems(reviewers);
	}

	private void handleSearch() {
		searchReviewerTextField.setOnKeyTyped(event -> {
			String search = searchReviewerTextField.getText();
			Pattern pattern = Pattern.compile("[@()\\\\<>+~%\\*\\-\\'\"]");
			Matcher matcher = pattern.matcher(search);
			if (search.length() > 0 && !matcher.find()) {
				ObservableList<Reviewer> searched = ReviewerDAO.getInstance().getReviewers(search);
				reviewerTableView.getItems().setAll(searched);
			} else {
				reviewers = ReviewerDAO.getInstance().getAllReviewers();
				reviewerTableView.getItems().setAll(reviewers);
			}
		});
	}

	public Reviewer getSelectedReviewer() {
		return reviewer;
	}
}
