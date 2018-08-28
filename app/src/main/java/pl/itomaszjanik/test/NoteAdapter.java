package pl.itomaszjanik.test;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.CustomViewHolder>{

    private List<Note> notes;
    NoteClickListener listener;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public TextView content, rating, noOfComments;

        public CustomViewHolder(View view) {
            super(view);
            content = (TextView) view.findViewById(R.id.note_content);
            rating = (TextView) view.findViewById(R.id.rating);
            noOfComments = (TextView) view.findViewById(R.id.note_item_comments_number);
        }
    }

    @Override
    public void onBindViewHolder(final NoteAdapter.CustomViewHolder holder, int position) {
        final Note note = notes.get(position);

        wrapper(note.getContent(), Values.NOTE_VISIBLE_LIMIT, context, holder.content);
        holder.rating.setText(String.valueOf(note.getRating()));
        holder.noOfComments.setText(String.valueOf(note.getNoOfComments()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, note);
            }
        });
    }

    public NoteAdapter(List<Note> notes, NoteClickListener listener, Context context){
        this.notes = notes;
        this.listener = listener;
        this.context = context;
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

    private void wrapper(String text, int wrapLength, Context context, TextView textView){
        int toWrap = text.length();

        if (toWrap > wrapLength){

            String output = text.substring(0, wrapLength) + context.getResources().getString(R.string.note_ellipsis);
            String next = context.getResources().getString(R.string.note_continue_reading);
            int spaceIndex = output.lastIndexOf(" ");

            if (spaceIndex != -1){
                output = output.substring(0, spaceIndex) + context.getResources().getString(R.string.note_ellipsis);
            }

            textView.setText(output + next, TextView.BufferType.SPANNABLE);
            Spannable s = (Spannable) textView.getText();
            int start = output.length();
            int end = start + next.length();
            s.setSpan(new ForegroundColorSpan(Color.GRAY), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else{
            textView.setText(text);
        }
    }
}

