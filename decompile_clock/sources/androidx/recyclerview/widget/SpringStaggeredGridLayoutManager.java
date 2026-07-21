package androidx.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;

/* JADX INFO: loaded from: classes.dex */
public class SpringStaggeredGridLayoutManager extends OriginalStaggeredGridLayoutManager {
    private boolean hasSetLimitBottom;
    private boolean hasSetLimitTop;
    private ActionBarOverlayLayout mActionBarOverlayLayout;
    private int mLimitBottomOffsetHeight;
    private int mLimitTopOffsetHeight;

    public SpringStaggeredGridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.hasSetLimitTop = false;
        this.hasSetLimitBottom = false;
        this.mLimitTopOffsetHeight = 0;
        this.mLimitBottomOffsetHeight = 0;
    }

    public SpringStaggeredGridLayoutManager(int i, int i2) {
        super(i, i2);
        this.hasSetLimitTop = false;
        this.hasSetLimitBottom = false;
        this.mLimitTopOffsetHeight = 0;
        this.mLimitBottomOffsetHeight = 0;
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

    @Override // androidx.recyclerview.widget.OriginalStaggeredGridLayoutManager
    protected void updateLayoutState(int i, RecyclerView.State state) {
        int totalSpace;
        int totalSpace2;
        ActionBarOverlayLayout actionBarOverlayLayout;
        ActionBarOverlayLayout actionBarOverlayLayout2;
        int targetScrollPosition;
        LayoutState layoutState = getLayoutState();
        boolean z = false;
        layoutState.mAvailable = 0;
        layoutState.mCurrentPosition = i;
        if (!isSmoothScrolling() || (targetScrollPosition = state.getTargetScrollPosition()) == -1) {
            totalSpace = 0;
            totalSpace2 = 0;
        } else {
            if (this.mShouldReverseLayout == (targetScrollPosition < i)) {
                totalSpace2 = this.mPrimaryOrientation.getTotalSpace();
                totalSpace = 0;
            } else {
                totalSpace = this.mPrimaryOrientation.getTotalSpace();
                totalSpace2 = 0;
            }
        }
        if (getClipToPadding()) {
            layoutState.mStartLine = this.mPrimaryOrientation.getStartAfterPadding() - totalSpace;
            layoutState.mEndLine = this.mPrimaryOrientation.getEndAfterPadding() + totalSpace2;
        } else {
            if (!this.hasSetLimitTop && (actionBarOverlayLayout2 = this.mActionBarOverlayLayout) != null && actionBarOverlayLayout2.isInOverlayMode() && this.mLimitTopOffsetHeight != this.mActionBarOverlayLayout.getContentInset().top) {
                this.mLimitTopOffsetHeight = this.mActionBarOverlayLayout.getContentInset().top;
            }
            if (!this.hasSetLimitBottom && (actionBarOverlayLayout = this.mActionBarOverlayLayout) != null && actionBarOverlayLayout.isInOverlayMode() && this.mLimitBottomOffsetHeight != this.mActionBarOverlayLayout.getContentInset().bottom) {
                this.mLimitBottomOffsetHeight = this.mActionBarOverlayLayout.getContentInset().bottom;
            }
            layoutState.mEndLine = this.mPrimaryOrientation.getEnd() + totalSpace2 + this.mLimitBottomOffsetHeight;
            layoutState.mStartLine = (-totalSpace) - this.mLimitTopOffsetHeight;
        }
        layoutState.mStopInFocusable = false;
        layoutState.mRecycle = true;
        if (this.mPrimaryOrientation.getMode() == 0 && this.mPrimaryOrientation.getEnd() == 0) {
            z = true;
        }
        layoutState.mInfinite = z;
    }
}
