package com.example.albertomartin.otrokioskitopiloto;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by AlbertoMartin on 30/03/2017.
 * Email: alberto.martin@trainingym.com
 */

public class LockerOverlayViewHandler {

    private final Context context;
    private WindowManager.LayoutParams viewLayoutParams;
    private View lockedOverlay = null;

    public LockerOverlayViewHandler(Context context) {
        this.context = context.getApplicationContext();
        initializeLayoutParams();
    }

    private void initializeLayoutParams() {
        viewLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        viewLayoutParams.gravity = Gravity.TOP | Gravity.START;
    }


    public void hideLockedOverlay() {
        if (lockedOverlay != null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(lockedOverlay);
            lockedOverlay = null;
        }
    }

    public void showLockedOverlay() {
        if (lockedOverlay != null) {
            return;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(context);
        lockedOverlay = inflater.inflate(R.layout.locked_overlay, null);
        windowManager.addView(lockedOverlay, viewLayoutParams);
    }
}
