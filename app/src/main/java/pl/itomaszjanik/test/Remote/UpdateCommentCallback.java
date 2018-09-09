package pl.itomaszjanik.test.Remote;

import pl.itomaszjanik.test.Comment;

public interface UpdateCommentCallback {
    void updateCommentSucceeded(Comment comment);
    void updateCommentFailed();
}
