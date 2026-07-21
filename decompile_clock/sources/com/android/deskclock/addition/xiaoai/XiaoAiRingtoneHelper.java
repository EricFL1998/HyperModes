package com.android.deskclock.addition.xiaoai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.ExtraRingtone;
import android.net.Uri;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiTheme;
import com.android.deskclock.alarm.AlarmClockFragment;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.AlarmUtils;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class XiaoAiRingtoneHelper {
    public static final String ACTION_HANDLE_NOT_SURE_ALERT_ACTION = "com.android.deskclock.HANDLE_NOT_SURE_ALERT";
    public static final String ALARM_HOUR = "alarm_hour";
    public static final String ALARM_MIN = "alarm_min";
    public static final String ALERT_MAX_DURATION = "alert_max_duration";
    public static final String ALERT_TIME = "alarm_time";
    private static final String BGM_NAME = "bgm";
    private static final String BGM_PATH = "/smartAlarm/bgm";
    private static final int CLOCK_TIME_INDEX = 3;
    private static final String CONTENT_URI = "content://com.miui.voiceassist.speech.api";
    public static final String IS_SNOOZE = "is_snooze";
    private static final int NEED_PLAY_NEWS_INDEX = 2;
    private static final String NEWS_NAME = "news";
    private static final String NEWS_PATH = "/smartAlarm/news";
    private static final String OFFLINE_BGM_NAME = "offline_bgm";
    private static final String OFFLINE_BGM_PATH = "/smartAlarm/offline/bgm";
    private static final String OFFLINE_TTS_NAME = "offline_tts";
    private static final String OFFLINE_TTS_PATH = "/smartAlarm/offline/tts";
    private static final int RES_UPDATE_TASK_RESULT_INDEX = 1;
    private static final int SMART_ALARM_OPEN_INDEX = 4;
    private static final String STATUS_PATH = "/smartAlarm/status";
    public static final String TAG = "DC:XiaoAiRingtone";
    private static final String TTS_NAME = "tts";
    private static final String TTS_PATH = "/smartAlarm/tts";
    private static final int UPDATE_TIME_INDEX = 0;
    public static final String UPDATE_XIAOAI_DATA = "com.miui.voiceassist.ACTION_SMART_ALARM_NOTIFY";
    public static final String XIAOAI_RINGTONE_IDS = "xiaoai_ringtone_ids";
    public static final String XIAO_AI_PKG_NAME = "com.miui.voiceassist";
    public static final String XIAO_AI_SET_TIME = "set_xiaoai_ids_time";
    private static final String RINGTONE_DIRECTORY = "xiaoai_ringtone";
    private static final String PRIVATE_RINGTONE_PATH = DeskClockApp.getAppDEContext().getFilesDir().getAbsolutePath() + File.separator + RINGTONE_DIRECTORY;
    private static String NEED_PLAY_NEWS = "need_play_news";
    private static String RINGTONE_ALERT_TIME = "smart_ringtone_alert_time";
    private static String PLAY_ONLINE_RES = "play_online_res";
    private static String NOT_SURE_ID = "not_sure_alert_id";
    public static Uri XIAO_AI_RINGTONE_URI_NEW = getRingtoneUri();
    private static Boolean IS_ENABLE = false;

    public static boolean isXiaoAiAlarm(Context context, int i) {
        Set<String> stringSet;
        if (!isAvailable() || (stringSet = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0).getStringSet(XIAOAI_RINGTONE_IDS, new HashSet())) == null) {
            return false;
        }
        Log.f(TAG, "isXiaoAiAlarm: " + stringSet.contains(String.valueOf(i)));
        return stringSet.contains(String.valueOf(i));
    }

    private static boolean isSupport() {
        return !Util.isInternational() && isEnable();
    }

    public static void resetEnableValue() {
        IS_ENABLE = Boolean.valueOf(!Util.isInternational() && isEnable());
    }

    public static boolean isAvailable() {
        Log.d(TAG, "isXiaoAiAvailable:" + IS_ENABLE);
        return IS_ENABLE.booleanValue();
    }

    private static boolean isEnable() {
        if (MiuiTheme.isThemeAppSupportSmartAlarm()) {
            return true;
        }
        boolean z = false;
        try {
            Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(Uri.parse("content://com.miui.voiceassist.speech.api/smartAlarm/status"), null, null, null, null);
            if (cursorQuery != null) {
                while (cursorQuery.moveToNext()) {
                    try {
                        z = Boolean.parseBoolean(cursorQuery.getString(4));
                    } catch (Throwable th) {
                        try {
                            cursorQuery.close();
                        } catch (Throwable th2) {
                            Log.e(TAG, "isEnable, cursor error" + th2);
                        }
                        throw th;
                    }
                }
                try {
                    cursorQuery.close();
                } catch (Throwable th3) {
                    Log.e(TAG, "isEnable, cursor error" + th3);
                }
            }
        } catch (Throwable th4) {
            Log.e(TAG, "isEnable, error" + th4);
        }
        return z;
    }

    public static void addXiaoAiRingtoneIds(Context context, int i) {
        Log.f(TAG, "addXiaoAiRingtoneIds id: " + i);
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
        Set<String> stringSet = sharedPreferences.getStringSet(XIAOAI_RINGTONE_IDS, new HashSet());
        if (stringSet == null) {
            stringSet = new HashSet<>();
        }
        stringSet.add(String.valueOf(i));
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putStringSet(XIAOAI_RINGTONE_IDS, stringSet);
        editorEdit.putLong(XIAO_AI_SET_TIME, System.currentTimeMillis());
        editorEdit.apply();
    }

    public static void removeXiaoAiRingtoneIds(Context context, int i) {
        Log.d(TAG, "removeXiaoAiRingtoneIds id: " + i);
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
        Set<String> stringSet = sharedPreferences.getStringSet(XIAOAI_RINGTONE_IDS, new HashSet());
        if (stringSet == null || !stringSet.contains(String.valueOf(i))) {
            return;
        }
        stringSet.remove(String.valueOf(i));
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putStringSet(XIAOAI_RINGTONE_IDS, stringSet);
        editorEdit.putLong(XIAO_AI_SET_TIME, System.currentTimeMillis());
        editorEdit.apply();
    }

    public static void clearXiaoAiRingtoneIds(Context context) {
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
        Set<String> stringSet = sharedPreferences.getStringSet(XIAOAI_RINGTONE_IDS, new HashSet());
        Log.d("clearXiaoAiRingtoneIds  snoozedIds : " + stringSet);
        if (stringSet != null) {
            stringSet.clear();
            SharedPreferences.Editor editorEdit = sharedPreferences.edit();
            editorEdit.putStringSet(XIAOAI_RINGTONE_IDS, stringSet);
            editorEdit.putLong(XIAO_AI_SET_TIME, System.currentTimeMillis());
            editorEdit.apply();
        }
    }

    public static void handleAlarmChange() {
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(DeskClockApp.getAppDEContext(), AlarmClockFragment.PREFERENCES, 0);
        Set<String> stringSet = sharedPreferences.getStringSet(XIAOAI_RINGTONE_IDS, new HashSet());
        Log.d(TAG, "change XiaoAiRingtoneIds, before: " + stringSet);
        Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(Alarm.Columns.CONTENT_URI, new String[]{"_id"}, null, null, null);
        HashSet hashSet = new HashSet();
        if (cursorQuery != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    do {
                        hashSet.add(String.valueOf(cursorQuery.getInt(0)));
                    } while (cursorQuery.moveToNext());
                }
                try {
                    cursorQuery.close();
                } catch (Exception unused) {
                }
            } catch (Throwable th) {
                try {
                    cursorQuery.close();
                } catch (Exception unused2) {
                }
                throw th;
            }
        }
        if (stringSet != null && stringSet.size() > 0) {
            if (hashSet.size() > 0) {
                HashSet hashSet2 = new HashSet();
                for (String str : stringSet) {
                    if (!hashSet.contains(str)) {
                        hashSet2.add(str);
                    }
                }
                stringSet.removeAll(hashSet2);
            } else {
                stringSet.clear();
            }
        }
        Log.d(TAG, "change XiaoAiRingtoneIds, after: " + stringSet);
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putStringSet(XIAOAI_RINGTONE_IDS, stringSet);
        editorEdit.putLong(XIAO_AI_SET_TIME, System.currentTimeMillis());
        editorEdit.apply();
    }

    public static String getXiaoAiRingtonePath() {
        return getRingtoneUri().toString();
    }

    public static Uri getRingtoneUri() {
        return new Uri.Builder().scheme("android.resource").authority(DeskClockApp.getAppContext().getPackageName()).path(String.valueOf(R.raw.xiaoai_ringtone_preview)).build();
    }

    public static boolean isXiaoAiRingtone(Uri uri) {
        Log.d("DC:SetAlarmRingtone", "getRingtoneUri(): " + getRingtoneUri() + "  XIAO_AI_RINGTONE_URI_NEW: " + XIAO_AI_RINGTONE_URI_NEW + " alert: " + uri);
        return getRingtoneUri().equals(uri) || XIAO_AI_RINGTONE_URI_NEW.equals(uri);
    }

    public static boolean isXiaoAiRingtoneByTitle(Context context) {
        String ringtoneTitle = ExtraRingtone.getRingtoneTitle(context, AlarmRingtoneUtil.getDefaultAlarmRingtone(), true);
        Log.d("DC:SetAlarmRingtone", " ringtoneTitle :" + ringtoneTitle);
        return ringtoneTitle.equals(context.getResources().getString(R.string.xiaoai_ringtone_title));
    }

    public static void sendBroadCastForUpdate(Context context, Alarm alarm) {
        Log.f(TAG, "sendBroadCastForUpdate: " + alarm.toString());
        Intent intent = new Intent(UPDATE_XIAOAI_DATA);
        intent.putExtra("alarm_time", alarm.time);
        intent.putExtra(IS_SNOOZE, AlarmHelper.hasAlarmBeenSnoozed(FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0), alarm.id));
        intent.putExtra(ALARM_HOUR, alarm.hour);
        intent.putExtra(ALARM_MIN, alarm.minutes);
        intent.putExtra(ALERT_MAX_DURATION, Integer.valueOf(FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppContext()).getString("auto_silence", "10")));
        intent.setPackage("com.miui.voiceassist");
        context.sendBroadcast(intent);
    }

    public static void loadAlertUri(final Alarm alarm) {
        Log.f(TAG, "loadAlertUri ");
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper.1
            @Override // java.lang.Runnable
            public void run() {
                XiaoAiRingtoneHelper.resetEnableValue();
                if (XiaoAiRingtoneHelper.isXiaoAiAlarm(DeskClockApp.getAppDEContext(), alarm.id)) {
                    XiaoAiRingtoneHelper.checkAndLoadRingtoneRes(alarm);
                    XiaoAiRingtoneHelper.loadOffLineRes();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkAndLoadRingtoneRes(Alarm alarm) {
        Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(Uri.parse("content://com.miui.voiceassist.speech.api/smartAlarm/status"), null, null, null, null);
        long j = 0;
        long j2 = 0;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        if (cursorQuery != null) {
            while (cursorQuery.moveToNext()) {
                try {
                    j = Long.parseLong(cursorQuery.getString(0));
                    z = Boolean.parseBoolean(cursorQuery.getString(1));
                    z2 = Boolean.parseBoolean(cursorQuery.getString(2));
                    j2 = Long.parseLong(cursorQuery.getString(3));
                    z3 = Boolean.parseBoolean(cursorQuery.getString(4));
                } catch (Throwable th) {
                    try {
                        cursorQuery.close();
                    } catch (Throwable th2) {
                        Log.e(TAG, "checkAndLoadRingtoneRes, cursor error" + th2);
                    }
                    throw th;
                }
            }
            try {
                cursorQuery.close();
            } catch (Throwable th3) {
                Log.e(TAG, "checkAndLoadRingtoneRes, cursor error" + th3);
            }
        }
        Log.f(TAG, "updateTime: " + Util.formatTimeForLog(j) + ", resUpdateTaskResult: " + z + ", needPlayNews: " + z2 + ",clockTime:" + j2 + ",smartAlarmOpen:" + z3);
        if ((MiuiTheme.isThemeAppSupportSmartAlarm() || z3) && j2 == alarm.time) {
            setAlertTime(j2);
            if (z) {
                saveOnLineRingtoneRes(z2);
            } else {
                setNeedPlayOnlineRes(false);
                setNeedPlayNews(false);
            }
        }
    }

    private static boolean saveOnLineRingtoneRes(boolean z) {
        boolean z2 = saveResToLocal(TTS_PATH, TTS_NAME) && saveResToLocal(BGM_PATH, BGM_NAME);
        setNeedPlayOnlineRes(z2);
        if (z2) {
            setNeedPlayNews(z && saveResToLocal(NEWS_PATH, "news"));
        } else {
            setNeedPlayNews(false);
        }
        return z2;
    }

    public static boolean loadResOnAlert() {
        setNeedPlayNews(false);
        setAlertTime(0L);
        setNeedPlayOnlineRes(false);
        return loadOffLineRes();
    }

    public static void setAlertTime(long j) {
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putLong(RINGTONE_ALERT_TIME, j).apply();
    }

    public static long getAlertTime() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getLong(RINGTONE_ALERT_TIME, 0L);
    }

    public static void setNeedPlayNews(boolean z) {
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putBoolean(NEED_PLAY_NEWS, z).apply();
    }

    public static boolean getNeedPlayNews() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getBoolean(NEED_PLAY_NEWS, false);
    }

    public static void setNeedPlayOnlineRes(boolean z) {
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putBoolean(PLAY_ONLINE_RES, z).apply();
    }

    public static boolean getNeedPlayOnlineRes() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getBoolean(PLAY_ONLINE_RES, false);
    }

    public static boolean isOnLineRes() {
        return getNeedPlayOnlineRes();
    }

    private static boolean saveResToLocal(String str, String str2) {
        boolean zCopyFile = false;
        try {
            Uri uri = Uri.parse(CONTENT_URI + str);
            String str3 = PRIVATE_RINGTONE_PATH;
            File file = new File(str3);
            if (file.exists() || (!file.exists() && file.mkdirs())) {
                zCopyFile = copyFile(uri, new File(str3 + File.separator + str2));
            }
        } catch (Throwable th) {
            Log.e(TAG, "saveResToLocal, name:" + str2 + ", error: " + th);
        }
        Log.d(TAG, "saveResToLocal, name:" + str2 + ", result: " + zCopyFile);
        return zCopyFile;
    }

    /* JADX WARN: Code duplicated, block: B:69:0x008c A[DONT_GENERATE, EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:77:0x0087 A[DONT_GENERATE, EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:83:0x0082 A[DONT_GENERATE, EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:89:0x007d A[DONT_GENERATE, EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:99:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    private static boolean copyFile(Uri uri, File file) {
        BufferedInputStream bufferedInputStream;
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        Object th;
        BufferedOutputStream bufferedOutputStream;
        try {
            fileInputStream = new FileInputStream(DeskClockApp.getAppDEContext().getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());
            try {
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                try {
                    fileOutputStream = new FileOutputStream(file);
                    try {
                        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        try {
                            byte[] bArr = new byte[1024];
                            while (true) {
                                int i = bufferedInputStream.read(bArr);
                                if (i != -1) {
                                    bufferedOutputStream.write(bArr, 0, i);
                                } else {
                                    try {
                                        break;
                                    } catch (IOException unused) {
                                    }
                                }
                            }
                            bufferedInputStream.close();
                            try {
                                bufferedOutputStream.flush();
                            } catch (IOException unused2) {
                            }
                            try {
                                fileOutputStream.close();
                            } catch (IOException unused3) {
                            }
                            try {
                                fileInputStream.close();
                            } catch (IOException unused4) {
                            }
                            return true;
                        } catch (Throwable th2) {
                            th = th2;
                            try {
                                Log.e(TAG, "Failed to copy file: " + uri + ", error:" + th);
                                if (fileInputStream == null) {
                                    return false;
                                }
                                try {
                                    return false;
                                } catch (IOException unused5) {
                                    return false;
                                }
                            } finally {
                                if (bufferedInputStream != null) {
                                    try {
                                        bufferedInputStream.close();
                                    } catch (IOException unused6) {
                                    }
                                }
                                if (bufferedOutputStream != 0) {
                                    try {
                                        bufferedOutputStream.flush();
                                    } catch (IOException unused7) {
                                    }
                                }
                                if (fileOutputStream != null) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (IOException unused8) {
                                    }
                                }
                                if (fileInputStream != null) {
                                    try {
                                        fileInputStream.close();
                                    } catch (IOException unused9) {
                                    }
                                }
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        bufferedOutputStream = 0;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    fileOutputStream = null;
                    th = th;
                    bufferedOutputStream = fileOutputStream;
                    Log.e(TAG, "Failed to copy file: " + uri + ", error:" + th);
                    if (fileInputStream == null) {
                        return false;
                    }
                    return false;
                }
            } catch (Throwable th5) {
                th = th5;
                bufferedInputStream = null;
                fileOutputStream = null;
            }
        } catch (Throwable th6) {
            th = th6;
            bufferedInputStream = null;
            fileInputStream = null;
            fileOutputStream = null;
        }
    }

    public static Uri getTts() {
        File file = new File(PRIVATE_RINGTONE_PATH + File.separator + TTS_NAME);
        if (file.exists()) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static Uri getBgm() {
        File file = new File(PRIVATE_RINGTONE_PATH + File.separator + BGM_NAME);
        if (file.exists()) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static Uri getNews() {
        File file = new File(PRIVATE_RINGTONE_PATH + File.separator + "news");
        if (file.exists()) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static Uri getOfflineTts() {
        File file = new File(PRIVATE_RINGTONE_PATH + File.separator + OFFLINE_TTS_NAME);
        if (file.exists()) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static Uri getOfflineBgm() {
        File file = new File(PRIVATE_RINGTONE_PATH + File.separator + OFFLINE_BGM_NAME);
        if (file.exists()) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static boolean isOffLineResExist() {
        StringBuilder sb = new StringBuilder();
        String str = PRIVATE_RINGTONE_PATH;
        return new File(sb.append(str).append(File.separator).append(OFFLINE_TTS_NAME).toString()).exists() && new File(new StringBuilder().append(str).append(File.separator).append(OFFLINE_BGM_NAME).toString()).exists();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean loadOffLineRes() {
        if (isOffLineResExist()) {
            return true;
        }
        return saveResToLocal(OFFLINE_TTS_PATH, OFFLINE_TTS_NAME) && saveResToLocal(OFFLINE_BGM_PATH, OFFLINE_BGM_NAME);
    }

    public static void preHandleNotSureAlarm(int i) {
        Log.d(TAG, "preHandleNotSureAlarm id：" + i);
        String alarmRingtoneTitle = AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), AlarmRingtoneUtil.getDefaultAlarmRingtone());
        if (alarmRingtoneTitle.equals(DeskClockApp.getAppContext().getString(R.string.xiaoai_ringtone_title))) {
            Log.d(TAG, "user ringtone is not xiaoai: " + alarmRingtoneTitle);
            return;
        }
        Context appDEContext = DeskClockApp.getAppDEContext();
        FBEUtil.getDefaultSharedPreferences(appDEContext).edit().putInt(NOT_SURE_ID, i).apply();
        Alarm alarm = AlarmHelper.getAlarm(appDEContext.getContentResolver(), i);
        if (alarm.time == 0) {
            alarm.time = AlarmHelper.calculateAlarmTime(appDEContext, alarm);
        }
        Intent intent = new Intent(ACTION_HANDLE_NOT_SURE_ALERT_ACTION);
        intent.setPackage(appDEContext.getPackageName());
        if (alarm.time - 300000 > System.currentTimeMillis()) {
            AlarmUtils.setAlarm(appDEContext, alarm.time - 300000, intent);
        }
    }

    public static boolean handleNotSureAlarm() {
        Context appDEContext = DeskClockApp.getAppDEContext();
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(appDEContext);
        int i = defaultSharedPreferences.getInt(NOT_SURE_ID, -1);
        boolean z = false;
        if (i == -1) {
            return false;
        }
        if (isAvailable()) {
            addXiaoAiRingtoneIds(appDEContext, i);
            AlarmHelper.setNextAlert(appDEContext);
            z = true;
        }
        defaultSharedPreferences.edit().putInt(NOT_SURE_ID, -1).apply();
        return z;
    }

    public static String getBackupIds(Context context) {
        Set<String> stringSet;
        resetEnableValue();
        if (isAvailable() && (stringSet = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0).getStringSet(XIAOAI_RINGTONE_IDS, null)) != null) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = stringSet.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
                sb.append("-");
            }
            return sb.toString();
        }
        return "";
    }

    public static void restoreToPhone(Context context, String str) {
        resetEnableValue();
        if (isAvailable()) {
            SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
            if (str != null) {
                String[] strArrSplit = str.split("-");
                HashSet hashSet = new HashSet();
                for (String str2 : strArrSplit) {
                    hashSet.add(str2);
                }
                sharedPreferences.edit().putStringSet(XIAOAI_RINGTONE_IDS, hashSet).putLong(XIAO_AI_SET_TIME, System.currentTimeMillis()).apply();
                Log.d(TAG, "restoreToPhone, idsSet: " + hashSet);
            }
        }
    }
}
