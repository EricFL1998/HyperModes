package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import miuix.flexible.grid.HyperGridConfiguration;
import miuix.flexible.grid.strategy.DynamicColumnFixedCellWidthGridStrategy;
import miuix.flexible.grid.strategy.DynamicColumnFixedSpacingFullGridStrategy;
import miuix.flexible.grid.strategy.DynamicColumnFixedSpacingGridStrategy;
import miuix.flexible.grid.strategy.FixedColumnFixedSpacingGridStrategy;

/* JADX INFO: loaded from: classes.dex */
public class HyperGridLayoutManager extends GridLayoutManager {
    private boolean mBottomRowSpacingEnable;
    private float mCellWidth;
    private int mColumnCount;
    private int mColumnMultiple;
    private float mColumnSpacing;
    private HyperGridConfiguration mConfiguration;
    private boolean mDisallowAutoColumnCount;
    private int mGravity;
    private int mHorizontalSpacing;
    private float mMaxCellWidth;
    private float mMaxColumnSpacing;
    private float mMinCellWidth;
    private float mMinColumnSpacing;
    private float mRowSpacing;
    private int mode;

    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollVertically() {
        return true;
    }

    public HyperGridLayoutManager(Context context) {
        super(context, 1);
        this.mode = 0;
        this.mMinColumnSpacing = 0.0f;
        this.mMaxColumnSpacing = Float.MAX_VALUE;
        this.mMaxCellWidth = Float.MAX_VALUE;
        this.mColumnCount = 1;
        this.mColumnMultiple = 1;
        this.mGravity = 17;
        this.mDisallowAutoColumnCount = false;
        this.mBottomRowSpacingEnable = false;
    }

