package pl.itomaszjanik.test.Comments;

import android.view.View;
import android.widget.RelativeLayout;
import pl.itomaszjanik.test.Comment;

public interface CommentClickListener {
    public void onItemClick(View v, Comment comment);

    public void onLikeClick(View v, RelativeLayout layout);

    public void onReplayClick(View v, Comment comment);

}
