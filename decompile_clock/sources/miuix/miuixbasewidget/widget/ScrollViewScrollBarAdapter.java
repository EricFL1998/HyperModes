package miuix.miuixbasewidget.widget;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes2.dex */
public class ScrollViewScrollBarAdapter implements ScrollableView {
    private int lastScrollY = -1;
    private ScrollableView.OnScrollListener scrollListener;
    private ScrollView scrollView;

    public ScrollViewScrollBarAdapter(ScrollView scrollView) {
        this.scrollView = scrollView;
        initScrollListener();
    }

    private void initScrollListener() {
        this.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() { // from class: miuix.miuixbasewidget.widget.ScrollViewScrollBarAdapter.1
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public void onScrollChanged() {
                if (ScrollViewScrollBarAdapter.this.scrollListener != null) {
                    int scrollY = ScrollViewScrollBarAdapter.this.getScrollY();
                    if (ScrollViewScrollBarAdapter.this.lastScrollY == -1 || Math.abs(scrollY - ScrollViewScrollBarAdapter.this.lastScrollY) > 0) {
                        ScrollViewScrollBarAdapter.this.scrollListener.onScroll(scrollY, ScrollViewScrollBarAdapter.this.getScrollRange());
                        ScrollViewScrollBarAdapter.this.lastScrollY = scrollY;
                    }
                }
            }
        });
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public int getScrollY() {
        return this.scrollView.getScrollY();
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public int getScrollRange() {
        View childAt = this.scrollView.getChildAt(0);
        if (childAt != null) {
            return Math.max(0, childAt.getHeight() - this.scrollView.getHeight());
        }
        return 0;
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public void scrollTo(int i) {
        stopFling();
        this.scrollView.scrollTo(0, i);
    }

    private void stopFling() {
        try {
            Field declaredField = ScrollView.class.getDeclaredField("mScroller");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(this.scrollView);
            if (obj != null) {
                obj.getClass().getMethod("abortAnimation", new Class[0]).invoke(obj, new Object[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // miuix.miuixbasewidget.widget.ScrollableView
    public void setOnScrollListener(ScrollableView.OnScrollListener onScrollListener) {
        this.scrollListener = onScrollListener;
    }
}
