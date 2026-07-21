package miuix.animation.motion;

import miuix.animation.function.Differentiable;

/* JADX INFO: loaded from: classes2.dex */
public final class DurationMotion implements Motion {
    public static final double DURATION_FOREVER = Double.POSITIVE_INFINITY;
    private final double duration;
    private final Motion origin;
    private final boolean stopAtEnd;

    public DurationMotion(Motion motion, double d) {
        this(motion, d, true);
    }

    public DurationMotion(Motion motion, double d, boolean z) {
        if (d <= 0.0d) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.origin = motion;
        this.duration = d;
        this.stopAtEnd = z;
    }

    @Override // miuix.animation.motion.Motion
    public void setInitialX(double d) {
        this.origin.setInitialX(d);
    }

    @Override // miuix.animation.motion.Motion
    public void setInitialV(double d) {
        this.origin.setInitialV(d);
    }

    @Override // miuix.animation.motion.Motion
    public double getInitialX() {
        return this.origin.getInitialX();
    }

    @Override // miuix.animation.motion.Motion
    public double getInitialV() {
        return this.origin.getInitialV();
    }

    @Override // miuix.animation.motion.Motion
    public Differentiable solve() {
        return this.origin.solve();
    }

    @Override // miuix.animation.motion.Motion
    public double finishTime() {
        return this.duration;
    }

    @Override // miuix.animation.motion.Motion
    public double stopPosition() {
        return this.origin.finishTime() < this.duration ? this.origin.stopPosition() : this.origin.solve().apply(this.duration);
    }

    @Override // miuix.animation.motion.Motion
    public double stopSpeed() {
        if (this.stopAtEnd) {
            return 0.0d;
        }
        return this.origin.finishTime() < this.duration ? this.origin.stopSpeed() : this.origin.solve().derivative().apply(this.duration);
    }
}
