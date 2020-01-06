package alfatec.controller.author;

import com.jfoenix.controls.JFXButton;

import alfatec.dao.country.CountryDAO;
import alfatec.dao.person.AuthorDAO;
import alfatec.dao.utils.Logging;
import alfatec.model.country.Country;
import alfatec.model.enums.Institution;
import alfatec.model.person.Author;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AuthorsPopupController {

	@FXML
	private TextField firstNameTextField;

	@FXML
	private Label firstNameErrorLabel;

	@FXML
	private TextField lastNameTextField;

	@FXML
	private Label lastNameErrorLabel;

	@FXML
	private ComboBox<Institution> institutionComboBox;

	@FXML
	private TextField institutionNameTextField;

	@FXML
	private Label institutionNameErrorLabel;

	@FXML
	private TextField emailTextField;

	@FXML
	private Label emailErrorLabel;

	@FXML
	private ComboBox<Country> countryComboBox;

	@FXML
	private TextArea noteTextArea;

	@FXML
	private JFXButton saveAuthorButton;

	private static final int FIRST_NAME_LENGTH = 30;
	private static final int LAST_NAME_LENGTH = 50;
	private static final int EMAIL_LENGTH = 50;
	private static final int INSTITUTION_NAME_LENGTH = 100;
	private static final int NOTE_LENGTH = 255;
	private Stage display;
	private Author author;
	private boolean saveClicked;

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
	void close(ActionEvent event) {
		display.close();
	}

	@FXML
	void saveAuthor() {
		if (isValidInput()) {
			saveClicked = true;
			display.close();
		} else
			saveAuthorButton.setDisable(saveClicked);
	}

	@FXML
	private void initialize() {
		countryComboBox.setItems(CountryDAO.getInstance().getAllCountries());
		countryComboBox.setValue(CountryDAO.getInstance().getCountry(195));
		institutionComboBox.setItems(FXCollections.observableArrayList(Institution.values()));
		institutionComboBox.setValue(Institution.UNIVERSITY);
		initializeListeners();
	}

	private void initializeListeners() {
		setListener(firstNameErrorLabel, firstNameTextField, FIRST_NAME_LENGTH);
		setListener(lastNameErrorLabel, lastNameTextField, LAST_NAME_LENGTH);
		setListener(emailErrorLabel, emailTextField, EMAIL_LENGTH);
		setListener(institutionNameErrorLabel, institutionNameTextField, INSTITUTION_NAME_LENGTH);
		noteTextArea.setOnKeyTyped(event -> {
			if (event.getCharacter().equals(KeyCode.TAB.getChar())
					|| event.getCharacter().equals(KeyCode.ENTER.getChar()))
				return;
			String string = noteTextArea.getText();
			if (string.length() > NOTE_LENGTH)
				noteTextArea.setText(noteTextArea.getText().substring(0, NOTE_LENGTH));
		});
		countryComboBox.valueProperty().addListener(new ChangeListener<Country>() {
			@Override
			public void changed(ObservableValue<? extends Country> observable, Country oldValue, Country newValue) {
				countryComboBox.getSelectionModel().select(newValue);
			}
		});
		institutionComboBox.valueProperty().addListener(new ChangeListener<Institution>() {
			@Override
			public void changed(ObservableValue<? extends Institution> observable, Institution oldValue,
					Institution newValue) {
				institutionComboBox.getSelectionModel().select(newValue);
			}
		});
	}

	public boolean isSaveClicked() {
		return saveClicked;
	}

	public void setDisplayStage(Stage stage) {
		this.display = stage;
	}

	public void setAuthor(Author author) {
		this.author = author;
		firstNameTextField.setText(author.getAuthorFirstName());
		lastNameTextField.setText(author.getAuthorLastName());
		emailTextField.setText(author.getAuthorEmail());
		countryComboBox.setItems(CountryDAO.getInstance().getAllCountries());
		countryComboBox.getSelectionModel().select(author.getCountryID() - 1);
		institutionComboBox.setItems(FXCollections.observableArrayList(Institution.values()));
		institutionComboBox.getSelectionModel().select(author.getInstitution());
		institutionNameTextField.setText(author.getInstitutionName());
		noteTextArea.setText(author.getNote());
	}

	private boolean isValidInput() {
		firstNameErrorLabel.setText(isValidFirstName() ? "" : "Empty first name field.");
		lastNameErrorLabel.setText(isValidLastName() ? "" : "Empty last name field.");
		emailErrorLabel.setText(isValidEmail() ? "" : "Empty or invalid email field.");
		return isValidEmail() && isValidFirstName() && isValidLastName();
	}

	private boolean isValidFirstName() {
		return firstNameTextField.getText() != null && firstNameTextField.getText().length() != 0;
	}

	private boolean isValidLastName() {
		return lastNameTextField.getText() != null && lastNameTextField.getText().length() != 0;
	}

	private boolean isValidEmail() {
		return emailTextField.getText() != null && emailTextField.getText().length() != 0
				&& emailTextField.getText().contains(".") && emailTextField.getText().contains("@");
	}

	private boolean isMailAlreadyInDB() {
		return AuthorDAO.getInstance().findAuthorByExactEmail(emailTextField.getText()) != null;
	}

	public void setFirstName() {
		if (!firstNameTextField.getText().equalsIgnoreCase(author.getAuthorFirstName()))
			AuthorDAO.getInstance().updateAuthorFirstName(author, firstNameTextField.getText());
	}

	public void setLastName() {
		if (!lastNameTextField.getText().equalsIgnoreCase(author.getAuthorLastName()))
			AuthorDAO.getInstance().updateAuthorLastName(author, lastNameTextField.getText());
	}

	public void setEmail() {
		if (!emailTextField.getText().equalsIgnoreCase(author.getAuthorEmail()))
			AuthorDAO.getInstance().updateAuthorEmail(author, emailTextField.getText());
	}

	public void setInstitutionType() {
		if (!institutionComboBox.getSelectionModel().getSelectedItem().name()
				.equalsIgnoreCase(author.getInstitution().name())) {
			AuthorDAO.getInstance().updateAuthorInstitution(author,
					institutionComboBox.getSelectionModel().getSelectedItem().name().toLowerCase());
		}
	}

	public String setInstitutionName() {
		if (institutionNameTextField.getText() == null) {
			return "";
		}
		if (!institutionNameTextField.getText().equalsIgnoreCase(author.getInstitutionName()))
			AuthorDAO.getInstance().updateAuthorInstitutionName(author, institutionNameTextField.getText());
		return institutionNameTextField.getText();
	}

	public void setCountry() {
		if (countryComboBox.getSelectionModel().getSelectedItem().getCountryID() != author.getCountryID())
			AuthorDAO.getInstance().updateAuthorCountry(author,
					countryComboBox.getSelectionModel().getSelectedItem().getCountryName());
	}

	public String setNote() {
		if (noteTextArea.getText() == null)
			noteTextArea.setText("");
		if (!noteTextArea.getText().equalsIgnoreCase(author.getNote()))
			AuthorDAO.getInstance().updateAuthorNote(author, noteTextArea.getText());
		return noteTextArea.getText();
	}

	public Author getNewAuthor() {
		if (isValidInput()) {
			author = AuthorDAO.getInstance().createAuthor(firstNameTextField.getText(), lastNameTextField.getText(),
					emailTextField.getText(), countryComboBox.getSelectionModel().getSelectedItem().getCountryName(),
					institutionComboBox.getSelectionModel().getSelectedItem().name().toLowerCase(),
					institutionNameTextField.getText(), noteTextArea.getText());
			Logging.getInstance().change("Create", "Create author: " + author.getAuthorEmail());
		}
		return author;
	}

	private void setListener(Label label, TextField text, int maxLenght) {
		text.setOnKeyTyped(event -> {
			saveAuthorButton.setDisable(saveClicked);
			if (event.getCharacter().equals(KeyCode.ESCAPE.getChar()))
				display.close();
			if (text.getText() != null) {
				if (text.getText().length() > maxLenght) {
					label.setText("Max allowed characters: " + maxLenght);
					text.setText(text.getText().substring(0, maxLenght));
				} else
					label.setText("");
				if (text == emailTextField && !isValidEmail())
					emailErrorLabel.setText("Invalid email address.");
				else if (text == emailTextField && isMailAlreadyInDB()) {
					emailErrorLabel.setText("Email already in use.");
					saveAuthorButton.setDisable(true);
				}
			}
		});
	}
}
