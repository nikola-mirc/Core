package alfatec.view.wrappers;

import alfatec.model.conference.Conference;
import alfatec.model.conference.Dates;

public class ConferenceDateSettings {

	private Conference conference;
	private Dates dates;

	public ConferenceDateSettings(Conference c, Dates d) {
		this.conference = c;
		this.dates = d;
	}

	public Conference getConference() {
		return conference;
	}

	public Dates getDates() {
		return dates;
	}

	public void setConference(Conference conference) {
		this.conference = conference;
	}

	public void setDates(Dates dates) {
		this.dates = dates;
	}
}
