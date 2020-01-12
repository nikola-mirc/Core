package alfatec.view.utils;

import java.util.List;
import java.util.function.UnaryOperator;

import alfatec.controller.utils.ClearPopUp;
import alfatec.controller.utils.Utils;
import database.DatabaseLimits;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class GUIUtils extends DatabaseLimits {

	private double x = 0;
	private double y = 0;
	private boolean addAction, editAction, popupOpen;

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

	public boolean isAddAction() {
		return addAction;
	}

	public void setAddAction(boolean action) {
		this.addAction = action;
	}

	public boolean isEditAction() {
		return editAction;
	}

	public void setEditAction(boolean action) {
		this.editAction = action;
	}

	public boolean isPopupOpen() {
		return popupOpen;
	}

	public void setPopupOpen(boolean action) {
		this.popupOpen = action;
	}

	private Alert createAlert(String header, String content, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setHeaderText(header);
		alert.setContentText(content);
		return alert;
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

	public void closeDetails(Node box) {
		transitionPopupX(box, 0, 1200, Interpolator.EASE_IN, 500);
	}

	public void closePopup(VBox box, double startingCoordinate, ClearPopUp popup) {
		transitionPopupX(box, 0, startingCoordinate, Interpolator.EASE_IN, 500);
		setPopupOpen(false);
		popup.clear();
	}

	public void openPopup(VBox box, double endingCoordinate) {
		transitionPopupX(box, endingCoordinate, 0, Interpolator.EASE_IN, 500);
		box.setVisible(true);
		setPopupOpen(true);
	}

	private UnaryOperator<Change> rejectChange(final int length) {
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

	/**
	 * fields and lengths must be arrays of same length and in appropriate order
	 * 
	 * @param <T>     type of fields to format
	 * @param fields  array of fields to format
	 * @param lengths array of corresponding database limits
	 */
	public <T> void setUpFields(T[] fields, int[] lengths) {
		for (int i = 0; i < fields.length; i++)
			((TextInputControl) fields[i]).setTextFormatter(new TextFormatter<String>(rejectChange(lengths[i])));
	}

	/**
	 * @param field first or last name field
	 * @return true if field is not empty
	 */
	public boolean isValidName(TextField field) {
		return Utils.notEmpty(field.getText());
	}

	public boolean isValidEmail(TextField email) {
		return Utils.notEmpty(email.getText()) && email.getText().contains(".") && email.getText().contains("@");
	}

	public boolean isValidPassword(PasswordField password) {
		return Utils.notEmpty(password.getText());
	}

	public boolean isValidConfirmPassword(PasswordField password, PasswordField repeatedPassword) {
		return Utils.equal(password.getText(), repeatedPassword.getText());
	}

	public <T> void refresh(TableView<T> table, int row, VBox box, ClearPopUp popup) {
		table.refresh();
		table.requestFocus();
		table.getSelectionModel().select(row);
		table.scrollTo(row);
		closePopup(box, 520, popup);
	}

	public <T> void clearFields(List<T> fields, List<Label> labels) {
		fields.forEach(field -> ((TextInputControl) field).clear());
		labels.forEach(label -> label.setText(""));
	}
}
