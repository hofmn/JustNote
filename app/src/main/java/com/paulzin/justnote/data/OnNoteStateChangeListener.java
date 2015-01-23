package com.paulzin.justnote.data;

/**
* This interface defines communication between
* NotesFragment and EditNoteFragment
*/
public interface OnNoteStateChangeListener {

    void onAddButtonClicked();
    void onNoteDetailsOpen(Note note);
    void onNoteAdded(String title, String content);
    void onNoteChanged(Note newNote, Note oldNote);
}
