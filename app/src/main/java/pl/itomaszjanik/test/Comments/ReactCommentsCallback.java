package pl.itomaszjanik.test.Comments;

import android.view.View;
import pl.itomaszjanik.test.Comment;

import java.util.List;

public interface ReactCommentsCallback {
    void reactCommentLikeSucceeded(Comment comment, View view);
    void reactCommentLikeFailed();
    void reactCommentUnlikeSucceeded(Comment comment, View view);
    void reactCommentUnlikeFailed();
    void reactCommentNoInternet();
}
