package miuix.animation.styles;

import android.graphics.Color;
import android.view.View;
import miuix.animation.IAnimTarget;
import miuix.animation.R;
import miuix.animation.ViewTarget;
import miuix.animation.internal.AnimData;
import miuix.animation.internal.ThreadPoolUtil;
import miuix.animation.listener.UpdateInfo;
import miuix.device.DeviceUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ForegroundColorStyle {
    public static final int MIUIX_TOUCH_RECT_LOCATION_MODE_ABSOLUTE = 2;
    public static final int MIUIX_TOUCH_RECT_LOCATION_MODE_CLEAR = 0;
    public static final int MIUIX_TOUCH_RECT_LOCATION_MODE_DEFAULT = 1;
    public static final int MIUIX_TOUCH_RECT_LOCATION_MODE_PADDING = 4;
    public static final int MIUIX_TOUCH_RECT_LOCATION_MODE_RELATIVE = 4104;

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isInvalid(View view) {
        return view == null;
    }

    public static void start(final IAnimTarget iAnimTarget, final int i) {
        ThreadPoolUtil.post(new Runnable() { // from class: miuix.animation.styles.ForegroundColorStyle.1
            @Override // java.lang.Runnable
            public void run() {
                View view = ForegroundColorStyle.getView(iAnimTarget);
                if (ForegroundColorStyle.isInvalid(view)) {
                    return;
                }
                int i2 = i;
                TintDrawable andGet = TintDrawable.setAndGet(view);
                Object tag = view.getTag(R.id.miuix_animation_tag_view_hover_corners);
                if (tag instanceof Float) {
                    andGet.setHoverCorner(((Float) tag).floatValue());
                } else if (tag instanceof Integer) {
                    andGet.setHoverCorner(((Integer) tag).floatValue());
                }
                if (DeviceUtils.getDeviceLevel() == 0 && i2 == -1) {
                    i2 = 1;
                } else if (i2 == -1) {
                    i2 = 0;
                }
                andGet.initTintBuffer(i2 & 3);
            }
        });
    }

    public static void end(final IAnimTarget iAnimTarget, final UpdateInfo updateInfo) {
        iAnimTarget.post(new Runnable() { // from class: miuix.animation.styles.ForegroundColorStyle.2
            @Override // java.lang.Runnable
            public void run() {
                View view = ForegroundColorStyle.getView(iAnimTarget);
                if (ForegroundColorStyle.isInvalid(view)) {
                    return;
                }
                TintDrawable tintDrawable = TintDrawable.get(view);
                int i = (int) updateInfo.animInfo.value;
                if (tintDrawable == null || Color.alpha(i) != 0) {
                    return;
                }
                tintDrawable.restoreOriginalDrawable();
            }
        });
    }

    public static boolean isValid(IAnimTarget iAnimTarget, AnimData animData) {
        View view;
        int i = (int) animData.targetValue;
        if (((i >> 24) & 255) != 0 || (view = getView(iAnimTarget)) == null || TintDrawable.get(view) != null) {
            return true;
        }
        if (view.getTag(R.id.miuix_animation_tag_foreground_color) == null) {
            return false;
        }
        view.setTag(R.id.miuix_animation_tag_foreground_color, Integer.valueOf(i));
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static View getView(IAnimTarget iAnimTarget) {
        if (iAnimTarget instanceof ViewTarget) {
            return ((ViewTarget) iAnimTarget).getTargetObject();
        }
        return null;
    }
}
