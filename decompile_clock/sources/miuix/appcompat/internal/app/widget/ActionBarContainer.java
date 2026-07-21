package miuix.appcompat.internal.app.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import miuix.appcompat.R;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.ActionBarTransitionListener;
import miuix.appcompat.internal.view.menu.action.ActionMenuView;
import miuix.appcompat.internal.view.menu.action.ResponsiveActionMenuView;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.RomUtils;
import miuix.core.util.WindowUtils;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.DeviceHelper;
import miuix.theme.token.hypermaterial.Blur;
import miuix.theme.token.hypermaterial.Mask;
import miuix.view.BlurableWidget;
import miuix.view.MiuiBlurUiHelper;

/* JADX INFO: loaded from: classes2.dex */
public class ActionBarContainer extends FrameLayout implements BlurableWidget, ActionBar.FragmentViewPagerChangeListener {
    private static final int BG_EMBEDED_TABS_IDX = 1;
    private static final int BG_LENGTH = 3;
    private static final int BG_NORMAL_IDX = 0;
    private static final int BG_STACKED_IDX = 2;
    private ActionBarContextView mActionBarContextView;
    private int mActionBarHeightGap;
    private int mActionBarHeightTotalGap;
    List<ActionBarTransitionListener> mActionBarTransitionListeners;
    private ActionBarView mActionBarView;
    private ActionMenuView mActionModeMenuView;
    private Drawable mBackground;
    private Drawable[] mBackgroundArray;
    private Drawable mBackgroundBackup;
    private final MiuiBlurUiHelper mBlurHelper;
    protected ActionBarCoordinateListener mCoordinateListener;
    private int mCoordinatedOffsetYInSearchModeAnimation;
    private int mCurBarExpandState;
    private boolean mCurBarResizable;
    private int mCurContextBarExpandState;
    private boolean mCurContextBarResizable;
    private Animator mCurrentShowAnim;
    private boolean mCustomBackground;
    private boolean mCustomViewAutoFitSystemWindow;
    private boolean mDrawBackground;
    private int mHeightMaxMeasureSpec;
    private AnimatorListenerAdapter mHideListener;
    private boolean mInternalApplyBgBlur;
    private boolean mInternalApplySpiltBgBlur;
    private boolean mIsInActionMode;
    private boolean mIsInWideMode;
    private boolean mIsMiuixFloating;
    private boolean mIsSplit;
    private boolean mIsStacked;
    private boolean mIsTransitioning;
    private float mLastActionBarResizingProcess;
    private int mLastToState;
    private MaterialDayNightConfig mMaterial;
    private boolean mNowShowing;
    private boolean mOverlayMode;
    private Rect mPendingInsets;
    private boolean mRequestAnimation;
    private ActionMenuView mResidentActionMenuView;
    private AnimatorListenerAdapter mShowListener;
    private Drawable mSplitBackground;
    private Drawable mSplitBackgroundBackup;
    private Drawable mStackedBackground;
    private View mTabContainer;
    private int mTabContainerPaddingTop;
    private Boolean mUserApplyBgBlur;
    private Boolean mUserApplySplitActionBarBgBlur;
    private Boolean mUserBgViewApplyBlur;

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrollStateChanged(int i) {
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageSelected(int i) {
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        return null;
    }

    public ActionBarContainer(Context context) {
        this(context, null);
    }

    public ActionBarContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mNowShowing = true;
        boolean z = false;
        this.mInternalApplyBgBlur = false;
        this.mInternalApplySpiltBgBlur = false;
        this.mUserApplyBgBlur = null;
        this.mUserApplySplitActionBarBgBlur = null;
        this.mUserBgViewApplyBlur = null;
        this.mResidentActionMenuView = null;
        this.mActionModeMenuView = null;
        this.mCustomBackground = false;
        this.mHeightMaxMeasureSpec = -1;
        this.mLastActionBarResizingProcess = 0.0f;
        this.mLastToState = 0;
        this.mActionBarHeightTotalGap = 0;
        this.mActionBarHeightGap = 0;
        this.mCoordinateListener = null;
        this.mActionBarTransitionListeners = new CopyOnWriteArrayList();
        this.mHideListener = new AnimatorListenerAdapter() { // from class: miuix.appcompat.internal.app.widget.ActionBarContainer.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ActionBarContainer.this.setVisibility(8);
                ActionBarContainer.this.mCurrentShowAnim = null;
            }
        };
        this.mShowListener = new AnimatorListenerAdapter() { // from class: miuix.appcompat.internal.app.widget.ActionBarContainer.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ActionBarContainer.this.mCurrentShowAnim = null;
            }
        };
        setBackground(null);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ActionBar);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ActionBar_android_background);
        this.mBackground = drawable;
        this.mBackgroundArray = new Drawable[]{drawable, typedArrayObtainStyledAttributes.getDrawable(R.styleable.ActionBar_actionBarEmbededTabsBackground), typedArrayObtainStyledAttributes.getDrawable(R.styleable.ActionBar_actionBarStackedBackground)};
        this.mCustomViewAutoFitSystemWindow = typedArrayObtainStyledAttributes.getBoolean(R.styleable.ActionBar_customViewAutoFitSystemWindow, false);
        if (getId() == R.id.split_action_bar) {
            this.mIsSplit = true;
            this.mSplitBackground = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ActionBar_android_backgroundSplit);
        }
        typedArrayObtainStyledAttributes.recycle();
        if (!this.mIsSplit) {
            setPadding(0, 0, 0, 0);
        }
        resizeSplitMaxHeight();
        if (!this.mIsSplit ? !(this.mBackground != null || this.mStackedBackground != null) : this.mSplitBackground == null) {
            z = true;
        }
        setWillNotDraw(z);
        this.mDrawBackground = true;
        if (HyperMaterialUtils.isEnable()) {
            this.mMaterial = MaterialDayNightConfig.create(RomUtils.getHyperOsVersion() > 2 ? Mask.Pured_Regular : Blur.ExtraHeavy);
            this.mBlurHelper = new MiuiBlurUiHelper(context, this, false, false, new MiuiBlurUiHelper.BlurStateCallback() { // from class: miuix.appcompat.internal.app.widget.ActionBarContainer.3
                final boolean isDarkThemeOverlay;

                {
                    this.isDarkThemeOverlay = MiuixUIUtils.isDarkThemeOverlay(ActionBarContainer.this.getContext(), R.color.miuix_default_color_on_surface_light);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public boolean isLightTheme() {
                    Integer colorFromDrawable;
                    if (ActionBarContainer.this.mBackground == null || (colorFromDrawable = MiuixUIUtils.getColorFromDrawable(ActionBarContainer.this.mBackground)) == null) {
                        return !this.isDarkThemeOverlay && AttributeResolver.resolveBoolean(ActionBarContainer.this.getContext(), R.attr.isLightTheme, true);
                    }
                    return MiuixUIUtils.isLightColor(colorFromDrawable.intValue()) && !this.isDarkThemeOverlay;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public Drawable getBackground() {
                    return ActionBarContainer.this.mBackground;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public int getBackgroundColor() {
                    if (this.isDarkThemeOverlay) {
                        return AttributeResolver.resolveColor(ActionBarContainer.this.getContext(), miuix.theme.R.attr.colorSurface, 0);
                    }
                    return 0;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public MaterialConfig.BlurConfig getBlurConfig(boolean z2) {
                    MaterialDayNightConfig materialDayNightConfig = ActionBarContainer.this.mMaterial;
                    if (materialDayNightConfig != null) {
                        return materialDayNightConfig.getBlurConfig(z2);
                    }
                    return null;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurEnableStateChanged(boolean z2) {
                    if (ActionBarContainer.this.mIsSplit) {
                        ActionBarContainer.this.mInternalApplySpiltBgBlur = z2;
                        if (ActionBarContainer.this.mResidentActionMenuView != null) {
                            boolean zBooleanValue = ActionBarContainer.this.mUserApplySplitActionBarBgBlur != null ? ActionBarContainer.this.mUserApplySplitActionBarBgBlur.booleanValue() : ActionBarContainer.this.mInternalApplySpiltBgBlur;
                            if (z2) {
                                ActionBarContainer.this.mResidentActionMenuView.setSupportBlur(true);
                                ActionBarContainer.this.mResidentActionMenuView.setEnableBlur(true);
                            }
                            ActionBarContainer.this.mResidentActionMenuView.applyBlur(zBooleanValue);
                        }
                    }
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurApplyStateChanged(boolean z2) {
                    if (z2) {
                        ActionBarContainer.this.mDrawBackground = false;
                    } else {
                        ActionBarContainer.this.mDrawBackground = true;
                    }
                    if (ActionBarContainer.this.mActionBarView != null) {
                        ActionBarContainer.this.mActionBarView.setApplyBgBlur(z2);
                    }
                    if (ActionBarContainer.this.mActionBarContextView != null) {
                        ActionBarContainer.this.mActionBarContextView.updateBackground(z2);
                    }
                    ActionBarContainer.this.invalidate();
                }
            });
        } else {
            this.mBlurHelper = null;
        }
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mActionBarView = (ActionBarView) findViewById(R.id.action_bar);
        this.mActionBarContextView = (ActionBarContextView) findViewById(R.id.action_context_bar);
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            actionBarView.bindActionBarTransitionListeners(this.mActionBarTransitionListeners);
            this.mCurBarExpandState = this.mActionBarView.getExpandState();
            this.mCurBarResizable = this.mActionBarView.isResizable();
        }
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null) {
            this.mCurContextBarExpandState = actionBarContextView.getExpandState();
            this.mCurContextBarResizable = this.mActionBarContextView.isResizable();
            this.mActionBarContextView.setActionBarView(this.mActionBarView);
        }
    }

    public void setMiuixFloatingOnInit(boolean z) {
        this.mIsMiuixFloating = z;
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null && z) {
            this.mCurBarResizable = actionBarView.isResizable();
            this.mActionBarView.setExpandState(0);
            this.mActionBarView.setResizable(false);
            this.mCurBarExpandState = this.mActionBarView.getExpandState();
        }
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView == null || !z) {
            return;
        }
        this.mCurContextBarResizable = actionBarContextView.isResizable();
        this.mActionBarContextView.setExpandState(0);
        this.mActionBarContextView.setResizable(false);
        this.mCurContextBarExpandState = this.mActionBarContextView.getExpandState();
    }

    public void setIsMiuixFloating(boolean z) {
        this.mIsMiuixFloating = z;
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            if (z) {
                this.mCurBarExpandState = actionBarView.getExpandState();
                this.mCurBarResizable = this.mActionBarView.isResizable();
                this.mActionBarView.setExpandState(0);
                this.mActionBarView.setResizable(false);
            } else {
                actionBarView.setResizable(this.mCurBarResizable);
                this.mActionBarView.setExpandState(this.mCurBarExpandState);
            }
        }
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null) {
            if (z) {
                this.mCurContextBarExpandState = actionBarContextView.getExpandState();
                this.mCurContextBarResizable = this.mActionBarContextView.isResizable();
                this.mActionBarContextView.setExpandState(0);
                this.mActionBarContextView.setResizable(false);
                return;
            }
            actionBarContextView.setResizable(this.mCurContextBarResizable);
            this.mActionBarContextView.setExpandState(this.mCurContextBarExpandState);
        }
    }

    public boolean isMiuixFloating() {
        return this.mIsMiuixFloating;
    }

    public void setOverlayMode(boolean z) {
        this.mOverlayMode = z;
    }

    public void setCoordinatedOffsetYInSearchModeAnimation(int i) {
        this.mCoordinatedOffsetYInSearchModeAnimation = i;
        ActionBarCoordinateListener actionBarCoordinateListener = this.mCoordinateListener;
        if (actionBarCoordinateListener != null) {
            actionBarCoordinateListener.onActionBarResizing(this.mLastToState, this.mLastActionBarResizingProcess, this.mActionBarHeightGap + i, this.mActionBarHeightTotalGap);
        }
    }

    public void setActionBarContextView(ActionBarContextView actionBarContextView) {
        this.mActionBarContextView = actionBarContextView;
        if (actionBarContextView != null) {
            actionBarContextView.setActionBarView(this.mActionBarView);
            this.mCurContextBarExpandState = this.mActionBarContextView.getExpandState();
            this.mCurContextBarResizable = this.mActionBarContextView.isResizable();
        }
    }

    public void setPendingInsets(Rect rect) {
        if (this.mIsSplit) {
            return;
        }
        if (this.mPendingInsets == null) {
            this.mPendingInsets = new Rect();
        }
        if (Objects.equals(this.mPendingInsets, rect)) {
            return;
        }
        this.mPendingInsets.set(rect);
        applyInsetsTopByMargin(this.mActionBarView);
        applyInsetsTopByMargin(this.mActionBarContextView);
    }

    public Rect getPendingInsets() {
        return this.mPendingInsets;
    }

    void setActionBarCoordinateListener(ActionBarCoordinateListener actionBarCoordinateListener) {
        this.mCoordinateListener = actionBarCoordinateListener;
    }

    ActionBarCoordinateListener getActionBarCoordinateListener() {
        return this.mCoordinateListener;
    }

    void addActionBarTransitionListener(ActionBarTransitionListener actionBarTransitionListener) {
        if (actionBarTransitionListener != null) {
            this.mActionBarTransitionListeners.add(actionBarTransitionListener);
        }
    }

    void removeActionBarTransitionListener(ActionBarTransitionListener actionBarTransitionListener) {
        if (actionBarTransitionListener != null) {
            this.mActionBarTransitionListeners.remove(actionBarTransitionListener);
        }
    }

    @Override // miuix.view.BlurableWidget
    public void setSupportBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.setSupportBlur(z);
        }
    }

    @Override // miuix.view.BlurableWidget
    public boolean isSupportBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isSupportBlur();
    }

    @Override // miuix.view.BlurableWidget
    public void setEnableBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.setEnableBlur(z);
    }

    @Override // miuix.view.BlurableWidget
    public boolean isEnableBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isEnableBlur();
    }

    @Override // miuix.view.BlurableWidget
    public void applyBlur(boolean z) {
        if (this.mBlurHelper == null || this.mIsSplit) {
            return;
        }
        if (z && getVisibility() == 0) {
            this.mBlurHelper.applyBlur(true);
        } else {
            this.mBlurHelper.applyBlur(false);
        }
    }

    @Override // miuix.view.BlurableWidget
    public boolean isApplyBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isApplyBlur();
    }

    void setActionBarBlurByNestedScrolled(boolean z) {
        this.mInternalApplyBgBlur = z;
        if (this.mUserApplyBgBlur != null) {
            return;
        }
        applyBlur(z);
    }

    void resetActionBarBlurConfigOnReshow() {
        Boolean bool = this.mUserApplyBgBlur;
        if (bool != null) {
            applyBlur(bool.booleanValue());
        } else {
            applyBlur(this.mInternalApplyBgBlur);
        }
    }

    void resetActionBarBlurConfig() {
        this.mUserApplyBgBlur = null;
        applyBlur(this.mInternalApplyBgBlur);
    }

    void setActionBarBlur(Boolean bool) {
        if (isEnableBlur()) {
            if (bool == null) {
                this.mUserApplyBgBlur = null;
                applyBlur(this.mInternalApplyBgBlur);
                return;
            }
            Boolean bool2 = this.mUserApplyBgBlur;
            if (bool2 == null || bool2.booleanValue() != bool.booleanValue()) {
                this.mUserApplyBgBlur = bool;
                applyBlur(bool.booleanValue());
            }
        }
    }

    void setSplitActionBarBlur(Boolean bool) {
        if (this.mIsSplit) {
            this.mUserApplySplitActionBarBgBlur = bool;
            ActionMenuView actionMenuView = this.mActionModeMenuView;
            if (actionMenuView != null) {
                actionMenuView.applyBlur(bool != null ? bool.booleanValue() : this.mInternalApplySpiltBgBlur);
            }
            ActionMenuView actionMenuView2 = this.mResidentActionMenuView;
            if (actionMenuView2 != null) {
                actionMenuView2.applyBlur(bool != null ? bool.booleanValue() : this.mInternalApplySpiltBgBlur);
            }
        }
    }

    int getCollapsedHeight() {
        int collapsedHeight;
        int i;
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null && actionBarContextView.getVisibility() != 8 && this.mActionBarContextView.getMeasuredHeight() > 0) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mActionBarContextView.getLayoutParams();
            collapsedHeight = this.mActionBarContextView.getCollapsedHeight();
            i = marginLayoutParams.topMargin;
        } else {
            ActionBarView actionBarView = this.mActionBarView;
            if (actionBarView == null) {
                return 0;
            }
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) actionBarView.getLayoutParams();
            collapsedHeight = this.mActionBarView.getCollapsedHeight();
            i = marginLayoutParams2.topMargin;
        }
        return collapsedHeight + i;
    }

    int getSplitCollapsedHeight() {
        if (this.mIsSplit) {
            return Math.max(Math.max(0, getActionMenuViewCollapseHeight(this.mActionModeMenuView)), getActionMenuViewCollapseHeight(this.mResidentActionMenuView));
        }
        return 0;
    }

    private int getActionMenuViewCollapseHeight(ActionMenuView actionMenuView) {
        if (actionMenuView == null || actionMenuView.getVisibility() != 0 || actionMenuView.getAlpha() == 0.0f || actionMenuView.getCollapsedHeight() <= 0) {
            return 0;
        }
        return Math.max(0, actionMenuView.getCollapsedHeight());
    }

    int getExpandedHeight() {
        int expandedHeight;
        int i;
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null && actionBarContextView.getVisibility() != 8 && this.mActionBarContextView.getMeasuredHeight() > 0) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mActionBarContextView.getLayoutParams();
            expandedHeight = this.mActionBarContextView.getExpandedHeight();
            i = marginLayoutParams.topMargin;
        } else {
            ActionBarView actionBarView = this.mActionBarView;
            if (actionBarView == null) {
                return 0;
            }
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) actionBarView.getLayoutParams();
            expandedHeight = this.mActionBarView.getExpandedHeight();
            i = marginLayoutParams2.topMargin;
        }
        return expandedHeight + i;
    }

    int getInsetHeight() {
        if (this.mIsSplit) {
            return Math.max(Math.max(0, getActionMenuViewInsetHeight(this.mActionModeMenuView)), getActionMenuViewInsetHeight(this.mResidentActionMenuView));
        }
        return 0;
    }

    private int getActionMenuViewInsetHeight(ActionMenuView actionMenuView) {
        if (actionMenuView == null || actionMenuView.getVisibility() != 0 || actionMenuView.getAlpha() == 0.0f || actionMenuView.getCollapsedHeight() <= 0) {
            return 0;
        }
        return Math.max(0, (int) (actionMenuView.getCollapsedHeight() - actionMenuView.getTranslationY()));
    }

    public void setPrimaryBackground(Drawable drawable) {
        Drawable drawable2 = this.mBackground;
        Rect rect = null;
        if (drawable2 != null) {
            Rect bounds = drawable2.getBounds();
            this.mBackground.setCallback(null);
            unscheduleDrawable(this.mBackground);
            rect = bounds;
        }
        this.mBackground = drawable;
        boolean z = true;
        if (drawable != null) {
            drawable.setCallback(this);
            if (rect == null) {
                requestLayout();
            } else {
                this.mBackground.setBounds(rect);
            }
            this.mCustomBackground = true;
        } else {
            this.mCustomBackground = false;
        }
        if (!this.mIsSplit ? this.mBackground != null || this.mStackedBackground != null : this.mSplitBackground != null) {
            z = false;
        }
        setWillNotDraw(z);
        invalidate();
    }

    public Drawable getPrimaryBackground() {
        return this.mBackground;
    }

    public void updateBackground(boolean z) {
        this.mUserBgViewApplyBlur = Boolean.valueOf(z);
        updateBackgroundInternal(z);
    }

    void updateBackgroundInternal(boolean z) {
        if (z) {
            this.mDrawBackground = false;
        } else {
            this.mDrawBackground = true;
        }
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null) {
            actionBarContextView.updateBackground(z);
        }
        ActionBarView actionBarView = this.mActionBarView;
        if (actionBarView != null) {
            actionBarView.setApplyBgBlur(z);
        }
        invalidate();
    }

    public void setStackedBackground(Drawable drawable) {
        Drawable drawable2 = this.mStackedBackground;
        if (drawable2 != null) {
            drawable2.setCallback(null);
            unscheduleDrawable(this.mStackedBackground);
        }
        this.mStackedBackground = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        boolean z = true;
        if (!this.mIsSplit ? this.mBackground != null || this.mStackedBackground != null : this.mSplitBackground != null) {
            z = false;
        }
        setWillNotDraw(z);
        View view = this.mTabContainer;
        if (view != null) {
            view.setBackground(this.mStackedBackground);
        }
    }

    public void setSplitBackground(Drawable drawable) {
        Drawable drawable2 = this.mSplitBackground;
        if (drawable2 != null) {
            drawable2.setCallback(null);
            unscheduleDrawable(this.mSplitBackground);
        }
        this.mSplitBackground = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        boolean z = true;
        if (!this.mIsSplit ? this.mBackground != null || this.mStackedBackground != null : this.mSplitBackground != null) {
            z = false;
        }
        setWillNotDraw(z);
        invalidate();
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        boolean z = i == 0;
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setVisible(z, false);
        }
        Drawable drawable2 = this.mStackedBackground;
        if (drawable2 != null) {
            drawable2.setVisible(z, false);
        }
        Drawable drawable3 = this.mSplitBackground;
        if (drawable3 != null) {
            drawable3.setVisible(z, false);
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return (drawable == this.mBackground && !this.mIsSplit) || (drawable == this.mStackedBackground && this.mIsStacked) || ((drawable == this.mSplitBackground && this.mIsSplit) || super.verifyDrawable(drawable));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mBackground;
        if (drawable != null && drawable.isStateful()) {
            this.mBackground.setState(getDrawableState());
        }
        Drawable drawable2 = this.mStackedBackground;
        if (drawable2 != null && drawable2.isStateful()) {
            this.mStackedBackground.setState(getDrawableState());
        }
        Drawable drawable3 = this.mSplitBackground;
        if (drawable3 == null || !drawable3.isStateful()) {
            return;
        }
        this.mSplitBackground.setState(getDrawableState());
    }

    public void setTransitioning(boolean z) {
        this.mIsTransitioning = z;
        setDescendantFocusability(z ? 393216 : 262144);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mIsTransitioning || super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return !this.mIsSplit && super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        return !this.mIsSplit;
    }

    public View getTabContainer() {
        return this.mTabContainer;
    }

    public void setTabContainer(ScrollingTabContainerView scrollingTabContainerView) {
        View view = this.mTabContainer;
        if (view != null) {
            removeView(view);
        }
        if (scrollingTabContainerView != null) {
            addView(scrollingTabContainerView);
            ViewGroup.LayoutParams layoutParams = scrollingTabContainerView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -2;
            scrollingTabContainerView.setAllowCollapse(false);
            this.mTabContainerPaddingTop = scrollingTabContainerView.getPaddingTop();
        } else {
            View view2 = this.mTabContainer;
            if (view2 != null) {
                view2.setBackground(null);
            }
        }
        this.mTabContainer = scrollingTabContainerView;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Drawable drawable;
        if (getWidth() == 0 || getHeight() == 0 || this.mIsSplit || (drawable = this.mBackground) == null || !this.mDrawBackground) {
            return;
        }
        drawable.draw(canvas);
    }

    private void applyInsetsTopByMargin(View view) {
        if (view == null || view.getVisibility() != 0) {
            return;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        Rect rect = this.mPendingInsets;
        marginLayoutParams.topMargin = rect != null ? rect.top : 0;
        view.setLayoutParams(marginLayoutParams);
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int measuredHeight;
        int measuredHeight2;
        Rect rect;
        if (this.mIsSplit) {
            onMeasureSplit(i, i2);
            return;
        }
        View view = this.mTabContainer;
        if (view != null) {
            view.setPadding(view.getPaddingLeft(), this.mTabContainerPaddingTop, this.mTabContainer.getPaddingRight(), this.mTabContainer.getPaddingBottom());
        }
        applyInsetsTopByMargin(this.mActionBarView);
        applyInsetsTopByMargin(this.mActionBarContextView);
        super.onMeasure(i, i2);
        ActionBarView actionBarView = this.mActionBarView;
        boolean z = (actionBarView == null || actionBarView.getVisibility() == 8 || this.mActionBarView.getMeasuredHeight() <= 0) ? false : true;
        if (z) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mActionBarView.getLayoutParams();
            measuredHeight = this.mActionBarView.isCollapsed() ? layoutParams.topMargin : layoutParams.bottomMargin + this.mActionBarView.getMeasuredHeight() + layoutParams.topMargin;
        } else {
            measuredHeight = 0;
        }
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if ((actionBarContextView == null || actionBarContextView.getVisibility() == 8 || this.mActionBarContextView.getMeasuredHeight() <= 0) ? false : true) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mActionBarContextView.getLayoutParams();
            measuredHeight2 = this.mActionBarContextView.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin;
        } else {
            measuredHeight2 = 0;
        }
        if (measuredHeight > 0 || measuredHeight2 > 0) {
            setMeasuredDimension(getMeasuredWidth(), Math.max(measuredHeight, measuredHeight2));
        }
        View view2 = this.mTabContainer;
        if (view2 != null && view2.getVisibility() != 8 && View.MeasureSpec.getMode(i2) == Integer.MIN_VALUE) {
            setMeasuredDimension(getMeasuredWidth(), Math.min(measuredHeight + this.mTabContainer.getMeasuredHeight(), View.MeasureSpec.getSize(i2)) + ((z || (rect = this.mPendingInsets) == null) ? 0 : rect.top));
        }
        int i3 = 0;
        for (int i4 = 0; i4 < getChildCount(); i4++) {
            View childAt = getChildAt(i4);
            if (childAt.getVisibility() == 0 && childAt.getMeasuredHeight() > 0 && childAt.getMeasuredWidth() > 0) {
                i3++;
            }
        }
        if (i3 == 0) {
            setMeasuredDimension(0, 0);
        }
    }

    private void onMeasureSplit(int i, int i2) {
        if (View.MeasureSpec.getMode(i) == Integer.MIN_VALUE) {
            i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), BasicMeasure.EXACTLY);
        }
        int i3 = this.mHeightMaxMeasureSpec;
        if (i3 != -1) {
            i2 = i3;
        }
        super.onMeasure(i, i2);
        int childCount = getChildCount();
        int iMax = 0;
        for (int i4 = 0; i4 < childCount; i4++) {
            iMax = Math.max(iMax, getChildAt(i4).getMeasuredHeight());
        }
        if (iMax == 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        ActionMenuView actionMenuView = this.mResidentActionMenuView;
        if (actionMenuView == null || !actionMenuView.hasOnlyCustomView()) {
            return;
        }
        ActionMenuView actionMenuView2 = this.mResidentActionMenuView;
        if (!(actionMenuView2 instanceof ResponsiveActionMenuView) || ((ResponsiveActionMenuView) actionMenuView2).isSuspend()) {
            return;
        }
        setMeasuredDimension(getMeasuredWidth(), iMax);
    }

    /* JADX WARN: Code duplicated, block: B:32:0x00ac  */
    /* JADX WARN: Code duplicated, block: B:49:0x0104  */
    /* JADX WARN: Code duplicated, block: B:50:0x0106  */
    /* JADX WARN: Code duplicated, block: B:53:0x011b  */
    /* JADX WARN: Code duplicated, block: B:57:0x0124  */
    /* JADX WARN: Code duplicated, block: B:65:0x013d  */
    /* JADX WARN: Code duplicated, block: B:69:0x014d  */
    /* JADX WARN: Code duplicated, block: B:74:0x016b  */
    /* JADX WARN: Code duplicated, block: B:77:0x015f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:79:0x0147 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:81:? A[RETURN, SYNTHETIC] */
    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        boolean z2;
        int i5;
        int expandedHeight;
        int collapsedHeight;
        int iMax;
        float f;
        float fMin;
        float f2;
        int i6;
        boolean zUpdateExpandStateOnLayout;
        ActionBarCoordinateListener actionBarCoordinateListener;
        super.onLayout(z, i, i2, i3, i4);
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = (int) (getMeasuredWidth() / getContext().getResources().getDisplayMetrics().density);
        View view = this.mTabContainer;
        if (view != null && view.getVisibility() != 8) {
            int measuredHeight2 = this.mTabContainer.getMeasuredHeight();
            ActionBarView actionBarView = this.mActionBarView;
            if (actionBarView != null && actionBarView.getVisibility() == 0 && this.mActionBarView.getMeasuredHeight() > 0) {
                View view2 = this.mTabContainer;
                view2.setPadding(view2.getPaddingLeft(), this.mTabContainerPaddingTop, this.mTabContainer.getPaddingRight(), this.mTabContainer.getPaddingBottom());
            } else {
                Rect rect = this.mPendingInsets;
                measuredHeight2 += rect != null ? rect.top : 0;
                View view3 = this.mTabContainer;
                int paddingLeft = view3.getPaddingLeft();
                Rect rect2 = this.mPendingInsets;
                view3.setPadding(paddingLeft, rect2 != null ? rect2.top + this.mTabContainerPaddingTop : this.mTabContainerPaddingTop, this.mTabContainer.getPaddingRight(), this.mTabContainer.getPaddingBottom());
            }
            this.mTabContainer.layout(i, measuredHeight - measuredHeight2, i3, measuredHeight);
        }
        if (this.mIsSplit) {
            Drawable drawable = this.mSplitBackground;
            if (drawable != null) {
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                z2 = true;
            } else {
                z2 = false;
            }
        } else {
            selectDrawable();
            Drawable drawable2 = this.mBackground;
            if (drawable2 != null) {
                drawable2.setBounds(0, 0, i3 - i, measuredHeight);
                z2 = true;
            } else {
                z2 = false;
            }
        }
        this.mIsInWideMode = measuredWidth > 640;
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null && this.mIsInActionMode) {
            this.mActionBarHeightGap = actionBarContextView.getMeasuredHeight() - this.mActionBarContextView.getCollapsedHeight();
            expandedHeight = this.mActionBarContextView.getExpandedHeight();
            collapsedHeight = this.mActionBarContextView.getCollapsedHeight();
        } else {
            ActionBarView actionBarView2 = this.mActionBarView;
            if (actionBarView2 != null) {
                this.mActionBarHeightGap = actionBarView2.getMeasuredHeight() - this.mActionBarView.getCollapsedHeight();
                expandedHeight = this.mActionBarView.getExpandedHeight();
                collapsedHeight = this.mActionBarView.getCollapsedHeight();
            } else {
                i5 = 0;
            }
            iMax = Math.max(0, this.mActionBarHeightGap);
            this.mActionBarHeightGap = iMax;
            if (i5 == 0) {
                f = 1.0f;
            } else {
                f = ((i5 - iMax) * 1.0f) / i5;
            }
            this.mActionBarHeightTotalGap = i5;
            fMin = Math.min(1.0f, f);
            if (getTranslationY() < 0.0f) {
                fMin = 0.0f;
            }
            f2 = this.mLastActionBarResizingProcess - fMin;
            i6 = f2 <= 0.0f ? 0 : 1;
            this.mLastToState = i6;
            if (this.mOverlayMode && (actionBarCoordinateListener = this.mCoordinateListener) != null) {
                actionBarCoordinateListener.onActionBarResizing(i6, fMin, this.mActionBarHeightGap + this.mCoordinatedOffsetYInSearchModeAnimation, this.mActionBarHeightTotalGap);
            }
            ActionBarView actionBarView3 = this.mActionBarView;
            zUpdateExpandStateOnLayout = actionBarView3 != null ? actionBarView3.updateExpandStateOnLayout() : false;
            for (ActionBarTransitionListener actionBarTransitionListener : this.mActionBarTransitionListeners) {
                actionBarTransitionListener.onActionBarMove(f2, fMin);
                actionBarTransitionListener.onActionBarResizing(this.mLastToState, fMin, this.mActionBarHeightGap);
                if (zUpdateExpandStateOnLayout) {
                    actionBarTransitionListener.onExpandStateChanged(this.mActionBarView.mExpandStateOnLayout);
                }
            }
            this.mLastActionBarResizingProcess = fMin;
            if (z2) {
                invalidate();
            }
        }
        i5 = expandedHeight - collapsedHeight;
        iMax = Math.max(0, this.mActionBarHeightGap);
        this.mActionBarHeightGap = iMax;
        if (i5 == 0) {
            f = 1.0f;
        } else {
            f = ((i5 - iMax) * 1.0f) / i5;
        }
        this.mActionBarHeightTotalGap = i5;
        fMin = Math.min(1.0f, f);
        if (getTranslationY() < 0.0f) {
            fMin = 0.0f;
        }
        f2 = this.mLastActionBarResizingProcess - fMin;
        if (f2 <= 0.0f) {
        }
        this.mLastToState = i6;
        if (this.mOverlayMode) {
            actionBarCoordinateListener.onActionBarResizing(i6, fMin, this.mActionBarHeightGap + this.mCoordinatedOffsetYInSearchModeAnimation, this.mActionBarHeightTotalGap);
        }
        ActionBarView actionBarView4 = this.mActionBarView;
        if (actionBarView4 != null) {
        }
        while (r10.hasNext()) {
            actionBarTransitionListener.onActionBarMove(f2, fMin);
            actionBarTransitionListener.onActionBarResizing(this.mLastToState, fMin, this.mActionBarHeightGap);
            if (zUpdateExpandStateOnLayout) {
                actionBarTransitionListener.onExpandStateChanged(this.mActionBarView.mExpandStateOnLayout);
            }
        }
        this.mLastActionBarResizingProcess = fMin;
        if (z2) {
            invalidate();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setActionBarCoordinateListener(null);
        this.mActionBarTransitionListeners.clear();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        resizeSplitMaxHeight();
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.onConfigChanged();
            if (this.mBlurHelper.isEnableBlur() || this.mUserBgViewApplyBlur != null) {
                return;
            }
            updateBackgroundInternal(false);
        }
    }

    private void resizeSplitMaxHeight() {
        TypedValue typedValueResolveTypedValue;
        if (this.mIsSplit && (typedValueResolveTypedValue = AttributeResolver.resolveTypedValue(getContext(), R.attr.actionBarSplitMaxPercentageHeight)) != null && typedValueResolveTypedValue.type == 6) {
            float windowHeight = WindowUtils.getWindowHeight(getContext());
            this.mHeightMaxMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) typedValueResolveTypedValue.getFraction(windowHeight, windowHeight), Integer.MIN_VALUE);
        }
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        super.setAlpha(f);
    }

    public void startActionMode() {
        this.mIsInActionMode = true;
    }

    public void finishActionMode() {
        this.mIsInActionMode = false;
    }

    public void onWindowShow() {
        if (this.mActionBarView.getMenuView() != null) {
            this.mActionBarView.getMenuView().startLayoutAnimation();
        }
    }

    public void onWindowHide() {
        if (this.mActionBarView.getMenuView() != null) {
            this.mActionBarView.getMenuView().startLayoutAnimation();
        }
    }

    public void hide(boolean z) {
        if (this.mNowShowing) {
            this.mNowShowing = false;
            Animator animator = this.mCurrentShowAnim;
            if (animator != null) {
                animator.cancel();
            }
            if (z && this.mIsSplit) {
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, "TranslationY", 0.0f, getHeight());
                this.mCurrentShowAnim = objectAnimatorOfFloat;
                objectAnimatorOfFloat.setDuration(DeviceHelper.isFeatureWholeAnim() ? getContext().getResources().getInteger(android.R.integer.config_shortAnimTime) : 0L);
                this.mCurrentShowAnim.addListener(this.mHideListener);
                this.mCurrentShowAnim.start();
                return;
            }
            setVisibility(8);
        }
    }

    public void show(boolean z) {
        if (this.mNowShowing) {
            return;
        }
        this.mNowShowing = true;
        Animator animator = this.mCurrentShowAnim;
        if (animator != null) {
            animator.cancel();
        }
        setVisibility(0);
        if (z) {
            if (this.mIsSplit) {
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, "TranslationY", getHeight(), 0.0f);
                this.mCurrentShowAnim = objectAnimatorOfFloat;
                objectAnimatorOfFloat.setDuration(DeviceHelper.isFeatureWholeAnim() ? getContext().getResources().getInteger(android.R.integer.config_shortAnimTime) : 0L);
                this.mCurrentShowAnim.addListener(this.mShowListener);
                this.mCurrentShowAnim.start();
                ActionMenuView actionMenuView = this.mResidentActionMenuView;
                if (actionMenuView != null) {
                    actionMenuView.startLayoutAnimation();
                    return;
                }
                return;
            }
            return;
        }
        setTranslationY(0.0f);
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
        ActionMenuView actionMenuView;
        if (!this.mIsSplit || (actionMenuView = this.mResidentActionMenuView) == null) {
            return;
        }
        actionMenuView.onPageScrolled(i, f, z, z2);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mRequestAnimation) {
            post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.ActionBarContainer.4
                @Override // java.lang.Runnable
                public void run() {
                    ActionBarContainer.this.show(true);
                }
            });
            this.mRequestAnimation = false;
        }
    }

    private void selectDrawable() {
        ActionBarView actionBarView;
        Drawable[] drawableArr;
        char c;
        if (this.mCustomBackground || this.mIsSplit || (actionBarView = this.mActionBarView) == null || this.mBackground == null || (drawableArr = this.mBackgroundArray) == null || drawableArr.length < 3) {
            return;
        }
        if (actionBarView.isTightTitleWithEmbeddedTabs()) {
            int displayOptions = this.mActionBarView.getDisplayOptions();
            c = ((displayOptions & 2) == 0 && (displayOptions & 4) == 0 && (displayOptions & 16) == 0) ? (char) 1 : (char) 2;
        } else {
            c = 0;
        }
        Drawable drawable = this.mBackgroundArray[c];
        if (drawable != null) {
            this.mBackground = drawable;
        }
    }

    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr, int[] iArr2) {
        int i6 = iArr[1];
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null && actionBarContextView.getVisibility() == 0) {
            this.mActionBarContextView.onNestedScroll(view, i, i2, i3, i4, i5, iArr, iArr2);
        } else if (!this.mIsSplit && getVisibility() != 8) {
            this.mActionBarView.onNestedScroll(view, i, i2, i3, i4, i5, iArr, iArr2);
        }
        int i7 = iArr[1] - i6;
        if (!this.mOverlayMode || i4 >= 0 || i7 > 0) {
            return;
        }
        setActionBarBlurByNestedScrolled(false);
        if (this.mIsSplit || getVisibility() != 8) {
            return;
        }
        this.mActionBarView.setExpandState(1);
        ActionBarCoordinateListener actionBarCoordinateListener = this.mCoordinateListener;
        if (actionBarCoordinateListener != null) {
            int i8 = this.mActionBarHeightTotalGap;
            actionBarCoordinateListener.onActionBarResizing(1, 0.0f, i8, i8);
        }
    }

    public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null && actionBarContextView.getVisibility() == 0) {
            return this.mActionBarContextView.onStartNestedScroll(view, view2, i, i2);
        }
        return this.mActionBarView.onStartNestedScroll(view, view2, i, i2);
    }

    public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null && actionBarContextView.getVisibility() == 0) {
            this.mActionBarContextView.onNestedScrollAccepted(view, view2, i, i2);
        } else {
            if (this.mIsSplit || getVisibility() == 8) {
                return;
            }
            this.mActionBarView.onNestedScrollAccepted(view, view2, i, i2);
        }
    }

    public void onStopNestedScroll(View view, int i) {
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null && actionBarContextView.getVisibility() == 0) {
            this.mActionBarContextView.onStopNestedScroll(view, i);
        } else {
            if (this.mIsSplit || getVisibility() == 8) {
                return;
            }
            this.mActionBarView.onStopNestedScroll(view, i);
        }
    }

    public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3, int[] iArr2) {
        ActionBarContextView actionBarContextView = this.mActionBarContextView;
        if (actionBarContextView != null && actionBarContextView.getVisibility() == 0) {
            this.mActionBarContextView.onNestedPreScroll(view, i, i2, iArr, i3, iArr2);
        } else if (!this.mIsSplit && getVisibility() != 8) {
            this.mActionBarView.onNestedPreScroll(view, i, i2, iArr, i3, iArr2);
        }
        if (!this.mOverlayMode || i2 <= 0 || i2 - iArr[1] <= 0) {
            return;
        }
        if (!this.mIsSplit && getVisibility() == 8) {
            setActionBarBlurByNestedScrolled(true);
            this.mActionBarView.setExpandState(0);
            ActionBarCoordinateListener actionBarCoordinateListener = this.mCoordinateListener;
            if (actionBarCoordinateListener != null) {
                actionBarCoordinateListener.onActionBarResizing(0, 1.0f, 0, this.mActionBarHeightTotalGap);
            }
        }
        if (isLayoutRequested()) {
            return;
        }
        setActionBarBlurByNestedScrolled(true);
    }

    void onResidentActionMenuViewAdded(ActionMenuView actionMenuView) {
        this.mResidentActionMenuView = actionMenuView;
        if (actionMenuView == null || !isSupportBlur()) {
            return;
        }
        actionMenuView.setSupportBlur(isSupportBlur());
        actionMenuView.setEnableBlur(isEnableBlur());
        Boolean bool = this.mUserApplySplitActionBarBgBlur;
        actionMenuView.applyBlur((bool != null ? bool.booleanValue() : isEnableBlur()) && getMeasuredWidth() > 0 && getMeasuredHeight() > 0);
    }

    void onResidentActionMenuViewRemoved(ActionMenuView actionMenuView) {
        if (this.mResidentActionMenuView == actionMenuView) {
            this.mResidentActionMenuView = null;
        }
    }

    void onActionModeMenuViewAdded(ActionMenuView actionMenuView) {
        this.mActionModeMenuView = actionMenuView;
        if (actionMenuView == null || !isSupportBlur()) {
            return;
        }
        Boolean bool = this.mUserApplySplitActionBarBgBlur;
        actionMenuView.applyBlur((bool != null ? bool.booleanValue() : isEnableBlur()) && actionMenuView.getMeasuredWidth() > 0 && actionMenuView.getMeasuredHeight() > 0);
    }

    void onActionModeMenuViewRemoved(ActionMenuView actionMenuView) {
        if (this.mActionModeMenuView == actionMenuView) {
            this.mActionModeMenuView = null;
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        int i;
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        Boolean bool = this.mUserApplyBgBlur;
        int i2 = 1;
        if (bool == null) {
            i = -1;
        } else {
            i = bool.booleanValue() ? 1 : 0;
        }
        savedState.userActionBarApplyBlur = i;
        Boolean bool2 = this.mUserApplySplitActionBarBgBlur;
        if (bool2 == null) {
            i2 = -1;
        } else if (!bool2.booleanValue()) {
            i2 = 0;
        }
        savedState.userSplitActionBarApplyBlur = i2;
        savedState.actionBarSupportBlur = isSupportBlur();
        savedState.actionBarEnableBlur = isEnableBlur();
        savedState.actionBarApplyBlur = isApplyBlur();
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.userActionBarApplyBlur == -1) {
            this.mUserApplyBgBlur = null;
        } else if (savedState.userActionBarApplyBlur == 0) {
            this.mUserApplyBgBlur = false;
        } else if (savedState.userActionBarApplyBlur == 1) {
            this.mUserApplyBgBlur = true;
        }
        if (savedState.userSplitActionBarApplyBlur == -1) {
            this.mUserApplySplitActionBarBgBlur = null;
        } else if (savedState.userSplitActionBarApplyBlur == 0) {
            this.mUserApplySplitActionBarBgBlur = false;
        } else if (savedState.userSplitActionBarApplyBlur == 1) {
            this.mUserApplySplitActionBarBgBlur = true;
        }
        if (savedState.actionBarSupportBlur) {
            setSupportBlur(true);
        }
        if (savedState.actionBarEnableBlur) {
            setEnableBlur(true);
        }
        if (savedState.actionBarApplyBlur && isEnableBlur()) {
            applyBlur(true);
        }
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.ClassLoaderCreator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: miuix.appcompat.internal.app.widget.ActionBarContainer.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }

            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }
        };
        boolean actionBarApplyBlur;
        boolean actionBarEnableBlur;
        boolean actionBarSupportBlur;
        int userActionBarApplyBlur;
        int userSplitActionBarApplyBlur;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        SavedState(Parcel parcel) {
            super(parcel);
            this.userActionBarApplyBlur = parcel.readInt();
            this.userSplitActionBarApplyBlur = parcel.readInt();
            this.actionBarSupportBlur = parcel.readInt() != 0;
            this.actionBarEnableBlur = parcel.readInt() != 0;
            this.actionBarApplyBlur = parcel.readInt() != 0;
        }

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.userActionBarApplyBlur = parcel.readInt();
            this.userSplitActionBarApplyBlur = parcel.readInt();
            this.actionBarSupportBlur = parcel.readInt() != 0;
            this.actionBarEnableBlur = parcel.readInt() != 0;
            this.actionBarApplyBlur = parcel.readInt() != 0;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.userActionBarApplyBlur);
            parcel.writeInt(this.userSplitActionBarApplyBlur);
            parcel.writeInt(this.actionBarSupportBlur ? 1 : 0);
            parcel.writeInt(this.actionBarEnableBlur ? 1 : 0);
            parcel.writeInt(this.actionBarApplyBlur ? 1 : 0);
        }
    }
}
