package miuix.animation.easing;

import miuix.animation.motion.DurationMotion;
import miuix.animation.motion.Motion;
import miuix.animation.motion.SimpleHarmonicMotion;

/* JADX INFO: loaded from: classes2.dex */
public class SineOutEasing implements SimpleEasing {
    private final double duration;
    private final double omega;

    public SineOutEasing() {
        this(1.0d);
    }

    public SineOutEasing(double d) {
        if (d <= 0.0d) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.duration = d;
        this.omega = 1.5707963267948966d / d;
    }

    @Override // miuix.animation.FolmeEase
    public Motion newMotion() {
        double d = this.omega;
        return new DurationMotion(new SimpleHarmonicMotion(1.0d, 0.0d, (1.0d / d) / d), this.duration, true);
    }

    @Override // miuix.animation.easing.SimpleEasing
    public final double duration() {
        return this.duration;
    }

    @Override // miuix.animation.easing.SimpleEasing
    public double startSpeed() {
        return this.omega;
    }

    public String toString() {
        return "SineOut(" + this.duration + ")";
    }
}
