package alfatec.view.utils;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class GUIUtils {

	private double x = 0;
	private double y = 0;

	@FXML
	void pressed(MouseEvent event) {
		x = event.getSceneX();
		y = event.getSceneY();
	}

	@FXML
	void dragged(MouseEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		stage.setX(event.getScreenX() - x);
		stage.setY(event.getScreenY() - y);
	}

	public static void alert(String title, String header, String content, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.show();
	}
	
	public static ButtonType confAlert(String title, String header, String content, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
		return alert.getResult();
	}
	
	public void transitionPopupX(Node node, double endingCoordinate, double startingCoordinate,
			Interpolator interpolator, int durrationInMillis) {
		node.translateXProperty().set(endingCoordinate);
		KeyValue kv = new KeyValue(node.translateXProperty(), startingCoordinate, interpolator);
		KeyFrame kf = new KeyFrame(Duration.millis(durrationInMillis), kv);
		Timeline tl = new Timeline(kf);
		tl.play();
	}
}
