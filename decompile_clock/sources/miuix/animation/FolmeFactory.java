package miuix.animation;

import android.util.Log;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.controller.AnimState;
import miuix.animation.internal.AndroidEngine;
import miuix.animation.internal.FolmeEngine;
import miuix.animation.internal.TargetHandler;
import miuix.animation.utils.CommonUtils;

/* JADX INFO: loaded from: classes2.dex */
public class FolmeFactory {
    public static FolmeEngine getEngine() {
        return AndroidEngine.getInst();
    }

    public static void fromToState(IAnimTarget iAnimTarget, AnimState animState, AnimState animState2, AnimConfigLink animConfigLink) {
        FolmeEngine engine = getEngine();
        if (engine == null) {
            Log.w(CommonUtils.TAG, "FolmeEngine:warning! do fromToState in non-ui thread! STOP!");
        } else {
            engine.fromTo(iAnimTarget, animState, animState2, animConfigLink);
        }
    }

    public static void start() {
        AndroidEngine.getInst().start();
    }

    public static void end() {
        AndroidEngine.getInst().end();
    }

    public static void clean(final IAnimTarget iAnimTarget) {
        Runnable runnable = new Runnable() { // from class: miuix.animation.FolmeFactory.1
            @Override // java.lang.Runnable
            public void run() {
                FolmeEngine engine = FolmeFactory.getEngine();
                if (engine == null) {
                    Log.w(CommonUtils.TAG, "FolmeEngine:warning! do clean in non-ui thread! STOP!");
                    return;
                }
                if (iAnimTarget.hasFlags(1L)) {
                    engine.removeFromOneShot(iAnimTarget);
                }
                iAnimTarget.clean();
            }
        };
        TargetHandler handler = iAnimTarget.getHandler();
        if (handler == null || handler.isInTargetThread()) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }
}
