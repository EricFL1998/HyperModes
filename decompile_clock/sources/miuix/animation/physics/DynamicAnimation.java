package miuix.animation.physics;

import android.util.AndroidRuntimeException;
import java.util.ArrayList;
import miuix.animation.physics.DynamicAnimation;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.FloatValueHolder;
import miuix.animation.property.ViewProperty;

/* JADX INFO: loaded from: classes2.dex */
public abstract class DynamicAnimation<T extends DynamicAnimation<T>> implements AnimationHandler.AnimationFrameCallback {
    public static final float MIN_VISIBLE_CHANGE_ALPHA = 0.00390625f;
    public static final float MIN_VISIBLE_CHANGE_PIXELS = 1.0f;
    public static final float MIN_VISIBLE_CHANGE_ROTATION_DEGREES = 0.1f;
    public static final float MIN_VISIBLE_CHANGE_SCALE = 0.002f;
    private static final float THRESHOLD_MULTIPLIER = 0.75f;
    private static final float UNSET = Float.MAX_VALUE;
    private final ArrayList<OnAnimationEndListener> mEndListeners;
    private long mLastFrameTimeNanos;
    float mMaxValue;
    float mMinValue;
    private float mMinVisibleChange;
    final FloatProperty mProperty;
    boolean mRunning;
    private long mStartDelay;
    private boolean mStartImmediately;
    private final ArrayList<OnAnimationStartListener> mStartListeners;
    boolean mStartValueIsSet;
    final Object mTarget;
    private final ArrayList<OnAnimationUpdateListener> mUpdateListeners;
    float mValue;
    float mVelocity;

