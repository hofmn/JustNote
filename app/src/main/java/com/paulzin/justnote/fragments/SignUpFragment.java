package com.paulzin.justnote.fragments;


import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.paulzin.justnote.NotesActivity;
import com.paulzin.justnote.R;

public class SignUpFragment extends Fragment {


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        final EditText emailEditText = (EditText) rootView.findViewById(R.id.emailEditText);
        final EditText passwordEditText = (EditText) rootView.findViewById(R.id.passwordEditText);

        final Button loginButton = (Button) rootView.findViewById(R.id.logInButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
                        .replace(R.id.container, new SignInFragment())
                        .commit();
            }
        });

        final Button signUpButton = (Button) rootView.findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                ParseUser newUser = new ParseUser();
                newUser.setEmail(email);
                newUser.setUsername(email);
                newUser.setPassword(password);
                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(getActivity(), NotesActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Snackbar.make(
                                    loginButton,
                                    getErrorMessage(e.getCode(),
                                            e.getMessage()), Snackbar.LENGTH_LONG
                            ).show();
                        }
                    }
                });
            }
        });

        final ImageView imageView = (ImageView) rootView.findViewById(R.id.logoImageView);
        imageView.setAlpha(0f);
        imageView.animate().alpha(1f).setDuration(2000).setInterpolator(new LinearInterpolator());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate()
                        .rotationX(360).rotationY(360)
                        .setDuration(1000)
                        .setInterpolator(new LinearInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                imageView.setRotationY(0);
                                imageView.setRotationX(0);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
            }
        });

        return rootView;
    }

    private String getErrorMessage(int code, String defaultMessage) {

        Log.d("SingUp", defaultMessage);

        switch (code) {
            case ParseException.EMAIL_TAKEN:
            case ParseException.USERNAME_TAKEN:
                return "This email is already taken";
            case ParseException.PASSWORD_MISSING:
                return "Please enter your password";
            case ParseException.EMAIL_MISSING:
            case ParseException.USERNAME_MISSING:
                return "Please enter your email";
            case ParseException.INVALID_EMAIL_ADDRESS:
                return "Please enter correct email";
            default:
                return "Please enter your email and password";

        }
    }


}
