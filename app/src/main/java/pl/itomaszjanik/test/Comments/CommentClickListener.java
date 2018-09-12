package pl.itomaszjanik.test.Comments;

import android.view.View;
import android.widget.RelativeLayout;
import pl.itomaszjanik.test.Comment;

public interface CommentClickListener {
    public void onCommentClick(View v, Comment comment);

    public void onCommentLikeClick(View v, Comment comment);

    public void onCommentReplayClick(View v, Comment comment);

    public void onCommentEllipsisClick(View v, RelativeLayout layout);

    public void onCommentShareClick(View v, Comment comment);

}
