package miuix.animation.motion;

import miuix.animation.function.Differentiable;
import miuix.animation.function.Linear;

/* JADX INFO: loaded from: classes2.dex */
public class UniformLinearMotion extends BaseMotion implements Motion {
    private Differentiable function;

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
            this.function = new Linear(getInitialV(), getInitialX());
        }
        return this.function;
    }
}
