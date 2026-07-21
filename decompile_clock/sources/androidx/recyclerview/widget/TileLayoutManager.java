package androidx.recyclerview.widget;

import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import java.util.HashSet;
import miuix.flexible.tile.ITileFullStrategy;
import miuix.flexible.tile.TileCache;

/* JADX INFO: loaded from: classes.dex */
public class TileLayoutManager extends RecyclerView.LayoutManager {
    private final HashSet<Integer> mAddedItems;
    private float mAspectRatio;
    private float mCellHeight;
    private float mCellWidth;
    private int mColumnCount;
    private float mColumnSpacing;
    private int mHeaderHeight;
    private boolean mIsShowHeader;
    private int mItemCount;
    private int mNormalTileViewType;
    private int mOffscreenTileLimit;
    private final TileLayoutParamsGetter mParamsGetter;
    private float mRowSpacing;
    private int mSumDy;
    private final TileCache mTileCache;
    private ITileFullStrategy mTileFullStrategy;

    public static abstract class TileLayoutParamsGetter implements TileCache.TileParamsGetter {
        public float getAspectRatio() {
            return 1.0f;
        }

        public int getHeaderHeight() {
            return -1;
        }

        public boolean isShowHeader() {
            return false;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollVertically() {
        return true;
    }

    public TileLayoutManager(TileLayoutParamsGetter tileLayoutParamsGetter) {
        this(tileLayoutParamsGetter, null);
    }

    public TileLayoutManager(TileLayoutParamsGetter tileLayoutParamsGetter, ITileFullStrategy iTileFullStrategy) {
        this.mColumnSpacing = 0.0f;
        this.mRowSpacing = 0.0f;
        this.mItemCount = 0;
        this.mColumnCount = 3;
        this.mHeaderHeight = 0;
        this.mAspectRatio = 1.0f;
        this.mOffscreenTileLimit = 0;
        this.mNormalTileViewType = 0;
        this.mSumDy = 0;
        this.mAddedItems = new HashSet<>();
        this.mParamsGetter = tileLayoutParamsGetter;
        this.mTileFullStrategy = iTileFullStrategy;
        this.mTileCache = new TileCache(new TileCache.TileParamsGetter() { // from class: androidx.recyclerview.widget.TileLayoutManager.1
            @Override // miuix.flexible.tile.TileCache.TileParamsGetter
            public int getItemCount() {
                TileLayoutManager tileLayoutManager = TileLayoutManager.this;
                return tileLayoutManager.getItemCountExclusiveHeader(tileLayoutManager.mIsShowHeader);
            }

            @Override // miuix.flexible.tile.TileCache.TileParamsGetter
            public int[] getTileSize(int i) {
                return (TileLayoutManager.this.mTileFullStrategy == null || !TileLayoutManager.this.mTileFullStrategy.isResized(i)) ? TileLayoutManager.this.mParamsGetter.getTileSize(i) : TileLayoutManager.this.mTileFullStrategy.getTileSize(i);
            }

            @Override // miuix.flexible.tile.TileCache.TileParamsGetter
            public int getColumnCount() {
                return TileLayoutManager.this.mParamsGetter.getColumnCount();
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2) {
        super.onMeasure(recycler, state, i, i2);
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        int size = (View.MeasureSpec.getSize(i) - getPaddingStart()) - getPaddingEnd();
        int columnCount = this.mParamsGetter.getColumnCount();
        float f = size;
        if (columnCount > 1) {
            float f2 = this.mColumnSpacing;
            f = ((f + f2) / columnCount) - f2;
        }
        this.mCellWidth = f;
        float aspectRatio = this.mParamsGetter.getAspectRatio();
        this.mAspectRatio = aspectRatio;
        this.mCellHeight = this.mCellWidth / aspectRatio;
        if (this.mColumnCount != columnCount) {
            recycler.getRecycledViewPool().setMaxRecycledViews(this.mNormalTileViewType, columnCount * 3);
        }
        updateTileCacheIfNeed();
    }

    private void updateTileCacheIfNeed() {
        int columnCount = this.mParamsGetter.getColumnCount();
        boolean zIsShowHeader = this.mParamsGetter.isShowHeader();
        int itemCountExclusiveHeader = getItemCountExclusiveHeader(zIsShowHeader);
        if (this.mColumnCount == columnCount && this.mItemCount == itemCountExclusiveHeader && this.mIsShowHeader == zIsShowHeader) {
            return;
        }
        this.mColumnCount = columnCount;
        this.mItemCount = itemCountExclusiveHeader;
        this.mIsShowHeader = zIsShowHeader;
        ITileFullStrategy iTileFullStrategy = this.mTileFullStrategy;
        if (iTileFullStrategy != null) {
            iTileFullStrategy.beforeUpdateTileCache(columnCount, itemCountExclusiveHeader);
        }
        this.mTileCache.updateCache();
        ITileFullStrategy iTileFullStrategy2 = this.mTileFullStrategy;
        if (iTileFullStrategy2 == null || !iTileFullStrategy2.afterUpdateTileCache(columnCount, itemCountExclusiveHeader)) {
            return;
        }
        this.mTileCache.updateCache();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v4, types: [int] */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r2v8 */
    /* JADX WARN: Type inference failed for: r5v3 */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int itemCount = getItemCount();
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);
        this.mAddedItems.clear();
        View viewMeasureHeader = this.mIsShowHeader ? measureHeader(recycler) : null;
        int iMin = Math.min(this.mSumDy, getTotalHeight() - getVerticalSpacing());
        this.mSumDy = iMin;
        this.mSumDy = Math.max(0, iMin);
        boolean z = this.mRecyclerView != null && this.mRecyclerView.getClipToPadding();
        if (viewMeasureHeader != null) {
            if (this.mSumDy < this.mHeaderHeight + (z ? 0 : getPaddingTop()) + (this.mOffscreenTileLimit * (this.mCellHeight + this.mRowSpacing))) {
                layoutHeader(viewMeasureHeader);
            } else {
                recycleHeader(viewMeasureHeader, recycler);
            }
        }
        int paddingTop = ((int) ((((this.mSumDy + this.mRowSpacing) - (z ? 0 : getPaddingTop())) - (this.mIsShowHeader ? this.mHeaderHeight + this.mRowSpacing : 0.0f)) / (this.mCellHeight + this.mRowSpacing))) - this.mOffscreenTileLimit;
        float height = ((this.mSumDy + getHeight()) - getPaddingTop()) - (z ? getPaddingBottom() : 0);
        boolean z2 = this.mIsShowHeader;
        int i = ((int) ((height - (z2 ? this.mRowSpacing + this.mHeaderHeight : 0.0f)) / (this.mCellHeight + this.mRowSpacing))) + this.mOffscreenTileLimit;
        ?? r2 = z2;
        while (r2 < itemCount) {
            int i2 = r2 - (this.mIsShowHeader ? 1 : 0);
            int x = this.mTileCache.getX(i2);
            int y = this.mTileCache.getY(i2);
            int width = this.mTileCache.getWidth(i2);
            int height2 = this.mTileCache.getHeight(i2);
            if (y + height2 > paddingTop && y <= i) {
                layoutItem(recycler, r2 == true ? 1 : 0, x, y, width, height2);
            }
            r2++;
        }
    }

    protected View measureHeader(RecyclerView.Recycler recycler) {
        int i = 0;
        View viewForPosition = recycler.getViewForPosition(0);
        int verticalSpacing = getVerticalSpacing();
        int headerHeight = this.mParamsGetter.getHeaderHeight();
        if (headerHeight < 0) {
            ViewGroup.LayoutParams layoutParams = viewForPosition.getLayoutParams();
            if (layoutParams.height >= 0) {
                verticalSpacing = layoutParams.height;
            }
            viewForPosition.measure(View.MeasureSpec.makeMeasureSpec(getHorizontalSpacing(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(verticalSpacing, i));
            this.mHeaderHeight = viewForPosition.getMeasuredHeight();
            return viewForPosition;
        }
        verticalSpacing = headerHeight;
        i = 1073741824;
        viewForPosition.measure(View.MeasureSpec.makeMeasureSpec(getHorizontalSpacing(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(verticalSpacing, i));
        this.mHeaderHeight = viewForPosition.getMeasuredHeight();
        return viewForPosition;
    }

    protected void layoutHeader(View view) {
        this.mAddedItems.add(0);
        addView(view);
        int paddingStart = getPaddingStart();
        int paddingTop = getPaddingTop() - this.mSumDy;
        layoutChildView(view, paddingStart, paddingTop, paddingStart + view.getMeasuredWidth(), paddingTop + view.getMeasuredHeight());
    }

    protected void recycleHeader(View view, RecyclerView.Recycler recycler) {
        this.mAddedItems.remove(0);
        removeAndRecycleView(view, recycler);
    }

    protected void layoutItem(RecyclerView.Recycler recycler, int i, int i2, int i3, int i4, int i5) {
        this.mAddedItems.add(Integer.valueOf(i));
        View viewForPosition = recycler.getViewForPosition(i);
        addView(viewForPosition);
        viewForPosition.measure(View.MeasureSpec.makeMeasureSpec((int) Math.ceil(calculateItemWidth(i4)), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec((int) Math.ceil(calculateItemHeight(i5)), BasicMeasure.EXACTLY));
        int paddingStart = (int) ((i2 * (this.mCellWidth + this.mColumnSpacing)) + getPaddingStart());
        int paddingTop = (int) (((i3 * (this.mCellHeight + this.mRowSpacing)) - this.mSumDy) + getPaddingTop() + (this.mIsShowHeader ? this.mHeaderHeight + this.mRowSpacing : 0.0f));
        layoutChildView(viewForPosition, paddingStart, paddingTop, (int) Math.ceil(paddingStart + calculateItemWidth(i4)), (int) Math.ceil(paddingTop + calculateItemHeight(i5)));
    }

    protected void layoutChildView(View view, int i, int i2, int i3, int i4) {
        boolean z = getLayoutDirection() == 1;
        int width = getWidth();
        int i5 = z ? width - i3 : i;
        if (z) {
            i3 = width - i;
        }
        view.layout(i5, i2, i3, i4);
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0046  */
    /* JADX WARN: Code duplicated, block: B:26:0x0068  */
    /* JADX WARN: Code duplicated, block: B:27:0x006a  */
    /* JADX WARN: Code duplicated, block: B:30:0x0075  */
    /* JADX WARN: Code duplicated, block: B:33:0x0084  */
    /* JADX WARN: Code duplicated, block: B:34:0x0086  */
    /* JADX WARN: Code duplicated, block: B:37:0x0091  */
    /* JADX WARN: Code duplicated, block: B:38:0x0098  */
    /* JADX WARN: Code duplicated, block: B:41:0x00b3  */
    /* JADX WARN: Code duplicated, block: B:42:0x00b8  */
    /* JADX WARN: Code duplicated, block: B:45:0x00bf  */
    /* JADX WARN: Code duplicated, block: B:48:0x00d4  */
    /* JADX WARN: Code duplicated, block: B:54:0x0113  */
    /* JADX WARN: Code duplicated, block: B:58:0x0123  */
    /* JADX WARN: Code duplicated, block: B:60:0x0129  */
    /* JADX WARN: Code duplicated, block: B:62:0x0137  */
    /* JADX WARN: Code duplicated, block: B:64:0x0143  */
    /* JADX WARN: Code duplicated, block: B:65:0x0148  */
    /* JADX WARN: Code duplicated, block: B:68:0x014f  */
    /* JADX WARN: Code duplicated, block: B:70:0x0159  */
    /* JADX WARN: Code duplicated, block: B:71:0x015e  */
    /* JADX WARN: Code duplicated, block: B:74:0x0166  */
    /* JADX WARN: Code duplicated, block: B:83:0x0172 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:84:0x0172 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:85:0x0172 A[SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v17 */
    /* JADX WARN: Type inference failed for: r5v0 */
    /* JADX WARN: Type inference failed for: r5v1, types: [int] */
    /* JADX WARN: Type inference failed for: r5v6 */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int i2;
        int i3;
        boolean z;
        float f;
        int paddingTop;
        float f2;
        int paddingBottom;
        int i4;
        ?? r5;
        int childCount;
        View childAt;
        RecyclerView.ViewHolder childViewHolder;
        int i5;
        float y;
        int paddingTop2;
        float y2;
        int height;
        int paddingBottom2;
        float f3;
        int i6;
        int paddingTop3;
        int itemCount = getItemCount();
        if (itemCount == 0 || i == 0) {
            return 0;
        }
        updateTileCacheIfNeed();
        int totalHeight = getTotalHeight();
        int verticalSpacing = getVerticalSpacing();
        int iMax = Math.max(totalHeight, verticalSpacing);
        int i7 = this.mSumDy;
        if (i7 + i >= 0) {
            int i8 = iMax - verticalSpacing;
            if (i7 + i > i8) {
                i3 = i8 - i7;
            } else {
                i2 = i;
            }
            this.mSumDy = i7 + i2;
            offsetChildrenVertical(-i2);
            if (this.mRecyclerView == null && this.mRecyclerView.getClipToPadding()) {
                z = true;
            } else {
                z = false;
            }
            f = this.mOffscreenTileLimit * (this.mCellHeight + this.mRowSpacing);
            if (!this.mAddedItems.contains(0) && this.mIsShowHeader) {
                f3 = this.mSumDy;
                i6 = this.mHeaderHeight;
                if (z) {
                    paddingTop3 = 0;
                } else {
                    paddingTop3 = getPaddingTop();
                }
                if (f3 < i6 + paddingTop3 + f) {
                    layoutHeader(measureHeader(recycler));
                }
            }
            float f4 = this.mSumDy + this.mRowSpacing;
            if (z) {
                paddingTop = 0;
            } else {
                paddingTop = getPaddingTop();
            }
            float f5 = f4 - paddingTop;
            if (this.mIsShowHeader) {
                f2 = this.mHeaderHeight + this.mRowSpacing;
            } else {
                f2 = 0.0f;
            }
            int i9 = ((int) ((f5 - f2) / (this.mCellHeight + this.mRowSpacing))) - this.mOffscreenTileLimit;
            int height2 = (this.mSumDy + getHeight()) - getPaddingTop();
            if (z) {
                paddingBottom = getPaddingBottom();
            } else {
                paddingBottom = 0;
            }
            float f6 = height2 - paddingBottom;
            boolean z2 = this.mIsShowHeader;
            i4 = ((int) ((f6 - (z2 ? this.mHeaderHeight + this.mRowSpacing : 0.0f)) / (this.mCellHeight + this.mRowSpacing))) + this.mOffscreenTileLimit;
            r5 = z2;
            while (r5 < itemCount) {
                int i10 = r5 - (this.mIsShowHeader ? 1 : 0);
                int x = this.mTileCache.getX(i10);
                int y3 = this.mTileCache.getY(i10);
                int width = this.mTileCache.getWidth(i10);
                int height3 = this.mTileCache.getHeight(i10);
                if (this.mAddedItems.contains(Integer.valueOf((int) r5)) && y3 + height3 > i9 && y3 <= i4) {
                    layoutItem(recycler, r5 == true ? 1 : 0, x, y3, width, height3);
                }
                i4 = i4;
                r5++;
            }
            for (childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                childAt = getChildAt(childCount);
                if (childAt != null) {
                    childViewHolder = this.mRecyclerView.getChildViewHolder(childAt);
                    i5 = childViewHolder.mPosition;
                    if (childViewHolder.isRecyclable()) {
                        y = childAt.getY() + childAt.getHeight();
                        if (z) {
                            paddingTop2 = getPaddingTop();
                        } else {
                            paddingTop2 = 0;
                        }
                        if (y >= paddingTop2 - f) {
                            y2 = childAt.getY();
                            height = getHeight();
                            if (z) {
                                paddingBottom2 = getPaddingBottom();
                            } else {
                                paddingBottom2 = 0;
                            }
                            if (y2 > (height - paddingBottom2) + f) {
                                removeAndRecycleView(childAt, recycler);
                                this.mAddedItems.remove(Integer.valueOf(i5));
                            }
                        } else {
                            removeAndRecycleView(childAt, recycler);
                            this.mAddedItems.remove(Integer.valueOf(i5));
                        }
                    }
                }
            }
            return i2;
        }
        i3 = -i7;
        i2 = i3;
        this.mSumDy = i7 + i2;
        offsetChildrenVertical(-i2);
        if (this.mRecyclerView == null) {
            z = false;
        } else {
            z = false;
        }
        f = this.mOffscreenTileLimit * (this.mCellHeight + this.mRowSpacing);
        if (!this.mAddedItems.contains(0)) {
            f3 = this.mSumDy;
            i6 = this.mHeaderHeight;
            if (z) {
                paddingTop3 = 0;
            } else {
                paddingTop3 = getPaddingTop();
            }
            if (f3 < i6 + paddingTop3 + f) {
                layoutHeader(measureHeader(recycler));
            }
        }
        float f7 = this.mSumDy + this.mRowSpacing;
        if (z) {
            paddingTop = 0;
        } else {
            paddingTop = getPaddingTop();
        }
        float f8 = f7 - paddingTop;
        if (this.mIsShowHeader) {
            f2 = this.mHeaderHeight + this.mRowSpacing;
        } else {
            f2 = 0.0f;
        }
        int i11 = ((int) ((f8 - f2) / (this.mCellHeight + this.mRowSpacing))) - this.mOffscreenTileLimit;
        int height4 = (this.mSumDy + getHeight()) - getPaddingTop();
        if (z) {
            paddingBottom = getPaddingBottom();
        } else {
            paddingBottom = 0;
        }
        float f9 = height4 - paddingBottom;
        boolean z3 = this.mIsShowHeader;
        i4 = ((int) ((f9 - (z3 ? this.mHeaderHeight + this.mRowSpacing : 0.0f)) / (this.mCellHeight + this.mRowSpacing))) + this.mOffscreenTileLimit;
        r5 = z3;
        while (r5 < itemCount) {
            int i12 = r5 - (this.mIsShowHeader ? 1 : 0);
            int x2 = this.mTileCache.getX(i12);
            int y4 = this.mTileCache.getY(i12);
            int width2 = this.mTileCache.getWidth(i12);
            int height5 = this.mTileCache.getHeight(i12);
            if (this.mAddedItems.contains(Integer.valueOf((int) r5))) {
            }
            i4 = i4;
            r5++;
        }
        while (childCount >= 0) {
            childAt = getChildAt(childCount);
            if (childAt != null) {
                childViewHolder = this.mRecyclerView.getChildViewHolder(childAt);
                i5 = childViewHolder.mPosition;
                if (childViewHolder.isRecyclable()) {
                    y = childAt.getY() + childAt.getHeight();
                    if (z) {
                        paddingTop2 = getPaddingTop();
                    } else {
                        paddingTop2 = 0;
                    }
                    if (y >= paddingTop2 - f) {
                        y2 = childAt.getY();
                        height = getHeight();
                        if (z) {
                            paddingBottom2 = getPaddingBottom();
                        } else {
                            paddingBottom2 = 0;
                        }
                        if (y2 > (height - paddingBottom2) + f) {
                            removeAndRecycleView(childAt, recycler);
                            this.mAddedItems.remove(Integer.valueOf(i5));
                        }
                    } else {
                        removeAndRecycleView(childAt, recycler);
                        this.mAddedItems.remove(Integer.valueOf(i5));
                    }
                }
            }
        }
        return i2;
    }

    protected int getVerticalSpacing() {
        return (getHeight() - getPaddingTop()) - getPaddingBottom();
    }

    protected int getHorizontalSpacing() {
        return (getWidth() - getPaddingStart()) - getPaddingEnd();
    }

    protected float calculateItemWidth(int i) {
        if (i <= 1) {
            return i * this.mCellWidth;
        }
        float f = this.mCellWidth;
        float f2 = this.mColumnSpacing;
        return (i * (f + f2)) - f2;
    }

    protected float calculateItemHeight(int i) {
        if (i <= 1) {
            return i * this.mCellHeight;
        }
        float f = this.mCellHeight;
        float f2 = this.mRowSpacing;
        return (i * (f + f2)) - f2;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int computeVerticalScrollRange(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return getTotalHeight();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int computeVerticalScrollExtent(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return getVerticalSpacing();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int computeVerticalScrollOffset(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return this.mSumDy;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void scrollToPosition(int i) {
        if (i < 0 || i >= getItemCount()) {
            return;
        }
        int i2 = 0;
        if (i != 0) {
            if (this.mIsShowHeader) {
                i--;
            }
            float y = this.mTileCache.getY(i);
            float f = this.mCellHeight;
            float f2 = this.mRowSpacing;
            i2 = (this.mIsShowHeader ? (int) (this.mHeaderHeight + f2) : 0) + ((int) (y * (f + f2)));
        }
        scrollVerticallyBy(i2 - this.mSumDy, this.mRecyclerView.mRecycler, this.mRecyclerView.mState);
    }

    public int getItemCountExclusiveHeader(boolean z) {
        return getItemCount() - (z ? 1 : 0);
    }

    public int getTotalHeight() {
        if (this.mIsShowHeader && getItemCountExclusiveHeader(true) == 0) {
            return this.mHeaderHeight;
        }
        return (int) (calculateItemHeight(this.mTileCache.getTotalHeight()) + (this.mIsShowHeader ? this.mHeaderHeight + this.mRowSpacing : 0.0f));
    }

    public void updateTileCache() {
        updateTileCacheIfNeed();
        requestLayout();
    }

    public void setTileFullStrategy(ITileFullStrategy iTileFullStrategy) {
        this.mTileFullStrategy = iTileFullStrategy;
    }

    public float getColumnSpacing() {
        return this.mColumnSpacing;
    }

    public void setColumnSpacing(float f) {
        this.mColumnSpacing = f;
        requestLayout();
    }

    public float getRowSpacing() {
        return this.mRowSpacing;
    }

    public void setRowSpacing(float f) {
        this.mRowSpacing = f;
        requestLayout();
    }

    public int getTileCacheHeight() {
        return this.mTileCache.getTotalHeight();
    }

    public float getAspectRatio() {
        return this.mAspectRatio;
    }

    public boolean getShowHeader() {
        return this.mIsShowHeader;
    }

    public void setOffscreenTileLimit(int i) {
        this.mOffscreenTileLimit = i;
    }

    public int getOffscreenTileLimit() {
        return this.mOffscreenTileLimit;
    }

    public void setNormalTileViewType(int i) {
        this.mNormalTileViewType = i;
    }

    public int getNormalTileViewType() {
        return this.mNormalTileViewType;
    }
}
