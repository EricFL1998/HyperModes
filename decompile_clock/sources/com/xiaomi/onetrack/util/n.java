package com.xiaomi.onetrack.util;

import android.content.Context;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes2.dex */
public class n {
    private static final String a = "IdentifierManager";
    private static Object b;
    private static Class<?> c;
    private static Method d;
    private static Method e;
    private static Method f;
    private static Method g;

    static {
        try {
            Class<?> cls = Class.forName("com.android.id.impl.IdProviderImpl");
            c = cls;
            b = cls.newInstance();
            d = c.getMethod("getUDID", Context.class);
            e = c.getMethod("getOAID", Context.class);
            f = c.getMethod("getVAID", Context.class);
            g = c.getMethod("getAAID", Context.class);
        } catch (Exception e2) {
            p.a(a, "reflect exception!", e2);
        }
    }

    public static boolean a() {
        return (c == null || b == null) ? false : true;
    }

    public static String a(Context context) {
        return a(context, d);
    }

    public static String b(Context context) {
        return a(context, e);
    }

    public static String c(Context context) {
        return a(context, f);
    }

    public static String d(Context context) {
        return a(context, g);
    }

    private static String a(Context context, Method method) {
        Object obj = b;
        if (obj != null && method != null) {
            try {
                Object objInvoke = method.invoke(obj, context);
                if (objInvoke != null) {
                    return (String) objInvoke;
                }
                return "";
            } catch (Exception e2) {
                p.a(a, "invoke exception!", e2);
                return "";
            }
        }
        return "";
    }
}
