package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public class InverseProportional implements Differentiable {
    private Function derivative;
    private final double k;

    public InverseProportional(double d) {
        this.k = d;
    }

    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    public double apply(double d) {
        return this.k / d;
    }

    @Override // miuix.animation.function.Differentiable
    public Function derivative() {
        if (this.derivative == null) {
            this.derivative = new Function() { // from class: miuix.animation.function.InverseProportional$$ExternalSyntheticLambda0
                @Override // miuix.animation.function.Function
                public final double apply(double d) {
                    return this.f$0.m1783lambda$derivative$0$miuixanimationfunctionInverseProportional(d);
                }
            };
        }
        return this.derivative;
    }

    /* JADX INFO: renamed from: lambda$derivative$0$miuix-animation-function-InverseProportional, reason: not valid java name */
    /* synthetic */ double m1783lambda$derivative$0$miuixanimationfunctionInverseProportional(double d) {
        return ((-this.k) / d) / d;
    }
}
