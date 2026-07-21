package com.android.deskclock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class FlowLayout extends ViewGroup {
    private List<List<View>> mAllViews;
    private List<Integer> mLineHeight;

    public FlowLayout(Context context) {
        super(context);
        this.mAllViews = new ArrayList();
        this.mLineHeight = new ArrayList();
    }

    public FlowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAllViews = new ArrayList();
        this.mLineHeight = new ArrayList();
    }

    public FlowLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAllViews = new ArrayList();
        this.mLineHeight = new ArrayList();
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new ViewGroup.MarginLayoutParams(layoutParams);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new ViewGroup.MarginLayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.MarginLayoutParams(-1, -1);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int childCount = getChildCount();
        int i3 = 0;
        int i4 = 0;
        int iMax = 0;
        int iMax2 = 0;
        int i5 = 0;
        while (i3 < childCount) {
            View childAt = getChildAt(i3);
            measureChild(childAt, i, i2);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
            int i6 = size2;
            int measuredWidth = childAt.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            int measuredHeight = childAt.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
            int i7 = i4 + measuredWidth;
            if (i7 > size) {
                iMax = Math.max(i4, iMax);
                i5 += iMax2;
                iMax2 = measuredHeight;
                i4 = measuredWidth;
            } else {
                iMax2 = Math.max(iMax2, measuredHeight);
                i4 = i7;
            }
            if (i3 == childCount - 1) {
                i5 += iMax2;
                iMax = Math.max(iMax, i4);
            }
            i3++;
            size2 = i6;
        }
        int i8 = size2;
        if (mode != 1073741824) {
            size = iMax;
        }
        setMeasuredDimension(size, mode2 == 1073741824 ? i8 : i5);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mAllViews.clear();
        this.mLineHeight.clear();
        int width = getWidth();
        ArrayList arrayList = new ArrayList();
        int childCount = getChildCount();
        int iMax = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
            if (marginLayoutParams.leftMargin + measuredWidth + marginLayoutParams.rightMargin + i5 > width) {
                this.mLineHeight.add(Integer.valueOf(iMax));
                this.mAllViews.add(arrayList);
                arrayList = new ArrayList();
                i5 = 0;
            }
            i5 += measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            iMax = Math.max(iMax, measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin);
            arrayList.add(childAt);
        }
        this.mLineHeight.add(Integer.valueOf(iMax));
        this.mAllViews.add(arrayList);
        int size = this.mAllViews.size();
        int i7 = 0;
        for (int i8 = 0; i8 < size; i8++) {
            List<View> list = this.mAllViews.get(i8);
            int iIntValue = this.mLineHeight.get(i8).intValue();
            int measuredWidth2 = 0;
            for (int i9 = 0; i9 < list.size(); i9++) {
                View view = list.get(i9);
                if (view.getVisibility() != 8) {
                    ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    int i10 = marginLayoutParams2.leftMargin + measuredWidth2;
                    int i11 = marginLayoutParams2.topMargin + i7;
                    view.layout(i10, i11, view.getMeasuredWidth() + i10, view.getMeasuredHeight() + i11);
                    measuredWidth2 += view.getMeasuredWidth() + marginLayoutParams2.rightMargin + marginLayoutParams2.leftMargin;
                }
            }
            i7 += iIntValue;
        }
    }
}
