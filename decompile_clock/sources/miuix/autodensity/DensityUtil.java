package miuix.autodensity;

import android.app.Activity;
import android.app.ResourcesManager;
import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.ResourcesImpl;
import android.content.res.ResourcesKey;
import android.content.res.loader.ResourcesLoader;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import java.lang.ref.WeakReference;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;
import miuix.core.util.RomUtils;
import miuix.os.DeviceHelper;
import miuix.reflect.ReflectionHelper;
import miuix.view.DisplayConfig;

/* JADX INFO: loaded from: classes2.dex */
public class DensityUtil {
    private static final String SYNERGY_OWNER_ANDROID_PAD = "AndroidPad";
    private static final String SYNERGY_OWNER_ANDROID_PAD_CAR = "AndroidPadCar";
    private static final String SYNERGY_OWNER_ANDROID_PHONE = "AndroidPhone";
    private static final String SYNERGY_OWNER_WINDOWS = "Windows";
    private static boolean sIsSupportSwitchResolution;
    private static Object sLock;
    private static ArrayMap<ResourcesKey, WeakReference<ResourcesImpl>> sResourcesImpls;
    private static ResourcesManager sResourcesManager;

    static {
        try {
            sResourcesManager = (ResourcesManager) ReflectionHelper.getConstructorInstance(ResourcesManager.class, new Class[0], new Object[0]);
            ResourcesManager resourcesManager = ResourcesManager.getInstance();
            sResourcesManager = resourcesManager;
            sResourcesImpls = (ArrayMap) ReflectionHelper.getFieldValue(ResourcesManager.class, resourcesManager, "mResourceImpls");
            sLock = sResourcesManager;
        } catch (Exception unused) {
        }
        if (Build.VERSION.SDK_INT >= 31) {
            try {
                sLock = ReflectionHelper.getFieldValue(ResourcesManager.class, sResourcesManager, "mLock");
            } catch (Exception unused2) {
                sLock = null;
            }
        }
        if (sResourcesManager == null || sResourcesImpls == null || sLock == null) {
            Log.w("AutoDensity", "ResourcesManager reflection failed, this app do not have permission to disable AutoDensity for activity/application");
        }
        try {
            int[] iArr = (int[]) ReflectionHelper.invokeObject(Class.forName("miui.util.FeatureParser"), null, "getIntArray", new Class[]{String.class}, "screen_resolution_supported");
            if (iArr == null || iArr.length <= 1) {
                return;
            }
            sIsSupportSwitchResolution = true;
        } catch (Throwable th) {
            Log.d("AutoDensity", "DensityUtil init screen_resolution_supported Exception: " + th);
        }
    }

    public static boolean restoreDefaultDensity(Context context) {
        if (context == null) {
            Log.w("AutoDensity", "restoreDefaultDensity context should not null");
            return false;
        }
        Display currentDisplay = getCurrentDisplay(context);
        if (DensityConfigManager.getInstance().isAutoDensityEnabled()) {
            return restoreDensity(context.getResources(), currentDisplay);
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void updateCustomDensity(Context context) {
        boolean zShouldProcessDensity;
        if (context == 0) {
            Log.w("AutoDensity", "updateCustomDensity context should not null");
            return;
        }
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("updateCustomDensity context is " + context);
        }
        if (DensityConfigManager.getInstance().isAutoDensityEnabled()) {
            int ratioUiBaseWidthDp = context instanceof IDensity ? ((IDensity) context).getRatioUiBaseWidthDp() : 0;
            Display currentDisplay = getCurrentDisplay(context);
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                Configuration configuration = activity.getResources().getConfiguration();
                zShouldProcessDensity = shouldProcessDensity(activity, currentDisplay);
                if (DebugUtil.isEnableDebug()) {
                    DebugUtil.printDensityLog("updateCustomDensity -> display is " + (currentDisplay != null ? currentDisplay.getName() : "null") + " id:" + (currentDisplay != null ? Integer.valueOf(currentDisplay.getDisplayId()) : "null") + " shouldProcessDensity=" + zShouldProcessDensity + " activity is " + activity + " config is " + configuration);
                }
                if (!zShouldProcessDensity && configuration.densityDpi == DensityConfigManager.getInstance().getDeviceCurrentAccessibilityDpi(currentDisplay)) {
                    if (DebugUtil.isEnableDebug()) {
                        DebugUtil.printDensityLog("updateCustomDensity -> will not changeDensity cause display is " + (currentDisplay != null ? currentDisplay.getName() : "null") + ", try restore density, activity is " + activity + " config is " + configuration);
                    }
                    restoreDefaultDensity(activity);
                }
            } else {
                zShouldProcessDensity = true;
            }
            if (zShouldProcessDensity) {
                changeDensity(context.getResources(), ratioUiBaseWidthDp, currentDisplay);
            }
        }
    }

