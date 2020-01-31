package alfatec.controller.conference;

import java.util.Arrays;

import org.controlsfx.control.PrefixSelectionComboBox;

import com.jfoenix.controls.JFXButton;

import alfatec.controller.utils.ClearPopUp;
import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.conference.FieldDAO;
import alfatec.dao.conference.RegistrationFeeDAO;
import alfatec.dao.country.CountryDAO;
import alfatec.dao.person.ReviewerDAO;
import alfatec.dao.utils.Logging;
import alfatec.model.conference.Field;
import alfatec.model.conference.RegistrationFee;
import alfatec.model.country.Country;
import alfatec.model.enums.Currency;
import alfatec.model.person.Reviewer;
import alfatec.view.utils.GUIUtils;
import alfatec.view.utils.Utility;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ConferenceManagementTabController extends GUIUtils {

	@FXML
	private TableView<Reviewer> reviewersTableView;

	@FXML
	private TableColumn<Reviewer, String> revNameCol, revEmailCol, revInstitutionCol, revContactCol, revCountryCol;

	@FXML
	private JFXButton addRevButton, editRevButton, deleteRevButton, addFieldButton, editFieldButton, deleteFieldButton,
			addFeeButton, editFeeButton, deleteFeeButton, saveRevButton, saveFieldButton, saveFeeButton;

	@FXML
	private TableView<Field> fieldsTableView;

	@FXML
	private TableColumn<Field, String> fieldCol;

	@FXML
	private TableView<RegistrationFee> feesTableView;

	@FXML
	private TableColumn<RegistrationFee, String> presetCol, currencyCol, amountCol, conferenceCol;

	@FXML
	private VBox revieversPopup, feePopup, fieldsPopup;

	@FXML
	private Button closeRevPopupButton, clearRevPupupButton, closeFeePopupButton, clearFeePopupButton,
			closeFieldPopupButton, clearFieldPopupButton;

	@FXML
	private TextField revNameTextField, revLastNameTextField, revEmailTextField, revContactTextField,
			feePresetTextField, feeAmountTextField, fieldTextField, revInstitutionTextField;

	@FXML
	private Label firstNameErrorLabel, lastNameErrorLabel, emailErrorLabel, feeNameErrorLabel, amountErrorLabel,
			fieldNameErrorLabel;

	@FXML
	private ComboBox<Currency> feeCurrencyComboBox;

	@FXML
	private PrefixSelectionComboBox<Country> revCountryComboBox;

	@FXML
	private TextArea noteTextArea;

	private ClearPopUp reviewerCleaner, registrationFeeCleaner, fieldCleaner;
	private ObservableList<Reviewer> reviewers;
	private ObservableList<RegistrationFee> fees;
	private ObservableList<Field> fields;
	private Reviewer reviewer;
	private RegistrationFee registrationFee;
	private Field field;
	private boolean openReviewer, openField, openFee;
	private String email;

	@FXML
	private void initialize() {
		setUpCleaners();
		generalSetUp();
		reviewers = ReviewerDAO.getInstance().getAllReviewers();
		populateReviewersTable();
		fees = RegistrationFeeDAO.getInstance().getAll();
		populateRegistrationFeeTable();
		fields = FieldDAO.getInstance().getAllFields();
		populateFieldsTable();
	}

	@FXML
	void addFee(ActionEvent event) {
		closeAll();
		registrationFeeCleaner.clear();
		setAddAction(true);
		saveFeeButton.setDisable(false);
		openYPopup(feePopup, -350);
		openFee = true;
	}

	@FXML
	void addField(ActionEvent event) {
		closeAll();
		fieldCleaner.clear();
		setAddAction(true);
		saveFieldButton.setDisable(false);
		openYPopup(fieldsPopup, -200);
		openField = true;
	}

	@FXML
	void addReviewer(ActionEvent event) {
		closeAll();
		reviewerCleaner.clear();
		setAddAction(true);
		saveRevButton.setDisable(false);
		openDetails(revieversPopup, 340);
		openReviewer = true;
	}

	@FXML
	void clearFeePopup(ActionEvent event) {
		registrationFeeCleaner.clear();
	}

	@FXML
	void clearFieldPopup(ActionEvent event) {
		fieldCleaner.clear();
	}

	@FXML
	void clearRevPopup(ActionEvent event) {
		reviewerCleaner.clear();
	}

	@FXML
	void closeFeePopup(ActionEvent event) {
		closeYPopup(feePopup, -350);
		registrationFeeCleaner.clear();
		openFee = false;
	}

	@FXML
	void closeFieldPopup(ActionEvent event) {
		closeYPopup(fieldsPopup, -200);
		fieldCleaner.clear();
		openField = false;
	}

	@FXML
	void closeRevPopup(ActionEvent event) {
		closeDetails(revieversPopup, 340);
		reviewerCleaner.clear();
		openReviewer = false;
	}

	@FXML
	void deleteFee(ActionEvent event) {
		registrationFee = feesTableView.getSelectionModel().getSelectedItem();
		if (registrationFee != null) {
			ButtonType bt = confirmationAlert("Please confirm:",
					"Are you sure you want to delete data for " + registrationFee.getRegistrationName() + "?",
					AlertType.CONFIRMATION);
			if (bt == ButtonType.OK) {
				RegistrationFeeDAO.getInstance().deleteRegistration(registrationFee);
				RegistrationFeeDAO.getInstance().getAll().remove(registrationFee);
				fees.remove(registrationFee);
				feesTableView.getItems().remove(registrationFee);
				refreshY(feesTableView, feesTableView.getSelectionModel().getSelectedIndex() - 1);
				closeFeePopup(event);
			}
		}
	}

	@FXML
	void deleteReviewer(ActionEvent event) {
		reviewer = reviewersTableView.getSelectionModel().getSelectedItem();
		if (reviewer != null) {
			ButtonType bt = confirmationAlert("Please confirm:", "Are you sure you want to delete data for "
					+ reviewer.getReviewerFirstName() + " " + reviewer.getReviewerLastName() + "?",
					AlertType.CONFIRMATION);
			if (bt == ButtonType.OK) {
				ReviewerDAO.getInstance().deleteReviewer(reviewer);
				ReviewerDAO.getInstance().getAllReviewers().remove(reviewer);
				reviewers.remove(reviewer);
				reviewersTableView.getItems().remove(reviewer);
				refresh(reviewersTableView, reviewersTableView.getSelectionModel().getSelectedIndex() - 1,
						revieversPopup, 340, reviewerCleaner);
				closeRevPopup(event);
			}
		}
	}

	@FXML
	void editFee(ActionEvent event) {
		closeAll();
		setEditAction(true);
		saveFeeButton.setDisable(false);
		openYPopup(feePopup, -350);
		openFee = true;
	}

	@FXML
	void editField(ActionEvent event) {
		closeAll();
		saveFeeButton.setDisable(false);
		openYPopup(feePopup, -200);
		openField = true;
	}

	@FXML
	void editReviewer(ActionEvent event) {
		closeAll();
		setEditAction(true);
		saveRevButton.setDisable(false);
		openDetails(revieversPopup, 340);
		openReviewer = true;
	}

	@FXML
	void saveField(ActionEvent event) {
		// TODO
		if (isAddAction()) {
			setAddAction(false);
		} else if (isEditAction()) {
			setEditAction(false);
		}
	}

	@FXML
	void saveReviewer(ActionEvent event) {
		if (isAddAction()) {
			if (isValidReviewer() && !isEmailAlreadyInDB()) {
				reviewers.add(handleAddReviewer());
				refresh(reviewersTableView, reviewers.size() - 1, revieversPopup, 320, reviewerCleaner);
				reviewer = reviewersTableView.getSelectionModel().getSelectedItem();
				openPopup(revieversPopup, 340);
				openReviewer = true;
				showReviewer(reviewer);
			} else if (isEmailAlreadyInDB()) {
				emailErrorLabel.setText("E-mail already exists in database.");
				return;
			}
			setAddAction(false);
		} else if (isEditAction()) {
			reviewer = reviewersTableView.getSelectionModel().getSelectedItem();
			if (isValidReviewer())
				if (!revEmailTextField.getText().equals(email) && isEmailAlreadyInDB()) {
					emailErrorLabel.setText("Database already has enter with the same e-mail address.");
					return;
				} else {
					handleEditReviewer();
					refresh(reviewersTableView, reviewersTableView.getSelectionModel().getSelectedIndex(),
							revieversPopup, 340, reviewerCleaner);
					openPopup(revieversPopup, 340);
					openReviewer = true;
					reviewer = reviewersTableView.getSelectionModel().getSelectedItem();
					showReviewer(reviewer);
				}
			setEditAction(false);
		}
	}

	@FXML
	private void deleteField(ActionEvent event) {
		field = fieldsTableView.getSelectionModel().getSelectedItem();
		if (field != null) {
			ButtonType bt = confirmationAlert("Please confirm:",
					"Are you sure you want to delete data for " + field.getFieldName() + "?", AlertType.CONFIRMATION);
			if (bt == ButtonType.OK) {
				FieldDAO.getInstance().deleteField(field);
				FieldDAO.getInstance().getAllFields().remove(field);
				fields.remove(field);
				fieldsTableView.getItems().remove(field);
				refreshY(fieldsTableView, fieldsTableView.getSelectionModel().getSelectedIndex() - 1);
				closeFieldPopup(event);
			}
		}
	}

	@FXML
	private void saveFee() {
		// TODO
		if (isAddAction()) {
			setAddAction(false);
		} else if (isEditAction()) {
			setEditAction(false);
		}
	}

	private void setUpCleaners() {
		reviewerCleaner = () -> {
			clearFields(
					Arrays.asList(revNameTextField, revLastNameTextField, revEmailTextField, revContactTextField,
							revInstitutionTextField),
					Arrays.asList(firstNameErrorLabel, lastNameErrorLabel, emailErrorLabel));
			revCountryComboBox.getSelectionModel().select(null);
			noteTextArea.clear();
			reviewer = null;
		};
		registrationFeeCleaner = () -> {
			clearFields(Arrays.asList(feePresetTextField, feeAmountTextField),
					Arrays.asList(feeNameErrorLabel, amountErrorLabel));
			feeCurrencyComboBox.getSelectionModel().select(null);
			registrationFee = null;
		};
		fieldCleaner = () -> {
			fieldTextField.clear();
			fieldNameErrorLabel.setText("");
			field = null;
		};
	}

	private void generalSetUp() {
		setUpFields(
				new TextField[] { revNameTextField, revLastNameTextField, revEmailTextField, revContactTextField,
						feePresetTextField, fieldTextField, revInstitutionTextField },
				new int[] { getFirstNameLength(), getLastNameLength(), getEmailLength(), getContactTelephoneLength(),
						getRegistrationFeeNameLength(), getFieldNameLength(), getInstitutionNameLength() });
		setUpDecimal(feeAmountTextField);
		setUpFields(new TextArea[] { noteTextArea }, new int[] { getNoteLength() });
		Utility.setUpStringCell(reviewersTableView);
		Utility.setUpStringCell(fieldsTableView);
		Utility.setUpStringCell(feesTableView);
		noteTextArea.setWrapText(true);
		setUpBoxes();
		revEmailTextField.setOnKeyPressed(event -> {
			if (emailErrorLabel.getText() != null || !emailErrorLabel.getText().equals(""))
				emailErrorLabel.setText("");
		});
	}

	private void setUpBoxes() {
		feeCurrencyComboBox.getItems().setAll(FXCollections.observableArrayList(Currency.values()));
		feeCurrencyComboBox.valueProperty().addListener(
				(observable, oldValue, newValue) -> feeCurrencyComboBox.getSelectionModel().select(newValue));
		feeCurrencyComboBox.setValue(Currency.RSD);
		revCountryComboBox.getItems().setAll(CountryDAO.getInstance().getAllCountries());
		revCountryComboBox.valueProperty().addListener(
				(observable, oldValue, newValue) -> revCountryComboBox.getSelectionModel().select(newValue));
	}

	private void populateReviewersTable() {
		reviewersTableView.setPlaceholder(new Label("Database table \"reviewer\" is empty"));
		revNameCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().toString()));
		revEmailCol.setCellValueFactory(cellData -> cellData.getValue().getReviewerEmailProperty());
		revInstitutionCol.setCellValueFactory(cellData -> cellData.getValue().getInstitutionNameProperty());
		revContactCol.setCellValueFactory(cellData -> cellData.getValue().getContactTelephoneProperty());
		revCountryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
		reviewersTableView.setItems(reviewers);
		reviewersTableView.setOnMousePressed(event -> {
			if (reviewersTableView.getSelectionModel().getSelectedItem() != null) {
				closeAll();
				openPopup(revieversPopup, 320);
				openReviewer = true;
				reviewer = reviewersTableView.getSelectionModel().getSelectedItem();
				showReviewer(reviewer);
			}
		});
	}

	private void populateRegistrationFeeTable() {
		feesTableView.setPlaceholder(new Label("Database table \" registration fee\" is empty"));
		presetCol.setCellValueFactory(cellData -> cellData.getValue().getRegistrationNameProperty());
		amountCol.setCellValueFactory(cellData -> cellData.getValue().getRegistrationPriceProperty().asString());
		currencyCol.setCellValueFactory(cellData -> cellData.getValue().getCurrencyProperty().asString());
		conferenceCol.setCellValueFactory(
				cellData -> ConferenceDAO.getInstance().findBy(cellData.getValue().getConferenceID()) == null ? null
						: ConferenceDAO.getInstance().findBy(cellData.getValue().getConferenceID())
								.getConferenceTitleProperty());
		feesTableView.setItems(fees);
		feesTableView.setOnMousePressed(event -> {
			if (feesTableView.getSelectionModel().getSelectedItem() != null) {
				feePopup.setVisible(true);
				closeAll();
				openYPopup(feePopup, -350);
				openFee = true;
				registrationFee = feesTableView.getSelectionModel().getSelectedItem();
				showRegistrationFee(registrationFee);

			}
		});
	}

	private void populateFieldsTable() {
		fieldsTableView.setPlaceholder(new Label("Database table \"field\" is empty"));
		fieldCol.setCellValueFactory(cellData -> cellData.getValue().getFieldNameProperty());
		fieldsTableView.setItems(fields);
		fieldsTableView.setOnMousePressed(event -> {
			if (fieldsTableView.getSelectionModel().getSelectedItem() != null) {
				fieldsPopup.setVisible(true);
				closeAll();
				openYPopup(fieldsPopup, -200);
				openField = true;
				field = fieldsTableView.getSelectionModel().getSelectedItem();
				showField(field);
			}
		});
	}

	private void showReviewer(Reviewer reviewer) {
		this.reviewer = reviewer;
		saveRevButton.setDisable(true);
		revNameTextField.setText(reviewer.getReviewerFirstName());
		revLastNameTextField.setText(reviewer.getReviewerLastName());
		revEmailTextField.setText(reviewer.getReviewerEmail());
		email = reviewer.getReviewerEmail();
		revInstitutionTextField.setText(reviewer.getInstitutionName());
		revContactTextField.setText(reviewer.getContactTelephone() == null ? "" : reviewer.getContactTelephone());
		revCountryComboBox.getSelectionModel().select(reviewer.getCountryID() - 1);
		noteTextArea.setText(reviewer.getNote());
	}

	private void showField(Field field) {
		saveFieldButton.setDisable(true);
		fieldTextField.setText(field.getFieldName());
	}

	private void showRegistrationFee(RegistrationFee fee) {
		saveFeeButton.setDisable(true);
		feePresetTextField.setText(fee.getRegistrationName());
		feeCurrencyComboBox.getSelectionModel().select(fee.getCurrency());
		feeAmountTextField.setText(fee.getRegistrationPrice().toString());
	}

	private void closeAll() {
		if (openReviewer) {
			closeDetails(revieversPopup, 340);
			openReviewer = false;
		}
		if (openFee) {
			closeYPopup(feePopup, -350);
			openFee = false;
		}
		if (openField) {
			closeYPopup(fieldsPopup, -200);
			openField = false;
		}
		setAddAction(false);
		setEditAction(false);
	}

	private boolean isValidReviewer() {
		firstNameErrorLabel.setText(isValidName(revNameTextField) ? "" : "Empty first name field.");
		lastNameErrorLabel.setText(isValidName(revLastNameTextField) ? "" : "Empty last name field.");
		emailErrorLabel.setText(isValidEmail(revEmailTextField) ? "" : "Empty or invalid email field.");
		return isValidName(revNameTextField) && isValidName(revLastNameTextField) && isValidEmail(revEmailTextField);
	}

	private boolean isEmailAlreadyInDB() {
		return ReviewerDAO.getInstance().findReviewerByExactEmail(revEmailTextField.getText()) != null;
	}

	private Reviewer handleAddReviewer() {
		String country = revCountryComboBox.getSelectionModel().getSelectedItem() != null
				? revCountryComboBox.getSelectionModel().getSelectedItem().getCountryName()
				: null;
		reviewer = ReviewerDAO.getInstance().createReviewer(revNameTextField.getText(), revLastNameTextField.getText(),
				revEmailTextField.getText(), revContactTextField.getText(), revInstitutionTextField.getText(), country,
				noteTextArea.getText());
		Logging.getInstance().change("Create", "Create reviewer:\n\t" + reviewer);
		return reviewer;
	}

	private void setFirstName() {
		if (!reviewer.getReviewerFirstName().equalsIgnoreCase(revNameTextField.getText()))
			ReviewerDAO.getInstance().updateReviewerFirstName(reviewer, revNameTextField.getText());
	}

	private void setLastName() {
		if (!revLastNameTextField.getText().equalsIgnoreCase(reviewer.getReviewerLastName()))
			ReviewerDAO.getInstance().updateReviewerLastName(reviewer, revLastNameTextField.getText());
	}

	private void setEmail() {
		if (!revEmailTextField.getText().equalsIgnoreCase(reviewer.getReviewerEmail()))
			ReviewerDAO.getInstance().updateReviewerEmail(reviewer, revEmailTextField.getText());
	}

	private void setCountry() {
		if (revCountryComboBox.getSelectionModel().getSelectedItem() != null
				&& revCountryComboBox.getSelectionModel().getSelectedItem().getCountryID() != reviewer.getCountryID())
			ReviewerDAO.getInstance().updateCountry(reviewer,
					revCountryComboBox.getSelectionModel().getSelectedItem().getCountryName());
	}

	private void setContactTelephone() {
		if (!revContactTextField.getText().equalsIgnoreCase(reviewer.getContactTelephone()))
			ReviewerDAO.getInstance().updateReviewerTelephone(reviewer, revContactTextField.getText());
	}

	private void setInstitution() {
		if (!revInstitutionTextField.getText().equalsIgnoreCase(reviewer.getInstitutionName()))
			ReviewerDAO.getInstance().updateReviewerInstitution(reviewer, revInstitutionTextField.getText());
	}

	private void setNote() {
		if (reviewer.getNote() != null && !noteTextArea.getText().equalsIgnoreCase(reviewer.getNote()))
			ReviewerDAO.getInstance().updateNote(reviewer, noteTextArea.getText());
	}

	private void handleEditReviewer() {
		setFirstName();
		setLastName();
		setEmail();
		setCountry();
		setContactTelephone();
		setInstitution();
		setNote();
		reviewersTableView.refresh();
	}

}
