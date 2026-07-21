package miuix.appcompat.app;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import java.util.List;
import miuix.appcompat.R;
import miuix.appcompat.internal.util.LayoutUIUtils;
import miuix.autodensity.AutoDensityConfig;
import miuix.container.ExtraPaddingObserver;
import miuix.container.ExtraPaddingPolicy;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.WindowBaseInfo;
import miuix.responsive.interfaces.IResponsive;
import miuix.responsive.map.ResponsiveState;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes2.dex */
public class Fragment extends androidx.fragment.app.Fragment implements IFragment, IContentInsetState, IResponsive<Fragment>, ShortcutsCallback {
    protected FragmentDelegate mDelegate;
    private int mInputViewLimitTextSizeDp;
    private boolean mHasMenu = true;
    private boolean mMenuVisible = true;

    @Override // miuix.appcompat.app.IFragment
    public void checkThemeLegality() {
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public Fragment getResponsiveSubject() {
        return this;
    }

    protected boolean isResponsiveEnabled() {
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return null;
    }

    @Override // miuix.appcompat.app.IFragment
    public void onOptionsMenuViewAdded(Menu menu, Menu menu2) {
    }

    @Override // miuix.appcompat.app.IFragment
    public void onPanelClosed(int i, Menu menu) {
    }

    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
    }

    public void onViewInflated(View view, Bundle bundle) {
    }

