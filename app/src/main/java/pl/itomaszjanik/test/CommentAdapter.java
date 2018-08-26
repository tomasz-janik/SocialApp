package pl.itomaszjanik.test;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    private List<Comment> comments;
    CommentClickListener listener;

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        public TextView username, date, content;

        public CommentViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.comment_username);
            date = (TextView) view.findViewById(R.id.comment_date);
            content = (TextView) view.findViewById(R.id.comment_content);

        }
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.CommentViewHolder holder, int position) {
        final Comment comment = comments.get(position);

        holder.username.setText(comment.getUsername());
        //holder.date.setText(uncodeDate(comment.getDate()));
        holder.content.setText(comment.getContent());
    }

    private String uncodeDate(String date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime creationTime = dateTimeFormatter.parseDateTime(date);

        Interval interval = new Interval(creationTime, new Instant());


        SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String output = "";
        return output;
    }

    public CommentAdapter(List<Comment> comments, CommentClickListener listener){
        this.comments = comments;
        this.listener = listener;
    }


    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}

