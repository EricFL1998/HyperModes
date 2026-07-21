package com.xiaomi.onetrack.util.oaid.a;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* JADX INFO: loaded from: classes2.dex */
public interface g extends IInterface {
    boolean a();

    String b();

    boolean c();

    void d();

    public static abstract class a extends Binder implements g {

        /* JADX INFO: renamed from: com.xiaomi.onetrack.util.oaid.a.g$a$a, reason: collision with other inner class name */
        public static class C0028a implements g {
            private IBinder a;

            @Override // com.xiaomi.onetrack.util.oaid.a.g
            public boolean c() {
                return false;
            }

            public C0028a(IBinder iBinder) {
                this.a = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.a;
            }

            @Override // com.xiaomi.onetrack.util.oaid.a.g
            public boolean a() {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.bun.lib.MsaIdInterface");
                    this.a.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    if (parcelObtain2.readInt() != 0) {
                        return false;
                    }
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                    return true;
                } catch (Throwable th) {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                    th.printStackTrace();
                    return false;
                }
            }

            @Override // com.xiaomi.onetrack.util.oaid.a.g
            public String b() {
                String string;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.bun.lib.MsaIdInterface");
                    this.a.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    string = parcelObtain2.readString();
                } catch (Throwable unused) {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                    string = null;
                }
                parcelObtain2.recycle();
                parcelObtain.recycle();
                return string;
            }

            @Override // com.xiaomi.onetrack.util.oaid.a.g
            public void d() {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.bun.lib.MsaIdInterface");
                    this.a.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } catch (Throwable unused) {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
                parcelObtain2.recycle();
                parcelObtain.recycle();
            }
        }
    }
}
