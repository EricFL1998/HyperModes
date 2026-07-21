package com.miui.miwallpaper;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.widget.RemoteViews;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;
import miui.os.Build;

/* JADX INFO: loaded from: classes2.dex */
public class MiuiWallpaperManager {
    public static final int BLUR_STATE_CLOSE = 0;
    public static final int BLUR_STATE_OPEN = 1;
    public static final int COUNT_LIMIT_PER_EFFECT = 10000;
    public static final int[] DEFAULT_CLOCK_TYPE_INFO;
    public static final int DEFAULT_EFFECT_ID = 0;
    public static final int DEFAULT_EFFECT_NONE_MODIFY = -1;
    public static final int DEFAULT_ORIGIN_BITMAP_WIDTH_HEIGHT = 0;
    public static final String DEFAULT_PENDING_PACKAGE = "none";
    public static final int EFFECT_ID_MIN_VALUE = 0;
    public static final String EXTRA_CAROUSEL = "carousel";
    public static final String EXTRA_CLOCK_STYLE_TYPE = "clock_style_type";
    public static final String EXTRA_CLOCK_TYPE_INFO = "clock_type_info";
    public static final String EXTRA_CONTENT = "content";
    public static final String EXTRA_EFFECT_ID = "effect_id";
    public static final String EXTRA_ENABLE_BLUR = "enable_blur";
    public static final String EXTRA_LOOP_VIDEO = "loop_video";
    public static final String EXTRA_ORIGIN_BITMAP_HEIGHT = "origin_bitmap_height";
    public static final String EXTRA_ORIGIN_BITMAP_WIDTH = "origin_bitmap_width";
    public static final String EXTRA_PENDING_PACKAGE = "pending_package";
    public static final String EXTRA_SUPPORT_DARK = "support_dark";
    public static final String EXTRA_SUPPORT_MATTING = "support_matting";
    public static final String EXTRA_VIDEO_PATH = "video_path";
    private static CountDownLatch INIT_LATCH = null;
    public static final boolean IS_FLIP_DEVICE;
    public static final boolean IS_FOLDABLE_DEVICE;
    public static final boolean IS_FOLD_DEVICE;
    public static final float MIUI1X_SDK_VERSION = 2.0f;
    public static final String MIUI_WALLPAPER_SERVICE_ACTION = "android.service.wallpaper.WallPaperControllerService";
    public static final int MI_EFFECT_TYPE_BLUR = 3;
    public static final int MI_EFFECT_TYPE_DEPTH = 2;
    public static final int MI_EFFECT_TYPE_GLASS = 1;
    public static final int MI_EFFECT_TYPE_GRID = 4;
    public static final int MI_EFFECT_TYPE_NONE = 0;
    private static final String MI_WALLPAPER_DEFAULT_BLUR_COLOR = "#80000000";
    public static final int MI_WALLPAPER_ROTATION_0 = 0;
    public static final int MI_WALLPAPER_ROTATION_180 = 180;
    public static final String MI_WALLPAPER_TYPE_DARK = "dark";
    public static final String MI_WALLPAPER_TYPE_DEFAULT = "default";
    public static final String MI_WALLPAPER_TYPE_GALLERY = "gallery";
    public static final String MI_WALLPAPER_TYPE_IMAGE = "image";
    public static final String MI_WALLPAPER_TYPE_LIVE_PICKER = "live_picker";
    public static final String MI_WALLPAPER_TYPE_MAML = "maml";
    public static final String MI_WALLPAPER_TYPE_ROTATION_IMAGE = "rotation_image";
    public static final String MI_WALLPAPER_TYPE_SENSOR = "sensor";
    public static final String MI_WALLPAPER_TYPE_SUPER_SAVE_POWER = "super_save_power";
    public static final String MI_WALLPAPER_TYPE_SUPER_WALLPAPER = "super_wallpaper";
    public static final String MI_WALLPAPER_TYPE_VIDEO = "video";
    public static final String MI_WALLPAPER_TYPE_VIDEO_GALLERY = "video_gallery";
    public static final int MI_WALLPAPER_WHICH_ALL;
    public static final int MI_WALLPAPER_WHICH_HOME = 1;
    public static final int MI_WALLPAPER_WHICH_LOCK = 2;
    public static final int MI_WALLPAPER_WHICH_NONE = 0;
    public static final int MI_WALLPAPER_WHICH_SMALL_HOME = 4;
    public static final int MI_WALLPAPER_WHICH_SMALL_LOCK = 8;
    private static final float SET_WALLPAPER_2_SDK_VERSION_LIMIT = 1.0f;
    public static final String TAG = "MiuiWallpaperManager";
    private static final Executor WALLPAPER_INIT_EXECUTOR;
    private static volatile MiuiWallpaperManager sInstance;
    private final Context mContext;
    private IBinder.DeathRecipient mDeathRecipient;
    private boolean mInitSync;
    private volatile IMiuiWallpaperManagerService mService;
    public static final String DEFAULT_WALLPAPER_PACKAGE_NAME = "com.miui.miwallpaper";
    public static final String DEFAULT_WALLPAPER_CLASS_NAME = "com.miui.miwallpaper.wallpaperservice.ImageWallpaper";
    public static final ComponentName DEFAULT_WALLPAPER_COMPONENT = new ComponentName(DEFAULT_WALLPAPER_PACKAGE_NAME, DEFAULT_WALLPAPER_CLASS_NAME);
    public static final String MAML_WALLPAPER_CLASS_NAME = "com.miui.miwallpaper.MiWallpaper";
    public static final ComponentName MAML_WALLPAPER_COMPONENT = new ComponentName(DEFAULT_WALLPAPER_PACKAGE_NAME, MAML_WALLPAPER_CLASS_NAME);
    private final ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.miui.miwallpaper.MiuiWallpaperManager.1
        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(MiuiWallpaperManager.TAG, "onServiceConnected::componentName = " + componentName + ", instance = " + MiuiWallpaperManager.sInstance + " size: " + MiuiWallpaperManager.this.mInitCallbacks.size());
            MiuiWallpaperManager.this.mService = IMiuiWallpaperManagerService.Stub.asInterface(iBinder);
            MiuiWallpaperManager.this.tryRegisterWallpaperChangeCallback();
            Iterator it = MiuiWallpaperManager.this.mInitCallbacks.iterator();
            while (it.hasNext()) {
                MiuiWallpaperManagerCallback miuiWallpaperManagerCallback = (MiuiWallpaperManagerCallback) it.next();
                if (miuiWallpaperManagerCallback != null) {
                    miuiWallpaperManagerCallback.onMiuiWallpaperInitialized(MiuiWallpaperManager.sInstance);
                }
                it.remove();
            }
            try {
                MiuiWallpaperManager.this.mDeathRecipient = new MiuiWallpaperManagerDeathRecipient(iBinder, MiuiWallpaperManager.this);
                iBinder.linkToDeath(MiuiWallpaperManager.this.mDeathRecipient, 0);
            } catch (Throwable th) {
                Log.e(MiuiWallpaperManager.TAG, "linkToDeath fail : ", th);
            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.miui.miwallpaper.MiuiWallpaperManager.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri data = intent.getData();
            if (!"android.intent.action.PACKAGE_ADDED".equals(action) || data == null) {
                return;
            }
            String schemeSpecificPart = data.getSchemeSpecificPart();
            if (MiuiWallpaperManager.DEFAULT_WALLPAPER_PACKAGE_NAME.equals(schemeSpecificPart)) {
                Log.i(MiuiWallpaperManager.TAG, "package update: action = " + action + " packageName = " + schemeSpecificPart);
                MiuiWallpaperManager.this.bindService();
            }
        }
    };
    private HashSet<MiuiWallpaperManagerCallback> mInitCallbacks = new HashSet<>();
    private final HashMap<IMiuiWallpaperManagerCallback, Integer> mWallpaperChangeCallbacks = new HashMap<>();

    public interface MiuiWallpaperManagerCallback {
        void onMiuiWallpaperInitialized(MiuiWallpaperManager miuiWallpaperManager);
    }

    public static int getSystemWhich(int i) {
        int i2 = i == 4 ? 1 : i;
        if (i == 8) {
            return 2;
        }
        return i2;
    }

    public static boolean isLockWhich(int i) {
        return i == 2 || i == 8;
    }

    static {
        boolean z = isFoldDevices() || isFlipDevice();
        IS_FOLDABLE_DEVICE = z;
        IS_FOLD_DEVICE = isFoldDevices();
        IS_FLIP_DEVICE = isFlipDevice();
        MI_WALLPAPER_WHICH_ALL = z ? 15 : 3;
        DEFAULT_CLOCK_TYPE_INFO = new int[]{-1, -1};
        WALLPAPER_INIT_EXECUTOR = Executors.newSingleThreadExecutor();
        INIT_LATCH = new CountDownLatch(1);
    }

    private static class MiuiWallpaperManagerDeathRecipient implements IBinder.DeathRecipient {
        private IBinder mBinder;
        private MiuiWallpaperManager mMiuiWallpaperManager;

        public MiuiWallpaperManagerDeathRecipient(IBinder iBinder, MiuiWallpaperManager miuiWallpaperManager) {
            this.mBinder = iBinder;
            this.mMiuiWallpaperManager = miuiWallpaperManager;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.i(MiuiWallpaperManager.TAG, "linkToDeath:MiuiWallpaperManagerService died, try rebind...");
            this.mBinder.unlinkToDeath(this, 0);
            this.mMiuiWallpaperManager.bindService();
        }
    }

    private MiuiWallpaperManager(Context context, boolean z) {
        this.mInitSync = false;
        this.mContext = context;
        this.mInitSync = z;
    }

    public static void init(Context context, MiuiWallpaperManagerCallback miuiWallpaperManagerCallback) {
        if (sInstance == null) {
            synchronized (MiuiWallpaperManager.class) {
                if (sInstance == null) {
                    Log.i(TAG, "init...");
                    sInstance = new MiuiWallpaperManager(context, false);
                    sInstance.registerBroadcast();
                    sInstance.mInitCallbacks.add(miuiWallpaperManagerCallback);
                    sInstance.bindService();
                } else if (sInstance.mService != null) {
                    miuiWallpaperManagerCallback.onMiuiWallpaperInitialized(sInstance);
                }
            }
            return;
        }
        if (sInstance.mService != null) {
            miuiWallpaperManagerCallback.onMiuiWallpaperInitialized(sInstance);
        }
    }

    public static MiuiWallpaperManager initSync(Context context, MiuiWallpaperManagerCallback miuiWallpaperManagerCallback) {
        SyncMiuiWallpaperManagerCallback syncMiuiWallpaperManagerCallback = new SyncMiuiWallpaperManagerCallback(miuiWallpaperManagerCallback);
        if (sInstance == null) {
            synchronized (MiuiWallpaperManager.class) {
                if (sInstance == null) {
                    Log.i(TAG, "initSync...");
                    sInstance = new MiuiWallpaperManager(context, true);
                    sInstance.registerBroadcast();
                    sInstance.mInitCallbacks.add(syncMiuiWallpaperManagerCallback);
                    sInstance.bindService();
                    waitBindingSuccess();
                } else if (sInstance.mService != null) {
                    syncMiuiWallpaperManagerCallback.onMiuiWallpaperInitialized(sInstance);
                } else {
                    sInstance.mInitCallbacks.add(syncMiuiWallpaperManagerCallback);
                    waitBindingSuccess();
                }
            }
        } else if (sInstance.mService != null) {
            syncMiuiWallpaperManagerCallback.onMiuiWallpaperInitialized(sInstance);
        } else {
            sInstance.mInitCallbacks.add(syncMiuiWallpaperManagerCallback);
            waitBindingSuccess();
        }
        return sInstance;
    }

    public static class SyncMiuiWallpaperManagerCallback implements MiuiWallpaperManagerCallback {
        private MiuiWallpaperManagerCallback miuiWallpaperManagerCallback;

        public SyncMiuiWallpaperManagerCallback(MiuiWallpaperManagerCallback miuiWallpaperManagerCallback) {
            this.miuiWallpaperManagerCallback = miuiWallpaperManagerCallback;
        }

        @Override // com.miui.miwallpaper.MiuiWallpaperManager.MiuiWallpaperManagerCallback
        public void onMiuiWallpaperInitialized(MiuiWallpaperManager miuiWallpaperManager) {
            MiuiWallpaperManagerCallback miuiWallpaperManagerCallback = this.miuiWallpaperManagerCallback;
            if (miuiWallpaperManagerCallback != null) {
                miuiWallpaperManagerCallback.onMiuiWallpaperInitialized(miuiWallpaperManager);
            }
            MiuiWallpaperManager.INIT_LATCH.countDown();
        }
    }

    private static void waitBindingSuccess() {
        try {
            INIT_LATCH.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initForUser(Context context, MiuiWallpaperManagerCallback miuiWallpaperManagerCallback, UserHandle userHandle) {
        if (sInstance == null) {
            synchronized (MiuiWallpaperManager.class) {
                if (sInstance == null) {
                    Log.i(TAG, "initForUser...");
                    sInstance = new MiuiWallpaperManager(context, false);
                    sInstance.registerBroadcast();
                    sInstance.mInitCallbacks.add(miuiWallpaperManagerCallback);
                    sInstance.bindServiceAsUser(userHandle);
                } else if (sInstance.mService != null) {
                    miuiWallpaperManagerCallback.onMiuiWallpaperInitialized(sInstance);
                }
            }
            return;
        }
        if (sInstance.mService != null) {
            miuiWallpaperManagerCallback.onMiuiWallpaperInitialized(sInstance);
        }
    }

    public void destroy() {
        unBindService();
        Log.e(TAG, "destroy, set sInstance null", new Exception());
        synchronized (this.mWallpaperChangeCallbacks) {
            this.mWallpaperChangeCallbacks.clear();
        }
        sInstance = null;
    }

    public static void staticDestroy() {
        if (sInstance != null) {
            sInstance.unBindService();
            Log.e(TAG, "staticDestroy, set sInstance null", new Exception());
            sInstance = null;
        }
    }

    private void unBindService() {
        if (isServiceReady()) {
            Log.e(TAG, "unBindService...");
            try {
                if (this.mDeathRecipient != null) {
                    this.mService.asBinder().unlinkToDeath(this.mDeathRecipient, 0);
                }
            } catch (Exception e) {
                Log.e(TAG, "unLinkFailed", e);
            }
            this.mContext.unregisterReceiver(this.mBroadcastReceiver);
            this.mContext.unbindService(this.mServiceConnection);
        }
    }

    public float getMiuiWallpaperSdkVersion() {
        if (!isServiceReady()) {
            return 0.0f;
        }
        try {
            return this.mService.getMiuiWallpaperSdkVersion();
        } catch (Throwable th) {
            Log.e(TAG, "getMiuiWallpaperSdkVersion fail : ", th);
            return 0.0f;
        }
    }

    public boolean setMiuiImageWallpaper(Bitmap bitmap, int i) {
        return new WallpaperApplierBuilder(this).which(i).image(bitmap).apply();
    }

    public boolean setMiuiImageWallpaper(InputStream inputStream, int i) {
        return new WallpaperApplierBuilder(this).which(i).image(inputStream).apply();
    }

    public boolean setMiuiDarkModeWallpaper(Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3, int i) {
        return new WallpaperApplierBuilder(this).which(i).dark(bitmap2, bitmap3).apply();
    }

    public boolean setMiuiDarkModeWallpaper(InputStream inputStream, InputStream inputStream2, InputStream inputStream3, int i) {
        return new WallpaperApplierBuilder(this).which(i).dark(inputStream2, inputStream3).apply();
    }

    public boolean setMiuiRotationImageWallpaper(Bitmap bitmap, Bitmap bitmap2, int i) {
        return new WallpaperApplierBuilder(this).which(i).rotationImage(bitmap, bitmap2).apply();
    }

    public boolean setMiuiRotationImageWallpaper(InputStream inputStream, InputStream inputStream2, int i) {
        return new WallpaperApplierBuilder(this).which(i).rotationImage(inputStream, inputStream2).apply();
    }

    public boolean setMiuiVideoWallpaper(String str, Bitmap bitmap, int i) {
        return new WallpaperApplierBuilder(this).which(i).video(bitmap, str, true).apply();
    }

    public boolean setMiuiVideoWallpaper(String str, InputStream inputStream, int i) {
        return new WallpaperApplierBuilder(this).which(i).video(inputStream, str, true).apply();
    }

    public boolean setMiuiVideoWallpaper(String str, Bitmap bitmap, int i, boolean z) {
        return new WallpaperApplierBuilder(this).which(i).video(bitmap, str, z).apply();
    }

    public boolean setMiuiVideoWallpaper(String str, InputStream inputStream, int i, boolean z) {
        return new WallpaperApplierBuilder(this).which(i).video(inputStream, str, z).apply();
    }

    public boolean setMiuiSensorWallpaper(String str, Bitmap bitmap, Bitmap bitmap2, int i) {
        return new WallpaperApplierBuilder(this).which(i).sensor(bitmap, bitmap2, str).apply();
    }

    public boolean setMiuiSensorWallpaper(String str, InputStream inputStream, InputStream inputStream2, int i) {
        return new WallpaperApplierBuilder(this).which(i).sensor(inputStream, inputStream2, str).apply();
    }

    public boolean setFashionGalleryWallpaper(Bitmap bitmap, int i, String str) {
        return new WallpaperApplierBuilder(this).which(i).gallery(bitmap, true, str).apply();
    }

    public boolean setFashionGalleryWallpaper(InputStream inputStream, int i, String str) {
        return new WallpaperApplierBuilder(this).which(i).gallery(inputStream, true, str).apply();
    }

    public boolean setFashionGalleryWallpaper(Bitmap bitmap, int i, String str, boolean z) {
        return new WallpaperApplierBuilder(this).which(i).gallery(bitmap, z, str).apply();
    }

    public boolean setFashionGalleryWallpaper(InputStream inputStream, int i, String str, boolean z) {
        return new WallpaperApplierBuilder(this).which(i).gallery(inputStream, z, str).apply();
    }

    public boolean setFashionGalleryVideoWallpaper(String str, Bitmap bitmap, int i, String str2, boolean z, boolean z2) {
        return new WallpaperApplierBuilder(this).which(i).videoGallery(bitmap, str, str2, z, z2).apply();
    }

    public boolean setFashionGalleryVideoWallpaper(String str, InputStream inputStream, int i, String str2, boolean z, boolean z2) {
        return new WallpaperApplierBuilder(this).which(i).videoGallery(inputStream, str, str2, z, z2).apply();
    }

    public void turnOffFashionGallery(int i) {
        if (isServiceReady() && isValidWhich(i)) {
            try {
                this.mService.turnOffFashionGallery(i);
            } catch (Throwable th) {
                Log.e(TAG, "getMiuiWallpaperColors fail : ", th);
            }
        }
    }

    public void setGalleryRemoteView(RemoteViews remoteViews, RemoteViews remoteViews2) {
        if (isServiceReady()) {
            try {
                this.mService.setGalleryRemoteView(remoteViews, remoteViews2);
            } catch (Throwable th) {
                Log.e(TAG, "getMiuiWallpaperColors fail : ", th);
            }
        }
    }

    public boolean setSuperWallpaper(ComponentName componentName, Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3, Bitmap bitmap4) {
        return new WallpaperApplierBuilder(this).superWallpaper(componentName, bitmap, bitmap2, bitmap3, bitmap4).apply();
    }

    public boolean setMamlWallpaper(Bitmap bitmap, int i) {
        return new WallpaperApplierBuilder(this).which(i).maml(bitmap).apply();
    }

    public boolean setMamlWallpaper(InputStream inputStream, int i) {
        return new WallpaperApplierBuilder(this).which(i).maml(inputStream).apply();
    }

    public void clearWallpaper(int i) {
        if (isServiceReady()) {
            try {
                if (getMiuiWallpaperSdkVersion() >= 2.0f) {
                    this.mService.clearWallpaperForPackage(i, this.mContext.getOpPackageName());
                } else {
                    this.mService.clearWallpaper(i);
                }
            } catch (Throwable th) {
                Log.e(TAG, "getMiuiWallpaperColors fail : ", th);
            }
        }
    }

    public WallpaperColors getMiuiWallpaperColors(int i) {
        if (!isServiceReady() || !isSingleWhich(i)) {
            return null;
        }
        try {
            return this.mService.getMiuiWallpaperColors(i);
        } catch (Throwable th) {
            Log.e(TAG, "getMiuiWallpaperColors fail : ", th);
            return null;
        }
    }

    public int getWallpaperBlurColor(int i) {
        if (getMiuiWallpaperSdkVersion() <= 1.0f) {
            return getBlurColorV10(i);
        }
        return getBlurColor(i);
    }

    private int getBlurColorV10(int i) {
        int fastBlurColor;
        int color = Color.parseColor(MI_WALLPAPER_DEFAULT_BLUR_COLOR);
        Bitmap miuiWallpaperPreview = getMiuiWallpaperPreview(i);
        return (miuiWallpaperPreview == null || (fastBlurColor = getFastBlurColor(miuiWallpaperPreview)) == -1) ? color : addTwoColor(fastBlurColor, color);
    }

    private int getBlurColor(int i) {
        if (isServiceReady() && isSingleWhich(i)) {
            try {
                return this.mService.getWallpaperBlurColor(i);
            } catch (Throwable th) {
                Log.e(TAG, "getMiuiWallpaperColors fail : ", th);
            }
        }
        return Color.parseColor(MI_WALLPAPER_DEFAULT_BLUR_COLOR);
    }

    public String getMiuiWallpaperType(int i) {
        if (!isServiceReady() || !isSingleWhich(i)) {
            return null;
        }
        try {
            return this.mService.getMiuiWallpaperType(i);
        } catch (Throwable th) {
            Log.e(TAG, "getMiuiWallpaperType fail : ", th);
            return null;
        }
    }

    public SurfaceControl getMiuiWallpaperSurfaceControl(int i) {
        if (!isServiceReady() || !isSingleWhich(i)) {
            return null;
        }
        try {
            return this.mService.getSurfaceControl(i);
        } catch (Throwable th) {
            Log.e(TAG, "getMiuiWallpaperType fail : ", th);
            return null;
        }
    }

    public String getLastMiuiWallpaperType(int i) {
        if (!isServiceReady() || !isSingleWhich(i)) {
            return null;
        }
        try {
            return this.mService.getLastMiuiWallpaperType(i);
        } catch (Throwable th) {
            Log.e(TAG, "getMiuiWallpaperType fail : ", th);
            return null;
        }
    }

    public String getMiuiWallpaperPath(String str, int i, boolean z, boolean z2) {
        if (!isServiceReady() || !isSingleWhich(i)) {
            return null;
        }
        try {
            return this.mService.getMiuiWallpaperPath(str, i, z, z2);
        } catch (Throwable th) {
            Log.e(TAG, "getMiuiWallpaperPath fail : ", th);
            return null;
        }
    }

    public String getDefaultLockWallpaperPath() {
        if (!isServiceReady()) {
            return null;
        }
        try {
            return this.mService.getMiuiPresetWallpaperPath();
        } catch (Throwable th) {
            Log.e(TAG, "getMiuiPresetWallpaperPath fail : ", th);
            return null;
        }
    }

    public String getDefaultHomeWallpaperPath() {
        return getDefaultLockWallpaperPath();
    }

    public String getMiuiWallpaperVideoFilePath(int i) {
        return getMiuiWallpaperPath(MI_WALLPAPER_TYPE_VIDEO, i, false, false);
    }

    public String getMiuiWallpaperSensorFilePath(int i) {
        String str;
        if (isMiuiDefaultWallpaper(i)) {
            str = MI_WALLPAPER_TYPE_DEFAULT;
        } else {
            str = MI_WALLPAPER_TYPE_SENSOR;
        }
        return getMiuiWallpaperPath(str, i, false, false);
    }

    public String getGalleryJson(int i) {
        if (isServiceReady() && isSingleWhich(i)) {
            try {
                return this.mService.getGalleryJson(i);
            } catch (Throwable th) {
                Log.e(TAG, "getMiuiWallpaperPath fail : ", th);
                return "";
            }
        }
        return "";
    }

    public Bitmap getMiuiWallpaperPreview(int i) {
        Bitmap fromFile = null;
        if (isServiceReady() && isSingleWhich(i)) {
            try {
                ParcelFileDescriptor miuiWallpaperPreview = this.mService.getMiuiWallpaperPreview(i);
                try {
                    if (miuiWallpaperPreview == null) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) WallpaperManager.getInstance(this.mContext).getDrawable();
                        if (bitmapDrawable != null) {
                            fromFile = bitmapDrawable.getBitmap();
                        }
                    } else {
                        fromFile = MiuiWallpaperFileUtils.readFromFile(miuiWallpaperPreview);
                    }
                    if (miuiWallpaperPreview != null) {
                        miuiWallpaperPreview.close();
                    }
                } catch (Throwable th) {
                    if (miuiWallpaperPreview != null) {
                        try {
                            miuiWallpaperPreview.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                Log.e(TAG, "getMiuiWallpaperPath fail : ", th3);
            }
        }
        return fromFile;
    }

    public Bitmap getMiuiWallpaperPreview(int i, int i2) {
        Bitmap fromFile = null;
        if (isServiceReady() && isSingleWhich(i)) {
            try {
                ParcelFileDescriptor miuiWallpaperRotationPreview = this.mService.getMiuiWallpaperRotationPreview(i, i2);
                try {
                    if (miuiWallpaperRotationPreview == null) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) WallpaperManager.getInstance(this.mContext).getDrawable();
                        if (bitmapDrawable != null) {
                            fromFile = bitmapDrawable.getBitmap();
                        }
                    } else {
                        fromFile = MiuiWallpaperFileUtils.readFromFile(miuiWallpaperRotationPreview);
                    }
                    if (miuiWallpaperRotationPreview != null) {
                        miuiWallpaperRotationPreview.close();
                    }
                } catch (Throwable th) {
                    if (miuiWallpaperRotationPreview != null) {
                        try {
                            miuiWallpaperRotationPreview.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                Log.e(TAG, "getMiuiWallpaperPath fail : ", th3);
            }
        }
        return fromFile;
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0076 A[Catch: all -> 0x0097, TryCatch #0 {all -> 0x0097, blocks: (B:18:0x0052, B:20:0x0076, B:21:0x0079, B:23:0x0083, B:26:0x008a, B:27:0x008d, B:29:0x0093), top: B:34:0x0052 }] */
    /* JADX WARN: Code duplicated, block: B:23:0x0083 A[Catch: all -> 0x0097, TryCatch #0 {all -> 0x0097, blocks: (B:18:0x0052, B:20:0x0076, B:21:0x0079, B:23:0x0083, B:26:0x008a, B:27:0x008d, B:29:0x0093), top: B:34:0x0052 }] */
    /* JADX WARN: Code duplicated, block: B:24:0x0087  */
    /* JADX WARN: Code duplicated, block: B:26:0x008a A[Catch: all -> 0x0097, TryCatch #0 {all -> 0x0097, blocks: (B:18:0x0052, B:20:0x0076, B:21:0x0079, B:23:0x0083, B:26:0x008a, B:27:0x008d, B:29:0x0093), top: B:34:0x0052 }] */
    /* JADX WARN: Code duplicated, block: B:29:0x0093 A[Catch: all -> 0x0097, TRY_LEAVE, TryCatch #0 {all -> 0x0097, blocks: (B:18:0x0052, B:20:0x0076, B:21:0x0079, B:23:0x0083, B:26:0x008a, B:27:0x008d, B:29:0x0093), top: B:34:0x0052 }] */
    public Bitmap getFitScreenWallpaperPreview(Context context, int i) {
        int iMax;
        int i2;
        int iMin;
        Bitmap bitmapCreateBitmap;
        int width;
        int i3;
        Bitmap miuiWallpaperPreview = getMiuiWallpaperPreview(i);
        if (miuiWallpaperPreview != null) {
            Rect bounds = ((WindowManager) context.getSystemService(WindowManager.class)).getCurrentWindowMetrics().getBounds();
            try {
                if (IS_FOLDABLE_DEVICE || Build.IS_TABLET) {
                    int iWidth = bounds.width();
                    int iHeight = bounds.height();
                    if (isSystemWhich(i)) {
                        iMax = iHeight;
                        i2 = iWidth;
                    } else {
                        int[] foldDisplaySize = getFoldDisplaySize(context, false);
                        if (foldDisplaySize != null) {
                            int i4 = foldDisplaySize[0];
                            iMax = foldDisplaySize[1];
                            iMin = i4;
                        } else {
                            iMax = iHeight;
                            iMin = iWidth;
                        }
                    }
                    int width2 = miuiWallpaperPreview.getWidth();
                    int height = miuiWallpaperPreview.getHeight();
                    float fMax = Math.max(i2 / width2, iMax / height);
                    Matrix matrix = new Matrix();
                    matrix.setScale(fMax, fMax);
                    bitmapCreateBitmap = Bitmap.createBitmap(miuiWallpaperPreview, 0, 0, width2, height, matrix, true);
                    if (miuiWallpaperPreview != bitmapCreateBitmap) {
                        BitmapUtils.recycleBitmap(miuiWallpaperPreview);
                    }
                    width = bitmapCreateBitmap.getWidth();
                    int height2 = bitmapCreateBitmap.getHeight();
                    if (width > i2) {
                        i3 = (width - i2) / 2;
                    } else {
                        i3 = 0;
                    }
                    miuiWallpaperPreview = Bitmap.createBitmap(bitmapCreateBitmap, i3, height2 > iMax ? (height2 - iMax) / 2 : 0, i2, iMax);
                    if (miuiWallpaperPreview != bitmapCreateBitmap) {
                        BitmapUtils.recycleBitmap(bitmapCreateBitmap);
                    }
                } else {
                    int iWidth2 = bounds.width();
                    int iHeight2 = bounds.height();
                    iMin = Math.min(iWidth2, iHeight2);
                    iMax = Math.max(iWidth2, iHeight2);
                }
                int width3 = miuiWallpaperPreview.getWidth();
                int height3 = miuiWallpaperPreview.getHeight();
                float fMax2 = Math.max(i2 / width3, iMax / height3);
                Matrix matrix2 = new Matrix();
                matrix2.setScale(fMax2, fMax2);
                bitmapCreateBitmap = Bitmap.createBitmap(miuiWallpaperPreview, 0, 0, width3, height3, matrix2, true);
                if (miuiWallpaperPreview != bitmapCreateBitmap) {
                    BitmapUtils.recycleBitmap(miuiWallpaperPreview);
                }
                width = bitmapCreateBitmap.getWidth();
                int height4 = bitmapCreateBitmap.getHeight();
                if (width > i2) {
                    i3 = (width - i2) / 2;
                } else {
                    i3 = 0;
                }
                miuiWallpaperPreview = Bitmap.createBitmap(bitmapCreateBitmap, i3, height4 > iMax ? (height4 - iMax) / 2 : 0, i2, iMax);
                if (miuiWallpaperPreview != bitmapCreateBitmap) {
                    BitmapUtils.recycleBitmap(bitmapCreateBitmap);
                }
            } catch (Throwable th) {
                Log.e(TAG, "getScreenCenterCropWallpaperPreview fail", th);
            }
            i2 = iMin;
        }
        return miuiWallpaperPreview;
    }

    public Drawable getCheckNeedDarkFitScreenPreview(Context context, int i) {
        Bitmap fitScreenWallpaperPreview = getFitScreenWallpaperPreview(context, i);
        if (fitScreenWallpaperPreview == null) {
            return null;
        }
        if (Settings.System.getInt(context.getContentResolver(), "darken_wallpaper_under_dark_mode", 1) != 1) {
            return new BitmapDrawable(fitScreenWallpaperPreview);
        }
        if (Settings.System.getInt(context.getContentResolver(), "dark_mode_enable", 0) != 1) {
            return new BitmapDrawable(fitScreenWallpaperPreview);
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(fitScreenWallpaperPreview);
        bitmapDrawable.setColorFilter(Color.parseColor("#B4B4B4"), PorterDuff.Mode.MULTIPLY);
        return bitmapDrawable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryRegisterWallpaperChangeCallback() {
        synchronized (this.mWallpaperChangeCallbacks) {
            if (this.mWallpaperChangeCallbacks.isEmpty()) {
                return;
            }
            for (Map.Entry<IMiuiWallpaperManagerCallback, Integer> entry : this.mWallpaperChangeCallbacks.entrySet()) {
                IMiuiWallpaperManagerCallback key = entry.getKey();
                Integer value = entry.getValue();
                if (value != null) {
                    registerWallpaperChangeListener(key, value.intValue());
                }
            }
        }
    }

    public void registerWallpaperChangeListener(IMiuiWallpaperManagerCallback iMiuiWallpaperManagerCallback, int i) {
        if (isServiceReady() && isValidWhich(i)) {
            try {
                synchronized (this.mWallpaperChangeCallbacks) {
                    this.mWallpaperChangeCallbacks.put(iMiuiWallpaperManagerCallback, Integer.valueOf(i));
                }
                this.mService.registerWallpaperChangeListener(iMiuiWallpaperManagerCallback, i);
            } catch (Throwable th) {
                Log.e(TAG, "registerWallpaperChangeListener fail : ", th);
            }
        }
    }

    public void unRegisterWallpaperChangeListener(IMiuiWallpaperManagerCallback iMiuiWallpaperManagerCallback) {
        if (isServiceReady()) {
            try {
                synchronized (this.mWallpaperChangeCallbacks) {
                    this.mWallpaperChangeCallbacks.remove(iMiuiWallpaperManagerCallback);
                }
                this.mService.unRegisterWallpaperChangeListener(iMiuiWallpaperManagerCallback);
            } catch (Throwable th) {
                Log.e(TAG, "unRegisterWallpaperChangeListener fail : ", th);
            }
        }
    }

    public static boolean isSingleWhich(int i) {
        boolean z = i == 1 || i == 2;
        if (IS_FOLDABLE_DEVICE) {
            z = z || i == 4 || i == 8;
        }
        if (!z) {
            Log.e(TAG, "is not single which: which = " + i);
        }
        return z;
    }

    public static boolean isSystemWhich(int i) {
        boolean z = !((i & 1) == 0 && (i & 2) == 0) && (i & 4) == 0 && (i & 8) == 0;
        if (!z) {
            Log.e(TAG, "isSystemWhich: which = " + i);
        }
        return z;
    }

    public static boolean isValidWhich(int i) {
        boolean z = i > 0 && i <= MI_WALLPAPER_WHICH_ALL;
        if (!z) {
            Log.e(TAG, "isValidWhich: which = " + i);
        }
        return z;
    }

    public static void forAllWallpaper(Function<Integer, Integer> function, int i) {
        int i2 = 1;
        while (i != 0) {
            if ((i2 & i) != 0) {
                function.apply(Integer.valueOf(i2));
                i ^= i2;
            }
            i2 <<= 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:20:0x00e2 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:21:0x00e3 A[Catch: all -> 0x0134, TryCatch #0 {all -> 0x0134, blocks: (B:8:0x001e, B:10:0x002d, B:21:0x00e3, B:23:0x00f3, B:25:0x00fc, B:27:0x0104, B:31:0x010f, B:33:0x011b, B:32:0x0113, B:12:0x0045, B:14:0x004f, B:15:0x0066, B:17:0x00c1, B:18:0x00c6), top: B:39:0x001e }] */
    /* JADX WARN: Code duplicated, block: B:32:0x0113 A[Catch: all -> 0x0134, TryCatch #0 {all -> 0x0134, blocks: (B:8:0x001e, B:10:0x002d, B:21:0x00e3, B:23:0x00f3, B:25:0x00fc, B:27:0x0104, B:31:0x010f, B:33:0x011b, B:32:0x0113, B:12:0x0045, B:14:0x004f, B:15:0x0066, B:17:0x00c1, B:18:0x00c6), top: B:39:0x001e }] */
    public boolean setWallpaper(int i, final String str, final Object obj, Object obj2, ComponentName componentName, final String str2, String str3, boolean z, boolean z2, boolean z3, int i2, int i3, boolean z4, String str4, int[] iArr, int i4, int i5) {
        Map wallpaper3;
        final ArrayList arrayList;
        if (!isServiceReady() || !isValidParam(i, str, obj, obj2, str2, str3)) {
            return false;
        }
        try {
            ArrayList arrayList2 = new ArrayList();
            if (getMiuiWallpaperSdkVersion() >= 1.0f) {
                if (getMiuiWallpaperSdkVersion() < 2.0f) {
                    wallpaper3 = this.mService.setWallpaper2(i, str, str3, componentName, z, z2, arrayList2);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_VIDEO_PATH, str2);
                    bundle.putString("content", str3);
                    bundle.putBoolean(EXTRA_LOOP_VIDEO, z);
                    bundle.putBoolean(EXTRA_CAROUSEL, z2);
                    bundle.putInt(EXTRA_EFFECT_ID, i2);
                    bundle.putString(EXTRA_PENDING_PACKAGE, this.mContext.getOpPackageName());
                    bundle.putBoolean(EXTRA_SUPPORT_DARK, z3);
                    bundle.putString(EXTRA_CLOCK_STYLE_TYPE, str4);
                    bundle.putIntArray(EXTRA_CLOCK_TYPE_INFO, iArr);
                    bundle.putInt(EXTRA_ORIGIN_BITMAP_WIDTH, i4);
                    bundle.putInt(EXTRA_ORIGIN_BITMAP_HEIGHT, i5);
                    if (i3 != -1) {
                        bundle.putInt(EXTRA_ENABLE_BLUR, i3);
                    }
                    bundle.putBoolean(EXTRA_SUPPORT_MATTING, z4);
                    wallpaper3 = this.mService.setWallpaper3(i, str, componentName, arrayList2, bundle);
                }
                if (wallpaper3 == null) {
                    return false;
                }
                MiuiWallpaperFileUtils.removeFile(arrayList2);
                arrayList = new ArrayList();
                if (!MI_WALLPAPER_TYPE_DARK.equals(str) || MI_WALLPAPER_TYPE_SUPER_WALLPAPER.equals(str) || MI_WALLPAPER_TYPE_SENSOR.equals(str) || MI_WALLPAPER_TYPE_ROTATION_IMAGE.equals(str)) {
                    arrayList.add(obj);
                    arrayList.add(obj2);
                } else if (obj != null) {
                    arrayList.add(obj);
                }
                final Map map = wallpaper3;
                forAllWallpaper(new Function() { // from class: com.miui.miwallpaper.MiuiWallpaperManager$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj3) {
                        return this.f$0.m199lambda$setWallpaper$0$commiuimiwallpaperMiuiWallpaperManager(str, map, str2, obj, arrayList, (Integer) obj3);
                    }
                }, i);
                return true;
            }
            wallpaper3 = this.mService.setWallpaper(i, str, str3, componentName, z, arrayList2);
            if (wallpaper3 == null) {
                return false;
            }
            MiuiWallpaperFileUtils.removeFile(arrayList2);
            arrayList = new ArrayList();
            if (MI_WALLPAPER_TYPE_DARK.equals(str)) {
                arrayList.add(obj);
                arrayList.add(obj2);
            } else {
                arrayList.add(obj);
                arrayList.add(obj2);
            }
            final Map map2 = wallpaper3;
            forAllWallpaper(new Function() { // from class: com.miui.miwallpaper.MiuiWallpaperManager$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj3) {
                    return this.f$0.m199lambda$setWallpaper$0$commiuimiwallpaperMiuiWallpaperManager(str, map2, str2, obj, arrayList, (Integer) obj3);
                }
            }, i);
            return true;
        } catch (Throwable th) {
            Log.e(TAG, "setMiuiWallpaper fail : ", th);
            return false;
        }
    }

    /* JADX INFO: renamed from: lambda$setWallpaper$0$com-miui-miwallpaper-MiuiWallpaperManager, reason: not valid java name */
    /* synthetic */ Integer m199lambda$setWallpaper$0$commiuimiwallpaperMiuiWallpaperManager(String str, Map map, String str2, Object obj, List list, Integer num) {
        str.hashCode();
        switch (str) {
            case "sensor":
                writeMiuiWallpaperSensor(map, num.intValue(), str2, list);
                return num;
            case "video":
            case "video_gallery":
                writeMiuiWallpaperVideo(map, num.intValue(), str2, obj);
                return num;
            default:
                writeMiuiWallpaper(map, num.intValue(), list);
                return num;
        }
    }

    private void writeMiuiWallpaper(Map<Integer, List<String>> map, int i, List<Object> list) {
        List<String> list2 = map.get(Integer.valueOf(i));
        if (list2 == null || list == null || list.size() != list2.size()) {
            return;
        }
        for (int i2 = 0; i2 < list2.size(); i2++) {
            Object obj = list.get(i2);
            if (obj instanceof Bitmap) {
                MiuiWallpaperFileUtils.writeToFile((Bitmap) obj, list2.get(i2));
            } else if (obj instanceof InputStream) {
                MiuiWallpaperFileUtils.copyStreamToWallpaperFile((InputStream) obj, list2.get(i2));
            }
        }
    }

    private void writeMiuiWallpaperVideo(Map<Integer, List<String>> map, int i, String str, Object obj) {
        List<String> list = map.get(Integer.valueOf(i));
        if (list == null || str == null || list.size() < 2) {
            return;
        }
        MiuiWallpaperFileUtils.copyFile(str, list.get(0));
        if (obj instanceof Bitmap) {
            MiuiWallpaperFileUtils.writeToFile((Bitmap) obj, list.get(1));
        } else if (obj instanceof InputStream) {
            MiuiWallpaperFileUtils.copyStreamToWallpaperFile((InputStream) obj, list.get(1));
        }
    }

    private void writeMiuiWallpaperSensor(Map<Integer, List<String>> map, int i, String str, List<Object> list) {
        Object obj;
        List<String> list2 = map.get(Integer.valueOf(i));
        if (list2 == null || str == null || list2.size() < 2) {
            return;
        }
        MiuiWallpaperFileUtils.copyFile(str, list2.get(0));
        if (list.size() <= 1) {
            obj = list.get(0);
        } else if (i == 8 || i == 4) {
            obj = list.get(1);
        } else {
            obj = list.get(0);
        }
        if (obj instanceof Bitmap) {
            MiuiWallpaperFileUtils.writeToFile((Bitmap) obj, list2.get(1));
        } else if (obj instanceof InputStream) {
            MiuiWallpaperFileUtils.copyStreamToWallpaperFile((InputStream) obj, list2.get(1));
        }
    }

    private boolean isValidParam(int i, String str, Object obj, Object obj2, String str2, String str3) {
        boolean zIsValidWhich = isValidWhich(i);
        str.hashCode();
        switch (str) {
            case "sensor":
                if (MI_WALLPAPER_WHICH_ALL != i || obj != null || obj2 != null) {
                    if ((3 == i && obj == null) || str2 == null) {
                        zIsValidWhich = false;
                    }
                    break;
                } else {
                    zIsValidWhich = false;
                    break;
                }
                break;
            case "rotation_image":
            case "dark":
            case "super_wallpaper":
                if (obj == null || obj2 == null) {
                    zIsValidWhich = false;
                    break;
                }
                break;
            case "gallery":
                if (obj == null || str3 == null) {
                    zIsValidWhich = false;
                    break;
                }
                break;
            case "maml":
            case "image":
                if (obj == null) {
                    zIsValidWhich = false;
                    break;
                }
                break;
            case "video":
            case "video_gallery":
                if (obj == null || str2 == null) {
                    zIsValidWhich = false;
                    break;
                }
                break;
            default:
                zIsValidWhich = false;
                break;
        }
        if (!zIsValidWhich) {
            Log.e(TAG, "set wallpaper param error: which = " + i + " type = " + str + " source1 = " + obj + " source2 = " + obj2 + " videoPath = " + str2 + " content = " + str3);
        }
        return zIsValidWhich;
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindService() {
        Intent intent = new Intent(MIUI_WALLPAPER_SERVICE_ACTION);
        intent.setPackage(DEFAULT_WALLPAPER_PACKAGE_NAME);
        if (!this.mInitSync) {
            this.mContext.bindService(intent, this.mServiceConnection, 1);
        } else {
            this.mContext.bindService(intent, 1, WALLPAPER_INIT_EXECUTOR, this.mServiceConnection);
        }
    }

    private void bindServiceAsUser(UserHandle userHandle) {
        Intent intent = new Intent(MIUI_WALLPAPER_SERVICE_ACTION);
        intent.setPackage(DEFAULT_WALLPAPER_PACKAGE_NAME);
        try {
            this.mContext.bindServiceAsUser(intent, this.mServiceConnection, 1, userHandle);
        } catch (Throwable th) {
            Log.e(TAG, "bindServiceAsUser fail", th);
        }
    }

    private boolean isServiceReady() {
        if (this.mService != null) {
            return true;
        }
        Log.e(TAG, "mService is null.");
        return false;
    }

    private static boolean isFoldDevices() {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            return ((Integer) cls.getMethod("getInt", String.class, Integer.TYPE).invoke(cls, "persist.sys.muiltdisplay_type", 0)).intValue() == 2;
        } catch (Throwable th) {
            Log.e(TAG, "isFoldDevices fail : ", th);
            return false;
        }
    }

    private static boolean isFlipDevice() {
        try {
            Class<?> cls = Class.forName("miui.util.MiuiMultiDisplayTypeInfo");
            Object objInvoke = cls.getMethod("isFlipDevice", new Class[0]).invoke(cls, new Object[0]);
            if (objInvoke instanceof Boolean) {
                return ((Boolean) objInvoke).booleanValue();
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "isFlipDevice fail", e);
            return false;
        }
    }

    private static int getFastBlurColor(Bitmap bitmap) {
        if (bitmap == null) {
            return -1;
        }
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float f = width;
            float f2 = height;
            matrix.setScale(1.0f / f, 1.0f / f2, f / 2.0f, f2 / 2.0f);
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
            int pixel = bitmapCreateBitmap == null ? -1 : bitmapCreateBitmap.getPixel(0, 0);
            BitmapUtils.recycleBitmap(bitmapCreateBitmap);
            System.gc();
            return pixel;
        } catch (Throwable th) {
            Log.e(TAG, "getFastBlurColor fail : ", th);
            return -1;
        }
    }

    private static int addTwoColor(int i, int i2) {
        float fAlpha = Color.alpha(i) / 255.0f;
        float fAlpha2 = Color.alpha(i2) / 255.0f;
        float f = (fAlpha + fAlpha2) - (fAlpha * fAlpha2);
        float f2 = 1.0f - fAlpha2;
        return Color.argb((int) (255.0f * f), (int) ((((Color.red(i) * fAlpha) * f2) + (Color.red(i2) * fAlpha2)) / f), (int) ((((Color.green(i) * fAlpha) * f2) + (Color.green(i2) * fAlpha2)) / f), (int) ((((Color.blue(i) * fAlpha) * f2) + (Color.blue(i2) * fAlpha2)) / f));
    }

    public static int computeFinalBlurColor(Bitmap bitmap) {
        int fastBlurColor;
        int color = Color.parseColor(MI_WALLPAPER_DEFAULT_BLUR_COLOR);
        return (bitmap == null || (fastBlurColor = getFastBlurColor(bitmap)) == -1) ? color : addTwoColor(fastBlurColor, color);
    }

    public void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) {
        if (isServiceReady()) {
            try {
                this.mService.bindSystemUIProxy(iMiuiKeyguardWallpaperCallback);
            } catch (Throwable th) {
                Log.e(TAG, "bindSystemUIProxy fail", th);
            }
        }
    }

    public void showWallpaperScreenOnAnim(boolean z) {
        if (isServiceReady()) {
            try {
                this.mService.showWallpaperScreenOnAnim(z);
            } catch (Throwable th) {
                Log.e(TAG, "showWallpaperScreenOnAnim fail", th);
            }
        }
    }

    public void updateKeyguardWallpaperState(boolean z) {
        if (isServiceReady()) {
            try {
                this.mService.updateKeyguardWallpaperState(z);
            } catch (Throwable th) {
                Log.e(TAG, "updateKeyguardWallpaperState fail", th);
            }
        }
    }

    public void updateKeyguardWallpaperRatio(float f, long j) {
        if (isServiceReady()) {
            try {
                this.mService.updateKeyguardWallpaperRatio(f, j);
            } catch (Throwable th) {
                Log.e(TAG, "updateKeyguardWallpaperRatio fail", th);
            }
        }
    }

    public void showWallpaperUnlockAnim() {
        if (isServiceReady()) {
            try {
                this.mService.showWallpaperUnlockAnim();
            } catch (Throwable th) {
                Log.e(TAG, "showWallpaperUnlockAnim fail", th);
            }
        }
    }

    public void updateKeyguardWallpaperStateAnim(boolean z, boolean z2, int i) {
        if (isServiceReady()) {
            try {
                this.mService.updateKeyguardWallpaperStateAnim(z, z2, i);
            } catch (Throwable th) {
                Log.e(TAG, "updateKeyguardWallpaperStateAnim fail", th);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean changeMiuiWallpaperInfo(int i, int i2, int i3, boolean z) {
        if (!isServiceReady()) {
            return false;
        }
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_EFFECT_ID, i2);
            if (i3 != -1) {
                bundle.putInt(EXTRA_ENABLE_BLUR, i3);
            }
            bundle.putBoolean(EXTRA_SUPPORT_MATTING, z);
            this.mService.changeMiuiWallpaperInfo(i, bundle);
            return true;
        } catch (Throwable th) {
            Log.e(TAG, "changeMiuiWallpaperEffectType: fail", th);
            return false;
        }
    }

    public boolean supportDark(int i) {
        if (!isServiceReady()) {
            return false;
        }
        try {
            return this.mService.supportDark(i);
        } catch (Throwable th) {
            Log.e(TAG, "supportDark: fail", th);
            return false;
        }
    }

    public boolean isMiuiDefaultWallpaper(int i) {
        if (!isServiceReady() || !isSingleWhich(i)) {
            return false;
        }
        try {
            if (getMiuiWallpaperSdkVersion() < 1.3f) {
                Log.e(TAG, "call method isMiuiDefaultWallpaper fail,sdk version must >= 1.3");
                return false;
            }
            return this.mService.isMiuiDefaultWallpaper(i);
        } catch (Throwable th) {
            Log.e(TAG, "isMiuiDefaultWallpaper fail", th);
            return false;
        }
    }

    public String getMiuiDefaultWallpaperType() {
        if (!isServiceReady()) {
            return null;
        }
        try {
            if (getMiuiWallpaperSdkVersion() < 1.3f) {
                Log.e(TAG, "call method getMiuiDefaultWallpaperType fail,sdk version must >= 1.3");
                return null;
            }
            return this.mService.getMiuiDefaultWallpaperType(1);
        } catch (Throwable th) {
            Log.e(TAG, "getMiuiDefaultWallpaperType fail", th);
            return null;
        }
    }

    public String getMiuiWallpaperSensorPreviewFilePath(int i) {
        String str;
        if (isMiuiDefaultWallpaper(i)) {
            str = MI_WALLPAPER_TYPE_DEFAULT;
        } else {
            str = MI_WALLPAPER_TYPE_SENSOR;
        }
        return getMiuiWallpaperPath(str, i, false, true);
    }

    public String[] getMiuiWallpaperDarkFilePath(int i) {
        String str;
        if (isMiuiDefaultWallpaper(i)) {
            str = MI_WALLPAPER_TYPE_DEFAULT;
        } else {
            str = MI_WALLPAPER_TYPE_DARK;
        }
        return new String[]{getMiuiWallpaperPath(str, i, false, false), getMiuiWallpaperPath(str, i, true, false)};
    }

    public String getMiuiWallpaperImageFilePath(int i) {
        String str;
        if (isMiuiDefaultWallpaper(i)) {
            str = MI_WALLPAPER_TYPE_DEFAULT;
        } else {
            str = "image";
        }
        return getMiuiWallpaperPath(str, i, false, false);
    }

    public boolean blurState(int i) {
        if (!isServiceReady()) {
            return false;
        }
        try {
            return this.mService.blurState(i);
        } catch (RemoteException e) {
            Log.e(TAG, "call method blurEnabled fail", e);
            return false;
        }
    }

    public Map getMiuiWallpaperPalette(int i, boolean z, Map<String, Rect> map) {
        if (!isServiceReady() || !isSingleWhich(i)) {
            return null;
        }
        try {
            return this.mService.getMiuiWallpaperPalette(i, z, map);
        } catch (Exception e) {
            Log.e(TAG, "getMiuiWallpaperColors fail : ", e);
            return null;
        }
    }

    public Map getMiuiLockPartWallpaperIsDeep(int i, boolean z, Map<String, Rect> map) {
        if (!isServiceReady()) {
            return null;
        }
        try {
            return this.mService.getMiuiLockPartWallpaperIsDeep(i, z, map);
        } catch (Throwable th) {
            Log.e(TAG, "getMiuiLockPartWallpaperColors failed ", th);
            return null;
        }
    }

    public boolean isMiuiWallpaperComponentUsing(int i) {
        if (!isServiceReady() || !isSingleWhich(i)) {
            return true;
        }
        try {
            return this.mService.isMiuiWallpaperComponentUsing(i);
        } catch (Throwable th) {
            Log.e(TAG, "isMiuiWallpaperComponentUsing: fail", th);
            return true;
        }
    }

    public int getEffectId(int i) {
        if (!isServiceReady() || !isSingleWhich(i)) {
            return 0;
        }
        try {
            return this.mService.getEffectId(i);
        } catch (Throwable th) {
            Log.e(TAG, "getEffectId: fail", th);
            return 0;
        }
    }

    public int getMiuiWallpaperSdkVersionCode() {
        if (!isServiceReady()) {
            return 0;
        }
        try {
            return this.mService.getMiuiWallpaperSdkVersionCode();
        } catch (Throwable th) {
            Log.e(TAG, "getMiuiWallpaperSdkVersionCode: fail", th);
            return 0;
        }
    }

    public static class WallpaperApplierBuilder {
        private String clockStyleType;
        private String content;
        private boolean isVideoLoop;
        private final MiuiWallpaperManager manager;
        private int multiWhich;
        private Object source1;
        private Object source2;
        private Object source3;
        private Object source4;
        private String type;
        private String videoPath;
        private boolean supportDark = true;
        private ComponentName componentName = MiuiWallpaperManager.DEFAULT_WALLPAPER_COMPONENT;
        private boolean carousel = true;
        private int effectId = -1;
        private int[] clockTypeInfo = MiuiWallpaperManager.DEFAULT_CLOCK_TYPE_INFO;
        private int blurState = -1;
        private boolean supportMatting = false;
        private boolean isWallpaperFileChanged = true;
        private int originBitmapWidth = 0;
        private int originBitmapHeight = 0;

        public WallpaperApplierBuilder(MiuiWallpaperManager miuiWallpaperManager) {
            this.manager = miuiWallpaperManager;
        }

        public WallpaperApplierBuilder which(int i) {
            this.multiWhich = i;
            return this;
        }

        public WallpaperApplierBuilder wallpaperType(String str) {
            this.type = str;
            return this;
        }

        public WallpaperApplierBuilder image(Object obj) {
            this.type = "image";
            this.source1 = obj;
            return this;
        }

        public WallpaperApplierBuilder rotationImage(Object obj, Object obj2) {
            this.type = MiuiWallpaperManager.MI_WALLPAPER_TYPE_ROTATION_IMAGE;
            this.source1 = obj;
            this.source2 = obj2;
            return this;
        }

        public WallpaperApplierBuilder supportDark(boolean z) {
            this.supportDark = z;
            return this;
        }

        public WallpaperApplierBuilder dark(Object obj, Object obj2) {
            this.type = MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK;
            this.source1 = obj;
            this.source2 = obj2;
            return this;
        }

        public WallpaperApplierBuilder video(Object obj, String str, boolean z) {
            this.type = MiuiWallpaperManager.MI_WALLPAPER_TYPE_VIDEO;
            this.videoPath = str;
            this.isVideoLoop = z;
            this.source1 = obj;
            return this;
        }

        public WallpaperApplierBuilder videoGallery(Object obj, String str, String str2, boolean z, boolean z2) {
            this.type = MiuiWallpaperManager.MI_WALLPAPER_TYPE_VIDEO_GALLERY;
            this.source1 = obj;
            this.videoPath = str;
            this.content = str2;
            this.isVideoLoop = z;
            this.carousel = z2;
            return this;
        }

        public WallpaperApplierBuilder sensor(Object obj, Object obj2, String str) {
            this.type = MiuiWallpaperManager.MI_WALLPAPER_TYPE_SENSOR;
            this.source1 = obj;
            this.source2 = obj2;
            this.videoPath = str;
            return this;
        }

        public WallpaperApplierBuilder gallery(Object obj, boolean z, String str) {
            this.type = "gallery";
            this.source1 = obj;
            this.carousel = z;
            this.content = str;
            return this;
        }

        public WallpaperApplierBuilder maml(Object obj) {
            this.type = MiuiWallpaperManager.MI_WALLPAPER_TYPE_MAML;
            this.componentName = MiuiWallpaperManager.MAML_WALLPAPER_COMPONENT;
            this.source1 = obj;
            return this;
        }

        public WallpaperApplierBuilder superWallpaper(ComponentName componentName, Object obj, Object obj2, Object obj3, Object obj4) {
            this.type = MiuiWallpaperManager.MI_WALLPAPER_TYPE_SUPER_WALLPAPER;
            this.componentName = componentName;
            this.source1 = obj;
            this.source2 = obj2;
            this.source3 = obj3;
            this.source4 = obj4;
            return this;
        }

        public WallpaperApplierBuilder effectId(int i) {
            this.effectId = i;
            return this;
        }

        public WallpaperApplierBuilder enableBlur(boolean z) {
            this.blurState = z ? 1 : 0;
            return this;
        }

        public WallpaperApplierBuilder supportMatting(boolean z) {
            this.supportMatting = z;
            return this;
        }

        public WallpaperApplierBuilder wallpaperFileChanged(boolean z) {
            this.isWallpaperFileChanged = z;
            return this;
        }

        public WallpaperApplierBuilder clockStyleType(String str) {
            this.clockStyleType = str;
            return this;
        }

        public WallpaperApplierBuilder clockTypeInfo(int[] iArr) {
            this.clockTypeInfo = iArr;
            return this;
        }

        public WallpaperApplierBuilder setOriginBitmapWH(int i, int i2) {
            this.originBitmapWidth = i;
            this.originBitmapHeight = i2;
            return this;
        }

        public boolean apply() {
            if (!this.isWallpaperFileChanged) {
                return this.manager.changeMiuiWallpaperInfo(this.multiWhich, this.effectId, this.blurState, this.supportMatting);
            }
            if (MiuiWallpaperManager.MI_WALLPAPER_TYPE_SUPER_WALLPAPER.equals(this.type)) {
                return this.manager.setWallpaper(1, MiuiWallpaperManager.MI_WALLPAPER_TYPE_SUPER_WALLPAPER, this.source1, this.source2, this.componentName, this.videoPath, this.content, this.isVideoLoop, this.carousel, this.supportDark, this.effectId, this.blurState, this.supportMatting, this.clockStyleType, this.clockTypeInfo, this.originBitmapWidth, this.originBitmapHeight) && this.manager.setWallpaper(2, MiuiWallpaperManager.MI_WALLPAPER_TYPE_SUPER_WALLPAPER, this.source3, this.source4, this.componentName, this.videoPath, this.content, this.isVideoLoop, this.carousel, this.supportDark, this.effectId, this.blurState, this.supportMatting, this.clockStyleType, this.clockTypeInfo, this.originBitmapWidth, this.originBitmapHeight);
            }
            return this.manager.setWallpaper(this.multiWhich, this.type, this.source1, this.source2, this.componentName, this.videoPath, this.content, this.isVideoLoop, this.carousel, this.supportDark, this.effectId, this.blurState, this.supportMatting, this.clockStyleType, this.clockTypeInfo, this.originBitmapWidth, this.originBitmapHeight);
        }
    }

    private int[] getFoldDisplaySize(Context context, boolean z) {
        Display display;
        Display[] displays = ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplays("android.hardware.display.category.ALL_INCLUDING_DISABLED");
        if (displays.length != 2 || (display = displays[0]) == null || displays[1] == null) {
            return null;
        }
        Display display2 = display.getMode().getPhysicalWidth() < displays[1].getMode().getPhysicalWidth() ? displays[0] : displays[1];
        Display display3 = displays[0].getMode().getPhysicalWidth() < displays[1].getMode().getPhysicalWidth() ? displays[1] : displays[0];
        if (z) {
            display2 = display3;
        }
        return new int[]{display2.getMode().getPhysicalWidth(), display2.getMode().getPhysicalHeight()};
    }
}
