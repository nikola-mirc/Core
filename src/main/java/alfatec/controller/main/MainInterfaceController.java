package alfatec.controller.main;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;

import alfatec.controller.author.AuthorsPopupController;
import alfatec.controller.email.SendEmailController;
import alfatec.controller.user.ChangePasswordController;
import alfatec.dao.person.AuthorDAO;
import alfatec.dao.user.UserDAO;
import alfatec.model.person.Author;
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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
	private TableColumn<Author, String> authorNameColumn;

	@FXML
	private TableColumn<Author, String> authorLastNameColumn;

	@FXML
	private TableColumn<Author, String> authorInstitutionColumn;

	@FXML
	private TableColumn<Author, String> authorEmailColumn;

	@FXML
	private TableColumn<Author, String> authorCountryColumn;

	@FXML
	private JFXTextArea detailsTextArea;

	@FXML
	private Button sendEmailButton;

	@FXML
	private TextField searchAuthorTextField;

	@FXML
	private JFXButton addAuthorButton;

	@FXML
	private JFXButton editAuthorButton;

	@FXML
	private JFXButton deleteAuthorButton;

	private AuthorsPopupController authorController;
	private ChangePasswordController changePasswordController;
	private SendEmailController send;
	private ObservableList<Author> authorsData;
	private String institution;
	private String note;
	private LoginData loginData;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		authorsTableView.setPlaceholder(new Label("Database table \"author\" is empty"));
		authorsData = AuthorDAO.getInstance().getAllAuthors();
		MainView.getInstance().loadTabs(tabPane);
		populateAuthorTable();
		handleSearch();
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
		Author author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null) {
			AuthorDAO.getInstance().deleteAuthor(author);
			AuthorDAO.getInstance().getAllAuthors().remove(author);
			authorsData.remove(author);
			authorsTableView.getItems().remove(author);
			int row = authorsData.size() - 1;
			authorsTableView.requestFocus();
			authorsTableView.getSelectionModel().select(row);
			authorsTableView.scrollTo(row);
		} else {
			alert(AlertType.WARNING, "No author selected",
					"Please select an author from the table in order to send them email.");
		}
	}

	@FXML
	void editAuthor() {
		Author author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null) {
			authorController = MainView.getInstance().loadEdit(authorController, author);
			handleEditAuthor();
		} else {
			alert(AlertType.WARNING, "No author selected",
					"Please select an author from the table in order to send them email.");
		}
	}

	@FXML
	void sendEmail() {
		Author author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null) {
			send = MainView.getInstance().loadEmailWindow(send, author.getAuthorEmail());
		} else {
			alert(AlertType.WARNING, "No author selected",
					"Please select an author from the table in order to send them email.");
		}
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
			Author author = authorController.getNewAuthor();
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
}
