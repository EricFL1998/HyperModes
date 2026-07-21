package miuix.appcompat.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class NestedScrollViewExpander extends ViewGroup {
    private View mExpandView;
    private int mMinCustomVisibleHeight;
    private int mParentHeightMeasureSpec;

    public NestedScrollViewExpander(Context context) {
        super(context);
        this.mMinCustomVisibleHeight = 0;
    }

    public NestedScrollViewExpander(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMinCustomVisibleHeight = 0;
    }

    public NestedScrollViewExpander(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMinCustomVisibleHeight = 0;
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new ViewGroup.MarginLayoutParams(getContext(), attributeSet);
    }

    void setParentHeightMeasureSpec(int i) {
        this.mParentHeightMeasureSpec = i;
    }

    public void setMinCustomVisibleHeight(int i) {
        this.mMinCustomVisibleHeight = i;
    }

    public void setExpandView(View view) {
        this.mExpandView = view;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int measuredHeight;
        View view;
        int mode = View.MeasureSpec.getMode(this.mParentHeightMeasureSpec);
        if (mode == 0) {
            mode = Integer.MIN_VALUE;
        }
        int i3 = mode;
        int size = View.MeasureSpec.getSize(i);
        int childCount = getChildCount();
        int measuredHeight2 = 0;
        boolean z = false;
        boolean z2 = false;
        int measuredHeight3 = 0;
        for (int i4 = 0; i4 < childCount; i4++) {
            View childAt = getChildAt(i4);
            if (childAt.getVisibility() != 8) {
                if (this.mExpandView != childAt) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
                    view = childAt;
                    measureChildWithMargins(childAt, i, 0, i2, 0);
                    measuredHeight2 += view.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
                    if (view.getId() == R.id.contentView) {
                        measuredHeight3 = view.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
                        z = true;
                    }
                } else {
                    view = childAt;
                }
                if (view.getId() == R.id.buttonPanel) {
                    z2 = true;
                }
            }
        }
        int size2 = View.MeasureSpec.getSize(this.mParentHeightMeasureSpec);
        int minimumHeight = size2 - measuredHeight2;
        View view2 = this.mExpandView;
        if (view2 == null || view2.getVisibility() == 8) {
            measuredHeight = 0;
        } else {
            if (minimumHeight < this.mExpandView.getMinimumHeight()) {
                minimumHeight = this.mExpandView.getMinimumHeight();
            }
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.mExpandView.getLayoutParams();
            if (z && !z2 && measuredHeight3 + this.mMinCustomVisibleHeight >= size2) {
                measureChildWithMargins(this.mExpandView, i, 0, i2, 0);
            } else {
                measureChildWithMargins(this.mExpandView, i, 0, View.MeasureSpec.makeMeasureSpec(minimumHeight, i3), 0);
            }
            measuredHeight = this.mExpandView.getMeasuredHeight() + marginLayoutParams2.topMargin + marginLayoutParams2.bottomMargin;
        }
        setMeasuredDimension(size, measuredHeight + measuredHeight2);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
            int i6 = (((((i3 - i) - measuredWidth) / 2) + i) + marginLayoutParams.leftMargin) - marginLayoutParams.rightMargin;
            int i7 = marginLayoutParams.topMargin + i2;
            childAt.layout(i6, i7, measuredWidth + i6, i7 + measuredHeight);
            i2 = i2 + marginLayoutParams.topMargin + measuredHeight + marginLayoutParams.bottomMargin;
        }
    }
}