    public void onVisibilityChanged(boolean z) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        AutoDensityConfig.updateDensity(context);
        FragmentFactory fragmentFactory = getParentFragmentManager().getFragmentFactory();
        if (fragmentFactory instanceof DelegateFragmentFactory) {
            this.mDelegate = ((DelegateFragmentFactory) fragmentFactory).createFragmentDelegate(this);
        } else {
            this.mDelegate = new FragmentDelegate(this);
        }
        this.mDelegate.setResponsiveEnabled(isResponsiveEnabled());
        this.mInputViewLimitTextSizeDp = MiuixUIUtils.isTallFontLang(getContext()) ? 16 : 27;
    }

    @Override // androidx.fragment.app.Fragment
    public Animator onCreateAnimator(int i, boolean z, int i2) {
        return this.mDelegate.onCreateAnimator(i, z, i2);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mDelegate.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mDelegate.onStop();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate != null) {
            fragmentDelegate.onPostResume();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.mDelegate.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.fragment.app.Fragment
    @Deprecated
    public void onViewCreated(View view, Bundle bundle) {
        final View viewFindViewById;
        this.mDelegate.onViewCreated(view, bundle);
        Rect contentInset = this.mDelegate.getContentInset();
        if (contentInset != null && (contentInset.top != 0 || contentInset.bottom != 0 || contentInset.left != 0 || contentInset.right != 0)) {
            onContentInsetChanged(contentInset);
        }
        if (view == null || !isAdded() || (viewFindViewById = view.findViewById(R.id.search_mode_stub)) == null) {
            return;
        }
        viewFindViewById.post(new Runnable() { // from class: miuix.appcompat.app.Fragment$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1812lambda$onViewCreated$0$miuixappcompatappFragment(viewFindViewById);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onViewCreated$0$miuix-appcompat-app-Fragment, reason: not valid java name */
    /* synthetic */ void m1812lambda$onViewCreated$0$miuixappcompatappFragment(View view) {
        if (isAdded()) {
            LayoutUIUtils.resetSearchModeStubInputTextSize(getResources(), view, this.mInputViewLimitTextSizeDp);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View getView() {
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate == null) {
            return null;
        }
        return fragmentDelegate.getView();
    }

    @Override // androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        AutoDensityConfig.updateDensityByConfig(getContext(), configuration);
        this.mDelegate.onConfigurationChanged(configuration);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mDelegate.onDestroyView();
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean hasActionBar() {
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate == null) {
            return false;
        }
        return fragmentDelegate.hasActionBar();
    }

    @Override // miuix.appcompat.app.IFragment
    public ActionBar getActionBar() {
        return this.mDelegate.getActionBar();
    }

    public AppCompatActivity getAppCompatActivity() {
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate == null) {
            return null;
        }
        return fragmentDelegate.getActivity();
    }

    public MenuInflater getMenuInflater() {
        return this.mDelegate.getMenuInflater();
    }

    @Override // miuix.appcompat.app.IFragment
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return this.mDelegate.startActionMode(callback);
    }

    @Override // miuix.appcompat.app.IFragment
    public void setThemeRes(int i) {
        this.mDelegate.setExtraThemeRes(i);
    }

    @Override // miuix.appcompat.app.IFragment
    public Context getThemedContext() {
        return this.mDelegate.getThemedContext();
    }

    @Override // androidx.fragment.app.Fragment
    public void setHasOptionsMenu(boolean z) {
        super.setHasOptionsMenu(z);
        if (this.mHasMenu != z) {
            this.mHasMenu = z;
            if (!z || this.mDelegate == null || isHidden() || !isAdded()) {
                return;
            }
            this.mDelegate.invalidateOptionsMenu();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void setMenuVisibility(boolean z) {
        FragmentDelegate fragmentDelegate;
        super.setMenuVisibility(z);
        if (this.mMenuVisible != z) {
            this.mMenuVisible = z;
            if (isHidden() || !isAdded() || (fragmentDelegate = this.mDelegate) == null) {
                return;
            }
            fragmentDelegate.invalidateOptionsMenu();
        }
    }

    public boolean requestWindowFeature(int i) {
        return this.mDelegate.requestWindowFeature(i);
    }

    public void invalidateOptionsMenu() {
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate != null) {
            fragmentDelegate.updateOptionsMenu(1);
            if (!isHidden() && this.mHasMenu && this.mMenuVisible && isAdded()) {
                this.mDelegate.invalidateOptionsMenu();
            }
        }
    }

    public void updateOptionsMenuContent() {
        if (this.mDelegate != null && !isHidden() && this.mHasMenu && this.mMenuVisible && isAdded()) {
            this.mDelegate.invalidateOptionsMenu();
        }
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean isInEditActionMode() {
        return this.mDelegate.isInEditActionMode();
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean isIsInSearchActionMode() {
        return this.mDelegate.isIsInSearchActionMode();
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean isRegisterResponsive() {
        return this.mDelegate.isRegisterResponsive();
    }

    @Override // miuix.appcompat.app.IFragment
    public void onActionModeStarted(ActionMode actionMode) {
        this.mDelegate.onActionModeStarted(actionMode);
    }

    @Override // miuix.appcompat.app.IFragment
    public void onActionModeFinished(ActionMode actionMode) {
        this.mDelegate.onActionModeFinished(actionMode);
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean zOnNavigateUpFromChild;
        if (menuItem.getItemId() != 16908332 || getActionBar() == null || (getActionBar().getDisplayOptions() & 4) == 0) {
            return false;
        }
        FragmentActivity activity = getActivity();
        if (activity.getParent() == null) {
            zOnNavigateUpFromChild = activity.onNavigateUp();
        } else {
            zOnNavigateUpFromChild = activity.getParent().onNavigateUpFromChild(activity);
        }
        if (zOnNavigateUpFromChild) {
            return true;
        }
        getActivity().getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean onCreatePanelMenu(int i, Menu menu) {
        if (i == 0 && this.mHasMenu && this.mMenuVisible && !isHidden() && isAdded()) {
            return onCreateOptionsMenu(menu);
        }
        return false;
    }

    @Override // miuix.appcompat.app.IFragment
    public void onPreparePanel(int i, View view, Menu menu) {
        if (i == 0 && this.mHasMenu && this.mMenuVisible && !isHidden() && isAdded()) {
            onPrepareOptionsMenu(menu);
        }
    }

    public View getInflatedView() {
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate == null) {
            return null;
        }
        return fragmentDelegate.getInflatedView();
    }

    @Override // androidx.fragment.app.Fragment
    public final void onHiddenChanged(boolean z) {
        FragmentDelegate fragmentDelegate;
        super.onHiddenChanged(z);
        if (!z && (fragmentDelegate = this.mDelegate) != null) {
            fragmentDelegate.invalidateOptionsMenu();
        }
        onVisibilityChanged(!z);
    }

    public void setEndActionMenuEnabled(boolean z) {
        this.mDelegate.setEndActionMenuEnabled(z);
    }

    public void setHyperSplitMenuEnabled(boolean z) {
        this.mDelegate.setHyperSplitMenuEnabled(z);
    }

    public void setHyperActionMenuEnabled(boolean z) {
        this.mDelegate.setHyperActionMenuEnabled(z);
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    @Deprecated
    public void setImmersionMenuEnabled(boolean z) {
        this.mDelegate.setImmersionMenuEnabled(z);
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    @Deprecated
    public void showImmersionMenu() {
        this.mDelegate.showImmersionMenu();
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    @Deprecated
    public void showImmersionMenu(View view, ViewGroup viewGroup) {
        this.mDelegate.showImmersionMenu(view, viewGroup);
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    @Deprecated
    public void dismissImmersionMenu(boolean z) {
        this.mDelegate.dismissImmersionMenu(z);
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void showEndOverflowMenu() {
        this.mDelegate.showEndOverflowMenu();
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void hideEndOverflowMenu() {
        this.mDelegate.hideEndOverflowMenu();
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void showOverflowMenu() {
        this.mDelegate.showOverflowMenu();
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void hideOverflowMenu() {
        this.mDelegate.hideOverflowMenu();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mDelegate.dismissImmersionMenu(false);
    }

    public void setOnStatusBarChangeListener(OnStatusBarChangeListener onStatusBarChangeListener) {
        this.mDelegate.setOnStatusBarChangeListener(onStatusBarChangeListener);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraPaddingPolicy(ExtraPaddingPolicy extraPaddingPolicy) {
        this.mDelegate.setExtraPaddingPolicy(extraPaddingPolicy);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public ExtraPaddingPolicy getExtraPaddingPolicy() {
        return this.mDelegate.getExtraPaddingPolicy();
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void addExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        this.mDelegate.addExtraPaddingObserver(extraPaddingObserver);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void removeExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        this.mDelegate.removeExtraPaddingObserver(extraPaddingObserver);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingEnable(boolean z) {
        this.mDelegate.setExtraHorizontalPaddingEnable(z);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingInitEnable(boolean z) {
        this.mDelegate.setExtraHorizontalPaddingInitEnable(z);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public boolean isExtraHorizontalPaddingEnable() {
        return this.mDelegate.isExtraHorizontalPaddingEnable();
    }

    @Override // miuix.container.ExtraPaddingObserver
    public boolean setExtraHorizontalPadding(int i) {
        return this.mDelegate.setExtraHorizontalPadding(i);
    }

    @Override // miuix.container.ExtraPaddingObserver
    public int getExtraHorizontalPadding() {
        return this.mDelegate.getExtraHorizontalPadding();
    }

    @Override // miuix.container.ExtraPaddingObserver
    public void onExtraPaddingChanged(int i) {
        this.mDelegate.onExtraPaddingChanged(i);
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean acceptExtraPaddingFromParent() {
        return this.mDelegate.acceptExtraPaddingFromParent();
    }

    @Deprecated
    public void setExtraHorizontalPaddingLevel(int i) {
        this.mDelegate.setExtraHorizontalPaddingLevel(i);
    }

    @Deprecated
    public int getExtraHorizontalPaddingLevel() {
        return this.mDelegate.getExtraHorizontalPaddingLevel();
    }

    public void setExtraPaddingApplyToContentEnable(boolean z) {
        this.mDelegate.setExtraPaddingApplyToContentEnable(z);
    }

    public boolean isExtraPaddingApplyToContentEnable() {
        return this.mDelegate.isExtraPaddingApplyToContentEnable();
    }

    public void registerCoordinateScrollView(View view) {
        this.mDelegate.registerCoordinateScrollView(view);
    }

    public void unregisterCoordinateScrollView(View view) {
        this.mDelegate.unregisterCoordinateScrollView(view);
    }

    public void setBottomExtraInset(int i) {
        this.mDelegate.setBottomExtraInset(i);
        int size = getChildFragmentManager().getFragments().size();
        for (int i2 = 0; i2 < size; i2++) {
            androidx.fragment.app.Fragment fragment = getChildFragmentManager().getFragments().get(i2);
            if ((fragment instanceof Fragment) && fragment.isAdded()) {
                ((Fragment) fragment).setBottomExtraInset(i);
            }
        }
    }

    public void setBottomMenuMode(int i) {
        this.mDelegate.setBottomMenuMode(i);
    }

    public int getBottomMenuMode() {
        return this.mDelegate.getBottomMenuMode();
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public Rect getContentInset() {
        return this.mDelegate.getContentInset();
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public boolean requestDispatchContentInset() {
        return this.mDelegate.requestDispatchContentInset();
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void bindViewWithContentInset(View view) {
        this.mDelegate.bindViewWithContentInset(view);
    }

    public void onContentInsetChanged(Rect rect) {
        this.mDelegate.onContentInsetChanged(rect);
        onProcessBindViewWithContentInset(rect);
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void onProcessBindViewWithContentInset(Rect rect) {
        this.mDelegate.onProcessBindViewWithContentInset(rect);
    }

    public void onUpdateArguments(Bundle bundle) {
        if (isStateSaved()) {
            return;
        }
        setArguments(bundle);
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public final void setCorrectNestedScrollMotionEventEnabled(boolean z) {
        this.mDelegate.setCorrectNestedScrollMotionEventEnabled(z);
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void onDispatchNestedScrollOffset(int[] iArr) {
        this.mDelegate.onDispatchNestedScrollOffset(iArr);
    }

    public int getWindowType() {
        WindowBaseInfo windowInfo = getWindowInfo();
        if (windowInfo != null) {
            return windowInfo.windowType;
        }
        return 1;
    }

    public WindowBaseInfo getWindowInfo() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return EnvStateManager.getWindowInfo(activity);
        }
        return null;
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public void dispatchResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        this.mDelegate.dispatchResponsiveLayout(configuration, screenSpec, z);
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public ResponsiveState getResponsiveState() {
        return this.mDelegate.getResponsiveState();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyEvent(KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyEvent(keyEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyShortcutEvent(KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyShortcutEvent(keyEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onTouchEvent(MotionEvent motionEvent) {
        for (androidx.fragment.app.Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onTouchEvent(motionEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        for (androidx.fragment.app.Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onTrackballEvent(motionEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        for (androidx.fragment.app.Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onGenericMotionEvent(motionEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ShortcutsCallback
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> list, Menu menu, int i) {
        for (androidx.fragment.app.Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback)) {
                ((ShortcutsCallback) fragment).onProvideKeyboardShortcuts(list, menu, i);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyDown(i, keyEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyLongPress(i, keyEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyUp(i, keyEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyMultiple(i, i2, keyEvent)) {
                return true;
            }
        }
        return false;
    }

    public FragmentDelegate getDelegate() {
        return this.mDelegate;
    }

    public void hideBottomMenu() {
        hideBottomMenu(true);
    }

    public void showBottomMenu() {
        showBottomMenu(true);
    }

    public void hideBottomMenu(boolean z) {
        this.mDelegate.hideBottomMenu(z);
    }

    public void showBottomMenu(boolean z) {
        this.mDelegate.showBottomMenu(z);
    }

    public void setBottomMenuCustomView(View view) {
        this.mDelegate.setBottomMenuCustomView(view);
    }

    public void removeBottomMenuCustomView() {
        this.mDelegate.removeBottomMenuCustomView();
    }

    public void showBottomMenuCustomView() {
        this.mDelegate.showBottomMenuCustomView();
    }

    public void hideBottomMenuCustomView() {
        this.mDelegate.hideBottomMenuCustomView();
    }

    public void setBottomMenuCustomViewTranslationYWithPx(int i) {
        this.mDelegate.setBottomMenuCustomViewTranslationYWithPx(i);
    }

    public int getBottomMenuCustomViewTranslationY() {
        return this.mDelegate.getBottomMenuCustomViewTranslationY();
    }

    @Override // miuix.appcompat.app.IFragment
    public void setNestedScrollingParentEnabled(boolean z) {
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate != null) {
            fragmentDelegate.setNestedScrollingParentEnabled(z);
        }
    }

    public void addGroupButtons(GroupButtonsConfig groupButtonsConfig) {
        addGroupButtons(groupButtonsConfig, true);
    }

    public void addGroupButtons(GroupButtonsConfig groupButtonsConfig, boolean z) {
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate != null) {
            fragmentDelegate.addGroupButtons(groupButtonsConfig, z);
        }
    }

    public void setGroupButtonsPanelBackground(Drawable drawable) {
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate != null) {
            fragmentDelegate.setGroupButtonsPanelBackground(drawable);
        }
    }

    public void setGroupButtonsPanelBackgroundColor(int i) {
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate != null) {
            fragmentDelegate.setGroupButtonsPanelBackgroundColor(i);
        }
    }

    public void setGroupButtonsPanelBackgroundResource(int i) {
        FragmentDelegate fragmentDelegate = this.mDelegate;
        if (fragmentDelegate != null) {
            fragmentDelegate.setGroupButtonsPanelBackgroundResource(i);
        }
    }
}
