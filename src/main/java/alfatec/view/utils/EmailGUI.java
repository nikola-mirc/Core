package alfatec.view.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import alfatec.dao.conference.ConferenceDAO;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.LoopiaEmail;

public abstract class EmailGUI extends GUIUtils {

	@FXML
	private JFXTextField emailid, subject, selected;

	@FXML
	private JFXPasswordField password;

	@FXML
	private HTMLEditor html;

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
		List<File> files = fileChooser.showOpenMultipleDialog(display);
		if (files != null)
			selectedFiles.addAll(files);
		if (selectedFiles != null && selectedFiles.size() > 0) {
			HashSet<File> set = new HashSet<File>(selectedFiles);
			set.addAll(selectedFiles);
			List<File> list = new ArrayList<File>();
			list.addAll(set);
			selected.setVisible(true);
			for (File file : list) {
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

	public void attachFile(File file) {
		selectedFiles.add(file);
		attachFiles.add(file.getAbsolutePath());
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

	public HTMLEditor getHTML() {
		return html;
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
		selectedFiles = new ArrayList<File>();
		loopia = new LoopiaEmail();
		emailid.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceEmail());
		password.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceEmailPassword());
		loopia.setConferenceBCC(ConferenceDAO.getInstance().getCurrentConference().getConferenceBcc());
		setListeners(new JFXTextField[] { emailid, subject }, password);
	}

	public void setListener(JFXTextField field) {
		field.setOnKeyTyped(event -> {
			if (event.getCharacter().equals(KeyCode.ESCAPE.getChar()))
				display.close();
		});
	}

	private void setListeners(JFXTextField[] textArray, JFXPasswordField pass) {
		for (JFXTextField text : textArray)
			setListener(text);
		pass.setOnKeyTyped(event -> {
			if (event.getCharacter().equals(KeyCode.ESCAPE.getChar()))
				display.close();
		});
	}

	public void setMessage(String htmlMessage) {
		this.html.setHtmlText(htmlMessage);
	}
}
