package com.paulzin.justnote.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.paulzin.justnote.R;
import com.paulzin.justnote.data.Note;
import com.paulzin.justnote.data.OnNoteStateChangeListener;
import com.paulzin.justnote.ui.ItemListener;
import com.paulzin.justnote.ui.NotesAdapter;
import com.paulzin.justnote.ui.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class NotesFragment extends Fragment implements ItemListener {
    private final String LOG_TAG = getClass().getCanonicalName();

    private OnNoteStateChangeListener callback;

    private ArrayList<Note> notes;
    private View listLoadingProgressBar;
    private NotesAdapter notesAdapter;

    private RecyclerView notesRecyclerView;
    private View emptyView;

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            if (context instanceof Activity) {
                callback = (OnNoteStateChangeListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " implement interfaces!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        listLoadingProgressBar = rootView.findViewById(R.id.listLoadingProgressBar);
        emptyView = rootView.findViewById(R.id.emptyView);

        if (notes != null && notes.isEmpty()) {
            showEmptyView();
        }

        if (notes == null) {
            notes = new ArrayList<>();
            refreshNotesList(true);
        }

        notesAdapter = new NotesAdapter(getContext(), this, notes);

        notesRecyclerView = (RecyclerView) rootView.findViewById(R.id.notes_recycler_view);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL));
        notesRecyclerView.setAdapter(notesAdapter);

        ItemTouchHelper.Callback touchCallback = new SimpleItemTouchHelperCallback(notesAdapter, notesRecyclerView);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchCallback);
        touchHelper.attachToRecyclerView(notesRecyclerView);

        rootView.findViewById(R.id.addNoteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onAddButtonClicked();
            }
        });

        getActivity().setTitle(R.string.title_notes);

        return rootView;
    }

    private int getSpanCount() {
        boolean inLandMode = getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        return inLandMode
                ? getActivity().getResources().getInteger(R.integer.span_count_landscape)
                : getActivity().getResources().getInteger(R.integer.span_count_portrait);

    }

    private void deleteNote(final Note note) {
        Log.d(LOG_TAG, "delete note: " + note);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        query.getInBackground(note.getId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    Log.d(LOG_TAG, "note deleted");
                    parseObject.deleteInBackground();
                    if (notesAdapter.getItemCount() == 0) {
                        showEmptyView();
                    }
                }
            }
        });
    }

    public void refreshNotesList(boolean showLoadingProgress) {
        if (showLoadingProgress) {
            listLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        query.whereEqualTo("author", ParseUser.getCurrentUser());
        query.orderByDescending("updatedAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    notes.clear();
                    for (ParseObject noteObject : parseObjects) {
                        Note note = new Note(
                                noteObject.getObjectId(),
                                noteObject.getString("title"),
                                noteObject.getString("content")
                        );
                        notes.add(note);
                    }
                    listLoadingProgressBar.setVisibility(View.GONE);
                    notesAdapter.notifyDataSetChanged();

                    if (notes.isEmpty()) {
                        showEmptyView();
                    }
                } else {
                    showEmptyView();
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        });
    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setAlpha(0);
        emptyView.animate().alpha(1).setDuration(700).start();
    }

    public void addNoteToList(Note note) {
        emptyView.setVisibility(View.GONE);
        notes.add(0, note);
        notesAdapter.notifyItemInserted(0);
    }

    public void addNoteToList(Note note, int position) {
        notes.add(position, note);
        notesAdapter.notifyItemInserted(position);
    }

    public void updateNote(Note newNote, Note oldNote) {
        for (Note note : notes) {
            if (oldNote.getId().equals(note.getId())) {
                note.setTitle(newNote.getTitle());
                note.setContent(newNote.getContent());
                notes.remove(note);
                notes.add(0, note);
                notesRecyclerView.scrollToPosition(0);
                notesAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onClick(Note note) {
        callback.onNoteDetailsOpen(note);
    }

    @Override
    public void onDismiss(Note note) {
        deleteNote(note);
    }
}
