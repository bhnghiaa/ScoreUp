package com.example.scoreup;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;
import java.util.Optional;



public class SavedConfiguration<T> {
    public SavedConfiguration(Activity activity, String storage, String key) {
        baseContext = activity.getBaseContext();
        preferences = baseContext.getSharedPreferences(storage, Context.MODE_PRIVATE);
        this.key = key;
    }

    public void save(T value) {
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            throw new IllegalArgumentException(String.format(Locale.US, "Unsupported type: %s", value.getClass().getName()));
        }
        editor.apply();
    }

    public Optional<T> getValue() {
        if (preferences.contains(key)) {
            Object value = preferences.getAll().get(key);
            return Optional.of((T) value);
        }
        return Optional.empty();
    }

    public void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    private final Context baseContext;
    private final SharedPreferences preferences;
    private final String key;
}