    @Deprecated
    public static void updateCustomDensity(Context context, int i) {
        if (context == null) {
            Log.w("AutoDensity", "context should not null");
        } else if (DensityConfigManager.getInstance().isAutoDensityEnabled()) {
            changeDensity(context.getResources(), i, null);
        }
    }

    public static void updateCustomDensity(Context context, int i, Display display) {
        if (context == null) {
            Log.w("AutoDensity", "context should not null");
        } else if (DensityConfigManager.getInstance().isAutoDensityEnabled()) {
            changeDensity(context.getResources(), i, display);
        }
    }

    public static boolean shouldUpdateDensityForConfig(Configuration configuration) {
        DensityConfig targetConfig = DensityConfigManager.getInstance().getTargetConfig();
        return (targetConfig == null || configuration.densityDpi == targetConfig.densityDpi) ? false : true;
    }

    public static boolean shouldUpdateDensityForConfig(Configuration configuration, Display display) {
        DensityConfig targetConfig = DensityConfigManager.getInstance().getTargetConfig(display);
        return (targetConfig == null || configuration.densityDpi == targetConfig.densityDpi) ? false : true;
    }

    public static boolean updateDensityForConfig(Context context, Configuration configuration) {
        DensityConfig targetConfig = DensityConfigManager.getInstance().getTargetConfig(getCurrentDisplay(context));
        if (targetConfig == null) {
            return false;
        }
        Resources resources = context.getResources();
        resources.getConfiguration().setTo(configuration);
        doChangeDensity(targetConfig, resources, 0);
        return true;
    }

    public static AutoDensityContextWrapper findAutoDensityContextWrapper(Context context) {
        if (!(context instanceof ContextThemeWrapper)) {
            return null;
        }
        while (true) {
            ContextThemeWrapper contextThemeWrapper = (ContextThemeWrapper) context;
            if (!(contextThemeWrapper.getBaseContext() instanceof ContextThemeWrapper)) {
                return null;
            }
            if (contextThemeWrapper.getBaseContext() instanceof AutoDensityContextWrapper) {
                return (AutoDensityContextWrapper) contextThemeWrapper.getBaseContext();
            }
            context = contextThemeWrapper.getBaseContext();
        }
    }

    public static Configuration getNoDensityOverrideConfiguration(Context context) {
        Configuration noOverrideConfiguration;
        AutoDensityContextWrapper autoDensityContextWrapperFindAutoDensityContextWrapper = findAutoDensityContextWrapper(context);
        if (autoDensityContextWrapperFindAutoDensityContextWrapper == null || (noOverrideConfiguration = autoDensityContextWrapperFindAutoDensityContextWrapper.getNoOverrideConfiguration()) == null) {
            return null;
        }
        return noOverrideConfiguration;
    }

    private static boolean restoreDensity(Resources resources, Display display) {
        DensityConfig originConfig = DensityConfigManager.getInstance().getOriginConfig(display);
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (originConfig == null || originConfig.density == displayMetrics.density) {
            return false;
        }
        DebugUtil.printDensityLog("restoreDensity success");
        doChangeDensity(originConfig, resources, 0);
        return true;
    }

    private static void changeDensity(Resources resources, int i, Display display) {
        DensityConfig targetConfig = DensityConfigManager.getInstance().getTargetConfig(display);
        if (targetConfig != null) {
            if (i > 0 || resources.getDisplayMetrics().densityDpi != targetConfig.densityDpi) {
                doChangeDensity(targetConfig, resources, i);
                if (AutoDensityConfig.shouldUpdateSystemResource()) {
                    setSystemResources(targetConfig);
                }
            }
        }
    }

    public static void doChangeDensity(DisplayConfig displayConfig, Resources resources, int i) {
        tryToCreateAndSetResourcesImpl(resources, displayConfig);
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        float f = 1.0f;
        float f2 = (displayMetrics.widthPixels * 1.0f) / displayConfig.density;
        if (i > 0) {
            float f3 = i;
            if (f2 < f3) {
                f = displayMetrics.widthPixels / (f3 * displayConfig.density);
            }
        }
        int i2 = (int) (displayConfig.densityDpi * f);
        configuration.densityDpi = i2;
        displayMetrics.densityDpi = i2;
        displayMetrics.density = displayConfig.density * f;
        displayMetrics.scaledDensity = displayConfig.scaledDensity * f;
        configuration.fontScale = displayConfig.fontScale;
        DebugUtil.printDensityLog("after doChangeDensity baseWidthDp:" + i + " ratio:" + f + " " + displayMetrics + " " + configuration);
    }

