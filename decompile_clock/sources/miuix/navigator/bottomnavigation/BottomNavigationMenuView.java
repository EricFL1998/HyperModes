package miuix.navigator.bottomnavigation;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.FrameLayout;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.List;
import miuix.appcompat.view.menu.MenuBuilder;
import miuix.core.util.MiuixUIUtils;
import miuix.navigator.R;
import miuix.navigator.navigation.NavigationBarItemView;
import miuix.navigator.navigation.NavigationBarMenuView;

/* JADX INFO: loaded from: classes3.dex */
public class BottomNavigationMenuView extends NavigationBarMenuView {
    private final int activeItemMaxWidth;
    private final int activeItemMinWidth;
    private final int inactiveItemMaxWidth;
    private final int inactiveItemMinWidth;
    private boolean itemHorizontalTranslationEnabled;
    private int itemMargin;
    private final int itemMarginDp;
    private int paddingHorizontal;
    private final int paddingHorizontalDefaultDp;
    private final List<Integer> tempChildWidths;

    public boolean filterLeftoverView(int i) {
        return false;
    }

    public boolean hasBackgroundView() {
        return false;
    }

    public boolean hasBlurBackgroundView() {
        return false;
    }

    public BottomNavigationMenuView(Context context) {
        super(context);
        this.itemMarginDp = 11;
        this.paddingHorizontalDefaultDp = 8;
        this.tempChildWidths = new ArrayList();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        setLayoutParams(layoutParams);
        Resources resources = getResources();
        this.inactiveItemMaxWidth = resources.getDimensionPixelSize(R.dimen.miuix_design_bottom_navigation_item_max_width);
        this.inactiveItemMinWidth = resources.getDimensionPixelSize(R.dimen.miuix_design_bottom_navigation_item_min_width);
        this.activeItemMaxWidth = resources.getDimensionPixelSize(R.dimen.miuix_design_bottom_navigation_active_item_max_width);
        this.activeItemMinWidth = resources.getDimensionPixelSize(R.dimen.miuix_design_bottom_navigation_active_item_min_width);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int iDp2px;
        int i3;
        int i4;
        MenuBuilder menu = getMenu();
        int size = View.MeasureSpec.getSize(i);
        int size2 = menu != null ? menu.getVisibleItems().size() : 0;
        int childCount = getChildCount();
        float f = getContext().getResources().getDisplayMetrics().density;
        this.itemMargin = MiuixUIUtils.dp2px(f, 11.0f);
        this.tempChildWidths.clear();
        if (isLayoutInWideStyle()) {
            double d = size2;
            iDp2px = (int) (((((0.0125d * d) * d) - (d * 0.1225d)) + 0.355d) * ((double) size));
        } else {
            iDp2px = MiuixUIUtils.dp2px(f, 8.0f);
        }
        this.paddingHorizontal = iDp2px;
        int i5 = (size - (iDp2px * 2)) - (this.itemMargin * (size2 - 1));
        int i6 = (int) ((i5 * 1.0f) / size2);
        int size3 = View.MeasureSpec.getSize(i2);
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size3, BasicMeasure.EXACTLY);
        if (isShifting(getLabelVisibilityMode(), size2) && isItemHorizontalTranslationEnabled()) {
            View childAt = getChildAt(getSelectedItemPosition());
            if (childAt.getVisibility() != 8) {
                childAt.measure(View.MeasureSpec.makeMeasureSpec(i6, BasicMeasure.EXACTLY), iMakeMeasureSpec);
            }
            int i7 = size2 - (childAt.getVisibility() != 8 ? 1 : 0);
            int i8 = i7 * i6;
            int i9 = (i5 - i6) - (i7 * i8);
            int i10 = 0;
            while (i10 < childCount) {
                if (getChildAt(i10).getVisibility() != 8) {
                    i4 = i10 == getSelectedItemPosition() ? i6 : i8;
                    if (i9 > 0) {
                        i4++;
                        i9--;
                    }
                } else {
                    i4 = 0;
                }
                this.tempChildWidths.add(Integer.valueOf(i4));
                i10++;
            }
        } else {
            int i11 = i5 - (size2 * i6);
            for (int i12 = 0; i12 < childCount; i12++) {
                if (getChildAt(i12).getVisibility() == 8) {
                    i3 = 0;
                } else if (i11 > 0) {
                    i3 = i6 + 1;
                    i11--;
                } else {
                    i3 = i6;
                }
                this.tempChildWidths.add(Integer.valueOf(i3));
            }
        }
        int measuredWidth = 0;
        for (int i13 = 0; i13 < childCount; i13++) {
            View childAt2 = getChildAt(i13);
            if (childAt2.getVisibility() != 8) {
                childAt2.measure(View.MeasureSpec.makeMeasureSpec(this.tempChildWidths.get(i13).intValue(), BasicMeasure.EXACTLY), iMakeMeasureSpec);
                childAt2.getLayoutParams().width = childAt2.getMeasuredWidth();
                measuredWidth += childAt2.getMeasuredWidth();
                if (i13 > 0) {
                    measuredWidth += this.itemMargin;
                }
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
                if (i7 > 0) {
                    measuredWidth += this.itemMargin;
                }
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

    @Override // miuix.navigator.navigation.NavigationBarMenuView
    protected NavigationBarItemView createNavigationBarItemView(Context context) {
        return new BottomNavigationItemView(context, getLayoutStyle());
    }
}
