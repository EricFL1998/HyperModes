package com.android.deskclock.console;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IAlarmConsoleV1Callback extends IInterface {
    public static final String DESCRIPTOR = "com.android.deskclock.console.IAlarmConsoleV1Callback";

    public static class Default implements IAlarmConsoleV1Callback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.deskclock.console.IAlarmConsoleV1Callback
        public void dismiss(int i) throws RemoteException {
        }

        @Override // com.android.deskclock.console.IAlarmConsoleV1Callback
        public void snooze(int i) throws RemoteException {
        }
    }

    void dismiss(int i) throws RemoteException;

    void snooze(int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IAlarmConsoleV1Callback {
        static final int TRANSACTION_dismiss = 2;
        static final int TRANSACTION_snooze = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IAlarmConsoleV1Callback.DESCRIPTOR);
        }

        public static IAlarmConsoleV1Callback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IAlarmConsoleV1Callback.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IAlarmConsoleV1Callback)) {
                return (IAlarmConsoleV1Callback) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IAlarmConsoleV1Callback.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IAlarmConsoleV1Callback.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                snooze(parcel.readInt());
                parcel2.writeNoException();
            } else if (i == 2) {
                dismiss(parcel.readInt());
                parcel2.writeNoException();
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements IAlarmConsoleV1Callback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAlarmConsoleV1Callback.DESCRIPTOR;
            }

            @Override // com.android.deskclock.console.IAlarmConsoleV1Callback
            public void snooze(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IAlarmConsoleV1Callback.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.deskclock.console.IAlarmConsoleV1Callback
            public void dismiss(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IAlarmConsoleV1Callback.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
