package miuix.animation.internal;

/* JADX INFO: loaded from: classes2.dex */
public class AnimInfo {
    public long delay;
    public double frameInterval;
    public long initTime;
    public boolean justEnd;
    public volatile byte op;
    public double progress;
    public long startTime;
    public int tintMode = -1;
    public double startValue = Double.MAX_VALUE;
    public double targetValue = Double.MAX_VALUE;
    public double value = Double.MAX_VALUE;
    public double setToValue = Double.MAX_VALUE;
    public double duration = 0.0d;

    public void clear() {
        this.op = (byte) 0;
        this.delay = 0L;
        this.initTime = 0L;
        this.startTime = 0L;
        this.progress = 0.0d;
        this.tintMode = -1;
        this.startValue = Double.MAX_VALUE;
        this.targetValue = Double.MAX_VALUE;
        this.value = Double.MAX_VALUE;
        this.setToValue = Double.MAX_VALUE;
        this.justEnd = false;
        this.frameInterval = 0.0d;
        this.duration = 0.0d;
    }

    public void reuse() {
        this.op = (byte) 6;
        this.delay = 0L;
        this.initTime = 0L;
        this.startTime = 0L;
        this.progress = 0.0d;
        this.tintMode = -1;
        this.startValue = Double.MAX_VALUE;
        this.targetValue = Double.MAX_VALUE;
        this.justEnd = false;
        this.frameInterval = 0.0d;
        this.duration = 0.0d;
    }

    public String toString() {
        return "AnimInfo{op=" + ((int) this.op) + ", delay=" + this.delay + ", v=" + this.value + ", start-v=" + this.startValue + ", target-v=" + this.targetValue + ", setTo-v=" + this.setToValue + ", duration=" + this.duration + ", init-t=" + this.initTime + ", start-t=" + this.startTime + ", progress=" + this.progress + ", config=" + this.tintMode + ", frameInterval=" + this.frameInterval + '}';
    }
}
