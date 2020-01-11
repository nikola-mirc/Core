package alfatec.controller.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.controlsfx.control.PrefixSelectionComboBox;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTabPane;

import alfatec.controller.email.GroupCallController;
import alfatec.controller.email.SendEmailController;
import alfatec.controller.user.ChangePasswordController;
import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.country.CountryDAO;
import alfatec.dao.person.AuthorDAO;
import alfatec.dao.relationship.ConferenceCallDAO;
import alfatec.dao.user.UserDAO;
import alfatec.dao.utils.Logging;
import alfatec.model.country.Country;
import alfatec.model.enums.Institution;
import alfatec.model.person.Author;
import alfatec.model.relationship.ConferenceCall;
import alfatec.model.user.LoginData;
import alfatec.model.user.User;
import alfatec.view.gui.MainView;
import alfatec.view.utils.GUIUtils;
import alfatec.view.utils.Utility;
import database.DatabaseUtility;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainInterfaceController extends GUIUtils implements Initializable {

	@FXML
	private Button quitButton, minimizeButton, closePopupButton, clearPopupButton;

	@FXML
	private Label welcomeLabel, firstNameLabel, lastNameLabel, emailLabel, institutionLabel, institutionNameLabel,
			countryLabel, firstNameErrorLabel, lastNameErrorLabel, emailErrorLabel, institutionErrorLabel,
			institutionNameErrorLabel, countryErrorLabel;

	@FXML
	private Hyperlink changePasswordHyperlink, logoutHyperlink;

	@FXML
	private JFXTabPane tabPane;

	@FXML
	private TextField searchAuthorTextField, firstNameTextField, lastNameTextField, emailTextField,
			institutionNameTextField;

	@FXML
	private TableView<Author> authorsTableView;

	@FXML
	private TableColumn<Author, String> authorColumn, emailColumn;

	@FXML
	private JFXButton addAuthorButton, editAuthorButton, deleteAuthorButton, saveAuthorButton, firstInviteButton,
			secondInviteButton, thirdInvButton, sendEmailButton;

	@FXML
	private HBox authorDetailsHbox, invitesHbox;

	@FXML
	private VBox popupVbox, detailsVbox;

	@FXML
	private JFXRadioButton firstRadio, secondRadio, thirdRadio;

	@FXML
	private JFXCheckBox interestedCheckbox;

	@FXML
	private ComboBox<Institution> institutionComboBox;

	@FXML
	private PrefixSelectionComboBox<Country> countryComboBox;

	@FXML
	private TextArea noteTextArea, noteTextAreaPreview;

	@FXML
	private ToggleGroup group;

	private ChangePasswordController changePasswordController;
	private SendEmailController send;
	private GroupCallController groupCall;
	private ObservableList<Author> authorsData;
	private String email;
	private Author author;
	private LoginData loginData;
	private boolean addAction, editAction, popupOpen;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		authorsTableView.setPlaceholder(new Label("Database table \"author\" is empty"));
		Utility.setUpStringCell(authorsTableView);
		authorsData = AuthorDAO.getInstance().getAllAuthors();
		populateAuthorTable();
		handleSearch();
		setUpRadioButton(firstRadio);
		setUpRadioButton(secondRadio);
		setUpRadioButton(thirdRadio);
		setUpBoxes();
		setUpFields();
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
			detailsVbox.setDisable(true);
			invitesHbox.setVisible(false);
		}
	}

	private void populateAuthorTable() {
		authorColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorFirstNameProperty().concat(" ")
				.concat(cellData.getValue().getAuthorLastNameProperty()));
		emailColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorEmailProperty());
		authorsTableView.setItems(authorsData);
		authorsTableView.setOnMousePressed(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				if (authorsTableView.getSelectionModel().getSelectedItem() != null) {
					if (isPopupOpen())
						closePopup();
					transitionPopupX(authorDetailsHbox, 1200, 0, Interpolator.EASE_IN, 500);
					showAuthor(authorsTableView.getSelectionModel().getSelectedItem());
				}
		});
	}

	private void handleSearch() {
		searchAuthorTextField.setOnKeyTyped(event -> {
			String search = searchAuthorTextField.getText();
			Pattern pattern = Pattern.compile("[@()]");
			Matcher matcher = pattern.matcher(search);
			if (search.length() > 0 && !matcher.find()) {
				ObservableList<Author> searched = AuthorDAO.getInstance().searchForAuthors(search);
				authorsTableView.getItems().setAll(searched);
			} else {
				authorsData = AuthorDAO.getInstance().getAllAuthors();
				authorsTableView.getItems().setAll(authorsData);
			}
		});
	}

	@FXML
	void addAuthor(ActionEvent event) {
		setUpBoxes();
		if (isPopupOpen())
			closePopup();
		setAddAction(true);
		transitionPopupX(popupVbox, 940, 0, Interpolator.EASE_IN, 500);
		popupVbox.setVisible(true);
		setPopupOpen(true);
	}

	@FXML
	void editAuthor() {
		if (isPopupOpen())
			closePopup();
		setEditAction(true);
		author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null) {
			transitionPopupX(popupVbox, 520, 0, Interpolator.EASE_IN, 500);
			setAuthor(author);
			popupVbox.setVisible(true);
			setPopupOpen(true);
		}
	}

	@FXML
	void deleteAuthor(ActionEvent event) {
		author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null) {
			ButtonType bt = confirmationAlert("Please confirm:", "Are you sure you want to delete data for "
					+ author.getAuthorFirstName() + " " + author.getAuthorLastName() + "?", AlertType.CONFIRMATION);
			if (bt == ButtonType.OK) {
				AuthorDAO.getInstance().deleteAuthor(author);
				AuthorDAO.getInstance().getAllAuthors().remove(author);
				authorsData.remove(author);
				int row = authorsTableView.getSelectionModel().getSelectedIndex() - 1;
				authorsTableView.getItems().remove(author);
				refresh(row);
				closeDetails();
			}
		}
	}

	@FXML
	void saveAuthor(ActionEvent event) {
		if (isAddAction()) {
			setAddAction(false);
			if (isValidInput() && !isEmailAlreadyInDB()) {
				getNewAuthor();
				authorsData.add(author);
				refresh(authorsData.size() - 1);
				closeDetails();
				transitionPopupX(authorDetailsHbox, 1200, 0, Interpolator.EASE_IN, 500);
				showAuthor(author);
			} else if (isEmailAlreadyInDB())
				emailErrorLabel.setText("E-mail already exists in database.");
		} else if (isEditAction()) {
			setEditAction(false);
			author = authorsTableView.getSelectionModel().getSelectedItem();
			email = author.getAuthorEmail();
			if (isValidInput())
				if (!emailTextField.getText().equals(email) && isEmailAlreadyInDB())
					emailErrorLabel.setText("Database already has enter with the same e-mail address.");
				else {
					handleEditAuthor();
					refresh(authorsTableView.getSelectionModel().getSelectedIndex());
					showAuthor(author);
				}
		}
	}

	public Author getNewAuthor() {
		if (isValidInput()) {
			author = AuthorDAO.getInstance().createAuthor(firstNameTextField.getText(), lastNameTextField.getText(),
					emailTextField.getText(), countryComboBox.getSelectionModel().getSelectedItem().getCountryName(),
					institutionComboBox.getSelectionModel().getSelectedItem().name().toLowerCase(),
					institutionNameTextField.getText(), noteTextArea.getText());
			Logging.getInstance().change("Create", "Create author: " + author.getAuthorEmail());
		}
		return author;
	}

	private void handleEditAuthor() {
		setFirstName();
		setLastName();
		setEmail();
		setCountry();
		setInstitutionType();
		setInstitutionName();
		setNote();
		authorsTableView.refresh();
	}

	public void setAuthor(Author author) {
		this.author = author;
		firstNameTextField.setText(author.getAuthorFirstName());
		lastNameTextField.setText(author.getAuthorLastName());
		emailTextField.setText(author.getAuthorEmail());
		countryComboBox.getSelectionModel().select(author.getCountryID() - 1);
		institutionComboBox.getSelectionModel().select(author.getInstitution());
		institutionNameTextField.setText(author.getInstitutionName());
		noteTextArea.setText(author.getNote());
	}

	@FXML
	void sendEmail() {
		author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null)
			send = MainView.getInstance().loadEmailWindow(send, author.getAuthorEmail());
	}

	@FXML
	private void sendFirstInvite() {
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
	private void sendSecondInvite() {
		List<String> list = new ArrayList<String>();
		for (Author a : authorsData) {
			ConferenceCall call = ConferenceCallDAO.getInstance().getCurrentAnswer(a.getAuthorID());
			if (call != null && !call.isFirstCallAnswered() && call.isInterested())
				list.add(a.getAuthorEmail());
		}
		groupCall = MainView.getInstance().loadEmailWindow(groupCall, list);
	}

	@FXML
	private void sendThirdInvite() {
		List<String> list = new ArrayList<String>();
		for (Author a : authorsData) {
			ConferenceCall call = ConferenceCallDAO.getInstance().getCurrentAnswer(a.getAuthorID());
			if (call != null && !call.isSecondCallAnswered() && call.isInterested())
				list.add(a.getAuthorEmail());
		}
		groupCall = MainView.getInstance().loadEmailWindow(groupCall, list);
	}

	@FXML
	void changePassword(ActionEvent event) {
		changePasswordController = MainView.getInstance().loadChangePassword(changePasswordController, loginData);
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
		DatabaseUtility.getInstance().databaseDisconnect();
		Platform.exit();
	}

	private void setUpRadioButton(RadioButton button) {
		button.setToggleGroup(group);
	}

	private boolean isAddAction() {
		return addAction;
	}

	private void setAddAction(boolean action) {
		this.addAction = action;
	}

	private boolean isEditAction() {
		return editAction;
	}

	private void setEditAction(boolean action) {
		this.editAction = action;
	}

	private boolean isPopupOpen() {
		return popupOpen;
	}

	private void setPopupOpen(boolean action) {
		this.popupOpen = action;
	}

	private boolean isValidInput() {
		firstNameErrorLabel.setText(isValidFirstName() ? "" : "Empty first name field.");
		lastNameErrorLabel.setText(isValidLastName() ? "" : "Empty last name field.");
		emailErrorLabel.setText(isValidEmail() ? "" : "Empty or invalid email field.");
		return isValidEmail() && isValidFirstName() && isValidLastName();
	}

	private boolean isValidFirstName() {
		return firstNameTextField.getText() != null && firstNameTextField.getText().length() != 0;
	}

	private boolean isValidLastName() {
		return lastNameTextField.getText() != null && lastNameTextField.getText().length() != 0;
	}

	private boolean isValidEmail() {
		return emailTextField.getText() != null && emailTextField.getText().length() != 0
				&& emailTextField.getText().contains(".") && emailTextField.getText().contains("@");
	}

	private boolean isEmailAlreadyInDB() {
		return AuthorDAO.getInstance().findAuthorByExactEmail(emailTextField.getText()) != null;
	}

	public void setFirstName() {
		if (!firstNameTextField.getText().equalsIgnoreCase(author.getAuthorFirstName()))
			AuthorDAO.getInstance().updateAuthorFirstName(author, firstNameTextField.getText());
	}

	public void setLastName() {
		if (!lastNameTextField.getText().equalsIgnoreCase(author.getAuthorLastName()))
			AuthorDAO.getInstance().updateAuthorLastName(author, lastNameTextField.getText());
	}

	public void setEmail() {
		if (!emailTextField.getText().equalsIgnoreCase(author.getAuthorEmail()))
			AuthorDAO.getInstance().updateAuthorEmail(author, emailTextField.getText());
	}

	public void setInstitutionType() {
		if (!institutionComboBox.getSelectionModel().getSelectedItem().name()
				.equalsIgnoreCase(author.getInstitution().name())) {
			AuthorDAO.getInstance().updateAuthorInstitution(author,
					institutionComboBox.getSelectionModel().getSelectedItem().name().toLowerCase());
		}
	}

	public void setInstitutionName() {
		if (institutionNameTextField.getText() == null)
			return;
		if (!institutionNameTextField.getText().equals(author.getInstitutionName()))
			AuthorDAO.getInstance().updateAuthorInstitutionName(author, institutionNameTextField.getText());
	}

	public void setCountry() {
		if (countryComboBox.getSelectionModel().getSelectedItem().getCountryID() != author.getCountryID())
			AuthorDAO.getInstance().updateAuthorCountry(author,
					countryComboBox.getSelectionModel().getSelectedItem().getCountryName());
	}

	public void setNote() {
		if (noteTextArea.getText() == null)
			return;
		if (!noteTextArea.getText().equalsIgnoreCase(author.getNote()))
			AuthorDAO.getInstance().updateAuthorNote(author, noteTextArea.getText());
	}

	@FXML
	private void clearPopup(ActionEvent event) {
		clearPopup();
	}

	@FXML
	private void closePopup(ActionEvent event) {
		closePopup();
		setPopupOpen(false);
	}

	private void closePopup() {
		transitionPopupX(popupVbox, 0, 520, Interpolator.EASE_IN, 500);
		clearPopup();
	}

	private void closeDetails() {
		transitionPopupX(authorDetailsHbox, 0, 1200, Interpolator.EASE_IN, 500);
	}

	private void clearPopup() {
		firstNameTextField.clear();
		lastNameTextField.clear();
		emailTextField.clear();
		institutionComboBox.setPromptText("Please select...");
		institutionNameTextField.clear();
		noteTextArea.clear();
		setUpBoxes();
		firstNameErrorLabel.setText("");
		lastNameErrorLabel.setText("");
		emailErrorLabel.setText("");
	}

	private void refresh(int row) {
		authorsTableView.refresh();
		authorsTableView.requestFocus();
		authorsTableView.getSelectionModel().select(row);
		authorsTableView.scrollTo(row);
		closePopup();
		setPopupOpen(false);
	}

	private void setUpBoxes() {
		institutionComboBox.getItems().setAll(FXCollections.observableArrayList(Institution.values()));
		institutionComboBox.setValue(Institution.UNIVERSITY);
		countryComboBox.getItems()
				.setAll(FXCollections.observableArrayList(CountryDAO.getInstance().getAllCountries()));
		countryComboBox.setValue(CountryDAO.getInstance().getCountry(195));
	}

	private void showAuthor(Author author) {
		email = author.getAuthorEmail();
		authorDetailsHbox.setVisible(true);
		firstNameLabel.setText(author.getAuthorFirstName() + " " + author.getAuthorLastName());
		emailLabel.setText(email);
		institutionLabel.setText(author.getInstitution().name());
		institutionNameLabel.setText(author.getInstitutionName());
		countryLabel.setText(author.countryProperty().get());
		noteTextAreaPreview.setText(author.getNoteProperty().get());
	}

	private void setUpFields() {
		firstNameTextField.setTextFormatter(new TextFormatter<String>(rejectChange(getFirstNameLength())));
		lastNameTextField.setTextFormatter(new TextFormatter<String>(rejectChange(getLastNameLength())));
		emailTextField.setTextFormatter(new TextFormatter<String>(rejectChange(getEmailLength())));
		institutionNameTextField.setTextFormatter(new TextFormatter<String>(rejectChange(getInstitutionNameLength())));
		noteTextArea.setTextFormatter(new TextFormatter<String>(rejectChange(getNoteLength())));
		noteTextArea.setWrapText(true);
		noteTextAreaPreview.setWrapText(true);
	}
}
