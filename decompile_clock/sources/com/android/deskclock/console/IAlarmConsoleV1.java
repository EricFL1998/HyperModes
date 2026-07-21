package com.android.deskclock.console;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IAlarmConsoleV1 extends IInterface {
    public static final String DESCRIPTOR = "com.android.deskclock.console.IAlarmConsoleV1";

    public static class Default implements IAlarmConsoleV1 {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.deskclock.console.IAlarmConsoleV1
        public void onAlert(ConsoleAlarm consoleAlarm) throws RemoteException {
        }

        @Override // com.android.deskclock.console.IAlarmConsoleV1
        public void onDismiss(ConsoleAlarm consoleAlarm) throws RemoteException {
        }

        @Override // com.android.deskclock.console.IAlarmConsoleV1
        public void onSnooze(ConsoleAlarm consoleAlarm) throws RemoteException {
        }

        @Override // com.android.deskclock.console.IAlarmConsoleV1
        public void registerConsoleCallback(IAlarmConsoleV1Callback iAlarmConsoleV1Callback) throws RemoteException {
        }

        @Override // com.android.deskclock.console.IAlarmConsoleV1
        public void unregisterConsoleCallback() throws RemoteException {
        }
    }

    void onAlert(ConsoleAlarm consoleAlarm) throws RemoteException;

    void onDismiss(ConsoleAlarm consoleAlarm) throws RemoteException;

    void onSnooze(ConsoleAlarm consoleAlarm) throws RemoteException;

    void registerConsoleCallback(IAlarmConsoleV1Callback iAlarmConsoleV1Callback) throws RemoteException;

    void unregisterConsoleCallback() throws RemoteException;

    public static abstract class Stub extends Binder implements IAlarmConsoleV1 {
        static final int TRANSACTION_onAlert = 3;
        static final int TRANSACTION_onDismiss = 4;
        static final int TRANSACTION_onSnooze = 5;
        static final int TRANSACTION_registerConsoleCallback = 1;
        static final int TRANSACTION_unregisterConsoleCallback = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IAlarmConsoleV1.DESCRIPTOR);
        }

        public static IAlarmConsoleV1 asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IAlarmConsoleV1.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IAlarmConsoleV1)) {
                return (IAlarmConsoleV1) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IAlarmConsoleV1.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IAlarmConsoleV1.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                registerConsoleCallback(IAlarmConsoleV1Callback.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
            } else if (i == 2) {
                unregisterConsoleCallback();
                parcel2.writeNoException();
            } else if (i == 3) {
                onAlert((ConsoleAlarm) _Parcel.readTypedObject(parcel, ConsoleAlarm.CREATOR));
                parcel2.writeNoException();
            } else if (i == 4) {
                onDismiss((ConsoleAlarm) _Parcel.readTypedObject(parcel, ConsoleAlarm.CREATOR));
                parcel2.writeNoException();
            } else if (i == 5) {
                onSnooze((ConsoleAlarm) _Parcel.readTypedObject(parcel, ConsoleAlarm.CREATOR));
                parcel2.writeNoException();
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements IAlarmConsoleV1 {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAlarmConsoleV1.DESCRIPTOR;
            }

            @Override // com.android.deskclock.console.IAlarmConsoleV1
            public void registerConsoleCallback(IAlarmConsoleV1Callback iAlarmConsoleV1Callback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IAlarmConsoleV1.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iAlarmConsoleV1Callback);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.deskclock.console.IAlarmConsoleV1
            public void unregisterConsoleCallback() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IAlarmConsoleV1.DESCRIPTOR);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.deskclock.console.IAlarmConsoleV1
            public void onAlert(ConsoleAlarm consoleAlarm) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IAlarmConsoleV1.DESCRIPTOR);
                    _Parcel.writeTypedObject(parcelObtain, consoleAlarm, 0);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.deskclock.console.IAlarmConsoleV1
            public void onDismiss(ConsoleAlarm consoleAlarm) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IAlarmConsoleV1.DESCRIPTOR);
                    _Parcel.writeTypedObject(parcelObtain, consoleAlarm, 0);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.deskclock.console.IAlarmConsoleV1
            public void onSnooze(ConsoleAlarm consoleAlarm) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IAlarmConsoleV1.DESCRIPTOR);
                    _Parcel.writeTypedObject(parcelObtain, consoleAlarm, 0);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }

    public static class _Parcel {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T> T readTypedObject(Parcel parcel, Parcelable.Creator<T> creator) {
            if (parcel.readInt() != 0) {
                return creator.createFromParcel(parcel);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void writeTypedObject(Parcel parcel, T t, int i) {
            if (t != null) {
                parcel.writeInt(1);
                t.writeToParcel(parcel, i);
            } else {
                parcel.writeInt(0);
            }
        }
    }
}
