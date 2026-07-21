package com.android.deskclock.alarm.lifepost;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.customview.widget.ViewDragHelper;
import com.airbnb.lottie.LottieAnimationView;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.weather.WeatherInfo;
import com.android.deskclock.addition.weather.WeatherUtils;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.alarm.alert.AlertAlarmFragment;
import com.android.deskclock.alarm.alert.AlertScreenController;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.TypefaceFactory;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import java.lang.ref.WeakReference;
import miuix.view.animation.CubicEaseOutInterpolator;
import miuix.view.animation.SineEaseInOutInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class AlarmLifePostScreenController extends AlertScreenController {
    private static final int BOUNCE_ANIMATION_DURATION = 400;
    private static final int LIFEPOST_ANIMATION_DURATION = 300;
    private static final float SCREEN_DRAG_THRESHOLD = 0.5f;
    private static final float SCREEN_SLIDE_THRESHOLD = 0.15f;
    private static final int SLIDER_ANIMATION_DURATION = 1000;
    private static final String TAG = "DC:AlarmLifePostScreenController";
    private Alarm mAlarm;
    private AlertAlarmFragment mAlertAlarmFragment;
    private View mAlertContent;
    private View mAlertSlideHint;
    private View mAlertSnoozeContainerView;
    private ImageView mBell;
    private LottieAnimationView mBellAnim;
    private ValueAnimator mBounceAnimator;
    private Context mContext;
    private boolean mDetermineDismiss;
    private int mFlingVelocity;
    private TextView mLabel;
    private View mLifePostContainerView;
    private LifePostController mLifePostController;
    private MultiMediaBackground mMultiMediaBackground;
    private LifePostDragHelperLayout mRoot;
    private float mScrollPercent;
    private View mSliderIcon;
    private Animation mSnoozeAnimation;
    private TextView mSnoozeHint;
    private int mSnoozeMinutes;
    Animation.AnimationListener mTransListener;
    private ViewDragHelper mViewDragHelper;
    private WeatherInfo mWeatherInfo;

    public AlarmLifePostScreenController(Context context, LifePostDragHelperLayout lifePostDragHelperLayout, Alarm alarm, int i, AlertAlarmFragment alertAlarmFragment) {
        super(context, lifePostDragHelperLayout, alarm.label);
        this.mWeatherInfo = null;
        this.mTransListener = new Animation.AnimationListener() { // from class: com.android.deskclock.alarm.lifepost.AlarmLifePostScreenController.5
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
                ObjectAnimator.ofFloat(AlarmLifePostScreenController.this.mAlertSnoozeContainerView, "alpha", AlarmLifePostScreenController.this.mAlertSnoozeContainerView.getAlpha(), 0.0f).setDuration(300L).start();
                AlarmLifePostScreenController.this.mSliderIcon.setAlpha(0.0f);
                AlarmLifePostScreenController.this.mAlertSlideHint.setAlpha(0.0f);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                AlarmLifePostScreenController.this.mLifePostContainerView.getAnimation().cancel();
                AlarmLifePostScreenController.this.mLifePostContainerView.clearAnimation();
                int height = AlarmLifePostScreenController.this.mAlertContent.getHeight() - AlarmLifePostScreenController.this.mLifePostContainerView.getHeight();
                AlarmLifePostScreenController.this.mLifePostContainerView.layout(AlarmLifePostScreenController.this.mLifePostContainerView.getLeft(), height, AlarmLifePostScreenController.this.mLifePostContainerView.getRight(), AlarmLifePostScreenController.this.mLifePostContainerView.getHeight() + height);
                AlarmLifePostScreenController.this.mMultiMediaBackground.removeViewMask();
                if (AlarmLifePostScreenController.this.mLifePostController != null) {
                    AlarmLifePostScreenController.this.mLifePostController.showLifePostNewsBackground(AlarmLifePostScreenController.this.mLifePostController.getWeatherType());
                }
                AlarmLifePostScreenController.this.startNewsRoundPlay();
            }
        };
        this.mContext = context;
        this.mAlertAlarmFragment = alertAlarmFragment;
        this.mAlarm = alarm;
        this.mSnoozeMinutes = i;
        this.mFlingVelocity = (int) TypedValue.applyDimension(1, ViewConfiguration.get(context).getScaledMinimumFlingVelocity(), this.mContext.getResources().getDisplayMetrics());
        this.mRoot = lifePostDragHelperLayout;
        initAlarmViews();
        initLifePostView();
        this.mViewDragHelper = ViewDragHelper.create(this.mRoot, new LifePostDragCallback());
    }

    private void initAlarmViews() {
        this.mAlertContent = this.mRoot.findViewById(R.id.alert_content);
        this.mSnoozeHint = (TextView) this.mRoot.findViewById(R.id.alert_snooze_hint);
        TextView textView = (TextView) this.mRoot.findViewById(R.id.smart_ringtone_desc);
        if (XiaoAiRingtoneHelper.isXiaoAiAlarm(this.mContext, this.mAlarm.id)) {
            textView.setVisibility(0);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mSnoozeHint.getLayoutParams();
            layoutParams.addRule(13);
            layoutParams.removeRule(2);
        }
        this.mLabel = (TextView) this.mRoot.findViewById(R.id.alert_label);
        this.mAlertSlideHint = this.mRoot.findViewById(R.id.alert_slider_hint);
        this.mSliderIcon = this.mRoot.findViewById(R.id.alert_slider_icon);
        if (MiuiSdk.isLiteMode()) {
            ImageView imageView = (ImageView) this.mRoot.findViewById(R.id.bell);
            this.mBell = imageView;
            imageView.setVisibility(0);
        } else {
            LottieAnimationView lottieAnimationView = (LottieAnimationView) this.mRoot.findViewById(R.id.bell_anim);
            this.mBellAnim = lottieAnimationView;
            lottieAnimationView.setVisibility(0);
        }
        this.mAlertSnoozeContainerView = this.mRoot.findViewById(R.id.alert_snooze_container);
        if (PadAdapterUtil.IS_PAD) {
            ((LinearLayout.LayoutParams) this.mAlertSnoozeContainerView.getLayoutParams()).topMargin = (int) this.mContext.getResources().getDimension(R.dimen.alert_screen_snooze_lifePost_pad_marginTop);
        }
    }

    private void initLifePostView() {
        this.mMultiMediaBackground = (MultiMediaBackground) this.mRoot.findViewById(R.id.life_post_background);
        this.mLifePostContainerView = this.mRoot.findViewById(R.id.life_post_container);
        Alarm alarm = this.mAlarm;
        this.mLifePostController = new LifePostController(this.mRoot, this.mContext, this.mLifePostContainerView, this.mAlertAlarmFragment, alarm != null && alarm.id == Integer.MIN_VALUE);
    }

    public void resetLifePostLayout() {
        LifePostController lifePostController = this.mLifePostController;
        if (lifePostController != null) {
            lifePostController.resetLifePostLayout();
            setPostContainerHeight();
        }
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void init() {
        this.mRoot.setViewDragHelper(this.mViewDragHelper);
        getWeatherInfo();
        doArrowSlideAnimation();
        Resources resources = this.mContext.getResources();
        int i = this.mSnoozeMinutes;
        String quantityString = resources.getQuantityString(R.plurals.snooze_message, i, Integer.valueOf(i));
        if (PadAdapterUtil.IS_PAD) {
            this.mSnoozeHint.setContentDescription(quantityString);
            this.mSnoozeHint.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.lifepost.AlarmLifePostScreenController.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Log.i(AlarmLifePostScreenController.TAG, "user click snooze");
                    AlarmLifePostScreenController.this.snooze();
                }
            });
        } else {
            this.mSnoozeHint.setImportantForAccessibility(2);
            this.mAlertSnoozeContainerView.setContentDescription(quantityString);
            this.mAlertSnoozeContainerView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.lifepost.AlarmLifePostScreenController.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (AlarmLifePostScreenController.this.mDetermineDismiss) {
                        return;
                    }
                    Log.i(AlarmLifePostScreenController.TAG, "user click snooze");
                    AlarmLifePostScreenController.this.snooze();
                }
            });
        }
        this.mAlertContent.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.lifepost.AlarmLifePostScreenController.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (AlarmLifePostScreenController.this.mDetermineDismiss) {
                    return;
                }
                Log.f(AlarmLifePostScreenController.TAG, "user click alertContent");
                AlarmLifePostScreenController.this.doScreenBounceAnimation(0);
            }
        });
        if (!TextUtils.isEmpty(this.mAlarm.label)) {
            this.mLabel.setText(this.mAlarm.label);
        }
        if (MiuiSdk.isLiteOrMiddleMode()) {
            ((RelativeLayout.LayoutParams) this.mSnoozeHint.getLayoutParams()).addRule(17, R.id.bell);
        }
        this.mSnoozeHint.setText(quantityString);
        this.mLabel.requestFocus();
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void onDestroy() {
        LifePostController lifePostController = this.mLifePostController;
        if (lifePostController != null) {
            lifePostController.onDestroy();
            this.mLifePostController = null;
        }
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void onPause() {
        this.mLifePostContainerView.getAnimation().cancel();
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void release() {
        if (MiuiSdk.isLiteOrMiddleMode()) {
            return;
        }
        this.mBellAnim.cancelAnimation();
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController
    public void onResume() {
        super.onResume();
        MultiMediaBackground multiMediaBackground = this.mMultiMediaBackground;
        if (multiMediaBackground != null) {
            multiMediaBackground.onResume();
        }
        if (this.mSliderIcon != null) {
            doArrowSlideAnimation();
        }
        setPostContainerHeight();
    }

    private void setPostContainerHeight() {
        int screenWidth = Util.getScreenWidth(this.mContext);
        int screenHeight = Util.getScreenHeight(this.mContext);
        Resources resources = this.mContext.getResources();
        int textViewHeight = Util.getTextViewHeight(this.mContext, "", resources.getDimensionPixelSize(R.dimen.alert_screen_textsize), screenWidth);
        int typefaceTextViewHeight = Util.getTypefaceTextViewHeight(this.mContext, "", resources.getDimensionPixelSize(R.dimen.life_post_alert_screen_clock_text_size), screenWidth, TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_40));
        this.mLifePostContainerView.setLayoutParams(new FrameLayout.LayoutParams(-1, screenHeight - (((((((int) resources.getDimension(R.dimen.life_post_alert_screen_clock_marginTop)) + (textViewHeight * 2)) + typefaceTextViewHeight) + Util.getTextViewHeight(this.mContext, "", resources.getDimensionPixelSize(R.dimen.life_post_alert_screen_clock_am_pm_text_size), screenWidth)) + ((int) resources.getDimension(R.dimen.life_post_date_display_margin_top))) + 30)));
    }

    public void showLifePost() {
        this.mRoot.post(new Runnable() { // from class: com.android.deskclock.alarm.lifepost.AlarmLifePostScreenController.4
            @Override // java.lang.Runnable
            public void run() {
                Log.i(AlarmLifePostScreenController.TAG, "user dismiss alarm, show lifePost");
                AlarmLifePostScreenController.this.mRoot.setMoving(false);
                AlarmLifePostScreenController.this.showLifePostInfo();
                AlarmLifePostScreenController.this.mDetermineDismiss = true;
                AlarmLifePostScreenController.this.dismiss(true);
                if (AlarmLifePostScreenController.this.mSliderIcon != null) {
                    AlarmLifePostScreenController.this.mSliderIcon.animate().cancel();
                    AlarmLifePostScreenController.this.mSliderIcon.setAlpha(0.0f);
                }
                if (AlarmLifePostScreenController.this.mBounceAnimator != null) {
                    AlarmLifePostScreenController.this.mBounceAnimator.cancel();
                }
                if (AlarmLifePostScreenController.this.mAlertSlideHint != null) {
                    AlarmLifePostScreenController.this.mAlertSlideHint.setAlpha(0.0f);
                }
                if (AlarmLifePostScreenController.this.mLifePostController != null) {
                    AlarmLifePostScreenController.this.mLifePostController.updateWakeUpTime(System.currentTimeMillis());
                }
                AlarmLifePostScreenController.this.mRoot.invalidate();
            }
        });
    }

    private class LifePostDragCallback extends ViewDragHelper.Callback {
        private boolean mIsDragged;

        private LifePostDragCallback() {
            this.mIsDragged = false;
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public boolean tryCaptureView(View view, int i) {
            return view == AlarmLifePostScreenController.this.mAlertContent;
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public void onViewReleased(View view, float f, float f2) {
            super.onViewReleased(view, f, f2);
            this.mIsDragged = false;
            AlarmLifePostScreenController.this.mRoot.setMoving(this.mIsDragged);
            if ((f2 < (-AlarmLifePostScreenController.this.mFlingVelocity) && AlarmLifePostScreenController.this.mScrollPercent > AlarmLifePostScreenController.SCREEN_SLIDE_THRESHOLD) || AlarmLifePostScreenController.this.mScrollPercent > 0.5f) {
                Log.i(AlarmLifePostScreenController.TAG, "onViewReleased user determine dismiss yvel:" + f2 + " mFlingVelocity:" + AlarmLifePostScreenController.this.mFlingVelocity + " mScrollPercent:" + AlarmLifePostScreenController.this.mScrollPercent);
                AlarmLifePostScreenController.this.showLifePostInfo();
                AlarmLifePostScreenController.this.mDetermineDismiss = true;
                AlarmLifePostScreenController.this.dismiss(true);
                if (AlarmLifePostScreenController.this.mAlarm != null) {
                    StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_POST_VISIBLE, StatHelper.buildLifePostTimeMap(AlarmLifePostScreenController.this.mAlarm));
                    OneTrackStatHelper.trackNumEvent((AlarmLifePostScreenController.this.mAlarm.hour * 60) + AlarmLifePostScreenController.this.mAlarm.minutes, OneTrackStatHelper.LIFE_POST_PAGE_VISIBLE);
                }
            } else {
                Log.i(AlarmLifePostScreenController.TAG, "onViewReleased doScreenBounceAnimation ");
                AlarmLifePostScreenController.this.mViewDragHelper.smoothSlideViewTo(AlarmLifePostScreenController.this.mLifePostContainerView, 0, AlarmLifePostScreenController.this.mAlertContent.getHeight());
                AlarmLifePostScreenController.this.mViewDragHelper.smoothSlideViewTo(AlarmLifePostScreenController.this.mAlertContent, 0, 0);
                AlarmLifePostScreenController.this.doArrowSlideAnimation();
            }
            AlarmLifePostScreenController.this.mRoot.invalidate();
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public void onViewCaptured(View view, int i) {
            AlarmLifePostScreenController.this.mSliderIcon.animate().cancel();
            if (AlarmLifePostScreenController.this.mBounceAnimator != null) {
                AlarmLifePostScreenController.this.mBounceAnimator.cancel();
            }
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public void onViewDragStateChanged(int i) {
            super.onViewDragStateChanged(i);
            if (i == 0 && AlarmLifePostScreenController.this.mDetermineDismiss) {
                Log.f(AlarmLifePostScreenController.TAG, "onViewDragStateChanged dismiss ");
                AlarmLifePostScreenController.this.dismiss(true);
            }
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public int clampViewPositionVertical(View view, int i, int i2) {
            return Math.min(0, Math.max(-AlarmLifePostScreenController.this.mLifePostContainerView.getHeight(), i));
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public int getViewVerticalDragRange(View view) {
            return AlarmLifePostScreenController.this.mLifePostContainerView.getHeight();
        }

        @Override // androidx.customview.widget.ViewDragHelper.Callback
        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
            if (AlarmLifePostScreenController.this.mDetermineDismiss) {
                return;
            }
            if (!this.mIsDragged) {
                this.mIsDragged = true;
                AlarmLifePostScreenController.this.mRoot.setMoving(this.mIsDragged);
                if (AlarmLifePostScreenController.this.mLifePostController != null) {
                    AlarmLifePostScreenController.this.mLifePostController.updateWakeUpTime(System.currentTimeMillis());
                }
            }
            AlarmLifePostScreenController alarmLifePostScreenController = AlarmLifePostScreenController.this;
            alarmLifePostScreenController.mScrollPercent = (-i2) / alarmLifePostScreenController.mLifePostContainerView.getHeight();
            AlarmLifePostScreenController.this.mAlertSnoozeContainerView.setAlpha(1.0f - AlarmLifePostScreenController.this.mScrollPercent);
            AlarmLifePostScreenController.this.mAlertSlideHint.setAlpha(1.0f - AlarmLifePostScreenController.this.mScrollPercent);
            AlarmLifePostScreenController.this.mSliderIcon.setAlpha(1.0f - AlarmLifePostScreenController.this.mScrollPercent);
            AlarmLifePostScreenController.this.mLifePostContainerView.offsetTopAndBottom(i4);
            AlarmLifePostScreenController.this.mAlertContent.invalidate();
        }
    }

    private void getWeatherInfo() {
        Log.i(TAG, "getWeatherInfo start ");
        new WeatherAsyncTask(this).execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWeatherInfo(WeatherInfo weatherInfo) {
        this.mWeatherInfo = weatherInfo;
        this.mMultiMediaBackground.showWeatherBackground(weatherInfo != null ? weatherInfo.weatherType : -1);
        this.mMultiMediaBackground.addViewMask();
        LifePostController lifePostController = this.mLifePostController;
        if (lifePostController != null) {
            lifePostController.setWeatherInfo(this.mWeatherInfo);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showLifePostInfo() {
        this.mRoot.setMovable(false);
        LifePostController lifePostController = this.mLifePostController;
        if (lifePostController != null) {
            lifePostController.recordShowLifePostTime();
            this.mLifePostController.saveWakeUp();
            this.mLifePostController.getSleepReport();
        }
        this.mAlertContent.setContentDescription(this.mContext.getString(R.string.life_post_setting));
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (this.mAlertContent.getHeight() - this.mLifePostContainerView.getHeight()) - this.mLifePostContainerView.getTop());
        translateAnimation.setAnimationListener(this.mTransListener);
        translateAnimation.setDuration(300L);
        translateAnimation.setFillAfter(true);
        translateAnimation.setInterpolator(new CubicEaseOutInterpolator());
        this.mRoot.invalidate();
        this.mLifePostContainerView.startAnimation(translateAnimation);
        StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_POST_ACTIVE_COUNT_WHEN_RING);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doArrowSlideAnimation() {
        if (MiuiSdk.isLiteOrMiddleMode()) {
            return;
        }
        this.mBellAnim.playAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doScreenBounceAnimation(int i) {
        Log.i(TAG, "doScreenBounceAnimation");
        if (this.mBounceAnimator == null) {
            ValueAnimator valueAnimator = new ValueAnimator();
            this.mBounceAnimator = valueAnimator;
            valueAnimator.setInterpolator(new DecelerateInterpolator(0.5f));
            this.mBounceAnimator.setDuration(400L);
            this.mBounceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.alarm.lifepost.AlarmLifePostScreenController.6
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    AlarmLifePostScreenController.this.mAlertContent.offsetTopAndBottom((int) (((Float) valueAnimator2.getAnimatedValue()).floatValue() - AlarmLifePostScreenController.this.mAlertContent.getTop()));
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

    private void doSnoozeAnimation() {
        Animation animationLoadAnimation = AnimationUtils.loadAnimation(this.mContext, R.anim.alert_snooze_animation);
        this.mSnoozeAnimation = animationLoadAnimation;
        animationLoadAnimation.setInterpolator(new SineEaseInOutInterpolator());
        this.mAlertSnoozeContainerView.startAnimation(this.mSnoozeAnimation);
    }

    public void onTrimMemory(int i) {
        LifePostController lifePostController;
        if (i < 20 || (lifePostController = this.mLifePostController) == null) {
            return;
        }
        lifePostController.onTrimMemory(i);
    }

    public void startNewsRoundPlay() {
        LifePostController lifePostController = this.mLifePostController;
        if (lifePostController != null) {
            lifePostController.startNewsRoundPlay();
        }
    }

    private static class WeatherAsyncTask extends AsyncTask<Void, Void, WeatherInfo> {
        private WeakReference<AlarmLifePostScreenController> mReference;

        public WeatherAsyncTask(AlarmLifePostScreenController alarmLifePostScreenController) {
            this.mReference = new WeakReference<>(alarmLifePostScreenController);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public WeatherInfo doInBackground(Void... voidArr) {
            Log.i(AlarmLifePostScreenController.TAG, "WeatherAsyncTask doInBackground ");
            WeakReference<AlarmLifePostScreenController> weakReference = this.mReference;
            if ((weakReference != null ? weakReference.get() : null) == null) {
                return null;
            }
            Context appDEContext = DeskClockApp.getAppDEContext();
            Time time = new Time();
            time.setToNow();
            int julianDay = Time.getJulianDay(time.toMillis(true), time.gmtoff);
            boolean z = FBEUtil.getDefaultSharedPreferences(appDEContext).getBoolean(WeatherUtils.PREFERENCE_WEATHER_PUBLISH_TIME_FLAG, false);
            String str = WeatherUtils.DATA_TYPE_OF_LIFE_POST;
            if (z) {
                str = WeatherUtils.DATA_TYPE_OF_DYNAMIC_ALARM;
            }
            return WeatherUtils.getLocalWeather(appDEContext, julianDay, str, false);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(WeatherInfo weatherInfo) {
            Log.i(AlarmLifePostScreenController.TAG, "WeatherAsyncTask onPostExecute, " + weatherInfo);
            WeakReference<AlarmLifePostScreenController> weakReference = this.mReference;
            AlarmLifePostScreenController alarmLifePostScreenController = weakReference != null ? weakReference.get() : null;
            if (alarmLifePostScreenController == null) {
                return;
            }
            alarmLifePostScreenController.setWeatherInfo(weatherInfo);
        }
    }
}
