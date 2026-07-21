package com.xiaomi.onetrack.f;

import android.content.Context;
import com.xiaomi.onetrack.util.i;

/* JADX INFO: loaded from: classes2.dex */
public class g {
    private static final String a = "OneTrackApp";
    private static g b;

    public static void a(Context context) {
        if (b == null) {
            b = new g(context);
        }
    }

    private g(Context context) {
        i.a(new h(this, context.getApplicationContext()));
    }
}
