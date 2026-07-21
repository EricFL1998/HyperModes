package com.android.deskclock.alarm.alert;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ViewDragHelper;
import com.airbnb.lottie.LottieAnimationView;
import com.android.deskclock.Alarm;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.alarm.bedtime.HealthDataUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.view.ViewDragHelperLayout;
import com.google.android.flexbox.FlexboxLayout;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MiuiBlurUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.theme.token.ColorBlendToken;

/* JADX INFO: loaded from: classes.dex */
public class DefaultAlarmAlertScreenController extends AlertScreenController {
    private static final int BOUNCE_ANIMATION_DURATION = 600;
    private static final float SCREEN_DRAG_THRESHOLD = 0.5f;
    private static final float SCREEN_SLIDE_THRESHOLD = 0.15f;
    private static final int SLIDER_ANIMATION_DURATION = 1000;
    private static final String TAG = "DC:DefaultAlarmAlertScreenController";
    private Alarm mAlarm;
    private View mAlertContent;
    private TextView mAlertSliderHide;
    private TextView mAmPmDisplay;
    private ImageView mBell;
    private LottieAnimationView mBellAnim;
    private ValueAnimator mBounceAnimator;
    private Context mContext;
    private TextView mDateDisplay;
    private FlexboxLayout mDetailInfo;
    private int mFlingVelocity;
    private TextView mLabel;
    private ViewDragHelperLayout mRoot;
    private View mSliderIcon;
    private View mSnooze;
    private TextView mSnoozeHint;
    private int mSnoozeMinutes;
    private View mSnoozeView;
    private TextView mTimeDisplay;
    private ViewDragHelper mViewDragHelper;
    private TextView mWeekDisplay;

