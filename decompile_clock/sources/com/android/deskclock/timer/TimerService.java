package com.android.deskclock.timer;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import com.android.deskclock.AlarmAlertWakeLock;
import com.android.deskclock.AlarmClockExtras;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.alarm.alert.AlarmService;
import com.android.deskclock.appaf.AppSearchUtil;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Notification.BackScreenNotificationUtil;
import com.android.deskclock.util.Notification.IslandNotificationUtil;
import com.android.deskclock.util.NotificationUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.fab.FabDataHelper;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.view.tab.TabViewModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TimerService extends Service {
    public static final String ACTION_STOP_TIMER = "android.intent.action.STOP_TIMER";
    public static final String ACTION_USER_SWITCHED = "android.intent.action.USER_SWITCHED";
    private static final String TAG = "DC:TimerService";
    private PowerManager.WakeLock mCpuWakeLock;
    private Handler mHandler;
    private Notification mNotification;
    private Runnable mSubBackScreenTimeSender;
    private Runnable mTicker;
    private Timer mTimer;
    private boolean mTickerStopped = true;
    private ArrayList<CallbackListener> mCallbackListener = new ArrayList<>();
    private boolean mNormalRunning = false;
    private int mMaxTimerItemCount = 6;
    private boolean mIsAddTimeTotal = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.deskclock.timer.TimerService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.f(TimerService.TAG, "TimerService receive: " + action);
            if ("android.intent.action.USER_SWITCHED".equals(action)) {
                TimerService.this.cancelTimer();
                return;
            }
            if (TimerDao.ACTION_TIMER_CANCEL.equals(action)) {
                TimerService.this.cancelTimer();
                TimerService.this.sendBroadcastToXiaoAi(TimerDao.ACTION_TIMER_CANCEL_TO_XIAOAI);
                return;
            }
            if (TimerDao.ACTION_TIMER_PAUSE.equals(action)) {
                TimerService.this.sendBroadcastToXiaoAi(TimerDao.ACTION_TIMER_PAUSE_TO_XIAOAI);
                TimerService.this.pauseTimer();
                return;
            }
            if (TimerDao.ACTION_TIMER_CONTINUE.equals(action)) {
                TimerService.this.sendBroadcastToXiaoAi(TimerDao.ACTION_TIMER_CANCEL_TO_CONTINUE);
                TimerService.this.continueTimer();
                return;
            }
            if (TimerDao.PROVIDER_TIMER_CANCEL.equals(action)) {
                TimerService.this.cancelTimer();
                return;
            }
            if (TimerDao.FUNCTION_TIMER_CANCEL.equals(action)) {
                TimerService.this.cancelTimer();
                return;
            }
            if (TimerDao.PROVIDER_TIMER_PAUSE.equals(action)) {
                TimerService.this.pauseTimer();
                return;
            }
            if (TimerDao.PROVIDER_TIMER_RESUME.equals(action)) {
                TimerService.this.continueTimer();
                return;
            }
            if ("action.timer_off".equals(action)) {
                TimerService.this.timerOff();
            } else if (TimerService.ACTION_STOP_TIMER.equals(action) && TimerService.this.mTimer.getState() == 3) {
                TimerService.this.cancelTimer();
            }
        }
    };

    public interface CallbackListener {
        void onTimerInfo(Timer timer, boolean z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBroadcastToXiaoAi(String str) {
        Intent intent = new Intent(str);
        intent.setPackage("com.miui.voiceassist");
        sendBroadcast(intent);
    }

    private void reShowTimerRunningNotification(Timer timer, boolean z) {
        long remain = timer.getRemain();
        if (remain <= 0) {
            return;
        }
        this.mTimer.setTime(System.currentTimeMillis() + remain);
        showTimerRunningNotification(this, timer, z, this.mIsAddTimeTotal, true);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return new CallbackBinder();
    }

    public class CallbackBinder extends Binder {
        public CallbackBinder() {
        }

        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "TimerService onCreate");
        this.mIsAddTimeTotal = IslandNotificationUtil.isAddTimerTotal();
        Log.d(TAG, "isAddTimeTotal: " + this.mIsAddTimeTotal);
        this.mTimer = TimerDao.getTimer(getApplicationContext());
        this.mTickerStopped = true;
        this.mHandler = new Handler();
        this.mTicker = new Runnable() { // from class: com.android.deskclock.timer.TimerService.2
            @Override // java.lang.Runnable
            public void run() {
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                if (!TimerService.this.mTickerStopped && TimerService.this.mNormalRunning) {
                    int state = TimerService.this.mTimer.getState();
                    long time = TimerService.this.mTimer.getTime() - System.currentTimeMillis();
                    if (time > 0 && state == 1) {
                        TimerService.this.mTimer.setRemain(time);
                        TimerService timerService = TimerService.this;
                        timerService.notifyTimerInfoChange(timerService.mTimer);
                    } else if (time <= 0) {
                        TimerService.this.timerOff();
                    }
                    TimerService.this.mHandler.postDelayed(TimerService.this.mTicker, Math.max(0L, (jElapsedRealtime + 20) - SystemClock.elapsedRealtime()));
                    return;
                }
                Log.d(TimerService.TAG, "timer tick stop");
            }
        };
        this.mSubBackScreenTimeSender = new Runnable() { // from class: com.android.deskclock.timer.TimerService.3
            @Override // java.lang.Runnable
            public void run() {
                if (TimerService.this.mTickerStopped || !Util.isIndependentRearDevice() || !Util.isSupportRearSmartAssistant()) {
                    TimerService.this.releaseCpuLock();
                    return;
                }
                TimerService timerService = TimerService.this;
                timerService.acquireCpuWakeLock(timerService);
                int state = TimerService.this.mTimer.getState();
                long time = TimerService.this.mTimer.getTime() - System.currentTimeMillis();
                if (time <= 0 || state != 1) {
                    return;
                }
                Log.d(TimerService.TAG, "send sub back screen timer running notification!");
                TimerService timerService2 = TimerService.this;
                BackScreenNotificationUtil.sendTimerRunningNotification(timerService2, timerService2.mTimer, true, false);
                long j = time % 60000;
                TimerService.this.mHandler.postDelayed(TimerService.this.mSubBackScreenTimeSender, j != 0 ? j : 60000L);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction(TimerDao.ACTION_TIMER_CANCEL);
        intentFilter.addAction(TimerDao.ACTION_TIMER_PAUSE);
        intentFilter.addAction(TimerDao.ACTION_TIMER_CONTINUE);
        intentFilter.addAction(TimerDao.PROVIDER_TIMER_CANCEL);
        intentFilter.addAction(TimerDao.FUNCTION_TIMER_CANCEL);
        intentFilter.addAction(TimerDao.PROVIDER_TIMER_PAUSE);
        intentFilter.addAction(TimerDao.PROVIDER_TIMER_RESUME);
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        intentFilter.addAction("action.timer_off");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction(ACTION_STOP_TIMER);
        intentFilter.setPriority(1000);
        if (Build.VERSION.SDK_INT >= 34) {
            registerReceiver(this.mReceiver, intentFilter, 2);
        } else {
            registerReceiver(this.mReceiver, intentFilter);
        }
        if (this.mTimer.getState() == 1) {
            reContinueTimer();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.d(TAG, "TimerService onStartCommand");
        if (intent == null) {
            Log.d("TimerService intent is null");
            return 2;
        }
        if (intent.hasExtra(AlarmClockExtras.TIMER_INTENT_EXTRA)) {
            long longExtra = intent.getLongExtra(AlarmClockExtras.TIMER_INTENT_EXTRA, 0L);
            long jCurrentTimeMillis = System.currentTimeMillis() + longExtra;
            Timer timer = new Timer();
            if (intent.hasExtra(AlarmHelper.ACTION_TIMER_NAME)) {
                timer.setLabel(intent.getStringExtra(AlarmHelper.ACTION_TIMER_NAME));
            }
            timer.setRemain(longExtra);
            if (intent.hasExtra(AlarmClockExtras.TIMER_INTENT_EXTRA_DURATION)) {
                timer.setDuration(intent.getLongExtra(AlarmClockExtras.TIMER_INTENT_EXTRA_DURATION, longExtra));
            } else {
                timer.setDuration(longExtra);
            }
            timer.setTime(jCurrentTimeMillis);
            startTimer(timer);
            if (intent.hasExtra(Util.IS_START_TIMER)) {
                List<TimerModel.TimerBean> timers = getTimers();
                ArrayList arrayList = new ArrayList();
                for (TimerModel.TimerBean timerBean : timers) {
                    if (timerBean.id != -1) {
                        arrayList.add(Integer.valueOf(timerBean.seconds));
                    }
                }
                if (timers != null) {
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        long j = longExtra / 1000;
                        if (j == ((Integer) it.next()).intValue()) {
                            Log.i(TAG, "addTimerHistory seconds have same " + j);
                            return 2;
                        }
                    }
                }
                if (timers != null && timers.size() < this.mMaxTimerItemCount) {
                    arrayList.add(0, Integer.valueOf((int) (longExtra / 1000)));
                } else if (timers != null && timers.size() == this.mMaxTimerItemCount) {
                    arrayList.remove(arrayList.size() - 1);
                    arrayList.add(0, Integer.valueOf((int) (longExtra / 1000)));
                }
                getContentResolver().delete(TimerHistoryTable.CONTENT_URI, null, null);
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    int iIntValue = ((Integer) it2.next()).intValue();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("seconds", Integer.valueOf(iIntValue));
                    getContentResolver().insert(TimerHistoryTable.CONTENT_URI, contentValues);
                }
            }
        }
        return 2;
    }

    private void showTimerRunningNotification(Context context, Timer timer, boolean z, boolean z2, boolean z3) {
        if (z3) {
            BackScreenNotificationUtil.sendTimerRunningNotification(context, timer, z, true);
        }
        NotificationUtil.showTimerRunningNotification(context, timer, z, z2);
    }

    private Notification getTimerRunningNotification(Context context, Timer timer, boolean z, boolean z2) {
        BackScreenNotificationUtil.sendTimerRunningNotification(context, timer, z, true);
        return NotificationUtil.getTimerRunningNotification(context, timer, z, z2);
    }

    public void startTimer(Timer timer) {
        Log.f(TAG, "startTimer: " + timer.toString());
        long remain = timer.getRemain();
        if (remain <= 0) {
            return;
        }
        AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).addAppSearchTimer(timer);
        this.mTimer.cloneFrom(timer);
        this.mTimer.setTime(System.currentTimeMillis() + remain);
        this.mTimer.setState(1);
        this.mNotification = getTimerRunningNotification(this, this.mTimer, true, this.mIsAddTimeTotal);
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(-4, this.mNotification, 1024);
        } else {
            startForeground(-4, this.mNotification);
        }
        TimerDao.saveTimer(this, this.mTimer);
        notifyTimerInfoChange(this.mTimer);
        this.mTickerStopped = false;
        this.mTicker.run();
        runSubBackScreenTimeSender();
        if (TimerDao.registerTimerToAlarmManager(this, this.mTimer) == 1) {
            acquireCpuWakeLock(this);
        }
    }

    public void pauseTimer() {
        if (this.mTimer.getState() != 0) {
            showTimerRunningNotification(this, this.mTimer, false, this.mIsAddTimeTotal, this.mTimer.getState() == 1);
        }
        if (this.mTimer.getState() != 1) {
            return;
        }
        Log.f(TAG, "pauseTimer");
        setTimerState(2);
        AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).updateAppSearchTimerToPause(this.mTimer);
        long time = this.mTimer.getTime() - System.currentTimeMillis();
        if (time > 0) {
            this.mTimer.setRemain(time);
        }
        TimerDao.updateRemainTime(this, this.mTimer.getRemain());
        this.mTickerStopped = true;
        TimerDao.unregisterTimerToAlarmManager(this);
        TimerDao.unRegisterTimerOffToAlarmManager(this);
        notifyTimerInfoChange(this.mTimer);
        releaseCpuLock();
    }

    public void continueTimer() {
        if (this.mTimer.getState() != 2) {
            showTimerRunningNotification(this, this.mTimer, true, this.mIsAddTimeTotal, this.mTimer.getState() == 2);
        } else {
            Log.f(TAG, "continueTimer");
            startTimer(this.mTimer);
            AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).updateAppSearchTimerToContinue(this.mTimer);
        }
    }

    public void reContinueTimer() {
        if (this.mTimer.getState() != 1) {
            return;
        }
        Timer timer = this.mTimer;
        timer.setRemain(timer.getTime() - System.currentTimeMillis());
        Log.d(TAG, "reContinueTimer: " + this.mTimer.toString());
        startService(new Intent(this, (Class<?>) TimerService.class));
        startTimer(this.mTimer);
        AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).updateAppSearchTimerToContinue(this.mTimer);
        showTimerRunningNotification(this, this.mTimer, true, this.mIsAddTimeTotal, true);
    }

    public void cancelTimer() {
        Log.f(TAG, "cancelTimer");
        Timer timer = this.mTimer;
        int state = timer == null ? -1 : timer.getState();
        setTimerState(0);
        this.mTimer.setRemain(0L);
        AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).deleteAppSearchTimer(this.mTimer.getId());
        this.mTickerStopped = true;
        TimerDao.unregisterTimerToAlarmManager(this);
        TimerDao.unRegisterTimerOffToAlarmManager(this);
        notifyTimerInfoChange(this.mTimer);
        this.mNotification = null;
        stopForeground(true);
        NotificationUtil.clearTimerRunningNotification(this);
        if (state != 3 || !AlarmService.getTimerAlarming()) {
            BackScreenNotificationUtil.clearTimerNotification(this);
        }
        releaseCpuLock();
        stopSelf();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void timerOff() {
        this.mHandler.post(new Runnable() { // from class: com.android.deskclock.timer.TimerService.4
            @Override // java.lang.Runnable
            public void run() {
                Log.f(TimerService.TAG, "timerOff");
                TimerService.this.setTimerState(3);
                TimerService.this.mTimer.setRemain(0L);
                TimerService.this.mTickerStopped = true;
                TimerService timerService = TimerService.this;
                timerService.notifyTimerInfoChange(timerService.mTimer);
                TimerService.this.mNotification = null;
                TimerService.this.stopForeground(true);
                TimerService.this.mHandler.postDelayed(new Runnable() { // from class: com.android.deskclock.timer.TimerService.4.1
                    @Override // java.lang.Runnable
                    public void run() {
                        NotificationUtil.clearTimerRunningNotification(TimerService.this);
                    }
                }, 500L);
                OneTrackStatHelper.recordTimerFinishAction(TimerService.this);
                TimerService.this.releaseCpuLock();
                TimerService.this.stopSelf();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTimerState(int i) {
        this.mTimer.setState(i);
        TimerDao.updateTimerState(this, i);
    }

    public void registerCallListener(CallbackListener callbackListener) {
        if (this.mCallbackListener.contains(callbackListener)) {
            Log.d(TAG, "registerCallListener, the listener has contained!");
        } else {
            this.mCallbackListener.add(callbackListener);
            callbackListener.onTimerInfo(this.mTimer, true);
        }
    }

    public void unRegisterCallbackListener(CallbackListener callbackListener) {
        this.mCallbackListener.remove(callbackListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyTimerInfoChange(Timer timer) {
        FabDataHelper.getInstance().changeFabState(TabViewModel.TAB_TIMER, timer.getState());
        ArrayList<CallbackListener> arrayList = this.mCallbackListener;
        if (arrayList == null || arrayList.size() == 0) {
            return;
        }
        Iterator<CallbackListener> it = this.mCallbackListener.iterator();
        while (it.hasNext()) {
            it.next().onTimerInfo(timer, false);
        }
    }

    public void setSilent(boolean z) {
        this.mTimer.setSilent(z);
        TimerDao.registerTimerToAlarmManager(getApplicationContext(), this.mTimer);
    }

    public void setScreenOnState(boolean z) {
        this.mTimer.setBright(z);
    }

    public int getTimerState() {
        Timer timer = this.mTimer;
        if (timer != null) {
            return timer.getState();
        }
        return 0;
    }

    private void runSubBackScreenTimeSender() {
        Handler handler;
        Runnable runnable = this.mSubBackScreenTimeSender;
        if (runnable == null || (handler = this.mHandler) == null) {
            return;
        }
        handler.removeCallbacks(runnable);
        releaseCpuLock();
        this.mSubBackScreenTimeSender.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acquireCpuWakeLock(Context context) {
        if (this.mCpuWakeLock != null || context == null) {
            return;
        }
        try {
            PowerManager.WakeLock wakeLockCreatePartialWakeLock = AlarmAlertWakeLock.createPartialWakeLock(context);
            this.mCpuWakeLock = wakeLockCreatePartialWakeLock;
            wakeLockCreatePartialWakeLock.acquire(600000L);
        } catch (Exception e) {
            Log.e(TAG, "acquireCpuWakeLock error is " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseCpuLock() {
        try {
            PowerManager.WakeLock wakeLock = this.mCpuWakeLock;
            if (wakeLock != null) {
                wakeLock.release();
                this.mCpuWakeLock = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "releaseCpuLock error is " + e);
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.d(TAG, "TimerService onDestroy");
        this.mTickerStopped = true;
        unregisterReceiver(this.mReceiver);
        super.onDestroy();
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacks(this.mTicker);
            this.mHandler.removeCallbacks(this.mSubBackScreenTimeSender);
        }
        releaseCpuLock();
    }

    public void setNormalState(boolean z) {
        Log.d(TAG, z + "setNormalState, mTickerStopped: " + this.mTickerStopped + " mNormalRunning:" + this.mNormalRunning);
        this.mNormalRunning = z;
        if (!z || this.mTickerStopped) {
            return;
        }
        Log.d(TAG, "setNormalState: ");
        this.mHandler.removeCallbacks(this.mTicker);
        this.mTicker.run();
    }

    public List<TimerModel.TimerBean> getTimers() {
        Log.i(TAG, "getTimers running in " + Thread.currentThread().getName());
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(TimerHistoryTable.CONTENT_URI, null, null, null, null);
        try {
            if (cursorQuery.moveToFirst()) {
                do {
                    arrayList.add(new TimerModel.TimerBean(Integer.valueOf(cursorQuery.getInt(0)).intValue(), Integer.valueOf(cursorQuery.getInt(1)).intValue(), cursorQuery.getString(2)));
                } while (cursorQuery.moveToNext());
            }
            return arrayList;
        } finally {
            cursorQuery.close();
        }
    }
}
