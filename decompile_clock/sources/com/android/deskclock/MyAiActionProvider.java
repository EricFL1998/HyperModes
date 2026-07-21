package com.android.deskclock;

import android.app.KeyguardManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.monitor.MonitorHelper;
import com.android.deskclock.addition.ringtone.star.WYStarRingtoneHelper;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.settings.AlarmRingtonePickerActivity;
import com.android.deskclock.settings.AlarmSettingsFragment;
import com.android.deskclock.timer.Timer;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmUtils;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;
import miuix.core.util.SystemProperties;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class MyAiActionProvider extends AlarmProvider {
    public static final String ACTION_ALARM = "urn:aiot-spec-v3:com.mi.phones:action:[com.android.deskclock/deskclock/alarm]";
    public static final String ACTION_CANCEL_TIMER = "urn:aiot-spec-v3:com.mi.phones:action:[com.android.deskclock/timer/cancelTimer]";
    public static final String ACTION_CONTINUE_TIMER = "urn:aiot-spec-v3:com.mi.phones:action:[com.android.deskclock/timer/continueTimer]";
    public static final String ACTION_CREATE_TIMER = "urn:aiot-spec-v3:com.mi.phones:action:[com.android.deskclock/timer/createTimer]";
    public static final String ACTION_JUMP_RINGTONEPICKER = "urn:aiot-spec-v3:com.mi.phones:action:[com.android.deskclock/settings/jumpToRingtonePicker]";
    public static final String ACTION_JUMP_TIMER_PAGE = "urn:aiot-spec-v3:com.mi.phones:action:[com.android.deskclock/timer/jumpToTimerPage]";
    public static final String ACTION_PAUSE_TIMER = "urn:aiot-spec-v3:com.mi.phones:action:[com.android.deskclock/timer/pauseTimer]";
    public static final String ACTION_QUERY_TIMER = "urn:aiot-spec-v3:com.mi.phones:action:[com.android.deskclock/timer/queryTimer]";
    public static final int ALARM_CLOSE = 3;
    public static final int ALARM_CREATE = 0;
    public static final int ALARM_DELETE = 2;
    public static final int ALARM_UPDATE = 1;
    private static final int DEVICE_NO_SUPPORT_RINGTONE = -2;
    public static final String EXECUTE_ACTION = "execute_action";
    public static final int FAIL_CODE = -1;
    public static final int ISLAND_ABSTRACT_CODE = 1;
    public static final String ISLAND_CODE_NAME = "island";
    public static final int ISLAND_EXPAND_CODE = 2;
    private static final int IS_LAND_PROTOCOL_VERSION = 3;
    private static final String IS_LAND_SUPPORT = "1";
    private static final String KEY_ACTION_REQUEST_ID = "action_request_id";
    private static final String KEY_CLIENT_REQUEST_ID = "client_request_id";
    private static final String KEY_TARGET_RESPONSE_ID = "target_response_id";
    private static final String NOTIFICATION_FOCUS_PROTOCOL = "notification_focus_protocol";
    public static final int NOT_EXIST_CODE = -2;
    public static final int NOT_EXIST_TIMER = -2;
    public static final int NOT_ISLAND_CODE = -1;
    public static final String OUT_CODE_NAME = "target_out";
    public static final String STATUS_CODE_NAME = "status";
    private static final String TAG = "DC:MyAiActionProvider";
    public static final int TARGET_CODE_SUCCESS = 0;
    public static final String TARGET_ISLAND_CODE = "target_island_code";
    public static final int TARGET_OUT_FAILED_NO_MATCH_ITEMS = -1;
    public static final int TARGET_OUT_FAILED_PARAMETERS_ERROR = -2;
    public static final int TARGET_OUT_FAILED_UNKNOWN = 0;
    public static final int TARGET_OUT_SUCCESS = 1;
    public static final String TARGET_STATUS_CODE_NAME = "target_status_code";
    public static final String TARGET_STATUS_KEY = "target_status";
    public static final String TARGET_TIMER_REMAIN_CODE_NAME = "target_timer_remain_code";
    public static final int TIMER_CONTINUE_CODE = -3;
    public static final int TIMER_PARAS_ERROR_CODE = -2;
    public static final int TIMER_PAUSED_CODE = -3;
    public static final int TIMER_PAUSE_CODE = 1;
    public static final String TIMER_REMAIN_CODE_NAME = "remainTimer";
    public static final int TIMER_RING_CLOSE_SUCCESS = 1;
    public static final int TIMER_RUNNING_CODE = 0;
    public static final int UNKNOWN_ERROR_CODE = -99;
    private static final String XIAOAI_PACKAGE_NAME = "com.xiaomi.aicr";
    private String action_callback_uri;
    private Context mContext;
    private AlarmDatabaseHelper mOpenHelper;
    Bundle reply = new Bundle();
    private String requestId;

    private boolean isValidTime(int i, int i2) {
        return i >= -1 && i <= 23 && i2 >= -1 && i2 <= 59;
    }

    @Override // com.android.deskclock.AlarmProvider, android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // com.android.deskclock.AlarmProvider, android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // com.android.deskclock.AlarmProvider, android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // com.android.deskclock.AlarmProvider, android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override // com.android.deskclock.AlarmProvider, android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    @Override // com.android.deskclock.AlarmProvider, android.content.ContentProvider
    public boolean onCreate() {
        this.mContext = getContext();
        this.mOpenHelper = new AlarmDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(getContext()));
        return true;
    }

    @Override // com.android.deskclock.AlarmProvider, android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        Log.d(TAG, "method =" + str);
        String[] packagesForUid = DeskClockApp.getAppDEContext().getPackageManager().getPackagesForUid(Binder.getCallingUid());
        String str3 = (packagesForUid == null || packagesForUid.length <= 0) ? null : packagesForUid[0];
        if (str3 == null || !str3.equals(XIAOAI_PACKAGE_NAME)) {
            Log.d(TAG, "callingPackage is null or not xiaoai");
        } else {
            if (bundle == null) {
                this.reply.putInt(TARGET_STATUS_CODE_NAME, -1);
                return this.reply;
            }
            if (str.equals(EXECUTE_ACTION)) {
                String string = bundle.getString("type");
                String string2 = bundle.getString("in");
                if (bundle == null || string2 == null) {
                    this.reply.putInt(TARGET_STATUS_CODE_NAME, -1);
                    return this.reply;
                }
                this.action_callback_uri = bundle.getString("action_callback_uri");
                this.requestId = bundle.getString(KEY_ACTION_REQUEST_ID);
                if (string.contains(ACTION_ALARM)) {
                    this.reply = alarmAction(string2, this.reply);
                }
                if (string.contains(ACTION_CREATE_TIMER)) {
                    this.reply = timerCreateAction(string2, this.reply);
                }
                if (string.contains(ACTION_PAUSE_TIMER)) {
                    this.reply = timerPauseAction(this.reply);
                }
                if (string.contains(ACTION_CONTINUE_TIMER)) {
                    this.reply = timerContinueAction(this.reply);
                }
                if (string.contains(ACTION_CANCEL_TIMER)) {
                    this.reply = timerCancelAction(this.reply);
                }
                if (string.contains(ACTION_JUMP_TIMER_PAGE)) {
                    this.reply = timerJumpTimerPageAction(this.reply);
                }
                if (string.contains(ACTION_QUERY_TIMER)) {
                    this.reply = timerQueryAction(string2, this.reply);
                }
                if (string.contains(ACTION_JUMP_RINGTONEPICKER)) {
                    this.reply = jumpToRingtonePicker(string2, this.reply);
                }
            }
            if (this.action_callback_uri != null) {
                new Thread(new Runnable() { // from class: com.android.deskclock.MyAiActionProvider.1
                    @Override // java.lang.Runnable
                    public void run() {
                        MyAiActionProvider myAiActionProvider = MyAiActionProvider.this;
                        myAiActionProvider.working(myAiActionProvider.reply);
                    }
                }).start();
            }
        }
        return this.reply;
    }

    private Bundle timerQueryAction(String str, Bundle bundle) {
        Timer timer = TimerDao.getTimer(getContext());
        if (timer.getState() == 0 || timer.getState() == 3) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -2);
        } else if (timer.getState() == 1) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, 0);
            bundle.putInt(TARGET_ISLAND_CODE, 1);
            bundle.putString(TARGET_TIMER_REMAIN_CODE_NAME, String.valueOf(timer.getTime() - System.currentTimeMillis()));
        } else if (timer.getState() == 2) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, 1);
            bundle.putInt(TARGET_ISLAND_CODE, 1);
            bundle.putString(TARGET_TIMER_REMAIN_CODE_NAME, String.valueOf(timer.getRemain() + 900));
        } else {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
        }
        return bundle;
    }

    private Bundle timerJumpTimerPageAction(Bundle bundle) {
        Log.d(TAG, "jump to ringtonePicker");
        try {
            Intent intent = new Intent(this.mContext, (Class<?>) DeskClockTabActivity.class);
            intent.putExtra(Util.NAVIGATION_TAB, 3);
            this.mContext.startActivity(intent);
            bundle.putInt(TARGET_STATUS_CODE_NAME, 0);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
        }
        return bundle;
    }

    private Bundle timerCancelAction(Bundle bundle) {
        boolean z = AlarmUtils.timerRingForXiaoAi;
        Log.d(TAG, "istimerAlarming = " + z);
        if (z) {
            Intent intent = new Intent(AlarmHelper.ACTION_TIMER_DISMISS);
            intent.setPackage(getContext().getPackageName());
            getContext().sendBroadcast(intent);
            bundle.putInt(TARGET_STATUS_CODE_NAME, 1);
            bundle.putInt(TARGET_ISLAND_CODE, -1);
            Log.d(TAG, "close ringing timer");
        } else {
            Timer timer = TimerDao.getTimer(getContext());
            if (timer.getState() == 0 || timer.getState() == 3) {
                bundle.putInt(TARGET_STATUS_CODE_NAME, -2);
            } else if (timer.getState() == 1 || timer.getState() == 2) {
                TimerDao.deleteTimer((Context) Objects.requireNonNull(getContext()));
                bundle.putInt(TARGET_STATUS_CODE_NAME, 0);
                bundle.putInt(TARGET_ISLAND_CODE, 1);
            } else {
                bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
            }
        }
        return bundle;
    }

    private Bundle timerContinueAction(Bundle bundle) {
        Timer timer = TimerDao.getTimer(getContext());
        if (timer.getState() == 0 || timer.getState() == 3) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -2);
        } else if (timer.getState() == 1) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -3);
            bundle.putInt(TARGET_ISLAND_CODE, islandStatus());
        } else if (timer.getState() == 2) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, updateTimerStatus(1));
            bundle.putInt(TARGET_ISLAND_CODE, islandStatus());
        } else {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
        }
        return bundle;
    }

    private Bundle timerPauseAction(Bundle bundle) {
        Timer timer = TimerDao.getTimer(getContext());
        if (timer.getState() == 0 || timer.getState() == 3) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -2);
        } else if (timer.getState() == 2) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -3);
            bundle.putInt(TARGET_ISLAND_CODE, islandStatus());
        } else if (timer.getState() == 1) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, updateTimerStatus(2));
            bundle.putInt(TARGET_ISLAND_CODE, islandStatus());
        } else {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
        }
        return bundle;
    }

    private Bundle timerCreateAction(String str, Bundle bundle) {
        try {
            String string = new JSONObject(str).getString(TypedValues.CycleType.S_WAVE_OFFSET);
            if ("{}".equals(str) || TextUtils.isEmpty(string)) {
                bundle.putInt(TARGET_STATUS_CODE_NAME, -2);
            }
            bundle.putInt(TARGET_STATUS_CODE_NAME, timerStart(getTimerDurationByIn(string)));
        } catch (JSONException e) {
            Log.d(TAG, "timerCreateAction: " + e);
        }
        bundle.putInt(TARGET_ISLAND_CODE, islandStatus());
        return bundle;
    }

    private long getTimerDurationByIn(String str) {
        if (str == null || !str.matches("([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d")) {
            return -1L;
        }
        String[] strArrSplit = str.split(MethodCodeHelper.IDENTITY_INFO_SEPARATOR);
        try {
            return ((((long) Integer.parseInt(strArrSplit[0])) * 3600) + (((long) Integer.parseInt(strArrSplit[1])) * 60) + ((long) Integer.parseInt(strArrSplit[2]))) * 1000;
        } catch (NumberFormatException unused) {
            return -1L;
        }
    }

    private int islandStatus() {
        int i = Settings.System.getInt(this.mContext.getContentResolver(), NOTIFICATION_FOCUS_PROTOCOL, 0);
        String str = SystemProperties.get("persist.sys.feature.island", WorldClockEditActivity.LOCAL_CITY_ID);
        boolean zInKeyguardRestrictedInputMode = ((KeyguardManager) this.mContext.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
        Log.d(TAG, "islandStatus: " + i);
        Log.d(TAG, "islandEnable: " + str);
        return (i < 3 || zInKeyguardRestrictedInputMode || !Objects.equals(str, "1")) ? -1 : 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void working(Bundle bundle) {
        JSONObject jSONObject;
        int i = bundle.getInt(TARGET_STATUS_CODE_NAME);
        int i2 = bundle.getInt(TARGET_ISLAND_CODE);
        String string = bundle.getString(TARGET_TIMER_REMAIN_CODE_NAME);
        try {
            jSONObject = new JSONObject(bundle.getString(OUT_CODE_NAME));
        } catch (Exception unused) {
            jSONObject = new JSONObject();
        }
        try {
            jSONObject.put("status", i);
            jSONObject.put(ISLAND_CODE_NAME, i2);
            jSONObject.put(TIMER_REMAIN_CODE_NAME, string);
            bundle.putString(OUT_CODE_NAME, jSONObject.toString());
            if (i != 0) {
                bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
            }
            bundle.putString(KEY_TARGET_RESPONSE_ID, this.requestId);
            bundle.putString(KEY_CLIENT_REQUEST_ID, this.requestId);
            try {
                Thread.sleep(10L);
                getContext().getContentResolver().call(Uri.parse(this.action_callback_uri), "action_result", (String) null, bundle);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (JSONException e2) {
            throw new RuntimeException(e2);
        }
    }

    private Bundle alarmAction(String str, Bundle bundle) {
        AlarmData alarmData = (AlarmData) new Gson().fromJson(str, AlarmData.class);
        int alarmAction = alarmData.getAlarmAction();
        if (alarmAction == 0) {
            return insertAlarm(alarmData, bundle);
        }
        if (1 == alarmAction) {
            return updateAlarm(alarmData, bundle);
        }
        if (2 == alarmAction) {
            return deleteAlarm(alarmData, bundle);
        }
        if (3 == alarmAction) {
            return closeAlarm(alarmData, bundle);
        }
        bundle.putInt(TARGET_STATUS_CODE_NAME, -99);
        return bundle;
    }

    private Bundle jumpToRingtonePicker(String str, Bundle bundle) {
        Log.d(TAG, "jump to ringtonePicker");
        try {
            boolean z = WeekRingtoneHelper.isRomSupport() && WeatherRingtoneHelper.isRomSupport();
            if (Util.isTinyScreen(this.mContext) || MiuiSdk.isLiteOrMiddleMode() || !z) {
                bundle.putInt(TARGET_STATUS_CODE_NAME, -2);
            } else {
                Intent intent = new Intent(this.mContext, (Class<?>) AlarmRingtonePickerActivity.class);
                intent.setFlags(268468224);
                this.mContext.startActivity(intent);
                bundle.putInt(TARGET_STATUS_CODE_NAME, 0);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
        }
        return bundle;
    }

    private int updateTimerStatus(int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimerDao.KEY_STATE, Integer.valueOf(i));
        clearIdentity();
        TimerDao.updateTimer(getContext(), contentValues);
        return 0;
    }

    private int timerStart(long j) {
        if (((int) (j / AlarmHelper.ARRIVING_ALARM_DURATION)) > 24) {
            return -2;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("duration", Long.valueOf(j));
        TimerDao.handleXiaoAiTimer(getContext(), contentValues);
        Uri uriInsertTimer = TimerDao.insertTimer(getContext(), contentValues);
        if (uriInsertTimer == null) {
            return -1;
        }
        getContext().getContentResolver().notifyChange(uriInsertTimer, null);
        return 0;
    }

    private Bundle insertAlarm(AlarmData alarmData, Bundle bundle) throws Throwable {
        int hour = alarmData.getHour();
        int minute = alarmData.getMinute();
        ContentValues contentValues = new ContentValues();
        contentValues.put("enabled", "1");
        contentValues.put("deleteAfterUse", "1");
        contentValues.put("alarmtime", "");
        contentValues.put("hour", String.valueOf(hour));
        contentValues.put("minutes", String.valueOf(minute));
        contentValues.put("alert", "content://settings/system/alarm_alert");
        contentValues.put("vibrate", "1");
        contentValues.put("message", "");
        contentValues.put("daysofweek", WorldClockEditActivity.LOCAL_CITY_ID);
        contentValues.put("skiptime", "1");
        contentValues.put("type", (Integer) 0);
        contentValues.put("is_smart_ringtone", (Boolean) false);
        WYStarRingtoneHelper.updateWYStarAlertToDefault(contentValues);
        boolean z = contentValues.containsKey("deleteAfterUse") && contentValues.getAsBoolean("deleteAfterUse").booleanValue() && contentValues.containsKey("daysofweek") && contentValues.getAsInteger("daysofweek").intValue() == 0;
        Boolean[] boolArrIsSmartRingtone = isSmartRingtone(contentValues);
        contentValues.put("deleteAfterUse", Boolean.valueOf(z));
        contentValues.put("vibrate", Boolean.valueOf(AlarmSettingsFragment.getVibrateState()));
        Uri uriCommonInsert = this.mOpenHelper.commonInsert(contentValues);
        long j = Long.parseLong(uriCommonInsert.getLastPathSegment());
        clearIdentity();
        int i = (int) j;
        MonitorHelper.modify(5, System.currentTimeMillis(), i, contentValues);
        if (boolArrIsSmartRingtone[0].booleanValue()) {
            XiaoAiRingtoneHelper.addXiaoAiRingtoneIds(getContext(), i);
        } else if (boolArrIsSmartRingtone[1].booleanValue()) {
            Log.d(TAG, "insert alarm from xiaoai, not sure alarm ");
            XiaoAiRingtoneHelper.preHandleNotSureAlarm(i);
        }
        AlarmHelper.setNextAlert(getContext());
        if (uriCommonInsert != null) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, 0);
        } else {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
        }
        return bundle;
    }

    private Bundle deleteAlarm(AlarmData alarmData, Bundle bundle) {
        int hour = alarmData.getHour();
        int minute = alarmData.getMinute();
        SQLiteDatabase readableDatabase = this.mOpenHelper.getReadableDatabase();
        List<Alarm> listQueryAlarmForTime = queryAlarmForTime(readableDatabase, "hour = " + hour + " AND minutes = " + minute);
        if (listQueryAlarmForTime.size() == 0) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -2);
            return bundle;
        }
        for (int i = 0; i < listQueryAlarmForTime.size(); i++) {
            long j = listQueryAlarmForTime.get(i).id;
            if (readableDatabase.delete("alarms", "_id = " + j, null) == 0) {
                bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
                return bundle;
            }
            clearIdentity();
            MonitorHelper.modify(6, System.currentTimeMillis(), (int) j, null);
            AlarmHelper.setNextAlert(getContext());
            getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, j), null);
        }
        readableDatabase.close();
        bundle.putInt(TARGET_STATUS_CODE_NAME, 0);
        return bundle;
    }

    private Bundle updateAlarm(AlarmData alarmData, Bundle bundle) {
        int hour = alarmData.getHour();
        int minute = alarmData.getMinute();
        int newHour = alarmData.getNewHour();
        int newMinute = alarmData.getNewMinute();
        SQLiteDatabase readableDatabase = this.mOpenHelper.getReadableDatabase();
        List<Alarm> listQueryAlarmForTime = queryAlarmForTime(readableDatabase, "hour = " + hour + " AND minutes = " + minute);
        if (listQueryAlarmForTime.size() == 0) {
            bundle.putInt(TARGET_STATUS_CODE_NAME, -2);
            return bundle;
        }
        int i = 0;
        while (i < listQueryAlarmForTime.size()) {
            Alarm alarm = listQueryAlarmForTime.get(i);
            ContentValues contentValues = new ContentValues();
            contentValues.put("hour", String.valueOf(newHour));
            contentValues.put("minutes", String.valueOf(newMinute));
            long j = alarm.id;
            int iUpdate = readableDatabase.update("alarms", contentValues, "_id=" + j, null);
            clearIdentity();
            int i2 = newHour;
            int i3 = (int) j;
            MonitorHelper.modify(7, System.currentTimeMillis(), i3, contentValues);
            AlarmHelper.updateAlarmTime(getContext(), i3);
            AlarmHelper.setNextAlert(getContext());
            if (iUpdate == 0) {
                bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
                return bundle;
            }
            getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, j), null);
            i++;
            newHour = i2;
        }
        readableDatabase.close();
        bundle.putInt(TARGET_STATUS_CODE_NAME, 0);
        return bundle;
    }

    private List<Alarm> queryAlarmForTime(SQLiteDatabase sQLiteDatabase, String str) {
        String str2;
        if (TextUtils.isEmpty(str)) {
            str2 = Alarm.Columns.WHERE_NORMAL_ALARM;
        } else {
            str2 = "type=0 AND (" + str + ")";
        }
        Cursor cursorQuery = sQLiteDatabase.query("alarms", Alarm.Columns.ALARM_QUERY_COLUMNS, str2, null, null, null, null);
        ArrayList arrayList = new ArrayList();
        if (cursorQuery != null) {
            while (cursorQuery.moveToNext()) {
                arrayList.add(new Alarm(cursorQuery));
            }
            cursorQuery.close();
        }
        return arrayList;
    }

    private Bundle closeAlarm(AlarmData alarmData, Bundle bundle) {
        MyAiActionProvider myAiActionProvider = this;
        SQLiteDatabase readableDatabase = myAiActionProvider.mOpenHelper.getReadableDatabase();
        JSONObject jSONObject = new JSONObject();
        try {
            String queryString = parseQueryString(alarmData);
            boolean zIsEmpty = TextUtils.isEmpty(queryString);
            String str = TARGET_STATUS_KEY;
            if (zIsEmpty) {
                Log.d(TAG, "closeAlarm TARGET_OUT_FAILED_PARAMETERS_ERROR");
                jSONObject.put(TARGET_STATUS_KEY, -2);
                readableDatabase.close();
                bundle.putString(OUT_CODE_NAME, jSONObject.toString());
                bundle.putInt(TARGET_STATUS_CODE_NAME, 0);
                return bundle;
            }
            List<Alarm> listQueryAlarmForTime = myAiActionProvider.queryAlarmForTime(readableDatabase, queryString);
            if (listQueryAlarmForTime.isEmpty()) {
                Log.d(TAG, "closeAlarm TARGET_OUT_FAILED_NO_MATCH_ITEMS");
                jSONObject.put(TARGET_STATUS_KEY, -1);
            }
            Iterator<Alarm> it = listQueryAlarmForTime.iterator();
            while (it.hasNext()) {
                Alarm next = it.next();
                if (!myAiActionProvider.needSkip(alarmData, next)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("enabled", WorldClockEditActivity.LOCAL_CITY_ID);
                    long j = next.id;
                    int iUpdate = readableDatabase.update("alarms", contentValues, "_id=" + j, null);
                    clearIdentity();
                    String str2 = str;
                    int i = (int) j;
                    Iterator<Alarm> it2 = it;
                    MonitorHelper.modify(7, System.currentTimeMillis(), i, contentValues);
                    AlarmHelper.updateAlarmTime(getContext(), i);
                    AlarmHelper.setNextAlert(getContext());
                    if (iUpdate == 0) {
                        Log.d(TAG, "closeAlarm TARGET_OUT_FAILED_UNKNOWN");
                        jSONObject.put(str2, 0);
                        bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
                        readableDatabase.close();
                        return bundle;
                    }
                    getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, j), null);
                    Log.d(TAG, "closeAlarm TARGET_OUT_SUCCESS");
                    jSONObject.put(str2, 1);
                    myAiActionProvider = this;
                    str = str2;
                    it = it2;
                }
            }
            readableDatabase.close();
            bundle.putString(OUT_CODE_NAME, jSONObject.toString());
            bundle.putInt(TARGET_STATUS_CODE_NAME, 0);
            return bundle;
        } catch (JSONException e) {
            Log.e(TAG, "closeAlarm " + e);
            bundle.putInt(TARGET_STATUS_CODE_NAME, -1);
        }
    }

    private String parseQueryString(AlarmData alarmData) {
        int hour = alarmData.getHour();
        int minute = alarmData.getMinute();
        if (minute < 0) {
            minute = 0;
        }
        int newHour = alarmData.getNewHour();
        int newMinute = alarmData.getNewMinute();
        int i = newMinute >= 0 ? newMinute : 0;
        String label = alarmData.getLabel();
        if (!isValidTime(hour, minute) || !isValidTime(newHour, i)) {
            return "";
        }
        String str = TextUtils.isEmpty(label) ? "" : "message = \"" + label + "\" COLLATE NOCASE";
        if (!TextUtils.isEmpty(label) && (hour > -1 || newHour > -1)) {
            str = str + " AND ";
        }
        if (hour > -1 && newHour > -1) {
            return str + "((hour > " + hour + " OR (hour = " + hour + " AND minutes >= " + minute + ")) AND (hour < " + newHour + " OR (hour = " + newHour + " AND minutes <= " + i + ")))";
        }
        if (hour > -1) {
            return str + "(hour > " + hour + " OR (hour = " + hour + " AND minutes > " + minute + "))";
        }
        return newHour > -1 ? str + "(hour < " + newHour + " OR (hour = " + newHour + " AND minutes < " + i + "))" : str;
    }

    private boolean needSkip(AlarmData alarmData, Alarm alarm) {
        if (alarmData.getDayBinary() < 1) {
            return false;
        }
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        int value = 1 << (localDateTimeNow.getDayOfWeek().getValue() - 1);
        int dayBinary = alarmData.getDayBinary();
        int hour = localDateTimeNow.getHour();
        int minute = localDateTimeNow.getMinute();
        if (alarm.daysOfWeek.get() != 0) {
            return (alarmData.getDayBinary() & alarm.daysOfWeek.get()) <= 0;
        }
        boolean z = dayBinary == value;
        boolean z2 = dayBinary == (value << 1);
        boolean z3 = alarm.hour > hour || (alarm.hour == hour && alarm.minutes > minute);
        if (z && z3) {
            return false;
        }
        return !z2 || z3;
    }

    private boolean checkPermission() {
        String[] packagesForUid = getContext().getPackageManager().getPackagesForUid(Binder.getCallingUid());
        String str = (packagesForUid == null || packagesForUid.length <= 0) ? null : packagesForUid[0];
        return str != null && str.equals(XIAOAI_PACKAGE_NAME) && getContext().checkCallingPermission("hyperos.permission.READ_AIACTION") == 0;
    }

    public class AlarmData {
        private int alarmAction = -1;
        private int dayBinary = -1;
        private int hour = -1;
        private int minute = -1;
        private int newHour = -1;
        private int newMinute = -1;
        private String label = "";

        public AlarmData() {
        }

        public int getAlarmAction() {
            return this.alarmAction;
        }

        public void setAlarmAction(int i) {
            this.alarmAction = i;
        }

        public int getDayBinary() {
            return this.dayBinary;
        }

        public void setDayBinary(int i) {
            this.dayBinary = i;
        }

        public int getHour() {
            return this.hour;
        }

        public void setHour(int i) {
            this.hour = i;
        }

        public int getMinute() {
            return this.minute;
        }

        public void setMinute(int i) {
            this.minute = i;
        }

        public int getNewHour() {
            return this.newHour;
        }

        public void setNewHour(int i) {
            this.newHour = i;
        }

        public int getNewMinute() {
            return this.newMinute;
        }

        public void setNewMinute(int i) {
            this.newMinute = i;
        }

        public String getLabel() {
            return this.label;
        }

        public void setLabel(String str) {
            this.label = str;
        }
    }

    public class TimerData {
        private String duration;
        private int timerAction;

        public TimerData() {
        }

        public int getTimerAction() {
            return this.timerAction;
        }

        public void setTimerAction(int i) {
            this.timerAction = i;
        }

        public String getDuration() {
            return this.duration;
        }

        public void setDuration(String str) {
            this.duration = str;
        }
    }
}
