package com.xiaomi.onetrack.api;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.SystemClock;
import com.xiaomi.onetrack.util.DeviceUtil;

/* JADX INFO: loaded from: classes2.dex */
class r implements Application.ActivityLifecycleCallbacks {
    final /* synthetic */ m a;
    private int b = 0;
    private int c;
    private long d;
    private boolean e;
    private boolean f;

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    r(m mVar) {
        this.a = mVar;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
        if (this.b == 0) {
            this.a.b.a(1);
            this.e = true;
            this.f = false;
            DeviceUtil.a();
        } else {
            this.e = false;
        }
        this.b++;
        com.xiaomi.onetrack.util.p.a("OneTrackImp", "onActivityStarted: " + activity.getLocalClassName());
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        this.a.e(this.f);
        this.c = System.identityHashCode(activity);
        this.d = SystemClock.elapsedRealtime();
        this.a.a(activity.getClass().getName(), this.e);
        if (com.xiaomi.onetrack.util.p.a) {
            com.xiaomi.onetrack.util.p.a("OneTrackImp", "onActivityResumed:" + activity.getLocalClassName() + " isAppStart:" + this.e);
        }
        this.e = false;
        this.a.d();
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
        this.a.a(activity.getClass().getName(), this.c == System.identityHashCode(activity) ? SystemClock.elapsedRealtime() - this.d : 0L);
        if (com.xiaomi.onetrack.util.p.a) {
            com.xiaomi.onetrack.util.p.a("OneTrackImp", "onActivityPaused:" + activity.getLocalClassName());
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
        int i = this.b - 1;
        this.b = i;
        if (i == 0) {
            this.a.b.a(2);
            this.a.j();
            this.f = true;
            this.e = false;
        } else {
            this.f = false;
        }
        this.a.e(this.f);
        com.xiaomi.onetrack.util.p.a("OneTrackImp", "onActivityStopped: " + activity.getLocalClassName());
    }
}
