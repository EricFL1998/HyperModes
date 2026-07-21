package miuix.appcompat.internal.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import miuix.appcompat.R;
import miuix.appcompat.internal.graphics.drawable.PlaceholderDrawable;
import miuix.core.util.RomUtils;
import miuix.internal.util.ViewUtils;
import miuix.smooth.SmoothContainerDrawable2;

/* JADX INFO: loaded from: classes2.dex */
public class DrawableUtil {
    private DrawableUtil() {
    }

    public static boolean isPlaceholder(Drawable drawable) {
        return (drawable instanceof PlaceholderDrawable) || ((drawable instanceof ColorDrawable) && ((ColorDrawable) drawable).getColor() == 0);
    }

    public static Drawable createDialogButtonBackground(Context context, int i) {
        Drawable drawable;
        int dimensionPixelSize;
        if (ViewUtils.isNightMode(context)) {
            drawable = context.getResources().getDrawable(R.drawable.miuix_appcompat_coloured_btn_fg_dark, null);
        } else {
            drawable = context.getResources().getDrawable(R.drawable.miuix_appcompat_coloured_btn_fg_light, null);
        }
        if (RomUtils.isMiuiXVSdkSupported()) {
            dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_button_bg_corner_radius);
        } else {
            dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_capsule_button_bg_corner_radius);
        }
        int dimensionPixelSize2 = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_button_height);
        SmoothContainerDrawable2 smoothContainerDrawable2 = new SmoothContainerDrawable2();
        smoothContainerDrawable2.setCornerRadius(dimensionPixelSize);
        smoothContainerDrawable2.setUseSmooth(true);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        shapeDrawable.setIntrinsicHeight(dimensionPixelSize2);
        shapeDrawable.setColorFilter(i, PorterDuff.Mode.SRC);
        smoothContainerDrawable2.setChildDrawable(shapeDrawable);
        return new LayerDrawable(new Drawable[]{smoothContainerDrawable2, drawable});
    }
}
