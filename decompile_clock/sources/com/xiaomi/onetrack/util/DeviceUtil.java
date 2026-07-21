package com.xiaomi.onetrack.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.xiaomi.onetrack.api.as;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class DeviceUtil {
    static final int a = 15;
    static final int b = 6;
    private static final String c = "DeviceUtil";
    private static final String d = "";
    private static final int e = 15;
    private static Method f = null;
    private static Method g = null;
    private static Object h = null;
    private static Method i = null;
    private static Method j = null;
    private static volatile String k = null;
    private static volatile String l = null;
    private static String m = null;
    private static String n = null;
    private static String o = null;
    private static volatile boolean p = false;

    private static boolean g() {
        return false;
    }

    static {
        try {
            f = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
        } catch (Throwable th) {
            p.b(c, "sGetProp init failed ex: " + th.getMessage());
        }
        try {
            Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
            h = cls.getMethod("getDefault", new Class[0]).invoke(null, new Object[0]);
            g = cls.getMethod("getImeiList", new Class[0]);
            j = cls.getMethod("getSubscriberIdForSlot", Integer.TYPE);
        } catch (Throwable th2) {
            p.b(c, "TelephonyManagerEx init failed ex: " + th2.getMessage());
        }
        try {
            i = Class.forName("android.telephony.TelephonyManager").getMethod("getImei", Integer.TYPE);
        } catch (Throwable th3) {
            p.b(c, "sGetImeiForSlot init failed ex: " + th3.getMessage());
        }
    }

    public static String a(Context context) {
        if (!TextUtils.isEmpty(k)) {
            return k;
        }
        g(context);
        if (!TextUtils.isEmpty(k)) {
            return k;
        }
        return "";
    }

    public static String b(Context context) {
        if (!TextUtils.isEmpty(n)) {
            return n;
        }
        String strA = a(context);
        if (!TextUtils.isEmpty(strA)) {
            String strC = com.xiaomi.onetrack.d.d.c(strA);
            n = strC;
            return strC;
        }
        return "";
    }

    public static String c(Context context) {
        if (!TextUtils.isEmpty(l)) {
            return l;
        }
        g(context);
        if (!TextUtils.isEmpty(l)) {
            return l;
        }
        return "";
    }

    public static String d(Context context) {
        if (!TextUtils.isEmpty(o)) {
            return o;
        }
        String strC = c(context);
        if (!TextUtils.isEmpty(strC)) {
            String strC2 = com.xiaomi.onetrack.d.d.c(strC);
            o = strC2;
            return strC2;
        }
        return "";
    }

    public static String e(Context context) {
        if (!TextUtils.isEmpty(m)) {
            return m;
        }
        if (GAIDClient.b(context)) {
            return "";
        }
        String strA = GAIDClient.a(context);
        if (TextUtils.isEmpty(strA)) {
            return "";
        }
        m = strA;
        return strA;
    }

    public static void a() {
        m = null;
    }

    public static List<String> f(Context context) {
        List<String> listG = g(context);
        ArrayList arrayList = new ArrayList();
        if (listG != null && !listG.isEmpty()) {
            for (int i2 = 0; i2 < listG.size(); i2++) {
                if (!TextUtils.isEmpty(listG.get(i2))) {
                    arrayList.add(i2, com.xiaomi.onetrack.d.d.c(listG.get(i2)));
                }
            }
        }
        return arrayList;
    }

    public static List<String> g(Context context) {
        List<String> listK = null;
        if (u.a(context)) {
            if (p) {
                return null;
            }
            List<String> listE = e();
            listK = (listE == null || listE.isEmpty()) ? k(context) : listE;
            p = true;
        }
        if (listK != null && !listK.isEmpty()) {
            Collections.sort(listK);
            k = listK.get(0);
            if (listK.size() >= 2) {
                l = listK.get(1);
            }
        }
        return listK;
    }

    private static List<String> e() {
        if (g == null || g()) {
            return null;
        }
        try {
            List<String> list = (List) g.invoke(h, new Object[0]);
            if (list == null || list.size() <= 0 || a(list)) {
                return null;
            }
            return list;
        } catch (Exception e2) {
            p.a(c, "getImeiListFromMiui failed ex: " + e2.getMessage());
            return null;
        }
    }

    private static List<String> k(Context context) {
        if (i == null) {
            return null;
        }
        try {
            ArrayList arrayList = new ArrayList();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(as.d);
            String str = (String) i.invoke(telephonyManager, 0);
            if (b(str)) {
                arrayList.add(str);
            }
            if (f()) {
                String str2 = (String) i.invoke(telephonyManager, 1);
                if (b(str2)) {
                    arrayList.add(str2);
                }
            }
            return arrayList;
        } catch (Exception e2) {
            p.a(c, "getImeiListAboveLollipop failed ex: " + e2.getMessage());
            return null;
        }
    }

    private static List<String> l(Context context) {
        try {
            ArrayList arrayList = new ArrayList();
            Class<?> cls = Class.forName("android.telephony.TelephonyManager");
            if (!f()) {
                String deviceId = ((TelephonyManager) cls.getMethod("getDefault", new Class[0]).invoke(null, new Object[0])).getDeviceId();
                if (b(deviceId)) {
                    arrayList.add(deviceId);
                }
                return arrayList;
            }
            String deviceId2 = ((TelephonyManager) cls.getMethod("getDefault", Integer.TYPE).invoke(null, 0)).getDeviceId();
            String deviceId3 = ((TelephonyManager) cls.getMethod("getDefault", Integer.TYPE).invoke(null, 1)).getDeviceId();
            if (b(deviceId2)) {
                arrayList.add(deviceId2);
            }
            if (b(deviceId3)) {
                arrayList.add(deviceId3);
            }
            return arrayList;
        } catch (Throwable th) {
            p.a(c, "getImeiListBelowLollipop failed ex: " + th.getMessage());
            return null;
        }
    }

    public static List<String> h(Context context) {
        String str;
        String str2;
        if (!u.b(context)) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        try {
            if (f()) {
                Class<?> cls = Class.forName("android.telephony.TelephonyManager");
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(as.d);
                SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service");
                Class<?> cls2 = Class.forName("android.telephony.SubscriptionManager");
                if (Build.VERSION.SDK_INT < 29) {
                    str = a(cls, cls2, telephonyManager, subscriptionManager)[0];
                    str2 = a(cls, cls2, telephonyManager, subscriptionManager)[1];
                } else {
                    str = b(cls, cls2, telephonyManager, subscriptionManager)[0];
                    str2 = b(cls, cls2, telephonyManager, subscriptionManager)[1];
                }
                if (!c(str)) {
                    str = "";
                }
                arrayList.add(str);
                if (!c(str2)) {
                    str2 = "";
                }
                arrayList.add(str2);
                return arrayList;
            }
            String subscriberId = ((TelephonyManager) context.getSystemService(as.d)).getSubscriberId();
            if (c(subscriberId)) {
                arrayList.add(subscriberId);
            }
            return arrayList;
        } catch (SecurityException unused) {
            p.a(c, "getImsiList failed with on permission");
            return null;
        } catch (Throwable th) {
            p.b(c, "getImsiList failed: " + th.getMessage());
            return null;
        }
    }

    private static boolean f() {
        if ("dsds".equals(a("persist.radio.multisim.config"))) {
            return true;
        }
        String str = Build.DEVICE;
        return "lcsh92_wet_jb9".equals(str) || "lcsh92_wet_tdd".equals(str) || "HM2013022".equals(str) || "HM2013023".equals(str) || "armani".equals(str) || "HM2014011".equals(str) || "HM2014012".equals(str);
    }

    private static String a(String str) {
        try {
            Method method = f;
            if (method != null) {
                return String.valueOf(method.invoke(null, str));
            }
        } catch (Exception e2) {
            p.a(c, "getProp failed ex: " + e2.getMessage());
        }
        return null;
    }

    private static boolean a(List<String> list) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (!b(it.next())) {
                return true;
            }
        }
        return false;
    }

    private static boolean b(String str) {
        return (str == null || str.length() != 15 || str.matches("^0*$")) ? false : true;
    }

    private static class GAIDClient {
        private static final String a = "GAIDClient";

        private GAIDClient() {
        }

        static String a(Context context) {
            if (!c(context)) {
                p.a(a, "Google play service is not available");
                return "";
            }
            AdvertisingConnection advertisingConnection = new AdvertisingConnection();
            try {
                try {
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    if (context.bindService(intent, advertisingConnection, 1)) {
                        return new a(advertisingConnection.a()).a();
                    }
                } catch (Exception e) {
                    p.a(a, "Query Google ADID failed ", e);
                }
                return "";
            } finally {
                context.unbindService(advertisingConnection);
            }
        }

        static boolean b(Context context) {
            if (!c(context)) {
                p.a(a, "Google play service is not available");
                return false;
            }
            AdvertisingConnection advertisingConnection = new AdvertisingConnection();
            try {
                try {
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    if (context.bindService(intent, advertisingConnection, 1)) {
                        return new a(advertisingConnection.a()).a(true);
                    }
                } catch (Exception e) {
                    p.a(a, "Query Google isLimitAdTrackingEnabled failed ", e);
                }
                return false;
            } finally {
                context.unbindService(advertisingConnection);
            }
        }

        private static boolean c(Context context) {
            try {
                context.getPackageManager().getPackageInfo("com.android.vending", 16384);
                return true;
            } catch (PackageManager.NameNotFoundException unused) {
                return false;
            }
        }

        private static final class AdvertisingConnection implements ServiceConnection {
            private static final int a = 30000;
            private boolean b;
            private IBinder c;

            private AdvertisingConnection() {
                this.b = false;
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                synchronized (this) {
                    this.c = iBinder;
                    notifyAll();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                this.b = true;
                this.c = null;
            }

            public IBinder a() throws InterruptedException {
                IBinder iBinder = this.c;
                if (iBinder != null) {
                    return iBinder;
                }
                if (iBinder == null && !this.b) {
                    synchronized (this) {
                        wait(30000L);
                        if (this.c == null) {
                            throw new InterruptedException("Not connect or connect timeout to google play service");
                        }
                    }
                }
                return this.c;
            }
        }

        private static final class a implements IInterface {
            private IBinder a;

            public a(IBinder iBinder) {
                this.a = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.a;
            }

            public String a() throws RemoteException {
                if (this.a == null) {
                    return "";
                }
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                    this.a.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public boolean a(boolean z) throws RemoteException {
                if (this.a == null) {
                    return false;
                }
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.a.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }

    private static boolean c(String str) {
        return str != null && str.length() >= 6 && str.length() <= 15 && !str.matches("^0*$");
    }

    private static String[] a(Class<?> cls, Class<?> cls2, TelephonyManager telephonyManager, SubscriptionManager subscriptionManager) {
        Method method;
        Object obj;
        String[] strArr = new String[2];
        try {
            String str = (String) cls.getMethod("getSubscriberId", Integer.TYPE).invoke(telephonyManager, Integer.valueOf(((int[]) cls2.getMethod("getSubId", Integer.TYPE).invoke(subscriptionManager, 0))[0]));
            strArr[0] = str;
            if (c(str) || (method = j) == null || (obj = h) == null) {
                String str2 = (String) cls.getMethod("getSubscriberId", Integer.TYPE).invoke(telephonyManager, Integer.valueOf(((int[]) cls2.getMethod("getSubId", Integer.TYPE).invoke(subscriptionManager, 1))[0]));
                strArr[1] = str2;
            } else {
                strArr[0] = (String) method.invoke(obj, 0);
                strArr[1] = (String) j.invoke(h, 1);
            }
        } catch (Exception e2) {
            p.a(c, "getImsiFromLToP: " + e2);
        }
        return strArr;
    }

    private static String[] b(Class<?> cls, Class<?> cls2, TelephonyManager telephonyManager, SubscriptionManager subscriptionManager) {
        String[] strArr = new String[2];
        try {
            int[] iArr = (int[]) cls2.getMethod("getSubscriptionIds", Integer.TYPE).invoke(subscriptionManager, 0);
            if (iArr != null) {
                String str = (String) cls.getMethod("getSubscriberId", Integer.TYPE).invoke(telephonyManager, Integer.valueOf(iArr[0]));
                strArr[0] = str;
            }
        } catch (Exception e2) {
            p.b(c, "get imsi1 above Android Q exception:" + e2);
        }
        try {
            int[] iArr2 = (int[]) cls2.getMethod("getSubscriptionIds", Integer.TYPE).invoke(subscriptionManager, 1);
            if (iArr2 != null) {
                String str2 = (String) cls.getMethod("getSubscriberId", Integer.TYPE).invoke(telephonyManager, Integer.valueOf(iArr2[0]));
                strArr[1] = str2;
            }
        } catch (Exception e3) {
            p.b(c, "get imsi2 above Android Q exception:" + e3);
        }
        return strArr;
    }

    public static String i(Context context) {
        try {
            List<String> listH = h(context);
            if (listH != null) {
                for (int i2 = 0; i2 < listH.size(); i2++) {
                    listH.set(i2, com.xiaomi.onetrack.d.d.h(listH.get(i2)));
                }
                return listH.toString();
            }
            return "";
        } catch (Throwable th) {
            p.b(p.a(c), "getImeiListMd5 failed!", th);
            return "";
        }
    }

    public static String b() {
        return Build.MODEL;
    }

    public static String c() {
        return a("ro.product.marketname");
    }

    public static String d() {
        return Build.MANUFACTURER;
    }

    public static String j(Context context) {
        return com.xiaomi.onetrack.util.oaid.a.a().a(context);
    }
}
