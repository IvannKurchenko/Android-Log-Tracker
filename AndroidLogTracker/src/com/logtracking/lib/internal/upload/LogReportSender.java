package com.logtracking.lib.internal.upload;

import java.io.File;

import android.content.Context;

import com.logtracking.lib.api.LogContext;
import com.logtracking.lib.api.settings.LogSettings;
import com.logtracking.lib.internal.IssueReport;

public abstract class LogReportSender {
	
	public interface OnFileSendListener {
		public void onFileSendSuccess(File file);
		public void onFileSendFail(File file);
	}

    protected static final int MAX_SEND_RETRIES = 3;

	private OnFileSendListener mListener;
    private LogContext mLogContext;
	
	public LogReportSender(LogContext logContext) {
        mLogContext = logContext;
	}

    public abstract void sendReport(IssueReport issueReport);
	
	public void setListener(OnFileSendListener listener){
        mListener = listener;
	}

    protected LogContext getLogContext(){
        return mLogContext;
    }

    protected final boolean checkSendPossibility(IssueReport issueReport){
        if(!ConnectionChecker.isNetworkConnected(mLogContext)){
            mListener.onFileSendFail(issueReport.getReportFile());
            return false;
        }
        return true;
    }

    protected final void notifyReportSendSuccess(IssueReport issueReport){
        if (mListener != null){
            mListener.onFileSendSuccess(issueReport.getReportFile());
        }
    }

    protected final void notifyReportSendFail(IssueReport issueReport){
        if (mListener != null){
            mListener.onFileSendFail(issueReport.getReportFile());
        }
    }

}
