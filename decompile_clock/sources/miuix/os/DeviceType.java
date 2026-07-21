package miuix.os;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes3.dex */
@Retention(RetentionPolicy.SOURCE)
public @interface DeviceType {
    public static final int AUTO = 11;
    public static final int FLIP = 4;
    public static final int FOLD_IN = 3;
    public static final int FOLD_OUT = 5;
    public static final int MIRROR_PC = 7;
    public static final int PHONE = 1;
    public static final int TABLET = 2;
    public static final int UNDEFINE = -1;
    public static final int VR_HEADSET = 6;
    public static final int WEAR = 10;
}
