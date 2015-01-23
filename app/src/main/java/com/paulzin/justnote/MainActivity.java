package com.paulzin.justnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.paulzin.justnote.data.Note;
import com.paulzin.justnote.data.OnNoteStateChangeListener;
import com.paulzin.justnote.fragments.EditNoteFragment;
import com.paulzin.justnote.fragments.NotesFragment;


public class MainActivity extends ActionBarActivity implements
        OnNoteStateChangeListener, FragmentManager.OnBackStackChangedListener {

    private static final String NOTES_FRAGMENT_TAG = "NotesFragment";
    private static final String EDIT_NOTE_FRAGMENT_TAG = "EditNoteFragment";

    private final String LOG_TAG = this.getClass().getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();

        fm.addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();

        Fragment fragment = fm.findFragmentById(R.id.container);

        if (fragment == null) {
            fragment = new NotesFragment();
            fm.beginTransaction()
                   // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.container, fragment, NOTES_FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        NotesFragment notesFragment
                = (NotesFragment) getSupportFragmentManager()
                .findFragmentByTag(NOTES_FRAGMENT_TAG);

        switch (item.getItemId()) {
            case R.id.action_refresh:
                notesFragment.refreshNotesList(true);
                return true;
            case R.id.action_insert_fake:
                notesFragment.insertFakeNotesForDebug();
                return true;
            case R.id.action_delete_all:
                notesFragment.deleteAllNotes();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onNoteDetailsOpen(Note note) {
        Fragment fragment = EditNoteFragment.newInstance(note.getId(),
                note.getTitle(), note.getContent(), false);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onNoteAdded(String title, String content) {
        NotesFragment notesFragment
                = (NotesFragment) getSupportFragmentManager().findFragmentByTag(NOTES_FRAGMENT_TAG);
        notesFragment.addNoteToList(new Note(title, content));
    }

    @Override
    public void onNoteChanged(Note newNote, Note oldNote) {
        NotesFragment notesFragment
                = (NotesFragment) getSupportFragmentManager().findFragmentByTag(NOTES_FRAGMENT_TAG);
        notesFragment.updateNote(newNote, oldNote);
    }

    @Override
    public void onAddButtonClicked() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new EditNoteFragment(), EDIT_NOTE_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackStackChanged() {
        hideUndoBar(); // hide undo bar when open new fragment
        shouldDisplayHomeUp();
    }

    private void shouldDisplayHomeUp() {
        boolean show = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(show);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    private void hideUndoBar() {
        View undoBar = findViewById(R.id.undoBar);
        if (undoBar != null) {
            undoBar.setVisibility(View.GONE);
        }
    }
}
