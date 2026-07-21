package miuix.navigator.navigatorinfo;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import miuix.appcompat.app.Fragment;
import miuix.navigator.Navigator;

/* JADX INFO: loaded from: classes3.dex */
public class UpdateDetailFragmentNavInfo extends DetailFragmentNavInfo {
    public UpdateDetailFragmentNavInfo(int i, Class<? extends Fragment> cls, Bundle bundle) {
        super(i, cls, bundle);
    }

    public UpdateDetailFragmentNavInfo(int i, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        super(i, cls, bundle, z);
    }

    @Override // miuix.navigator.navigatorinfo.DetailFragmentNavInfo, miuix.navigator.navigatorinfo.AbstractFragmentNavInfo, miuix.navigator.navigatorinfo.NavigatorInfo
    public boolean onNavigate(Navigator navigator) {
        FragmentManager fragmentManager = navigator.getByTag(Navigator.TAG_SECONDARY_CONTENT).getFragmentManager();
        androidx.fragment.app.Fragment fragmentFindFragmentByTag = fragmentManager.findFragmentByTag(Navigator.TAG_SECONDARY_CONTENT);
        if (getFragment().isInstance(fragmentFindFragmentByTag)) {
            if (!fragmentFindFragmentByTag.isStateSaved()) {
                ((Fragment) fragmentFindFragmentByTag).onUpdateArguments(getArgs());
                return false;
            }
            fragmentManager.beginTransaction().remove(fragmentFindFragmentByTag).commitAllowingStateLoss();
        }
        return super.onNavigate(navigator);
    }
}
