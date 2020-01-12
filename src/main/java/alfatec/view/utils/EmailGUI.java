package alfatec.view.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import alfatec.dao.conference.ConferenceDAO;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.LoopiaEmail;

public abstract class EmailGUI extends GUIUtils {

	@FXML
	private JFXTextField emailid, subject, selected;

	@FXML
	private JFXPasswordField password;

	@FXML
	private JFXTextArea message;

	private Stage display;
	private List<File> selectedFiles;
	private List<String> attachFiles;
	private LoopiaEmail loopia;
	private boolean sent;

	@FXML
	public abstract void initialize();

	@FXML
	public abstract void handleSendButton();

	@FXML
	public void handleAttachButton() {
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

	@FXML
	public void close() {
		display.close();
	}

	public void setDisplayStage(Stage stage) {
		this.display = stage;
	}

	public List<String> getAttachFiles() {
		return attachFiles;
	}

	public String[] getSelectedFiles() {
		return attachFiles.toArray(new String[attachFiles.size()]);
	}

	public LoopiaEmail getLoopia() {
		return loopia;
	}

	public JFXTextField getEmailid() {
		return emailid;
	}

	public JFXPasswordField getPassword() {
		return password;
	}

	public JFXTextField getSubject() {
		return subject;
	}

	public JFXTextArea getMessage() {
		return message;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public void setUp() {
		selected.setEditable(false);
		selected.setVisible(false);
		attachFiles = new ArrayList<String>();
		loopia = new LoopiaEmail();
		emailid.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceEmail());
		password.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceEmailPassword());
		loopia.setConferenceBCC(ConferenceDAO.getInstance().getCurrentConference().getConferenceBcc());
		setListeners(new JFXTextField[] { emailid, subject }, password, message);
	}

	public void setListener(JFXTextField field) {
		field.setOnKeyTyped(event -> {
			if (event.getCharacter().equals(KeyCode.ESCAPE.getChar()))
				display.close();
		});
	}

	private void setListeners(JFXTextField[] textArray, JFXPasswordField pass, JFXTextArea area) {
		for (JFXTextField text : textArray)
			setListener(text);
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