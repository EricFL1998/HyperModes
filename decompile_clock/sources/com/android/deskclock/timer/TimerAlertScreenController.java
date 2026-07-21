package com.android.deskclock.timer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.android.deskclock.Alarm;
import com.android.deskclock.R;
import com.android.deskclock.R2;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.alarm.alert.AlertScreenController;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import miuix.appcompat.widget.Button;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;
import miuix.theme.token.hypermaterial.Frosted;

/* JADX INFO: loaded from: classes.dex */
public class TimerAlertScreenController extends AlertScreenController {
    private static final String TAG = "DC:TimerAlertScreenController";
    private LottieAnimationView bellAnim;
    private View mAlertClock;
    private Context mContext;
    private Button mDismiss;
    private Handler mHandler;
    private ValueAnimator mRippleAnimator;
    private TextView mTimeDisplay;
    private Alarm mTimerAlarm;
    private RelativeLayout mTimerBellRippleAnim;
    private TextView mTimerDuration;
    private TextView mTimerLabel;
    private View rippleView1;
    private View rippleView2;
    private View rippleView3;

    public TimerAlertScreenController(Context context, View view, Alarm alarm) {
        super(context, view, alarm.label);
        this.mContext = context;
        this.mTimerAlarm = alarm;
        this.mTimeDisplay = (TextView) view.findViewById(R.id.time_display);
        this.mTimerDuration = (TextView) view.findViewById(R.id.timer_duration);
        this.mAlertClock = view.findViewById(R.id.alert_clock);
        this.mDismiss = (Button) view.findViewById(R.id.dismiss_btn);
        if (isSupportHyperMaterial()) {
            this.mDismiss.setMaterial(MaterialConfig.create(Frosted.Colored_Thin_Light));
        } else {
            this.mDismiss.setMaterial((MaterialDayNightConfig) null);
        }
        this.mTimerBellRippleAnim = (RelativeLayout) view.findViewById(R.id.timer_bell_ripple_anim);
        this.rippleView1 = view.findViewById(R.id.alert_ripple1);
        this.rippleView2 = view.findViewById(R.id.alert_ripple2);
        this.rippleView3 = view.findViewById(R.id.alert_ripple3);
        this.bellAnim = (LottieAnimationView) view.findViewById(R.id.bell_anim);
        this.mTimerLabel = (TextView) view.findViewById(R.id.timer_alert_label);
        this.mHandler = new Handler();
        if (MiuiSdk.isLiteOrMiddleMode()) {
            this.rippleView1.setVisibility(8);
            this.rippleView2.setVisibility(8);
            this.rippleView3.setVisibility(8);
        } else {
            resetRippleAnim(this.rippleView1, this.rippleView2, this.rippleView3);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.bellAnim.getLayoutParams();
        if (Util.isTinyScreen(this.mContext)) {
            layoutParams.height = (int) this.mContext.getResources().getDimension(R.dimen.timer_bell_lottie_tiny_width);
            layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_bell_lottie_tiny_width);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mTimeDisplay.getLayoutParams();
            layoutParams2.bottomMargin = (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_display_tiny_marginBottom);
            layoutParams2.height = (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_display_height);
            this.mTimeDisplay.setLayoutParams(layoutParams2);
            this.mTimeDisplay.setTextSize(0, (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_display_text_size));
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mTimerBellRippleAnim.getLayoutParams();
            layoutParams3.topMargin = (int) this.mContext.getResources().getDimension(R.dimen.timer_alert_anim_tiny_margin_bottom);
            this.mTimerBellRippleAnim.setLayoutParams(layoutParams3);
        }
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void init() {
        Button button = this.mDismiss;
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.timer.TimerAlertScreenController.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Log.f(TimerAlertScreenController.TAG, "timer click button dismiss");
                    TimerAlertScreenController.this.dismiss();
                }
            });
        }
        this.mTimerDuration.setText(Util.formatTimerDuration(this.mContext));
        if (!TextUtils.isEmpty(this.mTimerAlarm.label)) {
            this.mTimerLabel.setText(this.mTimerAlarm.label);
        }
        if (MiuiSdk.isLiteOrMiddleMode()) {
            return;
        }
        doTimerAnimation();
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void release() {
        this.bellAnim.cancelAnimation();
        ValueAnimator valueAnimator = this.mRippleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mRippleAnimator.removeAllUpdateListeners();
            this.mRippleAnimator = null;
            this.mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void doTimerAnimation() {
        if (this.mRippleAnimator == null) {
            ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(0, R2.attr.tabPaddingEnd);
            this.mRippleAnimator = valueAnimatorOfInt;
            valueAnimatorOfInt.setInterpolator(new LinearInterpolator());
            this.mRippleAnimator.setDuration(2250L);
            this.mRippleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.timer.TimerAlertScreenController.2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int iIntValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                    TimerAlertScreenController timerAlertScreenController = TimerAlertScreenController.this;
                    timerAlertScreenController.handleRippleAnim(iIntValue, timerAlertScreenController.rippleView1);
                    TimerAlertScreenController timerAlertScreenController2 = TimerAlertScreenController.this;
                    timerAlertScreenController2.handleRippleAnim(iIntValue - 350, timerAlertScreenController2.rippleView2);
                    TimerAlertScreenController timerAlertScreenController3 = TimerAlertScreenController.this;
                    timerAlertScreenController3.handleRippleAnim(iIntValue - 700, timerAlertScreenController3.rippleView3);
                }
            });
        }
        this.mRippleAnimator.setRepeatCount(-1);
        this.bellAnim.playAnimation();
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.deskclock.timer.TimerAlertScreenController.3
            @Override // java.lang.Runnable
            public void run() {
                if (TimerAlertScreenController.this.mRippleAnimator != null) {
                    TimerAlertScreenController.this.mRippleAnimator.start();
                }
            }
        }, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRippleAnim(int i, View view) {
        float f;
        if (i < 600 || i > 1520) {
            view.setAlpha(0.0f);
            return;
        }
        int i2 = i - 600;
        double d = i2;
        float f2 = (float) (((Util.isTinyScreen(this.mContext) ? 1.8d : 3.6d) / 1000.0d) * d);
        view.setScaleX(f2);
        view.setScaleY(f2);
        if (i2 < 250) {
            f = (float) ((d * 0.5d) / 250.0d);
        } else {
            f = i2 > 670 ? (float) ((((double) (R2.attr.minHeightInLargeFont - i)) * 0.5d) / 250.0d) : 0.5f;
        }
        view.setAlpha(f);
    }

    private void resetRippleAnim(View... viewArr) {
        for (View view : viewArr) {
            view.setAlpha(0.0f);
            view.setScaleX(0.0f);
        }
    }
}
