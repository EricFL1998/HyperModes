package miuix.core.util.screenutils;

import android.graphics.Point;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import miuix.core.util.WindowBaseInfo;

/* JADX INFO: loaded from: classes2.dex */
public class SplitScreenModeHelper {
    public static final int SCREEN_SPLIT_MODE_HALF = 4098;
    public static final int SCREEN_SPLIT_MODE_ONE_THIRD = 4097;
    public static final int SCREEN_SPLIT_MODE_TWO_THIRD = 4099;
    public static final int SUB_MODE_1_2 = 2;
    public static final int SUB_MODE_1_3 = 1;
    public static final int SUB_MODE_2_3 = 3;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SplitScreenMode {
    }

    private static boolean isInRegion(float f, float f2, float f3) {
        return f >= f2 && f < f3;
    }

    /* JADX WARN: Code duplicated, block: B:11:0x0034  */
    /* JADX WARN: Code duplicated, block: B:12:0x0039  */
    /* JADX WARN: Code duplicated, block: B:14:0x0042  */
    /* JADX WARN: Code duplicated, block: B:15:0x0047  */
    /* JADX WARN: Code duplicated, block: B:17:0x0050  */
    /* JADX WARN: Code duplicated, block: B:18:0x0055  */
    public static void detectSplitScreenInfo(WindowBaseInfo windowBaseInfo, Point point) {
        float f;
        float f2;
        int i;
        if (isScreenLandscape(point)) {
            f2 = windowBaseInfo.windowSize.x;
            i = point.x;
        } else {
            f = windowBaseInfo.windowSize.x / (point.x + 0.0f);
            if (f >= 0.95f) {
                f2 = windowBaseInfo.windowSize.y;
                i = point.y;
            }
            if (isInRegion(f, 0.0f, 0.4f)) {
                windowBaseInfo.windowMode = 4097;
                return;
            }
            if (isInRegion(f, 0.4f, 0.6f)) {
                windowBaseInfo.windowMode = 4098;
            } else if (isInRegion(f, 0.6f, 0.8f)) {
                windowBaseInfo.windowMode = 4099;
            } else {
                windowBaseInfo.windowMode = 0;
            }
        }
        f = f2 / (i + 0.0f);
        if (isInRegion(f, 0.0f, 0.4f)) {
            windowBaseInfo.windowMode = 4097;
            return;
        }
        if (isInRegion(f, 0.4f, 0.6f)) {
            windowBaseInfo.windowMode = 4098;
        } else if (isInRegion(f, 0.6f, 0.8f)) {
            windowBaseInfo.windowMode = 4099;
        } else {
            windowBaseInfo.windowMode = 0;
        }
    }

    private static boolean isScreenLandscape(Point point) {
        return point.x > point.y;
    }
}
