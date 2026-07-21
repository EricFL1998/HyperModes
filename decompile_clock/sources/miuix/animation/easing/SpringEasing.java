package miuix.animation.easing;

import miuix.animation.motion.DampedHarmonicMotion;
import miuix.animation.motion.Motion;

/* JADX INFO: loaded from: classes2.dex */
public class SpringEasing implements PhysicalEasing {
    private final double omega;
    private final double zeta;

    public SpringEasing(double d, double d2) {
        if (d < 0.0d) {
            throw new IllegalArgumentException("damping must not be negative");
        }
        if (d2 < 0.0d) {
            throw new IllegalArgumentException("response must not be negative");
        }
        this.zeta = d;
        this.omega = 6.283185307179586d / d2;
    }

    public final double getZeta() {
        return this.zeta;
    }

    public final double getOmega() {
        return this.omega;
    }

    @Override // miuix.animation.FolmeEase
    public Motion newMotion() {
        return newMotion(0.0d);
    }

    public Motion newMotion(double d) {
        return new DampedHarmonicMotion(this.zeta, this.omega, d, 0.0d);
    }

    public String toString() {
        return "Spring(" + this.zeta + ", " + this.omega + ")";
    }
}
