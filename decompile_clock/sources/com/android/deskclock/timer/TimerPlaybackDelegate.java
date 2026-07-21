package com.android.deskclock.timer;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import com.android.deskclock.Alarm;
import com.android.deskclock.addition.ringtone.digital.DigitalTimerRingtoneHelper;
import com.android.deskclock.alarm.alert.AsyncRingtonePlayer;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.Log;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class TimerPlaybackDelegate implements AsyncRingtonePlayer.PlaybackDelegate {
    private static final String TAG = "DC:TimerPlaybackDelegate";
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private int mSystemVolume;

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public boolean adjustVolume(Context context) {
        return false;
    }

    public TimerPlaybackDelegate(Context context) {
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public boolean play(Context context, Uri uri, Alarm alarm) {
        Log.i(TAG, "Play ringtone via TimerPlaybackDelegate.");
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
        }
        this.mSystemVolume = this.mAudioManager.getStreamVolume(4);
        Log.f(TAG, "system alarm volume: " + this.mSystemVolume);
        try {
            initPlayer();
            this.mMediaPlayer.setDataSource(context, uri);
            return startPlayback();
        } catch (Throwable th) {
            Log.f(TAG, "Using the fallback ringtone, could not play " + uri + ": " + th.getMessage());
            try {
                initPlayer();
                this.mMediaPlayer.setDataSource(context, DigitalTimerRingtoneHelper.getRingtoneUri());
                return startPlayback();
            } catch (Throwable th2) {
                Log.f(TAG, "Failed to play default ringtone: " + th2.getMessage());
                return false;
            }
        }
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public void stop(Context context) {
        Log.f(TAG, "Stop ringtone via TimerPlaybackDelegate.");
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
        this.mMediaPlayer.setOnCompletionListener(null);
        this.mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.android.deskclock.timer.TimerPlaybackDelegate.1
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer2, int i, int i2) {
                Log.f(TimerPlaybackDelegate.TAG, "play error, what: " + i + " ,extra: " + i2);
                return true;
            }
        });
    }

    private boolean startPlayback() throws IOException {
        this.mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(4).setContentType(4).build());
        this.mMediaPlayer.setAudioStreamType(4);
        this.mMediaPlayer.setLooping(true);
        this.mMediaPlayer.prepare();
        this.mAudioManager.requestAudioFocus(null, 4, 2);
        this.mMediaPlayer.start();
        return false;
    }

    private synchronized void stopMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                Log.e("TimerPlaybackDelegate#stopMediaPlayer, Error when stop media player: " + e.getMessage());
            }
        }
    }

    private synchronized void releaseMediaPlayer(final MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            if (mediaPlayer != null) {
                AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.timer.TimerPlaybackDelegate.2
                    @Override // java.lang.Runnable
                    public void run() {
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                });
            }
        }
    }
}
