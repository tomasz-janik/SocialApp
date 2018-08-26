package pl.itomaszjanik.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import pl.itomaszjanik.test.Fragments.NoteDetails;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.CustomViewHolder>{

    private List<Note> notes;
    ItemClickListener listener;

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public TextView content, rating;

        public CustomViewHolder(View view) {
            super(view);
            content = (TextView) view.findViewById(R.id.note_content);
            rating = (TextView) view.findViewById(R.id.rating);
        }
    }

    @Override
    public void onBindViewHolder(final NoteAdapter.CustomViewHolder holder, int position) {
        final Note note = notes.get(position);

        holder.content.setText(note.getContent());
        holder.rating.setText(String.valueOf(note.getRating()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, note);
            }
        });
    }

    public NoteAdapter(List<Note> notes, ItemClickListener listener){
        this.notes = notes;
        this.listener = listener;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private String wrapper(String text, int wrapLength){
        int toWrap = text.length();
        int lineBreakIndex = text.indexOf('\n');

        String output = text;

        if (toWrap > wrapLength){
            if (lineBreakIndex > wrapLength || lineBreakIndex < 0){
                toWrap = wrapLength;
            }
            else {
                toWrap = lineBreakIndex;
            }
            output = text.substring(0,toWrap) + "...";
        }

        return output;
    }
}

