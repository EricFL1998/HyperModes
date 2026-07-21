package miuix.animation.physics;

/* JADX INFO: loaded from: classes2.dex */
public class FrictionOperator implements PhysicsOperator {
    @Override // miuix.animation.physics.PhysicsOperator
    public void getParameters(double[] dArr, double[] dArr2) {
        dArr2[0] = 1.0d - Math.pow(2.718281828459045d, dArr[0] * (-4.199999809265137d));
    }

    @Override // miuix.animation.physics.PhysicsOperator
    public double updateVelocity(double d, double d2, double d3, double d4, double... dArr) {
        return d * Math.pow(1.0d - d2, d4);
    }
}
