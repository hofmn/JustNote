package com.paulzin.justnote.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.paulzin.justnote.MainActivity;
import com.paulzin.justnote.R;

public class LoginFragment extends Fragment {

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText emailEditText = (EditText) rootView.findViewById(R.id.emailEditText);
        final EditText passwordEditText = (EditText) rootView.findViewById(R.id.passwordEditText);

        CardView loginButton = (CardView) rootView.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                ParseUser.logInInBackground(email, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(),
                                    "Something went wrong...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        ImageView imageView = (ImageView) rootView.findViewById(R.id.logoImageView);
        imageView.setAlpha(0f);
        imageView.animate().alpha(1f).setDuration(500);

        return rootView;
    }
}
