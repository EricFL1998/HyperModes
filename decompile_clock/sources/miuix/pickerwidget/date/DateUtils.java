package miuix.pickerwidget.date;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import com.android.deskclock.R2;
import java.util.TimeZone;
import miuix.core.util.Pools;
import miuix.pickerwidget.R;

/* JADX INFO: loaded from: classes3.dex */
public class DateUtils {
    private static final Pools.Pool<Calendar> CALENDAR_POOL = Pools.createSoftReferencePool(new Pools.Manager<Calendar>() { // from class: miuix.pickerwidget.date.DateUtils.1
        @Override // miuix.core.util.Pools.Manager
        public Calendar createInstance() {
            return new Calendar();
        }
    }, 1);
    public static final int FORMAT_12HOUR = 16;
    public static final int FORMAT_24HOUR = 32;
    public static final int FORMAT_ABBREV_ALL = 28672;
    public static final int FORMAT_ABBREV_MONTH = 4096;
    public static final int FORMAT_ABBREV_TIME = 16384;
    public static final int FORMAT_ABBREV_WEEKDAY = 8192;
    public static final int FORMAT_NO_AM_PM = 64;
    public static final int FORMAT_NUMERIC_DATE = 32768;
    public static final int FORMAT_SHOW_BRIEF_TIME = 12;
    public static final int FORMAT_SHOW_DATE = 896;
    public static final int FORMAT_SHOW_HOUR = 8;
    public static final int FORMAT_SHOW_MILLISECOND = 1;
    public static final int FORMAT_SHOW_MINUTE = 4;
    public static final int FORMAT_SHOW_MONTH = 256;
    public static final int FORMAT_SHOW_MONTH_DAY = 128;
    public static final int FORMAT_SHOW_SECOND = 2;
    public static final int FORMAT_SHOW_TIME = 15;
    public static final int FORMAT_SHOW_TIME_ZONE = 2048;
    public static final int FORMAT_SHOW_WEEKDAY = 1024;
    public static final int FORMAT_SHOW_YEAR = 512;

    protected DateUtils() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static String formatDateTime(Context context, long j, int i) {
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        String string = formatDateTime(context, sbAcquire, j, i, null).toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string;
    }

    public static String formatDateTime(Context context, long j, int i, TimeZone timeZone) {
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        String string = formatDateTime(context, sbAcquire, j, i, timeZone).toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string;
    }

    public static StringBuilder formatDateTime(Context context, StringBuilder sb, long j, int i) {
        return formatDateTime(context, sb, j, i, null);
    }

