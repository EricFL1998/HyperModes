package miuix.navigator.navigatorinfo;

import android.os.Bundle;
import androidx.fragment.app.FragmentHelper;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import miuix.appcompat.app.Fragment;
import miuix.navigator.FragmentAnimationHelper;
import miuix.navigator.Navigator;
import miuix.navigator.NavigatorImpl;
import miuix.navigator.R;
import miuix.view.EditActionMode;

/* JADX INFO: loaded from: classes3.dex */
public class DetailFragmentNavInfo extends AbstractFragmentNavInfo {
    private boolean mSaveToBackStack;

    public DetailFragmentNavInfo(int i, Class<? extends Fragment> cls, Bundle bundle) {
        super(i, cls, bundle);
    }

    public DetailFragmentNavInfo(int i, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        this(i, cls, bundle);
        this.mSaveToBackStack = z;
    }

    public boolean isSaveToBackStack() {
        return this.mSaveToBackStack;
    }

    @Override // miuix.navigator.navigatorinfo.AbstractFragmentNavInfo, miuix.navigator.navigatorinfo.NavigatorInfo
    public boolean onNavigate(Navigator navigator) {
        boolean z;
        boolean z2;
        Navigator byTag = navigator.getByTag(Navigator.TAG_SECONDARY_CONTENT);
        FragmentManager fragmentManager = byTag.getFragmentManager();
        boolean z3 = fragmentManager.getBackStackEntryCount() == 0;
        Navigator.Mode navigationMode = navigator.getNavigationMode();
        boolean z4 = navigationMode == Navigator.Mode.NLC || navigationMode == Navigator.Mode.LC;
        if (isSaveToBackStack() || z3) {
            z = z3;
            z2 = false;
        } else {
            FragmentHelper.cleanBackStackAllowingStateLoss(fragmentManager);
            z2 = !z4 && FragmentAnimationHelper.isSecondaryOnTopNow(navigator);
            z = true;
        }
        fragmentManager.executePendingTransactions();
        if (navigator != byTag) {
            androidx.fragment.app.Fragment fragmentFindFragmentByTag = navigator.getByTag(Navigator.TAG_CONTENT).getFragmentManager().findFragmentByTag(Navigator.TAG_CONTENT);
            if (fragmentFindFragmentByTag instanceof Fragment) {
                Fragment fragment = (Fragment) fragmentFindFragmentByTag;
                if (fragment.getDelegate().getActionMode() instanceof EditActionMode) {
                    fragment.getDelegate().getActionMode().finish();
                }
            }
        }
        byTag.requestFocus(true);
        if (!byTag.isFocused()) {
            return false;
        }
        FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
        if (z2 || (!isSaveToBackStack() && z4)) {
            fragmentTransactionBeginTransaction.setCustomAnimations(0, 0, R.anim.miuix_appcompat_fragment_transition_activity_close_enter, R.anim.miuix_appcompat_fragment_transition_activity_close_exit);
        } else {
            fragmentTransactionBeginTransaction.setCustomAnimations(R.anim.miuix_appcompat_fragment_transition_activity_open_enter, R.anim.miuix_appcompat_fragment_transition_activity_open_exit, R.anim.miuix_appcompat_fragment_transition_activity_close_enter, R.anim.miuix_appcompat_fragment_transition_activity_close_exit);
        }
        if (isSaveToBackStack() || z) {
            fragmentTransactionBeginTransaction.addToBackStack(null);
        }
        fragmentTransactionBeginTransaction.replace(R.id.secondary_content_decor, getFragment(), getArgs(), Navigator.TAG_SECONDARY_CONTENT);
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
        if (z && !z2) {
            FragmentAnimationHelper.hideContentFragment((NavigatorImpl) navigator.getBaseNavigator());
        }
        return false;
    }
}
