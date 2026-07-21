package com.android.settings.services;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IMemoryOptimizationInterface extends IInterface {
    public static final String DESCRIPTOR = "com.android.settings.services.IMemoryOptimizationInterface";

    public static class Default implements IMemoryOptimizationInterface {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.settings.services.IMemoryOptimizationInterface
        public void startMemoryOptimization(Intent intent) throws RemoteException {
        }
    }

    void startMemoryOptimization(Intent intent) throws RemoteException;

    public static abstract class Stub extends Binder implements IMemoryOptimizationInterface {
        static final int TRANSACTION_startMemoryOptimization = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IMemoryOptimizationInterface.DESCRIPTOR);
        }

        public static IMemoryOptimizationInterface asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMemoryOptimizationInterface.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IMemoryOptimizationInterface)) {
                return (IMemoryOptimizationInterface) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(IMemoryOptimizationInterface.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                parcel.enforceInterface(IMemoryOptimizationInterface.DESCRIPTOR);
                startMemoryOptimization(parcel.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }

        private static class Proxy implements IMemoryOptimizationInterface {
            public static IMemoryOptimizationInterface sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMemoryOptimizationInterface.DESCRIPTOR;
            }

            @Override // com.android.settings.services.IMemoryOptimizationInterface
            public void startMemoryOptimization(Intent intent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMemoryOptimizationInterface.DESCRIPTOR);
                    if (intent != null) {
                        parcelObtain.writeInt(1);
                        intent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (!this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().startMemoryOptimization(intent);
                    } else {
                        parcelObtain2.readException();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IMemoryOptimizationInterface iMemoryOptimizationInterface) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iMemoryOptimizationInterface == null) {
                return false;
            }
            Proxy.sDefaultImpl = iMemoryOptimizationInterface;
            return true;
        }

        public static IMemoryOptimizationInterface getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
