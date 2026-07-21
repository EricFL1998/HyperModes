package miuix.appcompat.internal.app.widget;

import android.R;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.appcompat.app.ActionBar;
import miuix.internal.util.ViewUtils;
import miuix.viewpager.widget.ViewPager;

/* JADX INFO: compiled from: ActionBarViewPagerController.java */
/* JADX INFO: loaded from: classes2.dex */
class ViewPagerScrollEffect implements ActionBar.FragmentViewPagerChangeListener {
    DynamicFragmentPagerAdapter mPagerAdapter;
    ViewPager mViewPager;
    Rect sRect = new Rect();
    ArrayList<View> sList = new ArrayList<>();
    int mBaseItem = -1;
    boolean mBaseItemUpdated = true;
    int mScrollBasePosition = -1;
    int mIncomingPosition = -1;
    ViewGroup mListView = null;

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageSelected(int i) {
    }

    public ViewPagerScrollEffect(ViewPager viewPager, DynamicFragmentPagerAdapter dynamicFragmentPagerAdapter) {
        this.mViewPager = viewPager;
        this.mPagerAdapter = dynamicFragmentPagerAdapter;
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
        if (f == 0.0f) {
            this.mBaseItem = i;
            this.mBaseItemUpdated = true;
            ViewGroup viewGroup = this.mListView;
            if (viewGroup != null) {
                clearTranslation(viewGroup);
            }
        }
        if (this.mScrollBasePosition != i) {
            int i2 = this.mBaseItem;
            if (i2 < i) {
                this.mBaseItem = i;
            } else {
                int i3 = i + 1;
                if (i2 > i3) {
                    this.mBaseItem = i3;
                }
            }
            this.mScrollBasePosition = i;
            this.mBaseItemUpdated = true;
            ViewGroup viewGroup2 = this.mListView;
            if (viewGroup2 != null) {
                clearTranslation(viewGroup2);
            }
        }
        if (f > 0.0f) {
            if (this.mBaseItemUpdated) {
                this.mBaseItemUpdated = false;
                if (this.mBaseItem == i && i < this.mPagerAdapter.getCount() - 1) {
                    this.mIncomingPosition = i + 1;
                } else {
                    this.mIncomingPosition = i;
                }
                Fragment fragment = this.mPagerAdapter.getFragment(this.mIncomingPosition, false);
                this.mListView = null;
                if (fragment != null && fragment.getView() != null) {
                    View viewFindViewById = fragment.getView().findViewById(R.id.list);
                    if (viewFindViewById instanceof ViewGroup) {
                        this.mListView = (ViewGroup) viewFindViewById;
                    }
                }
            }
            if (this.mIncomingPosition == i) {
                f = 1.0f - f;
            }
            float f2 = f;
            ViewGroup viewGroup3 = this.mListView;
            if (viewGroup3 != null) {
                translateView(viewGroup3, viewGroup3.getWidth(), this.mListView.getHeight(), f2, this.mIncomingPosition != i);
            }
        }
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrollStateChanged(int i) {
        if (i == 0) {
            this.mBaseItem = this.mViewPager.getCurrentItem();
            this.mBaseItemUpdated = true;
            ViewGroup viewGroup = this.mListView;
            if (viewGroup != null) {
                clearTranslation(viewGroup);
            }
        }
    }

    void fillList(ViewGroup viewGroup, ArrayList<View> arrayList) {
        clearTranslation(arrayList, viewGroup);
        arrayList.clear();
        ViewUtils.getContentRect(viewGroup, this.sRect);
        if (this.sRect.isEmpty()) {
            return;
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() != 8 || childAt.getHeight() > 0) {
                arrayList.add(childAt);
            }
        }
    }

    void clearTranslation(ArrayList<View> arrayList, ViewGroup viewGroup) {
        for (View view : arrayList) {
            if (viewGroup.indexOfChild(view) == -1 && view.getTranslationX() != 0.0f) {
                view.setTranslationX(0.0f);
            }
        }
    }

    void clearTranslation(ViewGroup viewGroup) {
        fillList(viewGroup, this.sList);
        if (this.sList.isEmpty()) {
            return;
        }
        Iterator<View> it = this.sList.iterator();
        while (it.hasNext()) {
            it.next().setTranslationX(0.0f);
        }
    }

    void translateView(ViewGroup viewGroup, int i, int i2, float f, boolean z) {
        fillList(viewGroup, this.sList);
        if (this.sList.isEmpty()) {
            return;
        }
        int i3 = 0;
        int top = this.sList.get(0).getTop();
        int i4 = Integer.MAX_VALUE;
        for (View view : this.sList) {
            if (i4 != view.getTop()) {
                int top2 = view.getTop();
                int iComputOffset = computOffset(top2 - top, i, i2, f);
                if (!z) {
                    iComputOffset = -iComputOffset;
                }
                int i5 = iComputOffset;
                i4 = top2;
                i3 = i5;
            }
            view.setTranslationX(i3);
        }
    }

    int computOffset(int i, int i2, int i3, float f) {
        float f2 = (i < i3 ? (i * i2) / i3 : i2) + ((0.1f - ((f * f) / 0.9f)) * i2);
        if (f2 > 0.0f) {
            return (int) f2;
        }
        return 0;
    }
}
