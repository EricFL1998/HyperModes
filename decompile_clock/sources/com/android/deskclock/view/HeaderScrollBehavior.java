package com.android.deskclock.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import com.android.deskclock.worldclock.WorldClockFragment;
import miuix.animation.Folme;
import miuix.animation.IFolme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.property.ViewProperty;

/* JADX INFO: loaded from: classes.dex */
public class HeaderScrollBehavior extends CoordinatorLayout.Behavior<View> {
    private static final float MAX_TEXT_SCALE = 1.5f;
    private static final float MIN_CLOCK_SCALE = 0.52f;
    private static final float NORMAL_TEXT_SCALE = 1.0f;
    public static String TAG = "DC:HeaderScrollBehavior";
    private DeskClockTabActivity mActivity;
    private View mClockBgView;
    private View mClockView;
    private float mClockViewAlpha;
    private int mClockViewHeight;
    private float mClockViewScale;
    private View mDataDisplay;
    private float mListPer;
    private View mLocalTime;
    private int mLocalTimeHeight;
    private float mLocalTimeScale;
    private TextView mTimeDisplay;
    private LinearLayout mTimeTotal;
    private float offsetY;

    public float valFromPer(float f, float f2, float f3) {
        return f2 + ((f3 - f2) * f);
    }

    public float valToPer(float f, float f2, float f3) {
        return f2 - ((f2 - f3) * f);
    }

    public float valToPerAlpha(float f, float f2, float f3) {
        return f2 - ((f2 - f3) * f);
    }

    public HeaderScrollBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.offsetY = 0.0f;
        this.mActivity = (DeskClockTabActivity) context;
        this.offsetY = context.getResources().getDimension(R.dimen.clock_view_offset_y_2);
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean layoutDependsOn(CoordinatorLayout coordinatorLayout, View view, View view2) {
        return view2.getId() == R.id.clcok_nested_scroll_view;
    }

    public void setMaxLocalTimeScale(CoordinatorLayout coordinatorLayout, View view, View view2) {
        this.mLocalTimeScale = 1.5f;
        onDependentViewChanged(coordinatorLayout, view, view2);
    }

    public void setMinLocalTimeScale(CoordinatorLayout coordinatorLayout, View view, View view2) {
        this.mLocalTimeScale = 1.0f;
        onDependentViewChanged(coordinatorLayout, view, view2);
    }

    private float calculateTextScale() {
        return Math.min(1.5f, cubicInOutScaleYPer(Math.min(1.0f, Math.max(0.0f, this.mListPer)), 1.0f, 1.5f));
    }

    private float calculateTextScrollY(float f) {
        int i = this.mClockViewHeight;
        float f2 = this.offsetY;
        return (float) Math.min(i - f2, Math.max(0.0d, yPer(this.mListPer, f, i - f2)));
    }

    private float calculateClockScale() {
        return Math.max(MIN_CLOCK_SCALE, Math.min(1.0f, scaleYPer(Math.min(1.0f, this.mListPer), 1.0f, MIN_CLOCK_SCALE)));
    }

    private float calculateClockAlpha() {
        return alphaPer(Math.max(0.0f, Math.min(1.0f, this.mListPer)), 1.0f, 0.0f);
    }

    private float calculateClockScrollY(float f) {
        return (float) sinoutYPer(Math.min(1.0f, this.mListPer), f, this.mClockViewHeight - this.offsetY);
    }

