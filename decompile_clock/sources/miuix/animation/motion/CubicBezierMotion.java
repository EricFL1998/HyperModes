package miuix.animation.motion;

import miuix.animation.function.Bezier;
import miuix.animation.function.Differentiable;

/* JADX INFO: loaded from: classes2.dex */
public class CubicBezierMotion extends BaseMotion {
    private final double x1;
    private final double x2;
    private final double y1;
    private final double y2;

    @Override // miuix.animation.motion.Motion
    public double finishTime() {
        return 1.0d;
    }

    @Override // miuix.animation.motion.Motion
    public double stopPosition() {
        return 1.0d;
    }

    @Override // miuix.animation.motion.Motion
    public double stopSpeed() {
        return 0.0d;
    }

    public CubicBezierMotion(double d, double d2, double d3, double d4) {
        this.x1 = d;
        this.y1 = d2;
        this.x2 = d3;
        this.y2 = d4;
    }

    @Override // miuix.animation.motion.Motion
    public Differentiable solve() {
        return new Bezier(0.0d, 0.0d, this.x1, this.y1, this.x2, this.y2, 1.0d, 1.0d);
    }
}
