package pl.itomaszjanik.test.Comments;

import pl.itomaszjanik.test.Comment;

public interface CommentPostCallback {
    void commentPostSucceeded(Comment comment);
    void commentPostFailed();
    void commentPostNoInternet();
}
