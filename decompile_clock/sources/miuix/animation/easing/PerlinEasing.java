package miuix.animation.easing;

import miuix.animation.FolmeEase;
import miuix.animation.function.Differentiable;
import miuix.animation.motion.Motion;
import miuix.animation.motion.PerlinMotion;

/* JADX INFO: loaded from: classes2.dex */
public class PerlinEasing implements FolmeEase {
    public static final Differentiable INTERPOLATOR = PerlinMotion.INTERPOLATOR;
    public static final Differentiable INTERPOLATOR2 = PerlinMotion.INTERPOLATOR2;
    private final double duration;
    private final Differentiable interpolator;
    private final double range;

    public PerlinEasing(double d, double d2) {
        this(d, d2, INTERPOLATOR);
    }

    public PerlinEasing(double d, double d2, Differentiable differentiable) {
        this.duration = d;
        this.range = d2;
        this.interpolator = differentiable;
    }

    @Override // miuix.animation.FolmeEase
    public Motion newMotion() {
        return new PerlinMotion(this.duration, this.range, this.interpolator);
    }
}
