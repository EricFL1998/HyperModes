package miuix.animation.utils;

import android.os.SystemClock;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class VelocityMonitor {
    private static final long MAX_DELTA = 100;
    private static final int MAX_RECORD_COUNT = 10;
    private static final long MIN_DELTA = 30;
    private static final long TIME_THRESHOLD = 50;
    private float[] mVelocity;
    private Long mMinDeltaTime = Long.valueOf(MIN_DELTA);
    private Long mMaxDeltaTime = 100L;
    private MoveRecord[] mHistory = new MoveRecord[10];
    private int mWriteIndex = 0;
    private int mReadIndex = 0;
    private int mSize = 0;

    private float getVelocity(double d, double d2, long j) {
        return (float) (j == 0 ? 0.0d : (d - d2) / ((double) (j / 1000.0f)));
    }

    private static class MoveRecord {
        long timeStamp;
        double[] values;

        private MoveRecord() {
        }
    }

    public void setMinFeedbackTime(long j) {
        this.mMinDeltaTime = Long.valueOf(j);
    }

    public void setMaxFeedbackTime(long j) {
        this.mMaxDeltaTime = Long.valueOf(j);
    }

    public void update(float... fArr) {
        if (fArr == null || fArr.length == 0) {
            return;
        }
        MoveRecord moveRecord = getMoveRecord();
        moveRecord.values = new double[fArr.length];
        for (int i = 0; i < fArr.length; i++) {
            moveRecord.values[i] = fArr[i];
        }
        addAndUpdate(moveRecord);
    }

    public void update(double... dArr) {
        if (dArr == null || dArr.length == 0) {
            return;
        }
        MoveRecord moveRecord = getMoveRecord();
        moveRecord.values = dArr;
        addAndUpdate(moveRecord);
    }

    private MoveRecord getMoveRecord() {
        MoveRecord moveRecord = new MoveRecord();
        moveRecord.timeStamp = SystemClock.uptimeMillis();
        return moveRecord;
    }

    private void addAndUpdate(MoveRecord moveRecord) {
        MoveRecord[] moveRecordArr = this.mHistory;
        int i = this.mWriteIndex;
        moveRecordArr[i] = moveRecord;
        int i2 = (i + 1) % 10;
        this.mWriteIndex = i2;
        int i3 = this.mSize;
        if (i3 < 10) {
            this.mSize = i3 + 1;
        } else {
            this.mReadIndex = i2;
        }
        updateVelocity();
    }

    public float getVelocity(int i) {
        float[] fArr;
        long jUptimeMillis = SystemClock.uptimeMillis();
        if (this.mSize == 0 || Math.abs(jUptimeMillis - this.mHistory[(this.mWriteIndex + 9) % 10].timeStamp) > TIME_THRESHOLD || (fArr = this.mVelocity) == null || fArr.length <= i) {
            return 0.0f;
        }
        return fArr[i];
    }

    public void clear() {
        this.mWriteIndex = 0;
        this.mReadIndex = 0;
        this.mSize = 0;
        this.mHistory = new MoveRecord[10];
        clearVelocity();
    }

    private void clearVelocity() {
        float[] fArr = this.mVelocity;
        if (fArr != null) {
            Arrays.fill(fArr, 0.0f);
        }
    }

    private void updateVelocity() {
        if (this.mSize >= 2) {
            MoveRecord[] moveRecordArr = this.mHistory;
            int i = this.mWriteIndex;
            MoveRecord moveRecord = moveRecordArr[(i + 9) % 10];
            MoveRecord moveRecord2 = moveRecordArr[(i + 8) % 10];
            float[] fArr = this.mVelocity;
            if (fArr == null || fArr.length < moveRecord.values.length) {
                this.mVelocity = new float[moveRecord.values.length];
            }
            for (int i2 = 0; i2 < moveRecord.values.length; i2++) {
                this.mVelocity[i2] = calVelocity(i2, moveRecord, moveRecord2);
            }
            return;
        }
        clearVelocity();
    }

    private float calVelocity(int i, MoveRecord moveRecord, MoveRecord moveRecord2) {
        float velocity;
        double d = moveRecord.values[i];
        long j = moveRecord.timeStamp;
        double velocity2 = getVelocity(d, moveRecord2.values[i], j - moveRecord2.timeStamp);
        int i2 = (this.mWriteIndex + 8) % 10;
        while (true) {
            if (i2 == this.mReadIndex) {
                velocity = Float.MAX_VALUE;
                break;
            }
            MoveRecord moveRecord3 = this.mHistory[i2];
            long j2 = j - moveRecord3.timeStamp;
            if (j2 > this.mMinDeltaTime.longValue() && j2 < this.mMaxDeltaTime.longValue()) {
                velocity = getVelocity(d, moveRecord3.values[i], j2);
                double d2 = velocity;
                if (velocity2 * d2 <= 0.0d) {
                    break;
                }
                velocity = (float) (velocity > 0.0f ? Math.max(velocity2, d2) : Math.min(velocity2, d2));
                break;
            }
            i2 = (i2 + 9) % 10;
        }
        if (velocity == Float.MAX_VALUE && i2 != this.mReadIndex) {
            int i3 = (this.mWriteIndex + 8) % 10;
            long j3 = j - this.mHistory[i3].timeStamp;
            if (j3 > this.mMinDeltaTime.longValue() && j3 < this.mMaxDeltaTime.longValue()) {
                velocity = getVelocity(d, this.mHistory[i3].values[i], j3);
            }
        }
        if (velocity == Float.MAX_VALUE) {
            return 0.0f;
        }
        return velocity;
    }
}
