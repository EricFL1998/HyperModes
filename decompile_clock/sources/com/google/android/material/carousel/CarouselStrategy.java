package com.google.android.material.carousel;

import android.content.Context;
import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public abstract class CarouselStrategy {
    private float smallSizeMax;
    private float smallSizeMin;

    static float getChildMaskPercentage(float f, float f2, float f3) {
        return 1.0f - ((f - f3) / (f2 - f3));
    }

    boolean isContained() {
        return true;
    }

    abstract KeylineState onFirstChildMeasuredWithMargins(Carousel carousel, View view);

    boolean shouldRefreshKeylineState(Carousel carousel, int i) {
        return false;
    }

    void initialize(Context context) {
        float smallSizeMin = this.smallSizeMin;
        if (smallSizeMin <= 0.0f) {
            smallSizeMin = CarouselStrategyHelper.getSmallSizeMin(context);
        }
        this.smallSizeMin = smallSizeMin;
        float smallSizeMax = this.smallSizeMax;
        if (smallSizeMax <= 0.0f) {
            smallSizeMax = CarouselStrategyHelper.getSmallSizeMax(context);
        }
        this.smallSizeMax = smallSizeMax;
    }

    static int[] doubleCounts(int[] iArr) {
        int length = iArr.length;
        int[] iArr2 = new int[length];
        for (int i = 0; i < length; i++) {
            iArr2[i] = iArr[i] * 2;
        }
        return iArr2;
    }

    public void setSmallItemSizeMin(float f) {
        this.smallSizeMin = f;
    }

    public void setSmallItemSizeMax(float f) {
        this.smallSizeMax = f;
    }

    public float getSmallItemSizeMin() {
        return this.smallSizeMin;
    }

    public float getSmallItemSizeMax() {
        return this.smallSizeMax;
    }
}
