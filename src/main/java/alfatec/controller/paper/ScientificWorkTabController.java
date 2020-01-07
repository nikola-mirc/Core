package alfatec.controller.paper;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.controlsfx.control.PrefixSelectionComboBox;

public class ScientificWorkTabController {

    @FXML
    private TextField searchScientificWorkField;

    @FXML
    private TableView<?> applicationsTabbleView;

    @FXML
    private TableColumn<?, ?> authorsColumn;

    @FXML
    private TableColumn<?, ?> titleColumn;

    @FXML
    private JFXButton addApplicationButton;

    @FXML
    private JFXButton updateApplicationButton;

    @FXML
    private JFXButton insertAuthorButton;

    @FXML
    private TableView<?> miniAuthorTableView;

    @FXML
    private TableColumn<?, ?> miniAuthorColumn;

    @FXML
    private TextField swTitle;

    @FXML
    private JFXCheckBox paperReceivedCheckBox;

    @FXML
    private JFXButton importPaperButton;

    @FXML
    private TextField selectReviewerTextField;

    @FXML
    private JFXButton selectReviewerButton;

    @FXML
    private JFXCheckBox sentCheckBox;

    @FXML
    private JFXButton sendForReviewButton;

    @FXML
    private JFXButton rwAcceptedButton;

    @FXML
    private JFXButton rwSmallButton;

    @FXML
    private JFXButton rwBigButton;

    @FXML
    private JFXButton rwRejectedButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button clearButton;

    @FXML
    private PrefixSelectionComboBox<?> feeComboBox;

    @FXML
    private JFXCheckBox feePaidCheckBox;

    @FXML
    private JFXRadioButton liveRadio;

    @FXML
    private JFXRadioButton videoRadio;

    @FXML
    private JFXRadioButton pptRadio;

    @FXML
    private JFXRadioButton collectionRadio;

    @FXML
    private JFXRadioButton specialIssueRadio;

    @FXML
    private TextField swNote;

    @FXML
    private JFXButton sendEmailButton;

    @FXML
    void addApplication(ActionEvent event) {

    }

    @FXML
    void clearPopup(ActionEvent event) {

    }

    @FXML
    void closePopup(ActionEvent event) {

    }

    @FXML
    void importPaper(ActionEvent event) {

    }

    @FXML
    void insertAuthor(ActionEvent event) {

    }

    @FXML
    void selectReviewer(ActionEvent event) {

    }

    @FXML
    void sendEmail(ActionEvent event) {

    }

    @FXML
    void sendForReview(ActionEvent event) {

    }

    @FXML
    void updateApplication(ActionEvent event) {

    }

}
