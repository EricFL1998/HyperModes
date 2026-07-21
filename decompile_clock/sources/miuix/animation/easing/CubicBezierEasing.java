package miuix.animation.easing;

import miuix.animation.motion.CubicBezierMotion;
import miuix.animation.motion.Motion;
import miuix.animation.motion.ScaleMotion;

/* JADX INFO: loaded from: classes2.dex */
public class CubicBezierEasing implements SimpleEasing {
    private final double duration;
    private final double x1;
    private final double x2;
    private final double y1;
    private final double y2;

    @Override // miuix.animation.easing.SimpleEasing
    public double startSpeed() {
        return 0.0d;
    }

    public CubicBezierEasing(double d, double d2, double d3, double d4, double d5) {
        if (d2 < 0.0d || d2 > 1.0d) {
            throw new IllegalArgumentException("x1 must be between [0, 1]");
        }
        if (d4 < 0.0d || d4 > 1.0d) {
            throw new IllegalArgumentException("x2 must be between [0, 1]");
        }
        this.duration = d;
        this.x1 = d2;
        this.y1 = d3;
        this.x2 = d4;
        this.y2 = d5;
    }

    @Override // miuix.animation.FolmeEase
    public Motion newMotion() {
        return new ScaleMotion(new CubicBezierMotion(this.x1, this.y1, this.x2, this.y2), 1.0d, this.duration);
    }

    @Override // miuix.animation.easing.SimpleEasing
    public double duration() {
        return this.duration;
    }
}
