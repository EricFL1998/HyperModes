package miuix.navigator;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import androidx.activity.result.ActivityResultCaller;
import androidx.fragment.app.Fragment;
import java.util.List;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.FragmentDelegate;
import miuix.appcompat.app.IFragment;
import miuix.navigator.app.NavigatorResponsiveProvider;
import miuix.responsive.interfaces.IResponsive;
import miuix.responsive.map.ResponsiveState;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes3.dex */
class NavigatorFragmentDelegate extends FragmentDelegate {
    private final Fragment mFragment;
    private final SubNavigator mNavigator;
    protected ViewAfterNavigatorSwitchPresenter mSwitchPresenter;

    public NavigatorFragmentDelegate(SubNavigator subNavigator, Fragment fragment) {
        super(fragment);
        this.mNavigator = subNavigator;
        this.mFragment = fragment;
    }

    protected final SubNavigator getNavigator() {
        return this.mNavigator;
    }

    @Override // miuix.appcompat.app.FragmentDelegate
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        SubNavigator navigator = getNavigator();
        if (navigator != null) {
            ActivityResultCaller activityResultCaller = this.mFragment;
            if (activityResultCaller instanceof NavigatorFragmentListener) {
                navigator.addNavigatorFragmentListener((NavigatorFragmentListener) activityResultCaller);
            }
            if (hasActionBar()) {
                updateViewAfterNavigatorSwitchPresenter(getActionBar());
            }
        }
    }

    @Override // miuix.appcompat.app.FragmentDelegate, miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (hasActionBar()) {
            updateViewAfterNavigatorSwitchPresenter(getActionBar());
        }
    }

    @Override // miuix.appcompat.app.FragmentDelegate
    public void onDestroyView() {
        SubNavigator navigator = getNavigator();
        View view = getView();
        if (navigator != null && view != null) {
            navigator.removeNavigatorSwitch(view.findViewById(R.id.navigator_switch));
        }
        super.onDestroyView();
        if (navigator != null) {
            ActivityResultCaller activityResultCaller = this.mFragment;
            if (activityResultCaller instanceof NavigatorFragmentListener) {
                navigator.removeNavigatorFragmentListener((NavigatorFragmentListener) activityResultCaller);
            }
        }
    }

    @Override // miuix.appcompat.app.FragmentDelegate, miuix.responsive.interfaces.IResponsive
    public ResponsiveState getResponsiveState() {
        ActivityResultCaller activityResultCaller = this.mFragment;
        if (activityResultCaller instanceof IFragment ? ((IFragment) activityResultCaller).isRegisterResponsive() : false) {
            return super.getResponsiveState();
        }
        Object host = this.mFragment.getHost();
        if (host instanceof NavigatorResponsiveProvider) {
            return ((NavigatorResponsiveProvider) host).getResponsiveState();
        }
        return super.getResponsiveState();
    }

    @Override // miuix.appcompat.app.FragmentDelegate, miuix.responsive.interfaces.IResponsive
    public void dispatchResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        super.dispatchResponsiveLayout(configuration, screenSpec, z);
        List<Fragment> fragments = this.mFragment.getChildFragmentManager().getFragments();
        if (fragments.size() > 0) {
            for (ActivityResultCaller activityResultCaller : fragments) {
                if ((activityResultCaller instanceof IFragment) && (activityResultCaller instanceof IResponsive) && !((IFragment) activityResultCaller).isRegisterResponsive()) {
                    ((IResponsive) activityResultCaller).dispatchResponsiveLayout(configuration, screenSpec, z);
                }
            }
        }
    }

    void updateViewAfterNavigatorSwitchPresenter(ActionBar actionBar) {
        View view;
        View viewFindViewById;
        SubNavigator navigator = getNavigator();
        if (actionBar == null || navigator == null || (viewFindViewById = (view = getView()).findViewById(R.id.navigator_switch)) == null) {
            return;
        }
        if (this.mSwitchPresenter == null) {
            this.mSwitchPresenter = new ViewAfterNavigatorSwitchPresenter();
        }
        this.mSwitchPresenter.attachViews(actionBar.getStartView());
        this.mSwitchPresenter.attachViews(view.findViewById(R.id.up));
        this.mSwitchPresenter.attachViews(actionBar.getCustomView());
        navigator.addNavigatorSwitch(viewFindViewById, this.mSwitchPresenter);
    }
}
