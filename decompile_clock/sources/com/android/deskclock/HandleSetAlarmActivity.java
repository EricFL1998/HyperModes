package com.android.deskclock;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.android.deskclock.alarm.SetAlarmController;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.settings.AlarmSettingsActivity;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.timer.TimerService;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.util.ArrayList;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class HandleSetAlarmActivity extends BaseActivity {
    private static final String ITRANSFER_PACKAGE_NAME = "com.android.itransfer";
    private static final String ITRANSFER_PACKAGE_NAME_KEY = "CALLER_PACKAGE";

    @Override // com.android.deskclock.base.BaseActivity
    protected void checkCtaStatement() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        try {
            try {
                super.onCreate(bundle);
                Intent intent = getIntent();
                if (intent != null) {
                    Log.d("HandleSetAlarmActivity intent.getAction() = " + intent.getAction());
                    if ("android.intent.action.SET_ALARM".equals(intent.getAction())) {
                        handleSetAlarm(intent);
                    } else if (AlarmClockExtras.ACTION_SHOW_ALARMS.equals(intent.getAction())) {
                        handleShowAlarms();
                    } else if (AlarmClockExtras.ACTION_SET_TIMER.equals(intent.getAction())) {
                        handleSetTimer(intent);
                    } else if (AlarmClockExtras.ACTION_SHOW_TIMERS.equals(intent.getAction())) {
                        handleShowTimers();
                    } else if (AlarmClockExtras.ACTION_DISMISS_TIMER.equals(intent.getAction())) {
                        handleDismissTimer();
                    } else if (AlarmClockExtras.ACTION_DISMISS_ALARM.equals(intent.getAction())) {
                        handleDismissAlarm();
                    } else if ("android.intent.action.SNOOZE_ALARM".equals(intent.getAction())) {
                        handleSnoozeAlarm(intent);
                    }
                }
            } catch (Exception e) {
                Log.e("HandleSetAlarmActivity onCreate error, error is ", e);
            }
        } finally {
            finish();
        }
    }

    private void handleSetAlarm(Intent intent) {
        String str;
        Uri defaultUri;
        long timeInMillis;
        HandleSetAlarmActivity handleSetAlarmActivity;
        String toastNew;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int intExtra = intent.getIntExtra("android.intent.extra.alarm.HOUR", calendar.get(11));
        if (intExtra < 0 || intExtra > 23) {
            VoiceController.getController().notifyVoiceFailure(this, getString(R.string.invalid_time, new Object[]{Integer.valueOf(intExtra), Integer.valueOf(intent.getIntExtra("android.intent.extra.alarm.MINUTES", 0)), " "}));
            Log.i("Illegal hour: " + intExtra);
            return;
        }
        int intExtra2 = intent.getIntExtra("android.intent.extra.alarm.MINUTES", calendar.get(12));
        if (intExtra2 < 0 || intExtra2 > 59) {
            VoiceController.getController().notifyVoiceFailure(this, getString(R.string.invalid_time, new Object[]{Integer.valueOf(intExtra), Integer.valueOf(intExtra2), " "}));
            Log.i("Illegal minute: " + intExtra2);
            return;
        }
        boolean booleanExtra = intent.getBooleanExtra("android.intent.extra.alarm.SKIP_UI", false);
        String stringExtra = intent.getStringExtra("android.intent.extra.alarm.MESSAGE");
        boolean booleanExtra2 = intent.getBooleanExtra(AlarmClockExtras.EXTRA_VIBRATE, true);
        String stringExtra2 = intent.getStringExtra(AlarmClockExtras.EXTRA_RINGTONE);
        if (stringExtra2 == null) {
            defaultUri = RingtoneManager.getDefaultUri(4);
            str = "silent";
        } else if ("silent".equals(stringExtra2) || stringExtra2.isEmpty()) {
            str = "silent";
            defaultUri = AlarmClockExtras.NO_RINGTONE_URI;
        } else {
            if (Util.uriHasUserId(Uri.parse(stringExtra2))) {
                str = "silent";
                if (Util.getUserIdFromUri(Uri.parse(stringExtra2)) != Util.getCurrentUser()) {
                    Log.d("getUserIdFromUri: " + Util.getUserIdFromUri(Uri.parse(stringExtra2)) + ", getUserId: " + Util.getCurrentUser());
                    defaultUri = RingtoneManager.getDefaultUri(4);
                }
            } else {
                str = "silent";
            }
            defaultUri = Uri.parse(stringExtra2);
        }
        String string = AlarmClockExtras.NO_RINGTONE_URI.equals(defaultUri) ? str : defaultUri.toString();
        Log.v("HandleSetAlarmActivity#:handleSetAlarm ringToneUri: " + defaultUri);
        Alarm.DaysOfWeek daysFromIntent = getDaysFromIntent(intent);
        Uri uri = defaultUri;
        Log.v("HandleSetAlarmActivity#:handleSetAlarm dayOfWeek:" + daysFromIntent.getCoded());
        int i = (daysFromIntent.isRepeatSet() || !booleanExtra) ? 0 : 1;
        if (stringExtra == null) {
            stringExtra = "";
        }
        String stringExtra3 = intent.getStringExtra(ITRANSFER_PACKAGE_NAME_KEY);
        Log.d("HandleSetAlarmActivity#:handleSetAlarm packageName: " + stringExtra3);
        if (stringExtra3 != null && stringExtra3.equals(ITRANSFER_PACKAGE_NAME)) {
            timeInMillis = AlarmHelper.calculateAlarmTime(this, intExtra, intExtra2, daysFromIntent).getTimeInMillis();
        } else {
            timeInMillis = AlarmHelper.calculateAlarmTime(this, intExtra, intExtra2, new Alarm.DaysOfWeek(0)).getTimeInMillis();
        }
        long j = timeInMillis;
        ContentValues contentValues = new ContentValues();
        contentValues.put("hour", Integer.valueOf(intExtra));
        contentValues.put("minutes", Integer.valueOf(intExtra2));
        contentValues.put("message", stringExtra);
        contentValues.put("enabled", (Integer) 1);
        contentValues.put("vibrate", Integer.valueOf(booleanExtra2 ? 1 : 0));
        contentValues.put("daysofweek", Integer.valueOf(daysFromIntent.getCoded()));
        contentValues.put("alarmtime", Long.valueOf(j));
        contentValues.put("alert", string);
        contentValues.put("deleteAfterUse", Integer.valueOf(i));
        Cursor cursorQuery = null;
        try {
            cursorQuery = getContentResolver().query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, "hour=" + intExtra + " AND minutes=" + intExtra2 + " AND daysofweek=" + daysFromIntent.getCoded() + " AND vibrate=" + (booleanExtra2 ? 1 : 0) + " AND alarmtime=" + j + " AND enabled=1 AND message=?  AND alert=?  AND deleteAfterUse=" + i, new String[]{stringExtra, string}, null);
            if (handleCursorResult(cursorQuery, j, booleanExtra, contentValues)) {
                if (cursorQuery != null) {
                    cursorQuery.close();
                    return;
                }
                return;
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            long jCurrentTimeMillis = j - System.currentTimeMillis();
            if (booleanExtra) {
                ContentResolver contentResolver = getContentResolver();
                AlarmHelper.getAlarm(contentResolver, (int) Long.parseLong(contentResolver.insert(Alarm.Columns.CONTENT_URI, contentValues).getLastPathSegment())).enabled = true;
                AlarmHelper.setNextAlert(this);
                SetAlarmController.popAlarmSetToast(j);
                if (jCurrentTimeMillis > 60000) {
                    handleSetAlarmActivity = this;
                    toastNew = Util.formatToast(handleSetAlarmActivity, j, R.array.alarm_set_new_array);
                } else {
                    handleSetAlarmActivity = this;
                    toastNew = Util.formatToastNew(this);
                }
            } else {
                handleSetAlarmActivity = this;
                Alarm alarm = new Alarm();
                alarm.hour = intExtra;
                alarm.minutes = intExtra2;
                alarm.enabled = true;
                alarm.vibrate = booleanExtra2;
                alarm.daysOfWeek = daysFromIntent;
                alarm.time = j;
                alarm.alert = uri;
                alarm.label = stringExtra;
                Intent intent2 = new Intent(handleSetAlarmActivity, (Class<?>) DeskClockTabActivity.class);
                intent2.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
                intent2.putExtra(Util.NAVIGATION_TAB, 0);
                intent2.putExtra(Util.IS_FROM_CTS_SET_ALARM, true);
                handleSetAlarmActivity.startActivity(intent2);
                if (jCurrentTimeMillis > 60000) {
                    toastNew = Util.formatToast(handleSetAlarmActivity, j, R.array.alarm_set_new_array);
                } else {
                    toastNew = Util.formatToastNew(this);
                }
            }
            VoiceController.getController().notifyVoiceSuccess(handleSetAlarmActivity, toastNew);
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    private Alarm.DaysOfWeek getDaysFromIntent(Intent intent) {
        Alarm.DaysOfWeek daysOfWeek = new Alarm.DaysOfWeek(0);
        ArrayList<Integer> integerArrayListExtra = intent.getIntegerArrayListExtra(AlarmClockExtras.EXTRA_DAYS);
        if (integerArrayListExtra != null) {
            int[] iArr = new int[integerArrayListExtra.size()];
            for (int i = 0; i < integerArrayListExtra.size(); i++) {
                iArr[i] = integerArrayListExtra.get(i).intValue();
            }
            daysOfWeek.setDaysOfWeek(true, iArr);
        } else {
            int[] intArrayExtra = intent.getIntArrayExtra(AlarmClockExtras.EXTRA_DAYS);
            if (intArrayExtra != null) {
                daysOfWeek.setDaysOfWeek(true, intArrayExtra);
            }
        }
        return daysOfWeek;
    }

    private boolean handleCursorResult(Cursor cursor, long j, boolean z, ContentValues contentValues) {
        String toastNew;
        long jCurrentTimeMillis = j - System.currentTimeMillis();
        if (cursor == null || !cursor.moveToFirst()) {
            return false;
        }
        Alarm alarm = new Alarm(cursor);
        if (!z) {
            Intent intent = new Intent(this, (Class<?>) DeskClockTabActivity.class);
            intent.putExtra(Util.NAVIGATION_TAB, 0);
            intent.putExtra(Util.IS_FROM_CTS_SET_ALARM, true);
            intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
            startActivity(intent);
            if (jCurrentTimeMillis > 60000) {
                toastNew = Util.formatToast(this, j, R.array.alarm_set_new_array);
            } else {
                toastNew = Util.formatToastNew(this);
            }
        } else {
            SetAlarmController.popAlarmSetToast(j);
            getContentResolver().update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarm.id), contentValues, null, null);
            if (jCurrentTimeMillis > 60000) {
                toastNew = Util.formatToast(this, j, R.array.alarm_set_new_array);
            } else {
                toastNew = Util.formatToastNew(this);
            }
        }
        VoiceController.getController().notifyVoiceSuccess(this, toastNew);
        return true;
    }

    private void handleShowAlarms() {
        startActivity(new Intent(this, (Class<?>) DeskClockTabActivity.class).putExtra(Util.IS_FROM_CTS_SET_ALARM, false).putExtra(Util.NAVIGATION_TAB, 0));
    }

    private void handleSetTimer(Intent intent) {
        FBEUtil.getDefaultSharedPreferences(this);
        if (!intent.hasExtra(AlarmClockExtras.EXTRA_LENGTH)) {
            startActivity(new Intent(this, (Class<?>) DeskClockTabActivity.class).putExtra(Util.NAVIGATION_TAB, 3));
            return;
        }
        long intExtra = ((long) intent.getIntExtra(AlarmClockExtras.EXTRA_LENGTH, 0)) * 1000;
        if (intExtra < 1000 || intExtra > TimerDao.TIMER_MAX_LENGTH) {
            VoiceController.getController().notifyVoiceFailure(this, getString(R.string.invalid_timer_length));
            Log.i("Invalid timer length requested: " + intExtra);
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis() + intExtra;
        boolean booleanExtra = intent.getBooleanExtra("android.intent.extra.alarm.SKIP_UI", false);
        String stringExtra = intent.getStringExtra("android.intent.extra.alarm.MESSAGE");
        Log.v("HandleSetAlarmActivity#handleSetTimer: length:" + intExtra);
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(this).edit();
        editorEdit.putLong(TimerDao.KEY_END_TIME, jCurrentTimeMillis);
        editorEdit.putLong("duration", intExtra);
        editorEdit.commit();
        if (!booleanExtra) {
            Intent intentPutExtra = new Intent(this, (Class<?>) DeskClockTabActivity.class).putExtra(Util.NAVIGATION_TAB, 3).putExtra(AlarmClockExtras.TIMER_INTENT_EXTRA, intExtra);
            if (stringExtra != null) {
                intentPutExtra.putExtra(AlarmHelper.ACTION_TIMER_NAME, stringExtra);
            }
            startActivity(intentPutExtra);
        }
        Intent intent2 = new Intent(this, (Class<?>) TimerService.class);
        intent2.putExtra(AlarmClockExtras.TIMER_INTENT_EXTRA, intExtra);
        if (!TextUtils.isEmpty(stringExtra)) {
            intent2.putExtra(AlarmHelper.ACTION_TIMER_NAME, stringExtra);
        }
        if (!intent.hasExtra(AlarmClockExtras.EXTRA_LENGTH)) {
            intent2.putExtra(Util.IS_START_TIMER, false);
        } else {
            intent2.putExtra(Util.IS_START_TIMER, true);
        }
        startService(intent2);
        VoiceController.getController().notifyVoiceSuccess(this, getString(R.string.timer_created));
        Log.v("HandleSetAlarmActivity#handleSetTimer: notifyVoiceSuccess");
    }

    private void handleShowTimers() {
        startActivity(new Intent(this, (Class<?>) DeskClockTabActivity.class).putExtra(Util.NAVIGATION_TAB, 3));
    }

    private void handleDismissTimer() {
        if (FBEUtil.getDefaultSharedPreferences(this).getInt(TimerDao.KEY_STATE, 0) != 0) {
            Log.d("handleDismissTimer success by change timerState");
            SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(this).edit();
            editorEdit.putLong(TimerDao.KEY_STATE, 0L);
            editorEdit.commit();
            VoiceController.getController().notifyVoiceSuccess(this, getString(R.string.expired_timers_dismissed));
            return;
        }
        Intent intent = new Intent(AlarmHelper.ACTION_TIMER_DISMISS);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
        VoiceController.getController().notifyVoiceSuccess(this, getString(R.string.expired_timers_dismissed));
        Log.d("handleDismissTimer success by dismiss timer");
    }

    private void handleDismissAlarm() {
        Intent intent = new Intent(this, (Class<?>) DeskClockTabActivity.class);
        intent.putExtra(Util.NAVIGATION_TAB, 0);
        intent.putExtra(Util.IS_FROM_CTS_SET_ALARM, false);
        startActivity(intent);
        VoiceController.getController().notifyVoiceSuccess(this, "success");
    }

    private void handleSnoozeAlarm(Intent intent) {
        Intent intent2 = new Intent(this, (Class<?>) DeskClockTabActivity.class);
        intent2.putExtra(Util.NAVIGATION_TAB, 0);
        intent2.putExtra(Util.IS_FROM_CTS_SET_ALARM, false);
        startActivity(intent2);
        String stringExtra = intent.getStringExtra("android.intent.extra.alarm.SNOOZE_DURATION");
        try {
            int i = Integer.parseInt(stringExtra);
            if (i == 1 || (i > 1 && i <= 30 && i % 5 == 0)) {
                SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(this).edit();
                editorEdit.putString("snooze_duration", stringExtra);
                editorEdit.apply();
                AlarmSettingsActivity.popSnoozeSetToast(getResources().getQuantityString(R.plurals.snooze_duration_changed_info, i, Integer.valueOf(i)));
                VoiceController.getController().notifyVoiceSuccess(this, "success");
            }
        } catch (Exception e) {
            Log.e("updateSnooze error: " + e.getMessage());
            VoiceController.getController().notifyVoiceFailure(this, "failure");
        }
    }
}
