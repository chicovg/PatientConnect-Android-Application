package com.chicovg.symptommgmt;

import android.app.Application;

/**
 * Created by victorguthrie on 11/30/14.
 */
public class SymptomMgmtApplication extends Application {

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;
}
