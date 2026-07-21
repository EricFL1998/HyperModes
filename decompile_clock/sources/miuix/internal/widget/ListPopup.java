package miuix.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import java.lang.ref.WeakReference;
import miuix.animation.ViewHoverListener;
import miuix.animation.utils.LogUtils;
import miuix.appcompat.R;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiShadowUtils;
import miuix.core.util.WindowBaseInfo;
import miuix.core.util.WindowUtils;
import miuix.internal.util.AccessibilityUtil;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.popupwidget.internal.util.SinglePopControl;
import miuix.theme.token.DimToken;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
public class ListPopup extends PopupWindow {
    private static final float OFFSET_X = 8.0f;
    private static final float OFFSET_Y = 8.0f;
    private static final int SHADOW_OFFSET_X = 0;
    private static final int SHADOW_OFFSET_Y = 26;
    private static final int SHADOW_RADIUS = 32;
    private static final String TAG = "ListPopupWindow";
    private ListAdapter mAdapter;
    private WeakReference<View> mAnchor;
    protected final Rect mBackgroundPadding;
    private ContentSize mContentSize;
    protected View mContentView;
    private Context mContext;
    private int mDropDownGravity;
    protected int mElevation;
    protected int mElevationExtra;
    private WeakReference<View> mFenceDecor;
    private boolean mHasShadow;
    private boolean mIsCustomContent;
    private ListView mListView;
    protected int mMaxAllowedHeight;
    private int mMaxAllowedWidth;
    private int mMinAllowedWidth;
    private int mMinSafeInset;
    private DataSetObserver mObserver;
    private int mOffsetFromStatusBar;
    private int mOffsetX;
    private boolean mOffsetXSet;
    private int mOffsetY;
    private boolean mOffsetYSet;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private Rect mPositionSafeInsets;
    protected FrameLayout mRootView;
    private int mShadowColor;
    private int mUserAnimationGravity;
    private Rect mWindowDecorBounds;

    /* JADX INFO: renamed from: miuix.internal.widget.ListPopup$1, reason: invalid class name */
    class AnonymousClass1 extends DataSetObserver {
        AnonymousClass1() {
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            final View anchor;
            ListPopup.this.mContentSize.mHasContentWidth = false;
            if (!ListPopup.this.isShowing() || (anchor = ListPopup.this.getAnchor()) == null) {
                return;
            }
            anchor.post(new Runnable() { // from class: miuix.internal.widget.ListPopup$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1863lambda$onChanged$0$miuixinternalwidgetListPopup$1(anchor);
                }
            });
        }

