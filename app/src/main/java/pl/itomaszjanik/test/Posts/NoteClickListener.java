package pl.itomaszjanik.test.Posts;

import android.view.View;
import pl.itomaszjanik.test.Note;

public interface NoteClickListener {
    void onItemClick(View v, Note note);
    void onLikeClick(View v, Note note);
}
