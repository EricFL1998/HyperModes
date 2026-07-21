package com.android.deskclock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import androidx.core.view.ViewCompat;
import com.android.deskclock.R;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class IndicatorView extends LinearLayout {
    private View checkedView;
    private int color;
    private float dotRadiusFocus;
    private float dotRadiusUnfocus;
    private int focusedPosition;
    private float height;
    private Context mContext;
    private Paint mPaint;
    private boolean mRunning;
    private int number;
    private float size;
    private float space;
    private float width;

    public IndicatorView(Context context) {
        super(context);
        this.number = 5;
        this.focusedPosition = -1;
        this.mRunning = false;
        init();
    }

    public IndicatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.number = 5;
        this.focusedPosition = -1;
        this.mRunning = false;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.IndicatorView);
        this.color = typedArrayObtainStyledAttributes.getColor(0, getResources().getColor(R.color.alarm_timer_checked));
        this.number = typedArrayObtainStyledAttributes.getInteger(1, 6);
        this.size = typedArrayObtainStyledAttributes.getDimension(2, getResources().getDimension(R.dimen.stopwatch_indicator_size));
        this.mContext = context;
        typedArrayObtainStyledAttributes.recycle();
        init();
    }

    private void init() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.dotRadiusFocus = getResources().getDimension(R.dimen.stopwatch_indicator_dot_radius_focus);
        this.dotRadiusUnfocus = getResources().getDimension(R.dimen.stopwatch_indicator_dot_radius_unfocus);
        this.height = getHeight();
        float width = getWidth();
        this.width = width;
        this.space = (width - (this.dotRadiusFocus * 2.0f)) / (this.number - 1);
        this.mPaint.setColor(getResources().getColor(R.color.stopwatch_indicator_dot_color_unfocus));
        for (int i = 0; i < this.number; i++) {
            canvas.drawCircle(this.dotRadiusFocus + (this.space * i), this.height / 2.0f, this.dotRadiusUnfocus, this.mPaint);
        }
        if (this.mRunning || this.checkedView == null) {
            return;
        }
        translateCheckedView(this.focusedPosition);
        this.checkedView.setVisibility(0);
    }

    public void translateCheckedView(int i) {
        if (this.checkedView == null) {
            return;
        }
        if (Util.isRtl()) {
            i = -i;
        }
        this.checkedView.setTranslationX(this.space * i);
    }

    public void setRunning(boolean z) {
        this.mRunning = z;
        if (z || this.checkedView == null) {
            return;
        }
        translateCheckedView(this.focusedPosition);
        this.checkedView.setVisibility(0);
    }

    public void setChecked(long j) {
        int i = (int) (((j + 300) / 1000) % 60);
        if (this.focusedPosition == i) {
            return;
        }
        this.focusedPosition = i;
        translateCheckedView(i);
        View view = this.checkedView;
        if (view != null) {
            view.setVisibility(0);
        }
        if (this.mRunning) {
            AnimationSet animationSet = new AnimationSet(true);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(300L);
            float f = this.dotRadiusFocus + (this.space * this.focusedPosition);
            if (Util.isRtl()) {
                f = -(this.dotRadiusFocus + (this.space * (this.focusedPosition - 1)));
            }
            ScaleAnimation scaleAnimation = new ScaleAnimation(0.43f, 1.0f, 0.43f, 1.0f, f, 1.0f);
            scaleAnimation.setDuration(300L);
            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.deskclock.widget.IndicatorView.1
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    if (IndicatorView.this.mRunning) {
                        AnimationSet animationSet2 = new AnimationSet(true);
                        AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0f, 0.0f);
                        alphaAnimation2.setDuration(300L);
                        animationSet2.addAnimation(alphaAnimation2);
                        animationSet2.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.deskclock.widget.IndicatorView.1.1
                            @Override // android.view.animation.Animation.AnimationListener
                            public void onAnimationRepeat(Animation animation2) {
                            }

                            @Override // android.view.animation.Animation.AnimationListener
                            public void onAnimationStart(Animation animation2) {
                            }

                            @Override // android.view.animation.Animation.AnimationListener
                            public void onAnimationEnd(Animation animation2) {
                                if (IndicatorView.this.mRunning && IndicatorView.this.checkedView != null) {
                                    IndicatorView.this.checkedView.setVisibility(8);
                                }
                            }
                        });
                        if (IndicatorView.this.checkedView != null) {
                            IndicatorView.this.checkedView.startAnimation(animationSet2);
                        }
                    }
                }
            });
            View view2 = this.checkedView;
            if (view2 != null) {
                view2.startAnimation(animationSet);
            }
        }
    }

    public void setCheckedView(View view) {
        this.checkedView = view;
        view.setVisibility(8);
    }
}
