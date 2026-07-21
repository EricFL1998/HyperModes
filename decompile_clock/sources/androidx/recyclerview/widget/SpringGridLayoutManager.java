package androidx.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;

/* JADX INFO: loaded from: classes.dex */
public class SpringGridLayoutManager extends GridLayoutManager {
    static final boolean DEBUG = false;
    private static final String TAG = "MiLinearLayoutManager";
    private boolean hasSetLimitBottom;
    private boolean hasSetLimitTop;
    private ActionBarOverlayLayout mActionBarOverlayLayout;
    private final LinearLayoutManager.LayoutChunkResult mLayoutChunkResult;
    private int mLimitBottomOffsetHeight;
    private int mLimitTopOffsetHeight;

    public SpringGridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.hasSetLimitTop = false;
        this.hasSetLimitBottom = false;
        this.mLimitTopOffsetHeight = 0;
        this.mLimitBottomOffsetHeight = 0;
        this.mLayoutChunkResult = new LinearLayoutManager.LayoutChunkResult();
    }

    public SpringGridLayoutManager(Context context, int i) {
        super(context, i);
        this.hasSetLimitTop = false;
        this.hasSetLimitBottom = false;
        this.mLimitTopOffsetHeight = 0;
        this.mLimitBottomOffsetHeight = 0;
        this.mLayoutChunkResult = new LinearLayoutManager.LayoutChunkResult();
    }

    public SpringGridLayoutManager(Context context, int i, int i2, boolean z) {
        super(context, i, i2, z);
        this.hasSetLimitTop = false;
        this.hasSetLimitBottom = false;
        this.mLimitTopOffsetHeight = 0;
        this.mLimitBottomOffsetHeight = 0;
        this.mLayoutChunkResult = new LinearLayoutManager.LayoutChunkResult();
    }

    public void setTopLimitOffsetHeight(int i) {
        this.hasSetLimitTop = true;
        this.mLimitTopOffsetHeight = i;
    }

    public int getTopLimitOffsetHeight() {
        return this.mLimitTopOffsetHeight;
    }

    public void setBottomLimitOffsetHeight(int i) {
        this.hasSetLimitBottom = true;
        this.mLimitBottomOffsetHeight = i;
    }

    public int getBottomLimitOffsetHeight() {
        return this.mLimitBottomOffsetHeight;
    }

    public void attachActionBarOverlayLayout(ActionBarOverlayLayout actionBarOverlayLayout) {
        this.mActionBarOverlayLayout = actionBarOverlayLayout;
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager
    int fill(RecyclerView.Recycler recycler, LinearLayoutManager.LayoutState layoutState, RecyclerView.State state, boolean z) {
        int i;
        ActionBarOverlayLayout actionBarOverlayLayout;
        ActionBarOverlayLayout actionBarOverlayLayout2;
        int i2 = layoutState.mAvailable;
        if (layoutState.mScrollingOffset != Integer.MIN_VALUE) {
            if (layoutState.mAvailable < 0) {
                layoutState.mScrollingOffset += layoutState.mAvailable;
            }
            recycleByLayoutState(recycler, layoutState);
        }
        int i3 = layoutState.mAvailable + layoutState.mExtraFillSpace;
        LinearLayoutManager.LayoutChunkResult layoutChunkResult = this.mLayoutChunkResult;
        if (!this.hasSetLimitTop && (actionBarOverlayLayout2 = this.mActionBarOverlayLayout) != null && actionBarOverlayLayout2.isInOverlayMode() && this.mLimitTopOffsetHeight != this.mActionBarOverlayLayout.getContentInset().top) {
            this.mLimitTopOffsetHeight = this.mActionBarOverlayLayout.getContentInset().top;
        }
        if (!this.hasSetLimitBottom && (actionBarOverlayLayout = this.mActionBarOverlayLayout) != null && actionBarOverlayLayout.isInOverlayMode() && this.mLimitBottomOffsetHeight != this.mActionBarOverlayLayout.getContentInset().bottom) {
            this.mLimitBottomOffsetHeight = this.mActionBarOverlayLayout.getContentInset().bottom;
        }
        if (this.mShouldReverseLayout == (layoutState.mLayoutDirection == -1)) {
            i = this.mLimitTopOffsetHeight;
        } else {
            i = this.mLimitBottomOffsetHeight;
        }
        int i4 = 0 - i;
        while (true) {
            if ((!layoutState.mInfinite && i3 <= i4) || !layoutState.hasMore(state)) {
                break;
            }
            layoutChunkResult.resetInternal();
            layoutChunk(recycler, state, layoutState, layoutChunkResult);
            if (!layoutChunkResult.mFinished) {
                layoutState.mOffset += layoutChunkResult.mConsumed * layoutState.mLayoutDirection;
                if (!layoutChunkResult.mIgnoreConsumed || layoutState.mScrapList != null || !state.isPreLayout()) {
                    layoutState.mAvailable -= layoutChunkResult.mConsumed;
                    i3 -= layoutChunkResult.mConsumed;
                }
                if (layoutState.mScrollingOffset != Integer.MIN_VALUE) {
                    layoutState.mScrollingOffset += layoutChunkResult.mConsumed;
                    if (layoutState.mAvailable < 0) {
                        layoutState.mScrollingOffset += layoutState.mAvailable;
                    }
                    recycleByLayoutState(recycler, layoutState);
                }
                if (z && layoutChunkResult.mFocusable) {
                    break;
                }
            } else {
                break;
            }
        }
        return i2 - layoutState.mAvailable;
    }

    private void recycleByLayoutState(RecyclerView.Recycler recycler, LinearLayoutManager.LayoutState layoutState) {
        if (!layoutState.mRecycle || layoutState.mInfinite) {
            return;
        }
        int i = layoutState.mScrollingOffset;
        int i2 = layoutState.mNoRecycleSpace;
        if (layoutState.mLayoutDirection == -1) {
            recycleViewsFromEnd(recycler, i, i2);
        } else {
            recycleViewsFromStart(recycler, i, i2);
        }
    }

    private void recycleViewsFromStart(RecyclerView.Recycler recycler, int i, int i2) {
        if (i < 0) {
            return;
        }
        int i3 = i - i2;
        int childCount = getChildCount();
        if (!this.mShouldReverseLayout) {
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = getChildAt(i4);
                if (this.mOrientationHelper.getDecoratedEnd(childAt) > i3 - this.mLimitTopOffsetHeight || this.mOrientationHelper.getTransformedEndWithDecoration(childAt) > i3 - this.mLimitTopOffsetHeight) {
                    recycleChildren(recycler, 0, i4);
                    return;
                }
            }
            return;
        }
        int i5 = childCount - 1;
        for (int i6 = i5; i6 >= 0; i6--) {
            View childAt2 = getChildAt(i6);
            if (this.mOrientationHelper.getDecoratedEnd(childAt2) > i3 - this.mLimitTopOffsetHeight || this.mOrientationHelper.getTransformedEndWithDecoration(childAt2) > i3 - this.mLimitTopOffsetHeight) {
                recycleChildren(recycler, i5, i6);
                return;
            }
        }
    }

    private void recycleViewsFromEnd(RecyclerView.Recycler recycler, int i, int i2) {
        int childCount = getChildCount();
        if (i < 0) {
            return;
        }
        int end = (this.mOrientationHelper.getEnd() - i) + i2;
        if (this.mShouldReverseLayout) {
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (this.mOrientationHelper.getDecoratedStart(childAt) < this.mLimitBottomOffsetHeight + end || this.mOrientationHelper.getTransformedStartWithDecoration(childAt) < this.mLimitBottomOffsetHeight + end) {
                    recycleChildren(recycler, 0, i3);
                    return;
                }
            }
            return;
        }
        int i4 = childCount - 1;
        for (int i5 = i4; i5 >= 0; i5--) {
            View childAt2 = getChildAt(i5);
            if (this.mOrientationHelper.getDecoratedStart(childAt2) < this.mLimitBottomOffsetHeight + end || this.mOrientationHelper.getTransformedStartWithDecoration(childAt2) < this.mLimitBottomOffsetHeight + end) {
                recycleChildren(recycler, i4, i5);
                return;
            }
        }
    }

    private void recycleChildren(RecyclerView.Recycler recycler, int i, int i2) {
        if (i == i2) {
            return;
        }
        if (i2 <= i) {
            while (i > i2) {
                removeAndRecycleViewAt(i, recycler);
                i--;
            }
        } else {
            for (int i3 = i2 - 1; i3 >= i; i3--) {
                removeAndRecycleViewAt(i3, recycler);
            }
        }
    }
}
