package com.android.deskclock.util;

import android.os.SystemClock;
import com.scenariorecognition.ScenarioRecognition;
import com.xiaomi.settingsdk.backup.SettingsBackupConsts;
import kotlin.Metadata;

/* JADX INFO: compiled from: ScenarioRecognitionUtil.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u0011J \u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Lcom/android/deskclock/util/ScenarioRecognitionUtil;", "", "<init>", "()V", "TAG", "", "ALARM_LOADER_DATA", "", "WORLD_CLOCK_LOADER_DATA", "ALARM_DIALOG_SHOW", "ALARM_LIST_START_DELAY", "ALARM_LIST_FLUENCY", "ALARM_DIALOG_HIDE", "setScenarioState", "", "type", "isStart", "", SettingsBackupConsts.EXTRA_PACKAGE_NAME, "app_cnRelease"}, k = 1, mv = {2, 1, 0}, xi = 48)
public final class ScenarioRecognitionUtil {
    public static final long ALARM_DIALOG_HIDE = 337;
    public static final long ALARM_DIALOG_SHOW = 334;
    public static final long ALARM_LIST_FLUENCY = 336;
    public static final long ALARM_LIST_START_DELAY = 335;
    public static final long ALARM_LOADER_DATA = 332;
    public static final ScenarioRecognitionUtil INSTANCE = new ScenarioRecognitionUtil();
    private static final String TAG = "DC:ScenarioRecognitionUtil";
    public static final long WORLD_CLOCK_LOADER_DATA = 333;

    private ScenarioRecognitionUtil() {
    }

    public final void setScenarioState(long type, boolean isStart) {
        Log.i(TAG, "setScenarioState, type: " + type + ", isStart: " + isStart);
        ScenarioRecognition.getInstance().setScenarioState(type, SystemClock.elapsedRealtime(), isStart);
    }

    public final void setScenarioState(long type, boolean isStart, String packageName) {
        if (packageName == null) {
            Log.e(TAG, "setScenarioState, packageName is null");
            return;
        }
        Log.i(TAG, "setScenarioState, type:" + type + ", isStart: " + isStart + ", packageName: " + packageName);
        ScenarioRecognition.getInstance().setScenarioState(type, SystemClock.elapsedRealtime(), isStart, packageName);
    }
}
