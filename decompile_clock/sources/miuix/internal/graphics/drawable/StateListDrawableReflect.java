package miuix.internal.graphics.drawable;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/* JADX INFO: loaded from: classes2.dex */
public class StateListDrawableReflect {
    private StateListDrawableReflect() {
    }

    public static int getStateCount(StateListDrawable stateListDrawable) {
        return stateListDrawable.getStateCount();
    }

    public static int[] getStateSet(StateListDrawable stateListDrawable, int i) {
        return stateListDrawable.getStateSet(i);
    }

    public static Drawable getStateDrawable(StateListDrawable stateListDrawable, int i) {
        return stateListDrawable.getStateDrawable(i);
    }
}
