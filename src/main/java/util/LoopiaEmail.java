package util;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.imap.protocol.FLAGS;

/**
 * Class LoopiaEmail controls sending messages via Loopia server.
 * 
 * @author jelena
 */
public class LoopiaEmail {

	private static final String HOST = "mailcluster.loopia.se";
	private static final String SMTP_PORT = "465";
	private static final String IMAP_PORT = "993";
	private static final String SENT = "INBOX.Sent";
	private static final String BCC_TO = "core@application.java";

	/**
	 * Send email via Loopia server
	 * 
	 * @param user     email
	 * @param password
	 * @param receiver one or more receivers (in format "one,two,three")
	 * @param subject
	 * @param message
	 * @param bcc      true if there is more than one receivers, message will be
	 *                 sent as Blind Carbon Copy
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void sendEmail(String user, String password, String receiver, String subject, String message, boolean bcc)
			throws AddressException, MessagingException {
		Session session = Session.getInstance(createProperties(), new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});
		session.setDebug(true);
		MimeMessage mimeMessage;
		if (bcc) {
			InternetAddress[] addresses = InternetAddress.parse(receiver);
			mimeMessage = createMessage(session, user, addresses, subject, message);
		} else {
			mimeMessage = createMessage(session, user, receiver, subject, message);
		}
		Transport.send(mimeMessage);
		saveSentMessage(session, mimeMessage, user, password);
	}

	private Properties createProperties() {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", HOST);
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", SMTP_PORT);
		properties.put("mail.smtp.ssl.trust", HOST);
		properties.put("mail.store.protocol", "imaps");
		properties.setProperty("mail.imap.ssl.enable", "true");
		properties.put("mail.imap.socketFactory.fallback", "false");
		properties.put("mail.imap.socketFactory.port", IMAP_PORT);
		return properties;
	}

	private MimeMessage createMessage(Session session, String sender, String receiver, String subject,
			String textOfMessage) throws MessagingException {
		MimeMessage mimeMessage = setUp(session, sender, subject, textOfMessage);
		mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
		return mimeMessage;
	}

	private MimeMessage createMessage(Session session, String sender, Address[] receiver, String subject,
			String content) throws MessagingException {
		MimeMessage mimeMessage = setUp(session, sender, subject, content);
		mimeMessage.setRecipients(Message.RecipientType.TO, BCC_TO);
		mimeMessage.setRecipients(Message.RecipientType.BCC, receiver);
		return mimeMessage;
	}

	private MimeMessage setUp(Session session, String sender, String subject, String content)
			throws MessagingException {
		MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setFrom(new InternetAddress(sender));
		mimeMessage.addHeader("Content-type", "text/HTML; charset=UTF-8");
		mimeMessage.addHeader("format", "flowed");
		mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");
		mimeMessage.setSubject(subject, "UTF-8");
		mimeMessage.setText(content, "UTF-8");
		return mimeMessage;
	}

	/**
	 * By default, server doesn't save sent messages from the application. This
	 * method appends it to the Sent folder.
	 * 
	 * @param session  active mail session
	 * @param message  refers to sent message
	 * @param user     email
	 * @param password
	 * @throws MessagingException
	 */
	private void saveSentMessage(Session session, Message message, String user, String password)
			throws MessagingException {
		Store store = session.getStore();
		store.connect(HOST, user, password);
		Folder folder = (Folder) store.getFolder(SENT);
		if (!folder.exists()) {
			folder.create(Folder.HOLDS_MESSAGES);
		}
		folder.open(Folder.READ_WRITE);
		folder.appendMessages(new Message[] { message });
		message.setFlag(FLAGS.Flag.RECENT, true);
		store.close();
	}
}
