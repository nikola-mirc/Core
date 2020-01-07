package alfatec.controller.conference;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PrefixSelectionComboBox;

public class ConferenceManagementTabController {

	@FXML
	private TableView<?> reviewersTableView;

	@FXML
	private TableColumn<?, ?> revNameCol;

	@FXML
	private TableColumn<?, ?> revEmailCol;

	@FXML
	private TableColumn<?, ?> revInstitutionCol;

	@FXML
	private TableColumn<?, ?> revContactCol;

	@FXML
	private TableColumn<?, ?> revCountryCol;

	@FXML
	private JFXButton addRevButton;

	@FXML
	private JFXButton editRevButton;

	@FXML
	private JFXButton deleteRevButton;

	@FXML
	private TableView<?> fieldsTableView;

	@FXML
	private TableColumn<?, ?> fieldCol;

	@FXML
	private JFXButton addFieldButton;

	@FXML
	private JFXButton editFieldButton;

	@FXML
	private JFXButton deleteFieldButton;

	@FXML
	private TableView<?> feesTableView;

	@FXML
	private TableColumn<?, ?> presetCol;

	@FXML
	private TableColumn<?, ?> currencyCol;

	@FXML
	private TableColumn<?, ?> amountCol;

	@FXML
	private JFXButton addFeeButton;

	@FXML
	private JFXButton editFeeButton;

	@FXML
	private JFXButton deleteFeeButton;

	@FXML
	private VBox revieversPopup;

	@FXML
	private Button closeRevPopupButton;

	@FXML
	private Button clearRevPupupButton;

	@FXML
	private TextField revNameTextField;

	@FXML
	private Label firstNameErrorLabel;

	@FXML
	private TextField revLastNameTextField;

	@FXML
	private Label lastNameErrorLabel;

	@FXML
	private TextField revEmailTextField;

	@FXML
	private Label emailErrorLabel;

	@FXML
	private ComboBox<?> revInstitutionTextField;

	@FXML
	private TextField revContactTextField;

	@FXML
	private PrefixSelectionComboBox<?> revCountryComboBox;

	@FXML
	private JFXButton saveRevButton;

	@FXML
	private VBox feePopup;

	@FXML
	private Button closeFeePopupButton;

	@FXML
	private Button clearFeePopupButton;

	@FXML
	private TextField feePresetTextField;

	@FXML
	private TextField feeCurrencyTextField;

	@FXML
	private TextField feeAmountTextField;

	@FXML
	private JFXButton saveRevButton1;

	@FXML
	private VBox fieldsPopup;

	@FXML
	private Button closeFieldPopupButton;

	@FXML
	private Button clearFieldPopupButton;

	@FXML
	private TextField fieldTextField;

	@FXML
	private JFXButton saveFieldButton;

	@FXML
	void addFee(ActionEvent event) {

	}

	@FXML
	void addField(ActionEvent event) {

	}

	@FXML
	void addReviewer(ActionEvent event) {

	}

	@FXML
	void clearFeePopup(ActionEvent event) {

	}

	@FXML
	void clearFieldPopup(ActionEvent event) {

	}

	@FXML
	void clearRevPopup(ActionEvent event) {

	}

	@FXML
	void closeFeePopup(ActionEvent event) {

	}

	@FXML
	void closeFieldPopup(ActionEvent event) {

	}

	@FXML
	void closeRevPopup(ActionEvent event) {

	}

	@FXML
	void deleteButton(ActionEvent event) {

	}

	@FXML
	void deleteFee(ActionEvent event) {

	}

	@FXML
	void deleteReviewer(ActionEvent event) {

	}

	@FXML
	void editFee(ActionEvent event) {

	}

	@FXML
	void editField(ActionEvent event) {

	}

	@FXML
	void editReviewer(ActionEvent event) {

	}

	@FXML
	void saveField(ActionEvent event) {

	}

	@FXML
	void saveReviewer(ActionEvent event) {

	}

}
