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

	private static File getDirectory(final String name) throws IOException {
		String directory = System.getProperty("user.home").concat(File.separator + name);
		File file = createOrRetrieve(directory);
		file.deleteOnExit();
		return file;
	}

	public static File getResearchDirectory() throws IOException {
		return getDirectory(RESEARCH_DIRECTORY);
	}

	public static File getBackupDirectory() throws IOException {
		String directory = System.getProperty("user.home").concat(File.separator + BACKUP_DIRECTORY);
		return createOrRetrieve(directory);
	}

	public static File getConferenceDirectory() throws IOException {
		return getDirectory(RAPORT_DIRECTORY);
	}
}
