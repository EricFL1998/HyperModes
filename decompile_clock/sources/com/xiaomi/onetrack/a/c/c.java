package com.xiaomi.onetrack.a.c;

import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class c {
    private static final String a = "AdMonitorUploader";

    public static void a() {
        try {
            if (b()) {
                p.a(a, "即将读取数据库并上传数据");
                int i = 0;
                while (i <= 20) {
                    com.xiaomi.onetrack.a.a.a().d();
                    a aVarB = com.xiaomi.onetrack.a.a.a().b();
                    if (aVarB != null && aVarB.b != null && aVarB.b.size() > 0) {
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        for (com.xiaomi.onetrack.a.b.a aVar : aVarB.b) {
                            boolean zB = com.xiaomi.onetrack.g.b.b(aVar.c());
                            int iB = aVar.b();
                            if (zB) {
                                arrayList.add(Integer.valueOf(iB));
                            } else {
                                arrayList2.add(Integer.valueOf(iB));
                            }
                        }
                        if (arrayList.size() > 0) {
                            com.xiaomi.onetrack.a.a.a().a(arrayList);
                        }
                        if (arrayList2.size() > 0) {
                            com.xiaomi.onetrack.a.a.a().b(arrayList2);
                        }
                        i++;
                        if (aVarB.c) {
                            p.a(a, "No more ad monitor records");
                            return;
                        }
                    }
                    p.a(a, "满足条件的adMonitor记录为空，即将返回");
                    return;
                }
            }
        } catch (Throwable th) {
            p.a(a, "uploadData Throwable:" + th.getMessage());
        }
    }

    private static boolean b() {
        if (q.a(a)) {
            p.a(a, "the device is not provisioned, stop poll!");
            return false;
        }
        if (!com.xiaomi.onetrack.g.c.a()) {
            p.a(a, "network is unconnected, stop poll!");
            return false;
        }
        if (com.xiaomi.onetrack.a.a.a().e() != 0) {
            return true;
        }
        p.a(a, "no data remain in db, stop poll!");
        return false;
    }
}
