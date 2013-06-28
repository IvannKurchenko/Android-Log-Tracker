
package com.logtracking.lib.internal.upload;

import javax.mail.MessagingException;

import com.logtracking.lib.api.LogContext;
import com.logtracking.lib.api.settings.EmailLogSendingSettings;
import com.logtracking.lib.internal.IssueReport;

class EmailLogReportSender extends LogReportSender {

    private EmailSender mEmailSender;
    private String mRecipientList;
    private String mReportSubject;

	public EmailLogReportSender(LogContext logContext) {
		super(logContext);

        EmailLogSendingSettings sendingSettings = (EmailLogSendingSettings) getLogContext().getLogSettings().getSendingSettings();

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
