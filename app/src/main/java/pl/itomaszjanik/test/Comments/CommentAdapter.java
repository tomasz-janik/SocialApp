package pl.itomaszjanik.test.Comments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import pl.itomaszjanik.test.Comment;
import pl.itomaszjanik.test.R;
import pl.itomaszjanik.test.Utilities;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    private List<Comment> comments;
    private CommentClickListener listener;
    private Context context;

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        public TextView username, date, content, like, replays;
        public RelativeLayout likeLayout, replayLayout, ellipsisLayout;

        public CommentViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.comment_username);
            date = (TextView) view.findViewById(R.id.comment_date);
            content = (TextView) view.findViewById(R.id.comment_content);
            like = (TextView) view.findViewById(R.id.comment_like_number);
            replays = (TextView) view.findViewById(R.id.comment_item_replays);
            likeLayout = (RelativeLayout) view.findViewById(R.id.comment_like_it_layout);
            replayLayout = (RelativeLayout) view.findViewById(R.id.comment_replay_layout);
            ellipsisLayout = (RelativeLayout) view.findViewById(R.id.comment_ellipsis_layout);
        }
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.CommentViewHolder holder, int position) {
        final Comment comment = comments.get(position);

        holder.username.setText(comment.getUsername());
        holder.date.setText(Utilities.decodeDate(comment.getDate(), context));
        holder.content.setText(comment.getContent());
        holder.like.setText(String.valueOf(comment.getLikes()));
        holder.replays.setText(String.valueOf(comment.getNoOfReplays()));

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLikeClick(v, holder.likeLayout);
            }
        });

        holder.replayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReplayClick(v, comment);
            }
        });

        holder.ellipsisLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEllipsisClick(v, holder.ellipsisLayout);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, comment);
            }
        });
    }


    public CommentAdapter(List<Comment> comments, CommentClickListener listener, Context context){
        this.comments = comments;
        this.listener = listener;
        this.context = context;
    }


    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_new, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}

