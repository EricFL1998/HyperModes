package com.android.deskclock.alarm.alert;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import com.android.deskclock.Alarm;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.themeringtone.RingtoneHelper;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class DefaultPlaybackDelegate implements AsyncRingtonePlayer.PlaybackDelegate {
    private static final String TAG = "DC:DefaultPlaybackDelegate";
    private Alarm mAlarm;
    private AudioManager mAudioManager;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private int mPlayerVolume;
    private int mSystemVolume;

    private float computeVolume(int i, int i2) {
        return i / i2;
    }

    public static WeatherPlaybackDelegate newInstance(Context context) {
        return new WeatherPlaybackDelegate(context);
    }

    public DefaultPlaybackDelegate(Context context) {
        this.mContext = context;
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public boolean play(Context context, Uri uri, Alarm alarm) {
        Uri dataRingtoneUri;
        Log.i(TAG, "Play ringtone via DefaultPlaybackDelegate.");
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
        }
        this.mAlarm = alarm;
        this.mSystemVolume = this.mAudioManager.getStreamVolume(4);
        Log.f(TAG, "system alarm volume: " + this.mSystemVolume);
        this.mPlayerVolume = 1;
        try {
            initPlayer();
            if (uri != null && (dataRingtoneUri = RingtoneHelper.getDataRingtoneUri(uri)) != null) {
                this.mMediaPlayer.setDataSource(context, dataRingtoneUri);
            } else {
                this.mMediaPlayer.setDataSource(context, uri);
            }
            return startPlayback();
        } catch (Throwable th) {
            Log.f(TAG, "Using the fallback ringtone, could not play " + uri + ": " + th.getMessage());
            try {
                initPlayer();
                this.mMediaPlayer.setDataSource(context, AsyncRingtonePlayer.getFallbackRingtoneUri(context));
                return startPlayback();
            } catch (Throwable th2) {
                Log.f(TAG, "Failed to play default ringtone: " + th2.getMessage());
                return false;
            }
        }
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public void stop(Context context) {
        Log.f(TAG, "Stop ringtone via DefaultPlaybackDelegate.");
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            stopMediaPlayer(mediaPlayer);
        }
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            audioManager.abandonAudioFocus(null);
        }
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public boolean adjustVolume(Context context) {
        if (!isMediaPlayerPlaying(this.mMediaPlayer)) {
            return false;
        }
        int i = this.mPlayerVolume + 1;
        this.mPlayerVolume = i;
        int i2 = this.mSystemVolume;
        if (i >= i2) {
            setMediaPlayerVolume(this.mMediaPlayer, 1.0f);
            Log.i(TAG, "volume set to 100%, stop");
            return false;
        }
        float fComputeVolume = computeVolume(i, i2);
        setMediaPlayerVolume(this.mMediaPlayer, fComputeVolume);
        Log.i(TAG, "volume set to " + fComputeVolume);
        return true;
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public void release(Context context) {
        releaseMediaPlayer(this.mMediaPlayer);
        this.mMediaPlayer = null;
    }

    private void initPlayer() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer == null) {
            this.mMediaPlayer = new MediaPlayer();
        } else {
            try {
                stopMediaPlayer(mediaPlayer);
                this.mMediaPlayer.reset();
            } catch (Exception e) {
                Log.e(TAG, "reuse MediaPlayer error: " + e.getMessage());
                releaseMediaPlayer(this.mMediaPlayer);
                this.mMediaPlayer = new MediaPlayer();
            }
        }
        Log.i(TAG, "init MediaPlayer volume to 100%");
        setMediaPlayerVolume(this.mMediaPlayer, 1.0f);
        this.mMediaPlayer.setOnCompletionListener(null);
        this.mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.android.deskclock.alarm.alert.DefaultPlaybackDelegate.1
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer2, int i, int i2) {
                Log.f(DefaultPlaybackDelegate.TAG, "play error, what: " + i + " ,extra: " + i2);
                return true;
            }
        });
    }

    private boolean startPlayback() throws IOException {
        this.mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(4).setContentType(4).build());
        boolean z = AsyncRingtonePlayer.supportAscending(this.mContext) && this.mAlarm.id != -2;
        if (z) {
            float fComputeVolume = computeVolume(this.mPlayerVolume, this.mSystemVolume);
            this.mMediaPlayer.setVolume(fComputeVolume, fComputeVolume);
        }
        this.mMediaPlayer.setAudioStreamType(4);
        this.mMediaPlayer.setLooping(true);
        this.mMediaPlayer.prepare();
        this.mAudioManager.requestAudioFocus(null, 4, 2);
        this.mMediaPlayer.start();
        return z;
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
            if (mediaPlayer != null) {
                AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.alert.DefaultPlaybackDelegate.2
                    @Override // java.lang.Runnable
                    public void run() {
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                });
            }
        }
    }

    private void setMediaPlayerVolume(MediaPlayer mediaPlayer, float f) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setVolume(f, f);
            } catch (IllegalStateException e) {
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
}
