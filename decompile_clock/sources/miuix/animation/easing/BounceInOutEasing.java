package miuix.animation.easing;

import miuix.animation.function.BounceInOut;
import miuix.animation.motion.FunctionMotion;
import miuix.animation.motion.Motion;
import miuix.animation.motion.ScaleMotion;

/* JADX INFO: loaded from: classes2.dex */
public class BounceInOutEasing implements SimpleEasing {
    private final double duration;

    @Override // miuix.animation.easing.SimpleEasing
    public double startSpeed() {
        return 0.0d;
    }

    public BounceInOutEasing() {
        this(1.0d);
    }

    public BounceInOutEasing(double d) {
        if (d <= 0.0d) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.duration = d;
    }

    @Override // miuix.animation.FolmeEase
    public Motion newMotion() {
        return new ScaleMotion(new FunctionMotion(new BounceInOut()), 1.0d, this.duration);
    }

    @Override // miuix.animation.easing.SimpleEasing
    public final double duration() {
        return this.duration;
    }

    public String toString() {
        return "BounceOut(" + this.duration + ")";
    }
}
