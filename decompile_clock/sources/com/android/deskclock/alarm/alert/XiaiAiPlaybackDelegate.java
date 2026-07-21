package com.android.deskclock.alarm.alert;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtonePlayer;
import com.android.deskclock.util.Log;
import java.io.IOException;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class XiaiAiPlaybackDelegate implements AsyncRingtonePlayer.PlaybackDelegate {
    private static final String TAG = "DC:XiaiAiPlaybackDelegate";
    private Alarm mAlarm;
    private AudioManager mAudioManager;
    private MediaPlayer mBackgroundPlayer;
    private Context mContext;
    private MediaPlayer mNewsPlayer;
    private int mPlayerVolume;
    private int mSystemVolume;
    private MediaPlayer mVoicePlayer;
    private XiaoAiPlayerListener mXiaoAiAlarmListener;
    private XiaoAiRingtonePlayer mXiaoAiRingtonePlayer;
    private Handler mHandler = new Handler();
    private boolean mScheduleVolumeAdjustment = false;
    private Runnable mTtsPlayRunnable = new Runnable() { // from class: com.android.deskclock.alarm.alert.XiaiAiPlaybackDelegate.1
        @Override // java.lang.Runnable
        public void run() {
            if (XiaiAiPlaybackDelegate.this.mXiaoAiRingtonePlayer != null) {
                XiaiAiPlaybackDelegate.this.mXiaoAiRingtonePlayer.playTts();
            }
        }
    };

    private float computeVolume(int i, int i2) {
        return i / i2;
    }

    public XiaiAiPlaybackDelegate(Context context) {
        this.mContext = context;
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public boolean play(Context context, Uri uri, Alarm alarm) {
        Log.i(TAG, "Play ringtone via XiaiAiPlaybackDelegate.");
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
        }
        this.mAlarm = alarm;
        this.mSystemVolume = this.mAudioManager.getStreamVolume(4);
        Log.f(TAG, "system alarm volume: " + this.mSystemVolume);
        this.mPlayerVolume = 1;
        this.mXiaoAiAlarmListener = new XiaoAiPlayerListener(this);
        XiaoAiRingtonePlayer xiaoAiRingtonePlayer = new XiaoAiRingtonePlayer(this.mXiaoAiAlarmListener);
        this.mXiaoAiRingtonePlayer = xiaoAiRingtonePlayer;
        xiaoAiRingtonePlayer.play(alarm);
        boolean z = AsyncRingtonePlayer.supportAscending(this.mContext) && this.mAlarm.id != -2;
        this.mScheduleVolumeAdjustment = z;
        if (z) {
            adjustVolume(context);
        }
        return this.mScheduleVolumeAdjustment;
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public void stop(Context context) {
        Log.f(TAG, "Stop ringtone via XiaiAiPlaybackDelegate.");
        MediaPlayer mediaPlayer = this.mBackgroundPlayer;
        if (mediaPlayer != null) {
            stopMediaPlayer(mediaPlayer);
        }
        MediaPlayer mediaPlayer2 = this.mVoicePlayer;
        if (mediaPlayer2 != null) {
            stopMediaPlayer(mediaPlayer2);
        }
        MediaPlayer mediaPlayer3 = this.mNewsPlayer;
        if (mediaPlayer3 != null) {
            stopMediaPlayer(mediaPlayer3);
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
            setMediaPlayerVolume(this.mNewsPlayer, 1.0f);
            Log.i(TAG, "volume set to 100%, stop");
            return false;
        }
        float fComputeVolume = computeVolume(i, i2);
        setMediaPlayerVolume(this.mBackgroundPlayer, fComputeVolume);
        setMediaPlayerVolume(this.mVoicePlayer, fComputeVolume);
        setMediaPlayerVolume(this.mNewsPlayer, fComputeVolume);
        Log.i(TAG, "volume set to " + fComputeVolume);
        return true;
    }

    @Override // com.android.deskclock.alarm.alert.AsyncRingtonePlayer.PlaybackDelegate
    public void release(Context context) {
        this.mHandler.removeCallbacks(this.mTtsPlayRunnable);
        this.mHandler.removeCallbacksAndMessages(null);
        releaseMediaPlayer(this.mBackgroundPlayer);
        releaseMediaPlayer(this.mVoicePlayer);
        releaseMediaPlayer(this.mNewsPlayer);
        this.mBackgroundPlayer = null;
        this.mVoicePlayer = null;
        this.mNewsPlayer = null;
        XiaoAiRingtonePlayer xiaoAiRingtonePlayer = this.mXiaoAiRingtonePlayer;
        if (xiaoAiRingtonePlayer != null) {
            xiaoAiRingtonePlayer.clear();
            this.mXiaoAiRingtonePlayer = null;
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
        if (this.mScheduleVolumeAdjustment) {
            float fComputeVolume = computeVolume(this.mPlayerVolume, this.mSystemVolume);
            MediaPlayer mediaPlayer2 = this.mBackgroundPlayer;
            if (fComputeVolume > 1.0f) {
                fComputeVolume = 1.0f;
            }
            setMediaPlayerVolume(mediaPlayer2, fComputeVolume);
        }
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
        if (this.mScheduleVolumeAdjustment) {
            float fComputeVolume = computeVolume(this.mPlayerVolume, this.mSystemVolume);
            MediaPlayer mediaPlayer2 = this.mVoicePlayer;
            if (fComputeVolume > 1.0f) {
                fComputeVolume = 1.0f;
            }
            setMediaPlayerVolume(mediaPlayer2, fComputeVolume);
        }
    }

    private void initNewsPlayer() {
        MediaPlayer mediaPlayer = this.mNewsPlayer;
        if (mediaPlayer == null) {
            this.mNewsPlayer = new MediaPlayer();
        } else {
            try {
                stopMediaPlayer(mediaPlayer);
                this.mNewsPlayer.reset();
            } catch (Exception e) {
                Log.e(TAG, "reuse MediaPlayer error: " + e.getMessage());
                releaseMediaPlayer(this.mNewsPlayer);
                this.mNewsPlayer = new MediaPlayer();
            }
        }
        Log.i(TAG, "init newsPlayer volume to 100%");
        if (this.mScheduleVolumeAdjustment) {
            float fComputeVolume = computeVolume(this.mPlayerVolume, this.mSystemVolume);
            MediaPlayer mediaPlayer2 = this.mNewsPlayer;
            if (fComputeVolume > 1.0f) {
                fComputeVolume = 1.0f;
            }
            setMediaPlayerVolume(mediaPlayer2, fComputeVolume);
        }
    }

    private boolean startBackgroundPlayback() throws IllegalStateException, IOException, IllegalArgumentException {
        this.mBackgroundPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(4).setContentType(4).build());
        if (this.mScheduleVolumeAdjustment) {
            float fComputeVolume = computeVolume(this.mPlayerVolume, this.mSystemVolume);
            Log.d(TAG, "before play, set BackgroundPlayback volume: " + fComputeVolume);
            this.mBackgroundPlayer.setVolume(fComputeVolume, fComputeVolume);
        }
        this.mBackgroundPlayer.setAudioStreamType(4);
        this.mBackgroundPlayer.prepare();
        this.mAudioManager.requestAudioFocus(null, 4, 2);
        this.mBackgroundPlayer.start();
        return false;
    }

    private boolean startVoicePlayback() throws IllegalStateException, IOException, IllegalArgumentException {
        this.mVoicePlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(4).setContentType(4).build());
        if (this.mScheduleVolumeAdjustment) {
            float fComputeVolume = computeVolume(this.mPlayerVolume, this.mSystemVolume);
            Log.d(TAG, "before play, set voicePlay volume: " + fComputeVolume);
            this.mVoicePlayer.setVolume(fComputeVolume, fComputeVolume);
        }
        this.mVoicePlayer.setAudioStreamType(4);
        this.mVoicePlayer.prepare();
        return false;
    }

    private boolean startNewsPlayback() throws IllegalStateException, IOException, IllegalArgumentException {
        this.mNewsPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(4).setContentType(4).build());
        if (this.mScheduleVolumeAdjustment) {
            float fComputeVolume = computeVolume(this.mPlayerVolume, this.mSystemVolume);
            this.mNewsPlayer.setVolume(fComputeVolume, fComputeVolume);
        }
        this.mNewsPlayer.setAudioStreamType(4);
        this.mNewsPlayer.prepare();
        this.mAudioManager.requestAudioFocus(null, 4, 2);
        this.mNewsPlayer.start();
        return false;
    }

    private synchronized void stopMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                Log.e("AlarmService#stopMediaPlayer, Error when stop media player: " + e);
            }
        }
    }

    private synchronized void releaseMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            try {
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "releaseMediaPlayer, error: " + e);
            }
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

    /* JADX INFO: Access modifiers changed from: private */
    public boolean playDefault() {
        Log.i(TAG, "play default ringtone");
        try {
            stopMediaPlayer(this.mVoicePlayer);
            releaseMediaPlayer(this.mVoicePlayer);
            stopMediaPlayer(this.mNewsPlayer);
            releaseMediaPlayer(this.mNewsPlayer);
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

    public static class XiaoAiPlayerListener implements XiaoAiRingtonePlayer.OnPlayListener {
        private WeakReference<XiaiAiPlaybackDelegate> mReference;

        public XiaoAiPlayerListener(XiaiAiPlaybackDelegate xiaiAiPlaybackDelegate) {
            this.mReference = new WeakReference<>(xiaiAiPlaybackDelegate);
        }

        @Override // com.android.deskclock.addition.xiaoai.XiaoAiRingtonePlayer.OnPlayListener
        public void onError() {
            WeakReference<XiaiAiPlaybackDelegate> weakReference = this.mReference;
            XiaiAiPlaybackDelegate xiaiAiPlaybackDelegate = weakReference != null ? weakReference.get() : null;
            if (xiaiAiPlaybackDelegate == null) {
                return;
            }
            xiaiAiPlaybackDelegate.playDefault();
        }

        @Override // com.android.deskclock.addition.xiaoai.XiaoAiRingtonePlayer.OnPlayListener
        public void onBgmPlayed(Uri uri, boolean z) {
            WeakReference<XiaiAiPlaybackDelegate> weakReference = this.mReference;
            XiaiAiPlaybackDelegate xiaiAiPlaybackDelegate = weakReference != null ? weakReference.get() : null;
            if (xiaiAiPlaybackDelegate == null) {
                return;
            }
            xiaiAiPlaybackDelegate.playBgm(uri, z);
        }

        @Override // com.android.deskclock.addition.xiaoai.XiaoAiRingtonePlayer.OnPlayListener
        public void onTtsPlayed(Uri uri, boolean z) {
            WeakReference<XiaiAiPlaybackDelegate> weakReference = this.mReference;
            XiaiAiPlaybackDelegate xiaiAiPlaybackDelegate = weakReference != null ? weakReference.get() : null;
            if (xiaiAiPlaybackDelegate == null) {
                return;
            }
            xiaiAiPlaybackDelegate.playTts(uri, z);
        }

        @Override // com.android.deskclock.addition.xiaoai.XiaoAiRingtonePlayer.OnPlayListener
        public void onNewsPlayed(Uri uri) {
            WeakReference<XiaiAiPlaybackDelegate> weakReference = this.mReference;
            XiaiAiPlaybackDelegate xiaiAiPlaybackDelegate = weakReference != null ? weakReference.get() : null;
            if (xiaiAiPlaybackDelegate == null) {
                return;
            }
            xiaiAiPlaybackDelegate.playNews(uri);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playNews(Uri uri) {
        Log.d(TAG, "playNews: " + uri);
        try {
            stopMediaPlayer(this.mVoicePlayer);
            releaseMediaPlayer(this.mVoicePlayer);
            stopMediaPlayer(this.mBackgroundPlayer);
            releaseMediaPlayer(this.mBackgroundPlayer);
            initNewsPlayer();
            this.mNewsPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.deskclock.alarm.alert.XiaiAiPlaybackDelegate.2
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (XiaiAiPlaybackDelegate.this.mXiaoAiRingtonePlayer != null) {
                        XiaiAiPlaybackDelegate.this.mXiaoAiRingtonePlayer.playBgm();
                    }
                }
            });
            this.mNewsPlayer.setLooping(false);
            this.mNewsPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.android.deskclock.alarm.alert.XiaiAiPlaybackDelegate.3
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    Log.f(XiaiAiPlaybackDelegate.TAG, "play news error, what: " + i + " ,extra: " + i2);
                    return true;
                }
            });
            this.mNewsPlayer.setDataSource(DeskClockApp.getAppDEContext(), uri);
            startNewsPlayback();
        } catch (Exception e) {
            Log.e("play dynamic alarm error, play default", e);
            playDefault();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playBgm(Uri uri, boolean z) {
        Log.i(TAG, "play xiaoai bgm: " + uri);
        try {
            initBackgroundPlayer();
            this.mBackgroundPlayer.setOnCompletionListener(null);
            this.mBackgroundPlayer.setLooping(true);
            this.mBackgroundPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.android.deskclock.alarm.alert.XiaiAiPlaybackDelegate.4
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    Log.f(XiaiAiPlaybackDelegate.TAG, "play xiaoai bgm error, what: " + i + " ,extra: " + i2);
                    return true;
                }
            });
            this.mBackgroundPlayer.setDataSource(DeskClockApp.getAppDEContext(), uri);
            startBackgroundPlayback();
            this.mHandler.removeCallbacks(this.mTtsPlayRunnable);
            this.mHandler.postDelayed(this.mTtsPlayRunnable, 2000L);
        } catch (Exception e) {
            Log.e(TAG, "play bgm error, play default", e);
            stopMediaPlayer(this.mBackgroundPlayer);
            releaseMediaPlayer(this.mBackgroundPlayer);
            this.mBackgroundPlayer = null;
            playDefault();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playTts(Uri uri, final boolean z) {
        Log.d(TAG, "playTts: " + uri + ", willNewsPlay: " + z);
        try {
            initVoicePlayer();
            this.mVoicePlayer.setDataSource(DeskClockApp.getAppDEContext(), uri);
            if (z) {
                this.mVoicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.deskclock.alarm.alert.XiaiAiPlaybackDelegate.5
                    @Override // android.media.MediaPlayer.OnCompletionListener
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (z) {
                            XiaiAiPlaybackDelegate.this.mXiaoAiRingtonePlayer.playNews();
                        }
                    }
                });
                this.mVoicePlayer.setLooping(false);
            } else {
                this.mVoicePlayer.setOnCompletionListener(null);
                this.mVoicePlayer.setLooping(true);
            }
            startVoicePlayback();
            this.mVoicePlayer.start();
        } catch (Exception e) {
            Log.f(TAG, "play voice error: " + e.getMessage());
            stopMediaPlayer(this.mVoicePlayer);
            releaseMediaPlayer(this.mVoicePlayer);
            this.mVoicePlayer = null;
        }
    }
}
