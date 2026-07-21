package okio;

import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes3.dex */
final class PushableTimeout extends Timeout {
    private long originalDeadlineNanoTime;
    private boolean originalHasDeadline;
    private long originalTimeoutNanos;
    private Timeout pushed;

    PushableTimeout() {
    }

    void push(Timeout timeout) {
        this.pushed = timeout;
        boolean zHasDeadline = timeout.hasDeadline();
        this.originalHasDeadline = zHasDeadline;
        this.originalDeadlineNanoTime = zHasDeadline ? timeout.deadlineNanoTime() : -1L;
        long jTimeoutNanos = timeout.timeoutNanos();
        this.originalTimeoutNanos = jTimeoutNanos;
        timeout.timeout(minTimeout(jTimeoutNanos, timeoutNanos()), TimeUnit.NANOSECONDS);
        if (this.originalHasDeadline && hasDeadline()) {
            timeout.deadlineNanoTime(Math.min(deadlineNanoTime(), this.originalDeadlineNanoTime));
        } else if (hasDeadline()) {
            timeout.deadlineNanoTime(deadlineNanoTime());
        }
    }

    void pop() {
        this.pushed.timeout(this.originalTimeoutNanos, TimeUnit.NANOSECONDS);
        if (this.originalHasDeadline) {
            this.pushed.deadlineNanoTime(this.originalDeadlineNanoTime);
        } else {
            this.pushed.clearDeadline();
        }
    }
}
