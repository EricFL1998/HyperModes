package miuix.appcompat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.FolmeObject;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.ViewProperty;
import miuix.appcompat.R;
import miuix.appcompat.view.menu.HyperBaseAdapter;
import miuix.appcompat.view.menu.HyperMenuAdapter;
import miuix.appcompat.view.menu.HyperMenuContract;
import miuix.appcompat.view.menu.HyperSecondaryAdapter;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.popupwidget.internal.strategy.IPopupWindowStrategy;
import miuix.popupwidget.internal.strategy.PopupWindowSpec;
import miuix.popupwidget.internal.strategy.PopupWindowStrategy;
import miuix.popupwidget.internal.util.SinglePopControl;
import miuix.popupwidget.widget.PopupAnimHelper;
import miuix.popupwidget.widget.PopupWindow;
import miuix.smooth.SmoothContainerDrawable2;
import miuix.smooth.SmoothFrameLayout2;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
public class HyperPopupWindow extends PopupWindow {
    private static final String TAG = "HyperPopupWindow";
    private int mAnimationExtensionMargin;
    private ClipLayout mClipView;
    private ViewGroup mContainer;
    private final float mCornerRadius;
    private boolean mEnableFolmeAnimation;
    private boolean mEnableSecondaryMenu;
    private View mFocusedMainPopupItemView;
    private ClipLayout mInnerClip;
    private PopupContentHolder mMainPopContentHolder;
    protected IPopupWindowStrategy mMainPopupStrategy;
    private OnMenuItemClickListener mMenuItemClickListener;
    private Rect mRootBounds;
    private PopupContentHolder mSecondaryContentHolder;
    private IPopupWindowStrategy mSecondaryPopupStrategy;

    public interface OnMenuItemClickListener {
        void onMenuItemClick(MenuItem menuItem);
    }

    public HyperPopupWindow(Context context) {
        this(context, null);
    }

    public HyperPopupWindow(Context context, View view) {
        super(context, view);
        this.mEnableSecondaryMenu = true;
        this.mAnimationExtensionMargin = 35;
        setAutoDismiss(true);
        this.mMainPopupStrategy = new PopupWindowStrategy();
        this.mMainPopContentHolder = new PopupContentHolder(this.mContext, this.mMainPopupStrategy);
        this.mCornerRadius = this.mContext.getResources().getDimension(R.dimen.miuix_appcompat_drop_down_menu_radius);
    }

    @Override // miuix.popupwidget.widget.PopupWindow
    public void setAdapter(ListAdapter listAdapter) {
        this.mMainPopContentHolder.setAdapter(listAdapter);
    }

    @Override // miuix.popupwidget.widget.PopupWindow
    protected void prepareContentView() {
        FrameLayout frameLayout = this.mRootView;
        int i = this.mAnimationExtensionMargin;
        frameLayout.setPadding(i, i, i, i);
        super.prepareContentView();
    }

    @Override // android.widget.PopupWindow
    public void setClippingEnabled(boolean z) {
        if (this.mRootView != null) {
            if (z) {
                this.mAnimationExtensionMargin = 0;
            } else {
                this.mAnimationExtensionMargin = 35;
            }
            FrameLayout frameLayout = this.mRootView;
            int i = this.mAnimationExtensionMargin;
            frameLayout.setPadding(i, i, i, i);
        }
        super.setClippingEnabled(z);
    }

    @Override // miuix.popupwidget.widget.PopupWindow
    public void show(View view) {
        setAnchorView(view);
        updatePopupWindowSpec(this.mPopupWindowSpec);
        this.mRootBounds = getRootBounds();
        if (this.mContainer == null) {
            this.mContainer = new FrameLayout(this.mContext);
            this.mContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
            superSetContentViewWithoutClip(this.mContainer);
            this.mContainer.setLayoutDirection(0);
            this.mContainer.setClipChildren(false);
            this.mContainer.setClipToPadding(false);
            ((ViewGroup) this.mContainer.getParent()).setClipChildren(false);
            ((ViewGroup) this.mContainer.getParent()).setClipToPadding(false);
        }
        this.mMainPopContentHolder.mPopupWindowSpec = this.mPopupWindowSpec;
        this.mMainPopContentHolder.inflate();
        this.mMainPopContentHolder.setMenuListAccessibilityDelegate();
        this.mMainPopContentHolder.show(view, this.mContainer, this.mRootBounds, false);
        this.mMainPopContentHolder.setItemClickListener(new AnonymousClass1());
        if (this.mEnableSecondaryMenu) {
            int iWidth = this.mRootBounds.width();
            int iHeight = this.mRootBounds.height();
            setWidth(iWidth + (this.mAnimationExtensionMargin * 2));
            setHeight(iHeight + (this.mAnimationExtensionMargin * 2));
            showAtLocation(view, 0, this.mRootBounds.left - this.mAnimationExtensionMargin, this.mRootBounds.top - this.mAnimationExtensionMargin, this.mMainPopContentHolder.mBoundsRect);
            return;
        }
        Rect rect = this.mMainPopContentHolder.mBoundsRect;
        int iWidth2 = rect.width();
        int iHeight2 = rect.height();
        setWidth(iWidth2 + (this.mAnimationExtensionMargin * 2));
        setHeight(iHeight2 + (this.mAnimationExtensionMargin * 2));
        showAtLocation(view, 0, rect.left - this.mAnimationExtensionMargin, rect.top - this.mAnimationExtensionMargin, rect);
    }

