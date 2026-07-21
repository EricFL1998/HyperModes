package miuix.navigator;

import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/* JADX INFO: loaded from: classes3.dex */
class SecondaryContentSubNavigator extends SubNavigator {
    SecondaryContentSubNavigator(NavigatorImpl navigatorImpl) {
        super(navigatorImpl);
        navigatorImpl.addNavigatorFragmentListener(this);
    }

    @Override // miuix.navigator.SubNavigator
    void onControllerAttach(NavigatorFragmentController navigatorFragmentController) {
        super.onControllerAttach(navigatorFragmentController);
        navigatorFragmentController.getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() { // from class: miuix.navigator.SecondaryContentSubNavigator$$ExternalSyntheticLambda0
            @Override // androidx.fragment.app.FragmentManager.OnBackStackChangedListener
            public final void onBackStackChanged() {
                this.f$0.m1895xf8f50b72();
            }
        });
        navigatorFragmentController.getFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() { // from class: miuix.navigator.SecondaryContentSubNavigator.1
            @Override // androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
            public void onFragmentDetached(FragmentManager fragmentManager, Fragment fragment) {
                if (fragmentManager.getFragments().isEmpty()) {
                    SecondaryContentSubNavigator.this.getBaseNavigator().setSecondaryOnTop(SecondaryContentSubNavigator.this.getBaseNavigator().isSecondaryOnTop(), true);
                }
            }
        }, false);
    }

    /* JADX INFO: renamed from: lambda$onControllerAttach$0$miuix-navigator-SecondaryContentSubNavigator, reason: not valid java name */
    /* synthetic */ void m1895xf8f50b72() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            getBaseNavigator().setSecondaryOnTop(false, false);
            getBaseNavigator().getNavigationView().setSecondaryContentReady(false);
            FragmentAnimationHelper.showContentFragment(getBaseNavigator());
        }
        getBaseNavigator().updateOnBackPressedCallbackEnabled();
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator
    public String getTag() {
        return Navigator.TAG_SECONDARY_CONTENT;
    }

    @Override // miuix.navigator.SubNavigator
    public boolean requestUserFocus(boolean z) {
        if (getBaseNavigator().getNavigationView() == null) {
            return false;
        }
        requestFocus();
        getBaseNavigator().userFocusSecondaryContent(z);
        return true;
    }

    @Override // miuix.navigator.SubNavigator
    public boolean isUserFocused() {
        return getBaseNavigator().isSecondaryContentUserFocused();
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator
    public void requestFocus(boolean z) {
        if (getBaseNavigator().isContentUserFocused()) {
            return;
        }
        getBaseNavigator().setSecondaryOnTop(true, z);
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator
    public boolean isFocused() {
        return getBaseNavigator().isSecondaryOnTop();
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onSecondaryContentVisibilityChanged(int i) {
        int i2 = 1;
        boolean z = (i & 4) != 0;
        int i3 = i & 3;
        if (i3 != 0) {
            if (i3 == 1) {
                i2 = 3;
            } else if (i3 != 2) {
                return;
            } else {
                i2 = 4;
            }
        }
        getFragmentController().setFragmentState((i2 == 4 && z) ? 3 : i2);
    }

    @Override // miuix.navigator.SubNavigator
    void addNavigatorSwitch(View view, ViewAfterNavigatorSwitchPresenter viewAfterNavigatorSwitchPresenter) {
        getBaseNavigator().addSecondaryContentSwitch(view, viewAfterNavigatorSwitchPresenter);
    }

    @Override // miuix.navigator.SubNavigator
    void removeNavigatorSwitch(View view) {
        getBaseNavigator().removeSecondaryContentSwitch(view);
    }
}
