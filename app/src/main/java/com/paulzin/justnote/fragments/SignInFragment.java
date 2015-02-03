package com.paulzin.justnote.fragments;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.paulzin.justnote.MainActivity;
import com.paulzin.justnote.R;

public class SignInFragment extends Fragment {

    public SignInFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);

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
}
