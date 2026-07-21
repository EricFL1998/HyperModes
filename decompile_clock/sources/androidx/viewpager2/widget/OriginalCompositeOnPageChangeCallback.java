package androidx.viewpager2.widget;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
final class OriginalCompositeOnPageChangeCallback extends OriginalViewPager2.OnPageChangeCallback {
    private final List<OriginalViewPager2.OnPageChangeCallback> mCallbacks;

    OriginalCompositeOnPageChangeCallback(int i) {
        this.mCallbacks = new ArrayList(i);
    }

    void addOnPageChangeCallback(OriginalViewPager2.OnPageChangeCallback onPageChangeCallback) {
        this.mCallbacks.add(onPageChangeCallback);
    }

    void removeOnPageChangeCallback(OriginalViewPager2.OnPageChangeCallback onPageChangeCallback) {
        this.mCallbacks.remove(onPageChangeCallback);
    }

    @Override // androidx.viewpager2.widget.OriginalViewPager2.OnPageChangeCallback
    public void onPageScrolled(int i, float f, int i2) {
        try {
            Iterator<OriginalViewPager2.OnPageChangeCallback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                it.next().onPageScrolled(i, f, i2);
            }
        } catch (ConcurrentModificationException e) {
            throwCallbackListModifiedWhileInUse(e);
        }
    }

    @Override // androidx.viewpager2.widget.OriginalViewPager2.OnPageChangeCallback
    public void onPageSelected(int i) {
        try {
            Iterator<OriginalViewPager2.OnPageChangeCallback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                it.next().onPageSelected(i);
            }
        } catch (ConcurrentModificationException e) {
            throwCallbackListModifiedWhileInUse(e);
        }
    }

    @Override // androidx.viewpager2.widget.OriginalViewPager2.OnPageChangeCallback
    public void onPageScrollStateChanged(int i) {
        try {
            Iterator<OriginalViewPager2.OnPageChangeCallback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                it.next().onPageScrollStateChanged(i);
            }
        } catch (ConcurrentModificationException e) {
            throwCallbackListModifiedWhileInUse(e);
        }
    }

    private void throwCallbackListModifiedWhileInUse(ConcurrentModificationException concurrentModificationException) {
        throw new IllegalStateException("Adding and removing callbacks during dispatch to callbacks is not supported", concurrentModificationException);
    }
}
