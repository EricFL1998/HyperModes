package miuix.navigator.navigatorinfo;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import miuix.appcompat.app.Fragment;
import miuix.navigator.Navigator;

/* JADX INFO: loaded from: classes3.dex */
public class UpdateFragmentNavInfo extends FragmentNavInfo {
    private final boolean mBringToFront;

    public UpdateFragmentNavInfo(int i, Class<? extends Fragment> cls, Bundle bundle) {
        this(i, cls, bundle, false);
    }

    public UpdateFragmentNavInfo(int i, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        super(i, cls, bundle);
        this.mBringToFront = z;
    }

    @Override // miuix.navigator.navigatorinfo.FragmentNavInfo, miuix.navigator.navigatorinfo.AbstractFragmentNavInfo, miuix.navigator.navigatorinfo.NavigatorInfo
    public boolean onNavigate(Navigator navigator) {
        if (Navigator.TAG_SECONDARY_CONTENT.equals(navigator.getTag())) {
            return false;
        }
        Navigator byTag = navigator.getByTag(Navigator.TAG_CONTENT);
        FragmentManager fragmentManager = byTag.getFragmentManager();
        androidx.fragment.app.Fragment fragmentFindFragmentByTag = fragmentManager.findFragmentByTag(Navigator.TAG_CONTENT);
        if (getFragment().isInstance(fragmentFindFragmentByTag)) {
            if (this.mBringToFront) {
                byTag.requestFocus(true);
            }
            if (!fragmentFindFragmentByTag.isStateSaved()) {
                ((Fragment) fragmentFindFragmentByTag).onUpdateArguments(getArgs());
                return true;
            }
            fragmentManager.beginTransaction().remove(fragmentFindFragmentByTag).commitAllowingStateLoss();
        }
        return super.onNavigate(navigator);
    }
}
