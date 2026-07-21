package miuix.animation.base;

import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import miuix.animation.FolmeEase;
import miuix.animation.internal.DesignReview;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;
import miuix.animation.utils.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AnimConfig implements DesignReview {
    public static final long FLAG_AUTO_INIT = 8;
    public static final long FLAG_DELTA = 1;
    public static final long FLAG_INIT = 2;
    public static final long FLAG_INT = 4;
    public static final int TINT_ALPHA = 0;
    public static final int TINT_AUTO = -1;
    public static final int TINT_OPAQUE = 1;
    public static final int TINT_REGION_USER_DEFINED = 3;
    public static final EaseManager.EaseStyle sDefEase = FolmeEase.spring(0.95f, 0.35f);
    public long delay;
    public EaseManager.EaseStyle ease;
    public long flags;
    public float fromSpeed;
    public final HashSet<TransitionListener> listeners;
    private Set<String> mFocusPropertyNames;
    private Looper mObserverLooper;
    private final Map<String, AnimSpecialConfig> mSpecialNameMap;

    @Deprecated
    public long minDuration;
    public boolean startImmediately;
    public Object tag;
    public int tintMode;

    public AnimConfig() {
        this(false);
    }

    public AnimConfig(boolean z) {
        this.fromSpeed = Float.MAX_VALUE;
        this.startImmediately = true;
        this.mFocusPropertyNames = null;
        this.mObserverLooper = null;
        this.tintMode = -1;
        if (!z) {
            this.mSpecialNameMap = new HashMap();
            this.listeners = new HashSet<>();
        } else {
            this.mSpecialNameMap = null;
            this.listeners = null;
        }
    }

    public AnimConfig(AnimConfig animConfig) {
        this(false);
        copy(animConfig);
    }

    public void copy(AnimConfig animConfig) {
        if (animConfig == null || animConfig == this) {
            return;
        }
        this.delay = animConfig.delay;
        this.ease = animConfig.ease;
        this.listeners.addAll(animConfig.listeners);
        this.tag = animConfig.tag;
        this.flags = animConfig.flags;
        this.fromSpeed = animConfig.fromSpeed;
        this.startImmediately = animConfig.startImmediately;
        this.minDuration = animConfig.minDuration;
        this.tintMode = animConfig.tintMode;
        addFocusPropertyForComplete(animConfig);
        Map<String, AnimSpecialConfig> map = this.mSpecialNameMap;
        if (map != null) {
            map.clear();
            this.mSpecialNameMap.putAll(animConfig.mSpecialNameMap);
        }
    }

    public void clear() {
        this.delay = 0L;
        this.ease = null;
        this.listeners.clear();
        this.tag = null;
        this.flags = 0L;
        this.fromSpeed = Float.MAX_VALUE;
        this.startImmediately = true;
        this.minDuration = 0L;
        this.tintMode = -1;
        this.mFocusPropertyNames = null;
        Map<String, AnimSpecialConfig> map = this.mSpecialNameMap;
        if (map != null) {
            map.clear();
        }
    }

    public AnimConfig setEase(EaseManager.EaseStyle easeStyle) {
        this.ease = easeStyle;
        return this;
    }

    @Deprecated
    public AnimConfig setEase(int i, float... fArr) {
        EaseManager.EaseStyle style = EaseManager.getStyle(i, fArr);
        this.ease = style;
        if ((style instanceof EaseManager.DurationMotionEaseStyle) && (fArr == null || fArr.length == 0)) {
            style.setFactors(300.0d);
            LogUtils.logThread(CommonUtils.TAG, "Folme use warning!! You can't setEase " + FolmeEase.getStyleName(i) + " by style without factors, trace:" + Log.getStackTraceString(new Throwable()));
        }
        return this;
    }

    public AnimConfig setDelay(long j) {
        this.delay = j;
        return this;
    }

    public AnimConfig setFromSpeed(float f) {
        this.fromSpeed = f;
        return this;
    }

    public AnimConfig enableStartImmediately(boolean z) {
        this.startImmediately = z;
        return this;
    }

    public AnimConfig addListeners(TransitionListener... transitionListenerArr) {
        Collections.addAll(this.listeners, transitionListenerArr);
        return this;
    }

    public AnimConfig setTintMode(int i) {
        this.tintMode = i;
        return this;
    }

    public void addFocusPropertyForComplete(String... strArr) {
        if (strArr == null || strArr.length <= 0) {
            return;
        }
        if (this.mFocusPropertyNames == null) {
            this.mFocusPropertyNames = new HashSet();
        }
        this.mFocusPropertyNames.addAll(Arrays.asList(strArr));
    }

    public void addFocusPropertyForComplete(FloatProperty... floatPropertyArr) {
        if (floatPropertyArr == null || floatPropertyArr.length <= 0) {
            return;
        }
        if (this.mFocusPropertyNames == null) {
            this.mFocusPropertyNames = new HashSet();
        }
        for (FloatProperty floatProperty : floatPropertyArr) {
            this.mFocusPropertyNames.add(floatProperty.getName());
        }
    }

    public void clearFocusPropertyForComplete() {
        this.mFocusPropertyNames = null;
    }

    public void addFocusPropertyForComplete(AnimConfig animConfig) {
        if (animConfig.mFocusPropertyNames != null) {
            if (this.mFocusPropertyNames == null) {
                this.mFocusPropertyNames = new HashSet();
            }
            this.mFocusPropertyNames.addAll(animConfig.mFocusPropertyNames);
        }
    }

    public void addSpecialConfigs(AnimConfig animConfig) {
        Map<String, AnimSpecialConfig> map = animConfig.mSpecialNameMap;
        if (map != null) {
            this.mSpecialNameMap.putAll(map);
        }
    }

    public void setObserverLooper(Looper looper) {
        this.mObserverLooper = looper;
    }

    public Looper getObserverLooper() {
        return this.mObserverLooper;
    }

    public AnimConfig removeListeners(TransitionListener... transitionListenerArr) {
        if (transitionListenerArr.length == 0) {
            this.listeners.clear();
        } else {
            this.listeners.removeAll(Arrays.asList(transitionListenerArr));
        }
        return this;
    }

    public AnimConfig setTag(Object obj) {
        this.tag = obj;
        return this;
    }

    public AnimConfig setMinDuration(long j) {
        this.minDuration = j;
        return this;
    }

    public AnimConfig setSpecial(String str, long j, float... fArr) {
        return setSpecial(str, (EaseManager.EaseStyle) null, j, fArr);
    }

    public AnimConfig setSpecial(String str, EaseManager.EaseStyle easeStyle, float... fArr) {
        return setSpecial(str, easeStyle, 0L, fArr);
    }

    public AnimConfig setSpecial(String str, EaseManager.EaseStyle easeStyle, long j, float... fArr) {
        setSpecial(queryAndCreateSpecial(str, true), easeStyle, j, fArr);
        return this;
    }

    public AnimConfig setSpecial(FloatProperty floatProperty, long j, float... fArr) {
        return setSpecial(floatProperty, (EaseManager.EaseStyle) null, j, fArr);
    }

    public AnimConfig setSpecial(FloatProperty floatProperty, EaseManager.EaseStyle easeStyle, float... fArr) {
        setSpecial(floatProperty, easeStyle, -1L, fArr);
        return this;
    }

    public AnimConfig setSpecial(FloatProperty floatProperty, EaseManager.EaseStyle easeStyle, long j, float... fArr) {
        setSpecial(queryAndCreateSpecial(floatProperty, true), easeStyle, j, fArr);
        return this;
    }

    void setSpecial(AnimSpecialConfig animSpecialConfig, EaseManager.EaseStyle easeStyle, long j, float... fArr) {
        if (easeStyle != null) {
            animSpecialConfig.setEase(easeStyle);
        }
        if (j > 0) {
            animSpecialConfig.setDelay(j);
        }
        if (fArr.length > 0) {
            animSpecialConfig.setFromSpeed(fArr[0]);
        }
    }

    public boolean isFocusPropertyForComplete(FloatProperty floatProperty) {
        Set<String> set = this.mFocusPropertyNames;
        if (set == null) {
            return false;
        }
        return set.contains(floatProperty.getName());
    }

    public int getFocusPropertyCount() {
        Set<String> set = this.mFocusPropertyNames;
        if (set == null) {
            return 0;
        }
        return set.size();
    }

    public AnimSpecialConfig getSpecialConfig(FloatProperty floatProperty) {
        return queryAndCreateSpecial(floatProperty, false);
    }

    public AnimSpecialConfig queryAndCreateSpecial(FloatProperty floatProperty) {
        return queryAndCreateSpecial(floatProperty, true);
    }

    public AnimSpecialConfig queryAndCreateSpecial(String str) {
        return queryAndCreateSpecial(str, true);
    }

    private AnimSpecialConfig queryAndCreateSpecial(FloatProperty floatProperty, boolean z) {
        if (floatProperty == null) {
            return null;
        }
        return queryAndCreateSpecial(floatProperty.getName(), z);
    }

    public AnimSpecialConfig getSpecialConfig(String str) {
        return queryAndCreateSpecial(str, false);
    }

    public AnimConfig setSpecial(FloatProperty floatProperty, AnimSpecialConfig animSpecialConfig) {
        if (animSpecialConfig != null) {
            this.mSpecialNameMap.put(floatProperty.getName(), animSpecialConfig);
        } else {
            this.mSpecialNameMap.remove(floatProperty.getName());
        }
        return this;
    }

    public AnimConfig setSpecial(String str, AnimSpecialConfig animSpecialConfig) {
        if (animSpecialConfig != null) {
            this.mSpecialNameMap.put(str, animSpecialConfig);
        } else {
            this.mSpecialNameMap.remove(str);
        }
        return this;
    }

    public Set<String> getSpecialSet() {
        return this.mSpecialNameMap.keySet();
    }

    private AnimSpecialConfig queryAndCreateSpecial(String str, boolean z) {
        AnimSpecialConfig animSpecialConfig = this.mSpecialNameMap.get(str);
        if (animSpecialConfig != null || !z) {
            return animSpecialConfig;
        }
        AnimSpecialConfig animSpecialConfig2 = new AnimSpecialConfig();
        this.mSpecialNameMap.put(str, animSpecialConfig2);
        return animSpecialConfig2;
    }

    public String toString() {
        return "AnimConfig@" + hashCode() + "{\n\t\tdelay=" + this.delay + ", minDuration=" + this.minDuration + ", ease=" + this.ease + ",\n\t\tfromSpeed=" + this.fromSpeed + ", startImmediately=" + this.startImmediately + ", tintMode=" + this.tintMode + ", tag=" + this.tag + ",\n\t\tflags=" + this.flags + ", listeners=" + this.listeners + ", focusP=" + ((Object) CommonUtils.setToString(this.mFocusPropertyNames)) + ",\n\t\tspecialNameMap=" + ((Object) CommonUtils.mapToString(this.mSpecialNameMap, "    ")) + "\n\t}";
    }

    @Override // miuix.animation.internal.DesignReview
    public String getDesignInfo() {
        StringBuilder sb = new StringBuilder("{\"ease\": ");
        EaseManager.EaseStyle easeStyle = this.ease;
        ArrayList arrayList = null;
        sb.append(easeStyle != null ? easeStyle.getDesignInfo() : null);
        if (this.delay > 0) {
            sb.append(", \"delay\": ");
            sb.append(this.delay);
        }
        if (!this.mSpecialNameMap.isEmpty()) {
            for (String str : this.mSpecialNameMap.keySet()) {
                if (arrayList == null) {
                    arrayList = new ArrayList(this.mSpecialNameMap.size());
                }
                AnimSpecialConfig animSpecialConfig = this.mSpecialNameMap.get(str);
                if (animSpecialConfig != null && animSpecialConfig.ease != null) {
                    arrayList.add("\"" + str + "\":" + animSpecialConfig.getDesignInfo());
                }
            }
        }
        if (arrayList != null && !arrayList.isEmpty()) {
            sb.append(", \"special\": {");
            for (int i = 0; i < arrayList.size(); i++) {
                sb.append((String) arrayList.get(i));
                if (i == arrayList.size() - 1) {
                    break;
                }
                sb.append(", ");
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }
}
