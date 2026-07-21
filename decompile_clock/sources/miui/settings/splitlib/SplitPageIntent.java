package miui.settings.splitlib;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class SplitPageIntent implements Parcelable {
    public static final Parcelable.Creator<SplitPageIntent> CREATOR = new Parcelable.Creator<SplitPageIntent>() { // from class: miui.settings.splitlib.SplitPageIntent.1
        @Override // android.os.Parcelable.Creator
        public SplitPageIntent createFromParcel(Parcel parcel) {
            return new SplitPageIntent(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public SplitPageIntent[] newArray(int i) {
            return new SplitPageIntent[i];
        }
    };
    private Intent mIntent;
    private String mSplitPagePath;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SplitPageIntent(Intent intent, String str) {
        this.mIntent = intent;
        this.mSplitPagePath = str;
    }

    protected SplitPageIntent(Parcel parcel) {
        this.mIntent = (Intent) parcel.readParcelable(Intent.class.getClassLoader());
        this.mSplitPagePath = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mIntent, i);
        parcel.writeString(this.mSplitPagePath);
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public String getSplitPagePath() {
        return this.mSplitPagePath;
    }
}
