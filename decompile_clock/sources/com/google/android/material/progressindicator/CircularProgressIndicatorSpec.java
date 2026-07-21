package com.google.android.material.progressindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.google.android.material.R;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.resources.MaterialResources;

/* JADX INFO: loaded from: classes2.dex */
public final class CircularProgressIndicatorSpec extends BaseProgressIndicatorSpec {
    public int indicatorDirection;
    public int indicatorInset;
    public int indicatorSize;

    public CircularProgressIndicatorSpec(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.circularProgressIndicatorStyle);
    }

    public CircularProgressIndicatorSpec(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, CircularProgressIndicator.DEF_STYLE_RES);
    }

    public CircularProgressIndicatorSpec(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.mtrl_progress_circular_size_medium);
        int dimensionPixelSize2 = context.getResources().getDimensionPixelSize(R.dimen.mtrl_progress_circular_inset_medium);
        TypedArray typedArrayObtainStyledAttributes = ThemeEnforcement.obtainStyledAttributes(context, attributeSet, R.styleable.CircularProgressIndicator, i, i2, new int[0]);
        this.indicatorSize = Math.max(MaterialResources.getDimensionPixelSize(context, typedArrayObtainStyledAttributes, R.styleable.CircularProgressIndicator_indicatorSize, dimensionPixelSize), this.trackThickness * 2);
        this.indicatorInset = MaterialResources.getDimensionPixelSize(context, typedArrayObtainStyledAttributes, R.styleable.CircularProgressIndicator_indicatorInset, dimensionPixelSize2);
        this.indicatorDirection = typedArrayObtainStyledAttributes.getInt(R.styleable.CircularProgressIndicator_indicatorDirectionCircular, 0);
        typedArrayObtainStyledAttributes.recycle();
        validateSpec();
    }

    int getIndicatorTrackGapSizeDegree() {
        if (this.indicatorTrackGapSize == 0) {
            return 0;
        }
        return (int) Math.round(360.0d / ((((double) ((this.indicatorSize - (this.indicatorInset * 2)) - this.trackThickness)) * 3.141592653589793d) / ((double) (this.indicatorTrackGapSize + this.trackCornerRadius))));
    }
}
