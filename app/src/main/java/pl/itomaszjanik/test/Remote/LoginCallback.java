package pl.itomaszjanik.test.Remote;

public interface LoginCallback {
    void onLoginSucceeded(String username, int userID);
    void onLoginWrongPassword();
    void onLoginFailed();
    void onLoginNoInternet();
}
