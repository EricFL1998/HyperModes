package miuix.animation.easing;

import miuix.animation.motion.Motion;
import miuix.animation.motion.PolynomialMotion;
import miuix.animation.motion.ScaleMotion;

/* JADX INFO: loaded from: classes2.dex */
public class CubicOutEasing implements SimpleEasing {
    private final double duration;

    public CubicOutEasing() {
        this(1.0d);
    }

    public CubicOutEasing(double d) {
        if (d <= 0.0d) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.duration = d;
    }

    @Override // miuix.animation.FolmeEase
    public Motion newMotion() {
        return new ScaleMotion(new PolynomialMotion(3, 1.0d, -3.0d, 3.0d, 0.0d), 1.0d, this.duration);
    }

    @Override // miuix.animation.easing.SimpleEasing
    public final double duration() {
        return this.duration;
    }

    @Override // miuix.animation.easing.SimpleEasing
    public double startSpeed() {
        return 3.0d / this.duration;
    }

    public String toString() {
        return "CubicOut(" + this.duration + ")";
    }
}
