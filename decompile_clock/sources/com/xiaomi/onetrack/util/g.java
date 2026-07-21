package com.xiaomi.onetrack.util;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import java.util.HashMap;

/* JADX INFO: loaded from: classes2.dex */
class g implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ String b;
    final /* synthetic */ String c;
    final /* synthetic */ d d;

    g(d dVar, String str, String str2, String str3) {
        this.d = dVar;
        this.a = str;
        this.b = str2;
        this.c = str3;
    }

    @Override // java.lang.Runnable
    public void run() {
        String str;
        try {
            if (TextUtils.isEmpty(this.a)) {
                return;
            }
            if (this.a.contains("http://") || this.a.contains("https://")) {
                str = this.a + "/api/open/device/writeBack";
            } else {
                str = "https://" + this.a + "/api/open/device/writeBack";
            }
            HashMap map = new HashMap();
            map.put("instanceId", o.a().b());
            map.put("imei", DeviceUtil.b(this.d.j));
            map.put("oaid", com.xiaomi.onetrack.util.oaid.a.a().a(this.d.j));
            map.put("projectId", this.b);
            map.put("user", this.c);
            String strB = com.xiaomi.onetrack.g.b.b(str, map, false);
            if (!TextUtils.isEmpty(strB) && !"".equals(strB)) {
                this.d.b(strB);
                return;
            }
            Message messageObtain = Message.obtain();
            messageObtain.what = 100;
            Bundle bundle = new Bundle();
            bundle.putString("hint", "注册信息失败，请检查是网络环境是否在内网");
            messageObtain.setData(bundle);
            this.d.k.sendMessage(messageObtain);
        } catch (Exception e) {
            p.b(d.a, e.getMessage());
        }
    }
}
