package com.android.deskclock.util.stat;

import android.content.Context;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public interface IOneTrackStat {
    void init(Context context);

    void release();

    void track(String str, String str2);

    void track(String str, Map<String, Object> map, String str2);

    void trackPageEnd(String str);

    void trackPageStart(String str);

    void trackWithEnvironment(String str);
}
