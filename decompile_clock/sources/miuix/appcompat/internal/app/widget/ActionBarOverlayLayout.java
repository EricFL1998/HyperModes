package miuix.appcompat.internal.app.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.DisplayCutout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.DisplayCutoutCompat;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import miuix.appcompat.R;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.GroupButtonsConfig;
import miuix.appcompat.app.GroupButtonsPanel;
import miuix.appcompat.app.IContentInsetState;
import miuix.appcompat.app.IMenuState;
import miuix.appcompat.app.OnStatusBarChangeListener;
import miuix.appcompat.app.floatingactivity.FloatingABOLayoutSpec;
import miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper;
import miuix.appcompat.internal.view.SearchActionModeImpl;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.appcompat.internal.view.menu.MenuDialogHelper;
import miuix.appcompat.internal.view.menu.MenuPresenter;
import miuix.appcompat.internal.view.menu.action.ActionMenuView;
import miuix.appcompat.internal.view.menu.context.ContextMenuBuilder;
import miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindowHelper;
import miuix.appcompat.widget.Button;
import miuix.autodensity.AutoDensityConfig;
import miuix.autodensity.DebugUtil;
import miuix.autodensity.DensityConfigManager;
import miuix.container.ExtraPaddingObserver;
import miuix.container.ExtraPaddingPolicy;
import miuix.container.ExtraPaddingProcessor;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.WindowBaseInfo;
import miuix.graphics.BitmapFactory;
import miuix.internal.util.AttributeResolver;
import miuix.os.DeviceHelper;
import miuix.smooth.SmoothCornerHelper;
import miuix.view.SearchActionMode;
import miuix.view.WindowInsetsController;
import miuix.view.WindowInsetsState;

/* JADX INFO: loaded from: classes2.dex */
public class ActionBarOverlayLayout extends FrameLayout implements NestedScrollingParent3, IMenuState, ExtraPaddingProcessor, WindowInsetsController {
    private ActionBar mActionBar;
    private ActionBarContainer mActionBarBottom;
    private ActionBarContextView mActionBarContextView;
    protected ActionBarContainer mActionBarTop;
    protected ActionBarView mActionBarView;
    private ActionMode mActionMode;
    private Rect mAnimateContentMarginBottomInsets;
    private boolean mAnimating;
    private Rect mBaseContentInsets;
    private Rect mBaseInnerInsets;
    private int mBottomExtraInset;
    private int mBottomMenuExtraInset;
    private int mBottomMenuMode;
    private int mBottomMenuModeConfig;
    private final int[] mBottomMenuVisibleHeight;
    private Window.Callback mCallback;
    private boolean mContentAutoFitSystemWindow;
    private Drawable mContentHeaderBackground;
    private IContentInsetState mContentInsetStateCallback;
    private Rect mContentInsets;
    private View mContentMask;
    private Rect mContentMaskInsets;
    protected View mContentView;
    private ContextMenuBuilder mContextMenu;
    private ContextMenuCallback mContextMenuCallback;
    private MenuDialogHelper mContextMenuHelper;
    private ContextMenuPopupWindowHelper mContextMenuPopupWindowHelper;
    protected final HashSet<View> mCoordinatedScrollViewSet;
    private boolean mCorrectNestedScrollMotionEventEnabled;
    private final Rect mCurrentContentInset;
    private int mDeviceType;
    private boolean mEnableWindowStatusBarInsets;
    private int mExtraHorizontalPadding;
    private boolean mExtraPaddingApplyToContentEnable;
    private boolean mExtraPaddingEnable;
    private boolean mExtraPaddingInitEnable;
    private List<ExtraPaddingObserver> mExtraPaddingObserver;
    private ExtraPaddingPolicy mExtraPaddingPolicy;
    private FloatingABOLayoutSpec mFloatingWindowSize;
    private final Rect mGroupButtonInsetsRect;
    private GroupButtonsPanel mGroupButtonPanelView;
    private int mImeInsetBottom;
    private View mInflateLayout;
    private Rect mInnerInsets;
    private int mInsetTopInMiuixFloating;
    private WindowInsetsController.InsetsConfig mInsetsConfig;
    private WindowInsetsController.InsetsConfig mInternalInsetsConfig;
    private boolean mIsFloatingTheme;
    private boolean mIsFloatingWindow;
    private boolean mIsInSearchMode;
    private boolean mIsMiuixFloating;
    private Rect mLastBaseContentInsets;
    private final Rect mLastDispatchContentInset;
    private Rect mLastInnerInsets;
    private boolean mLayoutStable;
    private LifecycleOwner mLifecycleOwner;
    private boolean mNestedScrollingParentEnabled;
    private final int[] mOffsetInWindow;
    private View.OnLayoutChangeListener mOnContainerViewLayoutChangeListener;
    private OnStatusBarChangeListener mOnStatusBarChangeListener;
    private final Rect mOriginalInset;
    private boolean mOverlayMode;
    private Runnable mPostScroll;
    private View mPostScrollTarget;
    private final Scroller mPostScroller;
    private boolean mRequestFitSystemWindow;
    private boolean mRootSubDecor;
    private boolean mShouldExtraPaddingHorizontalNotifyChanged;
    protected ViewStub mSplitAnimContentMask;
    private boolean mSqueezeContentByIme;
    private int mSystemBarsInsetBottom;
    private final Rect mThemeCompatSystemInset;
    private int mTranslucentStatus;
    private WindowInsetsController.InsetsConfig mUserInsetsConfig;

    public ActionBarOverlayLayout(Context context) {
        this(context, null);
    }

