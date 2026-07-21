package com.android.deskclock.addition.resource;

import android.net.Uri;
import android.text.TextUtils;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.ringtone.RingtoneConstants;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.util.themeringtone.RingtoneHelper;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class MiuiResource {
    private static final String TAG = "DC:ExternalResourceUtils";
    private OnLoadListener mLoadListener;
    private ExternalResourceUtils.ModuleLoadListener mModuleLoadListener;

    public interface OnLoadListener {
        void onFail(boolean z);

        void onSuccess(int i, boolean z);
    }

    public MiuiResource(OnLoadListener onLoadListener) {
        this.mLoadListener = onLoadListener;
    }

    public void clear() {
        this.mLoadListener = null;
        this.mModuleLoadListener = null;
    }

    public static void checkLocalResourceVersion() {
        for (int i = 5; i >= 2; i--) {
            if (!TextUtils.isEmpty(ExternalResourceUtils.getExternalResourceExistFlag(i)) && new File(ExternalResourceUtils.getExternalResourceExistFlag(i)).exists()) {
                Log.i("DC:ExternalResourceUtils", "check local file and save resource version: " + i);
                PrefUtil.setMiuiResourceVersion(i);
                ExternalResourceUtils.recordRomResourceLoaded();
                return;
            }
        }
    }

    public void loadRomResource(final int i) throws Throwable {
        ExternalResourceUtils.ModuleLoadListener moduleLoadListener = new ExternalResourceUtils.ModuleLoadListener() { // from class: com.android.deskclock.addition.resource.MiuiResource.1
            @Override // com.android.deskclock.addition.resource.ExternalResourceUtils.ModuleLoadListener
            public void onLoadSuccess(String str, String str2) {
                int versionWithModuleName = ExternalResourceUtils.getVersionWithModuleName(str);
                if (versionWithModuleName == 2 || versionWithModuleName == 3 || versionWithModuleName == 4) {
                    Log.i("DC:ExternalResourceUtils", "save resource version: " + versionWithModuleName + " from rom load");
                    PrefUtil.setMiuiResourceVersion(versionWithModuleName);
                    MiuiResource.this.changeDefaultAlertToWeather();
                    if (MiuiResource.this.mLoadListener != null) {
                        MiuiResource.this.mLoadListener.onSuccess(versionWithModuleName, false);
                        return;
                    }
                    return;
                }
                if (versionWithModuleName == 5) {
                    Log.i("DC:ExternalResourceUtils", "save resource version: " + versionWithModuleName + " from rom load");
                    PrefUtil.setMiuiResourceVersion(versionWithModuleName);
                    MiuiResource.this.changeDefaultAlertToWeek();
                    if (MiuiResource.this.mLoadListener != null) {
                        MiuiResource.this.mLoadListener.onSuccess(versionWithModuleName, false);
                        return;
                    }
                    return;
                }
                Log.i("DC:ExternalResourceUtils", "save resource version: 1 with rom load failed");
                PrefUtil.setMiuiResourceVersion(1);
                if (MiuiResource.this.mLoadListener != null) {
                    MiuiResource.this.mLoadListener.onFail(false);
                }
            }

            @Override // com.android.deskclock.addition.resource.ExternalResourceUtils.ModuleLoadListener
            public void onLoadFail(String str, int i2) throws Throwable {
                int versionWithModuleName = ExternalResourceUtils.getVersionWithModuleName(str);
                int i3 = 3;
                if (versionWithModuleName == 3) {
                    i3 = 2;
                } else if (versionWithModuleName != 4) {
                    i3 = versionWithModuleName != 5 ? 1 : 4;
                }
                if (i3 == 1) {
                    Log.i("DC:ExternalResourceUtils", "save resource version: 1 with rom load failed");
                    PrefUtil.setMiuiResourceVersion(1);
                    if (MiuiResource.this.mLoadListener != null) {
                        MiuiResource.this.mLoadListener.onFail(false);
                        return;
                    }
                    return;
                }
                if (i3 == i) {
                    if (MiuiResource.this.mLoadListener != null) {
                        Log.i("DC:ExternalResourceUtils", "module: " + i3 + " has been loaded");
                        MiuiResource.this.mLoadListener.onSuccess(i, false);
                        return;
                    }
                    return;
                }
                ExternalResourceUtils.loadRomModule(i3, MiuiResource.this.mModuleLoadListener);
            }
        };
        this.mModuleLoadListener = moduleLoadListener;
        ExternalResourceUtils.loadRomModule(5, moduleLoadListener);
    }

    public void loadNetResource() throws Throwable {
        ExternalResourceUtils.ModuleLoadListener moduleLoadListener = new ExternalResourceUtils.ModuleLoadListener() { // from class: com.android.deskclock.addition.resource.MiuiResource.2
            @Override // com.android.deskclock.addition.resource.ExternalResourceUtils.ModuleLoadListener
            public void onLoadSuccess(String str, String str2) {
                int versionWithModuleName = ExternalResourceUtils.getVersionWithModuleName(str);
                Log.i("DC:ExternalResourceUtils", "save resource version: " + versionWithModuleName + " from net load");
                PrefUtil.setMiuiResourceVersion(versionWithModuleName);
                MiuiResource.this.changeDefaultAlertToWeek();
                if (MiuiResource.this.mLoadListener != null) {
                    MiuiResource.this.mLoadListener.onSuccess(versionWithModuleName, true);
                }
            }

            @Override // com.android.deskclock.addition.resource.ExternalResourceUtils.ModuleLoadListener
            public void onLoadFail(String str, int i) {
                if (MiuiResource.this.mLoadListener != null) {
                    MiuiResource.this.mLoadListener.onFail(true);
                }
            }
        };
        this.mModuleLoadListener = moduleLoadListener;
        ExternalResourceUtils.loadNetModule(moduleLoadListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeDefaultAlertToWeather() {
        Uri defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
        if (defaultAlarmRingtone != null && !defaultAlarmRingtone.toString().startsWith(RingtoneConstants.SYSTEM_MEDIA_PATH) && !defaultAlarmRingtone.toString().startsWith(RingtoneHelper.PRIVATE_RINGTONE_URI) && !defaultAlarmRingtone.toString().startsWith(RingtoneConstants.SYSTEM_MEDIA_PATH_NEW)) {
            Log.i("DC:ExternalResourceUtils", "don't change default alarm alert, default alarm ringtone: " + defaultAlarmRingtone.toString());
            PrefUtil.setRingtoneModifiedToWeather(true);
            PrefUtil.setRingtoneModifiedToWeek(true);
        } else {
            if (!WeatherRingtoneHelper.isRomSupport() || PrefUtil.hasRingtoneModifiedToWeather() || PrefUtil.hasRingtoneModifiedToWeek()) {
                return;
            }
            Log.i("DC:ExternalResourceUtils", "change default alarm alert to weather ringtone");
            AlarmRingtoneUtil.setDefaultAlarmRingtone(WeatherRingtoneHelper.getWeatherRingtoneUri());
            PrefUtil.setRingtoneModifiedToWeather(true);
            PrefUtil.setRingtoneModifiedToWeek(true);
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_RESOURCE_STATE, "NEW");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeDefaultAlertToWeek() {
        Uri defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
        if (defaultAlarmRingtone != null && !defaultAlarmRingtone.toString().startsWith(RingtoneConstants.SYSTEM_MEDIA_PATH) && !defaultAlarmRingtone.toString().startsWith(RingtoneHelper.PRIVATE_RINGTONE_URI) && !defaultAlarmRingtone.toString().startsWith(RingtoneConstants.SYSTEM_MEDIA_PATH_NEW)) {
            Log.i("DC:ExternalResourceUtils", "don't change default alarm alert, default alarm ringtone: " + defaultAlarmRingtone.toString());
            PrefUtil.setRingtoneModifiedToWeather(true);
            PrefUtil.setRingtoneModifiedToWeek(true);
            return;
        }
        if (Util.isRestored(DeskClockApp.getAppDEContext()) || (WeatherRingtoneHelper.getWeatherRingtoneUri() != null && WeatherRingtoneHelper.getWeatherRingtoneUri().equals(defaultAlarmRingtone))) {
            Log.i("DC:ExternalResourceUtils", "don't change backup alarm alert, default alarm ringtone: " + defaultAlarmRingtone);
            PrefUtil.setRingtoneModifiedToWeather(true);
            PrefUtil.setRingtoneModifiedToWeek(true);
        } else {
            if (!WeekRingtoneHelper.isRomSupport() || PrefUtil.hasRingtoneModifiedToWeek() || PrefUtil.hasRingtoneModifiedToWeather()) {
                return;
            }
            Log.i("DC:ExternalResourceUtils", "change default alarm alert to week ringtone");
            AlarmRingtoneUtil.setDefaultAlarmRingtone(WeekRingtoneHelper.getWeekRingtoneUri());
            PrefUtil.setRingtoneModifiedToWeek(true);
            PrefUtil.setRingtoneModifiedToWeather(true);
            PrefUtil.setWeekRingtoneRecommendInClock(true);
            PrefUtil.setWeekRingtoneRecommendInEdit(true);
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_RESOURCE_STATE, "NEW");
        }
    }
}
