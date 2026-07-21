package miuix.animation.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.List;
import miuix.animation.IAnimTarget;
import miuix.animation.utils.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public final class TargetHandler extends Handler {
    public static final int ANIM_MSG_END = 2;
    public static final int ANIM_MSG_REMOVE_WAIT = 3;
    public static final int ANIM_MSG_REPLACED = 5;
    public static final int ANIM_MSG_REPLACE_LISTENER = 4;
    public static final int ANIM_MSG_START = 0;
    public static final int ANIM_MSG_UPDATE = 1;
    private final IAnimTarget mTarget;

    public TargetHandler(Looper looper, IAnimTarget iAnimTarget) {
        super(looper);
        this.mTarget = iAnimTarget;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 0) {
            TransitionInfo transitionInfoRemove = this.mTarget.animManager.mTempTransMap.remove(Integer.valueOf(message.arg1));
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("<<< receive msg=ANIM_MSG_START, info=" + transitionInfoRemove + ", info.id=" + message.arg1 + ", obj =" + message.obj + ", target=" + this.mTarget, new Object[0]);
            }
            if (transitionInfoRemove == null || transitionInfoRemove.hasOnStart) {
                return;
            }
            this.mTarget.animManager.onStart(transitionInfoRemove, message.obj != null ? (List) message.obj : null, message.arg2 == 1);
            return;
        }
        if (i == 1) {
            TransitionInfo transitionInfoRemove2 = this.mTarget.animManager.mTempTransForUpdateMap.remove(Integer.valueOf(message.arg1));
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("<<< receive msg=ANIM_MSG_UPDATE, info=" + transitionInfoRemove2 + ", info.id=" + message.arg1 + ", obj =" + message.obj + ", target=" + this.mTarget, new Object[0]);
            }
            if (transitionInfoRemove2 != null) {
                this.mTarget.animManager.onUpdate(transitionInfoRemove2, message.obj != null ? (List) message.obj : null);
                return;
            }
            return;
        }
        if (i == 2) {
            TransitionInfo transitionInfoRemove3 = this.mTarget.animManager.mTempTransMap.remove(Integer.valueOf(message.arg1));
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("<<< receive msg=ANIM_MSG_END, info=" + transitionInfoRemove3 + ", info.id=" + message.arg1 + ", obj =" + message.obj + ", target=" + this.mTarget, new Object[0]);
            }
            if (transitionInfoRemove3 != null) {
                this.mTarget.animManager.onEnd(transitionInfoRemove3, message.arg2);
                return;
            }
            return;
        }
        if (i == 3) {
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("<<< receive msg=ANIM_MSG_REMOVE_WAIT, target=" + this.mTarget, new Object[0]);
            }
            this.mTarget.animManager.onRemoveWait();
            return;
        }
        if (i == 4) {
            TransitionInfo transitionInfoRemove4 = this.mTarget.animManager.mTempTransMap.remove(Integer.valueOf(message.arg1));
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("<<< receive msg=ANIM_MSG_REPLACE_LISTENER, info=" + transitionInfoRemove4 + ", info.id=" + message.arg1 + ", obj =" + message.obj + ", target=" + this.mTarget, new Object[0]);
            }
            if (transitionInfoRemove4 != null) {
                this.mTarget.animManager.onReplaceListeners(transitionInfoRemove4);
                return;
            }
            return;
        }
        if (i != 5) {
            return;
        }
        TransitionInfo transitionInfoRemove5 = this.mTarget.animManager.mTempTransMap.remove(Integer.valueOf(message.arg1));
        if (LogUtils.isLogMoreEnable()) {
            LogUtils.debug("<<< receive msg=ANIM_MSG_REPLACED, info=" + transitionInfoRemove5 + ", info.id=" + message.arg1 + ", obj =" + message.obj + ", target=" + this.mTarget, new Object[0]);
        }
        if (transitionInfoRemove5 != null) {
            this.mTarget.animManager.onReplaced(transitionInfoRemove5);
        }
    }

    public boolean isInTargetThread() {
        return getLooper().isCurrentThread();
    }
}
