package miuix.animation.controller;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import miuix.animation.FolmeFactory;
import miuix.animation.IAnimTarget;
import miuix.animation.ViewTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.internal.AnimValueUtils;
import miuix.animation.internal.DesignReview;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ColorProperty;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.property.ISpecificProperty;
import miuix.animation.property.IntValueProperty;
import miuix.animation.property.ValueProperty;
import miuix.animation.property.ViewProperty;
import miuix.animation.property.ViewPropertyExt;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.ObjectPool;

/* JADX INFO: loaded from: classes2.dex */
public class AnimState implements DesignReview {
    public static final long FLAG_IN_TOUCH = 4;
    public static final long FLAG_PARALLEL = 2;
    public static final long FLAG_QUEUE = 1;
    private static final int STEP = 100;
    private static final String TAG = "TAG_";
    public static final int VIEW_POS = 1000100;
    public static final int VIEW_SIZE = 1000000;
    private static final AtomicInteger sId = new AtomicInteger();
    public long flags;
    private volatile String mAlias;
    private final AnimConfig mConfig;
    private final Map<Object, Double> mInitMap;
    private final Map<Object, Double> mMap;
    private volatile Object mTag;
    public final boolean needDuplicate;
    IntValueProperty tempIntValueProperty;
    ValueProperty tempValueProperty;

    public static void alignState(AnimState animState, Collection<UpdateInfo> collection) {
        UpdateInfo updateInfoFindByName;
        for (UpdateInfo updateInfo : collection) {
            if (!animState.contains(updateInfo.property)) {
                if (updateInfo.useInt) {
                    animState.add(updateInfo.property, (int) updateInfo.animInfo.startValue);
                } else {
                    animState.add(updateInfo.property, (float) updateInfo.animInfo.startValue);
                }
            }
        }
        ObjectPool objPool = FolmeFactory.getEngine().getObjPool();
        List list = (List) ObjectPool.acquire(objPool, ArrayList.class, new Object[0]);
        for (Object obj : animState.keySet()) {
            if (obj instanceof FloatProperty) {
                updateInfoFindByName = UpdateInfo.findBy(collection, (FloatProperty) obj);
            } else {
                updateInfoFindByName = UpdateInfo.findByName(collection, (String) obj);
            }
            if (updateInfoFindByName == null) {
                list.add(obj);
            }
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            animState.remove(it.next());
        }
        ObjectPool.release(objPool, list);
    }

    public AnimState() {
        this(null, null, false);
    }

    public AnimState(Object obj) {
        this(obj, null, false);
    }

    public AnimState(Object obj, String str) {
        this(obj, str, false);
    }

    public AnimState(Object obj, boolean z) {
        this.tempValueProperty = new ValueProperty("");
        this.tempIntValueProperty = new IntValueProperty("");
        this.mConfig = new AnimConfig();
        this.mInitMap = new ConcurrentHashMap();
        this.mMap = new ConcurrentHashMap();
        setTag(obj);
        if (obj instanceof String) {
            setAlias((String) obj);
        }
        this.needDuplicate = z;
    }

    public AnimState(Object obj, String str, boolean z) {
        this.tempValueProperty = new ValueProperty("");
        this.tempIntValueProperty = new IntValueProperty("");
        this.mConfig = new AnimConfig();
        this.mInitMap = new ConcurrentHashMap();
        this.mMap = new ConcurrentHashMap();
        setTag(obj);
        if (str == null) {
            if (obj instanceof String) {
                setAlias((String) obj);
            }
        } else {
            setAlias(str);
        }
        this.needDuplicate = z;
    }

    public final void setTag(Object obj) {
        if (obj == null) {
            obj = TAG + sId.incrementAndGet();
        }
        this.mTag = obj;
    }

    public final AnimState setAlias(String str) {
        this.mAlias = str;
        return this;
    }

    public void clear() {
        this.mConfig.clear();
        this.mInitMap.clear();
        this.mMap.clear();
    }

    public void set(AnimState animState) {
        if (animState == null) {
            return;
        }
        setTag(animState.mTag);
        append(animState);
    }

    private void append(AnimState animState) {
        this.mConfig.copy(animState.mConfig);
        this.mInitMap.clear();
        this.mInitMap.putAll(animState.mInitMap);
        this.mMap.clear();
        this.mMap.putAll(animState.mMap);
    }

