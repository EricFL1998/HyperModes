package com.xiaomi.onetrack;

import android.content.Context;
import android.content.Intent;
import com.android.deskclock.settings.SearchContract;
import com.xiaomi.onetrack.api.g;
import com.xiaomi.onetrack.api.m;
import com.xiaomi.onetrack.f.a;
import com.xiaomi.onetrack.util.i;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.z;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miuix.android.content.MiuiIntent;

/* JADX INFO: loaded from: classes2.dex */
public class OneTrack {
    private static boolean a;
    private static boolean b;
    private static boolean c;
    private m d;

    public interface ICommonPropertyProvider {
        Map<String, Object> getDynamicProperty(String str);
    }

    public interface IEventHook {
        boolean isCustomDauEvent(String str);

        boolean isRecommendEvent(String str);
    }

    public enum Mode {
        APP("app"),
        PLUGIN("plugin"),
        SDK("sdk");

        private String a;

        Mode(String str) {
            this.a = str;
        }

        public String getType() {
            return this.a;
        }
    }

    public enum UserIdType {
        XIAOMI("xiaomi"),
        PHONE_NUMBER("phone_number"),
        WEIXIN("weixin"),
        WEIBO("weibo"),
        QQ("qq"),
        OTHER(SearchContract.SearchResultColumn.COLUMN_OTHER);

        private String a;

        UserIdType(String str) {
            this.a = str;
        }

        public String getUserIdType() {
            return this.a;
        }
    }

    public enum NetType {
        NOT_CONNECTED("NONE"),
        MOBILE_2G("2G"),
        MOBILE_3G("3G"),
        MOBILE_4G("4G"),
        MOBILE_5G("5G"),
        WIFI(MiuiIntent.WIFI_NAME),
        ETHERNET("ETHERNET"),
        UNKNOWN("UNKNOWN"),
        CONNECTED("CONNECTED");

        private String a;

        NetType(String str) {
            this.a = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.a;
        }
    }

    private OneTrack(Context context, Configuration configuration) {
        a.a(context.getApplicationContext());
        this.d = new m(context, configuration);
        setEventHook(new DefaultEventHook());
    }

    public static OneTrack createInstance(Context context, Configuration configuration) {
        return new OneTrack(context, configuration);
    }

    public static void setDebugMode(boolean z) {
        p.a(z);
    }

    public static void setDisable(boolean z) {
        a = z;
    }

    public static boolean isDisable() {
        return a;
    }

    public static void setUseSystemNetTrafficOnly() {
        b = true;
    }

    public static boolean isUseSystemNetTrafficOnly() {
        return b;
    }

    public static void setTestMode(boolean z) {
        p.b(z);
    }

    public static void registerCrashHook(Context context) {
        CrashAnalysis.a(context);
    }

    public static void setAccessNetworkEnable(Context context, final boolean z) {
        a(context);
        i.a(new Runnable() { // from class: com.xiaomi.onetrack.OneTrack.1
            @Override // java.lang.Runnable
            public void run() {
                com.xiaomi.onetrack.c.i.a(z);
                com.xiaomi.onetrack.c.i.b(z);
            }
        });
    }

    public static void setRestrictGetNetworkInfo(boolean z) {
        c = z;
    }

    public static boolean isRestrictGetNetworkInfo() {
        return c;
    }

    public static String sdkVersion() {
        return BuildConfig.SDK_VERSION;
    }

    private static void a(Context context) {
        if (context == null) {
            throw new IllegalStateException("context is null!");
        }
        a.a(context.getApplicationContext());
    }

    public void track(String str, Map<String, Object> map) {
        this.d.a(str, map);
    }

    public void trackEventFromH5(String str) {
        this.d.a(str);
    }

    public void track(String str, List<String> list, Map<String, Object> map) {
        HashMap map2 = new HashMap();
        if (map != null) {
            map2.putAll(map);
        }
        map2.put(g.ad, String.join(z.b, list));
        this.d.a(str, (Map<String, Object>) map2);
    }

    public void adTrack(String str, Map<String, Object> map) {
        this.d.a(str, map, (List<String>) null);
    }

    public void adTrack(String str, Map<String, Object> map, List<String> list) {
        this.d.a(str, map, list);
    }

    public void trackPluginEvent(String str, String str2, Map<String, Object> map) {
        this.d.a(str, str2, map);
    }

    public void login(String str, UserIdType userIdType, Map<String, Object> map, boolean z) {
        this.d.a(str, userIdType, map, z);
    }

    public void login(String str, UserIdType userIdType, Map<String, Object> map) {
        login(str, userIdType, map, false);
    }

    public void logout() {
        logout(null, false);
    }

    public void logout(Map<String, Object> map, boolean z) {
        this.d.a(map, z);
    }

    public void setCommonProperty(Map<String, Object> map) {
        this.d.c(map);
    }

    public void clearCommonProperty() {
        this.d.a();
    }

    public void removeCommonProperty(String str) {
        this.d.b(str);
    }

    public void setDynamicCommonProperty(ICommonPropertyProvider iCommonPropertyProvider) {
        this.d.a(iCommonPropertyProvider);
    }

    public void setUserProfile(Map<String, Object> map) {
        this.d.a(map);
    }

    public void setUserProfile(String str, Object obj) {
        this.d.a(str, obj);
    }

    public void userProfileIncrement(Map<String, ? extends Number> map) {
        this.d.b(map);
    }

    public void userProfileIncrement(String str, Number number) {
        this.d.a(str, number);
    }

    public void setInstanceId(String str) {
        this.d.c(str);
    }

    public String getInstanceId() throws OnMainThreadException {
        return this.d.b();
    }

    public String getOAID(Context context) throws OnMainThreadException {
        return this.d.a(context);
    }

    public void trackServiceQualityEvent(ServiceQualityEvent serviceQualityEvent) {
        this.d.a(serviceQualityEvent);
    }

    public void setEventHook(IEventHook iEventHook) {
        this.d.a(iEventHook);
    }

    public void setCustomPrivacyPolicyAccepted(boolean z) {
        this.d.b(z);
    }

    public void setBasicModeEnable(boolean z) {
        this.d.c(z);
    }

    public void setCloseOaidDependMsaSDK(boolean z) {
        this.d.d(z);
    }

    public String appActiveBroadcast(Intent intent) throws OnMainThreadException {
        return this.d.a(intent);
    }
}
