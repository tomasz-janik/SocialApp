package pl.itomaszjanik.test.Remote;

import pl.itomaszjanik.test.Comment;

public interface CommentPostCallback {
    void commentPostSucceeded(Comment comment);
    void commentPostFailed();
    void commentPostNoInternet();
}
