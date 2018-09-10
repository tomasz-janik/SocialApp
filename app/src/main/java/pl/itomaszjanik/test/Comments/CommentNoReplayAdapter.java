package pl.itomaszjanik.test.Comments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import pl.itomaszjanik.test.R;
import pl.itomaszjanik.test.Replay;
import pl.itomaszjanik.test.Utilities;

import java.util.ArrayList;
import java.util.List;

public class CommentNoReplayAdapter extends RecyclerView.Adapter{

    public static int VIEW_COMMENT = 0;
    public static int VIEW_LOADING = 1;

    private List<Replay> comments = new ArrayList<>();
    private ReplayClickListener listener;
    private Context context;

    class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView username, date, content, like;
        RelativeLayout likeLayout, replayLayout, ellipsisLayout, shareLayout;

        CommentViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.comment_username);
            date = (TextView) view.findViewById(R.id.comment_date);
            content = (TextView) view.findViewById(R.id.comment_content);
            like = (TextView) view.findViewById(R.id.comment_like_number);
            likeLayout = (RelativeLayout) view.findViewById(R.id.comment_like_it_layout);
            replayLayout = (RelativeLayout) view.findViewById(R.id.comment_replay_layout);
            ellipsisLayout = (RelativeLayout) view.findViewById(R.id.comment_ellipsis_layout);
            shareLayout = (RelativeLayout) view.findViewById(R.id.comment_share_layout);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentNoReplayAdapter.CommentViewHolder){
            final Replay comment = comments.get(position);

            ((CommentNoReplayAdapter.CommentViewHolder)holder).username.setText(comment.getUsername());
            ((CommentNoReplayAdapter.CommentViewHolder)holder).date.setText(Utilities.decodeDate(comment.getDate(), context));
            ((CommentNoReplayAdapter.CommentViewHolder)holder).content.setText(comment.getContent());
            ((CommentNoReplayAdapter.CommentViewHolder)holder).like.setText(String.valueOf(comment.getLikes()));

            ((CommentNoReplayAdapter.CommentViewHolder)holder).likeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onLikeClick(v, comment);
                }
            });

            ((CommentNoReplayAdapter.CommentViewHolder)holder).replayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onReplayClick(v, comment);
                }
            });

            ((CommentNoReplayAdapter.CommentViewHolder)holder).ellipsisLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEllipsisClick(v, ((CommentNoReplayAdapter.CommentViewHolder)holder).ellipsisLayout);
                }
            });

            ((CommentNoReplayAdapter.CommentViewHolder)holder).shareLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShareClick(v, comment);
                }
            });

            ((CommentNoReplayAdapter.CommentViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, comment);
                }
            });
        }
        else{
            ((CommentNoReplayAdapter.ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }


    public CommentNoReplayAdapter(ReplayClickListener listener, Context context){
        this.listener = listener;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_COMMENT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_no_replays, parent, false);
            return new CommentViewHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar_item, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return comments.get(position) != null ? VIEW_COMMENT : VIEW_LOADING;
    }

    public void insert(Replay replay){
        comments.add(replay);
        notifyItemInserted(comments.size() - 1);
    }

    public void insert(List<Replay> list){
        int size = list.size();
        for (int i = 0; i < size; i++){
            comments.add(list.get(i));
            notifyItemInserted(comments.size() - 1);
        }
    }

    public void insertNull(){
        comments.add(null);
        notifyItemInserted(comments.size() - 1);
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

