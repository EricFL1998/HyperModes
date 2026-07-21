package miuix.pickerwidget.widget.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import miuix.pickerwidget.R;

/* JADX INFO: loaded from: classes3.dex */
class CalendarSingleDateContainer extends LinearLayout {
    private int mMaxWidth;

    public CalendarSingleDateContainer(Context context) {
        super(context);
        getMaxWidth(context);
    }

    public CalendarSingleDateContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getMaxWidth(context);
    }

    public CalendarSingleDateContainer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        getMaxWidth(context);
    }

    public CalendarSingleDateContainer(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        getMaxWidth(context);
    }

    private void getMaxWidth(Context context) {
        this.mMaxWidth = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_calendar_grid_layout_child_container_width);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        int paddingTop = getPaddingTop() + getPaddingBottom();
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                measureChild(childAt, i, i2);
                paddingTop += childAt.getMeasuredHeight();
            }
        }
        int i4 = this.mMaxWidth;
        if (size > i4) {
            size = i4;
        }
        if (paddingTop < size) {
            paddingTop = size;
        }
        setMeasuredDimension(size, paddingTop);
    }
}
