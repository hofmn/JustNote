package com.paulzin.justnote.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.jensdriller.libs.undobar.UndoBar;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.paulzin.justnote.R;
import com.paulzin.justnote.data.Note;
import com.paulzin.justnote.data.OnNoteStateChangeListener;
import com.paulzin.justnote.ui.SwipeDismissListViewTouchListener;

import java.util.ArrayList;
import java.util.List;


public class NotesFragment extends Fragment {

    private final String LOG_TAG = getClass().getCanonicalName();

    private OnNoteStateChangeListener callback;

    private ArrayList<Note> notes;
    private ArrayAdapter<Note> adapter;

    private ProgressBar listLoadingProgressBar;
    private ListView notesListView;

    public NotesFragment() {
        // Required empty public constructor
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        listLoadingProgressBar = (ProgressBar) rootView.findViewById(R.id.listLoadingProgressBar);

        if (notes == null) {
            notes = new ArrayList<>();
            refreshNotesList(true);
        }

        adapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.note_list_item,
                R.id.titleTextView,
                notes);

        View emptyListTextView = rootView.findViewById(android.R.id.empty);
        emptyListTextView.setAlpha(0);
        emptyListTextView.animate().alpha(1).setDuration(2000);

        notesListView = (ListView) rootView.findViewById(R.id.notesList);
        notesListView.setAdapter(adapter);
        notesListView.setEmptyView(emptyListTextView);
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.onNoteDetailsOpen(adapter.getItem(position));
            }
        });

        final FloatingActionButton addNoteButton =
                (FloatingActionButton) rootView.findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAddButtonClicked();
            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        notesListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {

                                startMoveAnimation(addNoteButton, true);

                                for (final int position : reverseSortedPositions) {
                                    final Note note = adapter.getItem(position);
                                    adapter.remove(note);
                                    new UndoBar.Builder(getActivity())
                                            .setAlignParentBottom(true)
                                            .setStyle(UndoBar.Style.LOLLIPOP)
                                            .setMessage(R.string.undo_text)
                                            .setUndoColorResId(R.color.primary)
                                            .setListener(new UndoBar.Listener() {
                                                @Override
                                                public void onHide() {
                                                    startMoveAnimation(addNoteButton, false);
                                                    deleteNote(note);
                                                }

                                                @Override
                                                public void onUndo(Parcelable parcelable) {
                                                    startMoveAnimation(addNoteButton, false);
                                                    addNoteToList(note, position);
                                                }
                                            }).show();
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });

        notesListView.setOnTouchListener(touchListener);
        notesListView.setOnScrollListener(touchListener.makeScrollListener());

        getActivity().setTitle(getString(R.string.title_notes));

        return rootView;
    }

    private void deleteNote(final Note note) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        query.getInBackground(note.getId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    parseObject.deleteInBackground();
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
                        Note note = new Note(noteObject.getObjectId(),
                                noteObject.getString("title"),
                                noteObject.getString("content"));
                        notes.add(note);
                    }

                    listLoadingProgressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(LOG_TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    public void addNoteToList(Note note) {
        notes.add(0, note);
        adapter.notifyDataSetChanged();
    }

    public void addNoteToList(Note note, int position) {
        notes.add(position, note);
        adapter.notifyDataSetChanged();
    }

    public void updateNote(Note newNote, Note oldNote) {
        for (Note note : notes) {
            if (oldNote.getTitle().equals(note.getTitle())) {
                note.setTitle(newNote.getTitle());
                note.setContent(newNote.getContent());
                adapter.remove(note);
                adapter.insert(note, 0);
                break;
            }
        }
    }

    private void startMoveAnimation(View view, boolean moveUp) {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // 'logicalDensity' makes the value of 'moveTo' same for all screen sizes/resolutions
        float logicalDensity = metrics.density;

        float moveTo = moveUp ? -(48 * logicalDensity) : 0;

        view.animate()
                .translationY(moveTo)
                .setDuration(200)
                .setInterpolator(new LinearInterpolator());
    }

    public void insertFakeNotesForDebug() {
        ArrayList<String> titles = new ArrayList<>();
        titles.add("Weird Beer Flavors That Actually Exist");
        titles.add("Vampires aren't sparkly fabulous anymore?");
        titles.add("An imaginary life with Microsoft's new HoloLens");
        titles.add("So @davidduchovny is following me now");
        titles.add("The Rolling Stones, 1965");
        titles.add("Microsoft Office 2016 will be released later this year");
        titles.add("Дети очень быстро растут, мой сын уже старше меня");
        titles.add("Mythical Beasts! Do you need any weekend advice?");
        titles.add("So Sonos' new logo pulses like a speaker when you scroll");
        titles.add("Думаю летом поехать в Боснию или Герцеговину, выбираю");
        String contentText = "Praesent eleifend aliquet lectus. Aenean nec interdum leo. Pellentesque massa enim, tincidunt nec porttitor vitae, varius et urna. Nunc commodo a mi nec mattis. Cras sit amet pulvinar mi. Vivamus augue ligula, sagittis non magna sit amet, imperdiet mattis leo. Proin molestie ex a volutpat tristique. Proin ornare dolor et volutpat maximus. In cursus aliquet imperdiet. In iaculis, nisl id tristique scelerisque, nisl quam rutrum odio, ac feugiat est neque quis velit. Cras vitae pharetra massa. In quis facilisis turpis.";

        for (String title : titles) {
            ParseObject post = new ParseObject("Post");
            post.put("title", title);
            post.put("content", contentText);
            post.saveInBackground();
        }

        refreshNotesList(false);
    }

    public void deleteAllNotes() {
        ArrayList<ParseObject> posts = new ArrayList<>();

        for (Note note : notes) {
            ParseObject post = new ParseObject("Post");
            post.put("id", note.getId());
            post.put("title", note.getTitle());
            post.put("content", note.getContent());
            posts.add(post);
        }

        ParseObject.deleteAllInBackground(posts);
    }
}
