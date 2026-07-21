package miuix.bottomsheet;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MiuiBlurUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.ViewUtils;
import miuix.theme.token.DimToken;
import miuix.view.DynamicThemeWidget;

/* JADX INFO: loaded from: classes2.dex */
public class BottomSheetModal implements DynamicThemeWidget {
    private final SparseIntArray mAccessibility;
    private BottomSheetBehavior<FrameLayout> mBehavior;
    private BottomSheetView mBottomSheet;
    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback;
    private BottomSheetDragHandleView mBottomSheetDragHandleView;
    private boolean mCanceledOnTouchOutside;
    private FrameLayout mContainer;
    private final Context mContext;
    private CoordinatorLayout mCoordinator;
    private boolean mDragHandleViewEnabled;
    private boolean mExecuteDismissed;
    private View mModalBackground;
    private boolean mModalBackgroundEnabled;
    private OnBackListener mOnBackListener;
    private OnBackPressedCallback mOnBackPressedCallback;
    private OnDismissListener mOnDismissListener;
    private OnDismissStartListener mOnDismissStartListener;
    private OnShowListener mOnShowListener;
    private OnShowStartListener mOnShowStartListener;
    private OnTouchOutsideListener mOnTouchOutsideListener;
    private final ViewGroup mRootView;
    private boolean mShouldRequestLayout;
    private boolean mShowAndDismissWithAnimation;
    private Runnable mShowRunnable;
    private int mUserThemeType;

    public interface OnBackListener {
        boolean onBack();
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnDismissStartListener {
        void onDismissStart();
    }

    public interface OnShowListener {
        void onShow();
    }

    public interface OnShowStartListener {
        void onShowStart();
    }

    public interface OnTouchOutsideListener {
        boolean onTouchOutside();
    }

    static /* synthetic */ boolean lambda$wrapInBottomSheet$1(View view, MotionEvent motionEvent) {
        return true;
    }

    public BottomSheetModal(Activity activity) {
        this(activity, true);
    }

    public BottomSheetModal(Activity activity, boolean z) {
        this.mDragHandleViewEnabled = true;
        this.mCanceledOnTouchOutside = true;
        boolean z2 = false;
        this.mShouldRequestLayout = false;
        this.mModalBackgroundEnabled = false;
        this.mShowAndDismissWithAnimation = true;
        this.mExecuteDismissed = true;
        this.mAccessibility = new SparseIntArray();
        this.mBottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() { // from class: miuix.bottomsheet.BottomSheetModal.4
            @Override // miuix.bottomsheet.BottomSheetBehavior.BottomSheetCallback
            public void onSlide(View view, float f) {
            }

            @Override // miuix.bottomsheet.BottomSheetBehavior.BottomSheetCallback
            public void onStateChanged(View view, int i) {
                if (i != 5 || BottomSheetModal.this.mExecuteDismissed) {
                    return;
                }
                BottomSheetModal.this.dismissImmediately();
            }
        };
        View decorView = activity.getWindow().getDecorView();
        if (!(decorView instanceof ViewGroup)) {
            throw new IllegalStateException("DecorView from activity is not ViewGroup!");
        }
        this.mModalBackgroundEnabled = z;
        ViewGroup viewGroup = (ViewGroup) decorView;
        this.mRootView = viewGroup;
        this.mContext = viewGroup.getContext();
        if (activity instanceof ComponentActivity) {
            this.mOnBackPressedCallback = new OnBackPressedCallback(z2) { // from class: miuix.bottomsheet.BottomSheetModal.1
                @Override // androidx.activity.OnBackPressedCallback
                public void handleOnBackPressed() {
                    if ((BottomSheetModal.this.mOnBackListener == null || !BottomSheetModal.this.mOnBackListener.onBack()) && BottomSheetModal.this.isShowing()) {
                        BottomSheetModal.this.dismiss();
                    }
                }
            };
            ((ComponentActivity) activity).getOnBackPressedDispatcher().addCallback(this.mOnBackPressedCallback);
        }
    }

