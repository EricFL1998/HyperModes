package com.android.deskclock.stopwatch;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.NotificationUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class StopWatchService extends Service {
    public static final String ACTION_STOPWATCH_CONTINUE = "com.android.deskclock.stopwatch.CONTINUE";
    public static final String ACTION_STOPWATCH_LAP = "com.android.deskclock.stopwatch.LAP";
    public static final String ACTION_STOPWATCH_PAUSE = "com.android.deskclock.stopwatch.PAUSE";
    public static final String ACTION_STOPWATCH_RESET = "com.android.deskclock.stopwatch.RESET";
    public static final String ACTION_STOPWATCH_START = "com.android.deskclock.stopwatch.START";
    private static final String TAG = "DC:StopWatchService";
    private int mCurrentLapCount;
    private long mElapsedTime;
    private LapModel mLapModel;
    private long mLastElapsedTime;
    private Notification mNotification;
    private LapModel.LapObserver mServiceObserver;
    private long mStartTime;
    private StopwatchModel mStopwatchModel;
    private SharedPreferences prefs;
    private final IBinder mBinder = new StopWatchBinder();
    private boolean mIsRunning = false;
    private boolean mIsReset = false;
    private StopWatchListener mCallbackListener = null;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.deskclock.stopwatch.StopWatchService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(StopWatchService.TAG, "StopWatchService receive: " + action);
            if (StopWatchService.ACTION_STOPWATCH_START.equals(action)) {
                StopWatchService.this.startTimer();
                return;
            }
            if (StopWatchService.ACTION_STOPWATCH_PAUSE.equals(action)) {
                StopWatchService.this.pauseTimer();
                return;
            }
            if (StopWatchService.ACTION_STOPWATCH_LAP.equals(action)) {
                StopWatchService.this.lapTimer();
            } else if (StopWatchService.ACTION_STOPWATCH_CONTINUE.equals(action)) {
                StopWatchService.this.continueTimer();
            } else if (StopWatchService.ACTION_STOPWATCH_RESET.equals(action)) {
                StopWatchService.this.resetTimer();
            }
        }
    };

    public interface StopWatchListener {
        void onStopWatchUpdate(StopwatchModel stopwatchModel);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public class StopWatchBinder extends Binder {
        public StopWatchBinder() {
        }

        StopWatchService getService() {
            return StopWatchService.this;
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "StopWatchService onCreate");
        this.prefs = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext());
        loadState();
        this.mLapModel = LapModel.getInstance();
        LapModel.LapObserver lapObserver = new LapModel.LapObserver() { // from class: com.android.deskclock.stopwatch.StopWatchService.2
            @Override // com.android.deskclock.stopwatch.LapModel.LapObserver
            public void onLapLoaded(List<LapModel.LapBean> list) {
            }

            @Override // com.android.deskclock.stopwatch.LapModel.LapObserver
            public void onLapChanged() {
                if (StopWatchService.this.mStopwatchModel != null && StopWatchService.this.mStopwatchModel.isRest()) {
                    StopWatchService.this.mCurrentLapCount = 0;
                } else {
                    StopWatchService stopWatchService = StopWatchService.this;
                    stopWatchService.showStopWatchRunningNotification(stopWatchService, true, stopWatchService.prefs.getInt(LapModel.KEY_LAP_ITEM_COUNT, 0), StopWatchService.this.mElapsedTime);
                }
            }

            @Override // com.android.deskclock.stopwatch.LapModel.LapObserver
            public void onLastElapsedTimeGet(long j) {
                StopWatchService.this.mLastElapsedTime = j;
            }
        };
        this.mServiceObserver = lapObserver;
        this.mLapModel.registerObserver(lapObserver);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STOPWATCH_START);
        intentFilter.addAction(ACTION_STOPWATCH_PAUSE);
        intentFilter.addAction(ACTION_STOPWATCH_LAP);
        intentFilter.addAction(ACTION_STOPWATCH_CONTINUE);
        intentFilter.addAction(ACTION_STOPWATCH_RESET);
        intentFilter.setPriority(1000);
        if (Build.VERSION.SDK_INT >= 34) {
            registerReceiver(this.mReceiver, intentFilter, 4);
        } else {
            registerReceiver(this.mReceiver, intentFilter);
        }
        if (this.mStopwatchModel.isRunning()) {
            try {
                reContinueTimer();
            } catch (Exception e) {
                Log.e(TAG, "reContinueTimer error", e);
            }
        }
    }

    private void reContinueTimer() {
        startService(new Intent(this, (Class<?>) StopWatchService.class));
        NotificationUtil.clearStopWatchNotification(this);
        this.mNotification = NotificationUtil.getStopwatchRunningNotification(this, true, this.prefs.getInt(LapModel.KEY_LAP_ITEM_COUNT, 0), System.currentTimeMillis() - getStartTime());
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(-8, this.mNotification, 1024);
        } else {
            startForeground(-8, this.mNotification);
        }
    }

    public void registerListener(StopWatchListener stopWatchListener) {
        this.mCallbackListener = stopWatchListener;
    }

    public void unregisterListener(StopWatchListener stopWatchListener) {
        if (this.mCallbackListener == stopWatchListener) {
            this.mCallbackListener = null;
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        Log.d(TAG, "StopWatchService onStartCommand");
        return 2;
    }

    public void startTimer() {
        startOrContinueTimer();
        NotificationUtil.clearStopWatchNotification(this);
        this.mNotification = NotificationUtil.getStopwatchRunningNotification(this, true, this.prefs.getInt(LapModel.KEY_LAP_ITEM_COUNT, 0), 0L);
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(-8, this.mNotification, 1024);
        } else {
            startForeground(-8, this.mNotification);
        }
    }

    public void startOrContinueTimer() {
        this.mIsRunning = true;
        this.mStartTime = System.currentTimeMillis() - this.mElapsedTime;
        this.mIsReset = false;
        saveState();
        Util.setBaseTime(this.prefs.edit(), this.mStartTime);
        notifyUpdate();
    }

    public void pauseTimer() {
        this.mIsRunning = false;
        this.mElapsedTime = System.currentTimeMillis() - getStartTime();
        saveState();
        notifyUpdate();
        showStopWatchRunningNotification(this, false, this.prefs.getInt(LapModel.KEY_LAP_ITEM_COUNT, 0), this.mElapsedTime);
    }

    public void continueTimer() {
        showStopWatchRunningNotification(this, true, this.prefs.getInt(LapModel.KEY_LAP_ITEM_COUNT, 0), this.mElapsedTime);
        startOrContinueTimer();
    }

    public void resetTimer() {
        OneTrackStatHelper.trackNumEvent(this.mElapsedTime, OneTrackStatHelper.STOPWATCH_DURATION);
        this.mIsRunning = false;
        this.mElapsedTime = 0L;
        this.mStartTime = 0L;
        this.mLastElapsedTime = 0L;
        this.mIsReset = true;
        saveState();
        notifyUpdate();
        this.mNotification = null;
        stopForeground(true);
        NotificationUtil.clearStopWatchNotification(this);
        LapModel lapModel = this.mLapModel;
        if (lapModel != null) {
            lapModel.deleteLaps();
        }
        stopSelf();
    }

    public void lapTimer() {
        long jCurrentTimeMillis = System.currentTimeMillis() - getStartTime();
        this.mElapsedTime = jCurrentTimeMillis;
        LapModel lapModel = this.mLapModel;
        if (lapModel != null) {
            lapModel.insertLap(jCurrentTimeMillis, this.mLastElapsedTime);
        }
        this.mLastElapsedTime = this.mElapsedTime;
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.STOPWATCH_FAB_LAP);
    }

    private void saveState() {
        Util.setElapsedTime(this.prefs.edit(), this.mElapsedTime);
        Util.setRunningState(this.prefs.edit(), this.mIsRunning);
    }

    private void loadState() {
        this.mElapsedTime = Util.getElapsedTime(this.prefs);
        this.mIsRunning = Util.getRunningState(this.prefs);
        this.mStartTime = Util.getBaseTime(this.prefs);
        this.mStopwatchModel = new StopwatchModel(this.mElapsedTime, this.mIsRunning, this.mIsReset, this.mLastElapsedTime);
    }

    private void notifyUpdate() {
        this.mStopwatchModel.setReset(this.mIsReset);
        this.mStopwatchModel.setRunning(this.mIsRunning);
        this.mStopwatchModel.setLastElapsedTime(this.mLastElapsedTime);
        this.mStopwatchModel.setElapsedTime(this.mElapsedTime);
        StopWatchListener stopWatchListener = this.mCallbackListener;
        if (stopWatchListener != null) {
            stopWatchListener.onStopWatchUpdate(this.mStopwatchModel);
            this.mIsReset = false;
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "service onDestroy");
        saveState();
        LapModel lapModel = this.mLapModel;
        if (lapModel != null) {
            lapModel.unregisterObserver(this.mServiceObserver);
            this.mLapModel.release();
            this.mLapModel = null;
        }
        try {
            unregisterReceiver(this.mReceiver);
        } catch (Exception unused) {
            Log.d(TAG, "unregisterReceiver failed");
        }
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showStopWatchRunningNotification(Context context, boolean z, int i, long j) {
        NotificationUtil.showStopWatchRunningNotification(context, z, i, j);
    }

    private long getStartTime() {
        return Util.getBaseTime(this.prefs);
    }
}
