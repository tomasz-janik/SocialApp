package pl.itomaszjanik.test;

import android.view.View;
import android.widget.RelativeLayout;

public interface CommentClickListener {
    public void onItemClick(View v, Comment comment);

    public void onLikeClick(View v, RelativeLayout layout);

    public void onReplayClick(View v, Comment comment);

}
