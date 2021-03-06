package com.example.albertomartin.otrokioskitopiloto;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by AlbertoMartin on 30/03/2017.
 * Email: alberto.martin@trainingym.com
 */

public class PreventStatusBarExpansion {

    private final Context context;
    WindowManager.LayoutParams layoutParams;
    private CustomViewGroup mView;

    public PreventStatusBarExpansion(Context context) {
        this.context = context.getApplicationContext();

        initializeLayoutParams();
    }

    private void initializeLayoutParams() {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        layoutParams.gravity = Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            // Use Fallback size:
            result = 60; // 60px Fallback
        }

        layoutParams.height = result;
        layoutParams.format = PixelFormat.TRANSPARENT;
    }

    public void activate() {
        if (mView != null) {
            return;
        }

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mView = new CustomViewGroup(context);
        windowManager.addView(mView, layoutParams);
    }

    public void disable() {
        if (mView != null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(mView);
            mView = null;
        }
    }

    public static class CustomViewGroup extends ViewGroup {
        public CustomViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            // Intercepted touch!
            return true;
        }
    }
}
