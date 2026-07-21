package com.android.deskclock.alarm.shiftalarm;

import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.TimeUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarmGroup implements Parcelable {
    public static final Parcelable.Creator<ShiftAlarmGroup> CREATOR = new Parcelable.Creator<ShiftAlarmGroup>() { // from class: com.android.deskclock.alarm.shiftalarm.ShiftAlarmGroup.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ShiftAlarmGroup createFromParcel(Parcel parcel) {
            return new ShiftAlarmGroup(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ShiftAlarmGroup[] newArray(int i) {
            return new ShiftAlarmGroup[i];
        }
    };
    private static int DEFAULT_DURATION = 4;
    private static String TAG = "DC:ShiftAlarmGroup";
    public static final int TYPE_SHIFT_ALARM = 0;
    public static final int TYPE_SHIFT_GROUP = 1;
    public int alarmCount;
    public Set<Integer> alarmIds;
    public Uri alert;
    public long alertTime;
    public int duration;
    public boolean enable;
    public long groupId;
    public int id;
    public String label;
    public List<ShiftAlarm> shiftAlarms;
    public boolean silent;
    public int skipIndex;
    public long skipTime;
    public long startTime;
    public boolean vibrate;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected ShiftAlarmGroup(Parcel parcel) {
        this.skipIndex = -1;
        this.shiftAlarms = new ArrayList();
        this.alarmIds = new HashSet();
        this.id = parcel.readInt();
        this.startTime = parcel.readLong();
        this.alertTime = parcel.readLong();
        this.duration = parcel.readInt();
        this.alarmCount = parcel.readInt();
        this.alert = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.silent = parcel.readByte() != 0;
        this.enable = parcel.readByte() != 0;
        this.vibrate = parcel.readByte() != 0;
        this.label = parcel.readString();
        this.skipIndex = parcel.readInt();
        this.skipTime = parcel.readLong();
        this.groupId = parcel.readLong();
        this.shiftAlarms = parcel.createTypedArrayList(ShiftAlarm.CREATOR);
        this.alarmIds = new HashSet(parcel.readArrayList(Integer.class.getClassLoader()));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeLong(this.startTime);
        parcel.writeLong(this.alertTime);
        parcel.writeInt(this.duration);
        parcel.writeInt(this.alarmCount);
        parcel.writeParcelable(this.alert, i);
        parcel.writeByte(this.silent ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.enable ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.vibrate ? (byte) 1 : (byte) 0);
        parcel.writeString(this.label);
        parcel.writeInt(this.skipIndex);
        parcel.writeLong(this.skipTime);
        parcel.writeLong(this.groupId);
        parcel.writeTypedList(this.shiftAlarms);
        parcel.writeList(new ArrayList(this.alarmIds));
    }

    public void copy(ShiftAlarmGroup shiftAlarmGroup) {
        this.id = shiftAlarmGroup.id;
        this.startTime = shiftAlarmGroup.startTime;
        this.duration = shiftAlarmGroup.duration;
        this.alert = shiftAlarmGroup.alert;
        this.silent = shiftAlarmGroup.silent;
        this.enable = shiftAlarmGroup.enable;
        this.vibrate = shiftAlarmGroup.vibrate;
        this.alarmCount = shiftAlarmGroup.alarmCount;
        this.groupId = shiftAlarmGroup.groupId;
        this.label = shiftAlarmGroup.label;
        this.shiftAlarms = new ArrayList();
        Iterator<ShiftAlarm> it = shiftAlarmGroup.shiftAlarms.iterator();
        while (it.hasNext()) {
            this.shiftAlarms.add(it.next().copy());
        }
        resetAlarmIds();
        this.skipIndex = shiftAlarmGroup.skipIndex;
        this.skipTime = shiftAlarmGroup.skipTime;
    }

    public ShiftAlarmGroup copy() {
        ShiftAlarmGroup shiftAlarmGroup = new ShiftAlarmGroup();
        shiftAlarmGroup.copy(this);
        return shiftAlarmGroup;
    }

    private ShiftAlarmGroup() {
        this.skipIndex = -1;
        this.shiftAlarms = new ArrayList();
        this.alarmIds = new HashSet();
        this.id = -1;
        this.groupId = -1L;
        int i = DEFAULT_DURATION;
        this.duration = i;
        this.alarmCount = i;
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        this.startTime = calendar.getTimeInMillis();
        this.enable = true;
    }

    public void resetAlarmIds() {
        this.alarmIds.clear();
        for (ShiftAlarm shiftAlarm : this.shiftAlarms) {
            if (shiftAlarm.id > 0 && shiftAlarm.alarmId > 0) {
                this.alarmIds.add(Integer.valueOf(shiftAlarm.alarmId));
            }
        }
    }

    public static ShiftAlarmGroup getDefault() {
        ShiftAlarmGroup shiftAlarmGroup = new ShiftAlarmGroup();
        ArrayList arrayList = new ArrayList();
        for (int i = 1; i <= shiftAlarmGroup.duration; i++) {
            arrayList.add(new ShiftAlarm(i));
        }
        shiftAlarmGroup.shiftAlarms = arrayList;
        return shiftAlarmGroup;
    }

    public static List<ShiftAlarmGroup> fromCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        arrayList.add(new ShiftAlarmGroup(cursor));
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

    public boolean equals(Object obj) {
        return (obj instanceof ShiftAlarmGroup) && this.id == ((ShiftAlarmGroup) obj).id;
    }

    private ShiftAlarmGroup(Cursor cursor) {
        this.skipIndex = -1;
        this.shiftAlarms = new ArrayList();
        this.alarmIds = new HashSet();
        this.id = cursor.getInt(0);
        this.startTime = cursor.getLong(1);
        this.duration = cursor.getInt(2);
        String string = cursor.getString(3);
        if ("silent".equals(string)) {
            Log.v("Alarm is marked as silent");
            this.silent = true;
        } else {
            if (string != null && string.length() != 0) {
                this.alert = Uri.parse(string);
            }
            if (this.alert == null) {
                this.alert = RingtoneManager.getDefaultUri(4);
            }
        }
        this.skipTime = cursor.getLong(8);
        this.skipIndex = cursor.getInt(7);
        this.enable = cursor.getInt(4) == 1;
        this.vibrate = cursor.getInt(5) == 1;
        this.alarmCount = cursor.getInt(13);
        this.label = cursor.getString(6);
        this.groupId = cursor.getLong(9);
        this.shiftAlarms = ShiftAlarm.fromCursor(DeskClockApp.getAppContext().getContentResolver().query(ShiftAlarm.Columns.CONTENT_URI, null, "type=? AND groupid=?", new String[]{String.valueOf(0), String.valueOf(this.groupId)}, null));
        int enableCount = getEnableCount();
        resetAlarmIds();
        if (this.duration != this.shiftAlarms.size()) {
            Log.f(TAG, "error, quantity does not match!!! duration:" + this.duration + ", actual size:" + this.shiftAlarms.size());
            this.duration = this.shiftAlarms.size();
        }
        if (enableCount != this.alarmCount) {
            this.alarmCount = enableCount;
            Log.f(TAG, "error, quantity does not match!!! alarmCount:" + this.alarmCount + ", actual enableCount:" + enableCount);
        }
    }

    public void resetDuration(int i) {
        int i2;
        if (this.shiftAlarms == null || i == (i2 = this.duration)) {
            android.util.Log.i(TAG, "resetDuration, return");
            return;
        }
        if (i < i2) {
            for (int i3 = 0; i3 < this.duration - i; i3++) {
                if (!this.shiftAlarms.isEmpty()) {
                    List<ShiftAlarm> list = this.shiftAlarms;
                    list.remove(list.size() - 1);
                }
            }
            Iterator<ShiftAlarm> it = this.shiftAlarms.iterator();
            while (it.hasNext()) {
                if (it.next().enable) {
                }
            }
            if (!this.shiftAlarms.isEmpty()) {
                List<ShiftAlarm> list2 = this.shiftAlarms;
                list2.get(list2.size() - 1).enable = true;
            }
        } else {
            for (int i4 = i2 + 1; i4 <= i; i4++) {
                this.shiftAlarms.add(new ShiftAlarm(i4));
            }
        }
        this.duration = i;
        this.alarmCount = getEnableCount();
    }

    private int getEnableCount() {
        Iterator<ShiftAlarm> it = this.shiftAlarms.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().enable) {
                i++;
            }
        }
        return i;
    }

    public void resetAlarm(int i, int i2, boolean z, int i3) {
        if (!this.shiftAlarms.isEmpty() && this.shiftAlarms.size() >= i3 && i3 > 0) {
            int i4 = i3 - 1;
            this.shiftAlarms.get(i4).hour = i;
            this.shiftAlarms.get(i4).minutes = i2;
            this.shiftAlarms.get(i4).enable = z;
        }
        this.alarmCount = getEnableCount();
    }

    public void showLog() {
        Log.f(TAG, "------------------------shiftAlarms------------------------");
        if (this.shiftAlarms == null) {
            Log.f(TAG, "error alarms");
            return;
        }
        Log.f(TAG, toString());
        for (int i = 0; i < this.shiftAlarms.size(); i++) {
            Log.f(TAG, this.shiftAlarms.get(i).toString());
        }
    }

    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.startTime);
        return "id:" + this.id + " enable:" + this.enable + " skipTime:" + this.skipTime + " skipIndex:" + this.skipIndex + " startTime:" + TimeUtil.formatDate(DeskClockApp.getAppContext().getString(R.string.worldcolock_time_date), calendar.getTime()) + " duration:" + this.duration + " enableCount:" + this.alarmCount + " alarmIds:" + this.alarmIds;
    }
}
