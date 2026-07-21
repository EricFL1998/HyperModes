package miuix.animation.motion;

import miuix.animation.function.Differentiable;
import miuix.animation.function.DifferentiableScale;

/* JADX INFO: loaded from: classes2.dex */
public class ScaleMotion extends BaseMotion {
    private final Motion base;
    private Differentiable function;
    private final double pivotX;
    private final double scaleTime;
    private final double scaleX;

    public ScaleMotion(Motion motion, double d) {
        this(motion, d, 1.0d);
    }

    public ScaleMotion(Motion motion, double d, double d2) {
        this(motion, d, d2, 0.0d);
    }

    public ScaleMotion(Motion motion, double d, double d2, double d3) {
        this.base = motion;
        this.scaleX = d;
        this.scaleTime = d2;
        this.pivotX = d3;
    }

    @Override // miuix.animation.motion.Motion
    public Differentiable solve() {
        Differentiable differentiableSolve;
        if (this.function == null && (differentiableSolve = this.base.solve()) != null) {
            this.function = new DifferentiableScale(differentiableSolve, this.scaleTime, this.scaleX, 0.0d, this.pivotX);
        }
        return this.function;
    }

    @Override // miuix.animation.motion.Motion
    public double finishTime() {
        return this.base.finishTime() * this.scaleTime;
    }

    @Override // miuix.animation.motion.Motion
    public double stopPosition() {
        double dStopPosition = this.base.stopPosition();
        double d = this.pivotX;
        return ((dStopPosition - d) * this.scaleX) + d;
    }

    @Override // miuix.animation.motion.Motion
    public double stopSpeed() {
        return (this.base.stopSpeed() * this.scaleX) / this.scaleTime;
    }
}