    /* JADX INFO: renamed from: miuix.appcompat.widget.HyperPopupWindow$1, reason: invalid class name */
    class AnonymousClass1 implements AdapterView.OnItemClickListener {
        AnonymousClass1() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            BaseAdapter secondaryAdapterByItemId;
            if (HyperPopupWindow.this.mMainPopContentHolder.mAdapter instanceof HyperMenuAdapter) {
                secondaryAdapterByItemId = ((HyperMenuAdapter) HyperPopupWindow.this.mMainPopContentHolder.mAdapter).getSecondaryAdapterByItemId(j);
                HyperPopupWindow hyperPopupWindow = HyperPopupWindow.this;
                HyperMenuContract.HyperMenuTextItem textItem = hyperPopupWindow.getTextItem(hyperPopupWindow.mMainPopContentHolder.mAdapter, i);
                if (textItem != null && !textItem.isExpandable) {
                    ((HyperMenuAdapter) HyperPopupWindow.this.mMainPopContentHolder.mAdapter).resumePrimaryItemClickStatus((int) j, i);
                }
            } else {
                secondaryAdapterByItemId = null;
            }
            if (HyperPopupWindow.this.mSecondaryContentHolder == null) {
                if (secondaryAdapterByItemId != null) {
                    expandAndHandleSecondaryItemClick(secondaryAdapterByItemId, view);
                } else {
                    handlePrimaryItemClick(i);
                }
                HyperPopupWindow.this.mContainer.findViewById(R.id.mask).setOnClickListener(new View.OnClickListener() { // from class: miuix.appcompat.widget.HyperPopupWindow$1$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.m1843lambda$onItemClick$0$miuixappcompatwidgetHyperPopupWindow$1(view2);
                    }
                });
            }
        }

        /* JADX INFO: renamed from: lambda$onItemClick$0$miuix-appcompat-widget-HyperPopupWindow$1, reason: not valid java name */
        /* synthetic */ void m1843lambda$onItemClick$0$miuixappcompatwidgetHyperPopupWindow$1(View view) {
            HyperPopupWindow.this.collapseSecondaryMenu();
        }

        private void expandAndHandleSecondaryItemClick(final ListAdapter listAdapter, View view) {
            HyperPopupWindow.this.mSecondaryPopupStrategy = new SecondaryPopupWindowStrategy();
            HyperPopupWindow.this.expandSecondaryMenu(view, listAdapter);
            HyperPopupWindow.this.mSecondaryContentHolder.setItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.appcompat.widget.HyperPopupWindow$1$$ExternalSyntheticLambda0
                @Override // android.widget.AdapterView.OnItemClickListener
                public final void onItemClick(AdapterView adapterView, View view2, int i, long j) {
                    this.f$0.m1842x2eb0f909(listAdapter, adapterView, view2, i, j);
                }
            });
        }

        /* JADX INFO: renamed from: lambda$expandAndHandleSecondaryItemClick$1$miuix-appcompat-widget-HyperPopupWindow$1, reason: not valid java name */
        /* synthetic */ void m1842x2eb0f909(ListAdapter listAdapter, AdapterView adapterView, View view, int i, long j) {
            HyperMenuContract.HyperMenuTextItem textItem;
            if (view.getId() == R.id.tag_secondary_popup_menu_item_head) {
                HyperPopupWindow.this.collapseSecondaryMenu();
                return;
            }
            if ((listAdapter instanceof HyperSecondaryAdapter) && (textItem = HyperPopupWindow.this.getTextItem(listAdapter, i)) != null && !textItem.isHeaderItem) {
                ((HyperSecondaryAdapter) listAdapter).resumeSecondaryItemClickStatus((int) j);
            }
            MenuItem menuItem = (MenuItem) listAdapter.getItem(i);
            if (HyperPopupWindow.this.mMenuItemClickListener != null) {
                HyperPopupWindow.this.mMenuItemClickListener.onMenuItemClick(menuItem);
            }
            if (menuItem == null || !menuItem.isEnabled() || !HyperPopupWindow.this.isShowing() || HyperPopupWindow.this.mIsTransitioningToDismiss) {
                return;
            }
            HyperPopupWindow.this.dismiss();
        }

        private void handlePrimaryItemClick(int i) {
            MenuItem menuItem = (HyperPopupWindow.this.mMainPopContentHolder == null || HyperPopupWindow.this.mMainPopContentHolder.mAdapter == null) ? null : (MenuItem) HyperPopupWindow.this.mMainPopContentHolder.mAdapter.getItem(i);
            if (HyperPopupWindow.this.mMenuItemClickListener != null) {
                HyperPopupWindow.this.mMenuItemClickListener.onMenuItemClick(menuItem);
            }
            if (menuItem == null || !menuItem.isEnabled() || !HyperPopupWindow.this.isShowing() || HyperPopupWindow.this.mIsTransitioningToDismiss) {
                return;
            }
            HyperPopupWindow.this.dismiss();
        }
    }

    @Override // android.widget.PopupWindow
    public void update() {
        this.mMainPopContentHolder.update();
    }

    @Override // miuix.popupwidget.widget.PopupWindow
    protected void updateLocation(View view) {
        this.mMainPopContentHolder.update();
    }

    @Override // miuix.popupwidget.widget.PopupWindow
    public void setPopupWindowStrategy(IPopupWindowStrategy iPopupWindowStrategy) {
        this.mMainPopupStrategy = iPopupWindowStrategy;
        this.mMainPopContentHolder.mPopupWindowStrategy = iPopupWindowStrategy;
    }

    public void setSecondaryMenuEnabled(boolean z) {
        this.mEnableSecondaryMenu = z;
    }

    public boolean getSecondaryMenuEnabled() {
        return this.mEnableSecondaryMenu;
    }

    private void showAtLocation(View view, int i, int i2, int i3, Rect rect) {
        prepareWindowElevation();
        Rect rect2 = new Rect();
        view.getGlobalVisibleRect(rect2);
        int width = getWidth();
        int height = getHeight();
        if (rect == null) {
            rect = new Rect();
            rect.set(i2, i3, width + i2, height + i3);
        }
        showWithAnim(computeGravity(rect2, rect, 0, view.getLayoutDirection()));
        if (!isShowing()) {
            HapticCompat.performHapticFeedback(this.mRootView, HapticFeedbackConstants.MIUI_BUTTON_SMALL, HapticFeedbackConstants.MIUI_POPUP_NORMAL);
        }
        superShowAtLocation(view, i, i2, i3);
        this.mRootView.setElevation(0.0f);
        if (this.mWindowAnimationEnabled || this.mAnimHelper == null) {
            changeWindowBackground(this.mRootView.getRootView());
        }
        SinglePopControl.showPop(this.mContext, this);
    }

    @Override // miuix.popupwidget.widget.PopupWindow
    protected void showWithAnim(int i) {
        if (Build.VERSION.SDK_INT >= 29 && !this.mWindowAnimationEnabled && this.mAnimHelper == null) {
            this.mAnimHelper = new PopupAnimHelper((View) this.mContentView.findViewById(R.id.spring_back).getParent());
        }
        super.showWithAnim(i);
    }

    private void prepareWindowElevation() {
        if (shouldSetElevation()) {
            setElevation(this.mElevation + this.mElevationExtra);
        }
        prepareWindowElevation(this.mMainPopContentHolder.mContentView, this.mElevation + this.mElevationExtra);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mMenuItemClickListener = onMenuItemClickListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public HyperMenuContract.HyperMenuTextItem getTextItem(Object obj, int i) {
        HyperMenuContract.HyperMenuItem hyperMenuItem = obj instanceof HyperBaseAdapter ? ((HyperBaseAdapter) obj).getHyperMenuItem(i) : null;
        if (hyperMenuItem instanceof HyperMenuContract.HyperMenuTextItem) {
            return (HyperMenuContract.HyperMenuTextItem) hyperMenuItem;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void expandSecondaryMenu(View view, ListAdapter listAdapter) {
        toBackAnim();
        doExpandAnimation(view, listAdapter);
        disableMainMenuAccessibility(this.mMainPopContentHolder.mContentView);
        announceForSecondaryMenu(this.mContext.getResources().getString(R.string.miuix_appcompat_accessibility_expand_state));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void collapseSecondaryMenu() {
        PopupContentHolder popupContentHolder = this.mSecondaryContentHolder;
        if (popupContentHolder == null) {
            return;
        }
        popupContentHolder.mContentView.findViewById(R.id.mask).setVisibility(0);
        toFrontAnim();
        doCollapseAnimation();
        this.mSecondaryContentHolder = null;
        enableMainMenuAccessibility(this.mMainPopContentHolder.mContentView);
        announceForSecondaryMenu(this.mContext.getResources().getString(R.string.miuix_appcompat_accessibility_collapse_state));
    }

    private void enableAccessibility(View view, boolean z) {
        view.setImportantForAccessibility(z ? 1 : 2);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                enableAccessibility(viewGroup.getChildAt(i), z);
            }
        }
    }

    private void enableMainMenuAccessibility(View view) {
        if (view != null) {
            view.setImportantForAccessibility(1);
        }
        View view2 = this.mFocusedMainPopupItemView;
        if (view2 != null) {
            view2.post(new Runnable() { // from class: miuix.appcompat.widget.HyperPopupWindow$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1841x350888c8();
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$enableMainMenuAccessibility$0$miuix-appcompat-widget-HyperPopupWindow, reason: not valid java name */
    /* synthetic */ void m1841x350888c8() {
        this.mFocusedMainPopupItemView.sendAccessibilityEvent(8);
    }

    private void disableMainMenuAccessibility(View view) {
        if (view != null) {
            view.setImportantForAccessibility(4);
        }
    }

    private void announceForSecondaryMenu(String str) {
        if (this.mMainPopContentHolder.mContentView != null) {
            this.mMainPopContentHolder.mContentView.announceForAccessibility(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int[][] getItemViewBounds(ListAdapter listAdapter, ViewGroup viewGroup, Context context, int i, int i2) {
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
        int count = listAdapter.getCount();
        int[][] iArr = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, count, 2);
        View view = null;
        for (int i3 = 0; i3 < count; i3++) {
            if (viewGroup == null) {
                viewGroup = new FrameLayout(context);
            }
            view = listAdapter.getView(i3, view, viewGroup);
            view.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
            int measuredWidth = view.getMeasuredWidth();
            if (i2 != -1) {
                measuredWidth = Math.max(measuredWidth, i2);
            }
            int[] iArr2 = iArr[i3];
            iArr2[0] = measuredWidth;
            iArr2[1] = view.getMeasuredHeight();
        }
        return iArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Rect getRootBounds() {
        Rect rect = new Rect();
        PopupWindowSpec popupWindowSpecClone = this.mPopupWindowSpec.clone();
        rect.set(popupWindowSpecClone.mDecorViewBounds.left + popupWindowSpecClone.mSafeInsets.left, popupWindowSpecClone.mDecorViewBounds.top + popupWindowSpecClone.mSafeInsets.top, popupWindowSpecClone.mDecorViewBounds.right - popupWindowSpecClone.mSafeInsets.right, popupWindowSpecClone.mDecorViewBounds.bottom - popupWindowSpecClone.mSafeInsets.bottom);
        return rect;
    }

    @Override // miuix.popupwidget.widget.PopupWindow
    protected void dismissWithAnim() {
        if (this.mSecondaryContentHolder == null || this.mAnimHelper == null) {
            super.dismissWithAnim();
        } else {
            this.mIsTransitioningToDismiss = true;
            dismissWithAnimForSecondaryMenu();
        }
    }

    private void dismissWithAnimForSecondaryMenu() {
        Rect unionBounds = getUnionBounds(this.mMainPopContentHolder.mBoundsRect, this.mSecondaryContentHolder.mBoundsRect);
        int left = this.mClipView.getLeft();
        int top = this.mClipView.getTop();
        this.mRootView.setLayoutDirection(0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mContainer.getLayoutParams();
        layoutParams.width = unionBounds.width();
        layoutParams.height = unionBounds.height();
        layoutParams.leftMargin += left;
        layoutParams.topMargin += top;
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mMainPopContentHolder.mContentView.getLayoutParams();
        layoutParams2.leftMargin -= left;
        layoutParams2.topMargin -= top;
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mClipView.getLayoutParams();
        layoutParams3.leftMargin -= left;
        layoutParams3.topMargin -= top;
        this.mContainer.requestLayout();
        this.mAnimHelper.doDimAnimation(this.mContainer.getRootView(), false);
        doCollapseAnimation();
        AnimConfig animConfigAddListeners = new AnimConfig().setEase(FolmeEase.linear(150L)).addListeners(new TransitionListener() { // from class: miuix.appcompat.widget.HyperPopupWindow.2
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                HyperPopupWindow.this.realDismiss();
                HyperPopupWindow.this.mIsTransitioningToDismiss = false;
            }
        });
        int animGravity = this.mAnimHelper.getAnimGravity();
        int absoluteGravity = Gravity.getAbsoluteGravity(animGravity, this.mContainer.getLayoutDirection()) & 7;
        int absoluteGravity2 = Gravity.getAbsoluteGravity(animGravity, this.mContainer.getLayoutDirection()) & 112;
        if (absoluteGravity == 3) {
            this.mContainer.setPivotX(0.0f);
        } else {
            this.mContainer.setPivotX(unionBounds.width());
        }
        if (absoluteGravity2 == 48) {
            this.mContainer.setPivotY(0.0f);
        } else {
            this.mContainer.setPivotY(unionBounds.height());
        }
        Folme.use((View) this.mContainer).to(ViewProperty.ALPHA, Float.valueOf(0.0f), animConfigAddListeners);
        Folme.use((View) this.mContainer).to(ViewProperty.SCALE_X, Float.valueOf(0.5f), ViewProperty.SCALE_Y, Float.valueOf(0.5f));
    }

    protected void doExpandAnimation(View view, ListAdapter listAdapter) {
        PopupWindowSpec popupWindowSpecClone = this.mPopupWindowSpec.clone();
        ViewUtils.getBoundsInWindow(view, popupWindowSpecClone.mAnchorViewBounds);
        popupWindowSpecClone.mAnchorViewBounds.left += this.mRootBounds.left;
        popupWindowSpecClone.mAnchorViewBounds.right += this.mRootBounds.left;
        popupWindowSpecClone.mAnchorViewBounds.top += this.mRootBounds.top;
        popupWindowSpecClone.mAnchorViewBounds.bottom += this.mRootBounds.top;
        popupWindowSpecClone.mDecorViewBounds.set(this.mRootBounds.left, this.mRootBounds.top, this.mRootBounds.right, this.mRootBounds.bottom);
        PopupContentHolder popupContentHolder = new PopupContentHolder(this.mContext, listAdapter, this.mSecondaryPopupStrategy, popupWindowSpecClone);
        this.mSecondaryContentHolder = popupContentHolder;
        popupContentHolder.inflate();
        this.mSecondaryContentHolder.setMinWidth(this.mMainPopContentHolder.mContentView.getWidth());
        this.mSecondaryContentHolder.show(view, this.mContainer, this.mRootBounds, true);
    }

    protected void doCollapseAnimation() {
        PopupWindowSpec popupWindowSpec = this.mSecondaryContentHolder.mPopupWindowSpec;
        final SmoothFrameLayout2 smoothFrameLayout2 = this.mSecondaryContentHolder.mContentView;
        final ViewBounds viewBounds = this.mSecondaryContentHolder.mViewBounds;
        viewBounds.setMeasureWidth(smoothFrameLayout2.getWidth());
        Rect rect = popupWindowSpec.mAnchorViewBounds;
        Rect rect2 = this.mMainPopContentHolder.mBoundsRect;
        Rect unionBounds = getUnionBounds(rect2, this.mSecondaryContentHolder.mBoundsRect);
        final int iWidth = unionBounds.width();
        final int iHeight = unionBounds.height();
        final int i = rect2.left - unionBounds.left;
        final int i2 = rect2.top - unionBounds.top;
        final int iWidth2 = i + rect2.width();
        final int iHeight2 = i2 + rect2.height();
        final int left = smoothFrameLayout2.getLeft();
        final int top = smoothFrameLayout2.getTop();
        final int right = smoothFrameLayout2.getRight();
        final int bottom = smoothFrameLayout2.getBottom();
        final int i3 = rect.left - unionBounds.left;
        final int i4 = rect.top - unionBounds.top;
        final int i5 = rect.right - unionBounds.left;
        final int i6 = rect.bottom - unionBounds.top;
        final int i7 = this.mSecondaryContentHolder.mHeaderViewHeight;
        final int i8 = this.mSecondaryContentHolder.mAnchorHeight;
        final int i9 = this.mSecondaryContentHolder.mHeaderViewPaddingTop;
        final int i10 = this.mSecondaryContentHolder.mAnchorPaddingTop;
        final int i11 = this.mSecondaryContentHolder.mHeaderViewPaddingBottom;
        final int i12 = this.mSecondaryContentHolder.mAnchorPaddingBottom;
        this.mSecondaryContentHolder.mListView.setScrollBarStyle(0);
        this.mSecondaryContentHolder.mIsInAnimation = true;
        if (viewBounds.folme() == null) {
            return;
        }
        final int i13 = 0;
        AnimConfig animConfigAddListeners = new AnimConfig().addListeners(new TransitionListener() { // from class: miuix.appcompat.widget.HyperPopupWindow.3
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
                UpdateInfo updateInfoFindByName = UpdateInfo.findByName(collection, "fraction");
                if (updateInfoFindByName != null) {
                    float floatValue = updateInfoFindByName.getFloatValue();
                    if (floatValue >= 1.0f) {
                        viewBounds.folme().end();
                    }
                    int i14 = i13;
                    int i15 = (int) (i14 + ((i - i14) * floatValue));
                    int i16 = i13;
                    int i17 = (int) (i16 + ((i2 - i16) * floatValue));
                    int i18 = iWidth;
                    int i19 = (int) (i18 + ((iWidth2 - i18) * floatValue));
                    int i20 = iHeight;
                    HyperPopupWindow.this.mClipView.setClipBounds(i15, i17, i19, (int) (i20 + ((iHeight2 - i20) * floatValue)));
                    HyperPopupWindow.this.mClipView.refreshClipPath();
                    int i21 = left;
                    int i22 = (int) (i21 + ((i3 - i21) * floatValue));
                    int i23 = top;
                    int i24 = (int) (i23 + ((i4 - i23) * floatValue));
                    int i25 = right;
                    int i26 = (int) (i25 + ((i5 - i25) * floatValue));
                    int i27 = bottom;
                    int i28 = (int) (i27 + ((i6 - i27) * floatValue));
                    HyperPopupWindow.this.mInnerClip.setClipBounds(i22, i24, i26, i28);
                    HyperPopupWindow.this.mInnerClip.refreshClipPath();
                    int i29 = i9;
                    int i30 = (int) (i29 + ((i10 - i29) * floatValue));
                    int i31 = i11;
                    int i32 = (int) (i31 + ((i12 - i31) * floatValue));
                    int i33 = i7;
                    viewBounds.updateLeftTopRightBottom(i22, i24, i26, i28, (int) (i33 + ((i8 - i33) * floatValue)), i30, i32);
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                super.onComplete(obj);
                HyperPopupWindow.this.mContainer.removeView(smoothFrameLayout2);
                HyperPopupWindow.this.mContainer.removeView(HyperPopupWindow.this.mInnerClip);
                HyperPopupWindow.this.mContainer.removeView(HyperPopupWindow.this.mClipView);
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                super.onCancel(obj);
                HyperPopupWindow.this.mContainer.removeView(smoothFrameLayout2);
                HyperPopupWindow.this.mContainer.removeView(HyperPopupWindow.this.mInnerClip);
                HyperPopupWindow.this.mContainer.removeView(HyperPopupWindow.this.mClipView);
            }
        });
        animConfigAddListeners.setEase(FolmeEase.spring(0.95f, 0.2f));
        animConfigAddListeners.setSpecial(ViewBounds.ARROW_ROTATION_PROPERTY, FolmeEase.spring(0.95f, 0.3f), new float[0]);
        viewBounds.folme().resetTo("fraction", Float.valueOf(0.0f));
        viewBounds.folme().to(new AnimState().add("fraction", 1.0f).add(ViewBounds.ARROW_ROTATION_PROPERTY, 0.0d), animConfigAddListeners);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Rect getUnionBounds(Rect rect, Rect rect2) {
        Rect rect3 = new Rect();
        rect3.left = Math.min(rect.left, rect2.left);
        rect3.top = Math.min(rect.top, rect2.top);
        rect3.right = Math.max(rect.right, rect2.right);
        rect3.bottom = Math.max(rect.bottom, rect2.bottom);
        return rect3;
    }

    protected class PopupContentHolder {
        private ListAdapter mAdapter;
        private int mAnchorHeight;
        private int mAnchorPaddingBottom;
        private int mAnchorPaddingTop;
        private SmoothFrameLayout2 mContentView;
        private Context mContext;
        private View mHeaderView;
        private int mHeaderViewHeight;
        private int mHeaderViewPaddingBottom;
        private int mHeaderViewPaddingTop;
        private ListView mListView;
        private AdapterView.OnItemClickListener mOnItemClickListener;
        private PopupWindowSpec mPopupWindowSpec;
        private IPopupWindowStrategy mPopupWindowStrategy;
        private ViewBounds mViewBounds;
        private int mMinWidth = -1;
        private final Rect mBoundsRect = new Rect();
        private boolean mIsInAnimation = false;

        public PopupContentHolder(Context context, IPopupWindowStrategy iPopupWindowStrategy) {
            this.mContext = context;
            this.mPopupWindowStrategy = iPopupWindowStrategy;
        }

        public PopupContentHolder(Context context, ListAdapter listAdapter, IPopupWindowStrategy iPopupWindowStrategy, PopupWindowSpec popupWindowSpec) {
            this.mContext = context;
            this.mAdapter = listAdapter;
            this.mPopupWindowStrategy = iPopupWindowStrategy;
            this.mPopupWindowSpec = popupWindowSpec;
        }

        void setAdapter(ListAdapter listAdapter) {
            this.mAdapter = listAdapter;
        }

        void setItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        void inflate() {
            if (this.mContentView == null) {
                this.mContentView = (SmoothFrameLayout2) LayoutInflater.from(this.mContext).inflate(R.layout.miuix_appcompat_hyper_popup_list, (ViewGroup) null);
                Drawable drawableResolveDrawable = AttributeResolver.resolveDrawable(this.mContext, R.attr.immersionWindowBackground);
                if (drawableResolveDrawable instanceof SmoothContainerDrawable2) {
                    ((SmoothContainerDrawable2) drawableResolveDrawable).setCornerRadius(HyperPopupWindow.this.mCornerRadius);
                }
                if (drawableResolveDrawable != null) {
                    this.mContentView.setBackground(drawableResolveDrawable);
                }
                final View viewFindViewById = this.mContentView.findViewById(R.id.spring_back);
                this.mContentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: miuix.appcompat.widget.HyperPopupWindow.PopupContentHolder.1
                    @Override // android.view.View.OnLayoutChangeListener
                    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                        boolean z = !PopupContentHolder.this.mIsInAnimation && (PopupContentHolder.this.mAdapter != null ? PopupContentHolder.this.mPopupWindowStrategy.isNeedScroll(i4 - i2, PopupContentHolder.this.mPopupWindowSpec) : true);
                        viewFindViewById.setEnabled(z);
                        PopupContentHolder.this.mListView.setVerticalScrollBarEnabled(z);
                    }
                });
            }
            ListView listView = (ListView) this.mContentView.findViewById(android.R.id.list);
            this.mListView = listView;
            if (listView != null) {
                listView.setOnTouchListener(new AnonymousClass2());
                this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.appcompat.widget.HyperPopupWindow$PopupContentHolder$$ExternalSyntheticLambda0
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                        this.f$0.m1844xa07e3e61(adapterView, view, i, j);
                    }
                });
                this.mListView.setAdapter(this.mAdapter);
            }
        }

        /* JADX INFO: renamed from: miuix.appcompat.widget.HyperPopupWindow$PopupContentHolder$2, reason: invalid class name */
        class AnonymousClass2 implements View.OnTouchListener {
            int lastIndex = -1;

            AnonymousClass2() {
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                int firstVisiblePosition;
                int i;
                View childAt;
                int iPointToPosition = PopupContentHolder.this.mListView.pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
                int action = motionEvent.getAction();
                if (action != 0) {
                    if (action == 1 || action == 3 || action == 6) {
                        this.lastIndex = -1;
                        PopupContentHolder.this.mListView.postDelayed(new Runnable() { // from class: miuix.appcompat.widget.HyperPopupWindow$PopupContentHolder$2$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                HyperPopupWindow.PopupContentHolder.AnonymousClass2.lambda$onTouch$0(view);
                            }
                        }, ViewConfiguration.getPressedStateDuration());
                    }
                } else if (iPointToPosition != -1 && (firstVisiblePosition = iPointToPosition - PopupContentHolder.this.mListView.getFirstVisiblePosition()) != (i = this.lastIndex)) {
                    if (i != -1 && (childAt = PopupContentHolder.this.mListView.getChildAt(this.lastIndex)) != null) {
                        childAt.setPressed(false);
                    }
                    PopupContentHolder.this.mListView.getChildAt(firstVisiblePosition).setPressed(true);
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
                        Log.e(HyperPopupWindow.TAG, "list onTouch error " + e);
                    }
                }
            }
        }

        /* JADX INFO: renamed from: lambda$inflate$0$miuix-appcompat-widget-HyperPopupWindow$PopupContentHolder, reason: not valid java name */
        /* synthetic */ void m1844xa07e3e61(AdapterView adapterView, View view, int i, long j) {
            int headerViewsCount = i - this.mListView.getHeaderViewsCount();
            if (this.mOnItemClickListener == null || headerViewsCount < 0 || headerViewsCount >= this.mAdapter.getCount()) {
                return;
            }
            this.mOnItemClickListener.onItemClick(adapterView, view, headerViewsCount, j);
        }

        boolean show(final View view, ViewGroup viewGroup, Rect rect, boolean z) {
            PopupWindowSpec popupWindowSpec = this.mPopupWindowSpec;
            final Rect rect2 = popupWindowSpec.mAnchorViewBounds;
            if (z) {
                rect2.left -= HyperPopupWindow.this.mAnimationExtensionMargin;
                rect2.top -= HyperPopupWindow.this.mAnimationExtensionMargin;
                rect2.right -= HyperPopupWindow.this.mAnimationExtensionMargin;
                rect2.bottom -= HyperPopupWindow.this.mAnimationExtensionMargin;
            }
            popupWindowSpec.mItemViewBounds = HyperPopupWindow.getItemViewBounds(this.mAdapter, this.mListView, this.mContext, popupWindowSpec.mMaxWidth, this.mMinWidth);
            this.mPopupWindowStrategy.measureContentSize(popupWindowSpec);
            int xInWindow = this.mPopupWindowStrategy.getXInWindow(popupWindowSpec);
            int yInWindow = this.mPopupWindowStrategy.getYInWindow(popupWindowSpec);
            int i = popupWindowSpec.mFinalPopupWidth;
            int i2 = popupWindowSpec.mFinalPopupHeight;
            int i3 = xInWindow + i;
            int i4 = yInWindow + i2;
            this.mBoundsRect.set(xInWindow, yInWindow, i3, i4);
            if (HyperPopupWindow.this.mEnableSecondaryMenu) {
                HyperPopupWindow.this.offsetRootBounds(popupWindowSpec, xInWindow, yInWindow, i, i2);
            }
            if (z) {
                Rect rect3 = HyperPopupWindow.this.mMainPopContentHolder.mBoundsRect;
                Rect unionBounds = HyperPopupWindow.getUnionBounds(rect3, this.mBoundsRect);
                HyperPopupWindow.this.mClipView = HyperPopupWindow.this.new ClipLayout(this.mContext);
                HyperPopupWindow.this.mClipView.setBackgroundColor(0);
                HyperPopupWindow.this.mClipView.setRadius(HyperPopupWindow.this.mCornerRadius);
                HyperPopupWindow.this.mClipView.setElevation(HyperPopupWindow.this.mElevation + (HyperPopupWindow.this.mElevationExtra * 2));
                final int i5 = rect3.left - unionBounds.left;
                final int i6 = rect3.top - unionBounds.top;
                final int iWidth = i5 + rect3.width();
                final int iHeight = rect3.height() + i6;
                final int iWidth2 = unionBounds.width();
                final int iHeight2 = unionBounds.height();
                HyperPopupWindow.this.mClipView.setClipBounds(i5, i6, iWidth, iHeight);
                HyperPopupWindow.this.mClipView.refreshClipPath();
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(unionBounds.width(), unionBounds.height());
                layoutParams.leftMargin = unionBounds.left - rect.left;
                layoutParams.topMargin = unionBounds.top - rect.top;
                HyperPopupWindow.this.mClipView.setLayoutParams(layoutParams);
                viewGroup.addView(HyperPopupWindow.this.mClipView);
                final int i7 = rect2.left - unionBounds.left;
                final int i8 = rect2.top - unionBounds.top;
                final int i9 = rect2.right - unionBounds.left;
                final int i10 = rect2.bottom - unionBounds.top;
                final int i11 = xInWindow - unionBounds.left;
                final int i12 = yInWindow - unionBounds.top;
                final int i13 = i3 - unionBounds.left;
                final int i14 = i4 - unionBounds.top;
                this.mContentView.setLayoutParams(new FrameLayout.LayoutParams(i13 - i11, i14 - i12));
                ClipLayout clipLayout = HyperPopupWindow.this.new ClipLayout(this.mContext);
                clipLayout.setLayoutParams(new FrameLayout.LayoutParams(unionBounds.width(), unionBounds.height()));
                clipLayout.setBackgroundColor(0);
                clipLayout.setClipBounds(i7, i8, i9, i10);
                clipLayout.refreshClipPath();
                clipLayout.addView(this.mContentView);
                HyperPopupWindow.this.mClipView.addView(clipLayout);
                HyperPopupWindow.this.mInnerClip = clipLayout;
                ViewBounds viewBounds = new ViewBounds(this.mContentView);
                this.mViewBounds = viewBounds;
                viewBounds.setMeasureWidth(i);
                HyperPopupWindow.this.mMainPopContentHolder.mIsInAnimation = true;
                HyperPopupWindow.this.mSecondaryContentHolder.mIsInAnimation = true;
                final int i15 = 0;
                final int i16 = 0;
                this.mContentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: miuix.appcompat.widget.HyperPopupWindow.PopupContentHolder.3
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        View viewFindViewById = PopupContentHolder.this.mContentView.findViewById(R.id.tag_secondary_popup_menu_item_head);
                        if (viewFindViewById == null) {
                            return false;
                        }
                        PopupContentHolder.this.mContentView.getViewTreeObserver().removeOnPreDrawListener(this);
                        viewFindViewById.sendAccessibilityEvent(8);
                        PopupContentHolder.this.mAnchorHeight = rect2.height();
                        PopupContentHolder.this.mAnchorPaddingTop = view.getPaddingTop();
                        PopupContentHolder.this.mAnchorPaddingBottom = view.getPaddingBottom();
                        PopupContentHolder.this.mHeaderViewHeight = viewFindViewById.getHeight();
                        PopupContentHolder.this.mHeaderViewPaddingTop = viewFindViewById.getPaddingTop();
                        PopupContentHolder.this.mHeaderViewPaddingBottom = viewFindViewById.getPaddingBottom();
                        final int i17 = PopupContentHolder.this.mAnchorHeight;
                        final int i18 = PopupContentHolder.this.mHeaderViewHeight;
                        final int i19 = PopupContentHolder.this.mAnchorPaddingTop;
                        final int i20 = PopupContentHolder.this.mHeaderViewPaddingTop;
                        final int i21 = PopupContentHolder.this.mAnchorPaddingBottom;
                        final int i22 = PopupContentHolder.this.mHeaderViewPaddingBottom;
                        PopupContentHolder.this.mHeaderView = viewFindViewById;
                        AnimConfig animConfigAddListeners = new AnimConfig().addListeners(new TransitionListener() { // from class: miuix.appcompat.widget.HyperPopupWindow.PopupContentHolder.3.1
                            @Override // miuix.animation.listener.TransitionListener
                            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                                super.onUpdate(obj, collection);
                                UpdateInfo updateInfoFindByName = UpdateInfo.findByName(collection, "fraction");
                                if (updateInfoFindByName != null) {
                                    float floatValue = updateInfoFindByName.getFloatValue();
                                    HyperPopupWindow.this.mClipView.setClipBounds((int) (i5 + ((i15 - i5) * floatValue)), (int) (i6 + ((i16 - i6) * floatValue)), (int) (iWidth + ((iWidth2 - iWidth) * floatValue)), (int) (iHeight + ((iHeight2 - iHeight) * floatValue)));
                                    HyperPopupWindow.this.mClipView.refreshClipPath();
                                    int i23 = (int) (i7 + ((i11 - i7) * floatValue));
                                    int i24 = (int) (i8 + ((i12 - i8) * floatValue));
                                    int i25 = (int) (i9 + ((i13 - i9) * floatValue));
                                    int i26 = (int) (i10 + ((i14 - i10) * floatValue));
                                    HyperPopupWindow.this.mInnerClip.setClipBounds(i23, i24, i25, i26);
                                    HyperPopupWindow.this.mInnerClip.refreshClipPath();
                                    int i27 = i19;
                                    int i28 = (int) (i27 + ((i20 - i27) * floatValue));
                                    int i29 = i21;
                                    int i30 = (int) (i29 + ((i22 - i29) * floatValue));
                                    int i31 = i17;
                                    PopupContentHolder.this.mViewBounds.updateLeftTopRightBottom(i23, i24, i25, i26, (int) (i31 + ((i18 - i31) * floatValue)), i28, i30);
                                }
                            }

                            @Override // miuix.animation.listener.TransitionListener
                            public void onComplete(Object obj) {
                                super.onComplete(obj);
                                HyperPopupWindow.this.mMainPopContentHolder.mIsInAnimation = false;
                                if (HyperPopupWindow.this.mSecondaryContentHolder != null) {
                                    HyperPopupWindow.this.mSecondaryContentHolder.mIsInAnimation = false;
                                }
                            }

                            @Override // miuix.animation.listener.TransitionListener
                            public void onCancel(Object obj) {
                                super.onCancel(obj);
                                HyperPopupWindow.this.mMainPopContentHolder.mIsInAnimation = false;
                                if (HyperPopupWindow.this.mSecondaryContentHolder != null) {
                                    HyperPopupWindow.this.mSecondaryContentHolder.mIsInAnimation = false;
                                }
                            }
                        });
                        animConfigAddListeners.setSpecial(ViewBounds.ARROW_ROTATION_PROPERTY, FolmeEase.spring(0.95f, 0.2f), new float[0]).setSpecial(ViewBounds.CORNER_PROPERTY, FolmeEase.spring(0.97f, 0.2f), new float[0]);
                        Folme.use((FolmeObject) PopupContentHolder.this.mViewBounds);
                        float cornerRadius = PopupContentHolder.this.mViewBounds.getCornerRadius();
                        float f = HyperPopupWindow.this.mCornerRadius;
                        PopupContentHolder.this.mContentView.setCornerRadius(cornerRadius);
                        AnimState animStateAdd = new AnimState().add("fraction", 1.0f).add(ViewBounds.CORNER_PROPERTY, f).add(ViewBounds.ARROW_ROTATION_PROPERTY, PopupContentHolder.this.mContentView.getLayoutDirection() == 1 ? 90.0f : -90.0f);
                        PopupContentHolder.this.mViewBounds.folme().resetTo("fraction", Float.valueOf(0.0f));
                        PopupContentHolder.this.mViewBounds.folme().to(animStateAdd, animConfigAddListeners);
                        return false;
                    }
                });
                return true;
            }
            this.mContentView.setPivotX(i3 / 2 > rect2.centerX() ? 0.0f : i);
            this.mContentView.setPivotY(yInWindow <= rect2.top ? i2 : 0.0f);
            FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(i, i2);
            if (HyperPopupWindow.this.mEnableSecondaryMenu) {
                layoutParams2.leftMargin = xInWindow - rect.left;
                layoutParams2.topMargin = yInWindow - rect.top;
            }
            this.mContentView.setLayoutParams(layoutParams2);
            HyperPopupWindow hyperPopupWindow = HyperPopupWindow.this;
            hyperPopupWindow.safeAddView(hyperPopupWindow.mContainer, this.mContentView);
            return true;
        }

        boolean update() {
            PopupWindowSpec popupWindowSpec = this.mPopupWindowSpec;
            HyperPopupWindow hyperPopupWindow = HyperPopupWindow.this;
            hyperPopupWindow.mRootBounds = hyperPopupWindow.getRootBounds();
            popupWindowSpec.mItemViewBounds = HyperPopupWindow.getItemViewBounds(this.mAdapter, this.mListView, this.mContext, popupWindowSpec.mMaxWidth, this.mMinWidth);
            this.mPopupWindowStrategy.measureContentSize(popupWindowSpec);
            int xInWindow = this.mPopupWindowStrategy.getXInWindow(popupWindowSpec);
            int yInWindow = this.mPopupWindowStrategy.getYInWindow(popupWindowSpec);
            int i = popupWindowSpec.mFinalPopupWidth;
            int i2 = popupWindowSpec.mFinalPopupHeight;
            this.mBoundsRect.set(xInWindow, yInWindow, xInWindow + i, yInWindow + i2);
            if (HyperPopupWindow.this.mEnableSecondaryMenu) {
                HyperPopupWindow.this.offsetRootBounds(popupWindowSpec, xInWindow, yInWindow, i, i2);
                int iWidth = HyperPopupWindow.this.mRootBounds.width();
                int iHeight = HyperPopupWindow.this.mRootBounds.height();
                HyperPopupWindow.this.setWidth(iWidth);
                HyperPopupWindow.this.setHeight(iHeight);
                HyperPopupWindow hyperPopupWindow2 = HyperPopupWindow.this;
                hyperPopupWindow2.update(hyperPopupWindow2.mRootBounds.left, HyperPopupWindow.this.mRootBounds.top, this.mBoundsRect.width(), this.mBoundsRect.height());
                return true;
            }
            int iWidth2 = this.mBoundsRect.width();
            int iHeight2 = this.mBoundsRect.height();
            HyperPopupWindow.this.setWidth(iWidth2);
            HyperPopupWindow.this.setHeight(iHeight2);
            HyperPopupWindow.this.update(this.mBoundsRect.left, this.mBoundsRect.top, this.mBoundsRect.width(), this.mBoundsRect.height());
            return true;
        }

        protected void setMinWidth(int i) {
            this.mMinWidth = i;
        }

        protected void setMenuListAccessibilityDelegate() {
            ListView listView;
            if (HyperPopupWindow.this.mMainPopContentHolder == null || (listView = this.mListView) == null) {
                return;
            }
            ViewCompat.setAccessibilityDelegate(listView, new AccessibilityDelegateCompat() { // from class: miuix.appcompat.widget.HyperPopupWindow.PopupContentHolder.4
                @Override // androidx.core.view.AccessibilityDelegateCompat
                public boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
                    if (accessibilityEvent.getEventType() == 32768) {
                        HyperPopupWindow.this.mFocusedMainPopupItemView = view;
                    }
                    return super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void safeAddView(ViewGroup viewGroup, View view) {
        if (viewGroup == null || view == null) {
            return;
        }
        safeAddView(viewGroup, view, -1);
    }

    private void safeAddView(ViewGroup viewGroup, View view, int i) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        if (viewGroup != null) {
            viewGroup.addView(view, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void offsetRootBounds(PopupWindowSpec popupWindowSpec, int i, int i2, int i3, int i4) {
        Rect rect = this.mRootBounds;
        int i5 = popupWindowSpec.mMaxWidth;
        int i6 = popupWindowSpec.mMaxHeight + i2;
        if (i6 > rect.bottom) {
            rect.top = i2 + (rect.bottom - i6);
        } else {
            rect.top = i2;
            rect.bottom = i6;
        }
        int absoluteGravity = Gravity.getAbsoluteGravity(popupWindowSpec.mGravity, popupWindowSpec.layoutDirection) & 7;
        if (absoluteGravity != 1) {
            if (absoluteGravity == 5) {
                rect.right = i3 + i;
                rect.left = Math.max(i - i5, rect.left);
            } else {
                rect.left = i;
                rect.right = Math.min(i + i5, rect.right);
            }
        }
    }

    private void toBackAnim() {
        SmoothFrameLayout2 smoothFrameLayout2 = this.mMainPopContentHolder.mContentView;
        View viewFindViewById = smoothFrameLayout2.findViewById(R.id.mask);
        IStateStyle iStateStyleState = Folme.use((View) smoothFrameLayout2).state();
        ViewProperty viewProperty = ViewProperty.SCALE_X;
        Float fValueOf = Float.valueOf(0.95f);
        iStateStyleState.to(viewProperty, fValueOf, ViewProperty.SCALE_Y, fValueOf, ViewBounds.sOpenConfig);
        Folme.use(viewFindViewById).state().to(ViewProperty.AUTO_ALPHA, Float.valueOf(1.0f), ViewBounds.sOpenConfig);
    }

    private void toFrontAnim() {
        SmoothFrameLayout2 smoothFrameLayout2 = this.mMainPopContentHolder.mContentView;
        View viewFindViewById = smoothFrameLayout2.findViewById(R.id.mask);
        IStateStyle iStateStyleState = Folme.use((View) smoothFrameLayout2).state();
        ViewProperty viewProperty = ViewProperty.SCALE_X;
        Float fValueOf = Float.valueOf(1.0f);
        iStateStyleState.to(viewProperty, fValueOf, ViewProperty.SCALE_Y, fValueOf, ViewBounds.sCloseConfig);
        Folme.use(viewFindViewById).state().to(ViewProperty.AUTO_ALPHA, Float.valueOf(0.0f), ViewBounds.sCloseConfig);
    }

    protected static class ViewBounds implements FolmeObject {
        private static final String PROPERTY_FRACTION = "fraction";
        private Folme.ObjectFolmeImpl mFolmeAnimator;
        private WeakReference<View> mHeaderArrowView;
        private WeakReference<View> mView;
        private static final FloatProperty<ViewBounds> ARROW_ROTATION_PROPERTY = new FloatProperty<ViewBounds>("arrowRotation") { // from class: miuix.appcompat.widget.HyperPopupWindow.ViewBounds.1
            @Override // miuix.animation.property.FloatProperty
            public float getValue(ViewBounds viewBounds) {
                return viewBounds.getArrowRotation();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(ViewBounds viewBounds, float f) {
                viewBounds.setArrowRotation(f);
            }
        };
        private static final FloatProperty<ViewBounds> CORNER_PROPERTY = new FloatProperty<ViewBounds>("corner") { // from class: miuix.appcompat.widget.HyperPopupWindow.ViewBounds.2
            @Override // miuix.animation.property.FloatProperty
            public float getValue(ViewBounds viewBounds) {
                return viewBounds.getCornerRadius();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(ViewBounds viewBounds, float f) {
                viewBounds.setCornerRadius(f);
            }
        };
        private static final AnimConfig sOpenConfig = new AnimConfig();
        private static final AnimConfig sCloseConfig = new AnimConfig().setEase(-2, 0.95f, 0.2f);
        private int mMeasureWidth = -1;
        private float mCornerRadius = 0.0f;
        private float mArrowRotation = 0.0f;

        ViewBounds(View view) {
            this.mView = new WeakReference<>(view);
        }

        public void setMeasureWidth(int i) {
            this.mMeasureWidth = i;
        }

        public void setCornerRadius(float f) {
            this.mCornerRadius = f;
            View view = this.mView.get();
            Drawable background = view.getBackground();
            if (view instanceof SmoothFrameLayout2) {
                ((SmoothFrameLayout2) view).setCornerRadius(this.mCornerRadius);
            }
            if (background instanceof SmoothContainerDrawable2) {
                ((SmoothContainerDrawable2) background).setCornerRadius(this.mCornerRadius);
            }
            ((ClipLayout) view.getParent()).setRadius(f);
        }

        public float getCornerRadius() {
            return this.mCornerRadius;
        }

        public void setArrowRotation(float f) {
            this.mArrowRotation = f;
            WeakReference<View> weakReference = this.mHeaderArrowView;
            if (weakReference == null || weakReference.get() == null) {
                if (this.mView.get() == null) {
                    return;
                }
                View viewFindViewById = this.mView.get().findViewById(R.id.tag_secondary_popup_menu_item_head).findViewById(R.id.arrow);
                this.mHeaderArrowView = new WeakReference<>(viewFindViewById);
                viewFindViewById.setPivotX(viewFindViewById.getWidth() / 2.0f);
                viewFindViewById.setPivotY(viewFindViewById.getHeight() / 2.0f);
            }
            this.mHeaderArrowView.get().setRotation(f);
        }

        public float getArrowRotation() {
            return this.mArrowRotation;
        }

        public void updateLeftTopRightBottom(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
            View view = this.mView.get();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            if (view != null) {
                layoutParams.leftMargin = i;
                layoutParams.topMargin = i2;
                ViewGroup viewGroup = (ViewGroup) view.findViewById(android.R.id.list);
                for (int i8 = 0; i8 < viewGroup.getChildCount(); i8++) {
                    View childAt = viewGroup.getChildAt(i8);
                    AbsListView.LayoutParams layoutParams2 = (AbsListView.LayoutParams) viewGroup.getChildAt(i8).getLayoutParams();
                    if (childAt.getId() != R.id.tag_secondary_popup_menu_item_head) {
                        layoutParams2.width = this.mMeasureWidth;
                    } else {
                        layoutParams2.width = i3 - i;
                        layoutParams2.height = i5;
                        childAt.setPadding(childAt.getPaddingLeft(), i6, childAt.getPaddingRight(), i7);
                        childAt.requestLayout();
                    }
                }
            }
        }

        @Override // miuix.animation.FolmeObject
        public void setFolmeImpl(Folme.ObjectFolmeImpl objectFolmeImpl) {
            this.mFolmeAnimator = objectFolmeImpl;
        }

        @Override // miuix.animation.FolmeObject
        public Folme.ObjectFolmeImpl folme() {
            return this.mFolmeAnimator;
        }
    }

    static class SecondaryPopupWindowStrategy extends PopupWindowStrategy {
        SecondaryPopupWindowStrategy() {
        }

        @Override // miuix.popupwidget.internal.strategy.PopupWindowStrategy, miuix.popupwidget.internal.strategy.IPopupWindowStrategy
        public int getXInWindow(PopupWindowSpec popupWindowSpec) {
            Rect rect = popupWindowSpec.mAnchorViewBounds;
            Rect rect2 = popupWindowSpec.mDecorViewBounds;
            int i = popupWindowSpec.mFinalPopupWidth;
            int i2 = rect.left;
            int i3 = i2 + i;
            if (i3 > rect2.right) {
                i2 = rect2.right - i;
                i3 = rect2.right;
            }
            if (i2 < rect2.left) {
                i2 = rect2.left;
            }
            popupWindowSpec.mFinalPopupWidth = i3 - i2;
            return i2;
        }

        @Override // miuix.popupwidget.internal.strategy.PopupWindowStrategy, miuix.popupwidget.internal.strategy.IPopupWindowStrategy
        public int getYInWindow(PopupWindowSpec popupWindowSpec) {
            Rect rect = popupWindowSpec.mAnchorViewBounds;
            Rect rect2 = popupWindowSpec.mDecorViewBounds;
            int i = popupWindowSpec.mFinalPopupHeight;
            int i2 = rect.top;
            if (i2 + i < rect2.bottom) {
                return i2;
            }
            int i3 = rect2.bottom - i;
            if (i3 >= rect2.top) {
                return i3;
            }
            int i4 = rect2.top;
            popupWindowSpec.mFinalPopupHeight = rect2.bottom - rect2.top;
            return i4;
        }
    }

    public class ClipLayout extends FrameLayout {
        private OnBackInvokedCallback backCallBack;
        OnBackInvokedDispatcher dispatcher;
        private boolean interceptedTouchEvent;
        private Path mClipPath;
        private RectF mClipRoundRect;
        private boolean mIsClip;
        private float mRadius;

        public ClipLayout(Context context) {
            super(context);
            this.mIsClip = false;
            this.mClipRoundRect = new RectF();
            this.mClipPath = new Path();
            this.interceptedTouchEvent = false;
        }

        public ClipLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mIsClip = false;
            this.mClipRoundRect = new RectF();
            this.mClipPath = new Path();
            this.interceptedTouchEvent = false;
        }

        public ClipLayout(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.mIsClip = false;
            this.mClipRoundRect = new RectF();
            this.mClipPath = new Path();
            this.interceptedTouchEvent = false;
        }

        public void setRadius(float f) {
            this.mRadius = f;
        }

        public void setClipBounds(int i, int i2, int i3, int i4) {
            this.mClipRoundRect.set(i, i2, i3, i4);
        }

        public void refreshClipPath() {
            this.mClipPath.reset();
            Path path = this.mClipPath;
            RectF rectF = this.mClipRoundRect;
            float f = this.mRadius;
            path.addRoundRect(rectF, f, f, Path.Direction.CW);
            this.mIsClip = true;
        }

        @Override // android.view.View
        public void draw(Canvas canvas) {
            if (this.mIsClip) {
                canvas.clipPath(this.mClipPath);
            }
            super.draw(canvas);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptHoverEvent(MotionEvent motionEvent) {
            return this.interceptedTouchEvent;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (Build.VERSION.SDK_INT >= 33) {
                this.dispatcher = findOnBackInvokedDispatcher();
                final HyperPopupWindow hyperPopupWindow = HyperPopupWindow.this;
                OnBackInvokedCallback onBackInvokedCallback = new OnBackInvokedCallback() { // from class: miuix.appcompat.widget.HyperPopupWindow$ClipLayout$$ExternalSyntheticLambda0
                    @Override // android.window.OnBackInvokedCallback
                    public final void onBackInvoked() {
                        hyperPopupWindow.collapseSecondaryMenu();
                    }
                };
                this.backCallBack = onBackInvokedCallback;
                OnBackInvokedDispatcher onBackInvokedDispatcher = this.dispatcher;
                if (onBackInvokedDispatcher != null) {
                    onBackInvokedDispatcher.registerOnBackInvokedCallback(1000000, onBackInvokedCallback);
                }
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            OnBackInvokedDispatcher onBackInvokedDispatcher;
            super.onDetachedFromWindow();
            if (Build.VERSION.SDK_INT < 33 || (onBackInvokedDispatcher = this.dispatcher) == null) {
                return;
            }
            onBackInvokedDispatcher.unregisterOnBackInvokedCallback(this.backCallBack);
        }
    }
}
