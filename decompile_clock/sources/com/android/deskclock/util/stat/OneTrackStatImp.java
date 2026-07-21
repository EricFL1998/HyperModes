package com.android.deskclock.util.stat;

import android.content.Context;
import android.text.TextUtils;
import com.android.deskclock.util.Log;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.api.g;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class OneTrackStatImp implements IOneTrackStat {
    private static final String APP_ID = "31000000377";
    private static final String CHANNEL = "miui";
    private static String TAG = "DC:OneTrackImpl";
    private boolean mInit = false;
    private OneTrack sOneTrack;

    @Override // com.android.deskclock.util.stat.IOneTrackStat
    public void trackPageEnd(String str) {
    }

    @Override // com.android.deskclock.util.stat.IOneTrackStat
    public void trackPageStart(String str) {
    }

    @Override // com.android.deskclock.util.stat.IOneTrackStat
    public void trackWithEnvironment(String str) {
    }

    @Override // com.android.deskclock.util.stat.IOneTrackStat
    public void init(Context context) {
        if (this.mInit) {
            return;
        }
        try {
            this.sOneTrack = OneTrack.createInstance(context, new Configuration.Builder().setAppId(APP_ID).setChannel("miui").setMode(OneTrack.Mode.APP).setInternational(false).setExceptionCatcherEnable(true).setUseCustomPrivacyPolicy(false).build());
            this.mInit = true;
        } catch (Exception e) {
            Log.e("StatHelper init error", e);
        }
        Log.i("MiState has been initialized");
    }

    @Override // com.android.deskclock.util.stat.IOneTrackStat
    public void release() {
        if (this.mInit) {
            try {
                Log.i(TAG, "OneTrack release");
                this.mInit = false;
                OneTrack.setDisable(true);
            } catch (Exception unused) {
            }
        }
    }

    @Override // com.android.deskclock.util.stat.IOneTrackStat
    public void track(String str, Map<String, Object> map, String str2) {
        if (this.mInit && map != null) {
            try {
                if (!TextUtils.isEmpty(str2)) {
                    map.put(g.ac, str2);
                }
                this.sOneTrack.track(str, map);
            } catch (Exception unused) {
                Log.e(TAG, "trackEvent error");
            }
        }
    }

    @Override // com.android.deskclock.util.stat.IOneTrackStat
    public void track(String str, String str2) {
        if (this.mInit) {
            try {
                if (!TextUtils.isEmpty(str2)) {
                    HashMap map = new HashMap();
                    map.put(g.ac, str2);
                    this.sOneTrack.track(str, map);
                } else {
                    this.sOneTrack.track(str, null);
                }
            } catch (Exception unused) {
                Log.e(TAG, "trackEvent error");
            }
        }
    }
}
