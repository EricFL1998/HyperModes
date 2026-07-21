package miuix.pickerwidget.widget.Calendar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import java.time.ZonedDateTime;
import java.util.Locale;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.ViewUtils;
import miuix.pickerwidget.R;
import miuix.pickerwidget.date.Calendar;
import miuix.pickerwidget.widget.DatePicker;
import miuix.view.DensityChangedHelper;

/* JADX INFO: loaded from: classes3.dex */
public class CalendarDatePicker extends FrameLayout {
    public static final int CALENDAR_DAY_STYLE = 4;
    public static final int EXTRA_LAYOUT_SPACE = 1;
    public static final int HEADER_TEXT_SIZE_IN_LARGE_FONT = 28;
    public static final int HEADER_TEXT_SIZE_IN_NORMAL_FONT = 16;
    public static final int SMOOTH_SCROLL_MAX = 3;
    public static final int WEEKDAY_TEXT_SIZE_IN_LARGE_FONT = 25;
    public static final int WEEKDAY_TEXT_SIZE_IN_NORMAL_FONT = 13;
    private Drawable mArrowDown;
    private Drawable mArrowUp;
    private Calendar mCalendar;
    private CurrentDate mCurrent;
    private Drawable mCurrentArrow;
    private OnDateChangedListener mDateChangedListener;
    private DatePicker mDatePicker;
    private RecyclerView.OnScrollListener mExtraScrollListener;
    private int mFontLevel;
    private ViewGroup mHeaderLayout;
    private TextView mHeaderView;
    private CalendarDatePickerHelper mHelper;
    private boolean mIsDatePickerShowing;
    private View mLeftArrowView;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private CalendarDatePickerPanel mPanel;
    private miuix.recyclerview.widget.RecyclerView mRecyclerView;
    private View mRightArrowView;
    private LinearLayout mWeekDayLayout;

    public interface OnDateChangedListener {
        void onDateChanged(CalendarDatePicker calendarDatePicker, int i, int i2, int i3, String str);
    }

    public CalendarDatePicker(Context context) {
        this(context, null);
    }

