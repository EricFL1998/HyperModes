package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class FreeDamping implements Differentiable {
    private final double c;
    private final double d;
    private Function derivative;
    private final double g;
    private final double p;

    public FreeDamping(double d, double d2, double d3, double d4) {
        this.c = d;
        this.d = d2;
        this.p = d3;
        this.g = d4;
    }

    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        double d2 = this.c;
        double d3 = this.p;
        return ((-(d2 / d3)) * Math.exp((-d3) * d)) + ((this.g / this.p) * d) + this.d;
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        if (this.derivative == null) {
            this.derivative = new Function() { // from class: miuix.animation.function.FreeDamping$$ExternalSyntheticLambda0
                @Override // miuix.animation.function.Function
                public final double apply(double d) {
                    return this.f$0.m1782lambda$derivative$0$miuixanimationfunctionFreeDamping(d);
                }
            };
        }
        return this.derivative;
    }

    /* JADX INFO: renamed from: lambda$derivative$0$miuix-animation-function-FreeDamping, reason: not valid java name */
    /* synthetic */ double m1782lambda$derivative$0$miuixanimationfunctionFreeDamping(double d) {
        return (this.c * Math.exp((-this.p) * d)) + (this.g / this.p);
    }
}
