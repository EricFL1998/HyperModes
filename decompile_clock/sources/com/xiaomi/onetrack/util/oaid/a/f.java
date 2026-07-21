package com.xiaomi.onetrack.util.oaid.a;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* JADX INFO: loaded from: classes2.dex */
public interface f extends IInterface {
    String a();

    public static class a implements f {
        private IBinder a;

        public a(IBinder iBinder) {
            this.a = iBinder;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this.a;
        }

        @Override // com.xiaomi.onetrack.util.oaid.a.f
        public String a() {
            String string;
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            try {
                parcelObtain.writeInterfaceToken("com.samsung.android.deviceidservice.IDeviceIdService");
                this.a.transact(1, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                string = parcelObtain2.readString();
            } catch (Throwable th) {
                parcelObtain2.recycle();
                parcelObtain.recycle();
                th.printStackTrace();
                string = null;
            }
            parcelObtain2.recycle();
            parcelObtain.recycle();
            return string;
        }
    }

    public static abstract class b extends Binder implements f {
        public b() {
            attachInterface(this, "com.samsung.android.deviceidservice.IDeviceIdService");
        }

        public f a(IBinder iBinder) {
            if (iBinder == null || iBinder.queryLocalInterface("com.samsung.android.deviceidservice.IDeviceIdService") == null) {
                return null;
            }
            return new a(iBinder);
        }
    }
}
