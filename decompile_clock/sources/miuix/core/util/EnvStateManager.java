package miuix.core.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Window;
import android.view.WindowManager;
import java.util.concurrent.ConcurrentHashMap;
import miuix.responsive.ResponsiveStateHelper;
import miuix.view.DisplayConfig;

/* JADX INFO: loaded from: classes2.dex */
public class EnvStateManager {
    static DisplayConfig sOriginConfig;
    static final Point sScreenSize = new Point(-1, -1);
    static final ConcurrentHashMap<Integer, WindowBaseInfo> sWindowInfoMap = new ConcurrentHashMap<>();
    static final Object mNaviModeLock = new Object();
    static final Object mStatusBarHeightLock = new Object();
    static final Object mNavigationBarHeightLock = new Object();
    static volatile Boolean mIsFullScreenGestureMode = null;
    static volatile int mStatusBarHeight = -1;
    static volatile int mStatusBarHeightDp = -1;
    static volatile int mNavigationBarHeight = -1;
    static volatile int mNavigationBarHeightDp = -1;

    public static void init(Application application) {
        sOriginConfig = new DisplayConfig(application.getResources().getConfiguration());
    }

    public static void updateOriginConfig(DisplayConfig displayConfig) {
        sOriginConfig = displayConfig;
    }

    public static void removeInfoOfContext(Context context) {
        sWindowInfoMap.remove(Integer.valueOf(context.getResources().hashCode()));
    }

    public static void updateScreenAndWindowSize(WindowManager windowManager, Context context) {
        Point point = sScreenSize;
        synchronized (point) {
            WindowUtils.getScreenAndWindowSize(windowManager, context, point, getWindowSize(context));
        }
    }

    public static void updateScreenSize(WindowManager windowManager, Context context) {
        Point point = sScreenSize;
        synchronized (point) {
            WindowUtils.getScreenSize(windowManager, context, point);
        }
    }

    public static Point getScreenSize(Context context) {
        Point point = sScreenSize;
        if (isSizeDirty(point)) {
            updateScreenSize(WindowUtils.getWindowManager(context), context);
        }
        return point;
    }

    public static int getScreenShortEdge(Context context) {
        Point screenSize = getScreenSize(context);
        return Math.min(screenSize.x, screenSize.y);
    }

    public static void markEnvStateDirty(Context context) {
        Point point = sScreenSize;
        synchronized (point) {
            markSizeDirty(point);
        }
        synchronized (mNaviModeLock) {
            mIsFullScreenGestureMode = null;
        }
        synchronized (mNavigationBarHeightLock) {
            mNavigationBarHeight = -1;
            mNavigationBarHeightDp = -1;
        }
        synchronized (mStatusBarHeightLock) {
            mStatusBarHeight = -1;
            mStatusBarHeightDp = -1;
        }
    }

    public static void markSizeDirty(Point point) {
        if (point.x == -1 && point.y == -1) {
            return;
        }
        point.x = -1;
        point.y = -1;
    }

    public static synchronized void markWindowInfoDirty(Context context) {
        markWindowInfoDirty(getInnerWindowInfo(context));
    }

    public static void markWindowInfoDirty(WindowBaseInfo windowBaseInfo) {
        windowBaseInfo.modeDirty = true;
        windowBaseInfo.sizeDirty = true;
    }

    public static Point getWindowSize(Context context, Configuration configuration) {
        WindowBaseInfo innerWindowInfo = getInnerWindowInfo(context);
        if (innerWindowInfo.sizeDirty) {
            ensureOriginConfigExist(configuration);
            WindowUtils.getWindowSize(configuration, sOriginConfig.densityDpi, innerWindowInfo.windowSize);
            innerWindowInfo.sizeDirty = false;
        }
        return innerWindowInfo.windowSize;
    }

    public static Point getWindowSize(Context context) {
        WindowBaseInfo innerWindowInfo = getInnerWindowInfo(context);
        if (innerWindowInfo.sizeDirty) {
            updateWindowSize(context, innerWindowInfo);
        }
        return innerWindowInfo.windowSize;
    }

