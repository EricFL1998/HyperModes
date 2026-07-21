package com.miui.miwallpaper;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.RemoteViews;

/* JADX INFO: loaded from: classes2.dex */
public interface IMiuiKeyguardWallpaperCallback extends IInterface {
    void onRemoteViewChange(RemoteViews remoteViews, RemoteViews remoteViews2) throws RemoteException;

    public static abstract class Stub extends Binder implements IMiuiKeyguardWallpaperCallback {
        private static final String DESCRIPTOR = "com.miui.miwallpaper.IMiuiKeyguardWallpaperCallback";
        static final int TRANSACTION_onRemoteViewChange = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMiuiKeyguardWallpaperCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IMiuiKeyguardWallpaperCallback)) {
                return (IMiuiKeyguardWallpaperCallback) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i != 1) {
                if (i == 1598968902) {
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel.enforceInterface(DESCRIPTOR);
            onRemoteViewChange(parcel.readInt() != 0 ? (RemoteViews) RemoteViews.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? (RemoteViews) RemoteViews.CREATOR.createFromParcel(parcel) : null);
            parcel2.writeNoException();
            return true;
        }

        private static class Proxy implements IMiuiKeyguardWallpaperCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.miui.miwallpaper.IMiuiKeyguardWallpaperCallback
            public void onRemoteViewChange(RemoteViews remoteViews, RemoteViews remoteViews2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (remoteViews != null) {
                        parcelObtain.writeInt(1);
                        remoteViews.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (remoteViews2 != null) {
                        parcelObtain.writeInt(1);
                        remoteViews2.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
