package miuix.animation.internal;

import android.util.ArrayMap;
import java.lang.ref.WeakReference;
import java.util.Map;
import miuix.animation.IAnimTarget;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.VelocityMonitor;

/* JADX INFO: loaded from: classes2.dex */
public class TargetVelocityTracker {
    private Map<FloatProperty, MonitorInfo> mMonitors = new ArrayMap();

    private static class MonitorInfo {
        VelocityMonitor monitor;
        ResetRunnable resetTask;

        private MonitorInfo() {
            this.monitor = new VelocityMonitor();
            this.resetTask = new ResetRunnable(this);
        }
    }

    private static class ResetRunnable implements Runnable {
        MonitorInfo mMonitorInfo;
        FloatProperty mProperty;
        WeakReference<IAnimTarget> mTargetRef;

        ResetRunnable(MonitorInfo monitorInfo) {
            this.mMonitorInfo = monitorInfo;
        }

        void post(IAnimTarget iAnimTarget, FloatProperty floatProperty) {
            iAnimTarget.removeTask(this);
            WeakReference<IAnimTarget> weakReference = this.mTargetRef;
            if (weakReference == null || weakReference.get() != iAnimTarget) {
                this.mTargetRef = new WeakReference<>(iAnimTarget);
            }
            this.mProperty = floatProperty;
            iAnimTarget.postDelayed(this, 600L);
        }

        @Override // java.lang.Runnable
        public void run() {
            IAnimTarget iAnimTarget = this.mTargetRef.get();
            if (iAnimTarget != null) {
                if (!iAnimTarget.isAnimRunning(this.mProperty)) {
                    iAnimTarget.setVelocity(this.mProperty, 0.0d);
                }
                this.mMonitorInfo.monitor.clear();
            }
        }
    }

    public void trackVelocity(IAnimTarget iAnimTarget, FloatProperty floatProperty, double d) {
        MonitorInfo monitor = getMonitor(floatProperty);
        monitor.monitor.update(d);
        float velocity = monitor.monitor.getVelocity(0);
        if (velocity != 0.0f) {
            monitor.resetTask.post(iAnimTarget, floatProperty);
            iAnimTarget.setVelocity(floatProperty, velocity);
        }
    }

    private MonitorInfo getMonitor(FloatProperty floatProperty) {
        MonitorInfo monitorInfo = this.mMonitors.get(floatProperty);
        if (monitorInfo != null) {
            return monitorInfo;
        }
        MonitorInfo monitorInfo2 = new MonitorInfo();
        this.mMonitors.put(floatProperty, monitorInfo2);
        return monitorInfo2;
    }
}
