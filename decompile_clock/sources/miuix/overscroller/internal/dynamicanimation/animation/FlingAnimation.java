package miuix.overscroller.internal.dynamicanimation.animation;

/* JADX INFO: loaded from: classes3.dex */
public final class FlingAnimation extends DynamicAnimation<FlingAnimation> {
    private FinalValueListener mFinalValueListener;
    private final DragForce mFlingForce;

    public interface FinalValueListener {
        void onFinalValueArrived(int i);
    }

    public FlingAnimation(FloatValueHolder floatValueHolder, FinalValueListener finalValueListener) {
        super(floatValueHolder);
        DragForce dragForce = new DragForce();
        this.mFlingForce = dragForce;
        dragForce.setValueThreshold(getValueThreshold());
        this.mFinalValueListener = finalValueListener;
    }

    public <K> FlingAnimation(K k, FloatPropertyCompat<K> floatPropertyCompat) {
        super(k, floatPropertyCompat);
        DragForce dragForce = new DragForce();
        this.mFlingForce = dragForce;
        dragForce.setValueThreshold(getValueThreshold());
    }

    public FlingAnimation setFriction(float f) {
        if (f <= 0.0f) {
            throw new IllegalArgumentException("Friction must be positive");
        }
        this.mFlingForce.setFrictionScalar(f);
        return this;
    }

    public float getFriction() {
        return this.mFlingForce.getFrictionScalar();
    }

    @Override // miuix.overscroller.internal.dynamicanimation.animation.DynamicAnimation
    public FlingAnimation setMinValue(float f) {
        super.setMinValue(f);
        return this;
    }

    @Override // miuix.overscroller.internal.dynamicanimation.animation.DynamicAnimation
    public FlingAnimation setMaxValue(float f) {
        super.setMaxValue(f);
        return this;
    }

    @Override // miuix.overscroller.internal.dynamicanimation.animation.DynamicAnimation
    public FlingAnimation setStartVelocity(float f) {
        super.setStartVelocity(f);
        return this;
    }

    @Override // miuix.overscroller.internal.dynamicanimation.animation.DynamicAnimation
    boolean updateValueAndVelocity(long j) {
        DynamicAnimation.MassState massStateUpdateValueAndVelocity = this.mFlingForce.updateValueAndVelocity(this.mValue, this.mVelocity, j);
        this.mValue = massStateUpdateValueAndVelocity.mValue;
        this.mVelocity = massStateUpdateValueAndVelocity.mVelocity;
        if (this.mValue < this.mMinValue) {
            this.mValue = this.mMinValue;
            return true;
        }
        if (this.mValue > this.mMaxValue) {
            this.mValue = this.mMaxValue;
            return true;
        }
        if (!isAtEquilibrium(this.mValue, this.mVelocity)) {
            return false;
        }
        this.mFinalValueListener.onFinalValueArrived((int) this.mValue);
        return true;
    }

    @Override // miuix.overscroller.internal.dynamicanimation.animation.DynamicAnimation
    float getAcceleration(float f, float f2) {
        return this.mFlingForce.getAcceleration(f, f2);
    }

    @Override // miuix.overscroller.internal.dynamicanimation.animation.DynamicAnimation
    boolean isAtEquilibrium(float f, float f2) {
        return f >= this.mMaxValue || f <= this.mMinValue || this.mFlingForce.isAtEquilibrium(f, f2);
    }

    @Override // miuix.overscroller.internal.dynamicanimation.animation.DynamicAnimation
    void setValueThreshold(float f) {
        this.mFlingForce.setValueThreshold(f);
    }

    public float predictNaturalDest() {
        return (this.mValue - (this.mVelocity / this.mFlingForce.mFriction)) + ((Math.signum(this.mVelocity) * this.mFlingForce.mVelocityThreshold) / this.mFlingForce.mFriction);
    }

    public float predictTimeTo(float f) {
        return predictTimeWithVelocity(((f - this.mValue) + (this.mVelocity / this.mFlingForce.mFriction)) * this.mFlingForce.mFriction);
    }

    public float predictDuration() {
        return predictTimeWithVelocity(Math.signum(this.mVelocity) * this.mFlingForce.mVelocityThreshold);
    }

    private float predictTimeWithVelocity(float f) {
        return (float) ((Math.log(f / this.mVelocity) * 1000.0d) / ((double) this.mFlingForce.mFriction));
    }

    static final class DragForce implements Force {
        private static final float DEFAULT_FRICTION = -4.2f;
        private static final float VELOCITY_THRESHOLD_MULTIPLIER = 62.5f;
        private double mDragRate;
        private float mVelocityThreshold;
        private float mFriction = DEFAULT_FRICTION;
        private final DynamicAnimation.MassState mMassState = new DynamicAnimation.MassState();
        private final double NANOSECONDS_PER_SECOND = 1.0E9d;

        DragForce() {
        }

        void setFrictionScalar(float f) {
            float f2 = f * DEFAULT_FRICTION;
            this.mFriction = f2;
            this.mDragRate = 1.0d - Math.pow(2.718281828459045d, f2);
        }

        float getFrictionScalar() {
            return this.mFriction / DEFAULT_FRICTION;
        }

        DynamicAnimation.MassState updateValueAndVelocity(float f, float f2, long j) {
            double d = j / 1.0E9d;
            this.mMassState.mVelocity = (float) (((double) f2) * Math.pow(1.0d - this.mDragRate, d));
            DynamicAnimation.MassState massState = this.mMassState;
            massState.mValue = (float) (((double) f) + (((double) massState.mVelocity) * d));
            if (isAtEquilibrium(this.mMassState.mValue, this.mMassState.mVelocity)) {
                this.mMassState.mVelocity = 0.0f;
            }
            return this.mMassState;
        }

        @Override // miuix.overscroller.internal.dynamicanimation.animation.Force
        public float getAcceleration(float f, float f2) {
            return f2 * this.mFriction;
        }

        @Override // miuix.overscroller.internal.dynamicanimation.animation.Force
        public boolean isAtEquilibrium(float f, float f2) {
            return Math.abs(f2) < this.mVelocityThreshold;
        }

        void setValueThreshold(float f) {
            this.mVelocityThreshold = f * VELOCITY_THRESHOLD_MULTIPLIER;
        }
    }
}
