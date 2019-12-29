package alfatec.controller.conference;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ConferenceTabController {

	@FXML
	private HBox noConferenceHbox;

	@FXML
	private JFXButton newConferenceButton;

	@FXML
	private HBox addConferenceHbox;

	@FXML
	private TextField conferenceTitleTextField;

	@FXML
	private ComboBox<?> conferenceFieldComboBox;

	@FXML
	private TextField feeNameTextField;

	@FXML
	private ComboBox<?> feeCurrencyComboBox;

	@FXML
	private TextField feeAmountTextField;

	@FXML
	private DatePicker startDatePicker;

	@FXML
	private DatePicker endDatePicker;

	@FXML
	private DatePicker firstCallDatePicker;

	@FXML
	private DatePicker secondCallDatePicker;

	@FXML
	private DatePicker thirdCallDatePicker;

	@FXML
	private VBox notesVbox;

	@FXML
	private Button cancelAddConferenceButton;

	@FXML
	private TextArea conferenceNotes;

	@FXML
	private JFXButton saveConferenceButton;

	@FXML
	private HBox activeConferenceHbox;

	@FXML
	private Label activeConferenceLabel;

	@FXML
	private Label startDateLabel;

	@FXML
	private Label endDateLabel;

	@FXML
	private Label firstCallDateLabel;

	@FXML
	private Label secondCallDateLabel;

	@FXML
	private Label thirdCallDateLabel;

	@FXML
	private Label conferenceFieldLabel;

	@FXML
	private Label feePresetLabel;

	@FXML
	private Label currencyLabel;

	@FXML
	private Label amountLabel;

	@FXML
	private JFXButton editConferenceButton;

	@FXML
	private JFXButton closeConferenceButton;

	@FXML
	private HBox detailsHbox;

	@FXML
	private TableView<?> conferenceTableView;

	@FXML
	private TableColumn<?, ?> confTitleColumn;

	@FXML
	private TableColumn<?, ?> confRealizedColumn;

	@FXML
	private JFXButton previewConferenceButton;

	@FXML
	private TableView<?> reviewersTableView;

	@FXML
	private TableColumn<?, ?> reviewerNameColumn;

	@FXML
	private TableColumn<?, ?> reviewerLastNameColumn;

	@FXML
	private TableColumn<?, ?> reviewerInstitutionColumn;

	@FXML
	private TableColumn<?, ?> reviewerContactColumn;

	@FXML
	private TableColumn<?, ?> reviewerEmailColumn;

	@FXML
	private JFXButton addReviewerButton;

	@FXML
	private JFXButton editReviewerButton;

	@FXML
	private JFXButton deleteReviewerButton;

	@FXML
	void createNewConference(ActionEvent event) {
		noConferenceHbox.setVisible(false);
		addConferenceHbox.setVisible(true);
	}

	@FXML
	void cancelAddConference(ActionEvent event) {
		addConferenceHbox.setVisible(false);
		noConferenceHbox.setVisible(true);
	}

	@FXML
	void saveConference(ActionEvent event) {
		addConferenceHbox.setVisible(false);
		activeConferenceHbox.setVisible(true);
	}

	@FXML
	void editConference(ActionEvent event) {
		activeConferenceHbox.setVisible(false);
		addConferenceHbox.setVisible(true);
	}

	@FXML
	void closeConference(ActionEvent event) {

	}

	@FXML
	void previewConference(ActionEvent event) {

	}

	@FXML
	void addReviewer(ActionEvent event) {

	}

	@FXML
	void editReviewer(ActionEvent event) {

	}

	@FXML
	void deleteReviewer(ActionEvent event) {

	}

}
