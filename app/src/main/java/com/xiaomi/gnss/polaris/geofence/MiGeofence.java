package com.xiaomi.gnss.polaris.geofence;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public class MiGeofence implements Parcelable {
    public static final int CONFIDENCE_HIGH = 3;
    public static final int CONFIDENCE_LOW = 1;
    public static final int CONFIDENCE_MIDDLE = 2;
    public static final Parcelable.Creator<MiGeofence> CREATOR = new Parcelable.Creator<MiGeofence>() { // from class: com.xiaomi.gnss.polaris.geofence.MiGeofence.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MiGeofence createFromParcel(Parcel parcel) {
            return new MiGeofence(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MiGeofence[] newArray(int i10) {
            return new MiGeofence[i10];
        }
    };
    public static final int GEO_EVENT_ENTERED = 11;
    public static final int GEO_EVENT_EXITED = 12;
    public static final int GEO_EVENT_UNKNOWN = 20;
    public static final int TRANSITION_TYPE_ENTER = 1;
    public static final int TRANSITION_TYPE_ENTER_AND_EXIT = 3;
    public static final int TRANSITION_TYPE_EXIT = 2;
    public static final int TRIGGER_ALWAYS = 0;
    public static final int TRIGGER_IGNORE_FIRST = 1;
    private int confidence;

    /* JADX INFO: renamed from: id, reason: collision with root package name */
    private String f36203id;
    private double latitude;
    private double longitude;
    private String packageName;
    private int radius;
    private int transitionType;

    protected MiGeofence(Parcel parcel) {
        readFromParcel(parcel);
    }

    public static boolean equalsSettings(MiGeofence miGeofence, MiGeofence miGeofence2) {
        return Double.compare(miGeofence.latitude, miGeofence2.latitude) == 0 && Double.compare(miGeofence.longitude, miGeofence2.longitude) == 0 && miGeofence.radius == miGeofence2.radius && miGeofence.transitionType == miGeofence2.transitionType && miGeofence.confidence == miGeofence2.confidence && Objects.equals(miGeofence.f36203id, miGeofence2.f36203id);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MiGeofence miGeofence = (MiGeofence) obj;
        return equalsSettings(this, miGeofence) && Objects.equals(this.f36203id, miGeofence.f36203id) && Objects.equals(this.packageName, miGeofence.packageName);
    }

    public int getConfidence() {
        return this.confidence;
    }

    public String getId() {
        return this.f36203id;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public int getRadius() {
        return this.radius;
    }

    public int getTransitionType() {
        return this.transitionType;
    }

    public int hashCode() {
        return Objects.hash(this.f36203id, Double.valueOf(this.latitude), Double.valueOf(this.longitude), Integer.valueOf(this.radius), Integer.valueOf(this.transitionType), Integer.valueOf(this.confidence), this.packageName);
    }

    public void readFromParcel(Parcel parcel) {
        this.f36203id = parcel.readString();
        this.latitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
        this.radius = parcel.readInt();
        this.transitionType = parcel.readInt();
        this.confidence = parcel.readInt();
        this.packageName = parcel.readString();
    }

    public void setConfidence(int i10) {
        this.confidence = i10;
    }

    public void setId(String str) {
        this.f36203id = str;
    }

    public void setLatitude(double d10) {
        this.latitude = d10;
    }

    public void setLongitude(double d10) {
        this.longitude = d10;
    }

    public void setRadius(int i10) {
        this.radius = i10;
    }

    public void setTransitionType(int i10) {
        this.transitionType = i10;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i10) {
        parcel.writeString(this.f36203id);
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
        parcel.writeInt(this.radius);
        parcel.writeInt(this.transitionType);
        parcel.writeInt(this.confidence);
        parcel.writeString(this.packageName);
    }

    public MiGeofence() {
    }
}
