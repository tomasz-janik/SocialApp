package pl.itomaszjanik.test.Comments;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import pl.itomaszjanik.test.Comment;
import pl.itomaszjanik.test.Posts.NoteAdapter;
import pl.itomaszjanik.test.R;
import pl.itomaszjanik.test.Utilities;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter{

    private static int VIEW_COMMENT = 0;
    private static int VIEW_LOADING = 1;
    private List<Comment> comments;
    private CommentClickListener listener;
    private Context context;

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView username, date, content, like, likeText, replays;
        RelativeLayout likeLayout, replayLayout, ellipsisLayout, shareLayout;

        CommentViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.comment_username);
            date = (TextView) view.findViewById(R.id.comment_date);
            content = (TextView) view.findViewById(R.id.comment_content);
            like = (TextView) view.findViewById(R.id.comment_like_number);
            likeText = (TextView) view.findViewById(R.id.comment_like_text);
            replays = (TextView) view.findViewById(R.id.comment_item_replays);
            likeLayout = (RelativeLayout) view.findViewById(R.id.comment_like_it_layout);
            replayLayout = (RelativeLayout) view.findViewById(R.id.comment_replay_layout);
            ellipsisLayout = (RelativeLayout) view.findViewById(R.id.comment_ellipsis_layout);
            shareLayout = (RelativeLayout) view.findViewById(R.id.comment_share_layout);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentAdapter.CommentViewHolder){
            final Comment comment = comments.get(position);

            ((CommentAdapter.CommentViewHolder)holder).username.setText(comment.getUsername());
            ((CommentAdapter.CommentViewHolder)holder).date.setText(Utilities.decodeDate(comment.getDate(), context));
            ((CommentAdapter.CommentViewHolder)holder).content.setText(comment.getContent());
            ((CommentAdapter.CommentViewHolder)holder).like.setText(String.valueOf(comment.getLikes()));
            ((CommentAdapter.CommentViewHolder)holder).replays.setText(String.valueOf(comment.getReplays()));
            if (comment.getLiked()){
                ((CommentAdapter.CommentViewHolder)holder).likeText.setTextColor(Color.BLUE);
            }

            ((CommentAdapter.CommentViewHolder)holder).likeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCommentLikeClick(v, comment);
                }
            });

            ((CommentAdapter.CommentViewHolder)holder).replayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCommentReplayClick(v, comment);
                }
            });

            ((CommentAdapter.CommentViewHolder)holder).ellipsisLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCommentEllipsisClick(v, ((CommentAdapter.CommentViewHolder)holder).ellipsisLayout);
                }
            });

            ((CommentAdapter.CommentViewHolder)holder).shareLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCommentShareClick(v, comment);
                }
            });

            ((CommentAdapter.CommentViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCommentClick(v, comment);
                }
            });
        }
        else{
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }


    public CommentAdapter(List<Comment> comments, CommentClickListener listener, Context context){
        this.comments = comments;
        this.listener = listener;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_COMMENT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_new, parent, false);
            return new CommentViewHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar_item, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return comments.get(position) != null ? VIEW_COMMENT : VIEW_LOADING;
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void insert(Comment comment){
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
    }

    public void insert(List<Comment> list){
        int size = list.size();
        for (int i = 0; i < size; i++){
            comments.add(list.get(i));
            notifyItemInserted(comments.size() - 1);
        }
    }

    public void removeLast(){
        if (comments.size() > 0){
            comments.remove(comments.size() - 1);
            notifyItemRemoved(comments.size());
        }
    }

    public void removeAll(){
        int size = comments.size();
        comments.clear();
        if (size != 0)
            notifyItemRangeRemoved(0, size);
    }

}

