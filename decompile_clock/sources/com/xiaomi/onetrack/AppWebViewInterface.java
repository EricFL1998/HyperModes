package com.xiaomi.onetrack;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import com.xiaomi.onetrack.util.p;

/* JADX INFO: loaded from: classes2.dex */
public class AppWebViewInterface {
    public static final String JAVASCRIPT_INTERFACE_NAME = "OneTrack_APP_H5_Bridge";
    private static final String a = "AppWebViewInterface";
    private OneTrack b;

    public AppWebViewInterface(OneTrack oneTrack) {
        this.b = oneTrack;
    }

    @JavascriptInterface
    public boolean track(String str) {
        p.a(a, "received h5 data. data: " + str);
        if (this.b == null) {
            p.a(a, "mOneTrack is null, return false");
            return false;
        }
        if (TextUtils.isEmpty(str)) {
            p.a(a, "h5 data is empty, return false");
            return false;
        }
        this.b.trackEventFromH5(str);
        return true;
    }
}
