package pl.itomaszjanik.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.joda.time.*;
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
    Context context;

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
        holder.date.setText(decodeDate(comment.getDate()));
        holder.content.setText(comment.getContent());
    }

    private String decodeDate(String date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime creationTime = dateTimeFormatter.parseDateTime(date);

        Interval interval = new Interval(creationTime, new Instant());
        Duration duration = interval.toDuration();
        String output = "";
        if (duration.getStandardDays() >= 356){
            long years = duration.getStandardDays()/356;
            if (years == 1){
                output += context.getResources().getString(R.string.year_one);
            }
            else if (years < 5){
                output += years + " ";
                output += context.getResources().getString(R.string.year_two);
            }
            else{
                output += years + " ";
                output += context.getResources().getString(R.string.year_five);
            }
        }
        else if (duration.getStandardDays() >= 31){
            long months = duration.getStandardDays() / 31;
            if (months == 1){
                output += context.getResources().getString(R.string.month_one);
            }
            else if (months < 5){
                output += months + " ";
                output += context.getResources().getString(R.string.month_two);
            }
            else{
                output += months + " ";
                output += context.getResources().getString(R.string.month_five);
            }
        }
        else if (duration.getStandardDays() >= 7){
            long weeks = duration.getStandardDays() / 7;
            if (weeks == 1){
                output += context.getResources().getString(R.string.week_one);
            }
            else if (weeks < 5){
                output += weeks + " ";
                output += context.getResources().getString(R.string.week_two);
            }
            else{
                output += weeks + " ";
                output += context.getResources().getString(R.string.week_five);
            }
        }
        else if (duration.getStandardDays() >= 1){
            long days = duration.getStandardDays();
            if (days == 1){
                output += context.getResources().getString(R.string.day_one);
            }
            else{
                output += days + " ";
                output += context.getResources().getString(R.string.week_two);
            }
        }
        else if (duration.getStandardHours() >= 1){
            long hours = duration.getStandardHours();
            if (hours == 1){
                output += context.getResources().getString(R.string.hour_one);
            }
            else if (hours < 5){
                output += hours + " ";
                output += context.getResources().getString(R.string.hour_two);
            }
            else{
                output += hours + " ";
                output += context.getResources().getString(R.string.hour_five);
            }
        }
        else if (duration.getStandardMinutes() >= 1){
            long minutes = duration.getStandardMinutes();
            if (minutes == 1){
                output += context.getResources().getString(R.string.minute_one);
            }
            else if (minutes < 5){
                output += minutes + " ";
                output += context.getResources().getString(R.string.minute_two);
            }
            else{
                output += minutes + " ";
                output += context.getResources().getString(R.string.minute_five);
            }
        }
        else{
            output += context.getResources().getString(R.string.second);
        }
        return (output + " temu");
    }

    public CommentAdapter(List<Comment> comments, CommentClickListener listener, Context context){
        this.comments = comments;
        this.listener = listener;
        this.context = context;
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

