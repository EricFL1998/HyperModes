package androidx.fragment.app;

/* JADX INFO: loaded from: classes.dex */
public class FragmentHelper {
    private FragmentHelper() {
    }

    public static void cleanBackStackAllowingStateLoss(FragmentManager fragmentManager) {
        fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).getId(), 1, true);
    }
}
