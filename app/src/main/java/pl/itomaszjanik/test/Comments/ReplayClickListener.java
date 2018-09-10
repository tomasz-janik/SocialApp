package pl.itomaszjanik.test.Comments;

import android.view.View;
import android.widget.RelativeLayout;
import pl.itomaszjanik.test.Comment;
import pl.itomaszjanik.test.Replay;

public interface ReplayClickListener {
    public void onItemClick(View v, Replay replay);

    public void onLikeClick(View v, Replay replay);

    public void onReplayClick(View v, Replay replay);

    public void onEllipsisClick(View v, RelativeLayout layout);

    public void onShareClick(View v, Replay replay);

}
