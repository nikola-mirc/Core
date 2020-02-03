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
import alfatec.dao.person.AuthorDAO;
import alfatec.dao.relationship.ConferenceCallDAO;
import alfatec.dao.research.ResearchDAO;
import alfatec.dao.wrappers.CDSettingsDAO;
import alfatec.model.conference.Collection;
import alfatec.model.conference.Conference;
import alfatec.model.conference.Dates;
import alfatec.model.conference.Field;
import alfatec.model.conference.RegistrationFee;
import alfatec.model.conference.SpecialIssue;
import alfatec.model.person.Author;
import alfatec.model.user.LoginData;
import alfatec.view.factory.ResearchFactory;
import alfatec.view.utils.GUIUtils;
import alfatec.view.utils.Utility;
import alfatec.view.wrappers.ConferenceDateSettings;
import alfatec.view.wrappers.ScientificWork;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
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
import util.DateUtil;

public class ConferenceTabController extends GUIUtils {

	@FXML
	private TableView<ConferenceDateSettings> confTableView;

	@FXML
	private TableColumn<ConferenceDateSettings, String> confTitleColumn;

	@FXML
	private TableView<Collection> collectionTableView, thisConfCollectionTableView;

	@FXML
	private TableView<ScientificWork> thisConfResearchTableView;

	@FXML
	private TableColumn<ScientificWork, String> thisResearch;

	@FXML
	private TableColumn<Collection, String> titleColumn, confColumn, thisConfTitleCol;

	@FXML
	private TableView<SpecialIssue> specialTableView, thisConfSpecialTableView;

	@FXML
	private TableColumn<SpecialIssue, String> titleSpecial, confSpecial, thisConfCollection;

	@FXML
	private HBox noConferenceHbox, addConferenceHbox, activeConferenceHbox;

	@FXML
	private Label confLabel, activeConfLabel, confFieldLabel, confStartDate, confEndDate, firstCallLabel,
			secondCallLabel, thirdCallLabel, emailLabel;

	@FXML
	private JFXButton newConferenceButton, saveConfButton, editConfButton, closeConfButton;

	@FXML
	private TextField conferenceTitleTextField, confEmail, confBcc;

	@FXML
	private ComboBox<Field> conferenceFieldComboBox;

	@FXML
	private TableView<RegistrationFee> registrationTableView;

	@FXML
	private TableColumn<RegistrationFee, String> feePresetColumn, feeCurrencyColumn, feeAmountColumn;

	@FXML
	private Button closeButton, clearButton, closeButton1, clearButton1;

	@FXML
	private PasswordField confPassword;

	@FXML
	private DatePicker firstCallDatePicker, secondCallDatePicker, thirdCallDatePicker;

	@FXML
	private TextArea confNotesTextArea, addNote, statsTextArea;

