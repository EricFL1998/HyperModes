package miuix.miuixbasewidget.widget;

import android.util.SparseIntArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;

/* JADX INFO: loaded from: classes2.dex */
public class ListViewScrollBarAdapter implements ScrollableView {
    private HeightCache heightCache;
    private int lastScrollY = -1;
    private ListView listView;
    private ScrollPositionCalculator positionCalculator;
    private ScrollableView.OnScrollListener scrollListener;

    public ListViewScrollBarAdapter(ListView listView) {
        this.listView = listView;
        this.heightCache = new HeightCache();
        this.positionCalculator = new ScrollPositionCalculator();
        initScrollListener();
    }

    private void initScrollListener() {
        this.listView.setOnScrollListener(new ListViewScrollListener());
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public int getScrollY() {
        if (this.listView.getChildCount() == 0) {
            return 0;
        }
        int firstVisiblePosition = this.listView.getFirstVisiblePosition();
        View childAt = this.listView.getChildAt(0);
        if (childAt == null) {
            return 0;
        }
        return this.positionCalculator.calculateScrollY(firstVisiblePosition, childAt);
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public int getScrollRange() {
        ListAdapter adapter = this.listView.getAdapter();
        if (adapter == null || adapter.getCount() == 0) {
            return 0;
        }
        return Math.max(0, this.heightCache.getTotalHeight(adapter) - this.listView.getHeight());
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public void scrollTo(int i) {
        if (this.listView != null && Math.abs(i - getScrollY()) >= 5) {
            this.positionCalculator.scrollToPosition(i);
        }
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public void setOnScrollListener(ScrollableView.OnScrollListener onScrollListener) {
        this.scrollListener = onScrollListener;
    }

    public void clearHeightCache() {
        this.heightCache.clear();
    }

    public void updateItemHeight(int i, int i2) {
        this.heightCache.updateHeight(i, i2);
    }

    private class ListViewScrollListener implements AbsListView.OnScrollListener {
        private ListViewScrollListener() {
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 1 || i == 2) {
                notifyScrollListener();
            }
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            int scrollY = ListViewScrollBarAdapter.this.getScrollY();
            if (ListViewScrollBarAdapter.this.lastScrollY == -1 || Math.abs(scrollY - ListViewScrollBarAdapter.this.lastScrollY) > 0) {
                notifyScrollListener();
                ListViewScrollBarAdapter.this.lastScrollY = scrollY;
            }
        }

        private void notifyScrollListener() {
            if (ListViewScrollBarAdapter.this.scrollListener != null) {
                ListViewScrollBarAdapter.this.scrollListener.onScroll(ListViewScrollBarAdapter.this.getScrollY(), ListViewScrollBarAdapter.this.getScrollRange());
            }
        }
    }

    private class HeightCache {
        private int cachedTotalHeight;
        private SparseIntArray itemHeightCache;
        private int lastAdapterCount;

        private HeightCache() {
            this.itemHeightCache = new SparseIntArray();
            this.cachedTotalHeight = -1;
            this.lastAdapterCount = -1;
        }

        public int getTotalHeight(ListAdapter listAdapter) {
            int count = listAdapter.getCount();
            if (this.cachedTotalHeight == -1 || this.lastAdapterCount != count) {
                this.cachedTotalHeight = calculateTotalHeight(listAdapter);
                this.lastAdapterCount = count;
            }
            return this.cachedTotalHeight;
        }

        private int calculateTotalHeight(ListAdapter listAdapter) {
            int count = listAdapter.getCount();
            int itemHeight = 0;
            for (int i = 0; i < count; i++) {
                itemHeight += getItemHeight(i, listAdapter);
            }
            return itemHeight;
        }

        public int getItemHeight(int i, ListAdapter listAdapter) {
            int i2 = this.itemHeightCache.get(i, -1);
            if (i2 != -1) {
                return i2;
            }
            int heightFromVisibleView = getHeightFromVisibleView(i);
            if (heightFromVisibleView > 0) {
                this.itemHeightCache.put(i, heightFromVisibleView);
                return heightFromVisibleView;
            }
            int iMeasureItemHeight = measureItemHeight(i, listAdapter);
            if (iMeasureItemHeight > 0) {
                this.itemHeightCache.put(i, iMeasureItemHeight);
                return iMeasureItemHeight;
            }
            return getEstimatedHeight();
        }

        private int getHeightFromVisibleView(int i) {
            View childAt;
            int firstVisiblePosition = ListViewScrollBarAdapter.this.listView.getFirstVisiblePosition();
            int childCount = (ListViewScrollBarAdapter.this.listView.getChildCount() + firstVisiblePosition) - 1;
            if (i < firstVisiblePosition || i > childCount || (childAt = ListViewScrollBarAdapter.this.listView.getChildAt(i - firstVisiblePosition)) == null) {
                return 0;
            }
            int height = childAt.getHeight();
            if (height > 0) {
                return height;
            }
            int measuredHeight = childAt.getMeasuredHeight();
            if (measuredHeight > 0) {
                return measuredHeight;
            }
            return 0;
        }

        private int measureItemHeight(int i, ListAdapter listAdapter) {
            if (listAdapter != null && i >= 0 && i < listAdapter.getCount()) {
                try {
                    View view = listAdapter.getView(i, null, ListViewScrollBarAdapter.this.listView);
                    if (view != null) {
                        view.measure(View.MeasureSpec.makeMeasureSpec(ListViewScrollBarAdapter.this.listView.getWidth(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, 0));
                        return view.getMeasuredHeight();
                    }
                } catch (Exception unused) {
                }
            }
            return 0;
        }

        private int getEstimatedHeight() {
            int i = 0;
            if (this.itemHeightCache.size() <= 0) {
                if (ListViewScrollBarAdapter.this.listView.getChildCount() <= 0) {
                    return 100;
                }
                int i2 = 0;
                int height = 0;
                while (i < ListViewScrollBarAdapter.this.listView.getChildCount()) {
                    View childAt = ListViewScrollBarAdapter.this.listView.getChildAt(i);
                    if (childAt != null && childAt.getHeight() > 0) {
                        height += childAt.getHeight();
                        i2++;
                    }
                    i++;
                }
                if (i2 > 0) {
                    return height / i2;
                }
                return 100;
            }
            int iValueAt = 0;
            while (i < this.itemHeightCache.size()) {
                iValueAt += this.itemHeightCache.valueAt(i);
                i++;
            }
            return iValueAt / this.itemHeightCache.size();
        }

        public void clear() {
            this.itemHeightCache.clear();
            this.cachedTotalHeight = -1;
            this.lastAdapterCount = -1;
        }

        public void updateHeight(int i, int i2) {
            if (i2 > 0) {
                this.itemHeightCache.put(i, i2);
                this.cachedTotalHeight = -1;
            }
        }
    }

    private class ScrollPositionCalculator {
        private ScrollPositionCalculator() {
        }

        public int calculateScrollY(int i, View view) {
            ListAdapter adapter;
            if (view == null || (adapter = ListViewScrollBarAdapter.this.listView.getAdapter()) == null) {
                return 0;
            }
            int itemHeight = 0;
            for (int i2 = 0; i2 < i; i2++) {
                itemHeight += ListViewScrollBarAdapter.this.heightCache.getItemHeight(i2, adapter);
            }
            return Math.max(0, itemHeight - view.getTop());
        }

        public void scrollToPosition(int i) {
            int iFindPositionByScrollY;
            ListAdapter adapter = ListViewScrollBarAdapter.this.listView.getAdapter();
            if (ListViewScrollBarAdapter.this.listView != null && adapter != null && (iFindPositionByScrollY = findPositionByScrollY(i, adapter)) >= 0 && iFindPositionByScrollY < adapter.getCount()) {
                int scrollYForPosition = i - getScrollYForPosition(iFindPositionByScrollY, adapter);
                int height = ListViewScrollBarAdapter.this.listView.getHeight();
                int i2 = -ListViewScrollBarAdapter.this.heightCache.getItemHeight(iFindPositionByScrollY, adapter);
                if (scrollYForPosition < i2) {
                    scrollYForPosition = i2;
                }
                if (scrollYForPosition <= height) {
                    height = scrollYForPosition;
                }
                ListViewScrollBarAdapter.this.listView.setSelectionFromTop(iFindPositionByScrollY, -height);
            }
        }

        private int findPositionByScrollY(int i, ListAdapter listAdapter) {
            int itemHeight = 0;
            for (int i2 = 0; i2 < listAdapter.getCount(); i2++) {
                itemHeight += ListViewScrollBarAdapter.this.heightCache.getItemHeight(i2, listAdapter);
                if (itemHeight > i) {
                    return i2;
                }
            }
            return listAdapter.getCount() - 1;
        }

        private int getScrollYForPosition(int i, ListAdapter listAdapter) {
            int itemHeight = 0;
            for (int i2 = 0; i2 < i; i2++) {
                itemHeight += ListViewScrollBarAdapter.this.heightCache.getItemHeight(i2, listAdapter);
            }
            return itemHeight;
        }
    }
}
