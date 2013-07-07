
package com.logtracking.lib.internal.upload;

import javax.mail.MessagingException;

import com.logtracking.lib.api.config.EmailLogSendingConfiguration;
import com.logtracking.lib.internal.LogContext;
import com.logtracking.lib.internal.IssueReport;

class EmailLogReportSender extends LogReportSender {

    private EmailSender mEmailSender;
    private String mRecipientList;
    private String mReportSubject;

	public EmailLogReportSender(LogContext logContext) {
		super(logContext);

        EmailLogSendingConfiguration sendingSettings = (EmailLogSendingConfiguration) getLogContext().getLogConfiguration().getSendingSettings();

        mEmailSender= new EmailSender(sendingSettings.getEmailHost(),
                                      sendingSettings.getEmailAddress(),
                                      sendingSettings.getEmailPass(),
                                      MAX_SEND_RETRIES);

        mRecipientList = sendingSettings.getEmailRecipients();
        mReportSubject = sendingSettings.getEmailSubject();
	}

	@Override
	public void sendReport(IssueReport issueReport) {
        if ( !checkSendPossibility(issueReport) )
              return;

		try {

            mEmailSender.sendMail(mReportSubject ,
                                  issueReport.getIssueMessage() ,
                                  mRecipientList,
                                  issueReport.getReportFile().getAbsolutePath());

            notifyReportSendSuccess(issueReport);
			
		} catch (MessagingException e) {

            notifyReportSendFail(issueReport);

		}

	}
}