    public CalendarDatePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CalendarDatePicker(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public CalendarDatePicker(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mFontLevel = 0;
        this.mIsDatePickerShowing = false;
        init(context, attributeSet, i, i2);
    }

    private void init(Context context, AttributeSet attributeSet, int i, int i2) {
        this.mHelper = new CalendarDatePickerHelper(context);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CalendarDatePicker, i, i2);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.CalendarDatePicker_lunarMode, false);
        typedArrayObtainStyledAttributes.recycle();
        inflate(context, R.layout.miuix_appcompat_calendar_date_picker_inflate_layout, this);
        this.mPanel = (CalendarDatePickerPanel) findViewById(R.id.calendar_date_picker_panel);
        this.mArrowUp = ContextCompat.getDrawable(context, R.drawable.miuix_appcompat_calendar_date_picker_arrow_up);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.miuix_appcompat_calendar_date_picker_arrow_down);
        this.mArrowDown = drawable;
        this.mCurrentArrow = drawable;
        this.mCalendar = new Calendar();
        this.mCurrent = new CurrentDate();
        setupHeader();
        setupWeekDay();
        setupDatePicker();
        setupRecyclerView();
        refreshHeader();
        setLunarMode(z);
        if (getId() == -1) {
            setId(R.id.miuix_appcompat_calendar_date_picker);
        }
    }

    public boolean isLargeFontLevel() {
        if (this.mFontLevel == 0) {
            this.mFontLevel = MiuixUIUtils.getFontLevel(getContext());
        }
        return this.mFontLevel == 2;
    }

    private void setupHeader() {
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.calendar_date_picker_header_layout);
        this.mHeaderLayout = viewGroup;
        this.mHeaderView = (TextView) viewGroup.findViewById(R.id.calendar_date_picker_header);
        this.mLeftArrowView = this.mHeaderLayout.findViewById(R.id.left_arrow);
        this.mRightArrowView = this.mHeaderLayout.findViewById(R.id.right_arrow);
        ViewGroup viewGroup2 = this.mHeaderLayout;
        if (viewGroup2 == null || this.mHeaderView == null) {
            return;
        }
        viewGroup2.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.pickerwidget.widget.Calendar.CalendarDatePicker.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (CalendarDatePicker.this.mRecyclerView.getScrollState() != 0) {
                    return false;
                }
                boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(view);
                float left = CalendarDatePicker.this.mHeaderView.getLeft();
                float right = CalendarDatePicker.this.mHeaderView.getRight();
                float x = motionEvent.getX();
                if (motionEvent.getAction() == 1) {
                    if (x <= left || x >= right) {
                        if (CalendarDatePicker.this.mLeftArrowView.getVisibility() == 0 && CalendarDatePicker.this.mRightArrowView.getVisibility() == 0) {
                            if ((zIsLayoutRtl || x <= right) && (!zIsLayoutRtl || x >= left)) {
                                CalendarDatePicker.this.smoothScrollToLastMonth();
                            } else {
                                CalendarDatePicker.this.smoothScrollToNextMonth();
                            }
                        }
                    } else {
                        CalendarDatePicker.this.toggle();
                    }
                }
                return true;
            }
        });
        DensityChangedHelper.updateTextSizeDpUnit(this.mHeaderView, isLargeFontLevel() ? 28 : 16);
    }

    private void setupWeekDay() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.calendar_date_picker_weekday_layout);
        this.mWeekDayLayout = linearLayout;
        if (linearLayout != null) {
            int i = isLargeFontLevel() ? 25 : 13;
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            Locale locale = this.mWeekDayLayout.getResources().getConfiguration().locale;
            for (int i2 = 0; i2 < this.mWeekDayLayout.getChildCount(); i2++) {
                TextView textView = (TextView) this.mWeekDayLayout.getChildAt(i2);
                DensityChangedHelper.updateTextSizeDpUnit(textView, i);
                calendar.set(7, positionToDayOfWeek(i2, calendar));
                textView.setText(calendar.getDisplayName(7, CALENDAR_DAY_STYLE, locale));
            }
        }
    }

    private void setupRecyclerView() {
        this.mRecyclerView = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.recycler_view);
        SmoothCalendarLayoutManager smoothCalendarLayoutManager = new SmoothCalendarLayoutManager(getContext(), 0, 0 == true ? 1 : 0) { // from class: miuix.pickerwidget.widget.Calendar.CalendarDatePicker.2
            @Override // androidx.recyclerview.widget.LinearLayoutManager
            protected void calculateExtraLayoutSpace(RecyclerView.State state, int[] iArr) {
                iArr[0] = CalendarDatePicker.this.mRecyclerView.getMeasuredWidth();
                iArr[1] = CalendarDatePicker.this.mRecyclerView.getMeasuredWidth();
            }
        };
        MonthsPagerAdapter monthsPagerAdapter = new MonthsPagerAdapter(getContext(), this.mDatePicker.getMinDate(), this.mDatePicker.getMaxDate(), new MonthsPagerAdapter.OnDayClickListener() { // from class: miuix.pickerwidget.widget.Calendar.CalendarDatePicker.3
            @Override // miuix.pickerwidget.widget.Calendar.MonthsPagerAdapter.OnDayClickListener
            public void onDayClick(CalendarGridLayout calendarGridLayout, View view, MonthsPagerAdapter monthsPagerAdapter2) {
                calendarGridLayout.selectChild(view);
                TextView textView = (TextView) view.findViewById(R.id.calendar_date_picker_date);
                if (textView != null) {
                    int i = Integer.parseInt(textView.getText().toString());
                    CalendarDatePicker.this.mCurrent.setDayOfMonth(i);
                    monthsPagerAdapter2.notifyCurrentDayChanged(i);
                    int year = CalendarDatePicker.this.getYear();
                    int month = CalendarDatePicker.this.getMonth();
                    int dayOfMonth = CalendarDatePicker.this.getDayOfMonth();
                    calendarGridLayout.updateCurrentDate(year, month, dayOfMonth);
                    CalendarDatePicker calendarDatePicker = CalendarDatePicker.this;
                    calendarDatePicker.notifyDateChange(calendarDatePicker, year, month, dayOfMonth);
                }
            }
        });
        this.mOnScrollListener = new RecyclerView.OnScrollListener() { // from class: miuix.pickerwidget.widget.Calendar.CalendarDatePicker.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 0) {
                    int[] yearAndMonth = CalendarDatePicker.this.getAdapter().getYearAndMonth(CalendarDatePicker.this.getCurrentItemPosition());
                    int i2 = yearAndMonth[0];
                    int i3 = yearAndMonth[1];
                    CalendarDatePicker.this.mCurrent.setYear(i2);
                    CalendarDatePicker.this.mCurrent.setMonth(i3);
                    CalendarDatePicker.this.refreshHeader();
                    CalendarDatePicker calendarDatePicker = CalendarDatePicker.this;
                    calendarDatePicker.notifyDateChange(calendarDatePicker, i2, i3, calendarDatePicker.mCurrent.getDayOfMonth());
                }
                if (CalendarDatePicker.this.mExtraScrollListener != null) {
                    CalendarDatePicker.this.mExtraScrollListener.onScrollStateChanged(recyclerView, i);
                }
            }
        };
        this.mRecyclerView.setLayoutManager(smoothCalendarLayoutManager);
        this.mRecyclerView.setAdapter(monthsPagerAdapter);
        new PagerSnapHelper().attachToRecyclerView(this.mRecyclerView);
        setUpForAccessibility();
        smartScrollToSpecifiedMonth(getYear(), getMonth(), false);
    }

    private int positionToDayOfWeek(int i, java.util.Calendar calendar) {
        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        int maximum = calendar.getMaximum(7);
        int i2 = i + firstDayOfWeek;
        return i2 > maximum ? i2 - maximum : i2;
    }

    public MonthsPagerAdapter getAdapter() {
        return (MonthsPagerAdapter) this.mRecyclerView.getAdapter();
    }

    public LinearLayoutManager getLayoutManager() {
        return (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mRecyclerView.addOnScrollListener(this.mOnScrollListener);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mRecyclerView.removeOnScrollListener(this.mOnScrollListener);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (ViewUtils.isLayoutRtl(this)) {
            scrollRecyclerViewToCurrent();
        }
    }

    public void setWrapContent(boolean z) {
        CalendarDatePickerPanel calendarDatePickerPanel = this.mPanel;
        if (calendarDatePickerPanel != null) {
            calendarDatePickerPanel.setWrapContent(z);
        }
    }

    public int getCurrentItemPosition() {
        return getLayoutManager().findFirstVisibleItemPosition();
    }

    private void setUpForAccessibility() {
        ViewCompat.setAccessibilityDelegate(this.mRecyclerView, new AccessibilityDelegateCompat() { // from class: miuix.pickerwidget.widget.Calendar.CalendarDatePicker.5
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setScrollable(false);
            }
        });
    }

    private void postSmoothRecyclerViewScroll(final int i) {
        this.mRecyclerView.post(new Runnable() { // from class: miuix.pickerwidget.widget.Calendar.CalendarDatePicker$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1915x6bb4ab9a(i);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$postSmoothRecyclerViewScroll$0$miuix-pickerwidget-widget-Calendar-CalendarDatePicker, reason: not valid java name */
    /* synthetic */ void m1915x6bb4ab9a(int i) {
        this.mRecyclerView.smoothScrollToPosition(i);
    }

    public void scrollRecyclerViewToCurrent() {
        smartScrollToSpecifiedMonth(getYear(), getMonth(), false);
    }

    public void setRecyclerViewExtraOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.mExtraScrollListener = onScrollListener;
    }

    private void smartScrollToSpecifiedMonth(int i, int i2, boolean z) {
        int position = getAdapter().getPosition(i, i2);
        if (z) {
            int currentItemPosition = position - getCurrentItemPosition();
            boolean z2 = Math.abs(currentItemPosition) > 3;
            boolean z3 = currentItemPosition > 0;
            if (z2 && z3) {
                this.mRecyclerView.scrollToPosition(position - 3);
                postSmoothRecyclerViewScroll(position);
                return;
            } else if (z2) {
                this.mRecyclerView.scrollToPosition(position + 3);
                postSmoothRecyclerViewScroll(position);
                return;
            } else {
                postSmoothRecyclerViewScroll(position);
                return;
            }
        }
        this.mRecyclerView.scrollToPosition(position);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void smoothScrollToNextMonth() {
        int currentItemPosition = getCurrentItemPosition() + 1;
        if (currentItemPosition < getAdapter().getItemCount()) {
            this.mRecyclerView.smoothScrollToPosition(currentItemPosition);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void smoothScrollToLastMonth() {
        int currentItemPosition = getCurrentItemPosition() - 1;
        if (currentItemPosition >= 0) {
            this.mRecyclerView.smoothScrollToPosition(currentItemPosition);
        }
    }

    public Drawable getSelectedStateDrawable(View view) {
        return this.mHelper.getSelectedStateDrawable(view);
    }

    public String getLunarMessage(int i, int i2, int i3) {
        return this.mHelper.getLunarMessage(i, i2, i3);
    }

    private void safeUpdateDatePicker(int i, int i2, int i3) {
        if (this.mDatePicker != null) {
            this.mDatePicker.updateDate(i, i2, Math.min(this.mCalendar.daysInMonth(i, i2), i3));
        }
    }

    private void setupDatePicker() {
        DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
        this.mDatePicker = datePicker;
        datePicker.init(this.mCalendar.get(1), this.mCalendar.get(5), this.mCalendar.get(9), new DatePicker.OnDateChangedListener() { // from class: miuix.pickerwidget.widget.Calendar.CalendarDatePicker.6
            @Override // miuix.pickerwidget.widget.DatePicker.OnDateChangedListener
            public void onDateChanged(DatePicker datePicker2, int i, int i2, int i3, boolean z) {
                CalendarDatePicker.this.mCurrent.updateDate(i, i2, i3);
                CalendarDatePicker.this.refreshHeader();
                CalendarDatePicker calendarDatePicker = CalendarDatePicker.this;
                calendarDatePicker.notifyDateChange(calendarDatePicker, i, i2, i3);
            }
        });
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.year = getYear();
        savedState.month = getMonth();
        savedState.day = getDayOfMonth();
        savedState.isDatePickerVisible = this.mIsDatePickerShowing;
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.mCurrent.updateDate(savedState.year, savedState.month, savedState.day);
            refreshHeader();
            if (savedState.isDatePickerVisible) {
                toggle();
                return;
            } else {
                refreshGrid(false);
                return;
            }
        }
        Log.w("CalendarDatePicker", "Wrong state class, expecting SavedState! This usually happens when two views of different type have the same id in the same hierarchy.");
        super.onRestoreInstanceState(parcelable);
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        this.mDateChangedListener = onDateChangedListener;
    }

    public int getYear() {
        return this.mCurrent.getYear();
    }

    public int getMonth() {
        return this.mCurrent.getMonth();
    }

    public int getDayOfMonth() {
        return this.mCurrent.getDayOfMonth();
    }

    public boolean isLunarMode() {
        return getAdapter().isLunarMode();
    }

    public void setDate(long j, boolean z) {
        this.mCalendar.setSafeTimeInMillis(j, isLunarMode());
        safeUpdateDatePicker(this.mCalendar.get(1), this.mCalendar.get(5), this.mCalendar.get(9));
        refreshHeader();
        refreshGrid(z);
    }

    public void setDate(long j) {
        setDate(j, true);
    }

    public void setDate(ZonedDateTime zonedDateTime, boolean z) {
        setDate(this.mCalendar.set(1, zonedDateTime.getYear()).set(5, zonedDateTime.getMonth().getValue() - 1).set(9, zonedDateTime.getDayOfMonth()).getTimeInMillis(), z);
    }

    public void setDate(ZonedDateTime zonedDateTime) {
        setDate(zonedDateTime, true);
    }

    public void setHighlightColor(int i, boolean z) {
        getAdapter().notifyHighlightColorChanged(i);
        if (z) {
            this.mDatePicker.setLabelTextColor(i);
            this.mDatePicker.setTextColorHighlight(i);
        }
    }

    public void setHighlightColor(int i) {
        setHighlightColor(i, true);
    }

    public long getMinDate() {
        return this.mDatePicker.getMinDate();
    }

    public long getMaxDate() {
        return this.mDatePicker.getMaxDate();
    }

    public void setMinDate(long j) {
        getAdapter().notifyMinDateChanged(j);
        this.mDatePicker.setMinDate(j);
        if (this.mCalendar.getTimeInMillis() < j) {
            this.mCalendar.setTimeInMillis(j);
        }
        smartScrollToSpecifiedMonth(this.mCalendar.get(1), this.mCalendar.get(5), false);
    }

    public void setMaxDate(long j) {
        getAdapter().notifyMaxDateChanged(j);
        this.mDatePicker.setMaxDate(j);
        if (this.mCalendar.getTimeInMillis() > j) {
            this.mCalendar.setTimeInMillis(j);
        }
        smartScrollToSpecifiedMonth(this.mCalendar.get(1), this.mCalendar.get(5), false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggle() {
        Drawable drawable = this.mCurrentArrow;
        Drawable drawable2 = this.mArrowUp;
        if (drawable == drawable2) {
            this.mCurrentArrow = this.mArrowDown;
            hideChildren(this.mDatePicker);
            showChildren(this.mWeekDayLayout, this.mRecyclerView);
            this.mIsDatePickerShowing = false;
            refreshGrid(true);
        } else {
            this.mCurrentArrow = drawable2;
            showChildren(this.mDatePicker);
            hideChildren(this.mWeekDayLayout, this.mRecyclerView);
            post(new Runnable() { // from class: miuix.pickerwidget.widget.Calendar.CalendarDatePicker$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1916x9b2aae70();
                }
            });
            this.mIsDatePickerShowing = true;
        }
        this.mHeaderView.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, this.mCurrentArrow, (Drawable) null);
    }

    /* JADX INFO: renamed from: lambda$toggle$1$miuix-pickerwidget-widget-Calendar-CalendarDatePicker, reason: not valid java name */
    /* synthetic */ void m1916x9b2aae70() {
        safeUpdateDatePicker(getYear(), getMonth(), getDayOfMonth());
    }

    private void showChildren(View... viewArr) {
        int i = 0;
        for (View view : viewArr) {
            if (view != null) {
                view.setVisibility(0);
            }
        }
        if (viewArr.length == 1 && viewArr[0] == this.mDatePicker) {
            i = 4;
        }
        this.mLeftArrowView.setVisibility(i);
        this.mRightArrowView.setVisibility(i);
    }

    private void hideChildren(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.setVisibility(8);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshHeader() {
        String string = Integer.toString(getYear());
        String string2 = Integer.toString(getMonth() + 1);
        if (string2.length() == 1) {
            string2 = WorldClockEditActivity.LOCAL_CITY_ID + string2;
        }
        this.mHeaderView.setText(string + "/" + string2);
    }

    private void refreshGrid(boolean z) {
        smartScrollToSpecifiedMonth(getYear(), getMonth(), z);
        getAdapter().notifyCurrentDayChanged(getDayOfMonth());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyDateChange(CalendarDatePicker calendarDatePicker, int i, int i2, int i3) {
        OnDateChangedListener onDateChangedListener = this.mDateChangedListener;
        if (onDateChangedListener != null) {
            onDateChangedListener.onDateChanged(calendarDatePicker, i, i2, i3, getLunarMessage(i, i2, i3));
        }
    }

    public void setLunarMode(boolean z) {
        getAdapter().notifyLunarModeChanged(z);
    }

    public DatePicker getDatePicker() {
        return this.mDatePicker;
    }

    public View getHeaderLayout() {
        return this.mHeaderLayout;
    }

    private static class SavedState extends View.BaseSavedState {
        static final Parcelable.ClassLoaderCreator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: miuix.pickerwidget.widget.Calendar.CalendarDatePicker.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }

            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }
        };
        int day;
        boolean isDatePickerVisible;
        int month;
        int year;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        SavedState(Parcel parcel) {
            super(parcel);
            this.year = parcel.readInt();
            this.month = parcel.readInt();
            this.day = parcel.readInt();
            this.isDatePickerVisible = parcel.readInt() != 0;
        }

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.year = parcel.readInt();
            this.month = parcel.readInt();
            this.day = parcel.readInt();
            this.isDatePickerVisible = parcel.readInt() != 0;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.year);
            parcel.writeInt(this.month);
            parcel.writeInt(this.day);
            parcel.writeInt(this.isDatePickerVisible ? 1 : 0);
        }
    }

    private class CurrentDate {
        int dayOfMonth;
        int month;
        int year;

        CurrentDate() {
            this.year = CalendarDatePicker.this.mCalendar.get(1);
            this.month = CalendarDatePicker.this.mCalendar.get(5);
            this.dayOfMonth = CalendarDatePicker.this.mCalendar.get(9);
        }

        int getYear() {
            return this.year;
        }

        int getMonth() {
            return this.month;
        }

        int getDayOfMonth() {
            return this.dayOfMonth;
        }

        void setYear(int i) {
            if (this.year != i) {
                this.year = i;
            }
        }

        void setMonth(int i) {
            if (this.month != i) {
                this.month = i;
            }
        }

        void setDayOfMonth(int i) {
            if (this.dayOfMonth != i) {
                this.dayOfMonth = i;
            }
        }

        void updateDate(int i, int i2, int i3) {
            setYear(i);
            setMonth(i2);
            setDayOfMonth(i3);
        }
    }
}
