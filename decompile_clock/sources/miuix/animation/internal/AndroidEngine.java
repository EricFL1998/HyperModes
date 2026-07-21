package miuix.animation.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.util.HashMap;
import miuix.animation.Folme;
import miuix.animation.physics.AnimationHandler;
import miuix.animation.utils.BoostHelper;
import miuix.animation.utils.CommonUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidEngine extends FolmeEngine implements AnimationHandler.AnimationFrameCallback {
    private static final int MSG_END = 1;
    private static final int MSG_START = 0;
    static volatile EngineHandler sMainHandler;
    static volatile AndroidEngine sMainInstance;
    static final ThreadLocal<AndroidEngine> sThreadInstance = new ThreadLocal<AndroidEngine>() { // from class: miuix.animation.internal.AndroidEngine.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public AndroidEngine initialValue() {
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper == null) {
                return null;
            }
            if (looperMyLooper == Folme.getLooper() || Folme.getUiLooperByTid(looperMyLooper.getThread().getId()) != null) {
                return new AndroidEngine();
            }
            return null;
        }
    };
    private Handler mHandler;

    private static class EngineHandler extends Handler {
        public EngineHandler(Looper looper) {
            super(looper);
        }

        public EngineHandler(Looper looper, Handler.Callback callback) {
            super(looper, callback);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                AndroidEngine.getInst().startAnim();
            } else {
                if (i != 1) {
                    return;
                }
                AndroidEngine.getInst().endAnim();
            }
        }
    }

    public static AndroidEngine getInst() {
        AndroidEngine androidEngine = sThreadInstance.get();
        return androidEngine == null ? sMainInstance : androidEngine;
    }

    static void turboThreadIfNeed(int i) {
        HashMap<Integer, Boolean> map = getInst().mScheduler.mAnimTaskSchedMap;
        if (map.containsKey(Integer.valueOf(i)) || BoostHelper.getInstance().isTurboSchedDisabled || Folme.appContext == null) {
            return;
        }
        try {
            BoostHelper.getInstance().setTurboSchedActionWithPriority(new int[]{i}, 1000L, Folme.appContext);
            map.put(Integer.valueOf(i), true);
        } catch (Exception unused) {
        }
    }

    public AndroidEngine() {
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper == null) {
            return;
        }
        EngineHandler engineHandler = new EngineHandler(looperMyLooper);
        setHandler(engineHandler);
        if (looperMyLooper == Looper.getMainLooper()) {
            sMainInstance = this;
            sMainHandler = engineHandler;
        }
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
        this.mScheduler.handler = handler;
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    @Override // miuix.animation.internal.FolmeEngine
    protected void stopNextFrame() {
        AnimationHandler.getInstance().removeCallback(this);
    }

    @Override // miuix.animation.internal.FolmeEngine
    protected void scheduleNextFrame(long j) {
        AnimationHandler.getInstance().addAnimationFrameCallback(this, j);
    }

    @Override // miuix.animation.internal.FolmeEngine
    public void start() {
        Handler handler = this.mHandler;
        if (handler != null && handler.getLooper() == Looper.myLooper()) {
            startAnim();
            return;
        }
        if (handler == null) {
            handler = sMainHandler;
        }
        if (handler != null) {
            handler.sendEmptyMessage(0);
        } else {
            Log.w(CommonUtils.TAG, "AndroidEngine.start handler is null! looper: " + Looper.myLooper());
        }
    }

    @Override // miuix.animation.internal.FolmeEngine
    public void end() {
        Handler handler = this.mHandler;
        if (handler != null && handler.getLooper() == Looper.myLooper()) {
            endAnim();
            return;
        }
        if (handler == null) {
            handler = sMainHandler;
        }
        if (handler != null) {
            handler.sendEmptyMessage(1);
        } else {
            Log.w(CommonUtils.TAG, "AndroidEngine.end handler is null! looper: " + Looper.myLooper());
        }
    }
}
