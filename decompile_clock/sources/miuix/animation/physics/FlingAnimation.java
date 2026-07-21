package miuix.animation.physics;

import miuix.animation.property.FloatProperty;
import miuix.animation.property.FloatValueHolder;

/* JADX INFO: loaded from: classes2.dex */
public final class FlingAnimation extends DynamicAnimation<FlingAnimation> {
    private final DragForce mFlingForce;

    public FlingAnimation(FloatValueHolder floatValueHolder) {
        super(floatValueHolder);
        DragForce dragForce = new DragForce();
        this.mFlingForce = dragForce;
        dragForce.setValueThreshold(getValueThreshold());
    }

    public <K> FlingAnimation(K k, FloatProperty<K> floatProperty) {
        super(k, floatProperty);
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

    @Override // miuix.animation.physics.DynamicAnimation
    public FlingAnimation setMinValue(float f) {
        super.setMinValue(f);
        return this;
    }

    @Override // miuix.animation.physics.DynamicAnimation
    public FlingAnimation setMaxValue(float f) {
        super.setMaxValue(f);
        return this;
    }

    @Override // miuix.animation.physics.DynamicAnimation
    public FlingAnimation setStartVelocity(float f) {
        super.setStartVelocity(f);
        return this;
    }

    @Override // miuix.animation.physics.DynamicAnimation
    boolean updateValueAndVelocity(long j) {
        DynamicAnimation.MassState massStateUpdateValueAndVelocity = this.mFlingForce.updateValueAndVelocity(this.mValue, this.mVelocity, j);
        this.mValue = massStateUpdateValueAndVelocity.mValue;
        this.mVelocity = massStateUpdateValueAndVelocity.mVelocity;
        if (this.mValue < this.mMinValue) {
            this.mValue = this.mMinValue;
            return true;
        }
        if (this.mValue <= this.mMaxValue) {
            return isAtEquilibrium(this.mValue, this.mVelocity);
        }
        this.mValue = this.mMaxValue;
        return true;
    }

    @Override // miuix.animation.physics.DynamicAnimation
    float getAcceleration(float f, float f2) {
        return this.mFlingForce.getAcceleration(f, f2);
    }

    @Override // miuix.animation.physics.DynamicAnimation
    boolean isAtEquilibrium(float f, float f2) {
        return f >= this.mMaxValue || f <= this.mMinValue || this.mFlingForce.isAtEquilibrium(f, f2);
    }

    @Override // miuix.animation.physics.DynamicAnimation
    void setValueThreshold(float f) {
        this.mFlingForce.setValueThreshold(f);
    }

    private static final class DragForce implements Force {
        private static final float DEFAULT_FRICTION = -4.2f;
        private static final float VELOCITY_THRESHOLD_MULTIPLIER = 62.5f;
        private float mFriction;
        private final DynamicAnimation.MassState mMassState;
        private float mVelocityThreshold;

        private DragForce() {
            this.mFriction = DEFAULT_FRICTION;
            this.mMassState = new DynamicAnimation.MassState();
        }

        void setFrictionScalar(float f) {
            this.mFriction = f * DEFAULT_FRICTION;
        }

        float getFrictionScalar() {
            return this.mFriction / DEFAULT_FRICTION;
        }

        DynamicAnimation.MassState updateValueAndVelocity(float f, float f2, long j) {
            double d = j / 1.0E9d;
            this.mMassState.mVelocity = (float) (((double) f2) * Math.exp(((double) this.mFriction) * d));
            DynamicAnimation.MassState massState = this.mMassState;
            float f3 = this.mFriction;
            massState.mValue = (float) (((double) (f - (f2 / f3))) + (((double) (f2 / f3)) * Math.exp(((double) f3) * d)));
            if (isAtEquilibrium(this.mMassState.mValue, this.mMassState.mVelocity)) {
                this.mMassState.mVelocity = 0.0f;
            }
            return this.mMassState;
        }

        @Override // miuix.animation.physics.Force
        public float getAcceleration(float f, float f2) {
            return f2 * this.mFriction;
        }

        @Override // miuix.animation.physics.Force
        public boolean isAtEquilibrium(float f, float f2) {
            return Math.abs(f2) < this.mVelocityThreshold;
        }

        void setValueThreshold(float f) {
            this.mVelocityThreshold = f * VELOCITY_THRESHOLD_MULTIPLIER;
        }
    }
}
