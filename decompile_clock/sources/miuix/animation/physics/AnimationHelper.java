package miuix.animation.physics;

import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
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
