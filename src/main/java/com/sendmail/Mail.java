package com.sendmail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.Authenticator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Mail
 */
@WebServlet(urlPatterns = "/mailsender")
public class Mail extends HttpServlet {
	private String email, subject, message;

	private void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.email = request.getParameter("email");
		this.subject = request.getParameter("subject");
		this.message = request.getParameter("message");
		System.out.println(this.email + this.subject + this.message);
		try {
			sendMessage(this.email, this.subject, this.message);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	public static void sendMessage(String email, String subject, String message) throws SQLException {

		// App Password : kgdkyooloceyaqyj

		// Mention the Recipient's email address
		// String to = email;
		String to = "chakrasayanroy@gmail.com";
		// Mention the Sender's email address
		String from = "tapasroy862@gmail.com";
		// Mention the SMTP server address. Below Gmail's SMTP server is being used to
		// send email
		String host = "smtp.gmail.com";

		// Get system properties
		Properties properties = System.getProperties();
		// Setup mail server
		properties.put("mail.smtp.host", host);
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

		// Get the Session object.// and pass username and password
		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("tapasroy862@gmail.com", "kgdkyooloceyaqyj");
			}
		});
		// Used to debug SMTP issues
		session.setDebug(true);
		try {
			URL context = Mail.class.getProtectionDomain().getCodeSource().getLocation();
			String webContentPath = context.getPath();
			File file = new File(webContentPath);
			webContentPath = file.getParent();
			file = new File(webContentPath);
			webContentPath = file.getParent();
			System.out.println(webContentPath);

			// Create a default MimeMessage object.
			MimeMessage message1 = new MimeMessage(session);
			// Set From: header field of the header.
			message1.setFrom(new InternetAddress(from));
			// Set To: header field of the header.
			message1.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			// Set Subject: header field
			message1.setSubject(subject);
			// message1.setText(message);
			message1.setHeader("Content-Type", "text/html; charset=UTF-8");
			Multipart multipart = new MimeMultipart();

			// Load the HTML template
			String template = loadTemplate(webContentPath + "/template.html");
			System.out.println(template);

			// Create a MimeBodyPart for the HTML content
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(template, "text/html");

			// Add the HTML part to the multipart
			multipart.addBodyPart(htmlPart);

			// Set the multipart content as the message content
			message1.setContent(multipart);

			// Now set the actual message
			System.out.println("sending...");
			// Send message
			Transport.send(message1);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	private static String loadTemplate(String path) {
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = br.readLine()) != null) {
				contentBuilder.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}

}
