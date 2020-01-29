package alfatec.controller.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import alfatec.dao.conference.ConferenceDAO;
import alfatec.dao.person.AuthorDAO;
import alfatec.dao.relationship.ConferenceCallDAO;
import alfatec.model.person.Author;
import alfatec.model.relationship.ConferenceCall;
import javafx.collections.ObservableList;

public class GroupEmail {

	private static final int LOOPIA_HOURLY_LIMIT = 199;
	private List<String> validFirst, invalidFirst, validSecond, invalidSecond, validThird, invalidThird;
	private int first, second, third;

	public GroupEmail() {
	}

	public List<String> getValidFirst() {
		return validFirst;
	}

	public List<String> getInvalidFirst() {
		return invalidFirst;
	}

	public List<String> getValidSecond() {
		return validSecond;
	}

	public List<String> getInvalidSecond() {
		return invalidSecond;
	}

	public List<String> getValidThird() {
		return validThird;
	}

	public List<String> getInvalidThird() {
		return invalidThird;
	}

	public int getNumberOfFirstCalls() {
		return first;
	}

	public int getNumberOfSecondCalls() {
		return second;
	}

	public int getNumberOfThirdCalls() {
		return third;
	}

	public void prepareEmails() {
		if (activeConference() > 0) {
			prepareFirstCall();
			prepareSecondCall();
			prepareThirdCall();
		}
	}

	private int activeConference() {
		if (ConferenceDAO.getInstance().getCurrentConference() == null)
			return -1;
		else
			return ConferenceDAO.getInstance().getCurrentConference().getConferenceID();
	}

	private void prepareFirstCall() {
		validFirst = new ArrayList<String>();
		invalidFirst = new ArrayList<String>();
		List<Author> authors = getEmailsForCall(1);
		first = authors.size();
		List<Author> invalidAuthors = authors;
		List<Author> validAuthors = authors.stream().filter(Author::isValidEmail).collect(Collectors.toList());
		invalidAuthors.removeAll(validAuthors);
		validAuthors.forEach(author -> validFirst.add(author.getAuthorEmail()));
		invalidAuthors.forEach(author -> invalidFirst.add(author.getAuthorEmail()));
	}

	private void prepareSecondCall() {
		validSecond = new ArrayList<String>();
		invalidSecond = new ArrayList<String>();
		List<Author> authors = getEmailsForCall(2);
		second = authors.size();
		List<Author> invalidAuthors = authors;
		List<Author> validAuthors = authors.stream().filter(Author::isValidEmail).collect(Collectors.toList());
		invalidAuthors.removeAll(validAuthors);
		validAuthors.forEach(author -> validSecond.add(author.getAuthorEmail()));
		invalidAuthors.forEach(author -> invalidSecond.add(author.getAuthorEmail()));
	}

	private void prepareThirdCall() {
		validThird = new ArrayList<String>();
		invalidThird = new ArrayList<String>();
		List<Author> authors = getEmailsForCall(3);
		third = authors.size();
		List<Author> invalidAuthors = authors;
		List<Author> validAuthors = authors.stream().filter(Author::isValidEmail).collect(Collectors.toList());
		invalidAuthors.removeAll(validAuthors);
		validAuthors.forEach(author -> validThird.add(author.getAuthorEmail()));
		invalidAuthors.forEach(author -> invalidThird.add(author.getAuthorEmail()));
	}

	private List<Author> getEmailsForCall(int call) {
		ObservableList<ConferenceCall> calls = ConferenceCallDAO.getInstance()
				.getForConference(ConferenceDAO.getInstance().getCurrentConference().getConferenceID());
		List<Author> authors = new ArrayList<Author>();
		switch (call) {
		case 1:
			calls.removeIf(ConferenceCall::isFirstCallSent);
			break;
		case 2:
			calls.removeIf(ConferenceCall::isNotInterested);
			calls.removeIf(ConferenceCall::isFirstCallAnswered);
			calls.removeIf(ConferenceCall::isSecondCallSent);
			break;
		case 3:
			calls.removeIf(ConferenceCall::isNotInterested);
			calls.removeIf(ConferenceCall::isFirstCallAnswered);
			calls.removeIf(ConferenceCall::isSecondCallAnswered);
			calls.removeIf(ConferenceCall::isThirdCallSent);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + call);
		}
		int min = Math.min(LOOPIA_HOURLY_LIMIT, calls.size());
		for (int i = 0; i < min; i++)
			authors.add(AuthorDAO.getInstance().findAuthorByID(calls.get(i).getAuthorID()));
		return authors;
	}

}
