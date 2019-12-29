package alfatec.view.gui;

import alfatec.Main;
import alfatec.controller.main.MainInterfaceController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginView {

	private static LoginView instance;

	private LoginView() {
	}

	public static LoginView getInstance() {
		if (instance == null)
			synchronized (LoginView.class) {
				if (instance == null)
					instance = new LoginView();
			}
		return instance;
	}

	public MainInterfaceController loadMainView(MainInterfaceController controller) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/main_interface.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			controller = fxmlLoader.getController();
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			stage.show();
			Main.getInstance().getPrimaryStage().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return controller;
	}
}
