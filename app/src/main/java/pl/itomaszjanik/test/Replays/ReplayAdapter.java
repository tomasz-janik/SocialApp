package pl.itomaszjanik.test.Replays;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.Comments.CommentClickListener;

import java.util.ArrayList;
import java.util.List;

public class ReplayAdapter extends RecyclerView.Adapter{

    private static int VIEW_HEADER = 0;
    private static int VIEW_COMMENTS_HEADER = 1;
    private static int VIEW_COMMENTS_FOOTER = 3;
    private static int VIEW_COMMENTS_MIDDLE = 2;
    private static int VIEW_COMMENTS_LOADING = 4;

    private List<Replay> comments = new ArrayList<>();
    private OnEndScrolled onEndScrolled;
    private ReplayClickListener replayClickListener;
    private CommentClickListener commentClickListener;
    private ReplaysFooterClickListener replaysFooterClickListener;

    private Comment mComment;
    private Context context;

    public ReplayAdapter(ReplayClickListener listener, Comment comment, Context context, OnEndScrolled onEndScrolled){
        this.replayClickListener = listener;
        this.mComment = comment;
        this.context = context;
        this.onEndScrolled = onEndScrolled;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
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

    static class ReplaysHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView replays;

        ReplaysHeaderViewHolder(View v) {
            super(v);
            replays = v.findViewById(R.id.comment_details_comments_number);
        }
    }

    static class ReplaysMiddleViewHolder extends RecyclerView.ViewHolder{
        TextView username, date, content, likes, like;
        RelativeLayout likeLayout, replayLayout, ellipsisLayout, shareLayout;

        ReplaysMiddleViewHolder(View view) {
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

    static class ReplaysFooterViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout refreshLayout;

        ReplaysFooterViewHolder(View v){
            super(v);
            refreshLayout = v.findViewById(R.id.comment_details_comments_refresh);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_bar);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position == comments.size() - 1){
            onEndScrolled.onEnd();
        }
        if (holder instanceof HeaderViewHolder){
            initHeaderViewHolder((HeaderViewHolder)holder);
        }
        else if (holder instanceof ReplaysHeaderViewHolder){
            initReplayHeaderViewHolder((ReplaysHeaderViewHolder)holder);
        }
        else if (holder instanceof ReplaysMiddleViewHolder){
            Replay replay = comments.get(position);
            initReplayMiddleViewHolder((ReplaysMiddleViewHolder)holder, replay);
        }
        else if (holder instanceof ReplayAdapter.ReplaysFooterViewHolder){
            initReplayFooterViewHolder((ReplaysFooterViewHolder)holder);
        }
        else if (holder instanceof ReplayAdapter.ProgressViewHolder){
            ((ReplayAdapter.ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_HEADER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_details_header, parent, false);
            return new HeaderViewHolder(view);
        }
        if (viewType == VIEW_COMMENTS_HEADER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_details_recycler_header, parent, false);
            return new ReplaysHeaderViewHolder(view);
        }
        if (viewType == VIEW_COMMENTS_MIDDLE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_no_replays, parent, false);
            return new ReplaysMiddleViewHolder(view);
        }
        if (viewType == VIEW_COMMENTS_FOOTER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_details_recycler_footer, parent, false);
            return new ReplaysFooterViewHolder(view);
        }
        if (viewType == VIEW_COMMENTS_LOADING){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar_item, parent, false);
            return new ProgressViewHolder(view);
        }
        return null;
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
        if (position == 1){
            return VIEW_COMMENTS_HEADER;
        }
        if (position == comments.size() - 1){
            return VIEW_COMMENTS_FOOTER;
        }
        return comments.get(position) != null ? VIEW_COMMENTS_MIDDLE : VIEW_COMMENTS_LOADING;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder){
        Log.e("recycled", "" + viewHolder.getAdapterPosition());
    }

    private void initHeaderViewHolder(final ReplayAdapter.HeaderViewHolder holder){
        holder.username.setText(mComment.getUsername());
        holder.date.setText(Utilities.decodeDate(mComment.getDate(), context));
        holder.content.setText(mComment.getContent());
        holder.likes.setText(String.valueOf(mComment.getLikes()));
        holder.replays.setText(String.valueOf(mComment.getReplays()));

        if (mComment.getLiked()){
            holder.like.setTextColor(Color.BLUE);
        }
        else{
            holder.like.setTextColor(Color.parseColor("#747474"));
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
                commentClickListener.onCommentEllipsisClick(view, mComment, holder.ellipsisLayout);
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentClickListener.onCommentShareClick(view, mComment);
            }
        });
    }

    private void initReplayHeaderViewHolder(final ReplaysHeaderViewHolder holder){
        holder.replays.setText(Utilities.getReplaysVariation(mComment.getReplays(), context));
    }

    private void initReplayMiddleViewHolder(final ReplaysMiddleViewHolder holder, final Replay replay){
        holder.username.setText(replay.getUsername());
        holder.date.setText(Utilities.decodeDate(replay.getDate(), context));

        String replayContent = replay.getContent();
        if (replayContent.startsWith("@" + mComment.getUsername())){
            Spannable spannable = new SpannableString(replayContent);
            spannable.setSpan(new BackgroundColorSpan(Color.parseColor("#22000000")),0,
                    mComment.getUsername().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.content.setText(spannable);
        }
        else{
            holder.content.setText(replay.getContent());
        }

        holder.likes.setText(String.valueOf(replay.getLikes()));

        if (replay.getLiked()){
            holder.like.setTextColor(Color.BLUE);
        }
        else{
            holder.like.setTextColor(Color.parseColor("#747474"));
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayClickListener.onLikeClick(v, replay);
            }
        });

        holder.replayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayClickListener.onReplayClick(v, replay);
            }
        });

        holder.ellipsisLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayClickListener.onEllipsisClick(v, replay, holder.ellipsisLayout);
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayClickListener.onShareClick(v, replay);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayClickListener.onItemClick(v, replay);
            }
        });
    }

    private void initReplayFooterViewHolder(final ReplayAdapter.ReplaysFooterViewHolder holder){
        holder.refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaysFooterClickListener.onRefreshClick();
            }
        });
    }

    public void initListeners(CommentClickListener commentClickListener, ReplaysFooterClickListener replaysFooterClickListener){
        //this.listener = replayClickListener;
        this.commentClickListener = commentClickListener;
        this.replaysFooterClickListener = replaysFooterClickListener;
    }

    public void insert(Replay replay){
        comments.add(replay);
        notifyItemInserted(comments.size() - 1);
    }

    public void insert(List<Replay> list){
        for (Replay replay : list){
            comments.add(replay);
            notifyItemInserted(comments.size() - 1);
        }
    }

    public void insertNull(){
        comments.add(null);
        notifyItemInserted(comments.size() - 1);
    }

    public void insertFooter(){
        insertNull();
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
    }

}

