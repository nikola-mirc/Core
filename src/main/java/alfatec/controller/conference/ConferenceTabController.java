package alfatec.controller.conference;

import com.jfoenix.controls.JFXButton;

import alfatec.dao.conference.ConferenceDAO;
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

public class ConferenceTabController {

    @FXML
    private TableView<?> confTableView;

    @FXML
    private TableColumn<?, ?> confTitleColumn;

    @FXML
    private TableColumn<?, ?> titleColumn;

    @FXML
    private TableColumn<?, ?> confColumn;

    @FXML
    private TableView<?> specialTableView;

    @FXML
    private TableColumn<?, ?> collectionCol;

    @FXML
    private HBox noConferenceHbox;

    @FXML
    private Label confLabel;

    @FXML
    private JFXButton newConferenceButton;

    @FXML
    private HBox addConferenceHbox;

    @FXML
    private TextField conferenceTitleTextField;

    @FXML
    private ComboBox<?> conferenceFieldComboBox;

    @FXML
    private JFXButton insertFeesButton;

    @FXML
    private TableView<?> feesTableView;

    @FXML
    private TableColumn<?, ?> presetCol;

    @FXML
    private TableColumn<?, ?> currencyCol;

    @FXML
    private TableColumn<?, ?> amountCol;

    @FXML
    private Button closeButton;

    @FXML
    private Button clearButton;

    @FXML
    private TextField confEmail;

    @FXML
    private TextField confPassword;

    @FXML
    private TextField confBcc;

    @FXML
    private DatePicker firstCallDatePicker;

    @FXML
    private DatePicker secondCallDatePicker;

    @FXML
    private DatePicker thirdCallDatePicker;

    @FXML
    private JFXButton saveConfButton;

    @FXML
    private HBox activeConferenceHbox;

    @FXML
    private Button closeButton1;

    @FXML
    private Button clearButton1;

    @FXML
    private Label activeConfLabel;

    @FXML
    private Label confFieldLabel;

    @FXML
    private TableColumn<?, ?> feePresetColumn;

    @FXML
    private TableColumn<?, ?> feeCurrencyColumn;

    @FXML
    private TableColumn<?, ?> feeAmountColumn;

    @FXML
    private Label confStartDate;

    @FXML
    private Label confEndDate;

    @FXML
    private Label firstCallLabel;

    @FXML
    private Label secondCallLabel;

    @FXML
    private Label thirdCallLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextArea confNotesTextArea;

    @FXML
    private JFXButton editConfButton;

    @FXML
    private JFXButton closeConfButton;

    @FXML
    private TableView<?> thisConfCollectionTableView;

    @FXML
    private TableColumn<?, ?> thisConfTitleCol;

    @FXML
    private TableColumn<?, ?> thisConfColumn;

    @FXML
    private TableView<?> thisConfSpecialTableView;

    @FXML
    private TableColumn<?, ?> thisConfCollection;
    
    
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
    void clearPopup(ActionEvent event) {

    }

    @FXML
    void closeConf(ActionEvent event) {

    }

    @FXML
    void closePopup(ActionEvent event) {

    }

    @FXML
    void createNewConference(ActionEvent event) {

    }

    @FXML
    void editConf(ActionEvent event) {

    }

    @FXML
    void insertFees(ActionEvent event) {

    }

    @FXML
    void saveConference(ActionEvent event) {

    }

	public void disablePartsForAdminAccess(LoginData ld) {
		if (ld.getRoleID() == 2) {
			newConferenceButton.setVisible(false);
			editConfButton.setVisible(false);
			closeConfButton.setVisible(false);
		}
	}
}
