package com.android.deskclock.view;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import com.android.deskclock.R;

/* JADX INFO: loaded from: classes.dex */
public class ClockAnimations {
    public static final int ALERT_POINT_ROTATE_DURATION = 600;
    public static final int ALERT_POINT_SCALE_IN_DELAY = 500;
    public static final int ALERT_POINT_SCALE_IN_DURATION = 300;
    public static final int CLOCK_EXIT_ROTATE_DURATION = 400;
    public static final int CLOCK_RESUME_ROTATE_DURATION = 650;
    public static final int COLOR_ANIMATION_DEFAULT_DURATION = 300;
    public static final int STOPWATCH_LAP_ANIMATION_DELAY = 180;
    public static final int STOPWATCH_LAP_ANIMATION_DURATION = 350;
    public static final int STOPWATCH_LAP_POINT_FADE_DELAY = 300;
    public static final int STOPWATCH_RESET_DURATION = 400;
    public static final int TIMER_FLING_START_STOP_DURATION = 500;
    public static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator();
    public static final OvershootInterpolator STOPWATCH_LAP_OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(5.0f);

    public static void scaleIncreaseOvershoot(Context context, View view) {
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.analog_scale_increase_overshoot));
        }
    }

    public static void scaleDecreaseOvershoot(Context context, View view) {
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.analog_scale_decrease_overshoot));
        }
    }

    public static void scaleIncreaseOvershootFromZero(Context context, View view) {
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.analog_scale_increase_overshoot_from_zero));
        }
    }

    public static void scaleDecreaseToZero(Context context, View view) {
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.analog_scale_decrease_to_zero));
        }
    }

    public static void fadeInWhenVisible(Context context, View... viewArr) {
        for (View view : viewArr) {
            if (view != null && view.getVisibility() == 0) {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
            }
        }
    }

    public static void fadeOutWhenVisible(Context context, View view) {
        if (view == null || view.getVisibility() != 0) {
            return;
        }
        fadeOut(context, view);
    }

    public static void fadeOut(Context context, View view) {
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
        }
    }

    public static void clearAnimationAndHide(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.clearAnimation();
                view.setVisibility(4);
            }
        }
    }

    public static void show(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.setVisibility(0);
            }
        }
    }
}
