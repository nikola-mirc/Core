package alfatec.controller.conference;

import java.util.Arrays;

import com.jfoenix.controls.JFXButton;

import alfatec.controller.utils.ClearPopUp;
import alfatec.dao.conference.CollectionDAO;
import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.conference.DatesDAO;
import alfatec.dao.conference.FieldDAO;
import alfatec.dao.conference.RegistrationFeeDAO;
import alfatec.dao.conference.SpecialIssueDAO;
import alfatec.dao.research.ResearchDAO;
import alfatec.dao.wrappers.CDSettingsDAO;
import alfatec.model.conference.Collection;
import alfatec.model.conference.Conference;
import alfatec.model.conference.Dates;
import alfatec.model.conference.Field;
import alfatec.model.conference.RegistrationFee;
import alfatec.model.conference.SpecialIssue;
import alfatec.model.enums.Currency;
import alfatec.model.user.LoginData;
import alfatec.view.utils.GUIUtils;
import alfatec.view.utils.Utility;
import alfatec.view.wrappers.ConferenceDateSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.DateUtil;

public class ConferenceTabController extends GUIUtils {

	@FXML
	private TableView<ConferenceDateSettings> confTableView;

	@FXML
	private TableColumn<ConferenceDateSettings, String> confTitleColumn;

	@FXML
	private TableView<Collection> collectionTableView, thisConfCollectionTableView;

	@FXML
	private TableColumn<Collection, String> titleColumn, confColumn, thisConfTitleCol, thisConfColumn;

	@FXML
	private TableView<SpecialIssue> specialTableView, thisConfSpecialTableView;

	@FXML
	private TableColumn<SpecialIssue, String> titleSpecial, confSpecial, thisConfCollection;

	@FXML
	private HBox noConferenceHbox, addConferenceHbox, activeConferenceHbox;

	@FXML
	private VBox feePopup;

	@FXML
	private Label confLabel, activeConfLabel, confFieldLabel, confStartDate, confEndDate, firstCallLabel,
			secondCallLabel, thirdCallLabel, emailLabel;

	@FXML
	private JFXButton newConferenceButton, insertFeesButton, saveConfButton, editConfButton, closeConfButton,
			saveFeeButton;

	@FXML
	private TextField conferenceTitleTextField, confEmail, confBcc, feePresetTextField, feeAmountTextField;

	@FXML
	private ComboBox<Field> conferenceFieldComboBox;

	@FXML
	private ComboBox<Currency> feeCurrency;

	@FXML
	private TableView<RegistrationFee> feesTableView, registrationTableView;

	@FXML
	private TableColumn<RegistrationFee, String> presetCol, currencyCol, amountCol, feePresetColumn, feeCurrencyColumn,
			feeAmountColumn;

	@FXML
	private Button closeButton, clearButton, closeButton1, clearButton1, closeFeePopupButton, clearFeePopupButton;

	@FXML
	private PasswordField confPassword;

	@FXML
	private DatePicker firstCallDatePicker, secondCallDatePicker, thirdCallDatePicker;

	@FXML
	private TextArea confNotesTextArea;

	private BooleanProperty conferenceActive = new SimpleBooleanProperty(false);
	private BooleanProperty noActiveConference = new SimpleBooleanProperty(true);
	private BooleanProperty addingConferenceActive = new SimpleBooleanProperty(false);
	private ClearPopUp cleaner;
	private ConferenceDateSettings conference;
	private ObservableList<ConferenceDateSettings> conferenceData;
	private ObservableList<Collection> collections;
	private ObservableList<SpecialIssue> specials;
	private ObservableList<RegistrationFee> fees;

	@FXML
	private void initialize() {
		bindBoxes();
		cleaner = () -> {
			clearFields(Arrays.asList(confEmail, confBcc, conferenceTitleTextField), null);
			confPassword.setText(null);
			conferenceFieldComboBox.getSelectionModel().select(null);
			conference = null;
			firstCallDatePicker.setValue(null);
			secondCallDatePicker.setValue(null);
			thirdCallDatePicker.setValue(null);
			fees = FXCollections.observableArrayList();
			populateFeeTable();
		};
		conferenceData = CDSettingsDAO.getInstance().getAllData();
		collections = CollectionDAO.getInstance().getAll();
		specials = SpecialIssueDAO.getInstance().getAll();
		fees = FXCollections.observableArrayList();
		generalSetUp();
		populateHistoryTable();
		populateCollectionTable();
		populateSpecialTable();
	}

