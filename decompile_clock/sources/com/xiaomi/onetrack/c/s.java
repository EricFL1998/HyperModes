package com.xiaomi.onetrack.c;

import android.os.HandlerThread;
import android.text.TextUtils;
import com.xiaomi.onetrack.util.x;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class s {
    public static final String a = "config";
    public static final String b = "appId";
    public static final String c = "version";
    private static final String d = "UploaderEngine";
    private static final String e = "code";
    private static final String f = "UTF-8";
    private p g;

    private s() {
        b();
    }

    private static class a {
        private static final s a = new s();

        private a() {
        }
    }

    public static s a() {
        return a.a;
    }

    public synchronized void a(int i, boolean z) {
        p pVar = this.g;
        if (pVar != null) {
            pVar.a(i, z);
        } else {
            com.xiaomi.onetrack.util.p.b(d, "*** impossible, upload timer should not be null");
        }
    }

    public void a(boolean z) {
        p pVar = this.g;
        if (pVar != null) {
            pVar.a(z);
        } else {
            com.xiaomi.onetrack.util.p.b(d, "*** impossible, upload timer should not be null");
        }
    }

    public boolean a(int i) {
        h hVarA;
        com.xiaomi.onetrack.util.p.a(d, "即将读取数据库并上传数据");
        do {
            hVarA = c.a().a(i);
            if (hVarA == null) {
                com.xiaomi.onetrack.util.p.a(d, "满足条件的记录为空，即将返回, priority=" + i);
                return true;
            }
            ArrayList<Long> arrayList = hVarA.c;
            boolean zA = a(hVarA.a);
            com.xiaomi.onetrack.util.p.a(d, "upload success:" + zA);
            if (!zA) {
                return false;
            }
            if (c.a().a(arrayList) == 0) {
                com.xiaomi.onetrack.util.p.b(d, "delete DB failed!", new Throwable());
            }
            return true;
        } while (!hVarA.d);
        com.xiaomi.onetrack.util.p.a(d, "No more records for prio=" + i);
        return true;
    }

    private boolean a(JSONArray jSONArray) {
        try {
            String strB = x.a().b();
            String string = jSONArray.toString();
            com.xiaomi.onetrack.util.p.a(d, " payload:" + string);
            byte[] bArrA = a(a(string));
            com.xiaomi.onetrack.util.p.a(d, "before zip and encrypt, len=" + string.length() + ", after=" + bArrA.length);
            String strA = com.xiaomi.onetrack.g.b.a(strB, bArrA);
            com.xiaomi.onetrack.util.p.a(d, "sendDataToServer response: " + strA);
            if (TextUtils.isEmpty(strA)) {
                return false;
            }
            return b(strA);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(d, "Exception while uploading ", e2);
            return false;
        }
    }

    private void b() {
        HandlerThread handlerThread = new HandlerThread("onetrack_uploader_worker");
        handlerThread.start();
        this.g = new p(handlerThread.getLooper());
    }

    private static byte[] a(String str) throws Throwable {
        GZIPOutputStream gZIPOutputStream;
        ByteArrayOutputStream byteArrayOutputStream;
        ByteArrayOutputStream byteArrayOutputStream2 = null;
        byte[] byteArray = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream(str.getBytes(f).length);
            try {
                gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                try {
                    try {
                        gZIPOutputStream.write(str.getBytes(f));
                        gZIPOutputStream.finish();
                        byteArray = byteArrayOutputStream.toByteArray();
                    } catch (Throwable th) {
                        th = th;
                        byteArrayOutputStream2 = byteArrayOutputStream;
                        com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream2);
                        com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
                        throw th;
                    }
                } catch (Exception e2) {
                    e = e2;
                    com.xiaomi.onetrack.util.p.b(d, " zipData failed! " + e.toString());
                }
            } catch (Exception e3) {
                e = e3;
                gZIPOutputStream = null;
            } catch (Throwable th2) {
                th = th2;
                gZIPOutputStream = null;
                byteArrayOutputStream2 = byteArrayOutputStream;
                com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream2);
                com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            byteArrayOutputStream = null;
            gZIPOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            gZIPOutputStream = null;
            com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream2);
            com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
            throw th;
        }
        com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream);
        com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
        return byteArray;
    }

    private byte[] a(byte[] bArr) {
        if (bArr == null) {
            com.xiaomi.onetrack.util.p.b(d, "content is null");
            return null;
        }
        return com.xiaomi.onetrack.d.a.a(bArr, com.xiaomi.onetrack.d.c.a(com.xiaomi.onetrack.d.f.a().b()[0]));
    }

    private boolean b(String str) {
        boolean z = false;
        try {
            JSONObject jSONObject = new JSONObject(str);
            int iOptInt = jSONObject.optInt("code");
            if (iOptInt == 0) {
                com.xiaomi.onetrack.util.p.a(d, "成功发送数据到服务端");
                com.xiaomi.onetrack.b.a.a().a(jSONObject);
                z = true;
            } else if (iOptInt == -3) {
                com.xiaomi.onetrack.util.p.b(d, "signature expired, will update");
                com.xiaomi.onetrack.d.f.a().c();
            } else {
                com.xiaomi.onetrack.util.p.b(d, "Error: status code=" + iOptInt);
            }
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(d, "parseUploadingResult exception ", e2);
        }
        return z;
    }
}
