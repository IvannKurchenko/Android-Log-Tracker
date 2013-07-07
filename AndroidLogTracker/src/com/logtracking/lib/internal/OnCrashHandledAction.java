package com.logtracking.lib.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.logtracking.lib.api.config.LogConfiguration;
import com.logtracking.lib.api.config.LogConfiguration.AfterCrashAction;

import java.util.EnumMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


interface OnCrashHandledAction {

	void onCrashHandled(Context applicationContext);

    static class ActionResolver {
        private static Map<AfterCrashAction,CrashHandlingAction> sActions;
        static {
            sActions = new EnumMap<AfterCrashAction,CrashHandlingAction>(AfterCrashAction.class);
            sActions.put(AfterCrashAction.CLOSE_APPLICATION,CrashHandlingAction.CLOSE_APP_ACTION);
            sActions.put(AfterCrashAction.RELAUNCH_APPLICATION,CrashHandlingAction.RELAUNCH_APP_ACTION);
        }

        public static OnCrashHandledAction getAction(LogConfiguration configuration){
            return sActions.get(configuration.getAfterCrashAction());
        }
    }

    enum CrashHandlingAction implements OnCrashHandledAction {

        CLOSE_APP_ACTION {
            @Override
            public void onCrashHandled(Context applicationContext) {
                startLaunchIntent(applicationContext, FLAG_ACTIVITY_CLEAR_TOP);
            }
        },

        RELAUNCH_APP_ACTION {
            @Override
            public void onCrashHandled(Context applicationContext) {
                startLaunchIntent(applicationContext, FLAG_ACTIVITY_CLEAR_TOP , FLAG_ACTIVITY_NEW_TASK);
            }

        };

        private static void startLaunchIntent(Context applicationContext, int... intentFlags){
            PackageManager pm = applicationContext.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(applicationContext.getPackageName());
            if (intent != null){
                for (int flag : intentFlags)
                    intent.addFlags(flag);
                applicationContext.startActivity(intent);
            }
        }
    }
}