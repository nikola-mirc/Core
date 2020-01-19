package alfatec.view.gui;

import alfatec.controller.author.SearchAuthorsController;
import alfatec.controller.conference.SearchReviewersController;
import alfatec.model.person.Author;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ResearchView {

	private static ResearchView instance;

	private ResearchView() {
	}

	public static ResearchView getInstance() {
		if (instance == null)
			synchronized (ResearchView.class) {
				if (instance == null)
					instance = new ResearchView();
			}
		return instance;
	}

	public SearchAuthorsController loadSearchAuthors(SearchAuthorsController controller) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/search_authors_popup.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			controller = fxmlLoader.getController();
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error search authors.");
			e.printStackTrace();
		}
		return controller;
	}

	public SearchAuthorsController loadSearchAuthors(SearchAuthorsController controller,
			ObservableList<Author> authors) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getClassLoader().getResource("resources/fxml/search_authors_popup.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			controller = fxmlLoader.getController();
			controller.setSelectedAuthors(authors);
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error search authors.");
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
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(new Scene(root));
			controller = fxmlLoader.getController();
			controller.setDisplayStage(stage);
			stage.showAndWait();
		} catch (Exception e) {
			System.out.println("Error search reviewers.");
			e.printStackTrace();
		}
		return controller;
	}
}
