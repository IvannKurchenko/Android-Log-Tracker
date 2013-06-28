package com.logtracking.lib.internal.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

class EmailSender extends Authenticator {

    static {   
        Security.addProvider(new JSSEProvider());   
    }

    private String mUser;
    private String mPassword;
    private Session mSession;

    private int mSendRetriesCount;

    public EmailSender(String emailHost , String user, String password, int retriesCount) {
        mUser = user;
        this.mPassword = password;

        Properties props = new Properties();   
        props.setProperty("mail.transport.protocol", "smtp");   
        props.setProperty("mail.host", emailHost);   
        props.put("mail.smtp.auth", "true");   
        props.put("mail.smtp.port", "465");   
        props.put("mail.smtp.socketFactory.port", "465");   
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");   
        props.put("mail.smtp.socketFactory.fallback", "false");   
        props.setProperty("mail.smtp.quitwait", "false");   

        mSession = Session.getDefaultInstance(props, this);
        mSendRetriesCount = retriesCount;
    }   

    protected PasswordAuthentication getPasswordAuthentication() {   
        return new PasswordAuthentication(mUser, mPassword);
    }   

	public void sendMail(String subject, String body, String recipients, String attachment) throws MessagingException {

		MimeMessage message = new MimeMessage(mSession);
		DataHandler handler = new DataHandler(new ByteArrayDataSource(
				body.getBytes(), "text/plain"));
		message.setSender(new InternetAddress(mUser));
		message.setSubject(subject);
		message.setDataHandler(handler);

		if (recipients.indexOf(',') > 0)
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(recipients));
		else
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(
					recipients));

		Multipart multipart = new MimeMultipart();
		MimeBodyPart messageBodyPart = new MimeBodyPart();

		DataSource source = new FileDataSource(attachment);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(attachment);
		multipart.addBodyPart(messageBodyPart);
		message.setContent(multipart);

        sendMailWithRetries(message);
	}

    private void sendMailWithRetries(MimeMessage message) throws MessagingException{
        MessagingException exception = null;
        for(int i=1;i<=mSendRetriesCount;i++){
            try {
                Transport.send(message);
                return;
            } catch (MessagingException e) {
                exception = e;
            }
        }
        throw exception;
    }

    public class ByteArrayDataSource implements DataSource {   
        private byte[] data;   
        private String type;   

        public ByteArrayDataSource(byte[] data, String type) {   
            super();   
            this.data = data;   
            this.type = type;   
        }   

        public ByteArrayDataSource(byte[] data) {   
            super();   
            this.data = data;   
        }   

        public void setType(String type) {   
            this.type = type;   
        }   

        public String getContentType() {   
            if (type == null)   
                return "application/octet-stream";   
            else  
                return type;   
        }   

        public InputStream getInputStream() throws IOException {   
            return new ByteArrayInputStream(data);   
        }   

        public String getName() {   
            return "ByteArrayDataSource";   
        }   

        public OutputStream getOutputStream() throws IOException {   
            throw new IOException("Not Supported");   
        }   
    }   
}  