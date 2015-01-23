package com.paulzin.justnote;

import android.app.Application;

import com.parse.Parse;

public class JustNoteApp extends Application {

    private static final String APPLICATION_ID = "G7zDnLtwmi43906Usoegkz4S5npYVulZOrvrmACX";
    private static final String CLIENT_KEY = "v8aaH92gfAWrMM3POoMoohaDXvkhhnDDrEYRcU9G";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    }
}
