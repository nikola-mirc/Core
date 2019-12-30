package alfatec.view.gui;

import java.io.IOException;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;

import alfatec.Main;
import alfatec.controller.author.AuthorsPopupController;
import alfatec.controller.email.SendEmailController;
import alfatec.controller.user.ChangePasswordController;
import alfatec.model.person.Author;
import alfatec.model.user.LoginData;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainView {

	private static MainView instance;
	private Stage mainViewDisplay;
	private boolean logout;
	private double x;
	private double y;

	private MainView() {
	}

	public static MainView getInstance() {
		if (instance == null)
			synchronized (MainView.class) {
				if (instance == null)
					instance = new MainView();
			}
		return instance;
	}

	public void setMainDisplay(Stage display) {
		mainViewDisplay = display;
	}

	public void pressed(MouseEvent event) {
		x = event.getSceneX();
		y = event.getSceneY();
	}

	public void dragged(MouseEvent event) {
		Node node = (Node) event.getSource();
		mainViewDisplay = (Stage) node.getScene().getWindow();
		mainViewDisplay.setX(event.getScreenX() - x);
		mainViewDisplay.setY(event.getScreenY() - y);
	}

	public void loadTabs(JFXTabPane tabPane, LoginData ld) {
		Tab scientificWorkTab = new Tab("Scientific work");
		Tab conferenceTab = new Tab("Conference");
		Tab usersTab = new Tab("Users");
		Tab emailWebTab = new Tab("Email");
		AnchorPane scientificWorkAnchor;
		AnchorPane conferenceAnchor;
		AnchorPane usersAnchor;
		AnchorPane emailAnchor;
		try {
			usersAnchor = FXMLLoader.load(getClass().getClassLoader().getResource("resources/fxml/users_tab.fxml"));
			usersTab.setContent(usersAnchor);
			conferenceAnchor = FXMLLoader
					.load(getClass().getClassLoader().getResource("resources/fxml/conference_tab.fxml"));
			conferenceTab.setContent(conferenceAnchor);
			scientificWorkAnchor = FXMLLoader
					.load(getClass().getClassLoader().getResource("resources/fxml/scientific_work_tab.fxml"));
			scientificWorkTab.setContent(scientificWorkAnchor);
			emailAnchor = FXMLLoader.load(getClass().getClassLoader().getResource("resources/fxml/emailTab.fxml"));
			emailWebTab.setContent(emailAnchor);
			tabPane.getTabs().addAll(scientificWorkTab, conferenceTab, usersTab, emailWebTab);
		} catch (IOException e) {
			System.out.println("Error loading tabs.");
			e.printStackTrace();
		}
	}

	public AuthorsPopupController loadEdit(AuthorsPopupController controller, Author author) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/authors_popup.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			controller = fxmlLoader.getController();
			controller.setDisplayStage(stage);
			controller.setAuthor(author);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error editing author.");
			e.printStackTrace();
		}
		return controller;
	}

	public AuthorsPopupController loadAdd(AuthorsPopupController controller) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/authors_popup.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			controller = fxmlLoader.getController();
			controller.setAuthor(new Author());
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error adding author.");
			e.printStackTrace();
		}
		return controller;
	}

	public void closeMainView(ActionEvent event) {
		try {
			Node node = (Node) event.getSource();
			Stage stage = (Stage) node.getScene().getWindow();
			stage.close();
			Main.getInstance().getPrimaryStage().show();
			logout = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error logging out.");
		}
	}

	public ChangePasswordController loadChangePassword(ChangePasswordController controller, LoginData loginData) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/change_password.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initStyle(StageStyle.UNDECORATED);
			controller = fxmlLoader.getController();
			controller.setLogin(loginData);
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error changing password.");
			e.printStackTrace();
		}
		return controller;
	}

	public SendEmailController loadEmailWindow(SendEmailController controller, String email) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/sendEmailToAuthor.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			JFXTextField recieverid = (JFXTextField) root.lookup("#recieverid");
			recieverid.textProperty().set(email);
			controller = fxmlLoader.getController();
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error sending email.");
			e.printStackTrace();
		}
		return controller;
	}

	public boolean loggedOut() {
		return logout;
	}

}
