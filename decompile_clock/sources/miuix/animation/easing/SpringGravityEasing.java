package miuix.animation.easing;

import miuix.animation.motion.DampedHarmonicMotion;
import miuix.animation.motion.Motion;

/* JADX INFO: loaded from: classes2.dex */
public class SpringGravityEasing extends SpringEasing implements PhysicalEasing {
    private final double acceleration;

    public SpringGravityEasing(double d, double d2, double d3) {
        super(d, d2);
        this.acceleration = d3;
    }

    public final double getAcceleration() {
        return this.acceleration;
    }

    @Override // miuix.animation.easing.SpringEasing
    public Motion newMotion(double d) {
        return new DampedHarmonicMotion(getZeta(), getOmega(), d, this.acceleration);
    }

    @Override // miuix.animation.easing.SpringEasing
    public String toString() {
        return "SpringPhy(" + getZeta() + ", " + getOmega() + ", " + this.acceleration + ")";
    }
}