	@FXML
	void clearPopup(ActionEvent event) {
		cleaner.clear();
	}

	@FXML
	void closeConf(ActionEvent event) {
		if (conference.getConference().isRealized()) {
			closeYPopup(activeConferenceHbox, -570);
			return;
		}
		ButtonType type = confirmationAlert("Ending conference",
				"This action will mark current conference as realized and end it.\nAre you sure you want to continue?",
				AlertType.CONFIRMATION);
		if (type == ButtonType.OK) {
			DatesDAO.getInstance().endActiveConference(conference.getDates());
			ConferenceDAO.getInstance().endConference(conference.getConference());
			closeYPopup(activeConferenceHbox, -570);
			conferenceActive.set(false);
			noActiveConference.set(true);
		}
	}

	@FXML
	void closePopup(ActionEvent event) {
		if (event.getSource() == closeButton1)
			closeYPopup(activeConferenceHbox, -570);
		else if (event.getSource() == closeButton)
			closeYPopup(addConferenceHbox, -570);
	}

	@FXML
	void createNewConference(ActionEvent event) {
		addingConferenceActive.set(true);
		setAddAction(true);
		openYPopup(addConferenceHbox, -570);
	}

	@FXML
	void editConf(ActionEvent event) {
		setEditAction(true);
		openYPopup(addConferenceHbox, -570);
	}

	@FXML
	void insertFees(ActionEvent event) {
		feePopup.setVisible(true);
		openYPopup(feePopup, -350);
	}

	@FXML
	void saveConference(ActionEvent event) {
		if (isAddAction()) {
			handleAddAction(event);
			cleaner.clear();
		} else if (isEditAction()) {
			// to do...
		}
	}

	@FXML
	private void closeFeePopup(ActionEvent event) {
		closeYPopup(feePopup, -350);
		clearFeePopup(event);
	}

	@FXML
	private void clearFeePopup(ActionEvent event) {
		feeCurrency.getSelectionModel().select(Currency.RSD);
		feePresetTextField.setText(null);
		feeAmountTextField.setText(null);
	}

	@FXML
	private void saveFee(ActionEvent event) {
		if (isValidName(feePresetTextField) && isValidName(feeAmountTextField)) {
			RegistrationFee fee = RegistrationFeeDAO.getInstance().create(feePresetTextField.getText(),
					Double.parseDouble(feeAmountTextField.getText()),
					feeCurrency.getSelectionModel().getSelectedItem().name(), null);
			closeFeePopup(event);
			fees.add(fee);
			feesTableView.refresh();
		} else {
			alert("Empty fields", "Registration fee name and/or amount fields must not be empty.", AlertType.WARNING);
			return;
		}
	}

	public void disablePartsForAdminAccess(LoginData ld) {
		if (ld.getRoleID() == 2) {
			newConferenceButton.setVisible(false);
			editConfButton.setVisible(false);
			closeConfButton.setVisible(false);
		}
	}

	private void generalSetUp() {
		feeCurrency.getItems().setAll(FXCollections.observableArrayList(Currency.values()));
		feeCurrency.getSelectionModel().select(Currency.RSD);
		conferenceFieldComboBox.getItems().setAll(FieldDAO.getInstance().getAllFields());
		setUpFields(new TextField[] { conferenceTitleTextField, confEmail, confBcc, feePresetTextField }, new int[] {
				getConferenceTitleLength(), getEmailLength(), getEmailLength(), getRegistrationFeeNameLength() });
		setUpFields(new PasswordField[] { confPassword }, new int[] { getPasswordLength() });
		setUpFields(new TextArea[] { confNotesTextArea }, new int[] { getNoteLength() });
		setUpDecimal(feeAmountTextField);
		Utility.setUpStringCell(thisConfCollectionTableView);
		Utility.setUpStringCell(thisConfSpecialTableView);
		Utility.setUpStringCell(feesTableView);
		Utility.setUpStringCell(registrationTableView);
		populateFeeTable();
		confNotesTextArea.setEditable(false);
		confNotesTextArea.setWrapText(true);
	}

