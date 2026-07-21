package miuix.animation.motion;

import miuix.animation.function.Constant;
import miuix.animation.function.Differentiable;

/* JADX INFO: loaded from: classes2.dex */
public final class Teleport extends InstantMotion implements Motion {
    public static final int MODE_ABSOLUTE = 0;
    public static final int MODE_RELATIVE = 1;
    private Differentiable function;
    private final int mode;
    private final double x;

    public Teleport(double d) {
        this(0, d);
    }

    public Teleport(int i, double d) {
        this.mode = i;
        this.x = d;
        if (i == 0) {
            this.function = new Constant(d);
        }
    }

    @Override // miuix.animation.motion.BaseMotion
    protected void onInitialXChanged() {
        super.onInitialXChanged();
        if (this.mode == 1) {
            this.function = null;
        }
    }

    @Override // miuix.animation.motion.Motion
    public Differentiable solve() {
        if (this.function == null) {
            this.function = new Constant(getInitialX() + this.x);
        }
        return this.function;
    }

    @Override // miuix.animation.motion.Motion
    public double stopPosition() {
        int i = this.mode;
        if (i == 0) {
            return this.x;
        }
        if (i == 1) {
            return getInitialX() + this.x;
        }
        return super.stopPosition();
    }

    @Override // miuix.animation.motion.Motion
    public double stopSpeed() {
        return getInitialV();
    }
}
