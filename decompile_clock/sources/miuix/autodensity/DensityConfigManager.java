package miuix.autodensity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import com.android.deskclock.R2;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import miuix.core.util.RomUtils;
import miuix.os.DeviceHelper;

/* JADX INFO: loaded from: classes2.dex */
public class DensityConfigManager {
    private static DensityConfigManager sInstance;
    private ConcurrentHashMap<Integer, DisplayDensityConfig> mDisplayConfigMap = new ConcurrentHashMap<>();
    private boolean mUseStableDensityLogic = false;

    @Deprecated
    private boolean mUseDeprecatedDensityLogic = false;
    private double mUserDeviceScale = 0.0d;
    private int mUserPPI = 0;
    private float mUserAccessibilityDpiDelta = 0.0f;
    private int mUserForcedDpi = 0;

    public static DensityConfigManager getInstance() {
        if (sInstance == null) {
            sInstance = new DensityConfigManager();
        }
        return sInstance;
    }

    private DensityConfigManager() {
    }

    public void init(Context context) {
        Display currentDisplay = DensityUtil.getCurrentDisplay(context);
        if (DeviceHelper.isCarWithScreen(context, currentDisplay) || DeviceHelper.isXiaomiSynergy(context)) {
            return;
        }
        getOrCreateDisplayConfig(context, currentDisplay);
    }

    public DisplayDensityConfig getOrCreateDisplayConfig(Context context, Display display) {
        int iValueOf;
        DisplayDensityConfig displayDensityConfig = null;
        if (context != null && display != null) {
            if (display.getDisplayId() == 0) {
                iValueOf = 0;
            } else {
                iValueOf = Integer.valueOf(display.hashCode());
            }
            if (iValueOf == null) {
                return null;
            }
            displayDensityConfig = this.mDisplayConfigMap.get(iValueOf);
            if (displayDensityConfig == null) {
                displayDensityConfig = new DisplayDensityConfig(context, display);
                if (DebugUtil.isEnableDebug()) {
                    DebugUtil.printDensityLog("DisplayDensityConfig create DisplayConfig display:  display: " + display + " context: " + context);
                }
                displayDensityConfig.setUserPPI(this.mUserPPI);
                displayDensityConfig.setUserDeviceScale((float) this.mUserDeviceScale);
                displayDensityConfig.setUseStableDensityLogic(this.mUseStableDensityLogic);
                displayDensityConfig.setUseDeprecatedDensityLogic(this.mUseDeprecatedDensityLogic);
                this.mDisplayConfigMap.put(iValueOf, displayDensityConfig);
            }
        }
        return displayDensityConfig;
    }

    public DisplayDensityConfig getDefaultConfig() {
        return this.mDisplayConfigMap.get(0);
    }

