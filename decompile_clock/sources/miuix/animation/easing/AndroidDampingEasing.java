package miuix.animation.easing;

import miuix.animation.motion.AndroidFreeDampedMotion;
import miuix.animation.motion.Motion;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidDampingEasing extends DampingEasing implements PhysicalEasing {
    public AndroidDampingEasing(double d, double d2) {
        super(d, d2);
    }

    @Override // miuix.animation.easing.DampingEasing, miuix.animation.FolmeEase
    public Motion newMotion() {
        return new AndroidFreeDampedMotion(getDamping(), getAcceleration());
    }
}
