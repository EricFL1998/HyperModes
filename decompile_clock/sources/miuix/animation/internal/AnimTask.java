package miuix.animation.internal;

import android.util.Log;
import java.util.List;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LinkNode;
import miuix.animation.utils.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AnimTask extends LinkNode<AnimTask> implements Runnable {
    public static final int MAX_ANIM_COUNT_SINGLE_TASK = 100;
    public static final int MAX_MAIN_THREAD_TASK_SIZE = 4000;
    public static final int MAX_SUB_THREAD_TASK_SIZE = Math.round(56000.0f / (ThreadPoolUtil.MAX_SPLIT_COUNT - 1));
    public static final byte OP_CANCEL = 4;
    public static final byte OP_END = 3;
    public static final byte OP_FAILED = 5;
    public static final byte OP_INVALID = 0;
    public static final byte OP_REUSE = 6;
    public static final byte OP_START = 1;
    public static final byte OP_UPDATE = 2;
    public final AnimStats animStats = new AnimStats();
    public double delta;
    public long deltaTNanos;
    public int frameCount;
    public TransitionInfo info;
    public boolean runInMainThread;
    public AnimScheduler scheduler;
    public int startPos;
    public long totalTNanos;

    public static boolean isRunning(byte b) {
        return b == 1 || b == 2;
    }

    static void start(AnimTask animTask, AnimScheduler animScheduler, long j, long j2, int i, double d) {
        animTask.totalTNanos = j;
        animTask.deltaTNanos = j2;
        animTask.runInMainThread = true;
        animTask.scheduler = animScheduler;
        animTask.frameCount = i;
        animTask.delta = d;
        animTask.run();
    }

    static void asyncStart(AnimTask animTask, AnimScheduler animScheduler, long j, long j2, int i, double d) {
        animTask.totalTNanos = j;
        animTask.deltaTNanos = j2;
        animTask.runInMainThread = false;
        animTask.scheduler = animScheduler;
        animTask.frameCount = i;
        animTask.delta = d;
        ThreadPoolUtil.post(animTask);
    }

    public static int getAnimCountOfTaskStack(AnimTask animTask) {
        int i = 0;
        while (animTask != null) {
            i += animTask.animStats.animCount;
            animTask = (AnimTask) animTask.next;
        }
        return i;
    }

    void setup(int i, int i2, int i3) {
        this.animStats.clear();
        this.animStats.animCount = i2;
        this.animStats.focusCount = i3;
        this.startPos = i;
    }

    @Override // java.lang.Runnable
    public void run() {
        long j;
        String str;
        String str2;
        long jCurrentTimeMillis = System.currentTimeMillis();
        long id = Thread.currentThread().getId();
        boolean zIsLogDetailEnable = LogUtils.isLogDetailEnable();
        if (zIsLogDetailEnable) {
            LogUtils.logThread(CommonUtils.TAG, "++++ AnimTask run stack onFrame start belong to Scheduler@" + this.scheduler.hashCode() + "-" + id);
        }
        try {
            long j2 = this.totalTNanos;
            long j3 = this.deltaTNanos;
            int i = this.frameCount;
            double d = this.delta;
            j = id;
            str = CommonUtils.TAG;
            str2 = "-";
            try {
                AnimTaskStackRunner.doAnimationFrame(this, j2, j3, i, d, true);
            } catch (Exception e) {
                e = e;
                LogUtils.logThread(str, "---- AnimTaskRunner.doAnimationFrame failed", Log.getStackTraceString(e));
            }
        } catch (Exception e2) {
            e = e2;
            j = id;
            str = CommonUtils.TAG;
            str2 = "-";
        }
        int iDecrementAndGet = this.scheduler.runningStackCount.decrementAndGet();
        if (zIsLogDetailEnable) {
            LogUtils.logThread(str, "---- AnimTask run stack onFrame end cost " + (System.currentTimeMillis() - jCurrentTimeMillis) + " runStackCount " + iDecrementAndGet + " belong to Scheduler@" + this.scheduler.hashCode() + str2 + j);
        }
        if (iDecrementAndGet == 0) {
            this.scheduler.executeUpdate();
        }
    }

    public int getAnimCount() {
        return this.animStats.animCount;
    }

    void updateAnimStats() {
        List<UpdateInfo> list = this.info.updateList;
        int i = this.startPos;
        int i2 = this.animStats.animCount + i;
        while (i < i2) {
            UpdateInfo updateInfo = list.get(i);
            if (updateInfo != null) {
                if (updateInfo.animInfo.op == 0 || updateInfo.animInfo.op == 1) {
                    this.animStats.prepareCount++;
                } else {
                    this.animStats.startedCount++;
                    byte b = updateInfo.animInfo.op;
                    if (b == 3) {
                        this.animStats.endCount++;
                    } else if (b == 4) {
                        this.animStats.cancelCount++;
                    } else if (b == 5 || b == 6) {
                        this.animStats.failCount++;
                    }
                }
            }
            i++;
        }
    }

    public String toString() {
        return "AnimTask@" + hashCode() + "{info.id=" + this.info.id + " start=" + this.startPos + " animStats=" + this.animStats + "}";
    }
}
