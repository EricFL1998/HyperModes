package miuix.os;

import miuix.core.util.SystemProperties;

/* JADX INFO: loaded from: classes3.dex */
public class Build {
    public static final boolean IS_DEBUGGABLE;
    public static final boolean IS_FLIP;
    public static final boolean IS_FOLDABLE;
    public static final boolean IS_FOLD_INSIDE;
    public static final boolean IS_FOLD_OUTSIDE;
    public static final boolean IS_INDEPENDENT_REAR;
    public static final boolean IS_REAR;
    public static final boolean IS_INTERNATIONAL_BUILD = SystemProperties.get("ro.product.mod_device", "").contains("_global");
    public static final boolean IS_TABLET = isTablet();
    public static final boolean IS_AUTOMOTIVE = isAutoMotive();
    public static final boolean IS_WEAR = isWear();

    static {
        IS_DEBUGGABLE = SystemProperties.getInt("ro.debuggable", 0) == 1;
        if (android.os.Build.VERSION.SDK_INT > 33) {
            int i = SystemProperties.getInt("persist.sys.multi_display_type", 1);
            if (i > 1) {
                int i2 = i & 15;
                IS_REAR = i2 == 2;
                IS_FOLD_INSIDE = i2 == 3;
                IS_FLIP = i2 == 4;
                IS_FOLD_OUTSIDE = i2 == 5;
                IS_INDEPENDENT_REAR = i2 == 6;
            } else {
                int i3 = SystemProperties.getInt("persist.sys.muiltdisplay_type", 0);
                IS_REAR = i3 == 1;
                IS_FOLD_INSIDE = i3 == 2;
                IS_FLIP = false;
                IS_FOLD_OUTSIDE = false;
                IS_INDEPENDENT_REAR = false;
            }
        } else {
            int i4 = SystemProperties.getInt("persist.sys.muiltdisplay_type", 0);
            IS_REAR = i4 == 1;
            IS_FOLD_INSIDE = i4 == 2;
            IS_FLIP = false;
            IS_FOLD_OUTSIDE = false;
            IS_INDEPENDENT_REAR = false;
        }
        IS_FOLDABLE = IS_FOLD_INSIDE || IS_FOLD_OUTSIDE || IS_FLIP;
    }

    private static boolean isTablet() {
        return SystemProperties.get("ro.build.characteristics").contains("tablet");
    }

    private static boolean isAutoMotive() {
        return SystemProperties.get("ro.build.characteristics").contains("automotive");
    }

    private static boolean isWear() {
        return SystemProperties.get("ro.build.characteristics").contains("watch");
    }
}
