package com.xiaomi.onetrack.util.oaid.a;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* JADX INFO: loaded from: classes2.dex */
public interface a extends IInterface {
    String a();

    /* JADX INFO: renamed from: com.xiaomi.onetrack.util.oaid.a.a$a, reason: collision with other inner class name */
    public static final class C0024a implements a {
        private IBinder a;

        public C0024a(IBinder iBinder) {
            this.a = iBinder;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this.a;
        }

        @Override // com.xiaomi.onetrack.util.oaid.a.a
        public String a() {
            String string;
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            try {
                parcelObtain.writeInterfaceToken("com.asus.msa.SupplementaryDID.IDidAidlInterface");
                this.a.transact(3, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                string = parcelObtain2.readString();
            } catch (Throwable th) {
                parcelObtain.recycle();
                parcelObtain2.recycle();
                th.printStackTrace();
                string = null;
            }
            parcelObtain.recycle();
            parcelObtain2.recycle();
            return string;
        }
    }
}
