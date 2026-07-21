package miuix.popupwidget.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiShadowUtils;
import miuix.core.util.WindowBaseInfo;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.popupwidget.R;
import miuix.popupwidget.internal.strategy.IPopupWindowStrategy;
import miuix.popupwidget.internal.strategy.MarginPopupWindowStrategy;
import miuix.popupwidget.internal.strategy.PopupWindowSpec;
import miuix.popupwidget.internal.strategy.PopupWindowStrategy;
import miuix.popupwidget.internal.util.SinglePopControl;
import miuix.smooth.SmoothFrameLayout2;
import miuix.springback.view.SpringBackLayout;
import miuix.theme.token.DimToken;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes3.dex */
public class PopupWindow extends android.widget.PopupWindow {
    protected static final int ANIMATION_EXTENSION_MARGIN = 35;
    public static final int ANIMATION_STYLE_DEFAULT = -1;
    public static final int ANIMATION_STYLE_NONE = 0;
    private static final int SHADOW_OFFSET_X = 0;
    private static final int SHADOW_OFFSET_Y = 26;
    private static final int SHADOW_RADIUS = 32;
    private static final String TAG = "PopupWindow";
    protected ListAdapter mAdapter;
    ViewTreeObserver.OnGlobalLayoutListener mAnchorGlobalLayoutListener;
    private WeakReference<View> mAnchorView;
    protected PopupAnimHelper mAnimHelper;
    private int mAnimationStyle;
    private boolean mAutoDismiss;
    private ContentSize mContentSize;
    protected View mContentView;
    protected final Context mContext;
    private int mDensityDpi;
    private boolean mDetachAnchorLayoutFlag;
    private boolean mDifferDensityCompat;
    private float mDimAmount;
    protected int mElevation;
    protected int mElevationExtra;
    private WeakReference<View> mFenceDecor;
    private boolean mHasShadow;
    private boolean mHideSoftInputEnabled;
    protected boolean mIgnoreAnchorVisibility;
    protected boolean mIsTransitioningToDismiss;
    private ListView mListView;
    private int mMaxAllowedHeight;
    private int mMaxAllowedWidth;
    private int mMinAllowedWidth;
    private int mMinSafeInsetDimen;
    private final DataSetObserver mObserver;
    private android.widget.PopupWindow.OnDismissListener mOnDismissListener;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    public PopupWindowSpec mPopupWindowSpec;
    protected IPopupWindowStrategy mPopupWindowStrategy;
    protected FrameLayout mRootView;
    private int mShadowColor;
    private SpringBackLayout mSpringBackLayout;
    private int mUserAnimationGravity;
    protected boolean mWindowAnimationEnabled;
    private int mWindowManagerFlags;

    /* JADX INFO: renamed from: miuix.popupwidget.widget.PopupWindow$1, reason: invalid class name */
    class AnonymousClass1 extends DataSetObserver {
        AnonymousClass1() {
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            final View anchor;
            PopupWindow.this.mContentSize.mHasContentWidth = false;
            if (!PopupWindow.this.isShowing() || (anchor = PopupWindow.this.getAnchor()) == null) {
                return;
            }
            anchor.post(new Runnable() { // from class: miuix.popupwidget.widget.PopupWindow$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1927lambda$onChanged$0$miuixpopupwidgetwidgetPopupWindow$1(anchor);
                }
            });
        }

