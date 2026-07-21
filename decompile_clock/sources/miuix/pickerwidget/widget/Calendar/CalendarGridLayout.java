package miuix.pickerwidget.widget.Calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import miuix.core.util.MiuixUIUtils;
import miuix.pickerwidget.R;
import miuix.pickerwidget.date.Calendar;
import miuix.view.DensityChangedHelper;

/* JADX INFO: loaded from: classes3.dex */
class CalendarGridLayout extends GridLayout {
    public static final int DATE_TEXT_SIZE_IN_LARGE_FONT = 20;
    public static final int DATE_TEXT_SIZE_IN_NORMAL_FONT = 18;
    public static final int LUNAR_TEXT_SIZE_IN_LARGE_FONT = 12;
    public static final int LUNAR_TEXT_SIZE_IN_NORMAL_FONT = 10;
    private Calendar mCalendar;
    private int mChildPaddingIfNotLunarMode;
    private int mColumnCount;
    private int mDay;
    private CalendarDatePickerHelper mHelper;
    private int mLargeRowGap;
    private boolean mLunarMode;
    private int mMaxRowCount;
    private int mMediumRowGap;
    private int mMonth;
    private int mRowGap;
    private View mSelectedChild;
    private int mSmallRowGap;
    private int mYear;

    public CalendarGridLayout(Context context) {
        this(context, null);
    }