    public Object getTag() {
        return this.mTag;
    }

    public String getAlias() {
        return this.mAlias;
    }

    public AnimState add(String str, float f) {
        return add(str, f, (long[]) null);
    }

    public AnimState add(String str, float f, long... jArr) {
        if (jArr != null && jArr.length > 0) {
            setConfigFlag(str, jArr[0]);
        }
        return add(str, f);
    }

    public AnimState add(String str, int i) {
        return add(str, i, (long[]) null);
    }

    public AnimState add(String str, int i, long... jArr) {
        if (jArr != null && jArr.length > 0) {
            setConfigFlag(str, 4 | jArr[0]);
        } else {
            setConfigFlag(str, 4 | getConfigFlags(str));
        }
        return add(str, i);
    }

    public AnimState add(ViewProperty viewProperty, float f, long... jArr) {
        return add((FloatProperty) viewProperty, f, jArr);
    }

    public AnimState add(ViewProperty viewProperty, int i, long... jArr) {
        return add((FloatProperty) viewProperty, i, jArr);
    }

    public AnimState add(FloatProperty floatProperty, float f, long... jArr) {
        if (jArr != null && jArr.length > 0) {
            setConfigFlag(floatProperty, jArr[0]);
        }
        return add(floatProperty, f);
    }

    public AnimState add(FloatProperty floatProperty, int i, long... jArr) {
        if (jArr != null && jArr.length > 0) {
            setConfigFlag(floatProperty, 4 | jArr[0]);
        } else {
            setConfigFlag(floatProperty, 4 | getConfigFlags(floatProperty));
        }
        return add(floatProperty, i);
    }

    public AnimState add(Object obj, double d) {
        if (Double.isNaN(d)) {
            Log.w(CommonUtils.TAG, "warning! the add value is NaN, will not add to AnimState. key: " + obj + " trace: " + Log.getStackTraceString(new Throwable()));
            return this;
        }
        if (Double.isInfinite(d)) {
            Log.w(CommonUtils.TAG, "warning! the add value is Infinite, will not add to AnimState. key: " + obj + " trace: " + Log.getStackTraceString(new Throwable()));
            return this;
        }
        if (removeInitMapValue(obj)) {
            setConfigFlag(obj, getConfigFlags(obj) & (-9));
        }
        setMapValue(obj, d);
        return this;
    }

    public AnimState addWithInit(String str, float f, float f2) {
        return addWithInit(str, f, f2, (long[]) null);
    }

    public AnimState addWithInit(String str, float f, float f2, long... jArr) {
        if (jArr != null && jArr.length > 0) {
            setConfigFlag(str, jArr[0]);
        }
        return addWithInit(str, f, f2);
    }

    public AnimState addWithInit(String str, int i, int i2) {
        return addWithInit(str, i, i2, (long[]) null);
    }

    public AnimState addWithInit(String str, int i, int i2, long... jArr) {
        if (jArr != null && jArr.length > 0) {
            setConfigFlag(str, 4 | jArr[0]);
        } else {
            setConfigFlag(str, 4 | getConfigFlags(str));
        }
        return addWithInit(str, i, i2);
    }

    public AnimState addWithInit(ViewProperty viewProperty, float f, float f2, long... jArr) {
        return addWithInit((FloatProperty) viewProperty, f, f2, jArr);
    }

    public AnimState addWithInit(ViewProperty viewProperty, int i, int i2, long... jArr) {
        return addWithInit((FloatProperty) viewProperty, i, i2, jArr);
    }

    public AnimState addWithInit(FloatProperty floatProperty, float f, float f2, long... jArr) {
        if (jArr != null && jArr.length > 0) {
            setConfigFlag(floatProperty, jArr[0]);
        }
        return addWithInit(floatProperty, f, f2);
    }

    public AnimState addWithInit(FloatProperty floatProperty, int i, int i2, long... jArr) {
        if (jArr != null && jArr.length > 0) {
            setConfigFlag(floatProperty, 4 | jArr[0]);
        } else {
            setConfigFlag(floatProperty, 4 | getConfigFlags(floatProperty));
        }
        return addWithInit(floatProperty, i, i2);
    }

