package miuix.animation.controller;

import java.lang.reflect.Array;
import miuix.animation.IAnimTarget;
import miuix.animation.ValueTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.property.IntValueProperty;
import miuix.animation.property.ValueProperty;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes2.dex */
class StateHelper {
    static final ValueProperty DEFAULT_PROPERTY = new ValueProperty("defaultProperty");
    static final IntValueProperty DEFAULT_INT_PROPERTY = new IntValueProperty("defaultIntProperty");

    StateHelper() {
    }

    void parse(IAnimTarget iAnimTarget, AnimState animState, AnimConfigLink animConfigLink, boolean z, Object... objArr) {
        Object obj;
        int i;
        if (objArr.length == 0) {
            return;
        }
        int i2 = 0;
        Object obj2 = objArr[0];
        if (obj2 != null && obj2.equals(animState.getTag())) {
            i2 = 1;
        }
        int propertyAndValue = i2;
        while (propertyAndValue < objArr.length) {
            Object obj3 = objArr[propertyAndValue];
            if (z) {
                int i3 = propertyAndValue + 1;
                Object obj4 = i3 < objArr.length ? objArr[i3] : null;
                if ((obj3 instanceof String) && (obj4 instanceof String)) {
                    propertyAndValue = i3;
                } else {
                    i = 2;
                    obj = obj4;
                }
            } else {
                obj = null;
                i = 1;
            }
            int i4 = i + propertyAndValue;
            Object obj5 = i4 < objArr.length ? objArr[i4] : null;
            propertyAndValue = ((obj3 instanceof String) && (obj5 instanceof String)) ? propertyAndValue + 1 : setPropertyAndValue(iAnimTarget, animState, animConfigLink, obj3, obj, obj5, propertyAndValue, objArr);
        }
    }

    private int setPropertyAndValue(IAnimTarget iAnimTarget, AnimState animState, AnimConfigLink animConfigLink, Object obj, Object obj2, Object obj3, int i, Object... objArr) {
        int i2;
        FloatProperty property;
        int iAddProperty = 0;
        if (checkAndSetAnimConfig(animConfigLink, obj) || (property = getProperty(iAnimTarget, obj, obj3)) == null) {
            i2 = i;
        } else {
            i2 = isDefaultProperty(property) ? i : i + 1;
            iAddProperty = addProperty(iAnimTarget, animState, property, i2, obj2 != null, objArr);
        }
        return iAddProperty > 0 ? i2 + iAddProperty : i2 + 1;
    }

    private boolean isDefaultProperty(FloatProperty floatProperty) {
        return floatProperty == DEFAULT_PROPERTY || floatProperty == DEFAULT_INT_PROPERTY;
    }

    private boolean checkAndSetAnimConfig(AnimConfigLink animConfigLink, Object obj) {
        if (obj == null) {
            return false;
        }
        if ((obj instanceof TransitionListener) || (obj instanceof EaseManager.EaseStyle)) {
            setTempConfig(animConfigLink.getHead(), obj);
            return true;
        }
        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            boolean z = false;
            for (int i = 0; i < length; i++) {
                Object obj2 = Array.get(obj, i);
                if (obj2 != null) {
                    z = addConfigToLink(animConfigLink, obj2) || z;
                }
            }
            return z;
        }
        return addConfigToLink(animConfigLink, obj);
    }

    private void setTempConfig(AnimConfig animConfig, Object obj) {
        if (obj instanceof TransitionListener) {
            animConfig.addListeners((TransitionListener) obj);
        } else if (obj instanceof EaseManager.EaseStyle) {
            animConfig.setEase((EaseManager.EaseStyle) obj);
        }
    }

    private boolean addConfigToLink(AnimConfigLink animConfigLink, Object obj) {
        if (obj instanceof AnimConfig) {
            animConfigLink.add((AnimConfig) obj, new boolean[0]);
            return true;
        }
        if (obj instanceof AnimConfigLink) {
            animConfigLink.add((AnimConfigLink) obj, new boolean[0]);
        }
        return false;
    }

    private FloatProperty getProperty(IAnimTarget iAnimTarget, Object obj, Object obj2) {
        if (obj instanceof FloatProperty) {
            return (FloatProperty) obj;
        }
        if ((obj instanceof String) && (iAnimTarget instanceof ValueTarget)) {
            return ValueTarget.createProperty((String) obj, obj2 != null ? obj2.getClass() : null);
        }
        if (obj instanceof Float) {
            return DEFAULT_PROPERTY;
        }
        return null;
    }

    private int addProperty(IAnimTarget iAnimTarget, AnimState animState, FloatProperty floatProperty, int i, boolean z, Object... objArr) {
        Object propertyValue;
        int i2 = 0;
        if (floatProperty == null) {
            return 0;
        }
        if (z) {
            propertyValue = getPropertyValue(i, objArr);
            if (propertyValue != null) {
                i2 = 1;
            }
        } else {
            propertyValue = null;
        }
        int i3 = i2;
        Object propertyValue2 = getPropertyValue(i + i2, objArr);
        if (propertyValue2 == null || !addPropertyValues(animState, floatProperty, propertyValue, propertyValue2)) {
            return i3;
        }
        return setInitVelocity(iAnimTarget, floatProperty, i + (i2 + 1), objArr) ? i3 + 2 : i3 + 1;
    }

    private Object getPropertyValue(int i, Object... objArr) {
        if (i < objArr.length) {
            return objArr[i];
        }
        return null;
    }

    private boolean setInitVelocity(IAnimTarget iAnimTarget, FloatProperty floatProperty, int i, Object... objArr) {
        if (i >= objArr.length) {
            return false;
        }
        Object obj = objArr[i];
        if (!(obj instanceof Float)) {
            return false;
        }
        iAnimTarget.setVelocity(floatProperty, ((Float) obj).floatValue());
        return true;
    }

    private boolean addPropertyValues(AnimState animState, FloatProperty floatProperty, Object obj, Object obj2) {
        boolean z = obj2 instanceof Integer;
        if (!z && !(obj2 instanceof Float) && !(obj2 instanceof Double)) {
            return false;
        }
        if (floatProperty instanceof IIntValueProperty) {
            if (obj != null) {
                animState.addWithInit(floatProperty, toInt(obj, z), toInt(obj2, z));
                return true;
            }
            animState.add(floatProperty, toInt(obj2, z));
            return true;
        }
        if (obj != null) {
            animState.addWithInit(floatProperty, toFloat(obj, z), toFloat(obj2, z));
            return true;
        }
        animState.add(floatProperty, toFloat(obj2, z));
        return true;
    }

    private int toInt(Object obj, boolean z) {
        return z ? ((Integer) obj).intValue() : (int) ((Float) obj).floatValue();
    }

    private float toFloat(Object obj, boolean z) {
        return z ? ((Integer) obj).intValue() : ((Float) obj).floatValue();
    }
}