    public void setContentView(int i) {
        wrapInBottomSheet(i, null, null);
    }

    public void setContentView(View view) {
        wrapInBottomSheet(0, view, null);
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        wrapInBottomSheet(0, view, layoutParams);
    }

    public View getRootView() {
        return this.mContainer;
    }

    public BottomSheetView getBottomSheetView() {
        return this.mBottomSheet;
    }

    private View wrapInBottomSheet(int i, View view, ViewGroup.LayoutParams layoutParams) {
        ensureContainerAndBehavior();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) this.mContainer.findViewById(R.id.coordinator);
        ViewCompat.setAccessibilityPaneTitle(this.mContainer, this.mContext.getString(R.string.miuix_popup_window_default_title));
        if (i != 0 && view == null) {
            view = LayoutInflater.from(this.mContext).inflate(i, (ViewGroup) coordinatorLayout, false);
        }
        this.mBottomSheet.setDragHandleViewEnabled(this.mDragHandleViewEnabled);
        this.mBottomSheet.removeAllContentViews();
        if (layoutParams == null) {
            this.mBottomSheet.addContentChildView(view);
        } else {
            this.mBottomSheet.addContentChildView(view, layoutParams);
        }
        coordinatorLayout.findViewById(R.id.touch_outside).setOnClickListener(new View.OnClickListener() { // from class: miuix.bottomsheet.BottomSheetModal$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.m1849lambda$wrapInBottomSheet$0$miuixbottomsheetBottomSheetModal(view2);
            }
        });
        ViewCompat.setAccessibilityDelegate(this.mBottomSheet, new AccessibilityDelegateCompat() { // from class: miuix.bottomsheet.BottomSheetModal.2
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfoCompat);
                if (BottomSheetModal.this.mCanceledOnTouchOutside) {
                    accessibilityNodeInfoCompat.addAction(1048576);
                    accessibilityNodeInfoCompat.setDismissable(true);
                } else {
                    accessibilityNodeInfoCompat.setDismissable(false);
                }
            }

