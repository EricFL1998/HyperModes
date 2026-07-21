package miuix.animation.motion;

import miuix.animation.function.Differentiable;
import miuix.animation.function.Parabolic;

/* JADX INFO: loaded from: classes2.dex */
public class UniformlyAcceleratedMotion extends BaseMotion implements Motion {
    private final double a;
    private Differentiable function;

    public UniformlyAcceleratedMotion(double d) {
        this.a = d;
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
            this.function = new Parabolic(this.a / 2.0d, getInitialV(), getInitialX());
        }
        return this.function;
    }
}
