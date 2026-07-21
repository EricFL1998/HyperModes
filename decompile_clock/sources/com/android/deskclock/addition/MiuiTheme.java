package com.android.deskclock.addition;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcelable;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.ringtone.digital.DigitalTimerRingtoneHelper;
import com.android.deskclock.addition.ringtone.star.WYStarRingtoneHelper;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.themeringtone.RingtoneHelper;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class MiuiTheme {
    private static final String ACTION_MIUI_RINGTONE_PICKER = "miui.intent.action.RINGTONE_PICKER";
    private static final int DIRECT_CHANGE_MIN_VERSION_CODE = 260;
    private static final String EXTRA_REQUEST_ENTRY_TYPE = "REQUEST_ENTRY_TYPE";
    private static final String EXTRA_RINGTONE_URI_LIST = "miui.intent.extra.ringtone.EXTRA_RINGTONE_URI_LIST";
    private static final int GLOBAL_DYNAMIC_ALARM_MIN_VERSION_CODE = 330;
    private static final int INTERNAL_DYNAMIC_ALARM_MIN_VERSION_CODE = 310;
    private static final String PACKAGE_NAME = "com.android.thememanager";
    private static final String PARAM_CANNOT_PLAY = "canNotPlay";
    private static final String PARAM_PATH = "path";
    private static final String PARAM_TITLE = "title";
    private static final String RINGTONE_PICKER_ACTIVITY = "com.android.thememanager.activity.ThemeTabActivity";
    private static final String TAG = "DC:MiuiTheme";
    private static final String URI_AUTHORITY = "ringtonePick";
    private static final String URI_PATH = "extraRingtoneInfo";
    private static final String URI_SCHEME = "theme";
    private static int VERSION_CODE = -1;

    private static int getVersionCode() {
        if (VERSION_CODE == -1) {
            VERSION_CODE = Util.getPackageCode(PACKAGE_NAME);
        }
        return VERSION_CODE;
    }

    public static boolean supportWeatherRingtone() {
        int versionCode = getVersionCode();
        boolean z = true;
        if (!Util.isInternational() ? versionCode < 310 : versionCode < 330) {
            z = false;
        }
        Log.i(TAG, "theme version code: " + versionCode + ", support dynamic alarm: " + z);
        return z;
    }

    public static boolean supportWeekRingtone() {
        return supportWeatherRingtone();
    }

    public static boolean supportChangeAlertDirectly() {
        boolean z = getVersionCode() >= 260;
        Log.i(TAG, "theme support change alert directly: " + z);
        return z;
    }

    public static Intent createRingtonePickerIntent(Uri uri, ArrayList<Uri> arrayList, String str) {
        return createRingtonePickerIntent(uri, arrayList, false, str);
    }

    public static Intent createRingtonePickerIntent(Uri uri, String str) {
        return createRingtonePickerIntent(uri, null, false, str);
    }

    public static Intent createRingtonePickerIntent(Uri uri, boolean z, String str) {
        return createRingtonePickerIntent(uri, null, z, str);
    }

    public static Intent createRingtonePickerIntent(Uri uri, ArrayList<Uri> arrayList, boolean z, String str) {
        String str2 = (z && supportChangeAlertDirectly()) ? ACTION_MIUI_RINGTONE_PICKER : "android.intent.action.RINGTONE_PICKER";
        if (RingtoneManager.getDefaultUri(4).equals(uri)) {
            uri = AlarmRingtoneUtil.getDefaultAlarmRingtone();
        }
        if (WeatherRingtoneHelper.isWeatherRingtone(uri)) {
            uri = WeatherRingtoneHelper.getWeatherRingtoneUri();
        }
        Log.i(TAG, "RingtonePickerIntent: use action: " + str2 + ", selected ringtone uri: " + uri);
        ArrayList<? extends Parcelable> arrayList2 = new ArrayList<>();
        if (arrayList != null && arrayList.size() > 0) {
            arrayList2.addAll(arrayList);
        }
        if (WYStarRingtoneHelper.isSupport()) {
            arrayList2.add(getWYStarRingtoneUri());
            Log.i(TAG, "extra star ringtone: " + getWYStarRingtoneUri().toString());
        }
        arrayList2.add(getDigitalTimerRingtoneUri());
        Log.i(TAG, "RingtonePickerIntent: extra size: " + arrayList2.size());
        Intent intent = new Intent();
        intent.setAction(str2);
        intent.setPackage(PACKAGE_NAME);
        Uri uriHandleUriForTheme = RingtoneHelper.handleUriForTheme(uri);
        Log.d(TAG, "handleUriForTheme result: " + uriHandleUriForTheme);
        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", uriHandleUriForTheme);
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(4));
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
        intent.putExtra("android.intent.extra.ringtone.TYPE", 4);
        intent.putParcelableArrayListExtra(EXTRA_RINGTONE_URI_LIST, arrayList2);
        intent.putExtra(EXTRA_REQUEST_ENTRY_TYPE, DeskClockApp.getAppContext().getPackageName());
        intent.putExtra("activity_name", str);
        intent.addFlags(3);
        return intent;
    }

    public static ArrayList<Uri> createAlarmRingtoneExtras() {
        ArrayList<Uri> arrayList = new ArrayList<>();
        if (WeatherRingtoneHelper.isSupport()) {
            arrayList.add(getWeatherRingtoneUri());
            Log.i(TAG, "extra weather ringtone: " + getWeatherRingtoneUri().toString());
        }
        if (WeekRingtoneHelper.isSupport()) {
            arrayList.add(getWeekRingtoneUri());
            Log.i(TAG, "extra week ringtone: " + getWeekRingtoneUri().toString());
        }
        return arrayList;
    }

    public static ArrayList<Uri> createAlarmRingtoneExtrasWithXiaoAi() {
        ArrayList<Uri> arrayList = new ArrayList<>();
        if (WeatherRingtoneHelper.isSupport()) {
            arrayList.add(getWeatherRingtoneUri());
            Log.i(TAG, "extra weather ringtone: " + getWeatherRingtoneUri().toString());
        }
        if (WeekRingtoneHelper.isSupport()) {
            arrayList.add(getWeekRingtoneUri());
            Log.i(TAG, "extra week ringtone: " + getWeekRingtoneUri().toString());
        }
        if (XiaoAiRingtoneHelper.isAvailable() && !isThemeAppSupportSmartAlarm()) {
            arrayList.add(getXiaoAiRingtoneUri());
            Log.i(TAG, "extra xiaoai ringtone: " + getXiaoAiRingtoneUri().toString());
        }
        return arrayList;
    }

    public static ArrayList<Uri> createXiaoAiRingtoneExtra() {
        if (!XiaoAiRingtoneHelper.isAvailable() || isThemeAppSupportSmartAlarm()) {
            return null;
        }
        ArrayList<Uri> arrayList = new ArrayList<>();
        arrayList.add(getXiaoAiRingtoneUri());
        return arrayList;
    }

    public static boolean isThemeAppSupportSmartAlarm() {
        return Util.getBooleanMetaData(PACKAGE_NAME, RINGTONE_PICKER_ACTIVITY, "supportSmartAlarm");
    }

    private static Uri getWeatherRingtoneUri() {
        return new Uri.Builder().scheme(URI_SCHEME).authority(URI_AUTHORITY).appendEncodedPath(URI_PATH).appendQueryParameter("title", DeskClockApp.getAppDEContext().getString(R.string.ringtone_weather)).appendQueryParameter(PARAM_CANNOT_PLAY, "false").appendQueryParameter("path", WeatherRingtoneHelper.getWeatherRingtonePath()).build();
    }

    private static Uri getWeekRingtoneUri() {
        return new Uri.Builder().scheme(URI_SCHEME).authority(URI_AUTHORITY).appendEncodedPath(URI_PATH).appendQueryParameter("title", DeskClockApp.getAppDEContext().getString(R.string.ringtone_week)).appendQueryParameter(PARAM_CANNOT_PLAY, "false").appendQueryParameter("path", WeekRingtoneHelper.getWeekRingtonePath()).build();
    }

    private static Uri getWYStarRingtoneUri() {
        return new Uri.Builder().scheme(URI_SCHEME).authority(URI_AUTHORITY).appendEncodedPath(URI_PATH).appendQueryParameter("title", DeskClockApp.getAppDEContext().getString(R.string.star_wy_alarm_speech)).appendQueryParameter(PARAM_CANNOT_PLAY, "false").appendQueryParameter("path", WYStarRingtoneHelper.getWYStarRingtonePath()).build();
    }

    private static Uri getDigitalTimerRingtoneUri() {
        return new Uri.Builder().scheme(URI_SCHEME).authority(URI_AUTHORITY).appendEncodedPath(URI_PATH).appendQueryParameter("title", DeskClockApp.getAppDEContext().getString(R.string.ringtone_digital_timer)).appendQueryParameter(PARAM_CANNOT_PLAY, "false").appendQueryParameter("path", DigitalTimerRingtoneHelper.getRingtonePath()).build();
    }

    private static Uri getXiaoAiRingtoneUri() {
        return new Uri.Builder().scheme(URI_SCHEME).authority(URI_AUTHORITY).appendEncodedPath(URI_PATH).appendQueryParameter("title", DeskClockApp.getAppDEContext().getString(R.string.xiaoai_ringtone_title)).appendQueryParameter(PARAM_CANNOT_PLAY, "false").appendQueryParameter("path", XiaoAiRingtoneHelper.getXiaoAiRingtonePath()).build();
    }

    public static void recordVersion() {
        try {
            PackageInfo packageInfo = DeskClockApp.getAppDEContext().getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
            Log.f(TAG, "version code: " + packageInfo.versionCode + " , name:" + packageInfo.versionName);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
