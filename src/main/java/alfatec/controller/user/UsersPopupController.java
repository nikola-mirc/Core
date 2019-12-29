package alfatec.controller.user;

import com.jfoenix.controls.JFXButton;
import alfatec.dao.user.LoginDataDAO;
import alfatec.dao.user.UserDAO;
import alfatec.model.enums.RoleEnum;
import alfatec.model.user.LoginData;
import alfatec.model.user.User;
import alfatec.view.wrappers.UserLoginConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class UsersPopupController {

	@FXML
	private Button closeButton;

	@FXML
	private TextField firstNameTextField;

	@FXML
	private Label firstNameErrorLabel;

	@FXML
	private TextField lastNameTextField;

	@FXML
	private Label lastNameErrorLabel;

	@FXML
	private TextField emailTextField;

	@FXML
	private Label emailErrorLabel;

	@FXML
	private TextField mobileTextField;

	@FXML
	private Label MobileErrorLabel;

	@FXML
	private ComboBox<RoleEnum> roleComboBox;

	@FXML
	private Label roleErrorLabel;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Label passwordErrorLabel;

	@FXML
	private PasswordField confirmPasswordField;

	@FXML
	private Label confirmPasswordErrorLabel;

	@FXML
	private JFXButton saveUserButton;

	private static final int FIRST_NAME_LENGTH = 30;
	private static final int LAST_NAME_LENGTH = 50;
	private static final int EMAIL_LENGTH = 50;
	private static final int MOBILE_LENGTH = 20;
	private static final int PASSWORD_LENGTH = 50;

	private UserLoginConnection userData;
	private boolean saveClicked = false;

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
	void saveUser(ActionEvent event) {
		if (isValidInput()) {
			saveClicked = true;
			display.close();
		} else
			saveUserButton.setDisable(saveClicked);

	}

	@FXML
	private void initialize() {
		saveUserButton.setDefaultButton(true);
		roleComboBox.setItems(FXCollections.observableArrayList(RoleEnum.values()));
		initializeListeners();
	}

	private void initializeListeners() {
		setListener(firstNameErrorLabel, firstNameTextField, FIRST_NAME_LENGTH);
		setListener(lastNameErrorLabel, lastNameTextField, LAST_NAME_LENGTH);
		setListener(emailErrorLabel, emailTextField, EMAIL_LENGTH);
		setListener(passwordErrorLabel, passwordField, PASSWORD_LENGTH);
		setListener(MobileErrorLabel, mobileTextField, MOBILE_LENGTH);

		roleComboBox.valueProperty().addListener(new ChangeListener<RoleEnum>() {
			@Override
			public void changed(ObservableValue<? extends RoleEnum> observable, RoleEnum oldValue, RoleEnum newValue) {
				roleComboBox.getSelectionModel().select(newValue);
			}
		});
	}

	public boolean isSaveClicked() {
		return saveClicked;
	}

	public void setDisplayStage(Stage stage) {
		this.display = stage;
	}

	private boolean isValidInput() {
		firstNameErrorLabel.setText(isValidFirstName() ? "" : "Empty first name field.");
		lastNameErrorLabel.setText(isValidLastName() ? "" : "Empty last name field.");
		emailErrorLabel.setText(isValidEmail() ? "" : "Empty or invalid email field.");
		roleErrorLabel.setText(isValidRole() ? "" : "Please select your role");
		passwordErrorLabel.setText(isValidPassword() ? "" : "Empty or invalid password field.");
		confirmPasswordErrorLabel.setText(isValidConfirmPassword() ? "" : "Empty or invalid confirm password field.");

		return isValidEmail() && isValidFirstName() && isValidLastName() && isValidRole() && isValidPassword()
				&& isValidConfirmPassword();
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

	private boolean isMailAlreadyInDB() {
		return LoginDataDAO.getInstance().findUserDataByExactEmail(emailTextField.getText()) != null;
	}

	private boolean isValidRole() {
		return roleComboBox.getSelectionModel().getSelectedItem() != null;
	}

	private boolean isValidPassword() {
		return passwordField.getText() != null && passwordField.getText().length() != 0;
	}

	private boolean isValidConfirmPassword() {
		return confirmPasswordField.getText() != null && confirmPasswordField.getText().equals(passwordField.getText());
	}

	public UserLoginConnection getNewUser() {
		if (isValidInput()) {
			User user = UserDAO.getInstance().createUser(firstNameTextField.getText(), lastNameTextField.getText(),
					mobileTextField.getText());
			LoginData ld = LoginDataDAO.getInstance().createLoginData(emailTextField.getText(), passwordField.getText(),
					user.getUserID(), roleComboBox.getSelectionModel().getSelectedItem().getRoleID());
			userData = new UserLoginConnection(user, ld);
		}
		return userData;
	}

	private void setListener(Label label, TextField text, int maxLenght) {
		text.setOnKeyTyped(event -> {
			saveUserButton.setDisable(saveClicked);
			if (event.getCharacter().equals(KeyCode.ESCAPE.getChar()))
				display.close();
			if (text.getText() != null) {
				if (text.getText().length() > maxLenght) {
					label.setText("Max allowed characters: " + maxLenght);
					text.deleteText(maxLenght, text.getLength());
				} else
					label.setText("");
				if (text == emailTextField && !isValidEmail())
					emailErrorLabel.setText("Invalid email address.");
				else if (isMailAlreadyInDB()) {
					emailErrorLabel.setText("Email already in use.");
					saveUserButton.setDisable(true);
				}
				if (text == confirmPasswordField && !isValidConfirmPassword())
					confirmPasswordErrorLabel.setText("Invalid confirm password field.");
			}
		});
	}

}