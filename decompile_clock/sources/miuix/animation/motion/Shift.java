package miuix.animation.motion;

import miuix.animation.function.Constant;
import miuix.animation.function.Differentiable;

/* JADX INFO: loaded from: classes2.dex */
public final class Shift extends InstantMotion implements Motion {
    private Differentiable function;
    private final double v;

    public Shift(double d) {
        this.v = d;
    }

    @Override // miuix.animation.motion.Motion
    public Differentiable solve() {
        if (this.function == null) {
            this.function = new Constant(getInitialX());
        }
        return this.function;
    }

    @Override // miuix.animation.motion.Motion
    public double stopPosition() {
        return getInitialX();
    }

    @Override // miuix.animation.motion.Motion
    public double stopSpeed() {
        return this.v;
    }
}
