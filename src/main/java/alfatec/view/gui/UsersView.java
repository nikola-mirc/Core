package alfatec.view.gui;

import alfatec.controller.user.UsersPopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UsersView {

	private static UsersView instance;

	private UsersView() {
	}

	public static UsersView getInstance() {
		if (instance == null)
			synchronized (UsersView.class) {
				if (instance == null)
					instance = new UsersView();
			}
		return instance;
	}

	public UsersPopupController loadEdit(UsersPopupController controller) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/users_popup.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initStyle(StageStyle.UNDECORATED);
			controller = fxmlLoader.getController();
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error editing user.");
			e.printStackTrace();
		}
		return controller;
	}

	public UsersPopupController loadAdd(UsersPopupController controller) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/users_popup.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initStyle(StageStyle.UNDECORATED);
			controller = fxmlLoader.getController();
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error saving user.");
			e.printStackTrace();
		}
		return controller;
	}

}
