package miuix.animation.property;

/* JADX INFO: loaded from: classes2.dex */
public class IntValueProperty<T> extends ValueProperty<T> implements IIntValueProperty<T> {
    public IntValueProperty(String str) {
        super(str, 1.0f);
    }

    public IntValueProperty(String str, float f) {
        super(str, f);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.animation.property.IIntValueProperty
    public void setIntValue(T t, int i) {
        if (t instanceof ValueTargetObject) {
            ((ValueTargetObject) t).setPropertyValue(getName(), Integer.TYPE, Integer.valueOf(i));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.animation.property.IIntValueProperty
    public int getIntValue(T t) {
        Integer num;
        if (!(t instanceof ValueTargetObject) || (num = (Integer) ((ValueTargetObject) t).getPropertyValue(getName(), Integer.TYPE)) == null) {
            return Integer.MAX_VALUE;
        }
        return num.intValue();
    }

    @Override // miuix.animation.property.ValueProperty, miuix.animation.property.FloatProperty
    public String toString() {
        return "IntValueProperty@" + hashCode() + "{name='" + getName() + "',min='" + this.mMinVisibleChange + "'}";
    }
}
