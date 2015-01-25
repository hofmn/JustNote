package com.paulzin.justnote.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.paulzin.justnote.R;
import com.paulzin.justnote.data.Note;
import com.paulzin.justnote.data.OnNoteStateChangeListener;

public class EditNoteFragment extends Fragment {

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";
    private static final String ARG_SHOW_KEYBOARD = "showKeyboard";

    OnNoteStateChangeListener callback;

    private Note note;
    private EditText titleEditText;
    private EditText contentEditText;
    private boolean showKeyboard = true;

    private final String LOG_TAG = this.getClass().getCanonicalName();

    public static EditNoteFragment newInstance(String id, String title, String content,
                                               boolean showKeyboard) {
        EditNoteFragment fragment = new EditNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        args.putBoolean(ARG_SHOW_KEYBOARD, showKeyboard);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (OnNoteStateChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " implement interfaces!");
        }
    }

    public EditNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_note, container, false);

        titleEditText = (EditText) rootView.findViewById(R.id.titleEditText);
        contentEditText = (EditText) rootView.findViewById(R.id.contentEditText);

        if (getArguments() != null) {
            note = new Note(getArguments().getString(ARG_ID),
                    getArguments().getString(ARG_TITLE),
                    getArguments().getString(ARG_CONTENT));

            titleEditText.setText(note.getTitle());
            // Place cursor at the end of title
            titleEditText.setSelection(titleEditText.getText().length());
            contentEditText.setText(note.getContent());

            showKeyboard = getArguments().getBoolean(ARG_SHOW_KEYBOARD);
        }

        titleEditText.clearFocus();

        if (showKeyboard) {
            titleEditText.requestFocus();
        }

        getActivity().setTitle(getString(R.string.title_add_note));

        return rootView;
    }

    public void saveNote() {
        String titleToSave = titleEditText.getText().toString().trim();
        String contentToSave = contentEditText.getText().toString().trim();

        if (!contentToSave.isEmpty() || !titleToSave.isEmpty()) {

            if (note == null) {
                ParseObject post = new ParseObject("Post");
                titleToSave += titleToSave.isEmpty() ? "Untitled" : "";
                post.put("title", titleToSave);
                post.put("content", contentToSave);
                post.put("author", ParseUser.getCurrentUser());
                post.saveInBackground();
                callback.onNoteAdded(titleToSave, contentToSave);
            } else {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
                final String newTitleToSave = titleToSave.isEmpty() ? "Untitled" : titleToSave;
                final String newContentToSave = contentToSave;
                query.getInBackground(note.getId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            parseObject.put("title", newTitleToSave);
                            parseObject.put("content", newContentToSave);
                            callback.onNoteChanged(new Note(newTitleToSave, newContentToSave), note);
                            parseObject.saveInBackground();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (showKeyboard) {
            titleEditText.requestFocus();
            showSoftKeyboard(titleEditText);
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard(titleEditText);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        saveNote();
    }
}
