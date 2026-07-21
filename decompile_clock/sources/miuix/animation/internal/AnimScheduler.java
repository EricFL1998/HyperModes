package miuix.animation.internal;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import miuix.animation.Folme;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LinkNode;
import miuix.animation.utils.LogUtils;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;

/* JADX INFO: loaded from: classes2.dex */
class AnimScheduler {

    @Deprecated
    static final int MSG_CLEAN = 5;

    @Deprecated
    static final int MSG_RUN = 3;

    @Deprecated
    static final int MSG_SET_TO = 4;

    @Deprecated
    static final int MSG_TO = 1;

    @Deprecated
    static final int MSG_UPDATE = 2;
    protected final FolmeEngine mEngine;
    private volatile boolean mEngineStart;
    protected boolean mHasTaskStackRunning;
    private volatile boolean mStart;
    protected HashMap<Integer, Boolean> mAnimTaskSchedMap = null;
    private final Set<IAnimTarget> mOneShotTargets = new HashSet();
    protected final Set<IAnimTarget> mRunningTarget = new HashSet();
    protected final Map<IAnimTarget, AnimOperationInfo> mOpMap = new ConcurrentHashMap();
    protected final Map<IAnimTarget, TransitionInfo> mPrepareTransMap = new HashMap();
    private final List<TransitionInfo> mTempSetupInfoList = new ArrayList();
    private final List<AnimTask> mTaskStackList = new ArrayList();
    private final List<IAnimTarget> mPreDelTargetList = new ArrayList();
    private final List<TransitionInfo> mTransListForRun = new ArrayList();
    private final List<AnimTask> mAnimTaskForRun = new ArrayList();
    private final List<TransitionInfo> mTempTargetRunningInfo = new ArrayList();
    public Handler handler = null;
    public final AtomicInteger runningStackCount = new AtomicInteger();
    private int mRunningAnimCount = 0;
    private long mTotalTNanos = 0;
    private int mFrameCount = 0;
    private final int[] mTaskStackSplitInfo = new int[2];
    public final long runnerThreadId = Thread.currentThread().getId();

    public void destroy() {
    }

    AnimScheduler(FolmeEngine folmeEngine) {
        this.mEngine = folmeEngine;
    }

    public Set<IAnimTarget> getOneShotTargets() {
        return this.mOneShotTargets;
    }

    public void addToOneShot(IAnimTarget iAnimTarget) {
        this.mOneShotTargets.add(iAnimTarget);
    }

    public void removeFromOneShot(IAnimTarget iAnimTarget) {
        this.mOneShotTargets.remove(iAnimTarget);
    }