	private void bindBoxes() {
		if (ConferenceDAO.getInstance().getCurrentConference() != null) {
			this.conferenceActive.set(true);
			this.noActiveConference.set(false);
		}
		activeConferenceHbox.visibleProperty().bind(conferenceActive);
		noConferenceHbox.visibleProperty().bind(noActiveConference);
		addConferenceHbox.visibleProperty().bind(addingConferenceActive);
	}

	private void handleAddConference() {
		String field = conferenceFieldComboBox.getSelectionModel().getSelectedItem() == null ? null
				: conferenceFieldComboBox.getSelectionModel().getSelectedItem().getFieldName();
		Conference start = ConferenceDAO.getInstance().startConference(conferenceTitleTextField.getText(), field,
				confEmail.getText(), confPassword.getText(), confBcc.getText(), confNotesTextArea.getText(), null);
		populateFeeTable();
		setRegistrationFee(start);
		Dates dates = DatesDAO.getInstance().create(DateUtil.format(firstCallDatePicker.getValue()),
				DateUtil.format(secondCallDatePicker.getValue()), DateUtil.format(thirdCallDatePicker.getValue()));
		conference = new ConferenceDateSettings(start, dates);
		conferenceData.add(conference);
	}

	private void handleAddAction(ActionEvent event) {
		if (isValidName(conferenceTitleTextField) && isValidEmail(confEmail)) {
			if (!confBcc.getText().isEmpty() && !isValidEmail(confBcc)) {
				alert("Invalid field", "An email addresss (conference BCC) must contains \"@\" and \".\".",
						AlertType.WARNING);
				return;
			}
			handleAddConference();
			setAddAction(false);
			addingConferenceActive.set(false);
			noActiveConference.set(false);
			conferenceActive.set(true);
			refreshY(confTableView, conferenceData.size() - 1);
			openYPopup(activeConferenceHbox, -570);
			conference = confTableView.getSelectionModel().getSelectedItem();
			showConference(conference);
		} else {
			alert("Empty or invalid fields",
					"Conference title and/or email fields can not be empty.\nAlso, an email addresss must contains \"@\" and \".\".",
					AlertType.WARNING);
			return;
		}
	}

	private void handleEditConference(ConferenceDateSettings cds) {
		populateFeeTable();
		setRegistrationFee(cds.getConference());
		// to do...
	}

	private void populateHistoryTable() {
		confTableView.setPlaceholder(new Label("No past conferences"));
		Utility.setUpStringCell(confTableView);
		confTitleColumn
				.setCellValueFactory(cellData -> cellData.getValue().getConference().getConferenceTitleProperty());
		confTableView.setItems(conferenceData);
		confTableView.setOnMousePressed(event -> {
			if (confTableView.getSelectionModel().getSelectedItem() != null) {
				conferenceActive.set(true);
				openYPopup(activeConferenceHbox, -570);
				conference = confTableView.getSelectionModel().getSelectedItem();
				showConference(conference);
			}
		});
	}

	private void populateCollectionTable() {
		Utility.setUpStringCell(collectionTableView);
		titleColumn.setCellValueFactory(cellData -> ResearchDAO.getInstance()
				.getResearch(cellData.getValue().getResearchIDProperty().get()).getResearchTitleProperty());
		confColumn.setCellValueFactory(cellData -> ConferenceDAO.getInstance()
				.findBy(cellData.getValue().getFromConferenceIDProperty().get()).getConferenceTitleProperty());
		collectionTableView.setItems(collections);
	}

	private void populateSpecialTable() {
		Utility.setUpStringCell(specialTableView);
		titleSpecial.setCellValueFactory(cellData -> ResearchDAO
				.getInstance().getResearch(CollectionDAO.getInstance()
						.get(cellData.getValue().getCollectionIDProperty().get()).getResearchIDProperty().get())
				.getResearchTitleProperty());
		confSpecial.setCellValueFactory(cellData -> ConferenceDAO
				.getInstance().findBy(CollectionDAO.getInstance()
						.get(cellData.getValue().getCollectionIDProperty().get()).getFromConferenceIDProperty().get())
				.getConferenceTitleProperty());
		specialTableView.setItems(specials);
	}

