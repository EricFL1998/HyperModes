package com.xiaomi.onetrack.b;

import android.os.Message;
import android.text.TextUtils;
import com.xiaomi.onetrack.c.s;
import com.xiaomi.onetrack.util.p;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class c implements Runnable {
    final /* synthetic */ JSONObject a;
    final /* synthetic */ a b;

    c(a aVar, JSONObject jSONObject) {
        this.b = aVar;
        this.a = jSONObject;
    }

    @Override // java.lang.Runnable
    public void run() {
        p.a("AppConfigUpdater", "checkAppConfigVersion start");
        JSONArray jSONArrayOptJSONArray = this.a.optJSONArray(s.a);
        if (jSONArrayOptJSONArray != null) {
            for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i);
                String strOptString = jSONObjectOptJSONObject == null ? "" : jSONObjectOptJSONObject.optString(s.b);
                p.a("AppConfigUpdater", "appId: " + strOptString);
                if (!TextUtils.isEmpty(strOptString)) {
                    int iOptInt = jSONObjectOptJSONObject == null ? 0 : jSONObjectOptJSONObject.optInt("version");
                    int iE = h.a().e(strOptString);
                    boolean zH = a.h(strOptString);
                    boolean z = a.A.containsKey(strOptString) && ((Boolean) a.A.get(strOptString)).booleanValue();
                    p.a("AppConfigUpdater", "local version: " + iE + ", server version: " + iOptInt + ", canUpdate: " + zH + ", isScheduling: " + z);
                    if (iOptInt > 0 && iOptInt > iE && zH && !z) {
                        Message messageObtain = Message.obtain();
                        messageObtain.what = 0;
                        messageObtain.obj = strOptString;
                        long jRandom = (long) (Math.random() * 1800000.0d);
                        p.a("AppConfigUpdater", "the message will be handled after " + jRandom + " ms");
                        a.y.sendMessageDelayed(messageObtain, jRandom);
                        a.A.put(strOptString, true);
                    }
                }
            }
        }
    }
}
