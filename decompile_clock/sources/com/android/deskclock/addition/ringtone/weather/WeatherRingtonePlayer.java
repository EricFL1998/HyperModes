package com.android.deskclock.addition.ringtone.weather;

import android.os.AsyncTask;
import android.text.format.Time;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.resource.ExternalResourceUtils;
import com.android.deskclock.addition.resource.MiuiResource;
import com.android.deskclock.addition.weather.WeatherInfo;
import com.android.deskclock.addition.weather.WeatherUtils;
import com.android.deskclock.util.FileUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WeatherRingtonePlayer {
    private static final String TAG = "DC:WeatherRingtone";
    private OnPlayListener mListener;
    private int mModuleVersion = 0;
    private AsyncTask<Void, Void, ArrayList<WeatherRingtonePiece>> mVoiceCacheTask;
    private AsyncTask<Void, Void, String> mVoiceComposeTask;
    private String mWeatherAlarmBackground;
    private ArrayList<WeatherRingtonePiece> mWeatherAlarmVoiceCache;
    private WeatherInfo mWeatherInfo;
    private AsyncTask<Void, Void, WeatherInfo> mWeatherQueryTask;

    public interface OnPlayListener {
        void onBackgroundPlayed(boolean z, String str);

        void onError();

        void onVoicePlayed(long j, String str);
    }

    public WeatherRingtonePlayer(OnPlayListener onPlayListener) {
        this.mListener = onPlayListener;
    }

    public void startPlayWeatherAlarmBackground() {
        this.mWeatherInfo = null;
        this.mWeatherAlarmBackground = null;
        this.mWeatherAlarmVoiceCache = null;
        if (!WeatherRingtoneHelper.isRomSupport()) {
            Log.i(TAG, "play weather alarm error, ROM not support");
            this.mListener.onError();
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_WEATHER_RINGTONE, "NOT_SUPPORT");
            return;
        }
        this.mModuleVersion = PrefUtil.getMiuiResourceVersion();
        Log.i(ExternalResourceUtils.TAG, "current resource version: " + this.mModuleVersion);
        if (this.mModuleVersion <= 1) {
            MiuiResource.checkLocalResourceVersion();
            this.mModuleVersion = PrefUtil.getMiuiResourceVersion();
            Log.i(ExternalResourceUtils.TAG, "resource version after check: " + this.mModuleVersion);
        }
        queryWeatherInfo();
    }

    public void loopPlayWeatherAlarmBackground() {
        this.mListener.onBackgroundPlayed(true, this.mWeatherAlarmBackground);
    }

    public void startPlayWeatherAlarmVoice() {
        if (this.mWeatherAlarmVoiceCache == null) {
            createWeatherAlarmVoiceCache();
        } else {
            createWeatherAlarmVoice();
        }
    }

    public void clear() {
        this.mListener = null;
        cancelWeatherQueryTask();
        cancelVoiceCacheTask();
        cancelVoiceComposeTask();
    }

    private void queryWeatherInfo() {
        if (this.mModuleVersion <= 1) {
            Log.i(TAG, "play weather alarm error, module version not support");
            this.mListener.onError();
        } else {
            cancelWeatherQueryTask();
            QueryWeatherAsyncTask queryWeatherAsyncTask = new QueryWeatherAsyncTask(this);
            this.mWeatherQueryTask = queryWeatherAsyncTask;
            queryWeatherAsyncTask.execute(new Void[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleQueryWeatherResult(WeatherInfo weatherInfo) {
        if (weatherInfo == null) {
            Log.i(TAG, "play weather alarm error, weather is null");
            StatHelper.alarmEvent(StatHelper.EVENT_ALARM_DYNAMIC_ALARM_WEATHER_FAIL);
            OneTrackStatHelper.trackBoolEvent(false, OneTrackStatHelper.DYNAMIC_ALARM_WEATHER_STATE);
            this.mListener.onError();
            return;
        }
        this.mWeatherInfo = weatherInfo;
        String weatherRingtoneBackground = WeatherRingtoneHelper.getWeatherRingtoneBackground(this.mModuleVersion, weatherInfo);
        this.mWeatherAlarmBackground = weatherRingtoneBackground;
        this.mListener.onBackgroundPlayed(false, weatherRingtoneBackground);
        OneTrackStatHelper.trackBoolEvent(true, OneTrackStatHelper.DYNAMIC_ALARM_WEATHER_STATE);
    }

    private void createWeatherAlarmVoiceCache() {
        Log.i(TAG, "start transfer");
        cancelVoiceCacheTask();
        TransferWeatherAsyncTask transferWeatherAsyncTask = new TransferWeatherAsyncTask(this, this.mWeatherInfo, this.mModuleVersion);
        this.mVoiceCacheTask = transferWeatherAsyncTask;
        transferWeatherAsyncTask.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTransferWeatherResult(ArrayList<WeatherRingtonePiece> arrayList) {
        if (arrayList != null) {
            this.mWeatherAlarmVoiceCache = arrayList;
            cancelVoiceComposeTask();
            ComposeWeatherAsyncTask composeWeatherAsyncTask = new ComposeWeatherAsyncTask(this, this.mWeatherInfo, this.mWeatherAlarmVoiceCache, this.mModuleVersion);
            this.mVoiceComposeTask = composeWeatherAsyncTask;
            composeWeatherAsyncTask.execute(new Void[0]);
            return;
        }
        Log.e(TAG, "play weather ringtone voice error: WeatherRingtonePiece cache is null");
    }

    private void createWeatherAlarmVoice() {
        Log.i(TAG, "start compose");
        cancelVoiceComposeTask();
        ComposeWeatherAsyncTask composeWeatherAsyncTask = new ComposeWeatherAsyncTask(this, this.mWeatherInfo, this.mWeatherAlarmVoiceCache, this.mModuleVersion);
        this.mVoiceComposeTask = composeWeatherAsyncTask;
        composeWeatherAsyncTask.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleComposeWeatherResult(String str) {
        if (this.mListener != null && FileUtil.isFileExist(str)) {
            this.mListener.onVoicePlayed(this.mWeatherAlarmVoiceCache.get(0).time, str);
        } else {
            Log.e(TAG, "play weather ringtone voice error: voice wav is not exist " + str);
        }
    }

    private void cancelWeatherQueryTask() {
        AsyncTask<Void, Void, WeatherInfo> asyncTask = this.mWeatherQueryTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.mWeatherQueryTask = null;
        }
    }

    private void cancelVoiceCacheTask() {
        AsyncTask<Void, Void, ArrayList<WeatherRingtonePiece>> asyncTask = this.mVoiceCacheTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.mVoiceCacheTask = null;
        }
    }

    private void cancelVoiceComposeTask() {
        AsyncTask<Void, Void, String> asyncTask = this.mVoiceComposeTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.mVoiceComposeTask = null;
        }
    }

    private static class QueryWeatherAsyncTask extends AsyncTask<Void, Void, WeatherInfo> {
        private WeakReference<WeatherRingtonePlayer> mReference;

        public QueryWeatherAsyncTask(WeatherRingtonePlayer weatherRingtonePlayer) {
            this.mReference = new WeakReference<>(weatherRingtonePlayer);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public WeatherInfo doInBackground(Void... voidArr) {
            Time time = new Time();
            time.setToNow();
            return WeatherUtils.getLocalWeather(DeskClockApp.getAppDEContext(), Time.getJulianDay(time.toMillis(true), time.gmtoff), WeatherUtils.DATA_TYPE_OF_DYNAMIC_ALARM, true);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(WeatherInfo weatherInfo) {
            WeakReference<WeatherRingtonePlayer> weakReference = this.mReference;
            WeatherRingtonePlayer weatherRingtonePlayer = weakReference != null ? weakReference.get() : null;
            if (weatherRingtonePlayer == null) {
                return;
            }
            weatherRingtonePlayer.handleQueryWeatherResult(weatherInfo);
        }
    }

    private static class TransferWeatherAsyncTask extends AsyncTask<Void, Void, ArrayList<WeatherRingtonePiece>> {
        private WeakReference<WeatherRingtonePlayer> mReference;
        private int mResourceVersion;
        private WeatherInfo mWeatherInfo;

        public TransferWeatherAsyncTask(WeatherRingtonePlayer weatherRingtonePlayer, WeatherInfo weatherInfo, int i) {
            this.mReference = new WeakReference<>(weatherRingtonePlayer);
            this.mWeatherInfo = weatherInfo;
            this.mResourceVersion = i;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public ArrayList<WeatherRingtonePiece> doInBackground(Void... voidArr) {
            return WeatherRingtoneHelper.getWeatherRingtoneModel(this.mResourceVersion, this.mWeatherInfo);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(ArrayList<WeatherRingtonePiece> arrayList) {
            WeakReference<WeatherRingtonePlayer> weakReference = this.mReference;
            WeatherRingtonePlayer weatherRingtonePlayer = weakReference != null ? weakReference.get() : null;
            if (weatherRingtonePlayer == null) {
                return;
            }
            weatherRingtonePlayer.handleTransferWeatherResult(arrayList);
        }
    }

    private static class ComposeWeatherAsyncTask extends AsyncTask<Void, Void, String> {
        private ArrayList<WeatherRingtonePiece> mAlarmCache;
        private WeakReference<WeatherRingtonePlayer> mReference;
        private int mResourceVersion;
        private WeatherInfo mWeatherInfo;

        public ComposeWeatherAsyncTask(WeatherRingtonePlayer weatherRingtonePlayer, WeatherInfo weatherInfo, ArrayList<WeatherRingtonePiece> arrayList, int i) {
            this.mReference = new WeakReference<>(weatherRingtonePlayer);
            this.mWeatherInfo = weatherInfo;
            this.mAlarmCache = arrayList;
            this.mResourceVersion = i;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            return WeatherRingtoneHelper.createWeatherRingtoneVoice(this.mResourceVersion, this.mWeatherInfo, this.mAlarmCache);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            WeakReference<WeatherRingtonePlayer> weakReference = this.mReference;
            WeatherRingtonePlayer weatherRingtonePlayer = weakReference != null ? weakReference.get() : null;
            if (weatherRingtonePlayer == null) {
                return;
            }
            weatherRingtonePlayer.handleComposeWeatherResult(str);
        }
    }
}
