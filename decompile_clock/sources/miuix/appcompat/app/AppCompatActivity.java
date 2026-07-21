package miuix.appcompat.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import miuix.appcompat.R;
import miuix.appcompat.app.floatingactivity.IActivityIdentity;
import miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation;
import miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback;
import miuix.appcompat.app.floatingactivity.OnFloatingCallback;
import miuix.appcompat.app.floatingactivity.OnFloatingModeCallback;
import miuix.appcompat.internal.util.LayoutUIUtils;
import miuix.container.ExtraPaddingObserver;
import miuix.container.ExtraPaddingPolicy;
import miuix.container.ExtraPaddingProcessor;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.WindowBaseInfo;
import miuix.responsive.interfaces.IResponsive;
import miuix.responsive.map.ResponsiveState;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes2.dex */
public class AppCompatActivity extends FragmentActivity implements IActivity, IActivitySwitcherAnimation, IActivityIdentity, IResponsive<Activity>, ExtraPaddingProcessor {
    private AppDelegate mAppDelegate;
    private int mInputViewLimitTextSizeDp;
    private WindowBaseInfo mWindowInfo;

    @Override // miuix.appcompat.app.IActivity
    public void checkThemeLegality() {
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // miuix.responsive.interfaces.IResponsive
    public Activity getResponsiveSubject() {
        return this;
    }

    protected boolean isResponsiveEnabled() {
        return false;
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void onDispatchNestedScrollOffset(int[] iArr) {
    }

    public void onFloatingWindowModeChanged(boolean z) {
    }

    public boolean onFloatingWindowModeChanging(boolean z) {
        return true;
    }

    public void onOptionsMenuViewAdded(Menu menu, Menu menu2) {
    }

    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
    }

    public AppCompatActivity() {
        this.mAppDelegate = new AppDelegate(this, new Callback(), new FloatingModeCallback());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        EnvStateManager.markWindowInfoDirty(this);
        this.mAppDelegate.setResponsiveEnabled(isResponsiveEnabled());
        this.mAppDelegate.onCreate(bundle);
        this.mWindowInfo = EnvStateManager.getWindowInfo(this, null, true);
        this.mInputViewLimitTextSizeDp = MiuixUIUtils.isTallFontLang(this) ? 16 : 27;
        getWindow().getDecorView().post(new Runnable() { // from class: miuix.appcompat.app.AppCompatActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1805lambda$onCreate$0$miuixappcompatappAppCompatActivity();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onCreate$0$miuix-appcompat-app-AppCompatActivity, reason: not valid java name */
    /* synthetic */ void m1805lambda$onCreate$0$miuixappcompatappAppCompatActivity() {
        LayoutUIUtils.resetSearchModeStubInputTextSize(getResources(), findViewById(R.id.search_mode_stub), this.mInputViewLimitTextSizeDp);
    }

    public ActionBar getAppCompatActionBar() {
        return this.mAppDelegate.getActionBar();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(int i) {
        this.mAppDelegate.setContentView(i);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(View view) {
        this.mAppDelegate.setContentView(view);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        this.mAppDelegate.setContentView(view, layoutParams);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        this.mAppDelegate.addContentView(view, layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPostResume() {
        this.mAppDelegate.onPostResume();
    }

    @Override // android.app.Activity
    protected void onTitleChanged(CharSequence charSequence, int i) {
        super.onTitleChanged(charSequence, i);
        this.mAppDelegate.setTitle(charSequence);
    }

    @Override // android.app.Activity
    public void invalidateOptionsMenu() {
        this.mAppDelegate.invalidateOptionsMenu();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public View onCreatePanelView(int i) {
        return this.mAppDelegate.onCreatePanelView(i);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity, android.view.Window.Callback
    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        return this.mAppDelegate.onMenuItemSelected(i, menuItem);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity, android.view.Window.Callback
    public void onPanelClosed(int i, Menu menu) {
        this.mAppDelegate.onPanelClosed(i, menu);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onActionModeStarted(ActionMode actionMode) {
        this.mAppDelegate.onActionModeStarted(actionMode);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onActionModeFinished(ActionMode actionMode) {
        this.mAppDelegate.onActionModeFinished(actionMode);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int i) {
        return this.mAppDelegate.onWindowStartingActionMode(callback, i);
    }

    @Override // android.app.Activity
    public MenuInflater getMenuInflater() {
        return this.mAppDelegate.getMenuInflater();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        beforeConfigurationChanged(getResources().getConfiguration());
        if (!this.mWindowInfo.isDirty()) {
            EnvStateManager.markWindowInfoDirty(this.mWindowInfo);
        }
        this.mAppDelegate.onConfigurationChanged(configuration);
        afterConfigurationChanged(configuration);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        this.mAppDelegate.onSaveInstanceState(bundle);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        this.mAppDelegate.onRestoreInstanceState(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        this.mAppDelegate.onStop();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        this.mAppDelegate.onDestroy();
        EnvStateManager.removeInfoOfContext(this);
        this.mWindowInfo = null;
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (ShortcutsCallback.dispatchKeyDown(getSupportFragmentManager(), i, keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        if (ShortcutsCallback.dispatchKeyLongPress(getSupportFragmentManager(), i, keyEvent)) {
            return true;
        }
        return super.onKeyLongPress(i, keyEvent);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (ShortcutsCallback.dispatchKeyUp(getSupportFragmentManager(), i, keyEvent)) {
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        if (ShortcutsCallback.dispatchKeyMultiple(getSupportFragmentManager(), i, i2, keyEvent)) {
            return true;
        }
        return super.onKeyMultiple(i, i2, keyEvent);
    }

    protected void beforeConfigurationChanged(Configuration configuration) {
        this.mAppDelegate.beforeConfigurationChanged(configuration);
    }

    protected void afterConfigurationChanged(Configuration configuration) {
        this.mAppDelegate.afterConfigurationChanged(configuration);
    }

    public int getWindowType() {
        WindowBaseInfo windowBaseInfo = this.mWindowInfo;
        if (windowBaseInfo != null) {
            return windowBaseInfo.windowType;
        }
        return 1;
    }

    public WindowBaseInfo getWindowInfo() {
        return this.mWindowInfo;
    }

    protected boolean isRegisterResponsive() {
        return this.mAppDelegate.isRegisterResponsive();
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public ResponsiveState getResponsiveState() {
        return this.mAppDelegate.getResponsiveState();
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public void dispatchResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        this.mAppDelegate.dispatchResponsiveLayout(configuration, screenSpec, z);
    }

    public void testNotifyResponseChange(int i) {
        this.mAppDelegate.testNotifyResponseChange(i);
    }

    @Override // android.app.Activity
    public void finish() {
        if (this.mAppDelegate.shouldDelegateActivityFinish()) {
            return;
        }
        realFinish();
    }

    @Override // android.app.Activity
    public boolean isFinishing() {
        return this.mAppDelegate.isDelegateFinishing() || super.isFinishing();
    }

    public void exitFloatingActivityAll() {
        this.mAppDelegate.exitFloatingActivityAll();
    }

    public void realFinish() {
        super.finish();
    }

    public boolean requestExtraWindowFeature(int i) {
        return this.mAppDelegate.requestWindowFeature(i);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity, android.view.Window.Callback
    public boolean onCreatePanelMenu(int i, Menu menu) {
        return this.mAppDelegate.onCreatePanelMenu(i, menu);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity, android.view.Window.Callback
    public boolean onPreparePanel(int i, View view, Menu menu) {
        return this.mAppDelegate.onPreparePanel(i, view, menu);
    }

    @Override // android.app.Activity
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return this.mAppDelegate.startActionMode(callback);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return this.mAppDelegate.onWindowStartingActionMode(callback);
    }

    @Override // miuix.appcompat.app.IActivity
    public void setTranslucentStatus(int i) {
        this.mAppDelegate.setTranslucentStatus(i);
    }

    @Override // miuix.appcompat.app.IActivity
    public int getTranslucentStatus() {
        return this.mAppDelegate.getTranslucentStatus();
    }

    @Override // miuix.appcompat.app.IActivity
    public void setFloatingWindowMode(boolean z) {
        this.mAppDelegate.setFloatingWindowMode(z);
    }

    @Override // miuix.appcompat.app.IActivity
    public boolean isFloatingWindowTheme() {
        return this.mAppDelegate.isFloatingTheme();
    }

    @Override // miuix.appcompat.app.IActivity
    public void setFloatingWindowBorderEnable(boolean z) {
        this.mAppDelegate.setFloatingWindowBorderEnable(z);
    }

    @Override // miuix.appcompat.app.IActivity
    public boolean isInFloatingWindowMode() {
        return this.mAppDelegate.isInFloatingWindowMode();
    }

    public View getFloatingBrightPanel() {
        return this.mAppDelegate.getFloatingBrightPanel();
    }

    public void setOnStatusBarChangeListener(OnStatusBarChangeListener onStatusBarChangeListener) {
        this.mAppDelegate.setOnStatusBarChangeListener(onStatusBarChangeListener);
    }

    public void setOnFloatingWindowCallback(OnFloatingActivityCallback onFloatingActivityCallback) {
        this.mAppDelegate.setOnFloatingWindowCallback(onFloatingActivityCallback);
    }

    public void setEnableSwipToDismiss(boolean z) {
        this.mAppDelegate.setEnableSwipToDismiss(z);
    }

    public void setOnFloatingCallback(OnFloatingCallback onFloatingCallback) {
        this.mAppDelegate.setOnFloatingCallback(onFloatingCallback);
    }

    public void hideFloatingDimBackground() {
        this.mAppDelegate.hideFloatingDimBackground();
    }

    public void hideFloatingBrightPanel() {
        this.mAppDelegate.hideFloatingBrightPanel();
    }

    public void showFloatingBrightPanel() {
        this.mAppDelegate.showFloatingBrightPanel();
    }

    public void setEndActionMenuEnabled(boolean z) {
        this.mAppDelegate.setEndActionMenuEnabled(z);
    }

    public void setHyperActionMenuEnabled(boolean z) {
        this.mAppDelegate.setHyperActionMenuEnabled(z);
    }

    public void setHyperSplitMenuEnabled(boolean z) {
        this.mAppDelegate.setHyperSplitMenuEnabled(z);
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    @Deprecated
    public void setImmersionMenuEnabled(boolean z) {
        this.mAppDelegate.setImmersionMenuEnabled(z);
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    @Deprecated
    public void showImmersionMenu() {
        this.mAppDelegate.showImmersionMenu();
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    @Deprecated
    public void showImmersionMenu(View view, ViewGroup viewGroup) {
        this.mAppDelegate.showImmersionMenu(view, viewGroup);
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    @Deprecated
    public void dismissImmersionMenu(boolean z) {
        this.mAppDelegate.dismissImmersionMenu(z);
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void showEndOverflowMenu() {
        this.mAppDelegate.showEndOverflowMenu();
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void hideEndOverflowMenu() {
        this.mAppDelegate.hideEndOverflowMenu();
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void showOverflowMenu() {
        this.mAppDelegate.showOverflowMenu();
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void hideOverflowMenu() {
        this.mAppDelegate.hideOverflowMenu();
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeOpenEnterAnimation() {
        this.mAppDelegate.executeOpenEnterAnimation();
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeOpenExitAnimation() {
        this.mAppDelegate.executeOpenExitAnimation();
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeCloseEnterAnimation() {
        this.mAppDelegate.executeCloseEnterAnimation();
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivitySwitcherAnimation
    public void executeCloseExitAnimation() {
        this.mAppDelegate.executeCloseExitAnimation();
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraPaddingPolicy(ExtraPaddingPolicy extraPaddingPolicy) {
        this.mAppDelegate.setExtraPaddingPolicy(extraPaddingPolicy);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public ExtraPaddingPolicy getExtraPaddingPolicy() {
        return this.mAppDelegate.getExtraPaddingPolicy();
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void addExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        this.mAppDelegate.addExtraPaddingObserver(extraPaddingObserver);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void removeExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        this.mAppDelegate.removeExtraPaddingObserver(extraPaddingObserver);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingEnable(boolean z) {
        this.mAppDelegate.setExtraHorizontalPaddingEnable(z);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingInitEnable(boolean z) {
        this.mAppDelegate.setExtraHorizontalPaddingInitEnable(z);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public boolean isExtraHorizontalPaddingEnable() {
        return this.mAppDelegate.isExtraHorizontalPaddingEnable();
    }

    @Override // miuix.container.ExtraPaddingObserver
    public boolean setExtraHorizontalPadding(int i) {
        return this.mAppDelegate.setExtraHorizontalPadding(i);
    }

    @Override // miuix.container.ExtraPaddingObserver
    public int getExtraHorizontalPadding() {
        return this.mAppDelegate.getExtraHorizontalPadding();
    }

    @Override // miuix.container.ExtraPaddingObserver
    public void onExtraPaddingChanged(int i) {
        this.mAppDelegate.onExtraPaddingChanged(i);
    }

    public void setExtraPaddingApplyToContentEnable(boolean z) {
        this.mAppDelegate.setExtraPaddingApplyToContentEnable(z);
    }

    public boolean isExtraPaddingApplyToContentEnable() {
        return this.mAppDelegate.isExtraPaddingApplyToContentEnable();
    }

    @Deprecated
    public void setExtraHorizontalPaddingLevel(int i) {
        this.mAppDelegate.setExtraHorizontalPaddingLevel(i);
    }

    @Deprecated
    public int getExtraHorizontalPaddingLevel() {
        return this.mAppDelegate.getExtraHorizontalPaddingLevel();
    }

    @Override // miuix.appcompat.app.floatingactivity.IActivityIdentity
    public String getActivityIdentity() {
        return this.mAppDelegate.getActivityIdentity();
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public boolean requestDispatchContentInset() {
        return this.mAppDelegate.requestDispatchContentInset();
    }

    public void registerCoordinateScrollView(View view) {
        this.mAppDelegate.registerCoordinateScrollView(view);
    }

    public void unregisterCoordinateScrollView(View view) {
        this.mAppDelegate.unregisterCoordinateScrollView(view);
    }

    public void setBottomExtraInset(int i) {
        this.mAppDelegate.setBottomExtraInset(i);
    }

    public void setBottomMenuMode(int i) {
        this.mAppDelegate.setBottomMenuMode(i);
    }

    public int getBottomMenuMode() {
        return this.mAppDelegate.getBottomMenuMode();
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void bindViewWithContentInset(View view) {
        this.mAppDelegate.bindViewWithContentInset(view);
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public Rect getContentInset() {
        return this.mAppDelegate.getContentInset();
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void onContentInsetChanged(Rect rect) {
        this.mAppDelegate.onContentInsetChanged(rect);
        onProcessBindViewWithContentInset(rect);
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void onProcessBindViewWithContentInset(Rect rect) {
        this.mAppDelegate.onProcessBindViewWithContentInset(rect);
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public final void setCorrectNestedScrollMotionEventEnabled(boolean z) {
        this.mAppDelegate.setCorrectNestedScrollMotionEventEnabled(z);
    }

    public void addGroupButtons(GroupButtonsConfig groupButtonsConfig) {
        addGroupButtons(groupButtonsConfig, true);
    }

    public void addGroupButtons(GroupButtonsConfig groupButtonsConfig, boolean z) {
        AppDelegate appDelegate = this.mAppDelegate;
        if (appDelegate != null) {
            appDelegate.addGroupButtons(groupButtonsConfig, z);
        }
    }

    public void setGroupButtonsPanelBackground(Drawable drawable) {
        AppDelegate appDelegate = this.mAppDelegate;
        if (appDelegate != null) {
            appDelegate.setGroupButtonsPanelBackground(drawable);
        }
    }

    public void setGroupButtonsPanelBackgroundColor(int i) {
        AppDelegate appDelegate = this.mAppDelegate;
        if (appDelegate != null) {
            appDelegate.setGroupButtonsPanelBackgroundColor(i);
        }
    }

    public void setGroupButtonsPanelBackgroundResource(int i) {
        AppDelegate appDelegate = this.mAppDelegate;
        if (appDelegate != null) {
            appDelegate.setGroupButtonsPanelBackgroundResource(i);
        }
    }

    private class Callback implements ActivityCallback {
        private Callback() {
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public void onCreate(Bundle bundle) {
            AppCompatActivity.super.onCreate(bundle);
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public void onPostResume() {
            AppCompatActivity.super.onPostResume();
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public void onStop() {
            AppCompatActivity.super.onStop();
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public boolean onMenuItemSelected(int i, MenuItem menuItem) {
            return AppCompatActivity.super.onMenuItemSelected(i, menuItem);
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public void onPanelClosed(int i, Menu menu) {
            AppCompatActivity.super.onPanelClosed(i, menu);
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public View onCreatePanelView(int i) {
            return AppCompatActivity.super.onCreatePanelView(i);
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public boolean onCreatePanelMenu(int i, Menu menu) {
            return AppCompatActivity.super.onCreatePanelMenu(i, menu);
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public boolean onPreparePanel(int i, View view, Menu menu) {
            return AppCompatActivity.super.onPreparePanel(i, view, menu);
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public void onPanelViewAdded(int i, View view, Menu menu, Menu menu2) {
            AppCompatActivity.this.onOptionsMenuViewAdded(menu, menu2);
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public void onConfigurationChanged(Configuration configuration) {
            AppCompatActivity.super.onConfigurationChanged(configuration);
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public void onSaveInstanceState(Bundle bundle) {
            AppCompatActivity.super.onSaveInstanceState(bundle);
        }

        @Override // miuix.appcompat.app.ActivityCallback
        public void onRestoreInstanceState(Bundle bundle) {
            AppCompatActivity.super.onRestoreInstanceState(bundle);
        }
    }

    private class FloatingModeCallback implements OnFloatingModeCallback {
        private FloatingModeCallback() {
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingModeCallback
        public boolean onFloatingWindowModeChanging(boolean z) {
            return AppCompatActivity.this.onFloatingWindowModeChanging(z);
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingModeCallback
        public void onFloatingWindowModeChanged(boolean z) {
            AppCompatActivity.this.onFloatingWindowModeChanged(z);
        }
    }

    public void hideBottomMenu() {
        hideBottomMenu(true);
    }

    public void showBottomMenu() {
        showBottomMenu(true);
    }

    public void hideBottomMenu(boolean z) {
        this.mAppDelegate.hideBottomMenu(z);
    }

    public void showBottomMenu(boolean z) {
        this.mAppDelegate.showBottomMenu(z);
    }

    public void setBottomMenuCustomView(View view) {
        this.mAppDelegate.setBottomMenuCustomView(view);
    }

    public void removeBottomMenuCustomView() {
        this.mAppDelegate.removeBottomMenuCustomView();
    }

    public void showBottomMenuCustomView() {
        this.mAppDelegate.showBottomMenuCustomView();
    }

    public void hideBottomMenuCustomView() {
        this.mAppDelegate.hideBottomMenuCustomView();
    }

    public void setBottomMenuCustomViewTranslationYWithPx(int i) {
        this.mAppDelegate.setBottomMenuCustomViewTranslationYWithPx(i);
    }

    public int getBottomMenuCustomViewTranslationY() {
        return this.mAppDelegate.getBottomMenuCustomViewTranslationY();
    }
}
