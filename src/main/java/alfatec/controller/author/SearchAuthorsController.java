package alfatec.controller.author;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXButton;

import alfatec.controller.utils.Utils;
import alfatec.dao.person.AuthorDAO;
import alfatec.model.person.Author;
import alfatec.view.utils.GUIUtils;
import alfatec.view.utils.Utility;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SearchAuthorsController extends GUIUtils {

	@FXML
	private TableView<Author> authorsTableView;

	@FXML
	private TableColumn<Author, String> authorNameColumn, authorLastNameColumn, institutionColumn, emailColumn,
			countryColumn;

	@FXML
	private TextField searchAuthorTextField;

	@FXML
	private JFXButton selectAuthorButton;

	@FXML
	private Button closeButton;

	private Stage display;
	private ObservableList<Author> authors;
	private ObservableList<Author> selectedAuthors;

	@FXML
	public void initialize() {
		authors = AuthorDAO.getInstance().getAllAuthors();
		selectedAuthors = FXCollections.observableArrayList();
		populateTable();
		handleSearch();
	}

	@FXML
	void close(ActionEvent event) {
		display.close();
	}

	@FXML
	void selectAuthor(ActionEvent event) {
		selectedAuthors = Utils.removeDuplicates(selectedAuthors);
		close(event);
	}

	public void setDisplayStage(Stage stage) {
		this.display = stage;
	}

	private void populateTable() {
		authorsTableView.setPlaceholder(new Label("Database table \"author\" is empty"));
		Utility.setUpStringCell(authorsTableView);
		authorsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		authorsTableView.setRowFactory(tv -> {
			TableRow<Author> row = new TableRow<Author>();
			row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
				if (!row.isEmpty() && event.getClickCount() == 1) {
					Author author = row.getItem();
					if (authorsTableView.getSelectionModel().getSelectedItems().contains(author)) {
						int index = row.getIndex();
						authorsTableView.getSelectionModel().clearSelection(index);
						selectedAuthors.remove(author);
					} else {
						authorsTableView.getSelectionModel().select(author);
						selectedAuthors.add(author);
					}
					event.consume();
				}
			});
			return row;
		});
		authorNameColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorFirstNameProperty());
		authorLastNameColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorLastNameProperty());
		emailColumn.setCellValueFactory(cellData -> cellData.getValue().getAuthorEmailProperty());
		institutionColumn.setCellValueFactory(cellData -> {
			var name = cellData.getValue().getInstitutionProperty();
			return Bindings.when(name.isNull()).then("").otherwise(name.asString());
		});
		countryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
		authorsTableView.setItems(authors);
	}

	private void handleSearch() {
		searchAuthorTextField.setOnKeyTyped(event -> {
			String search = searchAuthorTextField.getText();
			Pattern pattern = Pattern.compile("[@()\\\\<>+~%\\*\\-\\'\"]");
			Matcher matcher = pattern.matcher(search);
			if (search.length() > 0 && !matcher.find()) {
				ObservableList<Author> searched = AuthorDAO.getInstance().searchForAuthors(search);
				authorsTableView.getItems().setAll(searched);
			} else {
				authors = AuthorDAO.getInstance().getAllAuthors();
				authorsTableView.getItems().setAll(authors);
			}
		});
	}

	public ObservableList<Author> getSelectedAuthors() {
		return selectedAuthors;
	}

	public void setSelectedAuthors(ObservableList<Author> list) {
		this.selectedAuthors.addAll(list);
		authors.removeAll(selectedAuthors);
	}

}
