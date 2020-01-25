package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.time.DateUtils;

import com.sun.javafx.PlatformUtil;

import util.Folder;

public class BackUpAndRestore {

	private static final int DELETE_FILES_OLDER_THAN = 10;
	private static final String USERNAME = "db.username";
	private static final String PASSWORD = "db.password";
	private static final String DB_URL = "db.url";
	private static String DATABASE_NAME;
	private static Properties properties = null;
	private static BackUpAndRestore instance;

	private BackUpAndRestore() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream("src/main/java/database/database.properties"));
			DATABASE_NAME = properties.getProperty(DB_URL).substring(
					properties.getProperty(DB_URL).lastIndexOf('/') + 1, properties.getProperty(DB_URL).indexOf('?'));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BackUpAndRestore getInstance() {
		if (instance == null)
			synchronized (BackUpAndRestore.class) {
				if (instance == null)
					instance = new BackUpAndRestore();
			}
		return instance;
	}

	public void backUp() throws IOException, InterruptedException, SQLException {
		deleteBackupFilesOlderThen(Folder.getBackupDirectory(), DELETE_FILES_OLDER_THAN);
		String path = Folder.getBackupDirectory().getAbsolutePath()
				.concat(File.separator + "conference".concat(LocalDateTime.now().toString()).concat(".sql"));
		if (PlatformUtil.isLinux()) {
			String executeCommand = "mysqldump -u" + properties.getProperty(USERNAME) + " -p"
					+ properties.getProperty(PASSWORD) + " --add-drop-database -B " + DATABASE_NAME + " -r " + path;
			Process runtimeProcess = Runtime.getRuntime().exec(executeCommand);
			runtimeProcess.waitFor();
		} else if (PlatformUtil.isWindows()) {
			Runtime shell = Runtime.getRuntime();
			Process runtimeProcess;
			if (!properties.getProperty(PASSWORD).isEmpty())
				runtimeProcess = shell.exec(new String[] { "cmd.exe", "/c", "mysqldump",
						"-u".concat(properties.getProperty(USERNAME)), "-p".concat(properties.getProperty(PASSWORD)),
						"startdb", DATABASE_NAME, ">", new File(Folder.getBackupDirectory(), path).getAbsolutePath() });
			else
				runtimeProcess = shell.exec(new String[] { "cmd.exe", "/c", "mysqldump",
						"-u".concat(properties.getProperty(USERNAME)), "startdb", DATABASE_NAME, ">",
						new File(Folder.getBackupDirectory(), path).getAbsolutePath() });
			runtimeProcess.waitFor();
		}
	}

	public void restore(String mysqlDbAbsolutePath) throws IOException, InterruptedException {
		if (PlatformUtil.isLinux()) {
			String[] executeCommand = new String[] { "mysql", DATABASE_NAME,
					"--user=".concat(properties.getProperty(USERNAME)),
					"--password=".concat(properties.getProperty(PASSWORD)), "-e", " source " + mysqlDbAbsolutePath };
			Process runtimeProcess = Runtime.getRuntime().exec(executeCommand);
			runtimeProcess.waitFor();
			System.out.println(runtimeProcess.exitValue());
		} else if (PlatformUtil.isWindows()) {
			Runtime shell = Runtime.getRuntime();
			Process runtimeProcess;
			if (!properties.getProperty(PASSWORD).isEmpty())
				runtimeProcess = shell.exec(new String[] { "cmd.exe", "/c", "mysql",
						"-u".concat(properties.getProperty(USERNAME)), "-p".concat(properties.getProperty(PASSWORD)),
						"startdb", DATABASE_NAME, "<", mysqlDbAbsolutePath });
			else
				runtimeProcess = shell
						.exec(new String[] { "cmd.exe", "/c", "mysql", "-u".concat(properties.getProperty(USERNAME)),
								"startdb", DATABASE_NAME, "<", mysqlDbAbsolutePath });
			runtimeProcess.waitFor();
		}
	}

	private void deleteBackupFilesOlderThen(File backupDirectory, int days) {
		if (backupDirectory.isDirectory()) {
			Collection<File> filesToDelete = FileUtils.listFiles(backupDirectory,
					new AgeFileFilter(DateUtils.addDays(new Date(), days * -1)), TrueFileFilter.TRUE);
			if (!filesToDelete.isEmpty()) {
				File newestFile = filesToDelete.stream().max(Comparator.comparingLong(f -> f.lastModified())).get();
				filesToDelete.remove(newestFile);
			}
			for (File file : filesToDelete)
				FileUtils.deleteQuietly(file);
		}
	}
}
