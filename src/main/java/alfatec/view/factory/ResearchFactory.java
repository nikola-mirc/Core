package alfatec.view.factory;

import alfatec.controller.utils.Utils;
import alfatec.dao.person.AuthorDAO;
import alfatec.dao.relationship.AuthorResearchDAO;
import alfatec.dao.research.PaperworkDAO;
import alfatec.dao.research.ResearchDAO;
import alfatec.model.enums.Institution;
import alfatec.model.person.Author;
import alfatec.model.relationship.AuthorResearch;
import alfatec.model.research.Research;
import alfatec.view.wrappers.PaperworkResearch;
import alfatec.view.wrappers.ScientificWork;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ResearchFactory {

	private static ResearchFactory instance;

	private ResearchFactory() {
	}

	public static ResearchFactory getInstance() {
		if (instance == null)
			synchronized (ResearchFactory.class) {
				if (instance == null)
					instance = new ResearchFactory();
			}
		return instance;
	}

	public ObservableList<ScientificWork> getAllData() {
		ObservableList<ScientificWork> united = FXCollections.observableArrayList();
		for (Research research : ResearchDAO.getInstance().getAllResearches())
			united.add(new ScientificWork(getAuthorsForResearch(research), new PaperworkResearch(
					PaperworkDAO.getInstance().getPaperworkForResearch(research.getResearchID()), research)));
		return united;
	}

	public ObservableList<Author> getAuthorsForResearch(Research research) {
		ObservableList<Author> authors = FXCollections.observableArrayList();
		ObservableList<AuthorResearch> forResearch = AuthorResearchDAO.getInstance()
				.getAllEntriesForResearch(research.getResearchID());
		for (AuthorResearch ar : forResearch)
			authors.add(AuthorDAO.getInstance().findAuthorByID(ar.getAuthorID()));
		return authors;
	}

	public ObservableList<Research> getResearchesForAuthor(Author author) {
		ObservableList<Research> authors = FXCollections.observableArrayList();
		ObservableList<AuthorResearch> forResearch = AuthorResearchDAO.getInstance()
				.getAllEntriesForAuthor(author.getAuthorID());
		for (AuthorResearch ar : forResearch)
			authors.add(ResearchDAO.getInstance().getResearch(ar.getResearchID()));
		return authors;
	}

	public ObservableList<ScientificWork> searchResearches(String startTyping) {
		ObservableList<ScientificWork> united = FXCollections.observableArrayList();
		for (Research research : ResearchDAO.getInstance().getResearch(startTyping))
			united.add(new ScientificWork(getAuthorsForResearch(research), new PaperworkResearch(
					PaperworkDAO.getInstance().getPaperworkForResearch(research.getResearchID()), research)));
		return united;
	}

	public ObservableList<ScientificWork> searchAuthors(String startTyping) {
		ObservableList<ScientificWork> united = FXCollections.observableArrayList();
		for (Author author : AuthorDAO.getInstance().searchForAuthors(startTyping)) {
			ObservableList<Research> researches = getResearchesForAuthor(author);
			for (Research research : researches) {
				ObservableList<Author> allAuthorsOfResearch = getAuthorsForResearch(research);
				united.add(new ScientificWork(allAuthorsOfResearch, new PaperworkResearch(
						PaperworkDAO.getInstance().getPaperworkForResearch(research.getResearchID()), research)));
			}
		}
		return united;
	}

	public ObservableList<ScientificWork> searchBothAuthorsAndResearches(String startTyping) {
		ObservableList<ScientificWork> united = FXCollections.observableArrayList();
		united.addAll(searchAuthors(startTyping));
		united.addAll(searchResearches(startTyping));
		return Utils.removeDuplicates(united);
	}

	public ObservableList<ScientificWork> searchAuthorsByInstitutionType(Institution institution) {
		ObservableList<ScientificWork> united = FXCollections.observableArrayList();
		for (Author author : AuthorDAO.getInstance().getAuthorsForInstitutionType(institution)) {
			ObservableList<Research> researches = getResearchesForAuthor(author);
			for (Research research : researches) {
				ObservableList<Author> allAuthorsOfResearch = getAuthorsForResearch(research);
				united.add(new ScientificWork(allAuthorsOfResearch, new PaperworkResearch(
						PaperworkDAO.getInstance().getPaperworkForResearch(research.getResearchID()), research)));
			}
		}
		return united;
	}

	public ObservableList<ScientificWork> searchBothAuthorsAndResearchesByInstitutionType(Institution institution) {
		ObservableList<ScientificWork> united = FXCollections.observableArrayList();
		united.addAll(searchAuthorsByInstitutionType(institution));
		// united.addAll(searchResearches(startTyping));
		return Utils.removeDuplicates(united);
	}

//	public ObservableList<ScientificWork> searchAuthorsByInstitutionName(String startTyping) {
//		ObservableList<ScientificWork> united = FXCollections.observableArrayList();
//		for (Author author : AuthorDAO.getInstance().getAuthorsForInstitutionName(startTyping)) {
//			ObservableList<Research> researches = getResearchesForAuthor(author);
//			for (Research research : researches) {
//				ObservableList<Author> allAuthorsOfResearch = getAuthorsForResearch(research);
//				united.add(new ScientificWork(allAuthorsOfResearch, new PaperworkResearch(
//						PaperworkDAO.getInstance().getPaperworkForResearch(research.getResearchID()), research)));
//			}
//		}
//		return united;
//	}
//
//	public ObservableList<ScientificWork> searchBothAuthorsAndResearchesByInstitutionName(String startTyping) {
//		ObservableList<ScientificWork> united = FXCollections.observableArrayList();
//		united.addAll(searchAuthorsByInstitutionName(startTyping));
//		// united.addAll(searchResearches(startTyping));
//		return Utils.removeDuplicates(united);
//	}

	public ObservableList<ScientificWork> searchAuthorsByCountry(String country) {
		ObservableList<ScientificWork> united = FXCollections.observableArrayList();
		for (Author author : AuthorDAO.getInstance().getAuthorsFrom(country)) {
			ObservableList<Research> researches = getResearchesForAuthor(author);
			for (Research research : researches) {
				ObservableList<Author> allAuthorsOfResearch = getAuthorsForResearch(research);
				united.add(new ScientificWork(allAuthorsOfResearch, new PaperworkResearch(
						PaperworkDAO.getInstance().getPaperworkForResearch(research.getResearchID()), research)));
			}
		}
		return united;
	}

	public ObservableList<ScientificWork> searchBothAuthorsAndResearchesByCountry(String country) {
		ObservableList<ScientificWork> united = FXCollections.observableArrayList();
		united.addAll(searchAuthorsByCountry(country));
		// united.addAll(searchResearches(startTyping));
		return Utils.removeDuplicates(united);
	}

}
