package alfatec.controller.paper;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.controlsfx.control.PrefixSelectionComboBox;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;

import alfatec.Main;
import alfatec.controller.author.SearchAuthorsController;
import alfatec.controller.conference.SearchReviewersController;
import alfatec.controller.email.GroupCallController;
import alfatec.controller.email.SendEmailController;
import alfatec.controller.utils.ClearPopUp;
import alfatec.controller.utils.Utils;
import alfatec.dao.conference.CollectionDAO;
import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.conference.RegistrationFeeDAO;
import alfatec.dao.conference.SpecialIssueDAO;
import alfatec.dao.country.CountryDAO;
import alfatec.dao.person.ReviewerDAO;
import alfatec.dao.relationship.AuthorResearchDAO;
import alfatec.dao.relationship.ReviewDAO;
import alfatec.dao.research.PaperworkDAO;
import alfatec.dao.research.ResearchDAO;
import alfatec.dao.utils.Logging;
import alfatec.model.conference.Collection;
import alfatec.model.conference.Conference;
import alfatec.model.conference.Field;
import alfatec.model.conference.RegistrationFee;
import alfatec.model.conference.SpecialIssue;
import alfatec.model.country.Country;
import alfatec.model.enums.Institution;
import alfatec.model.enums.Opinion;
import alfatec.model.enums.Presentation;
import alfatec.model.person.Author;
import alfatec.model.person.Reviewer;
import alfatec.model.relationship.Review;
import alfatec.model.research.Paperwork;
import alfatec.model.research.Research;
import alfatec.view.factory.ResearchFactory;
import alfatec.view.gui.MainView;
import alfatec.view.gui.ResearchView;
import alfatec.view.utils.GUIUtils;
import alfatec.view.utils.Utility;
import alfatec.view.wrappers.PaperworkResearch;
import alfatec.view.wrappers.ScientificWork;
import javafx.application.HostServices;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.DateUtil;

public class ScientificWorkTabController extends GUIUtils {

	@FXML
	private TextField searchScientificWorkField, selectReviewerTextField, swTitle;

	@FXML
	private TextArea swNote, noteTextArea;

	@FXML
	private TableView<ScientificWork> applicationsTableView;

	@FXML
	private TableColumn<ScientificWork, String> authorsColumn;

	@FXML
	private TableColumn<ScientificWork, String> titleColumn;

	@FXML
	private JFXButton addApplicationButton, updateApplicationButton, insertAuthorButton, importPaperButton,
			selectReviewerButton, sendForReviewButton, sendEmailButton, remove, saveButton, rwAcceptedButton,
			rwSmallButton, rwBigButton, rwRejectedButton, openPaperButton;

	@FXML
	private TableView<Author> miniAuthorTableView;

	@FXML
	private TableColumn<Author, String> miniAuthorColumn;

	@FXML
	private JFXCheckBox paperReceivedCheckBox, sentCheckBox, feePaidCheckBox;

	@FXML
	private Button closeButton, clearButton;

	@FXML
	private PrefixSelectionComboBox<RegistrationFee> feeComboBox;

	@FXML
	private JFXRadioButton liveRadio, videoRadio, pptRadio, collectionRadio, specialIssueRadio;

	@FXML
	private VBox details, authorDetails;

	@FXML
	private Label conferenceTitle, firstName, lastName, email, institution, institutionName, country, selected, opinion;

	/**
	 * Filters
	 */

	@FXML
	private ComboBox<Institution> filterInstitution;

	@FXML
	private TextField filterInstitutionName;

	@FXML
	private PrefixSelectionComboBox<Country> filterCountry;

	@FXML
	private ComboBox<Conference> filterConference;

	@FXML
	private ComboBox<Field> filterField;

	@FXML
	private JFXCheckBox filterCollSpec;

	@FXML
	private JFXCheckBox filterSentForReview;

	@FXML
	private TextField filterReviewer;

	@FXML
	private ComboBox<Opinion> filterStatus;

	@FXML
	private JFXCheckBox filterSubmittedWork;

	@FXML
	private ComboBox<Presentation> filterPresentation;

	@FXML
	private DatePicker filterDate;

