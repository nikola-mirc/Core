package alfatec.view.wrappers;

import alfatec.model.research.Paperwork;
import alfatec.model.research.Research;

public class PaperworkResearch {

	private Paperwork paperwork;
	private Research research;

	public PaperworkResearch(Paperwork p, Research r) {
		this.paperwork = p;
		this.research = r;
	}

	public Paperwork getPaperwork() {
		return paperwork;
	}

	public Research getResearch() {
		return research;
	}

	public void setPaperwork(Paperwork paperwork) {
		this.paperwork = paperwork;
	}

	public void setResearch(Research research) {
		this.research = research;
	}

}
