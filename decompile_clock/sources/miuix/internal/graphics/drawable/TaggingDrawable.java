package miuix.internal.graphics.drawable;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class TaggingDrawable extends DrawableWrapperCompat {
    private int[] mRawState;
    private int[] mTaggingState;

    public TaggingDrawable(Drawable drawable) {
        this(drawable, drawable.getState());
    }

    public TaggingDrawable(Drawable drawable, int[] iArr) {
        super(drawable);
        this.mTaggingState = new int[0];
        this.mRawState = new int[0];
        setTaggingState(iArr);
    }

    public boolean setTaggingState(int[] iArr) {
        if (Arrays.equals(iArr, this.mTaggingState)) {
            return false;
        }
        this.mTaggingState = iArr;
        return super.setState(mergeTaggingState(iArr, this.mRawState));
    }

    @Override // miuix.internal.graphics.drawable.DrawableWrapperCompat, android.graphics.drawable.Drawable
    public boolean setState(int[] iArr) {
        if (Arrays.equals(iArr, this.mRawState)) {
            return false;
        }
        this.mRawState = iArr;
        return super.setState(mergeTaggingState(this.mTaggingState, iArr));
    }

    private static int[] mergeTaggingState(int[] iArr, int[] iArr2) {
        int[] iArr3 = new int[iArr2.length + iArr.length];
        System.arraycopy(iArr, 0, iArr3, 0, iArr.length);
        System.arraycopy(iArr2, 0, iArr3, iArr.length, iArr2.length);
        return iArr3;
    }

    public static int[] mergeSortTaggingState(int[] iArr, int[] iArr2) {
        int[] iArr3 = new int[iArr2.length + iArr.length];
        System.arraycopy(iArr, 0, iArr3, 0, iArr.length);
        System.arraycopy(iArr2, 0, iArr3, iArr.length, iArr2.length);
        Arrays.sort(iArr3);
        return iArr3;
    }

    public static boolean containsTagState(StateListDrawable stateListDrawable, int[] iArr) {
        int stateCount = StateListDrawableReflect.getStateCount(stateListDrawable);
        for (int i = 0; i < stateCount; i++) {
            for (int i2 : StateListDrawableReflect.getStateSet(stateListDrawable, i)) {
                if (Arrays.binarySearch(iArr, i2) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