            @Override // androidx.core.view.AccessibilityDelegateCompat
            public boolean performAccessibilityAction(View view2, int i2, Bundle bundle) {
                if (i2 == 1048576 && BottomSheetModal.this.mCanceledOnTouchOutside) {
                    BottomSheetModal.this.dismiss();
                    return true;
                }
                return super.performAccessibilityAction(view2, i2, bundle);
            }
        });
        this.mBottomSheet.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.bottomsheet.BottomSheetModal$$ExternalSyntheticLambda2
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                return BottomSheetModal.lambda$wrapInBottomSheet$1(view2, motionEvent);
            }
        });
        if (this.mBottomSheet.isEnableBlur() && this.mBottomSheet.isApplyBlur()) {
            applyOverlayBlur(this.mContainer, true);
        }
        return this.mContainer;
    }

    /* JADX INFO: renamed from: lambda$wrapInBottomSheet$0$miuix-bottomsheet-BottomSheetModal, reason: not valid java name */
    /* synthetic */ void m1849lambda$wrapInBottomSheet$0$miuixbottomsheetBottomSheetModal(View view) {
        OnTouchOutsideListener onTouchOutsideListener = this.mOnTouchOutsideListener;
        if ((onTouchOutsideListener == null || !onTouchOutsideListener.onTouchOutside()) && this.mCanceledOnTouchOutside && isShowing() && !this.mExecuteDismissed) {
            dismiss();
        }
    }

    @Override // miuix.view.DynamicThemeWidget
    public void setThemeType(int i) {
        if (this.mUserThemeType != i) {
            this.mUserThemeType = i;
            BottomSheetView bottomSheetView = this.mBottomSheet;
            if (bottomSheetView != null) {
                bottomSheetView.setThemeType(i);
            }
        }
    }

    @Override // miuix.view.DynamicThemeWidget
    public int getThemeType() {
        return this.mUserThemeType;
    }

    @Override // miuix.view.DynamicThemeWidget
    public boolean hasThemeType() {
        return this.mUserThemeType > 0;
    }

    public void applyBlur(boolean z) {
        BottomSheetView bottomSheetView = this.mBottomSheet;
        if (bottomSheetView != null) {
            bottomSheetView.setEnableBlur(z);
            this.mBottomSheet.applyBlur(z);
            FrameLayout frameLayout = this.mContainer;
            if (frameLayout != null) {
                applyOverlayBlur(frameLayout, z);
            }
        }
    }

    public void applyOverlayBlur(View view, boolean z) {
        MaterialConfig.BlurConfig blurConfig;
        if (z) {
            if (this.mBottomSheet.getCurrentMaterial() == null || (blurConfig = this.mBottomSheet.getCurrentMaterial().getBlurConfig()) == null) {
                return;
            }
            MiuiBlurUtils.setBackgroundBlurMode(view, this.mModalBackgroundEnabled ? 1 : 2);
            MiuiBlurUtils.setBackgroundBlurRadius(view, MiuixUIUtils.dp2px(view.getContext(), blurConfig.blurRadius));
            return;
        }
        MiuiBlurUtils.clearBackgroundBlur(view);
    }

    private FrameLayout ensureContainerAndBehavior() {
        MaterialConfig.BlurConfig blurConfig;
        if (this.mContainer == null) {
            FrameLayout frameLayout = (FrameLayout) View.inflate(this.mContext, R.layout.miuix_bottom_sheet_modal_view, null);
            this.mContainer = frameLayout;
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) frameLayout.findViewById(R.id.coordinator);
            this.mCoordinator = coordinatorLayout;
            coordinatorLayout.setVisibility(4);
            this.mBottomSheet = (BottomSheetView) this.mContainer.findViewById(R.id.bottom_sheet_view);
            if (hasThemeType()) {
                this.mBottomSheet.setThemeType(this.mUserThemeType);
            }
            if (this.mBottomSheet.isApplyBlur() && this.mBottomSheet.getCurrentMaterial() != null && (blurConfig = this.mBottomSheet.getCurrentMaterial().getBlurConfig()) != null) {
                MiuiBlurUtils.setBackgroundBlurMode(this.mContainer, 2);
                MiuiBlurUtils.setBackgroundBlurRadius(this.mContainer, MiuixUIUtils.dp2px(this.mRootView.getContext(), blurConfig.blurRadius));
            }
            this.mBottomSheetDragHandleView = (BottomSheetDragHandleView) this.mBottomSheet.findViewById(R.id.drag_handle_view);
            View viewFindViewById = this.mContainer.findViewById(R.id.modal_background);
            this.mModalBackground = viewFindViewById;
            if (this.mModalBackgroundEnabled) {
                viewFindViewById.setVisibility(0);
            } else {
                viewFindViewById.setVisibility(8);
            }
            BottomSheetBehavior<FrameLayout> bottomSheetBehaviorFrom = BottomSheetBehavior.from(this.mBottomSheet);
            this.mBehavior = bottomSheetBehaviorFrom;
            bottomSheetBehaviorFrom.setOriginalWindowInsetsEnabled(true);
            this.mBehavior.addBottomSheetCallback(this.mBottomSheetCallback);
            this.mBehavior.setHideable(this.mCanceledOnTouchOutside);
            this.mBehavior.setReleaseAnimListener(new BottomSheetBehavior.ReleaseAnimListener() { // from class: miuix.bottomsheet.BottomSheetModal.3
                @Override // miuix.bottomsheet.BottomSheetBehavior.ReleaseAnimListener
                public void onStart(int i) {
                    if (i == 5) {
                        BottomSheetModal.this.mExecuteDismissed = true;
                        if (BottomSheetModal.this.mOnDismissStartListener != null) {
                            BottomSheetModal.this.mOnDismissStartListener.onDismissStart();
                        }
                        if (BottomSheetModal.this.mModalBackgroundEnabled) {
                            DimAnimator.dismiss(BottomSheetModal.this.mModalBackground);
                        }
                    }
                }

                @Override // miuix.bottomsheet.BottomSheetBehavior.ReleaseAnimListener
                public void onEnd(int i) {
                    if (i == 5) {
                        BottomSheetModal.this.dismissImmediately();
                    }
                }
            });
        }
        return this.mContainer;
    }

    public void show() {
        if (!isAddedToRootView()) {
            removeAccessibility();
            this.mRootView.addView(this.mContainer, -1, -1);
            if (this.mShouldRequestLayout) {
                this.mBottomSheet.requestLayout();
                this.mBottomSheet.requestApplyInsets();
            }
            if (this.mShowAndDismissWithAnimation) {
                this.mBottomSheet.post(new Runnable() { // from class: miuix.bottomsheet.BottomSheetModal$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.doShow();
                    }
                });
                return;
            }
            this.mExecuteDismissed = false;
            OnBackPressedCallback onBackPressedCallback = this.mOnBackPressedCallback;
            if (onBackPressedCallback != null) {
                onBackPressedCallback.setEnabled(true);
            }
            if (this.mShowRunnable == null) {
                this.mShowRunnable = new Runnable() { // from class: miuix.bottomsheet.BottomSheetModal.5
                    @Override // java.lang.Runnable
                    public void run() {
                        if (BottomSheetModal.this.mBehavior.getExpandedOffset() == 0) {
                            BottomSheetModal.this.mBottomSheet.post(this);
                        } else {
                            BottomSheetModal.this.mBehavior.startEnterAnimation(new BottomSheetBehavior.AnimationListener() { // from class: miuix.bottomsheet.BottomSheetModal.5.1
                                @Override // miuix.bottomsheet.BottomSheetBehavior.AnimationListener
                                public void onAnimationStart() {
                                    if (BottomSheetModal.this.mModalBackgroundEnabled) {
                                        BottomSheetModal.this.mModalBackground.setAlpha(ViewUtils.isNightMode(BottomSheetModal.this.mCoordinator.getContext()) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT);
                                    }
                                    if (BottomSheetModal.this.mOnShowStartListener != null) {
                                        BottomSheetModal.this.mOnShowStartListener.onShowStart();
                                    }
                                }

                                @Override // miuix.bottomsheet.BottomSheetBehavior.AnimationListener
                                public void onAnimationEnd() {
                                    BottomSheetModal.this.mCoordinator.setVisibility(0);
                                    if (BottomSheetModal.this.mOnShowListener != null && BottomSheetModal.this.isShowing()) {
                                        BottomSheetModal.this.mOnShowListener.onShow();
                                    }
                                    BottomSheetModal.this.mBottomSheet.removeCallbacks(BottomSheetModal.this.mShowRunnable);
                                }
                            }, true);
                        }
                    }
                };
            }
            this.mBottomSheet.post(this.mShowRunnable);
            return;
        }
        if (getBehavior().isAnimationInterruptible()) {
            doShow();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShow() {
        if (this.mBehavior.startEnterAnimation(new BottomSheetBehavior.AnimationListener() { // from class: miuix.bottomsheet.BottomSheetModal.6
            @Override // miuix.bottomsheet.BottomSheetBehavior.AnimationListener
            public void onAnimationStart() {
                BottomSheetModal.this.mExecuteDismissed = false;
                if (BottomSheetModal.this.mOnBackPressedCallback != null) {
                    BottomSheetModal.this.mOnBackPressedCallback.setEnabled(true);
                }
                if (BottomSheetModal.this.mOnShowStartListener != null) {
                    BottomSheetModal.this.mOnShowStartListener.onShowStart();
                }
            }

            @Override // miuix.bottomsheet.BottomSheetBehavior.AnimationListener
            public void onAnimationEnd() {
                if (BottomSheetModal.this.mOnShowListener == null || !BottomSheetModal.this.isShowing()) {
                    return;
                }
                BottomSheetModal.this.mOnShowListener.onShow();
            }
        }) && this.mModalBackgroundEnabled) {
            DimAnimator.show(this.mModalBackground);
        }
    }

    public void dismiss() {
        FrameLayout frameLayout = this.mContainer;
        if (frameLayout != null) {
            hideSoftIME(frameLayout);
            if (this.mShowAndDismissWithAnimation) {
                if (this.mBehavior.startExitAnimation(new AnonymousClass7()) && this.mModalBackgroundEnabled) {
                    DimAnimator.dismiss(this.mModalBackground);
                    return;
                }
                return;
            }
            doRemove();
        }
    }

    /* JADX INFO: renamed from: miuix.bottomsheet.BottomSheetModal$7, reason: invalid class name */
    class AnonymousClass7 implements BottomSheetBehavior.AnimationListener {
        AnonymousClass7() {
        }

        @Override // miuix.bottomsheet.BottomSheetBehavior.AnimationListener
        public void onAnimationStart() {
            BottomSheetModal.this.mExecuteDismissed = true;
            if (BottomSheetModal.this.mOnDismissStartListener != null) {
                BottomSheetModal.this.mOnDismissStartListener.onDismissStart();
            }
        }

        @Override // miuix.bottomsheet.BottomSheetBehavior.AnimationListener
        public void onAnimationEnd() {
            if (BottomSheetModal.this.mShowRunnable != null) {
                BottomSheetModal.this.mBottomSheet.removeCallbacks(BottomSheetModal.this.mShowRunnable);
            }
            BottomSheetModal.this.mRootView.post(new Runnable() { // from class: miuix.bottomsheet.BottomSheetModal$7$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1850lambda$onAnimationEnd$0$miuixbottomsheetBottomSheetModal$7();
                }
            });
        }

        /* JADX INFO: renamed from: lambda$onAnimationEnd$0$miuix-bottomsheet-BottomSheetModal$7, reason: not valid java name */
        /* synthetic */ void m1850lambda$onAnimationEnd$0$miuixbottomsheetBottomSheetModal$7() {
            BottomSheetModal.this.doRemove();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRemove() {
        this.mRootView.removeView(this.mContainer);
        this.mShouldRequestLayout = true;
        OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
        OnBackPressedCallback onBackPressedCallback = this.mOnBackPressedCallback;
        if (onBackPressedCallback != null) {
            onBackPressedCallback.setEnabled(false);
        }
        recoverAccessibility();
    }

    /* JADX INFO: renamed from: miuix.bottomsheet.BottomSheetModal$8, reason: invalid class name */
    class AnonymousClass8 implements BottomSheetBehavior.AnimationListener {
        AnonymousClass8() {
        }

        @Override // miuix.bottomsheet.BottomSheetBehavior.AnimationListener
        public void onAnimationStart() {
            BottomSheetModal.this.mExecuteDismissed = true;
        }

        @Override // miuix.bottomsheet.BottomSheetBehavior.AnimationListener
        public void onAnimationEnd() {
            if (BottomSheetModal.this.mShowRunnable != null) {
                BottomSheetModal.this.mBottomSheet.removeCallbacks(BottomSheetModal.this.mShowRunnable);
            }
            BottomSheetModal.this.mRootView.post(new Runnable() { // from class: miuix.bottomsheet.BottomSheetModal$8$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1851lambda$onAnimationEnd$0$miuixbottomsheetBottomSheetModal$8();
                }
            });
        }

        /* JADX INFO: renamed from: lambda$onAnimationEnd$0$miuix-bottomsheet-BottomSheetModal$8, reason: not valid java name */
        /* synthetic */ void m1851lambda$onAnimationEnd$0$miuixbottomsheetBottomSheetModal$8() {
            BottomSheetModal.this.doRemove();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissImmediately() {
        if (this.mContainer != null) {
            this.mBehavior.startExitAnimation(new AnonymousClass8(), true);
        }
    }

    private void hideSoftIME(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) ContextCompat.getSystemService(this.mContext, InputMethodManager.class);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void removeAccessibility() {
        ViewGroup viewGroup = this.mRootView;
        if (viewGroup == null) {
            return;
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mRootView.getChildAt(i);
            this.mAccessibility.append(childAt.hashCode(), childAt.getImportantForAccessibility());
            childAt.setImportantForAccessibility(4);
        }
    }

    private void recoverAccessibility() {
        ViewGroup viewGroup = this.mRootView;
        if (viewGroup == null) {
            return;
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mRootView.getChildAt(i);
            int i2 = this.mAccessibility.get(childAt.hashCode(), -1);
            if (i2 >= 0) {
                childAt.setImportantForAccessibility(i2);
            }
        }
        this.mAccessibility.clear();
    }

    public void setCanceledOnTouchOutside(boolean z) {
        this.mCanceledOnTouchOutside = z;
    }

    public void setDragHandleViewEnabled(boolean z) {
        this.mDragHandleViewEnabled = z;
        BottomSheetView bottomSheetView = this.mBottomSheet;
        if (bottomSheetView != null) {
            bottomSheetView.setDragHandleViewEnabled(z);
        }
    }

    public void setShowAndDismissWithAnimation(boolean z) {
        this.mShowAndDismissWithAnimation = z;
    }

    public void setAnimationInterruptible(boolean z) {
        getBehavior().setAnimationInterruptible(z);
    }

    public BottomSheetBehavior<FrameLayout> getBehavior() {
        if (this.mBehavior == null) {
            ensureContainerAndBehavior();
        }
        return this.mBehavior;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isShowing() {
        return isAddedToRootView();
    }

    private boolean isAddedToRootView() {
        ensureContainerAndBehavior();
        return this.mContainer.getParent() == this.mRootView;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public void setOnDismissStartListener(OnDismissStartListener onDismissStartListener) {
        this.mOnDismissStartListener = onDismissStartListener;
    }

    public void setOnOnShowListener(OnShowListener onShowListener) {
        this.mOnShowListener = onShowListener;
    }

    public void setOnShowStartListener(OnShowStartListener onShowStartListener) {
        this.mOnShowStartListener = onShowStartListener;
    }

    public void setOnTouchOutsideListener(OnTouchOutsideListener onTouchOutsideListener) {
        this.mOnTouchOutsideListener = onTouchOutsideListener;
    }

    public void setOnBackListener(OnBackListener onBackListener) {
        this.mOnBackListener = onBackListener;
    }

    public void release() {
        FrameLayout frameLayout;
        setOnBackListener(null);
        setOnTouchOutsideListener(null);
        setOnDismissListener(null);
        setOnDismissStartListener(null);
        setOnOnShowListener(null);
        setOnShowStartListener(null);
        OnBackPressedCallback onBackPressedCallback = this.mOnBackPressedCallback;
        if (onBackPressedCallback != null) {
            onBackPressedCallback.remove();
            this.mOnBackPressedCallback = null;
        }
        Runnable runnable = this.mShowRunnable;
        if (runnable != null && (frameLayout = this.mContainer) != null) {
            frameLayout.removeCallbacks(runnable);
        }
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = this.mBehavior;
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.release();
        }
    }

    private static class DimAnimator {
        private DimAnimator() {
        }

        public static void show(View view) {
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, "alpha", 0.0f, ViewUtils.isNightMode(view.getContext()) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT);
            objectAnimatorOfFloat.setInterpolator(new DecelerateInterpolator(1.5f));
            objectAnimatorOfFloat.setDuration(300L);
            objectAnimatorOfFloat.start();
        }

        public static void dismiss(View view) {
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, "alpha", ViewUtils.isNightMode(view.getContext()) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT, 0.0f);
            objectAnimatorOfFloat.setInterpolator(new DecelerateInterpolator(1.5f));
            objectAnimatorOfFloat.setDuration(250L);
            objectAnimatorOfFloat.start();
        }
    }
}
