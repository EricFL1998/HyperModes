package com.android.deskclock.addition.xiaoai;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import com.android.deskclock.Alarm;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class XiaoAiRingtonePlayer {
    private static final int DELAY_TIME = 2000;
    private static final String TAG = "DC:XiaoAiRingtonePlayer";
    private Handler mHandler = new Handler();
    private boolean mIsOnLineRes;
    private OnPlayListener mListener;
    private AsyncTask<Void, Void, Boolean> mLoadResAsyncTask;
    private boolean mQueryDone;
    private boolean mWillNewsPlay;

    public interface OnPlayListener {
        void onBgmPlayed(Uri uri, boolean z);

        void onError();

        void onNewsPlayed(Uri uri);

        void onTtsPlayed(Uri uri, boolean z);
    }

    public XiaoAiRingtonePlayer(OnPlayListener onPlayListener) {
        this.mListener = onPlayListener;
    }

    public void play(Alarm alarm) {
        Log.d(TAG, "play: ");
        if (XiaoAiRingtoneHelper.getAlertTime() == alarm.time) {
            Log.d(TAG, "same timestamp");
            this.mIsOnLineRes = XiaoAiRingtoneHelper.isOnLineRes();
            this.mWillNewsPlay = XiaoAiRingtoneHelper.getNeedPlayNews();
            Log.d(TAG, "play onLine res: " + this.mIsOnLineRes + ", play news: " + this.mWillNewsPlay);
            playBgm();
            return;
        }
        Log.d(TAG, "reload res");
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.deskclock.addition.xiaoai.XiaoAiRingtonePlayer.1
            @Override // java.lang.Runnable
            public void run() {
                if (XiaoAiRingtonePlayer.this.mQueryDone || XiaoAiRingtonePlayer.this.mListener == null) {
                    return;
                }
                XiaoAiRingtonePlayer.this.mListener.onError();
            }
        }, 2000L);
        reLoadRes();
    }

    private void reLoadRes() {
        cancelResLoadTask();
        this.mQueryDone = false;
        CheckAndLoadResAsyncTask checkAndLoadResAsyncTask = new CheckAndLoadResAsyncTask(this);
        this.mLoadResAsyncTask = checkAndLoadResAsyncTask;
        checkAndLoadResAsyncTask.execute(new Void[0]);
    }

    private void cancelResLoadTask() {
        AsyncTask<Void, Void, Boolean> asyncTask = this.mLoadResAsyncTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.mLoadResAsyncTask = null;
        }
    }

    public void clear() {
        Log.d(TAG, "clear: ");
        this.mListener = null;
        cancelResLoadTask();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    private static class CheckAndLoadResAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<XiaoAiRingtonePlayer> mReference;

        public CheckAndLoadResAsyncTask(XiaoAiRingtonePlayer xiaoAiRingtonePlayer) {
            this.mReference = new WeakReference<>(xiaoAiRingtonePlayer);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            return Boolean.valueOf(XiaoAiRingtoneHelper.loadResOnAlert());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            WeakReference<XiaoAiRingtonePlayer> weakReference = this.mReference;
            XiaoAiRingtonePlayer xiaoAiRingtonePlayer = weakReference != null ? weakReference.get() : null;
            if (xiaoAiRingtonePlayer == null) {
                return;
            }
            xiaoAiRingtonePlayer.handleTaskResult(bool.booleanValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTaskResult(boolean z) {
        Log.d(TAG, "reload res result: " + z);
        this.mQueryDone = true;
        if (z) {
            this.mIsOnLineRes = false;
            this.mWillNewsPlay = false;
            playBgm();
        } else {
            OnPlayListener onPlayListener = this.mListener;
            if (onPlayListener != null) {
                onPlayListener.onError();
            }
        }
    }

    public void playBgm() {
        Log.d(TAG, "playBgm: ");
        if (this.mIsOnLineRes) {
            this.mListener.onBgmPlayed(XiaoAiRingtoneHelper.getBgm(), this.mWillNewsPlay);
        } else {
            this.mListener.onBgmPlayed(XiaoAiRingtoneHelper.getOfflineBgm(), false);
        }
    }

    public void playTts() {
        Log.d(TAG, "playTts: ");
        if (this.mIsOnLineRes) {
            this.mListener.onTtsPlayed(XiaoAiRingtoneHelper.getTts(), this.mWillNewsPlay);
        } else {
            this.mListener.onTtsPlayed(XiaoAiRingtoneHelper.getOfflineTts(), false);
        }
    }

    public void playNews() {
        Log.d(TAG, "playNews: ");
        this.mListener.onNewsPlayed(XiaoAiRingtoneHelper.getNews());
    }
}