    private static void tryToCreateAndSetResourcesImpl(Resources resources, DisplayConfig displayConfig) {
        Object obj;
        ResourcesImpl resourcesImplFindOrCreateResourcesImplForKeyLocked;
        if (sResourcesManager == null || sResourcesImpls == null || (obj = sLock) == null) {
            return;
        }
        try {
            synchronized (obj) {
                ResourcesKey resourcesKeyFindResourcesKeyByResourcesImplLocked = findResourcesKeyByResourcesImplLocked((ResourcesImpl) ReflectionHelper.getFieldValue(Resources.class, resources, "mResourcesImpl"));
                DebugUtil.printDensityLog("oldKey " + resourcesKeyFindResourcesKeyByResourcesImplLocked);
                if (resourcesKeyFindResourcesKeyByResourcesImplLocked != null && (resourcesImplFindOrCreateResourcesImplForKeyLocked = findOrCreateResourcesImplForKeyLocked(resourcesKeyFindResourcesKeyByResourcesImplLocked, displayConfig)) != null) {
                    ReflectionHelper.invoke(Resources.class, resources, "setImpl", new Class[]{ResourcesImpl.class}, resourcesImplFindOrCreateResourcesImplForKeyLocked);
                    DebugUtil.printDensityLog("set impl success " + resourcesImplFindOrCreateResourcesImplForKeyLocked);
                }
            }
        } catch (Exception e) {
            DebugUtil.printDensityLog("tryToCreateAndSetResourcesImpl failed " + e.toString());
        }
    }

