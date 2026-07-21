package miuix.miuixbasewidget.widget.internal;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import java.util.ArrayList;
import java.util.List;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.ViewUtils;
import miuix.miuixbasewidget.R;

/* JADX INFO: loaded from: classes2.dex */
public class TabViewContainerView extends FrameLayout {
    private static final int MEASURE_MODE_COMPAT = 0;
    private static final int MEASURE_MODE_WIDE = 1;
    private static final int WIDE_LESS_THAN_TWO_ITEM_MIN_DP = 220;
    private static final int WIDE_MORE_THAN_FOUR_ITEM_MIN_DP = 150;
    private static final int WIDE_THREE_ITEM_MIN_DP = 180;
    private int mChildrenTotalWidth;
    private int mDensityDpi;
    private int mGapBetweenTabs;
    private boolean mLayoutCenter;
    private int mLayoutMode;
    private final List<View> mOverSizeViews;
    private final List<View> mSmallSizeViews;
    private int mSpaciousLessThanTwoItemMinWidth;
    private int mSpaciousMoreThanFourItemMinWidth;
    private int mSpaciousThreeItemMinWidth;
    private int mVerticalPaddingBottom;
    private int mVerticalPaddingTop;

    public TabViewContainerView(Context context) {
        this(context, null);
    }

