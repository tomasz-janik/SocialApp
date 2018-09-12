package pl.itomaszjanik.test.Comments;

import android.view.View;
import pl.itomaszjanik.test.Comment;

public interface UpdateCommentCallback {
    void updateCommentSucceeded(Comment comment);
    void updateCommentFailed();
}
