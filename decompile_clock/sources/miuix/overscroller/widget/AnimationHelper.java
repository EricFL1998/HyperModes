package miuix.overscroller.widget;

import android.view.View;
import miuix.overscroller.internal.dynamicanimation.animation.AnimationHandler;

/* JADX INFO: loaded from: classes3.dex */
public class AnimationHelper {
    public static void postInvalidateOnAnimation(View view) {
        AnimationHandler.getInstance().postVsyncCallback();
        view.postInvalidateOnAnimation();
    }

    public static void postOnAnimation(View view, Runnable runnable) {
        AnimationHandler.getInstance().postVsyncCallback();
        view.postOnAnimation(runnable);
    }
}
