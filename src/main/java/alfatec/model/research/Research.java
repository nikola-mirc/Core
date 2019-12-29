package alfatec.model.research;

import java.io.File;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model for database table "research".
 * 
 * Holds basic info about research.
 * 
 * @author jelena
 *
 */
public class Research {

	private final LongProperty researchID;
	private final StringProperty researchTitle;
	private final ObjectProperty<File> paper;
	private final StringProperty note;

	public Research() {
		this(0, null, null, null);
	}

	public Research(long researchID, String researchTitle, File paper, String note) {
		this.researchID = new SimpleLongProperty(researchID);
		this.researchTitle = new SimpleStringProperty(researchTitle);
		this.paper = new SimpleObjectProperty<File>(paper);
		this.note = new SimpleStringProperty(note);
	}

	public String getNote() {
		return note.get();
	}

	public StringProperty getNoteProperty() {
		return note;
	}

	public File getPaperFile() {
		return paper.get();
	}

	public ObjectProperty<File> getPaperProperty() {
		return paper;
	}

	public long getResearchID() {
		return researchID.get();
	}

	public LongProperty getResearchIDProperty() {
		return researchID;
	}

	public String getResearchTitle() {
		return researchTitle.get();
	}

	public StringProperty getResearchTitleProperty() {
		return researchTitle;
	}

	public void setNote(String note) {
		this.note.set(note);
	}

	public void setPaperFile(File paper) {
		this.paper.set(paper);
	}

	public void setPaperPath(String path) {
		this.paper.set(new File(path));
	}

	public void setResearchID(long id) {
		this.researchID.set(id);
	}

	public void setResearchTitle(String title) {
		this.researchTitle.set(title);
	}
}
