package miuix.appcompat.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes2.dex */
class DateTimePickerContainer extends LinearLayout {
    private int mMeasuredParentHeight;
    private boolean mWrapContent;

    public DateTimePickerContainer(Context context) {
        super(context);
        this.mMeasuredParentHeight = 0;
    }

    public DateTimePickerContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMeasuredParentHeight = 0;
    }

    public DateTimePickerContainer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMeasuredParentHeight = 0;
    }

    public DateTimePickerContainer(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mMeasuredParentHeight = 0;
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mWrapContent) {
            return;
        }
        if (this.mMeasuredParentHeight == 0) {
            this.mMeasuredParentHeight = getMeasuredHeight();
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(i), this.mMeasuredParentHeight);
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View childAt;
        super.onLayout(z, i, i2, i3, i4);
        if (this.mWrapContent || (childAt = getChildAt(1)) == null || childAt.getVisibility() != 0) {
            return;
        }
        int measuredHeight = childAt.getMeasuredHeight();
        int i5 = i4 - ((this.mMeasuredParentHeight - measuredHeight) >> 1);
        childAt.layout(i, i5 - measuredHeight, i3, i5);
    }

    void setWrapContent(boolean z) {
        if (this.mWrapContent != z) {
            this.mWrapContent = z;
            requestLayout();
        }
    }
}
