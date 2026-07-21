package miuix.viewpager.widget;

import android.util.Pair;
import android.view.View;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public class SharedElementTransformer implements OnPageChangeListener {
    private PagerAdapter mPagerAdapter;
    private Set<Pair<Integer, Integer>> mSharedElementIds = new HashSet();

    @Override // miuix.viewpager.widget.OnPageChangeListener
    public void onPageScrollStateChanged(int i) {
    }

    @Override // miuix.viewpager.widget.OnPageChangeListener
    public void onPageSelected(int i) {
    }

    public SharedElementTransformer(PagerAdapter pagerAdapter) {
        this.mPagerAdapter = pagerAdapter;
    }

    @Override // miuix.viewpager.widget.OnPageChangeListener
    public void onPageScrolled(int i, float f, int i2) {
        View viewFindViewById;
        View viewFindViewById2;
        int i3 = i + 1;
        PagerAdapter pagerAdapter = this.mPagerAdapter;
        if (pagerAdapter == null) {
            return;
        }
        if (i3 >= pagerAdapter.getCount()) {
            resetView(i);
            return;
        }
        for (Pair<Integer, Integer> pair : this.mSharedElementIds) {
            Integer num = (Integer) pair.first;
            Integer num2 = (Integer) pair.second;
            View view = this.mPagerAdapter.getView(i);
            View view2 = this.mPagerAdapter.getView(i3);
            if (view == null || view2 == null) {
                viewFindViewById = null;
                viewFindViewById2 = null;
            } else {
                viewFindViewById2 = view.findViewById(num.intValue());
                viewFindViewById = view2.findViewById(num2.intValue());
                if (viewFindViewById2 == null) {
                    viewFindViewById2 = null;
                }
                if (viewFindViewById == null) {
                    viewFindViewById = null;
                }
            }
            if (viewFindViewById2 != null && viewFindViewById != null) {
                View view3 = viewFindViewById2;
                View view4 = viewFindViewById;
                modifyPositions(view3, view4, view, view2, view, -f);
                modifyPositions(view3, view4, view, view2, view2, 1.0f - f);
            }
        }
    }

    public void addElement(int i, int i2) {
        this.mSharedElementIds.add(new Pair<>(Integer.valueOf(i), Integer.valueOf(i2)));
    }

    public void removeElement(int i, int i2) {
        this.mSharedElementIds.remove(new Pair(Integer.valueOf(i), Integer.valueOf(i2)));
    }

    public void clearElements() {
        this.mSharedElementIds.clear();
    }

    private void modifyPositions(View view, View view2, View view3, View view4, View view5, float f) {
        float left = view2.getLeft() - view.getLeft();
        float top = view2.getTop() - view.getTop();
        float left2 = view4.getLeft() - view3.getLeft();
        float width = view.getWidth();
        float height = view.getHeight();
        float width2 = view2.getWidth();
        float height2 = view2.getHeight();
        float f2 = width2 - width;
        float f3 = height2 - height;
        int id = view.getId();
        int id2 = view2.getId();
        if (-1.0f < f) {
            if (f < 1.0f) {
                float f4 = -f;
                float f5 = (top + (f3 / 2.0f)) * f4;
                float f6 = (left + left2 + (f2 / 2.0f)) * f4;
                if (view5.findViewById(id) != null) {
                    view.setTranslationX(f6);
                    view.setTranslationY(f5);
                    float f7 = width == 0.0f ? 1.0f : ((f2 * f4) + width) / width;
                    float f8 = height == 0.0f ? 1.0f : ((f3 * f4) + height) / height;
                    view.setScaleX(f7);
                    view.setScaleY(f8);
                }
                if (view5.findViewById(id2) != null) {
                    view2.setTranslationX(f6);
                    view2.setTranslationY(f5);
                    float f9 = width2 == 0.0f ? 1.0f : ((f2 * f4) + width2) / width2;
                    float f10 = height2 != 0.0f ? ((f3 * f4) + height2) / height2 : 1.0f;
                    view2.setScaleX(f9);
                    view2.setScaleY(f10);
                }
            }
        }
    }

    private void resetView(int i) {
        View view;
        PagerAdapter pagerAdapter = this.mPagerAdapter;
        if (pagerAdapter == null || (view = pagerAdapter.getView(i)) == null) {
            return;
        }
        for (Pair<Integer, Integer> pair : this.mSharedElementIds) {
            View viewFindViewById = view.findViewById(((Integer) pair.first).intValue());
            View viewFindViewById2 = view.findViewById(((Integer) pair.second).intValue());
            if (viewFindViewById != null) {
                viewFindViewById.setTranslationX(0.0f);
                viewFindViewById.setTranslationY(0.0f);
                viewFindViewById.setScaleX(1.0f);
                viewFindViewById.setScaleY(1.0f);
            }
            if (viewFindViewById2 != null) {
                viewFindViewById2.setTranslationX(0.0f);
                viewFindViewById2.setTranslationY(0.0f);
                viewFindViewById2.setScaleX(1.0f);
                viewFindViewById2.setScaleY(1.0f);
            }
        }
    }
}
