package com.xiaomi.onetrack.api;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.CrashAnalysis;
import com.xiaomi.onetrack.OnMainThreadException;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.ServiceQualityEvent;
import com.xiaomi.onetrack.util.DeviceUtil;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class m {
    private static final String a = "OneTrackImp";
    private static ExecutorService c;
    private j b;
    private Context d;
    private k e;
    private Configuration f;
    private OneTrack.ICommonPropertyProvider g;
    private OneTrack.IEventHook h;
    private com.xiaomi.onetrack.util.v i;
    private boolean j = false;

    public m(Context context, Configuration configuration) {
        Context applicationContext = context.getApplicationContext();
        this.d = applicationContext;
        this.f = configuration;
        b(applicationContext);
        Log.d(a, "OneTrackImp init : " + configuration.toString());
        Log.d(a, "OneTrackImp sdk ver : 2.0.9");
    }

    private void b(Context context) {
        com.xiaomi.onetrack.util.p.a();
        com.xiaomi.onetrack.util.q.a(this.f.isInternational(), this.f.getRegion(), this.f.getMode());
        if (c == null) {
            c = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        }
        this.i = new com.xiaomi.onetrack.util.v(this.f);
        if (com.xiaomi.onetrack.util.q.a() && e() && c()) {
            com.xiaomi.onetrack.util.o.a().a((Boolean) true);
            this.b = new ao(this.f, this.i);
        } else {
            com.xiaomi.onetrack.util.o.a().a((Boolean) false);
            this.b = new am(context, this.f, this.i);
        }
        if (this.f.getMode() == OneTrack.Mode.APP) {
            com.xiaomi.onetrack.util.q.a(this.f.isOverrideMiuiRegionSetting());
            c(context);
            if (this.f.isExceptionCatcherEnable()) {
                CrashAnalysis.start(context, this);
                if (!CrashAnalysis.isSupport()) {
                    k kVar = new k();
                    this.e = kVar;
                    kVar.a();
                }
            }
        }
        c.execute(new n(this));
    }

    private boolean c() {
        if (this.f.isOverrideMiuiRegionSetting()) {
            return TextUtils.equals(com.xiaomi.onetrack.util.q.j(), this.f.getRegion());
        }
        return true;
    }

    public void a(String str, String str2, Map<String, Object> map) {
        c.execute(new y(this, str2, map, str));
    }

    public void a(String str, Map<String, Object> map) {
        c.execute(new af(this, str, map));
    }

    public void a(String str, Map<String, Object> map, List<String> list) {
        c.execute(new ag(this, str, map, list));
    }

    public void a(String str) {
        c.execute(new ah(this, str));
    }

    public void a(String str, String str2, String str3, String str4, String str5, long j) {
        c.execute(new ai(this, str, str2, str3, str5, str4, j));
    }

    public void a(Map<String, Object> map) {
        c.execute(new aj(this, map));
    }

    public void a(String str, Object obj) {
        c.execute(new ak(this, obj, str));
    }

    public void a(String str, OneTrack.UserIdType userIdType, Map<String, Object> map, boolean z) {
        c.execute(new al(this, str, userIdType, z, map));
    }

    public void b(Map<String, ? extends Number> map) {
        c.execute(new o(this, map));
    }

    public void a(String str, Number number) {
        c.execute(new p(this, str, number));
    }

    public void a(Map<String, Object> map, boolean z) {
        c.execute(new q(this, z, map));
    }

    private void c(Context context) {
        ((Application) context).registerActivityLifecycleCallbacks(new r(this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d() {
        c.execute(new s(this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str, boolean z) {
        c.execute(new t(this, str, z));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str, long j) {
        c.execute(new u(this, str, j));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e(boolean z) {
        c.execute(new v(this, z));
    }

    private boolean e() {
        if (com.xiaomi.onetrack.util.p.a) {
            com.xiaomi.onetrack.util.p.a(a, "enable:" + f() + " isSupportEmptyEvent: " + g() + "_isSupportAdMonitor():" + h());
        }
        return f() && g() && h();
    }

    private boolean f() {
        try {
            int componentEnabledSetting = com.xiaomi.onetrack.f.a.b().getPackageManager().getComponentEnabledSetting(new ComponentName("com.miui.analytics", ar.b));
            return componentEnabledSetting == 1 || componentEnabledSetting == 0;
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b(a, "enable error:" + e.toString());
            return false;
        }
    }

    private static boolean g() {
        try {
            int i = com.xiaomi.onetrack.f.a.b().getPackageManager().getPackageInfo("com.miui.analytics", 0).versionCode;
            if (i >= 2020062900) {
                return true;
            }
            com.xiaomi.onetrack.util.p.a(a, "system analytics version: " + i);
        } catch (Throwable th) {
            com.xiaomi.onetrack.util.p.b(a, "isSupportEmptyEvent error:" + th.getMessage());
        }
        return false;
    }

    private boolean h() {
        try {
            if (TextUtils.isEmpty(this.f.getAdEventAppId()) || OneTrack.isUseSystemNetTrafficOnly()) {
                return true;
            }
            int i = com.xiaomi.onetrack.f.a.b().getPackageManager().getPackageInfo("com.miui.analytics", 0).versionCode;
            com.xiaomi.onetrack.util.p.a(a, "system analytics version: " + i);
            return i >= 2022042900;
        } catch (Throwable th) {
            com.xiaomi.onetrack.util.p.b(a, "isSupportAdMonitor error:" + th.getMessage());
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean d(String str) {
        boolean zA = com.xiaomi.onetrack.util.r.a(str);
        if (!zA) {
            com.xiaomi.onetrack.util.p.b(a, String.format("Invalid eventname: %s. Eventname can only consist of numbers, letters, underscores ,and can not start with a number or \"onetrack_\" or \"ot_\"", str));
        }
        return !zA;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean e(String str) {
        if ("onetrack_dau".equals(str) || g.af.equals(str) || "ot_login".equals(str) || "ot_logout".equals(str)) {
            return false;
        }
        boolean zA = com.xiaomi.onetrack.util.r.a(str);
        if (!zA) {
            com.xiaomi.onetrack.util.p.b(a, String.format("Invalid eventname: %s. Eventname can only consist of numbers, letters, underscores ,and can not start with a number or \"onetrack_\" or \"ot_\"", str));
        }
        return !zA;
    }

    public void c(Map<String, Object> map) {
        if (map == null) {
            return;
        }
        c.execute(new w(this, map));
    }

    public void a() {
        c.execute(new x(this));
    }

    public void b(String str) {
        c.execute(new z(this, str));
    }

    public void a(OneTrack.ICommonPropertyProvider iCommonPropertyProvider) {
        this.g = iCommonPropertyProvider;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public JSONObject f(String str) throws Throwable {
        try {
            OneTrack.ICommonPropertyProvider iCommonPropertyProvider = this.g;
            JSONObject jSONObjectA = com.xiaomi.onetrack.util.r.a(iCommonPropertyProvider != null ? iCommonPropertyProvider.getDynamicProperty(str) : null, false);
            String strA = com.xiaomi.onetrack.util.k.a(com.xiaomi.onetrack.util.r.a(this.f));
            return com.xiaomi.onetrack.util.r.a(jSONObjectA, !TextUtils.isEmpty(strA) ? new JSONObject(strA) : null);
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b(a, "getCommonProperty: " + e.toString());
            return null;
        }
    }

    public void c(String str) {
        c.execute(new aa(this, str));
    }

    public String b() throws OnMainThreadException {
        if (com.xiaomi.onetrack.util.w.a()) {
            throw new OnMainThreadException("Can't call this method on main thread");
        }
        return com.xiaomi.onetrack.util.o.a().b();
    }

    public String a(Context context) throws OnMainThreadException {
        if (com.xiaomi.onetrack.util.w.a()) {
            throw new OnMainThreadException("Can't call this method on main thread");
        }
        return DeviceUtil.j(context);
    }

    public void a(ServiceQualityEvent serviceQualityEvent) {
        if (serviceQualityEvent == null) {
            return;
        }
        c.execute(new ab(this, serviceQualityEvent));
    }

    public void a(boolean z) {
        com.xiaomi.onetrack.util.p.a = z;
    }

    private void i() {
        c.execute(new ac(this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void j() {
        if (com.xiaomi.onetrack.c.i.d()) {
            c.execute(new ad(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void k() {
        try {
            if (this.f.getMode() != OneTrack.Mode.APP) {
                return;
            }
            long jD = com.xiaomi.onetrack.f.a.d();
            String strA = a(jD, com.xiaomi.onetrack.f.a.c());
            String strA2 = com.xiaomi.onetrack.util.aa.A();
            if (TextUtils.isEmpty(strA2)) {
                com.xiaomi.onetrack.util.aa.j(strA);
                return;
            }
            JSONObject jSONObject = new JSONObject(strA2);
            long jOptLong = jSONObject.optLong(g.X);
            String strOptString = jSONObject.optString(g.Y);
            if (jOptLong != jD) {
                com.xiaomi.onetrack.util.aa.j(strA);
                this.b.a(g.j, h.a(jOptLong, strOptString, jD, com.xiaomi.onetrack.f.a.f(), this.f, this.h, this.i, this.j));
            }
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b(a, "trackUpgradeEvent error: " + e.toString());
        }
    }

    private String a(long j, String str) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(g.Y, str);
        jSONObject.put(g.X, j);
        return jSONObject.toString();
    }

    public void a(OneTrack.IEventHook iEventHook) {
        this.h = iEventHook;
        this.i.a(iEventHook);
    }

    public void b(boolean z) {
        if (this.f.isUseCustomPrivacyPolicy()) {
            c.execute(new ae(this, z));
        }
    }

    public void c(boolean z) {
        this.j = z;
    }

    public void d(boolean z) {
        com.xiaomi.onetrack.util.oaid.a.a().a(z);
    }

    public String a(Intent intent) throws OnMainThreadException {
        if (com.xiaomi.onetrack.util.w.a()) {
            throw new OnMainThreadException("Can't call this method on main thread");
        }
        if (intent == null || !a.a().c()) {
            com.xiaomi.onetrack.util.p.b(a, "Not allowed to call,intent is null or Not specify the package name");
            return "";
        }
        return a.a().a(intent);
    }
}