	private void populateThisCollectionTable(ObservableList<Collection> list) {
		thisConfTitleCol.setCellValueFactory(cellData -> ResearchDAO.getInstance()
				.getResearch(cellData.getValue().getResearchIDProperty().get()).getResearchTitleProperty());
		thisConfCollectionTableView.setItems(list);
		thisConfCollectionTableView.refresh();
	}

	private void populateThisSpecialTable(ObservableList<SpecialIssue> list) {
		thisConfCollection.setCellValueFactory(cellData -> ResearchDAO
				.getInstance().getResearch(CollectionDAO.getInstance()
						.get(cellData.getValue().getCollectionIDProperty().get()).getResearchIDProperty().get())
				.getResearchTitleProperty());
		thisConfSpecialTableView.setItems(list);
		thisConfSpecialTableView.refresh();
	}

	private void populateFeeTable() {
		presetCol.setCellValueFactory(cellData -> cellData.getValue().getRegistrationNameProperty());
		currencyCol.setCellValueFactory(
				cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCurrencyProperty().get().name()));
		amountCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getRegistrationPriceProperty().asString().get()));
		feesTableView.setItems(fees);
		feesTableView.refresh();
	}

	private void populateRegistrationFeeTable(ConferenceDateSettings cds) {
		feePresetColumn.setCellValueFactory(cellData -> cellData.getValue().getRegistrationNameProperty());
		feeCurrencyColumn.setCellValueFactory(
				cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCurrencyProperty().get().name()));
		feeAmountColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				cellData.getValue().getRegistrationPriceProperty().asString().get()));
		registrationTableView
				.setItems(RegistrationFeeDAO.getInstance().getAllForConference(cds.getConference().getConferenceID()));
	}

	private void setRegistrationFee(Conference conference) {
		for (RegistrationFee fee : fees)
			if (fee.getConferenceIDProperty().getValue() == 0)
				RegistrationFeeDAO.getInstance().updateConferenceID(fee, conference.getConferenceID());
	}

	private void showConference(ConferenceDateSettings cds) {
		editConfButton.setVisible(cds.getConference().isRealized() ? false : true);
		activeConfLabel.setText(cds.getConference().getConferenceTitle());
		confFieldLabel.setText(FieldDAO.getInstance().getField(cds.getConference().getFieldID()) == null ? null
				: FieldDAO.getInstance().getField(cds.getConference().getFieldID()).getFieldName());
		confStartDate.setText(cds.getDates().getStartTimestamp());
		confEndDate.setText(cds.getDates().getEndDateString());
		firstCallLabel.setText(cds.getDates().getFirstCallString());
		secondCallLabel.setText(cds.getDates().getSecondCallString());
		thirdCallLabel.setText(cds.getDates().getThirdCallString());
		emailLabel.setText(cds.getConference().getConferenceEmail());
		populateThisCollectionTable(
				CollectionDAO.getInstance().getAllForConference(cds.getConference().getConferenceID()));
		populateThisSpecialTable(
				SpecialIssueDAO.getInstance().getFromConference(cds.getConference().getConferenceID()));
		populateRegistrationFeeTable(cds);
	}

	public void refreshSpecialTab() {
		specials = SpecialIssueDAO.getInstance().getAll();
		specialTableView.setItems(specials);
		specialTableView.refresh();
	}

	public void refreshCollectionTab() {
		collections = CollectionDAO.getInstance().getAll();
		collectionTableView.setItems(collections);
		collectionTableView.refresh();
	}

	public void refreshSelection() {
		if (conferenceData.size() > 0 && ConferenceDAO.getInstance().getCurrentConference() != null) {
			refreshY(confTableView, conferenceData.size() - 1);
			conference = confTableView.getSelectionModel().getSelectedItem();
			showConference(conference);
		}
	}
}
