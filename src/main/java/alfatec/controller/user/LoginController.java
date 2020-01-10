package alfatec.controller.user;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import alfatec.controller.main.MainInterfaceController;
import alfatec.dao.user.LoginDataDAO;
import alfatec.dao.utils.Logging;
import alfatec.model.user.LoginData;
import alfatec.view.gui.LoginView;
import alfatec.view.utils.GUIUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import util.Password;

public class LoginController extends GUIUtils implements Initializable {

	@FXML
	private Button quitButton;

	@FXML
	private Button loginButton;

	@FXML
	private JFXTextField usernameTextField;

	@FXML
	private JFXPasswordField passwordField;

	private LoginData loginData;
	private MainInterfaceController controller;

	@FXML
	void login(ActionEvent event) {
		if (isValid()) {
			controller = LoginView.getInstance().loadMainView(controller);
			controller.setWelcomeMessage(loginData);
			controller.setLoginData(loginData);
			controller.loadTabs(loginData);
			controller.disableOptionsForUsers(loginData);
			Logging.getInstance().setUser(loginData);
		} else {
			alert("Invalid credentials", "Wrong username or password. Please try again.", AlertType.INFORMATION);
			passwordField.clear();
		}
	}

	@FXML
	void quit() {
		Platform.exit();
	}

	private boolean isValid() {
		loginData = LoginDataDAO.getInstance().findUserDataByExactEmail(usernameTextField.getText());
		return loginData != null && Password.checkPassword(passwordField.getText(), loginData.getPasswordHash());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		usernameTextField.setOnMousePressed(event -> usernameTextField.clear());
	}
}