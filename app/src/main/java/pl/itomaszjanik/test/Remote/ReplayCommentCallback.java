package pl.itomaszjanik.test.Remote;

import pl.itomaszjanik.test.Comment;
import pl.itomaszjanik.test.Replay;

public interface ReplayCommentCallback {
    void replayCommentSucceeded(Replay comment);
    void replayCommentFailed();
    void replayCommentNoInternet();
}
