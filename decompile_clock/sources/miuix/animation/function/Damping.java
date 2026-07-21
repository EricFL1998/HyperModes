package miuix.animation.function;

/* JADX INFO: loaded from: classes2.dex */
public interface Damping extends Differentiable {
    @Override // miuix.animation.function.Differentiable, miuix.animation.function.Function
    double apply(double d);

    @Override // miuix.animation.function.Differentiable
    Function derivative();
}
