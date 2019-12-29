package alfatec.controller.email;

import alfatec.dao.conference.ConferenceDAO;
import alfatec.view.gui.MainView;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Controller for Email Tab. Will set up JavaFX default web browser WebView to
 * open Loopia web mail automatically with data for the current conference. If
 * there is no active conference, default Loopia web mail login page will be
 * loaded.
 * 
 * @author jelena
 */
public class EmailTabController {

	@FXML
	private WebView webView;

	private static final String PATH = "https://webbmail.loopia.se/";
	private static final String SUBMIT = "$('#rcmloginsubmit')[0].click()";
	private static String email, password, setEmail, setPassword;
	private boolean login = true;

	@FXML
	private void initialize() {
		WebEngine webEngine = webView.getEngine();
		webEngine.setJavaScriptEnabled(true);
		setUpConference();
		webEngine.getLoadWorker().stateProperty()
				.addListener((ChangeListener<State>) (observable, oldState, newState) -> {
					try {
						if (newState == Worker.State.SUCCEEDED && login) {
							webEngine.executeScript(setEmail);
							webEngine.executeScript(setPassword);
							webEngine.executeScript(SUBMIT);
							login = MainView.getInstance().loggedOut();
						}
					} catch (Exception e) {
						login = false;
					}
				});
		webEngine.load(PATH);
	}

	private void setUpConference() {
		try {
			email = ConferenceDAO.getInstance().getCurrentConference().getConferenceEmail();
			password = ConferenceDAO.getInstance().getCurrentConference().getConferenceEmailPassword();
			setEmail = "document.getElementsByName('_user')[0].value='" + email + "';";
			setPassword = "document.getElementsByName('_pass')[0].value='" + password + "';";
		} catch (Exception e) {
			login = false;
		}
	}

}
