package alfatec.controller.user;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import alfatec.controller.main.MainInterfaceController;
import alfatec.dao.user.LoginDataDAO;
import alfatec.model.user.LoginData;
import alfatec.view.gui.LoginView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.Password;

public class LoginController {

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

	private double x = 0;
	private double y = 0;
	private Node node;
	private Stage stage;

	@FXML
	void pressed(MouseEvent event) {
		x = event.getSceneX();
		y = event.getSceneY();
	}

	@FXML
	void dragged(MouseEvent event) {
		node = (Node) event.getSource();
		stage = (Stage) node.getScene().getWindow();
		stage.setX(event.getScreenX() - x);
		stage.setY(event.getScreenY() - y);
	}

	@FXML
	private void initialize() {
		usernameTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB
						|| event.getCode() == KeyCode.DOWN) {
					passwordField.requestFocus();
					event.consume();
				}
			}
		});
		passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.UP) {
					usernameTextField.requestFocus();
					event.consume();
				}
				if (event.getCode() == KeyCode.ENTER) {
					loginButton.fire();
					event.consume();
				}
			}
		});
	}

	@FXML
	void login(ActionEvent event) {
		if (isValid()) {
			controller = LoginView.getInstance().loadMainView(controller);
			controller.setWelcomeMessage(loginData);
			controller.setLoginData(loginData);
			controller.loadTabs(loginData);
			controller.disableSendEmailForUsers(loginData);
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.initStyle(StageStyle.UNDECORATED);
			alert.setTitle("Access denied");
			alert.setContentText("The username or password you entered is incorrect.");
			alert.show();
			usernameTextField.setText("");
			passwordField.setText("");
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

}