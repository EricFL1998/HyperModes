package miuix.animation.physics;

/* JADX INFO: loaded from: classes2.dex */
public final class SpringStepForce extends SpringForce {
    @Override // miuix.animation.physics.SpringForce
    protected void init() {
        if (this.mInitialized) {
            return;
        }
        if (this.mFinalPosition == Double.MAX_VALUE) {
            throw new IllegalStateException("Error: Final position of the spring must be set before the miuix.animation starts");
        }
        this.mDampedFreq = Math.min(this.mNaturalFreq * 2.0d * this.mDampingRatio, 60.0d);
        this.mInitialized = true;
    }

    @Override // miuix.animation.physics.SpringForce
    DynamicAnimation.MassState updateValues(double d, double d2, long j) {
        init();
        double d3 = j / 1.0E9d;
        double stiffness = ((1.0d - (this.mDampedFreq * d3)) * d2) + (((double) getStiffness()) * (this.mFinalPosition - d) * d3);
        this.mMassState.mValue = (float) (d + ((d2 + stiffness) * 0.5d * d3));
        this.mMassState.mVelocity = (float) stiffness;
        return this.mMassState;
    }
}
