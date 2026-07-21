package com.android.deskclock.timer;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.util.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TimerModel {
    private static String TAG = "DC:TimerModel";
    private Handler mAsyncHandler;
    private ContentObserver mContentObserver;
    private Context mContext;
    private final HandlerThread mHandlerThread;
    private Handler mMainHandler;
    private TimerObserver mTimerObserver;
    private List<TimerBean> mTimers = new ArrayList();

    public interface TimerObserver {
        void onTimersChanged(int i);

        void onTimersLoaded(List<TimerBean> list);
    }

    public TimerModel(Context context, TimerObserver timerObserver) {
        HandlerThread handlerThread = new HandlerThread("TimerDataThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mAsyncHandler = new Handler(handlerThread.getLooper());
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mContext = context;
        this.mTimerObserver = timerObserver;
        this.mContentObserver = new ContentObserver(this.mAsyncHandler) { // from class: com.android.deskclock.timer.TimerModel.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                final List<TimerBean> timers = TimerModel.this.getTimers();
                TimerModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.timer.TimerModel.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        int size = TimerModel.this.mTimers.size();
                        int size2 = timers.size();
                        int size3 = TimerModel.this.mTimers.size() - 1;
                        if (size2 > size) {
                            for (int i = 0; i < timers.size(); i++) {
                                if (!TimerModel.this.mTimers.contains((TimerBean) timers.get(i))) {
                                    size3 = i;
                                    break;
                                }
                            }
                        }
                        TimerModel.this.mTimers.clear();
                        TimerModel.this.mTimers.addAll(timers);
                        if (TimerModel.this.mTimerObserver != null) {
                            if (size2 > size) {
                                TimerModel.this.mTimerObserver.onTimersChanged(size3);
                            } else {
                                TimerModel.this.mTimerObserver.onTimersChanged(-1);
                            }
                        }
                    }
                });
            }
        };
        this.mContext.getContentResolver().registerContentObserver(TimerHistoryTable.CONTENT_URI, true, this.mContentObserver);
    }

    public void release() {
        if (this.mContentObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
    }

    public void startLoad() {
        this.mAsyncHandler.post(new Runnable() { // from class: com.android.deskclock.timer.TimerModel.2
            @Override // java.lang.Runnable
            public void run() {
                final List<TimerBean> timers = TimerModel.this.getTimers();
                TimerModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.timer.TimerModel.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        TimerModel.this.mTimers.clear();
                        TimerModel.this.mTimers.addAll(timers);
                        if (TimerModel.this.mTimerObserver != null) {
                            TimerModel.this.mTimerObserver.onTimersLoaded(TimerModel.this.mTimers);
                        }
                    }
                });
            }
        });
    }

    public List<TimerBean> getTimers() {
        Log.i(TAG, "getTimers running in " + Thread.currentThread().getName());
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(TimerHistoryTable.CONTENT_URI, null, null, null, null);
        try {
            if (cursorQuery.moveToFirst()) {
                do {
                    arrayList.add(new TimerBean(Integer.valueOf(cursorQuery.getInt(0)).intValue(), Integer.valueOf(cursorQuery.getInt(1)).intValue(), cursorQuery.getString(2)));
                } while (cursorQuery.moveToNext());
            }
            return arrayList;
        } finally {
            cursorQuery.close();
        }
    }

    public static class TimerBean implements Serializable {
        public String desc;
        public int id;
        public int seconds;

        public TimerBean(int i, int i2, String str) {
            this.id = i;
            this.seconds = i2;
            this.desc = str;
        }

        public boolean equals(Object obj) {
            return (obj instanceof TimerBean) && this.seconds == ((TimerBean) obj).seconds;
        }

        public int getId() {
            return this.id;
        }

        public void setId(int i) {
            this.id = i;
        }

        public int getSeconds() {
            return this.seconds;
        }

        public void setSeconds(int i) {
            this.seconds = i;
        }

        public String getDesc() {
            return this.desc;
        }

        public void setDesc(String str) {
            this.desc = str;
        }
    }
}