    public HyperGridLayoutManager(Context context, int i) {
        this(context);
        this.mode = i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void calculateItemDecorationsForChild(View view, Rect rect) {
        if (this.mRecyclerView == null || this.mConfiguration == null) {
            return;
        }
        int childAdapterPosition = this.mRecyclerView.getChildAdapterPosition(view);
        int cachedSpanGroupIndex = this.mSpanSizeLookup.getCachedSpanGroupIndex(childAdapterPosition, getSpanCount());
        Rect itemDecorInsetsForChild = this.mRecyclerView.getItemDecorInsetsForChild(view);
        if (this.mBottomRowSpacingEnable || cachedSpanGroupIndex != this.mSpanSizeLookup.getCachedSpanGroupIndex(getItemCount() - 1, getSpanCount())) {
            itemDecorInsetsForChild.bottom = Math.round(this.mRowSpacing);
        } else {
            itemDecorInsetsForChild.bottom = 0;
        }
        view.getLayoutParams().width = Math.round(((this.mConfiguration.cellWidth + this.mConfiguration.columnSpacing) * this.mSpanSizeLookup.getSpanSize(childAdapterPosition)) - this.mConfiguration.columnSpacing);
        super.calculateItemDecorationsForChild(view, rect);
    }

    /* JADX WARN: Code duplicated, block: B:17:0x005a A[LOOP:0: B:16:0x0058->B:17:0x005a, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:20:0x007f  */
    /* JADX WARN: Code duplicated, block: B:21:0x0083  */
    /* JADX WARN: Code duplicated, block: B:23:0x0086  */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void layoutDecoratedWithMargins(View view, int i, int i2, int i3, int i4) {
        int i5;
        float horizontalSpacing;
        int paddingStart;
        int i6;
        boolean zIsLayoutRTL;
        int width;
        int iRound;
        int iRound2;
        int i7;
        HyperGridConfiguration hyperGridConfiguration = this.mConfiguration;
        if (hyperGridConfiguration != null && (i5 = hyperGridConfiguration.columnCount) > 0) {
            int childAdapterPosition = this.mRecyclerView.getChildAdapterPosition(view);
            int cachedSpanIndex = this.mSpanSizeLookup.getCachedSpanIndex(childAdapterPosition, getSpanCount());
            int spanSize = this.mSpanSizeLookup.getSpanSize(childAdapterPosition);
            int i8 = this.mGravity & 7;
            float f = this.mConfiguration.cellWidth;
            float f2 = this.mConfiguration.columnSpacing;
            float f3 = f + f2;
            float f4 = (i5 * f3) - f2;
            float paddingStart2 = getPaddingStart();
            if (i8 == 1) {
                horizontalSpacing = (getHorizontalSpacing() - f4) / 2.0f;
                paddingStart = getPaddingStart();
            } else {
                if (i8 == 5) {
                    horizontalSpacing = getHorizontalSpacing() - f4;
                    paddingStart = getPaddingStart();
                }
                for (i6 = 0; i6 < cachedSpanIndex; i6++) {
                    paddingStart2 += this.mSpanSizeLookup.getSpanSize((childAdapterPosition - cachedSpanIndex) + i6) * f3;
                }
                zIsLayoutRTL = isLayoutRTL();
                width = getWidth();
                iRound = Math.round(paddingStart2);
                iRound2 = Math.round(paddingStart2 + ((f3 * spanSize) - f2));
                if (zIsLayoutRTL) {
                    i7 = width - iRound2;
                } else {
                    i7 = iRound;
                }
                if (zIsLayoutRTL) {
                    iRound2 = width - iRound;
                }
                super.layoutDecoratedWithMargins(view, i7, i2, iRound2, i4);
            }
            paddingStart2 = horizontalSpacing + paddingStart;
            while (i6 < cachedSpanIndex) {
                paddingStart2 += this.mSpanSizeLookup.getSpanSize((childAdapterPosition - cachedSpanIndex) + i6) * f3;
            }
            zIsLayoutRTL = isLayoutRTL();
            width = getWidth();
            iRound = Math.round(paddingStart2);
            iRound2 = Math.round(paddingStart2 + ((f3 * spanSize) - f2));
            if (zIsLayoutRTL) {
                i7 = width - iRound2;
            } else {
                i7 = iRound;
            }
            if (zIsLayoutRTL) {
                iRound2 = width - iRound;
            }
            super.layoutDecoratedWithMargins(view, i7, i2, iRound2, i4);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2) {
        HyperGridConfiguration hyperGridConfiguration;
        super.onMeasure(recycler, state, i, i2);
        if (this.mHorizontalSpacing != getHorizontalSpacing() || this.mConfiguration == null) {
            this.mHorizontalSpacing = getHorizontalSpacing();
            if (this.mDisallowAutoColumnCount && (hyperGridConfiguration = this.mConfiguration) != null) {
                recycleHyperGridConfiguration(hyperGridConfiguration);
                this.mConfiguration = FixedColumnFixedSpacingGridStrategy.getConfiguration(this.mHorizontalSpacing, this.mConfiguration.columnCount, this.mConfiguration.columnSpacing);
            } else {
                int i3 = this.mode;
                if (i3 == 1) {
                    recycleHyperGridConfiguration(this.mConfiguration);
                    this.mConfiguration = DynamicColumnFixedSpacingFullGridStrategy.getConfiguration(this.mHorizontalSpacing, this.mColumnSpacing, this.mMinCellWidth, this.mMaxCellWidth, getItemCount());
                } else if (i3 == 2) {
                    recycleHyperGridConfiguration(this.mConfiguration);
                    this.mConfiguration = DynamicColumnFixedCellWidthGridStrategy.getConfiguration(this.mHorizontalSpacing, this.mMinColumnSpacing, this.mMaxColumnSpacing, this.mCellWidth, this.mColumnMultiple);
                } else if (i3 == 4) {
                    recycleHyperGridConfiguration(this.mConfiguration);
                    this.mConfiguration = FixedColumnFixedSpacingGridStrategy.getConfiguration(this.mHorizontalSpacing, this.mColumnCount, this.mColumnSpacing);
                } else {
                    recycleHyperGridConfiguration(this.mConfiguration);
                    this.mConfiguration = DynamicColumnFixedSpacingGridStrategy.getConfiguration(this.mHorizontalSpacing, this.mColumnSpacing, this.mMinCellWidth, this.mMaxCellWidth, this.mColumnMultiple);
                }
            }
            HyperGridConfiguration hyperGridConfiguration2 = this.mConfiguration;
            hyperGridConfiguration2.columnCount = Math.max(1, hyperGridConfiguration2.columnCount);
            HyperGridConfiguration hyperGridConfiguration3 = this.mConfiguration;
            hyperGridConfiguration3.cellWidth = Math.max(0.0f, hyperGridConfiguration3.cellWidth);
            HyperGridConfiguration hyperGridConfiguration4 = this.mConfiguration;
            hyperGridConfiguration4.columnSpacing = Math.max(0.0f, hyperGridConfiguration4.columnSpacing);
            setSpanCount(this.mConfiguration.columnCount);
        }
    }

    private void recycleHyperGridConfiguration(HyperGridConfiguration hyperGridConfiguration) {
        if (hyperGridConfiguration != null) {
            hyperGridConfiguration.recycle();
        }
    }

    protected int getVerticalSpacing() {
        return ((this.mRecyclerView == null ? getHeight() : this.mRecyclerView.getMeasuredHeight()) - getPaddingTop()) - getPaddingBottom();
    }

    protected int getHorizontalSpacing() {
        return ((this.mRecyclerView == null ? getWidth() : this.mRecyclerView.getMeasuredWidth()) - getPaddingStart()) - getPaddingEnd();
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int i) {
        this.mode = i;
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public float getColumnSpacing() {
        return this.mColumnSpacing;
    }

    public void setColumnSpacing(float f) {
        this.mColumnSpacing = f;
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public float getMinColumnSpacing() {
        return this.mMinColumnSpacing;
    }

    public void setMinColumnSpacing(float f) {
        this.mMinColumnSpacing = f;
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public float getMaxColumnSpacing() {
        return this.mMaxColumnSpacing;
    }

    public void setMaxColumnSpacing(float f) {
        this.mMaxColumnSpacing = f;
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public float getRowSpacing() {
        return this.mRowSpacing;
    }

    public void setRowSpacing(float f) {
        this.mRowSpacing = f;
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public float getCellWidth() {
        return this.mCellWidth;
    }

    public void setCellWidth(float f) {
        this.mCellWidth = f;
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public float getMinCellWidth() {
        return this.mMinCellWidth;
    }

    public void setMinCellWidth(float f) {
        this.mMinCellWidth = f;
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public float getMaxCellWidth() {
        return this.mMaxCellWidth;
    }

    public void setMaxCellWidth(float f) {
        this.mMaxCellWidth = f;
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public int getColumnCount() {
        return this.mColumnCount;
    }

    public void setColumnCount(int i) {
        this.mColumnCount = i;
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public int getColumnMultiple() {
        return this.mColumnMultiple;
    }

    public void setColumnMultiple(int i) {
        this.mColumnMultiple = i;
        this.mConfiguration = null;
        recycleHyperGridConfiguration(null);
        requestLayout();
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setGravity(int i) {
        this.mGravity = i;
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public void setDisallowAutoColumnCount(boolean z) {
        this.mDisallowAutoColumnCount = z;
        if (z) {
            return;
        }
        recycleHyperGridConfiguration(this.mConfiguration);
        this.mConfiguration = null;
        requestLayout();
    }

    public boolean getDisallowAutoColumnCount() {
        return this.mDisallowAutoColumnCount;
    }

    public void setBottomRowSpacingEnable(boolean z) {
        if (this.mBottomRowSpacingEnable == (!z)) {
            this.mBottomRowSpacingEnable = z;
            recycleHyperGridConfiguration(this.mConfiguration);
            this.mConfiguration = null;
            requestLayout();
        }
    }

    public boolean isBottomRowSpacingEnable() {
        return this.mBottomRowSpacingEnable;
    }
}
