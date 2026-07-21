package miuix.navigator;

import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/* JADX INFO: loaded from: classes3.dex */
class ContentSubNavigator extends SubNavigator {
    ContentSubNavigator(NavigatorImpl navigatorImpl) {
        super(navigatorImpl);
        navigatorImpl.addNavigatorFragmentListener(this);
        setContentShowListener();
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator
    public String getTag() {
        return Navigator.TAG_CONTENT;
    }

    @Override // miuix.navigator.SubNavigator
    public boolean requestUserFocus(boolean z) {
        if (getBaseNavigator().getNavigationView() == null) {
            return false;
        }
        requestFocus();
        getBaseNavigator().userFocusContent(z);
        return true;
    }

    @Override // miuix.navigator.SubNavigator
    public boolean isUserFocused() {
        return getBaseNavigator().isContentUserFocused();
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator
    public void requestFocus(boolean z) {
        if (getBaseNavigator().isSecondaryContentUserFocused()) {
            return;
        }
        getBaseNavigator().setSecondaryOnTop(false, z);
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator
    public boolean isFocused() {
        return !getBaseNavigator().isSecondaryOnTop();
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onContentVisibilityChanged(int i) {
        FragmentManager fragmentManager;
        Fragment fragmentFindFragmentByTag;
        boolean z = false;
        boolean z2 = (i & 4) != 0;
        int i2 = 3;
        int i3 = i & 3;
        if (i3 == 0) {
            i2 = 2;
        } else if (i3 != 1) {
            if (i3 != 2) {
                return;
            }
            i2 = z2 ? 3 : 4;
            z = true;
        }
        getFragmentController().setFragmentState(i2);
        if (z && (fragmentFindFragmentByTag = (fragmentManager = getFragmentManager()).findFragmentByTag(Navigator.TAG_CONTENT)) != null && fragmentFindFragmentByTag.isHidden()) {
            fragmentManager.beginTransaction().show(fragmentFindFragmentByTag).commitAllowingStateLoss();
        }
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onBottomNavigationVisibilityChanged(boolean z, int i) {
        for (Fragment fragment : getFragmentManager().getFragments()) {
            if (fragment instanceof miuix.appcompat.app.Fragment) {
                ((miuix.appcompat.app.Fragment) fragment).setBottomExtraInset(i);
            }
        }
    }

    @Override // miuix.navigator.SubNavigator
    void addNavigatorSwitch(View view, ViewAfterNavigatorSwitchPresenter viewAfterNavigatorSwitchPresenter) {
        getBaseNavigator().addContentSwitch(view, viewAfterNavigatorSwitchPresenter);
    }

    @Override // miuix.navigator.SubNavigator
    void removeNavigatorSwitch(View view) {
        getBaseNavigator().removeContentSwitch(view);
    }

    private void setContentShowListener() {
        getFragmentManager().addFragmentOnAttachListener(new FragmentOnAttachListener() { // from class: miuix.navigator.ContentSubNavigator$$ExternalSyntheticLambda0
            @Override // androidx.fragment.app.FragmentOnAttachListener
            public final void onAttachFragment(FragmentManager fragmentManager, Fragment fragment) {
                this.f$0.m1878x5818e003(fragmentManager, fragment);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setContentShowListener$1$miuix-navigator-ContentSubNavigator, reason: not valid java name */
    /* synthetic */ void m1878x5818e003(FragmentManager fragmentManager, Fragment fragment) {
        fragment.getLifecycle().addObserver(new LifecycleEventObserver() { // from class: miuix.navigator.ContentSubNavigator$$ExternalSyntheticLambda1
            @Override // androidx.lifecycle.LifecycleEventObserver
            public final void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                this.f$0.m1877x44710c82(lifecycleOwner, event);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setContentShowListener$0$miuix-navigator-ContentSubNavigator, reason: not valid java name */
    /* synthetic */ void m1877x44710c82(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        if (event.getTargetState() != Lifecycle.State.STARTED || getBaseNavigator().getNavigationView() == null) {
            return;
        }
        getBaseNavigator().getNavigationView().onContentShow();
    }
}
