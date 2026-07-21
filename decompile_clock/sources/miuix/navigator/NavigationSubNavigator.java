package miuix.navigator;

import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
class NavigationSubNavigator extends SubNavigator {
    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator
    public boolean isFocused() {
        return false;
    }

    @Override // miuix.navigator.SubNavigator
    public boolean isUserFocused() {
        return false;
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator
    public void requestFocus(boolean z) {
    }

    @Override // miuix.navigator.SubNavigator
    public boolean requestUserFocus(boolean z) {
        return false;
    }

    NavigationSubNavigator(NavigatorImpl navigatorImpl) {
        super(navigatorImpl);
        addNavigatorFragmentListener(this);
        addNavigatorFragmentListener(new NavigatorFragmentListener() { // from class: miuix.navigator.NavigationSubNavigator.1
            @Override // miuix.navigator.NavigatorFragmentListener
            public void onNavigatorModeChanged(Navigator.Mode mode, Navigator.Mode mode2) {
                if (mode2 == Navigator.Mode.NLC || mode2 == Navigator.Mode.NC) {
                    NavigationSubNavigator.this.removeNavigatorFragmentListener(this);
                    NavigationSubNavigator.this.getFragmentManager().beginTransaction().replace(R.id.navigation_decor, NavigationFragment.class, null, "miuix.navigation").commitNowAllowingStateLoss();
                }
            }
        });
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator
    public String getTag() {
        return "miuix.navigation";
    }

    @Override // miuix.navigator.SubNavigator, miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onNavigationVisibilityChanged(int i) {
        boolean z = (i & 4) != 0;
        int i2 = i & 3;
        int i3 = 2;
        if (i2 != 0) {
            if (i2 == 1) {
                i3 = 3;
            } else if (i2 != 2) {
                return;
            } else {
                i3 = 4;
            }
        }
        getFragmentController().setFragmentState((i3 == 4 && z) ? 3 : i3);
    }

    @Override // miuix.navigator.SubNavigator
    void addNavigatorSwitch(View view, ViewAfterNavigatorSwitchPresenter viewAfterNavigatorSwitchPresenter) {
        getBaseNavigator().setNavigationSwitch(view);
    }
}