	private static final int OTHER_POPUP = 340;
	private static final int POPUP = 720;
	private ObservableList<ScientificWork> data;
	private ObservableList<Author> addedAuthors, deletedAuthors;
	private ScientificWork united;
	private Reviewer reviewer;
	private Review review;
	private ClearPopUp clearDetails;
	private ToggleGroup group;
	private SendEmailController controllerEmail;
	private GroupCallController groupCall;
	private SearchAuthorsController controllerAuthors;
	private SearchReviewersController controllerReviewer;
	private String filePath;

	@FXML
	private void initialize() {
		clearDetails = () -> {
			united = null;
			reviewer = null;
			review = null;
			opinion.setText("");
			swTitle.clear();
			swNote.clear();
			selected.setText("Review:");
			setUpBox();
			clearBoxes(Arrays.asList(paperReceivedCheckBox, sentCheckBox, feePaidCheckBox),
					Arrays.asList(liveRadio, videoRadio, pptRadio, collectionRadio, specialIssueRadio));
			conferenceTitle.setText("Conference Title");
			selectReviewerTextField.clear();
			populateMiniTable(null);
			miniAuthorTableView.refresh();
		};
		setPopupOpen(true);
		setOtherPopupOpen(true);
		generalSetUp();
		populateMainTable();
		handleSearch();
	}

	@FXML
	void addApplication(ActionEvent event) {
		closePopUpsAndClear();
		setAddAction(true);
		changeDisable(!isAddAction());
		collectionRadio.setDisable(true);
		specialIssueRadio.setDisable(true);
		opinionButtonsChange(isAddAction());
		openPopup(details, POPUP);
	}

	@FXML
	void clearPopup(ActionEvent event) {
		clearDetails.clear();
	}

	@FXML
	void closePopup(ActionEvent event) {
		if (event.getSource() == closeButton) {
			closePopUps();
			setAddAction(false);
			setEditAction(false);
		} else
			closeOtherPopup(authorDetails, OTHER_POPUP);
	}

	@FXML
	void importPaper(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files", "*.*");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(((Stage) (((Button) event.getSource()).getScene().getWindow())));
		if (file != null && file.length() < getBlobLength()) {
			filePath = file.getAbsolutePath();
			return;
		} else if (file != null && file.length() > getBlobLength())
			alert("Selected file is too big",
					"Maximum size allowed in the database is 16MB.\nImport was not successful.", AlertType.INFORMATION);
		filePath = null;
	}

	@FXML
	private void openPaper() {
		if (united != null && united.getResearchProperty() != null) {
			File file = ResearchDAO.getInstance().getBlob(united.getResearchProperty().get().getResearch());
			if (!file.exists())
				alert("No research", "Research was not imported to the database.", AlertType.INFORMATION);
			else {
				HostServices hostServices = Main.getInstance().getHostServices();
				hostServices.showDocument(file.getAbsolutePath());
			}
		} else
			alert("No research", "Research must be first created.", AlertType.INFORMATION);
	}

	@FXML
	void insertAuthor(ActionEvent event) {
		if (miniAuthorTableView.getItems() != null)
			controllerAuthors = ResearchView.getInstance().loadSearchAuthors(controllerAuthors,
					miniAuthorTableView.getItems());
		else
			controllerAuthors = ResearchView.getInstance().loadSearchAuthors(controllerAuthors);
		populateMiniTable(Utils.removeDuplicates(controllerAuthors.getSelectedAuthors()));
		addedAuthors.setAll(miniAuthorTableView.getItems());
	}

	@FXML
	void selectReviewer(ActionEvent event) {
		controllerReviewer = ResearchView.getInstance().loadSearchReviewers(controllerReviewer);
		reviewer = controllerReviewer.getSelectedReviewer();
		selectReviewerTextField.setText(
				reviewer != null ? reviewer.getReviewerFirstName().concat(" ").concat(reviewer.getReviewerLastName())
						: null);
	}

