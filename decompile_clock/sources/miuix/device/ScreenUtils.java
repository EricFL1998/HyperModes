package miuix.device;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/* JADX INFO: loaded from: classes2.dex */
class ScreenUtils {
    private static final String TAG = "ScreenUtils";

    ScreenUtils() {
    }

    static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService("window");
    }

    static Display getDisplay(Context context) {
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                return context.getDisplay();
            } catch (UnsupportedOperationException unused) {
                Log.w(TAG, "This context is not associated with a display. You should use createDisplayContext() to create a display context to work with windows.");
            }
        }
        return getWindowManager(context).getDefaultDisplay();
    }

    static Point getScreenSize(Context context) {
        Point point = new Point();
        getScreenSize(context, point);
        return point;
    }

    static void getScreenSize(Context context, Point point) {
        if (Build.VERSION.SDK_INT >= 30) {
            Rect bounds = getWindowManager(context).getMaximumWindowMetrics().getBounds();
            point.x = bounds.width();
            point.y = bounds.height();
            return;
        }
        getDisplay(context).getRealSize(point);
    }

    static Point getScreenSizeDp(Context context) {
        float f = context.getResources().getDisplayMetrics().density;
        Point screenSize = getScreenSize(context);
        screenSize.x = (int) (screenSize.x / f);
        screenSize.y = (int) (screenSize.y / f);
        return screenSize;
    }
}
