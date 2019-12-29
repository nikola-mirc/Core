package alfatec.controller.paper;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import alfatec.view.gui.ScientificWorkView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ScientificWorkTabController {

	@FXML
	private JFXButton addScientificWorkButton;

	@FXML
	private JFXButton updateScientificWorkButton;

	@FXML
	private TableView<?> scientificWorkTableView;

	@FXML
	private TableColumn<?, ?> authorNameColumn;

	@FXML
	private TableColumn<?, ?> authorLastNameColumn;

	@FXML
	private TableColumn<?, ?> titleColumn;

	@FXML
	private TableColumn<?, ?> statusColumn;

	@FXML
	private TextField searchScientificWorkField;

	private ScientificWorkPopupController ScientificWorkPopupController;

	@FXML
	void addScientificWork(ActionEvent event) throws IOException {
		ScientificWorkPopupController = ScientificWorkView.getInstance().loadAdd(ScientificWorkPopupController);
	}

	@FXML
	void updateScientificWork(ActionEvent event) throws IOException {
		ScientificWorkPopupController = ScientificWorkView.getInstance().loadEdit(ScientificWorkPopupController);
	}

}
