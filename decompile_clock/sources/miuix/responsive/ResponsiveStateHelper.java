package miuix.responsive;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import miuix.core.util.WindowBaseInfo;
import miuix.responsive.map.ResponsiveState;
import miuix.responsive.map.ResponsiveStateManager;

/* JADX INFO: loaded from: classes3.dex */
public class ResponsiveStateHelper {
    public static int detectResponsiveWindowType(int i, int i2) {
        if (i <= 640) {
            return 1;
        }
        if (i >= 960) {
            return 3;
        }
        return i2 > 550 ? 2 : 1;
    }

    public static int detectResponsiveWindowType(int i, int i2, int i3, int i4) {
        if (i <= 0) {
            i = i3;
        }
        if (i <= 640) {
            return 1;
        }
        if (i >= 960) {
            return 3;
        }
        return i4 > 550 ? 2 : 1;
    }

    public static boolean isLargerThanCompact(int i) {
        return i == 2 || i == 3;
    }

    public static ResponsiveState computeResponsiveState(Context context, WindowBaseInfo windowBaseInfo) {
        return ResponsiveStateManager.getInstance().recordState(context, wrapperWindowInfo(context, windowBaseInfo));
    }

    public static ResponsiveState computeResponsiveStateOnConfigChanged(Context context, WindowBaseInfo windowBaseInfo, Configuration configuration) {
        return ResponsiveStateManager.getInstance().recordState(context, wrapperWindowInfoByConfig(configuration, windowBaseInfo));
    }

    private static ResponsiveState.WindowInfoWrapper wrapperWindowInfo(Context context, WindowBaseInfo windowBaseInfo) {
        return wrapWindowInfo(windowBaseInfo, context.getResources().getDisplayMetrics().density);
    }

    private static ResponsiveState.WindowInfoWrapper wrapperWindowInfoByConfig(Configuration configuration, WindowBaseInfo windowBaseInfo) {
        return wrapWindowInfo(windowBaseInfo, configuration.densityDpi / 160.0f);
    }

    private static ResponsiveState.WindowInfoWrapper wrapWindowInfo(WindowBaseInfo windowBaseInfo, float f) {
        ResponsiveState.WindowInfoWrapper windowInfoWrapper = new ResponsiveState.WindowInfoWrapper();
        windowInfoWrapper.windowWidth = windowBaseInfo.windowSize.x;
        windowInfoWrapper.windowHeight = windowBaseInfo.windowSize.y;
        windowInfoWrapper.windowWidthDp = windowBaseInfo.windowSizeDp.x;
        windowInfoWrapper.windowHeightDp = windowBaseInfo.windowSizeDp.y;
        windowInfoWrapper.windowType = windowBaseInfo.windowType;
        windowInfoWrapper.windowMode = windowModeWrapper(windowBaseInfo.windowMode);
        windowInfoWrapper.windowDensity = windowBaseInfo.windowDensity;
        return windowInfoWrapper;
    }

    private static int windowModeWrapper(int i) {
        if (i == 0) {
            return 4103;
        }
        switch (i) {
            case 4097:
                return 4097;
            case 4098:
                return 4098;
            case 4099:
                return 4100;
            default:
                switch (i) {
                    case 8192:
                        return 8192;
                    case 8193:
                        return 8193;
                    case 8194:
                        return 8194;
                    case 8195:
                        return 8195;
                    case 8196:
                        return 8196;
                    default:
                        Log.w("MiuixWarning", "Unknown window mode for : " + Integer.toHexString(i));
                        return 4103;
                }
        }
    }
}
