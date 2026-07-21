package com.android.deskclock.addition.resource;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.FileUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import java.io.File;
import java.lang.ref.WeakReference;
import miui.module.ModuleManager;

/* JADX INFO: loaded from: classes.dex */
public class ExternalResourceUtils {
    public static final String ACTION_MODULE_DOWNLOAD_FINISH = "module_download_finish";
    public static final String EXTERNAL_PATH_ASSERT = "assets";
    private static final String MODULE_NAME_V1 = "com.android.deskclocklib.res20180513";
    private static final String MODULE_NAME_V2 = "com.android.deskclocklib.res20180703";
    private static final String MODULE_NAME_V2_FLAG = "dynamic_alarm/Sunny/Alarm_Sunny_Remarks.mp3";
    private static final String MODULE_NAME_V3 = "com.android.deskclocklib.res20190122";
    private static final String MODULE_NAME_V3_FLAG = "dynamic_alarm/Sunny/Alarm_Sunny_Remarks_Low.mp3";
    private static final String MODULE_NAME_V4 = "com.android.deskclocklib.res20190606";
    private static final String MODULE_NAME_V4_FLAG = "dynamic_alarm/Overcast/Alarm_Overcast_Background1.mp3";
    private static final String MODULE_NAME_V5 = "com.android.deskclocklib.res20190812";
    private static final String MODULE_NAME_V5_FLAG = "week_ringtone/Monday/Monday1.mp3";
    public static final int NEWEST_VERSION = 5;
    public static final String PREF_NET_RESOURCE_REMIND = "can_remind_update_v5";
    public static final String PREF_ROM_RESOURCE_LOAD = "has_load_rom_resource";
    public static final String TAG = "DC:ExternalResourceUtils";
    public static final int V0 = 0;
    public static final int V1 = 1;
    public static final int V2 = 2;
    public static final int V3 = 3;
    public static final int V4 = 4;
    public static final int V5 = 5;
    private static WeakReference<ModuleLoadListener> sListenerReference;
    private static LoadManager.ModuleLoadListener sModuleLoadListenerForS = new LoadManager.ModuleLoadListener() { // from class: com.android.deskclock.addition.resource.ExternalResourceUtils.1
        @Override // com.android.deskclock.addition.resource.LoadManager.ModuleLoadListener
        public void onLoadSuccess(String str, String str2) throws Throwable {
            Log.i(ExternalResourceUtils.TAG, "loadModule success through defined way, module=" + str + ", path=" + str2);
            if (str.equals("com.miui.system")) {
                return;
            }
            FileUtil.unzipFile(str2, Util.getFilesDir() + File.separator + str, ExternalResourceUtils.EXTERNAL_PATH_ASSERT);
            if (str.equals(ExternalResourceUtils.getModuleNameWithVersion(5))) {
                ExternalResourceUtils.deleteOldModule();
            }
            ModuleLoadListener moduleLoadListener = ExternalResourceUtils.sListenerReference == null ? null : (ModuleLoadListener) ExternalResourceUtils.sListenerReference.get();
            if (moduleLoadListener != null) {
                moduleLoadListener.onLoadSuccess(str, str2);
            } else {
                Log.e(ExternalResourceUtils.TAG, "loadModule error, null listener");
            }
        }

        @Override // com.android.deskclock.addition.resource.LoadManager.ModuleLoadListener
        public void onLoadFail(String str, int i) {
            Log.i(ExternalResourceUtils.TAG, "loadModule failure through defined way, module=" + str + ", code=" + i);
            if (str.equals("com.miui.system")) {
                return;
            }
            ModuleLoadListener moduleLoadListener = ExternalResourceUtils.sListenerReference == null ? null : (ModuleLoadListener) ExternalResourceUtils.sListenerReference.get();
            if (moduleLoadListener != null) {
                moduleLoadListener.onLoadFail(str, i);
            } else {
                Log.e(ExternalResourceUtils.TAG, "loadModule error, null listener");
            }
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_RESOURCE_STATE, "NULL");
        }
    };
    private static final Object sDeleteFileLock = new Object();

    public interface ModuleLoadListener {
        void onLoadFail(String str, int i);

        void onLoadSuccess(String str, String str2);
    }

    public static int getVersionWithModuleName(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        if (str.equals(MODULE_NAME_V5)) {
            return 5;
        }
        if (str.equals(MODULE_NAME_V4)) {
            return 4;
        }
        if (str.equals(MODULE_NAME_V3)) {
            return 3;
        }
        if (str.equals(MODULE_NAME_V2)) {
            return 2;
        }
        return str.equals(MODULE_NAME_V1) ? 1 : 0;
    }

    public static String getModuleNameWithVersion(int i) {
        if (i == 1) {
            return MODULE_NAME_V1;
        }
        if (i == 2) {
            return MODULE_NAME_V2;
        }
        if (i == 3) {
            return MODULE_NAME_V3;
        }
        if (i == 4) {
            return MODULE_NAME_V4;
        }
        if (i == 5) {
            return MODULE_NAME_V5;
        }
        return "";
    }

    public static String getModuleExistFlagWithVersion(int i) {
        if (i == 2) {
            return MODULE_NAME_V2_FLAG;
        }
        if (i == 3) {
            return MODULE_NAME_V3_FLAG;
        }
        if (i == 4) {
            return MODULE_NAME_V4_FLAG;
        }
        if (i == 5) {
            return MODULE_NAME_V5_FLAG;
        }
        return "";
    }

    public static void recordRomResourceLoaded() {
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putBoolean(PREF_ROM_RESOURCE_LOAD, true).apply();
    }

    public static boolean hasLoadRomResource() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getBoolean(PREF_ROM_RESOURCE_LOAD, false);
    }

    public static boolean canRemindNetResource() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getBoolean(PREF_NET_RESOURCE_REMIND, true);
    }

    public static boolean needUpdateResource() {
        int miuiResourceVersion = PrefUtil.getMiuiResourceVersion();
        return miuiResourceVersion < 4 || (WeekRingtoneHelper.isRomSupport() && miuiResourceVersion < 5);
    }

    public static void loadRomModule(int i, ModuleLoadListener moduleLoadListener) throws Throwable {
        if (moduleLoadListener == null) {
            Log.e(TAG, "loadModule cancel, manager or listener is null");
            return;
        }
        sListenerReference = new WeakReference<>(moduleLoadListener);
        if (Util.isVersionS()) {
            LoadManager.loadRomModule(sModuleLoadListenerForS, getModuleNameWithVersion(i), getModuleLoadDirectory());
            return;
        }
        try {
            ModuleManager.getInstance().setModuleLoadListener(new ModuleManager.ModuleLoadListener() { // from class: com.android.deskclock.addition.resource.ExternalResourceUtils.2
                public void onLoadFinish() {
                }

                public void onLoadSuccess(String str, String str2) throws Throwable {
                    Log.i(ExternalResourceUtils.TAG, "loadModule success through ModuleManager, module=" + str + ", path=" + str2);
                    if (str.equals("com.miui.system")) {
                        return;
                    }
                    FileUtil.unzipFile(str2, Util.getFilesDir() + File.separator + str, ExternalResourceUtils.EXTERNAL_PATH_ASSERT);
                    if (str.equals(ExternalResourceUtils.getModuleNameWithVersion(5))) {
                        ExternalResourceUtils.deleteOldModule();
                    }
                    ModuleLoadListener moduleLoadListener2 = ExternalResourceUtils.sListenerReference == null ? null : (ModuleLoadListener) ExternalResourceUtils.sListenerReference.get();
                    if (moduleLoadListener2 != null) {
                        moduleLoadListener2.onLoadSuccess(str, str2);
                    } else {
                        Log.e(ExternalResourceUtils.TAG, "loadModule error, null listener");
                    }
                }

                public void onLoadFail(String str, int i2) {
                    Log.i(ExternalResourceUtils.TAG, "loadModule failure through ModuleManager, module=" + str + ", code=" + i2);
                    if (str.equals("com.miui.system")) {
                        return;
                    }
                    ModuleLoadListener moduleLoadListener2 = ExternalResourceUtils.sListenerReference == null ? null : (ModuleLoadListener) ExternalResourceUtils.sListenerReference.get();
                    if (moduleLoadListener2 != null) {
                        moduleLoadListener2.onLoadFail(str, i2);
                    } else {
                        Log.e(ExternalResourceUtils.TAG, "loadModule error, null listener");
                    }
                    StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_RESOURCE_STATE, "NULL");
                }
            });
            Log.i(TAG, "loadModule: " + i);
            ModuleManager.getInstance().loadModules(new ClockResourceRepository(DeskClockApp.getAppDEContext()), new String[]{getModuleNameWithVersion(i)});
        } catch (Error e) {
            Log.e(TAG, "loadRomModule from ModuleManager error:" + e);
            LoadManager.loadRomModule(sModuleLoadListenerForS, getModuleNameWithVersion(i), getModuleLoadDirectory());
        } catch (Exception e2) {
            Log.e(TAG, "loadRomModule from ModuleManager Exception:" + e2);
            LoadManager.loadRomModule(sModuleLoadListenerForS, getModuleNameWithVersion(i), getModuleLoadDirectory());
        }
    }

    public static void loadNetModule(ModuleLoadListener moduleLoadListener) throws Throwable {
        if (moduleLoadListener == null) {
            return;
        }
        sListenerReference = new WeakReference<>(moduleLoadListener);
        Log.i(TAG, "download module: 5");
        long jCurrentTimeMillis = System.currentTimeMillis();
        LoadManager.loadNetModule(sModuleLoadListenerForS, getModuleNameWithVersion(5), getModuleLoadDirectory());
        Log.d(TAG, "download module use: " + (System.currentTimeMillis() - jCurrentTimeMillis) + "ms");
        DeskClockApp.getAppContext().sendBroadcast(new Intent(ACTION_MODULE_DOWNLOAD_FINISH));
        StatHelper.deskclockEvent(StatHelper.EVENT_EXTERNAL_RESOURCE_DOWNLOAD_COUNT);
        OneTrackStatHelper.trackTriggerEvent("");
    }

    public static String getExternalResourceExistFlag(int i) {
        if (i <= 1 || i > 5) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(DeskClockApp.getAppDEContext().getFilesDir().getAbsolutePath()).append(File.separator).append(getModuleNameWithVersion(i)).append(File.separator).append(EXTERNAL_PATH_ASSERT).append(File.separator).append(getModuleExistFlagWithVersion(i));
        return sb.toString();
    }

    public static String getExternalResourcePath() {
        int miuiResourceVersion = PrefUtil.getMiuiResourceVersion();
        if (miuiResourceVersion <= 1) {
            return null;
        }
        try {
            File file = new File(DeskClockApp.getAppDEContext().getFilesDir().getAbsolutePath() + File.separator + getModuleNameWithVersion(miuiResourceVersion) + File.separator + EXTERNAL_PATH_ASSERT);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("getRootPath error", e);
            return null;
        }
    }

    public static String getAssetsPath() {
        try {
            File file = new File(DeskClockApp.getAppDEContext().getFilesDir().getAbsolutePath() + File.separator + EXTERNAL_PATH_ASSERT);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("getRootPath error", e);
            return null;
        }
    }

    public static boolean supportWeatherRingtone() {
        return PrefUtil.getMiuiResourceVersion() >= 2;
    }

    public static boolean supportWhiteNoise() {
        return PrefUtil.getMiuiResourceVersion() >= 2;
    }

    public static boolean supportWeekRingtone() {
        return PrefUtil.getMiuiResourceVersion() >= 5;
    }

    public static void toastResourceUnzipping() {
        Toast.makeText(DeskClockApp.getAppDEContext(), R.string.module_unzipping, 0).show();
    }

    public static void toastResourceDownloading() {
        Toast.makeText(DeskClockApp.getAppDEContext(), R.string.module_downloading, 0).show();
    }

    public static void toastDownloadFail() {
        Toast.makeText(DeskClockApp.getAppDEContext(), R.string.module_download_failure, 0).show();
    }

    public static void toastDownloadSuccess() {
        Toast.makeText(DeskClockApp.getAppDEContext(), R.string.module_download_success, 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void deleteOldModule() {
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.addition.resource.ExternalResourceUtils.3
            @Override // java.lang.Runnable
            public void run() {
                synchronized (ExternalResourceUtils.sDeleteFileLock) {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    Log.i(ExternalResourceUtils.TAG, "delete old module");
                    try {
                        String moduleLoadDirectory = ExternalResourceUtils.getModuleLoadDirectory();
                        String moduleUnzipDirectory = ExternalResourceUtils.getModuleUnzipDirectory();
                        for (int i = 2; i < 5; i++) {
                            String moduleNameWithVersion = ExternalResourceUtils.getModuleNameWithVersion(i);
                            Log.i(ExternalResourceUtils.TAG, "delete module: " + moduleNameWithVersion);
                            String str = moduleLoadDirectory + File.separator + moduleNameWithVersion + ResourceRepository.MODULE_FILE_EXTENSION;
                            if (FileUtil.isFileExist(str)) {
                                Log.i(ExternalResourceUtils.TAG, "delete module apk: " + str);
                                FileUtil.delete(str);
                                String str2 = moduleLoadDirectory + File.separator + moduleNameWithVersion + ".lib";
                                Log.i(ExternalResourceUtils.TAG, "delete module lib: " + str2);
                                FileUtil.delete(str2);
                                String str3 = moduleUnzipDirectory + File.separator + moduleNameWithVersion;
                                Log.i(ExternalResourceUtils.TAG, "delete module unzip: " + str3);
                                FileUtil.delete(str3);
                            } else {
                                Log.i(ExternalResourceUtils.TAG, "module apk: " + str + " not exist");
                            }
                        }
                    } catch (Exception e) {
                        Log.e(ExternalResourceUtils.TAG, "delete old module error: " + e.getMessage());
                    }
                    Log.i(ExternalResourceUtils.TAG, "delete old module files takes " + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getModuleLoadDirectory() {
        return Util.getFilesDir(DeskClockApp.getAppContext()) + File.separator + "miuisdk" + File.separator + "modules";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getModuleUnzipDirectory() {
        return Util.getFilesDir();
    }
}
