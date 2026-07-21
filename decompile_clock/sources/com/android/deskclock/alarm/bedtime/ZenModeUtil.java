package com.android.deskclock.alarm.bedtime;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ZenModeUtil {
    private static final String CLASS_NAME_FOR_ZENMODE = "SoundMode";
    private static final String KEY_IN_ZENMODE = "inZenMode";
    public static final String KEY_ZEN_RULE_END_TIME = "zenRuleEndTime";
    public static final String KEY_ZEN_RULE_ID = "zenRuleId";
    public static final String KEY_ZEN_RULE_START_TIME = "zenRuleStartTime";
    private static final String METHOD_NAME_FOR_ZENMODE = "setZenModeOn";
    private static final String PKG_NAME_MIUI_SETTING = "android.provider.MiuiSettings";
    private static final String RULE_REPEAT = "1.2.3.4.5.6.7";
    private static final String SCHEDULE_PATH = "schedule";
    private static final String SYSTEM_AUTHORITY = "android";
    private static Map<String, String> mRuleNameMap = new HashMap();
    private static final String RULE_NAME = DeskClockApp.getAppContext().getResources().getString(R.string.deskclock_sleep_no_disturbance);

    public static void enterZenMode(Context context) {
        if (!MiuiSdk.isSupportSleep() || !FBEUtil.isUserUnlocked(context)) {
            Log.f("DC:enterZenMode", "do nothing, return");
            return;
        }
        if (Build.VERSION.SDK_INT >= 30 && updateZenCheckPref(context, true)) {
            clearZenMode(context);
            FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putBoolean(KEY_IN_ZENMODE, true).apply();
            Log.d("DC:enterZenMode", "enterZenMode in R success");
            return;
        }
        int sleepAlarmHour = BedtimeUtil.getSleepAlarmHour(context);
        int sleepAlarmMin = BedtimeUtil.getSleepAlarmMin(context);
        int wakeAlarmHour = BedtimeUtil.getWakeAlarmHour(context);
        int wakeAlarmMin = BedtimeUtil.getWakeAlarmMin(context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (notificationManager.isNotificationPolicyAccessGranted()) {
            Uri conditionId = getConditionId(sleepAlarmHour + "." + sleepAlarmMin, wakeAlarmHour + "." + wakeAlarmMin);
            String strCheckIsExist = checkIsExist(getLocalConditionId(context), context);
            if (strCheckIsExist != null) {
                AutomaticZenRule automaticZenRule = notificationManager.getAutomaticZenRule(strCheckIsExist);
                automaticZenRule.setInterruptionFilter(2);
                automaticZenRule.setConditionId(conditionId);
                automaticZenRule.setEnabled(true);
                notificationManager.updateAutomaticZenRule(strCheckIsExist, automaticZenRule);
                setLocalZenRuleId(strCheckIsExist, context);
                setLocalZenRuleTime(context, sleepAlarmHour, sleepAlarmMin, wakeAlarmHour, wakeAlarmMin);
                return;
            }
            setLocalZenRuleId(notificationManager.addAutomaticZenRule(createAutomaticZenRule(getRuleName(), conditionId, true)), context);
            setLocalZenRuleTime(context, sleepAlarmHour, sleepAlarmMin, wakeAlarmHour, wakeAlarmMin);
        }
    }

    public static void exitZenMode(Context context) {
        String strCheckIsExist;
        if (!MiuiSdk.isSupportSleep() || !FBEUtil.isUserUnlocked(context)) {
            Log.f("DC:exitZenMode", "do nothing, return");
            return;
        }
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0);
        if (Build.VERSION.SDK_INT >= 30 && sharedPreferences.getBoolean(KEY_IN_ZENMODE, false) && updateZenCheckPref(context, false)) {
            sharedPreferences.edit().putBoolean(KEY_IN_ZENMODE, false).apply();
            clearZenMode(context);
            Log.d("DC:exitZenMode", "exitZenMode in R success");
            return;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (!notificationManager.isNotificationPolicyAccessGranted() || (strCheckIsExist = checkIsExist(getLocalConditionId(context), context)) == null) {
            return;
        }
        AutomaticZenRule automaticZenRule = notificationManager.getAutomaticZenRule(strCheckIsExist);
        automaticZenRule.setEnabled(false);
        notificationManager.updateAutomaticZenRule(strCheckIsExist, automaticZenRule);
        setLocalZenRuleId(strCheckIsExist, context);
    }

    private static String checkIsExist(Uri uri, Context context) {
        mRuleNameMap.clear();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        String str = null;
        if (notificationManager.isNotificationPolicyAccessGranted()) {
            String localZenRuleId = getLocalZenRuleId(context);
            String string = uri.toString();
            for (Map.Entry<String, AutomaticZenRule> entry : notificationManager.getAutomaticZenRules().entrySet()) {
                String key = entry.getKey();
                AutomaticZenRule value = entry.getValue();
                int interruptionFilter = value.getInterruptionFilter();
                String string2 = value.getConditionId().toString();
                String name = value.getName();
                if (2 == interruptionFilter && string.equals(string2) && (localZenRuleId.equals(key) || name.startsWith(RULE_NAME))) {
                    str = key;
                }
                String str2 = RULE_NAME;
                if (name.startsWith(str2)) {
                    mRuleNameMap.put(name, name.substring(str2.length()));
                }
            }
        }
        return str;
    }

    private static String getRuleName() {
        String str = RULE_NAME;
        Iterator<Map.Entry<String, String>> it = mRuleNameMap.entrySet().iterator();
        int i = -1;
        while (it.hasNext()) {
            String strTrim = it.next().getValue().trim();
            if (TextUtils.isEmpty(strTrim)) {
                strTrim = WorldClockEditActivity.LOCAL_CITY_ID;
            }
            try {
                int iAbs = Math.abs(Integer.valueOf(strTrim).intValue());
                if (iAbs > i) {
                    i = iAbs;
                }
            } catch (NumberFormatException unused) {
            }
        }
        return i > -1 ? str + String.valueOf(i + 1) : str;
    }

    private static AutomaticZenRule createAutomaticZenRule(String str, Uri uri, boolean z) {
        return new AutomaticZenRule(str, new ComponentName(SYSTEM_AUTHORITY, "ScheduleConditionProvider"), uri, 2, z);
    }

    private static Uri getConditionId(String str, String str2) {
        return new Uri.Builder().scheme("condition").authority(SYSTEM_AUTHORITY).appendPath(SCHEDULE_PATH).appendQueryParameter("days", RULE_REPEAT).appendQueryParameter("start", str).appendQueryParameter("end", str2).appendQueryParameter("exitAtAlarm", "false").build();
    }

    public static String getLocalZenRuleId(Context context) {
        return FBEUtil.getDefaultSharedPreferences(context).getString(KEY_ZEN_RULE_ID, "");
    }

    private static void setLocalZenRuleId(String str, Context context) {
        FBEUtil.getDefaultSharedPreferences(context).edit().putString(KEY_ZEN_RULE_ID, str).apply();
    }

    public static void setLocalZenRuleTime(Context context, int i, int i2, int i3, int i4) {
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
        defaultSharedPreferences.edit().putString(KEY_ZEN_RULE_START_TIME, i + "." + i2).apply();
        defaultSharedPreferences.edit().putString(KEY_ZEN_RULE_END_TIME, i3 + "." + i4).apply();
    }

    public static void resetZenRule(Context context) {
        FBEUtil.getDefaultSharedPreferences(context).edit().putString(KEY_ZEN_RULE_ID, "").putString(KEY_ZEN_RULE_START_TIME, "").putString(KEY_ZEN_RULE_END_TIME, "").apply();
    }

    private static Uri getLocalConditionId(Context context) {
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
        String string = defaultSharedPreferences.getString(KEY_ZEN_RULE_START_TIME, "");
        if ("".equals(string)) {
            string = BedtimeUtil.getSleepAlarmHour(context) + "." + BedtimeUtil.getSleepAlarmMin(context);
        }
        String string2 = defaultSharedPreferences.getString(KEY_ZEN_RULE_END_TIME, "");
        if ("".equals(string2)) {
            string2 = BedtimeUtil.getWakeAlarmHour(context) + "." + BedtimeUtil.getWakeAlarmMin(context);
        }
        return getConditionId(string, string2);
    }

    public static void clearZenMode(Context context) {
        if (FBEUtil.isUserUnlocked(context)) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            if (notificationManager.isNotificationPolicyAccessGranted()) {
                for (Map.Entry<String, AutomaticZenRule> entry : notificationManager.getAutomaticZenRules().entrySet()) {
                    AutomaticZenRule value = entry.getValue();
                    String key = entry.getKey();
                    if (value.getName().startsWith(RULE_NAME)) {
                        notificationManager.removeAutomaticZenRule(key);
                    }
                }
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:30:0x0037 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static boolean updateZenCheckPref(Context context, boolean z) {
        Class<?> cls;
        try {
            Class<?> cls2 = Class.forName(PKG_NAME_MIUI_SETTING);
            if (cls2 != null) {
                cls = null;
                for (Class<?> cls3 : cls2.getDeclaredClasses()) {
                    try {
                        if (cls3 != null && CLASS_NAME_FOR_ZENMODE.equals(cls3.getSimpleName())) {
                            cls = cls3;
                        }
                    } catch (Exception e) {
                        e = e;
                        Log.e("DC:updateZenCheckPref", "updateZenCheckPref failed", e);
                        if (cls != null) {
                            try {
                                cls.getMethod(METHOD_NAME_FOR_ZENMODE, Context.class, Boolean.TYPE, String.class).invoke(null, context, Boolean.valueOf(z), "set zenMode for deskclock");
                                Log.d("DC:updateZenCheckPref", "updateZenCheckPref success : " + z);
                                return true;
                            } catch (Exception e2) {
                                Log.e("DC:updateZenCheckPref", "updateZenCheckPref failed", e2);
                            }
                        }
                        return false;
                    }
                }
            } else {
                cls = null;
            }
        } catch (Exception e3) {
            e = e3;
            cls = null;
        }
        if (cls != null) {
            cls.getMethod(METHOD_NAME_FOR_ZENMODE, Context.class, Boolean.TYPE, String.class).invoke(null, context, Boolean.valueOf(z), "set zenMode for deskclock");
            Log.d("DC:updateZenCheckPref", "updateZenCheckPref success : " + z);
            return true;
        }
        return false;
    }
}
