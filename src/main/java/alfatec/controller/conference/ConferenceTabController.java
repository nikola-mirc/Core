package alfatec.controller.conference;

import com.jfoenix.controls.JFXButton;

import alfatec.dao.conference.ConferenceDAO;
import alfatec.model.conference.Field;
import alfatec.model.enums.Currency;
import alfatec.model.user.LoginData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
	private ComboBox<Field> conferenceFieldComboBox;

	@FXML
	private TextField feeNameTextField;

	@FXML
	private ComboBox<Currency> feeCurrencyComboBox;

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

	BooleanProperty conferenceActive = new SimpleBooleanProperty(false);
	BooleanProperty noActiveConference = new SimpleBooleanProperty(true);
	BooleanProperty addingConferenceActive = new SimpleBooleanProperty(false);

	@FXML
	private void initialize() {
		if (ConferenceDAO.getInstance().getCurrentConference() != null) {
			this.conferenceActive.set(true);
			this.noActiveConference.set(false);
		}
		activeConferenceHbox.visibleProperty().bind(conferenceActive);
		noConferenceHbox.visibleProperty().bind(noActiveConference);
		addConferenceHbox.visibleProperty().bind(addingConferenceActive);
	}

	@FXML
	void createNewConference(ActionEvent event) {
		noActiveConference.set(false);
		addingConferenceActive.set(true);
	}

	@FXML
	void cancelAddConference(ActionEvent event) {
		addingConferenceActive.set(false);
		noActiveConference.set(true);
	}

	@FXML
	void saveConference(ActionEvent event) {
		addingConferenceActive.set(false);
		conferenceActive.set(true);
	}

	@FXML
	void editConference(ActionEvent event) {
		conferenceActive.set(false);
		addingConferenceActive.set(true);
	}

	@FXML
	void closeConference(ActionEvent event) {
		conferenceActive.set(false);
		noActiveConference.set(true);
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

	public void disablePartsForAdminAccess(LoginData ld) {
		if (ld.getRoleID() == 2) {
			newConferenceButton.setVisible(false);
			editConferenceButton.setVisible(false);
			closeConferenceButton.setVisible(false);
			detailsHbox.setVisible(false);
		}
	}
}
