package alfatec.view.gui;

import alfatec.controller.author.SearchAuthorsController;
import alfatec.controller.conference.SearchReviewersController;
import alfatec.controller.paper.ScientificWorkPopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ScientificWorkView {

	private static ScientificWorkView instance;

	private ScientificWorkView() {
	}

	public static ScientificWorkView getInstance() {
		if (instance == null)
			synchronized (ScientificWorkView.class) {
				if (instance == null)
					instance = new ScientificWorkView();
			}
		return instance;
	}

	public ScientificWorkPopupController loadAdd(ScientificWorkPopupController controller) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/scientific_work_popup.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initStyle(StageStyle.UNDECORATED);
			controller = fxmlLoader.getController();
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error loading scientific work popup - add.");
			e.printStackTrace();
		}
		return controller;
	}

	public ScientificWorkPopupController loadEdit(ScientificWorkPopupController controller) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/scientific_work_popup.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initStyle(StageStyle.UNDECORATED);
			controller = fxmlLoader.getController();
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error loading scientific work popup - edit.");
			e.printStackTrace();
		}
		return controller;
	}

	public SearchReviewersController loadSearchReviewers(SearchReviewersController controller) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/search_reviewers_popup.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initStyle(StageStyle.UNDECORATED);
			controller = fxmlLoader.getController();
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error loading search reviewers popup.");
			e.printStackTrace();
		}
		return controller;
	}

	public SearchAuthorsController loadSearchAuthors(SearchAuthorsController controller) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/search_authors_popup.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.initStyle(StageStyle.UNDECORATED);
			controller = fxmlLoader.getController();
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error loading search authors popup.");
			e.printStackTrace();
		}
		return controller;
	}
}