	@FXML
	void sendEmail(ActionEvent event) {
		if (united != null && united.getAuthors() != null) {
			List<String> emails = united.getAuthors().stream().map(Author::getAuthorEmail).collect(Collectors.toList());
			if (emails.size() > 1)
				groupCall = MainView.getInstance().loadEmailWindow(groupCall, emails);
			else
				controllerEmail = MainView.getInstance().loadEmailWindow(controllerEmail, emails.get(0));
		} else
			alert("No selection", "Please select to whom you want to send E-mail", AlertType.INFORMATION);
	}

	@FXML
	void sendForReview(ActionEvent event) {
		if (united != null && reviewer != null) {
			File file = ResearchDAO.getInstance().getBlob(united.getPaperworkResearch().getResearch());
			ButtonType button = null;
			if (file != null && file.length() > 0)
				button = confirmationAlert("Research",
						"Research is imported to the database.\nDo you  want to attach it to the email?",
						AlertType.CONFIRMATION);
			if (button != null && button == ButtonType.OK)
				controllerEmail = MainView.getInstance().loadEmailWindow(controllerEmail, reviewer.getReviewerEmail(),
						file);
			else
				controllerEmail = MainView.getInstance().loadEmailWindow(controllerEmail, reviewer.getReviewerEmail());
		} else
			alert("No selection", "Please select to whom you want to send E-mail", AlertType.INFORMATION);
	}

	@FXML
	void updateApplication(ActionEvent event) {
		united = applicationsTableView.getSelectionModel().getSelectedItem();
		closePopUps();
		if (united != null && ConferenceDAO.getInstance().getCurrentConference() != null
				&& united.getPaperworkResearch().getPaperwork().getConferenceID() == ConferenceDAO.getInstance()
						.getCurrentConference().getConferenceID()) {
			setEditAction(true);
			changeDisable(!isEditAction());
			specialIssueRadio.setDisable(!collectionRadio.isSelected());
			showData(united);
			openPopup(details, POPUP);
		}
	}

	@FXML
	private void save() {
		if (isEditAction() && isValidName(swTitle)) {
			setEditAction(false);
			changeDisable(!isEditAction());
			handleEdit(united);
			refresh(applicationsTableView, applicationsTableView.getSelectionModel().getSelectedIndex(), details, POPUP,
					clearDetails);
			united = applicationsTableView.getSelectionModel().getSelectedItem();
			populateMiniTable(united.getAuthors());
			closePopUps();
			openPopup(details, POPUP);
			showData(united);
		} else if (isAddAction() && isValidName(swTitle)) {
			setAddAction(false);
			PaperworkResearch pr = create();
			united = new ScientificWork(miniAuthorTableView.getItems(), pr);
			connect();
			if (reviewer != null)
				assignReviewer(united);
			data.add(united);
			changeDisable(!isAddAction());
			refresh(applicationsTableView, data.size() - 1, details, POPUP, clearDetails);
			united = applicationsTableView.getSelectionModel().getSelectedItem();
			populateMiniTable(united.getAuthors());
			closePopUps();
			openPopup(details, POPUP);
			showData(united);
		} else if (!isValidName(swTitle))
			return;
		addedAuthors.removeAll(addedAuthors);
		deletedAuthors.removeAll(deletedAuthors);
		applicationsTableView.refresh();
		filePath = null;
	}

	@FXML
	private void removeAuthor() {
		deletedAuthors.add(miniAuthorTableView.getSelectionModel().getSelectedItem());
		miniAuthorTableView.getItems().remove(miniAuthorTableView.getSelectionModel().getSelectedItem());
		miniAuthorTableView.refresh();
		closeOtherPopup(authorDetails, OTHER_POPUP);
	}

