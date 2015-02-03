package com.paulzin.justnote;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.paulzin.justnote.fragments.SignInFragment;


public class AuthActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SignInFragment())
                    .commit();
        }
    }

}
