package alfatec.view.utils;

import java.util.function.UnaryOperator;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class GUIUtils {

	private static final int FIRST_NAME_LENGTH = 30;
	private static final int LAST_NAME_LENGTH = 50;
	private static final int EMAIL_LENGTH = 50;
	private static final int INSTITUTION_NAME_LENGTH = 100;
	private static final int NOTE_LENGTH = 255;
	private static final int PASSWORD_LENGTH = 64;
	private static final int CONTACT_TELEPHONE_LENGTH = 20;
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

	public void alert(String header, String content, AlertType alertType) {
		Alert alert = createAlert(header, content, alertType);
		alert.show();
	}

	public ButtonType confirmationAlert(String header, String content, AlertType alertType) {
		Alert alert = createAlert(header, content, alertType);
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

	private Alert createAlert(String header, String content, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setHeaderText(header);
		alert.setContentText(content);
		return alert;
	}

	public static int getFirstNameLength() {
		return FIRST_NAME_LENGTH;
	}

	public static int getLastNameLength() {
		return LAST_NAME_LENGTH;
	}

	public static int getEmailLength() {
		return EMAIL_LENGTH;
	}

	public static int getInstitutionNameLength() {
		return INSTITUTION_NAME_LENGTH;
	}

	public static int getNoteLength() {
		return NOTE_LENGTH;
	}

	public static int getPasswordLength() {
		return PASSWORD_LENGTH;
	}

	public static int getContactTelephoneLength() {
		return CONTACT_TELEPHONE_LENGTH;
	}

	public UnaryOperator<Change> rejectChange(final int length) {
		UnaryOperator<Change> rejectChange = change -> {
			if (change.isContentChange())
				if (change.getControlNewText().length() > length) {
					final ContextMenu menu = new ContextMenu();
					menu.getItems().add(new MenuItem("This field takes only " + length + " characters."));
					menu.show(change.getControl(), Side.BOTTOM, 0, 0);
					return null;
				}
			return change;
		};
		return rejectChange;
	}
}
