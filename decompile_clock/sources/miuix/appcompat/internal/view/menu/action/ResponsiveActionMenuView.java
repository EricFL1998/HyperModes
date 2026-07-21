package miuix.appcompat.internal.view.menu.action;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.appcompat.R;
import miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper;
import miuix.appcompat.internal.view.OutDropShadowView;
import miuix.core.util.ContextUtils;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;
import miuix.core.util.MiShadowUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.RomUtils;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.smooth.SmoothCornerHelper;
import miuix.theme.token.hypermaterial.Blur;
import miuix.theme.token.hypermaterial.Mask;
import miuix.view.MiuiBlurUiHelper;

/* JADX INFO: loaded from: classes2.dex */
public class ResponsiveActionMenuView extends ActionMenuView {
    private static final int ITEM_NORMAL_PADDING_TOP_DP = 8;
    private static final int ITEM_SMALL_PADDING_TOP_DP = 5;
    private static final int MENU_ITEM_GAP_DP = 11;
    private static final int MENU_ITEM_GAP_DP_IN_LARGER_FONT = 16;
    private static final int SUSPEND_ITEM_CENTER_EXTRA_UP_DP = 2;
    private static final int SUSPEND_MENU_MIN_MARGIN_DP = 16;
    private static final int SUSPEND_MENU_MIN_WIDTH_DP = 196;
    private static final String TARGET = "target";
    private AnimConfig mAnimConfig;
    private boolean mApplyBlur;
    private AttributeSet mAttrs;
    private Drawable mBackgroundInBlur;
    private final MiuiBlurUiHelper mBlurUiHelper;
    private Drawable mBottomMenuBackground;
    private int mBottomMenuItemHeight;
    private final Context mContext;
    private View mCustomView;
    private Rect mCustomViewClipBounds;
    private int mDensityDpi;
    private boolean mHasOnlyCustomView;
    private boolean mIsCustomViewHidden;
    private boolean mIsEmpty;
    boolean mIsFloatingWindow;
    private boolean mIsHidden;
    private int mItemNormalPaddingTop;
    private int mItemSmallPaddingTop;
    private boolean mLargeFontAdaptionEnabled;
    private MaterialDayNightConfig mMaterial;
    private int mMenuItemGap;
    private int mMenuItemHeight;
    private int mMenuItemWidth;
    private OutDropShadowView mMenuOutShadowView;
    private int mOffSet;
    private boolean[] mOriginViewParentClipState;
    private View.OnLayoutChangeListener mParentLayoutChangeListener;
    private boolean mSuspendEnabled;
    private int mSuspendItemCenterExtraUp;
    private Drawable mSuspendMenuBackground;
    private int mSuspendMenuBackgroundRadius;
    private int mSuspendMenuMiShadowColor;
    private float mSuspendMenuMiShadowRadius;
    private float mSuspendMenuMiShadowRadiusOffsetX;
    private float mSuspendMenuMiShadowRadiusOffsetY;
    private int mSuspendMenuMinMargin;
    private int mSuspendMenuMinWidth;
    private ViewOutlineProvider mViewOutlineInSuspend;

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
    }

    public ResponsiveActionMenuView(Context context) {
        this(context, null);
    }

    public ResponsiveActionMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSuspendEnabled = false;
        this.mHasOnlyCustomView = false;
        this.mIsEmpty = false;
        this.mMenuOutShadowView = null;
        this.mParentLayoutChangeListener = null;
        this.mOffSet = 0;
        this.mIsHidden = false;
        this.mIsCustomViewHidden = false;
        this.mApplyBlur = false;
        this.mLargeFontAdaptionEnabled = false;
        this.mViewOutlineInSuspend = new ViewOutlineProvider() { // from class: miuix.appcompat.internal.view.menu.action.ResponsiveActionMenuView.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), ResponsiveActionMenuView.this.mSuspendMenuBackgroundRadius);
            }
        };
        this.mAnimConfig = new AnimConfig().setEase(-2, 0.9f, 0.25f).addListeners(new TransitionListener() { // from class: miuix.appcompat.internal.view.menu.action.ResponsiveActionMenuView.2
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                ResponsiveActionMenuView.this.mOffSet = UpdateInfo.findByName(collection, "target").getIntValue();
                ResponsiveActionMenuView.this.requestLayout();
            }
        });
        boolean z = AttributeResolver.resolveBoolean(context, R.attr.largeFontAdaptationEnabled, true) && MiuixUIUtils.getFontLevel(context) == 2;
        this.mLargeFontAdaptionEnabled = z;
        this.mMenuItemGap = z ? MiuixUIUtils.dp2px(context, 16.0f) : MiuixUIUtils.dp2px(context, 11.0f);
        Resources resources = context.getResources();
        this.mBottomMenuItemHeight = this.mLargeFontAdaptionEnabled ? resources.getDimensionPixelSize(R.dimen.miuix_appcompat_bottom_menu_height_in_large_font) : resources.getDimensionPixelSize(R.dimen.miuix_appcompat_bottom_menu_height);
        this.mSuspendMenuMinMargin = MiuixUIUtils.dp2px(context, 16.0f);
        this.mSuspendMenuMinWidth = MiuixUIUtils.dp2px(context, 196.0f);
        this.mItemNormalPaddingTop = MiuixUIUtils.dp2px(context, 8.0f);
        this.mItemSmallPaddingTop = MiuixUIUtils.dp2px(context, 5.0f);
        this.mSuspendItemCenterExtraUp = MiuixUIUtils.dp2px(context, 2.0f);
        this.mSuspendMenuBackgroundRadius = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_suspend_menu_bg_radius);
        this.mSuspendMenuMiShadowColor = context.getResources().getColor(R.color.miuix_appcompat_suspend_menu_mi_shadow);
        this.mSuspendMenuMiShadowRadius = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_suspend_menu_mi_shadow_radius);
        this.mSuspendMenuMiShadowRadiusOffsetX = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_suspend_menu_mi_shadow_radius_offset_x);
        this.mSuspendMenuMiShadowRadiusOffsetY = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_suspend_menu_mi_shadow_radius_offset_y);
        this.mDensityDpi = context.getResources().getDisplayMetrics().densityDpi;
        this.mContext = context;
        this.mAttrs = attributeSet;
        setClickable(true);
        refreshMenuBackgroundDrawables(attributeSet);
        setClipToPadding(false);
        setWillNotDraw(false);
        SmoothCornerHelper.setViewSmoothCornerEnable(this, true);
        if (HyperMaterialUtils.isEnable()) {
            this.mMaterial = MaterialDayNightConfig.create(RomUtils.getHyperOsVersion() > 2 ? Mask.Pured_Regular : Blur.ExtraHeavy);
            this.mBlurUiHelper = new MiuiBlurUiHelper(context, this, false, false, new MiuiBlurUiHelper.BlurStateCallback() { // from class: miuix.appcompat.internal.view.menu.action.ResponsiveActionMenuView.3
                final boolean isDarkThemeOverlay;

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurEnableStateChanged(boolean z2) {
                }

                {
                    this.isDarkThemeOverlay = MiuixUIUtils.isDarkThemeOverlay(ResponsiveActionMenuView.this.getContext(), R.color.miuix_default_color_on_surface_light);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public boolean isLightTheme() {
                    Integer colorFromDrawable;
                    Drawable drawable = ResponsiveActionMenuView.this.mSuspendEnabled ? ResponsiveActionMenuView.this.mSuspendMenuBackground : ResponsiveActionMenuView.this.mBottomMenuBackground;
                    if (drawable == null || (colorFromDrawable = MiuixUIUtils.getColorFromDrawable(drawable)) == null) {
                        return !this.isDarkThemeOverlay && AttributeResolver.resolveBoolean(ResponsiveActionMenuView.this.getContext(), R.attr.isLightTheme, true);
                    }
                    return MiuixUIUtils.isLightColor(colorFromDrawable.intValue()) && !this.isDarkThemeOverlay;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public Drawable getBackground() {
                    return ResponsiveActionMenuView.this.mSuspendEnabled ? ResponsiveActionMenuView.this.mSuspendMenuBackground : ResponsiveActionMenuView.this.mBottomMenuBackground;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public int getBackgroundColor() {
                    if (this.isDarkThemeOverlay) {
                        return AttributeResolver.resolveColor(ResponsiveActionMenuView.this.getContext(), miuix.theme.R.attr.colorSurface, 0);
                    }
                    return 0;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public MaterialConfig.BlurConfig getBlurConfig(boolean z2) {
                    MaterialDayNightConfig materialDayNightConfig = ResponsiveActionMenuView.this.mMaterial;
                    if (materialDayNightConfig != null) {
                        return materialDayNightConfig.getBlurConfig(z2);
                    }
                    return null;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurApplyStateChanged(boolean z2) {
                    ResponsiveActionMenuView.this.mApplyBlur = z2;
                    ResponsiveActionMenuView.this.updateBackground();
                }
            });
        } else {
            this.mBlurUiHelper = null;
        }
        updateBackground();
    }

    private void refreshMenuBackgroundDrawables(AttributeSet attributeSet) {
        Context context = this.mContext;
        if (context == null) {
            return;
        }
        TypedArray typedArrayObtainStyledAttributes = null;
        try {
            this.mIsFloatingWindow = BaseFloatingActivityHelper.isFloatingWindow(ContextUtils.getActivity(context));
            typedArrayObtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, R.styleable.ResponsiveActionMenuView, R.attr.responsiveActionMenuViewStyle, 0);
            this.mBottomMenuBackground = typedArrayObtainStyledAttributes.getDrawable(this.mIsFloatingWindow ? R.styleable.ResponsiveActionMenuView_floatingWindowBottomMenuBackground : R.styleable.ResponsiveActionMenuView_bottomMenuBackground);
            this.mSuspendMenuBackground = typedArrayObtainStyledAttributes.getDrawable(this.mIsFloatingWindow ? R.styleable.ResponsiveActionMenuView_floatingWindowSuspendMenuBackground : R.styleable.ResponsiveActionMenuView_suspendMenuBackground);
            if (typedArrayObtainStyledAttributes != null) {
                typedArrayObtainStyledAttributes.recycle();
            }
            if (HyperMaterialUtils.isEnable()) {
                this.mBackgroundInBlur = new ColorDrawable(0);
            }
        } catch (Throwable th) {
            if (typedArrayObtainStyledAttributes != null) {
                typedArrayObtainStyledAttributes.recycle();
            }
            throw th;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (MiShadowUtils.SUPPORT_MI_SHADOW) {
            if (isSuspend()) {
                clipParent(this);
                MiShadowUtils.setMiShadow(this, this.mSuspendMenuMiShadowColor, this.mSuspendMenuMiShadowRadiusOffsetX, this.mSuspendMenuMiShadowRadiusOffsetY, this.mSuspendMenuBackgroundRadius);
                return;
            } else {
                restoreParentClipState(this);
                MiShadowUtils.clearMiShadow(this);
                return;
            }
        }
        if (isSuspend()) {
            if (this.mMenuOutShadowView == null) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(0, 0);
                OutDropShadowView outDropShadowView = new OutDropShadowView(getContext());
                this.mMenuOutShadowView = outDropShadowView;
                outDropShadowView.setShadowHostViewRadius(this.mSuspendMenuBackgroundRadius);
                ViewGroup viewGroup = (ViewGroup) getParent();
                viewGroup.addView(this.mMenuOutShadowView, layoutParams);
                View.OnLayoutChangeListener onLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: miuix.appcompat.internal.view.menu.action.ResponsiveActionMenuView$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnLayoutChangeListener
                    public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                        this.f$0.m1840x49a9a7cb(view, i, i2, i3, i4, i5, i6, i7, i8);
                    }
                };
                this.mParentLayoutChangeListener = onLayoutChangeListener;
                viewGroup.addOnLayoutChangeListener(onLayoutChangeListener);
                return;
            }
            return;
        }
        OutDropShadowView outDropShadowView2 = this.mMenuOutShadowView;
        if (outDropShadowView2 != null) {
            outDropShadowView2.onWillRemoved();
            ViewGroup viewGroup2 = (ViewGroup) getParent();
            viewGroup2.removeOnLayoutChangeListener(this.mParentLayoutChangeListener);
            viewGroup2.removeView(this.mMenuOutShadowView);
            this.mMenuOutShadowView = null;
        }
    }

    /* JADX INFO: renamed from: lambda$onAttachedToWindow$0$miuix-appcompat-internal-view-menu-action-ResponsiveActionMenuView, reason: not valid java name */
    /* synthetic */ void m1840x49a9a7cb(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        OutDropShadowView outDropShadowView = this.mMenuOutShadowView;
        if (outDropShadowView != null) {
            outDropShadowView.layout(getLeft(), getTop(), getRight(), getBottom());
        }
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        super.onRestoreInstanceState(parcelable);
        applyBlur(false);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    public void onWillRemoved() {
        super.onWillRemoved();
        applyBlur(false);
        restoreParentClipState(this);
        OutDropShadowView outDropShadowView = this.mMenuOutShadowView;
        if (outDropShadowView != null) {
            outDropShadowView.onWillRemoved();
            ViewGroup viewGroup = (ViewGroup) getParent();
            viewGroup.removeView(this.mMenuOutShadowView);
            viewGroup.removeOnLayoutChangeListener(this.mParentLayoutChangeListener);
            this.mMenuOutShadowView = null;
        }
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        applyBlur(false);
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        OutDropShadowView outDropShadowView = this.mMenuOutShadowView;
        if (outDropShadowView != null) {
            outDropShadowView.setTranslationY(f);
        }
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.onConfigChanged();
        }
        int i = configuration.densityDpi;
        if (i != this.mDensityDpi) {
            this.mDensityDpi = i;
            this.mMenuItemGap = this.mLargeFontAdaptionEnabled ? MiuixUIUtils.dp2px(this.mContext, 16.0f) : MiuixUIUtils.dp2px(this.mContext, 11.0f);
            this.mSuspendMenuMinMargin = MiuixUIUtils.dp2px(this.mContext, 16.0f);
            this.mSuspendMenuMinWidth = MiuixUIUtils.dp2px(this.mContext, 196.0f);
            this.mItemNormalPaddingTop = MiuixUIUtils.dp2px(this.mContext, 8.0f);
            this.mItemSmallPaddingTop = MiuixUIUtils.dp2px(this.mContext, 5.0f);
            this.mSuspendItemCenterExtraUp = MiuixUIUtils.dp2px(this.mContext, 2.0f);
            Resources resources = getContext().getResources();
            int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_bottom_menu_height);
            int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_bottom_menu_height_in_large_font);
            if (this.mLargeFontAdaptionEnabled) {
                dimensionPixelSize = dimensionPixelSize2;
            }
            this.mBottomMenuItemHeight = dimensionPixelSize;
            this.mSuspendMenuBackgroundRadius = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_suspend_menu_bg_radius);
            this.mSuspendMenuMiShadowRadius = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_suspend_menu_mi_shadow_radius);
            this.mSuspendMenuMiShadowRadiusOffsetX = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_suspend_menu_mi_shadow_radius_offset_x);
            this.mSuspendMenuMiShadowRadiusOffsetY = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_suspend_menu_mi_shadow_radius_offset_y);
            if (MiShadowUtils.SUPPORT_MI_SHADOW) {
                if (isSuspend()) {
                    MiShadowUtils.setMiShadow(this, this.mSuspendMenuMiShadowColor, this.mSuspendMenuMiShadowRadiusOffsetX, this.mSuspendMenuMiShadowRadiusOffsetY, this.mSuspendMenuMiShadowRadius);
                } else {
                    MiShadowUtils.clearMiShadow(this);
                }
            }
            refreshBackground();
            OutDropShadowView outDropShadowView = this.mMenuOutShadowView;
            if (outDropShadowView != null) {
                outDropShadowView.setShadowHostViewRadius(this.mSuspendMenuBackgroundRadius);
                return;
            }
            return;
        }
        if (this.mIsFloatingWindow != BaseFloatingActivityHelper.isFloatingWindow(ContextUtils.getActivity(this.mContext))) {
            this.mIsFloatingWindow = BaseFloatingActivityHelper.isFloatingWindow(this.mContext);
            refreshBackground();
        }
    }

    private void refreshBackground() {
        refreshMenuBackgroundDrawables(this.mAttrs);
        updateBackground();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBackground() {
        if (this.mSuspendEnabled) {
            setOutlineProvider(this.mViewOutlineInSuspend);
            setBackground(this.mApplyBlur ? this.mBackgroundInBlur : this.mSuspendMenuBackground);
            return;
        }
        setOutlineProvider(null);
        if (this.mBackgroundViewApplyBlur) {
            setBackground(null);
        } else {
            setBackground(this.mApplyBlur ? this.mBackgroundInBlur : this.mBottomMenuBackground);
        }
    }

    public void setSuspendEnabled(boolean z) {
        if (this.mSuspendEnabled != z) {
            this.mSuspendEnabled = z;
            MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
            if (miuiBlurUiHelper != null) {
                miuiBlurUiHelper.resetBlurParams();
                this.mBlurUiHelper.refreshBlur();
            }
        }
        updateBackground();
    }

    public boolean isSuspend() {
        return this.mSuspendEnabled;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    protected void clearBackground() {
        setBackground(null);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    protected void resetBackground() {
        updateBackground();
    }

    @Override // miuix.view.HyperMaterialWidget
    public void setMaterial(MaterialDayNightConfig materialDayNightConfig) {
        boolean z = this.mMaterial == null && materialDayNightConfig != null;
        if (materialDayNightConfig == null) {
            this.mMaterial = null;
            applyBlur(false);
            return;
        }
        this.mMaterial = materialDayNightConfig;
        if (this.mBlurUiHelper != null) {
            if (!isApplyBlur() && z) {
                applyBlur(true);
            }
            this.mBlurUiHelper.onConfigChanged();
        }
    }

    @Override // miuix.view.HyperMaterialWidget
    public MaterialDayNightConfig getMaterial() {
        return this.mMaterial;
    }

    @Override // miuix.view.BlurableWidget
    public void setSupportBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.setSupportBlur(z);
    }

    @Override // miuix.view.BlurableWidget
    public boolean isSupportBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isSupportBlur();
    }

    @Override // miuix.view.BlurableWidget
    public void setEnableBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.setEnableBlur(z);
        applyBlur(z && getMeasuredWidth() > 0 && getMeasuredHeight() > 0);
    }

    @Override // miuix.view.BlurableWidget
    public boolean isEnableBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isEnableBlur();
    }

    @Override // miuix.view.BlurableWidget
    public void applyBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.applyBlur(z);
    }

    @Override // miuix.view.BlurableWidget
    public boolean isApplyBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isApplyBlur();
    }

    public void addCustomView(View view) {
        if (view == null) {
            return;
        }
        this.mCustomView = view;
        addView(view);
    }

    public void showBottomMenuCustomView() {
        if (this.mCustomView == null || !this.mIsCustomViewHidden) {
            return;
        }
        Folme.useValue(new Object[0]).setTo("target", Float.valueOf(this.mCustomView.getMeasuredHeight())).to("target", Float.valueOf(0.0f), this.mAnimConfig);
        this.mIsCustomViewHidden = false;
    }

    public void hideBottomMenuCustomView() {
        if (this.mCustomView == null || this.mIsCustomViewHidden) {
            return;
        }
        Folme.useValue(new Object[0]).setTo("target", Float.valueOf(0.0f)).to("target", Float.valueOf(this.mCustomView.getMeasuredHeight()), this.mAnimConfig);
        this.mIsCustomViewHidden = true;
    }

    public void setBottomMenuCustomViewTranslationYWithPx(int i) {
        if (this.mCustomView == null || i < 0) {
            return;
        }
        this.mOffSet = i;
        requestLayout();
    }

    public int getBottomMenuCustomViewOffset() {
        return this.mOffSet;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, miuix.appcompat.internal.view.menu.MenuView
    public boolean filterLeftoverView(int i) {
        View childAt = getChildAt(i);
        if (isNotActionMenuItemChild(childAt)) {
            return false;
        }
        ActionMenuView.LayoutParams layoutParams = (ActionMenuView.LayoutParams) childAt.getLayoutParams();
        return (layoutParams == null || !layoutParams.isOverflowButton) && super.filterLeftoverView(i);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    public int getCollapsedHeight() {
        if (this.mIsEmpty) {
            return 0;
        }
        int measuredHeightWithMargin = ViewUtils.getMeasuredHeightWithMargin(this);
        View view = (View) getParent();
        int measuredHeight = view != null ? view.getMeasuredHeight() : 0;
        if (measuredHeight <= 0) {
            return 0;
        }
        return Math.max(measuredHeight, measuredHeightWithMargin);
    }

    private int getActionMenuItemCount() {
        int childCount = getChildCount();
        return indexOfChild(this.mCustomView) != -1 ? childCount - 1 : childCount;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    public boolean hasOnlyCustomView() {
        return this.mHasOnlyCustomView;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        this.mHasOnlyCustomView = false;
        this.mIsEmpty = false;
        int paddingBottom = getPaddingBottom();
        int paddingTop = getPaddingTop() + paddingBottom;
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int childCount = getChildCount();
        int actionMenuItemCount = getActionMenuItemCount();
        int size = View.MeasureSpec.getSize(i);
        if (childCount == 0 || actionMenuItemCount == 0) {
            this.mMenuItemHeight = 0;
            View view = this.mCustomView;
            if (view == null || view.getVisibility() == 8) {
                this.mIsEmpty = true;
                setMeasuredDimension(0, 0);
            } else {
                this.mHasOnlyCustomView = true;
                ActionMenuView.LayoutParams layoutParams = (ActionMenuView.LayoutParams) this.mCustomView.getLayoutParams();
                if (this.mSuspendEnabled) {
                    this.mCustomView.measure(View.MeasureSpec.makeMeasureSpec(size - (this.mSuspendMenuMinMargin * 2), BasicMeasure.EXACTLY), getChildMeasureSpec(i2, paddingTop, layoutParams.height));
                } else {
                    this.mCustomView.measure(View.MeasureSpec.makeMeasureSpec(size, BasicMeasure.EXACTLY), getChildMeasureSpec(i2, paddingTop, layoutParams.height));
                }
                this.mCustomView.setClipBounds(getCustomViewClipBounds());
                int measuredWidth = this.mCustomView.getMeasuredWidth();
                int measuredHeight = (this.mCustomView.getMeasuredHeight() + paddingTop) - this.mOffSet;
                setMeasuredDimension(measuredWidth, measuredHeight >= 0 ? measuredHeight : 0);
            }
            keepHidden();
            return;
        }
        if (this.mSuspendEnabled) {
            this.mMenuItemWidth = MiuixUIUtils.dp2px(this.mContext, 115.0f);
            int iDp2px = MiuixUIUtils.dp2px(this.mContext, 80.0f);
            int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(iDp2px, Integer.MIN_VALUE);
            int i3 = (actionMenuItemCount - 1) * this.mMenuItemGap;
            int i4 = (this.mMenuItemWidth * actionMenuItemCount) + i3;
            int i5 = this.mSuspendMenuMinMargin;
            if (i4 >= size - (i5 * 2)) {
                this.mMenuItemWidth = (((size - paddingLeft) - (i5 * 2)) - i3) / actionMenuItemCount;
            }
            measureActionMenuItem(View.MeasureSpec.makeMeasureSpec(this.mMenuItemWidth, BasicMeasure.EXACTLY), iMakeMeasureSpec, true);
            resetActionMenuItemPaddingTop((iDp2px - (getMaxChildrenTotalHeight() + (this.mSuspendItemCenterExtraUp * 2))) / 2);
            this.mMenuItemHeight = iDp2px;
            size = Math.max((this.mMenuItemWidth * actionMenuItemCount) + paddingLeft + i3, this.mSuspendMenuMinWidth);
        } else {
            int i6 = ((size - paddingLeft) - ((actionMenuItemCount - 1) * this.mMenuItemGap)) / actionMenuItemCount;
            this.mMenuItemWidth = i6;
            int i7 = this.mBottomMenuItemHeight + paddingBottom;
            measureActionMenuItem(View.MeasureSpec.makeMeasureSpec(i6, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(i7, BasicMeasure.EXACTLY), this.mSuspendEnabled);
            this.mMenuItemHeight = i7;
        }
        int measuredHeight2 = this.mMenuItemHeight + paddingTop;
        if (!this.mSuspendEnabled) {
            measuredHeight2 -= paddingBottom;
        }
        View view2 = this.mCustomView;
        if (view2 != null && view2.getVisibility() != 8) {
            this.mCustomView.measure(View.MeasureSpec.makeMeasureSpec(size, BasicMeasure.EXACTLY), getChildMeasureSpec(i2, paddingTop, ((ActionMenuView.LayoutParams) this.mCustomView.getLayoutParams()).height));
            this.mCustomView.setClipBounds(getCustomViewClipBounds());
            measuredHeight2 = (measuredHeight2 + this.mCustomView.getMeasuredHeight()) - this.mOffSet;
        }
        setMeasuredDimension(size, measuredHeight2);
        keepHidden();
    }

    private void keepHidden() {
        if (this.mIsHidden) {
            setTranslationY(ViewUtils.getMeasuredHeightWithMargin(this));
        }
    }

    private void measureActionMenuItem(int i, int i2, boolean z) {
        int i3;
        int childCount = getChildCount();
        for (int i4 = 0; i4 < childCount; i4++) {
            View childAt = getChildAt(i4);
            if (!isNotActionMenuItemChild(childAt)) {
                if (childAt instanceof LinearLayout) {
                    ((LinearLayout) childAt).setGravity(1);
                }
                if (z) {
                    childAt.setPadding(0, 0, 0, 0);
                } else {
                    if (MiuixUIUtils.isLayoutHideNavigation(this) && (!MiuixUIUtils.isFullScreenGestureMode(this.mContext) || MiuixUIUtils.isShowNavigationHandle(this.mContext))) {
                        i3 = this.mItemNormalPaddingTop;
                    } else {
                        i3 = this.mItemSmallPaddingTop;
                    }
                    childAt.setPadding(0, i3, 0, 0);
                }
                childAt.measure(i, i2);
            }
        }
    }

    private boolean isNotActionMenuItemChild(View view) {
        return view == this.mCustomView;
    }

    private int getMaxChildrenTotalHeight() {
        int childrenHeight;
        int childCount = getChildCount();
        int i = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (!isNotActionMenuItemChild(childAt) && (childAt instanceof LinearLayout) && i < (childrenHeight = getChildrenHeight((LinearLayout) childAt))) {
                i = childrenHeight;
            }
        }
        return i;
    }

    private int getChildrenHeight(LinearLayout linearLayout) {
        int childCount = linearLayout.getChildCount();
        int measuredHeight = 0;
        for (int i = 0; i < childCount; i++) {
            measuredHeight += linearLayout.getChildAt(i).getMeasuredHeight();
        }
        return measuredHeight;
    }

    private void resetActionMenuItemPaddingTop(int i) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (!isNotActionMenuItemChild(childAt) && (childAt instanceof LinearLayout)) {
                ((LinearLayout) childAt).setPadding(0, i, 0, 0);
            }
        }
    }

    private Rect getCustomViewClipBounds() {
        if (this.mCustomViewClipBounds == null) {
            this.mCustomViewClipBounds = new Rect();
        }
        this.mCustomViewClipBounds.set(0, 0, this.mCustomView.getMeasuredWidth(), this.mCustomView.getMeasuredHeight() - this.mOffSet);
        return this.mCustomViewClipBounds;
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0054 A[PHI: r3
  0x0054: PHI (r3v1 int) = (r3v0 int), (r3v0 int), (r3v4 int) binds: [B:11:0x002e, B:13:0x0034, B:15:0x0050] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int measuredHeight2 = 0;
        if (this.mHasOnlyCustomView) {
            View view = this.mCustomView;
            if (view == null || view.getVisibility() == 8) {
                return;
            }
            View view2 = this.mCustomView;
            ViewUtils.layoutChildView(this, view2, 0, 0, view2.getMeasuredWidth(), this.mCustomView.getMeasuredHeight());
            return;
        }
        View view3 = this.mCustomView;
        boolean z2 = false;
        if (view3 == null || view3.getVisibility() == 8) {
            i5 = measuredHeight2;
        } else {
            View view4 = this.mCustomView;
            ViewUtils.layoutChildView(this, view4, 0, 0, view4.getMeasuredWidth(), this.mCustomView.getMeasuredHeight());
            measuredHeight2 = this.mCustomView.getMeasuredHeight() - this.mOffSet;
            if (measuredHeight2 < 0) {
                i5 = 0;
            } else {
                i5 = measuredHeight2;
            }
        }
        int childCount = getChildCount();
        int actionMenuItemCount = getActionMenuItemCount();
        int paddingStart = getPaddingStart() + ((((measuredWidth - getPaddingStart()) - getPaddingEnd()) - ((this.mMenuItemWidth * actionMenuItemCount) + ((actionMenuItemCount - 1) * this.mMenuItemGap))) / 2);
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            if (!isNotActionMenuItemChild(childAt)) {
                ViewUtils.layoutChildView(this, childAt, paddingStart, i5, paddingStart + childAt.getMeasuredWidth(), measuredHeight);
                paddingStart += childAt.getMeasuredWidth() + this.mMenuItemGap;
            }
        }
        if (isEnableBlur() && measuredWidth > 0 && measuredHeight > 0) {
            z2 = true;
        }
        applyBlur(z2);
    }

    public void removeCustomView() {
        View view = this.mCustomView;
        if (view == null || view.getParent() == null) {
            return;
        }
        removeView(this.mCustomView);
        this.mOffSet = 0;
        this.mCustomView = null;
        this.mIsCustomViewHidden = false;
    }

    public void setHidden(boolean z) {
        this.mIsHidden = z;
    }

    public void clipParent(View view) {
        if (MiShadowUtils.SUPPORT_MI_SHADOW && this.mOriginViewParentClipState == null) {
            this.mOriginViewParentClipState = new boolean[2];
            for (int i = 0; i < 2; i++) {
                Object parent = view.getParent();
                if (!(parent instanceof ViewGroup)) {
                    return;
                }
                ViewGroup viewGroup = (ViewGroup) parent;
                this.mOriginViewParentClipState[i] = viewGroup.getClipChildren();
                viewGroup.setClipChildren(false);
                view = (View) parent;
            }
        }
    }

    private void restoreParentClipState(View view) {
        boolean[] zArr;
        if (!MiShadowUtils.SUPPORT_MI_SHADOW || (zArr = this.mOriginViewParentClipState) == null) {
            return;
        }
        int length = zArr.length;
        for (int i = 0; i < length; i++) {
            Object parent = view.getParent();
            if (!(parent instanceof ViewGroup)) {
                break;
            }
            ((ViewGroup) parent).setClipChildren(this.mOriginViewParentClipState[i]);
            view = (View) parent;
        }
        this.mOriginViewParentClipState = null;
    }
}
