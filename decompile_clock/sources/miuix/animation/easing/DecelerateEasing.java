package miuix.animation.easing;

import miuix.animation.function.Decelerate;
import miuix.animation.motion.FunctionMotion;
import miuix.animation.motion.Motion;
import miuix.animation.motion.ScaleMotion;

/* JADX INFO: loaded from: classes2.dex */
public class DecelerateEasing implements SimpleEasing {
    private final double duration;
    private final double factor;

    @Override // miuix.animation.easing.SimpleEasing
    public double startSpeed() {
        return 0.0d;
    }

    public DecelerateEasing() {
        this(1.0d);
    }

    public DecelerateEasing(double d) {
        if (d <= 0.0d) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.factor = 1.0d;
        this.duration = d;
    }

    public DecelerateEasing(double d, double d2) {
        if (d2 <= 0.0d) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.factor = Math.max(0.0d, d);
        this.duration = d2;
    }

    @Override // miuix.animation.FolmeEase
    public Motion newMotion() {
        return new ScaleMotion(new FunctionMotion(new Decelerate(this.factor)), 1.0d, this.duration);
    }

    @Override // miuix.animation.easing.SimpleEasing
    public final double duration() {
        return this.duration;
    }

    public String toString() {
        return "Decelerate(" + this.duration + ")";
    }
}