    public CalendarGridLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CalendarGridLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.Widget_CalendarGridLayout);
    }

    public CalendarGridLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mRowGap = 0;
        init(context, attributeSet, i, i2);
    }

    private void init(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CalendarGridLayout, i, i2);
        this.mLunarMode = typedArrayObtainStyledAttributes.getBoolean(R.styleable.CalendarGridLayout_lunarMode, false);
        typedArrayObtainStyledAttributes.recycle();
        this.mMaxRowCount = getRowCount();
        this.mColumnCount = getColumnCount();
        this.mHelper = new CalendarDatePickerHelper(context);
        this.mCalendar = new Calendar();
        this.mSmallRowGap = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_calendar_grid_layout_row_gap_small);
        this.mMediumRowGap = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_calendar_grid_layout_row_gap_medium);
        this.mLargeRowGap = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_calendar_grid_layout_row_gap_large);
        this.mChildPaddingIfNotLunarMode = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_calendar_grid_layout_child_padding);
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(context);
        for (int i3 = 0; i3 < this.mMaxRowCount; i3++) {
            for (int i4 = 0; i4 < this.mColumnCount; i4++) {
                layoutInflaterFrom.inflate(R.layout.miuix_appcompat_calendar_grid_child_layout, (ViewGroup) this, true);
            }
        }
    }

    private void selectChild(int i, int i2) {
        selectChild(getChildAt(i, i2));
    }

    void selectChild(View view) {
        if (view == null || view.isSelected()) {
            return;
        }
        View view2 = this.mSelectedChild;
        if (view2 != null) {
            view2.setSelected(false);
        }
        view.setSelected(true);
        this.mSelectedChild = view;
    }

    @Override // android.widget.GridLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(View.MeasureSpec.getSize(i), (this.mMaxRowCount * getChildAt(0).getMeasuredHeight()) + ((this.mMaxRowCount - 1) * this.mSmallRowGap));
    }

    View getSelectedChild() {
        return this.mSelectedChild;
    }

    private View getChildAt(int i, int i2) {
        return getChildAt((i * this.mColumnCount) + i2);
    }

    private void setDate(int i, int i2, int i3, int i4) {
        Context context = getContext();
        View childAt = getChildAt(i, i2);
        TextView textView = (TextView) childAt.findViewById(R.id.calendar_date_picker_date);
        TextView textView2 = (TextView) childAt.findViewById(R.id.calendar_date_picker_lunar_message);
        String lunarMessage = null;
        if (i4 == 0) {
            textView.setText((CharSequence) null);
            childAt.setEnabled(false);
            childAt.setImportantForAccessibility(2);
        } else {
            textView.setText(String.valueOf(i4));
            childAt.setEnabled(true);
            childAt.setImportantForAccessibility(1);
        }
        int fontLevel = MiuixUIUtils.getFontLevel(context);
        DensityChangedHelper.updateTextSizeDpUnit(textView, fontLevel == 2 ? 20 : 18);
        if (i3 >= 0 && i4 > 0) {
            lunarMessage = getLunarMessage(this.mYear, i3, i4);
        }
        textView2.setText(lunarMessage);
        DensityChangedHelper.updateTextSizeDpUnit(textView2, fontLevel == 2 ? 12 : 10);
    }

    private void refresh(int i, int i2, int i3) {
        this.mYear = i;
        this.mMonth = i2;
        int firstWeekDayInMonth = getFirstWeekDayInMonth(i, i2);
        int iDaysInMonth = this.mCalendar.daysInMonth(i, i2);
        int iMin = Math.min(i3, iDaysInMonth);
        this.mDay = iMin;
        int i4 = 1;
        for (int i5 = 0; i5 < this.mColumnCount; i5++) {
            if (i5 < firstWeekDayInMonth) {
                setDate(0, i5, 0, 0);
            } else {
                setDate(0, i5, i2, i4);
                int i6 = i4 + 1;
                if (i4 == iMin) {
                    selectChild(0, i5);
                }
                i4 = i6;
            }
            notifyChildLunarModeMaybeChanged(0, i5);
        }
        int i7 = 1;
        for (int i8 = 1; i8 < this.mMaxRowCount; i8++) {
            boolean z = false;
            for (int i9 = 0; i9 < this.mColumnCount; i9++) {
                if (i4 > iDaysInMonth) {
                    setDate(i8, i9, 0, 0);
                } else {
                    setDate(i8, i9, i2, i4);
                    int i10 = i4 + 1;
                    if (i4 == iMin) {
                        selectChild(i8, i9);
                    }
                    i4 = i10;
                    z = true;
                }
                notifyChildLunarModeMaybeChanged(i8, i9);
            }
            if (z) {
                i7++;
            }
        }
        adjustRowGap(i7);
    }

    void updateCurrentDate(int i, int i2, int i3) {
        this.mYear = i;
        this.mMonth = i2;
        this.mDay = i3;
    }

    private void adjustRowGap(int i) {
        int i2;
        if (i == 4) {
            i2 = this.mLargeRowGap;
        } else if (i == 5) {
            i2 = this.mMediumRowGap;
        } else {
            i2 = this.mSmallRowGap;
        }
        if (this.mRowGap == i2) {
            return;
        }
        int i3 = 0;
        while (i3 < this.mMaxRowCount) {
            for (int i4 = 0; i4 < this.mColumnCount; i4++) {
                View childAt = getChildAt(i3, i4);
                if (i3 < i) {
                    childAt.setVisibility(0);
                    if (i4 == 0) {
                        setChildMarginBottom(childAt, i3 == i + (-1) ? 0 : i2);
                    }
                } else {
                    childAt.setVisibility(8);
                }
            }
            i3++;
        }
        this.mRowGap = i2;
    }

    void setLunarMode(boolean z) {
        this.mLunarMode = z;
        refresh(this.mYear, this.mMonth, this.mDay);
    }

    void setHighlightColor(int i) {
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            Drawable selectedStateDrawable = getSelectedStateDrawable(getChildAt(i2).findViewById(R.id.calendar_date_picker_single_date_container));
            if (selectedStateDrawable instanceof GradientDrawable) {
                ((GradientDrawable) selectedStateDrawable).setColor(i);
            }
        }
    }

    private void notifyChildLunarModeMaybeChanged(int i, int i2) {
        View childAt = getChildAt(i, i2);
        int i3 = this.mLunarMode ? 0 : this.mChildPaddingIfNotLunarMode;
        View viewFindViewById = childAt.findViewById(R.id.calendar_date_picker_lunar_message);
        View viewFindViewById2 = childAt.findViewById(R.id.calendar_date_picker_child_container);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(this.mLunarMode ? 0 : 8);
        }
        if (viewFindViewById2 != null) {
            viewFindViewById2.setPadding(i3, i3, i3, i3);
        }
    }

    private void setChildMarginBottom(View view, int i) {
        this.mHelper.setChildMarginBottom(view, i);
    }

    private int getFirstWeekDayInMonth(int i, int i2) {
        return this.mHelper.getFirstWeekDayInMonth(i, i2);
    }

    String getLunarMessage(int i, int i2, int i3) {
        return this.mHelper.getLunarMessage(i, i2, i3);
    }

    private Drawable getSelectedStateDrawable(View view) {
        return this.mHelper.getSelectedStateDrawable(view);
    }
}
