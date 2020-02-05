package alfatec.controller.conference;

import java.util.Arrays;

import org.controlsfx.control.PrefixSelectionComboBox;

import com.jfoenix.controls.JFXButton;

import alfatec.controller.utils.ClearPopUp;
import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.conference.FieldDAO;
import alfatec.dao.conference.RegistrationFeeDAO;
import alfatec.dao.country.CountryDAO;
import alfatec.dao.person.AuthorDAO;
import alfatec.dao.person.ReviewerDAO;
import alfatec.dao.research.ResearchDAO;
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
			fieldNameErrorLabel, authors, researches;

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
		refreshLabel();
	}

	@FXML
	void addFee(ActionEvent event) {
		if (ConferenceDAO.getInstance().getCurrentConference() == null)
			alert("No active conference",
					"Registration fee and Conference are closely related.\nIn order to add registration fee, please start a Conference first.",
					AlertType.ERROR);
		else {
			closeAll();
			registrationFeeCleaner.clear();
			setAddAction(true);
			setRegistrationFee();
			saveFeeButton.setDisable(false);
			feePopup.setVisible(true);
			openYPopup(feePopup, -350);
			openFee = true;
		}
	}

	@FXML
	void addField(ActionEvent event) {
		closeAll();
		fieldCleaner.clear();
		setAddAction(true);
		setFields();
		saveFieldButton.setDisable(false);
		fieldsPopup.setVisible(true);
		openYPopup(fieldsPopup, -200);
		openField = true;
	}

	@FXML
	void addReviewer(ActionEvent event) {
		closeAll();
		reviewerCleaner.clear();
		setAddAction(true);
		setReviewerFields();
		saveRevButton.setDisable(false);
		revieversPopup.setVisible(true);
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
			if (ConferenceDAO.getInstance().getCurrentConference() == null || ConferenceDAO.getInstance()
					.getCurrentConference().getConferenceID() != registrationFee.getConferenceID())
				alert("Delete forbidden", "The conference with this registration fee is ended. Data are now read only.",
						AlertType.INFORMATION);
			else {
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
		registrationFee = feesTableView.getSelectionModel().getSelectedItem();
		if (registrationFee != null) {
			if (ConferenceDAO.getInstance().getCurrentConference() == null || ConferenceDAO.getInstance()
					.getCurrentConference().getConferenceID() != registrationFee.getConferenceID())
				alert("Edit forbidden", "The conference with this registration fee is ended. Data are now read only.",
						AlertType.INFORMATION);
			else {
				setEditAction(true);
				if (registrationFee != null) {
					setRegistrationFee();
					saveFeeButton.setDisable(false);
					openYPopup(feePopup, -350);
					openFee = true;
				}
			}
		}
	}

	@FXML
	void editField(ActionEvent event) {
		closeAll();
		field = fieldsTableView.getSelectionModel().getSelectedItem();
		setEditAction(true);
		if (field != null) {
			setFields();
			saveFieldButton.setDisable(false);
			openYPopup(fieldsPopup, -200);
			openField = true;
		}
	}

	@FXML
	void editReviewer(ActionEvent event) {
		closeAll();
		reviewer = reviewersTableView.getSelectionModel().getSelectedItem();
		setEditAction(true);
		if (reviewer != null) {
			setReviewerFields();
			saveRevButton.setDisable(false);
			openDetails(revieversPopup, 340);
			openReviewer = true;
		}
	}

	@FXML
	void saveField(ActionEvent event) {
		if (isAddAction()) {
			if (isValidFieldName() && !isFieldAlreadyInDB()) {
				fields.add(getNewField());
				refreshY(fieldsTableView, fields.size() - 1);
				closeFieldPopup(event);
				field = fieldsTableView.getSelectionModel().getSelectedItem();
			} else if (isFieldAlreadyInDB()) {
				fieldNameErrorLabel.setText("Field already exists.");
				return;
			} else
				return;
			setAddAction(false);
			setFields();
		} else if (isEditAction()) {
			field = fieldsTableView.getSelectionModel().getSelectedItem();
			if (isValidFieldName() && !isFieldAlreadyInDB())
				handleEditField();
			refreshY(fieldsTableView, fieldsTableView.getSelectionModel().getSelectedIndex());
			closeFieldPopup(event);
			field = fieldsTableView.getSelectionModel().getSelectedItem();
		}
		setEditAction(false);
		setFields();
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
			} else
				return;
			setAddAction(false);
			setReviewerFields();
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
			setReviewerFields();
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
	private void saveFee(ActionEvent event) {
		if (isAddAction()) {
			if (isValidFeeInput() && !isFeeAlreadyInDB()) {
				fees.add(getNewFee());
				refreshY(feesTableView, fees.size() - 1);
				closeFeePopup(event);
				registrationFee = feesTableView.getSelectionModel().getSelectedItem();
			} else if (isFeeAlreadyInDB()) {
				feeNameErrorLabel.setText("Registration fee already exists for this conference.");
				return;
			} else
				return;
			setAddAction(false);
			setRegistrationFee();
		} else if (isEditAction()) {
			registrationFee = feesTableView.getSelectionModel().getSelectedItem();
			if (isValidFeeInput())
				if (!feePresetTextField.getText().equals(registrationFee.getRegistrationName()) && isFeeAlreadyInDB()) {
					feeNameErrorLabel.setText("Database already has enter with the same fee name for this conference.");
					return;
				} else {
					handleEditRegistrationFee();
					refreshY(feesTableView, feesTableView.getSelectionModel().getSelectedIndex());
					closeFeePopup(event);
					registrationFee = feesTableView.getSelectionModel().getSelectedItem();
				}
			setEditAction(false);
			setRegistrationFee();
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
			feeCurrencyComboBox.setValue(Currency.RSD);
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
		setUpListeners();
		setReviewerFields();
		setFields();
		setRegistrationFee();
	}

	private void setReviewerFields() {
		Arrays.asList(revNameTextField, revLastNameTextField, revEmailTextField, revContactTextField,
				revInstitutionTextField).forEach(f -> f.setEditable(isAddAction() || isEditAction()));
		noteTextArea.setEditable(isAddAction() || isEditAction());
	}

	private void setFields() {
		fieldTextField.setEditable(isAddAction() || isEditAction());
	}

	private void setRegistrationFee() {
		Arrays.asList(feePresetTextField, feeAmountTextField)
				.forEach(f -> f.setEditable(isAddAction() || isEditAction()));
	}

	private void setUpListeners() {
		revEmailTextField.setOnKeyPressed(event -> {
			if (emailErrorLabel.getText() != null || !emailErrorLabel.getText().equals(""))
				emailErrorLabel.setText("");
		});
		revNameTextField.setOnKeyPressed(event -> {
			if (firstNameErrorLabel.getText() != null || !firstNameErrorLabel.getText().equals(""))
				firstNameErrorLabel.setText("");
		});
		revLastNameTextField.setOnKeyPressed(event -> {
			if (lastNameErrorLabel.getText() != null || !lastNameErrorLabel.getText().equals(""))
				lastNameErrorLabel.setText("");
		});
		fieldTextField.setOnKeyPressed(event -> {
			if (fieldNameErrorLabel.getText() != null || !fieldNameErrorLabel.getText().equals(""))
				fieldNameErrorLabel.setText("");
		});
		feePresetTextField.setOnKeyPressed(event -> {
			if (feeNameErrorLabel.getText() != null || !feeNameErrorLabel.getText().equals(""))
				feeNameErrorLabel.setText("");
		});
		feeAmountTextField.setOnKeyPressed(event -> {
			if (amountErrorLabel.getText() != null || !amountErrorLabel.getText().equals(""))
				amountErrorLabel.setText("");
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
		setReviewerFields();
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
		setFields();
		fieldTextField.setText(field.getFieldName());
	}

	private void showRegistrationFee(RegistrationFee fee) {
		saveFeeButton.setDisable(true);
		setRegistrationFee();
		feePresetTextField.setText(fee.getRegistrationName());
		feeCurrencyComboBox.getSelectionModel().select(fee.getCurrency());
		feeAmountTextField.setText(fee.getRegistrationPrice().toString());
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
		if (!noteTextArea.getText().equalsIgnoreCase(reviewer.getNote()))
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

	private boolean isValidFieldName() {
		fieldNameErrorLabel.setText(isValidName(fieldTextField) ? "" : "Empty field name.");
		return isValidName(fieldTextField);
	}

	private boolean isFieldAlreadyInDB() {
		return FieldDAO.getInstance().findFieldByName(fieldTextField.getText()) != null;
	}

	private Field getNewField() {
		field = FieldDAO.getInstance().createField(fieldTextField.getText());
		return field;
	}

	private void handleEditField() {
		if (!field.getFieldName().equalsIgnoreCase(fieldTextField.getText()))
			FieldDAO.getInstance().updateField(field, fieldTextField.getText());
		fieldsTableView.refresh();
	}

	private boolean isValidFeeInput() {
		feeNameErrorLabel.setText(isValidName(feePresetTextField) ? "" : "Empty registration fee name.");
		amountErrorLabel.setText(isValidName(feeAmountTextField) ? "" : "Empty amount for registration fee.");
		return isValidName(feeAmountTextField) && isValidName(feePresetTextField);
	}

	private boolean isFeeAlreadyInDB() {
		return RegistrationFeeDAO.getInstance().findFeeByName(feePresetTextField.getText()) != null;
	}

	private RegistrationFee getNewFee() {
		registrationFee = RegistrationFeeDAO.getInstance().create(feePresetTextField.getText(),
				Double.parseDouble(feeAmountTextField.getText()),
				feeCurrencyComboBox.getSelectionModel().getSelectedItem().name());
		return registrationFee;
	}

	private void setFeeName() {
		if (!registrationFee.getRegistrationName().equalsIgnoreCase(feePresetTextField.getText()))
			RegistrationFeeDAO.getInstance().updateRegistrationName(registrationFee, feePresetTextField.getText());
	}

	private void setFeeCurrency() {
		if (!registrationFee.getCurrency().equals(feeCurrencyComboBox.getSelectionModel().getSelectedItem()))
			RegistrationFeeDAO.getInstance().updateCurrency(registrationFee,
					feeCurrencyComboBox.getSelectionModel().getSelectedItem().name());
	}

	private void setFeePrice() {
		if (registrationFee.getRegistrationPriceDouble() != Double.parseDouble(feeAmountTextField.getText()))
			RegistrationFeeDAO.getInstance().updatePrice(registrationFee,
					Double.parseDouble(feeAmountTextField.getText()));
	}

	private void handleEditRegistrationFee() {
		setFeeName();
		setFeeCurrency();
		setFeePrice();
		feesTableView.refresh();
	}
	
	public void refreshLabel() {
		authors.setText("" + AuthorDAO.getInstance().getAllAuthors().size());
		researches.setText("" + ResearchDAO.getInstance().getAllResearches().size());
	}

}
