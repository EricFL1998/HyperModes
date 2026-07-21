package com.android.deskclock.console;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class ConsoleAlarm implements Parcelable {
    public static final Parcelable.Creator<ConsoleAlarm> CREATOR = new Parcelable.Creator<ConsoleAlarm>() { // from class: com.android.deskclock.console.ConsoleAlarm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConsoleAlarm createFromParcel(Parcel parcel) {
            return new ConsoleAlarm(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConsoleAlarm[] newArray(int i) {
            return new ConsoleAlarm[i];
        }
    };
    private long alertTime;
    private int id;
    private String label;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ConsoleAlarm(int i, long j, String str) {
        this.id = i;
        this.alertTime = j;
        this.label = str;
    }

    public ConsoleAlarm() {
    }

    protected ConsoleAlarm(Parcel parcel) {
        this.id = parcel.readInt();
        this.alertTime = parcel.readLong();
        this.label = parcel.readString();
    }

    public int getId() {
        return this.id;
    }

    public long getAlertTime() {
        return this.alertTime;
    }

    public String getLabel() {
        return this.label;
    }

    public void setId(int i) {
        this.id = i;
    }

    public void setAlertTime(long j) {
        this.alertTime = j;
    }

    public void setLabel(String str) {
        this.label = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeLong(this.alertTime);
        parcel.writeString(this.label);
    }
}
