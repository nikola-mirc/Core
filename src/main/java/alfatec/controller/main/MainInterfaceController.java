package alfatec.controller.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;

import alfatec.controller.author.AuthorsPopupController;
import alfatec.controller.email.GroupCallController;
import alfatec.controller.email.SendEmailController;
import alfatec.controller.user.ChangePasswordController;
import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.person.AuthorDAO;
import alfatec.dao.relationship.ConferenceCallDAO;
import alfatec.dao.user.UserDAO;
import alfatec.model.person.Author;
import alfatec.model.relationship.ConferenceCall;
import alfatec.model.user.LoginData;
import alfatec.model.user.User;
import alfatec.view.gui.MainView;
import alfatec.view.utils.Utility;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainInterfaceController implements Initializable {

	@FXML
	private Button minimizeButton;

	@FXML
	private Button quitButton;

	@FXML
	private Label welcomeLabel;

	@FXML
	private Hyperlink changePasswordHyperlink;

	@FXML
	private Hyperlink logoutHyperlink;

	@FXML
	private JFXTabPane tabPane;

	@FXML
	private TableView<Author> authorsTableView;

	@FXML
	private TableColumn<Author, String> authorNameColumn, authorLastNameColumn, authorInstitutionColumn,
			authorEmailColumn, authorCountryColumn;

	@FXML
	private RadioButton radio1, radio2, radio3;

	@FXML
	private ToggleGroup group;

	@FXML
	private CheckBox checkBox;

	@FXML
	private JFXTextArea detailsTextArea;

	@FXML
	private JFXButton sendEmailButton, firstCallButton, secondCallButton, thirdCallButton;

	@FXML
	private TextField searchAuthorTextField;

	@FXML
	private JFXButton addAuthorButton, editAuthorButton, deleteAuthorButton;

	private AuthorsPopupController authorController;
	private ChangePasswordController changePasswordController;
	private SendEmailController send;
	private GroupCallController groupCall;
	private ObservableList<Author> authorsData;
	private String institution, note;
	private Author author;
	private LoginData loginData;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		authorsTableView.setPlaceholder(new Label("Database table \"author\" is empty"));
		authorsData = AuthorDAO.getInstance().getAllAuthors();
		populateAuthorTable();
		handleSearch();
		setUpRadioButton(radio1);
		setUpRadioButton(radio2);
		setUpRadioButton(radio3);
	}

	@FXML
	void pressed(MouseEvent event) {
		MainView.getInstance().pressed(event);
	}

	@FXML
	void dragged(MouseEvent event) {
		MainView.getInstance().dragged(event);
	}

	@FXML
	void changePassword(ActionEvent event) {
		changePasswordHyperlink.setDisable(true);
		changePasswordController = MainView.getInstance().loadChangePassword(changePasswordController, loginData);
		changePasswordHyperlink.setDisable(false);
	}

	@FXML
	void minimizeApp() {
		Stage stage = (Stage) minimizeButton.getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	void logout(ActionEvent event) {
		MainView.getInstance().closeMainView(event);
	}

	@FXML
	void quitApp() {
		Platform.exit();
	}

	@FXML
	void addAuthor() {
		authorController = MainView.getInstance().loadAdd(authorController);
		handleNewAuthor();
	}

	@FXML
	void deleteAuthor(ActionEvent event) {
		author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null) {
			AuthorDAO.getInstance().deleteAuthor(author);
			AuthorDAO.getInstance().getAllAuthors().remove(author);
			authorsData.remove(author);
			authorsTableView.getItems().remove(author);
			int row = authorsData.size() - 1;
			authorsTableView.requestFocus();
			authorsTableView.getSelectionModel().select(row);
			authorsTableView.scrollTo(row);
		} else
			alert(AlertType.WARNING, "No author selected",
					"Please select an author from the table in order to send them email.");
	}

	@FXML
	void editAuthor() {
		author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null) {
			authorController = MainView.getInstance().loadEdit(authorController, author);
			handleEditAuthor();
		} else
			alert(AlertType.WARNING, "No author selected",
					"Please select an author from the table in order to send them email.");
	}

	@FXML
	void sendEmail() {
		author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null)
			send = MainView.getInstance().loadEmailWindow(send, author.getAuthorEmail());
		else
			alert(AlertType.WARNING, "No author selected",
					"Please select an author from the table in order to send them email.");
	}

	private void populateAuthorTable() {
		authorNameColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorFirstNameProperty());
		authorLastNameColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorLastNameProperty());
		authorInstitutionColumn
				.setCellValueFactory(cellData -> cellData.getValue().getInstitutionProperty().asString());
		authorEmailColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorEmailProperty());
		authorCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
		Utility.setUpStringCell(authorsTableView);
		authorsTableView.setItems(authorsData);
		authorsTableView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showAuthor(newValue));
	}

	private void showAuthor(Author author) {
		if (author != null) {
			institution = author.getInstitutionName() == null ? " " : author.getInstitutionName();
			note = author.getNote() == null ? " " : author.getNote();
			detailsTextArea.setText("Institution name: \n" + institution + "\n\nNote: \n" + note);
		}
	}

	private void handleSearch() {
		searchAuthorTextField.setOnKeyTyped(event -> {
			String search = searchAuthorTextField.getText();
			if (search.length() > 0) {
				ObservableList<Author> searched = AuthorDAO.getInstance().searchForAuthors(search);
				authorsTableView.getItems().setAll(searched);
			} else {
				authorsData = AuthorDAO.getInstance().getAllAuthors();
				authorsTableView.getItems().setAll(authorsData);
			}
		});
	}

	private void handleNewAuthor() {
		if (authorController.isSaveClicked()) {
			author = authorController.getNewAuthor();
			AuthorDAO.getInstance().getAllAuthors().add(author);
			authorsData.add(author);
			int row = authorsData.size() - 1;
			authorsTableView.requestFocus();
			authorsTableView.getSelectionModel().select(row);
			authorsTableView.scrollTo(row);
			showAuthor(author);
		}
	}

	private void handleEditAuthor() {
		if (authorController.isSaveClicked()) {
			authorController.setFirstName();
			authorController.setLastName();
			authorController.setEmail();
			authorController.setCountry();
			authorController.setInstitutionType();
			institution = authorController.setInstitutionName();
			note = authorController.setNote();
			authorsTableView.refresh();
			detailsTextArea.setText("Institution name: \n" + institution + "\n\nNote: \n" + note);
		}
	}

	private void alert(AlertType type, String headerText, String contentText) {
		Alert alert = new Alert(type);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	public void setWelcomeMessage(LoginData ld) {
		User user = UserDAO.getInstance().getUser(ld.getUserID());
		welcomeLabel.setText("Welcome, " + user.getUserFirstName());
	}

	public void setLoginData(LoginData data) {
		this.loginData = data;
	}

	public void loadTabs(LoginData lgData) {
		if (lgData.getRoleID() != 1)
			MainView.getInstance().loadTabs(tabPane, lgData);
	}

	public void disableOptionsForUsers(LoginData lgData) {
		if (lgData.getRoleID() == 1 || ConferenceDAO.getInstance().getCurrentConference() == null) {
			sendEmailButton.setVisible(false);
			firstCallButton.setDisable(true);
			secondCallButton.setDisable(true);
			thirdCallButton.setDisable(true);
			radio1.setDisable(true);
			radio2.setDisable(true);
			radio3.setDisable(true);
			checkBox.setDisable(true);
		}
	}

	@FXML
	private void makeFirstCall() {
		List<String> list = new ArrayList<String>();
		for (Author a : authorsData)
			list.add(a.getAuthorEmail());
		groupCall = MainView.getInstance().loadEmailWindow(groupCall, list);
		groupCall.setRecievers(list);
		if (groupCall.isSent())
			for (Author a : authorsData)
				ConferenceCallDAO.getInstance().createEntry(
						ConferenceDAO.getInstance().getCurrentConference().getConferenceID(), a.getAuthorID());
	}

	@FXML
	private void makeSecondCall() {
		List<String> list = new ArrayList<String>();
		for (Author a : authorsData) {
			ConferenceCall call = ConferenceCallDAO.getInstance().getCurrentAnswer(a.getAuthorID());
			if (call != null && !call.isFirstCallAnswered() && call.isInterested())
				list.add(a.getAuthorEmail());
		}
		groupCall = MainView.getInstance().loadEmailWindow(groupCall, list);
	}

	@FXML
	private void makeThirdCall() {
		List<String> list = new ArrayList<String>();
		for (Author a : authorsData) {
			ConferenceCall call = ConferenceCallDAO.getInstance().getCurrentAnswer(a.getAuthorID());
			if (call != null && !call.isSecondCallAnswered() && call.isInterested())
				list.add(a.getAuthorEmail());
		}
		groupCall = MainView.getInstance().loadEmailWindow(groupCall, list);
	}

	private void setUpRadioButton(RadioButton button) {
		button.setToggleGroup(group);
	}
}