    public interface OnAnimationEndListener {
        void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2);
    }

    public interface OnAnimationStartListener {
        void onAnimationStart(DynamicAnimation dynamicAnimation, float f, float f2);
    }

    public interface OnAnimationUpdateListener {
        void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2);
    }

    abstract float getAcceleration(float f, float f2);

    abstract boolean isAtEquilibrium(float f, float f2);

    abstract void setValueThreshold(float f);

    abstract boolean updateValueAndVelocity(long j);

    static class MassState {
        float mValue;
        float mVelocity;

        MassState() {
        }
    }

    DynamicAnimation(final FloatValueHolder floatValueHolder) {
        this.mVelocity = 0.0f;
        this.mValue = Float.MAX_VALUE;
        this.mStartValueIsSet = false;
        this.mRunning = false;
        this.mMaxValue = Float.MAX_VALUE;
        this.mMinValue = -Float.MAX_VALUE;
        this.mLastFrameTimeNanos = 0L;
        this.mStartDelay = 0L;
        this.mStartImmediately = false;
        this.mStartListeners = new ArrayList<>();
        this.mEndListeners = new ArrayList<>();
        this.mUpdateListeners = new ArrayList<>();
        this.mTarget = null;
        this.mProperty = new FloatProperty("FloatValueHolder") { // from class: miuix.animation.physics.DynamicAnimation.1
            @Override // miuix.animation.property.FloatProperty
            public float getValue(Object obj) {
                return floatValueHolder.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(Object obj, float f) {
                floatValueHolder.setValue(f);
            }
        };
        this.mMinVisibleChange = 1.0f;
    }

    <K> DynamicAnimation(K k, FloatProperty<K> floatProperty) {
        this.mVelocity = 0.0f;
        this.mValue = Float.MAX_VALUE;
        this.mStartValueIsSet = false;
        this.mRunning = false;
        this.mMaxValue = Float.MAX_VALUE;
        this.mMinValue = -Float.MAX_VALUE;
        this.mLastFrameTimeNanos = 0L;
        this.mStartDelay = 0L;
        this.mStartImmediately = false;
        this.mStartListeners = new ArrayList<>();
        this.mEndListeners = new ArrayList<>();
        this.mUpdateListeners = new ArrayList<>();
        this.mTarget = k;
        this.mProperty = floatProperty;
        if (floatProperty == ViewProperty.ROTATION || floatProperty == ViewProperty.ROTATION_X || floatProperty == ViewProperty.ROTATION_Y) {
            this.mMinVisibleChange = 0.1f;
            return;
        }
        if (floatProperty == ViewProperty.ALPHA) {
            this.mMinVisibleChange = 0.00390625f;
        } else if (floatProperty == ViewProperty.SCALE_X || floatProperty == ViewProperty.SCALE_Y) {
            this.mMinVisibleChange = 0.002f;
        } else {
            this.mMinVisibleChange = 1.0f;
        }
    }

    public T setStartValue(float f) {
        this.mValue = f;
        this.mStartValueIsSet = true;
        return this;
    }

    public T setStartVelocity(float f) {
        this.mVelocity = f;
        return this;
    }

    public T setMaxValue(float f) {
        this.mMaxValue = f;
        return this;
    }

    public T setMinValue(float f) {
        this.mMinValue = f;
        return this;
    }

    public void setStartDelay(long j) {
        if (j < 0) {
            j = 0;
        }
        this.mStartDelay = j;
    }

    public T addStartListener(OnAnimationStartListener onAnimationStartListener) {
        if (!this.mStartListeners.contains(onAnimationStartListener)) {
            this.mStartListeners.add(onAnimationStartListener);
        }
        return this;
    }

    public void removeStartListener(OnAnimationStartListener onAnimationStartListener) {
        removeEntry(this.mStartListeners, onAnimationStartListener);
    }

    public T addEndListener(OnAnimationEndListener onAnimationEndListener) {
        if (!this.mEndListeners.contains(onAnimationEndListener)) {
            this.mEndListeners.add(onAnimationEndListener);
        }
        return this;
    }

    public void removeEndListener(OnAnimationEndListener onAnimationEndListener) {
        removeEntry(this.mEndListeners, onAnimationEndListener);
    }

    public T addUpdateListener(OnAnimationUpdateListener onAnimationUpdateListener) {
        if (isRunning()) {
            throw new UnsupportedOperationException("Error: Update listeners must be added beforethe miuix.animation.");
        }
        if (!this.mUpdateListeners.contains(onAnimationUpdateListener)) {
            this.mUpdateListeners.add(onAnimationUpdateListener);
        }
        return this;
    }

    public void removeUpdateListener(OnAnimationUpdateListener onAnimationUpdateListener) {
        removeEntry(this.mUpdateListeners, onAnimationUpdateListener);
    }

    public T setMinimumVisibleChange(float f) {
        if (f <= 0.0f) {
            throw new IllegalArgumentException("Minimum visible change must be positive.");
        }
        this.mMinVisibleChange = f;
        setValueThreshold(f * 0.75f);
        return this;
    }

    public float getMinimumVisibleChange() {
        return this.mMinVisibleChange;
    }

    public void enableStartImmediately(boolean z) {
        this.mStartImmediately = z;
    }

    private static <T> void removeNullEntries(ArrayList<T> arrayList) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            if (arrayList.get(size) == null) {
                arrayList.remove(size);
            }
        }
    }

    private static <T> void removeEntry(ArrayList<T> arrayList, T t) {
        int iIndexOf = arrayList.indexOf(t);
        if (iIndexOf >= 0) {
            arrayList.set(iIndexOf, null);
        }
    }

    public void start() {
        if (!getAnimationHandler().isCurrentThread()) {
            throw new AndroidRuntimeException("Animations may only be started on the same thread as the animation handler");
        }
        if (this.mRunning) {
            return;
        }
        startAnimationInternal();
    }

    public void cancel() {
        if (!getAnimationHandler().isCurrentThread()) {
            throw new AndroidRuntimeException("Animations may only be canceled from the same thread as the animation handler");
        }
        if (this.mRunning) {
            endAnimationInternal(true);
        }
    }

    public boolean isRunning() {
        return this.mRunning;
    }

    private void startAnimationInternal() {
        if (this.mRunning) {
            return;
        }
        this.mRunning = true;
        if (!this.mStartValueIsSet) {
            this.mValue = getPropertyValue();
        }
        float f = this.mValue;
        if (f > this.mMaxValue || f < this.mMinValue) {
            throw new IllegalArgumentException("Starting value need to be in between min value and max value");
        }
        AnimationHandler.getInstance().addAnimationFrameCallback(this, this.mStartDelay);
    }

    @Override // miuix.animation.physics.AnimationHandler.AnimationFrameCallback
    public boolean doAnimationFrame(long j) {
        long frameDeltaNanos = AnimationHandler.getInstance().getFrameDeltaNanos();
        long j2 = this.mLastFrameTimeNanos;
        if (j2 == 0 && !this.mStartImmediately) {
            for (int i = 0; i < this.mStartListeners.size(); i++) {
                if (this.mStartListeners.get(i) != null) {
                    this.mStartListeners.get(i).onAnimationStart(this, this.mValue, this.mVelocity);
                }
            }
            removeNullEntries(this.mStartListeners);
            this.mLastFrameTimeNanos = j;
            setPropertyValue(this.mValue);
            return false;
        }
        if (frameDeltaNanos == 0) {
            frameDeltaNanos = j - j2;
        }
        this.mLastFrameTimeNanos = j;
        boolean zUpdateValueAndVelocity = updateValueAndVelocity(frameDeltaNanos);
        float fMin = Math.min(this.mValue, this.mMaxValue);
        this.mValue = fMin;
        float fMax = Math.max(fMin, this.mMinValue);
        this.mValue = fMax;
        setPropertyValue(fMax);
        if (zUpdateValueAndVelocity) {
            endAnimationInternal(false);
        }
        return zUpdateValueAndVelocity;
    }

    private void endAnimationInternal(boolean z) {
        this.mRunning = false;
        AnimationHandler.getInstance().removeCallback(this);
        this.mLastFrameTimeNanos = 0L;
        this.mStartValueIsSet = false;
        for (int i = 0; i < this.mEndListeners.size(); i++) {
            if (this.mEndListeners.get(i) != null) {
                this.mEndListeners.get(i).onAnimationEnd(this, z, this.mValue, this.mVelocity);
            }
        }
        removeNullEntries(this.mEndListeners);
    }

    void setPropertyValue(float f) {
        this.mProperty.setValue(this.mTarget, f);
        for (int i = 0; i < this.mUpdateListeners.size(); i++) {
            if (this.mUpdateListeners.get(i) != null) {
                this.mUpdateListeners.get(i).onAnimationUpdate(this, this.mValue, this.mVelocity);
            }
        }
        removeNullEntries(this.mUpdateListeners);
    }

    float getValueThreshold() {
        return this.mMinVisibleChange * 0.75f;
    }

    private float getPropertyValue() {
        return this.mProperty.getValue(this.mTarget);
    }

    public AnimationHandler getAnimationHandler() {
        return AnimationHandler.getInstance();
    }
}
