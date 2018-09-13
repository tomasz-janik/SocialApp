package pl.itomaszjanik.test.Posts;

import pl.itomaszjanik.test.Note;

public interface UpdatePostCallback {
    void updatePostSucceeded(Note note);
    void updatePostFailed();
}
