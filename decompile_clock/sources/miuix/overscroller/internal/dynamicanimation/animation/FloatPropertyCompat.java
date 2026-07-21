package miuix.overscroller.internal.dynamicanimation.animation;

import android.util.FloatProperty;

/* JADX INFO: loaded from: classes3.dex */
public abstract class FloatPropertyCompat<T> {
    final String mPropertyName;

    public abstract float getValue(T t);

    public abstract void setValue(T t, float f);

    public FloatPropertyCompat(String str) {
        this.mPropertyName = str;
    }

    public static <T> FloatPropertyCompat<T> createFloatPropertyCompat(final FloatProperty<T> floatProperty) {
        return new FloatPropertyCompat<T>(floatProperty.getName()) { // from class: miuix.overscroller.internal.dynamicanimation.animation.FloatPropertyCompat.1
            @Override // miuix.overscroller.internal.dynamicanimation.animation.FloatPropertyCompat
            public float getValue(T t) {
                return ((Float) floatProperty.get(t)).floatValue();
            }

            @Override // miuix.overscroller.internal.dynamicanimation.animation.FloatPropertyCompat
            public void setValue(T t, float f) {
                floatProperty.setValue(t, f);
            }
        };
    }
}
