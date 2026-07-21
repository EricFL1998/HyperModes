package miuix.internal.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

/* JADX INFO: loaded from: classes2.dex */
public class AttributeResolver {
    private static final TypedValue TYPED_VALUE = new TypedValue();
    private static final ThreadLocal<TypedValue> TYPED_VALUE_THREAD_LOCAL = new ThreadLocal<>();

    protected AttributeResolver() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    private static TypedValue getTypedValue(Context context) {
        if (context.getMainLooper().getThread() == Thread.currentThread()) {
            return TYPED_VALUE;
        }
        ThreadLocal<TypedValue> threadLocal = TYPED_VALUE_THREAD_LOCAL;
        TypedValue typedValue = threadLocal.get();
        if (typedValue != null) {
            return typedValue;
        }
        TypedValue typedValue2 = new TypedValue();
        threadLocal.set(typedValue2);
        return typedValue2;
    }

    public static int resolve(Context context, int i) {
        TypedValue typedValue = getTypedValue(context);
        if (context.getTheme().resolveAttribute(i, typedValue, true)) {
            return typedValue.resourceId;
        }
        return -1;
    }

    public static Drawable resolveDrawable(Context context, int i) {
        TypedValue typedValue = getTypedValue(context);
        if (!context.getTheme().resolveAttribute(i, typedValue, true)) {
            return null;
        }
        if (typedValue.resourceId > 0) {
            return context.getResources().getDrawable(typedValue.resourceId, context.getTheme());
        }
        if (typedValue.type < 28 || typedValue.type > 31) {
            return null;
        }
        return new ColorDrawable(typedValue.data);
    }

    public static int resolveColor(Context context, int i) {
        Integer numInnerResolveColor = innerResolveColor(context, i);
        if (numInnerResolveColor != null) {
            return numInnerResolveColor.intValue();
        }
        return context.getResources().getColor(-1);
    }

    public static int resolveColor(Context context, int i, int i2) {
        Integer numInnerResolveColor = innerResolveColor(context, i);
        return numInnerResolveColor != null ? numInnerResolveColor.intValue() : i2;
    }

    private static Integer innerResolveColor(Context context, int i) {
        TypedValue typedValue = getTypedValue(context);
        if (!context.getTheme().resolveAttribute(i, typedValue, true)) {
            return null;
        }
        if (typedValue.resourceId > 0) {
            return Integer.valueOf(context.getResources().getColor(typedValue.resourceId));
        }
        if (typedValue.type < 28 || typedValue.type > 31) {
            return null;
        }
        return Integer.valueOf(typedValue.data);
    }

    public static boolean resolveBoolean(Context context, int i, boolean z) {
        TypedValue typedValue = getTypedValue(context);
        if (!context.getTheme().resolveAttribute(i, typedValue, true) || typedValue.type != 18) {
            return z;
        }
        if (typedValue.resourceId > 0) {
            return context.getResources().getBoolean(typedValue.resourceId);
        }
        return typedValue.data != 0;
    }

    public static float resolveDimension(Context context, int i) {
        TypedValue typedValueResolveTypedValue = resolveTypedValue(context, i);
        if (typedValueResolveTypedValue == null || typedValueResolveTypedValue.type != 5) {
            return 0.0f;
        }
        if (typedValueResolveTypedValue.resourceId > 0) {
            return context.getResources().getDimension(typedValueResolveTypedValue.resourceId);
        }
        return TypedValue.complexToDimension(typedValueResolveTypedValue.data, context.getResources().getDisplayMetrics());
    }

    public static int resolveDimensionPixelSize(Context context, int i) {
        TypedValue typedValueResolveTypedValue = resolveTypedValue(context, i);
        if (typedValueResolveTypedValue == null || typedValueResolveTypedValue.type != 5) {
            return 0;
        }
        if (typedValueResolveTypedValue.resourceId > 0) {
            return context.getResources().getDimensionPixelSize(typedValueResolveTypedValue.resourceId);
        }
        return TypedValue.complexToDimensionPixelSize(typedValueResolveTypedValue.data, context.getResources().getDisplayMetrics());
    }

    public static int resolveInt(Context context, int i, int i2) {
        TypedValue typedValue = getTypedValue(context);
        if (!context.getTheme().resolveAttribute(i, typedValue, true)) {
            return i2;
        }
        if (typedValue.resourceId > 0) {
            return context.getResources().getInteger(typedValue.resourceId);
        }
        return (typedValue.type < 16 || typedValue.type > 31) ? i2 : typedValue.data;
    }

    public static float resolveFloat(Context context, int i, float f) {
        TypedValue typedValue = getTypedValue(context);
        if (!context.getTheme().resolveAttribute(i, typedValue, true) || typedValue.type != 4) {
            return f;
        }
        if (typedValue.resourceId > 0) {
            return Build.VERSION.SDK_INT >= 29 ? context.getResources().getFloat(typedValue.resourceId) : f;
        }
        return typedValue.data;
    }

    public static TypedValue resolveTypedValue(Context context, int i) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(i, typedValue, true)) {
            return typedValue;
        }
        return null;
    }
}
