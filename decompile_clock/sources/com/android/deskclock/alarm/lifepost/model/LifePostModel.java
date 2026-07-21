package com.android.deskclock.alarm.lifepost.model;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.bedtime.HealthDataUtil;
import com.android.deskclock.alarm.bedtime.SleepReport;
import com.android.deskclock.alarm.lifepost.LifePostUtils;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.Log;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class LifePostModel implements ILifePostModel {
    private Handler mAsyncHandler;
    private Context mContext = DeskClockApp.getAppDEContext();
    private Handler mMainHandler;
    private WakeUpAcumulateDayTask mWakeUpAcumulateDayTask;
    private int mWakeUpPercentage;
    private WakeUpSaveAsynTask mWakeUpSaveAsynTask;
    private long mWakeUpTime;
    private OnLoadListener onLoadListener;
    SleepReportObserver sleepReportObserver;

    public interface OnLoadListener {
        void onLoadAcumulateDay(int i);

        void onLoadSleepReport(SleepReport sleepReport);
    }

    public LifePostModel(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    @Override // com.android.deskclock.alarm.lifepost.model.ILifePostModel
    public void loadAcumulateDay() {
        if (this.mWakeUpAcumulateDayTask == null) {
            WakeUpAcumulateDayTask wakeUpAcumulateDayTask = new WakeUpAcumulateDayTask(this);
            this.mWakeUpAcumulateDayTask = wakeUpAcumulateDayTask;
            wakeUpAcumulateDayTask.execute(new Void[0]);
        }
    }

    @Override // com.android.deskclock.alarm.lifepost.model.ILifePostModel
    public void loadSleepReport() {
        HandlerThread handlerThread = new HandlerThread("LifePostThread");
        handlerThread.start();
        this.mAsyncHandler = new Handler(handlerThread.getLooper());
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.sleepReportObserver = new SleepReportObserver(new Handler(), this.onLoadListener, this);
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.lifepost.model.LifePostModel.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (LifePostModel.this.mContext == null || LifePostModel.this.onLoadListener == null || !HealthDataUtil.isHealthAppValuable(LifePostModel.this.mContext)) {
                        return;
                    }
                    LifePostModel.this.mContext.getContentResolver().registerContentObserver(Uri.parse("content://com.mi.health.provider.main/sleep/report"), false, LifePostModel.this.sleepReportObserver);
                } catch (Exception unused) {
                }
            }
        });
        querySleepReport(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void querySleepReport(final boolean z) {
        Log.i("DC:LifePostModel", "querySleepReport ");
        try {
            this.mAsyncHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.lifepost.model.LifePostModel.2
                @Override // java.lang.Runnable
                public void run() throws Throwable {
                    if (LifePostModel.this.mContext != null && BedtimeUtil.bedTimeAlarmCompleted(LifePostModel.this.mContext) && BedtimeUtil.isBedtimeOpen(LifePostModel.this.mContext) && HealthDataUtil.isHealthAppValuable(LifePostModel.this.mContext)) {
                        final SleepReport sleepReportQuerySleepReport = HealthDataUtil.querySleepReport(LifePostModel.this.mContext, 0L, z);
                        Log.i("DC:LifePostModel", "querySleepReport: " + sleepReportQuerySleepReport);
                        LifePostModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.lifepost.model.LifePostModel.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (LifePostModel.this.onLoadListener == null || sleepReportQuerySleepReport == null) {
                                    return;
                                }
                                Log.i("DC:LifePostModel", "LoadSleepReport: " + sleepReportQuerySleepReport);
                                LifePostModel.this.onLoadListener.onLoadSleepReport(sleepReportQuerySleepReport);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e("DC:LifePostModel", "querySleepReport, error " + e.getMessage());
        }
    }

    @Override // com.android.deskclock.alarm.lifepost.model.ILifePostModel
    public void saveWakeUpDay(long j, int i) {
        this.mWakeUpTime = j;
        this.mWakeUpPercentage = i;
        if (this.mWakeUpSaveAsynTask == null) {
            WakeUpSaveAsynTask wakeUpSaveAsynTask = new WakeUpSaveAsynTask();
            this.mWakeUpSaveAsynTask = wakeUpSaveAsynTask;
            wakeUpSaveAsynTask.execute(new Void[0]);
        }
    }

    private static class WakeUpAcumulateDayTask extends AsyncTask<Void, Void, Integer> {
        WeakReference<LifePostModel> mReference;

        public WakeUpAcumulateDayTask(LifePostModel lifePostModel) {
            this.mReference = new WeakReference<>(lifePostModel);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Integer doInBackground(Void... voidArr) {
            return Integer.valueOf(LifePostUtils.queryAcumulateWakeUpDays(DeskClockApp.getAppDEContext()) + 1);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Integer num) {
            WeakReference<LifePostModel> weakReference = this.mReference;
            LifePostModel lifePostModel = weakReference == null ? null : weakReference.get();
            if (lifePostModel != null) {
                lifePostModel.onLoadAcumulateDay(num.intValue());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLoadAcumulateDay(int i) {
        OnLoadListener onLoadListener = this.onLoadListener;
        if (onLoadListener != null) {
            onLoadListener.onLoadAcumulateDay(i);
        }
    }

    private class WakeUpSaveAsynTask extends AsyncTask<Void, Void, Void> {
        private WakeUpSaveAsynTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            if (LifePostModel.this.mWakeUpTime != -1 && LifePostModel.this.mContext != null) {
                LifePostUtils.recordWakeUp(LifePostModel.this.mContext, LifePostModel.this.mWakeUpTime, LifePostModel.this.mWakeUpPercentage);
            }
            return null;
        }
    }

    @Override // com.android.deskclock.alarm.lifepost.model.ILifePostModel
    public void onDestroy() {
        WakeUpAcumulateDayTask wakeUpAcumulateDayTask = this.mWakeUpAcumulateDayTask;
        if (wakeUpAcumulateDayTask != null) {
            wakeUpAcumulateDayTask.cancel(true);
            this.mWakeUpAcumulateDayTask = null;
        }
        if (this.sleepReportObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.sleepReportObserver);
        }
        Handler handler = this.mAsyncHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        Handler handler2 = this.mMainHandler;
        if (handler2 != null) {
            handler2.removeCallbacksAndMessages(null);
        }
    }

    private static class SleepReportObserver extends ContentObserver {
        private WeakReference<LifePostModel> mLifePostModel;
        private WeakReference<OnLoadListener> mOnLoadListener;

        public SleepReportObserver(Handler handler, OnLoadListener onLoadListener, LifePostModel lifePostModel) {
            super(handler);
            this.mLifePostModel = new WeakReference<>(lifePostModel);
            this.mOnLoadListener = new WeakReference<>(onLoadListener);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            Log.i("DC:LifePost", "SleepReportObserver onChange ");
            LifePostModel lifePostModel = this.mLifePostModel.get();
            OnLoadListener onLoadListener = this.mOnLoadListener.get();
            if (lifePostModel == null || onLoadListener == null) {
                return;
            }
            lifePostModel.querySleepReport(false);
        }
    }
}
