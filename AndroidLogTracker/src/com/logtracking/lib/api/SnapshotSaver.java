package com.logtracking.lib.api;


import android.app.Activity;
import android.view.Window;
import com.logtracking.lib.api.config.LogConfiguration;
import com.logtracking.lib.internal.SnapshotHelper;

/**
 * Util class for taking and saving snapshots of current visible screen.
 * Taken snapshot will be saved in file with given name.
 * Saved snapshots will be attached to issue report with other files.
 * Note: if snapshot with given name already exist it will be override.
 *
 * @see LogConfiguration.LogConfigurationBuilder#setSnapshotSavingEnable(boolean)
 * @see LogConfiguration.LogConfigurationBuilder#setSnapshotQuality(int)
 * @see LogConfiguration.LogConfigurationBuilder#setSnapshotFormat(android.graphics.Bitmap.CompressFormat)
 */
public class SnapshotSaver {

    private static SnapshotHelper sSnapshotHelper;

    static void init(SnapshotHelper snapshotHelper){
        sSnapshotHelper =   snapshotHelper;
    }

    /**
     * Save snapshot of current visible {@link android.app.Activity} with name
     * of this {@link android.app.Activity} class.
     * Should be called from UI thread.
     *
     * @param currentActivity current visible {@link android.app.Activity}.
     */
    public static void saveSnapshot(Activity currentActivity){
        saveSnapshot(currentActivity.getClass().getSimpleName(), currentActivity);
    }

    /**
     * Save snapshot of current visible {@link android.app.Activity} with given name.
     * Should be called from UI thread.
     *
     * @param name name of snapshot.
     * @param currentActivity current visible {@link android.app.Activity}
     */
    public static void saveSnapshot(String name, Activity currentActivity){
        saveSnapshot(name, currentActivity.getWindow());
    }

    /**
     * Save snapshot of current visible {@link android.view.Window} with given name.
     * Should be called from UI thread.
     *
     * @param name name of snapshot
     * @param currentWindow current visible {@link android.view.Window}
     */
    public static void saveSnapshot(String name, Window currentWindow){
        sSnapshotHelper.saveSnapshot(name, currentWindow);
    }
}