        /* JADX INFO: renamed from: lambda$onChanged$0$miuix-internal-widget-ListPopup$1, reason: not valid java name */
        /* synthetic */ void m1863lambda$onChanged$0$miuixinternalwidgetListPopup$1(View view) {
            if (ListPopup.this.mRootView == null || !ListPopup.this.mRootView.isAttachedToWindow()) {
                return;
            }
            ListPopup.this.updatePosition(view);
        }
    }

    public ListPopup(Context context) {
        this(context, null);
    }

    public ListPopup(Context context, View view) {
        super(context);
        this.mDropDownGravity = 8388661;
        this.mUserAnimationGravity = -1;
        this.mOffsetFromStatusBar = 0;
        this.mHasShadow = true;
        this.mShadowColor = 0;
        this.mIsCustomContent = false;
        this.mObserver = new AnonymousClass1();
        this.mContext = context;
        AnonymousClass1 anonymousClass1 = null;
        setBackgroundDrawable(null);
        setHeight(-2);
        this.mFenceDecor = new WeakReference<>(view);
        Resources resources = context.getResources();
        WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(this.mContext);
        Log.d("ListPopup", "new windowInfo w " + windowInfo.windowSize.x + " h " + windowInfo.windowSize.y);
        this.mMinSafeInset = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_context_menu_window_margin_screen);
        Rect rect = new Rect();
        this.mPositionSafeInsets = rect;
        int i = this.mMinSafeInset;
        rect.set(i, i, i, i);
        if (view != null) {
            Rect rect2 = new Rect();
            ViewUtils.getBoundsInWindow(view, rect2);
            updateSafeInsetsByDecor(view, rect2, new Rect(0, 0, windowInfo.windowSize.x, windowInfo.windowSize.y), new Rect(0, 0, windowInfo.windowSize.x, windowInfo.windowSize.y));
        }
        int width = view != null ? view.getWidth() : windowInfo.windowSize.x;
        int height = view != null ? view.getHeight() : windowInfo.windowSize.y;
        this.mMaxAllowedWidth = Math.min(width, resources.getDimensionPixelSize(R.dimen.miuix_appcompat_popup_menu_max_width));
        this.mMinAllowedWidth = Math.min(width, resources.getDimensionPixelSize(R.dimen.miuix_appcompat_popup_menu_min_width));
        this.mMaxAllowedHeight = Math.min(height, resources.getDimensionPixelSize(R.dimen.miuix_appcompat_popup_menu_max_height));
        float f = this.mContext.getResources().getDisplayMetrics().density;
        int i2 = (int) (8.0f * f);
        this.mOffsetX = i2;
        this.mOffsetY = i2;
        this.mBackgroundPadding = new Rect();
        this.mContentSize = new ContentSize(anonymousClass1);
        setFocusable(true);
        setOutsideTouchable(true);
        ContainerView containerView = new ContainerView(context);
        this.mRootView = containerView;
        containerView.setClipChildren(false);
        this.mRootView.setClipToPadding(false);
        this.mRootView.setOnClickListener(new View.OnClickListener() { // from class: miuix.internal.widget.ListPopup$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1860lambda$new$0$miuixinternalwidgetListPopup(view2);
            }
        });
        prepareContentView(context);
        setAnimationStyle(R.style.Animation_PopupWindow_ImmersionMenu);
        super.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: miuix.internal.widget.ListPopup$$ExternalSyntheticLambda1
            @Override // android.widget.PopupWindow.OnDismissListener
            public final void onDismiss() {
                this.f$0.m1861lambda$new$1$miuixinternalwidgetListPopup();
            }
        });
        this.mOffsetFromStatusBar = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_context_menu_window_margin_statusbar);
        this.mShadowColor = this.mContext.getResources().getColor(R.color.miuix_appcompat_drop_down_menu_spot_shadow_color);
        if (MiShadowUtils.SUPPORT_MI_SHADOW) {
            this.mElevation = (int) (f * 32.0f);
        } else {
            this.mElevation = AttributeResolver.resolveDimensionPixelSize(this.mContext, R.attr.popupWindowElevation);
            this.mElevationExtra = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_menu_popup_extra_elevation);
        }
    }

    /* JADX INFO: renamed from: lambda$new$0$miuix-internal-widget-ListPopup, reason: not valid java name */
    /* synthetic */ void m1860lambda$new$0$miuixinternalwidgetListPopup(View view) {
        dismiss();
    }

    /* JADX INFO: renamed from: lambda$new$1$miuix-internal-widget-ListPopup, reason: not valid java name */
    /* synthetic */ void m1861lambda$new$1$miuixinternalwidgetListPopup() {
        PopupWindow.OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    protected void prepareContentView(Context context) {
        super.setContentView(this.mRootView);
    }

    protected void setPopupWindowContentView(View view) {
        this.mIsCustomContent = true;
        super.setContentView(view);
    }

    private class ContainerView extends FrameLayout {
        public ContainerView(Context context) {
            super(context);
        }

        public ContainerView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public ContainerView(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            ListPopup.this.configurationChanged(configuration);
        }
    }

    @Override // android.widget.PopupWindow
    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void configurationChanged(Configuration configuration) {
        this.mRootView.post(new Runnable() { // from class: miuix.internal.widget.ListPopup.2
            @Override // java.lang.Runnable
            public void run() {
                if (ListPopup.this.mRootView == null || !ListPopup.this.mRootView.isAttachedToWindow()) {
                    return;
                }
                ListPopup.this.updatePosition(ListPopup.this.mAnchor != null ? (View) ListPopup.this.mAnchor.get() : null);
            }
        });
    }

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

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void show(View view, ViewGroup viewGroup) {
        if (view == null) {
            return;
        }
        View decorView = getDecorView(view);
        Rect rect = new Rect();
        ViewUtils.getBoundsInWindow(decorView, rect);
        updateSafeInsetsByDecor(decorView, rect, getWindowDecorActualBounds(), getWindowDecorVisibleBounds(view));
        if (prepareShow(view, viewGroup, rect)) {
            showWithAnchor(view, rect);
        }
        prepareWindowElevation(this.mContentView, this.mElevation + this.mElevationExtra);
        this.mRootView.setElevation(0.0f);
    }

    private void updateSafeInsetsByDecor(View view, Rect rect, Rect rect2, Rect rect3) {
        int i;
        int i2;
        int i3;
        int i4;
        DisplayCutout displayCutout;
        WindowInsets rootWindowInsets = view.getRootWindowInsets();
        if (rootWindowInsets != null) {
            if (Build.VERSION.SDK_INT >= 30) {
                Insets insets = rootWindowInsets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                this.mPositionSafeInsets.set(insets.left, insets.top, insets.right, insets.bottom);
            } else {
                Rect rect4 = new Rect();
                if (Build.VERSION.SDK_INT >= 28 && (displayCutout = rootWindowInsets.getDisplayCutout()) != null) {
                    rect4.set(displayCutout.getSafeInsetLeft(), displayCutout.getSafeInsetTop(), displayCutout.getSafeInsetRight(), displayCutout.getSafeInsetBottom());
                }
                this.mPositionSafeInsets.set(Math.max(rect4.left, rootWindowInsets.getSystemWindowInsetLeft()), Math.max(rect4.top, rootWindowInsets.getSystemWindowInsetTop()), Math.max(rect4.right, rootWindowInsets.getSystemWindowInsetRight()), Math.max(rect4.bottom, rootWindowInsets.getSystemWindowInsetBottom()));
            }
        }
        if (Build.VERSION.SDK_INT >= 30) {
            i = rect3.left - rect2.left;
            i3 = rect2.right - rect3.right;
            i4 = rect3.top - rect2.top;
            i2 = rect2.bottom - rect3.bottom;
        } else {
            i = 0;
            i2 = 0;
            i3 = 0;
            i4 = 0;
        }
        Rect rect5 = this.mPositionSafeInsets;
        rect5.left = Math.max(this.mMinSafeInset, (rect5.left - rect.left) - i);
        Rect rect6 = this.mPositionSafeInsets;
        rect6.right = Math.max(this.mMinSafeInset, (rect6.right - Math.max(0, rect3.width() - rect.right)) - i3);
        Rect rect7 = this.mPositionSafeInsets;
        rect7.top = Math.max(this.mMinSafeInset, (rect7.top - rect.top) - i4);
        Rect rect8 = this.mPositionSafeInsets;
        rect8.bottom = Math.max(this.mMinSafeInset, (rect8.bottom - Math.max(0, rect3.height() - rect.bottom)) - i2);
    }

    @Override // android.widget.PopupWindow
    public void update(int i, int i2, int i3, int i4, boolean z) {
        KeyEvent.Callback anchor = getAnchor();
        if ((anchor instanceof ViewHoverListener) && ((ViewHoverListener) anchor).isHover()) {
            LogUtils.debug("popupWindow update return", anchor);
        } else {
            LogUtils.debug("popupWindow update execute", anchor);
            super.update(i, i2, i3, i4, z);
        }
    }

    protected boolean isNeedScroll(int i, Rect rect) {
        int iCheckMaxHeight = checkMaxHeight(rect);
        int i2 = this.mContentSize.mHeight;
        return i2 > i || i2 > iCheckMaxHeight;
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

    protected boolean prepareShow(View view, ViewGroup viewGroup, Rect rect) {
        if (view == null) {
            Log.e(TAG, "show: anchor is null");
            return false;
        }
        Log.d("ListPopup", "prepareShow");
        if (shouldSetElevation()) {
            setElevation(this.mElevation + this.mElevationExtra);
        }
        if (this.mContentView == null) {
            this.mContentView = LayoutInflater.from(this.mContext).inflate(R.layout.miuix_appcompat_list_popup_list, (ViewGroup) null);
            Drawable drawableResolveDrawable = AttributeResolver.resolveDrawable(this.mContext, R.attr.immersionWindowBackground);
            if (drawableResolveDrawable != null) {
                drawableResolveDrawable.getPadding(this.mBackgroundPadding);
                this.mContentView.setBackground(drawableResolveDrawable);
            }
            this.mContentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: miuix.internal.widget.ListPopup.3
                private int lastContentHeight = -1;

                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    boolean zIsNeedScroll;
                    int measuredHeight = ListPopup.this.mContentView.getMeasuredHeight();
                    int i9 = this.lastContentHeight;
                    if (i9 == -1 || i9 != measuredHeight) {
                        if (ListPopup.this.mListView.getAdapter() != null) {
                            View anchor = ListPopup.this.getAnchor();
                            Rect rect2 = new Rect();
                            if (anchor == null) {
                                WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(ListPopup.this.mContext);
                                rect2.set(0, 0, windowInfo.windowSize.x, windowInfo.windowSize.y);
                            } else {
                                ViewUtils.getBoundsInWindow(ListPopup.this.getDecorView(anchor), rect2);
                            }
                            zIsNeedScroll = ListPopup.this.isNeedScroll(i4 - i2, rect2);
                        } else {
                            zIsNeedScroll = true;
                        }
                        ListPopup.this.mContentView.setEnabled(zIsNeedScroll);
                        ListPopup.this.mListView.setVerticalScrollBarEnabled(zIsNeedScroll);
                        this.lastContentHeight = measuredHeight;
                    }
                }
            });
            this.mIsCustomContent = false;
        }
        if (this.mRootView.getChildCount() != 1 || this.mRootView.getChildAt(0) != this.mContentView) {
            this.mRootView.removeAllViews();
            this.mRootView.addView(this.mContentView);
            if (this.mIsCustomContent) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mContentView.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = -2;
                layoutParams.gravity = 16;
            }
        }
        ListView listView = (ListView) this.mContentView.findViewById(android.R.id.list);
        this.mListView = listView;
        if (listView == null) {
            Log.e(TAG, "list not found");
            return false;
        }
        listView.setOnTouchListener(new AnonymousClass4());
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.internal.widget.ListPopup$$ExternalSyntheticLambda2
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view2, int i, long j) {
                this.f$0.m1862lambda$prepareShow$2$miuixinternalwidgetListPopup(adapterView, view2, i, j);
            }
        });
        this.mListView.setAdapter(this.mAdapter);
        setWidth(computePopupContentWidth(rect));
        int iCheckMaxHeight = checkMaxHeight(rect);
        setHeight(iCheckMaxHeight > 0 ? Math.min(this.mContentSize.mHeight, iCheckMaxHeight) : -2);
        ((InputMethodManager) this.mContext.getApplicationContext().getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
        return true;
    }

    /* JADX INFO: renamed from: miuix.internal.widget.ListPopup$4, reason: invalid class name */
    class AnonymousClass4 implements View.OnTouchListener {
        int lastIndex = -1;

        AnonymousClass4() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            int firstVisiblePosition;
            int i;
            int iPointToPosition = ListPopup.this.mListView.pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action == 1 || action == 3 || action == 6) {
                    this.lastIndex = -1;
                    ListPopup.this.mListView.postDelayed(new Runnable() { // from class: miuix.internal.widget.ListPopup$4$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ListPopup.AnonymousClass4.lambda$onTouch$0(view);
                        }
                    }, ViewConfiguration.getPressedStateDuration());
                }
            } else if (iPointToPosition != -1 && (firstVisiblePosition = iPointToPosition - ListPopup.this.mListView.getFirstVisiblePosition()) != (i = this.lastIndex)) {
                if (i != -1) {
                    ListPopup.this.mListView.getChildAt(this.lastIndex).setPressed(false);
                }
                ListPopup.this.mListView.getChildAt(firstVisiblePosition).setPressed(true);
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
                    Log.e(ListPopup.TAG, "list onTouch error " + e);
                }
            }
        }
    }

    /* JADX INFO: renamed from: lambda$prepareShow$2$miuix-internal-widget-ListPopup, reason: not valid java name */
    /* synthetic */ void m1862lambda$prepareShow$2$miuixinternalwidgetListPopup(AdapterView adapterView, View view, int i, long j) {
        int headerViewsCount = i - this.mListView.getHeaderViewsCount();
        if (this.mOnItemClickListener == null || headerViewsCount < 0 || headerViewsCount >= this.mAdapter.getCount()) {
            return;
        }
        this.mOnItemClickListener.onItemClick(adapterView, view, headerViewsCount, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePosition(View view) {
        if (view == null) {
            return;
        }
        View decorView = getDecorView(view);
        Rect rect = new Rect();
        ViewUtils.getBoundsInWindow(decorView, rect);
        updateSafeInsetsByDecor(decorView, rect, getWindowDecorActualBounds(), getWindowDecorVisibleBounds(view));
        int iCheckMaxHeight = checkMaxHeight(rect);
        int iComputePopupContentWidth = computePopupContentWidth(rect);
        int i = (iCheckMaxHeight <= 0 || this.mContentSize.mHeight <= iCheckMaxHeight) ? this.mContentSize.mHeight : iCheckMaxHeight;
        Rect rect2 = new Rect();
        ViewUtils.getBoundsInWindow(view, rect2);
        update(view, calculateXoffset(view.getLayoutDirection(), rect2, rect), calculateYoffset(rect2, rect), iComputePopupContentWidth, i);
    }

    private boolean shouldSetElevation() {
        return this.mHasShadow && (Build.VERSION.SDK_INT > 29 || !AccessibilityUtil.isTalkBackActive(this.mContext));
    }

    public void setContentWidth(int i) {
        this.mContentSize.updateWidth(i);
    }

    public void setContentHeight(int i) {
        this.mContentSize.mHeight = i;
    }

    public void setDropDownGravity(int i) {
        this.mDropDownGravity = i;
    }

    public void setFenceDecor(View view) {
        this.mFenceDecor = new WeakReference<>(view);
    }

    public ListView getListView() {
        return this.mListView;
    }

    public void setVerticalOffset(int i) {
        this.mOffsetY = i;
        this.mOffsetYSet = true;
    }

    public void setHorizontalOffset(int i) {
        this.mOffsetX = i;
        this.mOffsetXSet = true;
    }

    public void setHasShadow(boolean z) {
        this.mHasShadow = z;
    }

    public int getMinMarginScreen() {
        return this.mMinSafeInset;
    }

    public Rect getPositionSafeInsets() {
        return this.mPositionSafeInsets;
    }

    public int getOffsetFromStatusBar() {
        return this.mOffsetFromStatusBar;
    }

    public int getVerticalOffset() {
        return this.mOffsetY;
    }

    public int getHorizontalOffset() {
        return this.mOffsetX;
    }

    protected int computePopupContentWidth(Rect rect) {
        if (!this.mContentSize.mHasContentWidth) {
            measureContentSize(this.mAdapter, null, this.mContext, checkMaxWidth(rect));
        }
        int iMax = Math.max(this.mContentSize.mWidth, checkMinWidth(rect)) + this.mBackgroundPadding.left + this.mBackgroundPadding.right;
        this.mContentSize.updateWidth(iMax);
        return iMax;
    }

    private Rect getWindowDecorVisibleBounds(View view) {
        Rect rect = new Rect();
        View rootView = view.getRootView();
        if (rootView != null) {
            view = rootView;
        }
        view.getWindowVisibleDisplayFrame(rect);
        return rect;
    }

    protected Rect getWindowDecorActualBounds() {
        Rect rect = new Rect();
        WindowUtils.getWindowBounds(WindowUtils.getWindowManager(this.mContext), this.mContext, rect);
        return rect;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getDecorView(View view) {
        View view2 = this.mFenceDecor.get();
        return view2 != null ? view2 : view.getRootView();
    }

    private void showWithAnchor(View view, Rect rect) {
        view.getLocationInWindow(new int[2]);
        Rect rect2 = new Rect();
        ViewUtils.getBoundsInWindow(view, rect2);
        int iCalculateYoffset = calculateYoffset(rect2, rect);
        int iCalculateXoffset = calculateXoffset(view.getLayoutDirection(), rect2, rect);
        int width = getWidth() > 0 ? getWidth() : this.mContentSize.mWidth;
        int height = getHeight() > 0 ? getHeight() : this.mContentSize.mHeight;
        Rect rect3 = new Rect();
        rect3.set(0, 0, width, height);
        Log.d("ListPopup", "showWithAnchor getWidth " + getWidth() + " getHeight " + getHeight());
        int absoluteGravity = Gravity.getAbsoluteGravity(this.mDropDownGravity, view.getLayoutDirection());
        int i = absoluteGravity & 112;
        if ((absoluteGravity & 7) == 5) {
            rect3.offsetTo((rect2.right + iCalculateXoffset) - rect3.width(), rect2.bottom + iCalculateYoffset);
        } else {
            rect3.offsetTo(rect2.left + iCalculateXoffset, rect2.bottom + iCalculateYoffset);
        }
        int i2 = 48;
        if (Math.abs(rect3.centerY() - rect2.centerY()) <= 10 ? i == 80 : rect3.centerY() <= rect2.centerY()) {
            i2 = 80;
        }
        if (Math.abs(rect3.centerX() - rect2.centerX()) > 10) {
            i2 = rect3.centerX() > rect2.centerX() ? i2 | 3 : i2 | 5;
        }
        int i3 = this.mUserAnimationGravity;
        if (i3 != -1) {
            setAnimationStyleByGravity(i3);
        } else {
            setAnimationStyleByGravity(i2);
        }
        if (!isShowing()) {
            HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_BUTTON_SMALL, HapticFeedbackConstants.MIUI_POPUP_NORMAL);
        }
        showAsDropDown(view, iCalculateXoffset, iCalculateYoffset, this.mDropDownGravity);
        changeWindowBackground(this.mRootView.getRootView());
    }

    @Override // android.widget.PopupWindow
    public void showAsDropDown(View view, int i, int i2, int i3) {
        super.showAsDropDown(view, i, i2, i3);
        this.mAnchor = new WeakReference<>(view);
        SinglePopControl.showPop(this.mContext, this);
    }

    @Override // android.widget.PopupWindow
    public void showAtLocation(View view, int i, int i2, int i3) {
        int i4;
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        int width = getWidth() > 0 ? getWidth() : this.mContentSize.mWidth;
        int height = getHeight() > 0 ? getHeight() : this.mContentSize.mHeight;
        Rect rect2 = new Rect();
        rect2.set(i2, i3, width + i2, height + i3);
        Log.d("ListPopup", "showAtLocation getWidth " + getWidth() + " getHeight " + getHeight());
        if (rect2.top > rect.centerY()) {
            i4 = 48;
        } else {
            i4 = rect2.bottom <= rect.centerY() ? 80 : 0;
        }
        if (rect2.left >= rect.left && rect2.right > rect.right) {
            i4 |= 3;
        } else if (rect2.right <= rect.right && rect2.left < rect.left) {
            i4 |= 5;
        }
        if (i4 == 0 && rect.contains(rect2)) {
            i4 = 17;
        }
        int i5 = this.mUserAnimationGravity;
        if (i5 != -1) {
            setAnimationStyleByGravity(i5);
        } else {
            setAnimationStyleByGravity(i4);
        }
        super.showAtLocation(view, i, i2, i3);
        prepareWindowElevation(this.mContentView, this.mElevation + this.mElevationExtra);
        this.mRootView.setElevation(0.0f);
        SinglePopControl.showPop(this.mContext, this);
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        super.dismiss();
        SinglePopControl.hidePop(this.mContext, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getAnchor() {
        WeakReference<View> weakReference = this.mAnchor;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    private void setAnimationStyleByGravity(int i) {
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
        setAnimationStyle(i2);
    }

    private int calculateYoffset(Rect rect, Rect rect2) {
        int i = this.mOffsetYSet ? this.mOffsetY : (-rect.height()) - this.mBackgroundPadding.top;
        int iCheckMaxHeight = checkMaxHeight(rect2);
        int iMin = iCheckMaxHeight > 0 ? Math.min(this.mContentSize.mHeight, iCheckMaxHeight) : this.mContentSize.mHeight;
        int i2 = (rect2.bottom - this.mPositionSafeInsets.bottom) - rect.bottom;
        int i3 = (rect.top - this.mPositionSafeInsets.bottom) - rect2.top;
        if (iMin + i > i2) {
            int iHeight = 0;
            if (i2 >= i3) {
                if (this.mOffsetYSet) {
                    iHeight = rect.height();
                }
            } else {
                iHeight = (this.mOffsetYSet ? rect.height() : 0) + iMin;
            }
            i -= iHeight;
        }
        int i4 = rect.bottom + i;
        if (i4 < rect2.top + this.mPositionSafeInsets.top) {
            int i5 = (rect2.top + this.mPositionSafeInsets.top) - i4;
            setHeight(iMin - i5);
            i += i5;
        }
        int i6 = i4 + iMin;
        if (i6 > rect2.bottom - this.mPositionSafeInsets.bottom) {
            setHeight(iMin - (i6 - (rect2.bottom - this.mPositionSafeInsets.bottom)));
        }
        return i;
    }

    private int calculateXoffset(int i, Rect rect, Rect rect2) {
        int absoluteGravity = Gravity.getAbsoluteGravity(this.mDropDownGravity, i) & 7;
        if (absoluteGravity == 1) {
            return calculateXoffsetAlignCenterHorizontal(rect, rect2);
        }
        if (absoluteGravity == 5) {
            return calculateXoffsetAlignRight(rect, rect2);
        }
        return calculateXoffsetAlignLeft(rect, rect2);
    }

    private int calculateXoffsetAlignLeft(Rect rect, Rect rect2) {
        boolean z;
        int i;
        int i2 = rect.left + (this.mOffsetXSet ? this.mOffsetX : 0) + this.mContentSize.mWidth;
        if (i2 > rect2.right - this.mPositionSafeInsets.right) {
            i = (rect2.right - this.mPositionSafeInsets.right) - i2;
            z = true;
        } else {
            z = false;
            i = 0;
        }
        if (z) {
            return i;
        }
        int i3 = this.mOffsetXSet ? this.mOffsetX : 0;
        int i4 = rect.left + i3;
        int i5 = i4 < rect2.left + this.mPositionSafeInsets.left ? (rect2.left + this.mPositionSafeInsets.left) - i4 : i3;
        return i5 != 0 ? i5 - this.mBackgroundPadding.left : i5;
    }

    private int calculateXoffsetAlignRight(Rect rect, Rect rect2) {
        boolean z;
        int i;
        int i2 = (rect.right + (this.mOffsetXSet ? this.mOffsetX : 0)) - this.mContentSize.mWidth;
        if (i2 < rect2.left + this.mPositionSafeInsets.left) {
            i = (rect2.left + this.mPositionSafeInsets.left) - i2;
            z = true;
        } else {
            z = false;
            i = 0;
        }
        if (z) {
            return i;
        }
        int i3 = this.mOffsetXSet ? this.mOffsetX : 0;
        int i4 = rect.right + i3;
        int i5 = i4 > rect2.right - this.mPositionSafeInsets.right ? (rect2.right - this.mPositionSafeInsets.right) - i4 : i3;
        return i5 != 0 ? i5 + this.mBackgroundPadding.right : i5;
    }

    private int calculateXoffsetAlignCenterHorizontal(Rect rect, Rect rect2) {
        int i;
        boolean z;
        int iCenterX = rect.centerX();
        int i2 = rect.left;
        int i3 = this.mContentSize.mWidth + i2;
        int i4 = (this.mContentSize.mWidth / 2) + i2;
        if (i3 > rect2.right - this.mPositionSafeInsets.right) {
            i = (rect2.right - this.mPositionSafeInsets.right) - i3;
            z = true;
        } else {
            i = 0;
            z = false;
        }
        if (z) {
            return i;
        }
        int i5 = iCenterX - i4;
        return i2 + i5 >= rect2.left + this.mPositionSafeInsets.left ? i5 : i;
    }

    public void fastShow(View view, ViewGroup viewGroup) {
        View decorView = getDecorView(view);
        Rect rect = new Rect();
        ViewUtils.getBoundsInWindow(decorView, rect);
        setWidth(computePopupContentWidth(rect));
        int i = this.mContentSize.mHeight;
        int iCheckMaxHeight = checkMaxHeight(rect);
        if (i > iCheckMaxHeight) {
            i = iCheckMaxHeight;
        }
        setHeight(i);
        showWithAnchor(view, rect);
    }

    private void measureContentSize(ListAdapter listAdapter, ViewGroup viewGroup, Context context, int i) {
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
        int count = listAdapter.getCount();
        int i2 = 0;
        int i3 = 0;
        int measuredHeight = 0;
        View view = null;
        for (int i4 = 0; i4 < count; i4++) {
            int itemViewType = listAdapter.getItemViewType(i4);
            if (itemViewType != i2) {
                view = null;
                i2 = itemViewType;
            }
            if (viewGroup == null) {
                viewGroup = new FrameLayout(context);
            }
            view = listAdapter.getView(i4, view, viewGroup);
            view.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
            measuredHeight += view.getMeasuredHeight();
            if (!this.mContentSize.mHasContentWidth) {
                int measuredWidth = view.getMeasuredWidth();
                if (measuredWidth >= i) {
                    this.mContentSize.updateWidth(i);
                } else if (measuredWidth > i3) {
                    i3 = measuredWidth;
                }
            }
        }
        if (!this.mContentSize.mHasContentWidth) {
            this.mContentSize.updateWidth(i3);
        }
        this.mContentSize.mHeight = measuredHeight;
    }

    protected void setPopupShadowAlpha(View view) {
        if (EnvStateManager.isFreeFormMode(this.mContext)) {
            view.setOutlineProvider(null);
            return;
        }
        view.setOutlineProvider(new ViewOutlineProvider() { // from class: miuix.internal.widget.ListPopup.5
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

    public static void changeWindowBackground(View view) {
        WindowManager.LayoutParams layoutParams;
        if (view == null || (layoutParams = (WindowManager.LayoutParams) view.getLayoutParams()) == null) {
            return;
        }
        layoutParams.flags |= 2;
        layoutParams.dimAmount = ViewUtils.isNightMode(view.getContext()) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT;
        ((WindowManager) view.getContext().getSystemService("window")).updateViewLayout(view, layoutParams);
    }

    public void setMaxAllowedHeight(int i) {
        this.mMaxAllowedHeight = i;
    }

    protected int checkMaxHeight(Rect rect) {
        return Math.min(this.mMaxAllowedHeight, (rect.height() - this.mPositionSafeInsets.top) - this.mPositionSafeInsets.bottom);
    }

    protected int checkMaxWidth(Rect rect) {
        return Math.min(this.mMaxAllowedWidth, (rect.width() - this.mPositionSafeInsets.left) - this.mPositionSafeInsets.right);
    }

    protected int checkMinWidth(Rect rect) {
        return Math.min(this.mMinAllowedWidth, (rect.width() - this.mPositionSafeInsets.left) - this.mPositionSafeInsets.right);
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
