package pl.itomaszjanik.test.Replays;

import android.view.View;
import pl.itomaszjanik.test.Comment;
import pl.itomaszjanik.test.Replay;

public interface ReactReplayCallback {
    void reactReplayLikeSucceeded(Replay replay, View view);
    void reactReplayLikeFailed();
    void reactReplayUnlikeSucceeded(Replay replay, View view);
    void reactReplayUnlikeFailed();
    void reactReplayNoInternet();
}
