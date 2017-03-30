package com.example.albertomartin.otrokioskitopiloto;

import android.content.Context;
import android.content.SharedPreferences;

public class KioskModePreferences {
    private static final String KIOSK_MODE_PREFERENCES = "kiosk_mode_preferences";

    private final Context context;

    public KioskModePreferences(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean isKioskModeActive() {
        SharedPreferences preferences = getPreferences();
        return preferences.getBoolean(KIOSK_MODE_PREFERENCES, false);
    }

    public void setKioskModeActive(final boolean active) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(KIOSK_MODE_PREFERENCES, active);
        editor.apply();
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(KIOSK_MODE_PREFERENCES, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        SharedPreferences preferences = getPreferences();
        return preferences.edit();
    }


}
