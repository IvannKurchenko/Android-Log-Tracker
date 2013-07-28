package com.logtracking.lib.internal.upload;

import java.io.File;

import com.logtracking.lib.internal.LogContext;
import com.logtracking.lib.internal.IssueReport;

public abstract class LogReportSender {
	
	public interface OnFileSendListener {
		public void onFileSendSuccess(IssueReport issueReport);
		public void onFileSendFail(IssueReport issueReport);
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
            mListener.onFileSendFail(issueReport);
            return false;
        }
        return true;
    }

    protected final void notifyReportSendSuccess(IssueReport issueReport){
        if (mListener != null){
            mListener.onFileSendSuccess(issueReport);
        }
    }

    protected final void notifyReportSendFail(IssueReport issueReport){
        if (mListener != null){
            mListener.onFileSendFail(issueReport);
        }
    }

}
