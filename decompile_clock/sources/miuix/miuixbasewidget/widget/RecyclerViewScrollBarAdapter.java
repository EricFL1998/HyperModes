package miuix.miuixbasewidget.widget;

import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes2.dex */
public class RecyclerViewScrollBarAdapter implements ScrollableView {
    private RecyclerView.OnScrollListener onScrollListener;
    private RecyclerView recyclerView;
    private ScrollableView.OnScrollListener scrollListener;

    public RecyclerViewScrollBarAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        initScrollListener();
    }

    private void initScrollListener() {
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() { // from class: miuix.miuixbasewidget.widget.RecyclerViewScrollBarAdapter.1
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if ((i == 1 || i == 2) && RecyclerViewScrollBarAdapter.this.scrollListener != null) {
                    RecyclerViewScrollBarAdapter.this.scrollListener.onScroll(RecyclerViewScrollBarAdapter.this.getScrollY(), RecyclerViewScrollBarAdapter.this.getScrollRange());
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (RecyclerViewScrollBarAdapter.this.scrollListener != null) {
                    RecyclerViewScrollBarAdapter.this.scrollListener.onScroll(RecyclerViewScrollBarAdapter.this.getScrollY(), RecyclerViewScrollBarAdapter.this.getScrollRange());
                }
            }
        };
        this.onScrollListener = onScrollListener;
        this.recyclerView.addOnScrollListener(onScrollListener);
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public int getScrollY() {
        return this.recyclerView.computeVerticalScrollOffset();
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public int getScrollRange() {
        return this.recyclerView.computeVerticalScrollRange() - this.recyclerView.computeVerticalScrollExtent();
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public void scrollTo(int i) {
        this.recyclerView.scrollBy(0, i - getScrollY());
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public void setOnScrollListener(ScrollableView.OnScrollListener onScrollListener) {
        this.scrollListener = onScrollListener;
    }

    public void removeScrollListener() {
        RecyclerView.OnScrollListener onScrollListener = this.onScrollListener;
        if (onScrollListener != null) {
            this.recyclerView.removeOnScrollListener(onScrollListener);
        }
    }
}
