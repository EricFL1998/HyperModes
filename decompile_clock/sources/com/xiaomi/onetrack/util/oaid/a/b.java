package com.xiaomi.onetrack.util.oaid.a;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* JADX INFO: loaded from: classes2.dex */
public interface b extends IInterface {
    String a();

    boolean b();

    public static final class a implements b {
        private IBinder a;

        public a(IBinder iBinder) {
            this.a = iBinder;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this.a;
        }

        @Override // com.xiaomi.onetrack.util.oaid.a.b
        public String a() {
            String string;
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            try {
                parcelObtain.writeInterfaceToken("com.uodis.opendevice.aidl.OpenDeviceIdentifierService");
                this.a.transact(1, parcelObtain, parcelObtain2, 0);
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

        @Override // com.xiaomi.onetrack.util.oaid.a.b
        public boolean b() {
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            boolean z = false;
            try {
                parcelObtain.writeInterfaceToken("com.uodis.opendevice.aidl.OpenDeviceIdentifierService");
                this.a.transact(1, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                if (parcelObtain2.readInt() == 0) {
                    z = true;
                }
            } catch (Throwable unused) {
                parcelObtain.recycle();
                parcelObtain2.recycle();
            }
            parcelObtain.recycle();
            parcelObtain2.recycle();
            return z;
        }
    }
}
