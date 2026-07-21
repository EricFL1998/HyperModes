package com.android.deskclock.settings;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class RingtonePlayService extends Service {
    public static final String ACTION_USER_SWITCHED = "android.intent.action.USER_SWITCHED";
    private static final String HANDLER_THREAD_NAME = "RingtonePlayService";
    private static final int PLAY = 1;
    private static final long PLAY_DELAY = 500;
    private static final int RELEASE = 3;
    private static final int STOP = 2;
    private static final String TAG = "DC:RingtonePlayService";
    private AudioManager mAudioManager;
    private CallbackListener mCallbackListener;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private Looper mLooper;
    private MediaPlayer mMediaPlayer;
    private String mPlayingMusic;
    private boolean mPlaying = false;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() { // from class: com.android.deskclock.settings.RingtonePlayService.4
        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(int i) {
            if (i == -1 && RingtonePlayService.this.mPlaying) {
                RingtonePlayService.this.stop();
            }
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.deskclock.settings.RingtonePlayService.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                RingtonePlayService.this.stop();
                RingtonePlayService.this.stopForeground(true);
                RingtonePlayService.this.stopSelf();
            }
        }
    };

    public interface CallbackListener {
        void onPlayComplete(String str);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        return 2;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mAudioManager = (AudioManager) getSystemService("audio");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.setPriority(1000);
        if (Build.VERSION.SDK_INT >= 34) {
            registerReceiver(this.mReceiver, intentFilter, 4);
        } else {
            registerReceiver(this.mReceiver, intentFilter);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Log.d("TimerService onBind");
        return new CallbackBinder();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        releaseHandler();
        unregisterReceiver(this.mReceiver);
        stop();
        if (this.mCallbackListener != null) {
            this.mCallbackListener = null;
        }
    }

    public void registerCallbackListener(CallbackListener callbackListener) {
        this.mCallbackListener = callbackListener;
    }

    public void playRingtone(String str) {
        createHandler();
        this.mHandler.removeMessages(1);
        Message message = new Message();
        message.what = 1;
        message.obj = str;
        this.mHandler.sendMessageDelayed(message, PLAY_DELAY);
    }

    public void stopRingtone() {
        createHandler();
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessage(2);
    }

    public boolean isPlaying() {
        return this.mPlaying;
    }

    public String getPlayMusic() {
        return this.mPlayingMusic;
    }

    public synchronized void play(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (this.mPlaying && !TextUtils.isEmpty(this.mPlayingMusic) && this.mPlayingMusic.equals(str)) {
            return;
        }
        this.mAudioManager.requestAudioFocus(this.mAudioFocusChangeListener, 4, 2);
        try {
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer == null) {
                MediaPlayer mediaPlayer2 = new MediaPlayer();
                this.mMediaPlayer = mediaPlayer2;
                mediaPlayer2.setLooping(false);
                this.mMediaPlayer.setAudioStreamType(4);
                this.mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.android.deskclock.settings.RingtonePlayService.1
                    @Override // android.media.MediaPlayer.OnErrorListener
                    public boolean onError(MediaPlayer mediaPlayer3, int i, int i2) {
                        RingtonePlayService.this.stop();
                        return true;
                    }
                });
                this.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.deskclock.settings.RingtonePlayService.2
                    @Override // android.media.MediaPlayer.OnCompletionListener
                    public void onCompletion(MediaPlayer mediaPlayer3) {
                        RingtonePlayService.this.stop();
                    }
                });
                this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.deskclock.settings.RingtonePlayService.3
                    @Override // android.media.MediaPlayer.OnPreparedListener
                    public void onPrepared(MediaPlayer mediaPlayer3) {
                        RingtonePlayService.this.mPlaying = true;
                        if (RingtonePlayService.this.mMediaPlayer != null) {
                            try {
                                RingtonePlayService.this.mMediaPlayer.start();
                            } catch (Exception e) {
                                Log.e("onPrepared error: " + e);
                            }
                        }
                    }
                });
            } else {
                stopMediaPlayer(mediaPlayer);
                this.mMediaPlayer.reset();
            }
            this.mPlayingMusic = str;
            MediaPlayer mediaPlayer3 = this.mMediaPlayer;
            if (mediaPlayer3 != null) {
                try {
                    mediaPlayer3.setDataSource(context, Uri.parse(str));
                    this.mMediaPlayer.prepareAsync();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "MediaPlayer in wrong state " + e);
                }
            }
        } catch (Exception e2) {
            Log.i(TAG, "play normal alarm error: " + e2.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void stop() {
        this.mPlaying = false;
        this.mAudioManager.abandonAudioFocus(this.mAudioFocusChangeListener);
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            try {
                stopMediaPlayer(mediaPlayer);
                releaseMediaPlayer(this.mMediaPlayer);
                this.mMediaPlayer = null;
            } catch (Throwable th) {
                this.mMediaPlayer = null;
                throw th;
            }
        }
        CallbackListener callbackListener = this.mCallbackListener;
        if (callbackListener != null) {
            callbackListener.onPlayComplete(this.mPlayingMusic);
        }
    }

    public class CallbackBinder extends Binder {
        public CallbackBinder() {
        }

        public RingtonePlayService getService() {
            return RingtonePlayService.this;
        }
    }

    private void stopMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                Log.e("stopMediaPlayer, Error when stop media player: " + e.getMessage());
            }
        }
    }

    private void releaseMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.setOnErrorListener(null);
        mediaPlayer.setOnPreparedListener(null);
        try {
            mediaPlayer.reset();
            mediaPlayer.release();
        } catch (Exception e) {
            Log.e(TAG, "Error releasing media player: " + e.getMessage());
        }
    }

    private void createHandler() {
        if (this.mHandler == null) {
            HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
            this.mHandlerThread = handlerThread;
            handlerThread.start();
            this.mLooper = this.mHandlerThread.getLooper();
            this.mHandler = new Handler(this.mLooper, new Handler.Callback() { // from class: com.android.deskclock.settings.RingtonePlayService.6
                /* JADX WARN: Type inference fix 'apply assigned field type' failed
                java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
                	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
                	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
                	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
                 */
                @Override // android.os.Handler.Callback
                public boolean handleMessage(Message message) {
                    int i = message.what;
                    if (i == 1) {
                        RingtonePlayService ringtonePlayService = RingtonePlayService.this;
                        ringtonePlayService.play(ringtonePlayService, (String) message.obj);
                        return false;
                    }
                    if (i != 2) {
                        return false;
                    }
                    RingtonePlayService.this.stop();
                    return false;
                }
            });
        }
    }

    private void releaseHandler() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(1);
            this.mHandler.removeMessages(2);
            HandlerThread handlerThread = this.mHandlerThread;
            if (handlerThread != null) {
                handlerThread.quitSafely();
            }
            this.mHandler = null;
        }
    }
}
