package miuix.animation.motion;

import miuix.animation.function.Differentiable;
import miuix.animation.function.FreeDamping;

/* JADX INFO: loaded from: classes2.dex */
public class FreeDampedMotion extends BaseMotion implements Motion {
    private Differentiable function = null;
    private final double g;
    private final double p;

    public FreeDampedMotion(double d, double d2) {
        this.g = d2;
        this.p = d;
    }

    @Override // miuix.animation.motion.BaseMotion
    protected void onInitialXChanged() {
        super.onInitialXChanged();
        this.function = null;
    }

    @Override // miuix.animation.motion.BaseMotion
    protected void onInitialVChanged() {
        super.onInitialVChanged();
        this.function = null;
    }

    @Override // miuix.animation.motion.Motion
    public Differentiable solve() {
        if (this.function == null) {
            double initialV = getInitialV() - (this.g / this.p);
            this.function = new FreeDamping(initialV, getInitialX() + (initialV / this.p), this.p, this.g);
        }
        return this.function;
    }

    @Override // miuix.animation.motion.Motion
    public double finishTime() {
        if (this.g == 0.0d && getInitialV() == 0.0d) {
            return 0.0d;
        }
        return super.finishTime();
    }

    @Override // miuix.animation.motion.Motion
    public double stopPosition() {
        if (this.g == 0.0d) {
            return getInitialX() + (getInitialV() / this.p);
        }
        return Double.POSITIVE_INFINITY;
    }

    @Override // miuix.animation.motion.Motion
    public double stopSpeed() {
        return this.g / this.p;
    }
}
