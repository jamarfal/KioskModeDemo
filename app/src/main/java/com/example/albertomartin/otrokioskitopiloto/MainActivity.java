package com.example.albertomartin.otrokioskitopiloto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseKioskActivity {

    private KioskModePreferences kioskModePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kioskModePreferences = new KioskModePreferences(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    public void activateKioskMode(View view) {
        kioskModePreferences.setKioskModeActive(true);
        Intent serviceIntent = new Intent(this, KioskService.class);
        serviceIntent.putExtra("prueba", MainActivity.class);
        startService(serviceIntent);
    }

    public void deactivateKioskMode(View view) {
        kioskModePreferences.setKioskModeActive(false);
        stopService(new Intent(this, KioskService.class));
    }
}
