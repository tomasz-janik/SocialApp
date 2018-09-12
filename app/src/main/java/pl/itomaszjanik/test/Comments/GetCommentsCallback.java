package pl.itomaszjanik.test.Comments;

import pl.itomaszjanik.test.Comment;

import java.util.List;

public interface GetCommentsCallback {
    void getCommentSucceeded(List<Comment> list);
    void getCommentFailed();
    void getCommentNoInternet();
}