    private static WindowBaseInfo getInnerWindowInfo(Context context) {
        return getInnerWindowInfo(context, true);
    }

    private static WindowBaseInfo getInnerWindowInfo(Context context, boolean z) {
        int iHashCode = context.getResources().hashCode();
        ConcurrentHashMap<Integer, WindowBaseInfo> concurrentHashMap = sWindowInfoMap;
        WindowBaseInfo windowBaseInfo = concurrentHashMap.get(Integer.valueOf(iHashCode));
        if (windowBaseInfo == null) {
            windowBaseInfo = new WindowBaseInfo();
            if (z) {
                concurrentHashMap.put(Integer.valueOf(iHashCode), windowBaseInfo);
            }
        }
        return windowBaseInfo;
    }

    public static WindowBaseInfo getWindowInfo(Context context) {
        return getWindowInfo(context, null, false);
    }

    public static WindowBaseInfo getWindowInfo(Context context, Configuration configuration) {
        return getWindowInfo(context, configuration, false);
    }

    public static WindowBaseInfo getWindowInfo(Context context, Configuration configuration, boolean z) {
        WindowBaseInfo innerWindowInfo = getInnerWindowInfo(context);
        updateWindowInfo(context, innerWindowInfo, configuration, z);
        return innerWindowInfo;
    }

    public static WindowBaseInfo getWindowInfoNoCache(Context context) {
        return getWindowInfoNoCache(context, null);
    }

    public static WindowBaseInfo getWindowInfoNoCache(Context context, Configuration configuration) {
        WindowBaseInfo innerWindowInfo = getInnerWindowInfo(context, false);
        updateWindowInfo(context, innerWindowInfo, configuration, false);
        return innerWindowInfo;
    }

    public static void updateWindowInfo(Context context, WindowBaseInfo windowBaseInfo, Configuration configuration, boolean z) {
        Window window;
        boolean z2;
        if (windowBaseInfo == null) {
            return;
        }
        if (windowBaseInfo.sizeDirty || z) {
            if (configuration != null) {
                updateWindowSizeByConfig(configuration, windowBaseInfo);
            } else {
                updateWindowSize(context, windowBaseInfo);
            }
            if ((context instanceof Activity) && (window = ((Activity) context).getWindow()) != null) {
                boolean z3 = true;
                if (window.getAttributes().width < 0 || windowBaseInfo.windowSize.x == window.getAttributes().width) {
                    z2 = false;
                } else {
                    windowBaseInfo.windowSize.x = window.getAttributes().width;
                    z2 = true;
                }
                if (window.getAttributes().height < 0 || windowBaseInfo.windowSize.y == window.getAttributes().height) {
                    z3 = z2;
                } else {
                    windowBaseInfo.windowSize.y = window.getAttributes().height;
                }
                if (z3) {
                    if (configuration == null) {
                        configuration = context.getResources().getConfiguration();
                    }
                    float f = configuration.densityDpi / 160.0f;
                    windowBaseInfo.windowSizeDp.set(MiuixUIUtils.px2dp(f, windowBaseInfo.windowSize.x), MiuixUIUtils.px2dp(f, windowBaseInfo.windowSize.y));
                    windowBaseInfo.windowType = ResponsiveStateHelper.detectResponsiveWindowType(windowBaseInfo.windowSizeDp.x, windowBaseInfo.windowSizeDp.y);
                }
            }
        }
        if (windowBaseInfo.modeDirty || z) {
            updateWindowMode(context, windowBaseInfo);
        }
    }