        /* JADX INFO: renamed from: lambda$onChanged$0$miuix-popupwidget-widget-PopupWindow$1, reason: not valid java name */
        /* synthetic */ void m1927lambda$onChanged$0$miuixpopupwidgetwidgetPopupWindow$1(View view) {
            if (PopupWindow.this.mRootView == null || !PopupWindow.this.mRootView.isAttachedToWindow()) {
                return;
            }
            PopupWindow.this.updateLocation(view);
        }
    }

    public PopupWindow(Context context) {
        this(context, null);
    }

    public PopupWindow(Context context, View view) {
        this(context, view, null);
    }

    public PopupWindow(Context context, View view, IPopupWindowStrategy iPopupWindowStrategy) {
        super(context);
        this.mUserAnimationGravity = -1;
        this.mAnimationStyle = -1;
        this.mHasShadow = true;
        this.mShadowColor = 0;
        this.mHideSoftInputEnabled = true;
        this.mDifferDensityCompat = false;
        this.mDimAmount = Float.MAX_VALUE;
        this.mWindowManagerFlags = 2;
        this.mAutoDismiss = false;
        this.mIgnoreAnchorVisibility = false;
        this.mWindowAnimationEnabled = true;
        this.mObserver = new AnonymousClass1();
        this.mDetachAnchorLayoutFlag = false;
        this.mAnchorGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: miuix.popupwidget.widget.PopupWindow.2
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                View anchor = PopupWindow.this.getAnchor();
                if (anchor != null) {
                    PopupWindow popupWindow = PopupWindow.this;
                    popupWindow.updatePopupWindowSpec(popupWindow.mPopupWindowSpec);
                    PopupWindow.this.updateLocation(anchor);
                }
            }
        };
        this.mContext = context;
        this.mDensityDpi = context.getResources().getConfiguration().densityDpi;
        AnonymousClass1 anonymousClass1 = null;
        setBackgroundDrawable(null);
        updateDisplayConfig(view);
        this.mPopupWindowSpec = new PopupWindowSpec();
        this.mPopupWindowStrategy = iPopupWindowStrategy;
        if (iPopupWindowStrategy == null) {
            this.mPopupWindowStrategy = new MarginPopupWindowStrategy();
        }
        if (view != null) {
            setDecorView(view);
        }
        this.mContentSize = new ContentSize(anonymousClass1);
        setFocusable(true);
        setOutsideTouchable(true);
        ContainerView containerView = new ContainerView(context);
        this.mRootView = containerView;
        containerView.setClipChildren(false);
        this.mRootView.setClipToPadding(false);
        this.mRootView.setOnClickListener(new View.OnClickListener() { // from class: miuix.popupwidget.widget.PopupWindow$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1924lambda$new$0$miuixpopupwidgetwidgetPopupWindow(view2);
            }
        });
        prepareContentView();
        setClippingEnabled(false);
        super.setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() { // from class: miuix.popupwidget.widget.PopupWindow$$ExternalSyntheticLambda2
            @Override // android.widget.PopupWindow.OnDismissListener
            public final void onDismiss() {
                this.f$0.m1925lambda$new$1$miuixpopupwidgetwidgetPopupWindow();
            }
        });
        float f = context.getResources().getDisplayMetrics().density;
        this.mShadowColor = context.getResources().getColor(R.color.miuix_appcompat_drop_down_menu_spot_shadow_color);
        if (MiShadowUtils.SUPPORT_MI_SHADOW) {
            this.mElevation = (int) (f * 32.0f);
        } else {
            this.mElevation = AttributeResolver.resolveDimensionPixelSize(context, R.attr.popupWindowElevation);
            this.mElevationExtra = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_menu_popup_extra_elevation);
        }
        this.mDimAmount = AttributeResolver.resolveFloat(context, R.attr.popupWindowDimAmount, Float.MAX_VALUE);
    }

    /* JADX INFO: renamed from: lambda$new$0$miuix-popupwidget-widget-PopupWindow, reason: not valid java name */
    /* synthetic */ void m1924lambda$new$0$miuixpopupwidgetwidgetPopupWindow(View view) {
        dismiss();
    }

    /* JADX INFO: renamed from: lambda$new$1$miuix-popupwidget-widget-PopupWindow, reason: not valid java name */
    /* synthetic */ void m1925lambda$new$1$miuixpopupwidgetwidgetPopupWindow() {
        android.widget.PopupWindow.OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    protected void prepareContentView() {
        super.setContentView(this.mRootView);
    }

    protected void superSetContentViewWithoutClip(View view) {
        this.mRootView.removeAllViews();
        this.mRootView.addView(view);
        this.mContentView = view;
        super.setContentView(this.mRootView);
    }

    @Override // android.widget.PopupWindow
    public void setContentView(View view) {
        if (view instanceof SmoothFrameLayout2) {
            this.mContentView = view;
        } else {
            SmoothFrameLayout2 smoothFrameLayout2 = new SmoothFrameLayout2(this.mContext);
            smoothFrameLayout2.setCornerRadius(this.mContext.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_menu_radius));
            smoothFrameLayout2.addView(view);
            this.mContentView = smoothFrameLayout2;
        }
        this.mRootView.removeAllViews();
        this.mRootView.addView(this.mContentView);
        setPopupWindowStrategy(new PopupWindowStrategy());
        setClippingEnabled(true);
        super.setContentView(this.mRootView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void configurationChanged(Configuration configuration) {
        View anchor = getAnchor();
        if (isShowing() && this.mDifferDensityCompat && configuration.densityDpi != this.mDensityDpi) {
            this.mDensityDpi = configuration.densityDpi;
            updateDisplayConfig(null);
            if (isActivityRunning(getBaseActivity(this.mContext))) {
                dismissWithNoNotify();
                this.mRootView.removeAllViews();
                this.mContentView = null;
                if (prepareShow(anchor)) {
                    showAsDropDown(anchor);
                }
            }
        }
        if (anchor != null && !this.mDetachAnchorLayoutFlag) {
            this.mDetachAnchorLayoutFlag = true;
            anchor.getViewTreeObserver().addOnGlobalLayoutListener(this.mAnchorGlobalLayoutListener);
        }
        this.mContentSize.mHasContentWidth = false;
    }

    private void dismissWithNoNotify() {
        android.widget.PopupWindow.OnDismissListener onDismissListener = this.mOnDismissListener;
        this.mOnDismissListener = null;
        dismiss();
        this.mOnDismissListener = onDismissListener;
    }

    protected Rect updateSafeInsets(View view) {
        return updateSafeInsetsByDecor(this.mContext, view, this.mMinSafeInsetDimen);
    }

    protected void updateLocation(View view) {
        if (isShowing()) {
            computePopupContentSize();
            ViewUtils.getBoundsInWindow(view, this.mPopupWindowSpec.mAnchorViewBounds);
            int xInWindow = this.mPopupWindowStrategy.getXInWindow(this.mPopupWindowSpec);
            int yInWindow = this.mPopupWindowStrategy.getYInWindow(this.mPopupWindowSpec);
            setWidth(this.mPopupWindowSpec.mFinalPopupWidth);
            setHeight(this.mPopupWindowSpec.mFinalPopupHeight);
            update(xInWindow, yInWindow, this.mPopupWindowSpec.mFinalPopupWidth, this.mPopupWindowSpec.mFinalPopupHeight);
            if (this.mAnimHelper != null) {
                this.mAnimHelper.update(computeGravity(this.mPopupWindowSpec.mAnchorViewBounds, new Rect(xInWindow, yInWindow, this.mPopupWindowSpec.mFinalPopupWidth + xInWindow, this.mPopupWindowSpec.mFinalPopupHeight + yInWindow), 0, view.getLayoutDirection()));
            }
        }
    }

    @Deprecated
    public void setAnimationGravity(int i) {
        this.mUserAnimationGravity = i;
    }

    public void setAdapter(ListAdapter listAdapter) {
        ListAdapter listAdapter2 = this.mAdapter;
        if (listAdapter2 != null) {
            listAdapter2.unregisterDataSetObserver(this.mObserver);
        }
        this.mAdapter = listAdapter;
        if (listAdapter != null) {
            listAdapter.registerDataSetObserver(this.mObserver);
        }
    }

    public void setAutoDismiss(boolean z) {
        this.mAutoDismiss = z;
    }

    public boolean getAutoDismiss() {
        return this.mAutoDismiss;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override // android.widget.PopupWindow
    public void setOnDismissListener(android.widget.PopupWindow.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public void setAnchorView(View view) {
        if (getAnchor() != view) {
            detachAnchorView();
        }
        ViewUtils.getBoundsInWindow(view, this.mPopupWindowSpec.mAnchorViewBounds);
        this.mAnchorView = new WeakReference<>(view);
    }

    protected void detachAnchorView() {
        WeakReference<View> weakReference;
        if (this.mDetachAnchorLayoutFlag && (weakReference = this.mAnchorView) != null) {
            this.mDetachAnchorLayoutFlag = false;
            weakReference.get().getViewTreeObserver().removeOnGlobalLayoutListener(this.mAnchorGlobalLayoutListener);
        }
        this.mAnchorView = null;
    }

    public View getAnchor() {
        WeakReference<View> weakReference = this.mAnchorView;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public final boolean isInDismissAnimation() {
        return this.mIsTransitioningToDismiss;
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        detachAnchorView();
        dismissWithAnim();
        SinglePopControl.hidePop(this.mContext, this);
    }

    @Override // android.widget.PopupWindow
    public void setAnimationStyle(int i) {
        this.mAnimationStyle = i;
        super.setAnimationStyle(i);
    }

    public void setWindowAnimationEnabled(boolean z) {
        this.mWindowAnimationEnabled = z;
    }

    public void setContentWidth(int i) {
        this.mContentSize.updateWidth(i);
    }

    public void setContentHeight(int i) {
        this.mContentSize.mHeight = i;
    }

    public void setDecorView(final View view) {
        if (view == null) {
            return;
        }
        this.mFenceDecor = new WeakReference<>(view);
        if (view.isAttachedToWindow()) {
            updatePopupWindowSpec(this.mPopupWindowSpec);
        } else {
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: miuix.popupwidget.widget.PopupWindow.3
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View view2) {
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View view2) {
                    PopupWindow popupWindow = PopupWindow.this;
                    popupWindow.updatePopupWindowSpec(popupWindow.mPopupWindowSpec);
                    view.removeOnAttachStateChangeListener(this);
                }
            });
        }
    }

    public void setDropDownGravity(int i) {
        if (i != -1) {
            this.mPopupWindowSpec.mGravity = i;
        }
    }

    public void enableHideSoftInput(boolean z) {
        this.mHideSoftInputEnabled = z;
    }

    public void setPopupWindowStrategy(IPopupWindowStrategy iPopupWindowStrategy) {
        this.mPopupWindowStrategy = iPopupWindowStrategy;
    }

    public void setDimAmount(float f) {
        this.mDimAmount = f;
    }

    public float getDimAmount() {
        return this.mDimAmount;
    }

    public void setWindowManagerFlags(int i) {
        this.mWindowManagerFlags = i;
    }

    public int getWindowManagerFlags() {
        return this.mWindowManagerFlags;
    }

    protected void prepareWindowElevation(View view, int i) {
        if (shouldSetElevation()) {
            if (MiShadowUtils.SUPPORT_MI_SHADOW) {
                float f = view.getContext().getResources().getDisplayMetrics().density;
                MiShadowUtils.setMiShadow(view, this.mShadowColor, 0.0f * f, f * 26.0f, this.mElevation);
            } else {
                view.setElevation(i);
                setPopupShadowAlpha(view);
            }
        }
    }

    public boolean prepareShow(View view) {
        if (view == null) {
            Log.e(TAG, "show: anchor is null");
            return false;
        }
        boolean z = !view.getLocalVisibleRect(new Rect());
        if (!this.mIgnoreAnchorVisibility && z) {
            return false;
        }
        this.mAnchorView = new WeakReference<>(view);
        updatePopupWindowSpec(this.mPopupWindowSpec);
        if (this.mPopupWindowSpec.mMinWidth <= 0 || this.mPopupWindowSpec.mMaxWidth <= 0 || this.mPopupWindowSpec.mMaxHeight <= 0) {
            return false;
        }
        if (shouldSetElevation()) {
            setElevation(this.mElevation + this.mElevationExtra);
        }
        if (this.mContentView == null) {
            this.mContentView = LayoutInflater.from(this.mContext).inflate(R.layout.miuix_appcompat_drop_down_popup_list, (ViewGroup) null);
            Drawable drawableResolveDrawable = AttributeResolver.resolveDrawable(this.mContext, R.attr.immersionWindowBackground);
            if (drawableResolveDrawable != null) {
                this.mContentView.setBackground(drawableResolveDrawable);
            }
            this.mSpringBackLayout = (SpringBackLayout) this.mContentView.findViewById(R.id.spring_back);
            this.mContentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: miuix.popupwidget.widget.PopupWindow.4
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    boolean zIsNeedScroll = PopupWindow.this.mListView.getAdapter() != null ? PopupWindow.this.mPopupWindowStrategy.isNeedScroll(i4 - i2, PopupWindow.this.mPopupWindowSpec) : true;
                    PopupWindow.this.mSpringBackLayout.setEnabled(zIsNeedScroll);
                    PopupWindow.this.mListView.setVerticalScrollBarEnabled(zIsNeedScroll);
                }
            });
            setWindowAnimationEnabled(false);
        }
        if (this.mRootView.getChildCount() != 1 || this.mRootView.getChildAt(0) != this.mContentView) {
            this.mRootView.removeAllViews();
            this.mRootView.addView(this.mContentView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mContentView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -2;
            layoutParams.gravity = 16;
            layoutParams.setMargins(35, 35, 35, 35);
        }
        ListView listView = (ListView) this.mContentView.findViewById(android.R.id.list);
        this.mListView = listView;
        if (listView != null) {
            listView.setOnTouchListener(new AnonymousClass5());
            this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.popupwidget.widget.PopupWindow$$ExternalSyntheticLambda0
                @Override // android.widget.AdapterView.OnItemClickListener
                public final void onItemClick(AdapterView adapterView, View view2, int i, long j) {
                    this.f$0.m1926lambda$prepareShow$2$miuixpopupwidgetwidgetPopupWindow(adapterView, view2, i, j);
                }
            });
            this.mListView.setAdapter(this.mAdapter);
        }
        computePopupContentSize();
        setWidth(this.mPopupWindowSpec.mFinalPopupWidth);
        if (this.mHideSoftInputEnabled) {
            ((InputMethodManager) this.mContext.getApplicationContext().getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (Build.VERSION.SDK_INT >= 29 && !this.mWindowAnimationEnabled && this.mAnimHelper == null) {
            this.mAnimHelper = new PopupAnimHelper(this.mContentView);
            float f = this.mDimAmount;
            if (f == Float.MAX_VALUE) {
                f = ViewUtils.isNightMode(this.mContext) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT;
            }
            this.mAnimHelper.setDimValue(f);
            this.mAnimHelper.setWindowManagerFlags(this.mWindowManagerFlags);
        }
        return true;
    }

    /* JADX INFO: renamed from: miuix.popupwidget.widget.PopupWindow$5, reason: invalid class name */
    class AnonymousClass5 implements View.OnTouchListener {
        int lastIndex = -1;

        AnonymousClass5() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            int firstVisiblePosition;
            int i;
            View childAt;
            int iPointToPosition = PopupWindow.this.mListView.pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action == 1 || action == 3 || action == 6) {
                    this.lastIndex = -1;
                    PopupWindow.this.mListView.postDelayed(new Runnable() { // from class: miuix.popupwidget.widget.PopupWindow$5$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PopupWindow.AnonymousClass5.lambda$onTouch$0(view);
                        }
                    }, ViewConfiguration.getPressedStateDuration());
                }
            } else if (iPointToPosition != -1 && (firstVisiblePosition = iPointToPosition - PopupWindow.this.mListView.getFirstVisiblePosition()) != (i = this.lastIndex)) {
                if (i != -1 && (childAt = PopupWindow.this.mListView.getChildAt(this.lastIndex)) != null) {
                    childAt.setPressed(false);
                }
                PopupWindow.this.mListView.getChildAt(firstVisiblePosition).setPressed(true);
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
                    Log.e(PopupWindow.TAG, "list onTouch error " + e);
                }
            }
        }
    }

    /* JADX INFO: renamed from: lambda$prepareShow$2$miuix-popupwidget-widget-PopupWindow, reason: not valid java name */
    /* synthetic */ void m1926lambda$prepareShow$2$miuixpopupwidgetwidgetPopupWindow(AdapterView adapterView, View view, int i, long j) {
        int headerViewsCount = i - this.mListView.getHeaderViewsCount();
        if (this.mOnItemClickListener == null || headerViewsCount < 0 || headerViewsCount >= this.mAdapter.getCount()) {
            return;
        }
        this.mOnItemClickListener.onItemClick(adapterView, view, headerViewsCount, j);
    }

    protected void setPopupShadowAlpha(View view) {
        if (EnvStateManager.isFreeFormMode(this.mContext)) {
            view.setOutlineProvider(null);
            return;
        }
        view.setOutlineProvider(new ViewOutlineProvider() { // from class: miuix.popupwidget.widget.PopupWindow.6
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

    public void changeWindowBackground(View view) {
        WindowManager.LayoutParams layoutParams;
        if (view == null || (layoutParams = (WindowManager.LayoutParams) view.getLayoutParams()) == null) {
            return;
        }
        layoutParams.flags |= this.mWindowManagerFlags;
        float f = this.mDimAmount;
        if (f == Float.MAX_VALUE) {
            f = ViewUtils.isNightMode(view.getContext()) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT;
        }
        layoutParams.dimAmount = f;
        ((WindowManager) view.getContext().getSystemService("window")).updateViewLayout(view, layoutParams);
    }

    public void show(View view, ViewGroup viewGroup) {
        if (getDecorView() != viewGroup) {
            setDecorView(viewGroup);
        }
        show(view);
    }

    public void show(View view) {
        if (view == null) {
            return;
        }
        if (getAnchor() != view) {
            setAnchorView(view);
        }
        if (prepareShow(view)) {
            showAsDropDown(view);
        }
    }

    public void show() {
        show(getAnchor());
    }

    @Override // android.widget.PopupWindow, miuix.internal.widget.IPopupMenuWidget
    public void showAsDropDown(View view) {
        int iComputeGravity;
        this.mDifferDensityCompat = true;
        Log.d(TAG, "showAsDropDown popupwindowspec:" + this.mPopupWindowSpec);
        Rect rect = this.mPopupWindowSpec.mAnchorViewBounds;
        int xInWindow = this.mPopupWindowStrategy.getXInWindow(this.mPopupWindowSpec);
        int yInWindow = this.mPopupWindowStrategy.getYInWindow(this.mPopupWindowSpec);
        int i = this.mPopupWindowSpec.mFinalPopupWidth;
        int i2 = this.mPopupWindowSpec.mFinalPopupHeight;
        Rect rect2 = new Rect();
        rect2.set(0, 0, i, i2);
        setWidth(i);
        setHeight(i2);
        Log.d(TAG, "showWithAnchor getWidth " + i + " getHeight " + i2);
        rect2.offsetTo(xInWindow, yInWindow);
        if (this.mAnimationStyle == -1) {
            iComputeGravity = this.mUserAnimationGravity;
            if (iComputeGravity == -1) {
                iComputeGravity = computeGravity(this.mPopupWindowSpec.mAnchorViewBounds, rect2, this.mPopupWindowSpec.mGravity, view.getLayoutDirection());
            }
        } else {
            iComputeGravity = 0;
        }
        if (!isShowing()) {
            HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_BUTTON_SMALL, HapticFeedbackConstants.MIUI_POPUP_NORMAL);
        }
        showWithAnim(iComputeGravity);
        View decorView = getDecorView();
        if (decorView != null) {
            super.showAtLocation(decorView, 0, xInWindow, yInWindow);
        }
        prepareWindowElevation(this.mContentView, this.mElevation + this.mElevationExtra);
        this.mRootView.setElevation(0.0f);
        if (this.mWindowAnimationEnabled || this.mAnimHelper == null) {
            changeWindowBackground(this.mRootView.getRootView());
        }
        SinglePopControl.showPop(this.mContext, this);
    }

    protected void showWithAnim(int i) {
        if (this.mAnimHelper == null || this.mWindowAnimationEnabled) {
            setAnimationStyleByGravity(i);
            return;
        }
        float f = this.mDimAmount;
        if (f == Float.MAX_VALUE) {
            f = ViewUtils.isNightMode(this.mContext) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT;
        }
        this.mAnimHelper.setDimValue(f);
        this.mAnimHelper.showWithAnim(i);
    }

    protected void dismissWithAnim() {
        PopupAnimHelper popupAnimHelper = this.mAnimHelper;
        if (popupAnimHelper == null) {
            super.dismiss();
        } else {
            this.mIsTransitioningToDismiss = true;
            popupAnimHelper.dismissWithAnim(new Runnable() { // from class: miuix.popupwidget.widget.PopupWindow.7
                @Override // java.lang.Runnable
                public void run() {
                    PopupWindow.super.dismiss();
                    PopupWindow.this.mIsTransitioningToDismiss = false;
                }
            });
        }
    }

    protected void realDismiss() {
        super.dismiss();
    }

    public void showAsDropDown(View view, int i) {
        setDropDownGravity(i);
        showAsDropDown(view);
    }

    @Override // android.widget.PopupWindow
    public void showAsDropDown(View view, int i, int i2) {
        setHorizontalOffset(i);
        setVerticalOffset(i2);
        showAsDropDown(view);
    }

    @Override // android.widget.PopupWindow
    public void showAsDropDown(View view, int i, int i2, int i3) {
        setHorizontalOffset(i);
        setVerticalOffset(i2);
        setDropDownGravity(i3);
        showAsDropDown(view);
    }

    protected void superShowAtLocation(View view, int i, int i2, int i3) {
        super.showAtLocation(view, i, i2, i3);
    }

    @Override // android.widget.PopupWindow, miuix.internal.widget.IPopupMenuWidget
    public void showAtLocation(View view, int i, int i2, int i3) {
        if (view == null) {
            Log.e(TAG, "showAtLocation: parent is null");
            return;
        }
        this.mDifferDensityCompat = false;
        Rect rect = new Rect();
        ViewUtils.getBoundsInWindow(view, rect);
        int width = getWidth() > 0 ? getWidth() : this.mContentSize.mWidth;
        int height = getHeight() > 0 ? getHeight() : this.mContentSize.mHeight;
        Rect rect2 = new Rect();
        rect2.set(i2, i3, width + i2, height + i3);
        int iComputeGravity = this.mAnimationStyle == -1 ? computeGravity(rect, rect2, 0, view.getLayoutDirection()) : 0;
        if (!isShowing()) {
            HapticCompat.performHapticFeedback(this.mRootView, HapticFeedbackConstants.MIUI_BUTTON_SMALL, HapticFeedbackConstants.MIUI_POPUP_NORMAL);
        }
        super.showAtLocation(view, i, i2, i3);
        prepareWindowElevation(this.mContentView, this.mElevation + this.mElevationExtra);
        this.mRootView.setElevation(0.0f);
        if (this.mWindowAnimationEnabled || this.mAnimHelper == null) {
            changeWindowBackground(this.mRootView.getRootView());
        }
        showWithAnim(iComputeGravity);
        SinglePopControl.showPop(this.mContext, this);
    }

    public void setHasShadow(boolean z) {
        this.mHasShadow = z;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public void setVerticalOffset(int i) {
        this.mPopupWindowSpec.mOffsetYSet = true;
        this.mPopupWindowSpec.mUserOffsetY = i;
    }

    public void setHorizontalOffset(int i) {
        this.mPopupWindowSpec.mOffsetXSet = true;
        this.mPopupWindowSpec.mUserOffsetX = i;
    }

    public int getVerticalOffset() {
        return this.mPopupWindowSpec.mUserOffsetY;
    }

    public int getHorizontalOffset() {
        return this.mPopupWindowSpec.mUserOffsetX;
    }

    protected void computePopupContentSize() {
        Log.d(TAG, "computePopupContentSize");
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter != null) {
            this.mPopupWindowSpec.mItemViewBounds = getItemViewBounds(listAdapter, null, this.mContext);
        } else {
            getContentViewBounds(this.mPopupWindowSpec);
        }
        this.mPopupWindowStrategy.measureContentSize(this.mPopupWindowSpec);
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

    protected void getContentViewBounds(PopupWindowSpec popupWindowSpec) {
        if (this.mContentView != null) {
            popupWindowSpec.mContentViewBounds.set(0, 0, 0, 0);
            this.mContentView.measure(0, 0);
            popupWindowSpec.mContentViewBounds.set(0, 0, this.mContentView.getMeasuredWidth(), this.mContentView.getMeasuredHeight());
        }
    }

    public void setMaxAllowedHeight(int i) {
        this.mMaxAllowedHeight = i;
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

    protected boolean shouldSetElevation() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        return this.mHasShadow && (Build.VERSION.SDK_INT > 29 || (accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled()));
    }

    protected void setAnimationStyleByGravity(int i) {
        int i2 = R.style.Animation_PopupWindow_ImmersionMenu;
        if (i == 51) {
            i2 = R.style.Animation_PopupWindow_ImmersionMenu_LeftTop;
        } else if (i == 83) {
            i2 = R.style.Animation_PopupWindow_ImmersionMenu_LeftBottom;
        } else if (i == 53) {
            i2 = R.style.Animation_PopupWindow_ImmersionMenu_RightTop;
        } else if (i == 85) {
            i2 = R.style.Animation_PopupWindow_ImmersionMenu_RightBottom;
        } else if (i == 48) {
            i2 = R.style.Animation_PopupWindow_ImmersionMenu_Top;
        } else if (i == 80) {
            i2 = R.style.Animation_PopupWindow_ImmersionMenu_Bottom;
        } else if (i == 17) {
            i2 = R.style.Animation_PopupWindow_ImmersionMenu_Center;
        }
        super.setAnimationStyle(i2);
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

    public void updatePopupWindowSpec(PopupWindowSpec popupWindowSpec) {
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

    private static Activity getBaseActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private static boolean isActivityRunning(Activity activity) {
        return (activity == null || activity.isDestroyed() || activity.isFinishing()) ? false : true;
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

    private class ContainerView extends FrameLayout {
        private Runnable mPopupConfigChangeAction;

        public ContainerView(Context context) {
            super(context);
        }

        public ContainerView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public ContainerView(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            PopupWindow.this.detachAnchorView();
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            if (!PopupWindow.this.mAutoDismiss) {
                PopupWindow.this.configurationChanged(configuration);
            } else {
                PopupWindow.this.dismiss();
            }
        }
    }

    private static class ContentSize {
        boolean mHasContentWidth;
        int mHeight;
        int mWidth;

        private ContentSize() {
        }

        /* synthetic */ ContentSize(AnonymousClass1 anonymousClass1) {
            this();
        }

        public void updateWidth(int i) {
            this.mWidth = i;
            this.mHasContentWidth = true;
        }

        public String toString() {
            return "ContentSize{ w= " + this.mWidth + " h= " + this.mHeight + " }";
        }
    }
}
