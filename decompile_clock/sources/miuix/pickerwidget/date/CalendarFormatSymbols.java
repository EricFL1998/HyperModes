package miuix.pickerwidget.date;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;
import com.android.deskclock.R2;
import java.util.Locale;
import miuix.core.util.SoftReferenceSingleton;
import miuix.pickerwidget.R;

/* JADX INFO: loaded from: classes3.dex */
public class CalendarFormatSymbols {
    private static SoftReferenceSingleton<CalendarFormatSymbols> INSTANCE;
    private Resources mResources;

    private CalendarFormatSymbols(Context context) {
        this.mResources = context.getResources();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateResource(Context context) {
        this.mResources = context.getResources();
    }

    public static CalendarFormatSymbols getOrCreate(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SoftReferenceSingleton<CalendarFormatSymbols>() { // from class: miuix.pickerwidget.date.CalendarFormatSymbols.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // miuix.core.util.SoftReferenceSingleton
                public CalendarFormatSymbols createInstance(Object obj) {
                    return new CalendarFormatSymbols((Context) obj);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // miuix.core.util.SoftReferenceSingleton
                public void updateInstance(CalendarFormatSymbols calendarFormatSymbols, Object obj) {
                    super.updateInstance(calendarFormatSymbols, obj);
                    calendarFormatSymbols.updateResource((Context) obj);
                }
            };
        }
        return INSTANCE.get(context);
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public String[] getSolarTerms() {
        return this.mResources.getStringArray(R.array.solar_terms);
    }

    public String[] getChineseDays() {
        return this.mResources.getStringArray(R.array.chinese_days);
    }

    public SparseArray<String> getSolarHolidays() {
        SparseArray<String> sparseArray = new SparseArray<>();
        sparseArray.put(101, this.mResources.getString(R.string.solar_chinese_holiday_101));
        sparseArray.put(110, this.mResources.getString(R.string.solar_chinese_holiday_110));
        sparseArray.put(R2.attr.actionBarShareIcon, this.mResources.getString(R.string.solar_chinese_holiday_214));
        sparseArray.put(308, this.mResources.getString(R.string.solar_chinese_holiday_308));
        sparseArray.put(312, this.mResources.getString(R.string.solar_chinese_holiday_312));
        sparseArray.put(315, this.mResources.getString(R.string.solar_chinese_holiday_315));
        sparseArray.put(401, this.mResources.getString(R.string.solar_chinese_holiday_401));
        sparseArray.put(501, this.mResources.getString(R.string.solar_chinese_holiday_501));
        sparseArray.put(504, this.mResources.getString(R.string.solar_chinese_holiday_504));
        sparseArray.put(512, this.mResources.getString(R.string.solar_chinese_holiday_512));
        sparseArray.put(601, this.mResources.getString(R.string.solar_chinese_holiday_601));
        sparseArray.put(701, this.mResources.getString(R.string.solar_chinese_holiday_701));
        sparseArray.put(707, this.mResources.getString(R.string.solar_chinese_holiday_707));
        sparseArray.put(R2.attr.day_background_color, this.mResources.getString(R.string.solar_chinese_holiday_801));
        sparseArray.put(903, this.mResources.getString(R.string.solar_chinese_holiday_903));
        sparseArray.put(R2.attr.effectiveScreenOrientation, this.mResources.getString(R.string.solar_chinese_holiday_910));
        sparseArray.put(R2.attr.emptyStateView, this.mResources.getString(R.string.solar_chinese_holiday_918));
        sparseArray.put(1001, this.mResources.getString(R.string.solar_chinese_holiday_1001));
        sparseArray.put(R2.attr.firstBaselineToTopHeight, this.mResources.getString(R.string.solar_chinese_holiday_1031));
        sparseArray.put(R2.attr.indicatorColor, this.mResources.getString(R.string.solar_chinese_holiday_1213));
        sparseArray.put(R2.attr.isAutoDpi, this.mResources.getString(R.string.solar_chinese_holiday_1224));
        sparseArray.put(R2.attr.isLightTheme, this.mResources.getString(R.string.solar_chinese_holiday_1225));
        return sparseArray;
    }

    public SparseArray<String> getLunarHolidays() {
        SparseArray<String> sparseArray = new SparseArray<>();
        sparseArray.put(R2.attr.dependency, this.mResources.getString(R.string.lunar_chinese_holiday_815));
        return sparseArray;
    }

    public String[] getDetailedAmPms() {
        return this.mResources.getStringArray(R.array.detailed_am_pms);
    }

    public String[] getAmPms() {
        return this.mResources.getStringArray(R.array.am_pms);
    }

    public String[] getChineseDigits() {
        return this.mResources.getStringArray(R.array.chinese_digits);
    }

    public String[] getChineseLeapMonths() {
        return this.mResources.getStringArray(R.array.chinese_leap_months);
    }

    public String[] getChineseMonths() {
        return this.mResources.getStringArray(R.array.chinese_months);
    }

    public String[] getEarthlyBranches() {
        return this.mResources.getStringArray(R.array.earthly_branches);
    }

    public String[] getShortMonths() {
        return this.mResources.getStringArray(R.array.months_short);
    }

    public String[] getShortestMonths() {
        return this.mResources.getStringArray(R.array.months_shortest);
    }

    public String[] getMonths() {
        return this.mResources.getStringArray(R.array.months);
    }

    public String[] getHeavenlyStems() {
        return this.mResources.getStringArray(R.array.heavenly_stems);
    }

    public String[] getChineseSymbolAnimals() {
        return this.mResources.getStringArray(R.array.chinese_symbol_animals);
    }

    public String[] getEras() {
        return this.mResources.getStringArray(R.array.eras);
    }

    public String[] getShortWeekDays() {
        return this.mResources.getStringArray(R.array.week_days_short);
    }

    public String[] getShortestWeekDays() {
        return this.mResources.getStringArray(R.array.week_days_shortest);
    }

    public String[] getWeekDays() {
        return this.mResources.getStringArray(R.array.week_days);
    }
}
