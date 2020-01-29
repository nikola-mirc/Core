package alfatec.controller.main;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.controlsfx.control.PrefixSelectionComboBox;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTabPane;

import alfatec.controller.email.GroupCallController;
import alfatec.controller.email.SendEmailController;
import alfatec.controller.user.ChangePasswordController;
import alfatec.controller.utils.ClearPopUp;
import alfatec.controller.utils.GroupEmail;
import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.conference.FieldDAO;
import alfatec.dao.country.CountryDAO;
import alfatec.dao.filters.FilterAuthorsDAO;
import alfatec.dao.person.AuthorDAO;
import alfatec.dao.relationship.ConferenceCallDAO;
import alfatec.dao.user.UserDAO;
import alfatec.dao.utils.Logging;
import alfatec.model.country.Country;
import alfatec.model.enums.Institution;
import alfatec.model.enums.Opinion;
import alfatec.model.person.Author;
import alfatec.model.relationship.ConferenceCall;
import alfatec.model.user.LoginData;
import alfatec.model.user.User;
import alfatec.view.gui.MainView;
import alfatec.view.utils.GUIUtils;
import alfatec.view.utils.Utility;
import database.DatabaseUtility;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainInterfaceController extends GUIUtils implements Initializable {

	@FXML
	private Button quitButton, minimizeButton, closeDetailsButton, closePopupButton, clearPopupButton;

	@FXML
	private Label welcomeLabel, firstNameLabel, lastNameLabel, emailLabel, institutionLabel, institutionNameLabel,
			countryLabel, emailErrorLabel;

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

	/**
	 * Filters
	 */

	@FXML
	private ComboBox<String> filterInstitution;

	@FXML
	private ComboBox<String> filterInstitutionName;

	@FXML
	private PrefixSelectionComboBox<String> filterCountry;

	@FXML
	private ComboBox<String> filterConference;

	@FXML
	private ComboBox<String> filterField;

	@FXML
	private JFXCheckBox filterCollSpec;

	@FXML
	private JFXCheckBox filterSentForReview;

	@FXML
	private ComboBox<String> filterReviewStatus;

	@FXML
	private JFXCheckBox filterSubmittedWork;

	@FXML
	private JFXCheckBox filterFirstInv;

	@FXML
	private JFXCheckBox filterSecondInv;

	@FXML
	private JFXCheckBox filterThirdInv;

	@FXML
	private JFXCheckBox filterInterested;

	private ChangePasswordController changePasswordController;
	private SendEmailController send;
	private GroupCallController groupCall;
	private ObservableList<Author> authorsData;
	private String email;
	private Author author;
	private LoginData loginData;
	private ConferenceCall call;
	private ClearPopUp popup;
	private GroupEmail groupEmail;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		popup = () -> {
			clearFields(Arrays.asList(firstNameTextField, lastNameTextField, emailTextField, institutionNameTextField),
					Arrays.asList(emailErrorLabel));
			noteTextArea.clear();
			setUpBoxes();
		};
		authorsData = AuthorDAO.getInstance().getAllAuthors();
		populateAuthorTable();
		handleSearch();
		setUpDetails();
		groupEmail = new GroupEmail();
	}

	@FXML
	private void addAuthor(ActionEvent event) {
		if (isPopupOpen())
			closePopup(popupVbox, 520, popup);
		setAddAction(true);
		openPopup(popupVbox, 940);
	}

	@FXML
	private void editAuthor() {
		if (isPopupOpen())
			closePopup(popupVbox, 520, popup);
		setEditAction(true);
		author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null) {
			setAuthor(author);
			openPopup(popupVbox, 520);
		}
	}

	@FXML
	private void deleteAuthor(ActionEvent event) {
		author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null) {
			String name = author.getAuthorFirstName().isEmpty() ? author.getAuthorEmail()
					: author.getAuthorFirstName() + " " + author.getAuthorLastName();
			ButtonType bt = confirmationAlert("Please confirm:",
					"Are you sure you want to delete data for " + name + "?", AlertType.CONFIRMATION);
			if (bt == ButtonType.OK) {
				AuthorDAO.getInstance().deleteAuthor(author);
				AuthorDAO.getInstance().getAllAuthors().remove(author);
				authorsData.remove(author);
				authorsTableView.getItems().remove(author);
				refresh(authorsTableView, authorsTableView.getSelectionModel().getSelectedIndex() - 1, popupVbox, 520,
						popup);
				closeDetails(authorDetailsHbox, 1200);
			}
		}
	}

	@FXML
	private void saveAuthor(ActionEvent event) {
		if (isAddAction()) {
			if (isValidInput() && !isEmailAlreadyInDB()) {
				authorsData.add(getNewAuthor());
				refresh(authorsTableView, authorsData.size() - 1, popupVbox, 520, popup);
				closeDetails(authorDetailsHbox, 1200);
				openDetails(authorDetailsHbox, 1200);
				showAuthor(author);
			} else if (isEmailAlreadyInDB()) {
				emailErrorLabel.setText("E-mail already exists in database.");
				return;
			} else
				return;
			setAddAction(false);
		} else if (isEditAction()) {
			author = authorsTableView.getSelectionModel().getSelectedItem();
			if (isValidInput())
				if (!emailTextField.getText().equals(email) && isEmailAlreadyInDB()) {
					emailErrorLabel.setText("Database already has enter with the same e-mail address.");
					return;
				} else {
					handleEditAuthor();
					refresh(authorsTableView, authorsTableView.getSelectionModel().getSelectedIndex(), popupVbox, 520,
							popup);
					showAuthor(author);
				}
			else
				return;
			setEditAction(false);
		}
	}

	@FXML
	private void sendEmail() {
		author = authorsTableView.getSelectionModel().getSelectedItem();
		if (author != null)
			send = MainView.getInstance().loadEmailWindow(send, author.getAuthorEmail());
	}

	@FXML
	private void sendFirstInvite() {
		ButtonType bt = confirmationAlert("This may take a while",
				"Process of validating and collecting email addresses is time consuming. Please be patient.\nDo you want to continue?",
				AlertType.CONFIRMATION);
		if (bt == ButtonType.OK) {
			groupEmail.prepareEmails();
			boolean sentGroup = groupEmail.getValidFirst() != null ? firstCallGroupHelper() : false;
			boolean sentInvalid = groupEmail.getInvalidFirst() != null ? firstCallInvalidHelper() : false;
			if (!sentGroup && !sentInvalid)
				alert("No more available addresses", "First call for paper is sent to all authors in the database.",
						AlertType.INFORMATION);
		}
	}

	@FXML
	private void sendSecondInvite() {
		ButtonType bt = confirmationAlert("This may take a while",
				"Process of validating and collecting email addresses is time consuming. Please be patient.\nDo you want to continue?",
				AlertType.CONFIRMATION);
		if (bt == ButtonType.OK) {
			groupEmail.prepareEmails();
			boolean sentGroup = groupEmail.getValidSecond() != null ? secondCallGroupHelper() : false;
			boolean sentInvalid = groupEmail.getInvalidSecond() != null ? secondCallInvalidHelper() : false;
			if (!sentGroup && !sentInvalid)
				alert("No more available addresses", "First call for paper is sent to all authors in the database.",
						AlertType.INFORMATION);
		}
	}

	@FXML
	private void sendThirdInvite() {
		ButtonType bt = confirmationAlert("This may take a while",
				"Process of validating and collecting email addresses is time consuming. Please be patient.\nDo you want to continue?",
				AlertType.CONFIRMATION);
		if (bt == ButtonType.OK) {
			groupEmail.prepareEmails();
			boolean sentGroup = groupEmail.getValidThird() != null ? thirdCallGroupHelper() : false;
			boolean sentInvalid = groupEmail.getInvalidThird() != null ? thirdCallInvalidHelper() : false;
			if (!sentGroup && !sentInvalid)
				alert("No more available addresses", "First call for paper is sent to all authors in the database.",
						AlertType.INFORMATION);
		}
	}

	@FXML
	private void changePassword(ActionEvent event) {
		changePasswordController = MainView.getInstance().loadChangePassword(changePasswordController, loginData);
	}

	@FXML
	private void minimizeApp() {
		Stage stage = (Stage) minimizeButton.getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	private void logout(ActionEvent event) {
		MainView.getInstance().closeMainView(event);
	}

	@FXML
	private void quitApp() {
		DatabaseUtility.getInstance().databaseDisconnect();
		Platform.exit();
	}

	@FXML
	private void clearPopup(ActionEvent event) {
		popup.clear();
	}

	@FXML
	private void closePopup(ActionEvent event) {
		closePopup(popupVbox, 520, popup);
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
		authorsTableView.setPlaceholder(new Label("Database table \"author\" is empty"));
		Utility.setUpStringCell(authorsTableView);
		authorColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorFirstNameProperty().concat(" ")
				.concat(cellData.getValue().getAuthorLastNameProperty()));
		emailColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorEmailProperty());
		authorsTableView.setItems(authorsData);
		authorsTableView.setOnMousePressed(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				if (authorsTableView.getSelectionModel().getSelectedItem() != null) {
					if (isPopupOpen())
						closePopup(popupVbox, 520, popup);
					openDetails(authorDetailsHbox, 1200);
					showAuthor(authorsTableView.getSelectionModel().getSelectedItem());
				}
		});
	}

	private void handleSearch() {
		searchAuthorTextField.setOnKeyTyped(event -> {
			String search = searchAuthorTextField.getText();
			Pattern pattern = Pattern.compile("[@()\\\\<>+~%\\*\\-\\'\"]");
			Matcher matcher = pattern.matcher(search);
			if (search.length() > 0 && !matcher.find()) {
				ObservableList<Author> searched = AuthorDAO.getInstance().searchForAuthors(search);
				authorsTableView.getItems().setAll(searched);
			} else {
				authorsData = AuthorDAO.getInstance().getAllAuthors();
				authorsTableView.getItems().setAll(authorsData);
			}
			clearFilters();

		});
	}

	private void setAuthor(Author author) {
		this.author = author;
		firstNameTextField.setText(author.getAuthorFirstName());
		lastNameTextField.setText(author.getAuthorLastName());
		emailTextField.setText(author.getAuthorEmail());
		countryComboBox.getSelectionModel().select(author.getCountryID() - 1);
		institutionComboBox.getSelectionModel().select(author.getInstitution());
		institutionNameTextField.setText(author.getInstitutionName());
		noteTextArea.setText(author.getNote());
	}

	private void showAuthor(Author author) {
		call = ConferenceCallDAO.getInstance().getCurrentAnswer(author.getAuthorID());
		email = author.getAuthorEmail();
		authorDetailsHbox.setVisible(true);
		firstNameLabel.setText(author.getAuthorFirstName() + " " + author.getAuthorLastName());
		emailLabel.setText(email);
		institutionLabel.setText(author.getInstitution() == null ? "" : author.getInstitution().name());
		institutionNameLabel.setText(author.getInstitutionName());
		countryLabel.setText(author.countryProperty() == null ? "" : author.countryProperty().get());
		noteTextAreaPreview.setText(author.getNoteProperty().get());
		showDetails();
	}

	private Author getNewAuthor() {
		if (isValidInput()) {
			String country = countryComboBox.getSelectionModel().getSelectedItem() != null
					? countryComboBox.getSelectionModel().getSelectedItem().getCountryName()
					: null;
			String institution = institutionComboBox.getSelectionModel().getSelectedItem() != null
					? institutionComboBox.getSelectionModel().getSelectedItem().name().toLowerCase()
					: null;
			author = AuthorDAO.getInstance().createAuthor(firstNameTextField.getText(), lastNameTextField.getText(),
					emailTextField.getText(), country, institution, institutionNameTextField.getText(),
					noteTextArea.getText());
			Logging.getInstance().change("create", "Create author:\n\t" + author.getAuthorEmail());
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

	private boolean isValidInput() {
		emailErrorLabel.setText(isValidEmail(emailTextField) ? "" : "Empty or invalid email field.");
		return isValidEmail(emailTextField);
	}

	private boolean isEmailAlreadyInDB() {
		return AuthorDAO.getInstance().findAuthorByExactEmail(emailTextField.getText()) != null;
	}

	private void setFirstName() {
		if (!firstNameTextField.getText().equalsIgnoreCase(author.getAuthorFirstName()))
			AuthorDAO.getInstance().updateAuthorFirstName(author, firstNameTextField.getText());
	}

	private void setLastName() {
		if (!lastNameTextField.getText().equalsIgnoreCase(author.getAuthorLastName()))
			AuthorDAO.getInstance().updateAuthorLastName(author, lastNameTextField.getText());
	}

	private void setEmail() {
		if (!emailTextField.getText().equalsIgnoreCase(author.getAuthorEmail()))
			AuthorDAO.getInstance().updateAuthorEmail(author, emailTextField.getText());
	}

	private void setInstitutionType() {
		if ((institutionComboBox.getSelectionModel().getSelectedItem() != null && author.getInstitution() == null)
				|| (institutionComboBox.getSelectionModel().getSelectedItem() != null && author.getInstitution() != null
						&& !institutionComboBox.getSelectionModel().getSelectedItem().name()
								.equalsIgnoreCase(author.getInstitution().name())))
			AuthorDAO.getInstance().updateAuthorInstitution(author,
					institutionComboBox.getSelectionModel().getSelectedItem().name().toLowerCase());
	}

	private void setInstitutionName() {
		if (institutionNameTextField.getText() == null)
			return;
		if (!institutionNameTextField.getText().equals(author.getInstitutionName()))
			AuthorDAO.getInstance().updateAuthorInstitutionName(author, institutionNameTextField.getText());
	}

	private void setCountry() {
		if (countryComboBox.getSelectionModel().getSelectedItem() != null
				&& countryComboBox.getSelectionModel().getSelectedItem().getCountryID() != author.getCountryID())
			AuthorDAO.getInstance().updateAuthorCountry(author,
					countryComboBox.getSelectionModel().getSelectedItem().getCountryName());
	}

	private void setNote() {
		if (noteTextArea.getText() == null)
			return;
		if (!noteTextArea.getText().equalsIgnoreCase(author.getNote()))
			AuthorDAO.getInstance().updateAuthorNote(author, noteTextArea.getText());
	}

	private void setUpBoxes() {
		institutionComboBox.getItems().setAll(FXCollections.observableArrayList(Institution.values()));
		institutionComboBox.getSelectionModel().select(null);
		countryComboBox.getItems()
				.setAll(FXCollections.observableArrayList(CountryDAO.getInstance().getAllCountries()));
		countryComboBox.getSelectionModel().select(-1);
		institutionComboBox.setPromptText("Please select");
		countryComboBox.setPromptText("Please select");
		setUpFilterBoxes();
	}

	private void setUpFields() {
		setUpFields(new TextField[] { firstNameTextField, lastNameTextField, emailTextField, institutionNameTextField },
				new int[] { getFirstNameLength(), getLastNameLength(), getEmailLength(), getInstitutionNameLength() });
		setUpFields(new TextArea[] { noteTextArea }, new int[] { getNoteLength() });
		noteTextArea.setWrapText(true);
		noteTextAreaPreview.setWrapText(true);
	}

	private void setUpDetails() {
		setUpBoxes();
		setUpFields();
		group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			try {
				if ((newValue == firstRadio && !call.isFirstCallAnswered()) || oldValue == firstRadio)
					ConferenceCallDAO.getInstance().updateFirstCall(call.getAuthorID(), newValue == firstRadio);
				if ((newValue == secondRadio && !call.isSecondCallAnswered()) || oldValue == secondRadio)
					ConferenceCallDAO.getInstance().updateSecondCall(call.getAuthorID(), newValue == secondRadio);
				if ((newValue == thirdRadio && !call.isThirdCallAnswered()) || oldValue == thirdRadio)
					ConferenceCallDAO.getInstance().updateThirdCall(call.getAuthorID(), newValue == thirdRadio);
			} catch (NullPointerException e) {
				group.getToggles().forEach(button -> button.setSelected(false));
			}
		});
		interestedCheckbox.setOnAction(event -> {
			try {
				ConferenceCallDAO.getInstance().updateInterested(call.getAuthorID(), interestedCheckbox.isSelected());
			} catch (NullPointerException e) {
				interestedCheckbox.setSelected(false);
			}
		});
		emailTextField.setOnKeyPressed(event -> {
			if (emailErrorLabel.getText() != null || !emailErrorLabel.getText().equals(""))
				emailErrorLabel.setText("");
		});
	}

	private void showRadioButton(JFXRadioButton button) {
		try {
			button.setSelected(button == firstRadio ? call.isFirstCallAnswered()
					: button == secondRadio ? call.isSecondCallAnswered() : call.isThirdCallAnswered());
		} catch (NullPointerException e) {
			button.setSelected(false);
		}
	}

	private void showCheckBox() {
		try {
			interestedCheckbox.setSelected(call.isInterested());
		} catch (NullPointerException e) {
			interestedCheckbox.setSelected(false);
		}
	}

	private void showDetails() {
		showRadioButton(firstRadio);
		showRadioButton(secondRadio);
		showRadioButton(thirdRadio);
		showCheckBox();
	}

	@FXML
	private void closeDetails(ActionEvent event) {
		closeDetails(authorDetailsHbox, 1200);
	}

	@FXML
	private void filter(ActionEvent event) {
		filterAuthors();
	}

	@FXML
	private void resetFilters() {
		clearFilters();
		searchAuthorTextField.clear();
		authorsTableView.getItems().setAll(AuthorDAO.getInstance().getAllAuthors());
	}

	private void clearFilters() {
		filterInstitution.getSelectionModel().clearSelection();
		filterInstitutionName.getSelectionModel().clearSelection();
		filterCountry.getSelectionModel().clearSelection();
		filterConference.getSelectionModel().clearSelection();
		filterField.getSelectionModel().clearSelection();
		filterReviewStatus.getSelectionModel().clearSelection();
		filterCollSpec.setSelected(false);
		filterSentForReview.setSelected(false);
		filterSubmittedWork.setSelected(false);
		filterFirstInv.setSelected(false);
		filterSecondInv.setSelected(false);
		filterThirdInv.setSelected(false);
		filterInterested.setSelected(false);
	}

	private void setUpFilterBoxes() {
		List<String> institutions = new ArrayList<String>();
		for (Institution institution : Institution.values()) {
			institutions.add(institution.toString());
		}
		filterInstitution.getItems().setAll(FXCollections.observableArrayList(institutions));
		List<String> institutionNames = new ArrayList<String>();
		for (Author author : AuthorDAO.getInstance().getAllAuthors()) {
			if (author.getInstitutionName() != null)
				institutionNames.add(author.getInstitutionName());
		}
		filterInstitutionName.getItems().setAll(FXCollections.observableArrayList(
				institutionNames.stream().distinct().filter(s -> !s.isEmpty()).sorted().collect(Collectors.toList())));
		filterCountry.getItems().setAll(CountryDAO.getInstance().getAllCountryNames());
		filterConference.getItems().setAll(ConferenceDAO.getInstance().getAllConferenceNames());
		filterField.getItems().setAll(FXCollections.observableArrayList(FieldDAO.getInstance().getAllFieldNames()));
		List<String> opinions = new ArrayList<String>();
		for (Opinion opinion : Opinion.values()) {
			opinions.add(opinion.getOpinion());
		}
		filterReviewStatus.getItems().setAll(FXCollections.observableArrayList(opinions));
	}

	private void filterAuthors() {
		ArrayList<ComboBox<String>> comboBoxes = new ArrayList<ComboBox<String>>();
		comboBoxes.add(filterInstitution);
		comboBoxes.add(filterInstitutionName);
		comboBoxes.add(filterCountry);
		comboBoxes.add(filterConference);
		comboBoxes.add(filterField);
		comboBoxes.add(filterReviewStatus);
		JFXCheckBox[] checkBoxes = { filterCollSpec, filterSentForReview, filterSubmittedWork, filterFirstInv,
				filterSecondInv, filterThirdInv, filterInterested };
		authorsData = AuthorDAO.getInstance().getAllAuthors();
		authorsTableView.getItems().setAll(authorsData);
		ResultSet filtered = FilterAuthorsDAO.filterAuthors(comboBoxes, checkBoxes);
		authorsData = AuthorDAO.getInstance().getFilteredAuthors(filtered);
		List<Author> newList = authorsData.stream().distinct().collect(Collectors.toList());
		authorsTableView.getItems().setAll(newList);
	}

	private boolean firstCallGroupHelper() {
		if (groupEmail.getValidFirst() != null && !groupEmail.getValidFirst().isEmpty()) {
			groupCall = MainView.getInstance().loadEmailWindow(groupCall, groupEmail.getValidFirst());
			if (groupCall.isSent())
				for (String email : groupEmail.getValidFirst())
					ConferenceCallDAO.getInstance().updateFirstCallSent(ConferenceCallDAO.getInstance()
							.getCurrentAnswer(AuthorDAO.getInstance().findAuthorByExactEmail(email).getAuthorID()),
							true);
			return true;
		} else
			return false;
	}

	private boolean firstCallInvalidHelper() {
		if (groupEmail.getInvalidFirst() != null && !groupEmail.getInvalidFirst().isEmpty()) {
			if (groupCall == null)
				groupCall = MainView.getInstance().loadEmailWindow(groupCall, groupEmail.getInvalidFirst());
			for (String email : groupEmail.getInvalidFirst())
				try {
					groupCall.getLoopia().sendEmail(
							ConferenceDAO.getInstance().getCurrentConference().getConferenceEmail(),
							ConferenceDAO.getInstance().getCurrentConference().getConferenceEmailPassword(), email,
							groupCall.getSubject().getText(), groupCall.getMessage().getText(), false,
							groupCall.getSelectedFiles());
					ConferenceCallDAO.getInstance().updateFirstCallSent(ConferenceCallDAO.getInstance()
							.getCurrentAnswer(AuthorDAO.getInstance().findAuthorByExactEmail(email).getAuthorID()),
							true);
				} catch (MessagingException | IOException e) {
					e.printStackTrace();
				}
			return true;
		} else
			return false;
	}

	private boolean secondCallGroupHelper() {
		if (groupEmail.getValidSecond() != null && !groupEmail.getValidSecond().isEmpty()) {
			groupCall = MainView.getInstance().loadEmailWindow(groupCall, groupEmail.getValidSecond());
			if (groupCall.isSent())
				for (String email : groupEmail.getValidSecond())
					ConferenceCallDAO.getInstance().updateSecondCallSent(ConferenceCallDAO.getInstance()
							.getCurrentAnswer(AuthorDAO.getInstance().findAuthorByExactEmail(email).getAuthorID()),
							true);
			return true;
		} else
			return false;
	}

	private boolean secondCallInvalidHelper() {
		if (groupEmail.getInvalidSecond() != null && !groupEmail.getInvalidSecond().isEmpty()) {
			if (groupCall == null)
				groupCall = MainView.getInstance().loadEmailWindow(groupCall, groupEmail.getInvalidSecond());
			for (String email : groupEmail.getInvalidSecond())
				try {
					groupCall.getLoopia().sendEmail(
							ConferenceDAO.getInstance().getCurrentConference().getConferenceEmail(),
							ConferenceDAO.getInstance().getCurrentConference().getConferenceEmailPassword(), email,
							groupCall.getSubject().getText(), groupCall.getMessage().getText(), false,
							groupCall.getSelectedFiles());
					ConferenceCallDAO.getInstance().updateSecondCallSent(ConferenceCallDAO.getInstance()
							.getCurrentAnswer(AuthorDAO.getInstance().findAuthorByExactEmail(email).getAuthorID()),
							true);
				} catch (MessagingException | IOException e) {
					e.printStackTrace();
				}
			return true;
		} else
			return false;
	}

	private boolean thirdCallGroupHelper() {
		if (groupEmail.getValidThird() != null && !groupEmail.getValidThird().isEmpty()) {
			groupCall = MainView.getInstance().loadEmailWindow(groupCall, groupEmail.getValidThird());
			if (groupCall.isSent())
				for (String email : groupEmail.getValidThird())
					ConferenceCallDAO.getInstance().updateThirdCallSent(ConferenceCallDAO.getInstance()
							.getCurrentAnswer(AuthorDAO.getInstance().findAuthorByExactEmail(email).getAuthorID()),
							true);
			return true;
		} else
			return false;
	}

	private boolean thirdCallInvalidHelper() {
		if (groupEmail.getInvalidThird() != null && !groupEmail.getInvalidThird().isEmpty()) {
			if (groupCall == null)
				groupCall = MainView.getInstance().loadEmailWindow(groupCall, groupEmail.getInvalidThird());
			for (String email : groupEmail.getInvalidThird())
				try {
					groupCall.getLoopia().sendEmail(
							ConferenceDAO.getInstance().getCurrentConference().getConferenceEmail(),
							ConferenceDAO.getInstance().getCurrentConference().getConferenceEmailPassword(), email,
							groupCall.getSubject().getText(), groupCall.getMessage().getText(), false,
							groupCall.getSelectedFiles());
					ConferenceCallDAO.getInstance().updateThirdCallSent(ConferenceCallDAO.getInstance()
							.getCurrentAnswer(AuthorDAO.getInstance().findAuthorByExactEmail(email).getAuthorID()),
							true);
				} catch (MessagingException | IOException e) {
					e.printStackTrace();
				}
			return true;
		} else
			return false;
	}

}
