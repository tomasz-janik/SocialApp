package pl.itomaszjanik.test.Remote;

public interface GenerateIDCallback {
    void onGenerateSuccess(String username, int userID, int task);
    void onGenerateFailed(int task);
    void onGenerateNoInternet(int task);

}
