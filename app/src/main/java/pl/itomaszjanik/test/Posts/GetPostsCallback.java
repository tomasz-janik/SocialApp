package pl.itomaszjanik.test.Posts;

import pl.itomaszjanik.test.Comment;
import pl.itomaszjanik.test.Note;

import java.util.List;

public interface GetPostsCallback {
    void getPostSucceeded(List<Note> list);
    void getPostFailed();
    void getPostNoInternet();
}
