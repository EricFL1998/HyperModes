package com.miui.miwallpaper.keyguard;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.miui.miwallpaper.IMiuiKeyguardWallpaperCallback;

/* JADX INFO: loaded from: classes2.dex */
public interface IMiuiKeyguardWallpaperService extends IInterface {
    void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) throws RemoteException;

    void showWallpaperScreenOnAnim(boolean z) throws RemoteException;

    void showWallpaperUnlockAnim() throws RemoteException;

    void updateKeyguardWallpaperRatio(float f, long j) throws RemoteException;

    void updateKeyguardWallpaperState(boolean z) throws RemoteException;

    public static abstract class Stub extends Binder implements IMiuiKeyguardWallpaperService {
        private static final String DESCRIPTOR = "com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService";
        static final int TRANSACTION_bindSystemUIProxy = 1;
        static final int TRANSACTION_showWallpaperScreenOnAnim = 2;
        static final int TRANSACTION_showWallpaperUnlockAnim = 5;
        static final int TRANSACTION_updateKeyguardWallpaperRatio = 4;
        static final int TRANSACTION_updateKeyguardWallpaperState = 3;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMiuiKeyguardWallpaperService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IMiuiKeyguardWallpaperService)) {
                return (IMiuiKeyguardWallpaperService) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                bindSystemUIProxy(IMiuiKeyguardWallpaperCallback.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                showWallpaperScreenOnAnim(parcel.readInt() != 0);
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                updateKeyguardWallpaperState(parcel.readInt() != 0);
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                updateKeyguardWallpaperRatio(parcel.readFloat(), parcel.readLong());
                return true;
            }
            if (i == 5) {
                parcel.enforceInterface(DESCRIPTOR);
                showWallpaperUnlockAnim();
                return true;
            }
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }

        private static class Proxy implements IMiuiKeyguardWallpaperService {
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

            @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
            public void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iMiuiKeyguardWallpaperCallback != null ? iMiuiKeyguardWallpaperCallback.asBinder() : null);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
            public void showWallpaperScreenOnAnim(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
            public void updateKeyguardWallpaperState(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
            public void updateKeyguardWallpaperRatio(float f, long j) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeFloat(f);
                    parcelObtain.writeLong(j);
                    this.mRemote.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
            public void showWallpaperUnlockAnim() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
