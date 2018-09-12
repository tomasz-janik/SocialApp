package pl.itomaszjanik.test.Posts;

import android.view.View;
import android.widget.RelativeLayout;
import pl.itomaszjanik.test.Note;

public interface NoteDetailsClickListener {
    void onNoteClick(View v, Note note);
    void onNoteLikeClick(View v, Note note);
    void onNoteCommentClick(View v, Note note);
    void onNoteEllipsisClick(View v, Note note, RelativeLayout layout);
    void onNoteShareClick(View v, Note note);
}
