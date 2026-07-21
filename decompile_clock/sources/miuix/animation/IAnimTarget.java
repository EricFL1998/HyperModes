package miuix.animation;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.controller.AnimState;
import miuix.animation.internal.AnimManager;
import miuix.animation.internal.NotifyManager;
import miuix.animation.internal.TargetHandler;
import miuix.animation.internal.TargetVelocityTracker;
import miuix.animation.listener.ListenerNotifier;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public abstract class IAnimTarget<T> {
    public static final long FLAT_ONESHOT = 1;
    static final AtomicInteger sTargetIds = new AtomicInteger(Integer.MAX_VALUE);
    public final AnimManager animManager;
    protected TargetHandler handler;
    public final int id;
    float mDefaultMinVisible;
    long mFlags;
    long mFlagsSetTime;
    boolean mIsSleep;
    Map<Object, Float> mMinVisibleChanges;
    boolean mShouldCheckValue;
    final TargetVelocityTracker mTracker;
    NotifyManager notifyManager;

    public boolean allowAnimRun() {
        return true;
    }

    public abstract void clean();

    public float getDefaultMinVisible() {
        return 1.0f;
    }

    public abstract double getDoubleValue(FloatProperty floatProperty);

    public abstract T getTargetObject();

    public double getVelocity(String str) {
        return 0.0d;
    }

    public boolean isValid() {
        return true;
    }

    public void onFrameEnd(boolean z) {
    }

    public void setVelocity(String str, double d) {
    }

    public IAnimTarget(Looper looper) {
        AnimManager animManager = new AnimManager();
        this.animManager = animManager;
        this.notifyManager = new NotifyManager(this);
        this.mDefaultMinVisible = Float.MAX_VALUE;
        this.mMinVisibleChanges = new ConcurrentHashMap();
        this.mShouldCheckValue = true;
        this.id = sTargetIds.decrementAndGet();
        this.mTracker = new TargetVelocityTracker();
        this.handler = createHandler(looper);
        if (LogUtils.isLogMoreEnable()) {
            LogUtils.debug("IAnimTarget create with looper! " + looper, new Object[0]);
        }
        animManager.setTarget(this);
    }

    public IAnimTarget() {
        AnimManager animManager = new AnimManager();
        this.animManager = animManager;
        this.notifyManager = new NotifyManager(this);
        this.mDefaultMinVisible = Float.MAX_VALUE;
        this.mMinVisibleChanges = new ConcurrentHashMap();
        this.mShouldCheckValue = true;
        this.id = sTargetIds.decrementAndGet();
        this.mTracker = new TargetVelocityTracker();
        this.handler = createHandler(Looper.myLooper());
        if (LogUtils.isLogMoreEnable()) {
            LogUtils.debug("IAnimTarget create ! ", new Object[0]);
        }
        animManager.setTarget(this);
    }

    protected TargetHandler createHandler(Looper looper) {
        if (looper == null) {
            Log.w(CommonUtils.TAG, "warning!! the AnimTarget has created in a thread without Looper, the animation will do not work!!you should use HandlerThread instead of Thread, trace:" + Log.getStackTraceString(new Throwable()));
            return null;
        }
        if (Folme.getUiLooperByTid(looper.getThread().getId()) == null) {
            if (LogUtils.isLogDetailEnable()) {
                LogUtils.debug("IAnimTarget.createHandler registerUiLooper " + looper + " tid " + looper.getThread().getId(), new Object[0]);
            }
            Folme.registerUiLooper(looper);
        }
        return new TargetHandler(looper, this);
    }

    public ListenerNotifier getNotifier() {
        return this.notifyManager.getNotifier();
    }

    public void setToNotify(AnimState animState, AnimConfigLink animConfigLink) {
        this.notifyManager.setToNotify(animState, animConfigLink);
    }

    public boolean isAnimRunning(FloatProperty... floatPropertyArr) {
        return this.animManager.isAnimRunning(floatPropertyArr);
    }

    public int getId() {
        return this.id;
    }

    public void setFlags(long j) {
        this.mFlags = j;
        this.mFlagsSetTime = SystemClock.elapsedRealtime();
    }

    public boolean isValidFlag() {
        return SystemClock.elapsedRealtime() - this.mFlagsSetTime > 3;
    }

    public boolean hasFlags(long j) {
        return CommonUtils.hasFlags(this.mFlags, j);
    }

    public boolean isSleep() {
        return this.mIsSleep;
    }

    public boolean canClear() {
        return !isValid() || (!this.animManager.hasAnimSetup() && hasFlags(1L) && !isAnimRunning(new FloatProperty[0]) && isValidFlag());
    }

    public boolean canClearInvalid() {
        return !isValid() && isIdle();
    }

    public boolean isIdle() {
        return isIdle(false);
    }

    public boolean isIdle(boolean z) {
        if (z) {
            return (this.animManager.hasAnimSetup() || isAnimRunning(new FloatProperty[0])) ? false : true;
        }
        return (this.animManager.hasAnimSetup() || isAnimRunning(new FloatProperty[0]) || !isValidFlag()) ? false : true;
    }

    void awake() {
        this.mIsSleep = false;
    }

    void sleep() {
        this.mIsSleep = true;
    }

    public float getMinVisibleChange(FloatProperty floatProperty) {
        if (!this.mMinVisibleChanges.containsKey(floatProperty)) {
            return getMinVisibleChange(floatProperty.getName());
        }
        return getMinVisibleChange((Object) floatProperty);
    }

    public float getMinVisibleChange(Object obj) {
        Float f = this.mMinVisibleChanges.get(obj);
        if (f != null) {
            return f.floatValue();
        }
        float f2 = this.mDefaultMinVisible;
        return f2 != Float.MAX_VALUE ? f2 : getDefaultMinVisible();
    }

    public IAnimTarget setDefaultMinVisibleChange(float f) {
        this.mDefaultMinVisible = f;
        return this;
    }

    @Deprecated
    public IAnimTarget setMinVisibleChange(float f, FloatProperty... floatPropertyArr) {
        for (FloatProperty floatProperty : floatPropertyArr) {
            floatProperty.setMinVisibleChange(f);
        }
        return this;
    }

    @Deprecated
    public IAnimTarget setMinVisibleChange(Object obj, float f) {
        if (obj instanceof FloatProperty) {
            ((FloatProperty) obj).setMinVisibleChange(f);
        } else {
            this.mMinVisibleChanges.put(obj, Float.valueOf(f));
        }
        return this;
    }

    @Deprecated
    public IAnimTarget setMinVisibleChange(float f, String... strArr) {
        for (String str : strArr) {
            setMinVisibleChange(str, f);
        }
        return this;
    }

    public final UpdateInfo getUpdateInfo(FloatProperty floatProperty) {
        return this.animManager.getUpdateInfo(floatProperty);
    }

    public void executeOnInitialized(Runnable runnable) {
        post(runnable);
    }

    public void getLocationOnScreen(int[] iArr) {
        iArr[1] = 0;
        iArr[0] = 0;
    }

    public float getValue(FloatProperty floatProperty) {
        T targetObject = getTargetObject();
        if (targetObject != null) {
            return floatProperty.getValue(targetObject);
        }
        return Float.MAX_VALUE;
    }

    public final void setValue(final FloatProperty floatProperty, final float f) {
        TargetHandler handler = getHandler();
        if (handler == null || handler.isInTargetThread()) {
            doSetValue(floatProperty, f);
        } else {
            handler.post(new Runnable() { // from class: miuix.animation.IAnimTarget.1
                @Override // java.lang.Runnable
                public void run() {
                    IAnimTarget.this.doSetValue(floatProperty, f);
                }
            });
        }
    }

    public void doSetValue(FloatProperty floatProperty, float f) {
        T targetObject = getTargetObject();
        if (targetObject == null || Math.abs(f) == Float.MAX_VALUE || Float.isNaN(f) || Float.isInfinite(f)) {
            return;
        }
        floatProperty.setValue(targetObject, f);
    }

    public int getIntValue(IIntValueProperty iIntValueProperty) {
        T targetObject = getTargetObject();
        if (targetObject != null) {
            return iIntValueProperty.getIntValue(targetObject);
        }
        return Integer.MAX_VALUE;
    }

    public final void setIntValue(final IIntValueProperty iIntValueProperty, final int i) {
        TargetHandler handler = getHandler();
        if (handler == null || handler.isInTargetThread()) {
            doSetIntValue(iIntValueProperty, i);
        } else {
            handler.post(new Runnable() { // from class: miuix.animation.IAnimTarget.2
                @Override // java.lang.Runnable
                public void run() {
                    IAnimTarget.this.doSetIntValue(iIntValueProperty, i);
                }
            });
        }
    }

    public void doSetIntValue(IIntValueProperty iIntValueProperty, int i) {
        T targetObject = getTargetObject();
        if (targetObject == null || Math.abs(i) == Integer.MAX_VALUE) {
            return;
        }
        iIntValueProperty.setIntValue(targetObject, i);
    }

    public double getVelocity(FloatProperty floatProperty) {
        if (floatProperty == null) {
            return 0.0d;
        }
        return this.animManager.getVelocity(floatProperty);
    }

    public double getThresholdVelocity(FloatProperty floatProperty) {
        return floatProperty.getMinVisibleChange() * 0.75f * 8.333333f;
    }

    public void setVelocity(FloatProperty floatProperty, double d) {
        if (d != 3.4028234663852886E38d) {
            this.animManager.setVelocity(floatProperty, (float) d);
        }
    }

    public TargetHandler getHandler() {
        if (this.handler == null) {
            this.handler = createHandler(Looper.myLooper());
        }
        return this.handler;
    }

    public void post(Runnable runnable) {
        TargetHandler handler = getHandler();
        if (handler == null || handler.isInTargetThread()) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }

    public void postDelayed(Runnable runnable, long j) {
        TargetHandler handler = getHandler();
        if (handler != null) {
            handler.postDelayed(runnable, j);
        }
    }

    public void removeTask(Runnable runnable) {
        TargetHandler handler = getHandler();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public void cancelRunningAnim() {
        this.animManager.cancel();
    }

    public boolean shouldUseIntValue(FloatProperty floatProperty) {
        return floatProperty instanceof IIntValueProperty;
    }

    public void trackVelocity(FloatProperty floatProperty, double d) {
        this.mTracker.trackVelocity(this, floatProperty, d);
    }

    public String toString() {
        if (getTargetObject() == this) {
            return "Value{" + (isValid() ? "valid " : "invalid ") + "@" + hashCode() + " self}";
        }
        return "Value{" + (isValid() ? "valid " : "invalid ") + getTargetObject() + "}";
    }

    protected void finalize() throws Throwable {
        if (LogUtils.isLogMoreEnable()) {
            LogUtils.debug("IAnimTarget was destroyed！" + this, new Object[0]);
        }
        super.finalize();
    }

    public void enableCheckValue(boolean z) {
        this.mShouldCheckValue = z;
    }

    public boolean shouldCheckValue() {
        return this.mShouldCheckValue;
    }
}
