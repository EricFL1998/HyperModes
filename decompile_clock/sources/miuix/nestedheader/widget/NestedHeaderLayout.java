package miuix.nestedheader.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.ScrollingView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.RomUtils;
import miuix.device.DeviceUtils;
import miuix.internal.util.AttributeResolver;
import miuix.nestedheader.R;
import miuix.theme.token.hypermaterial.Blur;
import miuix.theme.token.hypermaterial.Mask;
import miuix.view.BlurableWidget;
import miuix.view.MiuiBlurUiHelper;

/* JADX INFO: loaded from: classes3.dex */
public class NestedHeaderLayout extends NestedScrollingLayout implements BlurableWidget {
    private static final String TAG = "NestedHeaderLayout";
    private boolean mAcceptHeaderRootViewAlpha;
    private boolean mAcceptTriggerRootViewAlpha;
    private boolean mAdsorptionToNoOverlay;
    private boolean mAutoAnim;
    private MiuiBlurUiHelper mBlurUiHelper;
    private Rect mClipOverBgBounds;
    private int mCoordinatorHeightGapInSearchMode;
    private boolean mEnableHeaderAutoClose;
    private int mHeaderBottomMargin;
    private int mHeaderContentBottomMargin;
    private int mHeaderContentId;
    private float mHeaderContentMinHeight;
    private View mHeaderContentView;
    private int mHeaderMeasuredHeight;
    private int mHeaderTopMargin;
    private int mHeaderTotalHeight;
    private View mHeaderView;
    private int mHeaderViewId;
    private int mHeaderVisibleHeight;
    private boolean mIsCommonLiteStrategy;
    private boolean mIsMaskBitmapFromWindowBg;
    private boolean mIsShowOverBg;
    private boolean mIsStickyBeyondTrigger;
    private boolean mIsTouchStart;
    private int mLastScrollingProgress;
    private Drawable mMaskBackground;
    private Drawable mMaskBackgroundInBlur;
    private MaterialDayNightConfig mMaterial;
    private NestedHeaderChangedListener mNestedHeaderChangedListener;
    private NestedScrollingLayout.OnNestedChangedListener mOnNestedChangedListener;
    private View mOverBgView;
    private float mRangeOffset;
    private View mRootView;
    private int mStickyTotalHeight;
    private View mStickyView;
    private int mStickyViewId;
    private int mTriggerBottomMargin;
    private int mTriggerContentBottomMargin;
    private int mTriggerContentId;
    private float mTriggerContentMinHeight;
    private View mTriggerContentView;
    private int mTriggerMeasuredHeight;
    private int mTriggerTopMargin;
    private View mTriggerView;
    private int mTriggerViewId;
    private String mValueTag;

    public interface NestedHeaderChangedListener {
        default void onHeaderClosed(View view) {
        }

        default void onHeaderOpened(View view) {
        }

        default void onOverViewBlurStateChanged(boolean z) {
        }

        default void onScrollingProgressChanged(int i, boolean z, int i2, float f) {
        }

        default void onTriggerClosed(View view) {
        }

        default void onTriggerOpened(View view) {
        }
    }

    public void setNestedHeaderChangedListener(NestedHeaderChangedListener nestedHeaderChangedListener) {
        this.mNestedHeaderChangedListener = nestedHeaderChangedListener;
    }

    public void removeNestedHeaderChangedListener() {
        this.mNestedHeaderChangedListener = null;
    }

    public NestedHeaderLayout(Context context) {
        this(context, null);
    }

