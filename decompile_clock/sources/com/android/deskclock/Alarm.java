package com.android.deskclock;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import androidx.loader.content.CursorLoader;
import com.android.deskclock.addition.holiday.HolidayHelper;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public final class Alarm implements Parcelable, Cloneable {
    public static final int ALARM_TYPE = 0;
    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() { // from class: com.android.deskclock.Alarm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Alarm createFromParcel(Parcel parcel) {
            return new Alarm(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Alarm[] newArray(int i) {
            return new Alarm[i];
        }
    };
    private static final int DAY_MAP_SIZE = 5;
    public static final int SHIFT_ALARM_TYPE = 2;
    public static final int TIMER_TYPE = 1;
    public Uri alert;
    public DaysOfWeek daysOfWeek;
    public boolean deleteAfterUse;
    public boolean enabled;
    public int hour;
    public int id;
    public String label;
    public int minutes;
    public int seconds;
    public boolean silent;
    public long skipTime;
    public long time;
    public int type;
    public boolean vibrate;

    public static class Columns implements BaseColumns {
        public static final int ALARM_ALERT_INDEX = 8;
        public static final int ALARM_DAYS_OF_WEEK_INDEX = 3;
        public static final int ALARM_ENABLED_INDEX = 5;
        public static final int ALARM_HOUR_INDEX = 1;
        public static final int ALARM_ID_INDEX = 0;
        public static final int ALARM_MESSAGE_INDEX = 7;
        public static final int ALARM_MINUTES_INDEX = 2;
        public static final int ALARM_SKIP_TIME_INDEX = 11;
        public static final String ALARM_TIME = "alarmtime";
        public static final int ALARM_TIME_INDEX = 4;
        public static final int ALARM_TYPE_INDEX = 9;
        public static final int ALARM_VIBRATE_INDEX = 6;
        public static final String ALERT = "alert";
        public static final String DAYS_OF_WEEK = "daysofweek";
        public static final String DEFAULT_SORT_ORDER = "hour, minutes ASC";
        public static final String DELETE_AFTER_USE = "deleteAfterUse";
        public static final int DELETE_AFTER_USE_INDEX = 10;
        public static final String ENABLED = "enabled";
        public static final String HOUR = "hour";
        public static final String MESSAGE = "message";
        public static final String MINUTES = "minutes";
        public static final String SKIP_TIME = "skiptime";
        public static final String TYPE = "type";
        public static final String VIBRATE = "vibrate";
        public static final String WHERE_ENABLED = "enabled=1";
        public static final String WHERE_ENABLED_AND_NON_REPEAT = "enabled=1 AND daysofweek=0";
        public static final String WHERE_ENABLED_OR_SKIPPED = "type=0 AND (enabled=1 OR skiptime!=0)";
        public static final String WHERE_NORMAL_ALARM = "type=0";
        public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/alarm");
        public static final String EXTERNAL_ID = "external_id";
        public static final String[] ALARM_QUERY_COLUMNS = {"_id", "hour", "minutes", "daysofweek", "alarmtime", "enabled", "vibrate", "message", "alert", "type", "deleteAfterUse", "skiptime", EXTERNAL_ID};
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public Alarm m78clone() {
        try {
            return (Alarm) super.clone();
        } catch (CloneNotSupportedException e) {
            Log.e("Fail to clone the Alarm", e);
            return null;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeInt(this.enabled ? 1 : 0);
        parcel.writeInt(this.hour);
        parcel.writeInt(this.minutes);
        parcel.writeInt(this.seconds);
        parcel.writeInt(this.daysOfWeek.getCoded());
        parcel.writeLong(this.time);
        parcel.writeInt(this.vibrate ? 1 : 0);
        parcel.writeString(this.label);
        parcel.writeParcelable(this.alert, i);
        parcel.writeInt(this.silent ? 1 : 0);
        parcel.writeInt(this.deleteAfterUse ? 1 : 0);
        parcel.writeLong(this.skipTime);
        parcel.writeInt(this.type);
    }

    public Alarm(Cursor cursor) {
        this.id = cursor.getInt(0);
        this.enabled = cursor.getInt(5) == 1;
        this.hour = cursor.getInt(1);
        this.minutes = cursor.getInt(2);
        this.daysOfWeek = new DaysOfWeek(cursor.getInt(3));
        this.time = cursor.getLong(4);
        this.vibrate = cursor.getInt(6) == 1;
        this.label = cursor.getString(7);
        this.deleteAfterUse = cursor.getInt(10) == 1;
        String string = cursor.getString(8);
        this.skipTime = cursor.getLong(11);
        this.type = cursor.getInt(9);
        if ("silent".equals(string)) {
            Log.v("Alarm is marked as silent");
            this.silent = true;
            return;
        }
        if (string != null && string.length() != 0) {
            this.alert = Uri.parse(string);
        }
        if (this.alert == null) {
            this.alert = RingtoneManager.getDefaultUri(4);
        }
    }

    public Alarm(Parcel parcel) {
        this.id = parcel.readInt();
        this.enabled = parcel.readInt() == 1;
        this.hour = parcel.readInt();
        this.minutes = parcel.readInt();
        this.seconds = parcel.readInt();
        this.daysOfWeek = new DaysOfWeek(parcel.readInt());
        this.time = parcel.readLong();
        this.vibrate = parcel.readInt() == 1;
        this.label = parcel.readString();
        this.alert = (Uri) parcel.readParcelable(null);
        this.silent = parcel.readInt() == 1;
        this.deleteAfterUse = parcel.readInt() == 1;
        this.skipTime = parcel.readLong();
        this.type = parcel.readInt();
    }

    public Alarm(Parcel parcel, int i) {
        this.id = parcel.readInt();
        this.enabled = parcel.readInt() == 1;
        this.hour = parcel.readInt();
        this.minutes = parcel.readInt();
        this.seconds = parcel.readInt();
        this.daysOfWeek = new DaysOfWeek(parcel.readInt());
        this.time = parcel.readLong();
        this.vibrate = parcel.readInt() == 1;
        this.label = parcel.readString();
        this.alert = (Uri) parcel.readParcelable(null);
        this.silent = parcel.readInt() == 1;
        this.deleteAfterUse = parcel.readInt() == 1;
        if (i != 1) {
            this.skipTime = parcel.readLong();
            this.type = parcel.readInt();
        }
    }

    public Alarm() {
        this.id = -1;
        this.enabled = true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        this.hour = calendar.get(11);
        this.minutes = calendar.get(12);
        this.seconds = calendar.get(13);
        this.vibrate = true;
        this.daysOfWeek = new DaysOfWeek(0);
        this.alert = RingtoneManager.getDefaultUri(4);
        this.label = "";
        this.deleteAfterUse = false;
    }

    public String getLabelOrDefault(Context context) {
        String str = this.label;
        if (str == null || str.length() == 0) {
            return context.getString(R.string.default_label);
        }
        return this.label;
    }

    public String getTimerLabelOrDefault(Context context) {
        String str = this.label;
        if (str == null || str.length() == 0) {
            return context.getString(R.string.timer_title);
        }
        return this.label;
    }

    public String getLabel(Context context) {
        String str = this.label;
        if (str == null || str.length() == 0) {
            return "";
        }
        return this.label;
    }

    public int hashCode() {
        return this.id;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Alarm) && this.id == ((Alarm) obj).id;
    }

    public static CursorLoader getAlarmsCursorLoader(Context context) {
        return new CursorLoader(context, Columns.CONTENT_URI, Columns.ALARM_QUERY_COLUMNS, null, null, Columns.DEFAULT_SORT_ORDER);
    }

    public static final class DaysOfWeek {
        public static final int ALARM_TYPE_EVERY_DAY = 1;
        public static final int ALARM_TYPE_EVERY_DAY_INTERNATIONAL = 1;
        public static final int ALARM_TYPE_LEGAL_OFF_DAY = 3;
        public static final int ALARM_TYPE_LEGAL_WORKDAY = 2;
        public static final int ALARM_TYPE_MONDAY_TO_FRIDAY = 4;
        public static final int ALARM_TYPE_MONDAY_TO_FRIDAY_INTERNATIONAL = 2;
        public static final int ALARM_TYPE_ONLY_INTERNATIONAL = 0;
        public static final int ALARM_TYPE_ONLY_ONCE = 0;
        public static final int ALARM_TYPE_SELF_DEFINE = 5;
        public static final int ALARM_TYPE_SELF_DEFINE_INTERNATIONAL = 3;
        public static final int ALARM_TYPE_SHIFT_ALARM = 6;
        private static int[] DAY_MAP = {2, 3, 4, 5, 6, 7, 1};
        public static final int EVERY_DAY = 127;
        public static final int LEGAL_OFF_DAY = 256;
        public static final int LEGAL_WORK_DAY = 128;
        public static final int MONDAY_TO_FRIDAY = 31;
        public static final int NO_DAY = 0;
        public static final int SHIFT_DAY = 512;
        public static final int WEEKENDS = 96;
        private int mDays;

        public DaysOfWeek(int i) {
            this.mDays = i;
        }

        public boolean isShiftAlarm() {
            return this.mDays == 512;
        }

        public String toString(Context context, boolean z) {
            StringBuilder sb = new StringBuilder();
            int i = this.mDays;
            if (i == 0) {
                return context.getText(R.string.never).toString();
            }
            if (i == 127) {
                return context.getText(R.string.every_day).toString();
            }
            if (i == 128 || i == 256) {
                int i2 = i == 128 ? R.string.legal_workday : R.string.legal_off_day;
                if (HolidayHelper.isHolidayDataInvalid(context)) {
                    i2 = R.string.legal_workday_invalidate;
                }
                return context.getText(i2).toString();
            }
            if (i == 512) {
                return context.getText(R.string.shift_alarm).toString();
            }
            int i3 = 0;
            while (i > 0) {
                if ((i & 1) == 1) {
                    i3++;
                }
                i >>= 1;
            }
            String[] shortWeekdays = new DateFormatSymbols().getShortWeekdays();
            ArrayList arrayList = new ArrayList();
            for (int i4 = 0; i4 < 7; i4++) {
                if ((this.mDays & (1 << i4)) != 0) {
                    sb.append(shortWeekdays[DAY_MAP[i4]]);
                    arrayList.add(Integer.valueOf(i4));
                    i3--;
                    if (i3 > 0) {
                        sb.append(" ");
                    }
                }
            }
            if (arrayList.size() == 5) {
                for (int i5 = 0; i5 < 5 && ((Integer) arrayList.get(i5)).intValue() == i5; i5++) {
                    if (i5 == 4) {
                        return context.getText(R.string.monday_to_friday).toString();
                    }
                }
            }
            return sb.toString();
        }

        public int getAlarmType() {
            int i = this.mDays;
            if (i == 0) {
                return 0;
            }
            if (i == 31) {
                return 4;
            }
            if (i == 256) {
                return 3;
            }
            if (i == 512) {
                return 6;
            }
            if (i != 127) {
                return i != 128 ? 5 : 2;
            }
            return 1;
        }

        private boolean isSet(int i) {
            return ((1 << i) & this.mDays) > 0;
        }

        public void set(int i, boolean z) {
            if (z) {
                this.mDays = (1 << i) | this.mDays;
            } else {
                this.mDays = (~(1 << i)) & this.mDays;
            }
        }

        public void set(DaysOfWeek daysOfWeek) {
            this.mDays = daysOfWeek.mDays;
        }

        public int get() {
            return this.mDays;
        }

        public void setWorkDay(boolean z) {
            if (z) {
                this.mDays = 0;
                set(7, true);
            } else {
                set(7, false);
            }
        }

        public void setOffDay(boolean z) {
            if (z) {
                this.mDays = 0;
                set(8, true);
            } else {
                set(8, false);
            }
        }

        public int getCoded() {
            return this.mDays;
        }

        public boolean[] getBooleanArray() {
            boolean[] zArr = new boolean[7];
            for (int i = 0; i < 7; i++) {
                zArr[i] = isSet(i);
            }
            return zArr;
        }

        public boolean isRepeatSet() {
            return this.mDays != 0;
        }

        public int getNextAlarm(Context context, Calendar calendar) {
            int i = this.mDays;
            if (i == 0 || i == 512) {
                return -1;
            }
            int i2 = 0;
            if (i == 128 || i == 256) {
                Calendar calendar2 = (Calendar) calendar.clone();
                boolean z = this.mDays == 128;
                while (i2 < 10) {
                    if ((z && !HolidayHelper.isHoliday(context, calendar2)) || (!z && HolidayHelper.isHoliday(context, calendar2))) {
                        return i2;
                    }
                    calendar2.add(6, 1);
                    i2++;
                }
            }
            int i3 = (calendar.get(7) + 5) % 7;
            while (i2 < 7 && !isSet((i3 + i2) % 7)) {
                i2++;
            }
            return i2;
        }

        public int getNextAlarmSkipOne(Context context, Calendar calendar) {
            if (this.mDays == 0) {
                return -1;
            }
            int nextAlarm = getNextAlarm(context, calendar) + 1;
            Calendar calendar2 = (Calendar) calendar.clone();
            calendar2.add(6, nextAlarm);
            return nextAlarm + getNextAlarm(context, calendar2);
        }

        private static int convertDayToBitIndex(int i) {
            return (i + 5) % 7;
        }

        public void setDaysOfWeek(boolean z, int... iArr) {
            for (int i : iArr) {
                set(convertDayToBitIndex(i), z);
            }
        }

        public void setDay(int i) {
            this.mDays = i;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Alarm:(id:");
        StringBuilder sbAppend = sb.append(this.id).append(", enabled:").append(this.enabled).append(", hour:").append(this.hour).append(", minutes:").append(this.minutes).append(", seconds:").append(this.seconds).append(", daysOfWeek:");
        DaysOfWeek daysOfWeek = this.daysOfWeek;
        StringBuilder sbAppend2 = sbAppend.append(daysOfWeek == null ? "null" : Integer.valueOf(daysOfWeek.getCoded())).append(", time:");
        long j = this.time;
        StringBuilder sbAppend3 = sbAppend2.append(j == 0 ? 0 : Util.formatTimeForLog(j)).append(", vibrate:").append(this.vibrate).append(", alert:");
        Uri uri = this.alert;
        sbAppend3.append(uri != null ? uri.toString() : "null").append(", silent:").append(this.silent).append(", deleteAfterUse:").append(this.deleteAfterUse).append(", skipTime:").append(this.skipTime).append("type:").append(this.type).append(")");
        return sb.toString();
    }
}
