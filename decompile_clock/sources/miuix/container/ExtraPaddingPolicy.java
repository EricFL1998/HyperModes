package miuix.container;

import android.util.Log;
import android.view.View;
import com.android.deskclock.R2;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ExtraPaddingPolicy {
    public static boolean DEBUGGABLE = true;
    public static final int EXTRA_PADDING_DEFAULT = 0;
    private boolean mEnable;
    private int mLevel;
    private int[] mPaddingsDp;
    private int[] mPaddingsDpInMultiColumns;
    private int mPaddingHorizontalCommonDp = 0;
    private int mLastWindowWidthDp = 0;
    private int mLastWindowHeightDp = 0;
    private int mLastContainerWidth = 0;
    private int mLastContainerHeight = 0;
    private boolean mIsFullWindow = true;
    private int[] mLevelThresholds = null;
    private int[] mLevelThresholdsInMultiColumns = null;
    private int mWidthLimitedThreshold = 0;
    private int mWidthLimitedExtraPaddingDp = 0;

    ExtraPaddingPolicy() {
    }

    public void applyExtraPadding(View view) {
        int i;
        int i2;
        if (this.mEnable) {
            int left = view.getLeft();
            int top = view.getTop();
            int right = view.getRight();
            int bottom = view.getBottom();
            int extraPaddingDp = (int) (getExtraPaddingDp() * (view.getResources().getConfiguration().densityDpi / 160.0f));
            if (ViewUtils.isLayoutRtl(view)) {
                i = left - extraPaddingDp;
                i2 = right - extraPaddingDp;
            } else {
                i = left + extraPaddingDp;
                i2 = right + extraPaddingDp;
            }
            view.layout(i, top, i2, bottom);
        }
    }

    public void setEnable(boolean z) {
        this.mEnable = z;
    }

    public boolean isEnable() {
        return this.mEnable;
    }

    public int getExtraPaddingDp() {
        return getExtraPaddingDp(true);
    }

    public int getExtraPaddingDp(boolean z) {
        int i;
        int i2;
        int[] iArr;
        if (this.mIsFullWindow || (iArr = this.mPaddingsDpInMultiColumns) == null) {
            i = this.mPaddingsDp[this.mLevel];
        } else {
            i = iArr[this.mLevel];
        }
        if (i == 0) {
            return i;
        }
        if (z) {
            i2 = this.mWidthLimitedExtraPaddingDp;
        } else {
            i += this.mPaddingHorizontalCommonDp;
            i2 = this.mWidthLimitedExtraPaddingDp;
        }
        return i + i2;
    }

    public int getLevel() {
        return this.mLevel;
    }

    public int getWidthLimitedThreshold() {
        return this.mWidthLimitedThreshold;
    }

    public void onContainerSizeChanged(int i, int i2, int i3, int i4, float f, boolean z) {
        if (this.mLastContainerWidth == i3 && this.mLastWindowWidthDp == i) {
            return;
        }
        if (DEBUGGABLE) {
            Log.d("ExtraPaddingPolicy", "onContainerSizeChanged new Win w = " + i + " h = " + i2 + " new C w = " + i3 + " h = " + i4);
            Log.d("ExtraPaddingPolicy", "onContainerSizeChanged old Win w = " + this.mLastWindowWidthDp + " h = " + this.mLastWindowHeightDp + " old C w = " + this.mLastContainerWidth + " h = " + this.mLastContainerHeight);
            Log.d("ExtraPaddingPolicy", "onContainerSizeChanged density " + f + " isInFloatingWindow = " + z);
        }
        this.mLastWindowWidthDp = i;
        this.mLastWindowHeightDp = i2;
        this.mLastContainerWidth = i3;
        this.mLastContainerHeight = i4;
        this.mIsFullWindow = (((float) i3) * 1.0f) / (((float) i) * f) >= 0.95f || z;
        if (DEBUGGABLE) {
            Log.d("ExtraPaddingPolicy", "onContainerSizeChanged isFullWindow " + this.mIsFullWindow);
        }
        if (this.mLastWindowHeightDp <= 550) {
            this.mLevel = 0;
            return;
        }
        if (!this.mIsFullWindow && this.mLevelThresholdsInMultiColumns != null) {
            int i5 = 0;
            while (true) {
                int[] iArr = this.mLevelThresholdsInMultiColumns;
                if (i5 >= iArr.length) {
                    break;
                }
                int i6 = (int) (iArr[i5] * f);
                if (i5 == 0 && i3 < i6) {
                    this.mLevel = i5;
                    break;
                } else if (i3 <= i6) {
                    this.mLevel = i5;
                    break;
                } else {
                    if (i5 == iArr.length - 1) {
                        this.mLevel = i5 + 1;
                    }
                    i5++;
                }
            }
        } else {
            int i7 = 0;
            while (true) {
                int[] iArr2 = this.mLevelThresholds;
                if (i7 >= iArr2.length) {
                    break;
                }
                int i8 = (int) (iArr2[i7] * f);
                if (i7 == 0 && i3 < i8) {
                    this.mLevel = i7;
                    break;
                } else if (i3 <= i8) {
                    this.mLevel = i7;
                    break;
                } else {
                    if (i7 == iArr2.length - 1) {
                        this.mLevel = i7 + 1;
                    }
                    i7++;
                }
            }
        }
        int i9 = this.mWidthLimitedThreshold;
        if (i9 > 0) {
            float f2 = (i3 / f) + 0.5f;
            if (f2 > i9) {
                this.mWidthLimitedExtraPaddingDp = (int) ((f2 - i9) / 2.0f);
                return;
            }
        }
        this.mWidthLimitedExtraPaddingDp = 0;
    }

    public static class Builder {
        ExtraPaddingPolicy mPolicy = new ExtraPaddingPolicy();

        public Builder setPaddingHorizontalCommonDp(int i) {
            this.mPolicy.mPaddingHorizontalCommonDp = i;
            return this;
        }

        public Builder setThresholds(int... iArr) {
            this.mPolicy.mLevelThresholds = iArr;
            return this;
        }

        public Builder setPaddingsDp(int... iArr) {
            this.mPolicy.mPaddingsDp = iArr;
            return this;
        }

        public Builder setThresholdsInMultiColumns(int... iArr) {
            this.mPolicy.mLevelThresholdsInMultiColumns = iArr;
            return this;
        }

        public Builder setPaddingsDpInMultiColumns(int... iArr) {
            this.mPolicy.mPaddingsDpInMultiColumns = iArr;
            return this;
        }

        public Builder setWidthLimitedThreshold(int i) {
            this.mPolicy.mWidthLimitedThreshold = i;
            return this;
        }

        public ExtraPaddingPolicy create() {
            return this.mPolicy;
        }

        public static ExtraPaddingPolicy createDefault(int i, int i2, int i3) {
            if (i == 2 || i == 3) {
                return new Builder().setPaddingHorizontalCommonDp(i3).setThresholds(420, 640, 800).setPaddingsDp(0, i2 * 2, i2 * 4, i2 * 11).setWidthLimitedThreshold(R2.attr.format_second).create();
            }
            return null;
        }

        public static ExtraPaddingPolicy createLegacyDefault(int i, int i2, int i3) {
            if (i == 2) {
                return new Builder().setPaddingHorizontalCommonDp(i3).setThresholds(640, 960).setPaddingsDp(0, i2 * 9, i2 * 25).setThresholdsInMultiColumns(640).setPaddingsDpInMultiColumns(0, i2 * 11).create();
            }
            if (i == 3) {
                return new Builder().setThresholds(640).setPaddingsDp(0, i2 * 7).create();
            }
            return null;
        }
    }
}
