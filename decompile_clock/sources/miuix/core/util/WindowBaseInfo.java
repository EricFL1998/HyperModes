package miuix.core.util;

import android.graphics.Point;

/* JADX INFO: loaded from: classes2.dex */
public class WindowBaseInfo {
    public static final int SCREEN_MODE_FREEFORM_BASE = 8192;
    public static final int SCREEN_MODE_FULL = 0;
    public static final int SCREEN_MODE_SPLITSCREEN_BASE = 4096;
    public static final int TYPE_COMPACT = 1;
    public static final int TYPE_LARGE = 3;
    public static final int TYPE_REGULAR = 2;
    public static final int TYPE_TINY = 0;
    public static final int TYPE_UNKNOWN = -1;
    public float windowDensity;
    public int windowMode;
    public int windowType;
    public boolean sizeDirty = true;
    public boolean modeDirty = true;
    public Point windowSize = new Point();
    public Point windowSizeDp = new Point();

    public boolean isDirty() {
        return this.sizeDirty || this.modeDirty;
    }
}
