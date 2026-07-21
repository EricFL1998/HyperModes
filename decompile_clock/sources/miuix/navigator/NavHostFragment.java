package miuix.navigator;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import java.util.List;
import miuix.appcompat.app.ShortcutsCallback;
import miuix.autodensity.AutoDensityConfig;
import miuix.core.util.MiuixUIUtils;
import miuix.navigator.app.NavigatorResponsiveProvider;
import miuix.os.DeviceHelper;
import miuix.responsive.ResponsiveStateHelper;
import miuix.responsive.map.ResponsiveState;
import miuix.responsive.map.ScreenSpec;
import miuix.responsive.page.manager.BaseResponseStateManager;

/* JADX INFO: loaded from: classes3.dex */
public class NavHostFragment extends Fragment implements NavigatorResponsiveProvider<Fragment>, ShortcutsCallback {
    public static final String TAG = "miuix.navHostFragment";
    private int mDeviceType = -1;
    private NavigatorImpl mNavigator;
    private BaseResponseStateManager mResponsiveStateManager;

    @Override // miuix.responsive.interfaces.IResponsive
    public Fragment getResponsiveSubject() {
        return this;
    }

    protected NavigatorImpl createNavigator(Bundle bundle, NavHostFragment navHostFragment) {
        return new NavigatorImpl(bundle, navHostFragment);
    }

    public Navigator getNavigator(Fragment fragment) {
        NavigatorImpl navigatorImpl = this.mNavigator;
        if (navigatorImpl != null) {
            return navigatorImpl.findNavigator(fragment);
        }
        if (fragment == null) {
            return null;
        }
        Log.e("MiuixNavigator", "Error!! You should not get navigator when NavHostFragment is detached!! fragment:" + fragment.isDetached() + " " + fragment + " trace: " + Log.getStackTraceString(new Throwable()));
        return null;
    }

    public int getDeviceType() {
        return this.mDeviceType;
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        this.mNavigator = createNavigator(getArguments(), this);
        this.mResponsiveStateManager = new BaseResponseStateManager(this) { // from class: miuix.navigator.NavHostFragment.1
            @Override // miuix.responsive.page.manager.BaseStateManager
            protected Context getContext() {
                return NavHostFragment.this.getActivity();
            }
        };
        super.onAttach(context);
        AutoDensityConfig.updateDensity(context);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mDeviceType = DeviceHelper.detectType(getContext());
        getParentFragmentManager().beginTransaction().setPrimaryNavigationFragment(this).commit();
        this.mResponsiveStateManager.updateResponsiveState();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, this.mNavigator.getOnBackPressedCallback());
        this.mNavigator.onCreate(bundle);
        if (bundle == null) {
            this.mNavigator.updateNavigationMode();
        }
        this.mNavigator.dispatchCreate();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.miuix_navigator_screen, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        this.mNavigator.setNavigationView(view);
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: miuix.navigator.NavHostFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                this.f$0.m1894lambda$onViewCreated$0$miuixnavigatorNavHostFragment(view2, i, i2, i3, i4, i5, i6, i7, i8);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onViewCreated$0$miuix-navigator-NavHostFragment, reason: not valid java name */
    /* synthetic */ void m1894lambda$onViewCreated$0$miuixnavigatorNavHostFragment(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        ResponsiveState responsiveState = getResponsiveState();
        float windowDensity = responsiveState.getWindowDensity();
        int iDetectResponsiveWindowType = ResponsiveStateHelper.detectResponsiveWindowType(MiuixUIUtils.px2dp(windowDensity, i3 - i), MiuixUIUtils.px2dp(windowDensity, i4 - i2), responsiveState.getWindowWidthDp(), responsiveState.getWindowHeightDp());
        if (responsiveState.getType() != iDetectResponsiveWindowType) {
            responsiveState.setType(iDetectResponsiveWindowType);
            this.mNavigator.updateNavigationMode();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mNavigator.dispatchActivityCreated();
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mNavigator.dispatchStart();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mNavigator.dispatchResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mNavigator.dispatchPause();
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mNavigator.dispatchStop();
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mNavigator.onSaveInstanceState(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mNavigator.dispatchDestroyView();
        this.mNavigator.releaseView();
        this.mResponsiveStateManager = null;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mNavigator.dispatchDestroy();
        this.mNavigator.getOnBackPressedCallback().remove();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.mNavigator = null;
    }

    @Override // androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        this.mResponsiveStateManager.beforeConfigurationChanged(getResources().getConfiguration());
        super.onConfigurationChanged(configuration);
        AutoDensityConfig.updateDensity(getContext());
        if (getView() != null) {
            ((MiuixNavigationLayout) getView()).dispatchConfigurationChanged();
        }
        this.mResponsiveStateManager.afterConfigurationChanged(configuration);
    }

    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyEvent(KeyEvent keyEvent) {
        return this.mNavigator.dispatchKeyEvent(keyEvent);
    }

    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyShortcutEvent(KeyEvent keyEvent) {
        return this.mNavigator.dispatchKeyShortcutEvent(keyEvent);
    }

    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mNavigator.dispatchTouchEvent(motionEvent);
    }

    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        return this.mNavigator.dispatchTrackballEvent(motionEvent);
    }

    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return this.mNavigator.dispatchGenericMotionEvent(motionEvent);
    }

    @Override // miuix.appcompat.app.ShortcutsCallback
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> list, Menu menu, int i) {
        this.mNavigator.dispatchProvideKeyboardShortcuts(list, menu, i);
    }

    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return this.mNavigator.dispatchKeyDown(i, keyEvent);
    }

    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        return this.mNavigator.dispatchKeyLongPress(i, keyEvent);
    }

    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return this.mNavigator.dispatchKeyUp(i, keyEvent);
    }

    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return this.mNavigator.dispatchKeyMultiple(i, i2, keyEvent);
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public ResponsiveState getResponsiveState() {
        return this.mResponsiveStateManager.getState();
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public void dispatchResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        super.dispatchResponsiveLayout(configuration, screenSpec, z);
        int iDetectType = DeviceHelper.detectType(getContext());
        this.mDeviceType = iDetectType;
        this.mNavigator.dispatchResponsiveLayout(iDetectType, configuration, screenSpec, z);
    }

    public void testNotifyResponseChange(int i) {
        this.mResponsiveStateManager.testNotifyResponseChange(i);
    }
}
