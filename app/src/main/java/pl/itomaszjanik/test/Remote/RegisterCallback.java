package pl.itomaszjanik.test.Remote;

public interface RegisterCallback {
    void onRegisterSucceeded(String username, int userID);
    void onRegisterFailed();
    void onRegisterNotUnique();
    void onRegisterNoInternet();
}
