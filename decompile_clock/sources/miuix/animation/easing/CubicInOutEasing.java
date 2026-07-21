package miuix.animation.easing;

import miuix.animation.motion.DurationMotion;
import miuix.animation.motion.MergeMotion;
import miuix.animation.motion.Motion;
import miuix.animation.motion.PolynomialMotion;
import miuix.animation.motion.ScaleMotion;

/* JADX INFO: loaded from: classes2.dex */
public class CubicInOutEasing implements SimpleEasing {
    private final double duration;

    @Override // miuix.animation.easing.SimpleEasing
    public double startSpeed() {
        return 0.0d;
    }

    public CubicInOutEasing() {
        this(1.0d);
    }

    public CubicInOutEasing(double d) {
        if (d <= 0.0d) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.duration = d;
    }

    @Override // miuix.animation.FolmeEase
    public Motion newMotion() {
        MergeMotion mergeMotion = new MergeMotion(2);
        mergeMotion.addMotion(new DurationMotion(new PolynomialMotion(3, 1.0d, 0.0d, 0.0d, 0.0d), 1.0d, false));
        mergeMotion.addMotion(new DurationMotion(new PolynomialMotion(3, 1.0d, -3.0d, 3.0d, 1.0d), 1.0d, true));
        return new ScaleMotion(mergeMotion, 0.5d, this.duration / 2.0d);
    }

    @Override // miuix.animation.easing.SimpleEasing
    public final double duration() {
        return this.duration;
    }

    public String toString() {
        return "CubicInOut(" + this.duration + ")";
    }
}
