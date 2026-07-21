package miuix.navigator.navigatorinfo;

import android.os.Bundle;
import miuix.appcompat.app.Fragment;
import miuix.navigator.Navigator;
import miuix.navigator.R;

/* JADX INFO: loaded from: classes3.dex */
public class FragmentNavInfo extends AbstractFragmentNavInfo {
    public FragmentNavInfo(int i, Class<? extends Fragment> cls, Bundle bundle) {
        super(i, cls, bundle);
    }

    @Override // miuix.navigator.navigatorinfo.AbstractFragmentNavInfo, miuix.navigator.navigatorinfo.NavigatorInfo
    public boolean onNavigate(Navigator navigator) {
        if (Navigator.TAG_SECONDARY_CONTENT.equals(navigator.getTag())) {
            return false;
        }
        Navigator byTag = navigator.getByTag(Navigator.TAG_CONTENT);
        byTag.getFragmentManager().executePendingTransactions();
        byTag.requestFocus(true);
        if (!byTag.isFocused()) {
            return false;
        }
        byTag.getFragmentManager().beginTransaction().replace(R.id.content_decor, getFragment(), getArgs(), Navigator.TAG_CONTENT).commitAllowingStateLoss();
        return true;
    }
}
