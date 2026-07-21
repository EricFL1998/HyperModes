package miuix.popupwidget.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import miuix.core.util.EnvStateManager;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialDayNightConfig;
import miuix.core.util.MiShadowUtils;
import miuix.core.util.MiuiBlurUtils;
import miuix.core.util.WindowBaseInfo;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.popupwidget.R;
import miuix.popupwidget.internal.strategy.IPopupWindowStrategy;
import miuix.popupwidget.internal.strategy.PopupWindowSpec;
import miuix.popupwidget.internal.strategy.PopupWindowStrategy;
import miuix.springback.view.SpringBackLayout;
import miuix.theme.token.DimToken;
import miuix.theme.token.MaterialDayNightToken;
import miuix.theme.token.hypermaterial.Mask;

/* JADX INFO: loaded from: classes3.dex */
public class PopupView extends FrameLayout {
    private static final int SELF_BLUR_PADDING = 128;
    private static final int SHADOW_OFFSET_X = 0;
    private static final int SHADOW_OFFSET_Y = 26;
    private static final int SHADOW_RADIUS = 32;
    private static final String TAG = "PopupView";
    private ListAdapter mAdapter;
    ViewTreeObserver.OnGlobalLayoutListener mAnchorGlobalLayoutListener;
    private WeakReference<View> mAnchorView;
    private PopupAnimHelper mAnimHelper;
    private boolean mBackgroundBlurEnabled;
    private View mContentView;
    private Context mContext;
    private boolean mDetachAnchorLayoutFlag;
    private boolean mDimEnabled;
    private boolean mDismissOnConfigurationChanged;
    private int mElevation;
    private int mElevationExtra;
    private WeakReference<View> mFenceDecor;
    private boolean mHasShadow;
    private boolean mHideSoftInputEnabled;
    private boolean mIsShowing;
    private int mMaxAllowedHeight;
    private int mMaxAllowedWidth;
    private View mMenuLayer;
    private int mMinAllowedWidth;
    private int mMinSafeInsetDimen;
    private OnDismissListener mOnDismissListener;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private boolean mOutsideTouchable;
    private boolean mPassBlurEnabled;
    private PopupWindowSpec mPopupWindowSpec;
    protected IPopupWindowStrategy mPopupWindowStrategy;
    private boolean mSelfBlurEnabled;
    private int mShadowColor;
    private SpringBackLayout mSpringBackLayout;
    private int mUserAnimationGravity;

    public interface OnDismissListener {
        void onDismiss();
    }

    public PopupView(Context context) {
        this(context, null);
    }

