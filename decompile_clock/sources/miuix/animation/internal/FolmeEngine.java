package miuix.animation.internal;

import android.util.Log;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import miuix.animation.Folme;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.EngineListener;
import miuix.animation.physics.AnimationHandler;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LogUtils;
import miuix.animation.utils.ObjectPool;

/* JADX INFO: loaded from: classes2.dex */
public abstract class FolmeEngine {
    public static final long MAX_DELTA = 16666666;
    protected static final int MAX_RECORD = 5;
    protected volatile boolean mIsRunning;
    protected long mLastFrameTimeNanos;
    private Set<EngineListener> mPendingAddEngineListener;
    private Set<EngineListener> mPendingRemoveEngineListener;
    protected float mRatio;
    protected volatile long mAverageDeltaNanos = MAX_DELTA;
    protected final long[] mDeltaRecord = {0, 0, 0, 0, 0};
    protected int mRecordCount = 0;
    private final Set<EngineListener> mEngineListener = new HashSet();
    protected final AnimScheduler mScheduler = new AnimScheduler(this);
    protected final ObjectPool mObjPool = new ObjectPool(this);

    protected abstract void scheduleNextFrame(long j);

    protected abstract void stopNextFrame();

    protected FolmeEngine() {
    }

    public Set<IAnimTarget> getOneShotTargets() {
        return this.mScheduler.getOneShotTargets();
    }

    public void addToOneShot(IAnimTarget iAnimTarget) {
        this.mScheduler.addToOneShot(iAnimTarget);
    }

    public void removeFromOneShot(IAnimTarget iAnimTarget) {
        this.mScheduler.removeFromOneShot(iAnimTarget);
    }

    public ObjectPool getObjPool() {
        return this.mObjPool;
    }

    public void start() {
        startAnim();
    }

    public void waitForAllTaskFinish() {
        waitAnim();
    }

    public void end() {
        endAnim();
    }

    public boolean doAnimationFrame(long j) {
        long jUpdateRunningTime = updateRunningTime(j);
        if (this.mIsRunning) {
            doAnimOnFrame(j, jUpdateRunningTime);
        } else {
            this.mLastFrameTimeNanos = 0L;
        }
        return this.mIsRunning;
    }

