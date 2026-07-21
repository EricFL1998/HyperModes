package com.android.deskclock.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.util.AnimationUtils;
import com.android.deskclock.util.Util;
import com.android.deskclock.worldclock.WorldClockFragment;
import miuix.core.util.MiuixUIUtils;

/* JADX INFO: loaded from: classes.dex */
public class NestedContentScrollBehavior extends CoordinatorLayout.Behavior<View> {
    public static String TAG = "DC:NestedContentScrollBehavior";
    private int TimeTextHeight;
    private int headerHeight;
    private HeaderScrollBehavior headerScrollBehavior;
    private DeskClockTabActivity mActivity;
    private View mClockView;
    private int mClockViewHeight;
    private View mHeadView;
    private boolean mIsFlingUp;
    private View mLocalTime;
    private TextView mLocalTimeDisplay;
    private boolean mTouchUp;
    private float offsetY;

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View view, View view2, int i, int i2, int i3, int i4, int i5, int[] iArr) {
    }

    public NestedContentScrollBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.headerHeight = 0;
        this.TimeTextHeight = 0;
        this.mClockViewHeight = 0;
        this.mTouchUp = false;
        this.offsetY = 0.0f;
        this.mIsFlingUp = false;
        this.offsetY = context.getResources().getDimension(R.dimen.clock_view_offset_y);
    }

    public void initContext(Context context) {
        this.mActivity = (DeskClockTabActivity) context;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, View view, int i) {
        coordinatorLayout.onLayoutChild(view, i);
        this.headerHeight = coordinatorLayout.findViewById(R.id.clock_head_view).getHeight();
        this.TimeTextHeight = coordinatorLayout.findViewById(R.id.local_time).getHeight();
        this.mClockViewHeight = coordinatorLayout.findViewById(R.id.clock_view).getHeight();
        if (MiuixUIUtils.getFontLevel(DeskClockApp.getAppDEContext()) == 2) {
            ViewCompat.offsetTopAndBottom(view, this.headerHeight - ((int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.world_clock_list_top_margin_font_level_large)));
            return true;
        }
        ViewCompat.offsetTopAndBottom(view, this.headerHeight - ((int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.world_clock_list_top_margin)));
        return true;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View view, View view2, View view3, int i, int i2) {
        this.mIsFlingUp = false;
        return (i & 2) != 0;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View view, View view2, int i, int i2, int[] iArr, int i3) {
        if ((!Util.isPadOrientationLand(this.mActivity) || Util.isInMultiWindowMode(this.mActivity)) && !WorldClockFragment.mClockIsInActionMode) {
            super.onNestedPreScroll(coordinatorLayout, view, view2, i, i2, iArr, i3);
            if (i2 > 0) {
                float translationY = view.getTranslationY() - i2;
                this.TimeTextHeight = coordinatorLayout.findViewById(R.id.local_time).getMeasuredHeight();
                if (i2 > 8) {
                    this.mTouchUp = true;
                }
                int i4 = this.mClockViewHeight;
                float f = this.offsetY;
                if (translationY >= (-i4) + f) {
                    iArr[1] = i2;
                    if (this.mIsFlingUp) {
                        return;
                    }
                    view.setTranslationY(translationY);
                    return;
                }
                iArr[1] = (int) ((i4 - f) + view.getTranslationY());
                if (this.mIsFlingUp) {
                    return;
                }
                view.setTranslationY((-this.mClockViewHeight) + this.offsetY);
                return;
            }
            float f2 = i2;
            float translationY2 = view.getTranslationY() - f2;
            if (view.getTranslationY() > 0.0f || f2 <= view.getTranslationY()) {
                if (translationY2 > 0.0f && translationY2 < this.offsetY / 2.0f && i3 == 0) {
                    iArr[1] = 5;
                    view.setTranslationY(view.getTranslationY() + 5.0f);
                }
            } else if (translationY2 <= 0.0f && i3 == 0) {
                iArr[1] = 5;
                view.setTranslationY(view.getTranslationY() + 5.0f);
            }
            if (i2 < -8) {
                this.mTouchUp = false;
            }
        }
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View view, View view2, int i) {
        this.mIsFlingUp = false;
        if (view2.getTranslationY() != 0.0f) {
            super.onStopNestedScroll(coordinatorLayout, view, view2, i);
            View viewFindViewById = coordinatorLayout.findViewById(R.id.clock_head_view);
            this.mHeadView = viewFindViewById;
            this.headerScrollBehavior = (HeaderScrollBehavior) ((CoordinatorLayout.LayoutParams) viewFindViewById.getLayoutParams()).getBehavior();
            this.mClockView = coordinatorLayout.findViewById(R.id.clock_view);
            View viewFindViewById2 = coordinatorLayout.findViewById(R.id.local_time);
            this.mLocalTime = viewFindViewById2;
            this.mLocalTimeDisplay = (TextView) viewFindViewById2.findViewById(R.id.time_display);
            float translationY = view2.getTranslationY();
            if (this.headerScrollBehavior != null) {
                boolean z = this.mTouchUp;
                if (z && i == 0) {
                    if (translationY <= (-this.mClockViewHeight) + this.offsetY || WorldClockFragment.mIsAnimRunning) {
                        return;
                    }
                    view.animate().cancel();
                    this.mIsFlingUp = true;
                    AnimationUtils.animateTranslateY(view, translationY, (-this.mClockViewHeight) + this.offsetY, 250L);
                    view.requestLayout();
                    return;
                }
                if (z || translationY <= (-this.mClockViewHeight) + this.offsetY || WorldClockFragment.mIsAnimRunning) {
                    return;
                }
                view.animate().cancel();
                AnimationUtils.animateTranslateY(view, translationY, 0.0f, 250L);
                view.requestLayout();
            }
        }
    }
}
