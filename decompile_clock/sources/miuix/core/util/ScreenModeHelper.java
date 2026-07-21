package miuix.core.util;

import android.content.Context;
import android.graphics.Point;
import miuix.core.util.screenutils.FreeFormModeHelper;
import miuix.core.util.screenutils.SplitScreenModeHelper;

/* JADX INFO: loaded from: classes2.dex */
public class ScreenModeHelper {

    @Deprecated
    public static final int BIG_SCREEN_THRESHOLD = 600;
    private static final int FILTER_SUB_WINDOW_MODE = 15;
    private static final int FILTER_WINDOW_MODE = 61440;

    @Deprecated
    public static int getSubWindowMode(int i) {
        return i & 15;
    }

    @Deprecated
    public static int getWindowMode(int i) {
        return i & FILTER_WINDOW_MODE;
    }

    public static boolean isInFreeFormMode(int i) {
        return (i & 8192) != 0;
    }

    public static boolean isInSplitScreenMode(int i) {
        return (i & 4096) != 0;
    }

    public static void detectWindowMode(Context context, WindowBaseInfo windowBaseInfo, Point point) {
        FreeFormModeHelper.detectFreeFormInfo(windowBaseInfo, context, point);
        if (isInFreeFormMode(windowBaseInfo.windowMode)) {
            return;
        }
        SplitScreenModeHelper.detectSplitScreenInfo(windowBaseInfo, point);
    }
}
