package com.paulzin.justnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseUser;
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

        registerFakeUser("ooswac@gmail.com", "1111", "ooswac@gmail.com");
        registerFakeUser("page@gmail.com", "1111", "page@gmail.com");
        registerFakeUser("ass@gmail.com", "1111", "ass@gmail.com");

        checkIfUserIsLoggedIn();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SettingsActivity.REQUEST_LOGOUT) {
            if (resultCode == RESULT_OK) {
                logOut();
            }
        }
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
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SettingsActivity.REQUEST_LOGOUT);
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

    private void checkIfUserIsLoggedIn() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            openLogInActivity();
        }
    }

    private void openLogInActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerFakeUser(String email, String password, String username) {
        ParseUser testUser = new ParseUser();
        testUser.setEmail(email);
        testUser.setPassword(password);
        testUser.setUsername(email);
        testUser.signUpInBackground();
    }

    private void logOut() {
        ParseUser.logOut();
        openLogInActivity();
    }
}
