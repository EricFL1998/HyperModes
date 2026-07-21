package com.google.android.material.bottomnavigation;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.ViewCompat;
import com.google.android.material.R;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarMenuView;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class BottomNavigationMenuView extends NavigationBarMenuView {
    private final int activeItemMaxWidth;
    private final int activeItemMinWidth;
    private final int inactiveItemMaxWidth;
    private final int inactiveItemMinWidth;
    private boolean itemHorizontalTranslationEnabled;
    private final List<Integer> tempChildWidths;

    public BottomNavigationMenuView(Context context) {
        super(context);
        this.tempChildWidths = new ArrayList();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        setLayoutParams(layoutParams);
        Resources resources = getResources();
        this.inactiveItemMaxWidth = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_item_max_width);
        this.inactiveItemMinWidth = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_item_min_width);
        this.activeItemMaxWidth = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_active_item_max_width);
        this.activeItemMinWidth = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_active_item_min_width);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        MenuBuilder menu = getMenu();
        int size = View.MeasureSpec.getSize(i);
        int size2 = menu.getVisibleItems().size();
        int childCount = getChildCount();
        this.tempChildWidths.clear();
        int size3 = View.MeasureSpec.getSize(i2);
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size3, BasicMeasure.EXACTLY);
        if (isShifting(getLabelVisibilityMode(), size2) && isItemHorizontalTranslationEnabled()) {
            View childAt = getChildAt(getSelectedItemPosition());
            int iMax = this.activeItemMinWidth;
            if (childAt.getVisibility() != 8) {
                childAt.measure(View.MeasureSpec.makeMeasureSpec(this.activeItemMaxWidth, Integer.MIN_VALUE), iMakeMeasureSpec);
                iMax = Math.max(iMax, childAt.getMeasuredWidth());
            }
            int i5 = size2 - (childAt.getVisibility() != 8 ? 1 : 0);
            int iMin = Math.min(size - (this.inactiveItemMinWidth * i5), Math.min(iMax, this.activeItemMaxWidth));
            int i6 = size - iMin;
            int iMin2 = Math.min(i6 / (i5 != 0 ? i5 : 1), this.inactiveItemMaxWidth);
            int i7 = i6 - (i5 * iMin2);
            int i8 = 0;
            while (i8 < childCount) {
                if (getChildAt(i8).getVisibility() != 8) {
                    i4 = i8 == getSelectedItemPosition() ? iMin : iMin2;
                    if (i7 > 0) {
                        i4++;
                        i7--;
                    }
                } else {
                    i4 = 0;
                }
                this.tempChildWidths.add(Integer.valueOf(i4));
                i8++;
            }
        } else {
            int iMin3 = Math.min(size / (size2 != 0 ? size2 : 1), this.activeItemMaxWidth);
            int i9 = size - (size2 * iMin3);
            for (int i10 = 0; i10 < childCount; i10++) {
                if (getChildAt(i10).getVisibility() == 8) {
                    i3 = 0;
                } else if (i9 > 0) {
                    i3 = iMin3 + 1;
                    i9--;
                } else {
                    i3 = iMin3;
                }
                this.tempChildWidths.add(Integer.valueOf(i3));
            }
        }
        int measuredWidth = 0;
        for (int i11 = 0; i11 < childCount; i11++) {
            View childAt2 = getChildAt(i11);
            if (childAt2.getVisibility() != 8) {
                childAt2.measure(View.MeasureSpec.makeMeasureSpec(this.tempChildWidths.get(i11).intValue(), BasicMeasure.EXACTLY), iMakeMeasureSpec);
                childAt2.getLayoutParams().width = childAt2.getMeasuredWidth();
                measuredWidth += childAt2.getMeasuredWidth();
            }
        }
        setMeasuredDimension(measuredWidth, size3);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int measuredWidth = 0;
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            if (childAt.getVisibility() != 8) {
                if (ViewCompat.getLayoutDirection(this) == 1) {
                    int i8 = i5 - measuredWidth;
                    childAt.layout(i8 - childAt.getMeasuredWidth(), 0, i8, i6);
                } else {
                    childAt.layout(measuredWidth, 0, childAt.getMeasuredWidth() + measuredWidth, i6);
                }
                measuredWidth += childAt.getMeasuredWidth();
            }
        }
    }

    public void setItemHorizontalTranslationEnabled(boolean z) {
        this.itemHorizontalTranslationEnabled = z;
    }

    public boolean isItemHorizontalTranslationEnabled() {
        return this.itemHorizontalTranslationEnabled;
    }

    @Override // com.google.android.material.navigation.NavigationBarMenuView
    protected NavigationBarItemView createNavigationBarItemView(Context context) {
        return new BottomNavigationItemView(context);
    }
}
