package com.android.deskclock;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class AlarmClockExtras {
    public static final String ACTION_DISMISS_ALARM = "android.intent.action.DISMISS_ALARM";
    public static final String ACTION_DISMISS_TIMER = "miui.intent.action.DISMISS_TIMER";
    public static final String ACTION_SET_TIMER = "android.intent.action.SET_TIMER";
    public static final String ACTION_SHOW_ALARMS = "android.intent.action.SHOW_ALARMS";
    public static final String ACTION_SHOW_TIMERS = "android.intent.action.SHOW_TIMERS";
    public static final String EXTRA_DAYS = "android.intent.extra.alarm.DAYS";
    public static final String EXTRA_LENGTH = "android.intent.extra.alarm.LENGTH";
    public static final String EXTRA_RINGTONE = "android.intent.extra.alarm.RINGTONE";
    public static final String EXTRA_VIBRATE = "android.intent.extra.alarm.VIBRATE";
    public static final String NO_RINGTONE;
    public static final Uri NO_RINGTONE_URI;
    public static final String START_TIMER = "start_timer";
    public static final String TIMER_INTENT_EXTRA = "timer.intent.extra";
    public static final String TIMER_INTENT_EXTRA_DURATION = "timer.intent.extra.duration";
    public static final String VALUE_RINGTONE_SILENT = "silent";

    static {
        Uri uri = Uri.EMPTY;
        NO_RINGTONE_URI = uri;
        NO_RINGTONE = uri.toString();
    }
}
