package miuix.navigator;

import android.os.Parcel;
import android.os.Parcelable;
import miuix.responsive.map.ResponsiveState;

/* JADX INFO: loaded from: classes3.dex */
public class NavigatorStrategy implements Parcelable {
    public static final Parcelable.Creator<NavigatorStrategy> CREATOR = new Parcelable.Creator<NavigatorStrategy>() { // from class: miuix.navigator.NavigatorStrategy.1
        @Override // android.os.Parcelable.Creator
        public NavigatorStrategy createFromParcel(Parcel parcel) {
            return new NavigatorStrategy(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public NavigatorStrategy[] newArray(int i) {
            return new NavigatorStrategy[i];
        }
    };
    private Navigator.Mode mCompactMode;
    private boolean mIgnoreSaveInstance;
    private Navigator.Mode mLargeMode;
    private Navigator.Mode mLargeModeInFold;
    private Navigator.Mode mRegularMode;
    private Navigator.Mode mRegularModeInFold;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected NavigatorStrategy(Parcel parcel) {
        this.mIgnoreSaveInstance = true;
        this.mCompactMode = Navigator.Mode.C;
        this.mRegularMode = Navigator.Mode.NLC;
        this.mRegularModeInFold = Navigator.Mode.LC;
        this.mLargeMode = Navigator.Mode.NLC;
        this.mLargeModeInFold = Navigator.Mode.LC;
        this.mIgnoreSaveInstance = parcel.readByte() != 0;
        this.mCompactMode = Navigator.Mode.values()[parcel.readInt()];
        this.mRegularMode = Navigator.Mode.values()[parcel.readInt()];
        this.mRegularModeInFold = Navigator.Mode.values()[parcel.readInt()];
        this.mLargeMode = Navigator.Mode.values()[parcel.readInt()];
        this.mLargeModeInFold = Navigator.Mode.values()[parcel.readInt()];
    }

    public NavigatorStrategy() {
        this.mIgnoreSaveInstance = true;
        this.mCompactMode = Navigator.Mode.C;
        this.mRegularMode = Navigator.Mode.NLC;
        this.mRegularModeInFold = Navigator.Mode.LC;
        this.mLargeMode = Navigator.Mode.NLC;
        this.mLargeModeInFold = Navigator.Mode.LC;
    }

    public NavigatorStrategy setCompactMode(Navigator.Mode mode) {
        this.mCompactMode = mode;
        return this;
    }

    public NavigatorStrategy setRegularMode(Navigator.Mode mode, Navigator.Mode mode2) {
        this.mRegularMode = mode;
        this.mRegularModeInFold = mode2;
        return this;
    }

    public NavigatorStrategy setLargeMode(Navigator.Mode mode) {
        return setLargeMode(mode, mode);
    }

    public NavigatorStrategy setLargeMode(Navigator.Mode mode, Navigator.Mode mode2) {
        this.mLargeMode = mode;
        this.mLargeModeInFold = mode2;
        return this;
    }

    public NavigatorStrategy setIgnoreSaveInstance(boolean z) {
        this.mIgnoreSaveInstance = z;
        return this;
    }

    public boolean isIgnoreSaveInstance() {
        return this.mIgnoreSaveInstance;
    }

    public void updateStrategyOnNavigationModeChanged(ResponsiveState responsiveState, int i, Navigator.Mode mode) {
        int type = responsiveState.getType();
        if (type == 2) {
            if (this.mRegularModeInFold == this.mRegularMode) {
                this.mRegularModeInFold = mode;
                this.mRegularMode = mode;
                return;
            } else if (i == 3) {
                this.mRegularModeInFold = mode;
                return;
            } else {
                this.mRegularMode = mode;
                return;
            }
        }
        if (type == 3) {
            if (this.mLargeModeInFold == this.mLargeMode) {
                this.mLargeModeInFold = mode;
                this.mLargeMode = mode;
                return;
            } else if (i == 3) {
                this.mLargeModeInFold = mode;
                return;
            } else {
                this.mLargeMode = mode;
                return;
            }
        }
        this.mCompactMode = mode;
    }

    public Navigator.Mode getCurrentMode(ResponsiveState responsiveState, int i) {
        int type = responsiveState.getType();
        if (type == 2) {
            if (i == 3) {
                return this.mRegularModeInFold;
            }
            return this.mRegularMode;
        }
        if (type != 3) {
            return this.mCompactMode;
        }
        if (i == 3) {
            return this.mLargeModeInFold;
        }
        return this.mLargeMode;
    }

    public static NavigatorStrategy createSimpleStrategy(Navigator.Mode mode) {
        NavigatorStrategy navigatorStrategy = new NavigatorStrategy();
        navigatorStrategy.setCompactMode(mode);
        navigatorStrategy.setRegularMode(mode, mode);
        navigatorStrategy.setLargeMode(mode, mode);
        return navigatorStrategy;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.mIgnoreSaveInstance ? (byte) 1 : (byte) 0);
        parcel.writeInt(this.mCompactMode.ordinal());
        parcel.writeInt(this.mRegularMode.ordinal());
        parcel.writeInt(this.mRegularModeInFold.ordinal());
        parcel.writeInt(this.mLargeMode.ordinal());
        parcel.writeInt(this.mLargeModeInFold.ordinal());
    }
}
