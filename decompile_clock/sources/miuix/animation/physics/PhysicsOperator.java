package miuix.animation.physics;

/* JADX INFO: loaded from: classes2.dex */
public interface PhysicsOperator {
    void getParameters(double[] dArr, double[] dArr2);

    double updateVelocity(double d, double d2, double d3, double d4, double... dArr);

    @Deprecated
    default void getParameters(float[] fArr, double[] dArr) {
        double[] dArr2 = new double[fArr.length];
        for (int i = 0; i < fArr.length; i++) {
            dArr2[i] = fArr[i];
        }
        getParameters(dArr2, dArr);
    }
}
