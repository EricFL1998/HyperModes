package miuix.animation.function;

import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class CriticalDamping implements Damping {
    private final double c1;
    private final double c2;
    private Function derivative;
    private final double r;
    private final double xStar;

    public CriticalDamping(double d, double d2, double d3, double d4) {
        this.c1 = d;
        this.c2 = d2;
        this.r = d3;
        this.xStar = d4;
    }

    @Override // miuix.animation.function.Damping, miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        return ((this.c1 + (this.c2 * d)) * Math.exp(this.r * d)) + this.xStar;
    }

    @Override // miuix.animation.function.Damping, miuix.animation.function.Differentiable
    public Function derivative() {
        if (this.derivative == null) {
            this.derivative = new Function() { // from class: miuix.animation.function.CriticalDamping$$ExternalSyntheticLambda0
                @Override // miuix.animation.function.Function
                public final double apply(double d) {
                    return this.f$0.m1781lambda$derivative$0$miuixanimationfunctionCriticalDamping(d);
                }
            };
        }
        return this.derivative;
    }

    /* JADX INFO: renamed from: lambda$derivative$0$miuix-animation-function-CriticalDamping, reason: not valid java name */
    /* synthetic */ double m1781lambda$derivative$0$miuixanimationfunctionCriticalDamping(double d) {
        double d2 = this.c1;
        double d3 = this.r;
        return ((d2 * d3) + (this.c2 * ((d3 * d) + 1.0d))) * Math.exp(d3 * d);
    }

    public String toString() {
        return String.format(Locale.getDefault(), "CriticalDamping{c1=%.3f c2=%.3f r=%.3f x*=%.3f}", Double.valueOf(this.c1), Double.valueOf(this.c2), Double.valueOf(this.r), Double.valueOf(this.xStar));
    }
}
