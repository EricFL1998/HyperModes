package com.android.deskclock.alarm.shiftalarm;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarm implements Parcelable {
    public static final Parcelable.Creator<ShiftAlarm> CREATOR = new Parcelable.Creator<ShiftAlarm>() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ShiftAlarm createFromParcel(Parcel parcel) {
            return new ShiftAlarm(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ShiftAlarm[] newArray(int i) {
            return new ShiftAlarm[i];
        }
    };
    public int alarmId;
    public int duration;
    public boolean enable;
    public long groupId;
    public int hour;
    public int id;
    public int index;
    public int minutes;
    public long skipTime;

    public static class Columns {
        public static final String ALARM_COUNT = "alarmCount";
        public static final String ALARM_ID = "alarmid";
        public static final String ALERT = "alert";
        public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/shift_alarms");
        public static final String DURATION = "duration";
        public static final String ENABLE = "enable";
        public static final String GROUP_ID = "groupid";
        public static final String HOUR = "hour";
        public static final String ID = "_id";
        public static final String MESSAGE = "message";
        public static final String MINUTES = "minutes";
        public static final String SHIFT_INDEX = "shiftindex";
        public static final String SKIP_INDEX = "skipindex";
        public static final String SKIP_TIME = "skiptime";
        public static final String START_TIME = "starttime";
        public static final String TYPE = "type";
        public static final String VIBRATE = "vibrate";
    }

    public static class Index {
        public static final int ALARM_COUNT = 13;
        public static final int ALARM_ID = 10;
        public static final int ALERT = 3;
        public static final int DURATION = 2;
        public static final int ENABLE = 4;
        public static final int GROUP_ID = 9;
        public static final int HOUR = 14;
        public static final int ID = 0;
        public static final int MESSAGE = 6;
        public static final int MINUTES = 15;
        public static final int SHIFT_INDEX = 11;
        public static final int SKIP_INDEX = 7;
        public static final int SKIP_TIME = 8;
        public static final int START_TIME = 1;
        public static final int TYPE = 12;
        public static final int VIBRATE = 5;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected ShiftAlarm(Parcel parcel) {
        this.id = parcel.readInt();
        this.groupId = parcel.readLong();
        this.index = parcel.readInt();
        this.duration = parcel.readInt();
        this.enable = parcel.readByte() != 0;
        this.skipTime = parcel.readLong();
        this.hour = parcel.readInt();
        this.minutes = parcel.readInt();
        this.alarmId = parcel.readInt();
    }

    public ShiftAlarm copy() {
        ShiftAlarm shiftAlarm = new ShiftAlarm(this.index);
        shiftAlarm.id = this.id;
        shiftAlarm.groupId = this.groupId;
        shiftAlarm.duration = this.duration;
        shiftAlarm.skipTime = this.skipTime;
        shiftAlarm.enable = this.enable;
        shiftAlarm.index = this.index;
        shiftAlarm.hour = this.hour;
        shiftAlarm.minutes = this.minutes;
        shiftAlarm.alarmId = this.alarmId;
        return shiftAlarm;
    }

    public ShiftAlarm(int i) {
        this.id = -1;
        this.index = i;
        Calendar calendar = Calendar.getInstance();
        this.hour = calendar.get(11);
        this.minutes = calendar.get(12);
        this.alarmId = -1;
        this.enable = true;
    }

    public static List<ShiftAlarm> fromCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        arrayList.add(new ShiftAlarm(cursor));
                    } while (cursor.moveToNext());
                }
            } finally {
                try {
                    cursor.close();
                } catch (Exception unused) {
                }
            }
        }
        return arrayList;
    }

    public ShiftAlarm(Cursor cursor) {
        this.id = cursor.getInt(0);
        this.groupId = cursor.getLong(9);
        this.index = cursor.getInt(11);
        this.enable = cursor.getInt(4) == 1;
        this.alarmId = cursor.getInt(10);
        this.hour = cursor.getInt(14);
        this.minutes = cursor.getInt(15);
    }

    public String toString() {
        return "index" + this.index + ", hour:" + this.hour + " minutes:" + this.minutes + " enable:" + this.enable + " alarmId：" + this.alarmId;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeLong(this.groupId);
        parcel.writeInt(this.index);
        parcel.writeInt(this.duration);
        parcel.writeByte(this.enable ? (byte) 1 : (byte) 0);
        parcel.writeLong(this.skipTime);
        parcel.writeInt(this.hour);
        parcel.writeInt(this.minutes);
        parcel.writeInt(this.alarmId);
    }
}
