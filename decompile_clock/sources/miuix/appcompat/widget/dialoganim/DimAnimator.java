package miuix.appcompat.widget.dialoganim;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import miuix.internal.util.ViewUtils;
import miuix.theme.token.DimToken;

/* JADX INFO: loaded from: classes2.dex */
public class DimAnimator {
    public static void show(View view) {
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, "alpha", 0.0f, ViewUtils.isNightMode(view.getContext()) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT);
        objectAnimatorOfFloat.setInterpolator(new DecelerateInterpolator(1.5f));
        objectAnimatorOfFloat.setDuration(300L);
        objectAnimatorOfFloat.start();
    }

    public static void dismiss(View view) {
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, "alpha", ViewUtils.isNightMode(view.getContext()) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT, 0.0f);
        objectAnimatorOfFloat.setInterpolator(new DecelerateInterpolator(1.5f));
        objectAnimatorOfFloat.setDuration(350L);
        objectAnimatorOfFloat.start();
    }
}
