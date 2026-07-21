package miuix.animation.motion;

import miuix.animation.function.Differentiable;

/* JADX INFO: loaded from: classes2.dex */
public final class FunctionMotion extends BaseMotion implements Motion {
    private final Differentiable function;

    public FunctionMotion(Differentiable differentiable) {
        this.function = differentiable;
    }

    @Override // miuix.animation.motion.Motion
    public Differentiable solve() {
        return this.function;
    }
}
