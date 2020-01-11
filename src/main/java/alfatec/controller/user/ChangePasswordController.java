package alfatec.controller.user;

import com.jfoenix.controls.JFXButton;

import alfatec.dao.user.LoginDataDAO;
import alfatec.model.user.LoginData;
import alfatec.view.utils.GUIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import util.Password;

public class ChangePasswordController extends GUIUtils {

	@FXML
	private Button closeButton;

	@FXML
	private PasswordField oldPasswordField, newPasswordField, repeatPasswordField;

	@FXML
	private Label oldPasswordErrorLabel, newPasswordErrorLabel, repeatPasswordErrorLabel;

	@FXML
	private JFXButton saveButton;

	private Stage display;
	private LoginData loginData;

	public void setLogin(LoginData loginData) {
		this.loginData = loginData;
	}

	@FXML
	private void initialize() {
		setListener(newPasswordField, newPasswordErrorLabel);
		setListener(oldPasswordField, oldPasswordErrorLabel);
		setListener(repeatPasswordField, repeatPasswordErrorLabel);
	}

	public void setDisplayStage(Stage stage) {
		this.display = stage;
	}

	@FXML
	void close(ActionEvent event) {
		display.close();
	}

	@FXML
	void save(ActionEvent event) {
		if (matchNewPassword() && matchOldPassword()) {
			LoginDataDAO.getInstance().updatePassword(loginData, newPasswordField.getText());
			display.close();
		}
	}

	private boolean matchOldPassword() {
		return Password.checkPassword(oldPasswordField.getText(), loginData.getPasswordHash());
	}

	private boolean matchNewPassword() {
		return newPasswordField.getText().equals(repeatPasswordField.getText());
	}

	private void setListener(PasswordField field, Label label) {
		field.setOnKeyTyped(event -> {
			if (field.getText() != null && field.getText().length() != 0) {
				boolean newPass = field == repeatPasswordField && !matchNewPassword();
				boolean oldPass = field == oldPasswordField && !matchOldPassword();
				label.setText(newPass || oldPass ? "Password doesn't match." : "");
			} else
				label.setText("Please fill out this field.");
		});
	}

}