package miuix.animation.controller;

import android.util.Log;
import java.lang.reflect.Array;
import miuix.animation.Folme;
import miuix.animation.FolmeFactory;
import miuix.animation.IAnimTarget;
import miuix.animation.IStateStyle;
import miuix.animation.ViewTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.internal.AndroidEngine;
import miuix.animation.internal.PredictTask;
import miuix.animation.listener.TransitionListener;
import miuix.animation.physics.FactorOperator;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;
import miuix.animation.utils.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class FolmeState implements IFolmeStateStyle {
    final IAnimTarget mTarget;
    StateManager mStateMgr = new StateManager();
    AnimConfigLink mConfigLink = new AnimConfigLink();
    private boolean mEnableAnim = true;

    @Override // miuix.animation.FolmeStyle
    public IStateStyle autoSetTo(Object... objArr) {
        return this;
    }

    @Override // miuix.animation.IStateStyle
    @Deprecated
    public IStateStyle setConfig(AnimConfig animConfig, FloatProperty... floatPropertyArr) {
        return this;
    }

    FolmeState(IAnimTarget iAnimTarget) {
        this.mTarget = iAnimTarget;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AnimConfigLink getConfigLink() {
        return this.mConfigLink;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSetTo(Object obj, AnimConfigLink animConfigLink, boolean z) {
        if ((obj instanceof Integer) || (obj instanceof Float)) {
            setTo(obj, animConfigLink);
            return;
        }
        AnimState state = getState(obj);
        if (LogUtils.isLogMainEnabled() && state != null) {
            LogUtils.debug("-> do FolmeState.setTo tag:'" + state.getTag() + "'", "\nsetToState:" + state);
        }
        this.mTarget.animManager.setTo(state, animConfigLink, z);
        this.mStateMgr.clearTempState(state);
        animConfigLink.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doFromTo(Object obj, Object obj2, AnimConfig... animConfigArr) {
        AnimConfigLink configLink = getConfigLink();
        for (AnimConfig animConfig : animConfigArr) {
            configLink.add(animConfig, new boolean[0]);
        }
        if (this.mEnableAnim) {
            this.mStateMgr.setup(obj2);
            if (obj != null) {
                setTo(obj);
            }
            AnimState state = getState(obj2);
            this.mStateMgr.addTempConfig(state, configLink);
            if (LogUtils.isLogMainEnabled()) {
                AnimState state2 = getState(obj);
                Object tag = "null";
                Object tag2 = state2 != null ? state2.getTag() : "null";
                if (state != null) {
                    tag = state.getTag();
                }
                LogUtils.debug("-> do FolmeState fromTo fromTag:'" + tag2 + "' toTag:'" + tag + "'\nfromState:" + state2, "\ntoState:" + state);
                LogUtils.debug("-> do FolmeState fromTo configLink:\n" + configLink, new Object[0]);
            }
            FolmeFactory.fromToState(this.mTarget, getState(obj), getState(obj2), configLink);
            this.mStateMgr.clearTempState(state);
            configLink.clear();
        }
    }

    @Override // miuix.animation.controller.IFolmeStateStyle, miuix.animation.FolmeStyle
    public IAnimTarget getTarget() {
        return this.mTarget;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle setTo(Object obj) {
        return setTo(obj, new AnimConfig[0]);
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle setTo(final Object obj, final AnimConfig... animConfigArr) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            AnimState animState = obj instanceof AnimState ? (AnimState) obj : null;
            LogUtils.debug("FolmeState.setTo tag:'" + (animState != null ? animState.getTag() : "null") + "'", "\nsetToState:" + animState, "\ntarget " + this.mTarget);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> setTo by" + LogUtils.getStackTrace(4), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("setTo tag=\"%s\"%s", obj, LogUtils.getStackTrace(4)));
        }
        IAnimTarget iAnimTarget = this.mTarget;
        if (iAnimTarget != null) {
            iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.1
                @Override // java.lang.Runnable
                public void run() {
                    FolmeState.this.doSetTo(obj, AnimConfigLink.linkConfig(animConfigArr), false);
                }
            });
            return this;
        }
        if (zIsLogMainEnabled) {
            LogUtils.debug("-> warning!! FolmeState.setTo target is null!!", new Object[0]);
        }
        return this;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle setTo(final Object... objArr) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("FolmeState.setTo propertyAndValues", "target " + this.mTarget);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> setTo by" + LogUtils.getStackTrace(4), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("setTo propertyAndValues target=\"%s\"%s", this.mTarget, LogUtils.getStackTrace(4)));
        }
        IAnimTarget iAnimTarget = this.mTarget;
        if (iAnimTarget != null) {
            iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.2
                @Override // java.lang.Runnable
                public void run() {
                    AnimConfigLink configLink = FolmeState.this.getConfigLink();
                    FolmeState.this.doSetTo(FolmeState.this.mStateMgr.getSetToState(FolmeState.this.mTarget, configLink, objArr), configLink, false);
                }
            });
            return this;
        }
        if (zIsLogMainEnabled) {
            LogUtils.debug("warning!! FolmeState.setTo target is null!!", new Object[0]);
        }
        return this;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle resetTo(Object obj) {
        return resetTo(obj, new AnimConfig[0]);
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle resetTo(final Object obj, final AnimConfig... animConfigArr) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("FolmeState.resetTo tag " + obj, "target " + this.mTarget);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> resetTo by" + LogUtils.getStackTrace(4), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("resetTo tag=\"%s\"%s", obj, LogUtils.getStackTrace(4)));
        }
        IAnimTarget iAnimTarget = this.mTarget;
        if (iAnimTarget != null) {
            iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.3
                @Override // java.lang.Runnable
                public void run() {
                    FolmeState.this.doSetTo(obj, AnimConfigLink.linkConfig(animConfigArr), true);
                }
            });
            return this;
        }
        if (zIsLogMainEnabled) {
            LogUtils.debug("-> warning!! FolmeState.resetTo target is null!!", new Object[0]);
        }
        return this;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle resetTo(final Object... objArr) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("FolmeState.resetTo propertyAndValues", "target " + this.mTarget);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> resetTo by" + LogUtils.getStackTrace(4), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("resetTo propertyAndValues target=\"%s\"%s", this.mTarget, LogUtils.getStackTrace(4)));
        }
        IAnimTarget iAnimTarget = this.mTarget;
        if (iAnimTarget != null) {
            iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.4
                @Override // java.lang.Runnable
                public void run() {
                    AnimConfigLink configLink = FolmeState.this.getConfigLink();
                    FolmeState.this.doSetTo(FolmeState.this.mStateMgr.getSetToState(FolmeState.this.mTarget, configLink, objArr), configLink, true);
                }
            });
            return this;
        }
        if (zIsLogMainEnabled) {
            LogUtils.debug("warning!! FolmeState.resetTo target is null!!", new Object[0]);
        }
        return this;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle to(final AnimConfig... animConfigArr) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("FolmeState.to oneTimeConfig", "target " + this.mTarget);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> to by" + LogUtils.getStackTrace(5), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("to oneTimeConfig target=\"%s\"%s", this.mTarget, LogUtils.getStackTrace(5)));
        }
        IAnimTarget iAnimTarget = this.mTarget;
        if (iAnimTarget != null) {
            iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.5
                @Override // java.lang.Runnable
                public void run() {
                    FolmeState folmeState = FolmeState.this;
                    folmeState.doFromTo(null, folmeState.getCurrentState(), animConfigArr);
                }
            });
            return this;
        }
        if (zIsLogMainEnabled) {
            LogUtils.debug("warning!! FolmeState.to target is null!!", new Object[0]);
        }
        return this;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle to(final Object obj, final AnimConfig... animConfigArr) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if ((obj instanceof AnimState) || this.mStateMgr.hasState(obj)) {
            if (zIsLogMainEnabled) {
                LogUtils.debug("FolmeState.to tag with oneTimeConfig", "target " + this.mTarget);
                if (LogUtils.isLogMoreEnable()) {
                    LogUtils.debug("-> to by trace:" + LogUtils.getStackTrace(5), new Object[0]);
                }
            }
            if (LogUtils.isLogDesignEnable()) {
                Log.i(CommonUtils.D_TAG, String.format("to tag with oneTimeConfig target=\"%s\"%s", this.mTarget, LogUtils.getStackTrace(5)));
            }
            IAnimTarget iAnimTarget = this.mTarget;
            if (iAnimTarget != null) {
                iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.6
                    @Override // java.lang.Runnable
                    public void run() {
                        FolmeState folmeState = FolmeState.this;
                        folmeState.doFromTo(null, folmeState.getState(obj), animConfigArr);
                    }
                });
                return this;
            }
            if (zIsLogMainEnabled) {
                LogUtils.debug("warning!! FolmeState.to target is null!!", new Object[0]);
            }
            return this;
        }
        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            Object[] objArr = new Object[animConfigArr.length + length];
            System.arraycopy(obj, 0, objArr, 0, length);
            System.arraycopy(animConfigArr, 0, objArr, length, animConfigArr.length);
            return to(objArr);
        }
        return to(obj, animConfigArr);
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle to(final Object... objArr) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("FolmeState.to propertyAndValues", "target " + this.mTarget);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> to by" + LogUtils.getStackTrace(4), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("to propertyAndValues target=\"%s\"%s", this.mTarget, LogUtils.getStackTrace(4)));
        }
        IAnimTarget iAnimTarget = this.mTarget;
        if (iAnimTarget != null) {
            iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.7
                @Override // java.lang.Runnable
                public void run() {
                    FolmeState folmeState = FolmeState.this;
                    folmeState.doFromTo(null, folmeState.mStateMgr.getToState(FolmeState.this.getTarget(), FolmeState.this.getConfigLink(), objArr), new AnimConfig[0]);
                }
            });
            return this;
        }
        if (zIsLogMainEnabled) {
            LogUtils.debug("warning!! FolmeState.to target is null!!", new Object[0]);
        }
        return this;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle then(final Object obj, final AnimConfig... animConfigArr) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("FolmeState.then tag with oneTimeConfig", "target " + this.mTarget);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> to by" + LogUtils.getStackTrace(4), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("to tag with oneTimeConfig target=\"%s\"%s", this.mTarget, LogUtils.getStackTrace(4)));
        }
        IAnimTarget iAnimTarget = this.mTarget;
        if (iAnimTarget != null) {
            iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.8
                @Override // java.lang.Runnable
                public void run() {
                    FolmeState.this.mStateMgr.setStateFlags(obj, 1L);
                    FolmeState.this.doFromTo(null, obj, animConfigArr);
                }
            });
            return this;
        }
        if (zIsLogMainEnabled) {
            LogUtils.debug("warning!! FolmeState.to target is null!!", new Object[0]);
        }
        return this;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle toWithInit(final Object... objArr) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("FolmeState.toWithInit propertyAndValues", "target " + this.mTarget);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> toWithInit by" + LogUtils.getStackTrace(4), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("toWithInit propertyAndValues target=\"%s\"%s", this.mTarget, LogUtils.getStackTrace(4)));
        }
        IAnimTarget iAnimTarget = this.mTarget;
        if (iAnimTarget != null) {
            iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.9
                @Override // java.lang.Runnable
                public void run() {
                    FolmeState folmeState = FolmeState.this;
                    folmeState.doFromTo(null, folmeState.mStateMgr.getToState(FolmeState.this.getTarget(), FolmeState.this.getConfigLink(), true, objArr), new AnimConfig[0]);
                }
            });
            return this;
        }
        if (zIsLogMainEnabled) {
            LogUtils.debug("warning!! FolmeState.toWithInit target is null!!", new Object[0]);
        }
        return this;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle fromTo(final Object obj, final Object obj2, final AnimConfig... animConfigArr) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("FolmeState.fromTo enter from " + obj, "to " + obj2, "target " + this.mTarget);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> fromTo by" + LogUtils.getStackTrace(5), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("setTo propertyAndValues from=\"%s\", to=\"%s\"%s", obj, obj2, LogUtils.getStackTrace(5)));
        }
        IAnimTarget iAnimTarget = this.mTarget;
        if (iAnimTarget != null) {
            iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.10
                @Override // java.lang.Runnable
                public void run() {
                    FolmeState.this.doFromTo(obj, obj2, animConfigArr);
                }
            });
            return this;
        }
        if (zIsLogMainEnabled) {
            LogUtils.debug("warning!! FolmeState.fromTo target is null!!", new Object[0]);
        }
        return this;
    }

    @Override // miuix.animation.IStateContainer
    public void enableDefaultAnim(boolean z) {
        this.mEnableAnim = z;
    }

    @Override // miuix.animation.FolmeStyle
    public void clean() {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.clean " + this.mTarget + LogUtils.getStackTrace(5), new Object[0]);
        }
        this.mTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.11
            @Override // java.lang.Runnable
            public void run() {
                FolmeState.this.cancel();
                FolmeState.this.mStateMgr.clear();
                FolmeState.this.mEnableAnim = true;
                FolmeState.this.mConfigLink.clear();
            }
        });
    }

    @Override // miuix.animation.ICancelableStyle
    public void cancel() {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.cancel empty params target " + this.mTarget, new Object[0]);
        }
        this.mTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.12
            @Override // java.lang.Runnable
            public void run() {
                if (FolmeState.this.mTarget.isIdle()) {
                    return;
                }
                FolmeState.this.mTarget.cancelRunningAnim();
            }
        });
    }

    @Override // miuix.animation.IStateStyle
    public void cancel(final AnimState animState) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.cancel with state " + animState + " target " + this.mTarget, new Object[0]);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> cancel by" + LogUtils.getStackTrace(5), new Object[0]);
            }
        }
        this.mTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.13
            @Override // java.lang.Runnable
            public void run() {
                if (FolmeState.this.mTarget.isIdle()) {
                    return;
                }
                AndroidEngine.getInst().cancel(FolmeState.this.mTarget, animState);
            }
        });
    }

    @Override // miuix.animation.ICancelableStyle
    public void cancel(final FloatProperty... floatPropertyArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.cancel FloatProperties target " + this.mTarget, new Object[0]);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> cancel by" + LogUtils.getStackTrace(5), new Object[0]);
            }
        }
        if (CommonUtils.isArrayEmpty(floatPropertyArr)) {
            cancel();
        } else {
            this.mTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.14
                @Override // java.lang.Runnable
                public void run() {
                    if (FolmeState.this.mTarget.isIdle()) {
                        return;
                    }
                    AndroidEngine.getInst().cancel(FolmeState.this.mTarget, floatPropertyArr);
                }
            });
        }
    }

    @Override // miuix.animation.ICancelableStyle
    public void cancel(final String... strArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.cancel PropertyNames target " + this.mTarget, new Object[0]);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> cancel by" + LogUtils.getStackTrace(5), new Object[0]);
            }
        }
        if (CommonUtils.isArrayEmpty(strArr)) {
            cancel();
        } else {
            this.mTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.15
                @Override // java.lang.Runnable
                public void run() {
                    if (FolmeState.this.mTarget.isIdle()) {
                        return;
                    }
                    AndroidEngine.getInst().cancel(FolmeState.this.mTarget, strArr);
                }
            });
        }
    }

    @Override // miuix.animation.FolmeStyle
    public void end() {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.end empty params target " + this.mTarget, new Object[0]);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> end by" + LogUtils.getStackTrace(5), new Object[0]);
            }
        }
        this.mTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.16
            @Override // java.lang.Runnable
            public void run() {
                AndroidEngine.getInst().end(FolmeState.this.mTarget, FolmeState.this.mStateMgr.getToState(FolmeState.this.mTarget, FolmeState.this.getConfigLink(), new Object[0]));
            }
        });
    }

    @Override // miuix.animation.ICancelableStyle
    public void end(final Object... objArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.end propertyList target " + this.mTarget, new Object[0]);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> end by" + LogUtils.getStackTrace(5), new Object[0]);
            }
        }
        this.mTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.17
            @Override // java.lang.Runnable
            public void run() {
                if (CommonUtils.isArrayEmpty(objArr)) {
                    AndroidEngine.getInst().end(FolmeState.this.mTarget, FolmeState.this.mStateMgr.getToState(FolmeState.this.mTarget, FolmeState.this.getConfigLink(), new Object[0]));
                    return;
                }
                Object[] objArr2 = objArr;
                if (objArr2.length > 0) {
                    if (objArr2[0] instanceof FloatProperty) {
                        FloatProperty[] floatPropertyArr = new FloatProperty[objArr2.length];
                        System.arraycopy(objArr2, 0, floatPropertyArr, 0, objArr2.length);
                        AndroidEngine.getInst().end(FolmeState.this.mTarget, floatPropertyArr);
                    } else {
                        String[] strArr = new String[objArr2.length];
                        System.arraycopy(objArr2, 0, strArr, 0, objArr2.length);
                        AndroidEngine.getInst().end(FolmeState.this.mTarget, strArr);
                    }
                }
            }
        });
    }

    @Override // miuix.animation.FolmeStyle
    public long predictDuration(Object... objArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.predictDuration target:" + this.mTarget, new Object[0]);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> predictDuration by" + LogUtils.getStackTrace(4), new Object[0]);
            }
        }
        AnimConfigLink animConfigLink = new AnimConfigLink();
        AnimState toState = this.mStateMgr.getToState(this.mTarget, animConfigLink, objArr);
        long jPredictDuration = PredictTask.predictDuration(this.mTarget, null, toState, animConfigLink);
        this.mStateMgr.clearTempState(toState);
        animConfigLink.clear();
        return jPredictDuration;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle then(final Object... objArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.then target:" + this.mTarget, new Object[0]);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> then by" + LogUtils.getStackTrace(4), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("then propertyAndValues target=\"%s\"%s", this.mTarget, LogUtils.getStackTrace(4)));
        }
        this.mTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState.18
            @Override // java.lang.Runnable
            public void run() {
                AnimState state = FolmeState.this.getState(objArr);
                state.flags = 1L;
                FolmeState.this.mStateMgr.setAnimState(FolmeState.this.getTarget(), state, FolmeState.this.getConfigLink(), objArr);
                FolmeState.this.doFromTo(null, state, new AnimConfig[0]);
            }
        });
        return this;
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle setFlags(final long j) {
        IAnimTarget iAnimTarget = this.mTarget;
        if (iAnimTarget == null) {
            return this;
        }
        iAnimTarget.post(new Runnable() { // from class: miuix.animation.controller.FolmeState$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1780lambda$setFlags$0$miuixanimationcontrollerFolmeState(j);
            }
        });
        return this;
    }

    /* JADX INFO: renamed from: lambda$setFlags$0$miuix-animation-controller-FolmeState, reason: not valid java name */
    /* synthetic */ void m1780lambda$setFlags$0$miuixanimationcontrollerFolmeState(long j) {
        this.mTarget.setFlags(j);
    }

    @Override // miuix.animation.controller.IFolmeStateStyle
    public AnimState getState(Object obj) {
        return this.mStateMgr.getState(obj);
    }

    @Override // miuix.animation.controller.IFolmeStateStyle
    public void addState(AnimState animState) {
        this.mStateMgr.addState(animState);
    }

    @Override // miuix.animation.FolmeStyle
    public IStateStyle setup(Object obj) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.setup tag:" + obj, "target:" + this.mTarget);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> setup by" + LogUtils.getStackTrace(5), new Object[0]);
            }
        }
        if (LogUtils.isLogDesignEnable()) {
            Log.i(CommonUtils.D_TAG, String.format("setup tag=\"%s\"%s", obj, LogUtils.getStackTrace(5)));
        }
        this.mStateMgr.setup(obj);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle set(Object obj) {
        this.mStateMgr.setup(obj);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle addListener(TransitionListener transitionListener) {
        this.mStateMgr.addListener(transitionListener);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle removeListener(TransitionListener transitionListener) {
        this.mStateMgr.removeListener(transitionListener);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle addInitProperty(FloatProperty floatProperty, int i) {
        this.mStateMgr.addInitProperty(floatProperty, i);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle addInitProperty(FloatProperty floatProperty, float f) {
        this.mStateMgr.addInitProperty(floatProperty, f);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle addInitProperty(String str, int i) {
        FloatProperty floatProperty;
        if ((this.mTarget instanceof ViewTarget) && (floatProperty = ViewTarget.getFloatProperty(str)) != null) {
            if (floatProperty instanceof IIntValueProperty) {
                this.mStateMgr.addInitProperty(floatProperty, i);
            } else {
                this.mStateMgr.addInitProperty(floatProperty, i);
            }
            return this;
        }
        this.mStateMgr.addInitProperty(str, i);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle addInitProperty(String str, float f) {
        FloatProperty floatProperty;
        if ((this.mTarget instanceof ViewTarget) && (floatProperty = ViewTarget.getFloatProperty(str)) != null) {
            if (floatProperty instanceof IIntValueProperty) {
                this.mStateMgr.add(floatProperty, (int) f);
            } else {
                this.mStateMgr.add(floatProperty, f);
            }
            return this;
        }
        this.mStateMgr.addInitProperty(str, f);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle add(FloatProperty floatProperty, int i, long j) {
        this.mStateMgr.add(floatProperty, i, j);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle add(String str, int i, long j) {
        FloatProperty floatProperty;
        if ((this.mTarget instanceof ViewTarget) && (floatProperty = ViewTarget.getFloatProperty(str)) != null) {
            if (floatProperty instanceof IIntValueProperty) {
                this.mStateMgr.add(floatProperty, i, j);
            } else if (j == 4) {
                this.mStateMgr.add(floatProperty, i);
            } else {
                this.mStateMgr.add(floatProperty, i, j);
            }
            return this;
        }
        this.mStateMgr.add(str, i, j);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle add(FloatProperty floatProperty, int i) {
        this.mStateMgr.add(floatProperty, i);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle add(FloatProperty floatProperty, float f) {
        this.mStateMgr.add(floatProperty, f);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle add(FloatProperty floatProperty, float f, long j) {
        this.mStateMgr.add(floatProperty, f, j);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle add(String str, int i) {
        FloatProperty floatProperty;
        if ((this.mTarget instanceof ViewTarget) && (floatProperty = ViewTarget.getFloatProperty(str)) != null) {
            if (floatProperty instanceof IIntValueProperty) {
                this.mStateMgr.add(floatProperty, i);
            } else {
                this.mStateMgr.add(floatProperty, i);
            }
            return this;
        }
        this.mStateMgr.add(str, i);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle add(String str, float f) {
        FloatProperty floatProperty;
        if ((this.mTarget instanceof ViewTarget) && (floatProperty = ViewTarget.getFloatProperty(str)) != null) {
            if (floatProperty instanceof IIntValueProperty) {
                this.mStateMgr.add(floatProperty, (int) f);
            } else {
                this.mStateMgr.add(floatProperty, f);
            }
            return this;
        }
        this.mStateMgr.add(str, f);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle add(String str, float f, long j) {
        FloatProperty floatProperty;
        if ((this.mTarget instanceof ViewTarget) && (floatProperty = ViewTarget.getFloatProperty(str)) != null) {
            if (floatProperty instanceof IIntValueProperty) {
                this.mStateMgr.add(floatProperty, (int) f, j);
            } else if (j == 4) {
                this.mStateMgr.add(floatProperty, f);
            } else {
                this.mStateMgr.add(floatProperty, f, j);
            }
            return this;
        }
        this.mStateMgr.add(str, f, j);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle setEase(EaseManager.EaseStyle easeStyle, FloatProperty... floatPropertyArr) {
        this.mStateMgr.setEase(easeStyle, floatPropertyArr);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    @Deprecated
    public IStateStyle setEase(int i, float... fArr) {
        this.mStateMgr.setEase(i, fArr);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle setEase(FloatProperty floatProperty, int i, float... fArr) {
        this.mStateMgr.setEase(floatProperty, i, fArr);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle setEase(int i, FactorOperator... factorOperatorArr) {
        this.mStateMgr.setEase(i, factorOperatorArr);
        return this;
    }

    @Override // miuix.animation.IStateStyle
    public AnimState getCurrentState() {
        return this.mStateMgr.getCurrentState();
    }

    @Override // miuix.animation.IStateStyle
    public float getPredictValue(FloatProperty floatProperty, float... fArr) {
        float predictDistance;
        float velocity = (float) this.mTarget.getVelocity(floatProperty);
        float value = this.mTarget.getValue(floatProperty);
        float thresholdVelocity = (float) Folme.getTarget(this.mTarget).getThresholdVelocity(floatProperty);
        if (velocity != 0.0f) {
            thresholdVelocity = Math.abs(thresholdVelocity) * Math.signum(velocity);
        }
        if (fArr == null || fArr.length == 0) {
            predictDistance = Folme.getPredictDistance(velocity, thresholdVelocity);
        } else {
            predictDistance = Folme.getPredictDistanceWithFriction(velocity, fArr[0], thresholdVelocity);
        }
        return value + predictDistance;
    }

    @Override // miuix.animation.IStateStyle
    public float getPredictFriction(FloatProperty floatProperty, float f) {
        float velocity = (float) this.mTarget.getVelocity(floatProperty);
        if (velocity == 0.0f) {
            return -1.0f;
        }
        return Folme.getPredictFriction(this.mTarget.getValue(floatProperty), f, velocity, Math.signum(velocity) * ((float) Folme.getTarget(this.mTarget).getThresholdVelocity(floatProperty)));
    }

    @Override // miuix.animation.IStateStyle
    public IStateStyle setTransitionFlags(long j, FloatProperty... floatPropertyArr) {
        StateManager stateManager = this.mStateMgr;
        stateManager.setTransitionFlags(stateManager.getCurrentState(), j, floatPropertyArr);
        return this;
    }

    @Override // miuix.animation.IStateContainer
    @Deprecated
    public void addConfig(Object obj, AnimConfig... animConfigArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeState.addConfig warning!! this interface can not work, target:" + this.mTarget, new Object[0]);
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("-> addConfig by" + LogUtils.getStackTrace(5), new Object[0]);
            }
        }
    }
}
