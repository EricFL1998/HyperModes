package miuix.theme.token;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
public class MaterialDayNightToken implements Parcelable {
    public static final Parcelable.Creator<MaterialDayNightToken> CREATOR = new Parcelable.Creator<MaterialDayNightToken>() { // from class: miuix.theme.token.MaterialDayNightToken.1
        @Override // android.os.Parcelable.Creator
        public MaterialDayNightToken createFromParcel(Parcel parcel) {
            return new MaterialDayNightToken(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public MaterialDayNightToken[] newArray(int i) {
            return new MaterialDayNightToken[i];
        }
    };
    private final MaterialToken mDarkToken;
    private final MaterialToken mDefaultToken;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MaterialDayNightToken(Parcel parcel) {
        int i = parcel.readInt();
        if (i < 1) {
            this.mDefaultToken = null;
            this.mDarkToken = null;
        } else if (i == 1) {
            this.mDefaultToken = new MaterialToken(parcel);
            this.mDarkToken = null;
        } else {
            this.mDefaultToken = new MaterialToken(parcel);
            this.mDarkToken = new MaterialToken(parcel);
        }
    }

    public MaterialDayNightToken(MaterialToken materialToken) {
        this.mDefaultToken = materialToken;
        this.mDarkToken = null;
    }

    public MaterialDayNightToken(MaterialToken materialToken, MaterialToken materialToken2) {
        this.mDefaultToken = materialToken;
        this.mDarkToken = materialToken2;
    }

    public MaterialToken getToken(boolean z) {
        MaterialToken materialToken = this.mDarkToken;
        if (materialToken == null) {
            return this.mDefaultToken;
        }
        return z ? this.mDefaultToken : materialToken;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        MaterialToken materialToken = this.mDefaultToken;
        if (materialToken != null && this.mDarkToken != null) {
            parcel.writeInt(2);
            this.mDefaultToken.writeToParcel(parcel, i);
            this.mDarkToken.writeToParcel(parcel, i);
        } else if (materialToken != null) {
            parcel.writeInt(1);
            this.mDefaultToken.writeToParcel(parcel, i);
        } else if (this.mDarkToken == null) {
            parcel.writeInt(0);
        }
    }
}
