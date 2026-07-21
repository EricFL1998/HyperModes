package com.android.deskclock.alarm.alert;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import com.android.deskclock.Alarm;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtonePlayer;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.FileUtil;
import com.android.deskclock.util.Log;
import java.io.IOException;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class WeatherPlaybackDelegate implements AsyncRingtonePlayer.PlaybackDelegate {
    private static final String TAG = "DC:WeatherPlaybackDelegate";
    private Alarm mAlarm;
    private AudioManager mAudioManager;
    private MediaPlayer mBackgroundPlayer;
    private Context mContext;
    private int mPlayerVolume;
    private long mPureSoundStartTime;
    private int mSystemVolume;
    private MediaPlayer mVoicePlayer;
    private WeatherAlarmPlayerListener mWeatherAlarmListener;
    private WeatherRingtonePlayer mWeatherRingtonePlayer;
    private Handler mHandler = new Handler();
    private boolean mScheduleVolumeAdjustment = false;
    private Runnable mVoicePlayRunnable = new Runnable() { // from class: com.android.deskclock.alarm.alert.WeatherPlaybackDelegate.1
        @Override // java.lang.Runnable
        public void run() {
            if (WeatherPlaybackDelegate.this.mVoicePlayer != null) {
                Log.d("weather alarm voice start time: " + (System.currentTimeMillis() - WeatherPlaybackDelegate.this.mPureSoundStartTime));
                try {
                    WeatherPlaybackDelegate.this.mVoicePlayer.start();
                } catch (Exception e) {
                    Log.e("weather alarm voice, error: " + e.getMessage());
                }
            }
        }
    };

    private float computeVolume(int i, int i2) {
        return i / i2;
    }

    public WeatherPlaybackDelegate(Context context) {
        this.mContext = context;
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public boolean play(Context context, Uri uri, Alarm alarm) {
        Log.i(TAG, "Play ringtone via WeatherPlaybackDelegate.");
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
        }
        this.mAlarm = alarm;
        this.mSystemVolume = this.mAudioManager.getStreamVolume(4);
        Log.f(TAG, "system alarm volume: " + this.mSystemVolume);
        this.mPlayerVolume = 1;
        this.mWeatherAlarmListener = new WeatherAlarmPlayerListener(this);
        WeatherRingtonePlayer weatherRingtonePlayer = new WeatherRingtonePlayer(this.mWeatherAlarmListener);
        this.mWeatherRingtonePlayer = weatherRingtonePlayer;
        weatherRingtonePlayer.startPlayWeatherAlarmBackground();
        boolean z = AsyncRingtonePlayer.supportAscending(this.mContext) && this.mAlarm.id != -2;
        this.mScheduleVolumeAdjustment = z;
        return z;
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public void stop(Context context) {
        Log.f(TAG, "Stop ringtone via WeatherPlaybackDelegate.");
        MediaPlayer mediaPlayer = this.mBackgroundPlayer;
        if (mediaPlayer != null) {
            stopMediaPlayer(mediaPlayer);
        }
        MediaPlayer mediaPlayer2 = this.mVoicePlayer;
        if (mediaPlayer2 != null) {
            stopMediaPlayer(mediaPlayer2);
        }
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            audioManager.abandonAudioFocus(null);
        }
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public boolean adjustVolume(Context context) {
        int i = this.mPlayerVolume + 1;
        this.mPlayerVolume = i;
        int i2 = this.mSystemVolume;
        if (i >= i2) {
            setMediaPlayerVolume(this.mBackgroundPlayer, 1.0f);
            setMediaPlayerVolume(this.mVoicePlayer, 1.0f);
            Log.i(TAG, "volume set to 100%, stop");
            return false;
        }
        float fComputeVolume = computeVolume(i, i2);
        setMediaPlayerVolume(this.mBackgroundPlayer, fComputeVolume);
        setMediaPlayerVolume(this.mVoicePlayer, fComputeVolume);
        Log.i(TAG, "volume set to " + fComputeVolume);
        return true;
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public void release(Context context) {
        this.mHandler.removeCallbacks(this.mVoicePlayRunnable);
        releaseMediaPlayer(this.mBackgroundPlayer);
        releaseMediaPlayer(this.mVoicePlayer);
        this.mBackgroundPlayer = null;
        this.mVoicePlayer = null;
        WeatherRingtonePlayer weatherRingtonePlayer = this.mWeatherRingtonePlayer;
        if (weatherRingtonePlayer != null) {
            weatherRingtonePlayer.clear();
            this.mWeatherRingtonePlayer = null;
        }
    }

    private void initBackgroundPlayer() {
        MediaPlayer mediaPlayer = this.mBackgroundPlayer;
        if (mediaPlayer == null) {
            this.mBackgroundPlayer = new MediaPlayer();
        } else {
            try {
                stopMediaPlayer(mediaPlayer);
                this.mBackgroundPlayer.reset();
            } catch (Exception e) {
                Log.e(TAG, "reuse MediaPlayer error: " + e.getMessage());
                releaseMediaPlayer(this.mBackgroundPlayer);
                this.mBackgroundPlayer = new MediaPlayer();
            }
        }
        Log.i(TAG, "init BackgroundPlayer volume to 100%");
        setMediaPlayerVolume(this.mBackgroundPlayer, 1.0f);
    }

    private void initVoicePlayer() {
        MediaPlayer mediaPlayer = this.mVoicePlayer;
        if (mediaPlayer == null) {
            this.mVoicePlayer = new MediaPlayer();
        } else {
            try {
                stopMediaPlayer(mediaPlayer);
                this.mVoicePlayer.reset();
            } catch (Exception e) {
                Log.e(TAG, "reuse MediaPlayer error: " + e.getMessage());
                releaseMediaPlayer(this.mVoicePlayer);
                this.mVoicePlayer = new MediaPlayer();
            }
        }
        Log.i(TAG, "init VoicePlayer volume to 100%");
        setMediaPlayerVolume(this.mVoicePlayer, 1.0f);
    }

    private boolean startBackgroundPlayback() throws IllegalStateException, IOException, IllegalArgumentException {
        this.mBackgroundPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(4).setContentType(4).build());
        if (this.mScheduleVolumeAdjustment) {
            float fComputeVolume = computeVolume(this.mPlayerVolume, this.mSystemVolume);
            this.mBackgroundPlayer.setVolume(fComputeVolume, fComputeVolume);
        }
        this.mBackgroundPlayer.setAudioStreamType(4);
        this.mBackgroundPlayer.prepare();
        this.mAudioManager.requestAudioFocus(null, 4, 2);
        this.mBackgroundPlayer.start();
        this.mPureSoundStartTime = System.currentTimeMillis();
        return false;
    }

    private boolean startVoicePlayback() throws IllegalStateException, IOException, IllegalArgumentException {
        this.mVoicePlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(4).setContentType(4).build());
        if (this.mScheduleVolumeAdjustment) {
            float fComputeVolume = computeVolume(this.mPlayerVolume, this.mSystemVolume);
            this.mVoicePlayer.setVolume(fComputeVolume, fComputeVolume);
        }
        this.mVoicePlayer.setAudioStreamType(4);
        this.mVoicePlayer.prepare();
        return false;
    }

    private synchronized void stopMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                Log.e("AlarmService#stopMediaPlayer, Error when stop media player: " + e.getMessage());
            }
        }
    }

    private synchronized void releaseMediaPlayer(final MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.alert.WeatherPlaybackDelegate.2
                @Override // java.lang.Runnable
                public void run() {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
            });
        }
    }

    private void setMediaPlayerVolume(MediaPlayer mediaPlayer, float f) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setVolume(f, f);
            } catch (Exception e) {
                Log.e("AlarmService#setMediaPlayerVolume, error: " + e.getMessage());
            }
        }
    }

    private boolean isMediaPlayerPlaying(MediaPlayer mediaPlayer) {
        if (mediaPlayer == null) {
            return false;
        }
        try {
            return mediaPlayer.isPlaying();
        } catch (IllegalStateException e) {
            Log.e("AlarmService#isMediaPlayerPlaying, error: " + e.getMessage());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playWeatherAlarmBackground(String str) {
        WeatherRingtonePlayer weatherRingtonePlayer;
        Log.i(TAG, "play weather alarm background: " + str);
        if (!FileUtil.isFileExist(str)) {
            playDefault();
            return;
        }
        try {
            initBackgroundPlayer();
            boolean zShouldPlayWeatherRingtoneVoice = WeatherRingtoneHelper.shouldPlayWeatherRingtoneVoice(this.mAlarm.id);
            Log.i(TAG, "need play weather alarm voice: " + zShouldPlayWeatherRingtoneVoice);
            if (zShouldPlayWeatherRingtoneVoice) {
                this.mBackgroundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.deskclock.alarm.alert.WeatherPlaybackDelegate.3
                    @Override // android.media.MediaPlayer.OnCompletionListener
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Log.d("onCompletion");
                        if (WeatherPlaybackDelegate.this.mWeatherRingtonePlayer != null) {
                            WeatherPlaybackDelegate.this.mWeatherRingtonePlayer.loopPlayWeatherAlarmBackground();
                        }
                    }
                });
                this.mBackgroundPlayer.setLooping(false);
            } else {
                this.mBackgroundPlayer.setOnCompletionListener(null);
                this.mBackgroundPlayer.setLooping(true);
            }
            this.mBackgroundPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.android.deskclock.alarm.alert.WeatherPlaybackDelegate.4
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    Log.f(WeatherPlaybackDelegate.TAG, "play weather ringtone alarm error, what: " + i + " ,extra: " + i2);
                    return true;
                }
            });
            this.mBackgroundPlayer.setDataSource(str);
            startBackgroundPlayback();
            if (!zShouldPlayWeatherRingtoneVoice || (weatherRingtonePlayer = this.mWeatherRingtonePlayer) == null) {
                return;
            }
            weatherRingtonePlayer.startPlayWeatherAlarmVoice();
        } catch (Exception e) {
            Log.e("play dynamic alarm error, play default", e);
            playDefault();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playWeatherAlarmVoice(long j, String str) {
        Log.i(TAG, "play weather alarm voice: " + str);
        try {
            initVoicePlayer();
            this.mVoicePlayer.setDataSource(str);
            startVoicePlayback();
            this.mHandler.removeCallbacks(this.mVoicePlayRunnable);
            this.mHandler.postDelayed(this.mVoicePlayRunnable, j - (System.currentTimeMillis() - this.mPureSoundStartTime));
        } catch (Exception e) {
            Log.f(TAG, "play weather ringtone voice error: " + e.getMessage());
            stopMediaPlayer(this.mVoicePlayer);
            releaseMediaPlayer(this.mVoicePlayer);
            this.mVoicePlayer = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean playDefault() {
        Log.i(TAG, "play default ringtone");
        try {
            initBackgroundPlayer();
            this.mBackgroundPlayer.setOnCompletionListener(null);
            this.mBackgroundPlayer.setLooping(true);
            MediaPlayer mediaPlayer = this.mBackgroundPlayer;
            Context context = this.mContext;
            mediaPlayer.setDataSource(context, AsyncRingtonePlayer.getFallbackRingtoneUri(context));
            return startBackgroundPlayback();
        } catch (Exception e) {
            Log.f(TAG, "failed to play default ringtone: " + e.getMessage());
            return false;
        }
    }

    public static class WeatherAlarmPlayerListener implements WeatherRingtonePlayer.OnPlayListener {
        private WeakReference<WeatherPlaybackDelegate> mReference;

        public WeatherAlarmPlayerListener(WeatherPlaybackDelegate weatherPlaybackDelegate) {
            this.mReference = new WeakReference<>(weatherPlaybackDelegate);
        }

        @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtonePlayer.OnPlayListener
        public void onError() {
            WeakReference<WeatherPlaybackDelegate> weakReference = this.mReference;
            WeatherPlaybackDelegate weatherPlaybackDelegate = weakReference != null ? weakReference.get() : null;
            if (weatherPlaybackDelegate == null) {
                return;
            }
            weatherPlaybackDelegate.playDefault();
        }

        @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtonePlayer.OnPlayListener
        public void onBackgroundPlayed(boolean z, String str) {
            WeakReference<WeatherPlaybackDelegate> weakReference = this.mReference;
            WeatherPlaybackDelegate weatherPlaybackDelegate = weakReference != null ? weakReference.get() : null;
            if (weatherPlaybackDelegate == null) {
                return;
            }
            weatherPlaybackDelegate.playWeatherAlarmBackground(str);
        }

        @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtonePlayer.OnPlayListener
        public void onVoicePlayed(long j, String str) {
            WeakReference<WeatherPlaybackDelegate> weakReference = this.mReference;
            WeatherPlaybackDelegate weatherPlaybackDelegate = weakReference != null ? weakReference.get() : null;
            if (weatherPlaybackDelegate == null) {
                return;
            }
            weatherPlaybackDelegate.playWeatherAlarmVoice(j, str);
        }
    }
}
