package alfatec.controller.user;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jfoenix.controls.JFXButton;

import alfatec.controller.utils.ClearPopUp;
import alfatec.controller.utils.Utils;
import alfatec.dao.user.LoginDataDAO;
import alfatec.dao.user.UserAuditDAO;
import alfatec.dao.user.UserDAO;
import alfatec.dao.utils.Logging;
import alfatec.dao.wrappers.UserLoginDAO;
import alfatec.model.enums.RoleEnum;
import alfatec.model.user.LoginData;
import alfatec.model.user.User;
import alfatec.model.user.UserAudit;
import alfatec.view.utils.GUIUtils;
import alfatec.view.utils.Utility;
import alfatec.view.wrappers.UserLoginConnection;
import javafx.animation.Interpolator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import util.DateUtil;

public class UsersTabController extends GUIUtils implements Initializable {

	@FXML
	private TableView<UserLoginConnection> usersTableView;

	@FXML
	private TableColumn<UserLoginConnection, String> userColumn;

	@FXML
	private LineChart<String, Number> activityChart;

	@FXML
	private VBox mainVbox, popupVbox;

	@FXML
	private Button closeButton, clearButton;

	@FXML
	private JFXButton addUserButton, editUserButton, deleteButton, saveUserButton;

	@FXML
	private Label usernameLabel, roleLabel, emailLabel, contactLabel, dateCreatedLabel, totalAddedLabel;

	@FXML
	private TextField searchUserTextField, firstNameTextField, lastNameTextField, emailTextField, contactTextField;

	@FXML
	private Label firstNameErrorLabel, lastNameErrorLabel, emailErrorLabel, contactErrorLabel, roleErrorLabel,
			passwordErrorLabel, confirmPasswordErrorLabel;

	@FXML
	private ComboBox<RoleEnum> roleComboBox;

	@FXML
	private PasswordField passwordField, confirmPasswordField;

