package miuix.animation.utils;

import android.content.Context;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public class BoostHelper {
    private static final int BOOST_PRIORITY = -20;
    private static final String TAG = "Miuix.Boost";
    private static final BoostHelper sInstance = new BoostHelper();
    public static volatile AtomicBoolean hasBindBigCpu = new AtomicBoolean(false);
    public static volatile AtomicBoolean enableBoostBigCpu = new AtomicBoolean(false);
    public static volatile AtomicBoolean hasBoostBigCpu = new AtomicBoolean(false);
    private Class<?> mTurboSchedManagerClass = null;
    private Object mTurboSchedManagerObject = null;
    public boolean isTurboSchedDisabled = false;

    private void createManagerClassAndInstance(Context context) throws Exception {
    }

    private BoostHelper() {
    }

    public static BoostHelper getInstance() {
        return sInstance;
    }

    public void setTurboSchedAction(int[] iArr, long j, Context context) throws Exception {
        if (this.mTurboSchedManagerClass == null) {
            createManagerClassAndInstance(context);
        }
        if (this.mTurboSchedManagerClass == null || this.mTurboSchedManagerObject == null) {
            this.isTurboSchedDisabled = true;
        }
    }

    public void setTurboSchedActionWithoutBlock(int[] iArr, long j, Context context) throws Exception {
        if (this.mTurboSchedManagerClass == null) {
            createManagerClassAndInstance(context);
        }
        if (this.mTurboSchedManagerClass == null || this.mTurboSchedManagerObject == null) {
            this.isTurboSchedDisabled = true;
        }
    }

    public void setTurboSchedActionWithBoostFrequency(int[] iArr, long j, Context context) throws Exception {
        if (this.mTurboSchedManagerClass == null) {
            createManagerClassAndInstance(context);
        }
        if (this.mTurboSchedManagerClass == null || this.mTurboSchedManagerObject == null) {
            this.isTurboSchedDisabled = true;
        }
    }

    public void setTurboSchedActionWithPriority(int[] iArr, long j, Context context) throws Exception {
        if (this.mTurboSchedManagerClass == null) {
            createManagerClassAndInstance(context);
        }
        if (this.mTurboSchedManagerClass == null || this.mTurboSchedManagerObject == null) {
            this.isTurboSchedDisabled = true;
        }
    }

    public void setTurboSchedActionToLittleCore(int[] iArr, long j, Context context) throws Exception {
        if (this.mTurboSchedManagerClass == null) {
            createManagerClassAndInstance(context);
        }
        if (this.mTurboSchedManagerClass == null || this.mTurboSchedManagerObject == null) {
            this.isTurboSchedDisabled = true;
        }
    }
}