    public static StringBuilder formatDateTime(Context context, StringBuilder sb, long j, int i, TimeZone timeZone) {
        if ((i & 16) == 0 && (i & 32) == 0) {
            i |= DateFormat.is24HourFormat(context) ? 32 : 16;
        }
        String string = context.getString(getFormatResId(i));
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        Calendar calendarAcquire = CALENDAR_POOL.acquire();
        calendarAcquire.setTimeZone(timeZone);
        calendarAcquire.setTimeInMillis(j);
        int length = string.length();
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = string.charAt(i2);
            if (cCharAt == 'D') {
                sbAcquire.append(context.getString(getDatePatternResId(i)));
            } else if (cCharAt == 'T') {
                sbAcquire.append(context.getString(getTimePatternResId(calendarAcquire, i)));
            } else if (cCharAt == 'W') {
                sbAcquire.append(context.getString(getWeekdayPatternResId(i)));
            } else {
                sbAcquire.append(cCharAt);
            }
        }
        calendarAcquire.format(context, sb, sbAcquire);
        Pools.getStringBuilderPool().release(sbAcquire);
        CALENDAR_POOL.release(calendarAcquire);
        return sb;
    }

    /* JADX WARN: Code duplicated, block: B:21:0x003b A[PHI: r0
  0x003b: PHI (r0v8 int) = (r0v6 int), (r0v7 int), (r0v7 int), (r0v6 int) binds: [B:15:0x0028, B:17:0x0032, B:19:0x0036, B:13:0x0024] A[DONT_GENERATE, DONT_INLINE]] */
    private static int getTimePatternResId(Calendar calendar, int i) {
        if ((i & 16384) == 16384 && (((i & 1) != 1 || calendar.get(22) == 0) && (i & 14) != 0)) {
            int i2 = i & (-2);
            if (((i & 2) != 2 || calendar.get(21) == 0) && (i & 12) != 0) {
                i2 = i & (-4);
                if (calendar.get(20) != 0 || (i & 8) == 0) {
                    i = i2;
                } else {
                    i &= -8;
                }
            } else {
                i = i2;
            }
        }
        if ((i & 8) != 8) {
            if ((i & 4) == 4) {
                if ((i & 2) != 2) {
                    return R.string.fmt_time_minute;
                }
                if ((i & 1) == 1) {
                    return R.string.fmt_time_minute_second_millis;
                }
                return R.string.fmt_time_minute_second;
            }
            if ((i & 2) == 2) {
                if ((i & 1) == 1) {
                    return R.string.fmt_time_second_millis;
                }
                return R.string.fmt_time_second;
            }
            if ((i & 1) == 1) {
                return R.string.fmt_time_millis;
            }
            throw new IllegalArgumentException("no any time date");
        }
        if ((i & 16) != 16) {
            if ((i & 4) != 4) {
                return R.string.fmt_time_24hour;
            }
            if ((i & 2) != 2) {
                return R.string.fmt_time_24hour_minute;
            }
            if ((i & 1) == 1) {
                return R.string.fmt_time_24hour_minute_second_millis;
            }
            return R.string.fmt_time_24hour_minute_second;
        }
        if ((i & 64) == 64) {
            if ((i & 4) != 4) {
                return R.string.fmt_time_12hour;
            }
            if ((i & 2) != 2) {
                return R.string.fmt_time_12hour_minute;
            }
            if ((i & 1) == 1) {
                return R.string.fmt_time_12hour_minute_second_millis;
            }
            return R.string.fmt_time_12hour_minute_second;
        }
        if ((i & 4) != 4) {
            return R.string.fmt_time_12hour_pm;
        }
        if ((i & 2) != 2) {
            return R.string.fmt_time_12hour_minute_pm;
        }
        if ((i & 1) == 1) {
            return R.string.fmt_time_12hour_minute_second_millis_pm;
        }
        return R.string.fmt_time_12hour_minute_second_pm;
    }

    private static int getDatePatternResId(int i) {
        if ((i & 32768) == 32768) {
            if ((i & 512) == 512) {
                if ((i & 256) != 256) {
                    return R.string.fmt_date_numeric_year;
                }
                if ((i & 128) == 128) {
                    return R.string.fmt_date_numeric_year_month_day;
                }
                return R.string.fmt_date_numeric_year_month;
            }
            if ((i & 256) == 256) {
                if ((i & 128) == 128) {
                    return R.string.fmt_date_numeric_month_day;
                }
                return R.string.fmt_date_numeric_month;
            }
            if ((i & 128) == 128) {
                return R.string.fmt_date_numeric_day;
            }
            throw new IllegalArgumentException("no any time date");
        }
        if ((i & 4096) == 4096) {
            if ((i & 512) == 512) {
                if ((i & 256) != 256) {
                    return R.string.fmt_date_year;
                }
                if ((i & 128) == 128) {
                    return R.string.fmt_date_short_year_month_day;
                }
                return R.string.fmt_date_short_year_month;
            }
            if ((i & 256) == 256) {
                if ((i & 128) == 128) {
                    return R.string.fmt_date_short_month_day;
                }
                return R.string.fmt_date_short_month;
            }
            if ((i & 128) == 128) {
                return R.string.fmt_date_day;
            }
            throw new IllegalArgumentException("no any time date");
        }
        if ((i & 512) == 512) {
            if ((i & 256) != 256) {
                return R.string.fmt_date_year;
            }
            if ((i & 128) == 128) {
                return R.string.fmt_date_long_year_month_day;
            }
            return R.string.fmt_date_long_year_month;
        }
        if ((i & 256) == 256) {
            if ((i & 128) == 128) {
                return R.string.fmt_date_long_month_day;
            }
            return R.string.fmt_date_long_month;
        }
        if ((i & 128) == 128) {
            return R.string.fmt_date_day;
        }
        throw new IllegalArgumentException("no any time date");
    }

    private static int getWeekdayPatternResId(int i) {
        if ((i & 8192) == 8192) {
            return R.string.fmt_weekday_short;
        }
        return R.string.fmt_weekday_long;
    }

    private static int getFormatResId(int i) {
        if ((i & 1024) == 1024) {
            if ((i & 896) != 0) {
                if ((i & 15) != 0) {
                    return (i & 2048) == 2048 ? R.string.fmt_weekday_date_time_timezone : R.string.fmt_weekday_date_time;
                }
                return (i & 2048) == 2048 ? R.string.fmt_weekday_date_timezone : R.string.fmt_weekday_date;
            }
            if ((i & 15) != 0) {
                return (i & 2048) == 2048 ? R.string.fmt_weekday_time_timezone : R.string.fmt_weekday_time;
            }
            return (i & 2048) == 2048 ? R.string.fmt_weekday_timezone : R.string.fmt_weekday;
        }
        if ((i & 896) != 0) {
            if ((i & 15) != 0) {
                return (i & 2048) == 2048 ? R.string.fmt_date_time_timezone : R.string.fmt_date_time;
            }
            return (i & 2048) == 2048 ? R.string.fmt_date_timezone : R.string.fmt_date;
        }
        if ((i & 15) != 0) {
            return (i & 2048) == 2048 ? R.string.fmt_time_timezone : R.string.fmt_time;
        }
        return (i & 2048) == 2048 ? R.string.fmt_timezone : R.string.empty;
    }

    public static String formatRelativeTime(Context context, long j, boolean z) {
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        String string = formatRelativeTime(context, sbAcquire, j, z, null).toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string;
    }

    public static String formatRelativeTime(Context context, long j, boolean z, TimeZone timeZone) {
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        String string = formatRelativeTime(context, sbAcquire, j, z, timeZone).toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string;
    }

    public static StringBuilder formatRelativeTime(Context context, StringBuilder sb, long j, boolean z) {
        return formatRelativeTime(context, sb, j, z, null);
    }

    /* JADX WARN: Code duplicated, block: B:54:0x0134  */
    /* JADX WARN: Code duplicated, block: B:56:0x0138 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:57:0x013a  */
    /* JADX WARN: Code duplicated, block: B:58:0x013d  */
    /* JADX WARN: Code duplicated, block: B:60:0x014d A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:61:0x014f  */
    /* JADX WARN: Code duplicated, block: B:62:0x0152  */
    public static StringBuilder formatRelativeTime(Context context, StringBuilder sb, long j, boolean z, TimeZone timeZone) {
        int i;
        int i2;
        String quantityString;
        long jCurrentTimeMillis = System.currentTimeMillis();
        boolean z2 = jCurrentTimeMillis >= j;
        long jAbs = Math.abs(jCurrentTimeMillis - j) / 60000;
        Resources resources = context.getResources();
        if (jAbs <= 60 && !z) {
            if (z2) {
                if (jAbs == 60) {
                    quantityString = resources.getQuantityString(R.plurals.abbrev_a_hour_ago, 1, 1);
                } else if (jAbs == 30) {
                    quantityString = resources.getQuantityString(R.plurals.abbrev_half_hour_ago, (int) jAbs);
                } else if (jAbs == 0) {
                    quantityString = resources.getQuantityString(R.plurals.abbrev_less_than_one_minute_ago, (int) jAbs);
                } else {
                    quantityString = resources.getQuantityString(R.plurals.abbrev_num_minutes_ago, (int) jAbs);
                }
            } else if (jAbs == 60) {
                quantityString = resources.getQuantityString(R.plurals.abbrev_in_a_hour, 1, 1);
            } else if (jAbs == 30) {
                quantityString = resources.getQuantityString(R.plurals.abbrev_in_half_hour, (int) jAbs);
            } else if (jAbs == 0) {
                quantityString = resources.getQuantityString(R.plurals.abbrev_in_less_than_one_minute, (int) jAbs);
            } else {
                quantityString = resources.getQuantityString(R.plurals.abbrev_in_num_minutes, (int) jAbs);
            }
            sb.append(String.format(quantityString, Long.valueOf(jAbs)));
        } else {
            Pools.Pool<Calendar> pool = CALENDAR_POOL;
            Calendar calendarAcquire = pool.acquire();
            calendarAcquire.setTimeZone(timeZone);
            calendarAcquire.setTimeInMillis(jCurrentTimeMillis);
            int i3 = calendarAcquire.get(1);
            int i4 = calendarAcquire.get(12);
            int i5 = calendarAcquire.get(14);
            calendarAcquire.setTimeInMillis(j);
            boolean z3 = i3 == calendarAcquire.get(1);
            if (z3 && i4 == calendarAcquire.get(12)) {
                formatDateTime(context, sb, j, R2.style.PageFragmentThemeTiny, timeZone);
            } else if (z3 && Math.abs(i4 - calendarAcquire.get(12)) < 2) {
                sb.append(resources.getString(z2 ? R.string.yesterday : R.string.tomorrow));
                sb.append(' ');
                formatDateTime(context, sb, j, R2.style.PageFragmentThemeTiny, timeZone);
            } else if (z3 && Math.abs(i4 - calendarAcquire.get(12)) < 7) {
                if (z2 == (i5 > calendarAcquire.get(14))) {
                    formatDateTime(context, sb, j, R2.style.Widget_Material3_MaterialCalendar, timeZone);
                } else if (z3) {
                    if (z) {
                        i2 = R2.attr.behavior_expandedOffset;
                    } else {
                        i2 = R2.attr.barOff;
                    }
                    formatDateTime(context, sb, j, 12288 | i2, timeZone);
                } else {
                    if (z) {
                        i = R2.attr.editTextSearchStyle;
                    } else {
                        i = 896;
                    }
                    formatDateTime(context, sb, j, 12288 | i, timeZone);
                }
            } else if (z3) {
                if (z) {
                    i2 = R2.attr.behavior_expandedOffset;
                } else {
                    i2 = R2.attr.barOff;
                }
                formatDateTime(context, sb, j, 12288 | i2, timeZone);
            } else {
                if (z) {
                    i = R2.attr.editTextSearchStyle;
                } else {
                    i = 896;
                }
                formatDateTime(context, sb, j, 12288 | i, timeZone);
            }
            pool.release(calendarAcquire);
        }
        return sb;
    }
}
