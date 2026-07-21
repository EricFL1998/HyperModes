package miuix.navigator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import miuix.view.EditActionMode;

/* JADX INFO: loaded from: classes3.dex */
public class FragmentAnimationHelper {
    private FragmentAnimationHelper() {
    }

    public static void hideContentFragment(NavigatorImpl navigatorImpl) {
        FragmentManager fragmentManager;
        Fragment fragmentFindFragmentByTag;
        Navigator byTag = navigatorImpl.getByTag(Navigator.TAG_CONTENT);
        if (navigatorImpl.getNavigationView() == null || !navigatorImpl.getNavigationView().isSecondaryOnTopNow() || (fragmentFindFragmentByTag = (fragmentManager = byTag.getFragmentManager()).findFragmentByTag(Navigator.TAG_CONTENT)) == null || fragmentFindFragmentByTag.isStateSaved()) {
            return;
        }
        if (fragmentFindFragmentByTag instanceof miuix.appcompat.app.Fragment) {
            miuix.appcompat.app.Fragment fragment = (miuix.appcompat.app.Fragment) fragmentFindFragmentByTag;
            if (fragment.getDelegate().getActionMode() instanceof EditActionMode) {
                fragment.getDelegate().getActionMode().finish();
            }
        }
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.miuix_appcompat_fragment_transition_activity_close_enter, R.anim.miuix_appcompat_fragment_transition_activity_open_exit).hide(fragmentFindFragmentByTag).commit();
    }

    public static void showContentFragment(NavigatorImpl navigatorImpl) {
        FragmentManager fragmentManager;
        Fragment fragmentFindFragmentByTag;
        Navigator byTag = navigatorImpl.getByTag(Navigator.TAG_CONTENT);
        if (navigatorImpl.getNavigationView() == null || !navigatorImpl.getNavigationView().isSecondaryOnTopNow() || (fragmentFindFragmentByTag = (fragmentManager = byTag.getFragmentManager()).findFragmentByTag(Navigator.TAG_CONTENT)) == null || fragmentFindFragmentByTag.isStateSaved()) {
            return;
        }
        if (!fragmentFindFragmentByTag.isHidden()) {
            fragmentManager.beginTransaction().hide(fragmentFindFragmentByTag).commitNowAllowingStateLoss();
        }
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.miuix_appcompat_fragment_transition_activity_close_enter, R.anim.miuix_appcompat_fragment_transition_activity_open_exit).show(fragmentFindFragmentByTag).commit();
    }

    public static boolean isSecondaryOnTopNow(Navigator navigator) {
        return ((NavigatorImpl) navigator.getBaseNavigator()).getNavigationView().isSecondaryOnTopNow();
    }
}