    private void setClockViewScale(float f) {
        View view = this.mClockView;
        if (view != null) {
            view.setScaleX(f);
            this.mClockView.setScaleY(f);
        }
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, View view, View view2) {
        float translationY = view2.getTranslationY();
        this.mClockView = view.findViewById(R.id.clock_view);
        this.mClockBgView = view.findViewById(R.id.clock_bg_view);
        View viewFindViewById = view.findViewById(R.id.local_time);
        this.mLocalTime = viewFindViewById;
        this.mTimeDisplay = (TextView) viewFindViewById.findViewById(R.id.time_display);
        this.mDataDisplay = this.mLocalTime.findViewById(R.id.time_total_desc);
        this.mTimeTotal = (LinearLayout) this.mLocalTime.findViewById(R.id.time_total);
        this.mClockViewHeight = this.mClockView.getHeight();
        this.mLocalTimeHeight = this.mLocalTime.getHeight();
        if (WorldClockFragment.mClockIsInActionMode) {
            this.mListPer = 1.0f;
        } else {
            this.mListPer = Math.min(1.0f, (-translationY) / (this.mClockViewHeight - this.mActivity.getResources().getDimension(R.dimen.clock_view_offset_y)));
        }
        this.mLocalTimeScale = calculateTextScale();
        this.mClockViewScale = calculateClockScale();
        this.mClockViewAlpha = calculateClockAlpha();
        if (Util.isPadOrientationLand(this.mActivity)) {
            if (!Util.isPadOrientationLand(this.mActivity)) {
                return true;
            }
            if (!Util.isInMultiWindowMode(this.mActivity) && WorldClockFragment.mClockIsInActionMode) {
                return true;
            }
        }
        textAnimate(this.mLocalTimeScale);
        clockViewAnimate(this.mClockViewScale, this.mClockViewAlpha);
        if (translationY < 0.0f) {
            float f = -translationY;
            this.mClockView.setTranslationY(-calculateClockScrollY(f));
            this.mClockBgView.setTranslationY(-calculateClockScrollY(f));
            this.mLocalTime.setTranslationY(-calculateTextScrollY(f));
            this.mDataDisplay.setTranslationY(this.mListPer * 25.0f);
            return true;
        }
        float f2 = 0.67f * translationY;
        this.mClockView.setTranslationY(f2);
        this.mClockBgView.setTranslationY(f2);
        this.mLocalTime.setTranslationY(translationY);
        return true;
    }

    private void textAnimate(float f) {
        try {
            this.mTimeTotal.clearAnimation();
            MiuiFolme.cancelFolme(this.mTimeTotal);
            double d = f;
            Folme.useAt(this.mTimeTotal).state().fromTo(new AnimState("textAnimateStart").add(ViewProperty.SCALE_X, d).add(ViewProperty.SCALE_Y, d), new AnimState("textAnimateEnd"), new AnimConfig[0]);
        } catch (Exception e) {
            Log.e(TAG, "miui folme anim error: " + e.getMessage());
        }
    }

    private void clockViewAnimate(float f, float f2) {
        try {
            this.mClockView.clearAnimation();
            this.mClockBgView.clearAnimation();
            MiuiFolme.cancelFolme(this.mClockView);
            MiuiFolme.cancelFolme(this.mClockBgView);
            double d = f;
            AnimState animStateAdd = new AnimState("clockViewAnimateStart").add(ViewProperty.ALPHA, f2).add(ViewProperty.SCALE_X, d).add(ViewProperty.SCALE_Y, d);
            AnimState animState = new AnimState("clockViewAnimateEnd");
            IFolme iFolmeUseAt = Folme.useAt(this.mClockView);
            IFolme iFolmeUseAt2 = Folme.useAt(this.mClockBgView);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(-2, 0.98f, 0.35f);
            iFolmeUseAt.state().fromTo(animStateAdd, animState, animConfig);
            iFolmeUseAt2.state().fromTo(animStateAdd, animState, animConfig);
        } catch (Exception e) {
            Log.e(TAG, "miui folme anim error: " + e.getMessage());
        }
    }

    public double yPer(float f, float f2, float f3) {
        return transYper(f, f2, f3);
    }

    public float transYper(float f, float f2, float f3) {
        return (this.mClockViewHeight - this.offsetY) * f;
    }

    public double sinoutYPer(float f, float f2, float f3) {
        return valFromPer(Math.max(0.0f, sinIn(f)), f2, f3);
    }

    public float alphaPer(float f, float f2, float f3) {
        return valToPerAlpha(Math.max(0.0f, sinIn(f)), f2, f3);
    }

    public float scaleXPer(float f, float f2, float f3, boolean z) {
        if (z) {
            return valFromPer(sinOut(f), f2, f3);
        }
        return valFromPer(sinOut(f), f2, f3);
    }

    public float scaleYPer(float f, float f2, float f3) {
        return valToPer(sinOut(f), f2, f3);
    }

    public float cubicInOutScaleYPer(float f, float f2, float f3) {
        return valFromPer(cubicInOut(f), f2, f3);
    }

    public float sinIn(double d) {
        return (float) ((-Math.cos(d * ((double) 1.5707964f))) + 1.0d);
    }

    public float sinOut(double d) {
        return (float) Math.sin(d * ((double) 1.5707964f));
    }

    public float cubicInOut(float f) {
        return ((double) f) < 0.5d ? 4.0f * f * f * f : (float) (1.0d - (Math.pow((f * (-2.0f)) + 2.0f, 3.0d) / 2.0d));
    }
}
