package pl.itomaszjanik.test;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.CustomViewHolder>{

    private List<Note> notes;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView content, rating;

        public CustomViewHolder(View view) {
            super(view);
            content = (TextView) view.findViewById(R.id.note_content);
            rating = (TextView) view.findViewById(R.id.rating);
        }
    }

    public NoteAdapter(List<Note> notes){
        this.notes = notes;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.content.setText(note.getContent());
        holder.rating.setText(String.valueOf(note.getRating()));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}