	private BooleanProperty conferenceActive = new SimpleBooleanProperty(false);
	private BooleanProperty noActiveConference = new SimpleBooleanProperty(true);
	private BooleanProperty addingConferenceActive = new SimpleBooleanProperty(false);
	private ClearPopUp cleaner;
	private ConferenceDateSettings conference;
	private ObservableList<ConferenceDateSettings> conferenceData;
	private ObservableList<Collection> collections;
	private ObservableList<SpecialIssue> specials;
	private int userRole;

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
			addNote.clear();
		};
		conferenceData = CDSettingsDAO.getInstance().getAllData();
		collections = CollectionDAO.getInstance().getAll();
		specials = SpecialIssueDAO.getInstance().getAll();
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
		conference = confTableView.getSelectionModel().getSelectedItem();
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
		closeYPopup(activeConferenceHbox, -570);
		addingConferenceActive.set(true);
		setEditAction(true);
		configureEditBox();
		openYPopup(addConferenceHbox, -570);
	}

	@FXML
	void saveConference(ActionEvent event) {
		if (isAddAction()) {
			handleAddAction(event);
			cleaner.clear();
		} else if (isEditAction() && handleEditAction()) {
			addingConferenceActive.set(false);
			cleaner.clear();
		}
	}

	public void disablePartsForAdminAccess(LoginData ld) {
		if (ld.getRoleID() == 2) {
			newConferenceButton.setVisible(false);
			editConfButton.setVisible(false);
			closeConfButton.setVisible(false);
		}
		userRole = ld.getRoleID();
	}

	private void generalSetUp() {
		conferenceFieldComboBox.getItems().setAll(FieldDAO.getInstance().getAllFields());
		setUpFields(new TextField[] { conferenceTitleTextField, confEmail, confBcc },
				new int[] { getConferenceTitleLength(), getEmailLength(), getEmailLength() });
		setUpFields(new PasswordField[] { confPassword }, new int[] { getPasswordLength() });
		setUpFields(new TextArea[] { confNotesTextArea, addNote }, new int[] { getNoteLength(), getNoteLength() });
		Utility.setUpStringCell(thisConfCollectionTableView);
		Utility.setUpStringCell(thisConfSpecialTableView);
		Utility.setUpStringCell(registrationTableView);
		confNotesTextArea.setEditable(false);
		confNotesTextArea.setWrapText(true);
		addNote.setWrapText(true);
		statsTextArea.setEditable(false);
		statsTextArea.setWrapText(true);
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
				confEmail.getText(), confPassword.getText(), confBcc.getText(), addNote.getText(), null);
		Dates dates = DatesDAO.getInstance().create(DateUtil.format(firstCallDatePicker.getValue()),
				DateUtil.format(secondCallDatePicker.getValue()), DateUtil.format(thirdCallDatePicker.getValue()));
		conference = new ConferenceDateSettings(start, dates);
		conferenceData.add(conference);
		for (Author author : AuthorDAO.getInstance().getAllAuthors())
			ConferenceCallDAO.getInstance().createEntry(conference.getConference().getConferenceID(),
					author.getAuthorID());
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
			conference = confTableView.getSelectionModel().getSelectedItem();
			showConference(conference);
			openYPopup(activeConferenceHbox, -570);
		} else {
			alert("Empty or invalid fields",
					"Conference title and/or email fields can not be empty.\nAlso, an email addresss must contains \"@\" and \".\".",
					AlertType.WARNING);
			return;
		}
	}

	private void handleEditConference() {
		setConferenceTitle();
		setEmail();
		setBCC();
		setPassword();
		setField();
		setFirstCall();
		setSecondCall();
		setThirdCall();
		setNote();
	}

	private boolean handleEditAction() {
		if (isValidName(conferenceTitleTextField) && isValidEmail(confEmail)) {
			if (!confBcc.getText().isEmpty() && !isValidEmail(confBcc)) {
				alert("Invalid field", "An email addresss (conference BCC) must contains \"@\" and \".\".",
						AlertType.WARNING);
				return false;
			}
			handleEditConference();
			setEditAction(false);
			refreshY(confTableView, conferenceData.size() - 1);
			conference = confTableView.getSelectionModel().getSelectedItem();
			showConference(conference);
			openYPopup(activeConferenceHbox, -570);
			return true;
		} else {
			alert("Empty or invalid fields",
					"Conference title and/or email fields can not be empty.\nAlso, an email addresss must contains \"@\" and \".\".",
					AlertType.WARNING);
			return false;
		}
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
		titleColumn.setCellValueFactory(cellData -> ResearchFactory.getInstance()
				.workToString(ResearchDAO.getInstance().getResearch(CollectionDAO.getInstance()
						.get(cellData.getValue().getCollectionIDProperty().get()).getResearchIDProperty().get())));
		confColumn.setCellValueFactory(cellData -> ConferenceDAO.getInstance()
				.findBy(cellData.getValue().getFromConferenceIDProperty().get()).getConferenceTitleProperty());
		collectionTableView.setItems(collections);
	}

	private void populateSpecialTable() {
		Utility.setUpStringCell(specialTableView);
		titleSpecial.setCellValueFactory(cellData -> ResearchFactory.getInstance()
				.workToString(ResearchDAO.getInstance().getResearch(CollectionDAO.getInstance()
						.get(cellData.getValue().getCollectionIDProperty().get()).getResearchIDProperty().get())));
		confSpecial.setCellValueFactory(cellData -> ConferenceDAO
				.getInstance().findBy(CollectionDAO.getInstance()
						.get(cellData.getValue().getCollectionIDProperty().get()).getFromConferenceIDProperty().get())
				.getConferenceTitleProperty());
		specialTableView.setItems(specials);
	}

	private void populateThisResearchTable(ObservableList<ScientificWork> list) {
		Utility.setUpStringCell(thisConfResearchTableView);
		thisResearch.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().toString()));
		thisConfResearchTableView.setItems(list);
		thisConfResearchTableView.refresh();
	}

	private void populateThisCollectionTable(ObservableList<Collection> list) {
		thisConfTitleCol.setCellValueFactory(cellData -> ResearchFactory.getInstance()
				.workToString(ResearchDAO.getInstance().getResearch(CollectionDAO.getInstance()
						.get(cellData.getValue().getCollectionIDProperty().get()).getResearchIDProperty().get())));
		thisConfCollectionTableView.setItems(list);
		thisConfCollectionTableView.refresh();
	}

	private void populateThisSpecialTable(ObservableList<SpecialIssue> list) {
		thisConfCollection.setCellValueFactory(cellData -> ResearchFactory.getInstance()
				.workToString(ResearchDAO.getInstance().getResearch(CollectionDAO.getInstance()
						.get(cellData.getValue().getCollectionIDProperty().get()).getResearchIDProperty().get())));
		thisConfSpecialTableView.setItems(list);
		thisConfSpecialTableView.refresh();
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

	private void showConference(ConferenceDateSettings cds) {
		editConfButton.setVisible(cds.getConference().isRealized() || userRole == 2 ? false : true);
		activeConfLabel.setText(cds.getConference().getConferenceTitle());
		confFieldLabel.setText(FieldDAO.getInstance().getField(cds.getConference().getFieldID()) == null ? null
				: FieldDAO.getInstance().getField(cds.getConference().getFieldID()).getFieldName());
		confStartDate.setText(cds.getDates().getStartDatum());
		confEndDate.setText(cds.getDates().getEndDateString());
		firstCallLabel.setText(cds.getDates().getFirstCallString());
		secondCallLabel.setText(cds.getDates().getSecondCallString());
		thirdCallLabel.setText(cds.getDates().getThirdCallString());
		emailLabel.setText(cds.getConference().getConferenceEmail());
		confNotesTextArea.setText(cds.getConference().getNoteProperty().get());
		populateThisCollectionTable(
				CollectionDAO.getInstance().getAllForConference(cds.getConference().getConferenceID()));
		populateThisSpecialTable(
				SpecialIssueDAO.getInstance().getFromConference(cds.getConference().getConferenceID()));
		populateRegistrationFeeTable(cds);
		populateThisResearchTable(ResearchFactory.getInstance()
				.getAllForConference(ConferenceDAO.getInstance().findBy(cds.getConference().getConferenceID())));
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

	private void setConferenceTitle() {
		if (!conference.getConference().getConferenceTitle().equals(conferenceTitleTextField.getText()))
			ConferenceDAO.getInstance().updateConferenceTitle(conference.getConference(),
					conferenceTitleTextField.getText());
	}

	private void setEmail() {
		if (!conference.getConference().getConferenceEmail().equals(confEmail.getText()))
			ConferenceDAO.getInstance().updateConferenceEmail(conference.getConference(), confEmail.getText());
	}

	private void setBCC() {
		if (!conference.getConference().getConferenceBcc().equals(confBcc.getText()))
			ConferenceDAO.getInstance().updateConferenceBcc(conference.getConference(), confBcc.getText());
	}

	private void setField() {
		if ((conference.getConference().getFieldIDProperty() == null
				&& conferenceFieldComboBox.getSelectionModel().getSelectedItem() != null)
				|| (conferenceFieldComboBox.getSelectionModel().getSelectedItem() != null && conference.getConference()
						.getFieldID() != conferenceFieldComboBox.getSelectionModel().getSelectedItem().getFieldID()))
			ConferenceDAO.getInstance().updateConferenceField(conference.getConference(),
					conferenceFieldComboBox.getSelectionModel().getSelectedItem().getFieldID());
	}

	private void setPassword() {
		if ((conference.getConference().getConferenceEmailPassword() == null
				&& conference.getConference().getConferenceEmailPassword() != confPassword.getText())
				|| (conference.getConference().getConferenceEmailPassword() != null
						&& !conference.getConference().getConferenceEmailPassword().equals(confPassword.getText())))
			ConferenceDAO.getInstance().updateConferencePassword(conference.getConference(), confPassword.getText());
	}

	private void setFirstCall() {
		if ((conference.getDates().getFirstCallString() == null
				&& conference.getDates().getFirstCallString() != DateUtil.format(firstCallDatePicker.getValue()))
				|| (conference.getDates().getFirstCallString() != null && !conference.getDates().getFirstCallString()
						.equals(DateUtil.format(firstCallDatePicker.getValue()))))
			DatesDAO.getInstance().updateFirstCall(conference.getDates(),
					DateUtil.format(firstCallDatePicker.getValue()));
	}

	private void setSecondCall() {
		if ((conference.getDates().getSecondCallString() == null
				&& conference.getDates().getSecondCallString() != DateUtil.format(secondCallDatePicker.getValue()))
				|| (conference.getDates().getSecondCallString() != null && !conference.getDates().getSecondCallString()
						.equals(DateUtil.format(secondCallDatePicker.getValue()))))
			DatesDAO.getInstance().updateSecondCall(conference.getDates(),
					DateUtil.format(secondCallDatePicker.getValue()));
	}

	private void setThirdCall() {
		if ((conference.getDates().getThirdCallString() == null
				&& conference.getDates().getThirdCallString() != DateUtil.format(thirdCallDatePicker.getValue()))
				|| (conference.getDates().getThirdCallString() != null && !conference.getDates().getThirdCallString()
						.equals(DateUtil.format(thirdCallDatePicker.getValue()))))
			DatesDAO.getInstance().updateThirdCall(conference.getDates(),
					DateUtil.format(thirdCallDatePicker.getValue()));
	}

	private void setNote() {
		if (!conference.getConference().getNote().equals(addNote.getText()))
			ConferenceDAO.getInstance().updateConferenceNote(conference.getConference(), addNote.getText());
	}

	private void configureEditBox() {
		conference = confTableView.getSelectionModel().getSelectedItem();
		conferenceTitleTextField.setText(conference.getConference().getConferenceTitle());
		confEmail.setText(conference.getConference().getConferenceEmail());
		confBcc.setText(conference.getConference().getConferenceBcc());
		conferenceFieldComboBox.getSelectionModel()
				.select(FieldDAO.getInstance().getField(conference.getConference().getFieldID()));
		confPassword.setText(conference.getConference().getConferenceEmailPassword());
		firstCallDatePicker.setValue(conference.getDates().getFirstCallDate());
		secondCallDatePicker.setValue(conference.getDates().getSecondCallDate());
		thirdCallDatePicker.setValue(conference.getDates().getThirdCallDate());
		addNote.setText(conference.getConference().getNote());
	}
}
