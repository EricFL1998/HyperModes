package miuix.appcompat.internal.app;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import java.lang.ref.WeakReference;
import miuix.animation.utils.SpringInterpolator;
import miuix.device.DeviceUtils;

/* JADX INFO: loaded from: classes2.dex */
public class FragmentAnimator extends ValueAnimator implements View.OnLayoutChangeListener, Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
    private static final float DIM_ALPHA = 0.3f;
    private static final float ENTER_OFFSET = 1.0f;
    private static final float EXIT_OFFSET = -0.25f;
    private int mFromDim;
    private float mFromXDelta;
    private float mFromXValue;
    private WeakReference<Object> mTarget;
    private int mToDim;
    private float mToXDelta;
    private float mToXValue;
    private static final boolean USE_DIM = !DeviceUtils.isMiuiLiteV2();
    private static final SpringInterpolator INTERPOLATOR = new SpringInterpolator(0.95f, 0.4f);

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animator) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationRepeat(Animator animator) {
    }

    public FragmentAnimator(Fragment fragment, boolean z, boolean z2) {
        Context context = fragment.getContext();
        boolean z3 = false;
        if (context != null && context.getResources().getConfiguration().getLayoutDirection() == 1) {
            z3 = true;
        }
        if (z) {
            if (!z2) {
                if (!z3) {
                    initValues(0.0f, EXIT_OFFSET);
                } else {
                    initValues(0.0f, 0.25f);
                }
                if (USE_DIM) {
                    this.mToDim = Math.round(76.5f);
                }
            } else if (!z3) {
                initValues(1.0f, 0.0f);
            } else {
                initValues(-1.0f, 0.0f);
            }
        } else if (z2) {
            if (!z3) {
                initValues(EXIT_OFFSET, 0.0f);
            } else {
                initValues(0.25f, 0.0f);
            }
            if (USE_DIM) {
                this.mFromDim = Math.round(76.5f);
            }
        } else if (!z3) {
            initValues(0.0f, 1.0f);
        } else {
            initValues(0.0f, -1.0f);
        }
        addListener(this);
        addUpdateListener(this);
        setFloatValues(0.0f, 1.0f);
        SpringInterpolator springInterpolator = INTERPOLATOR;
        setInterpolator(springInterpolator);
        setDuration(springInterpolator.getDuration());
    }

    private void initValues(float f, float f2) {
        this.mFromXValue = f;
        this.mToXValue = f2;
    }

    @Override // android.animation.Animator
    public void setTarget(Object obj) {
        Object target = getTarget();
        if (target != obj) {
            if (isStarted()) {
                cancel();
            }
            if (target instanceof View) {
                ((View) target).removeOnLayoutChangeListener(this);
            }
            this.mTarget = obj == null ? null : new WeakReference<>(obj);
        }
    }

    public Object getTarget() {
        WeakReference<Object> weakReference = this.mTarget;
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }

    private void updateTargetParams() {
        Object target = getTarget();
        float width = target instanceof View ? ((View) target).getWidth() : 0;
        this.mFromXDelta = this.mFromXValue * width;
        this.mToXDelta = this.mToXValue * width;
    }

    private void setForegroundDim(View view, int i) {
        if (i < 0) {
            i = 0;
        } else if (i > 255) {
            i = 255;
        }
        Drawable foreground = view.getForeground();
        if (foreground == null) {
            foreground = new ColorDrawable(ViewCompat.MEASURED_STATE_MASK);
            view.setForeground(foreground);
        }
        foreground.setAlpha(i);
    }

    private void clearForeground(View view) {
        view.setForeground(null);
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animator) {
        if (getTarget() instanceof View) {
            View view = (View) getTarget();
            updateTargetParams();
            view.addOnLayoutChangeListener(this);
            view.setTranslationX(this.mFromXDelta);
            if (this.mFromDim != this.mToDim) {
                setForegroundDim(view, this.mFromDim);
            }
        }
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animator) {
        if (getTarget() instanceof View) {
            View view = (View) getTarget();
            view.removeOnLayoutChangeListener(this);
            view.setTranslationX(0.0f);
            if (this.mFromDim != this.mToDim) {
                clearForeground(view);
            }
        }
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (getTarget() instanceof View) {
            View view = (View) getTarget();
            float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            float f = this.mFromXDelta;
            float f2 = this.mToXDelta;
            if (f != f2) {
                f += (f2 - f) * fFloatValue;
            }
            view.setTranslationX(f);
            if (this.mFromDim != this.mToDim) {
                int i = this.mFromDim;
                setForegroundDim(view, Math.round(i + ((this.mToDim - i) * fFloatValue)));
            }
        }
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        updateTargetParams();
        if (isRunning()) {
            onAnimationUpdate(this);
        }
    }
}
