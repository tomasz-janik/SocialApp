package pl.itomaszjanik.test.Comments;

import android.view.View;
import android.widget.RelativeLayout;
import pl.itomaszjanik.test.Comment;

public interface CommentClickListener {
    void onCommentClick(View v, Comment comment);
    void onCommentLikeClick(View v, Comment comment);
    void onCommentReplayClick(View v, Comment comment);
    void onCommentEllipsisClick(View v, Comment comment, RelativeLayout layout);
    void onCommentShareClick(View v, Comment comment);
}