    public ActionBarOverlayLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ActionBarOverlayLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCoordinatedScrollViewSet = new HashSet<>();
        this.mLifecycleOwner = null;
        this.mIsInSearchMode = false;
        this.mRootSubDecor = true;
        this.mBaseContentInsets = new Rect();
        this.mLastBaseContentInsets = new Rect();
        this.mContentInsets = new Rect();
        this.mBaseInnerInsets = new Rect();
        this.mLastInnerInsets = new Rect();
        this.mInnerInsets = new Rect();
        this.mContentMaskInsets = new Rect();
        this.mCurrentContentInset = new Rect();
        this.mLastDispatchContentInset = new Rect();
        this.mThemeCompatSystemInset = new Rect();
        this.mOriginalInset = new Rect();
        this.mBottomMenuVisibleHeight = new int[2];
        this.mAnimateContentMarginBottomInsets = null;
        this.mContextMenuCallback = new ContextMenuCallback();
        this.mIsFloatingTheme = false;
        this.mIsFloatingWindow = false;
        this.mImeInsetBottom = 0;
        this.mSystemBarsInsetBottom = 0;
        this.mCorrectNestedScrollMotionEventEnabled = true;
        this.mGroupButtonInsetsRect = new Rect();
        this.mSqueezeContentByIme = false;
        this.mLayoutStable = false;
        this.mNestedScrollingParentEnabled = true;
        this.mEnableWindowStatusBarInsets = true;
        this.mOffsetInWindow = new int[2];
        this.mPostScroll = new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarOverlayLayout.2
            @Override // java.lang.Runnable
            public void run() {
                if (ActionBarOverlayLayout.this.mPostScroller.computeScrollOffset()) {
                    if (ActionBarOverlayLayout.this.mActionBar != null && ActionBarOverlayLayout.this.mPostScrollTarget != null) {
                        ((ActionBarImpl) ActionBarOverlayLayout.this.mActionBar).updateTopOffsetOnPostScroll(ActionBarOverlayLayout.this.mPostScrollTarget, ActionBarOverlayLayout.this.mPostScroller.getCurrY());
                    }
                    if (ActionBarOverlayLayout.this.mPostScroller.isFinished()) {
                        return;
                    }
                    ActionBarOverlayLayout.this.postOnAnimation(this);
                }
            }
        };
        SmoothCornerHelper.init(context);
        this.mPostScroller = new Scroller(context);
        this.mFloatingWindowSize = new FloatingABOLayoutSpec(context, attributeSet);
        this.mDeviceType = DeviceHelper.detectType(context);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Window, i, 0);
        this.mIsFloatingTheme = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_isMiuixFloatingTheme, false);
        this.mIsFloatingWindow = BaseFloatingActivityHelper.isFloatingWindow(context);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_contentAutoFitSystemWindow, false);
        this.mContentAutoFitSystemWindow = z;
        if (z) {
            this.mContentHeaderBackground = typedArrayObtainStyledAttributes.getDrawable(R.styleable.Window_contentHeaderBackground);
        }
        this.mEnableWindowStatusBarInsets = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_enableWindowStatusBarInsets, true);
        typedArrayObtainStyledAttributes.recycle();
        this.mBottomMenuModeConfig = AttributeResolver.resolveInt(context, R.attr.bottomMenuMode, 0);
        this.mSqueezeContentByIme = AttributeResolver.resolveBoolean(context, R.attr.squeezeContentByIme, false);
        this.mLayoutStable = AttributeResolver.resolveBoolean(context, R.attr.layoutStable, false);
        this.mInsetTopInMiuixFloating = getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_floating_window_top_offset);
    }

    int getDeviceType() {
        return this.mDeviceType;
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.mLifecycleOwner = lifecycleOwner;
    }

    public void enableWindowStatusBarInsets(boolean z) {
        this.mEnableWindowStatusBarInsets = z;
    }

    public void setBottomExtraInset(int i) {
        if (this.mBottomExtraInset != i) {
            this.mBottomExtraInset = i;
            int iMax = Math.max(getBottomInset(), this.mBottomMenuExtraInset);
            if (isLayoutHideNavigation() && iMax <= this.mThemeCompatSystemInset.bottom) {
                iMax = this.mThemeCompatSystemInset.bottom;
            }
            int iMax2 = Math.max(iMax, this.mBottomExtraInset);
            if (this.mCurrentContentInset.bottom != iMax2) {
                this.mCurrentContentInset.bottom = iMax2;
                dispatchContentInset(this.mCurrentContentInset, true);
            }
        }
    }

    public void setBottomMenuMode(int i) {
        if (this.mBottomMenuModeConfig != i) {
            this.mBottomMenuModeConfig = i;
            updateBottomMenuMode();
        }
    }

    public void setCorrectNestedScrollMotionEventEnabled(boolean z) {
        this.mCorrectNestedScrollMotionEventEnabled = z;
    }

    public int getBottomMenuMode() {
        return this.mBottomMenuMode;
    }

    public void setContentInsetStateCallback(IContentInsetState iContentInsetState) {
        this.mContentInsetStateCallback = iContentInsetState;
    }

    public void onFloatingModeChanged(boolean z) {
        if (this.mIsMiuixFloating != (this.mIsFloatingTheme && z)) {
            this.mIsFloatingWindow = z;
            this.mIsMiuixFloating = z;
            if (z) {
                this.mInsetTopInMiuixFloating = getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_floating_window_top_offset);
            }
            this.mFloatingWindowSize.onFloatingModeChanged(this.mIsMiuixFloating);
            ActionBar actionBar = this.mActionBar;
            if (actionBar != null) {
                ((ActionBarImpl) actionBar).onFloatingModeChanged(this.mIsMiuixFloating);
            }
            requestFitSystemWindows();
            requestLayout();
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Context context = getContext();
        DebugUtil.printDensityLog("->processActionBarOverlayLayout ConfigurationChanged newConfig.densityDpi " + configuration.densityDpi);
        DensityConfigManager.getInstance().tryUpdateConfig(context, configuration);
        AutoDensityConfig.updateDensity(context);
        this.mFloatingWindowSize.onConfigurationChanged();
        post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarOverlayLayout$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1827xc38dd5b7();
            }
        });
        ContextMenuBuilder contextMenuBuilder = this.mContextMenu;
        if (contextMenuBuilder == null || !contextMenuBuilder.isContextMenuPopupWindowShowing()) {
            return;
        }
        this.mContextMenu.refreshContextMenuPopupWindow();
    }

    /* JADX INFO: renamed from: lambda$onConfigurationChanged$0$miuix-appcompat-internal-app-widget-ActionBarOverlayLayout, reason: not valid java name */
    /* synthetic */ void m1827xc38dd5b7() {
        if (isAttachedToWindow()) {
            ActionBarContextView actionBarContextView = this.mActionBarContextView;
            if (actionBarContextView != null) {
                actionBarContextView.refreshBottomMenu();
            }
            ActionBarView actionBarView = this.mActionBarView;
            if (actionBarView != null) {
                actionBarView.refreshBottomMenu();
            }
            if (this.mContextMenu != null) {
                LifecycleOwner lifecycleOwner = this.mLifecycleOwner;
                if (lifecycleOwner != null ? lifecycleOwner.getLifecycle().getState().equals(Lifecycle.State.RESUMED) : true) {
                    return;
                }
                this.mContextMenu.close();
            }
        }
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraPaddingPolicy(ExtraPaddingPolicy extraPaddingPolicy) {
        if (this.mExtraPaddingPolicy == null && extraPaddingPolicy != null) {
            this.mExtraPaddingPolicy = extraPaddingPolicy;
            extraPaddingPolicy.setEnable(this.mExtraPaddingEnable);
            if (this.mExtraPaddingInitEnable) {
                updateExtraPaddingHorizontal(getContext(), this.mExtraPaddingPolicy, -1, -1);
                this.mShouldExtraPaddingHorizontalNotifyChanged = false;
                if (this.mExtraPaddingObserver != null) {
                    for (int i = 0; i < this.mExtraPaddingObserver.size(); i++) {
                        this.mExtraPaddingObserver.get(i).setExtraHorizontalPadding(this.mExtraHorizontalPadding);
                    }
                }
            }
        } else {
            this.mExtraPaddingPolicy = extraPaddingPolicy;
            if (extraPaddingPolicy != null) {
                extraPaddingPolicy.setEnable(this.mExtraPaddingEnable);
            }
        }
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            ((ActionBarImpl) actionBar).setExtraPaddingPolicy(this.mExtraPaddingPolicy);
        }
        requestLayout();
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public ExtraPaddingPolicy getExtraPaddingPolicy() {
        return this.mExtraPaddingPolicy;
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void addExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        if (this.mExtraPaddingObserver == null) {
            this.mExtraPaddingObserver = new CopyOnWriteArrayList();
        }
        if (this.mExtraPaddingObserver.contains(extraPaddingObserver)) {
            return;
        }
        this.mExtraPaddingObserver.add(extraPaddingObserver);
        extraPaddingObserver.setExtraHorizontalPadding(this.mExtraHorizontalPadding);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void removeExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        List<ExtraPaddingObserver> list = this.mExtraPaddingObserver;
        if (list != null) {
            list.remove(extraPaddingObserver);
        }
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingEnable(boolean z) {
        if (this.mExtraPaddingEnable != z) {
            this.mExtraPaddingEnable = z;
            ExtraPaddingPolicy extraPaddingPolicy = this.mExtraPaddingPolicy;
            if (extraPaddingPolicy != null) {
                extraPaddingPolicy.setEnable(z);
                requestLayout();
            }
        }
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingInitEnable(boolean z) {
        if (this.mExtraPaddingInitEnable != z) {
            this.mExtraPaddingInitEnable = z;
        }
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public boolean isExtraHorizontalPaddingEnable() {
        ExtraPaddingPolicy extraPaddingPolicy = this.mExtraPaddingPolicy;
        if (extraPaddingPolicy != null) {
            return extraPaddingPolicy.isEnable();
        }
        return false;
    }

    public void setExtraPaddingApplyToContentEnable(boolean z) {
        if (this.mExtraPaddingApplyToContentEnable != z) {
            this.mExtraPaddingApplyToContentEnable = z;
            requestLayout();
        }
    }

    public boolean isExtraPaddingApplyToContentEnable() {
        return this.mExtraPaddingApplyToContentEnable;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        View.OnLayoutChangeListener onLayoutChangeListener;
        super.onDetachedFromWindow();
        setContentInsetStateCallback(null);
        List<ExtraPaddingObserver> list = this.mExtraPaddingObserver;
        if (list != null) {
            list.clear();
        }
        GroupButtonsPanel groupButtonsPanel = this.mGroupButtonPanelView;
        if (groupButtonsPanel == null || (onLayoutChangeListener = this.mOnContainerViewLayoutChangeListener) == null) {
            return;
        }
        groupButtonsPanel.removeOnLayoutChangeListener(onLayoutChangeListener);
    }

    public void registerCoordinatedScrollView(View view) {
        if (view != null) {
            this.mCoordinatedScrollViewSet.add(view);
        }
    }

    public void unregisterCoordinatedScrollView(View view) {
        if (view != null) {
            this.mCoordinatedScrollViewSet.remove(view);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean showContextMenuForChild(View view, float f, float f2) {
        if (internalShowContextMenu(view, f, f2)) {
            return true;
        }
        return getParent() != null && getParent().showContextMenuForChild(view, f, f2);
    }

    private boolean internalShowContextMenu(View view, float f, float f2) {
        ContextMenuBuilder contextMenuBuilder = this.mContextMenu;
        if (contextMenuBuilder == null) {
            ContextMenuBuilder contextMenuBuilder2 = new ContextMenuBuilder(getContext());
            this.mContextMenu = contextMenuBuilder2;
            contextMenuBuilder2.setCallback(this.mContextMenuCallback);
        } else {
            contextMenuBuilder.clear();
        }
        if (view != null) {
            this.mContextMenuPopupWindowHelper = this.mContextMenu.show(view, view.getWindowToken(), f, f2);
        }
        ContextMenuPopupWindowHelper contextMenuPopupWindowHelper = this.mContextMenuPopupWindowHelper;
        if (contextMenuPopupWindowHelper != null) {
            contextMenuPopupWindowHelper.setPresenterCallback(this.mContextMenuCallback);
            return true;
        }
        return super.showContextMenuForChild(view);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean showContextMenuForChild(View view) {
        ContextMenuBuilder contextMenuBuilder = this.mContextMenu;
        if (contextMenuBuilder == null) {
            ContextMenuBuilder contextMenuBuilder2 = new ContextMenuBuilder(getContext());
            this.mContextMenu = contextMenuBuilder2;
            contextMenuBuilder2.setCallback(this.mContextMenuCallback);
        } else {
            contextMenuBuilder.clear();
        }
        MenuDialogHelper menuDialogHelperShow = this.mContextMenu.show(view, view.getWindowToken());
        this.mContextMenuHelper = menuDialogHelperShow;
        if (menuDialogHelperShow != null) {
            menuDialogHelperShow.setPresenterCallback(this.mContextMenuCallback);
            return true;
        }
        return super.showContextMenuForChild(view);
    }

    @Override // android.view.View
    protected boolean fitSystemWindows(Rect rect) {
        boolean zApplyInsetsByMargin;
        boolean zApplyInsetsByMargin2;
        WindowInsetsController.InsetsConfig insetsConfig;
        WindowInsetsController.InsetsConfig insetsConfig2;
        Insets insets;
        Insets insets2;
        dispatchInsetsIgnoreVisibility(this, this.mLayoutStable);
        boolean zIsLayoutHideNavigation = isLayoutHideNavigation();
        boolean zIsTranslucentStatus = isTranslucentStatus();
        this.mSystemBarsInsetBottom = 0;
        this.mOriginalInset.set(rect);
        this.mThemeCompatSystemInset.set(rect);
        this.mBaseInnerInsets.set(rect);
        WindowInsetsCompat rootWindowInsets = ViewCompat.getRootWindowInsets(this);
        if (rootWindowInsets != null) {
            if (this.mLayoutStable) {
                insets = rootWindowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars());
                insets2 = rootWindowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            } else {
                insets = rootWindowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                insets2 = rootWindowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            }
            this.mSystemBarsInsetBottom = insets.bottom;
            this.mImeInsetBottom = rootWindowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            this.mThemeCompatSystemInset.left = insets2.left;
            this.mThemeCompatSystemInset.right = insets2.right;
            this.mThemeCompatSystemInset.bottom = this.mSystemBarsInsetBottom;
            if (this.mSqueezeContentByIme && this.mImeInsetBottom > 0) {
                this.mThemeCompatSystemInset.bottom = 0;
            }
        }
        if (!zIsLayoutHideNavigation) {
            this.mThemeCompatSystemInset.bottom = 0;
            if (isNavigationBarToLeftEdge(rootWindowInsets, this.mLayoutStable)) {
                this.mThemeCompatSystemInset.left = 0;
            }
            if (isNavigationBarToRightEdge(rootWindowInsets, this.mLayoutStable)) {
                this.mThemeCompatSystemInset.right = 0;
            }
        }
        OnStatusBarChangeListener onStatusBarChangeListener = this.mOnStatusBarChangeListener;
        if (onStatusBarChangeListener != null) {
            onStatusBarChangeListener.onStatusBarHeightChange(rect.top);
        }
        if (this.mIsMiuixFloating || ((insetsConfig2 = this.mInsetsConfig) != null && insetsConfig2.isFloatingMode)) {
            this.mBaseInnerInsets.top = this.mInsetTopInMiuixFloating;
            this.mBaseInnerInsets.left = 0;
            this.mBaseInnerInsets.right = 0;
            this.mThemeCompatSystemInset.top = this.mBaseContentInsets.top;
            this.mThemeCompatSystemInset.bottom = 0;
            this.mThemeCompatSystemInset.left = 0;
            this.mThemeCompatSystemInset.right = 0;
        }
        if (Build.VERSION.SDK_INT >= 28 && MiuixUIUtils.renderContentInCutoutArea(getContext())) {
            this.mBaseInnerInsets.left = 0;
            this.mBaseInnerInsets.right = 0;
            Insets displayCoutInsets = getDisplayCoutInsets();
            if (isCutoutToLeftEdge(displayCoutInsets)) {
                this.mThemeCompatSystemInset.left = 0;
            }
            if (isCutoutToRightEdge(displayCoutInsets)) {
                this.mThemeCompatSystemInset.right = 0;
            }
        }
        if (this.mOverlayMode) {
            updateCurrentContentInsetInOverlayMode();
        } else {
            updateCurrentContentInset();
        }
        if (!isRootSubDecor() && (!zIsLayoutHideNavigation || this.mBaseInnerInsets.bottom != this.mSystemBarsInsetBottom)) {
            this.mBaseInnerInsets.bottom = 0;
        }
        WindowInsetsController.InsetsConfig insetsConfig3 = this.mInsetsConfig;
        if (insetsConfig3 != null && !insetsConfig3.isFloatingMode) {
            if (this.mInsetsConfig.ignoreLeftInset) {
                this.mBaseInnerInsets.left = 0;
                this.mThemeCompatSystemInset.left = 0;
            }
            if (this.mInsetsConfig.ignoreTopInset) {
                this.mBaseInnerInsets.top = 0;
                this.mThemeCompatSystemInset.top = 0;
            }
            if (this.mInsetsConfig.ignoreRightInset) {
                this.mBaseInnerInsets.right = 0;
                this.mThemeCompatSystemInset.right = 0;
            }
            if (this.mInsetsConfig.ignoreBottomInset) {
                this.mBaseInnerInsets.bottom = 0;
                this.mThemeCompatSystemInset.bottom = 0;
            }
        }
        computeFitSystemInsets(zIsTranslucentStatus, zIsLayoutHideNavigation, this.mImeInsetBottom, this.mBaseInnerInsets, this.mBaseContentInsets);
        ActionBarContainer actionBarContainer = this.mActionBarTop;
        boolean z = true;
        if (actionBarContainer != null) {
            if (zIsTranslucentStatus) {
                actionBarContainer.setPendingInsets(this.mBaseInnerInsets);
            }
            ActionMode actionMode = this.mActionMode;
            if (actionMode instanceof SearchActionModeImpl) {
                ((SearchActionModeImpl) actionMode).setPendingInsets(this.mBaseInnerInsets);
            }
            zApplyInsetsByMargin = applyInsetsByMargin(this.mActionBarTop, this.mThemeCompatSystemInset, true, isRootSubDecor() && !zIsTranslucentStatus, true, false);
        } else {
            zApplyInsetsByMargin = false;
        }
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null) {
            actionBarContextView.setPendingInset(this.mThemeCompatSystemInset);
        }
        ActionBarContainer actionBarContainer2 = this.mActionBarBottom;
        if (actionBarContainer2 != null) {
            actionBarContainer2.setPendingInsets(this.mThemeCompatSystemInset);
            this.mContentMaskInsets.set(this.mBaseInnerInsets);
            Rect rect2 = new Rect();
            rect2.set(this.mBaseContentInsets);
            if (this.mIsFloatingWindow || ((insetsConfig = this.mInsetsConfig) != null && insetsConfig.isFloatingMode)) {
                rect2.bottom = 0;
            }
            if (this.mSqueezeContentByIme) {
                Rect rect3 = new Rect(this.mThemeCompatSystemInset);
                rect3.bottom = this.mBaseContentInsets.bottom;
                zApplyInsetsByMargin2 = applyInsetsByMargin(this.mActionBarBottom, rect3, true, false, true, true);
            } else {
                zApplyInsetsByMargin2 = applyInsetsByMargin(this.mActionBarBottom, this.mThemeCompatSystemInset, true, false, true, false);
            }
            zApplyInsetsByMargin |= zApplyInsetsByMargin2;
        }
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            actionBarView.setPendingInset(this.mThemeCompatSystemInset);
        }
        if (this.mLastBaseContentInsets.equals(this.mBaseContentInsets)) {
            z = zApplyInsetsByMargin;
        } else {
            this.mLastBaseContentInsets.set(this.mBaseContentInsets);
        }
        if (z) {
            requestLayout();
        }
        return super.fitSystemWindows(rect);
    }

    private void dispatchInsetsIgnoreVisibility(ViewGroup viewGroup, boolean z) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            KeyEvent.Callback childAt = viewGroup.getChildAt(i);
            if (childAt instanceof WindowInsetsState) {
                ((WindowInsetsState) childAt).setInsetsIgnoringVisibility(z);
            }
            if (childAt instanceof ViewGroup) {
                dispatchInsetsIgnoreVisibility((ViewGroup) childAt, z);
            }
        }
    }

    public Rect getContentInset() {
        return this.mCurrentContentInset;
    }

    void updateCurrentContentInsetInOverlayMode() {
        ActionBarContainer actionBarContainer;
        this.mCurrentContentInset.set(this.mThemeCompatSystemInset);
        int iMax = 0;
        if (this.mActionBar != null && (actionBarContainer = this.mActionBarTop) != null && actionBarContainer.getVisibility() != 8 && this.mActionBarTop.getMeasuredHeight() > 0) {
            iMax = Math.max(0, (int) (((ActionBarImpl) this.mActionBar).getTopViewHeight() + (this.mEnableWindowStatusBarInsets ? this.mThemeCompatSystemInset.top : 0) + (this.mIsMiuixFloating ? this.mInsetTopInMiuixFloating : 0) + this.mActionBarTop.getTranslationY()));
        }
        int iMax2 = Math.max(Math.max(Math.max(getBottomInset(), this.mBottomExtraInset), this.mBottomMenuExtraInset), this.mGroupButtonInsetsRect.bottom);
        if (isTranslucentStatus() && iMax < this.mThemeCompatSystemInset.top) {
            this.mCurrentContentInset.top = this.mThemeCompatSystemInset.top;
        } else {
            this.mCurrentContentInset.top = iMax;
        }
        if (isLayoutHideNavigation() && iMax2 < this.mThemeCompatSystemInset.bottom) {
            this.mCurrentContentInset.bottom = this.mThemeCompatSystemInset.bottom;
        } else {
            this.mCurrentContentInset.bottom = iMax2;
        }
        if (this.mCurrentContentInset.left < this.mThemeCompatSystemInset.left) {
            this.mCurrentContentInset.left = this.mThemeCompatSystemInset.left;
        }
        if (this.mCurrentContentInset.right < this.mThemeCompatSystemInset.right) {
            this.mCurrentContentInset.right = this.mThemeCompatSystemInset.right;
        }
        dispatchContentInset(this.mCurrentContentInset);
    }

    private void updateCurrentContentInset() {
        this.mCurrentContentInset.set(0, 0, 0, 0);
        int iMax = Math.max(Math.max(getBottomInset(), this.mBottomExtraInset), this.mBottomMenuExtraInset);
        if (isLayoutHideNavigation() && iMax < this.mThemeCompatSystemInset.bottom) {
            this.mCurrentContentInset.bottom = this.mThemeCompatSystemInset.bottom;
        } else {
            this.mCurrentContentInset.bottom = iMax;
        }
        if (this.mCurrentContentInset.left < this.mThemeCompatSystemInset.left) {
            this.mCurrentContentInset.left = this.mThemeCompatSystemInset.left;
        }
        if (this.mCurrentContentInset.right < this.mThemeCompatSystemInset.right) {
            this.mCurrentContentInset.right = this.mThemeCompatSystemInset.right;
        }
    }

    private boolean isCutoutToLeftEdge(Insets insets) {
        return insets != null && insets.left > 0;
    }

    private boolean isCutoutToRightEdge(Insets insets) {
        return insets != null && insets.right > 0;
    }

    private Insets getDisplayCoutInsets() {
        WindowInsetsCompat rootWindowInsets = ViewCompat.getRootWindowInsets(this);
        if (rootWindowInsets == null) {
            return null;
        }
        DisplayCutoutCompat displayCutout = rootWindowInsets.getDisplayCutout();
        if (displayCutout == null) {
            Activity activityContextFromView = getActivityContextFromView(this);
            if (activityContextFromView != null) {
                DisplayCutout cutout = Build.VERSION.SDK_INT >= 29 ? activityContextFromView.getWindowManager().getDefaultDisplay().getCutout() : null;
                if (cutout != null) {
                    return Insets.of(cutout.getSafeInsetLeft(), cutout.getSafeInsetTop(), cutout.getSafeInsetRight(), cutout.getSafeInsetBottom());
                }
            }
            return null;
        }
        return Insets.of(displayCutout.getSafeInsetLeft(), displayCutout.getSafeInsetTop(), displayCutout.getSafeInsetRight(), displayCutout.getSafeInsetBottom());
    }

    private boolean isNavigationBarToLeftEdge(WindowInsetsCompat windowInsetsCompat, boolean z) {
        Insets insets;
        if (windowInsetsCompat == null) {
            return false;
        }
        if (z) {
            insets = windowInsetsCompat.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.captionBar() | WindowInsetsCompat.Type.navigationBars());
        } else {
            insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.captionBar() | WindowInsetsCompat.Type.navigationBars());
        }
        return insets.left > 0;
    }

    private boolean isNavigationBarToRightEdge(WindowInsetsCompat windowInsetsCompat, boolean z) {
        Insets insets;
        if (windowInsetsCompat == null) {
            return false;
        }
        if (z) {
            insets = windowInsetsCompat.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.captionBar() | WindowInsetsCompat.Type.navigationBars());
        } else {
            insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.captionBar() | WindowInsetsCompat.Type.navigationBars());
        }
        return insets.right > 0;
    }

    public void requestDispatchContentInset() {
        notifyContentInset(false);
    }

    private void dispatchContentInset(Rect rect) {
        dispatchContentInset(rect, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchContentInset(Rect rect, boolean z) {
        if (!this.mLastDispatchContentInset.equals(rect)) {
            this.mLastDispatchContentInset.set(rect);
            notifyContentInset(z);
        }
    }

    private void notifyContentInset(boolean z) {
        boolean z2 = this.mOverlayMode;
        if (z2 || z) {
            ActionBar actionBar = this.mActionBar;
            if (actionBar != null && z2) {
                ((ActionBarImpl) actionBar).updateContentInsetForNestedObserver(this.mLastDispatchContentInset);
            }
            IContentInsetState iContentInsetState = this.mContentInsetStateCallback;
            if (iContentInsetState != null) {
                iContentInsetState.onContentInsetChanged(this.mLastDispatchContentInset);
            }
        }
    }

    private Activity getActivityContextFromView(View view) {
        Context context = ((ViewGroup) view.getRootView()).getChildAt(0).getContext();
        if (context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }

    public Rect getBaseInnerInsets() {
        return this.mBaseInnerInsets;
    }

    public Rect getInnerInsets() {
        return this.mInnerInsets;
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        WindowInsets windowInsetsOnApplyWindowInsets = super.onApplyWindowInsets(windowInsets);
        return (Build.VERSION.SDK_INT < 28 || windowInsetsOnApplyWindowInsets.isConsumed() || !isRootSubDecor()) ? windowInsetsOnApplyWindowInsets : windowInsets.consumeDisplayCutout();
    }

    private void computeFitSystemInsets(boolean z, boolean z2, int i, Rect rect, Rect rect2) {
        boolean zIsRootSubDecor = isRootSubDecor();
        rect2.set(rect);
        if ((!zIsRootSubDecor || z) && !this.mContentAutoFitSystemWindow) {
            rect2.top = 0;
        }
        if (this.mIsFloatingWindow || z2) {
            rect2.bottom = 0;
        } else if (rect2.bottom != 0) {
            rect2.bottom -= i;
            if (rect2.bottom < 0) {
                rect2.bottom = 0;
            }
        }
        if (!this.mSqueezeContentByIme || i <= 0) {
            return;
        }
        rect2.bottom = this.mOriginalInset.bottom;
    }

    private boolean applyInsetsByMargin(View view, Rect rect, boolean z, boolean z2, boolean z3, boolean z4) {
        boolean z5 = false;
        if (view == null) {
            return false;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (z && layoutParams.leftMargin != rect.left) {
            layoutParams.leftMargin = rect.left;
            z5 = true;
        }
        if (z2 && layoutParams.topMargin != rect.top) {
            layoutParams.topMargin = rect.top;
            z5 = true;
        }
        if (z3 && layoutParams.rightMargin != rect.right) {
            layoutParams.rightMargin = rect.right;
            z5 = true;
        }
        if (!z4 || layoutParams.bottomMargin == rect.bottom) {
            return z5;
        }
        layoutParams.bottomMargin = rect.bottom;
        return true;
    }

    public void animateContentMarginBottomByBottomMenu(int i) {
        if (this.mAnimateContentMarginBottomInsets == null) {
            this.mAnimateContentMarginBottomInsets = new Rect();
        }
        Rect rect = this.mAnimateContentMarginBottomInsets;
        rect.top = this.mContentInsets.top;
        rect.bottom = i;
        rect.right = this.mContentInsets.right;
        rect.left = this.mContentInsets.left;
        applyInsetsByMargin(this.mContentView, rect, true, true, true, true);
        this.mContentView.requestLayout();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestFitSystemWindows();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestFitSystemWindows() {
        super.requestFitSystemWindows();
        this.mRequestFitSystemWindow = true;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        pullChildren();
    }

    /* JADX WARN: Code duplicated, block: B:82:0x01bf  */
    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int iMakeMeasureSpec;
        int widthMeasureSpec = this.mFloatingWindowSize.getWidthMeasureSpec(i);
        int heightMeasureSpec = this.mFloatingWindowSize.getHeightMeasureSpec(i2);
        View view = this.mContentView;
        View view2 = this.mContentMask;
        int iMax = 0;
        int iMax2 = 0;
        int iCombineMeasuredStates = 0;
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View childAt = getChildAt(i3);
            if (childAt != view && childAt != view2 && childAt.getVisibility() != 8) {
                measureChildWithMargins(childAt, widthMeasureSpec, 0, heightMeasureSpec, 0);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                iMax = Math.max(iMax, childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
                iMax2 = Math.max(iMax2, childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
                iCombineMeasuredStates = combineMeasuredStates(iCombineMeasuredStates, childAt.getMeasuredState());
            }
        }
        ActionBarContainer actionBarContainer = this.mActionBarTop;
        int measuredHeight = (actionBarContainer == null || actionBarContainer.getVisibility() == 8) ? 0 : this.mActionBarTop.getMeasuredHeight();
        int bottomInset = getBottomInset();
        ActionBarView actionBarView = this.mActionBarView;
        int i4 = (actionBarView == null || !actionBarView.isSplitActionBar()) ? 0 : bottomInset;
        this.mInnerInsets.set(this.mBaseInnerInsets);
        this.mContentInsets.set(this.mBaseContentInsets);
        boolean zIsLayoutHideNavigation = isLayoutHideNavigation();
        boolean zIsTranslucentStatus = isTranslucentStatus();
        if (zIsTranslucentStatus && measuredHeight > 0) {
            this.mContentInsets.top = 0;
        }
        if (this.mOverlayMode) {
            if (!zIsTranslucentStatus) {
                this.mInnerInsets.top += measuredHeight;
            } else if (measuredHeight > 0) {
                this.mInnerInsets.top = measuredHeight;
            }
            this.mInnerInsets.bottom += i4;
        } else {
            this.mContentInsets.top += measuredHeight;
            this.mContentInsets.bottom += i4;
        }
        if ((!this.mIsFloatingTheme || !this.mIsFloatingWindow) && zIsLayoutHideNavigation) {
            if (getResources().getConfiguration().orientation == 2) {
                this.mContentInsets.right = 0;
                this.mContentInsets.left = 0;
            }
            if (bottomInset == 0 && (!this.mSqueezeContentByIme || this.mImeInsetBottom <= 0)) {
                this.mContentInsets.bottom = 0;
            }
        }
        if (!isBottomAnimating()) {
            applyInsetsByMargin(view, this.mContentInsets, true, true, true, true);
            this.mAnimateContentMarginBottomInsets = null;
        }
        if (!this.mOverlayMode) {
            view.setPadding(view.getPaddingLeft(), 0, view.getPaddingRight(), view.getPaddingBottom());
        }
        if (!this.mLastInnerInsets.equals(this.mInnerInsets) || this.mRequestFitSystemWindow) {
            this.mLastInnerInsets.set(this.mInnerInsets);
            this.mRequestFitSystemWindow = false;
        }
        if (isTranslucentStatus() && this.mContentAutoFitSystemWindow) {
            Drawable drawable = this.mContentHeaderBackground;
            if (drawable != null) {
                drawable.setBounds(0, 0, getRight() - getLeft(), this.mBaseContentInsets.top);
            } else {
                ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
                if (viewGroup != null && viewGroup.getChildCount() == 1) {
                    View childAt2 = viewGroup.getChildAt(0);
                    childAt2.setPadding(childAt2.getPaddingLeft(), 0, childAt2.getPaddingRight(), childAt2.getPaddingBottom());
                }
            }
        }
        ExtraPaddingPolicy extraPaddingPolicy = this.mExtraPaddingPolicy;
        if (extraPaddingPolicy == null || !extraPaddingPolicy.isEnable()) {
            iMakeMeasureSpec = widthMeasureSpec;
        } else {
            int size = View.MeasureSpec.getSize(widthMeasureSpec);
            updateExtraPaddingHorizontal(getContext(), this.mExtraPaddingPolicy, size, View.MeasureSpec.getSize(heightMeasureSpec));
            if (this.mExtraPaddingApplyToContentEnable) {
                iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size - (this.mExtraHorizontalPadding * 2), View.MeasureSpec.getMode(widthMeasureSpec));
            } else {
                iMakeMeasureSpec = widthMeasureSpec;
            }
        }
        measureChildWithMargins(view, iMakeMeasureSpec, 0, heightMeasureSpec, 0);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) view.getLayoutParams();
        int iMax3 = Math.max(iMax, view.getMeasuredWidth() + layoutParams2.leftMargin + layoutParams2.rightMargin);
        int iMax4 = Math.max(iMax2, view.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin);
        int iCombineMeasuredStates2 = combineMeasuredStates(iCombineMeasuredStates, view.getMeasuredState());
        if (view2 != null && view2.getVisibility() == 0) {
            applyInsetsByMargin(view2, this.mContentMaskInsets, true, false, true, true);
            measureChildWithMargins(view2, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        setMeasuredDimension(resolveSizeAndState(Math.max(iMax3 + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), widthMeasureSpec, iCombineMeasuredStates2), resolveSizeAndState(Math.max(iMax4 + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight()), heightMeasureSpec, iCombineMeasuredStates2 << 16));
        post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarOverlayLayout$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.updateBottomMenuMode();
            }
        });
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        WindowInsetsCompat rootWindowInsets;
        Insets insets;
        super.onLayout(z, i, i2, i3, i4);
        if (this.mOverlayMode) {
            updateCurrentContentInsetInOverlayMode();
        }
        ExtraPaddingPolicy extraPaddingPolicy = this.mExtraPaddingPolicy;
        if (extraPaddingPolicy != null && extraPaddingPolicy.isEnable()) {
            if (this.mShouldExtraPaddingHorizontalNotifyChanged && this.mExtraPaddingObserver != null) {
                this.mShouldExtraPaddingHorizontalNotifyChanged = false;
                for (int i5 = 0; i5 < this.mExtraPaddingObserver.size(); i5++) {
                    this.mExtraPaddingObserver.get(i5).onExtraPaddingChanged(this.mExtraHorizontalPadding);
                }
            }
            if (this.mExtraPaddingApplyToContentEnable) {
                this.mExtraPaddingPolicy.applyExtraPadding(this.mContentView);
            }
        }
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null && !this.mIsInSearchMode) {
            ((ActionBarImpl) actionBar).updateCoordinateOffsetView();
        }
        Context context = getContext();
        if (this.mUserInsetsConfig != null || (rootWindowInsets = ViewCompat.getRootWindowInsets(this)) == null) {
            return;
        }
        if (this.mLayoutStable) {
            insets = rootWindowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
        } else {
            insets = rootWindowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
        }
        int i6 = EnvStateManager.getScreenSize(context).x;
        if (i6 != -1) {
            int[] iArr = new int[2];
            getLocationOnScreen(iArr);
            int i7 = iArr[0];
            applyInternalWindowInsets(false, MiuixUIUtils.isLayoutHideNavigation(this), i7 >= insets.left, false, i6 - (getWidth() + i7) >= insets.right, false);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        Drawable drawable;
        if (this.mContentAutoFitSystemWindow && (drawable = this.mContentHeaderBackground) != null) {
            drawable.setBounds(0, 0, getRight() - getLeft(), this.mBaseContentInsets.top);
            this.mContentHeaderBackground.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    protected int getBottomInset() {
        ActionBarContainer actionBarContainer = this.mActionBarBottom;
        if (actionBarContainer != null) {
            return actionBarContainer.getInsetHeight();
        }
        return 0;
    }

    boolean isBottomAnimating() {
        return this.mAnimating;
    }

    public ActionBar getActionBar() {
        return this.mActionBar;
    }

    public void setActionBar(ActionBar actionBar) {
        this.mActionBar = actionBar;
        if (actionBar != null) {
            ((ActionBarImpl) actionBar).setExtraPaddingPolicy(this.mExtraPaddingPolicy);
        }
    }

    public void setOverlayMode(boolean z) {
        this.mOverlayMode = z;
        ActionBarContainer actionBarContainer = this.mActionBarTop;
        if (actionBarContainer != null) {
            actionBarContainer.setOverlayMode(z);
        }
    }

    public boolean isInOverlayMode() {
        return this.mOverlayMode;
    }

    public View getContentView() {
        return this.mContentView;
    }

    public ContentMaskAnimator getContentMaskAnimator(View.OnClickListener onClickListener) {
        return new ContentMaskAnimator(onClickListener);
    }

    public ActionBarView getActionBarView() {
        return this.mActionBarView;
    }

    public Window.Callback getCallback() {
        return this.mCallback;
    }

    public void setCallback(Window.Callback callback) {
        this.mCallback = callback;
    }

    public void setTranslucentStatus(int i) {
        if (this.mTranslucentStatus != i) {
            this.mTranslucentStatus = i;
            requestFitSystemWindows();
        }
    }

    public void setContentView(View view) {
        this.mContentView = view;
    }

    private void pullChildren() {
        if (this.mContentView == null) {
            this.mContentView = findViewById(android.R.id.content);
            ActionBarContainer actionBarContainer = (ActionBarContainer) findViewById(R.id.action_bar_container);
            this.mActionBarTop = actionBarContainer;
            boolean z = false;
            if (this.mIsFloatingTheme && this.mIsFloatingWindow && actionBarContainer != null && !AttributeResolver.resolveBoolean(getContext(), R.attr.windowActionBar, false)) {
                this.mActionBarTop.setVisibility(8);
                this.mActionBarTop = null;
            }
            ActionBarContainer actionBarContainer2 = this.mActionBarTop;
            if (actionBarContainer2 != null) {
                actionBarContainer2.setOverlayMode(this.mOverlayMode);
                ActionBarView actionBarView = (ActionBarView) this.mActionBarTop.findViewById(R.id.action_bar);
                this.mActionBarView = actionBarView;
                actionBarView.setBottomMenuMode(this.mBottomMenuMode);
                if (this.mIsFloatingTheme && this.mIsFloatingWindow) {
                    z = true;
                }
                this.mIsMiuixFloating = z;
                if (z) {
                    this.mInsetTopInMiuixFloating = getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_floating_window_top_offset);
                }
                this.mActionBarTop.setMiuixFloatingOnInit(this.mIsMiuixFloating);
            }
        }
    }

    public void setSplitActionBarView(ActionBarContainer actionBarContainer) {
        this.mActionBarBottom = actionBarContainer;
        actionBarContainer.setPendingInsets(this.mThemeCompatSystemInset);
    }

    public void setContentMask(View view) {
        this.mContentMask = view;
        if (!miuix.internal.util.DeviceHelper.isOled() || this.mContentMask == null) {
            return;
        }
        ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.miuix_appcompat_window_content_mask_oled, getContext().getTheme());
    }

    public View getContentMask() {
        return this.mContentMask;
    }

    public void setActionBarContextView(ActionBarContextView actionBarContextView) {
        this.mActionBarContextView = actionBarContextView;
        if (actionBarContextView != null) {
            actionBarContextView.setActionBarView(this.mActionBarView);
            this.mActionBarContextView.setBottomMenuMode(this.mBottomMenuMode);
            this.mActionBarContextView.setPendingInset(this.mThemeCompatSystemInset);
        }
    }

    public boolean isRootSubDecor() {
        return this.mRootSubDecor;
    }

    public boolean isTranslucentStatus() {
        if (MiuixUIUtils.isTargetSdkVersionAboveV(getContext())) {
            return true;
        }
        int windowSystemUiVisibility = getWindowSystemUiVisibility();
        boolean z = (windowSystemUiVisibility & 256) != 0;
        boolean z2 = (windowSystemUiVisibility & 1024) != 0;
        boolean z3 = this.mTranslucentStatus != 0;
        if (this.mIsFloatingTheme) {
            return z2 || z3;
        }
        return (z && z2) || z3;
    }

    private boolean isLayoutHideNavigation() {
        WindowInsetsController.InsetsConfig insetsConfig;
        return MiuixUIUtils.isLayoutHideNavigation(this) || ((insetsConfig = this.mInsetsConfig) != null && insetsConfig.renderUnderBottomDecorations);
    }

    public void setRootSubDecor(boolean z) {
        this.mRootSubDecor = z;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (super.onTouchEvent(motionEvent)) {
            return true;
        }
        return this.mIsFloatingTheme;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (super.dispatchKeyEvent(keyEvent)) {
            return true;
        }
        if (isBackPressed(keyEvent)) {
            if (this.mActionMode != null) {
                ActionBarContextView actionBarContextView = this.mActionBarContextView;
                if (actionBarContextView != null && actionBarContextView.hideOverflowMenu()) {
                    return true;
                }
                this.mActionMode.finish();
                this.mActionMode = null;
                return true;
            }
            ActionBarView actionBarView = this.mActionBarView;
            if (actionBarView != null && actionBarView.hideOverflowMenu()) {
                return true;
            }
        }
        return false;
    }

    private boolean isBackPressed(KeyEvent keyEvent) {
        return keyEvent.getKeyCode() == 4 && keyEvent.getAction() == 1;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        return startActionMode(view, callback);
    }

    @Override // android.view.View
    public ActionMode startActionMode(ActionMode.Callback callback) {
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        ActionMode actionModeOnWindowStartingActionMode = null;
        if (actionBarContextView != null && actionBarContextView.isAnimating()) {
            return null;
        }
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
        this.mActionMode = null;
        if (getCallback() != null) {
            actionModeOnWindowStartingActionMode = getCallback().onWindowStartingActionMode(createActionModeCallbackWrapper(callback));
        }
        if (actionModeOnWindowStartingActionMode != null) {
            this.mActionMode = actionModeOnWindowStartingActionMode;
        }
        if (this.mActionMode != null && getCallback() != null) {
            getCallback().onActionModeStarted(this.mActionMode);
        }
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null && actionBarView.isSplitActionBar()) {
            ActionMenuView actionMenuView = this.mActionBarView.getActionMenuView();
            if (actionMenuView != null) {
                setBottomMenuExtraInset(actionMenuView.getCollapsedHeight());
            }
            this.mActionBarView.makeMenuViewShowHideWithAnimation(false);
        }
        if ((this.mActionMode instanceof SearchActionMode) && this.mOverlayMode) {
            updateCurrentContentInsetInOverlayMode();
        }
        return this.mActionMode;
    }

    public ActionMode startActionMode(View view, ActionMode.Callback callback) {
        if (view instanceof ActionBarOverlayLayout) {
            ActionMode actionMode = this.mActionMode;
            if (actionMode != null) {
                actionMode.finish();
            }
            ActionMode actionModeStartActionMode = view.startActionMode(createActionModeCallbackWrapper(callback));
            this.mActionMode = actionModeStartActionMode;
            return actionModeStartActionMode;
        }
        return startActionMode(callback);
    }

    private ActionModeCallbackWrapper createActionModeCallbackWrapper(ActionMode.Callback callback) {
        if (callback instanceof SearchActionMode.Callback) {
            return new SearchActionModeCallbackWrapper(callback);
        }
        return new ActionModeCallbackWrapper(callback);
    }

    /* JADX WARN: Code duplicated, block: B:10:0x003a  */
    /* JADX WARN: Code duplicated, block: B:9:0x0038  */
    protected void updateBottomMenuMode() {
        int i = this.mBottomMenuModeConfig;
        float f = getContext().getResources().getDisplayMetrics().density;
        if (i == 0) {
            int measuredWidth = (int) ((getMeasuredWidth() * 1.0f) / f);
            int i2 = (int) ((EnvStateManager.getWindowSize(getContext()).x * 1.0f) / f);
            if (this.mDeviceType != 2 || measuredWidth <= 410 || i2 <= 640) {
                i = 2;
            } else {
                i = 3;
            }
        } else if (i == 1) {
            int i3 = (int) ((EnvStateManager.getWindowSize(getContext()).x * 1.0f) / f);
            if (this.mDeviceType != 2 || i3 <= 640) {
                i = 2;
            } else {
                i = 3;
            }
        }
        if (i != this.mBottomMenuMode) {
            this.mBottomMenuMode = i;
            ActionBarContextView actionBarContextView = this.mActionBarContextView;
            if (actionBarContextView != null) {
                actionBarContextView.setBottomMenuMode(i);
                this.mActionBarContextView.refreshBottomMenu();
            }
            ActionBarView actionBarView = this.mActionBarView;
            if (actionBarView != null) {
                actionBarView.setBottomMenuMode(this.mBottomMenuMode);
                this.mActionBarView.refreshBottomMenu();
            }
        }
    }

    @Override // miuix.appcompat.app.IMenuState
    public void onMenuStateChanged(int i, int i2) {
        int[] iArr = this.mBottomMenuVisibleHeight;
        iArr[i2] = i;
        int iMax = Math.max(iArr[0], iArr[1]);
        if (this.mOverlayMode) {
            if (isLayoutHideNavigation() && iMax <= this.mThemeCompatSystemInset.bottom) {
                iMax = this.mThemeCompatSystemInset.bottom;
            }
            this.mCurrentContentInset.bottom = Math.max(Math.max(iMax, this.mBottomMenuExtraInset), this.mBottomExtraInset);
            dispatchContentInset(this.mCurrentContentInset);
            return;
        }
        animateContentMarginBottomByBottomMenu(iMax);
    }

    public void setBottomMenuExtraInset(int i) {
        this.mBottomMenuExtraInset = i;
    }

    public void hideBottomMenu(boolean z) {
        if (this.mActionBarView != null) {
            setBottomMenuExtraInset(0);
            if (z) {
                this.mActionBarView.makeMenuViewShowHideWithAnimation(false);
            } else {
                this.mActionBarView.makeMenuViewShowHide(false);
            }
        }
    }

    public void showBottomMenu(boolean z) {
        if (this.mActionBarView != null) {
            setBottomMenuExtraInset(0);
            if (z) {
                this.mActionBarView.makeMenuViewShowHideWithAnimation(true);
            } else {
                this.mActionBarView.makeMenuViewShowHide(true);
            }
        }
    }

    public void setBottomMenuCustomView(View view) {
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            actionBarView.setBottomMenuCustomView(view);
        }
    }

    public void removeBottomMenuCustomView() {
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            actionBarView.removeBottomMenuCustomView();
        }
    }

    public void showBottomMenuCustomView() {
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            actionBarView.showBottomMenuCustomView();
        }
    }

    public void hideBottomMenuCustomView() {
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            actionBarView.hideBottomMenuCustomView();
        }
    }

    public void setBottomMenuCustomViewTranslationYWithPx(int i) {
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            actionBarView.setBottomMenuCustomViewTranslationYWithPx(i);
        }
    }

    public int getBottomMenuCustomViewTranslationY() {
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            return actionBarView.getBottomMenuCustomViewOffset();
        }
        return 0;
    }

    private class ActionModeCallbackWrapper implements ActionMode.Callback {
        private ActionMode.Callback mWrapped;

        public ActionModeCallbackWrapper(ActionMode.Callback callback) {
            this.mWrapped = callback;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return this.mWrapped.onCreateActionMode(actionMode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(actionMode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return this.mWrapped.onActionItemClicked(actionMode, menuItem);
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            this.mWrapped.onDestroyActionMode(actionMode);
            if (ActionBarOverlayLayout.this.mActionBarView != null && ActionBarOverlayLayout.this.mActionBarView.isSplitActionBar()) {
                ActionBarOverlayLayout.this.mActionBarView.makeMenuViewShowHideWithAnimation(true);
            }
            if (ActionBarOverlayLayout.this.getCallback() != null) {
                ActionBarOverlayLayout.this.getCallback().onActionModeFinished(actionMode);
            }
            ActionBarOverlayLayout.this.mActionMode = null;
        }
    }

    private class SearchActionModeCallbackWrapper extends ActionModeCallbackWrapper implements SearchActionMode.Callback {
        public SearchActionModeCallbackWrapper(ActionMode.Callback callback) {
            super(callback);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // miuix.appcompat.internal.app.widget.ActionBarOverlayLayout.ActionModeCallbackWrapper, android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            ((SearchActionMode) actionMode).setAnimatedViewListener(new SearchActionMode.AnimatedViewListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarOverlayLayout.SearchActionModeCallbackWrapper.1
                @Override // miuix.view.SearchActionMode.AnimatedViewListener
                public void onUpdateOffsetY(int i) {
                    if (ActionBarOverlayLayout.this.mActionBarTop != null) {
                        ActionBarOverlayLayout.this.mActionBarTop.setCoordinatedOffsetYInSearchModeAnimation(i);
                        ActionBarOverlayLayout.this.mActionBarTop.requestLayout();
                    }
                }

                @Override // miuix.view.SearchActionMode.AnimatedViewListener
                public void onInSearchMode(boolean z) {
                    if (ActionBarOverlayLayout.this.mIsInSearchMode != z) {
                        ActionBarOverlayLayout.this.mIsInSearchMode = z;
                        if (ActionBarOverlayLayout.this.mActionBar != null) {
                            ((ActionBarImpl) ActionBarOverlayLayout.this.mActionBar).updateCoordinateOffsetView();
                        }
                    }
                }
            });
            return super.onCreateActionMode(actionMode, menu);
        }
    }

    public class ContentMaskAnimator implements Animator.AnimatorListener {
        private ObjectAnimator mHideAnimator;
        private View.OnClickListener mOnClickListener;
        private ObjectAnimator mShowAnimator;

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        private ContentMaskAnimator(View.OnClickListener onClickListener) {
            this.mOnClickListener = onClickListener;
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(ActionBarOverlayLayout.this.mContentMask, "alpha", 0.0f, 1.0f);
            this.mShowAnimator = objectAnimatorOfFloat;
            objectAnimatorOfFloat.addListener(this);
            ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(ActionBarOverlayLayout.this.mContentMask, "alpha", 1.0f, 0.0f);
            this.mHideAnimator = objectAnimatorOfFloat2;
            objectAnimatorOfFloat2.addListener(this);
            if (miuix.internal.util.DeviceHelper.isFeatureWholeAnim()) {
                return;
            }
            this.mShowAnimator.setDuration(0L);
            this.mHideAnimator.setDuration(0L);
        }

        public Animator show() {
            return this.mShowAnimator;
        }

        public Animator hide() {
            return this.mHideAnimator;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            if (ActionBarOverlayLayout.this.mContentMask == null || ActionBarOverlayLayout.this.mActionBarBottom == null || animator != this.mShowAnimator) {
                return;
            }
            ActionBarOverlayLayout.this.mContentMask.setVisibility(0);
            ActionBarOverlayLayout.this.mContentMask.bringToFront();
            ActionBarOverlayLayout.this.mActionBarBottom.bringToFront();
            ActionBarOverlayLayout.this.mContentMask.setOnClickListener(this.mOnClickListener);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            if (ActionBarOverlayLayout.this.mContentMask == null || ActionBarOverlayLayout.this.mActionBarBottom == null || ActionBarOverlayLayout.this.mContentMask.getAlpha() != 0.0f) {
                return;
            }
            ActionBarOverlayLayout.this.mActionBarBottom.bringToFront();
            ActionBarOverlayLayout.this.mContentMask.setOnClickListener(null);
            ActionBarOverlayLayout.this.mContentMask.setVisibility(8);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            if (ActionBarOverlayLayout.this.mContentMask == null || ActionBarOverlayLayout.this.mActionBarBottom == null || animator != this.mHideAnimator) {
                return;
            }
            ActionBarOverlayLayout.this.mActionBarBottom.bringToFront();
            ActionBarOverlayLayout.this.mContentMask.setOnClickListener(null);
        }
    }

    private class ContextMenuCallback implements MenuBuilder.Callback, MenuPresenter.Callback {
        private MenuDialogHelper mSubMenuHelper;

        @Override // miuix.appcompat.internal.view.menu.MenuBuilder.Callback
        public void onMenuModeChange(MenuBuilder menuBuilder) {
        }

        private ContextMenuCallback() {
        }

        public void onCloseSubMenu(MenuBuilder menuBuilder) {
            if (ActionBarOverlayLayout.this.mCallback != null) {
                ActionBarOverlayLayout.this.mCallback.onPanelClosed(6, menuBuilder.getRootMenu());
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // miuix.appcompat.internal.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
            if (menuBuilder.getRootMenu() != menuBuilder) {
                onCloseSubMenu(menuBuilder);
            }
            if (z) {
                if (ActionBarOverlayLayout.this.mCallback != null) {
                    ActionBarOverlayLayout.this.mCallback.onPanelClosed(6, menuBuilder);
                }
                ActionBarOverlayLayout.this.dismissContextMenu();
                MenuDialogHelper menuDialogHelper = this.mSubMenuHelper;
                if (menuDialogHelper != null) {
                    menuDialogHelper.dismiss();
                    this.mSubMenuHelper = null;
                }
            }
        }

        @Override // miuix.appcompat.internal.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            if (menuBuilder == null) {
                return false;
            }
            menuBuilder.setCallback(this);
            MenuDialogHelper menuDialogHelper = new MenuDialogHelper(menuBuilder);
            this.mSubMenuHelper = menuDialogHelper;
            menuDialogHelper.show(null);
            return true;
        }

        @Override // miuix.appcompat.internal.view.menu.MenuBuilder.Callback
        public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
            if (ActionBarOverlayLayout.this.mCallback != null) {
                return ActionBarOverlayLayout.this.mCallback.onMenuItemSelected(6, menuItem);
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissContextMenu() {
        MenuDialogHelper menuDialogHelper = this.mContextMenuHelper;
        if (menuDialogHelper != null) {
            menuDialogHelper.dismiss();
            this.mContextMenuHelper = null;
            this.mContextMenu = null;
        }
    }

    public void setOnStatusBarChangeListener(OnStatusBarChangeListener onStatusBarChangeListener) {
        this.mOnStatusBarChangeListener = onStatusBarChangeListener;
    }

    public void setAnimating(boolean z) {
        this.mAnimating = z;
    }

    /* JADX WARN: Code duplicated, block: B:14:0x002b  */
    @Override // androidx.core.view.NestedScrollingParent3
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
        int i6;
        ActionBar actionBar;
        View adjustView = getAdjustView(view);
        if (adjustView == null) {
            return;
        }
        if (i4 < 0) {
            int i7 = iArr[1];
            if (i4 - i7 > 0 || (actionBar = this.mActionBar) == null || !(actionBar instanceof ActionBarImpl)) {
                i6 = i4;
            } else {
                int iUpdateTopOffsetOnNestedScroll = ((ActionBarImpl) actionBar).updateTopOffsetOnNestedScroll(view, i4 - i7);
                iArr[1] = iArr[1] + iUpdateTopOffsetOnNestedScroll;
                i6 = i4 - iUpdateTopOffsetOnNestedScroll;
            }
        } else {
            i6 = i4;
        }
        int[] iArr2 = this.mOffsetInWindow;
        iArr2[1] = 0;
        ActionBarContainer actionBarContainer = this.mActionBarTop;
        if (actionBarContainer != null && !this.mIsInSearchMode) {
            actionBarContainer.onNestedScroll(view, i, i2, i3, i6, i5, iArr, iArr2);
        }
        adjustNestedScrollMotionEventCoordinate(adjustView);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
        ActionBarContainer actionBarContainer;
        if (!this.mNestedScrollingParentEnabled) {
            return false;
        }
        if (!this.mPostScroller.isFinished()) {
            this.mPostScroller.forceFinished(true);
        }
        return (getAdjustView(view2) == null || (actionBarContainer = this.mActionBarTop) == null || !actionBarContainer.onStartNestedScroll(view, view2, i, i2)) ? false : true;
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
        ActionBarContainer actionBarContainer;
        if (!this.mPostScroller.isFinished()) {
            this.mPostScroller.forceFinished(true);
        }
        if (getAdjustView(view2) == null || (actionBarContainer = this.mActionBarTop) == null) {
            return;
        }
        actionBarContainer.onNestedScrollAccepted(view, view2, i, i2);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onStopNestedScroll(View view, int i) {
        ActionBarContainer actionBarContainer;
        ActionBar actionBar;
        if (getAdjustView(view) == null || (actionBarContainer = this.mActionBarTop) == null) {
            return;
        }
        actionBarContainer.onStopNestedScroll(view, i);
        if (isInOverlayMode() && this.mActionBarView != null && (actionBar = this.mActionBar) != null && actionBar.isAdsorptionToNoOverlay() && this.mActionBarView.getExpandState() == 0) {
            this.mPostScrollTarget = view;
            if (!this.mPostScroller.isFinished()) {
                this.mPostScroller.forceFinished(true);
            }
            int topOffsetForCoordinateView = ((ActionBarImpl) this.mActionBar).getTopOffsetForCoordinateView(this.mPostScrollTarget);
            int collapsedHeight = this.mActionBarView.getCollapsedHeight() + this.mActionBarView.getTop();
            if (topOffsetForCoordinateView != collapsedHeight) {
                this.mPostScroller.startScroll(0, topOffsetForCoordinateView, 0, collapsedHeight - topOffsetForCoordinateView);
                postOnAnimation(this.mPostScroll);
            }
        }
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        if (!this.mPostScroller.isFinished()) {
            this.mPostScroller.forceFinished(true);
        }
        removeCallbacks(this.mPostScroll);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
        ActionBar actionBar;
        if (!this.mPostScroller.isFinished()) {
            this.mPostScroller.forceFinished(true);
        }
        removeCallbacks(this.mPostScroll);
        View adjustView = getAdjustView(view);
        if (adjustView == null) {
            return;
        }
        int[] iArr2 = this.mOffsetInWindow;
        iArr2[1] = 0;
        ActionBarContainer actionBarContainer = this.mActionBarTop;
        if (actionBarContainer != null && !this.mIsInSearchMode) {
            actionBarContainer.onNestedPreScroll(view, i, i2, iArr, i3, iArr2);
        }
        if (i2 > 0) {
            int i4 = iArr[1];
            if (i2 - i4 > 0 && (actionBar = this.mActionBar) != null && (actionBar instanceof ActionBarImpl)) {
                int i5 = i2 - i4;
                int topOffsetForCoordinateView = ((ActionBarImpl) actionBar).getTopOffsetForCoordinateView(view);
                if (topOffsetForCoordinateView != -1) {
                    iArr[1] = iArr[1] + ((ActionBarImpl) this.mActionBar).updateTopOffsetOnNestedPreScroll(view, Math.max(0, topOffsetForCoordinateView - i5));
                }
            }
        }
        adjustNestedScrollMotionEventCoordinate(adjustView);
    }

    private View getAdjustView(View view) {
        if (this.mCoordinatedScrollViewSet.isEmpty()) {
            return this.mContentView;
        }
        return this.mCoordinatedScrollViewSet.contains(view) ? view : this.mContentView;
    }

    private void adjustNestedScrollMotionEventCoordinate(View view) {
        if (this.mOverlayMode && !this.mCorrectNestedScrollMotionEventEnabled) {
            IContentInsetState iContentInsetState = this.mContentInsetStateCallback;
            if (iContentInsetState != null) {
                iContentInsetState.onDispatchNestedScrollOffset(this.mOffsetInWindow);
                return;
            }
            return;
        }
        view.offsetTopAndBottom(-this.mOffsetInWindow[1]);
    }

    public void showContentMask(int i) {
        if (this.mSplitAnimContentMask == null) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.split_anim_content_mask);
            this.mSplitAnimContentMask = viewStub;
            this.mInflateLayout = viewStub.inflate();
        }
        ImageView imageView = (ImageView) this.mInflateLayout.findViewById(R.id.image_bg);
        Context context = getContext();
        int measuredWidth = getContentView().getMeasuredWidth();
        int measuredHeight = getContentView().getMeasuredHeight();
        if (measuredWidth > 0 && measuredHeight > 0) {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            getContentView().draw(new Canvas(bitmapCreateBitmap));
            if (Build.VERSION.SDK_INT >= 31) {
                float f = i;
                RenderEffect renderEffectCreateBlurEffect = RenderEffect.createBlurEffect(f, f, Shader.TileMode.CLAMP);
                imageView.setImageBitmap(bitmapCreateBitmap);
                imageView.setRenderEffect(renderEffectCreateBlurEffect);
            } else {
                imageView.setImageBitmap(BitmapFactory.fastBlur(context, bitmapCreateBitmap, i));
            }
        }
        this.mInflateLayout.setAlpha(1.0f);
        getContentView().setVisibility(4);
        this.mInflateLayout.setVisibility(0);
    }

    public void hideContentMask() {
        if (this.mSplitAnimContentMask != null) {
            this.mInflateLayout.setVisibility(8);
            getContentView().setVisibility(0);
        }
    }

    public void setNestedScrollingParentEnabled(boolean z) {
        this.mNestedScrollingParentEnabled = z;
    }

    private void updateExtraPaddingHorizontal(Context context, ExtraPaddingPolicy extraPaddingPolicy, int i, int i2) {
        Resources resources = context.getResources();
        WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(context, resources.getConfiguration());
        if (i == -1) {
            i = windowInfo.windowSize.x;
        }
        int i3 = i;
        if (i2 == -1) {
            i2 = windowInfo.windowSize.y;
        }
        float f = resources.getDisplayMetrics().density;
        extraPaddingPolicy.onContainerSizeChanged(windowInfo.windowSizeDp.x, windowInfo.windowSizeDp.y, i3, i2, f, this.mIsMiuixFloating);
        int extraPaddingDp = extraPaddingPolicy.isEnable() ? (int) (extraPaddingPolicy.getExtraPaddingDp() * f) : 0;
        if (extraPaddingDp != this.mExtraHorizontalPadding) {
            this.mExtraHorizontalPadding = extraPaddingDp;
            this.mShouldExtraPaddingHorizontalNotifyChanged = true;
        }
    }

    @Override // miuix.view.WindowInsetsController
    public void applyWindowInsets(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        if (this.mUserInsetsConfig == null) {
            this.mUserInsetsConfig = new WindowInsetsController.InsetsConfig();
        }
        this.mUserInsetsConfig.update(z, z2, z3, z4, z5, z6);
        applyInsetsConfig(this.mUserInsetsConfig);
    }

    private void applyInternalWindowInsets(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        if (this.mUserInsetsConfig != null) {
            return;
        }
        if (this.mInternalInsetsConfig == null) {
            this.mInternalInsetsConfig = new WindowInsetsController.InsetsConfig();
        }
        this.mInternalInsetsConfig.update(z, z2, z3, z4, z5, z6);
        applyInsetsConfig(this.mInternalInsetsConfig);
    }

    private void applyInsetsConfig(WindowInsetsController.InsetsConfig insetsConfig) {
        if (this.mInsetsConfig == null) {
            this.mInsetsConfig = new WindowInsetsController.InsetsConfig();
        }
        if (this.mInsetsConfig.update(insetsConfig)) {
            requestApplyInsets();
        }
    }

    public void setGroupButtonsPanelBackground(Drawable drawable) {
        GroupButtonsPanel groupButtonsPanel = this.mGroupButtonPanelView;
        if (groupButtonsPanel != null) {
            groupButtonsPanel.setBackground(drawable);
        }
    }

    public void setGroupButtonsPanelBackgroundColor(int i) {
        GroupButtonsPanel groupButtonsPanel = this.mGroupButtonPanelView;
        if (groupButtonsPanel != null) {
            groupButtonsPanel.setBackgroundColor(i);
        }
    }

    public void setGroupButtonsPanelBackgroundResource(int i) {
        GroupButtonsPanel groupButtonsPanel = this.mGroupButtonPanelView;
        if (groupButtonsPanel != null) {
            groupButtonsPanel.setBackgroundResource(i);
        }
    }

    public void addGroupButtons(GroupButtonsConfig groupButtonsConfig, final boolean z) {
        Context context = getContext();
        if (groupButtonsConfig == null || context == null) {
            return;
        }
        if (groupButtonsConfig.getOrientation() == 0) {
            inflate(context, R.layout.miuix_appcompat_group_buttons_horizontal_layout, this);
        } else {
            inflate(context, R.layout.miuix_appcompat_group_buttons_layout, this);
        }
        GroupButtonsPanel groupButtonsPanel = (GroupButtonsPanel) findViewById(R.id.group_buttons_root_layout);
        this.mGroupButtonPanelView = groupButtonsPanel;
        if (groupButtonsPanel != null) {
            if (this.mOnContainerViewLayoutChangeListener == null) {
                View.OnLayoutChangeListener onLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarOverlayLayout.1
                    @Override // android.view.View.OnLayoutChangeListener
                    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                        int i9;
                        if (ActionBarOverlayLayout.this.mGroupButtonPanelView.isAllChildrenInvisible()) {
                            ActionBarOverlayLayout.this.mGroupButtonPanelView.setVisibility(4);
                            i9 = 0;
                        } else {
                            i9 = i4 - i2;
                            ActionBarOverlayLayout.this.mGroupButtonPanelView.setVisibility(0);
                        }
                        if ((ActionBarOverlayLayout.this.mGroupButtonInsetsRect.bottom != i9 || ActionBarOverlayLayout.this.isNeedUpdateGroupButtonInsets()) && z) {
                            int iMax = Math.max(ActionBarOverlayLayout.this.mSystemBarsInsetBottom, i9);
                            if (ActionBarOverlayLayout.this.mCurrentContentInset.bottom != iMax || ActionBarOverlayLayout.this.isNeedUpdateGroupButtonInsets()) {
                                ActionBarOverlayLayout.this.mCurrentContentInset.bottom = iMax;
                                ActionBarOverlayLayout.this.mGroupButtonInsetsRect.set(ActionBarOverlayLayout.this.mCurrentContentInset.left, 0, ActionBarOverlayLayout.this.mCurrentContentInset.right, i9);
                                if (ActionBarOverlayLayout.this.mOverlayMode) {
                                    return;
                                }
                                ActionBarOverlayLayout actionBarOverlayLayout = ActionBarOverlayLayout.this;
                                actionBarOverlayLayout.dispatchContentInset(actionBarOverlayLayout.mCurrentContentInset, true);
                            }
                        }
                    }
                };
                this.mOnContainerViewLayoutChangeListener = onLayoutChangeListener;
                this.mGroupButtonPanelView.addOnLayoutChangeListener(onLayoutChangeListener);
            }
            Button button = (Button) findViewById(R.id.group_primary_button);
            Button button2 = (Button) findViewById(R.id.group_secondary_button);
            Button button3 = (Button) findViewById(R.id.group_tertiary_button);
            groupButtonsConfig.initButton(0, button);
            groupButtonsConfig.initButton(1, button2);
            groupButtonsConfig.initButton(2, button3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNeedUpdateGroupButtonInsets() {
        return (this.mGroupButtonInsetsRect.left == this.mCurrentContentInset.left && this.mGroupButtonInsetsRect.right == this.mCurrentContentInset.right) ? false : true;
    }
}
