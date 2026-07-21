package miuix.androidbasewidget.widget;

import android.view.View;
import androidx.viewpager2.widget.OriginalViewPager2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class CompositePageTransformer implements OriginalViewPager2.PageTransformer {
    private final List<OriginalViewPager2.PageTransformer> mTransformers = new ArrayList();

    public void addTransformer(OriginalViewPager2.PageTransformer pageTransformer) {
        this.mTransformers.add(pageTransformer);
    }

    public void removeTransformer(OriginalViewPager2.PageTransformer pageTransformer) {
        this.mTransformers.remove(pageTransformer);
    }

    @Override // androidx.viewpager2.widget.OriginalViewPager2.PageTransformer
    public void transformPage(View view, float f) {
        Iterator<OriginalViewPager2.PageTransformer> it = this.mTransformers.iterator();
        while (it.hasNext()) {
            it.next().transformPage(view, f);
        }
    }
}
