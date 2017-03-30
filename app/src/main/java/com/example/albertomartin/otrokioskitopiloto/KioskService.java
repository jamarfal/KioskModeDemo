package com.example.albertomartin.otrokioskitopiloto;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class KioskService extends Service {
    private static final long INTERVAL = TimeUnit.MILLISECONDS.toMillis(200); // periodic interval to check in seconds -> 2
    private static final String TAG = KioskService.class.getSimpleName();

    private Context context = null;
    private boolean running = false;
    private KioskModePreferences kioskModePreferences;
    private LockerOverlayViewHandler lockerOverlayViewHandler;
    private Handler mHandler;
    private Class classToOpenInIntent;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                handleKioskMode();
                mHandler.postDelayed(mRunnable, INTERVAL);
            } else {
                mHandler.removeCallbacks(mRunnable);
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        lockerOverlayViewHandler = new LockerOverlayViewHandler(context);
        kioskModePreferences = new KioskModePreferences(context);
        mHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping service 'KioskService'");
        running = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service 'KioskService'");
        classToOpenInIntent = (Class) intent.getExtras().get("prueba");
        running = true;
        mHandler.postDelayed(mRunnable, INTERVAL);
        return Service.START_NOT_STICKY;
    }

    private void handleKioskMode() {
        if (kioskModePreferences.isKioskModeActive()) {
            Log.i(TAG, "Kiosk mode is active'");
            if (isMyAppInForeground()) {
                Log.i(TAG, "App in foreground'");
                lockerOverlayViewHandler.hideLockedOverlay();
            } else {
                lockerOverlayViewHandler.showLockedOverlay();
                restoreApp();
                Log.i(TAG, "App in background'");
            }
        } else {
            Log.i(TAG, "Kiosk mode is not active'");
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private boolean isMyAppInForeground() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();

        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningProcesses.get(0);
        if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            String activeProcess = runningAppProcessInfo.pkgList[0];
            if (activeProcess.equalsIgnoreCase(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void restoreApp() {
        Intent i = new Intent(context, classToOpenInIntent);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }
}