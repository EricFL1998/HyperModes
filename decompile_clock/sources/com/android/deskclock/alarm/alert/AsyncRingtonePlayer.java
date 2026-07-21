package com.android.deskclock.alarm.alert;

import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.android.deskclock.Alarm;
import com.android.deskclock.R;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.timer.TimerPlaybackDelegate;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.AlarmUtils;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public final class AsyncRingtonePlayer {
    private static final String ALARM_KEY = "ALARM_KEY";
    private static final int EVENT_PLAY = 1;
    private static final int EVENT_RELEASE = 4;
    private static final int EVENT_STOP = 2;
    private static final int EVENT_VOLUME = 3;
    private static final String RINGTONE_URI_KEY = "RINGTONE_URI_KEY";
    private static final String TAG = "DC:AsyncRingtonePlayer";
    private static long mVolumeDelayMillis;
    private AudioManager mAudioManager;
    private final Context mContext;
    private DefaultPlaybackDelegate mDefaultPlaybackDelegate;
    private Handler mHandler;
    private PlaybackDelegate mPlaybackDelegate;
    private TimerPlaybackDelegate mTimerPlaybackDelegate;
    private WeatherPlaybackDelegate mWeatherPlaybackDelegate;
    private XiaiAiPlaybackDelegate mXiaiAiPlaybackDelegate;

    public interface PlaybackDelegate {
        boolean adjustVolume(Context context);

        boolean play(Context context, Uri uri, Alarm alarm);

        void release(Context context);

        void stop(Context context);
    }

    public AsyncRingtonePlayer(Context context) {
        this.mContext = context;
        mVolumeDelayMillis = getScheduleVolumeDelayMillis();
    }

    public void play(Uri uri, Alarm alarm) {
        Log.i(TAG, "Posting play.");
        postMessage(1, uri, alarm, 0L);
    }

    public void stop() {
        Log.i(TAG, "Posting stop.");
        postMessage(2, null, null, 0L);
        removeVolumeAdjustment();
    }

    public void release() {
        Log.i(TAG, "Posting release.");
        postMessage(4);
    }

    private long getScheduleVolumeDelayMillis() {
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
        }
        int streamVolume = this.mAudioManager.getStreamVolume(4);
        long j = streamVolume > 1 ? 3000 / ((long) (streamVolume - 1)) : 0L;
        Log.d(TAG, "getScheduleVolumeDelayMillis :" + j);
        return j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleVolumeAdjustment(long j) {
        Log.i(TAG, "Adjusting volume.");
        removeVolumeAdjustment();
        postMessage(3, null, null, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeVolumeAdjustment() {
        Log.i(TAG, "Cancel adjust volume.");
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(3);
        }
    }

    private void postMessage(int i, Uri uri, Alarm alarm, long j) {
        synchronized (this) {
            if (this.mHandler == null) {
                this.mHandler = getNewHandler();
            }
            Message messageObtainMessage = this.mHandler.obtainMessage(i);
            if (uri != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(RINGTONE_URI_KEY, uri);
                bundle.putParcelable(ALARM_KEY, alarm);
                messageObtainMessage.setData(bundle);
            }
            this.mHandler.sendMessageDelayed(messageObtainMessage, j);
        }
    }

    private void postMessage(int i) {
        synchronized (this) {
            if (this.mHandler == null) {
                this.mHandler = getNewHandler();
            }
            this.mHandler.sendMessage(this.mHandler.obtainMessage(i));
        }
    }

    public static boolean supportAscending(Context context) {
        return FBEUtil.getDefaultSharedPreferences(context.getApplicationContext()).getBoolean("alarm_ascending_mode", true);
    }

    private Handler getNewHandler() {
        HandlerThread handlerThread = new HandlerThread("ringtone-player");
        handlerThread.start();
        return new Handler(handlerThread.getLooper()) { // from class: com.android.deskclock.alarm.alert.AsyncRingtonePlayer.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                Log.f(AsyncRingtonePlayer.TAG, "handleMessage: " + message.what);
                int i = message.what;
                boolean z = false;
                if (i != 1) {
                    if (i == 2) {
                        if (AsyncRingtonePlayer.this.getPlaybackDelegate() != null) {
                            AsyncRingtonePlayer.this.getPlaybackDelegate().stop(AsyncRingtonePlayer.this.mContext);
                            if (AsyncRingtonePlayer.this.getPlaybackDelegate() == AsyncRingtonePlayer.this.getTimerPlaybackDelegate()) {
                                AlarmUtils.timerRingForXiaoAi = false;
                                Log.d(AsyncRingtonePlayer.TAG, "timerRingForXiaoAi: " + AlarmUtils.timerRingForXiaoAi);
                                return;
                            } else {
                                AlarmUtils.alarmRingForXiaoAi = false;
                                return;
                            }
                        }
                        return;
                    }
                    if (i != 3) {
                        if (i == 4 && AsyncRingtonePlayer.this.getPlaybackDelegate() != null) {
                            AsyncRingtonePlayer.this.getPlaybackDelegate().release(AsyncRingtonePlayer.this.mContext);
                            return;
                        }
                        return;
                    }
                    if (AsyncRingtonePlayer.this.getPlaybackDelegate() == null || !AsyncRingtonePlayer.this.getPlaybackDelegate().adjustVolume(AsyncRingtonePlayer.this.mContext)) {
                        return;
                    }
                    AsyncRingtonePlayer.this.scheduleVolumeAdjustment(AsyncRingtonePlayer.mVolumeDelayMillis);
                    return;
                }
                Bundle data = message.getData();
                Uri ringtoneUri = (Uri) data.getParcelable(AsyncRingtonePlayer.RINGTONE_URI_KEY);
                Alarm alarm = (Alarm) data.getParcelable(AsyncRingtonePlayer.ALARM_KEY);
                if (alarm == null) {
                    Log.e(AsyncRingtonePlayer.TAG, "handleMessage alarm is null");
                    return;
                }
                int i2 = alarm.id;
                Log.i(AsyncRingtonePlayer.TAG, "isXiaoRingtone : false   handleNotSureAlarm : " + XiaoAiRingtoneHelper.handleNotSureAlarm() + "  isXiaoAiAlarm id :" + XiaoAiRingtoneHelper.isXiaoAiAlarm(AsyncRingtonePlayer.this.mContext, i2));
                if (XiaoAiRingtoneHelper.isXiaoAiAlarm(AsyncRingtonePlayer.this.mContext, i2) || XiaoAiRingtoneHelper.handleNotSureAlarm()) {
                    ringtoneUri = XiaoAiRingtoneHelper.getRingtoneUri();
                    Log.i(AsyncRingtonePlayer.TAG, "isXiaoRingtone : true");
                    z = true;
                }
                AsyncRingtonePlayer asyncRingtonePlayer = AsyncRingtonePlayer.this;
                asyncRingtonePlayer.doRingtoneStat(asyncRingtonePlayer.mContext, ringtoneUri);
                if (i2 == -2) {
                    Log.f(AsyncRingtonePlayer.TAG, "play timer ringtone");
                    AlarmUtils.timerRingForXiaoAi = true;
                    Log.d(AsyncRingtonePlayer.TAG, "play timerRingForXiaoAi: " + AlarmUtils.timerRingForXiaoAi);
                    AsyncRingtonePlayer asyncRingtonePlayer2 = AsyncRingtonePlayer.this;
                    asyncRingtonePlayer2.setPlaybackDelegate(asyncRingtonePlayer2.getTimerPlaybackDelegate());
                } else if (z) {
                    Log.f(AsyncRingtonePlayer.TAG, "play XiaoAi ringtone");
                    AsyncRingtonePlayer asyncRingtonePlayer3 = AsyncRingtonePlayer.this;
                    asyncRingtonePlayer3.setPlaybackDelegate(asyncRingtonePlayer3.getXiaoAiPlaybackDelegate());
                } else if (WeatherRingtoneHelper.isWeatherRingtone(ringtoneUri)) {
                    Log.f(AsyncRingtonePlayer.TAG, "play weather ringtone");
                    AsyncRingtonePlayer asyncRingtonePlayer4 = AsyncRingtonePlayer.this;
                    asyncRingtonePlayer4.setPlaybackDelegate(asyncRingtonePlayer4.getWeatherPlaybackDelegate());
                } else if (WeekRingtoneHelper.isWeekRingtone(ringtoneUri)) {
                    String weekRingtoneBackground = WeekRingtoneHelper.getWeekRingtoneBackground(Calendar.getInstance());
                    Log.f(AsyncRingtonePlayer.TAG, "play week ringtone, path: " + weekRingtoneBackground);
                    if (weekRingtoneBackground != null) {
                        ringtoneUri = Uri.parse(weekRingtoneBackground);
                    } else {
                        Log.e(AsyncRingtonePlayer.TAG, "get week ringtone failed, play audition ringtone");
                    }
                    AsyncRingtonePlayer asyncRingtonePlayer5 = AsyncRingtonePlayer.this;
                    asyncRingtonePlayer5.setPlaybackDelegate(asyncRingtonePlayer5.getDefaultPlaybackDelegate());
                } else {
                    Log.f(AsyncRingtonePlayer.TAG, "play normal ringtone");
                    AsyncRingtonePlayer asyncRingtonePlayer6 = AsyncRingtonePlayer.this;
                    asyncRingtonePlayer6.setPlaybackDelegate(asyncRingtonePlayer6.getDefaultPlaybackDelegate());
                }
                if (AsyncRingtonePlayer.this.getPlaybackDelegate().play(AsyncRingtonePlayer.this.mContext, ringtoneUri, alarm)) {
                    AsyncRingtonePlayer.this.scheduleVolumeAdjustment(AsyncRingtonePlayer.mVolumeDelayMillis);
                } else {
                    AsyncRingtonePlayer.this.removeVolumeAdjustment();
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized WeatherPlaybackDelegate getWeatherPlaybackDelegate() {
        if (this.mWeatherPlaybackDelegate == null) {
            this.mWeatherPlaybackDelegate = new WeatherPlaybackDelegate(this.mContext);
        }
        return this.mWeatherPlaybackDelegate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized DefaultPlaybackDelegate getDefaultPlaybackDelegate() {
        if (this.mDefaultPlaybackDelegate == null) {
            this.mDefaultPlaybackDelegate = new DefaultPlaybackDelegate(this.mContext);
        }
        return this.mDefaultPlaybackDelegate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized TimerPlaybackDelegate getTimerPlaybackDelegate() {
        if (this.mTimerPlaybackDelegate == null) {
            this.mTimerPlaybackDelegate = new TimerPlaybackDelegate(this.mContext);
        }
        return this.mTimerPlaybackDelegate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public XiaiAiPlaybackDelegate getXiaoAiPlaybackDelegate() {
        if (this.mXiaiAiPlaybackDelegate == null) {
            this.mXiaiAiPlaybackDelegate = new XiaiAiPlaybackDelegate(this.mContext);
        }
        return this.mXiaiAiPlaybackDelegate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRingtoneStat(Context context, Uri uri) {
        if (uri == null) {
            return;
        }
        try {
            if (RingtoneManager.getDefaultUri(4).equals(uri)) {
                uri = AlarmRingtoneUtil.getDefaultAlarmRingtone(context);
            }
            if (uri != null) {
                String string = uri.toString();
                if (string.startsWith("file:///storage")) {
                    StatHelper.trackEvent(StatHelper.EVENT_ALERT_RINGTONE_URL_FORMAT, "STORAGE");
                    OneTrackStatHelper.trackStringEvent("storage", "");
                } else if (string.startsWith("content://")) {
                    StatHelper.trackEvent(StatHelper.EVENT_ALERT_RINGTONE_URL_FORMAT, "CONTENT");
                    OneTrackStatHelper.trackStringEvent("content", "");
                }
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "doRingtoneStat error" + e.getMessage());
        }
    }

    public static Uri getFallbackRingtoneUri(Context context) {
        return new Uri.Builder().scheme("android.resource").authority(context.getPackageName()).path(String.valueOf(R.raw.local_default_ringtone)).build();
    }

    public void setPlaybackDelegate(PlaybackDelegate playbackDelegate) {
        this.mPlaybackDelegate = playbackDelegate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PlaybackDelegate getPlaybackDelegate() {
        return this.mPlaybackDelegate;
    }
}
