package com.example.albertomartin.otrokioskitopiloto;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class KioskService extends Service {
    private static final long INTERVAL = TimeUnit.MILLISECONDS.toMillis(200); // periodic interval to check in seconds -> 2
    private static final String TAG = KioskService.class.getSimpleName();

    private Thread thread = null;
    private Context context = null;
    private boolean running = false;
    private KioskModePreferences kioskModePreferences;
    private Handler mHandler;
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
        running = true;
        mHandler.postDelayed(mRunnable, INTERVAL);

        // start a thread that periodically checks if your app is in the foreground

      /*  thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    handleKioskMode();
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        Log.i(TAG, "Thread interrupted: 'KioskService'");
                    }
                } while (running);
                stopSelf();
            }
        });*/

        //thread.start();
        return Service.START_NOT_STICKY;
    }

    private void handleKioskMode() {


        // is Kiosk Mode active?
        if (kioskModePreferences.isKioskModeActive()) {

            // is App in background?
            Log.i(TAG, "Kiosk mode is active'");
            if (isMyAppInForeground()) {
                Log.i(TAG, "App in foreground'");
                hideLockedOverlay();
                //restoreApp();
            } else {
                showLockedOverlay();
                restoreApp();
             /*   new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showLockedOverlay();
                    }
                });*/
                Log.i(TAG, "App in background'");
            }
        } else {
            Log.i(TAG, "Kiosk mode is not active'");
        }

    }


    private boolean isInBackground() {

        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeDialog);

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;

        return (!context.getApplicationContext().getPackageName().equals(componentInfo.getPackageName()));

    }

    public boolean isBackgroundRunning() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        //If your app is the process in foreground, then it's not in running in background
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isMyAppInForeground() {
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

        // Restart activity
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private View lockedOverlay = null;
    //

    private void hideLockedOverlay() {
        if (lockedOverlay != null) {
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.removeView(lockedOverlay);
            lockedOverlay = null;
        }
    }

    private void showLockedOverlay() {
        if (lockedOverlay != null) {
            return;
        }

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams viewLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        viewLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        LayoutInflater inflater = LayoutInflater.from(this);
        lockedOverlay = inflater.inflate(R.layout.locked_overlay, null);
        windowManager.addView(lockedOverlay, viewLayoutParams);
    }
}