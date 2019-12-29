package alfatec.controller.paper;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import alfatec.controller.author.SearchAuthorsController;
import alfatec.controller.conference.SearchReviewersController;
import alfatec.view.gui.ScientificWorkView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ScientificWorkPopupController {

	@FXML
	private Label activeConferenceLabel;

	@FXML
	private TextField searchAuthorTextField;

	@FXML
	private JFXButton searchAuthorButton;

	@FXML
	private TextField workTitleTextField;

	@FXML
	private CheckBox workReceivedCheckBox;

	@FXML
	private TextField searchReviewerTextField;

	@FXML
	private JFXButton searchReviewerButton;

	@FXML
	private CheckBox sentForReviewCheckBox;

	@FXML
	private RadioButton acceptedRadio;

	@FXML
	private RadioButton smallReviewRadio;

	@FXML
	private RadioButton bigReviewRadio;

	@FXML
	private RadioButton rejectedRadio;

	@FXML
	private ComboBox<?> feeComboBox;

	@FXML
	private CheckBox authorInformedCheckBox;

	@FXML
	private RadioButton liveRadio;

	@FXML
	private RadioButton videoRadio;

	@FXML
	private RadioButton pptRadio;

	@FXML
	private Button closeButton;

	@FXML
	private JFXTextArea detailsTextArea;

	@FXML
	private JFXButton sendEmailButton;

	@FXML
	private CheckBox feePaidCheckBox;

	@FXML
	private CheckBox collectionCheckBox;

	@FXML
	private CheckBox specialIssueCheckBox;

	@FXML
	private JFXButton saveScientificWorkButton;

	private SearchReviewersController searchReviewersController;
	private SearchAuthorsController searchAuthorsController;

	private Stage display;
	private double x = 0;
	private double y = 0;
	private Node node;

	@FXML
	void pressed(MouseEvent event) {
		x = event.getSceneX();
		y = event.getSceneY();
	}

	@FXML
	void dragged(MouseEvent event) {
		node = (Node) event.getSource();
		display = (Stage) node.getScene().getWindow();
		display.setX(event.getScreenX() - x);
		display.setY(event.getScreenY() - y);
	}

	@FXML
	void close(ActionEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		stage.close();
	}

	@FXML
	void saveScientificWork(ActionEvent event) {

	}

	@FXML
	void searchAuthor(ActionEvent event) throws IOException {
		searchAuthorsController = ScientificWorkView.getInstance().loadSearchAuthors(searchAuthorsController);
	}

	@FXML
	void searchReviewer(ActionEvent event) throws IOException {
		searchReviewersController = ScientificWorkView.getInstance().loadSearchReviewers(searchReviewersController);
	}

	@FXML
	void sendEmail(ActionEvent event) {

	}

	public void setDisplayStage(Stage stage) {
		this.display = stage;
	}

}