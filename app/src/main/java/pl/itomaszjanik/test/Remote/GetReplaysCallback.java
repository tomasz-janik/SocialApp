package pl.itomaszjanik.test.Remote;

import pl.itomaszjanik.test.Comment;
import pl.itomaszjanik.test.Replay;

import java.util.List;

public interface GetReplaysCallback {
    void getReplaySucceeded(List<Replay> list);
    void getReplayFailed();
    void getReplayNoInternet();
}
