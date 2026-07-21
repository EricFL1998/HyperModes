package androidx.viewpager2.widget;

import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
final class OriginalPageTransformerAdapter extends OriginalViewPager2.OnPageChangeCallback {
    private final LinearLayoutManager mLayoutManager;
    private OriginalViewPager2.PageTransformer mPageTransformer;

    @Override // androidx.viewpager2.widget.OriginalViewPager2.OnPageChangeCallback
    public void onPageScrollStateChanged(int i) {
    }

    @Override // androidx.viewpager2.widget.OriginalViewPager2.OnPageChangeCallback
    public void onPageSelected(int i) {
    }

    OriginalPageTransformerAdapter(LinearLayoutManager linearLayoutManager) {
        this.mLayoutManager = linearLayoutManager;
    }

    OriginalViewPager2.PageTransformer getPageTransformer() {
        return this.mPageTransformer;
    }

    void setPageTransformer(OriginalViewPager2.PageTransformer pageTransformer) {
        this.mPageTransformer = pageTransformer;
    }

    @Override // androidx.viewpager2.widget.OriginalViewPager2.OnPageChangeCallback
    public void onPageScrolled(int i, float f, int i2) {
        if (this.mPageTransformer == null) {
            return;
        }
        float f2 = -f;
        for (int i3 = 0; i3 < this.mLayoutManager.getChildCount(); i3++) {
            View childAt = this.mLayoutManager.getChildAt(i3);
            if (childAt == null) {
                throw new IllegalStateException(String.format(Locale.US, "LayoutManager returned a null child at pos %d/%d while transforming pages", Integer.valueOf(i3), Integer.valueOf(this.mLayoutManager.getChildCount())));
            }
            this.mPageTransformer.transformPage(childAt, (this.mLayoutManager.getPosition(childAt) - i) + f2);
        }
    }
}