    public AnimState addWithInit(Object obj, double d, double d2) {
        if (Double.isNaN(d)) {
            Log.w(CommonUtils.TAG, "warning! the add initValue is NaN, will not add to AnimState. key: " + obj + " trace: " + Log.getStackTraceString(new Throwable()));
            return this;
        }
        if (Double.isInfinite(d)) {
            Log.w(CommonUtils.TAG, "warning! the add initValue is Infinite, will not add to AnimState. key: " + obj + " trace: " + Log.getStackTraceString(new Throwable()));
            return this;
        }
        if (Double.isNaN(d2)) {
            Log.w(CommonUtils.TAG, "warning! the add value is NaN, will not add to AnimState. key: " + obj + " trace: " + Log.getStackTraceString(new Throwable()));
            return this;
        }
        if (Double.isInfinite(d2)) {
            Log.w(CommonUtils.TAG, "warning! the add value is Infinite, will not add to AnimState. key: " + obj + " trace: " + Log.getStackTraceString(new Throwable()));
            return this;
        }
        setConfigFlag(obj, getConfigFlags(obj) | 8);
        setInitMapValue(obj, d);
        setMapValue(obj, d2);
        return this;
    }

    public void setConfigFlag(Object obj, long j) {
        this.mConfig.queryAndCreateSpecial(obj instanceof FloatProperty ? ((FloatProperty) obj).getName() : (String) obj).flags = j;
    }

