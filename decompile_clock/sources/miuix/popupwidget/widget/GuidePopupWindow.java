package miuix.popupwidget.widget;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import miuix.core.util.WindowUtils;
import miuix.internal.util.ViewUtils;
import miuix.popupwidget.R;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes3.dex */
public class GuidePopupWindow extends ArrowPopupWindow {
    public static final int ARROW_BOTTOM_LEFT_MODE = 18;
    public static final int ARROW_BOTTOM_MODE = 16;
    public static final int ARROW_BOTTOM_RIGHT_MODE = 17;
    public static final int ARROW_LEFT_MODE = 32;
    public static final int ARROW_RIGHT_MODE = 64;
    public static final int ARROW_TOP_LEFT_MODE = 9;
    public static final int ARROW_TOP_MODE = 8;
    public static final int ARROW_TOP_RIGHT_MODE = 10;
    private static final int DEFAULT_SHOW_DURATION = 5000;
    private Runnable mDismissRunnable;
    private LinearLayout mGuideView;
    private int mShowDuration;
    private int mTextViewHeight;
    private int mTextViewWidth;
    private boolean mUseWrapContent;

    public GuidePopupWindow(Context context) {
        super(context);
        this.mTextViewWidth = 0;
        this.mDismissRunnable = new Runnable() { // from class: miuix.popupwidget.widget.GuidePopupWindow.2
            @Override // java.lang.Runnable
            public void run() {
                GuidePopupWindow.this.dismiss(true);
            }
        };
    }

    public GuidePopupWindow(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTextViewWidth = 0;
        this.mDismissRunnable = new Runnable() { // from class: miuix.popupwidget.widget.GuidePopupWindow.2
            @Override // java.lang.Runnable
            public void run() {
                GuidePopupWindow.this.dismiss(true);
            }
        };
    }

    public GuidePopupWindow(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTextViewWidth = 0;
        this.mDismissRunnable = new Runnable() { // from class: miuix.popupwidget.widget.GuidePopupWindow.2
            @Override // java.lang.Runnable
            public void run() {
                GuidePopupWindow.this.dismiss(true);
            }
        };
    }

    public GuidePopupWindow(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mTextViewWidth = 0;
        this.mDismissRunnable = new Runnable() { // from class: miuix.popupwidget.widget.GuidePopupWindow.2
            @Override // java.lang.Runnable
            public void run() {
                GuidePopupWindow.this.dismiss(true);
            }
        };
    }

    public void setWrapContent(boolean z) {
        this.mUseWrapContent = z;
    }

