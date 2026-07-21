package com.google.android.material.carousel;

import android.view.View;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes2.dex */
public final class MultiBrowseCarouselStrategy extends CarouselStrategy {
    private int keylineCount = 0;
    private static final int[] SMALL_COUNTS = {1};
    private static final int[] MEDIUM_COUNTS = {1, 0};

    @Override // com.google.android.material.carousel.CarouselStrategy
    KeylineState onFirstChildMeasuredWithMargins(Carousel carousel, View view) {
        float containerHeight = carousel.getContainerHeight();
        if (carousel.isHorizontal()) {
            containerHeight = carousel.getContainerWidth();
        }
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        float f = layoutParams.topMargin + layoutParams.bottomMargin;
        float measuredHeight = view.getMeasuredHeight();
        if (carousel.isHorizontal()) {
            f = layoutParams.leftMargin + layoutParams.rightMargin;
            measuredHeight = view.getMeasuredWidth();
        }
        float f2 = f;
        float smallItemSizeMin = getSmallItemSizeMin() + f2;
        float fMax = Math.max(getSmallItemSizeMax() + f2, smallItemSizeMin);
        float fMin = Math.min(measuredHeight + f2, containerHeight);
        float fClamp = MathUtils.clamp((measuredHeight / 3.0f) + f2, smallItemSizeMin + f2, fMax + f2);
        float f3 = (fMin + fClamp) / 2.0f;
        int[] iArrDoubleCounts = SMALL_COUNTS;
        if (containerHeight < 2.0f * smallItemSizeMin) {
            iArrDoubleCounts = new int[]{0};
        }
        int[] iArrDoubleCounts2 = MEDIUM_COUNTS;
        if (carousel.getCarouselAlignment() == 1) {
            iArrDoubleCounts = doubleCounts(iArrDoubleCounts);
            iArrDoubleCounts2 = doubleCounts(iArrDoubleCounts2);
        }
        int[] iArr = iArrDoubleCounts;
        int[] iArr2 = iArrDoubleCounts2;
        int iMax = (int) Math.max(1.0d, Math.floor(((containerHeight - (CarouselStrategyHelper.maxValue(iArr2) * f3)) - (CarouselStrategyHelper.maxValue(iArr) * fMax)) / fMin));
        int iCeil = (int) Math.ceil(containerHeight / fMin);
        int i = (iCeil - iMax) + 1;
        int[] iArr3 = new int[i];
        for (int i2 = 0; i2 < i; i2++) {
            iArr3[i2] = iCeil - i2;
        }
        Arrangement arrangementFindLowestCostArrangement = Arrangement.findLowestCostArrangement(containerHeight, fClamp, smallItemSizeMin, fMax, iArr, f3, iArr2, fMin, iArr3);
        this.keylineCount = arrangementFindLowestCostArrangement.getItemCount();
        if (ensureArrangementFitsItemCount(arrangementFindLowestCostArrangement, carousel.getItemCount())) {
            arrangementFindLowestCostArrangement = Arrangement.findLowestCostArrangement(containerHeight, fClamp, smallItemSizeMin, fMax, new int[]{arrangementFindLowestCostArrangement.smallCount}, f3, new int[]{arrangementFindLowestCostArrangement.mediumCount}, fMin, new int[]{arrangementFindLowestCostArrangement.largeCount});
        }
        return CarouselStrategyHelper.createKeylineState(view.getContext(), f2, containerHeight, arrangementFindLowestCostArrangement, carousel.getCarouselAlignment());
    }

    boolean ensureArrangementFitsItemCount(Arrangement arrangement, int i) {
        int itemCount = arrangement.getItemCount() - i;
        boolean z = itemCount > 0 && (arrangement.smallCount > 0 || arrangement.mediumCount > 1);
        while (itemCount > 0) {
            if (arrangement.smallCount > 0) {
                arrangement.smallCount--;
            } else if (arrangement.mediumCount > 1) {
                arrangement.mediumCount--;
            }
            itemCount--;
        }
        return z;
    }

    @Override // com.google.android.material.carousel.CarouselStrategy
    boolean shouldRefreshKeylineState(Carousel carousel, int i) {
        return (i < this.keylineCount && carousel.getItemCount() >= this.keylineCount) || (i >= this.keylineCount && carousel.getItemCount() < this.keylineCount);
    }
}