	private UserLoginConnection userData;
	private ObservableList<UserLoginConnection> users;
	private ObservableList<UserAudit> audit;
	private String role, email, password;
	private ClearPopUp popup;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		popup = () -> {
			clearFields(Arrays.asList(firstNameTextField, lastNameTextField, emailTextField, contactTextField),
					Arrays.asList(firstNameErrorLabel, lastNameErrorLabel, emailErrorLabel));
			clearFields(Arrays.asList(passwordField, confirmPasswordField),
					Arrays.asList(passwordErrorLabel, confirmPasswordErrorLabel));
			roleComboBox.setValue(RoleEnum.USER);
		};
		users = UserLoginDAO.getInstance().getAllData();
		populateUsersTable();
		handleSearch();
		setUpFields();
	}

	@FXML
	private void addUser(ActionEvent event) {
		if (isPopupOpen())
			closePopup(popupVbox, 340, popup);
		setAddAction(true);
		openPopup(popupVbox, 940);
	}

	@FXML
	private void editUser(ActionEvent event) {
		if (isPopupOpen())
			closePopup(popupVbox, 340, popup);
		setEditAction(true);
		userData = usersTableView.getSelectionModel().getSelectedItem();
		if (userData != null) {
			setUser(userData);
			openPopup(popupVbox, 340);
		}
	}

	@FXML
	private void deleteUser(ActionEvent event) {
		userData = usersTableView.getSelectionModel().getSelectedItem();
		if (userData != null) {
			ButtonType button = confirmationAlert(
					"Confirm action:", "Are you sure you want to delete credentials for user " + userData.getUser()
							.getUserFirstName().concat(" ").concat(userData.getUser().getUserLastName()) + "?",
					AlertType.CONFIRMATION);
			if (button == ButtonType.OK) {
				Logging.getInstance().change("Delete", "Delete user: " + userData.toString());
				UserDAO.getInstance().deleteUser(userData.getUser());
				UserDAO.getInstance().getAllUsers().remove(userData.getUser());
				users.remove(userData);
				usersTableView.getItems().remove(userData);
				refresh(usersTableView, usersTableView.getSelectionModel().getSelectedIndex() - 1, popupVbox, popup);
				closeDetails(mainVbox);
			}
		}
	}

	@FXML
	private void saveUser(ActionEvent event) {
		if (isAddAction()) {
			setAddAction(false);
			if (isValidInput() && !isEmailAlreadyInDB()) {
				users.add(getNewUser());
				refresh(usersTableView, users.size() - 1, popupVbox, popup);
				closeDetails(mainVbox);
				transitionPopupX(mainVbox, 1200, 0, Interpolator.EASE_IN, 500);
				showUser(userData);
			} else if (isEmailAlreadyInDB())
				emailErrorLabel.setText("E-mail already exists in database.");
		} else if (isEditAction()) {
			setEditAction(false);
			userData = usersTableView.getSelectionModel().getSelectedItem();
			if (isValidInput() && isEmailAlreadyInDB())
				if (emailTextField.getText().equals(email)) {
					if (passwordField.getText().equals(password))
						handleEditUserWithoutPWchange();
					else
						handleEditUser();
					refresh(usersTableView, usersTableView.getSelectionModel().getSelectedIndex(), popupVbox, popup);
					showUser(userData);
				} else
					emailErrorLabel.setText("Already exists.");
			else if (!isEmailAlreadyInDB()) {
				if (passwordField.getText().equals(password))
					handleEditUserWithoutPWchange();
				else
					handleEditUser();
				refresh(usersTableView, usersTableView.getSelectionModel().getSelectedIndex(), popupVbox, popup);
				showUser(userData);
			}
		}
	}

	@FXML
	private void clearPopup(ActionEvent event) {
		popup.clear();
	}

	@FXML
	private void closePopup(ActionEvent event) {
		closePopup(popupVbox, 340, popup);
	}

	private void populateUsersTable() {
		usersTableView.setPlaceholder(new Label("Database table \"users\" is empty"));
		Utility.setUpStringCell(usersTableView);
		userColumn.setCellValueFactory(cellData -> cellData.getValue().getUser().getUserFirstNameProperty().concat(" ")
				.concat(cellData.getValue().getUser().getUserLastNameProperty()));
		usersTableView.setItems(users);
		usersTableView.setOnMousePressed(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				if (usersTableView.getSelectionModel().getSelectedItem() != null) {
					if (isPopupOpen())
						closePopup(popupVbox, 340, popup);
					transitionPopupX(mainVbox, 1200, 0, Interpolator.EASE_IN, 500);
					showUser(usersTableView.getSelectionModel().getSelectedItem());
				}
		});
	}

	private void handleSearch() {
		FilteredList<UserLoginConnection> filteredData = new FilteredList<>(users, p -> true);
		searchUserTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(UserLoginConnection -> {
				if (newValue == null || newValue.isEmpty())
					return true;
				String lowerCaseFilter = newValue.toLowerCase();
				if (Utils
						.cyrillicToLatin(String.valueOf(UserLoginConnection.getUser().getUserFirstName()).toLowerCase())
						.startsWith(lowerCaseFilter))
					return true;
				else if (Utils
						.cyrillicToLatin(String.valueOf(UserLoginConnection.getUser().getUserLastName()).toLowerCase())
						.startsWith(lowerCaseFilter))
					return true;
				else if (String.valueOf(UserLoginConnection.getLoginData().getUserEmail()).toLowerCase()
						.startsWith(lowerCaseFilter))
					return true;
				return false;
			});
		});
		SortedList<UserLoginConnection> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(usersTableView.comparatorProperty());
		usersTableView.setItems(sortedData);
	}
	
	private void setUser(UserLoginConnection userData) {
		this.userData = userData;
		firstNameTextField.setText(userData.getUser().getUserFirstName());
		lastNameTextField.setText(userData.getUser().getUserLastName());
		emailTextField.setText(userData.getLoginData().getUserEmail());
		contactTextField.setText(userData.getUser().getContactTelephone());
		passwordField.setText(userData.getLoginData().getPasswordHash());
		confirmPasswordField.setText(userData.getLoginData().getPasswordHash());
		roleComboBox.setItems(FXCollections.observableArrayList(RoleEnum.values()));
		roleComboBox.getSelectionModel().select(userData.getLoginData().getRoleID() - 1);
	}
	
	private void showUser(UserLoginConnection userData) {
		email = userData.getLoginData().getUserEmail();
		password = userData.getLoginData().getPasswordHash();
		role = userData.getLoginData().getRoleName();
		transitionPopupX(mainVbox, 1200, 0, Interpolator.EASE_IN, 500);
		mainVbox.setVisible(true);
		usernameLabel.setText(userData.getUser().getUserFirstName() + " " + userData.getUser().getUserLastName());
		roleLabel.setText(role);
		emailLabel.setText(email);
		contactLabel.setText(userData.getUser().getContactTelephone());
		dateCreatedLabel.setText(DateUtil.format(userData.getUser().getCreatedTimeProperty().get()));
		audit = UserAuditDAO.getInstance().getAllFor(userData.getLoginData().getLoginID());
		createChart();
	}
	
	private UserLoginConnection getNewUser() {
		if (isValidInput()) {
			User user = UserDAO.getInstance().createUser(firstNameTextField.getText(), lastNameTextField.getText(),
					contactTextField.getText());
			LoginData ld = LoginDataDAO.getInstance().createLoginData(emailTextField.getText(), passwordField.getText(),
					user.getUserID(), roleComboBox.getSelectionModel().getSelectedItem().getRoleID());
			userData = new UserLoginConnection(user, ld);
			Logging.getInstance().change("Create", "Add user: " + userData.toString());
		}
		return userData;
	}

	private void handleEditUser() {
		setPassword();
		handleEditUserWithoutPWchange();
	}

	private void handleEditUserWithoutPWchange() {
		setFirstName();
		setLastName();
		setContact();
		setEmail();
		setRoleType();
		usersTableView.refresh();
	}

	private boolean isValidInput() {
		firstNameErrorLabel.setText(isValidName(firstNameTextField) ? "" : "Empty first name field.");
		lastNameErrorLabel.setText(isValidName(lastNameTextField) ? "" : "Empty last name field.");
		roleErrorLabel.setText(isValidRole() ? "" : "Please select your role");
		emailErrorLabel.setText(isValidEmail(emailTextField) ? "" : "Empty or invalid email field.");
		passwordErrorLabel
				.setText(isValidPassword(passwordField) && !isEditAction() ? "" : "Empty or invalid password field.");
		confirmPasswordErrorLabel
				.setText(isEditAction() && isValidConfirmPassword(passwordField, confirmPasswordField) ? ""
						: "Empty or invalid confirm password field.");
		return isValidName(firstNameTextField) && isValidName(lastNameTextField) && isValidEmail(emailTextField)
				&& isValidRole() && isValidPassword(passwordField)
				&& isValidConfirmPassword(passwordField, confirmPasswordField);
	}

	private boolean isEmailAlreadyInDB() {
		return LoginDataDAO.getInstance().findUserDataByExactEmail(emailTextField.getText()) != null;
	}

	private boolean isValidRole() {
		return roleComboBox.getSelectionModel().getSelectedItem() != null;
	}

	private void setFirstName() {
		if (!firstNameTextField.getText().equalsIgnoreCase(userData.getUser().getUserFirstName()))
			UserDAO.getInstance().updateUserFirstName(userData.getUser(), firstNameTextField.getText());
	}

	private void setLastName() {
		if (!lastNameTextField.getText().equalsIgnoreCase(userData.getUser().getUserLastName()))
			UserDAO.getInstance().updateUserLastName(userData.getUser(), lastNameTextField.getText());
	}

	private void setEmail() {
		if (!emailTextField.getText().equalsIgnoreCase(userData.getLoginData().getUserEmail()))
			LoginDataDAO.getInstance().updateEmail(userData.getLoginData(), emailTextField.getText());
	}

	private void setContact() {
		if (!contactTextField.getText().equalsIgnoreCase(userData.getUser().getContactTelephone()))
			UserDAO.getInstance().updateUserTelephone(userData.getUser(), contactTextField.getText());
	}

	private void setRoleType() {
		if (!roleComboBox.getSelectionModel().getSelectedItem().name()
				.equalsIgnoreCase(userData.getLoginData().getRoleName()))
			LoginDataDAO.getInstance().updateRole(userData.getLoginData(),
					roleComboBox.getSelectionModel().getSelectedItem().name().toUpperCase());
	}

	private void setPassword() {
		LoginDataDAO.getInstance().updatePassword(userData.getLoginData(), passwordField.getText());
		password = userData.getLoginData().getPasswordHash();
	}

	private void createChart() {
		@SuppressWarnings("unchecked")
		ObservableList<XYChart.Series<String, Number>> lineChartData = FXCollections.observableArrayList(
				createGraph("Create"), createGraph("Update"), createGraph("Delete"), createGraph("Email"));
		activityChart.getXAxis().setAnimated(false);
		activityChart.getData().setAll(lineChartData);
	}

	private int count(String eventType, LocalDate date) {
		int count = 0;
		for (UserAudit data : audit)
			if (data.getEventType().equalsIgnoreCase(eventType) && data.getTimestamp().toLocalDate().equals(date))
				count++;
			else if (eventType.equalsIgnoreCase("update") && data.getTimestamp().toLocalDate().equals(date)) {
				if (data.getEventType().equalsIgnoreCase("add"))
					count++;
			} else if (eventType.equalsIgnoreCase("delete") && data.getTimestamp().toLocalDate().equals(date))
				if (data.getEventType().equalsIgnoreCase("remove"))
					count++;
		return count;
	}

	private LineChart.Series<String, Number> createGraph(String eventType) {
		ObservableList<Data<String, Number>> list = FXCollections.observableArrayList();
		List<LocalDate> dates = Stream.iterate(LocalDate.now().minusDays(30), date -> date.plusDays(1))
				.limit(ChronoUnit.DAYS.between(LocalDate.now().minusDays(30), LocalDate.now().plusDays(1)))
				.collect(Collectors.toList());
		for (LocalDate data : dates)
			list.add(new XYChart.Data<String, Number>(data.toString(), count(eventType, data)));
		return new LineChart.Series<String, Number>(eventType, list);
	}

	private void setUpFields() {
		setUpFields(new TextField[] { firstNameTextField, lastNameTextField, emailTextField, contactTextField },
				new int[] { getFirstNameLength(), getLastNameLength(), getEmailLength(), getContactTelephoneLength() });
		setUpFields(new PasswordField[] { passwordField }, new int[] { getPasswordLength() });
		roleComboBox.setItems(FXCollections.observableArrayList(RoleEnum.values()));
		mainVbox.setVisible(false);
		popupVbox.setVisible(false);
	}
}
