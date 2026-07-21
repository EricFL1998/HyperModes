package miuix.navigator.navigatorinfo;

import android.content.Context;
import miuix.core.util.EnvStateManager;
import miuix.navigator.Navigator;

/* JADX INFO: loaded from: classes3.dex */
public class AutoNavInfo extends NavigatorInfo {
    private final ActivityNavInfo mActivityInfo;
    private final Context mContext;
    private final AbstractFragmentNavInfo mFragmentInfo;

    public AutoNavInfo(int i, Context context, AbstractFragmentNavInfo abstractFragmentNavInfo, ActivityNavInfo activityNavInfo) {
        super(i);
        this.mContext = context;
        this.mFragmentInfo = abstractFragmentNavInfo;
        this.mActivityInfo = activityNavInfo;
    }

    public boolean shouldToActivity(Navigator navigator) {
        return ((int) ((((float) EnvStateManager.getWindowSize(this.mContext).x) * 1.0f) / this.mContext.getResources().getDisplayMetrics().density)) <= 640;
    }

    @Override // miuix.navigator.navigatorinfo.NavigatorInfo
    public boolean onNavigate(Navigator navigator) {
        if (shouldToActivity(navigator)) {
            return this.mActivityInfo.onNavigate(navigator);
        }
        return this.mFragmentInfo.onNavigate(navigator);
    }
}