    private static ResourcesKey findResourcesKeyByResourcesImplLocked(ResourcesImpl resourcesImpl) {
        int size = sResourcesImpls.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                return null;
            }
            WeakReference<ResourcesImpl> weakReferenceValueAt = sResourcesImpls.valueAt(i);
            if (resourcesImpl == (weakReferenceValueAt != null ? weakReferenceValueAt.get() : null)) {
                return sResourcesImpls.keyAt(i);
            }
            i++;
        }
    }

    private static ResourcesImpl findOrCreateResourcesImplForKeyLocked(ResourcesKey resourcesKey, DisplayConfig displayConfig) {
        ResourcesKey resourcesKey2;
        try {
            Configuration configuration = new Configuration();
            Configuration configuration2 = (Configuration) ReflectionHelper.getFieldValue(ResourcesKey.class, resourcesKey, "mOverrideConfiguration");
            if (Build.VERSION.SDK_INT >= 35 && configuration.equals(configuration2)) {
                return null;
            }
            configuration.setTo(configuration2);
            configuration.densityDpi = displayConfig.densityDpi;
            int iIntValue = ((Integer) ReflectionHelper.getFieldValue(ResourcesKey.class, resourcesKey, "mDisplayId")).intValue();
            String[] strArr = (String[]) ReflectionHelper.getFieldValue(ResourcesKey.class, resourcesKey, "mLibDirs");
            CompatibilityInfo compatibilityInfo = (CompatibilityInfo) ReflectionHelper.getFieldValue(ResourcesKey.class, resourcesKey, "mCompatInfo");
            String[] strArr2 = Build.VERSION.SDK_INT <= 30 ? (String[]) ReflectionHelper.getFieldValue(ResourcesKey.class, resourcesKey, "mOverlayDirs") : (String[]) ReflectionHelper.getFieldValue(ResourcesKey.class, resourcesKey, "mOverlayPaths");
            if (Build.VERSION.SDK_INT <= 29) {
                resourcesKey2 = (ResourcesKey) ReflectionHelper.getConstructorInstance(ResourcesKey.class, new Class[]{String.class, String[].class, String[].class, String[].class, Integer.TYPE, Configuration.class, CompatibilityInfo.class}, resourcesKey.mResDir, resourcesKey.mSplitResDirs, strArr2, strArr, Integer.valueOf(iIntValue), configuration, compatibilityInfo);
            } else {
                resourcesKey2 = (ResourcesKey) ReflectionHelper.getConstructorInstance(ResourcesKey.class, new Class[]{String.class, String[].class, String[].class, String[].class, Integer.TYPE, Configuration.class, CompatibilityInfo.class, ResourcesLoader[].class}, resourcesKey.mResDir, resourcesKey.mSplitResDirs, strArr2, strArr, Integer.valueOf(iIntValue), configuration, compatibilityInfo, (ResourcesLoader[]) ReflectionHelper.getFieldValue(ResourcesKey.class, resourcesKey, "mLoaders"));
            }
            DebugUtil.printDensityLog("newKey " + resourcesKey2);
            return (ResourcesImpl) ReflectionHelper.invokeObject(ResourcesManager.class, sResourcesManager, "findOrCreateResourcesImplForKeyLocked", new Class[]{ResourcesKey.class}, resourcesKey2);
        } catch (Error e) {
            DebugUtil.printDensityLog("findOrCreateResourcesImplForKeyLocked failed " + e.toString());
            return null;
        } catch (Exception e2) {
            DebugUtil.printDensityLog("findOrCreateResourcesImplForKeyLocked failed " + e2.toString());
            return null;
        }
    }

    public static void setSystemResources(DisplayConfig displayConfig) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        Configuration configuration = Resources.getSystem().getConfiguration();
        int i = displayConfig.densityDpi;
        configuration.densityDpi = i;
        displayMetrics.densityDpi = i;
        displayMetrics.scaledDensity = displayConfig.scaledDensity;
        displayMetrics.density = displayConfig.density;
        configuration.fontScale = displayConfig.fontScale;
        setDefaultBitmapDensity(displayConfig.defaultBitmapDensity);
        DebugUtil.printDensityLog("setSystemResources " + displayMetrics + " " + configuration + " defaultBitmapDensity:" + displayConfig.defaultBitmapDensity);
    }

    private static void setDefaultBitmapDensity(int i) {
        try {
            ReflectionHelper.invoke(Bitmap.class, null, "setDefaultDensity", new Class[]{Integer.TYPE}, Integer.valueOf(i));
            DebugUtil.printDensityLog("setDefaultBitmapDensity " + i);
        } catch (Exception e) {
            DebugUtil.printDensityLog("reflect exception: " + e.toString());
        }
    }

    public static int getDefaultBitmapDensity() {
        try {
            return ((Integer) ReflectionHelper.invokeObject(Bitmap.class, null, "getDefaultDensity", new Class[0], new Object[0])).intValue();
        } catch (Exception e) {
            if (!DebugUtil.isEnableDebug()) {
                return -1;
            }
            DebugUtil.printDensityLog("reflect exception: " + e.toString());
            return -1;
        }
    }

    public static boolean isSupportSwitchResolution() {
        return sIsSupportSwitchResolution;
    }

    public static Display getCurrentDisplay(Context context) {
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                return context.getDisplay();
            } catch (Exception unused) {
            }
        }
        return getDefaultDisplay(context);
    }

    public static Display getDefaultDisplay(Context context) {
        return ((DisplayManager) context.getSystemService("display")).getDisplay(0);
    }

    public static boolean shouldProcessDensity(Context context, Display display) {
        if (DeviceHelper.isCarWithScreen(context, display)) {
            if (DebugUtil.isEnableDebug()) {
                DebugUtil.printDensityLog("shouldProcessDensity isCarWithScreen");
            }
            return false;
        }
        boolean zIsXiaomiSynergy = DeviceHelper.isXiaomiSynergy(context);
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("shouldProcessDensity isSynergy " + zIsXiaomiSynergy);
        }
        if (zIsXiaomiSynergy) {
            String synergyOwnerDevice = getSynergyOwnerDevice(display);
            if (SYNERGY_OWNER_WINDOWS.contentEquals(synergyOwnerDevice)) {
                return false;
            }
            if (SYNERGY_OWNER_ANDROID_PAD.contentEquals(synergyOwnerDevice)) {
                if (RomUtils.getHyperOsVersion() > 1) {
                    return false;
                }
            } else if (SYNERGY_OWNER_ANDROID_PAD_CAR.contentEquals(synergyOwnerDevice) && display.getDisplayId() != 0) {
                return false;
            }
        }
        return true;
    }

    private static String getSynergyOwnerDevice(Display display) {
        int iIndexOf;
        String name = display.getName();
        if (name == null || (iIndexOf = name.indexOf(MethodCodeHelper.IDENTITY_INFO_SEPARATOR)) == -1) {
            return "";
        }
        return name.substring(iIndexOf + 1);
    }
}
