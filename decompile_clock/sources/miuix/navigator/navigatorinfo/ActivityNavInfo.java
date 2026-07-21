package miuix.navigator.navigatorinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import miuix.navigator.Navigator;

/* JADX INFO: loaded from: classes3.dex */
public class ActivityNavInfo extends NavigatorInfo {
    private final Context mContext;
    private final Intent mIntent;
    private Bundle mOptions;

    public ActivityNavInfo(Context context, int i, Intent intent) {
        super(i);
        this.mContext = context;
        this.mIntent = intent;
    }

    public void setOptions(Bundle bundle) {
        this.mOptions = bundle;
    }

    @Override // miuix.navigator.navigatorinfo.NavigatorInfo
    public boolean onNavigate(Navigator navigator) {
        this.mContext.startActivity(this.mIntent, this.mOptions);
        return false;
    }
}
