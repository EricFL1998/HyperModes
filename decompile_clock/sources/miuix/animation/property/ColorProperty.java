package miuix.animation.property;

import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public class ColorProperty<T> extends FloatProperty<T> implements IIntValueProperty<T> {
    private int mColorValue;

    @Override // miuix.animation.property.FloatProperty
    public float getValue(T t) {
        return 0.0f;
    }

    @Override // miuix.animation.property.FloatProperty
    public void setValue(T t, float f) {
    }

    public ColorProperty(String str) {
        super(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.animation.property.IIntValueProperty
    public void setIntValue(T t, int i) {
        this.mColorValue = i;
        if (t instanceof ValueTargetObject) {
            ((ValueTargetObject) t).setPropertyValue(getName(), Integer.TYPE, Integer.valueOf(i));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.animation.property.IIntValueProperty
    public int getIntValue(T t) {
        if (t instanceof ValueTargetObject) {
            this.mColorValue = ((Integer) ((ValueTargetObject) t).getPropertyValue(getName(), Integer.TYPE)).intValue();
        }
        return this.mColorValue;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.mPropertyName.equals(((ColorProperty) obj).mPropertyName);
    }

    public int hashCode() {
        return Objects.hash(this.mPropertyName);
    }
}
