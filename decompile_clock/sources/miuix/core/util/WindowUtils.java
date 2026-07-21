package miuix.core.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;

/* JADX INFO: loaded from: classes2.dex */
public class WindowUtils {
    private static final String TAG = "WindowUtils";

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService("window");
    }

    public static Display getDisplay(Context context) {
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                return context.getDisplay();
            } catch (UnsupportedOperationException unused) {
                Log.w(TAG, "This context is not associated with a display. You should use createDisplayContext() to create a display context to work with windows.");
            }
        }
        return getWindowManager(context).getDefaultDisplay();
    }

    public static WindowMetrics getCurrentWindowMetrics(Context context) {
        return getWindowManager(context).getCurrentWindowMetrics();
    }

    public static void getWindowSize(Context context, Point point) {
        getWindowSize(getWindowManager(context), context, point);
    }

    public static void getWindowSize(Configuration configuration, Point point) {
        float f = configuration.densityDpi / 160.0f;
        point.x = MiuixUIUtils.dp2px(f, configuration.screenWidthDp);
        point.y = MiuixUIUtils.dp2px(f, configuration.screenHeightDp);
    }

    public static void getWindowSize(Configuration configuration, int i, Point point) {
        float f = (configuration.densityDpi / 160.0f) * ((i * 1.0f) / configuration.densityDpi);
        point.x = MiuixUIUtils.dp2px(f, configuration.screenWidthDp);
        point.y = MiuixUIUtils.dp2px(f, configuration.screenHeightDp);
    }

    public static void getWindowAppContentSizeFromConfiguration(Context context, Point point) {
        getWindowSize(context.getResources().getConfiguration(), point);
    }

    public static void getWindowSize(WindowManager windowManager, Context context, Point point) {
        Rect bounds;
        if (Build.VERSION.SDK_INT >= 31) {
            Rect bounds2 = windowManager.getCurrentWindowMetrics().getBounds();
            point.x = bounds2.width();
            point.y = bounds2.height();
        } else {
            if (Build.VERSION.SDK_INT == 30) {
                while ((context instanceof ContextWrapper) && !(context instanceof Activity)) {
                    context = ((ContextWrapper) context).getBaseContext();
                }
                if (context instanceof Activity) {
                    bounds = windowManager.getCurrentWindowMetrics().getBounds();
                } else {
                    bounds = windowManager.getMaximumWindowMetrics().getBounds();
                }
                point.x = bounds.width();
                point.y = bounds.height();
                return;
            }
            if (MiuixUIUtils.isInMultiWindowMode(context)) {
                getDisplay(context).getSize(point);
            } else {
                getDisplay(context).getRealSize(point);
            }
        }
    }

    public static void getWindowBounds(WindowManager windowManager, Context context, Rect rect) {
        Rect bounds;
        if (Build.VERSION.SDK_INT >= 31) {
            Rect bounds2 = windowManager.getCurrentWindowMetrics().getBounds();
            rect.set(bounds2.left, bounds2.top, bounds2.right, bounds2.bottom);
        } else {
            if (Build.VERSION.SDK_INT == 30) {
                while ((context instanceof ContextWrapper) && !(context instanceof Activity)) {
                    context = ((ContextWrapper) context).getBaseContext();
                }
                if (context instanceof Activity) {
                    bounds = windowManager.getCurrentWindowMetrics().getBounds();
                } else {
                    bounds = windowManager.getMaximumWindowMetrics().getBounds();
                }
                rect.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                return;
            }
            getDisplay(context).getRectSize(rect);
        }
    }

    public static Point getScreenSize(Context context) {
        Point point = new Point();
        getScreenSize(context, point);
        return point;
    }

    public static void getScreenSize(Context context, Point point) {
        getScreenSize(getWindowManager(context), context, point);
    }

    public static void getScreenSize(WindowManager windowManager, Context context, Point point) {
        if (Build.VERSION.SDK_INT >= 30) {
            Rect bounds = windowManager.getMaximumWindowMetrics().getBounds();
            point.x = bounds.width();
            point.y = bounds.height();
            return;
        }
        getDisplay(context).getRealSize(point);
    }

    public static Rect getWindowsBounds(Context context) {
        return getCurrentWindowMetrics(context).getBounds();
    }

    public static Point getWindowSize(Context context) {
        Point point = new Point();
        getWindowSize(context, point);
        return point;
    }

    @Deprecated
    public static int getWindowWidth(Context context) {
        return getWindowSize(context).x;
    }

    @Deprecated
    public static int getWindowHeight(Context context) {
        return getWindowSize(context).y;
    }

    public static Point getScreenSizeDp(Context context) {
        float f = context.getResources().getDisplayMetrics().density;
        Point screenSize = getScreenSize(context);
        screenSize.x = (int) ((screenSize.x / f) + 0.5f);
        screenSize.y = (int) ((screenSize.y / f) + 0.5f);
        return screenSize;
    }

    public static Point getWindowSizeDp(Context context) {
        float f = context.getResources().getDisplayMetrics().density;
        Point windowSize = getWindowSize(context);
        windowSize.x = (int) ((windowSize.x / f) + 0.5f);
        windowSize.y = (int) ((windowSize.y / f) + 0.5f);
        return windowSize;
    }

    public static void getWindowSizeDpFromConfiguration(Context context, Point point) {
        Configuration configuration = context.getResources().getConfiguration();
        point.x = configuration.screenWidthDp;
        point.y = configuration.screenHeightDp;
    }

    public static Point getWindowSize(View view) {
        Point point = new Point();
        Context context = view.getContext();
        if (Build.VERSION.SDK_INT >= 30) {
            WindowMetrics currentWindowMetrics = ((WindowManager) context.getSystemService(WindowManager.class)).getCurrentWindowMetrics();
            point.x = currentWindowMetrics.getBounds().width();
            point.y = currentWindowMetrics.getBounds().height();
        } else {
            View rootView = view.getRootView();
            point.x = rootView.getMeasuredWidth();
            point.y = rootView.getMeasuredHeight();
        }
        return point;
    }

    public static Point getWindowSizeDp(View view) {
        float f = view.getContext().getResources().getDisplayMetrics().density;
        Point windowSize = getWindowSize(view);
        windowSize.x = (int) ((windowSize.x / f) + 0.5f);
        windowSize.y = (int) ((windowSize.y / f) + 0.5f);
        return windowSize;
    }

    public static void getScreenAndWindowSize(Context context, Point point, Point point2) {
        getScreenAndWindowSize(getWindowManager(context), context, point, point2);
    }

    public static void getScreenAndWindowSize(WindowManager windowManager, Context context, Point point, Point point2) {
        if (Build.VERSION.SDK_INT >= 31) {
            Rect bounds = windowManager.getMaximumWindowMetrics().getBounds();
            point.x = bounds.width();
            point.y = bounds.height();
            Rect bounds2 = windowManager.getCurrentWindowMetrics().getBounds();
            point2.x = bounds2.width();
            point2.y = bounds2.height();
            return;
        }
        if (Build.VERSION.SDK_INT == 30) {
            while ((context instanceof ContextWrapper) && !(context instanceof Activity)) {
                context = ((ContextWrapper) context).getBaseContext();
            }
            Rect bounds3 = windowManager.getMaximumWindowMetrics().getBounds();
            point.x = bounds3.width();
            point.y = bounds3.height();
            if (context instanceof Activity) {
                Rect bounds4 = windowManager.getCurrentWindowMetrics().getBounds();
                point2.x = bounds4.width();
                point2.y = bounds4.height();
                return;
            } else {
                windowManager.getMaximumWindowMetrics().getBounds();
                point2.x = point.x;
                point2.y = point.y;
                return;
            }
        }
        if (MiuixUIUtils.isInMultiWindowMode(context)) {
            getDisplay(context).getRealSize(point);
            getDisplay(context).getSize(point2);
        } else {
            getDisplay(context).getRealSize(point);
            point2.x = point.x;
            point2.y = point.y;
        }
    }

    public static void getScreenAndWindowSizeDp(Context context, Point point, Point point2) {
        float f = context.getResources().getDisplayMetrics().density;
        getScreenAndWindowSize(context, point, point2);
        point.x = (int) ((point.x / f) + 0.5f);
        point.y = (int) ((point.y / f) + 0.5f);
        point2.x = (int) ((point2.x / f) + 0.5f);
        point2.y = (int) ((point2.y / f) + 0.5f);
    }

    public static boolean isFreeformMode(Configuration configuration, Point point, Point point2) {
        return configuration.toString().contains("mWindowingMode=freeform") && ((((float) point2.x) + 0.0f) / ((float) point.x) <= 0.9f || (((float) point2.y) + 0.0f) / ((float) point.y) <= 0.9f);
    }

    public static int getScreenType(Configuration configuration) {
        String string = configuration.toString();
        if (string.contains("screenType=0")) {
            return 0;
        }
        if (string.contains("screenType=1")) {
            return 1;
        }
        if (string.contains("screenType=2")) {
            return 2;
        }
        if (string.contains("screenType=3")) {
            return 3;
        }
        if (string.contains("screenType=4")) {
            return 4;
        }
        return string.contains("screenType=5") ? 5 : 0;
    }

    public static boolean isPortrait(Context context) {
        if (Build.VERSION.SDK_INT >= 31 || isActivity(context)) {
            return context.getResources().getConfiguration().orientation == 1;
        }
        return context.getApplicationContext().getResources().getConfiguration().orientation == 1;
    }

    private static boolean isActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return true;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return false;
    }
}
