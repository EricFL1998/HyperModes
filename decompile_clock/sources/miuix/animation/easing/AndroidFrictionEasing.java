package miuix.animation.easing;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidFrictionEasing extends AndroidDampingEasing implements PhysicalEasing {
    public AndroidFrictionEasing(double d) {
        super(d, 0.0d);
    }

    @Override // miuix.animation.easing.DampingEasing
    public String toString() {
        return "Friction(" + getDamping() + ")";
    }
}
