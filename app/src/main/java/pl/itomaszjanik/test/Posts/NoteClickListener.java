package pl.itomaszjanik.test.Posts;

import android.view.View;
import pl.itomaszjanik.test.Note;

public interface NoteClickListener {
    public void onItemClick(View v, Note note);
    public void onLikeClick(View v, Note note);
    public void onCommentClick(View v, Note note);
}
