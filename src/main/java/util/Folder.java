package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Folder {

	private static final String RESEARCH_DIRECTORY = "research";
	private static final String BACKUP_DIRECTORY = "backUp-conference_database";
	private static final String RAPORT_DIRECTORY = "conference_raport";

	private static File createOrRetrieve(final String target) throws IOException {
		final Path path = Paths.get(target);
		if (Files.notExists(path)) {
			File file = Files.createDirectories(path).toFile();
			file.setExecutable(true, false);
			file.setReadable(true, false);
			file.setWritable(true, false);
			return file;
		}
		return path.toFile();
	}

	public static File getResearchDirectory() throws IOException {
		String directory = System.getProperty("user.home").concat(File.separator + RESEARCH_DIRECTORY);
		return createOrRetrieve(directory);
	}

	public static File getBackupDirectory() throws IOException {
		String directory = System.getProperty("user.home").concat(File.separator + BACKUP_DIRECTORY);
		return createOrRetrieve(directory);
	}

	public static File getConferenceDirectory() throws IOException {
		String directory = System.getProperty("user.home").concat(File.separator + RAPORT_DIRECTORY);
		return createOrRetrieve(directory);
	}
}
