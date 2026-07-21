package miuix.miuixbasewidget.widget;

/* JADX INFO: loaded from: classes2.dex */
public interface ScrollableView {

    public interface OnScrollListener {
        void onScroll(int i, int i2);
    }

    int getScrollRange();

    int getScrollY();

    void scrollTo(int i);

    void setOnScrollListener(OnScrollListener onScrollListener);
}
