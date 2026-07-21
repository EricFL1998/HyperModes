package miuix.animation.easing;

import miuix.animation.motion.AndroidDampedHarmonicMotion;
import miuix.animation.motion.Motion;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidSpringEasing extends SpringEasing implements PhysicalEasing {
    public AndroidSpringEasing(double d, double d2) {
        super(d, d2);
    }

    @Override // miuix.animation.easing.SpringEasing
    public Motion newMotion(double d) {
        return new AndroidDampedHarmonicMotion(getZeta(), getOmega(), d, 0.0d);
    }
}
