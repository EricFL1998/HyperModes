package androidx.transition;

import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
class VelocityTracker1D {
    private static final int ASSUME_POINTER_MOVE_STOPPED_MILLIS = 40;
    private static final int HISTORY_SIZE = 20;
    private static final int HORIZON_MILLIS = 100;
    private float[] mDataSamples = new float[20];
    private int mIndex = 0;
    private long[] mTimeSamples;

    VelocityTracker1D() {
        long[] jArr = new long[20];
        this.mTimeSamples = jArr;
        Arrays.fill(jArr, Long.MIN_VALUE);
    }

    public void addDataPoint(long j, float f) {
        int i = (this.mIndex + 1) % 20;
        this.mIndex = i;
        this.mTimeSamples[i] = j;
        this.mDataSamples[i] = f;
    }

    public void resetTracking() {
        this.mIndex = 0;
        Arrays.fill(this.mTimeSamples, Long.MIN_VALUE);
        Arrays.fill(this.mDataSamples, 0.0f);
    }

    float calculateVelocity() {
        float fKineticEnergyToVelocity;
        int i = this.mIndex;
        if (i == 0 && this.mTimeSamples[i] == Long.MIN_VALUE) {
            return 0.0f;
        }
        long j = this.mTimeSamples[i];
        int i2 = 0;
        long j2 = j;
        while (true) {
            long j3 = this.mTimeSamples[i];
            if (j3 == Long.MIN_VALUE) {
                break;
            }
            float f = j - j3;
            float fAbs = Math.abs(j3 - j2);
            if (f > 100.0f || fAbs > 40.0f) {
                break;
            }
            if (i == 0) {
                i = 20;
            }
            i--;
            i2++;
            if (i2 >= 20) {
                break;
            }
            j2 = j3;
        }
        if (i2 < 2) {
            return 0.0f;
        }
        if (i2 == 2) {
            int i3 = this.mIndex;
            int i4 = i3 == 0 ? 19 : i3 - 1;
            long[] jArr = this.mTimeSamples;
            float f2 = jArr[i3] - jArr[i4];
            if (f2 == 0.0f) {
                return 0.0f;
            }
            float[] fArr = this.mDataSamples;
            fKineticEnergyToVelocity = (fArr[i3] - fArr[i4]) / f2;
        } else {
            int i5 = this.mIndex;
            int i6 = ((i5 - i2) + 21) % 20;
            int i7 = (i5 + 21) % 20;
            long j4 = this.mTimeSamples[i6];
            float f3 = this.mDataSamples[i6];
            int i8 = i6 + 1;
            float fKineticEnergyToVelocity2 = 0.0f;
            for (int i9 = i8 % 20; i9 != i7; i9 = (i9 + 1) % 20) {
                long j5 = this.mTimeSamples[i9];
                float f4 = j5 - j4;
                if (f4 != 0.0f) {
                    float f5 = this.mDataSamples[i9];
                    float f6 = (f5 - f3) / f4;
                    fKineticEnergyToVelocity2 += (f6 - kineticEnergyToVelocity(fKineticEnergyToVelocity2)) * Math.abs(f6);
                    if (i9 == i8) {
                        fKineticEnergyToVelocity2 *= 0.5f;
                    }
                    f3 = f5;
                    j4 = j5;
                }
            }
            fKineticEnergyToVelocity = kineticEnergyToVelocity(fKineticEnergyToVelocity2);
        }
        return fKineticEnergyToVelocity * 1000.0f;
    }

    private float kineticEnergyToVelocity(float f) {
        return (float) (((double) Math.signum(f)) * Math.sqrt(Math.abs(f) * 2.0f));
    }
}
