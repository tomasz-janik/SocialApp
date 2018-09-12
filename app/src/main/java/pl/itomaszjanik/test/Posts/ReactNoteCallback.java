package pl.itomaszjanik.test.Posts;

import android.view.View;
import pl.itomaszjanik.test.Comment;
import pl.itomaszjanik.test.Note;

public interface ReactNoteCallback {
    void reactNoteLikeSucceeded(Note note, View view);
    void reactNoteLikeFailed();
    void reactNoteUnlikeSucceeded(Note note, View view);
    void reactNoteUnlikeFailed();
    void reactNoteNoInternet();
}
