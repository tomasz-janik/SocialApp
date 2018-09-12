package pl.itomaszjanik.test.Replays;

import android.view.View;
import android.widget.RelativeLayout;
import pl.itomaszjanik.test.Replay;

public interface ReplayClickListener {
    void onItemClick(View v, Replay replay);
    void onLikeClick(View v, Replay replay);
    void onReplayClick(View v, Replay replay);
    void onEllipsisClick(View v, Replay replay, RelativeLayout layout);
    void onShareClick(View v, Replay replay);
}
