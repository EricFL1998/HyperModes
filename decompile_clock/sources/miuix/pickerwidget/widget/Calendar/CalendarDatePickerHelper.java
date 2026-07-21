package miuix.pickerwidget.widget.Calendar;

import android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.GridLayout;
import miuix.pickerwidget.date.Calendar;
import miuix.pickerwidget.date.CalendarFormatSymbols;

/* JADX INFO: loaded from: classes3.dex */
class CalendarDatePickerHelper {
    private Calendar mCalendar = new Calendar();
    private String[] mChineseDays;
    private SparseArray<String> mLunarHolidayCache;
    private SparseArray<String> mSolarHolidayCache;

    private int generateCacheKey(int i, int i2) {
        return ((i + 1) * 100) + i2;
    }

    CalendarDatePickerHelper(Context context) {
        CalendarFormatSymbols orCreate = CalendarFormatSymbols.getOrCreate(context);
        this.mSolarHolidayCache = orCreate.getSolarHolidays();
        this.mLunarHolidayCache = orCreate.getLunarHolidays();
        this.mChineseDays = orCreate.getChineseDays();
    }

    int getFirstWeekDayInMonth(int i, int i2) {
        this.mCalendar.set(1, i).set(5, i2).set(9, 1);
        return (this.mCalendar.get(14) + 5) % 7;
    }

    String getLunarMessage(int i, int i2, int i3) {
        int i4;
        this.mCalendar.set(1, i).set(5, i2).set(9, i3);
        String messageFromLunarHolidayCache = getMessageFromLunarHolidayCache(this.mCalendar.get(6), this.mCalendar.get(10));
        if (TextUtils.isEmpty(messageFromLunarHolidayCache)) {
            messageFromLunarHolidayCache = getMessageFromSolarHolidayCache(i2, i3);
        }
        if (!TextUtils.isEmpty(messageFromLunarHolidayCache) || (i4 = this.mCalendar.get(10)) <= 0) {
            return messageFromLunarHolidayCache;
        }
        String[] strArr = this.mChineseDays;
        return i4 <= strArr.length ? strArr[i4 - 1] : messageFromLunarHolidayCache;
    }

    void setChildMarginBottom(View view, int i) {
        GridLayout.LayoutParams layoutParams = (GridLayout.LayoutParams) view.getLayoutParams();
        layoutParams.bottomMargin = i;
        view.setLayoutParams(layoutParams);
    }

    Drawable getSelectedStateDrawable(View view) {
        Drawable background = view.getBackground();
        if (!(background instanceof StateListDrawable)) {
            return null;
        }
        StateListDrawable stateListDrawable = (StateListDrawable) background;
        stateListDrawable.setState(new int[]{R.attr.state_selected});
        return stateListDrawable.getCurrent();
    }

    private String getMessageFromLunarHolidayCache(int i, int i2) {
        return this.mLunarHolidayCache.get(generateCacheKey(i, i2));
    }

    private String getMessageFromSolarHolidayCache(int i, int i2) {
        return this.mSolarHolidayCache.get(generateCacheKey(i, i2));
    }
}
