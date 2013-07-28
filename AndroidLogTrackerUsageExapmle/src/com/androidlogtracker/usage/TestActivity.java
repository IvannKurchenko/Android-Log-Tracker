package com.androidlogtracker.usage;

import com.loglibrarryusage.R;
import com.logtracking.lib.api.Log;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.logtracking.lib.api.ReportIssueDialog;
import com.logtracking.lib.api.SnapshotSaver;

public class TestActivity extends Activity{

	private OnClickListener sendLogClickListener = new  OnClickListener(){
		@Override
		public void onClick(View v) {

			if (mLogThread!=null && mLogThread.isAlive()){
				stopSendingLogMessages();
				mSendLogMessageButton.setText(R.string.start_send_messages);
			} else {
				startSendingLogMessages();
				mSendLogMessageButton.setText(R.string.stop_send_messages);
			}
		}
	};
	
	private OnClickListener reportIssueClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
            ReportIssueDialog.show();
		}
	};
	
	private OnClickListener throwExceptionClickListener = new  OnClickListener(){
		@Override
		public void onClick(View v) {
			throw new TestException("Test exception");		
		}
	};

    private OnClickListener takeSnapshotClickListener = new  OnClickListener(){
        @Override
        public void onClick(View v) {
            SnapshotSaver.saveSnapshot(TestActivity.this);
        }
    };

    private Runnable printLogsAction = new Runnable() {

        private final int MESSAGE_COUNT = 5;

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                sendMessages(Log.ERROR,TestTags.ERROR_TAG);
                sendMessages(Log.DEBUG,TestTags.DEBUG_TAG);
                sendMessages(Log.INFO,TestTags.INFO_TAG);
                sendMessages(Log.VERBOSE,TestTags.VERBOSE_TAG);
            }
        }

        private void sendMessages(int level, String tag){
            for (int i=0;i<MESSAGE_COUNT;i++)  {
                switch (level){

                    case Log.VERBOSE:
                        Log.v(tag, "test message # " + i);
                        break;

                    case Log.DEBUG :
                        Log.d(tag, "test message # " + i);
                        break;

                    case Log.INFO:
                        Log.i(tag, "test message # " + i);
                        break;

                    case Log.WARN:
                        Log.w(tag, "test message # " + i);
                        break;

                    case Log.ERROR:
                        Log.e(tag, "test message # " + i);
                        break;
                }
            }
        }
    };
	
	private volatile Thread mLogThread;
	private Button mSendLogMessageButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		mSendLogMessageButton = (Button) findViewById(R.id.startSendLogMsgButton);
		mSendLogMessageButton.setOnClickListener(sendLogClickListener);
		
		Button throwExceptionButton = (Button) findViewById(R.id.throwExceptionButton);
		throwExceptionButton.setOnClickListener(throwExceptionClickListener);
		
		Button reportIssueButton = (Button) findViewById(R.id.reportButton);
		reportIssueButton.setOnClickListener(reportIssueClickListener);

        Button takeSnapshotButton = (Button) findViewById(R.id.takeSnapshotButton);
        takeSnapshotButton.setOnClickListener(takeSnapshotClickListener);
	}

	private void startSendingLogMessages(){
        mLogThread = new Thread(printLogsAction);
        mLogThread.start();
	}
	
	private void stopSendingLogMessages(){
        mLogThread.interrupt();
	}
}