    protected void startAnim() {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("+ FolmeEngine.startAnim", new Object[0]);
        }
        if (this.mIsRunning) {
            if (zIsLogMainEnabled) {
                LogUtils.debug("+ FolmeEngine.startAnim but isRunning, return", new Object[0]);
            }
        } else {
            this.mRatio = Folme.getTimeRatio();
            this.mIsRunning = true;
            Iterator<EngineListener> it = this.mEngineListener.iterator();
            while (it.hasNext()) {
                it.next().onBegin();
            }
            scheduleNextFrame(0L);
        }
    }

    protected void waitAnim() {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("- FolmeEngine.waitAnim start", new Object[0]);
        }
        this.mIsRunning = false;
        stopNextFrame();
        if (zIsLogMainEnabled) {
            LogUtils.debug("- FolmeEngine.waitAnim finish", new Object[0]);
        }
    }

    protected void endAnim() {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("- FolmeEngine.endAnim start", new Object[0]);
        }
        this.mLastFrameTimeNanos = 0L;
        if (!this.mIsRunning) {
            if (zIsLogMainEnabled) {
                LogUtils.debug("- FolmeEngine.endAnim return when runner is not running", new Object[0]);
            }
            stopNextFrame();
            Iterator<EngineListener> it = this.mEngineListener.iterator();
            while (it.hasNext()) {
                it.next().onComplete();
            }
            Set<EngineListener> set = this.mPendingRemoveEngineListener;
            if (set != null) {
                Iterator<EngineListener> it2 = set.iterator();
                while (it2.hasNext()) {
                    this.mEngineListener.remove(it2.next());
                }
                this.mPendingRemoveEngineListener.clear();
                this.mPendingRemoveEngineListener = null;
                return;
            }
            return;
        }
        this.mIsRunning = false;
        stopNextFrame();
        if (zIsLogMainEnabled) {
            LogUtils.debug("- FolmeEngine.endAnim finish", new Object[0]);
        }
    }

    @Deprecated
    public void pendingSetTo(IAnimTarget iAnimTarget, AnimState animState) {
        this.mScheduler.executePendingSetTo(iAnimTarget, animState);
    }

    public void toAnim(TransitionInfo transitionInfo) {
        this.mScheduler.executeTo(transitionInfo);
    }

    public void doAnimOnFrame(long j, long j2) {
        Iterator<EngineListener> it = this.mEngineListener.iterator();
        while (it.hasNext()) {
            it.next().onDoFrame(j, j2);
        }
        this.mScheduler.executeDoAnimOnFrame(j, j2);
        Iterator<EngineListener> it2 = this.mEngineListener.iterator();
        while (it2.hasNext()) {
            it2.next().onPostDoFrame(j, j2);
        }
        if (this.mIsRunning) {
            return;
        }
        Iterator<EngineListener> it3 = this.mEngineListener.iterator();
        while (it3.hasNext()) {
            it3.next().onComplete();
        }
        Set<EngineListener> set = this.mPendingRemoveEngineListener;
        if (set != null) {
            Iterator<EngineListener> it4 = set.iterator();
            while (it4.hasNext()) {
                this.mEngineListener.remove(it4.next());
            }
            this.mPendingRemoveEngineListener.clear();
            this.mPendingRemoveEngineListener = null;
        }
    }

    public void cancel(IAnimTarget iAnimTarget, AnimState animState) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("+ doCancel with toState", new Object[0]);
        }
        iAnimTarget.animManager.notifyRemoveWait();
        if (iAnimTarget.animManager.getRunningTransInfoByToState(animState) != null) {
            this.mScheduler.setOperation(new AnimOperationInfo(iAnimTarget, (byte) 4, null, null));
            return;
        }
        TransitionInfo prepareTransInfoByToState = iAnimTarget.animManager.getPrepareTransInfoByToState(animState);
        if (prepareTransInfoByToState != null) {
            iAnimTarget.animManager.cancelPrepareTransition(prepareTransInfoByToState);
        }
    }

    public void cancel(IAnimTarget iAnimTarget, String... strArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("+ doCancel with propertyNames", new Object[0]);
        }
        iAnimTarget.animManager.notifyRemoveWait();
        this.mScheduler.setOperation(new AnimOperationInfo(iAnimTarget, (byte) 4, strArr, null));
    }

    public void cancel(IAnimTarget iAnimTarget, FloatProperty... floatPropertyArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("+ doCancel with properties", new Object[0]);
        }
        iAnimTarget.animManager.notifyRemoveWait();
        this.mScheduler.setOperation(new AnimOperationInfo(iAnimTarget, (byte) 4, null, floatPropertyArr));
    }

    public void end(IAnimTarget iAnimTarget, AnimState animState) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("+ doEnd with toState", new Object[0]);
        }
        iAnimTarget.animManager.notifyRemoveWait();
        if (iAnimTarget.animManager.getRunningTransInfoByToState(animState) != null) {
            this.mScheduler.setOperation(new AnimOperationInfo(iAnimTarget, (byte) 3, null, null));
            return;
        }
        TransitionInfo prepareTransInfoByToState = iAnimTarget.animManager.getPrepareTransInfoByToState(animState);
        if (prepareTransInfoByToState != null) {
            iAnimTarget.animManager.endPrepareTransition(prepareTransInfoByToState);
        }
    }

    public void end(IAnimTarget iAnimTarget, String... strArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("+ doEnd with propertyNames", new Object[0]);
        }
        iAnimTarget.animManager.notifyRemoveWait();
        this.mScheduler.setOperation(new AnimOperationInfo(iAnimTarget, (byte) 3, strArr, null));
    }

    public void end(IAnimTarget iAnimTarget, FloatProperty... floatPropertyArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("+ doEnd with properties", new Object[0]);
        }
        iAnimTarget.animManager.notifyRemoveWait();
        this.mScheduler.setOperation(new AnimOperationInfo(iAnimTarget, (byte) 3, null, floatPropertyArr));
    }

    public void fromTo(IAnimTarget iAnimTarget, AnimState animState, AnimState animState2, AnimConfigLink animConfigLink) {
        TransitionInfo transitionInfo = new TransitionInfo(iAnimTarget, animState, animState2, animConfigLink);
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeEngine fromTo create TransitionInfo " + transitionInfo, new Object[0]);
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("start anim=%s", transitionInfo.getDesignInfo()));
            AnimDebugger.StateDebugInfo debugConfig = AnimDebugger.parseDebugConfig();
            if (debugConfig != null) {
                Log.i(CommonUtils.D_TAG, String.format("debug animstate=%s", debugConfig.state.getDesignInfo()));
                Log.i(CommonUtils.D_TAG, String.format("debug animconfig=%s", debugConfig.config.getDesignInfo()));
                if (AnimDebugger.updateTransitionInfo(debugConfig, transitionInfo)) {
                    Log.i(CommonUtils.D_TAG, String.format("start updated-anim=%s", transitionInfo.getDesignInfo()));
                }
            }
        }
        if (iAnimTarget.animManager.prepareAnim(transitionInfo)) {
            if (iAnimTarget.hasFlags(1L)) {
                addToOneShot(iAnimTarget);
            }
            toAnim(transitionInfo);
        }
    }

    public synchronized void addEngineListener(final EngineListener engineListener) {
        this.mScheduler.execute(new Runnable() { // from class: miuix.animation.internal.FolmeEngine$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1790lambda$addEngineListener$0$miuixanimationinternalFolmeEngine(engineListener);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$addEngineListener$0$miuix-animation-internal-FolmeEngine, reason: not valid java name */
    /* synthetic */ void m1790lambda$addEngineListener$0$miuixanimationinternalFolmeEngine(EngineListener engineListener) {
        if (this.mIsRunning && !this.mEngineListener.contains(engineListener)) {
            engineListener.onBegin();
        }
        this.mEngineListener.add(engineListener);
    }

    public synchronized void removeEngineListener(final EngineListener engineListener) {
        this.mScheduler.execute(new Runnable() { // from class: miuix.animation.internal.FolmeEngine$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1791x50321abb(engineListener);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$removeEngineListener$1$miuix-animation-internal-FolmeEngine, reason: not valid java name */
    /* synthetic */ void m1791x50321abb(EngineListener engineListener) {
        if (this.mEngineListener.contains(engineListener)) {
            if (this.mIsRunning) {
                if (this.mPendingRemoveEngineListener == null) {
                    this.mPendingRemoveEngineListener = new HashSet();
                }
                this.mPendingRemoveEngineListener.add(engineListener);
                return;
            }
            this.mEngineListener.remove(engineListener);
        }
    }

    public long getAverageDeltaNanos() {
        return this.mAverageDeltaNanos;
    }

    private long calculateAverageDelta(long j) {
        long jAverage = average(this.mDeltaRecord);
        if (jAverage > 0) {
            j = jAverage;
        }
        if (j == 0 || j > MAX_DELTA) {
            j = 16666666;
        }
        return (long) Math.ceil(j / this.mRatio);
    }

    protected long updateRunningTime(long j) {
        long j2;
        long j3 = this.mLastFrameTimeNanos;
        if (j3 == 0) {
            this.mLastFrameTimeNanos = j;
            j2 = 0;
        } else {
            j2 = j - j3;
            this.mLastFrameTimeNanos = j;
        }
        int i = this.mRecordCount;
        this.mDeltaRecord[i % 5] = j2;
        this.mRecordCount = i + 1;
        this.mAverageDeltaNanos = calculateAverageDelta(j2);
        long frameDeltaNanos = AnimationHandler.getInstance().getFrameDeltaNanos();
        if (frameDeltaNanos > 0) {
            this.mAverageDeltaNanos = frameDeltaNanos;
        }
        return j2;
    }

    private long average(long[] jArr) {
        long j = 0;
        int i = 0;
        for (long j2 : jArr) {
            j += j2;
            if (j2 > 0) {
                i++;
            }
        }
        if (i > 0) {
            return j / ((long) i);
        }
        return 0L;
    }
}
