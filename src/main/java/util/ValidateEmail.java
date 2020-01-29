package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class ValidateEmail {

	private static int hear(BufferedReader input) throws IOException {
		String line = null;
		int response = 0;
		while ((line = input.readLine()) != null) {
			String prefix = line.substring(0, 3);
			try {
				response = Integer.parseInt(prefix);
			} catch (Exception ex) {
				response = -1;
			}
			if (line.charAt(3) != '-')
				break;
		}
		return response;
	}

	private static void say(BufferedWriter writer, String text) throws IOException {
		writer.write(text + "\r\n");
		writer.flush();
		return;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static ArrayList getMX(String hostName) throws NamingException {
		Hashtable environment = new Hashtable();
		environment.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		DirContext initialContext = new InitialDirContext(environment);
		Attributes attributes = initialContext.getAttributes(hostName, new String[] { "MX" });
		Attribute attribute = attributes.get("MX");
		if ((attribute == null) || (attribute.size() == 0)) {
			attributes = initialContext.getAttributes(hostName, new String[] { "A" });
			attribute = attributes.get("A");
			if (attribute == null)
				throw new NamingException("No match for name '" + hostName + "'");
		}
		ArrayList response = new ArrayList();
		NamingEnumeration enumeration = attribute.getAll();
		while (enumeration.hasMore()) {
			String x = (String) enumeration.next();
			String f[] = x.split(" ");
			if (f[1].endsWith("."))
				f[1] = f[1].substring(0, (f[1].length() - 1));
			response.add(f[1]);
		}
		return response;
	}

	@SuppressWarnings({ "resource", "rawtypes" })
	public static boolean isAddressValid(String address) {
		int position = address.indexOf('@');
		if (position == -1)
			return false;
		String domain = address.substring(++position);
		ArrayList mxList = null;
		try {
			mxList = getMX(domain);
		} catch (NamingException ex) {
			return false;
		}
		if (mxList.size() == 0)
			return false;
		for (int mx = 0; mx < mxList.size(); mx++) {
			boolean valid = false;
			try {
				int response;
				Socket socket = new Socket((String) mxList.get(mx), 25);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				response = hear(reader);
				if (response != 220)
					throw new Exception("Invalid header");
				say(writer, "EHLO conference.com");
				response = hear(reader);
				if (response != 250)
					throw new Exception("Not ESMTP");
				say(writer, "MAIL FROM: <loopiaconference@gmail.com>");
				response = hear(reader);
				if (response != 250)
					throw new Exception("Sender rejected");
				say(writer, "RCPT TO: <" + address + ">");
				response = hear(reader);
				say(writer, "RSET");
				hear(reader);
				say(writer, "QUIT");
				hear(reader);
				if (response != 250)
					throw new Exception("Address is not valid!");
				valid = true;
				reader.close();
				writer.close();
				socket.close();
			} catch (Exception ex) {
				// Do nothing but try next host
			} finally {
				if (valid)
					return true;
			}
		}
		return false;
	}

	public List<String> collectInvalid(List<String> addresses) {
		List<String> list = new ArrayList<String>();
		for (String address : addresses)
			if (!isAddressValid(address))
				list.add(address);
		return list;
	}
}
