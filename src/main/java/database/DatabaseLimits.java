package database;

public class DatabaseLimits {

	private static final int FIRST_NAME_LENGTH = 30;
	private static final int LAST_NAME_LENGTH = 50;
	private static final int EMAIL_LENGTH = 50;
	private static final int INSTITUTION_NAME_LENGTH = 100;
	private static final int NOTE_LENGTH = 255;
	private static final int PASSWORD_LENGTH = 64;
	private static final int CONTACT_TELEPHONE_LENGTH = 20;
	private static final int RESEARCH_TITLE_LENGTH = 255;
	private static final long BLOB_LENGTH = 16777216;

	public static int getFirstNameLength() {
		return FIRST_NAME_LENGTH;
	}

	public static int getLastNameLength() {
		return LAST_NAME_LENGTH;
	}

	public static int getEmailLength() {
		return EMAIL_LENGTH;
	}

	public static int getInstitutionNameLength() {
		return INSTITUTION_NAME_LENGTH;
	}

	public static int getNoteLength() {
		return NOTE_LENGTH;
	}

	public static int getPasswordLength() {
		return PASSWORD_LENGTH;
	}

	public static int getContactTelephoneLength() {
		return CONTACT_TELEPHONE_LENGTH;
	}

	public static int getResearchTitleLength() {
		return RESEARCH_TITLE_LENGTH;
	}

	public static long getBlobLength() {
		return BLOB_LENGTH;
	}
}