    public PopupView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PopupView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHasShadow = true;
        this.mUserAnimationGravity = 0;
        this.mBackgroundBlurEnabled = false;
        this.mDimEnabled = true;
        this.mHideSoftInputEnabled = true;
        this.mDetachAnchorLayoutFlag = false;
        this.mPassBlurEnabled = false;
        this.mAnchorGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: miuix.popupwidget.widget.PopupView.4
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                PopupView popupView = PopupView.this;
                popupView.updatePopupWindowSpec(popupView.mPopupWindowSpec);
                PopupView popupView2 = PopupView.this;
                popupView2.updateLocation(popupView2.getAnchor());
            }
        };
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.miuix_appcompat_popup_menu_view, (ViewGroup) this, true);
        this.mContext = context;
        this.mPopupWindowSpec = new PopupWindowSpec();
        this.mPopupWindowStrategy = new PopupWindowStrategy();
        updateDisplayConfig(null);
        applyContentView();
        applyShadow();
        setClipChildren(false);
        setClipToOutline(false);
    }

    private void applyContentView() {
        this.mMenuLayer = findViewById(R.id.menu_layer);
        this.mContentView = findViewById(R.id.content_view);
        setOnClickListener(new View.OnClickListener() { // from class: miuix.popupwidget.widget.PopupView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1920lambda$applyContentView$0$miuixpopupwidgetwidgetPopupView(view);
            }
        });
        this.mContentView.setBackground(AttributeResolver.resolveDrawable(this.mContext, R.attr.immersionWindowBackground));
    }

    /* JADX INFO: renamed from: lambda$applyContentView$0$miuix-popupwidget-widget-PopupView, reason: not valid java name */
    /* synthetic */ void m1920lambda$applyContentView$0$miuixpopupwidgetwidgetPopupView(View view) {
        dismiss();
    }

    private void applyShadow() {
        float f = this.mContext.getResources().getDisplayMetrics().density;
        this.mShadowColor = this.mContext.getResources().getColor(R.color.miuix_appcompat_drop_down_menu_spot_shadow_color);
        if (MiShadowUtils.SUPPORT_MI_SHADOW) {
            this.mElevation = (int) (f * 32.0f);
        } else {
            this.mElevation = AttributeResolver.resolveDimensionPixelSize(this.mContext, R.attr.popupWindowElevation);
            this.mElevationExtra = this.mContext.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_menu_popup_extra_elevation);
        }
        this.mMenuLayer.setElevation(this.mElevation + this.mElevationExtra);
    }

    public void showWithAnchor() {
        ViewGroup viewGroup = (ViewGroup) getDecorView();
        if (!prepareShow(getAnchor()) || viewGroup == null) {
            return;
        }
        int xInWindow = this.mPopupWindowStrategy.getXInWindow(this.mPopupWindowSpec) - this.mPopupWindowSpec.mDecorViewBounds.left;
        int yInWindow = this.mPopupWindowStrategy.getYInWindow(this.mPopupWindowSpec) - this.mPopupWindowSpec.mDecorViewBounds.top;
        int i = this.mPopupWindowSpec.mFinalPopupWidth;
        int i2 = this.mPopupWindowSpec.mFinalPopupHeight;
        viewGroup.addView(this, new FrameLayout.LayoutParams(-1, -1));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mContentView.getLayoutParams();
        layoutParams.width = i;
        layoutParams.height = i2;
        this.mMenuLayer.setTranslationX(xInWindow - 128);
        this.mMenuLayer.setTranslationY(yInWindow - 128);
        Rect rect = new Rect();
        int i3 = this.mPopupWindowSpec.mDecorViewBounds.left;
        int i4 = this.mPopupWindowSpec.mDecorViewBounds.top;
        rect.set(0, 0, i, i2);
        rect.offsetTo(xInWindow + i3, yInWindow + i4);
        int iComputeGravity = this.mUserAnimationGravity;
        if (iComputeGravity == 0) {
            iComputeGravity = computeGravity(this.mPopupWindowSpec.mAnchorViewBounds, rect, this.mPopupWindowSpec.mGravity, viewGroup.getLayoutDirection());
        }
        showWithAnim(iComputeGravity);
        this.mIsShowing = true;
    }

    public boolean prepareShow(View view) {
        if (view == null) {
            Log.e(TAG, "show: anchor is null");
            return false;
        }
        this.mAnchorView = new WeakReference<>(view);
        updatePopupWindowSpec(this.mPopupWindowSpec);
        if (this.mPopupWindowSpec.mMinWidth <= 0 || this.mPopupWindowSpec.mMaxWidth <= 0 || this.mPopupWindowSpec.mMaxHeight <= 0) {
            return false;
        }
        prepareElevation();
        prepareContentView();
        computePopupContentSize();
        prepareSoftInputMethod(view.getWindowToken());
        prepareHyperMaterial();
        prepareAnim();
        return true;
    }

    private void prepareContentView() {
        final ListView listView = (ListView) findViewById(android.R.id.list);
        this.mSpringBackLayout = (SpringBackLayout) this.mContentView.findViewById(R.id.spring_back);
        this.mContentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: miuix.popupwidget.widget.PopupView.1
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                boolean zIsNeedScroll = PopupView.this.mPopupWindowStrategy.isNeedScroll(i4 - i2, PopupView.this.mPopupWindowSpec);
                PopupView.this.mSpringBackLayout.setEnabled(zIsNeedScroll);
                ListView listView2 = listView;
                if (listView2 != null) {
                    listView2.setVerticalScrollBarEnabled(zIsNeedScroll);
                }
            }
        });
        if (listView != null) {
            listView.setAdapter(this.mAdapter);
            listView.setOnTouchListener(new AnonymousClass2(listView));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.popupwidget.widget.PopupView$$ExternalSyntheticLambda3
                @Override // android.widget.AdapterView.OnItemClickListener
                public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                    this.f$0.m1922lambda$prepareContentView$1$miuixpopupwidgetwidgetPopupView(listView, adapterView, view, i, j);
                }
            });
        }
    }

    /* JADX INFO: renamed from: miuix.popupwidget.widget.PopupView$2, reason: invalid class name */
    class AnonymousClass2 implements View.OnTouchListener {
        int lastIndex = -1;
        final /* synthetic */ ListView val$listView;

        AnonymousClass2(ListView listView) {
            this.val$listView = listView;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            int firstVisiblePosition;
            int i;
            View childAt;
            int iPointToPosition = this.val$listView.pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action == 1 || action == 3 || action == 6) {
                    this.lastIndex = -1;
                    this.val$listView.postDelayed(new Runnable() { // from class: miuix.popupwidget.widget.PopupView$2$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PopupView.AnonymousClass2.lambda$onTouch$0(view);
                        }
                    }, ViewConfiguration.getPressedStateDuration());
                }
            } else if (iPointToPosition != -1 && (firstVisiblePosition = iPointToPosition - this.val$listView.getFirstVisiblePosition()) != (i = this.lastIndex)) {
                if (i != -1 && (childAt = this.val$listView.getChildAt(i)) != null) {
                    childAt.setPressed(false);
                }
                this.val$listView.getChildAt(firstVisiblePosition).setPressed(true);
                this.lastIndex = firstVisiblePosition;
            }
            return false;
        }

        static /* synthetic */ void lambda$onTouch$0(View view) {
            if (view instanceof ViewGroup) {
                try {
                    int childCount = ((ViewGroup) view).getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        ((ViewGroup) view).getChildAt(i).setPressed(false);
                    }
                } catch (Exception e) {
                    Log.e(PopupView.TAG, "list onTouch error " + e);
                }
            }
        }
    }

    /* JADX INFO: renamed from: lambda$prepareContentView$1$miuix-popupwidget-widget-PopupView, reason: not valid java name */
    /* synthetic */ void m1922lambda$prepareContentView$1$miuixpopupwidgetwidgetPopupView(ListView listView, AdapterView adapterView, View view, int i, long j) {
        int headerViewsCount = i - listView.getHeaderViewsCount();
        if (this.mOnItemClickListener != null && headerViewsCount >= 0 && headerViewsCount < this.mAdapter.getCount()) {
            this.mOnItemClickListener.onItemClick(adapterView, view, headerViewsCount, j);
        }
        dismiss();
    }

    private void prepareSoftInputMethod(IBinder iBinder) {
        if (this.mHideSoftInputEnabled) {
            ((InputMethodManager) this.mContext.getApplicationContext().getSystemService("input_method")).hideSoftInputFromWindow(iBinder, 0);
        }
    }

    private void prepareHyperMaterial() {
        if (HyperMaterialUtils.isFeatureEnable(this.mContext) && isBackgroundBlurEnabled()) {
            this.mContentView.setBackground(null);
            MiuiBlurUtils.setPassWindowBlurEnabled(this.mContentView, this.mPassBlurEnabled);
            HyperMaterialUtils.applyViewMaterial(this.mContentView, MaterialDayNightConfig.create(new MaterialDayNightToken(Mask.Pured_Thick_Light, Mask.Pured_Extra_Thin_Dark)).get(!ViewUtils.isNightMode(this.mContext)));
            return;
        }
        if (this.mContentView.getBackground() == null) {
            this.mContentView.setBackground(AttributeResolver.resolveDrawable(this.mContext, R.attr.immersionWindowBackground));
        }
    }

    private void prepareAnim() {
        if (Build.VERSION.SDK_INT < 29 || this.mAnimHelper != null) {
            return;
        }
        this.mAnimHelper = new PopupAnimHelper(this.mContentView);
        if (isDimEnabled()) {
            this.mAnimHelper.setDimMask(this);
            this.mAnimHelper.setDimValue(ViewUtils.isNightMode(this.mContext) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT);
        } else {
            this.mAnimHelper.setDimValue(0.0f);
        }
        this.mAnimHelper.setBlurEnabled(isSelfBlurEnabled());
        this.mAnimHelper.setBackgroundBlurEnabled(isBackgroundBlurEnabled());
    }

    public void showAtLocation(View view, int i, int i2, int i3) {
        if (prepareShow(view)) {
            this.mPopupWindowStrategy.getXInWindow(this.mPopupWindowSpec);
            this.mPopupWindowStrategy.getYInWindow(this.mPopupWindowSpec);
            int i4 = this.mPopupWindowSpec.mFinalPopupWidth;
            int i5 = this.mPopupWindowSpec.mFinalPopupHeight;
            if (getDecorView() == null) {
                setDecorView(view.getRootView());
            }
            int i6 = this.mPopupWindowSpec.mDecorViewBounds.left;
            int i7 = this.mPopupWindowSpec.mDecorViewBounds.top;
            ViewGroup viewGroup = (ViewGroup) getDecorView();
            viewGroup.addView(this, new FrameLayout.LayoutParams(-1, -1));
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mContentView.getLayoutParams();
            layoutParams.width = i4;
            layoutParams.height = i5;
            int i8 = i2 + i6;
            int i9 = i3 + i7;
            this.mMenuLayer.setTranslationX(i8 - 128);
            this.mMenuLayer.setTranslationY(i9 - 128);
            Rect rect = new Rect();
            rect.set(0, 0, i4, i5);
            rect.offsetTo(i8, i9);
            if (i == 0) {
                i = computeGravity(this.mPopupWindowSpec.mDecorViewBounds, rect, this.mPopupWindowSpec.mGravity, viewGroup.getLayoutDirection());
            }
            showWithAnim(i);
            this.mIsShowing = true;
        }
    }

    public void dismiss() {
        if (isShowing()) {
            final ViewGroup viewGroup = (ViewGroup) getDecorView();
            this.mAnimHelper.dismissWithAnim(new Runnable() { // from class: miuix.popupwidget.widget.PopupView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1921lambda$dismiss$2$miuixpopupwidgetwidgetPopupView(viewGroup);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$dismiss$2$miuix-popupwidget-widget-PopupView, reason: not valid java name */
    /* synthetic */ void m1921lambda$dismiss$2$miuixpopupwidgetwidgetPopupView(ViewGroup viewGroup) {
        viewGroup.removeView(this);
        this.mIsShowing = false;
        OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
        this.mAnimHelper.setOnAnimationListener(null);
    }

    private void showWithAnim(int i) {
        PopupAnimHelper popupAnimHelper = this.mAnimHelper;
        if (popupAnimHelper != null) {
            popupAnimHelper.setBlurEnabled(isSelfBlurEnabled());
            this.mAnimHelper.setBlurView(this.mMenuLayer);
            this.mAnimHelper.showWithAnim(i);
        }
    }

    public boolean isShowing() {
        return this.mIsShowing;
    }

    public PopupView setAdapter(ListAdapter listAdapter) {
        this.mAdapter = listAdapter;
        return this;
    }

    public PopupView setStrategy(IPopupWindowStrategy iPopupWindowStrategy) {
        this.mPopupWindowStrategy = iPopupWindowStrategy;
        return this;
    }

    public PopupView enableHideSoftInput(boolean z) {
        this.mHideSoftInputEnabled = z;
        return this;
    }

    public PopupView setSelfBlurEnabled(boolean z) {
        this.mSelfBlurEnabled = z;
        return this;
    }

    public boolean isSelfBlurEnabled() {
        return this.mSelfBlurEnabled;
    }

    public PopupView setBackgroundBlurEnabled(boolean z) {
        this.mBackgroundBlurEnabled = z;
        return this;
    }

    public boolean isBackgroundBlurEnabled() {
        return this.mBackgroundBlurEnabled;
    }

    public PopupView setDimEnabled(boolean z) {
        this.mDimEnabled = z;
        return this;
    }

    public boolean isDimEnabled() {
        return this.mDimEnabled;
    }

    public void setPassWindowBlurEnabled(boolean z) {
        this.mPassBlurEnabled = z;
    }

    public void setOffset(int i, int i2) {
        PopupAnimHelper popupAnimHelper = this.mAnimHelper;
        if (popupAnimHelper != null) {
            popupAnimHelper.setOffset(i, i2);
        }
    }

    public void setAnimationGravity(int i) {
        this.mUserAnimationGravity = i;
    }

    public PopupView dismissOnConfigurationChanged(boolean z) {
        this.mDismissOnConfigurationChanged = z;
        return this;
    }

    public PopupView setHasShadow(boolean z) {
        this.mHasShadow = z;
        return this;
    }

    public PopupView setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    public PopupView setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
        return this;
    }

    public PopupView setAnchorView(View view) {
        if (view == null) {
            return this;
        }
        if (getAnchor() != view) {
            detachAnchorView();
        }
        ViewUtils.getBoundsInWindow(view, this.mPopupWindowSpec.mAnchorViewBounds);
        this.mAnchorView = new WeakReference<>(view);
        return this;
    }

    public View getAnchor() {
        WeakReference<View> weakReference = this.mAnchorView;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public PopupView setDecorView(View view) {
        if (view == null) {
            return this;
        }
        this.mFenceDecor = new WeakReference<>(view);
        ViewUtils.getBoundsInWindow(view, this.mPopupWindowSpec.mDecorViewBounds);
        return this;
    }

    protected View getDecorView() {
        WeakReference<View> weakReference = this.mFenceDecor;
        if (weakReference == null || weakReference.get() == null) {
            WeakReference<View> weakReference2 = this.mAnchorView;
            if (weakReference2 != null) {
                return weakReference2.get().getRootView();
            }
            return null;
        }
        return this.mFenceDecor.get();
    }

    public View getContentView() {
        return this.mContentView;
    }

    protected boolean shouldSetElevation() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        return this.mHasShadow && (Build.VERSION.SDK_INT > 29 || (accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled()));
    }

    protected void prepareElevation() {
        if (shouldSetElevation()) {
            if (MiShadowUtils.SUPPORT_MI_SHADOW) {
                float f = this.mContentView.getContext().getResources().getDisplayMetrics().density;
                float f2 = 0.0f * f;
                float f3 = f * 26.0f;
                MiShadowUtils.setMiShadow(this.mContentView, this.mShadowColor, f2, f3, this.mElevation);
                MiShadowUtils.setMiShadow(this.mMenuLayer, this.mShadowColor, f2, f3, this.mElevation);
                return;
            }
            this.mContentView.setElevation(this.mElevation);
            setPopupShadowAlpha(this.mContentView);
        }
    }

    protected void setPopupShadowAlpha(View view) {
        if (EnvStateManager.isFreeFormMode(this.mContext)) {
            view.setOutlineProvider(null);
            return;
        }
        view.setOutlineProvider(new ViewOutlineProvider() { // from class: miuix.popupwidget.widget.PopupView.3
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view2, Outline outline) {
                if (view2.getWidth() == 0 || view2.getHeight() == 0) {
                    return;
                }
                outline.setAlpha(AttributeResolver.resolveFloat(view2.getContext(), R.attr.popupWindowShadowAlpha, 0.3f));
                if (view2.getBackground() != null) {
                    view2.getBackground().getOutline(outline);
                }
            }
        });
        if (Build.VERSION.SDK_INT >= 28) {
            view.setOutlineSpotShadowColor(this.mContext.getColor(R.color.miuix_appcompat_drop_down_menu_spot_shadow_color));
        }
    }

    protected void updatePopupWindowSpec(PopupWindowSpec popupWindowSpec) {
        View anchor = getAnchor();
        View decorView = getDecorView();
        if (anchor == null || decorView == null) {
            return;
        }
        Rect rectUpdateSafeInsets = updateSafeInsets(decorView);
        ViewUtils.getBoundsInWindow(decorView, popupWindowSpec.mDecorViewBounds);
        ViewUtils.getBoundsInWindow(anchor, popupWindowSpec.mAnchorViewBounds);
        Rect rect = popupWindowSpec.mDecorViewBounds;
        Point windowSize = EnvStateManager.getWindowSize(this.mContext);
        rect.set(Math.max(0, rect.left), Math.max(0, rect.top), Math.min(windowSize.x, rect.right), Math.min(windowSize.y, rect.bottom));
        int iCheckMaxWidth = checkMaxWidth(rect, rectUpdateSafeInsets);
        int iCheckMinWidth = checkMinWidth(rect, rectUpdateSafeInsets);
        int iCheckMaxHeight = checkMaxHeight(rect, rectUpdateSafeInsets);
        popupWindowSpec.mSafeInsets = rectUpdateSafeInsets;
        popupWindowSpec.mMaxWidth = iCheckMaxWidth;
        popupWindowSpec.mMinWidth = iCheckMinWidth;
        popupWindowSpec.mMaxHeight = iCheckMaxHeight;
        popupWindowSpec.layoutDirection = decorView.getLayoutDirection();
    }

    public void setOutsideTouchable(boolean z) {
        this.mOutsideTouchable = z;
        if (z) {
            setOnClickListener(null);
            setClickable(false);
        } else {
            setOnClickListener(new View.OnClickListener() { // from class: miuix.popupwidget.widget.PopupView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1923lambda$setOutsideTouchable$3$miuixpopupwidgetwidgetPopupView(view);
                }
            });
            setClickable(true);
        }
    }

    /* JADX INFO: renamed from: lambda$setOutsideTouchable$3$miuix-popupwidget-widget-PopupView, reason: not valid java name */
    /* synthetic */ void m1923lambda$setOutsideTouchable$3$miuixpopupwidgetwidgetPopupView(View view) {
        dismiss();
    }

    protected int[][] getItemViewBounds(ListAdapter listAdapter, ViewGroup viewGroup, Context context) {
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mPopupWindowSpec.mMaxWidth, Integer.MIN_VALUE);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
        int count = listAdapter.getCount();
        int[][] iArr = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, count, 2);
        int i = 0;
        View view = null;
        for (int i2 = 0; i2 < count; i2++) {
            int itemViewType = listAdapter.getItemViewType(i2);
            if (itemViewType != i) {
                view = null;
                i = itemViewType;
            }
            if (viewGroup == null) {
                viewGroup = new FrameLayout(context);
            }
            view = listAdapter.getView(i2, view, viewGroup);
            view.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
            iArr[i2][0] = view.getMeasuredWidth();
            iArr[i2][1] = view.getMeasuredHeight();
        }
        return iArr;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mDismissOnConfigurationChanged) {
            dismiss();
        } else {
            configurationChanged(configuration);
        }
    }

    protected void updateLocation(View view) {
        if (isShowing()) {
            computePopupContentSize();
            ViewUtils.getBoundsInWindow(view, this.mPopupWindowSpec.mAnchorViewBounds);
            int xInWindow = this.mPopupWindowStrategy.getXInWindow(this.mPopupWindowSpec) - this.mPopupWindowSpec.mDecorViewBounds.left;
            int yInWindow = this.mPopupWindowStrategy.getYInWindow(this.mPopupWindowSpec) - this.mPopupWindowSpec.mDecorViewBounds.top;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mContentView.getLayoutParams();
            if (layoutParams.width != this.mPopupWindowSpec.mFinalPopupWidth || layoutParams.height != this.mPopupWindowSpec.mFinalPopupHeight) {
                layoutParams.width = this.mPopupWindowSpec.mFinalPopupWidth;
                layoutParams.height = this.mPopupWindowSpec.mFinalPopupHeight;
                this.mContentView.setLayoutParams(layoutParams);
            }
            this.mMenuLayer.setTranslationX(xInWindow - 128);
            this.mMenuLayer.setTranslationY(yInWindow - 128);
            if (this.mAnimHelper != null) {
                Rect rect = new Rect(0, 0, this.mPopupWindowSpec.mFinalPopupWidth, this.mPopupWindowSpec.mFinalPopupHeight);
                rect.offsetTo(xInWindow + this.mPopupWindowSpec.mDecorViewBounds.left, yInWindow + this.mPopupWindowSpec.mDecorViewBounds.top);
                int iComputeGravity = this.mUserAnimationGravity;
                if (iComputeGravity == 0) {
                    iComputeGravity = computeGravity(this.mPopupWindowSpec.mAnchorViewBounds, rect, 0, view.getLayoutDirection());
                }
                this.mAnimHelper.update(iComputeGravity);
            }
        }
    }

    protected static int computeGravity(Rect rect, Rect rect2, int i, int i2) {
        int absoluteGravity = Gravity.getAbsoluteGravity(i, i2) & 112;
        int i3 = 48;
        if (Math.abs(rect2.centerY() - rect.centerY()) <= 10 ? absoluteGravity == 80 : rect2.centerY() <= rect.centerY()) {
            i3 = 80;
        }
        if (Math.abs(rect2.centerX() - rect.centerX()) > 10) {
            return rect2.centerX() > rect.centerX() ? i3 | 3 : i3 | 5;
        }
        return i3;
    }

    protected Rect updateSafeInsets(View view) {
        return updateSafeInsetsByDecor(this.mContext, view, this.mMinSafeInsetDimen);
    }

    protected int checkMaxHeight(Rect rect, Rect rect2) {
        return Math.min(this.mMaxAllowedHeight, (rect.height() - rect2.top) - rect2.bottom);
    }

    protected int checkMaxWidth(Rect rect, Rect rect2) {
        return Math.min(this.mMaxAllowedWidth, (rect.width() - rect2.left) - rect2.right);
    }

    protected int checkMinWidth(Rect rect, Rect rect2) {
        return Math.min(this.mMinAllowedWidth, (rect.width() - rect2.left) - rect2.right);
    }

    private void computePopupContentSize() {
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter != null) {
            this.mPopupWindowSpec.mItemViewBounds = getItemViewBounds(listAdapter, null, getContext());
        }
        this.mPopupWindowStrategy.measureContentSize(this.mPopupWindowSpec);
    }

    private void updateDisplayConfig(View view) {
        if (view == null) {
            view = getDecorView();
        }
        Resources resources = this.mContext.getResources();
        WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(this.mContext);
        int width = view != null ? view.getWidth() : windowInfo.windowSize.x;
        int height = view != null ? view.getHeight() : windowInfo.windowSize.y;
        this.mMaxAllowedWidth = Math.min(width, resources.getDimensionPixelSize(R.dimen.miuix_popup_window_max_width));
        this.mMinAllowedWidth = Math.min(width, resources.getDimensionPixelSize(R.dimen.miuix_popup_window_min_width));
        this.mMaxAllowedHeight = Math.min(height, resources.getDimensionPixelSize(R.dimen.miuix_popup_window_max_height));
        this.mMinSafeInsetDimen = resources.getDimensionPixelSize(R.dimen.miuix_popup_window_safe_margin);
    }

    private void configurationChanged(Configuration configuration) {
        View anchor = getAnchor();
        if (anchor == null || this.mDetachAnchorLayoutFlag) {
            return;
        }
        this.mDetachAnchorLayoutFlag = true;
        anchor.getViewTreeObserver().addOnGlobalLayoutListener(this.mAnchorGlobalLayoutListener);
    }

    private void detachAnchorView() {
        if (this.mDetachAnchorLayoutFlag) {
            this.mDetachAnchorLayoutFlag = true;
            View anchor = getAnchor();
            if (anchor != null) {
                anchor.getViewTreeObserver().removeOnGlobalLayoutListener(this.mAnchorGlobalLayoutListener);
            }
        }
    }

    private static Rect updateSafeInsetsByDecor(Context context, View view, int i) {
        DisplayCutout displayCutout;
        Rect rect = new Rect();
        rect.set(i, 0, i, i);
        Rect rect2 = new Rect();
        ViewUtils.getBoundsInWindow(view, rect2);
        WindowInsets rootWindowInsets = view.getRootWindowInsets();
        if (rootWindowInsets != null) {
            if (Build.VERSION.SDK_INT >= 30) {
                Insets insets = rootWindowInsets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                rect.set(insets.left, insets.top, insets.right, insets.bottom);
            } else {
                Rect rect3 = new Rect();
                if (Build.VERSION.SDK_INT >= 28 && (displayCutout = rootWindowInsets.getDisplayCutout()) != null) {
                    rect3.set(displayCutout.getSafeInsetLeft(), displayCutout.getSafeInsetTop(), displayCutout.getSafeInsetRight(), displayCutout.getSafeInsetBottom());
                }
                rect.set(Math.max(rect3.left, rootWindowInsets.getSystemWindowInsetLeft()), Math.max(rect3.top, rootWindowInsets.getSystemWindowInsetTop()), Math.max(rect3.right, rootWindowInsets.getSystemWindowInsetRight()), Math.max(rect3.bottom, rootWindowInsets.getSystemWindowInsetBottom()));
            }
        }
        WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(context);
        rect.left = Math.max(i, rect.left - rect2.left);
        rect.right = Math.max(i, rect.right - Math.max(0, windowInfo.windowSize.x - rect2.right));
        rect.top = Math.max(i, rect.top - rect2.top);
        rect.bottom = Math.max(i, rect.bottom - Math.max(0, windowInfo.windowSize.y - rect2.bottom));
        return rect;
    }
}
