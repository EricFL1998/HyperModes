package com.android.deskclock.stopwatch;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LapModel {
    public static final String KEY_LAP_ITEM_COUNT = "key_lap_item_count";
    private static final String LAP_INDEX_FORMAT = "%02d";
    private static String TAG = "DC:LapModel";
    private static volatile LapModel sInstance;
    private Handler mAsyncHandler;
    private final HandlerThread mHandlerThread;
    private LapObserver mLapObserver;
    private Handler mMainHandler;
    private SharedPreferences mPrefs;
    private final List<WeakReference<LapObserver>> mLapObservers = new ArrayList();
    private List<LapBean> mDataList = new ArrayList();

    public interface LapObserver {
        void onLapChanged();

        void onLapLoaded(List<LapBean> list);

        void onLastElapsedTimeGet(long j);
    }

    private LapModel() {
        HandlerThread handlerThread = new HandlerThread("LapDataThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mAsyncHandler = new Handler(handlerThread.getLooper());
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mPrefs = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext());
    }

    public static LapModel getInstance() {
        if (sInstance == null) {
            synchronized (LapModel.class) {
                if (sInstance == null) {
                    sInstance = new LapModel();
                }
            }
        }
        return sInstance;
    }

    public void registerObserver(final LapObserver lapObserver) {
        Handler handler;
        if (lapObserver != null) {
            cleanupWeakReferences();
            Iterator<WeakReference<LapObserver>> it = this.mLapObservers.iterator();
            while (it.hasNext()) {
                LapObserver lapObserver2 = it.next().get();
                if (lapObserver2 != null && lapObserver2.equals(lapObserver)) {
                    return;
                }
            }
            this.mLapObservers.add(new WeakReference<>(lapObserver));
            Log.d(TAG, "register observer: " + lapObserver);
            Log.d(TAG, "Observer registered. Total: " + this.mLapObservers.size());
            List<LapBean> list = this.mDataList;
            if (list == null || list.isEmpty() || (handler = this.mMainHandler) == null) {
                return;
            }
            handler.post(new Runnable() { // from class: com.android.deskclock.stopwatch.LapModel$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m101xf0f1fed2(lapObserver);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$registerObserver$0$com-android-deskclock-stopwatch-LapModel, reason: not valid java name */
    /* synthetic */ void m101xf0f1fed2(LapObserver lapObserver) {
        if (isObserverRegistered(lapObserver)) {
            lapObserver.onLapLoaded(new ArrayList(this.mDataList));
        }
    }

    private boolean isObserverRegistered(LapObserver lapObserver) {
        Iterator<WeakReference<LapObserver>> it = this.mLapObservers.iterator();
        while (it.hasNext()) {
            LapObserver lapObserver2 = it.next().get();
            if (lapObserver2 != null && lapObserver2.equals(lapObserver)) {
                return true;
            }
        }
        return false;
    }

    private void cleanupWeakReferences() {
        Iterator<WeakReference<LapObserver>> it = this.mLapObservers.iterator();
        while (it.hasNext()) {
            if (it.next().get() == null) {
                it.remove();
            }
        }
    }

    public void unregisterObserver(LapObserver lapObserver) {
        if (lapObserver != null) {
            Log.d(TAG, "unregister observer: " + lapObserver);
            Iterator<WeakReference<LapObserver>> it = this.mLapObservers.iterator();
            while (it.hasNext()) {
                LapObserver lapObserver2 = it.next().get();
                if (lapObserver2 == null || lapObserver2 == lapObserver) {
                    it.remove();
                }
            }
        }
        Log.d(TAG, "Observer unregistered. Total: " + this.mLapObservers.size());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyLapLoaded(final List<LapBean> list) {
        if (this.mMainHandler == null) {
            return;
        }
        cleanupWeakReferences();
        this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.stopwatch.LapModel$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m99x9508a5ed(list);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$notifyLapLoaded$1$com-android-deskclock-stopwatch-LapModel, reason: not valid java name */
    /* synthetic */ void m99x9508a5ed(List list) {
        Iterator<WeakReference<LapObserver>> it = this.mLapObservers.iterator();
        while (it.hasNext()) {
            LapObserver lapObserver = it.next().get();
            if (lapObserver != null) {
                try {
                    lapObserver.onLapLoaded(list);
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying observer onLapLoaded", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyLapChanged() {
        if (this.mMainHandler == null) {
            return;
        }
        cleanupWeakReferences();
        this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.stopwatch.LapModel$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m98xec480b47();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$notifyLapChanged$2$com-android-deskclock-stopwatch-LapModel, reason: not valid java name */
    /* synthetic */ void m98xec480b47() {
        Iterator<WeakReference<LapObserver>> it = this.mLapObservers.iterator();
        while (it.hasNext()) {
            LapObserver lapObserver = it.next().get();
            Log.d(TAG, "observer: " + lapObserver);
            if (lapObserver != null) {
                try {
                    lapObserver.onLapChanged();
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying observer onLapChanged", e);
                }
            }
        }
    }

    private void notifyLastElapsedTimeGet(final long j) {
        if (this.mMainHandler == null) {
            return;
        }
        cleanupWeakReferences();
        this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.stopwatch.LapModel$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m100x5914b85c(j);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$notifyLastElapsedTimeGet$3$com-android-deskclock-stopwatch-LapModel, reason: not valid java name */
    /* synthetic */ void m100x5914b85c(long j) {
        Iterator<WeakReference<LapObserver>> it = this.mLapObservers.iterator();
        while (it.hasNext()) {
            LapObserver lapObserver = it.next().get();
            if (lapObserver != null) {
                try {
                    lapObserver.onLastElapsedTimeGet(j);
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying observer onLastElapsedTimeGet", e);
                }
            }
        }
    }

    public void startLoad() {
        Handler handler = this.mAsyncHandler;
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.android.deskclock.stopwatch.LapModel.1
            @Override // java.lang.Runnable
            public void run() {
                final List listQuery = LapModel.this.query();
                if (LapModel.this.mMainHandler == null) {
                    return;
                }
                LapModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.stopwatch.LapModel.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        LapModel.this.mDataList.clear();
                        LapModel.this.mDataList.addAll(listQuery);
                        LapModel.this.notifyLapLoaded(LapModel.this.mDataList);
                    }
                });
            }
        });
    }

    public void insertLap(final long j, final long j2) {
        Handler handler = this.mAsyncHandler;
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.android.deskclock.stopwatch.LapModel.2
            @Override // java.lang.Runnable
            public void run() {
                final LapBean lapBeanInsert = LapModel.this.insert(j, j2);
                if (LapModel.this.mMainHandler == null) {
                    return;
                }
                LapModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.stopwatch.LapModel.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        lapBeanInsert.index = Util.formatTime(String.format(DeskClockApp.getAppDEContext().getResources().getString(R.string.lap_index), Integer.valueOf(LapModel.this.mDataList.size() + 1)), new Object[0]);
                        LapModel.this.mDataList.add(0, lapBeanInsert);
                        LapModel.this.mPrefs.edit().putInt(LapModel.KEY_LAP_ITEM_COUNT, LapModel.this.mDataList.size()).apply();
                        Log.d(LapModel.TAG, "lap count: " + LapModel.this.mPrefs.getInt(LapModel.KEY_LAP_ITEM_COUNT, 0));
                        LapModel.this.notifyLapChanged();
                    }
                });
            }
        });
    }

    public void deleteLaps() {
        Handler handler = this.mAsyncHandler;
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.android.deskclock.stopwatch.LapModel.3
            @Override // java.lang.Runnable
            public void run() {
                LapModel.this.delete();
                LapModel.this.mPrefs.edit().putInt(LapModel.KEY_LAP_ITEM_COUNT, 0).apply();
                if (LapModel.this.mMainHandler == null) {
                    return;
                }
                LapModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.stopwatch.LapModel.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        LapModel.this.mDataList.clear();
                        Log.d(LapModel.TAG, "lap count: " + LapModel.this.mPrefs.getInt(LapModel.KEY_LAP_ITEM_COUNT, 0));
                        LapModel.this.notifyLapChanged();
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<LapBean> query() {
        Log.i(TAG, "getLaps running in " + Thread.currentThread().getName());
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(Stopwatch.CONTENT_URI, Stopwatch.PROJECTION, null, null, "_id DESC");
        int count = cursorQuery.getCount();
        try {
            if (cursorQuery.moveToFirst()) {
                do {
                    long j = cursorQuery.getLong(1);
                    long j2 = cursorQuery.getLong(2);
                    int position = cursorQuery.getPosition();
                    if (position == 0) {
                        notifyLastElapsedTimeGet(j);
                    }
                    arrayList.add(new LapBean(Util.formatTime(String.format(DeskClockApp.getAppDEContext().getResources().getString(R.string.lap_index), Integer.valueOf(count - position)), new Object[0]), Util.formatElapsedTime(j), Util.formatElapsedTime(j2)));
                } while (cursorQuery.moveToNext());
            }
            return arrayList;
        } finally {
            cursorQuery.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized LapBean insert(long j, long j2) {
        long deltaTime;
        Log.i(TAG, "insertLap running in " + Thread.currentThread().getName());
        deltaTime = getDeltaTime(j, j2);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Stopwatch.Columns.TOTAL_ELAPSED_COLUMN, Long.valueOf(j));
        contentValues.put(Stopwatch.Columns.LAP_ELAPSED_COLUMN, Long.valueOf(deltaTime));
        try {
            DeskClockApp.getAppDEContext().getContentResolver().insert(Stopwatch.CONTENT_URI, contentValues);
            Log.i(TAG, "insert done");
        } catch (Exception e) {
            Log.e(TAG, "insertLap insert error: ", e);
            Log.e(TAG, "values: " + contentValues);
        }
        return new LapBean(Util.formatTime(String.format(DeskClockApp.getAppDEContext().getResources().getString(R.string.lap_index), Integer.valueOf(this.mDataList.size() + 1)), new Object[0]), Util.formatElapsedTime(j), Util.formatElapsedTime(deltaTime));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void delete() {
        Log.i(TAG, "deleteLap running in " + Thread.currentThread().getName());
        DeskClockApp.getAppDEContext().getContentResolver().delete(Stopwatch.CONTENT_URI, null, null);
    }

    public static long getDeltaTime(long j, long j2) {
        return (Math.round(j / 10.0d) - Math.round(j2 / 10.0d)) * 10;
    }

    public void release() {
        if (!this.mLapObservers.isEmpty()) {
            Log.d(TAG, "还有observer没注销，所以不release");
            return;
        }
        Handler handler = this.mAsyncHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.mAsyncHandler = null;
        }
        Handler handler2 = this.mMainHandler;
        if (handler2 != null) {
            handler2.removeCallbacksAndMessages(null);
            this.mMainHandler = null;
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
        synchronized (LapModel.class) {
            sInstance = null;
        }
        this.mLapObservers.clear();
    }

    public static class LapBean {
        public String elapsedTime;
        public String index;
        public String lapElapsedTime;

        public LapBean(String str, String str2, String str3) {
            this.index = str;
            this.elapsedTime = str2;
            this.lapElapsedTime = str3;
        }
    }
}
