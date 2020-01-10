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
import alfatec.view.utils.GUIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.LoopiaEmail;

public class GroupCallController extends GUIUtils {

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
	private boolean sent;

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
			alert("No active conference", "You cann't send group call if there is no active conference.",
					AlertType.ERROR);
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
			alert("Message sent",
					"Message was sent to " + bccid.getText() + ".\nTotal authors selected: " + recievers.size(),
					AlertType.INFORMATION);
			sent = true;
		} catch (MessagingException | IOException e) {
			alert("Empty or invalid fields",
					"In order to send email, You must provide accurate credentials.\nMessage was not sent to "
							+ bccid.getText() + ".",
					AlertType.ERROR);
		}
		display.close();
	}

	public void setDisplayStage(Stage stage) {
		this.display = stage;
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
		StringBuilder builder = new StringBuilder();
		recievers.forEach(s -> builder.append(s).append(","));
		System.out.println(builder.toString());
		if (builder.length() > 0)
			return builder.substring(0, builder.length() - 1);
		return null;
	}

	public boolean isSent() {
		return sent;
	}

	public void setRecievers(List<String> list) {
		recievers.addAll(list);
	}
}
