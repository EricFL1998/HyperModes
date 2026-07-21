package miuix.navigator.navigatorinfo;

import android.os.Bundle;
import miuix.appcompat.app.Fragment;
import miuix.navigator.Navigator;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractFragmentNavInfo extends NavigatorInfo {
    private final Bundle mArgs;
    private final Class<? extends Fragment> mFragment;

    @Override // miuix.navigator.navigatorinfo.NavigatorInfo
    public abstract boolean onNavigate(Navigator navigator);

    @Override // miuix.navigator.navigatorinfo.NavigatorInfo
    public boolean shouldCloseOverlay() {
        return true;
    }

    public AbstractFragmentNavInfo(int i, Class<? extends Fragment> cls, Bundle bundle) {
        super(i);
        this.mFragment = cls;
        this.mArgs = bundle;
    }

    public Class<? extends Fragment> getFragment() {
        return this.mFragment;
    }

    public Bundle getArgs() {
        return this.mArgs;
    }
}
