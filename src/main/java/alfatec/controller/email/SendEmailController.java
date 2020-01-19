package alfatec.controller.email;

import java.io.IOException;

import javax.mail.MessagingException;

import com.jfoenix.controls.JFXTextField;

import alfatec.dao.utils.Logging;
import alfatec.view.utils.EmailGUI;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;

public class SendEmailController extends EmailGUI {

	@FXML
	private JFXTextField recieverid;

	@FXML
	public void initialize() {
		try {
			setUp();
			setListener(recieverid);
			setSent(false);
		} catch (Exception e) {
			alert("No active conference", "Send email to selected author via loopia server with your own credentials.",
					AlertType.ERROR);
		}
	}

	@FXML
	public void handleSendButton() {
		try {
			getLoopia().sendEmail(getEmailid().getText(), getPassword().getText(), recieverid.getText(),
					getSubject().getText(), getMessage().getText(), false, getSelectedFiles());
			alert("Message sent", "Message was sent to " + recieverid.getText() + ".", AlertType.INFORMATION);
			Logging.getInstance().change("email", "SEND EMAIL TO " + recieverid.getText());
			setSent(true);
		} catch (MessagingException | IOException e) {
			alert("Empty or invalid fields",
					"In order to send email, You must provide accurate credentials.\nMessage was not sent to "
							+ recieverid.getText() + ".",
					AlertType.ERROR);
		}
		close();
	}

	public void setReciever(JFXTextField recieverEmail) {
		this.recieverid = recieverEmail;
	}
}
