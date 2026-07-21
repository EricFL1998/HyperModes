package miuix.appcompat.app.floatingactivity.multiapp;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes2.dex */
public interface IFloatingService extends IInterface {
    Bundle callServiceMethod(int i, Bundle bundle) throws RemoteException;

    int registerServiceNotify(IServiceNotify iServiceNotify, String str) throws RemoteException;

    void unregisterServiceNotify(IServiceNotify iServiceNotify, String str) throws RemoteException;

    void upDateRemoteActivityInfo(String str, int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IFloatingService {
        private static final String DESCRIPTOR = "miuix.appcompat.app.floatingactivity.multiapp.IFloatingService";
        static final int TRANSACTION_callServiceMethod = 2;
        static final int TRANSACTION_registerServiceNotify = 3;
        static final int TRANSACTION_unregisterServiceNotify = 4;
        static final int TRANSACTION_updateActivity = 5;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IFloatingService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            if (iBinder.queryLocalInterface(DESCRIPTOR) instanceof IFloatingService) {
                return (IFloatingService) iBinder;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        protected boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (parcel2 == null) {
                return super.onTransact(i, parcel, null, i2);
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                parcel2.writeBundle(callServiceMethod(parcel.readInt(), parcel.readBundle(getClass().getClassLoader())));
                parcel2.writeNoException();
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                parcel2.writeInt(registerServiceNotify(IServiceNotify.Stub.asInterface(parcel.readStrongBinder()), parcel.readString()));
                parcel2.writeNoException();
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                unregisterServiceNotify(IServiceNotify.Stub.asInterface(parcel.readStrongBinder()), parcel.readString());
                parcel2.writeNoException();
                return true;
            }
            if (i != 5) {
                if (i == 1598968902) {
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel.enforceInterface(DESCRIPTOR);
            upDateRemoteActivityInfo(parcel.readString(), parcel.readInt());
            parcel2.writeNoException();
            return true;
        }

        private static class Proxy implements IFloatingService {
            private final IBinder mRemote;

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

            @Override // miuix.appcompat.app.floatingactivity.multiapp.IFloatingService
            public Bundle callServiceMethod(int i, Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeBundle(bundle);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    Bundle bundle2 = parcelObtain2.readBundle(getClass().getClassLoader());
                    parcelObtain2.readException();
                    return bundle2;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miuix.appcompat.app.floatingactivity.multiapp.IFloatingService
            public int registerServiceNotify(IServiceNotify iServiceNotify, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iServiceNotify == null ? null : iServiceNotify.asBinder());
                    parcelObtain.writeString(str);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    int i = parcelObtain2.readInt();
                    parcelObtain2.readException();
                    return i;
                } finally {
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                }
            }

            @Override // miuix.appcompat.app.floatingactivity.multiapp.IFloatingService
            public void upDateRemoteActivityInfo(String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                }
            }

            @Override // miuix.appcompat.app.floatingactivity.multiapp.IFloatingService
            public void unregisterServiceNotify(IServiceNotify iServiceNotify, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iServiceNotify == null ? null : iServiceNotify.asBinder());
                    parcelObtain.writeString(str);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                } finally {
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                }
            }
        }
    }
}
