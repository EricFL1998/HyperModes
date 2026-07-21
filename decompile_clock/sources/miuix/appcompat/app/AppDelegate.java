package miuix.appcompat.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.appcompat.view.WindowCallbackWrapper;
import androidx.lifecycle.LifecycleOwner;
import java.util.Arrays;
import java.util.List;
import miuix.appcompat.R;
import miuix.appcompat.app.floatingactivity.FloatingActivitySwitcher;
import miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback;
import miuix.appcompat.app.floatingactivity.OnFloatingCallback;
import miuix.appcompat.app.floatingactivity.OnFloatingModeCallback;
import miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper;
import miuix.appcompat.app.floatingactivity.helper.FloatingHelperFactory;
import miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingActivitySwitcher;
import miuix.appcompat.internal.app.widget.ActionBarContainer;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.appcompat.internal.app.widget.ActionBarView;
import miuix.appcompat.internal.util.AppcompatClassPreLoader;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.container.ExtraPaddingObserver;
import miuix.container.ExtraPaddingPolicy;
import miuix.core.util.EnvStateManager;
import miuix.core.util.variable.WindowWrapper;
import miuix.internal.util.AttributeResolver;
import miuix.os.DeviceHelper;
import miuix.responsive.interfaces.IResponsive;
import miuix.responsive.map.ResponsiveState;
import miuix.responsive.map.ScreenSpec;
import miuix.responsive.page.manager.BaseResponseStateManager;
import miuix.view.SearchActionMode;

/* JADX INFO: loaded from: classes2.dex */
class AppDelegate extends ActionBarDelegateImpl implements IResponsive<Activity> {
    private static final String ACTION_BAR_TAG = "miuix:ActionBar";
    private ActionBarContainer mActionBarContainer;
    private ActivityCallback mActivityCallback;
    private final String mActivityIdentity;
    private AppCompatWindowCallback mAppCompatWindowCallback;
    private ViewGroup mContentParent;
    private boolean mDelegateFinished;
    private OnFloatingModeCallback mFloatingModeCallback;
    private ViewGroup mFloatingRoot;
    private BaseFloatingActivityHelper mFloatingWindowHelper;
    private final Runnable mInvalidateMenuRunnable;
    private boolean mIsFloatingTheme;
    private boolean mIsFloatingWindow;
    private boolean mIsUserResponsiveEnabled;
    private LayoutInflater mLayoutInflater;
    private BaseResponseStateManager mResponsiveStateManager;
    private boolean mSplitActionBarEnable;
    private ActionBarOverlayLayout mSubDecor;
    private CharSequence mTitle;
    private int mUIMode;
    private Runnable mUpdateWindowInfoRunnable;
    private Boolean mUserIsFloatingWindow;
    Window mWindow;