    public static void updateWindowSize(Context context, WindowBaseInfo windowBaseInfo) {
        WindowUtils.getWindowSize(context, windowBaseInfo.windowSize);
        float f = context.getResources().getConfiguration().densityDpi / 160.0f;
        windowBaseInfo.windowDensity = f;
        windowBaseInfo.windowSizeDp.set(MiuixUIUtils.px2dp(f, windowBaseInfo.windowSize.x), MiuixUIUtils.px2dp(f, windowBaseInfo.windowSize.y));
        windowBaseInfo.windowType = ResponsiveStateHelper.detectResponsiveWindowType(windowBaseInfo.windowSizeDp.x, windowBaseInfo.windowSizeDp.y);
        windowBaseInfo.sizeDirty = false;
    }

    public static void updateWindowSizeByConfig(Configuration configuration, WindowBaseInfo windowBaseInfo) {
        ensureOriginConfigExist(configuration);
        float f = configuration.densityDpi / 160.0f;
        float f2 = (sOriginConfig.densityDpi * 1.0f) / configuration.densityDpi;
        windowBaseInfo.windowDensity = f;
        float f3 = f * f2;
        windowBaseInfo.windowSize.set(MiuixUIUtils.dp2px(f3, configuration.screenWidthDp), MiuixUIUtils.dp2px(f3, configuration.screenHeightDp));
        windowBaseInfo.windowSizeDp.set(Math.round(configuration.screenWidthDp * f2), Math.round(configuration.screenHeightDp * f2));
        windowBaseInfo.windowType = ResponsiveStateHelper.detectResponsiveWindowType(windowBaseInfo.windowSizeDp.x, windowBaseInfo.windowSizeDp.y);
        windowBaseInfo.sizeDirty = false;
    }

    public static void updateWindowMode(Context context, WindowBaseInfo windowBaseInfo) {
        if (windowBaseInfo.sizeDirty) {
            updateWindowSize(context, windowBaseInfo);
        }
        ScreenModeHelper.detectWindowMode(context, windowBaseInfo, getScreenSize(context));
        windowBaseInfo.modeDirty = false;
    }

    private static boolean isSizeDirty(Point point) {
        return point.x == -1 && point.y == -1;
    }

    public static boolean isFreeFormMode(Context context) {
        return ScreenModeHelper.isInFreeFormMode(getInnerWindowInfo(context).windowMode);
    }

    public static boolean isFullScreenGestureMode(Context context) {
        if (mIsFullScreenGestureMode == null) {
            synchronized (mNaviModeLock) {
                if (mIsFullScreenGestureMode == null) {
                    mIsFullScreenGestureMode = Boolean.valueOf(MiuixUIUtils.isFullScreenGestureMode(context));
                }
            }
        }
        return mIsFullScreenGestureMode.booleanValue();
    }

    public static int getStatusBarHeight(Context context, boolean z) {
        if (mStatusBarHeight == -1) {
            synchronized (mStatusBarHeightLock) {
                if (mStatusBarHeight == -1) {
                    mStatusBarHeight = MiuixUIUtils.getStatusBarHeight(context);
                    mStatusBarHeightDp = (int) (mStatusBarHeight / (context.getResources().getConfiguration().densityDpi / 160.0f));
                }
            }
        }
        return z ? mStatusBarHeightDp : mStatusBarHeight;
    }

    public static int getNavigationBarHeight(Context context, boolean z) {
        if (mNavigationBarHeight == -1) {
            synchronized (mNavigationBarHeightLock) {
                if (mNavigationBarHeight == -1) {
                    mNavigationBarHeight = MiuixUIUtils.getNavigationBarHeight(context);
                    mNavigationBarHeightDp = (int) (mNavigationBarHeight / (context.getResources().getConfiguration().densityDpi / 160.0f));
                }
            }
        }
        return z ? mNavigationBarHeightDp : mNavigationBarHeight;
    }

    public static int getSmallestScreenWidthDp(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        ensureOriginConfigExist(configuration);
        return (int) (configuration.smallestScreenWidthDp * ((sOriginConfig.densityDpi * 1.0f) / configuration.densityDpi));
    }

    private static void ensureOriginConfigExist(Configuration configuration) {
        if (sOriginConfig == null) {
            sOriginConfig = new DisplayConfig(configuration);
        }
    }
}
