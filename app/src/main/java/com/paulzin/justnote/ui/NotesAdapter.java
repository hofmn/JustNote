package com.paulzin.justnote.ui;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulzin.justnote.R;
import com.paulzin.justnote.data.Note;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private List<Note> notesList;
    private ItemListener clickListener;
    private boolean canRemoveItems = true;
    private Snackbar snackbar;
    private Context context;

    public NotesAdapter(Context context, ItemListener clickListener, List<Note> notesList) {
        this.clickListener = clickListener;
        this.notesList = notesList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note = notesList.get(position);

        if (note.getTitle() == null || note.getTitle().isEmpty()) {
            holder.titleTextView.setVisibility(View.GONE);
        } else {
            holder.titleTextView.setVisibility(View.VISIBLE);
            holder.titleTextView.setText(note.getTitle());
        }

        if (note.getContent() == null || note.getContent().isEmpty()) {
            holder.contentTextView.setVisibility(View.GONE);
        } else {
            holder.contentTextView.setVisibility(View.VISIBLE);
            holder.contentTextView.setText(note.getContent());
            switch (note.getContent().length()) {
                case 1:
                case 2:
                case 3:
                    holder.contentTextView.setTextAppearance(R.style.TextAppearanceExtraLarge);
                    break;
                case 4:
                case 5:
                case 6:
                    holder.contentTextView.setTextAppearance(R.style.TextAppearanceLarge);
                    break;
                case 7:
                case 8:
                case 9:
                    holder.contentTextView.setTextAppearance(R.style.TextAppearanceMedium);
                    break;
                case 10:
                case 11:
                case 12:
                    holder.contentTextView.setTextAppearance(R.style.TextAppearanceSmall);
            }
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void onItemDismiss(final int adapterPosition, RecyclerView recyclerView) {
        final Note note = notesList.get(adapterPosition);

        snackbar = Snackbar.make(recyclerView, R.string.note_deleted, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canRemoveItems = false;
                notesList.add(adapterPosition, note);
                notifyItemInserted(adapterPosition);
            }
        });
        snackbar.getView().addOnAttachStateChangeListener(
                new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {

                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        if (canRemoveItems) {
                            clickListener.onDismiss(note);
                        }
                    }
                });
        snackbar.show();

        notesList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView titleTextView;
        public final TextView contentTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            contentTextView = (TextView) itemView.findViewById(R.id.contentTextView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClick(notesList.get(getAdapterPosition()));
                }
            });
        }
    }
}
