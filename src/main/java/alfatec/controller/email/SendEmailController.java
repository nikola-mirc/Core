package alfatec.controller.email;

import javax.mail.MessagingException;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import alfatec.dao.conference.ConferenceDAO;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.LoopiaEmail;

public class SendEmailController {

	@FXML
	private JFXTextField emailid, subject, recieverid;

	@FXML
	private JFXPasswordField password;

	@FXML
	private JFXTextArea message;

	private LoopiaEmail loopia;
	private Stage display;
	private double x = 0;
	private double y = 0;
	private Node node;

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

	@FXML
	private void initialize() {
		try {
			loopia = new LoopiaEmail();
			emailid.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceEmail());
			password.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceEmailPassword());
			setListeners(new JFXTextField[] { emailid, subject, recieverid }, password, message);
		} catch (Exception e) {
			alert(AlertType.ERROR, "No active conference",
					"Send email to selected author via loopia server with your own credentials.");
		}
	}

	@FXML
	private void close() {
		display.close();
	}

	@FXML
	private void handleSendButton() {
		try {
			loopia.sendEmail(emailid.getText(), password.getText(), recieverid.getText(), subject.getText(),
					message.getText(), false);
			alert(AlertType.INFORMATION, "Message sent", "Message was sent to " + recieverid.getText() + ".");
		} catch (MessagingException e) {
			alert(AlertType.ERROR, "Empty or invalid fields",
					"In order to send email, You must provide accurate credentials.\nMessage was not sent to "
							+ recieverid.getText() + ".");
		}
		display.close();
	}

	public void setReciever(String email) {
		recieverid.textProperty().set(email);
	}

	public void setDisplayStage(Stage stage) {
		this.display = stage;
	}

	private void alert(AlertType type, String headerText, String contentText) {
		Alert alert = new Alert(type);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	private void setListeners(JFXTextField[] textArray, JFXPasswordField pass, JFXTextArea area) {
		for (JFXTextField text : textArray)
			text.setOnKeyTyped(event -> {
				if (event.getCharacter().equals(KeyCode.ESCAPE.getChar()))
					display.close();
			});
		pass.setOnKeyTyped(event -> {
			if (event.getCharacter().equals(KeyCode.ESCAPE.getChar()))
				display.close();
		});
		area.setOnKeyTyped(event -> {
			if (event.getCharacter().equals(KeyCode.ESCAPE.getChar()))
				display.close();
		});
	}
}
