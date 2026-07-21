package com.android.deskclock.alarm.lifepost;

import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.alarm.lifepost.model.LifePost;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DataModel {
    private Handler mAsyncHandler;
    private List<DataBean> mDataList = new ArrayList();
    private Handler mMainHandler;
    private Observer mObserver;

    public interface Observer {
        void onDataLoaded(List<DataBean> list);
    }

    public DataModel(Observer observer) {
        HandlerThread handlerThread = new HandlerThread("LapDataThread");
        handlerThread.start();
        this.mAsyncHandler = new Handler(handlerThread.getLooper());
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mObserver = observer;
    }

    public void startLoad() {
        Handler handler = this.mAsyncHandler;
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.android.deskclock.alarm.lifepost.DataModel.1
            @Override // java.lang.Runnable
            public void run() {
                final List listQuery = DataModel.this.query();
                if (DataModel.this.mMainHandler == null) {
                    return;
                }
                DataModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.lifepost.DataModel.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        DataModel.this.mDataList.clear();
                        DataModel.this.mDataList.addAll(listQuery);
                        if (DataModel.this.mObserver != null) {
                            DataModel.this.mObserver.onDataLoaded(DataModel.this.mDataList);
                        }
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<DataBean> query() {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(LifePost.CONTENT_URI, LifePost.PROJECTION, null, null, "_id DESC");
        try {
            if (cursorQuery.moveToFirst()) {
                do {
                    arrayList.add(new DataBean(cursorQuery.getLong(1), cursorQuery.getInt(2)));
                } while (cursorQuery.moveToNext());
            }
            return arrayList;
        } finally {
            cursorQuery.close();
        }
    }

    public void release() {
        Handler handler = this.mAsyncHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        Handler handler2 = this.mMainHandler;
        if (handler2 != null) {
            handler2.removeCallbacksAndMessages(null);
        }
    }

    public class DataBean {
        public int percentage;
        public long time;

        public DataBean(long j, int i) {
            this.time = j;
            this.percentage = i;
        }
    }
}
