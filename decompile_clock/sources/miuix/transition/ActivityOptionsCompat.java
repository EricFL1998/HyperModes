package miuix.transition;

import android.app.ActivityOptions;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
class ActivityOptionsCompat {
    ActivityOptionsCompat() {
    }

    @Deprecated
    public static ActivityOptions makeScaleUpAnimationFromRoundedView(View view, Bitmap bitmap, int i, int i2, int i3, int i4, float f, Handler handler, Runnable runnable, Runnable runnable2, Runnable runnable3, Runnable runnable4) {
        return ActivityOptions.makeScaleUpAnimationFromRoundedView(view, bitmap, i, i2, i3, i4, f, handler, runnable, runnable2, runnable3, runnable4);
    }

    public static ActivityOptions makeScaleUpDown(View view, Bitmap bitmap, int i, int i2, int i3, int i4, float f, Handler handler, Runnable runnable, Runnable runnable2, Runnable runnable3, Runnable runnable4, int i5) {
        return ActivityOptions.makeScaleUpDown(view, bitmap, i, i2, i3, i4, f, handler, runnable, runnable2, runnable3, runnable4, i5);
    }

    public static ActivityOptions makeMiuiClipAnimation(Rect rect, Rect rect2, float f, float f2, int i, float f3, boolean z) {
        return ActivityOptions.makeMiuiClipAnimation(rect, rect2, f, f2, i, f3, z);
    }

    public static ActivityOptions makeMiuiRoundAnimation(float f, float f2, int i, float f3) {
        return ActivityOptions.makeMiuiRoundAnimation(f, f2, i, f3);
    }
}
