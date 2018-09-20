package pl.itomaszjanik.test.Posts;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import pl.itomaszjanik.test.*;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter{

    private static final int VIEW_NOTE_HEADER = 0;
    private static final int VIEW_NOTE_MIDDLE = 1;
    private static final int VIEW_NOTE_FOOTER = 2;
    private static final int VIEW_NOTE_END = 3;

    private List<Note> notes = new ArrayList<>();
    private NoteClickListener noteClickListener;
    private NoteNoMoreClickListener noteNoMoreClickListener;
    private OnEndScrolled onEndScrolled;
    private int topLayout;

    private Context context;

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView username, date, content, likedText, likes, noOfComments;
        RelativeLayout likeLayout, commentLayout;

        CustomViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.note_username);
            date = view.findViewById(R.id.note_date);
            content = view.findViewById(R.id.note_content);
            likes = view.findViewById(R.id.note_like_number);
            likedText = view.findViewById(R.id.note_like_text);
            noOfComments = view.findViewById(R.id.note_item_comments_number);
            likeLayout = view.findViewById(R.id.note_like_layout);
            commentLayout = view.findViewById(R.id.note_comments_layout);
        }
    }

    class NoMoreViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout refreshLayout;

        NoMoreViewHolder(View view){
            super(view);
            refreshLayout = view.findViewById(R.id.note_refresh);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position == notes.size() - 5){
            onEndScrolled.onEnd();
        }
        if (holder instanceof CustomViewHolder){
            final Note note = notes.get(position);
            initCustomViewHolder((CustomViewHolder)holder, note);
        }
        else if (holder instanceof NoMoreViewHolder){
            initNoMoreViewHolder((NoMoreViewHolder)holder);
        }

    }

    public NoteAdapter(int layout, Context context){
        this.topLayout = layout;
        this.context = context;
    }

    public void initListeners(NoteClickListener noteClickListener, NoteNoMoreClickListener noteNoMoreClickListener, OnEndScrolled onEndScrolled){
        this.noteClickListener = noteClickListener;
        this.noteNoMoreClickListener = noteNoMoreClickListener;
        this.onEndScrolled = onEndScrolled;
    }

    private void initNoMoreViewHolder(NoMoreViewHolder holder){
        holder.refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteNoMoreClickListener.onRefreshClick();
            }
        });
    }

    private void initCustomViewHolder(CustomViewHolder holder, final Note note){
        holder.username.setText(note.getUsername());
        wrapper(note.getContent(), Values.NOTE_VISIBLE_LIMIT, context, holder.content);
        holder.likes.setText(String.valueOf(note.getLikes()));
        holder.noOfComments.setText(String.valueOf(note.getComments()));
        if (note.getLiked()){
            holder.likedText.setTextColor(Color.BLUE);
        }
        else{
            holder.likedText.setTextColor(Color.parseColor("#747474"));
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteClickListener.onLikeClick(v, note);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteClickListener.onItemClick(v, note);
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        if (notes.get(position) == null){
            return VIEW_NOTE_END;
        }
        if (position == 0){
            return VIEW_NOTE_HEADER;
        }
        if (position == notes.size() - 1){
            return VIEW_NOTE_FOOTER;
        }
        return VIEW_NOTE_MIDDLE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_NOTE_HEADER){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(topLayout, parent, false);
            return new CustomViewHolder(itemView);
        }
        if (viewType == VIEW_NOTE_MIDDLE){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
            return new CustomViewHolder(itemView);
        }
        if (viewType == VIEW_NOTE_FOOTER){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_bottom, parent, false);
            return new CustomViewHolder(itemView);
        }
        if (viewType == VIEW_NOTE_END){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_end, parent, false);
            return new NoMoreViewHolder(itemView);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder){
        Log.e("recycled", "" + viewHolder.getAdapterPosition());
    }

    public void insert(Note note){
        notes.add(note);
        notifyItemInserted(notes.size() - 1);
    }

    public void insert(List<Note> list){
        for (Note note : list){
            notes.add(note);
            notifyItemInserted(notes.size() - 1);
        }
    }

    public void insertNull(){
        notes.add(null);
        notifyItemInserted(notes.size() - 1);
    }

    public void removeAll(){
        int size = notes.size();
        notes.clear();
        if (size != 0)
            notifyItemRangeRemoved(0, size);
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

