package pl.itomaszjanik.test.Remote;

public interface RegisterCallback {
    void onRegisterSucceeded();
    void onRegisterFailed();
    void onRegisterNotUnique();
    void onRegisterNoInternet();
}