    public NestedHeaderLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NestedHeaderLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHeaderBottomMargin = 0;
        this.mHeaderTopMargin = 0;
        this.mTriggerBottomMargin = 0;
        this.mTriggerTopMargin = 0;
        this.mHeaderContentBottomMargin = 0;
        this.mTriggerContentBottomMargin = 0;
        this.mHeaderTotalHeight = 0;
        this.mHeaderMeasuredHeight = 0;
        this.mHeaderVisibleHeight = 0;
        this.mTriggerMeasuredHeight = 0;
        this.mStickyTotalHeight = 0;
        this.mLastScrollingProgress = 0;
        this.mIsTouchStart = false;
        this.mAutoAnim = true;
        this.mAdsorptionToNoOverlay = false;
        this.mAcceptTriggerRootViewAlpha = false;
        this.mAcceptHeaderRootViewAlpha = false;
        this.mClipOverBgBounds = new Rect();
        this.mIsShowOverBg = false;
        this.mCoordinatorHeightGapInSearchMode = 0;
        this.mValueTag = Long.toString(SystemClock.elapsedRealtime());
        this.mOnNestedChangedListener = new NestedScrollingLayout.OnNestedChangedListener() { // from class: miuix.nestedheader.widget.NestedHeaderLayout.3
            @Override // miuix.nestedheader.widget.NestedScrollingLayout.OnNestedChangedListener
            public void onStartNestedScroll(int i2) {
                if (i2 == 0) {
                    updateTouch(true);
                } else {
                    updateTag();
                }
            }

            @Override // miuix.nestedheader.widget.NestedScrollingLayout.OnNestedChangedListener
            public void onStopNestedScrollAccepted(int i2) {
                if (NestedHeaderLayout.this.mAutoAnim) {
                    updateAdsorption();
                }
            }

            @Override // miuix.nestedheader.widget.NestedScrollingLayout.OnNestedChangedListener
            public void onStopNestedScroll(int i2) {
                if (i2 == 0) {
                    updateTouch(false);
                }
            }

            private void updateAdsorption() {
                int scrollingFrom = NestedHeaderLayout.this.getScrollingFrom();
                int scrollingTo = NestedHeaderLayout.this.getScrollingTo();
                int i2 = NestedHeaderLayout.this.mContentInsetTop + scrollingFrom;
                int scrollingProgress = NestedHeaderLayout.this.getScrollingProgress();
                if (scrollingProgress == 0 || scrollingProgress >= scrollingTo || scrollingProgress <= scrollingFrom) {
                    if (scrollingProgress != 0 && scrollingProgress < scrollingTo && scrollingProgress == scrollingFrom && NestedHeaderLayout.this.mAdsorptionToNoOverlay) {
                        NestedHeaderLayout nestedHeaderLayout = NestedHeaderLayout.this;
                        nestedHeaderLayout.autoAdsorption(nestedHeaderLayout.getStickyScrollToOnNested());
                        return;
                    } else {
                        if (NestedHeaderLayout.this.mOverScrollingTo > 0) {
                            autoAdsorptionForOverScrollTo();
                            return;
                        }
                        return;
                    }
                }
                if (!NestedHeaderLayout.this.mEnableHeaderAutoClose || scrollingProgress >= i2 * 0.33f) {
                    if (scrollingProgress < scrollingTo * 0.5f) {
                        if (!NestedHeaderLayout.this.mEnableHeaderAutoClose && scrollingProgress < 0) {
                            return;
                        } else {
                            scrollingTo = 0;
                        }
                    }
                } else if (NestedHeaderLayout.this.isHeaderOpen() || scrollingProgress >= i2) {
                    scrollingTo = NestedHeaderLayout.this.getHeaderCloseProgress();
                } else {
                    scrollingTo = NestedHeaderLayout.this.getScrollingFrom();
                }
                if (NestedHeaderLayout.this.mAdsorptionToNoOverlay) {
                    scrollingTo = NestedHeaderLayout.this.getStickyScrollToOnNested();
                }
                NestedHeaderLayout.this.autoAdsorption(scrollingTo);
            }

            private void updateTouch(boolean z) {
                NestedHeaderLayout.this.mIsTouchStart = z;
                if (NestedHeaderLayout.this.mIsTouchStart) {
                    updateTag();
                }
            }

            private void updateTag() {
                NestedHeaderLayout.this.mValueTag = Long.toString(SystemClock.elapsedRealtime());
            }

            private void autoAdsorptionForOverScrollTo() {
                final String string = Long.toString(SystemClock.elapsedRealtime());
                NestedHeaderLayout.this.mValueTag = string;
                Folme.useValue(new Object[0]).setTo(string, Integer.valueOf(NestedHeaderLayout.this.mOverScrollingTo)).to(string, 0, new AnimConfig().setEase(-2, 1.0f, 0.4f).addListeners(new TransitionListener() { // from class: miuix.nestedheader.widget.NestedHeaderLayout.3.1
                    @Override // miuix.animation.listener.TransitionListener
                    public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                        UpdateInfo updateInfoFindByName = UpdateInfo.findByName(collection, string);
                        if (updateInfoFindByName == null || !NestedHeaderLayout.this.isScrolling(string)) {
                            return;
                        }
                        NestedHeaderLayout.this.syncOverScrollTo(updateInfoFindByName.getIntValue());
                    }
                }));
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.NestedHeaderLayout);
        this.mHeaderViewId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.NestedHeaderLayout_headerView, R.id.header_view);
        this.mStickyViewId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.NestedHeaderLayout_stickyView, R.id.sticky_view);
        this.mTriggerViewId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.NestedHeaderLayout_triggerView, R.id.trigger_view);
        this.mHeaderContentId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.NestedHeaderLayout_headerContentId, R.id.header_content_view);
        this.mTriggerContentId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.NestedHeaderLayout_triggerContentId, R.id.trigger_content_view);
        this.mHeaderContentMinHeight = typedArrayObtainStyledAttributes.getDimension(R.styleable.NestedHeaderLayout_headerContentMinHeight, context.getResources().getDimension(R.dimen.miuix_nested_header_layout_content_min_height));
        this.mTriggerContentMinHeight = typedArrayObtainStyledAttributes.getDimension(R.styleable.NestedHeaderLayout_triggerContentMinHeight, context.getResources().getDimension(R.dimen.miuix_nested_header_layout_content_min_height));
        this.mRangeOffset = typedArrayObtainStyledAttributes.getDimension(R.styleable.NestedHeaderLayout_rangeOffset, 0.0f);
        this.mEnableHeaderAutoClose = typedArrayObtainStyledAttributes.getBoolean(R.styleable.NestedHeaderLayout_headerAutoClose, true);
        this.mIsStickyBeyondTrigger = typedArrayObtainStyledAttributes.getBoolean(R.styleable.NestedHeaderLayout_stickyBeyondTrigger, false);
        try {
            Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.NestedHeaderLayout_maskBackground);
            this.mMaskBackground = drawable;
            if (drawable == null) {
                Drawable drawableMutate = AttributeResolver.resolveDrawable(getContext(), android.R.attr.windowBackground).mutate();
                this.mMaskBackground = drawableMutate;
                if ((drawableMutate instanceof BitmapDrawable) || (drawableMutate instanceof NinePatchDrawable)) {
                    this.mIsMaskBitmapFromWindowBg = true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "maskBackground error " + e);
        }
        typedArrayObtainStyledAttributes.recycle();
        addOnScrollListener(this.mOnNestedChangedListener);
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout, miuix.core.view.NestedCoordinatorObserver
    public int getNestedScrollableValue() {
        return -(this.mStickyTotalHeight + this.mHeaderTotalHeight);
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout, miuix.core.view.NestedCoordinatorObserver
    public void updateCoordinatorHeightGapInfo(int i, int i2) {
        super.updateCoordinatorHeightGapInfo(i, i2);
        if (this.mInSearchMode) {
            updateScrollingProgressImmediately(Math.min(i, 0));
        } else {
            updateOverBgState(getScrollingProgress(), this.mHeaderVisibleHeight);
        }
    }

    @Override // android.view.View
    public void offsetTopAndBottom(int i) {
        super.offsetTopAndBottom(i);
        updateOverBgState(getScrollingProgress(), this.mHeaderVisibleHeight);
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHeaderView = findViewById(this.mHeaderViewId);
        this.mStickyView = findViewById(this.mStickyViewId);
        this.mTriggerView = findViewById(this.mTriggerViewId);
        View view = this.mStickyView;
        if (view != null) {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: miuix.nestedheader.widget.NestedHeaderLayout.1
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    int i9 = (i4 - i2) - (i8 - i6);
                    if (i9 == 0 || !NestedHeaderLayout.this.mInSearchMode) {
                        return;
                    }
                    NestedHeaderLayout.this.updateScrollingRange(true, false, false, false);
                    NestedHeaderLayout nestedHeaderLayout = NestedHeaderLayout.this;
                    nestedHeaderLayout.updateScrollingProgressImmediately(Math.min(nestedHeaderLayout.getScrollingProgress() + i9, -NestedHeaderLayout.this.mHeaderTotalHeight));
                }
            });
        }
        View view2 = this.mHeaderView;
        if (view2 == null && this.mTriggerView == null && this.mStickyView == null) {
            throw new IllegalArgumentException("The headerView or triggerView or stickyView attribute is required and must refer to a valid child.");
        }
        if (view2 != null) {
            View viewFindViewById = view2.findViewById(this.mHeaderContentId);
            this.mHeaderContentView = viewFindViewById;
            if (viewFindViewById == null) {
                this.mHeaderContentView = this.mHeaderView.findViewById(android.R.id.inputArea);
            }
        }
        View view3 = this.mTriggerView;
        if (view3 != null) {
            View viewFindViewById2 = view3.findViewById(this.mTriggerContentId);
            this.mTriggerContentView = viewFindViewById2;
            if (viewFindViewById2 == null) {
                this.mTriggerContentView = this.mTriggerView.findViewById(android.R.id.inputArea);
            }
        }
        if (this.mOverBgView == null) {
            View view4 = new View(getContext());
            this.mOverBgView = view4;
            view4.setVisibility(4);
            this.mOverBgView.setClickable(true);
            this.mOverBgView.setBackground(this.mMaskBackground);
            this.mOverBgView.setImportantForAccessibility(4);
            addView(this.mOverBgView, indexOfChild(this.mScrollableView) + 1, new ViewGroup.LayoutParams(-1, -2));
        }
        this.mIsOverlayMode = true;
        if (HyperMaterialUtils.isEnable()) {
            this.mMaterial = MaterialDayNightConfig.create(RomUtils.getHyperOsVersion() > 2 ? Mask.Pured_Regular : Blur.ExtraHeavy);
            this.mBlurUiHelper = new MiuiBlurUiHelper(getContext(), this.mOverBgView, false, false, new MiuiBlurUiHelper.BlurStateCallback() { // from class: miuix.nestedheader.widget.NestedHeaderLayout.2
                final boolean isDarkThemeOverlay;

                {
                    this.isDarkThemeOverlay = MiuixUIUtils.isDarkThemeOverlay(NestedHeaderLayout.this.getContext(), R.color.miuix_default_color_on_surface_light);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public boolean isLightTheme() {
                    Integer colorFromDrawable;
                    if (NestedHeaderLayout.this.mMaskBackground == null || (colorFromDrawable = MiuixUIUtils.getColorFromDrawable(NestedHeaderLayout.this.mMaskBackground)) == null) {
                        return !this.isDarkThemeOverlay && AttributeResolver.resolveBoolean(NestedHeaderLayout.this.getContext(), android.R.attr.isLightTheme, true);
                    }
                    return MiuixUIUtils.isLightColor(colorFromDrawable.intValue()) && !this.isDarkThemeOverlay;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public Drawable getBackground() {
                    return NestedHeaderLayout.this.mMaskBackground;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public int getBackgroundColor() {
                    if (this.isDarkThemeOverlay) {
                        return AttributeResolver.resolveColor(NestedHeaderLayout.this.getContext(), miuix.theme.R.attr.colorSurface, 0);
                    }
                    return 0;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public MaterialConfig.BlurConfig getBlurConfig(boolean z) {
                    MaterialConfig.BlurConfig blurConfig;
                    MaterialDayNightConfig materialDayNightConfig = NestedHeaderLayout.this.mMaterial;
                    if (materialDayNightConfig == null || materialDayNightConfig == null) {
                        return null;
                    }
                    if (NestedHeaderLayout.this.mIsMaskBitmapFromWindowBg && (blurConfig = materialDayNightConfig.getBlurConfig(z)) != null && blurConfig.colorBlendConfig != null) {
                        return new MaterialConfig.BlurConfig(blurConfig.blurBgMode, blurConfig.blurContentMode, blurConfig.blurType, blurConfig.blurRadius, new int[]{blurConfig.colorBlendConfig.blendColors[0]}, new int[]{blurConfig.colorBlendConfig.blendModes[0]});
                    }
                    return materialDayNightConfig.getBlurConfig(z);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurEnableStateChanged(boolean z) {
                    if (z) {
                        NestedHeaderLayout.this.mMaskBackgroundInBlur = new ColorDrawable(0);
                    }
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurApplyStateChanged(boolean z) {
                    if (z) {
                        NestedHeaderLayout.this.mOverBgView.setBackground(NestedHeaderLayout.this.mMaskBackgroundInBlur);
                    } else {
                        NestedHeaderLayout.this.mOverBgView.setBackground(NestedHeaderLayout.this.mMaskBackground);
                    }
                    if (NestedHeaderLayout.this.mNestedHeaderChangedListener != null) {
                        NestedHeaderLayout.this.mNestedHeaderChangedListener.onOverViewBlurStateChanged(z);
                    }
                }
            });
            boolean z = DeviceUtils.isMiuiLiteV2() || DeviceUtils.isLiteV1StockPlus() || DeviceUtils.isMiuiMiddle();
            this.mIsCommonLiteStrategy = z;
            if (!z) {
                setSupportBlur(true);
                setEnableBlur(true);
            } else {
                this.mIsOverlayMode = false;
            }
        } else {
            this.mBlurUiHelper = null;
            this.mIsOverlayMode = false;
        }
        if (this.mUserSetOverlayMode != null) {
            this.mIsOverlayMode = this.mUserSetOverlayMode.booleanValue();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mRootView = getRootView();
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        View childAt;
        super.onMeasure(i, i2);
        View view = this.mHeaderView;
        if (!(((view instanceof ViewGroup) && (view instanceof ScrollingView)) || (view instanceof ScrollView)) || (childAt = ((ViewGroup) view).getChildAt(0)) == null || childAt.getMeasuredHeight() <= this.mHeaderView.getMeasuredHeight()) {
            return;
        }
        this.mHeaderView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 0));
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    public void setSelfScrollFirst(boolean z) {
        if (this.mIsSelfScrollFirst != z && this.mIsSelfScrollFirst && !isHeaderOpen()) {
            startNestedScroll(2, 1);
            dispatchNestedScroll(0, 0, 0, -this.mCoordinatorHeightTotalGap, this.mParentOffsetInWindow, 1);
            stopNestedScroll(1);
            syncScrollingProgress(0);
        }
        super.setSelfScrollFirst(z);
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    protected int getHeaderProgressFrom() {
        if (this.mIsOverlayMode) {
            return getScrollingFrom() + this.mContentInsetTop + this.mHeaderTotalHeight;
        }
        return getScrollingFrom();
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    protected int getHeaderProgressTo() {
        int scrollingFrom;
        int i;
        if (this.mIsOverlayMode) {
            scrollingFrom = getScrollingFrom() + this.mContentInsetTop + this.mHeaderTotalHeight;
            i = this.mStickyTotalHeight;
        } else {
            scrollingFrom = getScrollingFrom();
            i = this.mHeaderTotalHeight;
        }
        return scrollingFrom + i;
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    protected int getHeaderCloseProgress() {
        if (this.mIsOverlayMode) {
            return getScrollingFrom() + this.mContentInsetTop + this.mStickyTotalHeight;
        }
        return getScrollingFrom();
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    protected int getStickyScrollToOnNested() {
        int scrollingFrom;
        int i;
        View view;
        if (this.mInSearchMode && (view = this.mStickyView) != null && view.getVisibility() == 4) {
            scrollingFrom = getScrollingFrom();
            i = this.mContentInsetTop;
        } else {
            scrollingFrom = getScrollingFrom() + this.mContentInsetTop;
            i = this.mStickyTotalHeight;
        }
        return scrollingFrom + i;
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    protected int getScrollableViewMaxHeightWithoutOverlay() {
        View view;
        int measuredHeight = getMeasuredHeight();
        if (this.mInSearchMode && (view = this.mStickyView) != null && view.getVisibility() != 0) {
            int i = measuredHeight - this.mContentInsetTop;
            return i <= 0 ? measuredHeight : i;
        }
        View view2 = this.mStickyView;
        if (view2 != null && view2.getVisibility() != 8) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mStickyView.getLayoutParams();
            this.mStickyTotalHeight = this.mStickyView.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
        }
        int iMax = (measuredHeight - this.mContentInsetTop) - Math.max(0, this.mStickyTotalHeight);
        return iMax <= 0 ? measuredHeight : iMax;
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    public void onUpdateScrollingRangeOnLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onUpdateScrollingRangeOnLayout(z, i, i2, i3, i4);
        updateScrollingRange(true, false, false, false);
    }

    /* JADX WARN: Code duplicated, block: B:123:0x0297  */
    /* JADX WARN: Code duplicated, block: B:125:0x029b  */
    /* JADX WARN: Code duplicated, block: B:127:0x02a2  */
    /* JADX WARN: Code duplicated, block: B:128:0x02c0  */
    /* JADX WARN: Code duplicated, block: B:130:0x02d4  */
    /* JADX WARN: Code duplicated, block: B:135:? A[RETURN, SYNTHETIC] */
    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    protected void onScrollingProgressUpdated(int i) {
        int iMax;
        float f;
        float f2;
        int iMax2;
        int i2;
        int iMax3;
        boolean z;
        View view;
        int i3;
        float f3;
        float f4;
        float f5;
        float f6;
        super.onScrollingProgressUpdated(i);
        int paddingTop = !getClipToPadding() ? getPaddingTop() : 0;
        int i4 = this.mEnableOverScrollTo ? this.mOverScrollingTo : 0;
        View view2 = this.mHeaderView;
        boolean z2 = (view2 == null || view2.getVisibility() == 8) ? false : true;
        View view3 = this.mStickyView;
        boolean z3 = (view3 == null || view3.getVisibility() == 8) ? false : true;
        View view4 = this.mTriggerView;
        boolean z4 = (view4 == null || view4.getVisibility() == 8) ? false : true;
        int i5 = paddingTop + this.mContentInsetTop;
        int i6 = z2 ? this.mHeaderMeasuredHeight + this.mHeaderTopMargin + this.mHeaderBottomMargin : 0;
        if (z3) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mStickyView.getLayoutParams();
            iMax = this.mStickyView.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
        } else {
            iMax = 0;
        }
        if (z4) {
            int i7 = this.mTriggerTopMargin + this.mTriggerBottomMargin + this.mTriggerMeasuredHeight;
            iMax2 = i - Math.max(0, Math.min(getScrollingTo(), i));
            int iMax4 = Math.max(getScrollingFrom(), Math.min(getScrollingTo(), i));
            int i8 = i5 + i4 + i6 + this.mTriggerTopMargin;
            if (this.mIsStickyBeyondTrigger) {
                i8 += iMax;
            }
            int i9 = i8;
            View view5 = this.mTriggerContentView;
            if (view5 == null) {
                view5 = this.mTriggerView;
            }
            View view6 = view5;
            f = 1.0f;
            f2 = 0.0f;
            relayoutContent(this.mTriggerView, view6, i9, iMax4 - i7, false);
            if (this.mTriggerContentView == null) {
                f5 = iMax4 - this.mTriggerBottomMargin;
                f6 = this.mTriggerContentMinHeight;
            } else {
                f5 = iMax4 - this.mTriggerContentBottomMargin;
                f6 = this.mTriggerContentMinHeight;
            }
            float f7 = f5 / f6;
            float fMax = Math.max(0.0f, Math.min(1.0f, f7));
            if (this.mAcceptTriggerRootViewAlpha) {
                this.mTriggerView.setAlpha(fMax);
            } else {
                View view7 = this.mTriggerView;
                if ((view7 instanceof ViewGroup) && ((ViewGroup) view7).getChildCount() > 0) {
                    for (int i10 = 0; i10 < ((ViewGroup) this.mTriggerView).getChildCount(); i10++) {
                        ((ViewGroup) this.mTriggerView).getChildAt(i10).setAlpha(fMax);
                    }
                }
            }
            applyContentAlpha(makeTriggerContentViewList(view6), f7 - 1.0f);
        } else {
            f = 1.0f;
            f2 = 0.0f;
            iMax2 = i;
        }
        if (z2) {
            i2 = i5 + i6;
            View view8 = this.mHeaderContentView;
            if (view8 == null) {
                view8 = this.mHeaderView;
            }
            View view9 = view8;
            if (getScrollType() == 1) {
                int i11 = -this.mHeaderView.getTop();
                int iMax5 = Math.max(-i6, iMax2 + i5 + i4);
                this.mHeaderView.offsetTopAndBottom(i11 + iMax5);
                int iMax6 = (this.mHeaderTopMargin + this.mHeaderMeasuredHeight) - Math.max(0, this.mContentInsetTop - iMax5);
                Rect clipBounds = this.mHeaderView.getClipBounds();
                if (clipBounds == null) {
                    clipBounds = new Rect();
                }
                clipBounds.set(0, this.mHeaderMeasuredHeight - iMax6, this.mHeaderView.getMeasuredWidth(), this.mHeaderMeasuredHeight);
                this.mHeaderView.setClipBounds(clipBounds);
                this.mHeaderVisibleHeight = iMax6 + this.mHeaderTopMargin + this.mHeaderBottomMargin;
            } else {
                relayoutContent(this.mHeaderView, view9, i5 + i4 + this.mHeaderTopMargin, iMax2, false);
                if (this.mHeaderContentView == null) {
                    f3 = iMax2 - this.mHeaderBottomMargin;
                    f4 = this.mHeaderContentMinHeight;
                } else {
                    f3 = iMax2 - this.mHeaderContentBottomMargin;
                    f4 = this.mHeaderContentMinHeight;
                }
                float f8 = (f3 + f4) / f4;
                float fMax2 = Math.max(f2, Math.min(f, f8 + f));
                if (this.mAcceptHeaderRootViewAlpha) {
                    this.mHeaderView.setAlpha(fMax2);
                } else {
                    View view10 = this.mHeaderView;
                    if ((view10 instanceof ViewGroup) && ((ViewGroup) view10).getChildCount() > 0) {
                        for (int i12 = 0; i12 < ((ViewGroup) this.mHeaderView).getChildCount(); i12++) {
                            ((ViewGroup) this.mHeaderView).getChildAt(i12).setAlpha(fMax2);
                        }
                    }
                }
                applyContentAlpha(makeHeaderContentViewList(view9), f8);
                this.mHeaderVisibleHeight = this.mHeaderView.getHeight() + this.mHeaderTopMargin + this.mHeaderBottomMargin;
            }
        } else {
            i2 = i5;
        }
        int i13 = i6 + i5 + i4;
        if (z3) {
            i2 += iMax;
            int i14 = -this.mStickyView.getTop();
            if (this.mIsStickyBeyondTrigger) {
                iMax3 = Math.max(i5, iMax2 + i13);
            } else {
                iMax3 = Math.max(i5, i + i13);
            }
            this.mStickyView.offsetTopAndBottom(i14 + iMax3);
        } else if (this.mIsStickyBeyondTrigger) {
            iMax3 = Math.max(i5, iMax2 + i13);
        } else {
            iMax3 = Math.max(i5, i + i13);
        }
        int i15 = iMax3 + iMax;
        if (z3) {
            if (this.mStickyView.getVisibility() == 4) {
                i15 = iMax3;
                iMax = 0;
            } else if (this.mInSearchMode && this.mCurrentCoordinatorHeightGap < 0) {
                iMax = Math.max(0, iMax + this.mCurrentCoordinatorHeightGap);
            }
        }
        int i16 = iMax3 + iMax;
        int iMin = i + i2;
        if (!this.mIsOverlayMode) {
            if (this.mInSearchMode) {
                iMin = Math.max(iMin, i16);
            } else {
                iMin = Math.min(iMin, i16);
            }
        }
        this.mScrollableView.offsetTopAndBottom((-this.mScrollableView.getTop()) + iMin);
        int i17 = this.mLastScrollingProgress;
        if (i - i17 > 0) {
            checkSendHeaderChangeListener(i17, i, true);
        } else {
            if (i - i17 < 0) {
                z = false;
                checkSendHeaderChangeListener(i17, i, false);
            }
            this.mLastScrollingProgress = i;
            updateHeaderOpen(isHeaderOpen());
            view = this.mOverBgView;
            if (view != null) {
                if (this.mIsMaskBitmapFromWindowBg) {
                    view.setClickable(z);
                    if (this.mRootView != null) {
                        View view11 = this.mOverBgView;
                        i3 = 0;
                        view11.layout(view11.getLeft(), 0, this.mOverBgView.getLeft() + this.mRootView.getWidth(), this.mRootView.getHeight());
                    } else {
                        i3 = 0;
                    }
                    this.mClipOverBgBounds.set(i3, i3, this.mOverBgView.getWidth(), i15);
                    this.mOverBgView.setClipBounds(this.mClipOverBgBounds);
                } else {
                    view.setClickable(true);
                    View view12 = this.mOverBgView;
                    view12.layout(view12.getLeft(), z ? 1 : 0, this.mOverBgView.getRight(), i15);
                }
                updateOverBgState(i, this.mHeaderVisibleHeight);
            }
        }
        z = false;
        this.mLastScrollingProgress = i;
        updateHeaderOpen(isHeaderOpen());
        view = this.mOverBgView;
        if (view != null) {
            if (this.mIsMaskBitmapFromWindowBg) {
                view.setClickable(z);
                if (this.mRootView != null) {
                    View view13 = this.mOverBgView;
                    i3 = 0;
                    view13.layout(view13.getLeft(), 0, this.mOverBgView.getLeft() + this.mRootView.getWidth(), this.mRootView.getHeight());
                } else {
                    i3 = 0;
                }
                this.mClipOverBgBounds.set(i3, i3, this.mOverBgView.getWidth(), i15);
                this.mOverBgView.setClipBounds(this.mClipOverBgBounds);
            } else {
                view.setClickable(true);
                View view14 = this.mOverBgView;
                view14.layout(view14.getLeft(), z ? 1 : 0, this.mOverBgView.getRight(), i15);
            }
            updateOverBgState(i, this.mHeaderVisibleHeight);
        }
    }

    private void updateOverBgState(int i, int i2) {
        if (this.mOverBgView != null) {
            int i3 = this.mHeaderTotalHeight;
            if (!this.mInSearchMode) {
                i2 = i3;
            } else if (this.mCurrentCoordinatorHeightGap > this.mCoordinatorHeightGapInSearchMode || (i = i - getStickyScrollToOnNested()) > 0) {
                i = 0;
            }
            if (this.mIsOverlayMode) {
                if (getTop() <= 0 && i < (-i2) && !this.mIsShowOverBg) {
                    this.mIsShowOverBg = true;
                    this.mOverBgView.setVisibility(0);
                    applyBlur(true);
                } else if ((getTop() > 0 || i >= (-i2)) && this.mIsShowOverBg) {
                    this.mIsShowOverBg = false;
                    this.mOverBgView.setVisibility(4);
                    applyBlur(false);
                }
                if (this.mOverBgView.getVisibility() == 0) {
                    this.mScrollableView.setClipBounds(null);
                    return;
                }
                int height = this.mOverBgView.getHeight();
                if (this.mIsMaskBitmapFromWindowBg && this.mOverBgView.getClipBounds() != null) {
                    height = this.mOverBgView.getClipBounds().height();
                }
                Rect clipBounds = this.mScrollableView.getClipBounds();
                if (clipBounds == null) {
                    clipBounds = new Rect();
                }
                clipBounds.set(0, height - this.mScrollableView.getTop(), this.mScrollableView.getWidth(), this.mScrollableView.getHeight());
                this.mScrollableView.setClipBounds(clipBounds);
                return;
            }
            if (getTop() <= 0 && i < (-i2) && !this.mIsShowOverBg) {
                this.mIsShowOverBg = true;
                this.mOverBgView.setVisibility(0);
            } else if ((getTop() > 0 || i >= (-i2)) && this.mIsShowOverBg) {
                this.mIsShowOverBg = false;
                this.mOverBgView.setVisibility(4);
            }
            Rect clipBounds2 = this.mScrollableView.getClipBounds();
            if (clipBounds2 == null) {
                clipBounds2 = new Rect();
            }
            clipBounds2.set(0, 0, this.mScrollableView.getWidth(), this.mScrollableView.getHeight());
            this.mScrollableView.setClipBounds(clipBounds2);
        }
    }

    private void checkSendHeaderChangeListener(int i, int i2, boolean z) {
        if (this.mNestedHeaderChangedListener == null) {
            return;
        }
        int iMax = 0;
        if (z) {
            if (i2 == getScrollingTo() && getTriggerViewVisible()) {
                this.mNestedHeaderChangedListener.onTriggerOpened(this.mTriggerView);
            }
            if ((i < getHeaderProgressTo() && i2 >= getHeaderProgressTo() && getHeaderViewVisible()) || i2 == getHeaderProgressTo()) {
                this.mNestedHeaderChangedListener.onHeaderOpened(this.mHeaderView);
            }
        } else {
            if (i2 == 0 && getTriggerViewVisible()) {
                this.mNestedHeaderChangedListener.onTriggerClosed(this.mTriggerView);
            } else if (i2 == getScrollingFrom() && !getHeaderViewVisible()) {
                this.mNestedHeaderChangedListener.onTriggerClosed(this.mTriggerView);
            }
            int scrollingFrom = getHeaderViewVisible() ? 0 : getScrollingFrom();
            if (i > getHeaderProgressFrom() && i2 <= getHeaderProgressFrom() && getHeaderViewVisible()) {
                this.mNestedHeaderChangedListener.onHeaderClosed(this.mHeaderView);
            }
            if (i > scrollingFrom && i2 < scrollingFrom && getTriggerViewVisible()) {
                this.mNestedHeaderChangedListener.onTriggerClosed(this.mTriggerView);
            }
        }
        boolean z2 = i2 < getHeaderProgressFrom();
        View view = this.mHeaderView;
        if (view != null) {
            int height = view.getHeight();
            Rect clipBounds = this.mHeaderView.getClipBounds();
            iMax = clipBounds != null ? Math.max(0, clipBounds.height()) : height;
        }
        this.mNestedHeaderChangedListener.onScrollingProgressChanged(i2, z2, iMax, Math.max(0.0f, 1.0f - ((iMax * 1.0f) / this.mHeaderTotalHeight)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateScrollingRange(boolean z, boolean z2, boolean z3, boolean z4) {
        boolean z5;
        int i;
        boolean z6;
        boolean z7;
        int i2 = 0;
        int i3 = this.mIsOverlayMode ? -(this.mContentInsetTop + (getClipToPadding() ? 0 : getPaddingTop())) : 0;
        this.mHeaderTotalHeight = 0;
        View view = this.mHeaderView;
        if (view == null || view.getVisibility() == 8) {
            z5 = false;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mHeaderView.getLayoutParams();
            this.mHeaderBottomMargin = marginLayoutParams.bottomMargin;
            this.mHeaderTopMargin = marginLayoutParams.topMargin;
            int measuredHeight = this.mHeaderView.getMeasuredHeight();
            this.mHeaderMeasuredHeight = measuredHeight;
            this.mHeaderTotalHeight = measuredHeight + this.mHeaderTopMargin + this.mHeaderBottomMargin;
            View view2 = this.mHeaderContentView;
            if (view2 != null) {
                this.mHeaderContentBottomMargin = ((ViewGroup.MarginLayoutParams) view2.getLayoutParams()).bottomMargin;
            }
            i3 += (int) ((-this.mHeaderTotalHeight) + this.mRangeOffset);
            z5 = true;
        }
        this.mStickyTotalHeight = 0;
        View view3 = this.mStickyView;
        if (view3 == null || view3.getVisibility() == 8) {
            i = i3;
            z6 = false;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.mStickyView.getLayoutParams();
            this.mStickyTotalHeight = this.mStickyView.getMeasuredHeight() + marginLayoutParams2.topMargin + marginLayoutParams2.bottomMargin;
            if (this.mIsOverlayMode) {
                i3 += -this.mStickyTotalHeight;
            }
            i = i3;
            z6 = true;
        }
        View view4 = this.mTriggerView;
        if (view4 == null || view4.getVisibility() == 8) {
            z7 = false;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams3 = (ViewGroup.MarginLayoutParams) this.mTriggerView.getLayoutParams();
            this.mTriggerBottomMargin = marginLayoutParams3.bottomMargin;
            this.mTriggerTopMargin = marginLayoutParams3.topMargin;
            this.mTriggerMeasuredHeight = this.mTriggerView.getMeasuredHeight();
            View view5 = this.mTriggerContentView;
            if (view5 != null) {
                this.mTriggerContentBottomMargin = ((ViewGroup.MarginLayoutParams) view5.getLayoutParams()).bottomMargin;
            }
            i2 = this.mTriggerBottomMargin + this.mTriggerMeasuredHeight + this.mTriggerTopMargin;
            z7 = true;
        }
        if (this.mInSearchMode) {
            int i4 = -this.mHeaderTotalHeight;
            if (z6 && this.mStickyView.getVisibility() == 4) {
                i4 -= this.mStickyTotalHeight;
            }
            i2 = i4;
        }
        setScrollingRange(i, i2, z5, z7, z6, z, z2, z3, z4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void autoAdsorption(int i) {
        final String string = Long.toString(SystemClock.elapsedRealtime());
        this.mValueTag = string;
        Folme.useValue(new Object[0]).setTo(string, Integer.valueOf(getScrollingProgress())).to(string, Integer.valueOf(i), new AnimConfig().setEase(-2, 1.0f, 0.35f).addListeners(new TransitionListener() { // from class: miuix.nestedheader.widget.NestedHeaderLayout.4
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                UpdateInfo updateInfoFindByName = UpdateInfo.findByName(collection, string);
                if (updateInfoFindByName == null || !NestedHeaderLayout.this.isScrolling(string)) {
                    return;
                }
                NestedHeaderLayout.this.syncScrollingProgress(updateInfoFindByName.getIntValue());
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isScrolling(String str) {
        return (this.mIsTouchStart || !this.mValueTag.equals(str) || getAcceptedNestedFlingInConsumedProgress()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncOverScrollTo(int i) {
        this.mOverScrollingTo = i;
        updateScrollingProgress(getScrollingProgress());
        onScrollingProgressUpdated(getScrollingProgress());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncScrollingProgress(int i) {
        updateScrollingProgress(i);
        onScrollingProgressUpdated(i);
    }

    private List<View> makeTriggerContentViewList(View view) {
        return makeContentViewList(view, this.mTriggerContentId == R.id.trigger_content_view || this.mTriggerContentView != null);
    }

    private List<View> makeHeaderContentViewList(View view) {
        return makeContentViewList(view, this.mHeaderContentId == R.id.header_content_view || this.mHeaderContentView != null);
    }

    private List<View> makeContentViewList(View view, boolean z) {
        if (view == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        if (z) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    arrayList.add(viewGroup.getChildAt(i));
                }
            } else if (view != null) {
                arrayList.add(view);
            }
            return arrayList;
        }
        arrayList.add(view);
        return arrayList;
    }

    private void applyContentAlpha(List<View> list, float f) {
        if (list == null) {
            return;
        }
        float fMax = Math.max(0.0f, Math.min(1.0f, f));
        Iterator<View> it = list.iterator();
        while (it.hasNext()) {
            it.next().setAlpha(fMax);
        }
    }

    private void relayoutContent(View view, View view2, int i, int i2, boolean z) {
        view.layout(view.getLeft(), i, view.getRight(), Math.max(i, view.getMeasuredHeight() + i + i2));
        if (view != view2) {
            int iMax = Math.max(view2.getTop(), 0);
            int top = view2.getTop();
            int measuredHeight = view2.getMeasuredHeight() + iMax;
            if (z) {
                i2 /= 2;
            }
            view2.layout(view2.getLeft(), iMax, view2.getRight(), Math.max(top, measuredHeight + i2));
        }
    }

    public void setOverlayMode(boolean z) {
        this.mUserSetOverlayMode = Boolean.valueOf(z);
        this.mIsOverlayMode = z;
    }

    public boolean isOverlayMode() {
        return this.mIsOverlayMode;
    }

    public void updateScrollingProgressImmediately(int i) {
        updateScrollingProgress(i);
        onScrollingProgressUpdated(i);
    }

    public void setInSearchMode(boolean z) {
        this.mInSearchMode = z;
        if (this.mInSearchMode) {
            this.mCoordinatorHeightGapInSearchMode = getNestedScrollableValue();
        } else {
            this.mCoordinatorHeightGapInSearchMode = 0;
        }
        updateScrollingRange(false, false, false, false);
        requestLayout();
    }

    public void setAdsorptionToNoOverlay(boolean z) {
        this.mAdsorptionToNoOverlay = z;
    }

    public void setAutoAnim(boolean z) {
        this.mAutoAnim = z;
    }

    public boolean isAutoAnim() {
        return this.mAutoAnim;
    }

    public void setTriggerViewVisible(boolean z) {
        View view = this.mTriggerView;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
            updateScrollingRange(false, z, false, false);
        }
    }

    public boolean getTriggerViewVisible() {
        View view = this.mTriggerView;
        return view != null && view.getVisibility() == 0;
    }

    public void setStickyViewVisible(boolean z) {
        View view = this.mStickyView;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
            updateScrollingRange(false, false, z, false);
        }
    }

    public boolean getStickyViewVisible() {
        View view = this.mStickyView;
        return view != null && view.getVisibility() == 0;
    }

    public void setHeaderViewVisible(boolean z) {
        View view = this.mHeaderView;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
            updateScrollingRange(false, false, false, z);
        }
    }

    public boolean getHeaderViewVisible() {
        View view = this.mHeaderView;
        return view != null && view.getVisibility() == 0;
    }

    public View getHeaderView() {
        return this.mHeaderView;
    }

    public View getScrollableView() {
        return this.mScrollableView;
    }

    public View getStickyView() {
        return this.mStickyView;
    }

    public void setAutoTriggerClose(boolean z) {
        int scrollingFrom;
        if (getTriggerViewVisible() && getHeaderViewVisible() && getScrollingProgress() > 0) {
            scrollingFrom = 0;
        } else {
            scrollingFrom = (!getTriggerViewVisible() || getHeaderViewVisible() || getScrollingProgress() <= getScrollingFrom()) ? -1 : getScrollingFrom();
        }
        if (scrollingFrom != -1 && z) {
            autoAdsorption(scrollingFrom);
        } else if (scrollingFrom != -1) {
            syncScrollingProgress(scrollingFrom);
        }
    }

    public void setHeaderAutoCloseEnable(boolean z) {
        this.mEnableHeaderAutoClose = z;
    }

    public void setAutoHeaderClose(boolean z) {
        if (this.mIsSelfScrollFirst) {
            startNestedScroll(2, 1);
            dispatchNestedPreScroll(0, this.mCoordinatorHeightTotalGap, new int[2], new int[2], 1);
            stopNestedScroll(1);
        }
        if (!getHeaderViewVisible() || getScrollingProgress() <= getScrollingFrom()) {
            return;
        }
        if (z) {
            autoAdsorption(getHeaderCloseProgress());
        } else if (getHeaderViewVisible()) {
            syncScrollingProgress(getHeaderCloseProgress());
        }
    }

    public void setAutoAllClose(boolean z) {
        if (this.mIsSelfScrollFirst) {
            startNestedScroll(2, 1);
            dispatchNestedPreScroll(0, this.mCoordinatorHeightTotalGap, new int[2], new int[2], 1);
            stopNestedScroll(1);
        }
        if (getScrollingProgress() > getHeaderCloseProgress()) {
            if (z) {
                autoAdsorption(getHeaderCloseProgress());
            } else {
                syncScrollingProgress(getHeaderCloseProgress());
            }
        }
    }

    public void setAutoTriggerOpen(boolean z) {
        if (this.mIsSelfScrollFirst && !isHeaderOpen()) {
            startNestedScroll(2, 1);
            dispatchNestedScroll(0, 0, 0, -this.mCoordinatorHeightTotalGap, this.mParentOffsetInWindow, 1);
            stopNestedScroll(1);
        }
        if (!getTriggerViewVisible() || getScrollingProgress() >= getScrollingTo()) {
            return;
        }
        if (z) {
            autoAdsorption(getScrollingTo());
        } else {
            syncScrollingProgress(getScrollingTo());
        }
    }

    public void setAutoHeaderOpen(boolean z) {
        if (this.mIsSelfScrollFirst) {
            startNestedScroll(2, 1);
            dispatchNestedScroll(0, 0, 0, -this.mCoordinatorHeightTotalGap, this.mParentOffsetInWindow, 1);
            stopNestedScroll(1);
        }
        if (!getHeaderViewVisible() || getScrollingProgress() >= 0) {
            return;
        }
        if (z) {
            autoAdsorption(getHeaderProgressTo());
        } else {
            syncScrollingProgress(getHeaderProgressTo());
        }
    }

    public void setAutoAllOpen(boolean z) {
        if (this.mIsSelfScrollFirst) {
            startNestedScroll(2, 1);
            dispatchNestedScroll(0, 0, 0, -this.mCoordinatorHeightTotalGap, this.mParentOffsetInWindow, 1);
            stopNestedScroll(1);
        }
        if (getScrollingProgress() < getScrollingTo()) {
            if (z) {
                autoAdsorption(getScrollingTo());
            } else {
                syncScrollingProgress(getScrollingTo());
            }
        }
    }

    public boolean isHeaderOpen() {
        return getHeaderViewVisible() && getScrollingProgress() >= getHeaderProgressTo();
    }

    public boolean isTriggerOpen() {
        return getTriggerViewVisible() && ((getHeaderViewVisible() && getScrollingProgress() >= getScrollingTo()) || (!getHeaderViewVisible() && getScrollingProgress() >= 0));
    }

    public void setAcceptTriggerRootViewAlpha(boolean z) {
        this.mAcceptTriggerRootViewAlpha = z;
    }

    public boolean isAcceptTriggerRootViewAlpha() {
        return this.mAcceptTriggerRootViewAlpha;
    }

    public void setHeaderRootViewAcceptAlpha(boolean z) {
        this.mAcceptHeaderRootViewAlpha = z;
    }

    public boolean isAcceptHeaderRootViewAlpha() {
        return this.mAcceptHeaderRootViewAlpha;
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
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.setSupportBlur(z);
        }
    }

    @Override // miuix.view.BlurableWidget
    public boolean isSupportBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            return miuiBlurUiHelper.isSupportBlur();
        }
        return false;
    }

    @Override // miuix.view.BlurableWidget
    public void setEnableBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.setEnableBlur(z);
        }
    }

    @Override // miuix.view.BlurableWidget
    public boolean isEnableBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            return miuiBlurUiHelper.isEnableBlur();
        }
        return false;
    }

    @Override // miuix.view.BlurableWidget
    public void applyBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.applyBlur(z);
        }
    }

    @Override // miuix.view.BlurableWidget
    public boolean isApplyBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            return miuiBlurUiHelper.isApplyBlur();
        }
        return false;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.onConfigChanged();
            if (this.mIsCommonLiteStrategy || isOverlayMode() || this.mUserSetOverlayMode != null) {
                return;
            }
            this.mIsOverlayMode = true;
        }
    }
}