    public TabViewContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TabViewContainerView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public TabViewContainerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mLayoutCenter = false;
        this.mLayoutMode = 0;
        this.mOverSizeViews = new ArrayList();
        this.mSmallSizeViews = new ArrayList();
        updateLayoutParams();
    }

    private void updateLayoutParams() {
        Context context = getContext();
        Resources resources = getResources();
        this.mGapBetweenTabs = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_filter_sort_view2_tab_gap);
        this.mVerticalPaddingTop = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_filter_sort_view2_vertical_padding_top);
        this.mVerticalPaddingBottom = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_filter_sort_view2_vertical_padding_bottom);
        this.mSpaciousLessThanTwoItemMinWidth = MiuixUIUtils.dp2px(context, 220.0f);
        this.mSpaciousThreeItemMinWidth = MiuixUIUtils.dp2px(context, 180.0f);
        this.mSpaciousMoreThanFourItemMinWidth = MiuixUIUtils.dp2px(context, 150.0f);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.densityDpi;
        if (i != this.mDensityDpi) {
            this.mDensityDpi = i;
            updateLayoutParams();
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        this.mLayoutCenter = false;
        this.mChildrenTotalWidth = 0;
        int childCount = getChildCount();
        int i3 = 0;
        for (int i4 = 0; i4 < childCount; i4++) {
            if (!isViewGone(getChildAt(i4))) {
                i3++;
            }
        }
        if (i3 <= 0) {
            super.onMeasure(i, i2);
            return;
        }
        int i5 = this.mLayoutMode;
        if (i5 == 2) {
            measureByWrapMode(i, i2, i3);
            return;
        }
        if (i5 != 0) {
            if (i5 == 1) {
                if (!measureByWideMode(i, i2, i3)) {
                    this.mLayoutCenter = true;
                    return;
                }
            } else {
                throw new IllegalStateException("Unexpected layout mode: " + this.mLayoutMode);
            }
        }
        measureByCompatMode(i, i2, i3);
    }

    private void measureByWrapMode(int i, int i2, int i3) {
        int i4 = i3 > 1 ? (i3 - 1) * this.mGapBetweenTabs : 0;
        super.onMeasure(i, i2);
        int childCount = getChildCount();
        int i5 = 0;
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            if (!isViewGone(childAt)) {
                int measuredWidth = childAt.getMeasuredWidth();
                i5 += measuredWidth;
                childAt.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), BasicMeasure.EXACTLY));
            }
        }
        setMeasuredDimension(getPaddingStart() + getPaddingEnd() + i5 + i4, getMeasuredHeight() + this.mVerticalPaddingTop + this.mVerticalPaddingBottom);
    }

    private boolean measureByWideMode(int i, int i2, int i3) {
        int paddingStart = getPaddingStart() + getPaddingEnd();
        int i4 = i3 > 1 ? (i3 - 1) * this.mGapBetweenTabs : 0;
        int size = View.MeasureSpec.getSize(i);
        int i5 = (size - paddingStart) - i4;
        int childCount = getChildCount();
        int i6 = 0;
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            if (i3 <= 2) {
                childAt.setMinimumWidth(this.mSpaciousLessThanTwoItemMinWidth);
                i6 = this.mSpaciousLessThanTwoItemMinWidth;
            } else if (i3 == 3) {
                childAt.setMinimumWidth(this.mSpaciousThreeItemMinWidth);
                i6 = this.mSpaciousThreeItemMinWidth;
            } else {
                childAt.setMinimumWidth(this.mSpaciousMoreThanFourItemMinWidth);
                i6 = this.mSpaciousMoreThanFourItemMinWidth;
            }
        }
        super.onMeasure(i, i2);
        int i8 = 0;
        for (int i9 = 0; i9 < childCount; i9++) {
            View childAt2 = getChildAt(i9);
            if (!isViewGone(childAt2)) {
                int measuredWidth = childAt2.getMeasuredWidth();
                i8 += measuredWidth;
                childAt2.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), BasicMeasure.EXACTLY));
            }
        }
        this.mChildrenTotalWidth = i4 + i8;
        setMeasuredDimension(size, getMeasuredHeight() + this.mVerticalPaddingTop + this.mVerticalPaddingBottom);
        return i8 >= i5 - i6;
    }

    private void measureByCompatMode(int i, int i2, int i3) {
        this.mOverSizeViews.clear();
        this.mSmallSizeViews.clear();
        int childCount = getChildCount();
        int i4 = 0;
        for (int i5 = 0; i5 < childCount; i5++) {
            getChildAt(i5).setMinimumWidth(0);
        }
        super.onMeasure(i, i2);
        int paddingStart = getPaddingStart() + getPaddingEnd();
        int i6 = i3 > 1 ? (i3 - 1) * this.mGapBetweenTabs : 0;
        int size = View.MeasureSpec.getSize(i);
        int i7 = (size - paddingStart) - i6;
        int i8 = i7 / i3;
        int i9 = i7 % i3;
        int i10 = 0;
        int i11 = 0;
        int i12 = 0;
        int i13 = 0;
        while (i10 < childCount) {
            View childAt = getChildAt(i10);
            childAt.setMinimumWidth(i4);
            if (!isViewGone(childAt)) {
                int measuredWidth = childAt.getMeasuredWidth();
                i11 += measuredWidth;
                if (measuredWidth > i8) {
                    this.mOverSizeViews.add(childAt);
                    i13 += measuredWidth;
                } else {
                    this.mSmallSizeViews.add(childAt);
                    i12 += measuredWidth;
                }
                childAt.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), BasicMeasure.EXACTLY));
            }
            i10++;
            i4 = 0;
        }
        int measuredHeight = getMeasuredHeight() + this.mVerticalPaddingTop + this.mVerticalPaddingBottom;
        if (i11 > i7) {
            setMeasuredDimension(i11 + i6 + paddingStart, measuredHeight);
            return;
        }
        if (this.mOverSizeViews.isEmpty()) {
            int i14 = 0;
            while (i14 < childCount) {
                View childAt2 = getChildAt(i14);
                if (!isViewGone(childAt2)) {
                    childAt2.measure(View.MeasureSpec.makeMeasureSpec((i14 < i9 ? 1 : 0) + i8, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(childAt2.getMeasuredHeight(), BasicMeasure.EXACTLY));
                }
                i14++;
            }
        } else if (i12 > 0) {
            int size2 = this.mSmallSizeViews.size();
            int i15 = i7 - i13;
            for (int i16 = 0; i16 < size2; i16++) {
                View view = this.mSmallSizeViews.get(i16);
                int measuredWidth2 = (int) (((view.getMeasuredWidth() * 1.0f) / i12) * i15);
                if (!isViewGone(view)) {
                    view.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth2, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), BasicMeasure.EXACTLY));
                }
            }
        }
        setMeasuredDimension(size, measuredHeight);
    }

    private boolean isViewGone(View view) {
        return view.getVisibility() == 8;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int paddingStart;
        int i5 = i3 - i;
        int childCount = getChildCount();
        int i6 = this.mVerticalPaddingTop;
        if (this.mLayoutCenter) {
            paddingStart = getPaddingStart() + ((i5 - this.mChildrenTotalWidth) / 2);
        } else {
            paddingStart = getPaddingStart();
        }
        int i7 = paddingStart;
        for (int i8 = 0; i8 < childCount; i8++) {
            View childAt = getChildAt(i8);
            if (!isViewGone(childAt)) {
                int measuredWidth = childAt.getMeasuredWidth() + i7;
                ViewUtils.layoutChildView(this, childAt, i7, i6, measuredWidth, i6 + childAt.getMeasuredHeight());
                i7 = measuredWidth + this.mGapBetweenTabs;
            }
        }
    }

    public void setTabViewLayoutMode(int i) {
        if (this.mLayoutMode != i) {
            this.mLayoutMode = i;
            requestLayout();
        }
    }
}
