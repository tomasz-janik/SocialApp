package pl.itomaszjanik.test.Remote;

public interface RemoveCallback {
    void onRemovePostSuccess();
    void onRemoveCommentSuccess();
    void onRemoveReplaySuccess();
    void onRemoveFailed();
    void onRemoveNoInternet();

}
