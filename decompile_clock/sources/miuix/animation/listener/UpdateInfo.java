package miuix.animation.listener;

import com.android.deskclock.util.AlarmHelper;
import java.util.Collection;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.internal.AnimInfo;
import miuix.animation.internal.AnimTask;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.utils.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class UpdateInfo {
    public int frameCount;
    public boolean isCompleted;
    public boolean justStart;
    public final FloatProperty property;
    public final boolean useInt;
    public double velocity;
    public Integer preparedTransitionId = null;
    public final AnimInfo animInfo = new AnimInfo();

    public static UpdateInfo findByName(Collection<UpdateInfo> collection, String str) {
        for (UpdateInfo updateInfo : collection) {
            if (updateInfo.property.getName().equals(str)) {
                return updateInfo;
            }
        }
        return null;
    }

    public static UpdateInfo findBy(Collection<UpdateInfo> collection, FloatProperty floatProperty) {
        for (UpdateInfo updateInfo : collection) {
            if (updateInfo.property.equals(floatProperty)) {
                return updateInfo;
            }
        }
        return null;
    }

    public UpdateInfo(FloatProperty floatProperty) {
        this.property = floatProperty;
        this.useInt = floatProperty instanceof IIntValueProperty;
    }

    public Class<?> getType() {
        return this.property instanceof IIntValueProperty ? Integer.TYPE : Float.TYPE;
    }

    public <T> T getValue(Class<T> cls) {
        if (cls == Float.class || cls == Float.TYPE) {
            return (T) Float.valueOf(getFloatValue());
        }
        if (cls == Double.class || cls == Double.TYPE) {
            return (T) Double.valueOf(this.animInfo.value);
        }
        return (T) Integer.valueOf(getIntValue());
    }

    public float getFloatValue() {
        double d = this.animInfo.setToValue;
        if (d != Double.MAX_VALUE) {
            return (float) d;
        }
        float f = (float) (this.animInfo.value < 3.4028234663852886E38d ? this.animInfo.value : 3.4028234663852886E38d);
        if (f == Float.MAX_VALUE) {
            LogUtils.debug("warning value is Float.MAX_VALUE !! correct to startValue " + this.animInfo.startValue + " " + this, new Object[0]);
            AnimInfo animInfo = this.animInfo;
            animInfo.value = animInfo.startValue;
            return (float) this.animInfo.startValue;
        }
        return Math.max(-3.4028235E38f, f);
    }

    public int getIntValue() {
        double d = this.animInfo.setToValue;
        if (d != Double.MAX_VALUE) {
            return (int) d;
        }
        int i = this.animInfo.value >= Double.MAX_VALUE ? Integer.MAX_VALUE : (int) this.animInfo.value;
        if (i == Integer.MAX_VALUE) {
            LogUtils.debug("warning value is Integer.MAX_VALUE !! correct to startValue " + this.animInfo.startValue + " " + this, new Object[0]);
            AnimInfo animInfo = this.animInfo;
            animInfo.value = animInfo.startValue;
            return (int) this.animInfo.startValue;
        }
        return Math.max(AlarmHelper.SLEEP_ALARM_ID, i);
    }

    public String toString() {
        return "UpdateInfo{id=" + hashCode() + " " + this.property.getName() + "=" + this.animInfo.value + ", v_format=" + (this.useInt ? Integer.toHexString((int) this.animInfo.value) : Double.toString(this.animInfo.value)) + ", p=" + this.property + ", op=" + ((int) this.animInfo.op) + ", v=" + this.animInfo.value + ", start-v=" + this.animInfo.startValue + ", target-v=" + this.animInfo.targetValue + ", useInt=" + this.useInt + ", completed=" + this.isCompleted + ", setTo-v=" + this.animInfo.setToValue + ", velocity=" + this.velocity + ", start-t=" + this.animInfo.startTime + ", frameCount=" + this.frameCount + ", frameInterval=" + this.animInfo.frameInterval + '}';
    }

    public void reset() {
        this.isCompleted = false;
        this.frameCount = 0;
    }

    public void setOp(byte b) {
        boolean z = b == 0 || b > 2;
        this.isCompleted = z;
        if (z && AnimTask.isRunning(this.animInfo.op)) {
            this.animInfo.justEnd = true;
        }
        this.animInfo.op = b;
        if (LogUtils.isLogMoreEnable()) {
            LogUtils.debug("---- UpdateInfo id=" + hashCode(), "name=" + this.property.getName(), "setOp=" + ((int) b), "justEnd=" + this.animInfo.justEnd, "completed=" + this.isCompleted);
        }
    }

    public boolean isValid() {
        return this.property != null;
    }

    public void skipToTargetValue(IAnimTarget iAnimTarget) {
        if (this.animInfo.targetValue != Double.MAX_VALUE) {
            AnimInfo animInfo = this.animInfo;
            animInfo.value = animInfo.targetValue;
        }
        this.velocity = 0.0d;
        setTargetValue(iAnimTarget, false);
        setOp((byte) 3);
    }

    public void setTargetValue(IAnimTarget iAnimTarget, boolean z) {
        if (z) {
            if (this.useInt) {
                iAnimTarget.doSetIntValue((IIntValueProperty) this.property, getIntValue());
                return;
            } else {
                iAnimTarget.doSetValue(this.property, getFloatValue());
                return;
            }
        }
        if (this.useInt) {
            iAnimTarget.setIntValue((IIntValueProperty) this.property, getIntValue());
        } else {
            iAnimTarget.setValue(this.property, getFloatValue());
        }
    }

    public static int getSafeIntValue(int i, AnimSpecialConfig animSpecialConfig) {
        double d;
        if (animSpecialConfig == null || !animSpecialConfig.hasSetSafeValue) {
            return i;
        }
        if (i < ((int) animSpecialConfig.minValue)) {
            d = animSpecialConfig.minValue;
        } else {
            if (i <= ((int) animSpecialConfig.maxValue)) {
                return i;
            }
            d = animSpecialConfig.maxValue;
        }
        return (int) d;
    }

    public static float getSafeFloatValue(float f, AnimSpecialConfig animSpecialConfig) {
        double d;
        if (animSpecialConfig == null || !animSpecialConfig.hasSetSafeValue) {
            return f;
        }
        if (f < ((float) animSpecialConfig.minValue)) {
            d = animSpecialConfig.minValue;
        } else {
            if (f <= ((float) animSpecialConfig.maxValue)) {
                return f;
            }
            d = animSpecialConfig.maxValue;
        }
        return (float) d;
    }
}
