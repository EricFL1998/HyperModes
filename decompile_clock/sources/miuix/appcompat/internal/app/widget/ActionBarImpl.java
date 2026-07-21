package miuix.appcompat.internal.app.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.SpinnerAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;
import miuix.appcompat.R;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.ActionBarTransitionListener;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.IFragment;
import miuix.appcompat.app.TextViewDrawableConfig;
import miuix.appcompat.app.strategy.ActionBarConfig;
import miuix.appcompat.app.strategy.ActionBarSpec;
import miuix.appcompat.app.strategy.CommonActionBarStrategy;
import miuix.appcompat.app.strategy.IActionBarStrategy;
import miuix.appcompat.internal.app.widget.actionbar.CollapseTitle;
import miuix.appcompat.internal.app.widget.actionbar.ExpandTitle;
import miuix.appcompat.internal.view.ActionBarPolicy;
import miuix.appcompat.internal.view.ActionModeImpl;
import miuix.appcompat.internal.view.EditActionModeImpl;
import miuix.appcompat.internal.view.SearchActionModeImpl;
import miuix.appcompat.internal.view.menu.action.ActionMenuView;
import miuix.appcompat.internal.view.menu.action.PhoneActionMenuView;
import miuix.appcompat.internal.view.menu.action.ResponsiveActionMenuView;
import miuix.container.ExtraPaddingPolicy;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiuiBlurUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.core.view.NestedContentInsetObserver;
import miuix.core.view.NestedCoordinatorObserver;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.LiteUtils;
import miuix.view.SearchActionMode;

