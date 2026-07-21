package miuix.animation.function;

import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class UnderDamping implements Damping {
    private final double alpha;
    private final double beta;
    private final double c1;
    private final double c2;
    private Function derivative;
    private final double xStar;

    public UnderDamping(double d, double d2, double d3, double d4, double d5) {
        this.c1 = d;
        this.c2 = d2;
        this.alpha = d3;
        this.beta = d4;
        this.xStar = d5;
    }

    @Override // miuix.animation.function.Damping, miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        return (Math.exp(this.alpha * d) * ((this.c1 * Math.cos(this.beta * d)) + (this.c2 * Math.sin(this.beta * d)))) + this.xStar;
    }

    @Override // miuix.animation.function.Damping, miuix.animation.function.Differentiable
    public Function derivative() {
        if (this.derivative == null) {
            this.derivative = new Function() { // from class: miuix.animation.function.UnderDamping$$ExternalSyntheticLambda0
                @Override // miuix.animation.function.Function
                public final double apply(double d) {
                    return this.f$0.m1785lambda$derivative$0$miuixanimationfunctionUnderDamping(d);
                }
            };
        }
        return this.derivative;
    }

    /* JADX INFO: renamed from: lambda$derivative$0$miuix-animation-function-UnderDamping, reason: not valid java name */
    /* synthetic */ double m1785lambda$derivative$0$miuixanimationfunctionUnderDamping(double d) {
        double dExp = Math.exp(this.alpha * d);
        double d2 = this.c1 * this.alpha;
        double d3 = this.c2;
        double d4 = this.beta;
        double dCos = (d2 + (d3 * d4)) * Math.cos(d4 * d);
        double d5 = this.c2 * this.alpha;
        double d6 = this.c1;
        double d7 = this.beta;
        return dExp * (dCos + ((d5 - (d6 * d7)) * Math.sin(d7 * d)));
    }

    public String toString() {
        return String.format(Locale.getDefault(), "UnderDamping{c1=%.3f c2=%.3f a=%.3f b=%.3f x*=%.3f}", Double.valueOf(this.c1), Double.valueOf(this.c2), Double.valueOf(this.alpha), Double.valueOf(this.beta), Double.valueOf(this.xStar));
    }
}
