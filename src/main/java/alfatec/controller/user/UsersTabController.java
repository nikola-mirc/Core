package alfatec.controller.user;

import com.jfoenix.controls.JFXButton;

import alfatec.dao.wrappers.UserLoginDAO;
import alfatec.model.enums.RoleEnum;
import alfatec.view.gui.UsersView;
import alfatec.view.utils.Utility;
import alfatec.view.wrappers.UserLoginConnection;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class UsersTabController {

	@FXML
	private TableView<UserLoginConnection> usersTableView;

	@FXML
	private TableColumn<UserLoginConnection, String> userNameColumn;

	@FXML
	private TableColumn<UserLoginConnection, String> userLastNameColumn;

	@FXML
	private TableColumn<UserLoginConnection, String> userEmail;

	@FXML
	private TableColumn<UserLoginConnection, String> userMobileColumn;

	@FXML
	private TableColumn<UserLoginConnection, String> userRoleColumn;

	@FXML
	private TableColumn<UserLoginConnection, String> dateCreatedColumn;

	@FXML
	private TextField searchUserTextField;

	@FXML
	private JFXButton addUserButton;

	@FXML
	private JFXButton editUserButton;

	@FXML
	private JFXButton deleteUserButton;

	private UsersPopupController usersPopupController;
	private ObservableList<UserLoginConnection> users;
	boolean saveClicked;

	@FXML
	private void initialize() {
		users = UserLoginDAO.getInstance().getAllData();
		userNameColumn.setCellValueFactory(cellData -> cellData.getValue().getUser().getUserFirstNameProperty());
		userLastNameColumn.setCellValueFactory(cellData -> cellData.getValue().getUser().getUserLastNameProperty());
		userEmail.setCellValueFactory(cellData -> cellData.getValue().getLoginData().getUserEmailProperty());
		userMobileColumn.setCellValueFactory(cellData -> cellData.getValue().getUser().getContactTelephoneProperty());
		userRoleColumn.setCellValueFactory(cellData -> new ObservableValue<String>() {
			@Override
			public void addListener(InvalidationListener listener) {
			}

			@Override
			public void removeListener(InvalidationListener listener) {
			}

			@Override
			public void addListener(ChangeListener<? super String> listener) {
			}

			@Override
			public void removeListener(ChangeListener<? super String> listener) {
			}

			@Override
			public String getValue() {
				return RoleEnum.getRoleName(cellData.getValue().getLoginData().getRoleIDProperty().get());
			}
		});
		dateCreatedColumn
				.setCellValueFactory(cellData -> cellData.getValue().getUser().getCreatedTimeProperty().asString());
		Utility.setUpStringCell(usersTableView);
		usersTableView.setItems(users);
//		usersTableView.getSelectionModel().selectedItemProperty()
//				.addListener((observable, oldValue, newValue) -> showUser(newValue));
	}

	@FXML
	void addUser(ActionEvent event) {
		usersPopupController = UsersView.getInstance().loadAdd(usersPopupController);
		handleNewUser();
	}

	@FXML
	void deleteUser(ActionEvent event) {

	}

	@FXML
	void editUser(ActionEvent event) {
		usersPopupController = UsersView.getInstance().loadEdit(usersPopupController);
	}

	private void handleNewUser() {
		if (usersPopupController.isSaveClicked()) {
			UserLoginConnection userData = usersPopupController.getNewUser();
			users.add(userData);
		}
	}

}
