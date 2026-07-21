package com.android.deskclock.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

/* JADX INFO: loaded from: classes.dex */
public class ParcelableUtil {
    public static String serialize(Parcelable parcelable) {
        Parcel parcelObtain = Parcel.obtain();
        parcelable.writeToParcel(parcelObtain, 0);
        byte[] bArrMarshall = parcelObtain.marshall();
        parcelObtain.recycle();
        return Base64.encodeToString(bArrMarshall, 0);
    }

    public static <T> T deserialize(String str, Parcelable.Creator<T> creator) {
        byte[] bArrDecode = Base64.decode(str, 0);
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.unmarshall(bArrDecode, 0, bArrDecode.length);
        parcelObtain.setDataPosition(0);
        return creator.createFromParcel(parcelObtain);
    }
}
