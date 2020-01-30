package alfatec.controller.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import com.jfoenix.controls.JFXTextField;

import alfatec.controller.utils.Utils;
import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.utils.Logging;
import alfatec.view.utils.EmailGUI;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;

public class GroupCallController extends EmailGUI {

	@FXML
	private JFXTextField bccid;

	private List<String> recievers;

	@FXML
	public void initialize() {
		recievers = new ArrayList<String>();
		setSent(false);
		instructions();
		try {
			setUp();
			bccid.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceBcc());
			setListener(bccid);
		} catch (Exception e) {
			alert("No active conference", "You cann't send group call if there is no active conference.",
					AlertType.ERROR);
			close();
		}
	}

	@FXML
	public void handleSendButton() {
		getLoopia().setConferenceBCC(bccid.getText());
		try {
			getLoopia().sendEmail(getEmailid().getText(), getPassword().getText(), Utils.mergeList(recievers),
					getSubject().getText(), getMessage().getText(), true, getSelectedFiles());
			Logging.getInstance().change("email", "Send group e-mail to\n\t" + bccid.getText());
			alert("Message sent", "Message was sent to " + bccid.getText() + ".", AlertType.INFORMATION);
			setSent(true);
		} catch (MessagingException | IOException e) {
			alert("Empty or invalid fields",
					"In order to send email, You must provide accurate credentials.\nMessage was not sent to "
							+ bccid.getText() + ".",
					AlertType.ERROR);
		}
		close();
	}

	public void setRecievers(List<String> list) {
		recievers.addAll(list);
	}
}
