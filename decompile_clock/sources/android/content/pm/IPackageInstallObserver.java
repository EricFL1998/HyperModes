package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IPackageInstallObserver extends IInterface {
    public static final String DESCRIPTOR = "android.content.pm.IPackageInstallObserver";

    public static class Default implements IPackageInstallObserver {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.content.pm.IPackageInstallObserver
        public void packageInstalled(String str, int i) throws RemoteException {
        }
    }

    void packageInstalled(String str, int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IPackageInstallObserver {
        static final int TRANSACTION_packageInstalled = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IPackageInstallObserver.DESCRIPTOR);
        }

        public static IPackageInstallObserver asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IPackageInstallObserver.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IPackageInstallObserver)) {
                return (IPackageInstallObserver) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IPackageInstallObserver.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IPackageInstallObserver.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                packageInstalled(parcel.readString(), parcel.readInt());
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }

        private static class Proxy implements IPackageInstallObserver {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPackageInstallObserver.DESCRIPTOR;
            }

            @Override // android.content.pm.IPackageInstallObserver
            public void packageInstalled(String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPackageInstallObserver.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
