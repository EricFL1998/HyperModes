package com.android.deskclock.util;

import android.content.Intent;
import android.net.Uri;
import com.android.deskclock.Alarm;

/* JADX INFO: loaded from: classes.dex */
public class SetAlarmUtil {
    public static final String ACTION_REQUEST_SET_ALARM = "com.android.deskclock.SET_ALARM";
    public static final String ACTION_RESPONSE_SET_ALARM = "com.android.deskclock.SET_ALARM_RESULT";
    public static final String EXTRA_SET_ALARM_RESULT = "set_alarm_result";
    public static final String PACKAGE_NAME_SMART_TRAVEL = "com.miui.smarttravel";
    public static final String REQUEST_ALARM_DELETE_AFTER_USE = "request_alarm_delete_after_use";
    public static final String REQUEST_ALARM_HOUR = "request_alarm_hour";
    public static final String REQUEST_ALARM_MINUTE = "request_alarm_minute";
    public static final String REQUEST_MESSAGE = "request_alarm_message";
    public static final String REQUEST_PACKAGE = "request_package";
    public static final String REQUEST_REPEAT_TYPE = "request_alarm_days_of_week";
    public static final String TAG = "SetAlarm";

    public static void handleRequestSetAlarm(Intent intent, Alarm alarm) {
        if (intent == null) {
            return;
        }
        Uri data = intent.getData();
        try {
            if (ACTION_REQUEST_SET_ALARM.equals(intent.getAction()) && intent.hasExtra(REQUEST_PACKAGE)) {
                Log.i(TAG, "intent from pkg: " + intent.getStringExtra(REQUEST_PACKAGE));
                if (PACKAGE_NAME_SMART_TRAVEL.equals(intent.getStringExtra(REQUEST_PACKAGE))) {
                    int intExtra = intent.getIntExtra(REQUEST_ALARM_HOUR, alarm.hour);
                    if (intExtra >= 0 && intExtra <= 23) {
                        alarm.hour = intExtra;
                    }
                    int intExtra2 = intent.getIntExtra(REQUEST_ALARM_MINUTE, alarm.minutes);
                    if (intExtra2 >= 0 && intExtra2 <= 59) {
                        alarm.minutes = intExtra2;
                    }
                    String stringExtra = intent.getStringExtra(REQUEST_MESSAGE);
                    if (stringExtra != null) {
                        alarm.label = stringExtra;
                    }
                    alarm.deleteAfterUse = intent.getBooleanExtra(REQUEST_ALARM_DELETE_AFTER_USE, true);
                    alarm.daysOfWeek = new Alarm.DaysOfWeek(intent.getIntExtra(REQUEST_REPEAT_TYPE, 0));
                    return;
                }
                return;
            }
            if (data != null) {
                Log.i(TAG, "uri: " + data);
                int iIntValue = Integer.valueOf(data.getQueryParameter(REQUEST_ALARM_HOUR)).intValue();
                if (iIntValue >= 0 && iIntValue <= 23) {
                    alarm.hour = iIntValue;
                }
                int iIntValue2 = Integer.valueOf(data.getQueryParameter(REQUEST_ALARM_MINUTE)).intValue();
                if (iIntValue2 >= 0 && iIntValue2 <= 59) {
                    alarm.minutes = iIntValue2;
                }
                String queryParameter = data.getQueryParameter(REQUEST_MESSAGE);
                if (queryParameter != null) {
                    alarm.label = queryParameter;
                }
                alarm.deleteAfterUse = Boolean.valueOf(data.getQueryParameter(REQUEST_ALARM_DELETE_AFTER_USE)).booleanValue();
                alarm.daysOfWeek = new Alarm.DaysOfWeek(Integer.valueOf(data.getQueryParameter(REQUEST_REPEAT_TYPE)).intValue());
            }
        } catch (Exception e) {
            Log.e("handleRequestSetAlarm error, error is ", e);
        }
    }

    public static Intent getAckIntent(Intent intent, boolean z) {
        Log.i(TAG, "getAckIntent for smart travel start");
        if (intent == null) {
            return null;
        }
        Uri data = intent.getData();
        if (intent.hasExtra(REQUEST_PACKAGE)) {
            if (!PACKAGE_NAME_SMART_TRAVEL.equals(intent.getStringExtra(REQUEST_PACKAGE))) {
                return null;
            }
            Intent intent2 = new Intent(ACTION_RESPONSE_SET_ALARM);
            intent2.setPackage(PACKAGE_NAME_SMART_TRAVEL);
            intent2.putExtra(EXTRA_SET_ALARM_RESULT, z);
            return intent2;
        }
        if (data == null || !PACKAGE_NAME_SMART_TRAVEL.equals(data.getQueryParameter(REQUEST_PACKAGE))) {
            return null;
        }
        Intent intent3 = new Intent(ACTION_RESPONSE_SET_ALARM);
        intent3.setPackage(PACKAGE_NAME_SMART_TRAVEL);
        intent3.putExtra(EXTRA_SET_ALARM_RESULT, z);
        return intent3;
    }
}
