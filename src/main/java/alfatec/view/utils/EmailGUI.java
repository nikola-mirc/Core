package alfatec.view.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import alfatec.dao.conference.ConferenceDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
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

	@FXML
	private MenuButton menu;

	@FXML
	private MenuItem item;

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
		selectedFiles.addAll(fileChooser.showOpenMultipleDialog(display));
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

	public void instructions() {
		ImageView html_code = new ImageView(
				new Image(getClass().getResourceAsStream("/resources/images/html_code.png")));
		ImageView html_preview = new ImageView(
				new Image(getClass().getResourceAsStream("/resources/images/html_preview.png")));
		item.setGraphic(createPopupContent(html_code, html_preview));
		menu.getItems().setAll(item);
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
		selectedFiles = new ArrayList<File>();
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

	private VBox createPopupContent(final ImageView imageView1, final ImageView imageView2) {
		final Label label1 = createLabel(imageView1.getImage().getWidth(),
				"The email client uses HTML markups to format content of an email. For example, code");
		final Label label2 = createLabel(imageView1.getImage().getWidth(), "will produce");
		final Label label3 = createLabel(imageView1.getImage().getWidth(),
				"So, use tag <i> for italic, <b> for bold and <br> for breaking a line (simple enter will not get a job done).");
		final VBox box = new VBox(5);
		box.setAlignment(Pos.CENTER);
		box.getChildren().setAll(label1, imageView1, label2, imageView2, label3);
		return box;
	}

	private final Label createLabel(double width, String text) {
		final Label htmlMarkups = new Label();
		htmlMarkups.setWrapText(true);
		htmlMarkups.setTextAlignment(TextAlignment.JUSTIFY);
		htmlMarkups.setMaxWidth(width);
		htmlMarkups.setText(text);
		return htmlMarkups;
	}
}
