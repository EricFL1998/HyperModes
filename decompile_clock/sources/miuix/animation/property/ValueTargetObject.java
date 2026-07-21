package miuix.animation.property;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.FieldManager;
import miuix.animation.utils.GenericClassManager;

/* JADX INFO: loaded from: classes2.dex */
public class ValueTargetObject {
    private WeakReference<Object> mRef;
    private Object mTempObj;
    private FieldManager mFieldManager = new FieldManager();
    private GenericClassManager mClassManager = new GenericClassManager();
    private Map<String, Object> mValueMap = new ConcurrentHashMap();

    public ValueTargetObject(Object obj) {
        if (CommonUtils.isBuiltInClass(obj.getClass())) {
            this.mTempObj = obj;
        } else {
            this.mRef = new WeakReference<>(obj);
        }
    }

    public boolean isValid() {
        return getRealObject() != null;
    }

    public Object getRealObject() {
        WeakReference<Object> weakReference = this.mRef;
        return weakReference != null ? weakReference.get() : this.mTempObj;
    }

    public <T> boolean setField(Object obj, String str, Class<T> cls, T t) {
        return this.mFieldManager.setField(obj, str, cls, t);
    }

    public <T> T getField(Object obj, String str, Class<T> cls) {
        return (T) this.mFieldManager.getField(obj, str, cls);
    }

    public Class<?> getGenericClass(FloatProperty floatProperty) {
        return this.mClassManager.get(floatProperty);
    }

    public Class<?> getGenericClass(IIntValueProperty iIntValueProperty) {
        return this.mClassManager.get(iIntValueProperty);
    }

    public boolean containProperty(String str) {
        return this.mValueMap.containsKey(str);
    }

    public double getPropertyValue(String str) {
        Object obj = this.mValueMap.get(str);
        if (obj == null) {
            return Double.MAX_VALUE;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        if (obj instanceof Float) {
            return ((Float) obj).floatValue();
        }
        if (obj instanceof Double) {
            return ((Double) obj).doubleValue();
        }
        return Double.MAX_VALUE;
    }

    public <T> T getPropertyValue(String str, Class<T> cls) {
        Object realObject = getRealObject();
        if (realObject == null || this.mTempObj == realObject) {
            return (T) this.mValueMap.get(str);
        }
        T t = (T) this.mValueMap.get(str);
        return t != null ? t : (T) getField(realObject, str, cls);
    }

    public <T> void setPropertyValue(String str, Class<T> cls, T t) {
        Object realObject = getRealObject();
        if (realObject == null || this.mTempObj == realObject) {
            this.mValueMap.put(str, t);
        } else if (this.mValueMap.containsKey(str) || !setField(realObject, str, cls, t)) {
            this.mValueMap.put(str, t);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            Object obj2 = this.mTempObj;
            if (obj2 != null) {
                return Objects.equals(obj2, obj);
            }
            Object realObject = getRealObject();
            if (realObject != null) {
                return Objects.equals(realObject, obj);
            }
            return false;
        }
        ValueTargetObject valueTargetObject = (ValueTargetObject) obj;
        Object obj3 = this.mTempObj;
        if (obj3 != null) {
            return Objects.equals(obj3, valueTargetObject.mTempObj);
        }
        return Objects.equals(getRealObject(), valueTargetObject.getRealObject());
    }

    public int hashCode() {
        Object obj = this.mTempObj;
        if (obj != null) {
            return obj.hashCode();
        }
        Object realObject = getRealObject();
        if (realObject != null) {
            return realObject.hashCode();
        }
        return 0;
    }

    public String toString() {
        return "ValueTargetObject@" + hashCode() + "{" + getRealObject() + "}";
    }
}
