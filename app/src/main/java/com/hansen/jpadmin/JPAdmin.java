package com.hansen.jpadmin;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Created by POlonde on 9/28/2017.
 */

public class JPAdmin extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
    }
}
