package alfatec.controller.user;

import com.jfoenix.controls.JFXButton;

import alfatec.dao.user.LoginDataDAO;
import alfatec.model.user.LoginData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.Password;

public class ChangePasswordController {

	@FXML
	private Button closeButton;

	@FXML
	private PasswordField oldPasswordField;

	@FXML
	private Label oldPasswordErrorLabel;

	@FXML
	private PasswordField newPasswordField;

	@FXML
	private Label newPasswordErrorLabel;

	@FXML
	private PasswordField repeatPasswordField;

	@FXML
	private Label repeatPasswordErrorLabel;

	@FXML
	private JFXButton saveButton;

	private Stage display;
	private double x = 0;
	private double y = 0;
	private Node node;
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

	public void setDisplayStage(Stage stage) {
		this.display = stage;
	}

	@FXML
	void close(ActionEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		stage.close();
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
				if (newPass || oldPass)
					label.setText("Password doesn't match.");
				else
					label.setText("");
			} else
				label.setText("Please fill out this field.");
		});
	}

}