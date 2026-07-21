package com.scenariorecognition;

import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes2.dex */
public class ScenarioRecognition {
    public static final long APP_TO_RECENTS_ANIMATION = 260;
    public static final long APP_TO_RECENTS_MOTION = 259;
    public static final long CLEAR_RECENTS = 270;
    public static final long COLLAPSE_CONTROL_CENTER_ANIMATION = 280;
    public static final long COLLAPSE_CONTROL_CENTER_MOTION = 279;
    public static final long COLLAPSE_NOTIFICATION_PANEL_ANIMATION = 276;
    public static final long COLLAPSE_NOTIFICATION_PANEL_MOTION = 275;
    public static final long CORE_SCENARIO_TYPE = 256;
    public static final long EXPAND_CONTROL_CENTER_ANIMATION = 278;
    public static final long EXPAND_CONTROL_CENTER_MOTION = 277;
    public static final long EXPAND_NOTIFICATION_PANEL_ANIMATION = 272;
    public static final long EXPAND_NOTIFICATION_PANEL_MOTION = 271;
    public static final long FACE_UNLOCK_ANIMATION = 288;
    public static final long GESTURELINE_SWITCH_ANIMATION = 267;
    public static final long GESTURELINE_SWITCH_MOTION = 266;
    public static final long HOME_TO_RECENTS_ANIMATION = 258;
    public static final long HOME_TO_RECENTS_MOTION = 257;
    public static final long LOCKSCREEN_SCREEN_OFF_ANIMATION = 290;
    public static final long LOCKSCREEN_SCREEN_ON_ANIMATION = 289;
    public static final long MAGAZINE_SCREEN_OFF_ANIMATION = 294;
    public static final long MAGAZINE_SCREEN_ON_ANIMATION = 293;
    public static final long MIX_PASSWORD_UNLOCK_ANIMATION = 283;
    public static final long NUMBER_PASSWORD_UNLOCK_ANIMATION = 282;
    public static final long RECENTS_CLICK_ANIMATION = 265;
    public static final long RECENTS_SCROLLING_ANIMATION = 262;
    public static final long RECENTS_SCROLLING_MOTION = 261;
    public static final long RECENTS_TO_HOME_ANIMATION = 264;
    public static final long RECENTS_TO_HOME_MOTION = 263;
    public static final long REMOVE_APP_FROM_RECENTS_ANIMATION = 269;
    public static final long REMOVE_APP_FROM_RECENTS_MOTION = 268;
    public static final long REMOVE_NOTIFICATION_ANIMATION = 274;
    public static final long REMOVE_NOTIFICATION_MOTION = 273;
    public static final long RHOMBUS_SCREEN_OFF_ANIMATION = 292;
    public static final long RHOMBUS_SCREEN_ON_ANIMATION = 291;
    public static final long SCREEN_OFF_FINGERPRINT_UNLOCK_ANIMATION = 285;
    public static final long SCREEN_OFF_FINGERPRINT_UNLOCK_MOTION = 284;
    public static final long SCREEN_ON_FINGERPRINT_UNLOCK_ANIMATION = 287;
    public static final long SCREEN_ON_FINGERPRINT_UNLOCK_MOTION = 286;
    private static final String TAG = "CoreScenarioRecognition";
    public static final long UNLOCK_HOME_ANIMATION_ANIMATION = 281;
    private static ScenarioRecognition sModule;
    private int MAX_ATTEMPTS = 10;
    private int invokeFailTimes = 0;
    private Object mInstance;
    private Object mParaInstance;
    private Method mParaMethod;
    private Method mSetMethod;
    private Method mSetMethodWithPara;

    public static synchronized ScenarioRecognition getInstance() {
        if (sModule == null) {
            sModule = new ScenarioRecognition();
        }
        return sModule;
    }

    private ScenarioRecognition() {
        try {
            Class<?> cls = Class.forName("miui.scenariorecognition.ScenarioRecognitionManager");
            this.mInstance = cls.getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            this.mSetMethod = cls.getDeclaredMethod("setScenarioState", Long.TYPE, Long.TYPE, Boolean.TYPE);
            Class<?> cls2 = Class.forName("miui.scenariorecognition.PluginParameter");
            this.mParaInstance = cls2.getDeclaredMethod("create", new Class[0]).invoke(null, new Object[0]);
            this.mParaMethod = cls2.getDeclaredMethod("putString", String.class, String.class);
            this.mSetMethodWithPara = cls.getDeclaredMethod("setScenarioState", Long.TYPE, Long.TYPE, Boolean.TYPE, cls2);
        } catch (Exception unused) {
            System.out.println("CoreScenarioRecognition reflect fail");
            this.invokeFailTimes++;
        }
    }

    public void setScenarioState(long j, long j2, boolean z) {
        Method method;
        if (this.invokeFailTimes > this.MAX_ATTEMPTS) {
            return;
        }
        Object obj = this.mInstance;
        if (obj != null && (method = this.mSetMethod) != null) {
            try {
                method.invoke(obj, Long.valueOf(j), Long.valueOf(j2), Boolean.valueOf(z));
                this.mSetMethod.setAccessible(true);
                return;
            } catch (Exception unused) {
                System.out.println("CoreScenarioRecognition reflect fail");
                this.invokeFailTimes++;
                return;
            }
        }
        System.out.println("CoreScenarioRecognition instance is null!");
        this.invokeFailTimes++;
    }

    public void setScenarioState(long j, long j2, boolean z, String str) {
        Object obj;
        if (this.invokeFailTimes > this.MAX_ATTEMPTS) {
            return;
        }
        if (this.mInstance != null && (obj = this.mParaInstance) != null) {
            try {
                this.mParaMethod.invoke(obj, TAG, str);
                this.mSetMethodWithPara.invoke(this.mInstance, Long.valueOf(j), Long.valueOf(j2), Boolean.valueOf(z), this.mParaInstance);
                return;
            } catch (Exception unused) {
                System.out.println("CoreScenarioRecognition reflect fail");
                this.invokeFailTimes++;
                return;
            }
        }
        System.out.println("CoreScenarioRecognition instance is null!");
        this.invokeFailTimes++;
    }
}
