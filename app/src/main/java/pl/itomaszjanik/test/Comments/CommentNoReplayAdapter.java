package pl.itomaszjanik.test.Comments;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import pl.itomaszjanik.test.*;

import java.util.ArrayList;
import java.util.List;

public class CommentNoReplayAdapter extends RecyclerView.Adapter{

    public static int VIEW_COMMENT = 2;
    public static int VIEW_HEADER = 0;
    public static int VIEW_COMMENTS_HEADER = 1;
    public static int VIEW_COMMENTS_FOOTER = 3;
    public static int VIEW_COMMENTS_MIDDLE = 2;
    public static int VIEW_COMMENTS_LOADING = 4;

    private List<Replay> comments = new ArrayList<>();
    private OnEndScrolled onEndScrolled;
    private ReplayClickListener listener;
    private CommentClickListener commentClickListener;
    private ReplaysFooterClickListener replaysFooterClickListener;

    private Comment mComment;
    private Context context;

    class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView username, date, content, likes, like;
        RelativeLayout likeLayout, replayLayout, ellipsisLayout, shareLayout;

        CommentViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.comment_username);
            date = view.findViewById(R.id.comment_date);
            content = view.findViewById(R.id.comment_content);
            likes = view.findViewById(R.id.comment_like_number);
            like = view.findViewById(R.id.comment_like_text);
            likeLayout = view.findViewById(R.id.comment_like_it_layout);
            replayLayout = view.findViewById(R.id.comment_replay_layout);
            ellipsisLayout = view.findViewById(R.id.comment_ellipsis_layout);
            shareLayout = view.findViewById(R.id.comment_share_layout);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_bar);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView username,date,content,likes,like,replays;
        RelativeLayout likeLayout, replayLayout, shareLayout, ellipsisLayout;

        HeaderViewHolder(View v) {
            super(v);
            username = v.findViewById(R.id.comment_username);
            date = v.findViewById(R.id.comment_date);
            content = v.findViewById(R.id.comment_content);
            likes = v.findViewById(R.id.comment_like_number);
            like = v.findViewById(R.id.comment_like_text);
            replays = v.findViewById(R.id.comment_item_replays);

            likeLayout = v.findViewById(R.id.comment_like_it_layout);
            replayLayout = v.findViewById(R.id.comment_replay_layout);
            shareLayout = v.findViewById(R.id.comment_share_layout);
            ellipsisLayout = v.findViewById(R.id.comment_ellipsis_layout);
        }
    }

    static class CommentsHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView replays;

        CommentsHeaderViewHolder(View v) {
            super(v);
            replays = v.findViewById(R.id.comment_details_comments_number);
        }
    }

    static class ReplaysFooterViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout refreshLayout;

        ReplaysFooterViewHolder(View v){
            super(v);
            refreshLayout = v.findViewById(R.id.comment_details_comments_refresh);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position == comments.size() - 1){
            onEndScrolled.onEnd();
        }
        if (holder instanceof CommentNoReplayAdapter.CommentViewHolder){
            final Replay replay = comments.get(position);
            initReplayViewHolder((CommentViewHolder)holder, replay);
        }
        else if (holder instanceof CommentNoReplayAdapter.ProgressViewHolder){
            ((CommentNoReplayAdapter.ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
        else if (holder instanceof CommentNoReplayAdapter.HeaderViewHolder){
            initHeaderViewHolder((HeaderViewHolder)holder);
        }
        else if (holder instanceof CommentNoReplayAdapter.CommentsHeaderViewHolder){
            initReplayHeaderViewHolder((CommentsHeaderViewHolder)holder);
        }
        else if (holder instanceof CommentNoReplayAdapter.ReplaysFooterViewHolder){
            initReplayFooterViewHolder((ReplaysFooterViewHolder)holder);
        }
    }

    private void initReplayFooterViewHolder(final CommentNoReplayAdapter.ReplaysFooterViewHolder holder){
        holder.refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaysFooterClickListener.onRefreshClick();
            }
        });
    }

    private void initReplayHeaderViewHolder(final CommentNoReplayAdapter.CommentsHeaderViewHolder holder){
        holder.replays.setText(Utilities.getReplaysVariation(mComment.getReplays(), context));
    }

    private void initReplayViewHolder(final CommentNoReplayAdapter.CommentViewHolder holder, final Replay replay){
        holder.username.setText(replay.getUsername());
        holder.date.setText(Utilities.decodeDate(replay.getDate(), context));
        holder.content.setText(replay.getContent());
        holder.likes.setText(String.valueOf(replay.getLikes()));

        if (replay.getLiked()){
            holder.like.setTextColor(Color.BLUE);
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLikeClick(v, replay);
            }
        });

        holder.replayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReplayClick(v, replay);
            }
        });

        holder.ellipsisLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEllipsisClick(v, holder.ellipsisLayout);
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShareClick(v, replay);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, replay);
            }
        });
    }


    private void initHeaderViewHolder(final CommentNoReplayAdapter.HeaderViewHolder holder){
        holder.username.setText(mComment.getUsername());
        holder.date.setText(Utilities.decodeDate(mComment.getDate(), context));
        holder.content.setText(mComment.getContent());
        holder.likes.setText(String.valueOf(mComment.getLikes()));
        holder.replays.setText(String.valueOf(mComment.getReplays()));

        if (mComment.getLiked()){
            holder.like.setTextColor(Color.BLUE);
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentClickListener.onCommentLikeClick(view, mComment);
            }
        });

        holder.replayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentClickListener.onCommentReplayClick(view, mComment);
            }
        });

        holder.ellipsisLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentClickListener.onCommentEllipsisClick(view, holder.ellipsisLayout);
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentClickListener.onCommentShareClick(view, mComment);
            }
        });
    }

    public CommentNoReplayAdapter(ReplayClickListener listener, Comment comment, Context context, OnEndScrolled onEndScrolled){
        this.listener = listener;
        this.mComment = comment;
        this.context = context;
        this.onEndScrolled = onEndScrolled;
    }

    public void initListeners(CommentClickListener commentClickListener, ReplaysFooterClickListener replaysFooterClickListener){
        //this.listener = replayClickListener;
        this.commentClickListener = commentClickListener;
        this.replaysFooterClickListener = replaysFooterClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_COMMENTS_MIDDLE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_no_replays, parent, false);
            return new CommentViewHolder(view);
        }
        else if (viewType == VIEW_COMMENTS_LOADING){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar_item, parent, false);
            return new ProgressViewHolder(view);
        }
        else if (viewType == VIEW_HEADER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_details_header, parent, false);
            return new HeaderViewHolder(view);
        }
        else if (viewType == VIEW_COMMENTS_HEADER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_header, parent, false);
            return new CommentsHeaderViewHolder(view);
        }
        else if (viewType == VIEW_COMMENTS_FOOTER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_footer, parent, false);
            return new ReplaysFooterViewHolder(view);
        }
        else{
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (comments.size() == 0 || comments.size() == 2){
            return VIEW_COMMENTS_FOOTER;
        }
        if (position == 0){
            return VIEW_HEADER;
        }
        else if (position == 1){
            return VIEW_COMMENTS_HEADER;
        }
        else if (position == comments.size() - 1){
            return VIEW_COMMENTS_FOOTER;
        }
        return comments.get(position) != null ? VIEW_COMMENTS_MIDDLE : VIEW_COMMENTS_LOADING;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder){
        Log.e("recycled", "" + viewHolder.getAdapterPosition());
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

    public void insertFooter(){
        comments.add(null);
        notifyItemInserted(comments.size() - 1);
    }

    public void removeFooter(){
        if (comments.size() > 0){
            comments.remove(comments.size() - 1);
            notifyItemRemoved(comments.size());
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
        insertNull();
        insertNull();
        //insertNull();
    }

}

