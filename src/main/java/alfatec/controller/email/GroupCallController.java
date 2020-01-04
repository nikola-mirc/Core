package alfatec.controller.email;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.utils.Logging;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.LoopiaEmail;

public class GroupCallController {

	@FXML
	private JFXTextField emailid, subject, bccid, selected;

	@FXML
	private JFXPasswordField password;

	@FXML
	private JFXTextArea message;

	private LoopiaEmail loopia;
	private List<File> selectedFiles;
	private List<String> attachFiles, recievers;
	private Stage display;
	private double x = 0;
	private double y = 0;
	private Node node;
	private boolean sent;

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
		selected.setEditable(false);
		selected.setVisible(false);
		attachFiles = new ArrayList<String>();
		recievers = new ArrayList<String>();
		try {
			loopia = new LoopiaEmail();
			loopia.setConferenceBCC(ConferenceDAO.getInstance().getCurrentConference().getConferenceBcc());
			emailid.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceEmail());
			password.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceEmailPassword());
			bccid.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceBcc());
			setListeners(new JFXTextField[] { emailid, subject, bccid }, password, message);
		} catch (Exception e) {
			alert(AlertType.ERROR, "No active conference",
					"You cann't send group call if there is no active conference.");
			display.close();
		}
	}

	@FXML
	private void close() {
		display.close();
	}

	@FXML
	private void handleSendButton() {
		String[] files = attachFiles.toArray(new String[attachFiles.size()]);
		loopia.setConferenceBCC(bccid.getText());
		try {
			loopia.sendEmail(emailid.getText(), password.getText(), collectEmails(), subject.getText(),
					message.getText(), true, files);
			Logging.getInstance().change("email", "SEND GROUP EMAIL TO " + bccid.getText());
			alert(AlertType.INFORMATION, "Message sent",
					"Message was sent to " + bccid.getText() + ".\nTotal authors selected: " + recievers.size());
			sent = true;
		} catch (MessagingException | IOException e) {
			alert(AlertType.ERROR, "Empty or invalid fields",
					"In order to send email, You must provide accurate credentials.\nMessage was not sent to "
							+ bccid.getText() + ".");
		}
		display.close();
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

	@FXML
	private void handleAttachButton() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files", "*.*");
		fileChooser.getExtensionFilters().add(extFilter);
		selectedFiles = fileChooser.showOpenMultipleDialog(display);
		if (selectedFiles != null && selectedFiles.size() > 0) {
			selected.setVisible(true);
			for (File file : selectedFiles) {
				selected.setText(selected.getText() + " " + file.getName() + ", ");
				attachFiles.add(file.getAbsolutePath());
			}
		}
	}

	private String collectEmails() {
		String s = "";
		for (String str : recievers)
			s += str + ",";
		if (s.length() > 0)
			return s.substring(0, s.length() - 1);
		return null;
	}

	public boolean isSent() {
		return sent;
	}

	public void setRecievers(List<String> list) {
		for (String s : list)
			recievers.add(s);
	}
}
