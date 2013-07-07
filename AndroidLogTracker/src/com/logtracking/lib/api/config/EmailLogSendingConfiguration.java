package com.logtracking.lib.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Class represents config of sending issue report by e-mail.
 *  Default supported e-mail server is GMAIL, for supporting other servers for sending , you should
 *  set correct "e-mail host" parameter for server you use.
 */
public final class EmailLogSendingConfiguration implements LogSendingConfiguration {

	private static final String DEFAULT_MAIL_SUBJECT = "Android Logtracking Library : Bug report";
	
	private static final String GMAIL_HOST = "smtp.gmail.com";
	
	private String mEmailHost = GMAIL_HOST;
	private String mEmailUserAddress;
	private String mEmailUserPass;
	private List<String> mEmailRecipients;
	private String mEmailSubject = DEFAULT_MAIL_SUBJECT;

    /**
     * @param emailAddress address of email from log-file should be send
     * @param pass  pass to account of email from log-file should be send
     * @param emailRecipients email addresses for sending bug reports
     */
    public EmailLogSendingConfiguration(String emailAddress, String pass, String... emailRecipients){
        mEmailUserAddress = emailAddress;
        mEmailUserPass = pass;
        mEmailRecipients = new ArrayList<String>(Arrays.asList(emailRecipients));
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public SendingService getServiceType() {
		return SendingService.E_MAIL;
	}

    /**
     * Return address of email from log-file should be send
     * @return address of email from log-file should be send
     */
	public String getEmailAddress(){
		return mEmailUserAddress;
	}

    /**
     * Return pass to account of email from log-file should be send
     *
     * @return pass to account of email from log-file should be send
     */
	public String getEmailPass(){
		return mEmailUserPass;
	}
	
	/**
	 * Set host of email server of current email account.
	 * By default supported "GMAIL" host.
     * @param emailHost host
	 */
	public void setEmailHost(String emailHost){
		this.mEmailHost = emailHost;
	}

    /**
     * Return host of email server of current email account.
     *
     * @return host of email server of current email account.
     */
	public String getEmailHost(){
		return mEmailHost;
	}
	
	/**
	 * Add address to whom will be log-files send.
	 * @param emailAddress of recipient.
	 */
	public void addEmailRecipient(String emailAddress){
	    mEmailRecipients.add(emailAddress);
	}

    /**
     * Return comma separated of bug report recipients.
     *
     * @return comma separated list of add  address to whom will be log-files send.
     */
    public String getEmailRecipients(){
        StringBuilder recipients = new StringBuilder();
        for (String recipient : mEmailRecipients){
            recipients.append(recipient);
            recipients.append(",");
        }
        return recipients.toString();
    }
	
	/**
	 * Set subject of mail with issue report.
	 * @param emailSubject subject of mail with issue report.
	 */
	public void setEmailSubject(String emailSubject){
		mEmailSubject = emailSubject;
	}

    /**
     * Return subject of mail with issue report.
     * @return  subject of mail with issue report.
     */
	public String getEmailSubject(){
		return mEmailSubject;
	}
}
