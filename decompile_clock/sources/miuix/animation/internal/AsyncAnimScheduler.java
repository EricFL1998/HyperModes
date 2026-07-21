package miuix.animation.internal;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import miuix.animation.Folme;
import miuix.animation.IAnimTarget;
import miuix.animation.controller.AnimState;
import miuix.animation.utils.BoostHelper;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
class AsyncAnimScheduler extends AnimScheduler {
    private final Handler mScheduleHandler;
    protected final Map<Integer, TransitionInfo> mTempInfoMap;
    private final HandlerThread mThread;

    AsyncAnimScheduler(FolmeEngine folmeEngine) {
        super(folmeEngine);
        this.mTempInfoMap = new ConcurrentHashMap();
        HandlerThread handlerThread = new HandlerThread("SubAnimSchedulerThread", ThreadPoolUtil.sThreadPriority);
        this.mThread = handlerThread;
        handlerThread.start();
        this.mScheduleHandler = new ScheduleHandler(handlerThread.getLooper());
    }

    @Override // miuix.animation.internal.AnimScheduler
    public void destroy() {
        this.mThread.quitSafely();
    }

    @Override // miuix.animation.internal.AnimScheduler
    void executePendingSetTo(IAnimTarget iAnimTarget, AnimState animState) {
        AnimScheduler.SetToInfo setToInfo = new AnimScheduler.SetToInfo();
        setToInfo.target = iAnimTarget;
        if (animState.needDuplicate) {
            setToInfo.state = new AnimState();
            setToInfo.state.set(animState);
        } else {
            setToInfo.state = animState;
        }
        this.mScheduleHandler.obtainMessage(4, setToInfo).sendToTarget();
    }

    @Override // miuix.animation.internal.AnimScheduler
    void executeTo(TransitionInfo transitionInfo) {
        this.mTempInfoMap.put(Integer.valueOf(transitionInfo.id), transitionInfo);
        this.mScheduleHandler.obtainMessage(1, transitionInfo.id, 0).sendToTarget();
    }

    @Override // miuix.animation.internal.AnimScheduler
    void executeDoAnimOnFrame(long j, long j2) {
        Message messageObtainMessage = this.mScheduleHandler.obtainMessage();
        messageObtainMessage.what = 3;
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.frameTime = j;
        timeInfo.deltaT = j2;
        messageObtainMessage.obj = timeInfo;
        this.mScheduleHandler.sendMessage(messageObtainMessage);
    }

    @Override // miuix.animation.internal.AnimScheduler
    public void executeUpdate() {
        Message messageObtainMessage = this.mScheduleHandler.obtainMessage();
        messageObtainMessage.what = 2;
        this.mScheduleHandler.sendMessage(messageObtainMessage);
    }

    @Override // miuix.animation.internal.AnimScheduler
    protected void runAnimTaskOnFrame(long j, long j2, long j3) {
        if (!BoostHelper.hasBindBigCpu.get() && !BoostHelper.getInstance().isTurboSchedDisabled && Folme.appContext != null) {
            try {
                BoostHelper.getInstance().setTurboSchedActionWithoutBlock(new int[]{this.mThread.getThreadId()}, 3000L, Folme.appContext);
                BoostHelper.hasBindBigCpu.set(true);
            } catch (Exception unused) {
            }
        }
        super.runAnimTaskOnFrame(j, j2, j3);
    }

    class ScheduleHandler extends Handler {
        ScheduleHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                AsyncAnimScheduler.this.m1789lambda$executeTo$1$miuixanimationinternalAnimScheduler(AsyncAnimScheduler.this.mTempInfoMap.get(Integer.valueOf(message.arg1)));
            } else if (i == 2) {
                AsyncAnimScheduler.this.update();
            } else if (i != 3) {
                if (i == 4) {
                    AsyncAnimScheduler.this.pendingSetTo((AnimScheduler.SetToInfo) message.obj);
                }
            } else if (message.obj instanceof TimeInfo) {
                TimeInfo timeInfo = (TimeInfo) message.obj;
                AsyncAnimScheduler.this.m1786x52eb5ba4(timeInfo.frameTime, timeInfo.deltaT);
            }
            message.obj = null;
        }
    }

    class TimeInfo {
        long deltaT;
        long frameTime;

        TimeInfo() {
        }
    }
}
