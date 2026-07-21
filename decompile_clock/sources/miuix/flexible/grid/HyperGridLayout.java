package miuix.flexible.grid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import miuix.flexible.R;
import miuix.flexible.grid.strategy.DynamicColumnFixedCellWidthGridStrategy;
import miuix.flexible.grid.strategy.DynamicColumnFixedSpacingFullGridStrategy;
import miuix.flexible.grid.strategy.DynamicColumnFixedSpacingGridStrategy;
import miuix.flexible.grid.strategy.FixedColumnFixedSpacingGridStrategy;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class HyperGridLayout extends ViewGroup {
    private static final String TAG = "HyperGridLayout";
    private float mCellWidth;
    private int mColumnCount;
    private int mColumnMultiple;
    private float mColumnSpacing;
    private HyperGridConfiguration mConfiguration;
    private boolean mDisallowAutoColumnCount;
    private int mGravity;
    private float mMaxCellWidth;
    private float mMaxColumnSpacing;
    private float mMinCellWidth;
    private float mMinColumnSpacing;
    private float mRowSpacing;
    private float maxCellHeight;
    private int mode;

    public HyperGridLayout(Context context) {
        super(context);
        this.mode = 0;
        this.mMinColumnSpacing = 0.0f;
        this.mMaxColumnSpacing = Float.MAX_VALUE;
        this.mMaxCellWidth = Float.MAX_VALUE;
        this.mColumnCount = 1;
        this.mColumnMultiple = 1;
        this.mGravity = 0;
        this.mDisallowAutoColumnCount = false;
        init(context, null);
    }

    public HyperGridLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mode = 0;
        this.mMinColumnSpacing = 0.0f;
        this.mMaxColumnSpacing = Float.MAX_VALUE;
        this.mMaxCellWidth = Float.MAX_VALUE;
        this.mColumnCount = 1;
        this.mColumnMultiple = 1;
        this.mGravity = 0;
        this.mDisallowAutoColumnCount = false;
        init(context, attributeSet);
    }

    public HyperGridLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mode = 0;
        this.mMinColumnSpacing = 0.0f;
        this.mMaxColumnSpacing = Float.MAX_VALUE;
        this.mMaxCellWidth = Float.MAX_VALUE;
        this.mColumnCount = 1;
        this.mColumnMultiple = 1;
        this.mGravity = 0;
        this.mDisallowAutoColumnCount = false;
        init(context, attributeSet);
    }

    public HyperGridLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mode = 0;
        this.mMinColumnSpacing = 0.0f;
        this.mMaxColumnSpacing = Float.MAX_VALUE;
        this.mMaxCellWidth = Float.MAX_VALUE;
        this.mColumnCount = 1;
        this.mColumnMultiple = 1;
        this.mGravity = 0;
        this.mDisallowAutoColumnCount = false;
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HyperGridLayout);
            int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArrayObtainStyledAttributes.getIndex(i);
                if (index == R.styleable.HyperGridLayout_grid_mode) {
                    this.mode = typedArrayObtainStyledAttributes.getInt(index, 0);
                } else if (index == R.styleable.HyperGridLayout_android_gravity) {
                    this.mGravity = typedArrayObtainStyledAttributes.getInt(index, 0);
                } else if (index == R.styleable.HyperGridLayout_column_spacing) {
                    this.mColumnSpacing = typedArrayObtainStyledAttributes.getDimension(index, 0.0f);
                } else if (index == R.styleable.HyperGridLayout_min_column_spacing) {
                    this.mMinColumnSpacing = typedArrayObtainStyledAttributes.getDimension(index, 0.0f);
                } else if (index == R.styleable.HyperGridLayout_max_column_spacing) {
                    this.mMaxColumnSpacing = typedArrayObtainStyledAttributes.getDimension(index, Float.MAX_VALUE);
                } else if (index == R.styleable.HyperGridLayout_row_spacing) {
                    this.mRowSpacing = typedArrayObtainStyledAttributes.getDimension(index, 0.0f);
                } else if (index == R.styleable.HyperGridLayout_cell_width) {
                    this.mCellWidth = typedArrayObtainStyledAttributes.getDimension(index, 1.0f);
                } else if (index == R.styleable.HyperGridLayout_min_cell_width) {
                    this.mMinCellWidth = typedArrayObtainStyledAttributes.getDimension(index, 1.0f);
                } else if (index == R.styleable.HyperGridLayout_max_cell_width) {
                    this.mMaxCellWidth = typedArrayObtainStyledAttributes.getDimension(index, Float.MAX_VALUE);
                } else if (index == R.styleable.HyperGridLayout_column_count) {
                    this.mColumnCount = typedArrayObtainStyledAttributes.getInt(index, 1);
                } else if (index == R.styleable.HyperGridLayout_column_multiple) {
                    this.mColumnMultiple = typedArrayObtainStyledAttributes.getInt(index, 1);
                }
            }
            typedArrayObtainStyledAttributes.recycle();
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        HyperGridConfiguration hyperGridConfiguration;
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        if (mode == 0) {
            Log.w(TAG, "The width mode of the HyperGridLayout can not be UNSPECIFIED! Container width must be determined.");
        }
        int childCount = getChildCount();
        int i3 = 0;
        for (int i4 = 0; i4 < childCount; i4++) {
            if (getChildAt(i4).getVisibility() != 8) {
                i3++;
            }
        }
        int paddingStart = (size - getPaddingStart()) - getPaddingEnd();
        if (this.mDisallowAutoColumnCount && (hyperGridConfiguration = this.mConfiguration) != null) {
            recycleHyperGridConfiguration(hyperGridConfiguration);
            this.mConfiguration = FixedColumnFixedSpacingGridStrategy.getConfiguration(paddingStart, this.mConfiguration.columnCount, this.mConfiguration.columnSpacing);
        } else {
            int i5 = this.mode;
            if (i5 == 1) {
                recycleHyperGridConfiguration(this.mConfiguration);
                this.mConfiguration = DynamicColumnFixedSpacingFullGridStrategy.getConfiguration(paddingStart, this.mColumnSpacing, this.mMinCellWidth, this.mMaxCellWidth, i3);
            } else if (i5 == 2) {
                recycleHyperGridConfiguration(this.mConfiguration);
                this.mConfiguration = DynamicColumnFixedCellWidthGridStrategy.getConfiguration(paddingStart, this.mMinColumnSpacing, this.mMaxColumnSpacing, this.mCellWidth, this.mColumnMultiple);
            } else if (i5 == 4) {
                recycleHyperGridConfiguration(this.mConfiguration);
                this.mConfiguration = FixedColumnFixedSpacingGridStrategy.getConfiguration(paddingStart, this.mColumnCount, this.mColumnSpacing);
            } else {
                recycleHyperGridConfiguration(this.mConfiguration);
                this.mConfiguration = DynamicColumnFixedSpacingGridStrategy.getConfiguration(paddingStart, this.mColumnSpacing, this.mMinCellWidth, this.mMaxCellWidth, this.mColumnMultiple);
            }
        }
        HyperGridConfiguration hyperGridConfiguration2 = this.mConfiguration;
        hyperGridConfiguration2.columnCount = Math.max(1, hyperGridConfiguration2.columnCount);
        HyperGridConfiguration hyperGridConfiguration3 = this.mConfiguration;
        hyperGridConfiguration3.cellWidth = Math.max(0.0f, hyperGridConfiguration3.cellWidth);
        HyperGridConfiguration hyperGridConfiguration4 = this.mConfiguration;
        hyperGridConfiguration4.columnSpacing = Math.max(0.0f, hyperGridConfiguration4.columnSpacing);
        this.maxCellHeight = 0.0f;
        int iMin = Integer.MAX_VALUE;
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            if (childAt.getVisibility() != 8) {
                measureChildView(childAt, (int) Math.ceil(this.mConfiguration.cellWidth), size2);
                this.maxCellHeight = Math.max(this.maxCellHeight, childAt.getMeasuredHeight());
                iMin = Math.min(iMin, childAt.getMeasuredHeight());
            }
        }
        if (iMin != this.maxCellHeight) {
            uniformChildViewHeightIfNeeded((int) Math.ceil(this.mConfiguration.cellWidth), (int) this.maxCellHeight);
        }
        int iCeil = (int) Math.ceil(((double) i3) / ((double) this.mConfiguration.columnCount));
        if (mode2 != 1073741824) {
            float f = this.maxCellHeight;
            float f2 = this.mRowSpacing;
            size2 = (int) (((iCeil * (f + f2)) - (iCeil > 0 ? f2 : 0.0f)) + getPaddingTop() + getPaddingBottom());
        }
        setMeasuredDimension(size, size2);
    }

    private void recycleHyperGridConfiguration(HyperGridConfiguration hyperGridConfiguration) {
        if (hyperGridConfiguration != null) {
            hyperGridConfiguration.recycle();
        }
    }

    protected void measureChildView(View view, int i, int i2) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            view.measure(getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(i, BasicMeasure.EXACTLY), 0, layoutParams.width), getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(i2, 0), 0, layoutParams.height));
        }
    }

    protected void uniformChildViewHeightIfNeeded(int i, int i2) {
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                measureChildViewHeightForExactlyIfNeeded(childAt, i, i2);
            }
        }
    }

    protected void measureChildViewHeightForExactlyIfNeeded(View view, int i, int i2) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null || layoutParams.height != -1 || view.getMeasuredHeight() == i2) {
            return;
        }
        view.measure(getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(i, BasicMeasure.EXACTLY), 0, layoutParams.width), getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(i2, BasicMeasure.EXACTLY), 0, layoutParams.height));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        int iMax = Math.max(1, this.mConfiguration.columnCount);
        float f = iMax;
        int iCeil = (int) Math.ceil(childCount / f);
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int paddingStart = getPaddingStart();
        int paddingTop = getPaddingTop();
        int i7 = this.mGravity;
        int i8 = i7 & 112;
        int i9 = i7 & 7;
        if (i8 == 16) {
            float f2 = this.mRowSpacing;
            paddingTop = getPaddingTop() + ((((i6 - getPaddingTop()) - getPaddingBottom()) - ((int) ((iCeil * (this.maxCellHeight + f2)) - (iCeil > 0 ? f2 : 0.0f)))) / 2);
        } else if (i8 == 80) {
            float f3 = this.mRowSpacing;
            paddingTop = (i6 - ((int) ((iCeil * (this.maxCellHeight + f3)) - (iCeil > 0 ? f3 : 0.0f)))) - getPaddingBottom();
        }
        if (i9 == 1) {
            paddingStart = getPaddingStart() + ((((i5 - getPaddingStart()) - getPaddingEnd()) - ((int) ((f * (this.mConfiguration.columnSpacing + this.mConfiguration.cellWidth)) - this.mConfiguration.columnSpacing))) / 2);
        } else if (i9 == 5) {
            paddingStart = (i5 - ((int) ((f * (this.mConfiguration.columnSpacing + this.mConfiguration.cellWidth)) - this.mConfiguration.columnSpacing))) - getPaddingEnd();
        }
        int i10 = 0;
        for (int i11 = 0; i11 < childCount; i11++) {
            View childAt = getChildAt(i11);
            if (childAt.getVisibility() != 8) {
                int measuredWidth = (int) (paddingStart + ((i10 % iMax) * (this.mConfiguration.columnSpacing + this.mConfiguration.cellWidth)) + ((this.mConfiguration.cellWidth - childAt.getMeasuredWidth()) / 2.0f));
                float f4 = this.mRowSpacing;
                float f5 = this.maxCellHeight;
                int measuredHeight = (int) (paddingTop + ((i10 / iMax) * (f4 + f5)) + ((f5 - childAt.getMeasuredHeight()) / 2.0f));
                ViewUtils.layoutChildView(this, childAt, measuredWidth, measuredHeight, measuredWidth + childAt.getMeasuredWidth(), measuredHeight + childAt.getMeasuredHeight());
                i10++;
            }
        }
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int i) {
        this.mode = i;
        requestLayout();
    }

    public float getColumnSpacing() {
        return this.mColumnSpacing;
    }

    public void setColumnSpacing(float f) {
        this.mColumnSpacing = f;
        requestLayout();
    }

    public float getMinColumnSpacing() {
        return this.mMinColumnSpacing;
    }

    public void setMinColumnSpacing(float f) {
        this.mMinColumnSpacing = f;
        requestLayout();
    }

    public float getMaxColumnSpacing() {
        return this.mMaxColumnSpacing;
    }

    public void setMaxColumnSpacing(float f) {
        this.mMaxColumnSpacing = f;
        requestLayout();
    }

    public float getRowSpacing() {
        return this.mRowSpacing;
    }

    public void setRowSpacing(float f) {
        this.mRowSpacing = f;
        requestLayout();
    }

    public float getCellWidth() {
        return this.mCellWidth;
    }

    public void setCellWidth(float f) {
        this.mCellWidth = f;
        requestLayout();
    }

    public float getMinCellWidth() {
        return this.mMinCellWidth;
    }

    public void setMinCellWidth(float f) {
        this.mMinCellWidth = f;
        requestLayout();
    }

    public float getMaxCellWidth() {
        return this.mMaxCellWidth;
    }

    public void setMaxCellWidth(float f) {
        this.mMaxCellWidth = f;
        requestLayout();
    }

    public int getColumnCount() {
        return this.mColumnCount;
    }

    public void setColumnCount(int i) {
        this.mColumnCount = i;
        requestLayout();
    }

    public int getColumnMultiple() {
        return this.mColumnMultiple;
    }

    public void setColumnMultiple(int i) {
        this.mColumnMultiple = i;
        requestLayout();
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setGravity(int i) {
        this.mGravity = i;
        requestLayout();
    }

    public void setDisallowAutoColumnCount(boolean z) {
        this.mDisallowAutoColumnCount = z;
        if (z) {
            return;
        }
        requestLayout();
    }

    public boolean getDisallowAutoColumnCount() {
        return this.mDisallowAutoColumnCount;
    }
}