    @Deprecated
    public void setUseStableDensityLogic(boolean z) {
        this.mUseStableDensityLogic = z;
        Iterator<Map.Entry<Integer, DisplayDensityConfig>> it = this.mDisplayConfigMap.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().setUseStableDensityLogic(z);
        }
    }

    @Deprecated
    public void setUseDeprecatedDensityLogic(boolean z) {
        this.mUseDeprecatedDensityLogic = z;
        Iterator<Map.Entry<Integer, DisplayDensityConfig>> it = this.mDisplayConfigMap.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().setUseDeprecatedDensityLogic(z);
        }
    }

    public boolean isEnableLogicMetrics() {
        if (this.mUseDeprecatedDensityLogic || RomUtils.getMiuiVersion() < 14) {
            return true;
        }
        return Build.VERSION.SDK_INT >= 35 && !this.mUseStableDensityLogic;
    }

    public void setUserDeviceScale(float f) {
        this.mUserDeviceScale = f;
        Iterator<Map.Entry<Integer, DisplayDensityConfig>> it = this.mDisplayConfigMap.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().setUserDeviceScale(f);
        }
    }

    public void setUserDeviceScale(float f, Display display) {
        DisplayDensityConfig displayConfig = getDisplayConfig(display);
        if (displayConfig != null) {
            displayConfig.setUserDeviceScale(f);
        }
    }

    public void setUserPPI(int i) {
        this.mUserPPI = i;
        Iterator<Map.Entry<Integer, DisplayDensityConfig>> it = this.mDisplayConfigMap.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().setUserPPI(i);
        }
    }

    public void setUserPPI(int i, Display display) {
        DisplayDensityConfig displayConfig = getDisplayConfig(display);
        if (displayConfig != null) {
            displayConfig.setUserPPI(i);
        }
    }

    public void setUserAccessibilityDpiDelta(float f) {
        this.mUserAccessibilityDpiDelta = f;
        Iterator<Map.Entry<Integer, DisplayDensityConfig>> it = this.mDisplayConfigMap.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().setUserAccessibilityDpiDelta(f);
        }
    }

    public void setUserAccessibilityDpiDelta(float f, Display display) {
        DisplayDensityConfig displayConfig = getDisplayConfig(display);
        if (displayConfig != null) {
            displayConfig.setUserAccessibilityDpiDelta(f);
        }
    }

    public void setUserForcedDpi(int i) {
        this.mUserForcedDpi = i;
        Iterator<Map.Entry<Integer, DisplayDensityConfig>> it = this.mDisplayConfigMap.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().setUserForcedDpi(i);
        }
    }

    public void setUserForcedDpi(int i, Display display) {
        DisplayDensityConfig displayConfig = getDisplayConfig(display);
        if (displayConfig != null) {
            displayConfig.setUserForcedDpi(i);
        }
    }

    public double getCurrentDeviceScale(Display display) {
        DisplayDensityConfig displayConfig = getDisplayConfig(display);
        if (displayConfig == null) {
            return 1.0d;
        }
        return displayConfig.getCurrentDeviceScale();
    }

    public double getCurrentDeviceScale() {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        if (defaultConfig == null) {
            return 1.0d;
        }
        return defaultConfig.getCurrentDeviceScale();
    }

    public double getCurrentPPI(Display display) {
        DisplayDensityConfig displayConfig = getDisplayConfig(display);
        if (displayConfig == null) {
            return 0.0d;
        }
        return displayConfig.getCurrentPPI();
    }

    public double getCurrentPPI() {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        if (defaultConfig == null) {
            return 0.0d;
        }
        return defaultConfig.getCurrentPPI();
    }

    public float getScreenInches(Display display) {
        DisplayDensityConfig displayConfig = getDisplayConfig(display);
        if (displayConfig == null) {
            return 0.0f;
        }
        return displayConfig.getScreenInches();
    }

    public float getScreenInches() {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        if (defaultConfig == null) {
            return 0.0f;
        }
        return defaultConfig.getScreenInches();
    }

    public DisplayDensityConfig getDisplayConfig(Display display) {
        int iValueOf;
        if (display == null) {
            return null;
        }
        if (display.getDisplayId() == 0) {
            iValueOf = 0;
        } else {
            iValueOf = Integer.valueOf(display.hashCode());
        }
        if (iValueOf == null) {
            return null;
        }
        return this.mDisplayConfigMap.get(iValueOf);
    }

    public DensityConfig getTargetConfig(Display display) {
        if (display == null) {
            return getTargetConfig();
        }
        DisplayDensityConfig displayDensityConfig = this.mDisplayConfigMap.get(Integer.valueOf(display.getDisplayId() == 0 ? 0 : display.hashCode()));
        if (displayDensityConfig == null) {
            return null;
        }
        return displayDensityConfig.getTargetConfig();
    }

    @Deprecated
    public DensityConfig getTargetConfig() {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        if (defaultConfig == null) {
            return null;
        }
        return defaultConfig.getTargetConfig();
    }

    public DensityConfig getOriginConfig(Display display) {
        if (display == null) {
            return getOriginConfig();
        }
        DisplayDensityConfig displayDensityConfig = this.mDisplayConfigMap.get(Integer.valueOf(display.getDisplayId() == 0 ? 0 : display.hashCode()));
        if (displayDensityConfig == null) {
            return null;
        }
        return displayDensityConfig.getOriginConfig();
    }

    @Deprecated
    public DensityConfig getOriginConfig() {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        if (defaultConfig == null) {
            return null;
        }
        return defaultConfig.getOriginConfig();
    }

    public boolean isAutoDensityEnabled() {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        if (defaultConfig == null) {
            return false;
        }
        return defaultConfig.isAutoDensityEnabled();
    }

    public boolean tryUpdateConfig(Context context, Configuration configuration) {
        Display currentDisplay = DensityUtil.getCurrentDisplay(context);
        DisplayDensityConfig orCreateDisplayConfig = getOrCreateDisplayConfig(context, currentDisplay);
        if (orCreateDisplayConfig == null) {
            Log.w("AutoDensity", " -> tryUpdateConfig failed: displayConfig is null, ");
            return false;
        }
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("tryUpdateConfig newConfig " + configuration + " context " + context);
        }
        return orCreateDisplayConfig.tryUpdateConfig(context, currentDisplay, configuration);
    }

    public void updateConfig(Context context, Configuration configuration) {
        Display currentDisplay = DensityUtil.getCurrentDisplay(context);
        DisplayDensityConfig orCreateDisplayConfig = getOrCreateDisplayConfig(context, currentDisplay);
        if (orCreateDisplayConfig == null) {
            Log.w("AutoDensity", " -> updateConfig failed: displayConfig is null");
        } else {
            orCreateDisplayConfig.updateConfig(context, currentDisplay, configuration);
        }
    }

    public void updateConfig(Context context, Configuration configuration, Display display) {
        if (display == null) {
            updateConfig(context, configuration);
            return;
        }
        DisplayDensityConfig orCreateDisplayConfig = getOrCreateDisplayConfig(context, display);
        if (orCreateDisplayConfig == null) {
            Log.w("AutoDensity", " -> updateConfig failed: displayConfig is null");
        } else {
            orCreateDisplayConfig.updateConfig(context, display, configuration);
        }
    }

    public boolean isLocalOriginDpi(int i) {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        if (defaultConfig == null) {
            return false;
        }
        return defaultConfig.isLocalOriginDpi(i);
    }

    @Deprecated
    public int getAccessibilityDefaultDisplayDpi(int i) {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        return defaultConfig == null ? R2.array.time : defaultConfig.getAccessibilityDisplayDpi();
    }

    public int getDeviceCurrentDefaultDpi() {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        return defaultConfig == null ? R2.array.time : defaultConfig.getDeviceCurrentDefaultDpi();
    }

    public int getDeviceCurrentForcedDpi(Display display) {
        if (display == null) {
            return getDeviceCurrentForcedDpi();
        }
        DisplayDensityConfig displayDensityConfig = this.mDisplayConfigMap.get(Integer.valueOf(display.getDisplayId() == 0 ? 0 : display.hashCode()));
        return displayDensityConfig == null ? R2.array.time : displayDensityConfig.getCurrentForcedDpi();
    }

    public int getDeviceCurrentForcedDpi() {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        return defaultConfig == null ? R2.array.time : defaultConfig.getCurrentForcedDpi();
    }

    public int getDeviceCurrentAccessibilityDpi(Display display) {
        if (display == null) {
            return getDeviceCurrentAccessibilityDpi();
        }
        DisplayDensityConfig displayDensityConfig = this.mDisplayConfigMap.get(Integer.valueOf(display.getDisplayId() == 0 ? 0 : display.hashCode()));
        return displayDensityConfig == null ? R2.array.time : displayDensityConfig.getCurrentAccessibilityDpi();
    }

    public int getDeviceCurrentAccessibilityDpi() {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        return defaultConfig == null ? R2.array.time : defaultConfig.getCurrentAccessibilityDpi();
    }

    public int getDeviceDefaultDpi() {
        DisplayDensityConfig defaultConfig = getDefaultConfig();
        return defaultConfig == null ? R2.array.time : defaultConfig.getDeviceDefaultDpi();
    }
}
