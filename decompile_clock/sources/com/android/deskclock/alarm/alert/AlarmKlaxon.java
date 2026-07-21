package com.android.deskclock.alarm.alert;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import com.android.deskclock.Alarm;
import com.android.deskclock.R;
import com.android.deskclock.addition.ringtone.RingtoneUriCompat;
import com.android.deskclock.addition.ringtone.digital.DigitalTimerRingtoneHelper;
import com.android.deskclock.addition.ringtone.star.WYStarRingtoneHelper;
import com.android.deskclock.util.BleUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class AlarmKlaxon {
    private static final String TAG = "DC:AlarmKlaxon";
    private static final long[] VIBRATE_PATTERN = {500, 500};
    private AsyncRingtonePlayer mAsyncRingtonePlayer;
    private boolean mAudioStarted = false;

    public void start(Context context, Alarm alarm) {
        Log.v(TAG, "AlarmKlaxon.start()");
        if (alarm.vibrate) {
            Log.f(TAG, "start vibrator");
            vibrateLOrLater(getVibrator(context));
            Log.i(TAG, "vibrate mi bracelet");
            BleUtil.vibrateMiBracelet(context);
        } else {
            Log.f(TAG, "cancel vibrator for alarm setting");
            stopVibrator(context);
        }
        stop(context);
        Uri uriPrepareRingtone = prepareRingtone(context, alarm);
        if (uriPrepareRingtone != null) {
            getAsyncRingtonePlayer(context).play(uriPrepareRingtone, alarm);
        } else {
            Log.f(TAG, "play silent ringtone");
        }
        this.mAudioStarted = true;
        Log.d(TAG, "play mAudioStarted: " + this.mAudioStarted);
    }

    public void stop(Context context) {
        Log.d(TAG, "stop mAudioStarted: " + this.mAudioStarted);
        if (this.mAudioStarted) {
            Log.v("AlarmKlaxon.stop()");
            this.mAudioStarted = false;
            getAsyncRingtonePlayer(context).stop();
        }
    }

    public void stopVibrator(Context context) {
        Log.v("stopVibrator");
        ((Vibrator) context.getSystemService("vibrator")).cancel();
    }

    public void release(Context context) {
        stop(context);
        stopVibrator(context);
        getAsyncRingtonePlayer(context).release();
    }

    private static Uri prepareRingtone(Context context, Alarm alarm) {
        if (alarm == null) {
            return null;
        }
        if (alarm.silent) {
            Log.f(TAG, "alarm use silent ringtone");
            return null;
        }
        Uri uri = alarm.alert;
        if (alarm.id != -2) {
            Uri defaultUri = RingtoneManager.getDefaultUri(4);
            Uri actualDefaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, 4);
            if (uri == null) {
                uri = defaultUri;
            }
            boolean z = actualDefaultRingtoneUri == null;
            if (defaultUri.equals(uri) && z) {
                Log.f(TAG, "alarm use default ringtone and default is silent");
                return null;
            }
            if (WYStarRingtoneHelper.updateWYStarAlertToDefault(context, alarm)) {
                Log.f(TAG, "change ringtone for WYStar not support");
                uri = alarm.alert;
            }
        }
        Log.f(TAG, "prepare ringtone: " + Util.getRingtoneTitle(context, uri));
        return (Util.getRingtoneTitle(context, uri).contains(context.getString(R.string.ringtone_digital_timer)) && RingtoneUriCompat.atLeastU()) ? DigitalTimerRingtoneHelper.getRingtoneUri() : uri;
    }

    private void vibrateLOrLater(Vibrator vibrator) {
        vibrator.vibrate(VIBRATE_PATTERN, 0, new AudioAttributes.Builder().setUsage(4).setContentType(4).build());
    }

    private Vibrator getVibrator(Context context) {
        return (Vibrator) context.getSystemService("vibrator");
    }

    private synchronized AsyncRingtonePlayer getAsyncRingtonePlayer(Context context) {
        if (this.mAsyncRingtonePlayer == null) {
            this.mAsyncRingtonePlayer = new AsyncRingtonePlayer(context.getApplicationContext());
        }
        return this.mAsyncRingtonePlayer;
    }
}
