package com.android.deskclock.alarm.lifepost;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.deskclock.R;
import com.android.deskclock.addition.weather.WeatherType;

/* JADX INFO: loaded from: classes.dex */
public class MultiMediaBackground extends FrameLayout {
    private static final int ALPHA_IN_ANIM_DURATION = 500;
    private Context mContext;
    private View mCoverView;
    private Drawable mWeatherBg;
    private int mWeatherType;

    public void onResume() {
    }

    public MultiMediaBackground(Context context) {
        this(context, null);
    }

    public MultiMediaBackground(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MultiMediaBackground(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
        View view = new View(this.mContext);
        this.mCoverView = view;
        view.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        this.mCoverView.setBackgroundColor(Color.parseColor("#000000"));
    }

    public void showWeatherBackground(int i) {
        Log.i("DC:MultiMediaBackground", "showWeatherBackground, weatherType: " + i);
        int shownWeatherType = WeatherType.getShownWeatherType(i);
        this.mWeatherType = shownWeatherType;
        switch (shownWeatherType) {
            case 0:
                showSunnyAnim();
                break;
            case 1:
                showCloudyAnim();
                break;
            case 2:
                showOvercastAnim();
                break;
            case 3:
            case 4:
                showRainAnim();
                break;
            case 5:
            case 6:
            case 7:
                showHazeAnim();
                break;
            default:
                showSunnyAnim();
                break;
        }
    }

    public void onDestroy() {
        removeAllViews();
    }

    private void showHazeAnim() {
        this.mWeatherBg = getResources().getDrawable(R.drawable.life_post_haze_background);
        setFullScreenBg();
    }

    private void showSunnyAnim() {
        this.mWeatherBg = getResources().getDrawable(R.drawable.life_post_sunny_background);
        setFullScreenBg();
    }

    private void showRainAnim() {
        this.mWeatherBg = getResources().getDrawable(R.drawable.life_post_rain_background);
        setFullScreenBg();
    }

    private void showCloudyAnim() {
        this.mWeatherBg = getResources().getDrawable(R.drawable.life_post_cloudy_background);
        setFullScreenBg();
    }

    private void showOvercastAnim() {
        this.mWeatherBg = getResources().getDrawable(R.drawable.life_post_overcast_background);
        setFullScreenBg();
    }

    private void setFullScreenBg() {
        removeAllViews();
        ImageView imageView = new ImageView(this.mContext);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageDrawable(this.mWeatherBg);
        imageView.setCropToPadding(false);
        addView(imageView);
    }

    public void addViewMask() {
        addView(this.mCoverView);
        ObjectAnimator.ofFloat(this.mCoverView, "alpha", 1.0f, 0.5f).setDuration(500L).start();
    }

    public void removeViewMask() {
        ObjectAnimator duration = ObjectAnimator.ofFloat(this.mCoverView, "alpha", 0.5f, 0.0f).setDuration(500L);
        duration.addListener(new Animator.AnimatorListener() { // from class: com.android.deskclock.alarm.lifepost.MultiMediaBackground.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                MultiMediaBackground multiMediaBackground = MultiMediaBackground.this;
                multiMediaBackground.removeView(multiMediaBackground.mCoverView);
            }
        });
        duration.start();
    }
}
