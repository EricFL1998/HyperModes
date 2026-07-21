package miuix.appcompat.app;

import miuix.pickerwidget.widget.TimePicker;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarDatePickerDialogCallBack {
    private int mDayOfMonth;
    private int mHourOfDay;
    private int mMinute;
    private int mMonth;
    private int mYear;
    private String mLunarMessage = null;
    private int mSpinnerSelection = 0;

    public CalendarDatePickerDialogCallBack(CalendarDatePickerDialog calendarDatePickerDialog) {
        this.mYear = calendarDatePickerDialog.getCalendarDatePicker().getYear();
        this.mMonth = calendarDatePickerDialog.getCalendarDatePicker().getMonth();
        this.mDayOfMonth = calendarDatePickerDialog.getCalendarDatePicker().getDayOfMonth();
        TimePicker timePicker = calendarDatePickerDialog.getTimePicker();
        if (timePicker != null) {
            this.mHourOfDay = timePicker.getCurrentHour().intValue();
            this.mMinute = timePicker.getCurrentMinute().intValue();
        }
    }

    public int getYear() {
        return this.mYear;
    }

    public int getMonth() {
        return this.mMonth;
    }

    public int getDayOfMonth() {
        return this.mDayOfMonth;
    }

    public int getHourOfDay() {
        return this.mHourOfDay;
    }

    public int getMinute() {
        return this.mMinute;
    }

    public String getLunarMessage() {
        return this.mLunarMessage;
    }

    public int getSpinnerSelection() {
        return this.mSpinnerSelection;
    }

    public void setLunarMessage(String str) {
        this.mLunarMessage = str;
    }

    public void setSpinnerSelection(int i) {
        this.mSpinnerSelection = i;
    }

    public void setTime(int i, int i2) {
        this.mHourOfDay = i;
        this.mMinute = i2;
    }

    public void setDate(int i, int i2, int i3) {
        this.mYear = i;
        this.mMonth = i2;
        this.mDayOfMonth = i3;
    }
}
