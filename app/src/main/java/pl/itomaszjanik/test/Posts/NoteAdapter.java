package pl.itomaszjanik.test.Posts;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import pl.itomaszjanik.test.*;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.CustomViewHolder>{

    private List<Note> notes;
    NoteClickListener listener;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public TextView username, date, content, likedText, likes, noOfComments;
        public RelativeLayout likeLayout, commentLayout;

        public CustomViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.note_username);
            date = (TextView) view.findViewById(R.id.note_date);
            content = (TextView) view.findViewById(R.id.note_content);
            likes = (TextView) view.findViewById(R.id.note_like_number);
            likedText = (TextView) view.findViewById(R.id.note_like_text);
            noOfComments = (TextView) view.findViewById(R.id.note_item_comments_number);
            likeLayout = (RelativeLayout) view.findViewById(R.id.note_like_layout);
            commentLayout = (RelativeLayout) view.findViewById(R.id.note_comments_layout);
        }
    }

    @Override
    public void onBindViewHolder(final NoteAdapter.CustomViewHolder holder, int position) {
        final Note note = notes.get(position);

        holder.username.setText(note.getUsername());
        //holder.date.setText(Utilities.decodeDate(note.getDate(), context));
        wrapper(note.getContent(), Values.NOTE_VISIBLE_LIMIT, context, holder.content);
        holder.likes.setText(String.valueOf(note.getLikes()));
        if (note.getLiked()){
            holder.likedText.setTextColor(Color.BLUE);
        }

        holder.noOfComments.setText(String.valueOf(note.getComments()));

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLikeClick(v, note);
            }
        });

       /* holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCommentClick(v, note);
            }
        });
*/
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

            String temp = output + next;
            textView.setText(temp, TextView.BufferType.SPANNABLE);
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

