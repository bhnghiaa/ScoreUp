package com.example.scoreup;


import android.app.Activity;
import android.content.Context;

import java.util.Locale;

public class LocaleManager {
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_VIETNAMESE = "vi";
    public static final String LANGUAGE_JAPANESE = "ja";

    public LocaleManager(Context context, Activity activity) {
        this.baseContext = context;
        this.savedConfiguration = new SavedConfiguration<>(activity, "locale", "locale");

        if (!savedConfiguration.getValue().isPresent()) {
            savedConfiguration.save(Locale.getDefault().getLanguage());
        }

        setLocale(savedConfiguration.getValue().get());
    }

    public void setLocale(String localeCode) {
        String crrContextLocale = baseContext.getResources().getConfiguration().getLocales().get(0).getLanguage();

        if (crrContextLocale.equals(localeCode)) {
            return;
        }

        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        android.content.res.Configuration config = baseContext.getResources().getConfiguration();
        config.setLocale(locale);
        baseContext.getResources().updateConfiguration(config, baseContext.getResources().getDisplayMetrics());
        savedConfiguration.save(localeCode);
    }
    public String getLocale() {
        return savedConfiguration.getValue().get();
        /*return baseContext.getResources().getConfiguration().locale.getLanguage();*/

    }

    private final Context baseContext;
    private final SavedConfiguration<String> savedConfiguration;
}