    public DefaultAlarmAlertScreenController(Context context, ViewDragHelperLayout viewDragHelperLayout, Alarm alarm, int i) {
        super(context, viewDragHelperLayout, alarm.label);
        this.mContext = context;
        this.mAlarm = alarm;
        this.mSnoozeMinutes = i;
        this.mFlingVelocity = (int) TypedValue.applyDimension(1, ViewConfiguration.get(context).getScaledMinimumFlingVelocity(), this.mContext.getResources().getDisplayMetrics());
        this.mRoot = viewDragHelperLayout;
        this.mAlertContent = viewDragHelperLayout.findViewById(R.id.alert_content);
        this.mTimeDisplay = (TextView) viewDragHelperLayout.findViewById(R.id.time_display);
        this.mAlertSliderHide = (TextView) viewDragHelperLayout.findViewById(R.id.alert_slider_hint);
        if (Util.isTinyScreen(context) && MiuixUIUtils.getFontLevel(this.mContext) == 2) {
            this.mAlertSliderHide.setTextSize(12.0f);
        }
        View viewFindViewById = viewDragHelperLayout.findViewById(R.id.snooze_view_group);
        this.mSnooze = viewFindViewById;
        MiuiFolme.touchView(viewFindViewById);
        this.mSnoozeView = viewDragHelperLayout.findViewById(R.id.snooze_container);
        if (isSupportHyperMaterial()) {
            ColorBlendToken colorBlendToken = ColorBlendToken.Colored_Thin_Light;
            MiuiBlurUtils.setViewBlurMode(this.mSnoozeView, 1);
            MiuiBlurUtils.setBackgroundBlendConfig(this.mSnoozeView, colorBlendToken.colors, colorBlendToken.blendModes);
            Drawable background = this.mSnoozeView.getBackground();
            if (background != null) {
                background.setAlpha(0);
            }
        } else {
            HyperMaterialUtils.applyViewMaterial(this.mSnoozeView, null);
        }
        this.mSnoozeHint = (TextView) viewDragHelperLayout.findViewById(R.id.alert_snooze_hint);
        this.mDetailInfo = (FlexboxLayout) viewDragHelperLayout.findViewById(R.id.detail_info);
        this.mDateDisplay = (TextView) viewDragHelperLayout.findViewById(R.id.date_display);
        this.mWeekDisplay = (TextView) viewDragHelperLayout.findViewById(R.id.week_display);
        this.mAmPmDisplay = (TextView) viewDragHelperLayout.findViewById(R.id.am_pm);
        if (MiuiSdk.isLiteOrMiddleMode()) {
            ImageView imageView = (ImageView) viewDragHelperLayout.findViewById(R.id.bell);
            this.mBell = imageView;
            imageView.setVisibility(0);
        } else {
            LottieAnimationView lottieAnimationView = (LottieAnimationView) viewDragHelperLayout.findViewById(R.id.bell_anim);
            this.mBellAnim = lottieAnimationView;
            lottieAnimationView.setVisibility(0);
        }
        TextView textView = (TextView) viewDragHelperLayout.findViewById(R.id.smart_ringtone_desc);
        if (XiaoAiRingtoneHelper.isXiaoAiAlarm(this.mContext, alarm.id)) {
            textView.setVisibility(0);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mSnoozeHint.getLayoutParams();
            layoutParams.addRule(13);
            layoutParams.removeRule(2);
        }
        this.mLabel = (TextView) viewDragHelperLayout.findViewById(R.id.alert_label);
        this.mSliderIcon = viewDragHelperLayout.findViewById(R.id.alert_slider_icon);
        this.mViewDragHelper = ViewDragHelper.create(this.mRoot, new DragCallback());
        if (PadAdapterUtil.IS_PAD) {
            ((RelativeLayout.LayoutParams) this.mSnooze.getLayoutParams()).topMargin = (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_snooze_lifePost_pad_marginTop);
            return;
        }
        if (Util.isTinyScreen(context)) {
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.mSnooze.getLayoutParams();
            layoutParams2.topMargin = (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_snooze_tiny_marginTop);
            layoutParams2.width = (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_snooze_width);
            this.mSnooze.setLayoutParams(layoutParams2);
            RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) this.mTimeDisplay.getLayoutParams();
            layoutParams3.topMargin = (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_clock_marginTop);
            layoutParams3.height = (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_display_height);
            this.mTimeDisplay.setLayoutParams(layoutParams3);
            RelativeLayout.LayoutParams layoutParams4 = (RelativeLayout.LayoutParams) this.mDetailInfo.getLayoutParams();
            layoutParams4.topMargin = (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_detail_tiny_marginTop);
            layoutParams4.bottomMargin = (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_clock_marginTop);
            this.mDetailInfo.setLayoutParams(layoutParams4);
            if (Build.VERSION.SDK_INT >= 28) {
                this.mDateDisplay.setLineHeight((int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_detail_info_line_height));
                this.mWeekDisplay.setLineHeight((int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_detail_info_line_height));
                this.mAmPmDisplay.setLineHeight((int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_detail_info_line_height));
            }
            this.mTimeDisplay.setTextSize(0, (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_display_text_size));
            this.mDateDisplay.setTextSize(0, (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_detail_info_text_size));
            this.mWeekDisplay.setTextSize(0, (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_detail_info_text_size));
            this.mAmPmDisplay.setTextSize(0, (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_time_detail_info_text_size));
            this.mSnoozeHint.setTextSize(0, (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_snooze_hint_text_size));
        }
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void init() {
        this.mRoot.setViewDragHelper(this.mViewDragHelper);
        doArrowSlideAnimation();
        Resources resources = this.mContext.getResources();
        int i = this.mSnoozeMinutes;
        String quantityString = resources.getQuantityString(R.plurals.snooze_message, i, Integer.valueOf(i));
        this.mSnooze.setContentDescription(quantityString);
        this.mSnooze.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.alert.DefaultAlarmAlertScreenController.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.i(DefaultAlarmAlertScreenController.TAG, "user click snooze");
                DefaultAlarmAlertScreenController.this.snooze();
            }
        });
        ViewCompat.setAccessibilityDelegate(this.mSnooze, new AccessibilityDelegateCompat() { // from class: com.android.deskclock.alarm.alert.DefaultAlarmAlertScreenController.2
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setClassName(Button.class.getName());
            }
        });
        this.mAlertContent.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.alert.DefaultAlarmAlertScreenController.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.i(DefaultAlarmAlertScreenController.TAG, "user click alertContent");
                DefaultAlarmAlertScreenController.this.doScreenBounceAnimation(0);
            }
        });
        if (!TextUtils.isEmpty(this.mAlarm.label)) {
            this.mLabel.setText(this.mAlarm.label);
        } else {
            this.mLabel.setFocusable(false);
            this.mLabel.setClickable(false);
        }
        if (MiuiSdk.isLiteOrMiddleMode()) {
            ((RelativeLayout.LayoutParams) this.mSnoozeHint.getLayoutParams()).addRule(17, R.id.bell);
        }
        this.mSnoozeHint.setText(quantityString);
        this.mLabel.requestFocus();
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void onDestroy() {
        super.onDestroy();
        ValueAnimator valueAnimator = this.mBounceAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mBounceAnimator.removeAllUpdateListeners();
            this.mBounceAnimator.removeAllListeners();
            this.mBounceAnimator = null;
        }
        View view = this.mSliderIcon;
        if (view != null) {
            view.clearAnimation();
        }
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void release() {
        if (MiuiSdk.isLiteOrMiddleMode()) {
            return;
        }
        this.mBellAnim.cancelAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doArrowSlideAnimation() {
        this.mSliderIcon.animate().translationY((-this.mSliderIcon.getMeasuredHeight()) * 2).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.alarm.alert.DefaultAlarmAlertScreenController.5
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                if (animatedFraction > 0.5d) {
                    animatedFraction = 1.0f - animatedFraction;
                }
                DefaultAlarmAlertScreenController.this.mSliderIcon.setAlpha(animatedFraction * 2.0f);
            }
        }).setListener(new AnimatorListenerAdapter() { // from class: com.android.deskclock.alarm.alert.DefaultAlarmAlertScreenController.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                DefaultAlarmAlertScreenController.this.mSliderIcon.setTranslationY(0.0f);
                if (DefaultAlarmAlertScreenController.this.mSliderIcon.isShown()) {
                    DefaultAlarmAlertScreenController.this.doArrowSlideAnimation();
                }
            }
        }).setDuration(1000L).start();
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void onResume() {
        LottieAnimationView lottieAnimationView;
        super.onResume();
        if (!MiuiSdk.isLiteOrMiddleMode() && (lottieAnimationView = this.mBellAnim) != null) {
            lottieAnimationView.playAnimation();
        }
        if (this.mSliderIcon != null) {
            doArrowSlideAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doScreenBounceAnimation(int i) {
        Log.i(TAG, "doScreenBounceAnimation");
        if (this.mBounceAnimator == null) {
            ValueAnimator valueAnimator = new ValueAnimator();
            this.mBounceAnimator = valueAnimator;
            valueAnimator.setInterpolator(new DecelerateInterpolator(0.5f));
            this.mBounceAnimator.setDuration(600L);
            this.mBounceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.alarm.alert.DefaultAlarmAlertScreenController.6
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    DefaultAlarmAlertScreenController.this.mAlertContent.offsetTopAndBottom((int) (((Float) valueAnimator2.getAnimatedValue()).floatValue() - DefaultAlarmAlertScreenController.this.mAlertContent.getTop()));
                }
            });
        }
        this.mBounceAnimator.cancel();
        if (i == 0) {
            this.mBounceAnimator.setFloatValues(0.0f, (-this.mAlertContent.getHeight()) * 0.06f, 0.0f, (-this.mAlertContent.getHeight()) * 0.02f, 0.0f);
        } else {
            float f = i;
            this.mBounceAnimator.setFloatValues(f, 0.0f, 0.3f * f, 0.0f, 0.1f * f, 0.0f);
        }
        this.mBounceAnimator.start();
    }

    private class DragCallback extends ViewDragHelper.Callback {
        private boolean mDetermineDismiss;
        private float mScrollPercent;

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public int clampViewPositionVertical(View view, int i, int i2) {
            if (i > 0) {
                return 0;
            }
            return i;
        }

        private DragCallback() {
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public boolean tryCaptureView(View view, int i) {
            return view == DefaultAlarmAlertScreenController.this.mAlertContent;
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public void onViewReleased(View view, float f, float f2) {
            super.onViewReleased(view, f, f2);
            if ((f2 >= (-DefaultAlarmAlertScreenController.this.mFlingVelocity) || this.mScrollPercent <= DefaultAlarmAlertScreenController.SCREEN_SLIDE_THRESHOLD) && this.mScrollPercent <= 0.5f) {
                if (DefaultAlarmAlertScreenController.this.mAlertContent.getTop() != 0) {
                    Log.i(DefaultAlarmAlertScreenController.TAG, "onViewReleased doScreenBounceAnimation");
                    DefaultAlarmAlertScreenController defaultAlarmAlertScreenController = DefaultAlarmAlertScreenController.this;
                    defaultAlarmAlertScreenController.doScreenBounceAnimation(defaultAlarmAlertScreenController.mAlertContent.getTop());
                    DefaultAlarmAlertScreenController.this.mRoot.animate().alpha(1.0f).setDuration(300L).start();
                }
            } else {
                Log.i(DefaultAlarmAlertScreenController.TAG, "onViewReleased user determine dismiss yvel:" + f2 + " mFlingVelocity:" + DefaultAlarmAlertScreenController.this.mFlingVelocity + " mScrollPercent:" + this.mScrollPercent);
                DefaultAlarmAlertScreenController.this.mViewDragHelper.settleCapturedViewAt(0, -view.getHeight());
                this.mDetermineDismiss = true;
            }
            DefaultAlarmAlertScreenController.this.mRoot.invalidate();
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public void onViewCaptured(View view, int i) {
            if (DefaultAlarmAlertScreenController.this.mBounceAnimator != null) {
                DefaultAlarmAlertScreenController.this.mBounceAnimator.cancel();
            }
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public void onViewDragStateChanged(int i) {
            super.onViewDragStateChanged(i);
            if (i == 0 && this.mDetermineDismiss) {
                if (DefaultAlarmAlertScreenController.this.mAlarm != null && DefaultAlarmAlertScreenController.this.mAlarm.id == Integer.MIN_VALUE && DefaultAlarmAlertScreenController.this.mContext != null) {
                    HealthDataUtil.stopSleepRecord(DefaultAlarmAlertScreenController.this.mContext);
                }
                Log.f(DefaultAlarmAlertScreenController.TAG, "onViewDragStateChanged dismiss");
                DefaultAlarmAlertScreenController.this.dismiss();
            }
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public int getViewVerticalDragRange(View view) {
            return view.getHeight();
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
            this.mScrollPercent = (-i2) / view.getHeight();
            DefaultAlarmAlertScreenController.this.mRoot.animate().cancel();
            DefaultAlarmAlertScreenController.this.mRoot.setAlpha(1.0f - this.mScrollPercent);
        }
    }
}