    AppDelegate(AppCompatActivity appCompatActivity, ActivityCallback activityCallback, OnFloatingModeCallback onFloatingModeCallback) {
        super(appCompatActivity);
        this.mIsUserResponsiveEnabled = false;
        this.mIsFloatingTheme = false;
        this.mIsFloatingWindow = false;
        this.mUserIsFloatingWindow = null;
        this.mFloatingRoot = null;
        this.mDelegateFinished = false;
        this.mInvalidateMenuRunnable = new Runnable() { // from class: miuix.appcompat.app.AppDelegate.3
            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r0v1, types: [android.view.Menu, miuix.appcompat.internal.view.menu.MenuBuilder] */
            /* JADX WARN: Type inference fix 'apply assigned field type' failed
            java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
            	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
            	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
            	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
             */
            @Override // java.lang.Runnable
            public void run() {
                ?? CreateMenu = AppDelegate.this.createMenu();
                if ((AppDelegate.this.isEndActionMenuEnabled() || AppDelegate.this.mSplitActionBarEnable) && AppDelegate.this.mActivityCallback.onCreatePanelMenu(0, CreateMenu) && AppDelegate.this.mActivityCallback.onPreparePanel(0, null, CreateMenu)) {
                    AppDelegate.this.setMenu(CreateMenu);
                } else {
                    AppDelegate.this.setMenu(null);
                }
            }
        };
        this.mActivityIdentity = String.valueOf(SystemClock.elapsedRealtimeNanos());
        this.mActivityCallback = activityCallback;
        this.mFloatingModeCallback = onFloatingModeCallback;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public LifecycleOwner getLifecycleOwner() {
        return this.mActivity;
    }

    public void setResponsiveEnabled(boolean z) {
        this.mIsUserResponsiveEnabled = z;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void onCreate(Bundle bundle) {
        this.mActivity.checkThemeLegality();
        if (!AppcompatClassPreLoader.sIsActionBarClassInit) {
            AppcompatClassPreLoader.sIsActionBarClassInit = true;
            AppcompatClassPreLoader.preloadClass(getThemedContext().getApplicationContext());
        }
        boolean zResolveBoolean = AttributeResolver.resolveBoolean(this.mActivity, R.attr.windowExtraPaddingHorizontalEnable, AttributeResolver.resolveInt(this.mActivity, R.attr.windowExtraPaddingHorizontal, 0) != 0);
        if (this.mExtraPaddingEnable) {
            zResolveBoolean = true;
        }
        boolean zResolveBoolean2 = AttributeResolver.resolveBoolean(this.mActivity, R.attr.windowExtraPaddingHorizontalInitEnable, this.mExtraPaddingInitEnable);
        if (this.mExtraPaddingInitEnable) {
            zResolveBoolean2 = true;
        }
        boolean zResolveBoolean3 = this.mExtraPaddingApplyToContentEnable ? true : AttributeResolver.resolveBoolean(this.mActivity, R.attr.windowExtraPaddingApplyToContentEnable, this.mExtraPaddingApplyToContentEnable);
        setExtraHorizontalPaddingEnable(zResolveBoolean);
        setExtraHorizontalPaddingInitEnable(zResolveBoolean2);
        setExtraPaddingApplyToContentEnable(zResolveBoolean3);
        this.mActivityCallback.onCreate(bundle);
        installSubDecor();
        installFloatingSwitcher(this.mIsFloatingTheme, bundle);
    }

    public void installFloatingSwitcher(boolean z, Bundle bundle) {
        if (z) {
            Intent intent = this.mActivity.getIntent();
            if (intent != null && MultiAppFloatingActivitySwitcher.isFromMultiApp(intent)) {
                MultiAppFloatingActivitySwitcher.install(this.mActivity, intent, bundle);
            } else {
                FloatingActivitySwitcher.install(this.mActivity, bundle);
            }
        }
    }

    View getActionBarOverlay() {
        return this.mSubDecor;
    }

    private void ensureWindow() {
        Window window = this.mWindow;
        if (window != null) {
            return;
        }
        if (window == null && this.mActivity != null) {
            attachToWindow(this.mActivity.getWindow());
        }
        if (this.mWindow == null) {
            throw new IllegalStateException("We have not been given a Window");
        }
    }

    private void attachToWindow(Window window) {
        if (this.mWindow != null) {
            throw new IllegalStateException("AppCompat has already installed itself into the Window");
        }
        Window.Callback callback = window.getCallback();
        if (callback instanceof AppCompatWindowCallback) {
            throw new IllegalStateException("AppCompat has already installed itself into the Window");
        }
        AppCompatWindowCallback appCompatWindowCallback = new AppCompatWindowCallback(callback);
        this.mAppCompatWindowCallback = appCompatWindowCallback;
        window.setCallback(appCompatWindowCallback);
        this.mWindow = window;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public ActionBar createActionBar() {
        if (!this.mSubDecorInstalled) {
            installSubDecor();
        }
        if (this.mSubDecor == null) {
            return null;
        }
        return new ActionBarImpl(this.mActivity, this.mSubDecor);
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public Context getThemedContext() {
        return this.mActivity;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onStop() {
        this.mActivityCallback.onStop();
        dismissImmersionMenu(false);
        ActionBarImpl actionBarImpl = (ActionBarImpl) getActionBar();
        if (actionBarImpl != null) {
            actionBarImpl.setShowHideAnimationEnabled(false);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onPostResume() {
        this.mActivityCallback.onPostResume();
        ActionBarImpl actionBarImpl = (ActionBarImpl) getActionBar();
        if (actionBarImpl != null) {
            actionBarImpl.setShowHideAnimationEnabled(true);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1, types: [miuix.appcompat.app.ActivityCallback] */
    /* JADX WARN: Type inference failed for: r1v2, types: [miuix.appcompat.app.ActivityCallback] */
    /* JADX WARN: Type inference failed for: r4v0, types: [miuix.appcompat.app.AppDelegate] */
    /* JADX WARN: Type inference failed for: r5v2, types: [miuix.appcompat.internal.view.menu.MenuBuilder] */
    /* JADX WARN: Type inference failed for: r5v3, types: [miuix.appcompat.internal.view.menu.MenuBuilder] */
    /* JADX WARN: Type inference failed for: r5v4, types: [android.view.Menu, miuix.appcompat.internal.view.menu.MenuBuilder] */
    /* JADX WARN: Type inference failed for: r5v5, types: [android.view.Menu, miuix.appcompat.internal.view.menu.MenuBuilder] */
    /* JADX WARN: Type inference failed for: r5v8 */
    /* JADX WARN: Type inference failed for: r5v9 */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // miuix.appcompat.app.ActionBarDelegate
    public View onCreatePanelView(int i) {
        ?? CreateMenu;
        if (i != 0) {
            return this.mActivityCallback.onCreatePanelView(i);
        }
        if (isEndActionMenuEnabled() || this.mSplitActionBarEnable) {
            ?? r5 = this.mMenu;
            boolean zOnPreparePanel = true;
            r5 = r5;
            if (this.mActionMode == null) {
                if (r5 == 0) {
                    CreateMenu = createMenu();
                    setMenu(CreateMenu);
                    CreateMenu.stopDispatchingItemsChanged();
                    zOnPreparePanel = this.mActivityCallback.onCreatePanelMenu(0, CreateMenu);
                }
                if (zOnPreparePanel) {
                    r5 = CreateMenu;
                    r5.stopDispatchingItemsChanged();
                    zOnPreparePanel = this.mActivityCallback.onPreparePanel(0, null, r5);
                }
            } else if (r5 == 0) {
                zOnPreparePanel = false;
            }
            if (zOnPreparePanel) {
                r5.startDispatchingItemsChanged();
            } else {
                setMenu(null);
            }
        }
        return null;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public boolean onCreatePanelMenu(int i, Menu menu) {
        return i != 0 && this.mActivityCallback.onCreatePanelMenu(i, menu);
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public boolean onPreparePanel(int i, View view, Menu menu) {
        return i != 0 && this.mActivityCallback.onPreparePanel(i, view, menu);
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void onPanelViewAdded(int i, View view, Menu menu, Menu menu2) {
        this.mActivityCallback.onPanelViewAdded(i, view, menu, menu2);
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        boolean zOnNavigateUpFromChild;
        if (this.mActivityCallback.onMenuItemSelected(i, menuItem)) {
            return true;
        }
        if (i == 0 && menuItem.getItemId() == 16908332 && getActionBar() != null && (getActionBar().getDisplayOptions() & 4) != 0) {
            if (this.mActivity.getParent() == null) {
                zOnNavigateUpFromChild = this.mActivity.onNavigateUp();
            } else {
                zOnNavigateUpFromChild = this.mActivity.getParent().onNavigateUpFromChild(this.mActivity);
            }
            if (!zOnNavigateUpFromChild) {
                this.mActivity.finish();
            }
        }
        return false;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void onPanelClosed(int i, Menu menu) {
        this.mActivityCallback.onPanelClosed(i, menu);
    }

    private void installSubDecor() {
        ActionBarOverlayLayout actionBarOverlayLayout;
        if (this.mSubDecorInstalled) {
            return;
        }
        ensureWindow();
        this.mSubDecorInstalled = true;
        Window window = this.mActivity.getWindow();
        this.mLayoutInflater = window.getLayoutInflater();
        TypedArray typedArrayObtainStyledAttributes = this.mActivity.obtainStyledAttributes(R.styleable.Window);
        if (typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_responsiveEnabled, this.mIsUserResponsiveEnabled)) {
            this.mResponsiveStateManager = new BaseResponseStateManager(this) { // from class: miuix.appcompat.app.AppDelegate.1
                @Override // miuix.responsive.page.manager.BaseStateManager
                protected Context getContext() {
                    return AppDelegate.this.mActivity;
                }
            };
        }
        if (typedArrayObtainStyledAttributes.getInt(R.styleable.Window_windowLayoutMode, 0) == 1) {
            this.mActivity.getWindow().setGravity(80);
        }
        if (!typedArrayObtainStyledAttributes.hasValue(R.styleable.Window_windowActionBar)) {
            Log.i("MiuixException", "miuix theme parse error!! TypedArray:" + typedArrayObtainStyledAttributes);
            typedArrayObtainStyledAttributes.recycle();
            throw new IllegalStateException("You need to use a miuix theme (or descendant) such as Theme.DayNight with this activity.\n\tR.styleable.Window:" + Arrays.toString(R.styleable.Window) + "\n\tR.styleable.Window_windowActionBar:" + R.styleable.Window_windowActionBar + "\n\t0x" + Integer.toHexString(R.styleable.Window_windowActionBar) + "\n\t theme:" + this.mActivity.getTheme());
        }
        if (typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_windowActionBar, false)) {
            requestWindowFeature(8);
        }
        if (typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_windowActionBarOverlay, false)) {
            requestWindowFeature(9);
        }
        this.mIsFloatingTheme = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_isMiuixFloatingTheme, false);
        this.mIsFloatingWindow = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_windowFloating, false);
        setTranslucentStatus(typedArrayObtainStyledAttributes.getInt(R.styleable.Window_windowTranslucentStatus, 0));
        this.mUIMode = this.mActivity.getResources().getConfiguration().uiMode;
        installToDecor(window);
        ActionBarOverlayLayout actionBarOverlayLayout2 = this.mSubDecor;
        if (actionBarOverlayLayout2 != null) {
            actionBarOverlayLayout2.setCallback(this.mActivity);
            this.mSubDecor.setContentInsetStateCallback(this.mActivity);
            this.mSubDecor.addExtraPaddingObserver(this.mActivity);
            this.mSubDecor.setTranslucentStatus(getTranslucentStatus());
        }
        if (this.mHasActionBar && (actionBarOverlayLayout = this.mSubDecor) != null) {
            this.mActionBarContainer = (ActionBarContainer) actionBarOverlayLayout.findViewById(R.id.action_bar_container);
            this.mSubDecor.setOverlayMode(this.mOverlayActionBar);
            this.mActionBarView = (ActionBarView) this.mSubDecor.findViewById(R.id.action_bar);
            this.mActionBarView.setLifecycleOwner(getLifecycleOwner());
            this.mActionBarView.setWindowCallback(this.mActivity);
            if (this.mFeatureIndeterminateProgress) {
                this.mActionBarView.initIndeterminateProgress();
            }
            if (isEndActionMenuEnabled()) {
                this.mActionBarView.setEndActionMenuEnable(true);
            }
            if (this.mActionBarView.getCustomNavigationView() != null) {
                this.mActionBarView.setDisplayOptions(this.mActionBarView.getDisplayOptions() | 16);
            }
            boolean zEquals = "splitActionBarWhenNarrow".equals(getUiOptionsFromMetadata());
            if (zEquals) {
                this.mSplitActionBarEnable = this.mActivity.getResources().getBoolean(R.bool.abc_split_action_bar_is_narrow);
            } else {
                this.mSplitActionBarEnable = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_windowSplitActionBar, false);
            }
            if (this.mSplitActionBarEnable) {
                addSplitActionBar(true, zEquals, this.mSubDecor);
                setHyperSplitMenuEnabled(typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_hyperSplitMenuEnabled, false));
            }
            if (typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_endActionMenuEnabled, false)) {
                setEndActionMenuEnabled(true, typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_hyperActionMenuEnabled, false), false);
            } else if (this.mActivity.getWindow() != null) {
                this.mActivity.getWindow().getDecorView().post(this.mInvalidateMenuRunnable);
            }
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    public void setFloatingWindowMode(boolean z) {
        this.mUserIsFloatingWindow = Boolean.valueOf(z);
        setFloatingWindowMode(z, this.mUIMode, true, true);
    }

    public boolean isFloatingTheme() {
        return this.mIsFloatingTheme;
    }

    public boolean isInFloatingWindowMode() {
        Boolean bool = this.mUserIsFloatingWindow;
        return bool == null ? shouldShowFloatingActivityTabletMode() : bool.booleanValue();
    }

    public void setFloatingWindowBorderEnable(boolean z) {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.setFloatingWindowBorderEnable(z);
        }
    }

    public View getFloatingBrightPanel() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            return baseFloatingActivityHelper.getFloatingBrightPanel();
        }
        return null;
    }

    private void updateSystemUiStateInFloatingTheme(boolean z) {
        Window window = this.mActivity.getWindow();
        int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
        boolean z2 = ((systemUiVisibility & 1024) != 0) || (getTranslucentStatus() != 0);
        if (!z) {
            systemUiVisibility = z2 ? systemUiVisibility | 1024 : systemUiVisibility & (-1025);
            if (Build.VERSION.SDK_INT >= 30) {
                if (z2) {
                    window.setDecorFitsSystemWindows(false);
                } else {
                    window.setDecorFitsSystemWindows(true);
                }
            }
        } else {
            window.addFlags(201326592);
            if (Build.VERSION.SDK_INT >= 30) {
                window.setDecorFitsSystemWindows(false);
            }
        }
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    private void setFloatingWindowMode(boolean z, int i, boolean z2, boolean z3) {
        if (this.mIsFloatingTheme) {
            if (z3 || miuix.os.Build.IS_TABLET) {
                if (this.mIsFloatingWindow != z && this.mFloatingModeCallback.onFloatingWindowModeChanging(z)) {
                    this.mIsFloatingWindow = z;
                    this.mFloatingWindowHelper.setFloatingWindowMode(z);
                    updateSystemUiStateInFloatingTheme(this.mIsFloatingWindow);
                    ViewGroup.LayoutParams floatingLayoutParam = this.mFloatingWindowHelper.getFloatingLayoutParam();
                    if (floatingLayoutParam != null) {
                        if (z) {
                            floatingLayoutParam.height = -2;
                            floatingLayoutParam.width = -2;
                        } else {
                            floatingLayoutParam.height = -1;
                            floatingLayoutParam.width = -1;
                        }
                    }
                    ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
                    if (actionBarOverlayLayout != null) {
                        actionBarOverlayLayout.requestLayout();
                        this.mSubDecor.onFloatingModeChanged(z);
                    }
                    if (z2) {
                        notifyFloatWindowModeChanged(z);
                        return;
                    }
                    return;
                }
                if (i != this.mUIMode) {
                    this.mUIMode = i;
                    this.mFloatingWindowHelper.setFloatingWindowMode(z);
                }
            }
        }
    }

    private void notifyFloatWindowModeChanged(boolean z) {
        this.mFloatingModeCallback.onFloatingWindowModeChanged(z);
    }

    public void setContentView(int i) {
        if (!this.mSubDecorInstalled) {
            installSubDecor();
        }
        ViewGroup viewGroup = this.mContentParent;
        if (viewGroup != null) {
            viewGroup.removeAllViews();
            this.mLayoutInflater.inflate(i, this.mContentParent);
        }
        this.mAppCompatWindowCallback.getWrapped().onContentChanged();
    }

    public void setContentView(View view) {
        setContentView(view, new ViewGroup.LayoutParams(-1, -1));
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        if (!this.mSubDecorInstalled) {
            installSubDecor();
        }
        ViewGroup viewGroup = this.mContentParent;
        if (viewGroup != null) {
            viewGroup.removeAllViews();
            this.mContentParent.addView(view, layoutParams);
        }
        this.mAppCompatWindowCallback.getWrapped().onContentChanged();
    }

    public void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        if (!this.mSubDecorInstalled) {
            installSubDecor();
        }
        ViewGroup viewGroup = this.mContentParent;
        if (viewGroup != null) {
            viewGroup.addView(view, layoutParams);
        }
        this.mAppCompatWindowCallback.getWrapped().onContentChanged();
    }

    private boolean shouldShowFloatingActivityTabletMode() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        return baseFloatingActivityHelper != null && baseFloatingActivityHelper.isFloatingModeSupport();
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private void installToDecor(Window window) {
        ViewGroup viewGroupReplaceSubDecor;
        this.mFloatingWindowHelper = this.mIsFloatingTheme ? FloatingHelperFactory.get(this.mActivity) : null;
        this.mFloatingRoot = null;
        View viewInflate = View.inflate(this.mActivity, getDecorViewLayoutRes(window), null);
        View view = viewInflate;
        if (this.mFloatingWindowHelper != null) {
            boolean zShouldShowFloatingActivityTabletMode = shouldShowFloatingActivityTabletMode();
            this.mIsFloatingWindow = zShouldShowFloatingActivityTabletMode;
            this.mFloatingWindowHelper.setFloatingWindowMode(zShouldShowFloatingActivityTabletMode);
            viewGroupReplaceSubDecor = this.mFloatingWindowHelper.replaceSubDecor(viewInflate, this.mIsFloatingWindow);
            this.mFloatingRoot = viewGroupReplaceSubDecor;
            updateSystemUiStateInFloatingTheme(this.mIsFloatingWindow);
            if (this.mFloatingWindowHelper.shouldInterceptBack()) {
                view = viewGroupReplaceSubDecor;
                this.mActivity.getOnBackPressedDispatcher().addCallback(this.mActivity, new OnBackPressedCallback(true) { // from class: miuix.appcompat.app.AppDelegate.2
                    @Override // androidx.activity.OnBackPressedCallback
                    public void handleOnBackPressed() {
                        AppDelegate.this.mFloatingWindowHelper.onBackPressed();
                    }
                });
                view = viewGroupReplaceSubDecor;
            }
        }
        view = viewGroupReplaceSubDecor;
        if (!this.mUserExtraPaddingPolicy) {
            initExtraPaddingPolicy();
        }
        View viewFindViewById = view.findViewById(R.id.action_bar_overlay_layout);
        if (viewFindViewById instanceof ActionBarOverlayLayout) {
            ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) viewFindViewById;
            this.mSubDecor = actionBarOverlayLayout;
            actionBarOverlayLayout.setLifecycleOwner(getLifecycleOwner());
            this.mSubDecor.setExtraHorizontalPaddingEnable(this.mExtraPaddingEnable);
            this.mSubDecor.setExtraHorizontalPaddingInitEnable(this.mExtraPaddingInitEnable);
            this.mSubDecor.setExtraPaddingApplyToContentEnable(this.mExtraPaddingApplyToContentEnable);
            this.mSubDecor.setExtraPaddingPolicy(getExtraPaddingPolicy());
            ViewGroup viewGroup = (ViewGroup) this.mSubDecor.findViewById(android.R.id.content);
            ViewGroup viewGroup2 = (ViewGroup) window.findViewById(android.R.id.content);
            if (viewGroup2 != null) {
                while (viewGroup2.getChildCount() > 0) {
                    View childAt = viewGroup2.getChildAt(0);
                    viewGroup2.removeViewAt(0);
                    viewGroup.addView(childAt);
                }
                viewGroup2.setId(-1);
                viewGroup.setId(android.R.id.content);
                if (viewGroup2 instanceof FrameLayout) {
                    ((FrameLayout) viewGroup2).setForeground(null);
                }
            }
        }
        window.setContentView(view);
        ActionBarOverlayLayout actionBarOverlayLayout2 = this.mSubDecor;
        if (actionBarOverlayLayout2 != null) {
            this.mContentParent = (ViewGroup) actionBarOverlayLayout2.findViewById(android.R.id.content);
        }
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.init(this.mFloatingRoot, shouldShowFloatingActivityTabletMode());
        }
    }

    void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        if (this.mActionBarView != null) {
            this.mActionBarView.setWindowTitle(charSequence);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public View getView() {
        return this.mSubDecor;
    }

    private int getDecorViewLayoutRes(Window window) {
        int i;
        Context context = window.getContext();
        if (AttributeResolver.resolveBoolean(context, R.attr.windowActionBar, false)) {
            if (AttributeResolver.resolveBoolean(context, R.attr.windowActionBarMovable, false)) {
                i = R.layout.miuix_appcompat_screen_action_bar_movable;
            } else {
                i = R.layout.miuix_appcompat_screen_action_bar;
            }
        } else {
            i = R.layout.miuix_appcompat_screen_simple;
        }
        int iResolve = AttributeResolver.resolve(context, R.attr.startingWindowOverlay);
        if (iResolve > 0 && isSystemProcess() && isWindowActionBarEnabled(context)) {
            i = iResolve;
        }
        if (!window.isFloating() && (window.getCallback() instanceof Dialog)) {
            WindowWrapper.setTranslucentStatus(window, AttributeResolver.resolveInt(context, R.attr.windowTranslucentStatus, 0));
        }
        return i;
    }

    private boolean isSystemProcess() {
        return "android".equals(getActivity().getApplicationContext().getApplicationInfo().packageName);
    }

    private static boolean isWindowActionBarEnabled(Context context) {
        return AttributeResolver.resolveBoolean(context, R.attr.windowActionBar, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    protected boolean onCreateImmersionMenu(MenuBuilder menuBuilder) {
        return this.mActivity.onCreateOptionsMenu(menuBuilder);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    protected boolean onPrepareImmersionMenu(MenuBuilder menuBuilder) {
        return this.mActivity.onPrepareOptionsMenu(menuBuilder);
    }

    public void setOnStatusBarChangeListener(OnStatusBarChangeListener onStatusBarChangeListener) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setOnStatusBarChangeListener(onStatusBarChangeListener);
        }
    }

    public void setOnFloatingCallback(OnFloatingCallback onFloatingCallback) {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.setOnFloatingCallback(onFloatingCallback);
        }
    }

    public void setOnFloatingWindowCallback(OnFloatingActivityCallback onFloatingActivityCallback) {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.setOnFloatingWindowCallback(onFloatingActivityCallback);
        }
    }

    public void exitFloatingActivityAll() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.exitFloatingActivityAll();
        }
    }

    public void setEnableSwipToDismiss(boolean z) {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.setEnableSwipToDismiss(z);
        }
    }

    public void hideFloatingDimBackground() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.hideFloatingDimBackground();
        }
    }

    public void hideFloatingBrightPanel() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.hideFloatingBrightPanel();
        }
    }

    public void showFloatingBrightPanel() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.showFloatingBrightPanel();
        }
    }

    public void executeOpenEnterAnimation() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.executeOpenEnterAnimation();
        }
    }

    public void executeOpenExitAnimation() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.executeOpenExitAnimation();
        }
    }

    public void executeCloseEnterAnimation() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.executeCloseEnterAnimation();
        }
    }

    public void executeCloseExitAnimation() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper != null) {
            baseFloatingActivityHelper.executeCloseExitAnimation();
        }
    }

    public boolean shouldDelegateActivityFinish() {
        BaseFloatingActivityHelper baseFloatingActivityHelper = this.mFloatingWindowHelper;
        if (baseFloatingActivityHelper == null) {
            return false;
        }
        boolean zDelegateFinishFloatingActivityInternal = baseFloatingActivityHelper.delegateFinishFloatingActivityInternal();
        if (zDelegateFinishFloatingActivityInternal) {
            this.mDelegateFinished = true;
        }
        return zDelegateFinishFloatingActivityInternal;
    }

    public boolean isDelegateFinishing() {
        return this.mDelegateFinished;
    }

    public void beforeConfigurationChanged(Configuration configuration) {
        BaseResponseStateManager baseResponseStateManager = this.mResponsiveStateManager;
        if (baseResponseStateManager != null) {
            baseResponseStateManager.beforeConfigurationChanged(configuration);
        }
    }

    public void afterConfigurationChanged(Configuration configuration) {
        BaseResponseStateManager baseResponseStateManager = this.mResponsiveStateManager;
        if (baseResponseStateManager != null) {
            baseResponseStateManager.afterConfigurationChanged(configuration);
        }
    }

    public boolean isRegisterResponsive() {
        return this.mResponsiveStateManager != null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // miuix.responsive.interfaces.IResponsive
    public Activity getResponsiveSubject() {
        return this.mActivity;
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public ResponsiveState getResponsiveState() {
        BaseResponseStateManager baseResponseStateManager = this.mResponsiveStateManager;
        if (baseResponseStateManager != null) {
            return baseResponseStateManager.getState();
        }
        return null;
    }

    public void testNotifyResponseChange(int i) {
        BaseResponseStateManager baseResponseStateManager = this.mResponsiveStateManager;
        if (baseResponseStateManager != null) {
            baseResponseStateManager.testNotifyResponseChange(i);
        }
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public void dispatchResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        onResponsiveLayout(configuration, screenSpec, z);
    }

    @Override // miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        if (this.mActivity instanceof IResponsive) {
            this.mActivity.onResponsiveLayout(configuration, screenSpec, z);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onConfigurationChanged(final Configuration configuration) {
        int iDetectType;
        EnvStateManager.updateWindowInfo(this.mActivity, this.mActivity.getWindowInfo(), configuration, false);
        this.mUpdateWindowInfoRunnable = new Runnable() { // from class: miuix.appcompat.app.AppDelegate$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1806lambda$onConfigurationChanged$0$miuixappcompatappAppDelegate(configuration);
            }
        };
        if (this.mActivity.getWindow() != null) {
            this.mActivity.getWindow().getDecorView().post(this.mUpdateWindowInfoRunnable);
        }
        super.onConfigurationChanged(configuration);
        if (!this.mUserExtraPaddingPolicy && this.mDeviceType != (iDetectType = DeviceHelper.detectType(this.mActivity))) {
            this.mDeviceType = iDetectType;
            initExtraPaddingPolicy();
            ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
            if (actionBarOverlayLayout != null) {
                actionBarOverlayLayout.setExtraPaddingPolicy(this.mExtraPaddingPolicy);
            }
        }
        this.mActivityCallback.onConfigurationChanged(configuration);
        if (isImmersionMenuShowing()) {
            showImmersionMenu();
        }
    }

    /* JADX INFO: renamed from: lambda$onConfigurationChanged$0$miuix-appcompat-app-AppDelegate, reason: not valid java name */
    /* synthetic */ void m1806lambda$onConfigurationChanged$0$miuixappcompatappAppDelegate(Configuration configuration) {
        EnvStateManager.updateWindowInfo(this.mActivity, this.mActivity.getWindowInfo(), null, true);
        setFloatingWindowMode(isInFloatingWindowMode(), configuration.uiMode, true, miuix.os.Build.IS_FOLDABLE);
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        this.mActivityCallback.onSaveInstanceState(bundle);
        if (this.mFloatingWindowHelper != null) {
            FloatingActivitySwitcher.onSaveInstanceState(this.mActivity, bundle);
            MultiAppFloatingActivitySwitcher.onSaveInstanceState(this.mActivity.getTaskId(), this.mActivity.getActivityIdentity(), bundle);
        }
        if (this.mActionBarContainer != null) {
            SparseArray<? extends Parcelable> sparseArray = new SparseArray<>();
            this.mActionBarContainer.saveHierarchyState(sparseArray);
            bundle.putSparseParcelableArray(ACTION_BAR_TAG, sparseArray);
        }
    }

    public void onRestoreInstanceState(Bundle bundle) {
        SparseArray sparseParcelableArray;
        this.mActivityCallback.onRestoreInstanceState(bundle);
        if (this.mActionBarContainer == null || (sparseParcelableArray = bundle.getSparseParcelableArray(ACTION_BAR_TAG)) == null) {
            return;
        }
        this.mActionBarContainer.restoreHierarchyState(sparseParcelableArray);
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void invalidateOptionsMenu() {
        if (this.mActivity == null || this.mActivity.isFinishing() || this.mActivity.isDestroyed()) {
            return;
        }
        this.mInvalidateMenuRunnable.run();
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onDestroy() {
        super.onDestroy();
        if (this.mActivity == null || this.mActivity.getWindow() == null) {
            return;
        }
        View decorView = this.mActivity.getWindow().getDecorView();
        decorView.removeCallbacks(this.mInvalidateMenuRunnable);
        Runnable runnable = this.mUpdateWindowInfoRunnable;
        if (runnable != null) {
            decorView.removeCallbacks(runnable);
        }
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public boolean requestDispatchContentInset() {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout == null) {
            return false;
        }
        actionBarOverlayLayout.requestDispatchContentInset();
        return true;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void setBottomExtraInset(int i) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setBottomExtraInset(i);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegate
    public void setBottomMenuMode(int i) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setBottomMenuMode(i);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public int getBottomMenuMode() {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            return actionBarOverlayLayout.getBottomMenuMode();
        }
        return super.getBottomMenuMode();
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public Rect getContentInset() {
        return this.mContentInset;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.IContentInsetState
    public void onContentInsetChanged(Rect rect) {
        super.onContentInsetChanged(rect);
        List<androidx.fragment.app.Fragment> fragments = this.mActivity.getSupportFragmentManager().getFragments();
        int size = fragments.size();
        for (int i = 0; i < size; i++) {
            ActivityResultCaller activityResultCaller = (androidx.fragment.app.Fragment) fragments.get(i);
            if (activityResultCaller instanceof IFragment) {
                IFragment iFragment = (IFragment) activityResultCaller;
                if (!iFragment.hasActionBar()) {
                    iFragment.onContentInsetChanged(rect);
                }
            }
        }
    }

    @Override // miuix.container.ExtraPaddingObserver
    public void onExtraPaddingChanged(int i) {
        this.mExtraHorizontalPadding = i;
    }

    @Override // miuix.appcompat.app.ActionBarDelegate, miuix.appcompat.app.IContentInsetState
    public void setCorrectNestedScrollMotionEventEnabled(boolean z) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setCorrectNestedScrollMotionEventEnabled(z);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (callback instanceof SearchActionMode.Callback) {
            addContentMask(this.mSubDecor);
        }
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            return actionBarOverlayLayout.startActionMode(callback);
        }
        return null;
    }

    @Override // miuix.appcompat.internal.view.menu.MenuBuilder.Callback
    public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
        return this.mActivity.onMenuItemSelected(0, menuItem);
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        if (getActionBar() != null) {
            return ((ActionBarImpl) getActionBar()).startActionMode(callback);
        }
        return super.onWindowStartingActionMode(callback);
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.container.ExtraPaddingProcessor
    public void setExtraPaddingPolicy(ExtraPaddingPolicy extraPaddingPolicy) {
        super.setExtraPaddingPolicy(extraPaddingPolicy);
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setExtraPaddingPolicy(this.mExtraPaddingPolicy);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.container.ExtraPaddingProcessor
    public void addExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        super.addExtraPaddingObserver(extraPaddingObserver);
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.addExtraPaddingObserver(extraPaddingObserver);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.container.ExtraPaddingProcessor
    public void removeExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        super.removeExtraPaddingObserver(extraPaddingObserver);
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.removeExtraPaddingObserver(extraPaddingObserver);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingEnable(boolean z) {
        super.setExtraHorizontalPaddingEnable(z);
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setExtraHorizontalPaddingEnable(z);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingInitEnable(boolean z) {
        super.setExtraHorizontalPaddingInitEnable(z);
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setExtraHorizontalPaddingInitEnable(z);
        }
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void setExtraPaddingApplyToContentEnable(boolean z) {
        super.setExtraPaddingApplyToContentEnable(z);
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setExtraPaddingApplyToContentEnable(z);
        }
    }

    public void addGroupButtons(GroupButtonsConfig groupButtonsConfig, boolean z) {
        super.addGroupButtons(groupButtonsConfig);
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.addGroupButtons(groupButtonsConfig, z);
        }
    }

    public void setGroupButtonsPanelBackground(Drawable drawable) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setGroupButtonsPanelBackground(drawable);
        }
    }

    public void setGroupButtonsPanelBackgroundColor(int i) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setGroupButtonsPanelBackgroundColor(i);
        }
    }

    public void setGroupButtonsPanelBackgroundResource(int i) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setGroupButtonsPanelBackgroundResource(i);
        }
    }

    public String getActivityIdentity() {
        return this.mActivityIdentity;
    }

    class AppCompatWindowCallback extends WindowCallbackWrapper {
        @Override // androidx.appcompat.view.WindowCallbackWrapper, android.view.Window.Callback
        public void onContentChanged() {
        }

        public AppCompatWindowCallback(Window.Callback callback) {
            super(callback);
        }

        @Override // androidx.appcompat.view.WindowCallbackWrapper, android.view.Window.Callback
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            if (ShortcutsCallback.dispatchKeyEvent(AppDelegate.this.mActivity.getSupportFragmentManager(), keyEvent)) {
                return true;
            }
            return super.dispatchKeyEvent(keyEvent);
        }

        @Override // androidx.appcompat.view.WindowCallbackWrapper, android.view.Window.Callback
        public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
            if (ShortcutsCallback.dispatchKeyShortcutEvent(AppDelegate.this.mActivity.getSupportFragmentManager(), keyEvent)) {
                return true;
            }
            return super.dispatchKeyShortcutEvent(keyEvent);
        }

        @Override // androidx.appcompat.view.WindowCallbackWrapper, android.view.Window.Callback
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (ShortcutsCallback.dispatchTouchEvent(AppDelegate.this.mActivity.getSupportFragmentManager(), motionEvent)) {
                return true;
            }
            return super.dispatchTouchEvent(motionEvent);
        }

        @Override // androidx.appcompat.view.WindowCallbackWrapper, android.view.Window.Callback
        public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
            if (ShortcutsCallback.dispatchTrackballEvent(AppDelegate.this.mActivity.getSupportFragmentManager(), motionEvent)) {
                return true;
            }
            return super.dispatchTrackballEvent(motionEvent);
        }

        @Override // androidx.appcompat.view.WindowCallbackWrapper, android.view.Window.Callback
        public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
            if (ShortcutsCallback.dispatchGenericMotionEvent(AppDelegate.this.mActivity.getSupportFragmentManager(), motionEvent)) {
                return true;
            }
            return super.dispatchGenericMotionEvent(motionEvent);
        }

        @Override // androidx.appcompat.view.WindowCallbackWrapper, android.view.Window.Callback
        public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> list, Menu menu, int i) {
            ShortcutsCallback.dispatchProvideKeyboardShortcuts(AppDelegate.this.mActivity.getSupportFragmentManager(), list, menu, i);
            super.onProvideKeyboardShortcuts(list, menu, i);
        }
    }

    public void setBottomMenuCustomView(View view) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setBottomMenuCustomView(view);
        }
    }

    public void removeBottomMenuCustomView() {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.removeBottomMenuCustomView();
        }
    }

    public void hideBottomMenu(boolean z) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.hideBottomMenu(z);
        }
    }

    public void showBottomMenu(boolean z) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.showBottomMenu(z);
        }
    }

    public void showBottomMenuCustomView() {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.showBottomMenuCustomView();
        }
    }

    public void hideBottomMenuCustomView() {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.hideBottomMenuCustomView();
        }
    }

    public void setBottomMenuCustomViewTranslationYWithPx(int i) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setBottomMenuCustomViewTranslationYWithPx(i);
        }
    }

    public int getBottomMenuCustomViewTranslationY() {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mSubDecor;
        if (actionBarOverlayLayout != null) {
            return actionBarOverlayLayout.getBottomMenuCustomViewTranslationY();
        }
        return 0;
    }
}
