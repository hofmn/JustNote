package com.paulzin.justnote.ui;

import com.paulzin.justnote.data.Note;

public interface ItemListener {
    void onClick(Note note);
    void onDismiss(Note note);
}