    public boolean contains(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.mMap.containsKey(obj)) {
            return true;
        }
        if (obj instanceof FloatProperty) {
            return this.mMap.containsKey(((FloatProperty) obj).getName());
        }
        return false;
    }

    public boolean isEmpty() {
        return this.mMap.isEmpty();
    }

    public Set<Object> keySet() {
        return this.mMap.keySet();
    }

    public int getInt(IIntValueProperty iIntValueProperty) {
        Double mapValue = getMapValue(iIntValueProperty);
        if (mapValue != null) {
            return mapValue.intValue();
        }
        return Integer.MAX_VALUE;
    }

    public int getInt(String str) {
        return getInt(new IntValueProperty(str));
    }

    public float getFloat(FloatProperty floatProperty) {
        Double mapValue = getMapValue(floatProperty);
        if (mapValue != null) {
            return mapValue.floatValue();
        }
        return Float.MAX_VALUE;
    }

    public float getFloat(String str) {
        Double mapValue = getMapValue(str);
        if (mapValue != null) {
            return mapValue.floatValue();
        }
        return Float.MAX_VALUE;
    }

    public double get(IAnimTarget iAnimTarget, FloatProperty floatProperty) {
        Double mapValue = getMapValue(floatProperty);
        if (mapValue != null) {
            return getProperValue(iAnimTarget, floatProperty, mapValue.doubleValue());
        }
        return Double.MAX_VALUE;
    }

    public double getInit(IAnimTarget iAnimTarget, FloatProperty floatProperty) {
        Double initMapValue = getInitMapValue(floatProperty);
        if (initMapValue != null) {
            return initMapValue.doubleValue();
        }
        return Double.MAX_VALUE;
    }

    private Double getInitMapValue(Object obj) {
        Double d = this.mInitMap.get(obj);
        return (d == null && (obj instanceof FloatProperty)) ? this.mInitMap.get(((FloatProperty) obj).getName()) : d;
    }

    private void setInitMapValue(Object obj, double d) {
        if (obj instanceof FloatProperty) {
            FloatProperty floatProperty = (FloatProperty) obj;
            if (this.mInitMap.containsKey(floatProperty.getName())) {
                this.mInitMap.put(floatProperty.getName(), Double.valueOf(d));
                return;
            }
        }
        this.mInitMap.put(obj, Double.valueOf(d));
    }

    private boolean removeInitMapValue(Object obj) {
        if (obj instanceof FloatProperty) {
            FloatProperty floatProperty = (FloatProperty) obj;
            if (this.mInitMap.containsKey(floatProperty.getName())) {
                this.mInitMap.remove(floatProperty.getName());
                return true;
            }
        }
        if (!this.mInitMap.containsKey(obj)) {
            return false;
        }
        this.mInitMap.remove(obj);
        return true;
    }

    private Double getMapValue(Object obj) {
        Double d = this.mMap.get(obj);
        return (d == null && (obj instanceof FloatProperty)) ? this.mMap.get(((FloatProperty) obj).getName()) : d;
    }

    private void setMapValue(Object obj, double d) {
        if (obj instanceof FloatProperty) {
            FloatProperty floatProperty = (FloatProperty) obj;
            if (this.mMap.containsKey(floatProperty.getName())) {
                this.mMap.put(floatProperty.getName(), Double.valueOf(d));
                return;
            }
        }
        this.mMap.put(obj, Double.valueOf(d));
    }

    private double getProperValue(IAnimTarget iAnimTarget, FloatProperty floatProperty, double d) {
        long configFlags = getConfigFlags(floatProperty);
        boolean zHasFlags = CommonUtils.hasFlags(configFlags, 1L);
        if (!zHasFlags && d != 1000000.0d && d != 1000100.0d && !(floatProperty instanceof ISpecificProperty)) {
            return d;
        }
        double value = AnimValueUtils.getValue(iAnimTarget, floatProperty, d);
        if (!zHasFlags || !AnimValueUtils.isValid(d)) {
            return value;
        }
        setConfigFlag(floatProperty, configFlags & (-2));
        double d2 = value + d;
        setMapValue(floatProperty, d2);
        return d2;
    }

    public long getConfigFlags(Object obj) {
        AnimSpecialConfig specialConfig = this.mConfig.getSpecialConfig(obj instanceof FloatProperty ? ((FloatProperty) obj).getName() : (String) obj);
        if (specialConfig != null) {
            return specialConfig.flags;
        }
        return 0L;
    }

    public AnimConfig getConfig() {
        return this.mConfig;
    }

    public AnimState remove(Object obj) {
        this.mMap.remove(obj);
        if (obj instanceof FloatProperty) {
            this.mMap.remove(((FloatProperty) obj).getName());
        }
        return this;
    }

    public FloatProperty getProperty(IAnimTarget iAnimTarget, Object obj) {
        FloatProperty floatProperty = ((obj instanceof String) && (iAnimTarget instanceof ViewTarget)) ? ViewTarget.getFloatProperty((String) obj) : null;
        return floatProperty == null ? getProperty(obj) : floatProperty;
    }

    public FloatProperty getProperty(Object obj) {
        if (obj instanceof FloatProperty) {
            return (FloatProperty) obj;
        }
        String str = (String) obj;
        if (CommonUtils.hasFlags(getConfigFlags(str), 4L)) {
            return new IntValueProperty(str);
        }
        return new ValueProperty(str);
    }

    public FloatProperty getTempProperty(Object obj) {
        if (obj instanceof FloatProperty) {
            return (FloatProperty) obj;
        }
        String str = (String) obj;
        ValueProperty valueProperty = CommonUtils.hasFlags(getConfigFlags(str), 4L) ? this.tempIntValueProperty : this.tempValueProperty;
        valueProperty.setName(str);
        return valueProperty;
    }

    public String toString() {
        return "\nState{\n\ttag='" + this.mTag + "',\n\tflags=" + this.flags + ",\n\tconfig=" + this.mConfig + ",\n\tmaps=" + ((Object) CommonUtils.mapToString(this.mMap, "    ")) + "\n}";
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.animation.internal.DesignReview
    public String getDesignInfo() {
        String string;
        StringBuilder sb = new StringBuilder();
        sb.append("{\"name\": \"");
        sb.append(this.mAlias == null ? "null" : this.mAlias).append("\", ");
        for (Object obj : this.mMap.keySet()) {
            if (obj instanceof FloatProperty) {
                string = ((FloatProperty) obj).getName();
            } else {
                string = obj.toString();
            }
            FloatProperty property = getProperty(obj);
            sb.append("\"").append(string).append("\": ");
            if (property == ViewPropertyExt.FOREGROUND || property == ViewPropertyExt.BACKGROUND || (property instanceof ColorProperty)) {
                int i = getInt((IIntValueProperty) property);
                if (i != 0) {
                    sb.append("\"#" + Integer.toHexString(i) + "\"");
                } else {
                    sb.append("\"#00000000\"");
                }
            } else if (property instanceof IIntValueProperty) {
                sb.append(getInt((IIntValueProperty) property));
            } else {
                sb.append(getFloat(property));
            }
            sb.append(", ");
        }
        int iLastIndexOf = sb.lastIndexOf(", ");
        sb.delete(iLastIndexOf, iLastIndexOf + 2);
        sb.append('}');
        return sb.toString();
    }
}
