package alfatec.view.wrappers;

import java.util.Objects;
import java.util.stream.Collectors;

import alfatec.model.person.Author;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Wrapper class to represent all authors of an research
 * 
 * @author jelena
 *
 */
public class ScientificWork {

	private ObjectProperty<ObservableList<Author>> authors;
	private ObjectProperty<PaperworkResearch> research;
	private StringProperty collectAuthors;

	public ScientificWork(ObservableList<Author> authors, PaperworkResearch research) {
		this.authors = new SimpleObjectProperty<ObservableList<Author>>(authors);
		this.research = new SimpleObjectProperty<PaperworkResearch>(research);
		this.collectAuthors = new SimpleStringProperty();
		setAuthorsString(authors);
	}

	public ObservableList<Author> getAuthors() {
		return authors.get();
	}

	public void setAuthors(ObservableList<Author> authors) {
		this.authors.set(authors);
		setAuthorsString(authors);
	}

	public ObjectProperty<ObservableList<Author>> getAuthorsProperty() {
		return authors;
	}

	public PaperworkResearch getPaperworkResearch() {
		return research.get();
	}

	public void setPaperworkResearch(PaperworkResearch research) {
		this.research.set(research);
	}

	public ObjectProperty<PaperworkResearch> getResearchProperty() {
		return research;
	}

	public String getAuthorsString() {
		return collectAuthors.get();
	}

	private void setAuthorsString(ObservableList<Author> authors) {
		if (authors != null)
			this.collectAuthors.set(authors.stream().collect(Collectors.toList()).stream()
					.map(author -> author.getAuthorFirstName().concat(" ").concat(author.getAuthorLastName()))
					.collect(Collectors.joining(",\n")));
	}

	public StringProperty getAuthorsStringProperty() {
		return collectAuthors;
	}

	public void addAuthor(Author author) {
		if (authors != null && authors.get() != null && authors.get().size() > 0)
			this.authors.get().add(author);
		else {
			authors = new SimpleObjectProperty<ObservableList<Author>>(FXCollections.observableArrayList());
			authors.get().add(author);
		}
		setAuthorsString(getAuthors());
	}

	public void removeAuthor(Author author) {
		this.authors.get().remove(author);
		setAuthorsString(getAuthors());
	}

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (!(object instanceof ScientificWork))
			return false;
		ScientificWork united = (ScientificWork) object;
		return united.getPaperworkResearch().getResearch().getResearchIDProperty()
				.isEqualTo(getPaperworkResearch().getResearch().getResearchIDProperty()).get();
	}

	@Override
	public int hashCode() {
		return Objects.hash(research.get().getResearch().getResearchID());
	}
}
