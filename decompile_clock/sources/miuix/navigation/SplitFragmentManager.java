package miuix.navigation;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SplitFragmentManager {
    private static final String FRAGMENT_TAG_PREFIX = "miuix_";
    private SplitContainerFragment mContentContainerFragment;
    private final List<Fragment> mContentFragmentList;
    private FragmentManager mFragmentManager;
    private SplitContainerFragment mNavigationContainerFragment;
    private final List<Fragment> mNavigationFragmentList;
    private SplitLayout mSplitLayout;

    public interface OnBackCallback {
        boolean onBackPressed();
    }

    @Deprecated
    public SplitFragmentManager(FragmentManager fragmentManager, SplitLayout splitLayout) {
        this.mNavigationFragmentList = new ArrayList();
        this.mContentFragmentList = new ArrayList();
        this.mFragmentManager = fragmentManager;
        this.mSplitLayout = splitLayout;
    }

    public SplitFragmentManager(FragmentManager fragmentManager, SplitLayout splitLayout, int i, int i2) {
        this(fragmentManager, splitLayout, i, i2, true);
    }

    public SplitFragmentManager(FragmentManager fragmentManager, SplitLayout splitLayout, int i, int i2, boolean z) {
        this.mNavigationFragmentList = new ArrayList();
        this.mContentFragmentList = new ArrayList();
        this.mFragmentManager = fragmentManager;
        this.mSplitLayout = splitLayout;
        initContainer(i, i2, z);
    }

    public void initContainer(int i, int i2) {
        initContainer(i, i2, true);
    }

    public void initContainer(int i, int i2, boolean z) {
        String str = FRAGMENT_TAG_PREFIX + i;
        SplitContainerFragment splitContainerFragment = (SplitContainerFragment) this.mFragmentManager.findFragmentByTag(str);
        this.mNavigationContainerFragment = splitContainerFragment;
        if (splitContainerFragment == null || !z) {
            this.mNavigationContainerFragment = SplitContainerFragment.newInstance();
            this.mFragmentManager.beginTransaction().replace(i, this.mNavigationContainerFragment, str).commitAllowingStateLoss();
        }
        String str2 = FRAGMENT_TAG_PREFIX + i2;
        SplitContainerFragment splitContainerFragment2 = (SplitContainerFragment) this.mFragmentManager.findFragmentByTag(str2);
        this.mContentContainerFragment = splitContainerFragment2;
        if (splitContainerFragment2 == null || !z) {
            this.mContentContainerFragment = SplitContainerFragment.newInstance();
            this.mFragmentManager.beginTransaction().replace(i2, this.mContentContainerFragment, str2).commitAllowingStateLoss();
        }
    }

    @Deprecated
    public void replaceRootNavigationFragment(int i, Fragment fragment) {
        if (this.mNavigationContainerFragment == null) {
            this.mNavigationContainerFragment = SplitContainerFragment.newInstance();
            this.mFragmentManager.beginTransaction().replace(i, this.mNavigationContainerFragment).commitAllowingStateLoss();
        }
        replaceRootNavigationFragment(fragment);
    }

    public void replaceRootNavigationFragment(final Fragment fragment) {
        SplitContainerFragment splitContainerFragment = this.mNavigationContainerFragment;
        if (splitContainerFragment == null) {
            throw new IllegalStateException("Navigation fragment container is not initialize, please call initContainer() before add fragment or set navigationViewId in constructor");
        }
        if (!splitContainerFragment.isAdded()) {
            this.mFragmentManager.addFragmentOnAttachListener(new FragmentOnAttachListener() { // from class: miuix.navigation.SplitFragmentManager$$ExternalSyntheticLambda4
                @Override // androidx.fragment.app.FragmentOnAttachListener
                public final void onAttachFragment(FragmentManager fragmentManager, Fragment fragment2) {
                    this.f$0.m1876xa7c5181c(fragment, fragmentManager, fragment2);
                }
            });
        } else {
            this.mNavigationContainerFragment.replaceRootFragment(fragment);
        }
        this.mNavigationFragmentList.clear();
        this.mNavigationFragmentList.add(fragment);
    }

    /* JADX INFO: renamed from: lambda$replaceRootNavigationFragment$0$miuix-navigation-SplitFragmentManager, reason: not valid java name */
    /* synthetic */ void m1876xa7c5181c(Fragment fragment, FragmentManager fragmentManager, Fragment fragment2) {
        SplitContainerFragment splitContainerFragment = this.mNavigationContainerFragment;
        if (fragment2 == splitContainerFragment) {
            splitContainerFragment.replaceRootFragment(fragment);
        }
    }

    @Deprecated
    public void replaceNavigationFragment(int i, Fragment fragment, int i2, int i3, int i4, int i5) {
        if (this.mNavigationContainerFragment == null) {
            this.mNavigationContainerFragment = SplitContainerFragment.newInstance();
            this.mFragmentManager.beginTransaction().replace(i, this.mNavigationContainerFragment).commitAllowingStateLoss();
        }
        replaceNavigationFragment(fragment, i2, i3, i4, i5);
    }

    public void replaceNavigationFragment(final Fragment fragment, final int i, final int i2, final int i3, final int i4) {
        SplitContainerFragment splitContainerFragment = this.mNavigationContainerFragment;
        if (splitContainerFragment == null) {
            throw new IllegalStateException("Navigation fragment container is not initialize, please call initContainer() before add fragment or set navigationViewId in constructor");
        }
        if (!splitContainerFragment.isAdded()) {
            this.mFragmentManager.addFragmentOnAttachListener(new FragmentOnAttachListener() { // from class: miuix.navigation.SplitFragmentManager$$ExternalSyntheticLambda0
                @Override // androidx.fragment.app.FragmentOnAttachListener
                public final void onAttachFragment(FragmentManager fragmentManager, Fragment fragment2) {
                    this.f$0.m1874x9725a45b(fragment, i, i2, i3, i4, fragmentManager, fragment2);
                }
            });
        } else {
            this.mNavigationContainerFragment.replaceChildFragment(fragment, i, i2, i3, i4);
        }
        this.mNavigationFragmentList.add(fragment);
    }

    /* JADX INFO: renamed from: lambda$replaceNavigationFragment$1$miuix-navigation-SplitFragmentManager, reason: not valid java name */
    /* synthetic */ void m1874x9725a45b(Fragment fragment, int i, int i2, int i3, int i4, FragmentManager fragmentManager, Fragment fragment2) {
        SplitContainerFragment splitContainerFragment = this.mNavigationContainerFragment;
        if (fragment2 == splitContainerFragment) {
            splitContainerFragment.replaceChildFragment(fragment, i, i2, i3, i4);
        }
    }

    @Deprecated
    public void replaceNavigationFragment(int i, Fragment fragment) {
        replaceNavigationFragment(i, fragment, R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void replaceNavigationFragment(Fragment fragment) {
        replaceNavigationFragment(fragment, R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void addNavigationFragment(final Fragment fragment, int i, int i2, int i3, int i4) {
        SplitContainerFragment splitContainerFragment = this.mNavigationContainerFragment;
        if (splitContainerFragment == null) {
            throw new IllegalStateException("Navigation fragment container is not initialize, please call initContainer() before add fragment or set navigationViewId in constructor");
        }
        if (!splitContainerFragment.isAdded()) {
            this.mFragmentManager.addFragmentOnAttachListener(new FragmentOnAttachListener() { // from class: miuix.navigation.SplitFragmentManager$$ExternalSyntheticLambda5
                @Override // androidx.fragment.app.FragmentOnAttachListener
                public final void onAttachFragment(FragmentManager fragmentManager, Fragment fragment2) {
                    this.f$0.m1872x8b4c58c9(fragment, fragmentManager, fragment2);
                }
            });
        } else {
            this.mNavigationContainerFragment.addChildFragment(fragment, i, i2, i3, i4);
        }
        this.mNavigationFragmentList.add(fragment);
    }

    /* JADX INFO: renamed from: lambda$addNavigationFragment$2$miuix-navigation-SplitFragmentManager, reason: not valid java name */
    /* synthetic */ void m1872x8b4c58c9(Fragment fragment, FragmentManager fragmentManager, Fragment fragment2) {
        SplitContainerFragment splitContainerFragment = this.mNavigationContainerFragment;
        if (fragment2 == splitContainerFragment) {
            splitContainerFragment.addChildFragment(fragment);
        }
    }

    public void addNavigationFragment(Fragment fragment) {
        addNavigationFragment(fragment, R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Deprecated
    public void replaceRootContentFragment(int i, Fragment fragment) {
        if (this.mContentContainerFragment == null) {
            this.mContentContainerFragment = SplitContainerFragment.newInstance();
            this.mFragmentManager.beginTransaction().replace(i, this.mContentContainerFragment).commitAllowingStateLoss();
        }
        replaceRootContentFragment(fragment);
    }

    public void replaceRootContentFragment(final Fragment fragment) {
        SplitContainerFragment splitContainerFragment = this.mContentContainerFragment;
        if (splitContainerFragment == null) {
            throw new IllegalStateException("Content fragment container is not initialize, please call initContainer() before add fragment or set contentViewId in constructor");
        }
        if (!splitContainerFragment.isAdded()) {
            this.mFragmentManager.addFragmentOnAttachListener(new FragmentOnAttachListener() { // from class: miuix.navigation.SplitFragmentManager$$ExternalSyntheticLambda1
                @Override // androidx.fragment.app.FragmentOnAttachListener
                public final void onAttachFragment(FragmentManager fragmentManager, Fragment fragment2) {
                    this.f$0.m1875x68052672(fragment, fragmentManager, fragment2);
                }
            });
        } else {
            this.mContentContainerFragment.replaceRootFragment(fragment);
        }
        this.mContentFragmentList.clear();
        this.mContentFragmentList.add(fragment);
    }

    /* JADX INFO: renamed from: lambda$replaceRootContentFragment$3$miuix-navigation-SplitFragmentManager, reason: not valid java name */
    /* synthetic */ void m1875x68052672(Fragment fragment, FragmentManager fragmentManager, Fragment fragment2) {
        SplitContainerFragment splitContainerFragment = this.mContentContainerFragment;
        if (fragment2 == splitContainerFragment) {
            splitContainerFragment.replaceRootFragment(fragment);
        }
    }

    @Deprecated
    public void replaceContentFragment(int i, Fragment fragment, int i2, int i3, int i4, int i5) {
        if (this.mContentContainerFragment == null) {
            this.mContentContainerFragment = SplitContainerFragment.newInstance();
            this.mFragmentManager.beginTransaction().replace(i, this.mContentContainerFragment).commitAllowingStateLoss();
        }
        replaceContentFragment(fragment, i2, i3, i4, i5);
    }

    public void replaceContentFragment(final Fragment fragment, final int i, final int i2, final int i3, final int i4) {
        SplitContainerFragment splitContainerFragment = this.mContentContainerFragment;
        if (splitContainerFragment == null) {
            throw new IllegalStateException("Content fragment container is not initialize, please call initContainer() before add fragment or set contentViewId in constructor");
        }
        if (!splitContainerFragment.isAdded()) {
            this.mFragmentManager.addFragmentOnAttachListener(new FragmentOnAttachListener() { // from class: miuix.navigation.SplitFragmentManager$$ExternalSyntheticLambda2
                @Override // androidx.fragment.app.FragmentOnAttachListener
                public final void onAttachFragment(FragmentManager fragmentManager, Fragment fragment2) {
                    this.f$0.m1873x6dc0d975(fragment, i, i2, i3, i4, fragmentManager, fragment2);
                }
            });
        } else {
            this.mContentContainerFragment.replaceChildFragment(fragment, i, i2, i3, i4);
        }
        this.mContentFragmentList.add(fragment);
    }

    /* JADX INFO: renamed from: lambda$replaceContentFragment$4$miuix-navigation-SplitFragmentManager, reason: not valid java name */
    /* synthetic */ void m1873x6dc0d975(Fragment fragment, int i, int i2, int i3, int i4, FragmentManager fragmentManager, Fragment fragment2) {
        SplitContainerFragment splitContainerFragment = this.mContentContainerFragment;
        if (fragment2 == splitContainerFragment) {
            splitContainerFragment.replaceChildFragment(fragment, i, i2, i3, i4);
        }
    }

    @Deprecated
    public void replaceContentFragment(int i, Fragment fragment) {
        replaceContentFragment(i, fragment, R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void replaceContentFragment(Fragment fragment) {
        replaceContentFragment(fragment, R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void addContentFragment(final Fragment fragment, int i, int i2, int i3, int i4) {
        SplitContainerFragment splitContainerFragment = this.mContentContainerFragment;
        if (splitContainerFragment == null) {
            throw new IllegalStateException("Content fragment container is not initialize, please call initContainer() before add fragment or set contentViewId in constructor");
        }
        if (!splitContainerFragment.isAdded()) {
            this.mFragmentManager.addFragmentOnAttachListener(new FragmentOnAttachListener() { // from class: miuix.navigation.SplitFragmentManager$$ExternalSyntheticLambda3
                @Override // androidx.fragment.app.FragmentOnAttachListener
                public final void onAttachFragment(FragmentManager fragmentManager, Fragment fragment2) {
                    this.f$0.m1871xe5a7eaa9(fragment, fragmentManager, fragment2);
                }
            });
        } else {
            this.mContentContainerFragment.addChildFragment(fragment, i, i2, i3, i4);
        }
        this.mContentFragmentList.add(fragment);
    }

    /* JADX INFO: renamed from: lambda$addContentFragment$5$miuix-navigation-SplitFragmentManager, reason: not valid java name */
    /* synthetic */ void m1871xe5a7eaa9(Fragment fragment, FragmentManager fragmentManager, Fragment fragment2) {
        SplitContainerFragment splitContainerFragment = this.mContentContainerFragment;
        if (fragment2 == splitContainerFragment) {
            splitContainerFragment.addChildFragment(fragment);
        }
    }

    public void addContentFragment(Fragment fragment) {
        addContentFragment(fragment, R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public int getContentFragmentSize() {
        return this.mContentFragmentList.size();
    }

    public int getNavigationFragmentSize() {
        return this.mNavigationFragmentList.size();
    }

    public boolean popFragment() {
        SplitLayout splitLayout;
        if (this.mFragmentManager != null && (splitLayout = this.mSplitLayout) != null) {
            int splitMode = splitLayout.getSplitMode();
            if (splitMode == 0) {
                if (this.mSplitLayout.isNavigationOpen()) {
                    int size = this.mNavigationFragmentList.size();
                    if (size > 0 && checkInternalBack(this.mNavigationFragmentList.get(size - 1))) {
                        return true;
                    }
                    this.mSplitLayout.closeNavigation();
                    return true;
                }
                if (this.mContentFragmentList.size() > 1 && popContentFragment()) {
                    return true;
                }
                if (this.mContentFragmentList.size() == 1 && checkInternalBack(this.mContentFragmentList.get(0))) {
                    return true;
                }
            } else if (2 == splitMode) {
                if (this.mContentFragmentList.size() > 1 && popContentFragment()) {
                    return true;
                }
                if (this.mContentFragmentList.size() == 1) {
                    if (checkInternalBack(this.mContentFragmentList.get(0))) {
                        return true;
                    }
                    this.mSplitLayout.openNavigation();
                    if (this.mContentFragmentList.remove(0).isAdded() && this.mContentContainerFragment.clearBackStack()) {
                        return true;
                    }
                }
                if (this.mNavigationFragmentList.size() > 1 && popNavigationFragment()) {
                    return true;
                }
                if (this.mNavigationFragmentList.size() == 1 && checkInternalBack(this.mNavigationFragmentList.get(0))) {
                    return true;
                }
            } else if (1 == splitMode) {
                if (!this.mSplitLayout.isNavigationEnable() && this.mContentFragmentList.size() > 0) {
                    if (this.mContentFragmentList.size() > 1 && popContentFragment()) {
                        return true;
                    }
                    if (this.mContentFragmentList.size() == 1) {
                        return checkInternalBack(this.mContentFragmentList.get(0));
                    }
                }
                if (!this.mSplitLayout.isContentEnable() && this.mNavigationFragmentList.size() > 0) {
                    if (this.mNavigationFragmentList.size() > 1 && popNavigationFragment()) {
                        return true;
                    }
                    if (this.mNavigationFragmentList.size() == 1) {
                        return checkInternalBack(this.mNavigationFragmentList.get(0));
                    }
                }
                if (this.mContentFragmentList.size() > 1 && popContentFragment()) {
                    return true;
                }
                if (this.mContentFragmentList.size() == 1 && checkInternalBack(this.mContentFragmentList.get(0))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean popContentFragment() {
        List<Fragment> list = this.mContentFragmentList;
        if (checkInternalBack(list.get(list.size() - 1))) {
            return true;
        }
        SplitContainerFragment splitContainerFragment = this.mContentContainerFragment;
        if (splitContainerFragment == null || !splitContainerFragment.popBackStack()) {
            return false;
        }
        List<Fragment> list2 = this.mContentFragmentList;
        list2.remove(list2.size() - 1);
        return true;
    }

    private boolean popNavigationFragment() {
        List<Fragment> list = this.mNavigationFragmentList;
        if (checkInternalBack(list.get(list.size() - 1))) {
            return true;
        }
        SplitContainerFragment splitContainerFragment = this.mNavigationContainerFragment;
        if (splitContainerFragment == null || !splitContainerFragment.popBackStack()) {
            return false;
        }
        List<Fragment> list2 = this.mNavigationFragmentList;
        list2.remove(list2.size() - 1);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean checkInternalBack(Fragment fragment) {
        return (fragment instanceof OnBackCallback) && ((OnBackCallback) fragment).onBackPressed();
    }

    public void destroy() {
        this.mNavigationFragmentList.clear();
        this.mContentFragmentList.clear();
        this.mFragmentManager = null;
        this.mSplitLayout = null;
        this.mNavigationContainerFragment = null;
        this.mContentContainerFragment = null;
    }
}
