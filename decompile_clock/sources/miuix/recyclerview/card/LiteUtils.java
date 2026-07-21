package miuix.recyclerview.card;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import miuix.device.DeviceUtils;

/* JADX INFO: loaded from: classes3.dex */
public class LiteUtils {
    private static Boolean mIsCommonLiteStrategy;

    private LiteUtils() {
    }

    public static boolean isCommonLiteStrategy() {
        if (mIsCommonLiteStrategy == null) {
            mIsCommonLiteStrategy = Boolean.valueOf(DeviceUtils.isMiuiLiteV2() || DeviceUtils.isLiteV1StockPlus() || DeviceUtils.isMiuiMiddle());
        }
        return mIsCommonLiteStrategy.booleanValue();
    }

    public static int getThemeColor(Context context, int i) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        int color = typedArrayObtainStyledAttributes.getColor(0, 0);
        typedArrayObtainStyledAttributes.recycle();
        return color;
    }

    public static Drawable getThemeDrawable(Context context, int i) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(0);
        typedArrayObtainStyledAttributes.recycle();
        return drawable;
    }

    public static int getThemeDimens(Resources.Theme theme, Resources resources, int i) {
        TypedValue typedValue = new TypedValue();
        if (!theme.resolveAttribute(i, typedValue, true) || typedValue.resourceId <= 0) {
            return 0;
        }
        return resources.getDimensionPixelSize(typedValue.resourceId);
    }
}
