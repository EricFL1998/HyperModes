package miuix.animation.easing;

import miuix.animation.motion.DurationMotion;
import miuix.animation.motion.Motion;
import miuix.animation.motion.UniformlyAcceleratedMotion;

/* JADX INFO: loaded from: classes2.dex */
public class QuadOutEasing implements SimpleEasing {
    private final double duration;

    public QuadOutEasing() {
        this(1.0d);
    }

    public QuadOutEasing(double d) {
        if (d <= 0.0d) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.duration = d;
    }

    @Override // miuix.animation.FolmeEase
    public Motion newMotion() {
        double d = this.duration;
        return new DurationMotion(new UniformlyAcceleratedMotion((-2.0d) / (d * d)), this.duration, true);
    }

    @Override // miuix.animation.easing.SimpleEasing
    public final double duration() {
        return this.duration;
    }

    @Override // miuix.animation.easing.SimpleEasing
    public double startSpeed() {
        return 2.0d / this.duration;
    }

    public String toString() {
        return "QuadOut(" + this.duration + ")";
    }
}
