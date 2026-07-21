package miuix.animation.controller;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import java.util.Map;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: classes2.dex */
public class ListViewTouchListener implements View.OnTouchListener {
    private int mTouchSlop;
    private WeakHashMap<View, View.OnTouchListener> mListeners = new WeakHashMap<>();
    private Rect mRect = new Rect();
    private float mDownX = Float.MAX_VALUE;
    private float mDownY = Float.MAX_VALUE;

    ListViewTouchListener(AbsListView absListView) {
        this.mTouchSlop = ViewConfiguration.get(absListView.getContext()).getScaledTouchSlop();
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean z;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mDownX = motionEvent.getRawX();
            this.mDownY = motionEvent.getRawY();
        } else if (actionMasked != 2) {
            this.mDownY = Float.MAX_VALUE;
            this.mDownX = Float.MAX_VALUE;
        } else {
            if (motionEvent.getRawY() - this.mDownY > this.mTouchSlop || motionEvent.getRawX() - this.mDownX > this.mTouchSlop) {
                z = true;
            }
            notifyItemListeners((AbsListView) view, motionEvent, z);
            return false;
        }
        z = false;
        notifyItemListeners((AbsListView) view, motionEvent, z);
        return false;
    }

    public void putListener(View view, View.OnTouchListener onTouchListener) {
        this.mListeners.put(view, onTouchListener);
    }

    private void notifyItemListeners(AbsListView absListView, MotionEvent motionEvent, boolean z) {
        View touchedItemView = getTouchedItemView(absListView, motionEvent);
        for (Map.Entry<View, View.OnTouchListener> entry : this.mListeners.entrySet()) {
            View key = entry.getKey();
            entry.getValue().onTouch(key, !z && key == touchedItemView ? motionEvent : null);
        }
    }

    private View getTouchedItemView(AbsListView absListView, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int childCount = absListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = absListView.getChildAt(i);
            childAt.getLocalVisibleRect(this.mRect);
            this.mRect.offset(childAt.getLeft(), childAt.getTop());
            if (this.mRect.contains(x, y)) {
                return childAt;
            }
        }
        return null;
    }
}
