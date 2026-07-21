package miuix.animation.property;

import android.util.Property;

/* JADX INFO: loaded from: classes2.dex */
public abstract class FloatProperty<T> extends Property<T, Float> {
    float mMinVisibleChange;
    final String mPropertyName;

    public abstract float getValue(T t);

    public abstract void setValue(T t, float f);

    public FloatProperty(String str) {
        this(str, -1.0f);
    }

    public FloatProperty(String str, float f) {
        super(Float.class, str);
        this.mPropertyName = str;
        this.mMinVisibleChange = f;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.util.Property
    public Float get(T t) {
        if (t == null) {
            return Float.valueOf(0.0f);
        }
        return Float.valueOf(getValue(t));
    }

    @Override // android.util.Property
    public final void set(T t, Float f) {
        if (t != null) {
            setValue(t, f.floatValue());
        }
    }

    public void setMinVisibleChange(float f) {
        this.mMinVisibleChange = f;
    }

    public float getMinVisibleChange() {
        return this.mMinVisibleChange;
    }

    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode() + "{name='" + this.mPropertyName + "',min='" + this.mMinVisibleChange + "'}";
    }
}