	private void populateMainTable() {
		applicationsTableView.setPlaceholder(new Label("Database table \"research\" is empty"));
		Utility.setUpStringCell(applicationsTableView);
		titleColumn.setCellValueFactory(cellData -> cellData.getValue().getResearchProperty().getValue().getResearch()
				.getResearchTitleProperty());
		authorsColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorsStringProperty());
		applicationsTableView.setItems(data);
		applicationsTableView.refresh();
		applicationsTableView.setOnMousePressed(event -> {
			miniAuthorTableView.getSelectionModel().clearSelection();
			closePopUps();
			united = applicationsTableView.getSelectionModel().getSelectedItem();
			populateMiniTable(united.getAuthorsProperty().get());
			showData(united);
			openPopup(details, POPUP);
		});
	}

	private void populateMiniTable(ObservableList<Author> authors) {
		if (isOtherPopupOpen())
			closeOtherPopup(authorDetails, OTHER_POPUP);
		miniAuthorTableView.setItems(authors);
		miniAuthorTableView.refresh();
	}

	private void showData(ScientificWork united) {
		swTitle.setText(isAddAction() ? null : united.getResearchProperty().get().getResearch().getResearchTitle());
		swNote.setText(isAddAction() ? null : united.getResearchProperty().get().getResearch().getNote());
		paperReceivedCheckBox.setSelected(
				isAddAction() ? false : united.getResearchProperty().get().getPaperwork().isSubmittetWork());
		sentCheckBox.setSelected(
				isAddAction() ? false : united.getResearchProperty().get().getPaperwork().isSentToReview());
		feeComboBox.getSelectionModel().select(RegistrationFeeDAO.getInstance().getRegistration(
				united.getResearchProperty().get().getPaperwork().getRegistrationFeeIDProperty().get()));
		feePaidCheckBox
				.setSelected(isAddAction() ? false : united.getResearchProperty().get().getPaperwork().isFeePaid());
		String presentation = united == null || united.getResearchProperty() == null
				|| united.getResearchProperty().get().getPaperwork().getPresentation() == null ? ""
						: united.getResearchProperty().get().getPaperwork().getPresentationName();
		liveRadio.setSelected(presentation.equalsIgnoreCase(Presentation.LIVE.name()));
		videoRadio.setSelected(presentation.equalsIgnoreCase(Presentation.VIDEO.name()));
		pptRadio.setSelected(presentation.equalsIgnoreCase(Presentation.PPT.name()));
		collectionRadio.setSelected(
				isAddAction() ? false : united.getResearchProperty().get().getPaperwork().isForCollection());
		Collection collection = isAddAction() ? null
				: CollectionDAO.getInstance().getCollection(united.getResearchProperty().get().getResearch());
		specialIssueRadio.setSelected(collection == null ? false : collection.isForSpecialIssue());
		if (united.getResearchProperty() != null
				&& united.getResearchProperty().get().getPaperwork().getConferenceID() != 0)
			conferenceTitle.setText(ConferenceDAO.getInstance()
					.findBy(united.getResearchProperty().get().getPaperwork().getConferenceID()).getConferenceTitle());
		else if (ConferenceDAO.getInstance().getCurrentConference() != null)
			conferenceTitle.setText(ConferenceDAO.getInstance().getCurrentConference().getConferenceTitle());
		else
			conferenceTitle.setText("Conference Title");
		ObservableList<Review> reviews = ReviewDAO.getInstance()
				.getAllFor(united.getResearchProperty().get().getResearch().getResearchID());
		reviewer = reviews.size() > 0
				? ReviewerDAO.getInstance().getReviewer(reviews.get(reviews.size() - 1).getReviewerID())
				: null;
		selectReviewerTextField.setText(
				reviewer != null ? reviewer.getReviewerFirstName().concat(" ").concat(reviewer.getReviewerLastName())
						: null);
		setupFields(reviews);
		makeFakeRadio(Arrays.asList(rwAcceptedButton, rwBigButton, rwRejectedButton, rwSmallButton), reviews);
		review = reviews.size() > 0 ? reviews.get(reviews.size() - 1) : null;
		opinion.setText(getOpinions(reviews));
	}

	private void handleSearch() {
		searchScientificWorkField.setOnKeyTyped(event -> {
			closePopUpsAndClear();
			String search = searchScientificWorkField.getText();
			Pattern pattern = Pattern.compile("[@()\\\\<>+~%\\*\\-\\'\"]");
			Matcher matcher = pattern.matcher(search);
			if (search.length() > 0 && !matcher.find()) {
				ObservableList<ScientificWork> searched = ResearchFactory.getInstance()
						.searchBothAuthorsAndResearches(search);
				applicationsTableView.getItems().setAll(searched);
			} else {
				data = ResearchFactory.getInstance().getAllData();
				applicationsTableView.getItems().setAll(data);
			}
		});
	}

	private PaperworkResearch create() {
		String selectedToggle = group.getSelectedToggle() == null ? null
				: group.getSelectedToggle().getUserData().toString();
		int selectedFee = feeComboBox.getSelectionModel().getSelectedItem() == null ? 0
				: feeComboBox.getSelectionModel().getSelectedItem().getRegistrationFeeID();
		Research work;
		if (filePath == null)
			work = ResearchDAO.getInstance().createResearch(swTitle.getText(), swNote.getText());
		else
			work = ResearchDAO.getInstance().createResearch(swTitle.getText(), filePath, swNote.getText());
		Paperwork paperwork;
		if (ConferenceDAO.getInstance().getCurrentConference() == null)
			paperwork = PaperworkDAO.getInstance().createPaperwork(work.getResearchID(),
					paperReceivedCheckBox.isSelected(), selectedToggle, sentCheckBox.isSelected(), selectedFee,
					feePaidCheckBox.isSelected(), collectionRadio.isSelected(), DateUtil.format(LocalDateTime.now()));
		else
			paperwork = PaperworkDAO.getInstance().createPaperworkForCurrentConference(work.getResearchID(),
					paperReceivedCheckBox.isSelected(), selectedToggle, sentCheckBox.isSelected(), selectedFee,
					feePaidCheckBox.isSelected(), collectionRadio.isSelected(), DateUtil.format(LocalDateTime.now()));
		return new PaperworkResearch(paperwork, work);
	}

	private void connect() {
		if (united.getAuthors() != null)
			for (Author author : united.getAuthors())
				if (AuthorResearchDAO.getInstance().getEntry(
						united.getPaperworkResearch().getResearch().getResearchID(), author.getAuthorID()) == null)
					AuthorResearchDAO.getInstance().createEntry(author.getAuthorID(),
							united.getPaperworkResearch().getResearch().getResearchID());
	}

	private void showAuthor(Author author) {
		firstName.setText(author == null ? "" : author.getAuthorFirstName());
		lastName.setText(author == null ? "" : author.getAuthorLastName());
		email.setText(author == null ? "" : author.getAuthorEmail());
		institution.setText(author == null || author.getInstitution() == null ? "" : author.getInstitution().name());
		institutionName.setText(author == null ? "" : author.getInstitutionName());
		country.setText(author == null || CountryDAO.getInstance().getCountry(author.getCountryID()) == null ? ""
				: CountryDAO.getInstance().getCountry(author.getCountryID()).getCountryName());
		noteTextArea.setText(author == null ? "" : author.getNote());
		remove.setDisable(isAddAction() || isEditAction() ? false : true);
	}

	private void handleEdit(ScientificWork work) {
		setAuthors(work);
		setName(work);
		setNote(work);
		setPaper(work);
		setPresentation(work);
		setSpecial(work);
		setCollection(work);
		setRegistrationFee(work);
		setFeePaid(work);
		setSentToReview(work);
		setSubmittet(work);
		setReviewer(work);
		handleRemove(work);
		setReview(work);
		applicationsTableView.refresh();
	}

	private void setAuthors(ScientificWork work) {
		if (addedAuthors.size() > 0) {
			for (Author author : addedAuthors)
				if (AuthorResearchDAO.getInstance().getEntry(work.getPaperworkResearch().getResearch().getResearchID(),
						author.getAuthorID()) == null) {
					work.addAuthor(author);
					AuthorResearchDAO.getInstance().createEntry(author.getAuthorID(),
							work.getPaperworkResearch().getResearch().getResearchID());
					Logging.getInstance().change("update", "Added author\n\t" + author.getAuthorEmail()
							+ "\nto research\n\t" + work.getPaperworkResearch().getResearch().getResearchTitle());
				}
		}
	}

	private void handleRemove(ScientificWork work) {
		if (deletedAuthors.size() > 0)
			for (Author author : deletedAuthors) {
				work.removeAuthor(author);
				AuthorResearchDAO.getInstance().deleteEntry(AuthorResearchDAO.getInstance()
						.getEntry(work.getPaperworkResearch().getResearch().getResearchID(), author.getAuthorID()));
			}
	}

	private void setName(ScientificWork work) {
		if (!work.getPaperworkResearch().getResearch().getResearchTitle().equals(swTitle.getText()))
			ResearchDAO.getInstance().updateTitle(work.getPaperworkResearch().getResearch(), swTitle.getText());
	}

	private void setNote(ScientificWork work) {
		if (!work.getPaperworkResearch().getResearch().getNote().equals(swNote.getText()))
			ResearchDAO.getInstance().updateNote(work.getPaperworkResearch().getResearch(), swNote.getText());
	}

	private void setPaper(ScientificWork work) {
		if (filePath != null)
			ResearchDAO.getInstance().updatePaper(work.getPaperworkResearch().getResearch(), filePath);
	}

	private void setPresentation(ScientificWork work) {
		if (group.getSelectedToggle() != null
				&& !Presentation.getName(work.getPaperworkResearch().getPaperwork().getPresentation())
						.equalsIgnoreCase(group.getSelectedToggle().getUserData().toString()))
			PaperworkDAO.getInstance().updatePaperworkPresentationType(work.getPaperworkResearch().getPaperwork(),
					group.getSelectedToggle().getUserData().toString());
	}

	private void setCollection(ScientificWork work) {
		if (work.getPaperworkResearch().getPaperwork().isForCollection() != collectionRadio.isSelected()) {
			PaperworkDAO.getInstance().setIsForCollection(work.getPaperworkResearch().getPaperwork(),
					collectionRadio.isSelected());
			Collection collection = CollectionDAO.getInstance()
					.getCollection(work.getPaperworkResearch().getResearch());
			if (collection == null && work.getPaperworkResearch().getPaperwork().isForCollection())
				CollectionDAO.getInstance().createEntry(work.getPaperworkResearch().getResearch().getResearchID(),
						ConferenceDAO.getInstance().getCurrentConference().getConferenceID(), false);
			else if (collection != null && !work.getPaperworkResearch().getPaperwork().isForCollection())
				CollectionDAO.getInstance().deleteEntry(collection);
		}
	}

	private void setSpecial(ScientificWork work) {
		Collection collection = CollectionDAO.getInstance().getCollection(work.getPaperworkResearch().getResearch());
		if (collection != null && collection.isForSpecialIssue() != specialIssueRadio.isSelected()) {
			CollectionDAO.getInstance().updateForSpecialIssue(collection, specialIssueRadio.isSelected());
			SpecialIssue special = SpecialIssueDAO.getInstance().getByCollectionID(collection.getCollectionID());
			if (special != null && !collection.isForSpecialIssue())
				SpecialIssueDAO.getInstance().deleteEntry(special);
			else if (special == null && collection.isForSpecialIssue())
				SpecialIssueDAO.getInstance().createEntry(collection.getCollectionID());
		}
	}

	private void setRegistrationFee(ScientificWork work) {
		if (feeComboBox.getSelectionModel().getSelectedItem() != null && work.getPaperworkResearch().getPaperwork()
				.getRegistrationFeeID() != feeComboBox.getSelectionModel().getSelectedItem().getRegistrationFeeID())
			PaperworkDAO.getInstance().setRegistrationFee(work.getPaperworkResearch().getPaperwork(),
					feeComboBox.getSelectionModel().getSelectedItem().getRegistrationFeeID());
	}

	private void setFeePaid(ScientificWork work) {
		if (work.getPaperworkResearch().getPaperwork().isFeePaid() != feePaidCheckBox.isSelected())
			PaperworkDAO.getInstance().updateIsFeePaid(work.getPaperworkResearch().getPaperwork(),
					feePaidCheckBox.isSelected());
	}

	private void setSentToReview(ScientificWork work) {
		if (work.getPaperworkResearch().getPaperwork().isSentToReview() != sentCheckBox.isSelected())
			PaperworkDAO.getInstance().updateIsSentToReview(work.getPaperworkResearch().getPaperwork(),
					sentCheckBox.isSelected());
	}

	private void setSubmittet(ScientificWork work) {
		if (work.getPaperworkResearch().getPaperwork().isSubmittetWork() != paperReceivedCheckBox.isSelected())
			PaperworkDAO.getInstance().updateIsSubmittetWork(work.getPaperworkResearch().getPaperwork(),
					paperReceivedCheckBox.isSelected());
	}

	private void setReview(ScientificWork work) {
		if (!selected.getText().equals("Review:")) {
			ReviewDAO.getInstance().updateOpinion(ReviewDAO.getInstance().getReviewByID(review.getReviewID()),
					selected.getText().substring(8));
		}
	}

	private void assignReviewer(ScientificWork work) {
		if (reviewer != null) {
			review = ReviewDAO.getInstance().createReview(work.getPaperworkResearch().getResearch().getResearchID(),
					reviewer.getReviewerID(), null);
		}
	}

	private void setReviewer(ScientificWork work) {
		ObservableList<Review> reviews = ReviewDAO.getInstance()
				.getAllFor(work.getPaperworkResearch().getResearch().getResearchID());
		if (reviews.size() > 0) {
			if (reviews.size() == 1 && reviews.get(0).getOpinionName() != null)
				assignReviewer(work);
			else if (reviews.size() == 1 && reviews.get(0).getOpinionName() == null && reviewer != null
					&& reviewer.getReviewerID() != reviews.get(0).getReviewerID())
				ReviewDAO.getInstance().updateReviewer(reviews.get(0), reviewer.getReviewerID());
			else if (reviews.size() == 2 && reviews.get(1).getOpinionName() == null && reviewer != null
					&& reviewer.getReviewerID() != reviews.get(1).getReviewerID())
				ReviewDAO.getInstance().updateReviewer(reviews.get(1), reviewer.getReviewerID());
		} else
			assignReviewer(work);
	}

	private void setUpGroup() {
		group = new ToggleGroup();
		liveRadio.setUserData(Presentation.LIVE.name());
		liveRadio.setToggleGroup(group);
		videoRadio.setUserData(Presentation.VIDEO.name());
		videoRadio.setToggleGroup(group);
		pptRadio.setUserData(Presentation.PPT.name());
		pptRadio.setToggleGroup(group);
	}

	private void generalSetUp() {
		data = ResearchFactory.getInstance().getAllData();
		addedAuthors = FXCollections.observableArrayList();
		deletedAuthors = FXCollections.observableArrayList();
		setUpBox();
		setUpGroup();
		setUpFields(new TextArea[] { swNote }, new int[] { getNoteLength() });
		setUpFields(new TextField[] { swTitle }, new int[] { getResearchTitleLength() });
		setUpMiniTable();
		closePopUps();
		swNote.setEditable(false);
		noteTextArea.setEditable(false);
		noteTextArea.setWrapText(true);
		selectReviewerTextField.setEditable(false);
		Arrays.asList(rwAcceptedButton, rwBigButton, rwRejectedButton, rwSmallButton)
				.forEach(button -> button.setOnMousePressed(event -> selected.setText("Review: " + button.getText())));
		updateApplicationButton
				.setTooltip(new Tooltip("Only researches participating in active conference can be updated."));
	}

	private void setUpMiniTable() {
		Utility.setUpStringCell(miniAuthorTableView);
		miniAuthorColumn.setCellValueFactory(cellData -> {
			var firstName = cellData.getValue().getAuthorFirstNameProperty();
			var lastName = cellData.getValue().getAuthorLastNameProperty();
			var email = cellData.getValue().getAuthorEmailProperty();
			return Bindings.when(firstName.isEmpty().and(lastName.isEmpty())).then(email)
					.otherwise(firstName.concat(" ").concat(lastName));
		});
		miniAuthorTableView.setOnMousePressed(event -> {
			if (isOtherPopupOpen())
				closeOtherPopup(authorDetails, OTHER_POPUP);
			openOtherPopup(authorDetails, OTHER_POPUP);
			showAuthor(miniAuthorTableView.getSelectionModel().getSelectedItem());
		});
	}

	private void clearBoxes(List<JFXCheckBox> check, List<JFXRadioButton> radio) {
		check.forEach(box -> box.setSelected(false));
		radio.forEach(button -> button.setSelected(false));
	}

	private void changeDisable(boolean allow) {
		liveRadio.setDisable(allow);
		videoRadio.setDisable(allow);
		pptRadio.setDisable(allow);
		collectionRadio.setDisable(allow);
		specialIssueRadio.setDisable(allow);
		swNote.setEditable(!allow);
		swTitle.setEditable(!allow);
		paperReceivedCheckBox.setDisable(allow);
		sentCheckBox.setDisable(allow);
		feePaidCheckBox.setDisable(allow);
		feeComboBox.setDisable(allow);
		clearButton.setDisable(allow);
		insertAuthorButton.setDisable(allow);
		importPaperButton.setDisable(allow);
		selectReviewerButton.setDisable(allow);
		remove.setDisable(allow);
		saveButton.setDisable(allow);
		opinionButtonsChange(allow);
	}

	private void closePopUps() {
		changeDisable(true);
		if (isPopupOpen())
			closeDetails(details, POPUP);
		if (isOtherPopupOpen())
			closeOtherPopup(authorDetails, OTHER_POPUP);
	}

	private void closePopUpsAndClear() {
		changeDisable(true);
		if (isAddAction())
			setAddAction(false);
		if (isEditAction())
			setEditAction(false);
		if (isPopupOpen())
			closePopup(details, POPUP, clearDetails);
		if (isOtherPopupOpen())
			closeOtherPopup(authorDetails, OTHER_POPUP);
	}

	private void opinionButtonsChange(boolean allow) {
		rwAcceptedButton.setDisable(allow);
		rwBigButton.setDisable(allow);
		rwRejectedButton.setDisable(allow);
		rwSmallButton.setDisable(allow);
	}

	private void makeFakeRadio(List<JFXButton> buttons, ObservableList<Review> reviews) {
		buttons.forEach(button -> {
			if (reviewer == null)
				button.setDisable(true);
			if (reviews.size() == 1 && reviews.get(0).getOpinionProperty().get() != null
					&& (reviews.get(0).getOpinion().equalsIgnoreCase("rejected")
							|| reviews.get(0).getOpinion().equalsIgnoreCase("accepted")))
				disableButton(button);
			if (reviews.size() == 2 && isEditAction()) {
				if (button == rwBigButton || button == rwSmallButton)
					button.setVisible(false);
			} else
				button.setVisible(true);
			if (reviews.size() == 2 && reviews.get(1).getOpinionProperty().get() != null)
				disableButton(button);
		});
	}

	private void disableButton(JFXButton button) {
		button.setDisable(true);
		selectReviewerButton.setDisable(true);
		sentCheckBox.setDisable(true);
	}

	private String getOpinions(ObservableList<Review> reviews) {
		String first = "";
		if (reviews != null) {
			if (reviews.size() > 0 && reviews.get(0).getOpinionName() != null) {
				Reviewer firstR = ReviewerDAO.getInstance().getReviewer(reviews.get(0).getReviewerID());
				String name = firstR != null
						? firstR.getReviewerFirstName().concat(" ").concat(firstR.getReviewerLastName())
						: "deleted reviewer";
				first = "Status: 1. ".concat(reviews.get(0).getOpinionName().getOpinion()).concat(" by ").concat(name);
			}
			if (reviews.size() == 1) {
				return first;
			}
			if (reviews.size() == 2 && reviews.get(1).getOpinionName() != null)
				return first.concat(", 2. ").concat(reviews.get(1).getOpinionName().getOpinion());
			else if (reviews.size() == 2 && reviews.get(1).getOpinionName() == null)
				return first;
		}
		return "";
	}

	private void setupFields(ObservableList<Review> reviews) {
		if (reviews.size() == 1 && reviews.get(0).getOpinionProperty().get() != null) {
			reviewer = null;
			selectReviewerTextField.setText(null);
		}
	}

	private void setUpBox() {
		if (RegistrationFeeDAO.getInstance().getCurrentFees() != null)
			feeComboBox.getItems().setAll(RegistrationFeeDAO.getInstance().getCurrentFees());
		feeComboBox.getSelectionModel().select(null);
		feeComboBox.setPromptText("Please select");
	}
}