    public void setOperation(AnimOperationInfo animOperationInfo) {
        if (animOperationInfo.target.isAnimRunning(new FloatProperty[0])) {
            animOperationInfo.sendTime = System.nanoTime();
            if (!this.mHasTaskStackRunning) {
                if (LogUtils.isLogMainEnabled()) {
                    Log.i(CommonUtils.TAG, "++ setOperation: mHasTaskStackRunning is false, execute setOperation immediately");
                }
                doOperationForTarget(animOperationInfo);
            } else {
                this.mOpMap.put(animOperationInfo.target, animOperationInfo);
                if (LogUtils.isLogMainEnabled()) {
                    Log.i(CommonUtils.TAG, "++ setOperation: mHasTaskStackRunning is true, pending setOperation");
                }
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0068  */
    private void doOperationForTarget(AnimOperationInfo animOperationInfo) {
        int i;
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        ArrayList<TransitionInfo> arrayList = new ArrayList();
        AnimOperationInfo animOperationInfo2 = animOperationInfo;
        IAnimTarget iAnimTarget = animOperationInfo2.target;
        iAnimTarget.animManager.addToTransitionInfoList(arrayList);
        for (TransitionInfo transitionInfo : arrayList) {
            if (zIsLogMoreEnable) {
                LogUtils.debug("-- doOperationForTarget -> update transInfo " + transitionInfo, new Object[0]);
            }
            if (animOperationInfo2 != null && transitionInfo.startTime > animOperationInfo2.sendTime) {
                animOperationInfo2 = null;
            }
            AnimStats infoAnimStats = transitionInfo.getInfoAnimStats();
            if (infoAnimStats.isStarted()) {
                doOperationForUpdateInfoList(transitionInfo, animOperationInfo2, infoAnimStats, transitionInfo.updateList);
            }
            if (!infoAnimStats.isRunning()) {
                if (animOperationInfo2 != null) {
                    i = animOperationInfo2.op != 4 ? 3 : 4;
                }
                if (infoAnimStats.focusCount > 0 && infoAnimStats.focusCount == infoAnimStats.focusEndCount) {
                    i = 3;
                }
                if (zIsLogMoreEnable) {
                    LogUtils.debug("--- notifyTransitionEndOrCancel from doOperationForTarget msg=" + i + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + transitionInfo, new Object[0]);
                }
                if (i == 3) {
                    if (zIsLogMoreEnable) {
                        LogUtils.debug("--- notifyTransitionUpdate before notifyTransitionEnd from doOperationForTarget", new Object[0]);
                    }
                    iAnimTarget.animManager.notifyTransitionUpdate(transitionInfo, transitionInfo.updateListForNotify);
                }
                iAnimTarget.animManager.notifyTransitionEndOrCancel(transitionInfo, 2, i);
            }
            if (zIsLogMoreEnable) {
                LogUtils.debug("-- doOperationForTarget , id=" + transitionInfo.id, "key=" + transitionInfo.key, "targetOpInfo=" + animOperationInfo2, "info.startTime=" + transitionInfo.startTime, "targetOpInfo.time=" + (animOperationInfo2 != null ? Long.valueOf(animOperationInfo2.sendTime) : null), "statsFromInfo.isRunning=" + infoAnimStats.isRunning(), "statsFromInfo=" + infoAnimStats, "target=" + iAnimTarget);
            }
        }
    }

    private void doOperationForUpdateInfoList(TransitionInfo transitionInfo, AnimOperationInfo animOperationInfo, AnimStats animStats, List<UpdateInfo> list) {
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        boolean z = transitionInfo.state == 2;
        for (AnimTask animTask : transitionInfo.animTasks) {
            int i = animTask.startPos;
            int animCount = animTask.getAnimCount() + i;
            while (i < animCount) {
                UpdateInfo updateInfo = list.get(i);
                if (updateInfo != null) {
                    boolean zHandleSetToPropertyOnUpdate = handleSetToPropertyOnUpdate(updateInfo, animTask.animStats, animStats);
                    if (zIsLogMoreEnable) {
                        LogUtils.debug(" |---- step0", "doSetTo " + zHandleSetToPropertyOnUpdate, updateInfo);
                    }
                    if (animOperationInfo != null && z && !zHandleSetToPropertyOnUpdate) {
                        changeRunningPropertyOp(updateInfo, animOperationInfo, animTask.animStats, animStats, transitionInfo.target, transitionInfo.config);
                        if (zIsLogMoreEnable) {
                            LogUtils.debug(" |---- step2 changeRunningPropertyOp finish taskInfo " + animTask.info, new Object[0]);
                        }
                    }
                }
                i++;
            }
        }
    }

    protected final boolean isInMainThread(long j) {
        return this.runnerThreadId == j;
    }

    final void execute(Runnable runnable) {
        if (isInMainThread(Thread.currentThread().getId())) {
            runnable.run();
            return;
        }
        Handler handler = this.handler;
        if (handler != null) {
            handler.post(runnable);
        } else {
            Log.w(CommonUtils.TAG, "execute warning!! this scheduler has no handler" + LogUtils.getStackTrace(8));
            runnable.run();
        }
    }

    @Deprecated
    void executePendingSetTo(IAnimTarget iAnimTarget, AnimState animState) {
        final SetToInfo setToInfo = new SetToInfo();
        setToInfo.target = iAnimTarget;
        if (animState.needDuplicate) {
            setToInfo.state = new AnimState();
            setToInfo.state.set(animState);
        } else {
            setToInfo.state = animState;
        }
        Handler handler = this.handler;
        if (handler == null) {
            Log.w(CommonUtils.TAG, "executeSetTo warning!! this scheduler has no handler, so direct run executePendingSetTo(info)" + LogUtils.getStackTrace(8));
        }
        if (isInMainThread(Thread.currentThread().getId()) || handler == null) {
            pendingSetTo(setToInfo);
        } else {
            handler.post(new Runnable() { // from class: miuix.animation.internal.AnimScheduler.1
                @Override // java.lang.Runnable
                public void run() {
                    AnimScheduler.this.pendingSetTo(setToInfo);
                }
            });
        }
    }

    void executeTo(final TransitionInfo transitionInfo) {
        if (LogUtils.isLogDetailEnable()) {
            LogUtils.debug("++ executeTo", new Object[0]);
        }
        if (transitionInfo.config.delay > 0) {
            if (this.handler != null) {
                if (LogUtils.isLogMainEnabled()) {
                    LogUtils.debug("-- to with delay Scheduler@" + hashCode() + " " + transitionInfo, new Object[0]);
                }
                this.handler.postDelayed(new Runnable() { // from class: miuix.animation.internal.AnimScheduler$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1788lambda$executeTo$0$miuixanimationinternalAnimScheduler(transitionInfo);
                    }
                }, transitionInfo.config.delay);
                return;
            }
            return;
        }
        if (isInMainThread(Thread.currentThread().getId())) {
            m1789lambda$executeTo$1$miuixanimationinternalAnimScheduler(transitionInfo);
            return;
        }
        Handler handler = this.handler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: miuix.animation.internal.AnimScheduler$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1789lambda$executeTo$1$miuixanimationinternalAnimScheduler(transitionInfo);
                }
            });
        } else {
            Log.w(CommonUtils.TAG, "executeTo warning!! this scheduler has no handler, so direct run to(info)" + LogUtils.getStackTrace(8));
            m1789lambda$executeTo$1$miuixanimationinternalAnimScheduler(transitionInfo);
        }
    }

    void executeDoAnimOnFrame(final long j, final long j2) {
        if (isInMainThread(Thread.currentThread().getId())) {
            m1786x52eb5ba4(j, j2);
            return;
        }
        Handler handler = this.handler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: miuix.animation.internal.AnimScheduler$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1786x52eb5ba4(j, j2);
                }
            });
        } else {
            Log.w(CommonUtils.TAG, "executeOnFrame warning!! this scheduler has no handler" + LogUtils.getStackTrace(8));
            m1786x52eb5ba4(j, j2);
        }
    }

    void executeNotifyTransitionBegin(final TransitionInfo transitionInfo) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("----- TaskStackRunner before update : notifyTransitionBegin ", new Object[0]);
        }
        if (isInMainThread(Thread.currentThread().getId())) {
            m1787xf6f744ca(transitionInfo);
            return;
        }
        Handler handler = this.handler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: miuix.animation.internal.AnimScheduler$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1787xf6f744ca(transitionInfo);
                }
            });
        } else {
            Log.w(CommonUtils.TAG, "executeNotifyTransitionBegin warning!! this scheduler has no handler" + LogUtils.getStackTrace(8));
            m1787xf6f744ca(transitionInfo);
        }
    }

    void executeUpdate() {
        if (LogUtils.isLogDetailEnable()) {
            LogUtils.debug("-- executeUpdate", new Object[0]);
        }
        if (isInMainThread(Thread.currentThread().getId())) {
            update();
            return;
        }
        Handler handler = this.handler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: miuix.animation.internal.AnimScheduler$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.update();
                }
            });
        } else {
            Log.w(CommonUtils.TAG, "executeUpdate warning!! this scheduler has no handler" + LogUtils.getStackTrace(8));
            update();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Deprecated
    protected final void pendingSetTo(SetToInfo setToInfo) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        IAnimTarget iAnimTarget = setToInfo.target;
        if (zIsLogMainEnabled) {
            LogUtils.debug("-- setTo Scheduler@" + hashCode() + " " + setToInfo + " " + iAnimTarget, new Object[0]);
        }
        AnimState animState = setToInfo.state;
        setToInfo.target.animManager.setTo(animState, null);
        Iterator<Object> it = animState.keySet().iterator();
        while (it.hasNext()) {
            FloatProperty property = animState.getProperty(iAnimTarget, it.next());
            UpdateInfo updateInfo = iAnimTarget.animManager.mUpdateMap.get(property);
            if (updateInfo != null) {
                double d = animState.get(iAnimTarget, property);
                if (zIsLogMainEnabled) {
                    LogUtils.debug("-- setTo setToValue=" + d + " " + property + " toState " + animState, new Object[0]);
                }
                updateInfo.animInfo.startValue = d;
                updateInfo.animInfo.setToValue = d;
                if (updateInfo.useInt && (property instanceof IIntValueProperty)) {
                    iAnimTarget.doSetIntValue((IIntValueProperty) property, updateInfo.getIntValue());
                } else {
                    if (updateInfo.useInt) {
                        LogUtils.debug("-- setTo Warning!! the property is " + property, new Object[0]);
                    }
                    iAnimTarget.doSetValue(property, updateInfo.getFloatValue());
                }
            }
        }
        if (zIsLogMainEnabled) {
            LogUtils.debug("-- setTo done " + iAnimTarget, new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: to, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public final void m1789lambda$executeTo$1$miuixanimationinternalAnimScheduler(TransitionInfo transitionInfo) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("-- to Scheduler@" + hashCode() + " " + transitionInfo, new Object[0]);
        }
        if (transitionInfo != null) {
            addToMap(transitionInfo.target, transitionInfo, this.mPrepareTransMap);
            if (this.mHasTaskStackRunning) {
                return;
            }
            if (zIsLogMainEnabled) {
                LogUtils.debug("-- to->startEngine", new Object[0]);
            }
            startEngine();
        }
    }

    private void setup() {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("--- setup prepareTrans.size=" + this.mPrepareTransMap.size() + " runningTarget.size=" + this.mRunningTarget.size() + " Scheduler@" + hashCode(), new Object[0]);
        }
        for (TransitionInfo transitionInfo : this.mPrepareTransMap.values()) {
            if (LogUtils.isLogMainEnabled()) {
                LogUtils.debug("---- setupAllInfoToTarget-> " + transitionInfo, new Object[0]);
            }
            this.mRunningTarget.add(transitionInfo.target);
            this.mTempSetupInfoList.add(transitionInfo);
        }
        this.mPrepareTransMap.clear();
        for (int i = 0; i < this.mTempSetupInfoList.size(); i++) {
            TransitionInfo.setupAllInfoToTarget(this.mTempSetupInfoList.get(i));
        }
        this.mTempSetupInfoList.clear();
    }

    private void startEngine() {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("-- startEngine mEngineStart=" + this.mEngineStart + " Scheduler@" + hashCode(), new Object[0]);
        }
        if (this.mEngineStart) {
            return;
        }
        this.mEngineStart = true;
        this.mEngine.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: doAnimationFrame, reason: merged with bridge method [inline-methods] */
    public final void m1786x52eb5ba4(long j, long j2) {
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug(String.format("++ doAnimationFrame: deltaTNanos=%d Scheduler@%s", Long.valueOf(j2), Integer.valueOf(hashCode())), new Object[0]);
        }
        setup();
        if (zIsLogMoreEnable) {
            LogUtils.debug(String.format("++ doAnimationFrame: |-> after setup: mRunningTarget.size=%s", Integer.valueOf(this.mRunningTarget.size())), new Object[0]);
        }
        if (!this.mRunningTarget.isEmpty()) {
            long averageDeltaNanos = AndroidEngine.getInst().getAverageDeltaNanos();
            if (zIsLogMoreEnable) {
                LogUtils.debug(String.format("++ doAnimationFrame: |--> hasRunningTarget mStart=%s mHasTaskStackRunning=%s ", Boolean.valueOf(this.mStart), Boolean.valueOf(this.mHasTaskStackRunning)), new Object[0]);
            }
            if (!this.mStart) {
                this.mStart = true;
                this.mTotalTNanos = 0L;
                this.mFrameCount = 0;
            }
            runAnimTaskOnFrame(j, j2, averageDeltaNanos);
        }
        releaseIdleOneShotTargetAfterRun();
    }

    protected void runAnimTaskOnFrame(long j, long j2, long j3) {
        HashSet hashSet = new HashSet(this.mRunningTarget);
        this.mTotalTNanos += j2;
        if (j2 > 0) {
            this.mFrameCount++;
        }
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("+++ runAnimTaskOnFrame start frameCount=" + this.mFrameCount + " nowNanos=" + j + " deltaTNanos=" + j2 + " averageDelta=" + j3 + " Scheduler@" + hashCode(), new Object[0]);
        }
        this.mRunningAnimCount = 0;
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            ((IAnimTarget) it.next()).animManager.addToTransitionInfoList(this.mTransListForRun);
        }
        for (TransitionInfo transitionInfo : this.mTransListForRun) {
            this.mRunningAnimCount += transitionInfo.getAnimCount();
            TransitionInfo.tickOnFrame(transitionInfo, j3);
            this.mAnimTaskForRun.addAll(transitionInfo.animTasks);
        }
        boolean zIsEmpty = this.mTransListForRun.isEmpty();
        this.mTransListForRun.clear();
        ThreadPoolUtil.getSplitCount(Math.max(0, this.mRunningAnimCount - 4000), this.mTaskStackSplitInfo);
        int[] iArr = this.mTaskStackSplitInfo;
        assignAnimTaskToStack(this.mAnimTaskForRun, iArr[1], iArr[0]);
        this.mAnimTaskForRun.clear();
        this.mHasTaskStackRunning = !this.mTaskStackList.isEmpty();
        this.runningStackCount.getAndAdd(this.mTaskStackList.size());
        if (zIsLogMoreEnable) {
            LogUtils.debug("+++ runAnimTaskOnFrame mTaskStackList.size " + this.mTaskStackList.size(), new Object[0]);
        }
        double d = j3 / 1.0E9d;
        if (this.mHasTaskStackRunning) {
            AnimTask animTask = this.mTaskStackList.get(0);
            if (this.mTaskStackList.size() > 1) {
                for (int i = 1; i < this.mTaskStackList.size(); i++) {
                    AnimTask.asyncStart(this.mTaskStackList.get(i), this, this.mTotalTNanos, j2, 1, d);
                }
            }
            AnimTask.start(animTask, this, this.mTotalTNanos, j2, 1, d);
            this.mTaskStackList.clear();
        }
        if (zIsLogMoreEnable) {
            LogUtils.debug(String.format("--- runAnimTaskOnFrame finish isAllTransFinish:%s mHasTaskStackRunning:%s", Boolean.valueOf(zIsEmpty), Boolean.valueOf(this.mHasTaskStackRunning)), new Object[0]);
        }
        if (this.mHasTaskStackRunning) {
            this.mEngineStart = false;
            this.mEngine.waitForAllTaskFinish();
        } else if (zIsEmpty) {
            if (zIsLogMoreEnable) {
                LogUtils.debug("--- runAnimTaskOnFrame->endEngine: no transList then endEngine", new Object[0]);
            }
            endEngine();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: notifyTransitionBegin, reason: merged with bridge method [inline-methods] */
    public final void m1787xf6f744ca(TransitionInfo transitionInfo) {
        transitionInfo.target.animManager.notifyTransitionBegin(transitionInfo, transitionInfo.updateList, false);
    }

    protected final void update() {
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        if (zIsLogMoreEnable) {
            LogUtils.debug("-- update from runningStackCount == 0 frameCount=" + this.mFrameCount + " Scheduler@" + hashCode(), new Object[0]);
        }
        this.mRunningAnimCount = 0;
        boolean z = false;
        for (IAnimTarget iAnimTarget : new HashSet(this.mRunningTarget)) {
            if (updateTarget(iAnimTarget, this.mTempTargetRunningInfo) || prepareWaitTransAfterUpdated(iAnimTarget)) {
                z = true;
            } else {
                this.mPreDelTargetList.add(iAnimTarget);
            }
            if (LogUtils.isLogMainEnabled()) {
                this.mRunningAnimCount += iAnimTarget.animManager.getTotalAnimCount();
            }
        }
        this.mHasTaskStackRunning = false;
        if (!this.mPreDelTargetList.isEmpty()) {
            this.mRunningTarget.removeAll(this.mPreDelTargetList);
            this.mPreDelTargetList.clear();
        }
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("-- update after traversal all target", "mRunningAnimCount=" + this.mRunningAnimCount, "mPrepareTransMap.size=" + this.mPrepareTransMap.size(), "mRunningTarget.size=" + this.mRunningTarget.size());
        }
        boolean z2 = !this.mPrepareTransMap.isEmpty();
        boolean z3 = !this.mRunningTarget.isEmpty();
        if (z2 || z3) {
            if (zIsLogMoreEnable) {
                LogUtils.debug("-- update finish->startEngine hasPrepareTrans:" + z2 + " hasRunningTarget:" + z3, new Object[0]);
            }
            startEngine();
            z = true;
        }
        if (z) {
            return;
        }
        if (zIsLogMoreEnable) {
            LogUtils.debug("-- update->endEngine when isRunning is false", new Object[0]);
        }
        endEngine();
    }

    private void endEngine() {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("-- endEngine Scheduler@" + hashCode(), new Object[0]);
        }
        this.mRunningTarget.clear();
        if (zIsLogMainEnabled) {
            LogUtils.debug("-- endEngine after mRunningTarget.clear()", new Object[0]);
        }
        if (this.mStart) {
            if (zIsLogMainEnabled) {
                LogUtils.debug("-- endEngine", "frames=" + this.mFrameCount, "total_time_ms=" + (this.mTotalTNanos / FolmeCore.NANOS_TO_MS), "Scheduler@" + hashCode());
            }
            this.mStart = false;
            this.mEngineStart = false;
            this.mTotalTNanos = 0L;
            this.mFrameCount = 0;
            this.mEngine.end();
        }
    }

    private void releaseIdleOneShotTargetAfterRun() {
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        if (zIsLogMoreEnable) {
            LogUtils.debug("--- releaseIdleOneShotTargetAfterRun", new Object[0]);
        }
        Set<IAnimTarget> oneShotTargets = this.mEngine.getOneShotTargets();
        if (oneShotTargets.isEmpty()) {
            return;
        }
        ArrayList<IAnimTarget> arrayList = null;
        for (IAnimTarget iAnimTarget : oneShotTargets) {
            if (iAnimTarget.isIdle()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(iAnimTarget);
            }
        }
        if (arrayList != null) {
            for (IAnimTarget iAnimTarget2 : arrayList) {
                if (zIsLogMoreEnable) {
                    LogUtils.debug(" |--- clean idle oneshot target " + iAnimTarget2, new Object[0]);
                }
                if (iAnimTarget2.hasFlags(1L)) {
                    removeFromOneShot(iAnimTarget2);
                }
                Folme.clean(iAnimTarget2);
            }
        }
    }

    private boolean updateTarget(IAnimTarget iAnimTarget, List<TransitionInfo> list) {
        boolean z;
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        iAnimTarget.animManager.addToTransitionInfoList(list);
        int i = 0;
        if (zIsLogMoreEnable) {
            LogUtils.debug("--- update->updateTarget start transList " + list.size() + " " + iAnimTarget, new Object[0]);
        }
        AnimOperationInfo animOperationInfo = this.mOpMap.get(iAnimTarget);
        Iterator<TransitionInfo> it = list.iterator();
        int i2 = 0;
        int i3 = 0;
        while (it.hasNext()) {
            TransitionInfo next = it.next();
            if (zIsLogMoreEnable) {
                LogUtils.debug("--- update->updateTarget-> update transInfo " + next, new Object[i]);
            }
            if (next.state == 0) {
                i3++;
                if (zIsLogMoreEnable) {
                    LogUtils.debug("---- update->updateTarget-> this info isInfoOnPrepare runCount " + i3, new Object[i]);
                }
            } else {
                if (animOperationInfo != null && next.startTime > animOperationInfo.sendTime) {
                    i2++;
                    animOperationInfo = null;
                }
                AnimStats infoAnimStats = next.getInfoAnimStats();
                if (infoAnimStats.isStarted()) {
                    updateTransInfo(next, animOperationInfo, infoAnimStats);
                }
                if (zIsLogMoreEnable) {
                    LogUtils.debug("---- update->updateTarget after updateTransInfo " + infoAnimStats, new Object[i]);
                }
                if (infoAnimStats.isRunning()) {
                    i3++;
                } else {
                    int i4 = 3;
                    int i5 = infoAnimStats.cancelCount > infoAnimStats.endCount ? 4 : 3;
                    if (infoAnimStats.focusCount <= 0 || infoAnimStats.focusCount != infoAnimStats.focusEndCount) {
                        i4 = i5;
                    } else {
                        if (zIsLogMoreEnable) {
                            LogUtils.debug("--- transitionComplete by focus end all info.id=" + next.id, new Object[i]);
                        }
                        for (int i6 = i; i6 < next.updateListForNotify.size(); i6++) {
                            UpdateInfo updateInfo = next.updateListForNotify.get(i6);
                            if (updateInfo != null && !updateInfo.isCompleted) {
                                updateInfo.skipToTargetValue(iAnimTarget);
                            }
                        }
                    }
                    if (zIsLogMoreEnable) {
                        LogUtils.debug("--- notifyTransitionEndOrCancel from updateTarget msg=" + i4 + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + next, new Object[i]);
                        LogUtils.debug("--- notifyTransitionUpdate before notifyTransitionEndOrCancel from updateTarget", new Object[i]);
                    }
                    iAnimTarget.animManager.notifyTransitionUpdate(next, next.updateListForNotify);
                    iAnimTarget.animManager.notifyTransitionEndOrCancel(next, 2, i4);
                }
                if (zIsLogMoreEnable) {
                    LogUtils.debug("--- update->after handleUpdate , id=" + next.id, "key=" + next.key, "runCount=" + i3, "animStartAfterCancel=" + i2, "targetOpInfo=" + animOperationInfo, "info.startTime=" + next.startTime, "targetOpInfo.time=" + (animOperationInfo != null ? Long.valueOf(animOperationInfo.sendTime) : null), "statsFromInfo.isRunning=" + infoAnimStats.isRunning(), "statsFromInfo=" + infoAnimStats, "target=" + iAnimTarget);
                }
                it = it;
                i2 = i2;
                i = 0;
            }
        }
        if (animOperationInfo != null && (i2 == list.size() || animOperationInfo.isUsed())) {
            this.mOpMap.remove(iAnimTarget);
        }
        list.clear();
        if (zIsLogMoreEnable) {
            z = false;
            LogUtils.debug("--- update->updateTarget finish runCount=" + i3, new Object[0]);
        } else {
            z = false;
        }
        if (i3 > 0) {
            return true;
        }
        return z;
    }

    private void updateTransInfo(TransitionInfo transitionInfo, AnimOperationInfo animOperationInfo, AnimStats animStats) {
        if (animStats == null) {
            animStats = transitionInfo.getInfoAnimStats();
        }
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        if (zIsLogMoreEnable) {
            LogUtils.debug("---- updateTransInfo start " + transitionInfo + " opInfo:" + animOperationInfo, new Object[0]);
        }
        boolean z = transitionInfo.state == 2;
        doOperationForUpdateInfoList(transitionInfo, animOperationInfo, animStats, transitionInfo.updateList);
        if (animStats.isRunning() && animStats.updateCount > 0) {
            if (zIsLogMoreEnable) {
                LogUtils.debug("---- updateTransInfo finish " + transitionInfo, new Object[0]);
            }
            if (!z || (!transitionInfo.hasSendNotifyStart && !transitionInfo.hasOnStart)) {
                if (zIsLogMoreEnable) {
                    LogUtils.debug("---- notifyTransitionBegin from updateTransInfo: " + transitionInfo, new Object[0]);
                }
                transitionInfo.target.animManager.notifyTransitionBegin(transitionInfo, transitionInfo.updateList, true);
                return;
            }
            if (zIsLogMoreEnable) {
                LogUtils.debug("---- notifyTransitionUpdate from updateTransInfo:" + transitionInfo, new Object[0]);
            }
            if (transitionInfo.updateList == null || transitionInfo.updateList.isEmpty()) {
                if (zIsLogMoreEnable) {
                    LogUtils.debug("---- notifyTransitionUpdate fail updateList is empty " + transitionInfo, new Object[0]);
                    return;
                }
                return;
            }
            List<UpdateInfo> list = transitionInfo.updateListForNotify;
            if (transitionInfo.target.shouldCheckValue()) {
                List<UpdateInfo> list2 = transitionInfo.updateList;
                list.clear();
                for (UpdateInfo updateInfo : list2) {
                    if (updateInfo.animInfo.op > 1 && updateInfo.animInfo.op < 6 && AnimValueUtils.isValid(updateInfo.animInfo.value)) {
                        list.add(updateInfo);
                    }
                }
                if (!list.isEmpty() && zIsLogMoreEnable) {
                    LogUtils.debug("---- notifyTransitionUpdate withCheckValue info.id=" + transitionInfo.id, "info.key=" + transitionInfo.key, "updateList.size=" + list.size());
                }
            } else {
                list.addAll(transitionInfo.updateList);
            }
            transitionInfo.target.animManager.notifyTransitionUpdate(transitionInfo, list);
            return;
        }
        if (transitionInfo.updateListForNotify.isEmpty()) {
            transitionInfo.updateListForNotify.addAll(transitionInfo.updateList);
        }
    }

    private static boolean handleSetToPropertyOnUpdate(UpdateInfo updateInfo, AnimStats animStats, AnimStats animStats2) {
        if (!AnimValueUtils.handleSetToValue(updateInfo)) {
            return false;
        }
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        if (AnimTask.isRunning(updateInfo.animInfo.op)) {
            if (zIsLogMoreEnable) {
                LogUtils.debug("----- setToPropertyOnUpdate start updateInfo p=" + updateInfo.property, "id=" + updateInfo.hashCode(), "op=" + ((int) updateInfo.animInfo.op), updateInfo);
            }
            animStats.cancelCount++;
            animStats2.cancelCount++;
            updateInfo.setOp((byte) 4);
            TransitionInfo.decreasePrepareCountForDelayAnim(animStats, animStats2, updateInfo, updateInfo.animInfo.op);
            if (zIsLogMoreEnable) {
                LogUtils.debug("----- setToPropertyOnUpdate finish updateInfo p=" + updateInfo.property, "id=" + updateInfo.hashCode(), "op=" + ((int) updateInfo.animInfo.op), "task-stats.cancelCount " + animStats.cancelCount, "info-stats.cancelCount " + animStats2.cancelCount, updateInfo);
            }
        }
        return true;
    }

    private static void changeRunningPropertyOp(UpdateInfo updateInfo, AnimOperationInfo animOperationInfo, AnimStats animStats, AnimStats animStats2, IAnimTarget iAnimTarget, AnimConfig animConfig) {
        byte b = updateInfo.animInfo.op;
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        if (zIsLogMoreEnable) {
            LogUtils.debug(" |---- before step1 changeRunningPropertyOp doOp " + (animOperationInfo.propList == null || animOperationInfo.propList.contains(updateInfo.property)) + " update.property=" + updateInfo.property, new Object[0]);
        }
        if (!AnimTask.isRunning(b) || animOperationInfo.op == 0) {
            return;
        }
        if (animOperationInfo.propList == null || animOperationInfo.propList.contains(updateInfo.property)) {
            if (zIsLogMoreEnable) {
                LogUtils.debug(" |---- step1 changeRunningPropertyOp " + updateInfo.property.getName() + "'s op=" + ((int) b) + " to opInfo.op=" + ((int) animOperationInfo.op), new Object[0]);
            }
            animOperationInfo.usedCount++;
            if (animOperationInfo.op == 3) {
                animStats.endCount++;
                animStats2.endCount++;
                updateInfo.skipToTargetValue(iAnimTarget);
                if (animConfig.isFocusPropertyForComplete(updateInfo.property)) {
                    animStats.focusEndCount++;
                    animStats2.focusEndCount++;
                }
            } else if (animOperationInfo.op == 4) {
                animStats.cancelCount++;
                animStats2.cancelCount++;
                if (animConfig.isFocusPropertyForComplete(updateInfo.property)) {
                    animStats.focusCount--;
                    animStats2.focusCount--;
                }
            }
            updateInfo.setOp(animOperationInfo.op);
            TransitionInfo.decreasePrepareCountForDelayAnim(animStats, animStats2, updateInfo, b);
            if (zIsLogMoreEnable) {
                LogUtils.debug("----- changeRunningPropertyOp finish update.animInfo.op=" + ((int) b), "opInfo=" + animOperationInfo, "task-stats=" + animStats);
            }
        }
    }

    private <T extends LinkNode> void addToMap(IAnimTarget iAnimTarget, T t, Map<IAnimTarget, T> map) {
        T t2 = map.get(iAnimTarget);
        if (t2 == null) {
            map.put(iAnimTarget, t);
        } else {
            t2.addToTail(t);
        }
    }

    private boolean prepareWaitTransAfterUpdated(IAnimTarget iAnimTarget) {
        boolean zIsLogMoreEnable = LogUtils.isLogMoreEnable();
        if (zIsLogMoreEnable) {
            LogUtils.debug("--- update->prepareWaitTransAfterUpdated " + iAnimTarget, new Object[0]);
        }
        TransitionInfo transitionInfoPoll = iAnimTarget.animManager.mWaitState.poll();
        if (transitionInfoPoll == null || !iAnimTarget.animManager.prepareAnim(transitionInfoPoll, true)) {
            if (zIsLogMoreEnable) {
                LogUtils.debug("--- update->prepareWaitTransAfterUpdated return false " + transitionInfoPoll, new Object[0]);
            }
            return false;
        }
        addToMap(transitionInfoPoll.target, transitionInfoPoll, this.mPrepareTransMap);
        if (zIsLogMoreEnable) {
            LogUtils.debug("--- update->prepareWaitTransAfterUpdated return true " + transitionInfoPoll, new Object[0]);
        }
        return true;
    }

    private void assignAnimTaskToStack(List<AnimTask> list, int i, int i2) {
        for (AnimTask animTask : list) {
            if (this.mTaskStackList.isEmpty()) {
                this.mTaskStackList.add(animTask);
                if (animTask.next != 0) {
                    Log.w(CommonUtils.TAG, "warning!! first task of first stack has next task!! " + animTask + " next:" + animTask.next);
                    animTask.next = null;
                }
            } else if (this.mTaskStackList.size() == 1) {
                AnimTask animTask2 = this.mTaskStackList.get(0);
                int animCountOfTaskStack = AnimTask.getAnimCountOfTaskStack(animTask2);
                if (LogUtils.isLogMainEnabled()) {
                    LogUtils.debug("+++ assignAnimTaskToStack-> firstStackCount " + animCountOfTaskStack, new Object[0]);
                }
                if (animCountOfTaskStack + animTask.getAnimCount() > 4000) {
                    this.mTaskStackList.add(animTask);
                } else {
                    animTask2.addToTail(animTask);
                }
            } else {
                Pair<AnimTask, Integer> minAnimCountOfOtherStack = getMinAnimCountOfOtherStack();
                if (LogUtils.isLogMainEnabled()) {
                    LogUtils.debug("+++ assignAnimTaskToStack-> minAnimCountStackInfo.min " + minAnimCountOfOtherStack.second, new Object[0]);
                }
                AnimTask animTask3 = (AnimTask) minAnimCountOfOtherStack.first;
                int iIntValue = ((Integer) minAnimCountOfOtherStack.second).intValue();
                if (this.mTaskStackList.size() <= i2 - 1 && iIntValue + animTask.getAnimCount() > i) {
                    this.mTaskStackList.add(animTask);
                } else {
                    animTask3.addToTail(animTask);
                }
            }
        }
    }

    private Pair<AnimTask, Integer> getMinAnimCountOfOtherStack() {
        int i = Integer.MAX_VALUE;
        AnimTask animTask = null;
        for (int i2 = 1; i2 < this.mTaskStackList.size(); i2++) {
            AnimTask animTask2 = this.mTaskStackList.get(i2);
            int animCountOfTaskStack = AnimTask.getAnimCountOfTaskStack(animTask2);
            if (animCountOfTaskStack < i) {
                animTask = animTask2;
                i = animCountOfTaskStack;
            }
        }
        return new Pair<>(animTask, Integer.valueOf(i));
    }

    int getTotalRunningTransitionCount() {
        Iterator it = new HashSet(this.mRunningTarget).iterator();
        int runningTransitionCount = 0;
        while (it.hasNext()) {
            runningTransitionCount += ((IAnimTarget) it.next()).animManager.getRunningTransitionCount();
        }
        return runningTransitionCount;
    }

    static class SetToInfo {
        AnimState state;
        IAnimTarget target;

        SetToInfo() {
        }
    }
}
