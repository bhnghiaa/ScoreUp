package com.example.scoreup;

public class GlobalData extends android.app.Application {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
