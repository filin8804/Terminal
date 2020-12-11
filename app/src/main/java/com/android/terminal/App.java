package com.android.terminal;

import android.app.Application;

public class App extends Application {
    public static final String TAG = App.class.getSimpleName();
    public static String PACKAGE_NAME;

    public static final float   DEFAULT_Size = 20f;
    public static final boolean DEFAULT_Mono = true;
    public static final String PREF_Size = "size";
    public static final String PREF_Mono = "mono";

    public static final String NOTE_FILENAME = "/notes.txt";

    @Override
    public void onCreate() {
        super.onCreate();
        PACKAGE_NAME = getApplicationContext().getPackageName();
    }
}