/* JADX INFO: loaded from: classes2.dex */
public class ActionBarImpl extends ActionBar {
    private static final int CONTEXT_DISPLAY_NORMAL = 0;
    private static final int CONTEXT_DISPLAY_SPLIT = 1;
    private static final int INVALID_POSITION = -1;
    public static final boolean IS_COMPLY_WITH_THEME = true;
    private static final int MAX_ACTION_MENU_ITEM_COUNT_UNSET = -1;
    private IActionBarStrategy mActionBarStrategy;
    ActionMode mActionMode;
    private ActionModeView mActionModeView;
    private ActionBarView mActionView;
    private boolean mAdsorptionToNoOverlay;
    private IStateStyle mContainerAnim;
    private ActionBarContainer mContainerView;
    private Rect mContentInset;
    private View mContentMask;
    private View.OnClickListener mContentMaskOnClickListener;
    private Context mContext;
    private int mContextDisplayMode;
    private ActionBarContextView mContextView;
    private int mCurrentAccessibilityImportant;
    private int mCurrentExpandState;
    private boolean mCurrentResizable;
    private boolean mDisplayHomeAsUpSet;
    private ScrollingTabContainerView mExpandTabScrollView;
    private ExtraPaddingPolicy mExtraPaddingPolicy;
    private FragmentManager mFragmentManager;
    private boolean mHiddenByApp;
    private boolean mHiddenBySystem;
    private boolean mIsWindowInfoChanged;
    private ActionBarOverlayLayout mOverlayLayout;
    private SearchActionModeView mSearchActionModeView;
    private SecondaryTabBar mSecondaryExpandTabScrollView;
    private SecondaryTabBar mSecondaryTabScrollView;
    private TabImpl mSelectedTab;
    private boolean mShowHideAnimationEnabled;
    private boolean mShowingForMode;
    private PhoneActionMenuView mSplitMenuView;
    private ActionBarContainer mSplitView;
    private IStateStyle mSpliterAnim;
    private ScrollingTabContainerView mTabScrollView;
    private Context mThemedContext;
    private ActionBarViewPagerController mViewPagerController;
    private int mWindowMode;
    private static androidx.appcompat.app.ActionBar.TabListener sTabListenerWrapper = new androidx.appcompat.app.ActionBar.TabListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl.1
        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabSelected(androidx.appcompat.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            TabImpl tabImpl = (TabImpl) tab;
            if (tabImpl.mInternalCallback != null) {
                tabImpl.mInternalCallback.onTabSelected(tab, fragmentTransaction);
            }
            if (tabImpl.mCallback != null) {
                tabImpl.mCallback.onTabSelected(tab, fragmentTransaction);
            }
        }

        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabUnselected(androidx.appcompat.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            TabImpl tabImpl = (TabImpl) tab;
            if (tabImpl.mInternalCallback != null) {
                tabImpl.mInternalCallback.onTabUnselected(tab, fragmentTransaction);
            }
            if (tabImpl.mCallback != null) {
                tabImpl.mCallback.onTabUnselected(tab, fragmentTransaction);
            }
        }

        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabReselected(androidx.appcompat.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            TabImpl tabImpl = (TabImpl) tab;
            if (tabImpl.mInternalCallback != null) {
                tabImpl.mInternalCallback.onTabReselected(tab, fragmentTransaction);
            }
            if (tabImpl.mCallback != null) {
                tabImpl.mCallback.onTabReselected(tab, fragmentTransaction);
            }
        }
    };
    private static final Integer UNINITIALIZED_OFFSET = -1;
    private final HashMap<View, Integer> mCoordinateOffsetViewSet = new HashMap<>();
    private final HashSet<NestedContentInsetObserver> mNestedContentInsetObserverSet = new HashSet<>();
    private ArrayList<TabImpl> mTabs = new ArrayList<>();
    private boolean isSelectingTab = false;
    private int mSavedTabPosition = -1;
    private ArrayList<androidx.appcompat.app.ActionBar.OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList<>();
    private int mCurWindowVisibility = 0;
    private boolean mNowShowing = true;
    private ActionModeImpl.ActionModeCallback mActionModeCallback = new ActionModeImpl.ActionModeCallback() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl.2
        @Override // miuix.appcompat.internal.view.ActionModeImpl.ActionModeCallback
        public void onActionModeFinish(ActionMode actionMode) {
            ActionBarImpl.this.animateToMode(false);
            ActionBarImpl.this.mActionMode = null;
        }
    };
    private boolean mIsContainerAnimationRunning = false;
    private int mMaxActionMenuItemCount = -1;
    private int mContentInsetTop = 0;
    private int mCurrentActionBarHeightGap = 0;
    private int mActionBarHeightTotalGap = 0;
    private int mCurrentActionBarHeightGapOnShow = 0;
    private int mActionBarHeightTotalGapOnShow = 0;
    private float mTargetTranslationY = 0.0f;
    private final TransitionListener mContainerViewAnimationListener = new TransitionListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl.7
        @Override // miuix.animation.listener.TransitionListener
        public void onBegin(Object obj) {
            super.onBegin(obj);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
            super.onUpdate(obj, collection);
            float translationY = (ActionBarImpl.this.mTargetTranslationY - ActionBarImpl.this.mContainerView.getTranslationY()) / ActionBarImpl.this.mTargetTranslationY;
            ActionBarImpl actionBarImpl = ActionBarImpl.this;
            actionBarImpl.mActionBarHeightTotalGap = (int) Math.max(0.0f, actionBarImpl.mActionBarHeightTotalGapOnShow * translationY);
            ActionBarImpl actionBarImpl2 = ActionBarImpl.this;
            actionBarImpl2.mCurrentActionBarHeightGap = (int) Math.max(0.0f, actionBarImpl2.mCurrentActionBarHeightGapOnShow * translationY);
            ActionBarImpl.this.mOverlayLayout.updateCurrentContentInsetInOverlayMode();
            ActionBarImpl.this.updateCoordinateOffsetView();
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onCancel(Object obj) {
            super.onCancel(obj);
            ActionBarImpl.this.mIsContainerAnimationRunning = false;
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            super.onComplete(obj);
            ActionBarImpl.this.mIsContainerAnimationRunning = false;
        }
    };

    private static boolean checkShowingFlags(boolean z, boolean z2, boolean z3) {
        if (z3) {
            return true;
        }
        return (z || z2) ? false : true;
    }

    public boolean hasNonEmbeddedTabs() {
        return false;
    }

    @Override // miuix.appcompat.app.ActionBar
    public void onDestroy() {
    }

    @Override // miuix.appcompat.app.ActionBar
    public void showActionBarShadow(boolean z) {
    }

    public ActionBarImpl(AppCompatActivity appCompatActivity, ViewGroup viewGroup) {
        this.mContext = appCompatActivity;
        this.mFragmentManager = appCompatActivity.getSupportFragmentManager();
        init(viewGroup);
        this.mActionView.setWindowTitle(appCompatActivity.getTitle());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ActionBarImpl(Fragment fragment) {
        this.mContext = ((IFragment) fragment).getThemedContext();
        this.mFragmentManager = fragment.getChildFragmentManager();
        init((ViewGroup) fragment.getView());
        FragmentActivity activity = fragment.getActivity();
        this.mActionView.setWindowTitle(activity != null ? activity.getTitle() : null);
    }

    public ActionBarImpl(Dialog dialog, ViewGroup viewGroup) {
        this.mContext = dialog.getContext();
        init(viewGroup);
    }

    public static ActionBarImpl getActionBar(View view) {
        while (view != null) {
            if (view instanceof ActionBarOverlayLayout) {
                return (ActionBarImpl) ((ActionBarOverlayLayout) view).getActionBar();
            }
            view = view.getParent() instanceof View ? (View) view.getParent() : null;
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void init(ViewGroup viewGroup) {
        int iResolveInt;
        ExtraPaddingPolicy extraPaddingPolicy;
        if (viewGroup == null) {
            return;
        }
        TypedValue typedValueResolveTypedValue = AttributeResolver.resolveTypedValue(this.mContext, R.attr.actionBarStrategy);
        if (typedValueResolveTypedValue != null) {
            try {
                this.mActionBarStrategy = (IActionBarStrategy) Class.forName(typedValueResolveTypedValue.string.toString()).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception unused) {
            }
        }
        this.mWindowMode = EnvStateManager.getWindowInfo(this.mContext).windowMode;
        ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) viewGroup;
        this.mOverlayLayout = actionBarOverlayLayout;
        actionBarOverlayLayout.setActionBar(this);
        ActionBarView actionBarView = (ActionBarView) viewGroup.findViewById(R.id.action_bar);
        this.mActionView = actionBarView;
        if (actionBarView != null && (extraPaddingPolicy = this.mExtraPaddingPolicy) != null) {
            actionBarView.setExtraPaddingPolicy(extraPaddingPolicy);
        }
        this.mContextView = (ActionBarContextView) viewGroup.findViewById(R.id.action_context_bar);
        this.mContainerView = (ActionBarContainer) viewGroup.findViewById(R.id.action_bar_container);
        this.mSplitView = (ActionBarContainer) viewGroup.findViewById(R.id.split_action_bar);
        View viewFindViewById = viewGroup.findViewById(R.id.content_mask);
        this.mContentMask = viewFindViewById;
        if (viewFindViewById != null) {
            this.mContentMaskOnClickListener = new View.OnClickListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (ActionBarImpl.this.mSplitMenuView == null || !ActionBarImpl.this.mSplitMenuView.isOverflowMenuShowing()) {
                        return;
                    }
                    ActionBarImpl.this.mSplitMenuView.getPresenter().hideOverflowMenu(true);
                }
            };
        }
        ActionBarView actionBarView2 = this.mActionView;
        if (actionBarView2 == null && this.mContextView == null && this.mContainerView == null) {
            throw new IllegalStateException(getClass().getSimpleName() + " can only be used with a compatible window decor layout");
        }
        this.mContextDisplayMode = actionBarView2.isSplitActionBar() ? 1 : 0;
        byte b = (this.mActionView.getDisplayOptions() & 4) != 0;
        if (b != false) {
            this.mDisplayHomeAsUpSet = true;
        }
        ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(this.mContext);
        setHomeButtonEnabled(actionBarPolicy.enableHomeButtonByDefault() || b == true);
        setHasEmbeddedTabs(actionBarPolicy.hasEmbeddedTabs());
        boolean z = MiuiBlurUtils.isEnable() && !LiteUtils.isCommonLiteStrategy();
        ActionBarContainer actionBarContainer = this.mContainerView;
        if (actionBarContainer != null) {
            actionBarContainer.setSupportBlur(z);
        }
        ActionBarContainer actionBarContainer2 = this.mSplitView;
        if (actionBarContainer2 != null) {
            actionBarContainer2.setSupportBlur(z);
        }
        if (z && (iResolveInt = AttributeResolver.resolveInt(this.mContext, R.attr.bgBlurOptions, 0)) != 0) {
            int displayOptions = getDisplayOptions();
            if ((iResolveInt & 1) != 0) {
                displayOptions |= 32768;
            }
            if ((iResolveInt & 2) != 0) {
                displayOptions |= 16384;
            }
            setDisplayOptions(displayOptions);
        }
        if (this.mActionBarStrategy == null) {
            this.mActionBarStrategy = new CommonActionBarStrategy();
        }
        this.mOverlayLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl.4
            int lastWidth = 0;

            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                int measuredWidth = ActionBarImpl.this.mOverlayLayout.getMeasuredWidth();
                if (this.lastWidth == measuredWidth && !ActionBarImpl.this.mIsWindowInfoChanged) {
                    return true;
                }
                ActionBarImpl.this.mIsWindowInfoChanged = false;
                this.lastWidth = measuredWidth;
                int expandState = ActionBarImpl.this.getExpandState();
                ActionBarImpl actionBarImpl = ActionBarImpl.this;
                actionBarImpl.applyActionBarStrategy(actionBarImpl.mActionView, ActionBarImpl.this.mContextView);
                int expandState2 = ActionBarImpl.this.getExpandState();
                ActionBarImpl.this.mOverlayLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                ActionBarImpl.this.mOverlayLayout.requestLayout();
                return expandState == expandState2;
            }
        });
        this.mOverlayLayout.addOnLayoutChangeListener(new AnonymousClass5());
    }

    /* JADX INFO: renamed from: miuix.appcompat.internal.app.widget.ActionBarImpl$5, reason: invalid class name */
    class AnonymousClass5 implements View.OnLayoutChangeListener {
        int lastWidth = 0;

        AnonymousClass5() {
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            int i9 = i3 - i;
            if (this.lastWidth != i9 || ActionBarImpl.this.mIsWindowInfoChanged) {
                ActionBarImpl.this.mIsWindowInfoChanged = false;
                this.lastWidth = i9;
                ActionBarImpl.this.mActionView.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1826x4a40acd0();
                    }
                });
            }
        }

        /* JADX INFO: renamed from: lambda$onLayoutChange$0$miuix-appcompat-internal-app-widget-ActionBarImpl$5, reason: not valid java name */
        /* synthetic */ void m1826x4a40acd0() {
            ActionBarImpl actionBarImpl = ActionBarImpl.this;
            actionBarImpl.applyActionBarStrategy(actionBarImpl.mActionView, ActionBarImpl.this.mContextView);
        }
    }

    private ActionBarSpec getActionBarSpec(ActionBarContainer actionBarContainer, ActionBarView actionBarView) {
        ActionBarSpec actionBarSpec = new ActionBarSpec();
        actionBarSpec.deviceType = this.mOverlayLayout.getDeviceType();
        actionBarSpec.windowMode = this.mWindowMode;
        if (actionBarContainer != null && actionBarView != null) {
            float f = actionBarView.getContext().getResources().getDisplayMetrics().density;
            Point windowSize = EnvStateManager.getWindowSize(actionBarView.getContext());
            actionBarSpec.windowWidth = windowSize.x;
            actionBarSpec.windowHeight = windowSize.y;
            actionBarSpec.windowWidthDp = MiuixUIUtils.px2dp(f, actionBarSpec.windowWidth);
            actionBarSpec.windowHeightDp = MiuixUIUtils.px2dp(f, actionBarSpec.windowHeight);
            actionBarSpec.actionBarWidth = actionBarContainer.getMeasuredWidth();
            if (actionBarSpec.actionBarWidth == 0) {
                actionBarSpec.actionBarWidth = this.mOverlayLayout.getMeasuredWidth();
            }
            actionBarSpec.actionBarWidthDp = MiuixUIUtils.px2dp(f, actionBarSpec.actionBarWidth);
            actionBarSpec.actionBarHeight = actionBarView.getMeasuredHeight();
            actionBarSpec.actionBarHeightDp = MiuixUIUtils.px2dp(f, actionBarSpec.actionBarHeight);
            actionBarSpec.isUserSetExpandState = actionBarView.isUserSetExpandState();
            actionBarSpec.expandState = actionBarView.getExpandState();
            actionBarSpec.resizable = actionBarView.isResizable();
            actionBarSpec.isUserSetEndActionMenuItemLimit = actionBarView.isUserSetEndActionMenuItemLimit();
            actionBarSpec.endActionMenuItemLimit = actionBarView.getEndActionMenuItemLimit();
        }
        Context context = this.mContext;
        if (context instanceof AppCompatActivity) {
            actionBarSpec.isInFloatingWindowMode = ((AppCompatActivity) context).isInFloatingWindowMode();
        }
        return actionBarSpec;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyActionBarStrategy(ActionBarView actionBarView, ActionBarContextView actionBarContextView) {
        if (this.mActionBarStrategy == null) {
            return;
        }
        int expandState = getExpandState();
        ActionBarConfig actionBarConfigConfig = this.mActionBarStrategy.config(this, getActionBarSpec(this.mContainerView, this.mActionView));
        if (actionBarView != null && actionBarConfigConfig != null) {
            if (!actionBarView.isUserSetExpandState() || actionBarConfigConfig.overrideUserExpandStateConfig) {
                if (!actionBarView.isResizable() || !actionBarConfigConfig.resizable) {
                    actionBarView.setExpandState(actionBarConfigConfig.expandState, false, true);
                }
                actionBarView.setResizable(actionBarConfigConfig.resizable);
            }
            if (!actionBarView.isUserSetEndActionMenuItemLimit() || actionBarConfigConfig.overrideUserEndMenuConfig) {
                actionBarView.setEndActionMenuItemLimit(actionBarConfigConfig.endMenuMaxItemCount);
            }
        }
        if (actionBarContextView != null && actionBarConfigConfig != null && (!actionBarContextView.isUserSetExpandState() || actionBarConfigConfig.overrideUserExpandStateConfig)) {
            if (!actionBarContextView.isResizable() || !actionBarConfigConfig.resizable) {
                actionBarContextView.setExpandState(actionBarConfigConfig.expandState, false, true);
            }
            actionBarContextView.setResizable(actionBarConfigConfig.resizable);
        }
        this.mCurrentExpandState = getExpandState();
        this.mCurrentResizable = isResizable();
        int i = this.mCurrentExpandState;
        if (i != 1 || expandState == i || this.mContentInset == null) {
            return;
        }
        Iterator<View> it = this.mCoordinateOffsetViewSet.keySet().iterator();
        while (it.hasNext()) {
            this.mCoordinateOffsetViewSet.put(it.next(), Integer.valueOf(this.mContentInset.top));
        }
        Iterator<NestedContentInsetObserver> it2 = this.mNestedContentInsetObserverSet.iterator();
        while (it2.hasNext()) {
            it2.next().onContentInsetChanged(this.mContentInset);
        }
        ActionBarContainer actionBarContainer = this.mContainerView;
        if (actionBarContainer != null) {
            actionBarContainer.setActionBarBlurByNestedScrolled(false);
        }
    }

    @Override // androidx.appcompat.app.ActionBar
    public void onConfigurationChanged(Configuration configuration) {
        this.mIsWindowInfoChanged = true;
        this.mWindowMode = EnvStateManager.getWindowInfo(this.mContext, configuration).windowMode;
        setHasEmbeddedTabs(ActionBarPolicy.get(this.mContext).hasEmbeddedTabs());
        SearchActionModeView searchActionModeView = this.mSearchActionModeView;
        if (searchActionModeView == null || searchActionModeView.isAttachedToWindow()) {
            return;
        }
        this.mSearchActionModeView.onConfigurationChanged(configuration);
    }

    public void onFloatingModeChanged(boolean z) {
        this.mContainerView.setIsMiuixFloating(z);
        SearchActionModeView searchActionModeView = this.mSearchActionModeView;
        if (searchActionModeView != null) {
            searchActionModeView.onFloatingModeChanged();
        }
    }

    private void setHasEmbeddedTabs(boolean z) {
        ActionBarContainer actionBarContainer = this.mContainerView;
        if (actionBarContainer != null) {
            actionBarContainer.setTabContainer(null);
        }
        this.mActionView.setEmbeddedTabView(this.mTabScrollView, this.mExpandTabScrollView, this.mSecondaryTabScrollView, this.mSecondaryExpandTabScrollView);
        boolean z2 = getNavigationMode() == 2;
        ScrollingTabContainerView scrollingTabContainerView = this.mTabScrollView;
        if (scrollingTabContainerView != null) {
            if (z2) {
                scrollingTabContainerView.setVisibility(0);
            } else {
                scrollingTabContainerView.setVisibility(8);
            }
            this.mTabScrollView.setEmbeded(true);
        }
        ScrollingTabContainerView scrollingTabContainerView2 = this.mExpandTabScrollView;
        if (scrollingTabContainerView2 != null) {
            if (z2) {
                scrollingTabContainerView2.setVisibility(0);
            } else {
                scrollingTabContainerView2.setVisibility(8);
            }
            this.mExpandTabScrollView.setEmbeded(true);
        }
        SecondaryTabBar secondaryTabBar = this.mSecondaryTabScrollView;
        if (secondaryTabBar != null) {
            if (z2) {
                secondaryTabBar.asViewGroup().setVisibility(0);
            } else {
                secondaryTabBar.asViewGroup().setVisibility(8);
            }
        }
        SecondaryTabBar secondaryTabBar2 = this.mSecondaryExpandTabScrollView;
        if (secondaryTabBar2 != null) {
            if (z2) {
                secondaryTabBar2.asViewGroup().setVisibility(0);
            } else {
                secondaryTabBar2.asViewGroup().setVisibility(8);
            }
        }
        this.mActionView.setCollapsable(false);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setTabsMode(boolean z) {
        setHasEmbeddedTabs(z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setTabBadgeVisibility(int i, boolean z) {
        this.mTabScrollView.setBadgeVisibility(i, z);
        this.mExpandTabScrollView.setBadgeVisibility(i, z);
        this.mSecondaryTabScrollView.setBadgeVisibility(i, z);
        this.mSecondaryExpandTabScrollView.setBadgeVisibility(i, z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setTabBadgeDisappearOnClick(int i, boolean z) {
        this.mSecondaryTabScrollView.setTabBadgeDisappearOnClick(i, z);
        this.mSecondaryExpandTabScrollView.setTabBadgeDisappearOnClick(i, z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setTabIconWithPosition(int i, int i2, int i3, int i4, int i5, int i6) {
        setTabIconWithPosition(i, i2, i3 != 0 ? this.mContext.getDrawable(i3) : null, i4 != 0 ? this.mContext.getDrawable(i4) : null, i5 != 0 ? this.mContext.getDrawable(i5) : null, i6 != 0 ? this.mContext.getDrawable(i6) : null);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setTabIconWithPosition(int i, int i2, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        this.mTabScrollView.setTabIconWithPosition(i, i2, drawable, drawable2, drawable3, drawable4);
        this.mExpandTabScrollView.setTabIconWithPosition(i, i2, drawable, drawable2, drawable3, drawable4);
        this.mSecondaryTabScrollView.setTabIconWithPosition(i, i2, drawable, drawable2, drawable3, drawable4);
        this.mSecondaryExpandTabScrollView.setTabIconWithPosition(i, i2, drawable, drawable2, drawable3, drawable4);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setTabTextAppearance(int i, int i2) {
        this.mTabScrollView.setTextAppearance(i, i2);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setExpandTabTextAppearance(int i, int i2) {
        this.mExpandTabScrollView.setTextAppearance(i, i2);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setSecondaryTabTextAppearance(int i, int i2) {
        this.mSecondaryTabScrollView.setTextAppearance(i, i2);
        this.mSecondaryExpandTabScrollView.setTextAppearance(i, i2);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setSecondaryTabTextAppearance(int i, int i2, int i3) {
        this.mSecondaryTabScrollView.setTextAppearance(i, i2, i3);
        this.mSecondaryExpandTabScrollView.setTextAppearance(i, i2, i3);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setCustomView(View view) {
        this.mActionView.setCustomNavigationView(view);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setCustomView(View view, androidx.appcompat.app.ActionBar.LayoutParams layoutParams) {
        view.setLayoutParams(layoutParams);
        this.mActionView.setCustomNavigationView(view);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void registerCoordinatedScrollView(View view) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mOverlayLayout;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.registerCoordinatedScrollView(view);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void unregisterCoordinatedScrollView(View view) {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mOverlayLayout;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.unregisterCoordinatedScrollView(view);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setAdsorptionToNoOverlay(boolean z) {
        this.mAdsorptionToNoOverlay = z;
    }

    @Override // miuix.appcompat.app.ActionBar
    public boolean isAdsorptionToNoOverlay() {
        return this.mAdsorptionToNoOverlay;
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setIcon(int i) {
        this.mActionView.setIcon(i);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setIcon(Drawable drawable) {
        this.mActionView.setIcon(drawable);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setLogo(int i) {
        this.mActionView.setLogo(i);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setLogo(Drawable drawable) {
        this.mActionView.setLogo(drawable);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setListNavigationCallbacks(SpinnerAdapter spinnerAdapter, androidx.appcompat.app.ActionBar.OnNavigationListener onNavigationListener) {
        this.mActionView.setDropdownAdapter(spinnerAdapter);
        this.mActionView.setCallback(onNavigationListener);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setSelectedNavigationItem(int i) {
        int navigationMode = this.mActionView.getNavigationMode();
        if (navigationMode == 1) {
            this.mActionView.setDropdownSelectedPosition(i);
        } else {
            if (navigationMode == 2) {
                selectTab(this.mTabs.get(i));
                return;
            }
            throw new IllegalStateException("setSelectedNavigationIndex not valid for current navigation mode");
        }
    }

    @Override // androidx.appcompat.app.ActionBar
    public int getSelectedNavigationIndex() {
        TabImpl tabImpl;
        int navigationMode = this.mActionView.getNavigationMode();
        if (navigationMode == 1) {
            return this.mActionView.getDropdownSelectedPosition();
        }
        if (navigationMode == 2 && (tabImpl = this.mSelectedTab) != null) {
            return tabImpl.getPosition();
        }
        return -1;
    }

    @Override // androidx.appcompat.app.ActionBar
    public int getNavigationItemCount() {
        int navigationMode = this.mActionView.getNavigationMode();
        if (navigationMode != 1) {
            if (navigationMode != 2) {
                return 0;
            }
            return this.mTabs.size();
        }
        SpinnerAdapter dropdownAdapter = this.mActionView.getDropdownAdapter();
        if (dropdownAdapter != null) {
            return dropdownAdapter.getCount();
        }
        return 0;
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setTitle(CharSequence charSequence) {
        this.mActionView.setTitle(charSequence);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setSubtitle(CharSequence charSequence) {
        this.mActionView.setSubtitle(charSequence);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setSubTitleDrawable(TextViewDrawableConfig textViewDrawableConfig) {
        this.mActionView.setSubTitleDrawable(textViewDrawableConfig);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayOptions(int i, int i2) {
        int displayOptions = this.mActionView.getDisplayOptions();
        if ((i2 & 4) != 0) {
            this.mDisplayHomeAsUpSet = true;
        }
        this.mActionView.setDisplayOptions((i & i2) | ((~i2) & displayOptions));
        int displayOptions2 = this.mActionView.getDisplayOptions();
        ActionBarContainer actionBarContainer = this.mContainerView;
        if (actionBarContainer != null) {
            actionBarContainer.setEnableBlur((32768 & displayOptions2) != 0);
        }
        ActionBarContainer actionBarContainer2 = this.mSplitView;
        if (actionBarContainer2 != null) {
            actionBarContainer2.setEnableBlur((displayOptions2 & 16384) != 0);
        }
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayUseLogoEnabled(boolean z) {
        int blurOptions = getBlurOptions();
        setDisplayOptions((z ? 1 : 0) | blurOptions, blurOptions | 1);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayShowHomeEnabled(boolean z) {
        int blurOptions = getBlurOptions();
        setDisplayOptions((z ? 2 : 0) | blurOptions, blurOptions | 2);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayHomeAsUpEnabled(boolean z) {
        int blurOptions = getBlurOptions();
        setDisplayOptions((z ? 4 : 0) | blurOptions, blurOptions | 4);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayShowTitleEnabled(boolean z) {
        int blurOptions = getBlurOptions();
        setDisplayOptions((z ? 8 : 0) | blurOptions, blurOptions | 8);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayShowCustomEnabled(boolean z) {
        int blurOptions = getBlurOptions();
        setDisplayOptions((z ? 16 : 0) | blurOptions, blurOptions | 16);
    }

    private int getBlurOptions() {
        return ((getDisplayOptions() & 32768) != 0 ? 32768 : 0) | ((getDisplayOptions() & 16384) != 0 ? 16384 : 0);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setHomeButtonEnabled(boolean z) {
        this.mActionView.setHomeButtonEnabled(z);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setBackgroundDrawable(Drawable drawable) {
        this.mContainerView.setPrimaryBackground(drawable);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setSplitBackgroundDrawable(Drawable drawable) {
        if (this.mSplitView != null) {
            for (int i = 0; i < this.mSplitView.getChildCount(); i++) {
                if (this.mSplitView.getChildAt(i) instanceof ActionMenuView) {
                    this.mSplitView.getChildAt(i).setBackground(drawable);
                }
            }
        }
    }

    @Override // androidx.appcompat.app.ActionBar
    public View getCustomView() {
        return this.mActionView.getCustomNavigationView();
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setCustomView(int i) {
        setCustomView(LayoutInflater.from(getThemedContext()).inflate(i, (ViewGroup) this.mActionView, false));
    }

    @Override // androidx.appcompat.app.ActionBar
    public CharSequence getTitle() {
        return this.mActionView.getTitle();
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setTitle(int i) {
        setTitle(this.mContext.getString(i));
    }

    @Override // androidx.appcompat.app.ActionBar
    public CharSequence getSubtitle() {
        return this.mActionView.getSubtitle();
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setSubtitle(int i) {
        setSubtitle(this.mContext.getString(i));
    }

    @Override // androidx.appcompat.app.ActionBar
    public int getNavigationMode() {
        return this.mActionView.getNavigationMode();
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setNavigationMode(int i) {
        if (this.mActionView.getNavigationMode() == 2) {
            this.mSavedTabPosition = getSelectedNavigationIndex();
            selectTab(null);
            this.mTabScrollView.setVisibility(8);
            this.mExpandTabScrollView.setVisibility(8);
            this.mSecondaryTabScrollView.asViewGroup().setVisibility(8);
            this.mSecondaryExpandTabScrollView.asViewGroup().setVisibility(8);
        }
        this.mActionView.setNavigationMode(i);
        if (i == 2) {
            ensureTabsExist();
            this.mTabScrollView.setVisibility(0);
            this.mExpandTabScrollView.setVisibility(0);
            this.mSecondaryTabScrollView.asViewGroup().setVisibility(0);
            this.mSecondaryExpandTabScrollView.asViewGroup().setVisibility(0);
            int i2 = this.mSavedTabPosition;
            if (i2 != -1) {
                setSelectedNavigationItem(i2);
                this.mSavedTabPosition = -1;
            }
        }
        this.mActionView.setCollapsable(false);
    }

    @Override // androidx.appcompat.app.ActionBar
    public int getDisplayOptions() {
        return this.mActionView.getDisplayOptions();
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setDisplayOptions(int i) {
        if ((i & 4) != 0) {
            this.mDisplayHomeAsUpSet = true;
        }
        this.mActionView.setDisplayOptions(i);
        int displayOptions = this.mActionView.getDisplayOptions();
        ActionBarContainer actionBarContainer = this.mContainerView;
        if (actionBarContainer != null) {
            actionBarContainer.setEnableBlur((displayOptions & 32768) != 0);
        }
        ActionBarContainer actionBarContainer2 = this.mSplitView;
        if (actionBarContainer2 != null) {
            actionBarContainer2.setEnableBlur((i & 16384) != 0);
        }
    }

    @Override // androidx.appcompat.app.ActionBar
    public androidx.appcompat.app.ActionBar.Tab newTab() {
        return new TabImpl();
    }

    @Override // androidx.appcompat.app.ActionBar
    public void addTab(androidx.appcompat.app.ActionBar.Tab tab) {
        addTab(tab, this.mTabs.isEmpty());
    }

    @Override // androidx.appcompat.app.ActionBar
    public void addTab(androidx.appcompat.app.ActionBar.Tab tab, boolean z) {
        if (isFragmentViewPagerMode()) {
            throw new IllegalStateException("Cannot add tab directly in fragment view pager mode!\n Please using addFragmentTab().");
        }
        internalAddTab(tab, z);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void addTab(androidx.appcompat.app.ActionBar.Tab tab, int i) {
        addTab(tab, i, this.mTabs.isEmpty());
    }

    @Override // androidx.appcompat.app.ActionBar
    public void addTab(androidx.appcompat.app.ActionBar.Tab tab, int i, boolean z) {
        if (isFragmentViewPagerMode()) {
            throw new IllegalStateException("Cannot add tab directly in fragment view pager mode!\n Please using addFragmentTab().");
        }
        internalAddTab(tab, i, z);
    }

    ActionBarOverlayLayout getActionBarOverlayLayout() {
        return this.mOverlayLayout;
    }

    void internalAddTab(androidx.appcompat.app.ActionBar.Tab tab) {
        internalAddTab(tab, getTabCount() == 0);
    }

    void internalAddTab(androidx.appcompat.app.ActionBar.Tab tab, boolean z) {
        ensureTabsExist();
        this.mTabScrollView.addTab(tab, z);
        this.mExpandTabScrollView.addTab(tab, z);
        this.mSecondaryTabScrollView.addTab(tab, z);
        this.mSecondaryExpandTabScrollView.addTab(tab, z);
        configureTab(tab, this.mTabs.size());
        if (z) {
            selectTab(tab);
        }
    }

    void internalAddTab(androidx.appcompat.app.ActionBar.Tab tab, int i) {
        internalAddTab(tab, i, i == getTabCount());
    }

    void internalAddTab(androidx.appcompat.app.ActionBar.Tab tab, int i, boolean z) {
        ensureTabsExist();
        this.mTabScrollView.addTab(tab, i, z);
        this.mExpandTabScrollView.addTab(tab, i, z);
        this.mSecondaryTabScrollView.addTab(tab, i, z);
        this.mSecondaryExpandTabScrollView.addTab(tab, i, z);
        configureTab(tab, i);
        if (z) {
            selectTab(tab);
        }
    }

    void updateTab(int i) {
        ensureTabsExist();
        this.mTabScrollView.updateTab(i);
        this.mExpandTabScrollView.updateTab(i);
        this.mSecondaryTabScrollView.updateTab(i);
        this.mSecondaryExpandTabScrollView.updateTab(i);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void removeTab(androidx.appcompat.app.ActionBar.Tab tab) {
        if (isFragmentViewPagerMode()) {
            throw new IllegalStateException("Cannot add tab directly in fragment view pager mode!\n Please using addFragmentTab().");
        }
        internalRemoveTab(tab);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void removeTabAt(int i) {
        if (isFragmentViewPagerMode()) {
            throw new IllegalStateException("Cannot add tab directly in fragment view pager mode!\n Please using addFragmentTab().");
        }
        internalRemoveTabAt(i);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void removeAllTabs() {
        if (isFragmentViewPagerMode()) {
            throw new IllegalStateException("Cannot add tab directly in fragment view pager mode!\n Please using addFragmentTab().");
        }
        internalRemoveAllTabs();
    }

    void internalRemoveTab(androidx.appcompat.app.ActionBar.Tab tab) {
        internalRemoveTabAt(tab.getPosition());
    }

    void internalRemoveTabAt(int i) {
        if (this.mTabScrollView == null) {
            return;
        }
        TabImpl tabImpl = this.mSelectedTab;
        int position = tabImpl != null ? tabImpl.getPosition() : this.mSavedTabPosition;
        this.mTabScrollView.removeTabAt(i);
        this.mExpandTabScrollView.removeTabAt(i);
        this.mSecondaryTabScrollView.removeTabAt(i);
        this.mSecondaryExpandTabScrollView.removeTabAt(i);
        TabImpl tabImplRemove = this.mTabs.remove(i);
        if (tabImplRemove != null) {
            tabImplRemove.setPosition(-1);
        }
        int size = this.mTabs.size();
        for (int i2 = i; i2 < size; i2++) {
            this.mTabs.get(i2).setPosition(i2);
        }
        if (position == i) {
            selectTab(this.mTabs.isEmpty() ? null : this.mTabs.get(Math.max(0, i - 1)));
        }
        if (this.mTabs.isEmpty()) {
            this.mSavedTabPosition = -1;
        }
    }

    void internalRemoveAllTabs() {
        cleanupTabs();
    }

    @Override // androidx.appcompat.app.ActionBar
    public void selectTab(androidx.appcompat.app.ActionBar.Tab tab) {
        selectTab(tab, true);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void selectTab(androidx.appcompat.app.ActionBar.Tab tab, boolean z) {
        if (this.isSelectingTab) {
            this.isSelectingTab = false;
            return;
        }
        this.isSelectingTab = true;
        Context context = this.mContext;
        if ((context instanceof Activity) && (((Activity) context).isDestroyed() || ((Activity) this.mContext).isFinishing())) {
            return;
        }
        if (getNavigationMode() != 2) {
            this.mSavedTabPosition = tab != null ? tab.getPosition() : -1;
            return;
        }
        FragmentTransaction fragmentTransactionDisallowAddToBackStack = this.mFragmentManager.beginTransaction().disallowAddToBackStack();
        TabImpl tabImpl = this.mSelectedTab;
        if (tabImpl != tab) {
            this.mTabScrollView.setTabSelected(tab != null ? tab.getPosition() : -1, z);
            this.mExpandTabScrollView.setTabSelected(tab != null ? tab.getPosition() : -1, z);
            this.mSecondaryTabScrollView.setTabSelected(tab != null ? tab.getPosition() : -1);
            this.mSecondaryExpandTabScrollView.setTabSelected(tab != null ? tab.getPosition() : -1);
            TabImpl tabImpl2 = this.mSelectedTab;
            if (tabImpl2 != null) {
                tabImpl2.getCallback().onTabUnselected(this.mSelectedTab, fragmentTransactionDisallowAddToBackStack);
            }
            TabImpl tabImpl3 = (TabImpl) tab;
            this.mSelectedTab = tabImpl3;
            if (tabImpl3 != null) {
                tabImpl3.mWithAnim = z;
                this.mSelectedTab.getCallback().onTabSelected(this.mSelectedTab, fragmentTransactionDisallowAddToBackStack);
            }
        } else if (tabImpl != null) {
            tabImpl.getCallback().onTabReselected(this.mSelectedTab, fragmentTransactionDisallowAddToBackStack);
            this.mTabScrollView.animateToTab(tab.getPosition());
            this.mExpandTabScrollView.animateToTab(tab.getPosition());
            this.mSecondaryTabScrollView.animateToTab(tab.getPosition());
            this.mSecondaryExpandTabScrollView.animateToTab(tab.getPosition());
        }
        if (!fragmentTransactionDisallowAddToBackStack.isEmpty()) {
            fragmentTransactionDisallowAddToBackStack.commit();
        }
        this.isSelectingTab = false;
    }

    @Override // androidx.appcompat.app.ActionBar
    public androidx.appcompat.app.ActionBar.Tab getSelectedTab() {
        return this.mSelectedTab;
    }

    @Override // androidx.appcompat.app.ActionBar
    public androidx.appcompat.app.ActionBar.Tab getTabAt(int i) {
        return this.mTabs.get(i);
    }

    @Override // androidx.appcompat.app.ActionBar
    public int getTabCount() {
        return this.mTabs.size();
    }

    @Override // androidx.appcompat.app.ActionBar
    public Context getThemedContext() {
        if (this.mThemedContext == null) {
            TypedValue typedValue = new TypedValue();
            this.mContext.getTheme().resolveAttribute(android.R.attr.actionBarWidgetTheme, typedValue, true);
            int i = typedValue.resourceId;
            if (i != 0) {
                this.mThemedContext = new ContextThemeWrapper(this.mContext, i);
            } else {
                this.mThemedContext = this.mContext;
            }
        }
        return this.mThemedContext;
    }

    @Override // androidx.appcompat.app.ActionBar
    public int getHeight() {
        return this.mContainerView.getHeight();
    }

    @Override // miuix.appcompat.app.ActionBar
    public int getExpandedHeight() {
        return this.mContainerView.getExpandedHeight();
    }

    @Override // androidx.appcompat.app.ActionBar
    public void show() {
        show((AnimState) null);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void show(boolean z) {
        show(z, null);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void show(AnimState animState) {
        show(true, animState);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void show(boolean z, AnimState animState) {
        if (this.mHiddenByApp) {
            this.mHiddenByApp = false;
            updateVisibility(false, z, animState);
        }
    }

    void showForActionMode() {
        if (this.mShowingForMode) {
            return;
        }
        this.mShowingForMode = true;
        updateVisibility(false);
        this.mCurrentExpandState = getExpandState();
        this.mCurrentResizable = isResizable();
        if (this.mActionModeView instanceof SearchActionModeView) {
            setResizable(false);
        } else {
            this.mContainerView.startActionMode();
            ((ActionBarContextView) this.mActionModeView).setExpandState(this.mCurrentExpandState);
            ((ActionBarContextView) this.mActionModeView).setResizable(this.mCurrentResizable);
        }
        this.mCurrentAccessibilityImportant = this.mActionView.getImportantForAccessibility();
        this.mActionView.setImportantForAccessibility(4);
        this.mActionView.onActionModeStart(this.mActionModeView instanceof SearchActionModeView, (getDisplayOptions() & 32768) != 0);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void hide() {
        hide((AnimState) null);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void hide(boolean z) {
        hide(z, null);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void hide(AnimState animState) {
        hide(true, animState);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void hide(boolean z, AnimState animState) {
        if (this.mHiddenByApp) {
            return;
        }
        this.mHiddenByApp = true;
        updateVisibility(false, z, animState);
    }

    @Override // miuix.appcompat.app.ActionBar
    public View getActionBarView() {
        return this.mActionView;
    }

    @Override // miuix.appcompat.app.ActionBar
    public View getTitleView(int i) {
        return this.mActionView.getTitleView(i);
    }

    @Override // miuix.appcompat.app.ActionBar
    public View getSubTitleView(int i) {
        return this.mActionView.getSubTitleView(i);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setTitleClickable(boolean z) {
        this.mActionView.setTitleClickable(z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setSubTitleClickListener(View.OnClickListener onClickListener) {
        this.mActionView.setSubTitleClickListener(onClickListener);
    }

    void hideForActionMode() {
        if (this.mShowingForMode) {
            this.mShowingForMode = false;
            this.mActionView.onActionModeEnd((getDisplayOptions() & 32768) != 0);
            updateVisibility(false);
            if (this.mActionModeView instanceof SearchActionModeView) {
                setResizable(this.mCurrentResizable);
            } else {
                this.mContainerView.finishActionMode();
                this.mCurrentResizable = ((ActionBarContextView) this.mActionModeView).isResizable();
                this.mCurrentExpandState = ((ActionBarContextView) this.mActionModeView).getExpandState();
                setResizable(this.mCurrentResizable);
                this.mActionView.setExpandState(this.mCurrentExpandState);
            }
            this.mActionView.setImportantForAccessibility(this.mCurrentAccessibilityImportant);
        }
    }

    @Override // androidx.appcompat.app.ActionBar
    public boolean isShowing() {
        return this.mNowShowing;
    }

    @Override // androidx.appcompat.app.ActionBar
    public void addOnMenuVisibilityListener(androidx.appcompat.app.ActionBar.OnMenuVisibilityListener onMenuVisibilityListener) {
        this.mMenuVisibilityListeners.add(onMenuVisibilityListener);
    }

    @Override // androidx.appcompat.app.ActionBar
    public void removeOnMenuVisibilityListener(androidx.appcompat.app.ActionBar.OnMenuVisibilityListener onMenuVisibilityListener) {
        this.mMenuVisibilityListeners.remove(onMenuVisibilityListener);
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        Rect baseInnerInsets;
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
        ActionMode actionModeCreateActionMode = createActionMode(callback);
        ActionModeView actionModeView = this.mActionModeView;
        if (((actionModeView instanceof SearchActionModeView) && (actionModeCreateActionMode instanceof SearchActionModeImpl)) || ((actionModeView instanceof ActionBarContextView) && (actionModeCreateActionMode instanceof EditActionModeImpl))) {
            actionModeView.closeMode();
            this.mActionModeView.killMode();
        }
        ActionModeView actionModeViewCreateActionModeView = createActionModeView(callback);
        this.mActionModeView = actionModeViewCreateActionModeView;
        if (actionModeViewCreateActionModeView == null) {
            throw new IllegalStateException("not set windowSplitActionBar true in activity style!");
        }
        if (!(actionModeCreateActionMode instanceof ActionModeImpl)) {
            return null;
        }
        ActionModeImpl actionModeImpl = (ActionModeImpl) actionModeCreateActionMode;
        actionModeImpl.setActionModeView(actionModeViewCreateActionModeView);
        if ((actionModeImpl instanceof SearchActionModeImpl) && (baseInnerInsets = this.mOverlayLayout.getBaseInnerInsets()) != null) {
            ((SearchActionModeImpl) actionModeImpl).setPendingInsets(baseInnerInsets);
        }
        actionModeImpl.setActionModeCallback(this.mActionModeCallback);
        if (!actionModeImpl.dispatchOnCreate()) {
            return null;
        }
        actionModeCreateActionMode.invalidate();
        this.mActionModeView.initForMode(actionModeCreateActionMode);
        animateToMode(true);
        ActionBarContainer actionBarContainer = this.mSplitView;
        if (actionBarContainer != null && this.mContextDisplayMode == 1 && actionBarContainer.getVisibility() != 0) {
            this.mSplitView.setVisibility(0);
        }
        ActionModeView actionModeView2 = this.mActionModeView;
        if (actionModeView2 instanceof ActionBarContextView) {
            ((ActionBarContextView) actionModeView2).sendAccessibilityEvent(32);
        }
        this.mActionMode = actionModeCreateActionMode;
        return actionModeCreateActionMode;
    }

    void animateToMode(boolean z) {
        if (z) {
            showForActionMode();
        } else {
            hideForActionMode();
        }
        this.mActionModeView.animateToVisibility(z);
        if (this.mTabScrollView == null || this.mActionView.isTightTitleWithEmbeddedTabs() || !this.mActionView.isCollapsed()) {
            return;
        }
        this.mTabScrollView.setEnabled(!z);
        this.mExpandTabScrollView.setEnabled(!z);
        this.mSecondaryTabScrollView.asViewGroup().setEnabled(!z);
        this.mSecondaryExpandTabScrollView.asViewGroup().setEnabled(!z);
    }

    private ActionMode createActionMode(ActionMode.Callback callback) {
        if (callback instanceof SearchActionMode.Callback) {
            return new SearchActionModeImpl(this.mContext, callback);
        }
        return new EditActionModeImpl(this.mContext, callback);
    }

    public ActionModeView createActionModeView(ActionMode.Callback callback) {
        ActionModeView actionModeView;
        int i;
        if (callback instanceof SearchActionMode.Callback) {
            if (this.mSearchActionModeView == null) {
                SearchActionModeView searchActionModeViewCreateSearchActionModeView = createSearchActionModeView();
                this.mSearchActionModeView = searchActionModeViewCreateSearchActionModeView;
                searchActionModeViewCreateSearchActionModeView.setExtraPaddingPolicy(this.mExtraPaddingPolicy);
            }
            if (this.mOverlayLayout != this.mSearchActionModeView.getParent()) {
                this.mOverlayLayout.addView(this.mSearchActionModeView);
            }
            measureSearchActionModeView();
            this.mSearchActionModeView.addAnimationListener(this.mActionView);
            actionModeView = this.mSearchActionModeView;
        } else {
            actionModeView = this.mContextView;
            if (actionModeView == null) {
                throw new IllegalStateException("not set windowSplitActionBar true in activity style!");
            }
        }
        if ((actionModeView instanceof ActionBarContextView) && (i = this.mMaxActionMenuItemCount) != -1) {
            ((ActionBarContextView) actionModeView).setActionMenuItemLimit(i);
        }
        return actionModeView;
    }

    public SearchActionModeView createSearchActionModeView() {
        SearchActionModeView searchActionModeView = (SearchActionModeView) LayoutInflater.from(getThemedContext()).inflate(R.layout.miuix_appcompat_search_action_mode_view, (ViewGroup) this.mOverlayLayout, false);
        searchActionModeView.setOverlayModeView(this.mOverlayLayout);
        searchActionModeView.setOnBackClickListener(new View.OnClickListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ActionBarImpl.this.mActionMode != null) {
                    ActionBarImpl.this.mActionMode.finish();
                }
            }
        });
        return searchActionModeView;
    }

    private void measureSearchActionModeView() {
        this.mSearchActionModeView.measure(ViewGroup.getChildMeasureSpec(this.mOverlayLayout.getMeasuredWidth(), 0, this.mSearchActionModeView.getLayoutParams().width), ViewGroup.getChildMeasureSpec(this.mOverlayLayout.getMeasuredHeight(), 0, this.mSearchActionModeView.getLayoutParams().height));
    }

    void setExtraPaddingPolicy(ExtraPaddingPolicy extraPaddingPolicy) {
        if (this.mExtraPaddingPolicy != extraPaddingPolicy) {
            this.mExtraPaddingPolicy = extraPaddingPolicy;
            ActionBarView actionBarView = this.mActionView;
            if (actionBarView != null) {
                actionBarView.setExtraPaddingPolicy(extraPaddingPolicy);
            }
            SearchActionModeView searchActionModeView = this.mSearchActionModeView;
            if (searchActionModeView != null) {
                searchActionModeView.setExtraPaddingPolicy(this.mExtraPaddingPolicy);
            }
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void showSplitActionBar(boolean z, boolean z2) {
        if (this.mActionView.isSplitActionBar()) {
            if (z) {
                this.mSplitView.show(z2);
            } else {
                this.mSplitView.hide(z2);
            }
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public boolean isFragmentViewPagerMode() {
        return this.mViewPagerController != null;
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setFragmentViewPagerMode(FragmentActivity fragmentActivity) {
        setFragmentViewPagerMode(fragmentActivity, true);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setFragmentViewPagerMode(FragmentActivity fragmentActivity, boolean z) {
        if (isFragmentViewPagerMode()) {
            return;
        }
        removeAllTabs();
        setNavigationMode(2);
        this.mViewPagerController = new ActionBarViewPagerController(this, this.mFragmentManager, fragmentActivity.getLifecycle(), z);
        addOnFragmentViewPagerChangeListener(this.mTabScrollView);
        addOnFragmentViewPagerChangeListener(this.mExpandTabScrollView);
        addOnFragmentViewPagerChangeListener(this.mSecondaryTabScrollView);
        addOnFragmentViewPagerChangeListener(this.mSecondaryExpandTabScrollView);
        ActionBarContainer actionBarContainer = this.mSplitView;
        if (actionBarContainer != null) {
            addOnFragmentViewPagerChangeListener(actionBarContainer);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addOnFragmentViewPagerChangeListener(ActionBar.FragmentViewPagerChangeListener fragmentViewPagerChangeListener) {
        this.mViewPagerController.addOnFragmentViewPagerChangeListener(fragmentViewPagerChangeListener);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void removeOnFragmentViewPagerChangeListener(ActionBar.FragmentViewPagerChangeListener fragmentViewPagerChangeListener) {
        this.mViewPagerController.removeOnFragmentViewPagerChangeListener(fragmentViewPagerChangeListener);
    }

    @Override // miuix.appcompat.app.ActionBar
    public int getFragmentTabCount() {
        return this.mViewPagerController.getFragmentTabCount();
    }

    @Override // miuix.appcompat.app.ActionBar
    public Fragment getFragmentAt(int i) {
        return this.mViewPagerController.getFragmentAt(i);
    }

    @Override // miuix.appcompat.app.ActionBar
    public int addFragmentTab(String str, androidx.appcompat.app.ActionBar.Tab tab, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        return this.mViewPagerController.addFragmentTab(str, tab, cls, bundle, z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public int addFragmentTab(String str, androidx.appcompat.app.ActionBar.Tab tab, int i, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        return this.mViewPagerController.addFragmentTab(str, tab, i, cls, bundle, z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void replaceFragmentTab(String str, int i, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        this.mViewPagerController.replaceFragmentTab(str, i, cls, bundle, z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void removeFragmentTabAt(int i) {
        this.mViewPagerController.removeFragmentAt(i);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void removeFragmentTab(String str) {
        this.mViewPagerController.removeFragmentTab(str);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void removeFragmentTab(androidx.appcompat.app.ActionBar.Tab tab) {
        this.mViewPagerController.removeFragmentTab(tab);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void removeFragmentTab(Fragment fragment) {
        this.mViewPagerController.removeFragment(fragment);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void removeAllFragmentTab() {
        this.mViewPagerController.removeAllFragmentTab();
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setFragmentActionMenuAt(int i, boolean z) {
        this.mViewPagerController.setFragmentActionMenuAt(i, z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setProgressBarVisibility(boolean z) {
        this.mActionView.setProgressBarVisibility(z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setProgressBarIndeterminateVisibility(boolean z) {
        this.mActionView.setProgressBarIndeterminateVisibility(z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setProgressBarIndeterminate(boolean z) {
        this.mActionView.setProgressBarIndeterminate(z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setProgress(int i) {
        this.mActionView.setProgress(i);
    }

    @Override // miuix.appcompat.app.ActionBar
    public int getViewPagerOffscreenPageLimit() {
        return this.mViewPagerController.getViewPagerOffscreenPageLimit();
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setViewPagerOffscreenPageLimit(int i) {
        this.mViewPagerController.setViewPagerOffscreenPageLimit(i);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setStartView(View view) {
        this.mActionView.setStartView(view);
    }

    @Override // miuix.appcompat.app.ActionBar
    public View getStartView() {
        return this.mActionView.getStartView();
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setEndView(View view) {
        this.mActionView.setEndView(view);
    }

    @Override // miuix.appcompat.app.ActionBar
    public View getEndView() {
        return this.mActionView.getEndView();
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setViewPagerDecor(View view) {
        this.mViewPagerController.setViewPagerDecor(view);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setViewPagerDraggable(boolean z) {
        this.mViewPagerController.setViewPagerDraggable(z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setResizable(boolean z) {
        this.mActionView.setResizable(z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public boolean isResizable() {
        return this.mActionView.isResizable();
    }

    @Override // miuix.appcompat.app.ActionBar
    public int getExpandState() {
        return this.mActionView.getExpandState();
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setExpandState(int i) {
        this.mActionView.setExpandStateByUser(i);
        this.mActionView.setExpandState(i);
        ActionBarContextView actionBarContextView = this.mContextView;
        if (actionBarContextView != null) {
            actionBarContextView.setExpandStateByUser(i);
            this.mContextView.setExpandState(i);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setExpandState(int i, boolean z) {
        this.mActionView.setExpandStateByUser(i);
        this.mActionView.setExpandState(i, z, false);
        ActionBarContextView actionBarContextView = this.mContextView;
        if (actionBarContextView != null) {
            actionBarContextView.setExpandStateByUser(i);
            this.mContextView.setExpandState(i, z, false);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setExpandState(int i, boolean z, boolean z2) {
        this.mActionView.setExpandStateByUser(i);
        this.mActionView.setExpandState(i, z, z2);
        ActionBarContextView actionBarContextView = this.mContextView;
        if (actionBarContextView != null) {
            actionBarContextView.setExpandStateByUser(i);
            this.mContextView.setExpandState(i, z, z2);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setActionModeAnim(boolean z) {
        ActionBarContextView actionBarContextView = this.mContextView;
        if (actionBarContextView != null) {
            actionBarContextView.setActionModeAnim(z);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBar
    public void registerCoordinateScrollView(View view) {
        if (view == 0) {
            Log.w("miuix-appcompat", "warning!! the view is null on registerCoordinateScrollView!!");
            return;
        }
        if (view instanceof NestedContentInsetObserver) {
            NestedContentInsetObserver nestedContentInsetObserver = (NestedContentInsetObserver) view;
            this.mNestedContentInsetObserverSet.add(nestedContentInsetObserver);
            Rect rect = this.mContentInset;
            if (rect != null) {
                nestedContentInsetObserver.onContentInsetChanged(rect);
            }
        } else {
            HashMap<View, Integer> map = this.mCoordinateOffsetViewSet;
            Rect rect2 = this.mContentInset;
            map.put(view, Integer.valueOf(rect2 != null ? rect2.top : UNINITIALIZED_OFFSET.intValue()));
            Rect rect3 = this.mContentInset;
            if (rect3 != null) {
                this.mCoordinateOffsetViewSet.put(view, Integer.valueOf(rect3.top));
                doUpdateTopOffsetForCoordinateView(view, this.mContentInset.top);
            }
        }
        if (this.mContainerView.getActionBarCoordinateListener() == null) {
            this.mContainerView.setActionBarCoordinateListener(createActionBarCoordinateListener());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.appcompat.app.ActionBar
    public void unregisterCoordinateScrollView(View view) {
        if (view instanceof NestedContentInsetObserver) {
            this.mNestedContentInsetObserverSet.remove((NestedContentInsetObserver) view);
        } else {
            this.mCoordinateOffsetViewSet.remove(view);
        }
        if (this.mCoordinateOffsetViewSet.size() == 0 && this.mNestedContentInsetObserverSet.size() == 0) {
            this.mContainerView.setActionBarCoordinateListener(null);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void resetCoordinateScrollView(View view) {
        if (view instanceof NestedContentInsetObserver) {
            if (this.mNestedContentInsetObserverSet.contains(view)) {
                doUpdateTopOffsetForCoordinateView(view, 0);
            }
        } else if (this.mCoordinateOffsetViewSet.containsKey(view)) {
            HashMap<View, Integer> map = this.mCoordinateOffsetViewSet;
            Rect rect = this.mContentInset;
            map.put(view, Integer.valueOf(rect != null ? rect.top : UNINITIALIZED_OFFSET.intValue()));
            Rect rect2 = this.mContentInset;
            doUpdateTopOffsetForCoordinateView(view, rect2 != null ? rect2.top : 0);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addActionBarTransitionListener(ActionBarTransitionListener actionBarTransitionListener) {
        this.mContainerView.addActionBarTransitionListener(actionBarTransitionListener);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void removeActionBarTransitionListener(ActionBarTransitionListener actionBarTransitionListener) {
        this.mContainerView.removeActionBarTransitionListener(actionBarTransitionListener);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setActionBarStrategy(IActionBarStrategy iActionBarStrategy) {
        this.mActionBarStrategy = iActionBarStrategy;
        this.mOverlayLayout.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1825x6aa58b14();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setActionBarStrategy$0$miuix-appcompat-internal-app-widget-ActionBarImpl, reason: not valid java name */
    /* synthetic */ void m1825x6aa58b14() {
        applyActionBarStrategy(this.mActionView, this.mContextView);
    }

    @Override // miuix.appcompat.app.ActionBar
    public IActionBarStrategy getActionBarStrategy() {
        return this.mActionBarStrategy;
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setEndActionMenuItemLimit(int i) {
        this.mActionView.setUserSetEndActionMenuItemLimit(true);
        this.mActionView.setEndActionMenuItemLimit(i);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setActionMenuItemLimit(int i) {
        this.mMaxActionMenuItemCount = i;
        this.mActionView.setActionMenuItemLimit(i);
        ActionModeView actionModeView = this.mActionModeView;
        if (actionModeView instanceof ActionBarContextView) {
            ((ActionBarContextView) actionModeView).setActionMenuItemLimit(this.mMaxActionMenuItemCount);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addBadgeOnItemView(int i) {
        addBadgeOnItemView(i, 2);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addBadgeOnItemView(int i, int i2) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isEndActionMenuEnable()) {
            return;
        }
        this.mActionView.addBadgeOnItemView(i, i2);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addBadgeOnItemView(MenuItem menuItem) {
        addBadgeOnItemView(menuItem, 2);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addBadgeOnItemView(MenuItem menuItem, int i) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isEndActionMenuEnable()) {
            return;
        }
        this.mActionView.addBadgeOnItemView(menuItem, i);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addNumberBadgeOnItemView(int i, int i2) {
        addNumberBadgeOnItemView(i, i2, 2);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addNumberBadgeOnItemView(int i, int i2, int i3) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isEndActionMenuEnable()) {
            return;
        }
        this.mActionView.addNumberBadgeOnItemView(i, i2, i3);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addBadgeOnMoreButton() {
        addBadgeOnMoreButton(2);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addBadgeOnMoreButton(int i) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isEndActionMenuEnable()) {
            return;
        }
        this.mActionView.addBadgeOnMoreButton(i);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addNumberBadgeOnMoreButton(int i) {
        addNumberBadgeOnMoreButton(i, 2);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void addNumberBadgeOnMoreButton(int i, int i2) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isEndActionMenuEnable()) {
            return;
        }
        this.mActionView.addNumberBadgeOnMoreButton(i, i2);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void clearBadgeOnItemView(int i) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isEndActionMenuEnable()) {
            return;
        }
        this.mActionView.clearBadgeOnItemView(i);
    }

    @Override // miuix.appcompat.app.ActionBar
    public Map<Integer, Boolean> getHyperMenuPrimaryCheckedData() {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView != null && actionBarView.isEndActionMenuEnable() && this.mActionView.isHyperActionMenuEnable()) {
            return this.mActionView.getHyperMenuPrimaryCheckedData();
        }
        return null;
    }

    @Override // miuix.appcompat.app.ActionBar
    public Map<Integer, Boolean[]> getHyperMenuSecondaryCheckedData() {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView != null && actionBarView.isEndActionMenuEnable() && this.mActionView.isHyperActionMenuEnable()) {
            return this.mActionView.getHyperMenuSecondaryCheckedData();
        }
        return null;
    }

    @Override // miuix.appcompat.app.ActionBar
    public void restoreHyperMenuPrimaryCheckedData(Map<Integer, Boolean> map) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView != null && actionBarView.isEndActionMenuEnable() && this.mActionView.isHyperActionMenuEnable()) {
            this.mActionView.restorePrimaryMenuCheckedData(map);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void restoreHyperMenuSecondaryCheckedData(Map<Integer, Boolean[]> map) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView != null && actionBarView.isEndActionMenuEnable() && this.mActionView.isHyperActionMenuEnable()) {
            this.mActionView.restoreSecondaryMenuCheckedData(map);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setHyperMenuSaveStatusByIdEnabled(boolean z) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView != null && actionBarView.isEndActionMenuEnable() && this.mActionView.isHyperActionMenuEnable()) {
            this.mActionView.setHyperMenuSaveStatusByIdEnabled(z);
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public Map<Integer, Boolean> getHyperSplitMenuPrimaryCheckedData() {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isHyperSplitMenuEnabled()) {
            return null;
        }
        return this.mActionView.getHyperSplitMenuPrimaryCheckedData();
    }

    @Override // miuix.appcompat.app.ActionBar
    public Map<Integer, Boolean[]> getHyperSplitMenuSecondaryCheckedData() {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isHyperSplitMenuEnabled()) {
            return null;
        }
        return this.mActionView.getHyperSplitMenuSecondaryCheckedData();
    }

    @Override // miuix.appcompat.app.ActionBar
    public void restoreHyperSplitMenuPrimaryCheckedData(Map<Integer, Boolean> map) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isHyperSplitMenuEnabled()) {
            return;
        }
        this.mActionView.restoreHyperSplitMenuPrimaryCheckedData(map);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void restoreHyperSplitMenuSecondaryCheckedData(Map<Integer, Boolean[]> map) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isHyperSplitMenuEnabled()) {
            return;
        }
        this.mActionView.restoreHyperSplitMenuSecondaryCheckedData(map);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void setHyperSplitMenuSaveStatusByIdEnabled(boolean z) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isHyperSplitMenuEnabled()) {
            return;
        }
        this.mActionView.setHyperSplitMenuSaveStatusByIdEnabled(z);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void clearBadgeOnItemView(MenuItem menuItem) {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isEndActionMenuEnable()) {
            return;
        }
        this.mActionView.clearBadgeOnItemView(menuItem);
    }

    @Override // miuix.appcompat.app.ActionBar
    public void clearBadgeOnMoreButton() {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView == null || !actionBarView.isEndActionMenuEnable()) {
            return;
        }
        this.mActionView.clearBadgeOnMoreButton();
    }

    public View getContentView() {
        ActionBarOverlayLayout actionBarOverlayLayout = this.mOverlayLayout;
        if (actionBarOverlayLayout != null) {
            return actionBarOverlayLayout.findViewById(android.R.id.content);
        }
        return null;
    }

    private void createContextView(boolean z, boolean z2) {
        ActionBarContainer actionBarContainer;
        ViewStub viewStub = (ViewStub) this.mOverlayLayout.findViewById(R.id.split_action_bar_vs);
        if (viewStub != null) {
            actionBarContainer = (ActionBarContainer) viewStub.inflate();
        } else {
            actionBarContainer = (ActionBarContainer) this.mOverlayLayout.findViewById(R.id.split_action_bar);
        }
        if (actionBarContainer != null) {
            this.mActionView.setSplitView(actionBarContainer);
            this.mActionView.setSplitActionBar(z);
            this.mActionView.setSplitWhenNarrow(z2);
            ViewStub viewStub2 = (ViewStub) this.mOverlayLayout.findViewById(R.id.action_context_bar_vs);
            if (viewStub2 != null) {
                this.mContextView = (ActionBarContextView) viewStub2.inflate();
            } else {
                this.mContextView = (ActionBarContextView) this.mOverlayLayout.findViewById(R.id.action_context_bar);
            }
            ActionBarContextView actionBarContextView = this.mContextView;
            if (actionBarContextView != null) {
                this.mContainerView.setActionBarContextView(actionBarContextView);
                this.mOverlayLayout.setActionBarContextView(this.mContextView);
                this.mContextView.setSplitView(actionBarContainer);
                this.mContextView.setSplitActionBar(z);
                this.mContextView.setSplitWhenNarrow(z2);
            }
        }
    }

    private void addContentMask() {
        View viewFindViewById;
        ViewStub viewStub = (ViewStub) this.mOverlayLayout.findViewById(R.id.content_mask_vs);
        if (viewStub != null) {
            viewFindViewById = viewStub.inflate();
        } else {
            viewFindViewById = this.mOverlayLayout.findViewById(R.id.content_mask);
        }
        this.mOverlayLayout.setContentMask(viewFindViewById);
    }

    private void ensureTabsExist() {
        SecondaryTabBar secondarySegmentTabBar;
        SecondaryTabBar secondarySegmentTabBar2;
        if (this.mTabScrollView != null) {
            this.mActionView.checkTabsAdded();
            return;
        }
        CollapseTabContainer collapseTabContainer = new CollapseTabContainer(this.mContext);
        ExpandTabContainer expandTabContainer = new ExpandTabContainer(this.mContext);
        int iResolveInt = AttributeResolver.resolveInt(this.mContext, R.attr.actionBarSecondaryTabBarType, 0);
        if (iResolveInt == 0) {
            secondarySegmentTabBar = new SecondaryCollapseTabContainer(this.mContext);
            secondarySegmentTabBar2 = new SecondaryExpandTabContainer(this.mContext);
        } else if (iResolveInt == 1) {
            secondarySegmentTabBar = new SecondarySegmentTabBar(this.mContext);
            secondarySegmentTabBar2 = new SecondarySegmentTabBar(this.mContext);
        } else {
            throw new IllegalArgumentException("actionBarSecondaryTabBarType: " + iResolveInt + " is invalid.");
        }
        collapseTabContainer.setVisibility(0);
        expandTabContainer.setVisibility(0);
        secondarySegmentTabBar.asViewGroup().setVisibility(0);
        secondarySegmentTabBar2.asViewGroup().setVisibility(0);
        this.mActionView.setEmbeddedTabView(collapseTabContainer, expandTabContainer, secondarySegmentTabBar, secondarySegmentTabBar2);
        collapseTabContainer.setEmbeded(true);
        this.mTabScrollView = collapseTabContainer;
        this.mExpandTabScrollView = expandTabContainer;
        this.mSecondaryTabScrollView = secondarySegmentTabBar;
        this.mSecondaryExpandTabScrollView = secondarySegmentTabBar2;
    }

    private void configureTab(androidx.appcompat.app.ActionBar.Tab tab, int i) {
        TabImpl tabImpl = (TabImpl) tab;
        if (tabImpl.getCallback() == null) {
            throw new IllegalStateException("Action Bar Tab must have a Callback");
        }
        tabImpl.setPosition(i);
        this.mTabs.add(i, tabImpl);
        int size = this.mTabs.size();
        while (true) {
            i++;
            if (i >= size) {
                return;
            } else {
                this.mTabs.get(i).setPosition(i);
            }
        }
    }

    private void cleanupTabs() {
        if (this.mSelectedTab != null) {
            selectTab(null);
        }
        this.mTabs.clear();
        ScrollingTabContainerView scrollingTabContainerView = this.mTabScrollView;
        if (scrollingTabContainerView != null) {
            scrollingTabContainerView.removeAllTabs();
        }
        ScrollingTabContainerView scrollingTabContainerView2 = this.mExpandTabScrollView;
        if (scrollingTabContainerView2 != null) {
            scrollingTabContainerView2.removeAllTabs();
        }
        SecondaryTabBar secondaryTabBar = this.mSecondaryTabScrollView;
        if (secondaryTabBar != null) {
            secondaryTabBar.removeAllTabs();
        }
        SecondaryTabBar secondaryTabBar2 = this.mSecondaryExpandTabScrollView;
        if (secondaryTabBar2 != null) {
            secondaryTabBar2.removeAllTabs();
        }
        this.mSavedTabPosition = -1;
    }

    private void updateVisibility(boolean z) {
        updateVisibility(z, true, null);
    }

    private void updateVisibility(boolean z, boolean z2, AnimState animState) {
        if (checkShowingFlags(this.mHiddenByApp, this.mHiddenBySystem, this.mShowingForMode)) {
            if (this.mNowShowing) {
                return;
            }
            this.mNowShowing = true;
            doShow(z, z2, animState);
            return;
        }
        if (this.mNowShowing) {
            this.mNowShowing = false;
            doHide(z, z2, animState);
        }
    }

    private IStateStyle startContainerViewAnimation(boolean z, String str, AnimState animState, AnimState animState2) {
        AnimState animStateAdd;
        AnimState animStateAdd2;
        int height = this.mContainerView.getHeight();
        if (height == 0) {
            int childMeasureSpec = ViewGroup.getChildMeasureSpec(this.mOverlayLayout.getMeasuredWidth(), 0, this.mOverlayLayout.getLayoutParams().width);
            int childMeasureSpec2 = ViewGroup.getChildMeasureSpec(this.mOverlayLayout.getMeasuredHeight(), 0, this.mOverlayLayout.getLayoutParams().height);
            this.mContainerView.measure(childMeasureSpec, childMeasureSpec2);
            applyActionBarStrategy(this.mActionView, this.mContextView);
            this.mContainerView.measure(childMeasureSpec, childMeasureSpec2);
            height = this.mContainerView.getMeasuredHeight();
        }
        int i = -height;
        this.mTargetTranslationY = i;
        AnimConfig animConfig = new AnimConfig();
        animConfig.addListeners(this.mContainerViewAnimationListener);
        if (z) {
            animConfig.setEase(EaseManager.getStyle(-2, 0.9f, 0.25f));
            animConfig.addListeners(new ViewShowTransitionListener(this.mContainerView, this));
            animStateAdd = animState2 == null ? new AnimState(str).add(ViewProperty.TRANSLATION_Y, 0.0d).add(ViewProperty.ALPHA, 1.0d) : animState2;
            animStateAdd2 = (animState == null && animState2 == null) ? new AnimState(str).add(ViewProperty.TRANSLATION_Y, i).add(ViewProperty.ALPHA, 0.0d) : animState;
        } else {
            animConfig.setEase(EaseManager.getStyle(-2, 1.0f, 0.35f));
            animConfig.addListeners(new ViewHideTransitionListener(this.mContainerView, this));
            AnimState animStateAdd3 = animState2 == null ? new AnimState(str).add(ViewProperty.TRANSLATION_Y, i).add(ViewProperty.ALPHA, 0.0d) : animState2;
            if (animState == null && animState2 == null) {
                animStateAdd = animStateAdd3;
                animStateAdd2 = new AnimState(str).add(ViewProperty.TRANSLATION_Y, 0.0d).add(ViewProperty.ALPHA, 1.0d);
            } else {
                animStateAdd = animStateAdd3;
            }
        }
        IStateStyle iStateStyleState = Folme.useAt(this.mContainerView).state();
        if (animStateAdd2 != null) {
            animStateAdd2.setTag(str);
            iStateStyleState = iStateStyleState.setTo(animStateAdd2);
        }
        iStateStyleState.to(animStateAdd, animConfig);
        this.mIsContainerAnimationRunning = true;
        return iStateStyleState;
    }

    private int getSplitHeight() {
        View childAt;
        int height = this.mSplitView.getHeight();
        if (this.mSplitView.getChildCount() != 1 || (childAt = this.mSplitView.getChildAt(0)) == null) {
            return height;
        }
        if (childAt instanceof ResponsiveActionMenuView) {
            return height;
        }
        if (!(childAt instanceof PhoneActionMenuView)) {
            return height;
        }
        PhoneActionMenuView phoneActionMenuView = (PhoneActionMenuView) childAt;
        return !phoneActionMenuView.isOverflowMenuShowing() ? phoneActionMenuView.getCollapsedHeight() : height;
    }

    private IStateStyle startSplitViewAnimation(boolean z, String str, AnimState animState) {
        int splitHeight = getSplitHeight();
        if (z) {
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(EaseManager.getStyle(-2, 0.9f, 0.25f));
            AnimState animStateAdd = new AnimState(str).add(ViewProperty.TRANSLATION_Y, 0.0d).add(ViewProperty.ALPHA, 1.0d);
            IStateStyle iStateStyleState = Folme.useAt(this.mSplitView).state();
            if (animState != null) {
                animState.setTag(str);
                iStateStyleState = iStateStyleState.setTo(animState);
            }
            return iStateStyleState.to(animStateAdd, animConfig);
        }
        AnimConfig animConfig2 = new AnimConfig();
        animConfig2.setEase(EaseManager.getStyle(-2, 1.0f, 0.35f));
        animConfig2.addListeners(new ViewHideTransitionListener(this.mSplitView, this));
        AnimState animStateAdd2 = new AnimState(str).add(ViewProperty.TRANSLATION_Y, splitHeight + 100).add(ViewProperty.ALPHA, 0.0d);
        IStateStyle iStateStyleState2 = Folme.useAt(this.mSplitView).state();
        if (animState != null) {
            animState.setTag(str);
            iStateStyleState2 = iStateStyleState2.setTo(animState);
        }
        return iStateStyleState2.to(animStateAdd2, animConfig2);
    }

    private void doShow(boolean z) {
        doShow(z, true, null);
    }

    private void doShow(boolean z, boolean z2, AnimState animState) {
        AnimState currentState;
        IStateStyle iStateStyle = this.mContainerAnim;
        if (iStateStyle == null || !this.mIsContainerAnimationRunning) {
            currentState = null;
        } else {
            currentState = iStateStyle.getCurrentState();
            this.mContainerAnim.cancel();
        }
        boolean z3 = (isShowHideAnimationEnabled() || z) && z2;
        if (this.mActionMode instanceof SearchActionMode) {
            this.mContainerView.setVisibility(this.mOverlayLayout.isInOverlayMode() ? 4 : 8);
        } else {
            this.mContainerView.setVisibility(0);
        }
        this.mContainerView.resetActionBarBlurConfigOnReshow();
        if (z3) {
            this.mContainerAnim = startContainerViewAnimation(true, "ShowActionBar", currentState, animState);
        } else {
            this.mContainerView.setTranslationY(0.0f);
            this.mContainerView.setAlpha(1.0f);
        }
    }

    private void doHide(boolean z) {
        doHide(z, true, null);
    }

    private void doHide(boolean z, boolean z2, AnimState animState) {
        AnimState currentState;
        IStateStyle iStateStyle = this.mContainerAnim;
        if (iStateStyle == null || !this.mIsContainerAnimationRunning) {
            currentState = null;
        } else {
            currentState = iStateStyle.getCurrentState();
            this.mContainerAnim.cancel();
        }
        if ((isShowHideAnimationEnabled() || z) && z2) {
            this.mContainerAnim = startContainerViewAnimation(false, "HideActionBar", currentState, animState);
            return;
        }
        ActionBarContainer actionBarContainer = this.mContainerView;
        actionBarContainer.setTranslationY(-actionBarContainer.getHeight());
        this.mContainerView.setAlpha(0.0f);
        this.mActionBarHeightTotalGap = 0;
        this.mCurrentActionBarHeightGap = 0;
        this.mContainerView.setVisibility(8);
    }

    boolean isShowHideAnimationEnabled() {
        return this.mShowHideAnimationEnabled;
    }

    @Override // androidx.appcompat.app.ActionBar
    public void setShowHideAnimationEnabled(boolean z) {
        this.mShowHideAnimationEnabled = z;
        if (z) {
            return;
        }
        if (isShowing()) {
            doShow(false);
        } else {
            doHide(false);
        }
    }

    private void updateContentMaskVisibility(boolean z) {
        if (this.mSplitView.getChildCount() == 2 && (this.mSplitView.getChildAt(1) instanceof PhoneActionMenuView)) {
            PhoneActionMenuView phoneActionMenuView = (PhoneActionMenuView) this.mSplitView.getChildAt(1);
            this.mSplitMenuView = phoneActionMenuView;
            if (!phoneActionMenuView.isOverflowMenuShowing() || this.mContentMask == null) {
                return;
            }
            if (z) {
                this.mOverlayLayout.getContentMaskAnimator(this.mContentMaskOnClickListener).show().start();
            } else {
                this.mOverlayLayout.getContentMaskAnimator(null).hide().start();
            }
        }
    }

    public class TabImpl extends androidx.appcompat.app.ActionBar.Tab {
        private androidx.appcompat.app.ActionBar.TabListener mCallback;
        private CharSequence mContentDesc;
        private View mCustomView;
        private Drawable mIcon;
        private androidx.appcompat.app.ActionBar.TabListener mInternalCallback;
        private Object mTag;
        private CharSequence mText;
        private int mPosition = -1;
        public boolean mWithAnim = true;

        public TabImpl() {
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public Object getTag() {
            return this.mTag;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public androidx.appcompat.app.ActionBar.Tab setTag(Object obj) {
            this.mTag = obj;
            return this;
        }

        public androidx.appcompat.app.ActionBar.TabListener getCallback() {
            return ActionBarImpl.sTabListenerWrapper;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public androidx.appcompat.app.ActionBar.Tab setTabListener(androidx.appcompat.app.ActionBar.TabListener tabListener) {
            this.mCallback = tabListener;
            return this;
        }

        public androidx.appcompat.app.ActionBar.Tab setInternalTabListener(androidx.appcompat.app.ActionBar.TabListener tabListener) {
            this.mInternalCallback = tabListener;
            return this;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public View getCustomView() {
            return this.mCustomView;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public androidx.appcompat.app.ActionBar.Tab setCustomView(View view) {
            this.mCustomView = view;
            if (!ActionBarImpl.this.mActionView.isUserSetExpandState()) {
                ActionBarImpl.this.mActionView.setExpandState(0);
                ActionBarImpl.this.setResizable(false);
            }
            if (this.mPosition >= 0) {
                ActionBarImpl.this.mTabScrollView.updateTab(this.mPosition);
            }
            return this;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public androidx.appcompat.app.ActionBar.Tab setCustomView(int i) {
            return setCustomView(LayoutInflater.from(ActionBarImpl.this.getThemedContext()).inflate(i, (ViewGroup) null));
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public Drawable getIcon() {
            return this.mIcon;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public int getPosition() {
            return this.mPosition;
        }

        public void setPosition(int i) {
            this.mPosition = i;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public CharSequence getText() {
            return this.mText;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public androidx.appcompat.app.ActionBar.Tab setIcon(Drawable drawable) {
            this.mIcon = drawable;
            if (this.mPosition >= 0) {
                ActionBarImpl.this.mTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mExpandTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mSecondaryTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mSecondaryExpandTabScrollView.updateTab(this.mPosition);
            }
            return this;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public androidx.appcompat.app.ActionBar.Tab setIcon(int i) {
            return setIcon(ActionBarImpl.this.mContext.getResources().getDrawable(i));
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public androidx.appcompat.app.ActionBar.Tab setText(CharSequence charSequence) {
            this.mText = charSequence;
            if (this.mPosition >= 0) {
                ActionBarImpl.this.mTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mExpandTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mSecondaryTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mSecondaryTabScrollView.updateTab(this.mPosition);
            }
            return this;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public androidx.appcompat.app.ActionBar.Tab setText(int i) {
            return setText(ActionBarImpl.this.mContext.getResources().getText(i));
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public void select() {
            ActionBarImpl.this.selectTab(this, true);
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public androidx.appcompat.app.ActionBar.Tab setContentDescription(int i) {
            return setContentDescription(ActionBarImpl.this.mContext.getResources().getText(i));
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public androidx.appcompat.app.ActionBar.Tab setContentDescription(CharSequence charSequence) {
            this.mContentDesc = charSequence;
            if (this.mPosition >= 0) {
                ActionBarImpl.this.mTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mExpandTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mSecondaryTabScrollView.updateTab(this.mPosition);
                ActionBarImpl.this.mSecondaryExpandTabScrollView.updateTab(this.mPosition);
            }
            return this;
        }

        @Override // androidx.appcompat.app.ActionBar.Tab
        public CharSequence getContentDescription() {
            return this.mContentDesc;
        }
    }

    private static class ViewShowTransitionListener extends TransitionListener {
        private WeakReference<ActionBarImpl> mActionBarRef;
        private WeakReference<View> mRef;

        public ViewShowTransitionListener(View view, ActionBarImpl actionBarImpl) {
            this.mRef = new WeakReference<>(view);
            this.mActionBarRef = new WeakReference<>(actionBarImpl);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            super.onComplete(obj);
            ActionBarImpl actionBarImpl = this.mActionBarRef.get();
            View view = this.mRef.get();
            if (view == null || actionBarImpl == null || !actionBarImpl.mNowShowing || view.getId() != R.id.action_bar_container) {
                return;
            }
            view.setTranslationY(0.0f);
        }
    }

    private static class ViewHideTransitionListener extends TransitionListener {
        private WeakReference<ActionBarImpl> mActionBarRef;
        private WeakReference<View> mRef;

        public ViewHideTransitionListener(View view, ActionBarImpl actionBarImpl) {
            this.mRef = new WeakReference<>(view);
            this.mActionBarRef = new WeakReference<>(actionBarImpl);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            super.onComplete(obj);
            ActionBarImpl actionBarImpl = this.mActionBarRef.get();
            View view = this.mRef.get();
            if (view == null || actionBarImpl == null || actionBarImpl.mNowShowing) {
                return;
            }
            view.setVisibility(8);
            if (view.getId() == R.id.action_bar_container) {
                view.setTranslationY(-view.getHeight());
            }
        }
    }

    @Override // miuix.appcompat.app.ActionBar
    public CollapseTitle getCollapseTitle() {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView != null) {
            return actionBarView.getCollapseTitle();
        }
        return null;
    }

    @Override // miuix.appcompat.app.ActionBar
    public ExpandTitle getExpandTitle() {
        ActionBarView actionBarView = this.mActionView;
        if (actionBarView != null) {
            return actionBarView.getExpandTitle();
        }
        return null;
    }

    int getTopViewHeight() {
        ActionModeView actionModeView;
        if (this.mActionMode != null && (actionModeView = this.mActionModeView) != null) {
            return actionModeView.getViewHeight();
        }
        if (this.mActionView.isCollapsed()) {
            return 0;
        }
        return this.mActionView.getCollapsedHeight();
    }

    public void updateBackgroundViewBlurState(boolean z) {
        if (!z || MiuiBlurUtils.isEffectEnable(getThemedContext())) {
            SearchActionModeView searchActionModeView = this.mSearchActionModeView;
            if (searchActionModeView != null) {
                searchActionModeView.updateBackground(z);
            }
            this.mContainerView.updateBackground(z);
        }
    }

    public ActionBarContainer getActionBarContainer() {
        return this.mContainerView;
    }

    public void setBlur(Boolean bool) {
        ActionBarContainer actionBarContainer;
        if ((getDisplayOptions() & 32768) == 0 || (actionBarContainer = this.mContainerView) == null) {
            return;
        }
        actionBarContainer.setActionBarBlur(bool);
    }

    public void setSplitActionBarBlur(Boolean bool) {
        ActionBarContainer actionBarContainer;
        if ((getDisplayOptions() & 16384) == 0 || (actionBarContainer = this.mSplitView) == null) {
            return;
        }
        actionBarContainer.setSplitActionBarBlur(bool);
    }

    int updateTopOffsetOnNestedPreScroll(View view, int i) {
        if (this.mCoordinateOffsetViewSet.containsKey(view)) {
            Integer coordinateOffsetViewTopOffsetOrDefault = getCoordinateOffsetViewTopOffsetOrDefault(view);
            if (coordinateOffsetViewTopOffsetOrDefault.intValue() > i) {
                this.mCoordinateOffsetViewSet.put(view, Integer.valueOf(i));
                doUpdateTopOffsetForCoordinateView(view, i);
                return coordinateOffsetViewTopOffsetOrDefault.intValue() - i;
            }
        }
        return 0;
    }

    private Integer getCoordinateOffsetViewTopOffsetOrDefault(View view) {
        Integer num = this.mCoordinateOffsetViewSet.get(view);
        return Integer.valueOf(Objects.equals(num, UNINITIALIZED_OFFSET) ? 0 : num.intValue());
    }

    int updateTopOffsetOnNestedScroll(View view, int i) {
        int i2 = 0;
        for (View view2 : this.mCoordinateOffsetViewSet.keySet()) {
            int iIntValue = getCoordinateOffsetViewTopOffsetOrDefault(view2).intValue();
            int i3 = iIntValue - i;
            Rect rect = this.mContentInset;
            int iMin = Math.min(i3, rect == null ? 0 : rect.top);
            if (iIntValue < iMin) {
                this.mCoordinateOffsetViewSet.put(view2, Integer.valueOf(iMin));
                doUpdateTopOffsetForCoordinateView(view2, iMin);
                if (view == view2) {
                    i2 = iIntValue - iMin;
                }
            }
        }
        return i2;
    }

    void updateTopOffsetOnPostScroll(View view, int i) {
        for (View view2 : this.mCoordinateOffsetViewSet.keySet()) {
            if (view == view2) {
                int iIntValue = getCoordinateOffsetViewTopOffsetOrDefault(view2).intValue();
                Rect rect = this.mContentInset;
                int iMin = Math.min(i, rect == null ? 0 : rect.top);
                if (iIntValue != iMin) {
                    this.mCoordinateOffsetViewSet.put(view2, Integer.valueOf(iMin));
                    doUpdateTopOffsetForCoordinateView(view2, iMin);
                }
            }
        }
    }

    int getTopOffsetForCoordinateView(View view) {
        if (this.mCoordinateOffsetViewSet.containsKey(view)) {
            return getCoordinateOffsetViewTopOffsetOrDefault(view).intValue();
        }
        return -1;
    }

    void updateContentInsetForNestedObserver(Rect rect) {
        this.mContentInset = rect;
        int i = rect.top - this.mContentInsetTop;
        this.mContentInsetTop = rect.top;
        Iterator<NestedContentInsetObserver> it = this.mNestedContentInsetObserverSet.iterator();
        while (it.hasNext()) {
            it.next().onContentInsetChanged(rect);
        }
        for (View view : this.mCoordinateOffsetViewSet.keySet()) {
            Integer num = this.mCoordinateOffsetViewSet.get(view);
            if (num != null && i != 0) {
                if (Objects.equals(num, UNINITIALIZED_OFFSET)) {
                    num = 0;
                } else if (num.intValue() == 0) {
                }
                int iMax = Math.max(0, num.intValue() + i);
                this.mCoordinateOffsetViewSet.put(view, Integer.valueOf(iMax));
                doUpdateTopOffsetForCoordinateView(view, iMax);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    void updateCoordinateOffsetView() {
        if (this.mCoordinateOffsetViewSet.size() == 0 && this.mNestedContentInsetObserverSet.size() == 0) {
            this.mContainerView.setActionBarCoordinateListener(null);
            return;
        }
        for (View view : this.mCoordinateOffsetViewSet.keySet()) {
            doUpdateTopOffsetForCoordinateView(view, getCoordinateOffsetViewTopOffsetOrDefault(view).intValue());
        }
        Iterator<NestedContentInsetObserver> it = this.mNestedContentInsetObserverSet.iterator();
        while (it.hasNext()) {
            View view2 = (View) ((NestedContentInsetObserver) it.next());
            if (view2 instanceof NestedCoordinatorObserver) {
                ((NestedCoordinatorObserver) view2).updateCoordinatorHeightGapInfo(this.mCurrentActionBarHeightGap, this.mActionBarHeightTotalGap);
            }
            doUpdateTopOffsetForCoordinateView(view2, 0);
        }
    }

    private void doUpdateTopOffsetForCoordinateView(View view, int i) {
        int top = view.getTop();
        int i2 = this.mCurrentActionBarHeightGap;
        if (top != i2 + i) {
            view.offsetTopAndBottom((Math.max(0, i2) + i) - top);
        }
    }

    protected ActionBarCoordinateListener createActionBarCoordinateListener() {
        return new ActionBarCoordinateListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarImpl$$ExternalSyntheticLambda0
            @Override // miuix.appcompat.internal.app.widget.ActionBarCoordinateListener
            public final void onActionBarResizing(int i, float f, int i2, int i3) {
                this.f$0.m1824xd8b8fa3a(i, f, i2, i3);
            }
        };
    }

    /* JADX INFO: renamed from: lambda$createActionBarCoordinateListener$1$miuix-appcompat-internal-app-widget-ActionBarImpl, reason: not valid java name */
    /* synthetic */ void m1824xd8b8fa3a(int i, float f, int i2, int i3) {
        this.mCurrentActionBarHeightGapOnShow = i2;
        this.mActionBarHeightTotalGapOnShow = i3;
        float height = (this.mContainerView.getHeight() + this.mContainerView.getTranslationY()) / this.mContainerView.getHeight();
        float f2 = this.mTargetTranslationY;
        if (f2 != 0.0f) {
            height = (f2 - this.mContainerView.getTranslationY()) / this.mTargetTranslationY;
        }
        if (this.mContainerView.getHeight() == 0) {
            height = 1.0f;
        }
        this.mCurrentActionBarHeightGap = (int) (this.mCurrentActionBarHeightGapOnShow * height);
        this.mActionBarHeightTotalGap = (int) (this.mActionBarHeightTotalGapOnShow * height);
    }
}
