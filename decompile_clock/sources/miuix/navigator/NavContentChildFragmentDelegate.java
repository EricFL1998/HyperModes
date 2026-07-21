package miuix.navigator;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import androidx.activity.result.ActivityResultCaller;
import androidx.fragment.app.Fragment;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.FragmentDelegate;
import miuix.appcompat.app.IFragment;
import miuix.internal.util.AttributeResolver;

/* JADX INFO: loaded from: classes3.dex */
class NavContentChildFragmentDelegate extends NavigatorFragmentDelegate {
    private Fragment mFragment;

    public NavContentChildFragmentDelegate(SubNavigator subNavigator, Fragment fragment) {
        super(subNavigator, fragment);
        this.mFragment = fragment;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public ActionBar getActionBar() {
        if (!hasActionBar()) {
            ActivityResultCaller parentFragment = this.mFragment.getParentFragment();
            if (parentFragment instanceof IFragment) {
                return ((IFragment) parentFragment).getActionBar();
            }
        }
        return super.getActionBar();
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setExtraThemeRes(AttributeResolver.resolve(getActivity(), R.attr.navigatorContentChildStyle));
    }

    @Override // miuix.navigator.NavigatorFragmentDelegate, miuix.appcompat.app.FragmentDelegate
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (hasActionBar()) {
            return;
        }
        Fragment parentFragment = this.mFragment.getParentFragment();
        if (parentFragment instanceof miuix.appcompat.app.Fragment) {
            FragmentDelegate delegate = ((miuix.appcompat.app.Fragment) parentFragment).getDelegate();
            if (delegate instanceof NavContentFragmentDelegate) {
                ((NavContentFragmentDelegate) delegate).updateViewAfterNavigatorSwitchPresenter(getActionBar());
            }
        }
    }

    @Override // miuix.appcompat.app.FragmentDelegate, miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public ActionMode startActionMode(ActionMode.Callback callback) {
        ActivityResultCaller activityResultCaller = this.mFragment;
        if ((activityResultCaller instanceof IFragment) && ((IFragment) activityResultCaller).hasActionBar()) {
            return super.startActionMode(callback);
        }
        Fragment parentFragment = this.mFragment.getParentFragment();
        if (parentFragment instanceof miuix.appcompat.app.Fragment) {
            return ((miuix.appcompat.app.Fragment) parentFragment).startActionMode(callback);
        }
        return super.startActionMode(callback);
    }
}
