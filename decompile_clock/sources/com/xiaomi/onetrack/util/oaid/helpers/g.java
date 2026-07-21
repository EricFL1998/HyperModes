package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.Context;
import com.xiaomi.onetrack.util.p;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class g {
    private static final String b = "MsaSDKHelper";
    public final LinkedBlockingQueue<a> a = new LinkedBlockingQueue<>(1);

    public String a(Context context) {
        try {
            Class<?> cls = Class.forName("com.bun.miitmdid.core.MdidSdkHelper");
            Class<?> cls2 = Class.forName("com.bun.supplier.IIdentifierListener");
            cls.getDeclaredMethod("InitSdk", Context.class, Boolean.TYPE, cls2).invoke(cls, context, true, Proxy.newProxyInstance(context.getClassLoader(), new Class[]{cls2}, new b()));
            a aVarPoll = this.a.poll(1L, TimeUnit.SECONDS);
            if (aVarPoll == null) {
                return "";
            }
            return aVarPoll.c[1].getClass().getMethod("getOAID", new Class[0]).invoke(aVarPoll.c[1], new Object[0]).toString();
        } catch (Throwable th) {
            p.a(b, th.getMessage());
            return "";
        }
    }

    public class b implements InvocationHandler {
        public b() {
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
            try {
                g.this.a.offer(g.this.new a(obj, method, objArr), 1L, TimeUnit.SECONDS);
                return null;
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                    return null;
                } catch (Exception e2) {
                    p.a(g.b, e2.getMessage());
                    return null;
                }
            }
        }
    }

    private class a {
        Object a;
        Method b;
        Object[] c;

        public a(Object obj, Method method, Object[] objArr) {
            this.a = obj;
            this.b = method;
            this.c = objArr;
        }
    }
}
