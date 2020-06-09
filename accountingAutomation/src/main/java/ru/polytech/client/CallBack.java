package ru.polytech.client;

public class CallBack<T> {

    public void onSuccess(T result) {
    }

    public void onFail(String message, int requestCode) {

    }

    public void onFailure(String message) {
        System.err.println(message);
    }

}
