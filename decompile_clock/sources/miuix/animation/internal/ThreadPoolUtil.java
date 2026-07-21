package miuix.animation.internal;

import android.os.Process;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes2.dex */
public class ThreadPoolUtil {
    private static final int CPU_COUNT;
    private static final int KEEP_ALIVE = 30;
    private static final int KEEP_POOL_SIZE;
    public static final int MAX_SPLIT_COUNT;
    private static final ThreadPoolExecutor sCacheThread;
    private static final Executor sSingleThread;
    public static int sThreadPriority;

    static {
        int iAvailableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = iAvailableProcessors;
        int i = iAvailableProcessors * 2;
        MAX_SPLIT_COUNT = i + 1;
        int i2 = iAvailableProcessors < 4 ? 0 : (iAvailableProcessors / 2) + 1;
        KEEP_POOL_SIZE = i2;
        sThreadPriority = -2;
        sCacheThread = new ThreadPoolExecutor(i2, i + 4, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue(100), getThreadFactory("AnimThread"), new RejectedExecutionHandler() { // from class: miuix.animation.internal.ThreadPoolUtil.1
            @Override // java.util.concurrent.RejectedExecutionHandler
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                ThreadPoolUtil.sSingleThread.execute(runnable);
            }
        });
        sSingleThread = Executors.newSingleThreadExecutor(getThreadFactory("WorkThread"));
    }

    private static ThreadFactory getThreadFactory(final String str) {
        return new ThreadFactory() { // from class: miuix.animation.internal.ThreadPoolUtil.2
            final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, str + "-" + this.threadNumber.getAndIncrement()) { // from class: miuix.animation.internal.ThreadPoolUtil.2.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        Process.setThreadPriority(ThreadPoolUtil.sThreadPriority);
                        super.run();
                    }
                };
            }
        };
    }

    public static void post(Runnable runnable) {
        sCacheThread.execute(runnable);
    }

    public static void getSplitCount(int i, int[] iArr) {
        int i2 = (i / AnimTask.MAX_SUB_THREAD_TASK_SIZE) + 1;
        int i3 = MAX_SPLIT_COUNT;
        if (i2 > i3) {
            i2 = i3;
        }
        int iCeil = AnimTask.MAX_SUB_THREAD_TASK_SIZE;
        if (i2 > 1) {
            iCeil = (int) Math.ceil(i / (i2 - 1));
        }
        iArr[0] = i2;
        iArr[1] = iCeil;
    }

    public static void setThreadPriority(int i) {
        sThreadPriority = i;
    }
}
