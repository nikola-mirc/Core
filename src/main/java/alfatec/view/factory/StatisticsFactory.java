package alfatec.view.factory;

import java.util.List;
import java.util.stream.Collectors;

import alfatec.dao.conference.CollectionDAO;
import alfatec.dao.conference.EmailDAO;
import alfatec.dao.conference.RegistrationFeeDAO;
import alfatec.dao.conference.SpecialIssueDAO;
import alfatec.dao.relationship.ConferenceCallDAO;
import alfatec.model.conference.Collection;
import alfatec.model.conference.Conference;
import alfatec.model.conference.EmailHelper;
import alfatec.model.conference.RegistrationFee;
import alfatec.model.conference.SpecialIssue;
import alfatec.model.relationship.ConferenceCall;
import alfatec.view.wrappers.ScientificWork;
import javafx.collections.ObservableList;

public class StatisticsFactory {

	private ObservableList<EmailHelper> helper;
	private ObservableList<ScientificWork> works;
	private ObservableList<Collection> collection;
	private ObservableList<SpecialIssue> special;
	private ObservableList<ConferenceCall> calls;

	public StatisticsFactory(Conference conference) {
		helper = EmailDAO.getInstance().getDataForConference(conference);
		works = ResearchFactory.getInstance().getAllForConference(conference);
		collection = CollectionDAO.getInstance().getAllForConference(conference.getConferenceID());
		special = SpecialIssueDAO.getInstance().getFromConference(conference.getConferenceID());
		calls = ConferenceCallDAO.getInstance().getForConference(conference.getConferenceID());
	}

	private int getInvitesSent(int call) {
		return helper.stream().filter(email -> email.getOrdinal() == call).mapToInt(EmailHelper::getCount).sum();
	}

	private int getNumberOfResearches() {
		return works.size();
	}

	private int getSizeOfCollection() {
		return collection.size();
	}

	private int getSizeOfSpecial() {
		return special.size();
	}

	private double sumMoney(String currency) {
		double money = 0;
		List<ScientificWork> swList = works.stream()
				.filter(x -> x.getPaperworkResearch().getPaperwork().getFeePaid() == 1).collect(Collectors.toList());
		for (ScientificWork work : swList) {
			RegistrationFee fee = RegistrationFeeDAO.getInstance()
					.getRegistration(work.getPaperworkResearch().getPaperwork().getRegistrationFeeID());
			if (fee.getCurrencyString().equalsIgnoreCase(currency))
				money += fee.getRegistrationPriceDouble();
		}
		return money;
	}

	private int getNumberOfPositiveFirstAnswers() {
		return calls.stream().filter(ConferenceCall::isFirstCallAnswered).collect(Collectors.toList()).size();
	}

	private int getNumberOfPositiveSecondAnswers() {
		return calls.stream().filter(ConferenceCall::isSecondCallAnswered).collect(Collectors.toList()).size();
	}

	private int getNumberOfPositiveThirdAnswers() {
		return calls.stream().filter(ConferenceCall::isThirdCallAnswered).collect(Collectors.toList()).size();
	}

	public String createStats() {
		return String.format(
				"First calls\n\tsent: %d\n\tanswered: %d\n" + "Second calls\n\tsent: %d\n\tanswered: %d\n"
						+ "Third calls\n\tsent: %d\n\tanswered: %d\n\n" + "Number of researches: %d\n"
						+ "Size of Collection: %d\n" + "Size of Special Issue: %d\n\n"
						+ "Total in RSD: %s\nTotal in EUR: %s\nTotal in USD: %s",
				getInvitesSent(1), getNumberOfPositiveFirstAnswers(), getInvitesSent(2),
				getNumberOfPositiveSecondAnswers(), getInvitesSent(3), getNumberOfPositiveThirdAnswers(),
				getNumberOfResearches(), getSizeOfCollection(), getSizeOfSpecial(), sumMoney("RSD"), sumMoney("EUR"),
				sumMoney("USD"));
	}
}
