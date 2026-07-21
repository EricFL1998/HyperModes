package miuix.appcompat.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import miuix.appcompat.R;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
class CalendarFlexibleLayout extends ViewGroup {
    private View mDateView;
    private int mDateViewHeight;
    private int mDateViewWidth;
    private int mGapEnd;
    private int mGapVertical;
    private int mLayoutType;
    private int mParentWidth;
    private View mTimeView;
    private int mTimeViewHeight;
    private int mTimeViewWidth;
    private View mTitleView;
    private int mTitleViewHeight;
    private int mTitleViewWidth;
    private View mVirtualDateView;
    private View mVirtualTimeView;

    public CalendarFlexibleLayout(Context context) {
        this(context, null);
    }

    public CalendarFlexibleLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CalendarFlexibleLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public CalendarFlexibleLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mGapEnd = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_calendar_date_picker_dialog_date_view_gap_end);
        this.mGapVertical = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_calendar_date_picker_dialog_date_view_gap_vertical);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        this.mLayoutType = getLayoutTypeAfterMeasureChildren(i, i2);
        setMeasuredDimension(this.mParentWidth, getMeasuredHeightRaw());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int paddingLeft = getPaddingLeft() + i;
        int paddingTop = getPaddingTop() + i2;
        int paddingRight = i3 - getPaddingRight();
        int paddingBottom = i4 - getPaddingBottom();
        if (this.mLayoutType == LayoutType.SINGLE_LINE) {
            int i5 = this.mTitleViewHeight;
            int i6 = paddingTop + (((paddingBottom - paddingTop) - i5) >> 1);
            ViewUtils.layoutChildView(this, this.mTitleView, paddingLeft, i6, paddingLeft + this.mTitleViewWidth, i6 + i5);
            ViewUtils.layoutChildView(this, this.mTimeView, paddingRight - this.mTimeViewWidth, paddingTop, paddingRight, paddingTop + this.mTimeViewHeight);
            int i7 = (paddingRight - this.mTimeViewWidth) - this.mGapEnd;
            ViewUtils.layoutChildView(this, this.mDateView, i7 - this.mDateViewWidth, paddingTop, i7, paddingTop + this.mDateViewHeight);
            return;
        }
        ViewUtils.layoutChildView(this, this.mTitleView, paddingLeft, paddingTop, paddingLeft + this.mTitleViewWidth, paddingTop + this.mTitleViewHeight);
        int i8 = paddingTop + this.mTitleViewHeight + this.mGapVertical;
        ViewUtils.layoutChildView(this, this.mDateView, paddingLeft, i8, paddingLeft + this.mDateViewWidth, i8 + this.mDateViewHeight);
        int i9 = paddingLeft + this.mDateViewWidth + this.mGapEnd;
        ViewUtils.layoutChildView(this, this.mTimeView, i9, i8, i9 + this.mTimeViewWidth, i8 + this.mTimeViewHeight);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTitleView = findViewById(R.id.dateTimePanelTitle);
        this.mDateView = findViewById(R.id.dateView);
        this.mTimeView = findViewById(R.id.timeView);
        this.mVirtualDateView = findViewById(R.id.virtualDateView);
        this.mVirtualTimeView = findViewById(R.id.virtualTimeView);
    }

    private void getChildrenMeasuredSize() {
        this.mTitleViewWidth = this.mTitleView.getMeasuredWidth();
        this.mTitleViewHeight = this.mTitleView.getMeasuredHeight();
        this.mDateViewWidth = this.mDateView.getMeasuredWidth();
        this.mDateViewHeight = this.mDateView.getMeasuredHeight();
        this.mTimeViewWidth = this.mTimeView.getMeasuredWidth();
        this.mTimeViewHeight = this.mTimeView.getMeasuredHeight();
    }

    private int getLayoutTypeAfterMeasureChildren(int i, int i2) {
        measureChildren(i, i2);
        getChildrenMeasuredSize();
        this.mParentWidth = View.MeasureSpec.getSize(i);
        int measuredWidth = this.mVirtualDateView.getMeasuredWidth();
        int measuredWidth2 = this.mVirtualTimeView.getMeasuredWidth();
        if (this.mTitleViewWidth + measuredWidth + measuredWidth2 + this.mGapEnd <= (this.mParentWidth - getPaddingLeft()) - getPaddingRight()) {
            return LayoutType.SINGLE_LINE;
        }
        return LayoutType.DOUBLE_LINES;
    }

    private int getMeasuredHeightRaw() {
        int iMax;
        if (this.mLayoutType == LayoutType.SINGLE_LINE) {
            iMax = Math.max(this.mTitleViewHeight, Math.max(this.mDateViewHeight, this.mTimeViewHeight));
        } else {
            iMax = this.mTitleViewHeight + this.mGapVertical + Math.max(this.mDateViewHeight, this.mTimeViewHeight);
        }
        return iMax + getPaddingTop() + getPaddingBottom();
    }

    static class LayoutType {
        static int DOUBLE_LINES = 1;
        static int SINGLE_LINE;

        LayoutType() {
        }
    }
}
