package alfatec.view.gui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;

import alfatec.Main;
import alfatec.controller.conference.ConferenceTabController;
import alfatec.controller.email.GroupCallController;
import alfatec.controller.email.SendEmailController;
import alfatec.controller.user.ChangePasswordController;
import alfatec.model.user.LoginData;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainView {

	private static MainView instance;
	private boolean logout;

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

	public void loadTabs(JFXTabPane tabPane, LoginData loginData) {
		Tab scientificWorkTab = new Tab("Scientific work"), conferenceTab = new Tab("Conference"),
				confManagementTab = new Tab("Conf. management"), usersTab = new Tab("Users"),
				emailWebTab = new Tab("Email");
		try {
			setUp(usersTab, "resources/fxml/users_tab.fxml");
			AnchorPane conferenceAnchor = setUp(conferenceTab, "resources/fxml/conference_tab.fxml");
			setUp(confManagementTab, "resources/fxml/conference_management_tab.fxml");
			setUp(scientificWorkTab, "resources/fxml/scientific_work_tab.fxml");
			setUp(emailWebTab, "resources/fxml/emailTab.fxml");
			ConferenceTabController conferenceController = (ConferenceTabController) getController(conferenceAnchor);
			conferenceController.disablePartsForAdminAccess(loginData);
			conferenceTab.setOnSelectionChanged(event -> {
				if (conferenceTab.isSelected()) {
					conferenceController.refreshCollectionTab();
					conferenceController.refreshSpecialTab();
					conferenceController.refreshSelection();
				}
			});
			if (loginData.getRoleID() == 3)
				tabPane.getTabs().addAll(scientificWorkTab, conferenceTab, confManagementTab, usersTab, emailWebTab);
			else
				tabPane.getTabs().addAll(scientificWorkTab, conferenceTab, emailWebTab);
		} catch (IOException e) {
			System.out.println("Error loading tabs.");
			e.printStackTrace();
		}
	}

	private AnchorPane setUp(Tab tab, String resource) throws IOException {
		AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource(resource));
		tab.setContent(pane);
		return pane;
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
			stage.initModality(Modality.APPLICATION_MODAL);
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

	public Object getController(Node node) {
		Object controller = null;
		do {
			controller = node.getProperties().get("foo");
			node = node.getParent();
		} while (controller == null && node != null);
		return controller;
	}

	public GroupCallController loadEmailWindow(GroupCallController controller, List<String> list) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/Group_call.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			controller = fxmlLoader.getController();
			controller.setRecievers(list);
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error sending group email.");
			e.printStackTrace();
		}
		return controller;
	}

	public SendEmailController loadEmailWindow(SendEmailController controller, String email, File file) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/sendEmailToAuthor.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			JFXTextField recieverid = (JFXTextField) root.lookup("#recieverid");
			recieverid.textProperty().set(email);
			JFXTextField selected = (JFXTextField) root.lookup("#selected");
			selected.textProperty().set(file.getName());
			selected.setVisible(true);
			controller = fxmlLoader.getController();
			controller.attachFile(file);
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error sending email.");
			e.printStackTrace();
		}
		return controller;
	}

	public GroupCallController loadEmailWindow(GroupCallController controller, List<String> list, String message) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/Group_call.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			controller = fxmlLoader.getController();
			controller.setRecievers(list);
			controller.setMessage(message);
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error sending group email.");
			e.printStackTrace();
		}
		return controller;
	}
}
