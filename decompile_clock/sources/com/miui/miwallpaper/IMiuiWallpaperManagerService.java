package com.miui.miwallpaper;

import android.app.WallpaperColors;
import android.content.ComponentName;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.view.SurfaceControl;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public interface IMiuiWallpaperManagerService extends IInterface {
    void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) throws RemoteException;

    boolean blurState(int i) throws RemoteException;

    void changeMiuiWallpaperInfo(int i, Bundle bundle) throws RemoteException;

    void clearWallpaper(int i) throws RemoteException;

    void clearWallpaperForPackage(int i, String str) throws RemoteException;

    int getEffectId(int i) throws RemoteException;

    String getGalleryJson(int i) throws RemoteException;

    String getLastMiuiWallpaperType(int i) throws RemoteException;

    String getMiuiDefaultWallpaperType(int i) throws RemoteException;

    Map getMiuiLockPartWallpaperIsDeep(int i, boolean z, Map map) throws RemoteException;

    String getMiuiPresetWallpaperPath() throws RemoteException;

    WallpaperColors getMiuiWallpaperColors(int i) throws RemoteException;

    Map getMiuiWallpaperPalette(int i, boolean z, Map map) throws RemoteException;

    String getMiuiWallpaperPath(String str, int i, boolean z, boolean z2) throws RemoteException;

    ParcelFileDescriptor getMiuiWallpaperPreview(int i) throws RemoteException;

    ParcelFileDescriptor getMiuiWallpaperRotationPreview(int i, int i2) throws RemoteException;

    float getMiuiWallpaperSdkVersion() throws RemoteException;

    int getMiuiWallpaperSdkVersionCode() throws RemoteException;

    String getMiuiWallpaperType(int i) throws RemoteException;

    SurfaceControl getSurfaceControl(int i) throws RemoteException;

    int getWallpaperBlurColor(int i) throws RemoteException;

    boolean isMiuiDefaultWallpaper(int i) throws RemoteException;

    boolean isMiuiWallpaperComponentUsing(int i) throws RemoteException;

    void registerWallpaperChangeListener(IMiuiWallpaperManagerCallback iMiuiWallpaperManagerCallback, int i) throws RemoteException;

    void setGalleryRemoteView(RemoteViews remoteViews, RemoteViews remoteViews2) throws RemoteException;

    Map setMiuiWallpaper(int i, String str, String str2, ComponentName componentName, List<String> list) throws RemoteException;

    Map setWallpaper(int i, String str, String str2, ComponentName componentName, boolean z, List<String> list) throws RemoteException;

    Map setWallpaper2(int i, String str, String str2, ComponentName componentName, boolean z, boolean z2, List<String> list) throws RemoteException;

    Map setWallpaper3(int i, String str, ComponentName componentName, List<String> list, Bundle bundle) throws RemoteException;

    void showWallpaperScreenOnAnim(boolean z) throws RemoteException;

    void showWallpaperUnlockAnim() throws RemoteException;

    boolean supportDark(int i) throws RemoteException;

    void turnOffFashionGallery(int i) throws RemoteException;

    void unRegisterWallpaperChangeListener(IMiuiWallpaperManagerCallback iMiuiWallpaperManagerCallback) throws RemoteException;

    void updateKeyguardWallpaperRatio(float f, long j) throws RemoteException;

    void updateKeyguardWallpaperState(boolean z) throws RemoteException;

    void updateKeyguardWallpaperStateAnim(boolean z, boolean z2, int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IMiuiWallpaperManagerService {
        private static final String DESCRIPTOR = "com.miui.miwallpaper.IMiuiWallpaperManagerService";
        static final int TRANSACTION_bindSystemUIProxy = 16;
        static final int TRANSACTION_blurState = 33;
        static final int TRANSACTION_changeMiuiWallpaperInfo = 27;
        static final int TRANSACTION_clearWallpaper = 4;
        static final int TRANSACTION_clearWallpaperForPackage = 28;
        static final int TRANSACTION_getEffectId = 35;
        static final int TRANSACTION_getGalleryJson = 12;
        static final int TRANSACTION_getLastMiuiWallpaperType = 14;
        static final int TRANSACTION_getMiuiDefaultWallpaperType = 25;
        static final int TRANSACTION_getMiuiLockPartWallpaperIsDeep = 30;
        static final int TRANSACTION_getMiuiPresetWallpaperPath = 15;
        static final int TRANSACTION_getMiuiWallpaperColors = 5;
        static final int TRANSACTION_getMiuiWallpaperPalette = 29;
        static final int TRANSACTION_getMiuiWallpaperPath = 7;
        static final int TRANSACTION_getMiuiWallpaperPreview = 8;
        static final int TRANSACTION_getMiuiWallpaperRotationPreview = 37;
        static final int TRANSACTION_getMiuiWallpaperSdkVersion = 1;
        static final int TRANSACTION_getMiuiWallpaperSdkVersionCode = 36;
        static final int TRANSACTION_getMiuiWallpaperType = 6;
        static final int TRANSACTION_getSurfaceControl = 31;
        static final int TRANSACTION_getWallpaperBlurColor = 22;
        static final int TRANSACTION_isMiuiDefaultWallpaper = 24;
        static final int TRANSACTION_isMiuiWallpaperComponentUsing = 34;
        static final int TRANSACTION_registerWallpaperChangeListener = 9;
        static final int TRANSACTION_setGalleryRemoteView = 11;
        static final int TRANSACTION_setMiuiWallpaper = 2;
        static final int TRANSACTION_setWallpaper = 13;
        static final int TRANSACTION_setWallpaper2 = 21;
        static final int TRANSACTION_setWallpaper3 = 26;
        static final int TRANSACTION_showWallpaperScreenOnAnim = 17;
        static final int TRANSACTION_showWallpaperUnlockAnim = 20;
        static final int TRANSACTION_supportDark = 32;
        static final int TRANSACTION_turnOffFashionGallery = 3;
        static final int TRANSACTION_unRegisterWallpaperChangeListener = 10;
        static final int TRANSACTION_updateKeyguardWallpaperRatio = 19;
        static final int TRANSACTION_updateKeyguardWallpaperState = 18;
        static final int TRANSACTION_updateKeyguardWallpaperStateAnim = 23;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMiuiWallpaperManagerService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IMiuiWallpaperManagerService)) {
                return (IMiuiWallpaperManagerService) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    float miuiWallpaperSdkVersion = getMiuiWallpaperSdkVersion();
                    parcel2.writeNoException();
                    parcel2.writeFloat(miuiWallpaperSdkVersion);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    int i3 = parcel.readInt();
                    String string = parcel.readString();
                    String string2 = parcel.readString();
                    ComponentName componentName = parcel.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(parcel) : null;
                    ArrayList arrayList = new ArrayList();
                    Map miuiWallpaper = setMiuiWallpaper(i3, string, string2, componentName, arrayList);
                    parcel2.writeNoException();
                    parcel2.writeMap(miuiWallpaper);
                    parcel2.writeStringList(arrayList);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    turnOffFashionGallery(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    clearWallpaper(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    WallpaperColors miuiWallpaperColors = getMiuiWallpaperColors(parcel.readInt());
                    parcel2.writeNoException();
                    if (miuiWallpaperColors != null) {
                        parcel2.writeInt(1);
                        miuiWallpaperColors.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    String miuiWallpaperType = getMiuiWallpaperType(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeString(miuiWallpaperType);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    String miuiWallpaperPath = getMiuiWallpaperPath(parcel.readString(), parcel.readInt(), parcel.readInt() != 0, parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeString(miuiWallpaperPath);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    ParcelFileDescriptor miuiWallpaperPreview = getMiuiWallpaperPreview(parcel.readInt());
                    parcel2.writeNoException();
                    if (miuiWallpaperPreview != null) {
                        parcel2.writeInt(1);
                        miuiWallpaperPreview.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerWallpaperChangeListener(IMiuiWallpaperManagerCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    unRegisterWallpaperChangeListener(IMiuiWallpaperManagerCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    setGalleryRemoteView(parcel.readInt() != 0 ? (RemoteViews) RemoteViews.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? (RemoteViews) RemoteViews.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    String galleryJson = getGalleryJson(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeString(galleryJson);
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    int i4 = parcel.readInt();
                    String string3 = parcel.readString();
                    String string4 = parcel.readString();
                    ComponentName componentName2 = parcel.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(parcel) : null;
                    boolean z = parcel.readInt() != 0;
                    ArrayList arrayList2 = new ArrayList();
                    Map wallpaper = setWallpaper(i4, string3, string4, componentName2, z, arrayList2);
                    parcel2.writeNoException();
                    parcel2.writeMap(wallpaper);
                    parcel2.writeStringList(arrayList2);
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    String lastMiuiWallpaperType = getLastMiuiWallpaperType(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeString(lastMiuiWallpaperType);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    String miuiPresetWallpaperPath = getMiuiPresetWallpaperPath();
                    parcel2.writeNoException();
                    parcel2.writeString(miuiPresetWallpaperPath);
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    bindSystemUIProxy(IMiuiKeyguardWallpaperCallback.Stub.asInterface(parcel.readStrongBinder()));
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    showWallpaperScreenOnAnim(parcel.readInt() != 0);
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    updateKeyguardWallpaperState(parcel.readInt() != 0);
                    return true;
                case 19:
                    parcel.enforceInterface(DESCRIPTOR);
                    updateKeyguardWallpaperRatio(parcel.readFloat(), parcel.readLong());
                    return true;
                case 20:
                    parcel.enforceInterface(DESCRIPTOR);
                    showWallpaperUnlockAnim();
                    return true;
                case 21:
                    parcel.enforceInterface(DESCRIPTOR);
                    int i5 = parcel.readInt();
                    String string5 = parcel.readString();
                    String string6 = parcel.readString();
                    ComponentName componentName3 = parcel.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(parcel) : null;
                    boolean z2 = parcel.readInt() != 0;
                    boolean z3 = parcel.readInt() != 0;
                    ArrayList arrayList3 = new ArrayList();
                    Map wallpaper2 = setWallpaper2(i5, string5, string6, componentName3, z2, z3, arrayList3);
                    parcel2.writeNoException();
                    parcel2.writeMap(wallpaper2);
                    parcel2.writeStringList(arrayList3);
                    return true;
                case 22:
                    parcel.enforceInterface(DESCRIPTOR);
                    int wallpaperBlurColor = getWallpaperBlurColor(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(wallpaperBlurColor);
                    return true;
                case 23:
                    parcel.enforceInterface(DESCRIPTOR);
                    updateKeyguardWallpaperStateAnim(parcel.readInt() != 0, parcel.readInt() != 0, parcel.readInt());
                    return true;
                case 24:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsMiuiDefaultWallpaper = isMiuiDefaultWallpaper(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsMiuiDefaultWallpaper ? 1 : 0);
                    return true;
                case 25:
                    parcel.enforceInterface(DESCRIPTOR);
                    String miuiDefaultWallpaperType = getMiuiDefaultWallpaperType(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeString(miuiDefaultWallpaperType);
                    return true;
                case 26:
                    parcel.enforceInterface(DESCRIPTOR);
                    int i6 = parcel.readInt();
                    String string7 = parcel.readString();
                    ComponentName componentName4 = parcel.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(parcel) : null;
                    ArrayList arrayList4 = new ArrayList();
                    Map wallpaper3 = setWallpaper3(i6, string7, componentName4, arrayList4, parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeMap(wallpaper3);
                    parcel2.writeStringList(arrayList4);
                    return true;
                case 27:
                    parcel.enforceInterface(DESCRIPTOR);
                    changeMiuiWallpaperInfo(parcel.readInt(), parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 28:
                    parcel.enforceInterface(DESCRIPTOR);
                    clearWallpaperForPackage(parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 29:
                    parcel.enforceInterface(DESCRIPTOR);
                    Map miuiWallpaperPalette = getMiuiWallpaperPalette(parcel.readInt(), parcel.readInt() != 0, parcel.readHashMap(getClass().getClassLoader()));
                    parcel2.writeNoException();
                    parcel2.writeMap(miuiWallpaperPalette);
                    return true;
                case 30:
                    parcel.enforceInterface(DESCRIPTOR);
                    Map miuiLockPartWallpaperIsDeep = getMiuiLockPartWallpaperIsDeep(parcel.readInt(), parcel.readInt() != 0, parcel.readHashMap(getClass().getClassLoader()));
                    parcel2.writeNoException();
                    parcel2.writeMap(miuiLockPartWallpaperIsDeep);
                    return true;
                case 31:
                    parcel.enforceInterface(DESCRIPTOR);
                    SurfaceControl surfaceControl = getSurfaceControl(parcel.readInt());
                    parcel2.writeNoException();
                    if (surfaceControl != null) {
                        parcel2.writeInt(1);
                        surfaceControl.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 32:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zSupportDark = supportDark(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zSupportDark ? 1 : 0);
                    return true;
                case 33:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zBlurState = blurState(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zBlurState ? 1 : 0);
                    return true;
                case 34:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsMiuiWallpaperComponentUsing = isMiuiWallpaperComponentUsing(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsMiuiWallpaperComponentUsing ? 1 : 0);
                    return true;
                case 35:
                    parcel.enforceInterface(DESCRIPTOR);
                    int effectId = getEffectId(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(effectId);
                    return true;
                case 36:
                    parcel.enforceInterface(DESCRIPTOR);
                    int miuiWallpaperSdkVersionCode = getMiuiWallpaperSdkVersionCode();
                    parcel2.writeNoException();
                    parcel2.writeInt(miuiWallpaperSdkVersionCode);
                    return true;
                case 37:
                    parcel.enforceInterface(DESCRIPTOR);
                    ParcelFileDescriptor miuiWallpaperRotationPreview = getMiuiWallpaperRotationPreview(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    if (miuiWallpaperRotationPreview != null) {
                        parcel2.writeInt(1);
                        miuiWallpaperRotationPreview.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IMiuiWallpaperManagerService {
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

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public float getMiuiWallpaperSdkVersion() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readFloat();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public Map setMiuiWallpaper(int i, String str, String str2, ComponentName componentName, List<String> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    if (componentName != null) {
                        parcelObtain.writeInt(1);
                        componentName.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    HashMap hashMap = parcelObtain2.readHashMap(getClass().getClassLoader());
                    parcelObtain2.readStringList(list);
                    return hashMap;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void turnOffFashionGallery(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void clearWallpaper(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public WallpaperColors getMiuiWallpaperColors(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? (WallpaperColors) WallpaperColors.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public String getMiuiWallpaperType(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public String getMiuiWallpaperPath(String str, int i, boolean z, boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public ParcelFileDescriptor getMiuiWallpaperPreview(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void registerWallpaperChangeListener(IMiuiWallpaperManagerCallback iMiuiWallpaperManagerCallback, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iMiuiWallpaperManagerCallback != null ? iMiuiWallpaperManagerCallback.asBinder() : null);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void unRegisterWallpaperChangeListener(IMiuiWallpaperManagerCallback iMiuiWallpaperManagerCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iMiuiWallpaperManagerCallback != null ? iMiuiWallpaperManagerCallback.asBinder() : null);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void setGalleryRemoteView(RemoteViews remoteViews, RemoteViews remoteViews2) throws RemoteException {
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
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public String getGalleryJson(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public Map setWallpaper(int i, String str, String str2, ComponentName componentName, boolean z, List<String> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    if (componentName != null) {
                        parcelObtain.writeInt(1);
                        componentName.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    HashMap hashMap = parcelObtain2.readHashMap(getClass().getClassLoader());
                    parcelObtain2.readStringList(list);
                    return hashMap;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public String getLastMiuiWallpaperType(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public String getMiuiPresetWallpaperPath() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iMiuiKeyguardWallpaperCallback != null ? iMiuiKeyguardWallpaperCallback.asBinder() : null);
                    this.mRemote.transact(16, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void showWallpaperScreenOnAnim(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(17, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void updateKeyguardWallpaperState(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(18, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void updateKeyguardWallpaperRatio(float f, long j) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeFloat(f);
                    parcelObtain.writeLong(j);
                    this.mRemote.transact(19, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void showWallpaperUnlockAnim() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public Map setWallpaper2(int i, String str, String str2, ComponentName componentName, boolean z, boolean z2, List<String> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    if (componentName != null) {
                        parcelObtain.writeInt(1);
                        componentName.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(21, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    HashMap hashMap = parcelObtain2.readHashMap(getClass().getClassLoader());
                    parcelObtain2.readStringList(list);
                    return hashMap;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public int getWallpaperBlurColor(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(22, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void updateKeyguardWallpaperStateAnim(boolean z, boolean z2, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(23, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public boolean isMiuiDefaultWallpaper(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(24, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public String getMiuiDefaultWallpaperType(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(25, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public Map setWallpaper3(int i, String str, ComponentName componentName, List<String> list, Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    if (componentName != null) {
                        parcelObtain.writeInt(1);
                        componentName.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (bundle != null) {
                        parcelObtain.writeInt(1);
                        bundle.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(26, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    HashMap hashMap = parcelObtain2.readHashMap(getClass().getClassLoader());
                    parcelObtain2.readStringList(list);
                    return hashMap;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void changeMiuiWallpaperInfo(int i, Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (bundle != null) {
                        parcelObtain.writeInt(1);
                        bundle.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(27, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public void clearWallpaperForPackage(int i, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(28, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public Map getMiuiWallpaperPalette(int i, boolean z, Map map) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeMap(map);
                    this.mRemote.transact(29, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public Map getMiuiLockPartWallpaperIsDeep(int i, boolean z, Map map) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeMap(map);
                    this.mRemote.transact(30, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public SurfaceControl getSurfaceControl(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(31, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? (SurfaceControl) SurfaceControl.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public boolean supportDark(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(32, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public boolean blurState(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(33, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public boolean isMiuiWallpaperComponentUsing(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(34, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public int getEffectId(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(35, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public int getMiuiWallpaperSdkVersionCode() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(36, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.miwallpaper.IMiuiWallpaperManagerService
            public ParcelFileDescriptor getMiuiWallpaperRotationPreview(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(37, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