    @Override // miuix.popupwidget.widget.ArrowPopupWindow
    protected void onPrepareWindow() {
        super.onPrepareWindow();
        this.mShowDuration = 5000;
        setFocusable(true);
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.miuix_appcompat_guide_popup_content_view, (ViewGroup) null, false);
        this.mGuideView = linearLayout;
        setContentView(linearLayout);
        this.mArrowPopupView.enableShowingAnimation(false);
        setAccessibilityDelegate();
    }

    private void setAccessibilityDelegate() {
        this.mGuideView.setImportantForAccessibility(1);
        this.mGuideView.setFocusableInTouchMode(true);
        ViewCompat.setAccessibilityDelegate(this.mGuideView, new AccessibilityDelegateCompat() { // from class: miuix.popupwidget.widget.GuidePopupWindow.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
                boolean zDispatchPopulateAccessibilityEvent = super.dispatchPopulateAccessibilityEvent(view, accessibilityEvent);
                if (shouldDismissForAccessibilityEvent(accessibilityEvent)) {
                    GuidePopupWindow.this.dismiss(true);
                }
                return zDispatchPopulateAccessibilityEvent;
            }

            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
                super.onInitializeAccessibilityEvent(view, accessibilityEvent);
                if (shouldDismissForAccessibilityEvent(accessibilityEvent)) {
                    GuidePopupWindow.this.dismiss(true);
                }
            }

            private boolean shouldDismissForAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
                int eventType = accessibilityEvent.getEventType();
                return eventType == 256 || eventType == 65536;
            }
        });
    }

    public void setGuideText(String str) {
        addGuideTextView(str);
    }

    public void setGuideText(int i) {
        setGuideText(getContext().getString(i));
    }

    private void addGuideTextView(String str) {
        String[] strArrSplit;
        if (TextUtils.isEmpty(str) || (strArrSplit = str.split("\n")) == null || strArrSplit.length == 0) {
            return;
        }
        Point point = new Point();
        WindowUtils.getWindowSize(getContext(), point);
        for (String str2 : strArrSplit) {
            AppCompatTextView appCompatTextView = new AppCompatTextView(getContext(), null, R.attr.guidePopupTextStyle);
            appCompatTextView.setMaxWidth(getContext().getResources().getDimensionPixelSize(R.dimen.miuix_popup_guide_text_view_max_width));
            appCompatTextView.setText(str2);
            appCompatTextView.setTextDirection(5);
            int[] textViewHeightAndWidth = getTextViewHeightAndWidth(appCompatTextView, point);
            this.mTextViewHeight += textViewHeightAndWidth[0];
            this.mTextViewWidth = Math.max(this.mTextViewWidth, textViewHeightAndWidth[1]);
            this.mGuideView.addView(appCompatTextView);
        }
    }

    private int[] getTextViewHeightAndWidth(View view, Point point) {
        view.measure(View.MeasureSpec.makeMeasureSpec(point.x, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(point.y, Integer.MIN_VALUE));
        return new int[]{view.getMeasuredHeight(), view.getMeasuredWidth()};
    }

    @Override // miuix.popupwidget.widget.ArrowPopupWindow
    public void show(View view, int i, int i2) {
        if (this.mUseWrapContent) {
            showWithWrapContent(view);
        } else {
            super.show(view, i, i2);
        }
    }

    /* JADX WARN: Code duplicated, block: B:38:0x013f  */
    /* JADX WARN: Code duplicated, block: B:39:0x0146  */
    /* JADX WARN: Code duplicated, block: B:41:0x014c  */
    /* JADX WARN: Code duplicated, block: B:42:0x0153  */
    /* JADX WARN: Code duplicated, block: B:45:0x0162  */
    /* JADX WARN: Code duplicated, block: B:46:0x016e  */
    private void showWithWrapContent(View view) {
        int measuredHeight;
        DisplayMetrics displayMetrics;
        FrameLayout.LayoutParams layoutParams;
        int i;
        int i2;
        int i3;
        int i4;
        int popupElevation;
        int popupElevation2;
        int measuredHeight2;
        int i5;
        int i6;
        super.setSuperHeight(-2);
        super.setSuperWidth(-2);
        this.mArrowPopupView.setAnchor(view);
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        int measuredWidth = iArr[0];
        int i7 = iArr[1];
        int[] iArr2 = {this.mArrowPopupView.getArrowHeight(getArrowMode()), this.mArrowPopupView.getArrowWidth(getArrowMode())};
        int dimensionPixelOffset = getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_guide_popup_horizontal_padding);
        int dimensionPixelOffset2 = getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_guide_popup_vertical_padding);
        int dimensionPixelOffset3 = getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_arrow_popup_view_paddingStart);
        int dimensionPixelOffset4 = getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_arrow_popup_view_paddingTop);
        int dimensionPixelOffset5 = getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_guide_popup_margin_horizontal);
        int arrowMode = getArrowMode();
        if (arrowMode == 32) {
            if (this.mRtlMode == 1 || (this.mRtlMode == 2 && ViewUtils.isLayoutRtl(view))) {
                measuredWidth -= ((dimensionPixelOffset3 + (dimensionPixelOffset * 2)) + this.mTextViewWidth) + iArr2[1];
            } else {
                measuredWidth += view.getMeasuredWidth() - dimensionPixelOffset3;
            }
            measuredHeight = (iArr2[0] - view.getMeasuredHeight()) / 2;
        } else {
            if (arrowMode != 64) {
                switch (arrowMode) {
                    case 8:
                        measuredWidth -= ((((dimensionPixelOffset3 * 2) + (dimensionPixelOffset * 2)) + this.mTextViewWidth) - view.getMeasuredWidth()) / 2;
                        measuredHeight2 = view.getMeasuredHeight();
                        i7 += measuredHeight2 - dimensionPixelOffset4;
                        break;
                    case 9:
                    case 10:
                        measuredHeight2 = view.getMeasuredHeight();
                        i7 += measuredHeight2 - dimensionPixelOffset4;
                        break;
                    default:
                        switch (arrowMode) {
                            case 16:
                                measuredWidth -= ((((dimensionPixelOffset3 * 2) + (dimensionPixelOffset * 2)) + this.mTextViewWidth) - view.getMeasuredWidth()) / 2;
                                i5 = this.mTextViewHeight;
                                i6 = iArr2[0];
                                break;
                            case 17:
                            case 18:
                                i5 = this.mTextViewHeight;
                                i6 = iArr2[0];
                                break;
                        }
                        measuredHeight = i5 + i6 + dimensionPixelOffset4 + dimensionPixelOffset2;
                        break;
                }
                displayMetrics = getContext().getResources().getDisplayMetrics();
                LinearLayout linearLayout = (LinearLayout) this.mArrowPopupView.findViewById(R.id.content_wrapper);
                layoutParams = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
                i = displayMetrics.heightPixels;
                i2 = this.mTextViewHeight;
                i3 = dimensionPixelOffset4 * 2;
                i4 = dimensionPixelOffset2 * 2;
                if (i >= (i2 * 5) + iArr2[0] + i3 + i4) {
                    layoutParams.setMargins(dimensionPixelOffset5, i2 * 2, dimensionPixelOffset5, i2 * 2);
                } else {
                    if (isSideMode()) {
                        popupElevation = this.mArrowPopupView.getPopupElevation();
                    } else {
                        popupElevation = this.mArrowPopupView.getPopupElevation() + iArr2[0];
                    }
                    if (isSideMode()) {
                        popupElevation2 = ((((displayMetrics.heightPixels - this.mTextViewHeight) - iArr2[0]) - i3) - i4) / 2;
                    } else {
                        popupElevation2 = this.mArrowPopupView.getPopupElevation();
                    }
                    layoutParams.setMargins(dimensionPixelOffset5, popupElevation2, dimensionPixelOffset5, popupElevation);
                }
                linearLayout.setLayoutParams(layoutParams);
                showAtLocation(view, 8388659, measuredWidth - layoutParams.leftMargin, i7 - layoutParams.topMargin);
                this.mArrowPopupView.setAutoDismiss(getAutoDismiss());
                this.mArrowPopupView.animateToShow();
            }
            if (this.mRtlMode == 1 || (this.mRtlMode == 2 && ViewUtils.isLayoutRtl(view))) {
                measuredWidth += view.getMeasuredWidth() - dimensionPixelOffset3;
            } else {
                measuredWidth -= ((dimensionPixelOffset3 + (dimensionPixelOffset * 2)) + this.mTextViewWidth) + iArr2[1];
            }
            measuredHeight = (iArr2[0] - view.getMeasuredHeight()) / 2;
        }
        i7 -= measuredHeight;
        displayMetrics = getContext().getResources().getDisplayMetrics();
        LinearLayout linearLayout2 = (LinearLayout) this.mArrowPopupView.findViewById(R.id.content_wrapper);
        layoutParams = (FrameLayout.LayoutParams) linearLayout2.getLayoutParams();
        i = displayMetrics.heightPixels;
        i2 = this.mTextViewHeight;
        i3 = dimensionPixelOffset4 * 2;
        i4 = dimensionPixelOffset2 * 2;
        if (i >= (i2 * 5) + iArr2[0] + i3 + i4) {
            layoutParams.setMargins(dimensionPixelOffset5, i2 * 2, dimensionPixelOffset5, i2 * 2);
        } else {
            if (isSideMode()) {
                popupElevation = this.mArrowPopupView.getPopupElevation();
            } else {
                popupElevation = this.mArrowPopupView.getPopupElevation() + iArr2[0];
            }
            if (isSideMode()) {
                popupElevation2 = ((((displayMetrics.heightPixels - this.mTextViewHeight) - iArr2[0]) - i3) - i4) / 2;
            } else {
                popupElevation2 = this.mArrowPopupView.getPopupElevation();
            }
            layoutParams.setMargins(dimensionPixelOffset5, popupElevation2, dimensionPixelOffset5, popupElevation);
        }
        linearLayout2.setLayoutParams(layoutParams);
        showAtLocation(view, 8388659, measuredWidth - layoutParams.leftMargin, i7 - layoutParams.topMargin);
        this.mArrowPopupView.setAutoDismiss(getAutoDismiss());
        this.mArrowPopupView.animateToShow();
    }

    private boolean isSideMode() {
        return getArrowMode() == 32 || getArrowMode() == 64;
    }

    public void show(View view, boolean z) {
        show(view, 0, 0, z);
    }

    public void show(View view, int i, int i2, boolean z) {
        setAutoDismiss(z);
        show(view, i, i2);
        if (z) {
            this.mArrowPopupView.postDelayed(this.mDismissRunnable, this.mShowDuration);
        }
        if (HapticCompat.doesSupportHaptic(HapticCompat.HapticVersion.HAPTIC_VERSION_2)) {
            return;
        }
        HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_POPUP_LIGHT);
    }

    public void setOffset(int i, int i2) {
        this.mArrowPopupView.setOffset(i, i2);
    }

    public void setShowDuration(int i) {
        this.mShowDuration = i;
    }
}
