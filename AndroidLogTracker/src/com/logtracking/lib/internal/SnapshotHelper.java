package com.logtracking.lib.internal;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import com.logtracking.lib.api.config.LogConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SnapshotHelper {

    private static class SaveAction implements Runnable{

        private Bitmap mSnapshot;
        private File mPath;
        private Runnable mClearAction;
        private Handler mClearHandler;
        private LogConfiguration mLogConfiguration;

        SaveAction(LogConfiguration logConfiguration, Bitmap snapshot, File path, Runnable clearAction, Handler clearHandler){
            mLogConfiguration = logConfiguration;
            mSnapshot = snapshot;
            mPath = path;
            mClearAction = clearAction;
            mClearHandler = clearHandler;
        }

        @Override
        public void run() {

            if(mSnapshot == null || mSnapshot.isRecycled()) {
                return;
            }

            FileOutputStream out = null;

            try {

                File snapshotDirectory = mPath.getAbsoluteFile().getParentFile();
                if(!snapshotDirectory.exists()){
                    snapshotDirectory.mkdirs();
                }

                out = new FileOutputStream(mPath);
                mSnapshot.compress(mLogConfiguration.getSnapshotFormat(), mLogConfiguration.getSnapshotQuality(), out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } finally {

                if(out != null){
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                mClearHandler.post(mClearAction);
            }
        }
    }

    private static class ClearViewCacheAction implements Runnable {

        private View mView;

        ClearViewCacheAction(View view){
            mView = view;
        }

        @Override
        public void run() {
            mView.destroyDrawingCache();
            mView.setDrawingCacheEnabled(false);
        }
    }

    private static final String FILE_NAME_SEPARATOR = System.getProperty("file.separator");

    private LogConfiguration mLogConfiguration;
    private ExecutorService mSavingService;
    private Handler mUiHandler;

    public SnapshotHelper(LogConfiguration logConfiguration){
        mLogConfiguration = logConfiguration;
        mSavingService = Executors.newFixedThreadPool(2);
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    public void saveSnapshot(String name, Window currentWindow){

        if(!mLogConfiguration.isSnapshotSavingEnable())  {
            return;
        }

        View rootView = currentWindow.getDecorView();
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache(true);
        Bitmap screenShot = rootView.getDrawingCache();
        File snapshotFile = new File(mLogConfiguration.getSnapshotDirectoryName() + FILE_NAME_SEPARATOR + name + "."+ Bitmap.CompressFormat.PNG.name());
        ClearViewCacheAction clearAction = new ClearViewCacheAction(rootView);
        SaveAction saveAction = new SaveAction(mLogConfiguration, screenShot, snapshotFile, clearAction, mUiHandler);
        mSavingService.execute(saveAction);
    }

    void removeSnapshots(){
        List<File> allCurrentSnapshots = Arrays.asList( new File(mLogConfiguration.getSnapshotDirectoryName()).listFiles() );
        for(File snapshotToRemove : allCurrentSnapshots){
            snapshotToRemove.delete();
        }
    }
}
