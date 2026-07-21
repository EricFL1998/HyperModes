package com.android.deskclock.util.themeringtone;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.ringtone.RingtoneConstants;
import com.android.deskclock.addition.ringtone.RingtoneUriCompat;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.settings.AlarmRingtonePickerActivity;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class RingtoneHelper {
    private static final String NEW_THEME_RINGTONE_FILE_PROVIDER = "content://com.android.thememanager.fileprovider";
    private static final String NEW_THEME_RINGTONE_PATH = "/storage/emulated/0/Android/data/com.android.thememanager/files/MIUI/.ringtone";
    public static final String NEW_THEME_RINGTONE_URI = "file:///storage/emulated/0/Android/data/com.android.thememanager/files/MIUI/.ringtone";
    private static final String OLD_THEME_RINGTONE_PATH = "/storage/emulated/0/MIUI/.ringtone";
    private static final String OLD_THEME_RINGTONE_URI = "file:///storage/emulated/0/MIUI/.ringtone";
    public static final String PRIVATE_RINGTONE_PATH;
    public static final String PRIVATE_RINGTONE_URI;
    public static final String RINGTONE_DIRECTORY = "theme_ringtone";
    private static final String TAG = "DC:RingtoneHelper";

    static {
        String str = DeskClockApp.getAppDEContext().getFilesDir().getAbsolutePath() + File.separator + RINGTONE_DIRECTORY;
        PRIVATE_RINGTONE_PATH = str;
        PRIVATE_RINGTONE_URI = Uri.fromFile(new File(str)).toString();
    }

    public static void handleAlert(final Alarm alarm) {
        if (alarm.alert == null || Build.VERSION.SDK_INT < 29) {
            return;
        }
        String string = alarm.alert.toString();
        final String urlStartStr = getUrlStartStr(string);
        Log.d(TAG, "handleAlert: " + string + " urlStartStr: " + urlStartStr);
        if (urlStartStr != null) {
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.util.themeringtone.RingtoneHelper.1
                @Override // java.lang.Runnable
                public void run() {
                    RingtoneHelper.changeAndSaveAlert(alarm.id, urlStartStr);
                }
            });
        }
    }

    private static String getUrlStartStr(String str) {
        if (str.startsWith(OLD_THEME_RINGTONE_URI)) {
            return OLD_THEME_RINGTONE_URI;
        }
        if (str.startsWith(NEW_THEME_RINGTONE_FILE_PROVIDER)) {
            return NEW_THEME_RINGTONE_FILE_PROVIDER;
        }
        return null;
    }

    public static void handleWakeAlert(final Uri uri) {
        final String urlStartStr;
        if (uri == null || Build.VERSION.SDK_INT < 29 || (urlStartStr = getUrlStartStr(uri.toString())) == null) {
            return;
        }
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.util.themeringtone.RingtoneHelper.2
            @Override // java.lang.Runnable
            public void run() {
                RingtoneHelper.changeAndSaveWakeAlert(uri, urlStartStr);
            }
        });
    }

    public static void handleTimerAlert(final Uri uri) {
        final String urlStartStr;
        Log.d(TAG, "handleTimerAlert, alert: " + uri);
        if (uri == null || Build.VERSION.SDK_INT < 29 || (urlStartStr = getUrlStartStr(uri.toString())) == null) {
            return;
        }
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.util.themeringtone.RingtoneHelper.3
            @Override // java.lang.Runnable
            public void run() {
                RingtoneHelper.changeAndSaveTimerAlert(uri, urlStartStr);
            }
        });
    }

    public static void handleDefaultRingtone() {
        try {
            Uri defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
            Log.d(TAG, "handleDefaultRingtone: " + defaultAlarmRingtone);
            if (defaultAlarmRingtone == null || !defaultAlarmRingtone.toString().startsWith(PRIVATE_RINGTONE_URI) || new File(new URI(defaultAlarmRingtone.toString())).exists()) {
                return;
            }
            Log.d(TAG, "default ringtone did not exist ");
            AlarmRingtoneUtil.setDefaultAlarmRingtone(WeatherRingtoneHelper.getWeatherRingtoneUri());
        } catch (Exception e) {
            Log.e(TAG, "handleDefaultRingtone error, " + e);
        }
    }

    public static void handleRingtonePickerAlert(Context context) {
        if (Build.VERSION.SDK_INT < 29) {
            return;
        }
        final String string = FBEUtil.getDefaultSharedPreferences(context).getString(AlarmRingtonePickerActivity.KEY_LAST_OTHER_RINGTONE, AlarmRingtonePickerActivity.VALUE_NO_RECORD);
        final String urlStartStr = getUrlStartStr(string);
        Log.d(TAG, "handleRingtonePickerAlert: " + string);
        if (urlStartStr != null) {
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.util.themeringtone.RingtoneHelper.4
                @Override // java.lang.Runnable
                public void run() {
                    RingtoneHelper.changeAndSaveTempAlert(Uri.parse(string), urlStartStr);
                }
            });
        }
    }

    public static synchronized void changeAndSaveTempAlert(Uri uri, String str) {
        if (uri != null) {
            if (uri.toString().startsWith(str)) {
                Uri uriAddRingtone = addRingtone(uri);
                Log.d(TAG, "changeAndSaveTempAlert, result: " + uriAddRingtone);
                if (uriAddRingtone != null) {
                    SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext());
                    Log.d(TAG, "changeAndSaveTempAlert---------: " + uriAddRingtone.toString());
                    defaultSharedPreferences.edit().putString(AlarmRingtonePickerActivity.KEY_LAST_OTHER_RINGTONE, uriAddRingtone.toString()).apply();
                }
            }
        }
    }

    public static synchronized void changeAndSaveAlert(int i, String str) {
        Alarm alarm = AlarmHelper.getAlarm(DeskClockApp.getAppContext().getContentResolver(), i);
        if (alarm == null) {
            return;
        }
        Uri uri = alarm.alert;
        Log.d(TAG, "changeAndSaveAlert, alert: " + uri + " id: " + i);
        if (uri != null && uri.toString().startsWith(str)) {
            Uri uriAddRingtone = addRingtone(uri);
            Log.d(TAG, "addRingtone result: " + uriAddRingtone);
            if (uriAddRingtone != null && str.equals(NEW_THEME_RINGTONE_FILE_PROVIDER)) {
                String fileNameFromUri = getFileNameFromUri(DeskClockApp.getAppDEContext(), uri);
                if (fileNameFromUri == null) {
                    return;
                }
                Uri uriFromFile = Uri.fromFile(new File(OLD_THEME_RINGTONE_PATH + File.separator + fileNameFromUri));
                Log.d(TAG, "changeAndSaveAlert, new uri: " + uriFromFile);
                if (alarm.id == Integer.MIN_VALUE) {
                    FBEUtil.getSharedPreferences(DeskClockApp.getAppContext(), "BedtimeAlarm", 0).edit().putString(BedtimeUtil.SP_WAKE_ALARM_ALERT, uriFromFile.toString()).apply();
                } else {
                    alarm.alert = uriFromFile;
                    AlarmHelper.setAlarm(DeskClockApp.getAppContext(), alarm);
                }
            }
        }
    }

    public static synchronized void changeAndSaveWakeAlert(Uri uri, String str) {
        if (uri != null) {
            if (uri.toString().startsWith(str)) {
                Uri uriAddRingtone = addRingtone(uri);
                Log.d(TAG, "changeAndSaveWakeAlert, result:" + uriAddRingtone);
                if (uriAddRingtone != null && str.equals(NEW_THEME_RINGTONE_FILE_PROVIDER)) {
                    String fileNameFromUri = getFileNameFromUri(DeskClockApp.getAppDEContext(), uri);
                    if (fileNameFromUri == null) {
                        return;
                    }
                    Uri uriFromFile = Uri.fromFile(new File(OLD_THEME_RINGTONE_PATH + File.separator + fileNameFromUri));
                    Log.d(TAG, "changeAndSaveWakeAlert, new uri: " + uriFromFile);
                    FBEUtil.getSharedPreferences(DeskClockApp.getAppContext(), "BedtimeAlarm", 0).edit().putString(BedtimeUtil.SP_WAKE_ALARM_ALERT, uriFromFile.toString()).apply();
                }
            }
        }
    }

    public static synchronized void changeAndSaveTimerAlert(Uri uri, String str) {
        if (uri != null) {
            if (uri.toString().startsWith(str)) {
                Uri uriAddRingtone = addRingtone(uri);
                Log.d(TAG, "changeAndSaveTimerAlert, result:" + uriAddRingtone);
                if (uriAddRingtone != null) {
                    TimerDao.setTimerRingtone(uriAddRingtone);
                }
            }
        }
    }

    public static Uri addRingtone(Uri uri) throws Throwable {
        try {
            Log.d(TAG, "addRingtone: " + uri);
            if (uri.toString().startsWith(OLD_THEME_RINGTONE_URI)) {
                ThemeProviderHelper.GrantThemeResult grantThemeResultRequestGrantThemeFiles = ThemeProviderHelper.requestGrantThemeFiles(DeskClockApp.getAppDEContext(), ".ringtone/" + getFileNameFromUri(DeskClockApp.getAppDEContext(), uri), null);
                if (grantThemeResultRequestGrantThemeFiles != null && grantThemeResultRequestGrantThemeFiles.uri != null) {
                    uri = grantThemeResultRequestGrantThemeFiles.uri;
                }
            }
            String str = PRIVATE_RINGTONE_PATH;
            File file = new File(str);
            if (!file.exists()) {
                file.mkdirs();
            }
            File file2 = new File(str + File.separator + getFileNameFromUri(DeskClockApp.getAppContext(), uri));
            if (file2.exists()) {
                Log.d(TAG, "targetFile exists");
                return Uri.fromFile(file2);
            }
            if (copyFile(uri, file2)) {
                return Uri.fromFile(file2);
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "addRingtone error: " + e);
        }
    }

    private static String getFileNameFromUri(Context context, Uri uri) {
        Cursor cursorQuery;
        String string = uri.toString();
        if (string.startsWith(OLD_THEME_RINGTONE_URI) || string.startsWith(PRIVATE_RINGTONE_URI)) {
            try {
                return new File(new URI(string)).getName();
            } catch (Exception e) {
                Log.e(TAG, " getFileNameFromUri error: " + e);
                e.printStackTrace();
            }
        } else if (string.startsWith(NEW_THEME_RINGTONE_FILE_PROVIDER)) {
            try {
                cursorQuery = context.getContentResolver().query(uri, null, null, null, null);
            } catch (Exception e2) {
                Log.e(TAG, " getFileNameFromUri error: " + e2);
                cursorQuery = null;
            }
            try {
                if (cursorQuery != null) {
                    try {
                        int columnIndex = cursorQuery.getColumnIndex("_display_name");
                        cursorQuery.moveToFirst();
                        String string2 = cursorQuery.getString(columnIndex);
                        cursorQuery.close();
                        return string2;
                    } catch (Exception e3) {
                        Log.e(TAG, " getFileNameFromUri error: " + e3);
                        cursorQuery.close();
                    }
                }
            } catch (Throwable th) {
                cursorQuery.close();
                throw th;
            }
        } else {
            try {
                return new File(new URI(string)).getName();
            } catch (Exception e4) {
                Log.e(TAG, " getFileNameFromUri error: " + e4);
                e4.printStackTrace();
            }
        }
        Log.e(TAG, "getFileNameFromUri is null!  alert: " + uri);
        return null;
    }

    /* JADX WARN: Code duplicated, block: B:104:0x0115 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:106:0x0110 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:110:0x010b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:116:0x011a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:131:? A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:92:0x0106 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static boolean copyFile(Uri uri, File file) throws Throwable {
        BufferedOutputStream bufferedOutputStream;
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        Log.d(TAG, "copyFile: " + uri + " targetFile: " + file);
        BufferedInputStream bufferedInputStream = null;
        try {
            if (uri.toString().startsWith(OLD_THEME_RINGTONE_URI)) {
                fileInputStream = new FileInputStream(new File(new URI(uri.toString())));
            } else {
                if (!uri.toString().startsWith(NEW_THEME_RINGTONE_FILE_PROVIDER)) {
                    return false;
                }
                fileInputStream = new FileInputStream(DeskClockApp.getAppDEContext().getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());
            }
            try {
                BufferedInputStream bufferedInputStream2 = new BufferedInputStream(fileInputStream);
                try {
                    fileOutputStream = new FileOutputStream(file);
                    try {
                        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        try {
                            byte[] bArr = new byte[1024];
                            while (true) {
                                int i = bufferedInputStream2.read(bArr);
                                if (i != -1) {
                                    bufferedOutputStream.write(bArr, 0, i);
                                } else {
                                    try {
                                        break;
                                    } catch (IOException unused) {
                                    }
                                }
                            }
                            bufferedInputStream2.close();
                            try {
                                bufferedOutputStream.flush();
                            } catch (IOException unused2) {
                            }
                            try {
                                bufferedOutputStream.close();
                            } catch (IOException unused3) {
                            }
                            try {
                                fileOutputStream.close();
                            } catch (IOException unused4) {
                            }
                            try {
                                fileInputStream.close();
                            } catch (IOException unused5) {
                            }
                            return true;
                        } catch (Exception e) {
                            e = e;
                            bufferedInputStream = bufferedInputStream2;
                            try {
                                Log.e(TAG, "Failed to copy file ," + e);
                                if (bufferedInputStream != null) {
                                    try {
                                        bufferedInputStream.close();
                                    } catch (IOException unused6) {
                                    }
                                }
                                if (bufferedOutputStream != null) {
                                    try {
                                        bufferedOutputStream.flush();
                                    } catch (IOException unused7) {
                                    }
                                }
                                if (bufferedOutputStream != null) {
                                    try {
                                        bufferedOutputStream.close();
                                    } catch (IOException unused8) {
                                    }
                                }
                                if (fileOutputStream != null) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (IOException unused9) {
                                    }
                                }
                                if (fileInputStream == null) {
                                    return false;
                                }
                                try {
                                    fileInputStream.close();
                                    return false;
                                } catch (IOException unused10) {
                                    return false;
                                }
                            } catch (Throwable th) {
                                th = th;
                                if (bufferedInputStream != null) {
                                    try {
                                        bufferedInputStream.close();
                                    } catch (IOException unused11) {
                                    }
                                }
                                if (bufferedOutputStream != null) {
                                    try {
                                        bufferedOutputStream.flush();
                                    } catch (IOException unused12) {
                                    }
                                }
                                if (bufferedOutputStream != null) {
                                    try {
                                        bufferedOutputStream.close();
                                    } catch (IOException unused13) {
                                    }
                                }
                                if (fileOutputStream != null) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (IOException unused14) {
                                    }
                                }
                                if (fileInputStream != null) {
                                    try {
                                        fileInputStream.close();
                                        throw th;
                                    } catch (IOException unused15) {
                                        throw th;
                                    }
                                }
                                throw th;
                            }
                        } catch (Throwable th2) {
                            bufferedInputStream = bufferedInputStream2;
                            fileInputStream = fileInputStream;
                            th = th2;
                            if (bufferedInputStream != null) {
                                bufferedInputStream.close();
                            }
                            if (bufferedOutputStream != null) {
                                bufferedOutputStream.flush();
                            }
                            if (bufferedOutputStream != null) {
                                bufferedOutputStream.close();
                            }
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                            if (fileInputStream != null) {
                                fileInputStream.close();
                                throw th;
                            }
                            throw th;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        bufferedOutputStream = null;
                    } catch (Throwable th3) {
                        fileInputStream = fileInputStream;
                        th = th3;
                        bufferedOutputStream = null;
                        bufferedInputStream = bufferedInputStream2;
                    }
                } catch (Exception e3) {
                    fileOutputStream = null;
                    bufferedInputStream = bufferedInputStream2;
                    fileInputStream = fileInputStream;
                    e = e3;
                    bufferedOutputStream = null;
                } catch (Throwable th4) {
                    fileOutputStream = null;
                    bufferedInputStream = bufferedInputStream2;
                    fileInputStream = fileInputStream;
                    th = th4;
                    bufferedOutputStream = null;
                }
            } catch (Exception e4) {
                fileInputStream = fileInputStream;
                e = e4;
                bufferedOutputStream = null;
                fileOutputStream = null;
            } catch (Throwable th5) {
                fileInputStream = fileInputStream;
                th = th5;
                bufferedOutputStream = null;
                fileOutputStream = null;
            }
        } catch (Exception e5) {
            e = e5;
            bufferedOutputStream = null;
            fileInputStream = null;
            fileOutputStream = null;
        } catch (Throwable th6) {
            th = th6;
            bufferedOutputStream = null;
            fileInputStream = null;
            fileOutputStream = null;
        }
    }

    public static synchronized void updateRingtone() {
        Log.f(TAG, "updateRingtone start");
        File file = new File(PRIVATE_RINGTONE_PATH);
        if (file.exists()) {
            HashSet<String> allUsedRingtone = getAllUsedRingtone();
            Log.f(TAG, "updateRingtone ringtoneSet: " + allUsedRingtone);
            File[] fileArrListFiles = file.listFiles();
            if (fileArrListFiles == null) {
                return;
            }
            for (File file2 : fileArrListFiles) {
                if (file2.isFile()) {
                    String name = file2.getName();
                    if (allUsedRingtone.contains(name)) {
                        continue;
                    } else {
                        Log.f(TAG, "updateRingtone delete: " + name);
                        file2.delete();
                    }
                }
            }
        }
    }

    private static HashSet<String> getAllUsedRingtone() {
        String string;
        String fileNameFromUri;
        String fileNameFromUri2;
        Context appDEContext = DeskClockApp.getAppDEContext();
        HashSet<String> hashSet = new HashSet<>();
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = appDEContext.getContentResolver().query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, Alarm.Columns.DEFAULT_SORT_ORDER);
                if (cursorQuery.moveToFirst()) {
                    do {
                        Uri uri = new Alarm(cursorQuery).alert;
                        if (uri != null && uri.toString().startsWith(OLD_THEME_RINGTONE_URI) && (fileNameFromUri2 = getFileNameFromUri(DeskClockApp.getAppDEContext(), uri)) != null) {
                            hashSet.add(fileNameFromUri2);
                        }
                    } while (cursorQuery.moveToNext());
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "getAllUsedRingtone error,  " + e);
                if (cursorQuery != null) {
                }
            }
            if (BedtimeUtil.isWakeAlarmSupport(appDEContext) && (string = FBEUtil.getSharedPreferences(DeskClockApp.getAppContext(), "BedtimeAlarm", 0).getString(BedtimeUtil.SP_WAKE_ALARM_ALERT, "")) != null) {
                try {
                    if (string.startsWith(OLD_THEME_RINGTONE_URI) && (fileNameFromUri = getFileNameFromUri(DeskClockApp.getAppDEContext(), Uri.parse(string))) != null) {
                        hashSet.add(fileNameFromUri);
                    }
                } catch (Exception e2) {
                    Log.e(TAG, "getAllUsedRingtone error,  " + e2);
                }
            }
            Uri timerRingtone = TimerDao.getTimerRingtone();
            if (timerRingtone != null && timerRingtone.toString().startsWith(PRIVATE_RINGTONE_URI)) {
                try {
                    String fileNameFromUri3 = getFileNameFromUri(DeskClockApp.getAppDEContext(), timerRingtone);
                    if (fileNameFromUri3 != null) {
                        hashSet.add(fileNameFromUri3);
                    }
                } catch (Exception e3) {
                    Log.e(TAG, "getAllUsedRingtone error,  " + e3);
                }
            }
            String string2 = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getString(AlarmRingtonePickerActivity.KEY_LAST_OTHER_RINGTONE, AlarmRingtonePickerActivity.VALUE_NO_RECORD);
            if (string2 != null && string2.startsWith(PRIVATE_RINGTONE_URI)) {
                try {
                    String fileNameFromUri4 = getFileNameFromUri(DeskClockApp.getAppDEContext(), Uri.parse(string2));
                    if (fileNameFromUri4 != null) {
                        hashSet.add(fileNameFromUri4);
                    }
                } catch (Exception e4) {
                    Log.e(TAG, "getAllUsedRingtone error,  " + e4);
                }
            }
            Uri defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
            if (defaultAlarmRingtone != null && defaultAlarmRingtone.toString().startsWith(PRIVATE_RINGTONE_URI)) {
                try {
                    String fileNameFromUri5 = getFileNameFromUri(DeskClockApp.getAppDEContext(), defaultAlarmRingtone);
                    if (fileNameFromUri5 != null) {
                        hashSet.add(fileNameFromUri5);
                    }
                } catch (Exception e5) {
                    Log.e(TAG, "getAllUsedRingtone error,  " + e5);
                }
            }
            return hashSet;
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    public static Uri handleUriForTheme(Uri uri) {
        String fileNameFromUri;
        Log.d(TAG, "handleUriForTheme: " + uri);
        if (uri == null) {
            return null;
        }
        String string = uri.toString();
        if ((!string.startsWith(PRIVATE_RINGTONE_URI) && !string.startsWith(OLD_THEME_RINGTONE_URI) && !string.startsWith(NEW_THEME_RINGTONE_FILE_PROVIDER)) || (fileNameFromUri = getFileNameFromUri(DeskClockApp.getAppDEContext(), uri)) == null) {
            return uri;
        }
        if (Build.VERSION.SDK_INT <= 29) {
            return Uri.fromFile(new File(OLD_THEME_RINGTONE_PATH + File.separator + fileNameFromUri));
        }
        if (ThemeProviderHelper.requestGrantThemeFiles(DeskClockApp.getAppDEContext(), ".ringtone/" + fileNameFromUri, null) == null) {
            return Uri.fromFile(new File(OLD_THEME_RINGTONE_PATH + File.separator + fileNameFromUri));
        }
        return Uri.fromFile(new File(NEW_THEME_RINGTONE_PATH + File.separator + fileNameFromUri));
    }

    public static Uri getDataRingtoneUri(Uri uri) {
        Uri uriFromFile = null;
        if (uri.toString().startsWith(OLD_THEME_RINGTONE_URI)) {
            try {
                File file = new File(PRIVATE_RINGTONE_PATH + File.separator + getFileNameFromUri(DeskClockApp.getAppDEContext(), uri));
                if (file.exists()) {
                    uriFromFile = Uri.fromFile(file);
                }
            } catch (Exception e) {
                Log.e(TAG, "getDataRingtoneUri error,  " + e);
            }
        }
        Log.d(TAG, "getDataRingtoneUri: " + uri + ", newUri: " + uriFromFile);
        return uriFromFile;
    }

    public static void showAllUsedRingtone() {
        Context appDEContext = DeskClockApp.getAppDEContext();
        new HashSet();
        Cursor cursorQuery = appDEContext.getContentResolver().query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, Alarm.Columns.DEFAULT_SORT_ORDER);
        try {
            try {
                if (cursorQuery.moveToFirst()) {
                    do {
                        Alarm alarm = new Alarm(cursorQuery);
                        Log.d(TAG, "normal alarm ringtone, id: " + alarm.id + " " + alarm.alert);
                    } while (cursorQuery.moveToNext());
                }
            } catch (Exception e) {
                Log.e(TAG, "getAllUsedRingtone error,  " + e);
            }
            cursorQuery.close();
            if (BedtimeUtil.isWakeAlarmSupport(appDEContext)) {
                Log.d(TAG, "wake alarm ringtone, id: " + FBEUtil.getSharedPreferences(DeskClockApp.getAppContext(), "BedtimeAlarm", 0).getString(BedtimeUtil.SP_WAKE_ALARM_ALERT, ""));
            }
            Log.d(TAG, "timerAlert " + TimerDao.getTimerRingtone());
            Log.d(TAG, "temp ringtone: " + FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getString(AlarmRingtonePickerActivity.KEY_LAST_OTHER_RINGTONE, AlarmRingtonePickerActivity.VALUE_NO_RECORD));
            Log.d(TAG, "defaultAlert " + AlarmRingtoneUtil.getDefaultAlarmRingtone());
        } catch (Throwable th) {
            cursorQuery.close();
            throw th;
        }
    }

    public static String transToFileProviderUri(Uri uri) throws Throwable {
        String fileNameFromUri;
        if (uri.toString().startsWith(NEW_THEME_RINGTONE_URI) && (fileNameFromUri = getFileNameFromUri(DeskClockApp.getAppDEContext(), uri)) != null) {
            ThemeProviderHelper.GrantThemeResult grantThemeResultRequestGrantThemeFiles = ThemeProviderHelper.requestGrantThemeFiles(DeskClockApp.getAppDEContext(), ".ringtone/" + fileNameFromUri, null);
            if (grantThemeResultRequestGrantThemeFiles != null && grantThemeResultRequestGrantThemeFiles.uri != null) {
                return grantThemeResultRequestGrantThemeFiles.uri.toString();
            }
        }
        return uri.toString();
    }

    public static Uri getDewRingtoneUri() {
        if (RingtoneUriCompat.atLeastU()) {
            return RingtoneConstants.RINGTONE_URI_DEW_NEW;
        }
        return RingtoneConstants.RINGTONE_URI_DEW;
    }

    public static Uri getFireflyRingtoneUri() {
        if (RingtoneUriCompat.atLeastU()) {
            return RingtoneConstants.RINGTONE_URI_FIREFLY_NEW;
        }
        return RingtoneConstants.RINGTONE_URI_FIREFLY;
    }

    public static Uri getDreamRingtoneUri() {
        if (RingtoneUriCompat.atLeastU()) {
            return RingtoneConstants.RINGTONE_URI_DREAM_NEW;
        }
        return RingtoneConstants.RINGTONE_URI_DREAM;
    }
}
