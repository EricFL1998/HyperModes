package miuix.appcompat.internal.view.menu.action;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import miuix.appcompat.R;
import miuix.internal.util.DeviceHelper;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class EndActionMenuView extends ActionMenuView {
    private int mActionCount;
    private Context mContext;
    private int mMaxActionButtonWidth;
    private int mMenuItemGap;
    private int mMenuItemHeight;
    private int mMenuItemWidth;
    private int mStartPadding;

    private boolean isNotActionMenuItemChild(View view) {
        return false;
    }

    @Override // miuix.view.BlurableWidget
    public void applyBlur(boolean z) {
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, miuix.appcompat.internal.view.menu.MenuView
    public boolean hasBackgroundView() {
        return false;
    }

    @Override // miuix.view.BlurableWidget
    public boolean isApplyBlur() {
        return false;
    }

    @Override // miuix.view.BlurableWidget
    public boolean isEnableBlur() {
        return false;
    }

    @Override // miuix.view.BlurableWidget
    public boolean isSupportBlur() {
        return false;
    }

    @Override // miuix.view.BlurableWidget
    public void setEnableBlur(boolean z) {
    }

    @Override // miuix.view.BlurableWidget
    public void setSupportBlur(boolean z) {
    }

    public EndActionMenuView(Context context) {
        this(context, null);
    }

    public EndActionMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMaxActionButtonWidth = 0;
        this.mMenuItemGap = 0;
        this.mStartPadding = 0;
        this.mActionCount = 0;
        super.setBackground(null);
        this.mContext = context;
        this.mMenuItemGap = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_end_menu_button_gap);
        this.mStartPadding = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_bar_title_view_padding_horizontal);
        this.mMaxActionButtonWidth = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_button_max_width);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, miuix.appcompat.internal.view.menu.MenuView
    public boolean filterLeftoverView(int i) {
        ActionMenuView.LayoutParams layoutParams;
        View childAt = getChildAt(i);
        return !isNotActionMenuItemChild(childAt) && ((layoutParams = (ActionMenuView.LayoutParams) childAt.getLayoutParams()) == null || !layoutParams.isOverflowButton) && super.filterLeftoverView(i);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    public int getCollapsedHeight() {
        return this.mMenuItemHeight;
    }

    private int getActionMenuItemCount() {
        return getChildCount();
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView, android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int childCount = getChildCount();
        int actionMenuItemCount = getActionMenuItemCount();
        this.mActionCount = actionMenuItemCount;
        if (childCount == 0 || actionMenuItemCount == 0) {
            this.mMenuItemHeight = 0;
            setMeasuredDimension(0, 0);
            return;
        }
        int size = View.MeasureSpec.getSize(i);
        int iMin = Math.min(size / this.mActionCount, this.mMaxActionButtonWidth);
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(iMin, Integer.MIN_VALUE);
        int iMin2 = 0;
        int iMax = 0;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (!isNotActionMenuItemChild(childAt)) {
                measureChildWithMargins(childAt, iMakeMeasureSpec, 0, i2, 0);
                iMin2 += Math.min(childAt.getMeasuredWidth(), iMin);
                iMax = Math.max(iMax, childAt.getMeasuredHeight());
            }
        }
        int i4 = this.mMenuItemGap * (this.mActionCount - 1);
        int i5 = this.mStartPadding;
        if (i5 + iMin2 + i4 > size) {
            this.mMenuItemGap = 0;
        }
        int i6 = iMin2 + i4 + i5;
        this.mMenuItemWidth = i6;
        this.mMenuItemHeight = iMax;
        setMeasuredDimension(i6, iMax);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i4 - i2;
        int childCount = getChildCount();
        int measuredWidth = this.mStartPadding;
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            if (!isNotActionMenuItemChild(childAt)) {
                ViewUtils.layoutChildView(this, childAt, measuredWidth, 0, measuredWidth + childAt.getMeasuredWidth(), i5);
                measuredWidth += childAt.getMeasuredWidth() + this.mMenuItemGap;
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent);
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
        if (DeviceHelper.isFeatureWholeAnim()) {
            setAlpha(computeAlpha(f, z, z2));
        }
        float fComputeTranslationY = computeTranslationY(f, z, z2);
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (!isNotActionMenuItemChild(childAt)) {
                childAt.setTranslationY(fComputeTranslationY);
            }
        }
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView
    public ActionMenuView.LayoutParams generateOverflowButtonLayoutParams(View view) {
        ActionMenuView.LayoutParams layoutParamsGenerateLayoutParams = generateLayoutParams(view.getLayoutParams());
        layoutParamsGenerateLayoutParams.isOverflowButton = true;
        return layoutParamsGenerateLayoutParams;
    }
}
