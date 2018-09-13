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
import pl.itomaszjanik.test.Posts.NoteAdapter;
import pl.itomaszjanik.test.Posts.NoteClickListener;
import pl.itomaszjanik.test.Posts.NoteDetailsClickListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter{

    private static int VIEW_HEADER = 0;
    private static int VIEW_COMMENTS_HEADER = 1;
    private static int VIEW_COMMENTS_FOOTER = 3;
    private static int VIEW_COMMENTS_MIDDLE = 2;
    private static int VIEW_COMMENTS_LOADING = 4;

    private Note mNote;
    private List<Comment> comments;
    private NoteDetailsClickListener noteClickListener;
    private CommentClickListener commentClickListener;
    private CommentsFooterClickListener commentsFooterClickListener;
    private OnEndScrolled onEndScrolled;
    private Context context;

    static class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView username, date, content, likes, likeText, comments, tags;
        RelativeLayout likeLayout, commentLayout, ellipsisLayout, shareLayout;

        HeaderViewHolder(View view){
            super(view);
            username = view.findViewById(R.id.note_details_user);
            date = view.findViewById(R.id.note_details_date);
            content = view.findViewById(R.id.note_details_content);
            likes = view.findViewById(R.id.note_details_like_number);
            likeText = view.findViewById(R.id.note_details_like_text);
            comments = view.findViewById(R.id.note_details_comments_number);
            tags = view.findViewById(R.id.note_details_hashes);
            likeLayout = view.findViewById(R.id.note_details_like_it_layout);
            commentLayout = view.findViewById(R.id.note_details_comment_layout);
            ellipsisLayout = view.findViewById(R.id.note_details_ellipsis_layout);
            shareLayout = view.findViewById(R.id.note_details_share_layout);
        }
    }

    static class CommentsHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView replays;

        CommentsHeaderViewHolder(View v) {
            super(v);
            replays = v.findViewById(R.id.note_details_comments_number);
        }
    }

    static class CommentsMiddleViewHolder extends RecyclerView.ViewHolder{
        TextView username, date, content, likes, likeText, replays;
        RelativeLayout likeLayout, replayLayout, ellipsisLayout, shareLayout;

        CommentsMiddleViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.comment_username);
            date = view.findViewById(R.id.comment_date);
            content = view.findViewById(R.id.comment_content);
            likes = view.findViewById(R.id.comment_like_number);
            likeText = view.findViewById(R.id.comment_like_text);
            replays = view.findViewById(R.id.comment_item_replays);
            likeLayout = view.findViewById(R.id.comment_like_it_layout);
            replayLayout = view.findViewById(R.id.comment_replay_layout);
            ellipsisLayout = view.findViewById(R.id.comment_ellipsis_layout);
            shareLayout = view.findViewById(R.id.comment_share_layout);
        }
    }

    static class CommentsFooterViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout refreshLayout;

        CommentsFooterViewHolder(View v){
            super(v);
            refreshLayout = v.findViewById(R.id.note_details_comments_refresh);
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
        else if (holder instanceof CommentsHeaderViewHolder){
            initCommentHeaderViewHolder((CommentsHeaderViewHolder)holder);
        }
        else if (holder instanceof  CommentsMiddleViewHolder){
            Comment comment = comments.get(position);
            initCommentMiddleViewHolder((CommentsMiddleViewHolder)holder, comment);
        }
        else if (holder instanceof CommentsFooterViewHolder){
            initCommentFooterViewHolder((CommentsFooterViewHolder)holder);
        }
        else if (holder instanceof ProgressViewHolder){
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder){
        Log.e("recycled", "" + viewHolder.getAdapterPosition());
    }

    private void initHeaderViewHolder(final HeaderViewHolder holder){
        holder.username.setText(mNote.getUsername());
        holder.date.setText(Utilities.decodeDate(mNote.getDate(), context));
        holder.content.setText(mNote.getContent());
        holder.likes.setText(String.valueOf(mNote.getLikes()));
        holder.comments.setText(String.valueOf(mNote.getComments()));
        holder.tags.setText(Utilities.prepareHasheshText(mNote.getHashesh()));

        if (mNote.getLiked()){
            holder.likeText.setTextColor(Color.BLUE);
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteClickListener.onNoteLikeClick(view, mNote);
            }
        });

        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteClickListener.onNoteCommentClick(view, mNote);
            }
        });

        holder.ellipsisLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteClickListener.onNoteEllipsisClick(view, mNote, holder.ellipsisLayout);
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteClickListener.onNoteShareClick(view, mNote);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteClickListener.onNoteClick(view, mNote);
            }
        });
    }

    private void initCommentHeaderViewHolder(final CommentsHeaderViewHolder holder){
        holder.replays.setText(Utilities.getCommentVariation(mNote.getComments(), context));
    }

    private void initCommentMiddleViewHolder(final CommentsMiddleViewHolder holder, final Comment comment){
        holder.username.setText(comment.getUsername());
        holder.date.setText(Utilities.decodeDate(comment.getDate(), context));
        holder.content.setText(comment.getContent());
        holder.likes.setText(String.valueOf(comment.getLikes()));
        holder.replays.setText(String.valueOf(comment.getReplays()));

        if (comment.getLiked()){
            holder.likeText.setTextColor(Color.BLUE);
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentLikeClick(v, comment);
            }
        });

        holder.replayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentReplayClick(holder.itemView, comment);
            }
        });

        holder.ellipsisLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentEllipsisClick(v, comment, holder.ellipsisLayout);
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentShareClick(v, comment);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentClick(v, comment);
            }
        });
    }

    private void initCommentFooterViewHolder(final CommentsFooterViewHolder holder){
        holder.refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsFooterClickListener.onRefreshClick();
            }
        });
    }

    public CommentAdapter(List<Comment> comments, Context context, Note note){
        this.comments = comments;
        this.context = context;
        this.mNote = note;
    }

    public void initListeners(NoteDetailsClickListener noteClickListener, CommentClickListener commentClickListener,
                              OnEndScrolled onEndScrolled, CommentsFooterClickListener commentsFooterClickListener){
        this.noteClickListener = noteClickListener;
        this.commentClickListener = commentClickListener;
        this.onEndScrolled = onEndScrolled;
        this.commentsFooterClickListener = commentsFooterClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_HEADER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_details_header, parent, false);
            return new HeaderViewHolder(view);
        }
        if (viewType == VIEW_COMMENTS_HEADER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_details_recycler_header, parent, false);
            return new CommentsHeaderViewHolder(view);
        }
        if (viewType == VIEW_COMMENTS_MIDDLE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_details_recycler_middle, parent, false);
            return new CommentsMiddleViewHolder(view);
        }
        if (viewType == VIEW_COMMENTS_FOOTER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_details_recycler_footer, parent, false);
            return new CommentsFooterViewHolder(view);
        }
        if (viewType == VIEW_COMMENTS_LOADING){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar_item, parent, false);
            return new ProgressViewHolder(view);
        }
        return null;
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
    public int getItemCount() {
        return comments.size();
    }

    public void insert(Comment comment){
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
    }

    public void insert(List<Comment> list){
        for (Comment comment : list){
            comments.add(comment);
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

