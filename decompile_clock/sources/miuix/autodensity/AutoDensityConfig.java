package miuix.autodensity;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.ICompatCameraControlCallback;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewRootImpl;
import miuix.core.util.EnvStateManager;
import miuix.core.util.ScreenModeHelper;
import miuix.core.util.WindowBaseInfo;
import miuix.os.DeviceHelper;
import miuix.reflect.ReflectionHelper;

/* JADX INFO: loaded from: classes2.dex */
public class AutoDensityConfig extends DensityProcessor {
    private static final String TAG_CONFIG_CHANGE_FRAGMENT = "ConfigurationChangeFragment";
    private static AutoDensityConfig sInstance = null;
    private static boolean sUpdateSystemResources = true;
    private boolean sCanAccessHiddenAPI = false;

    public static void setForceDeviceScale(float f) {
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("setForceDeviceScale " + f + " trace:" + Log.getStackTraceString(new Throwable()));
        }
        DensityConfigManager.getInstance().setUserDeviceScale(f);
    }

    public static void setForcePPI(int i) {
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("setForcePPI " + i + " trace:" + Log.getStackTraceString(new Throwable()));
        }
        DensityConfigManager.getInstance().setUserPPI(i);
    }

    @Deprecated
    public static void setUseStableDensityLogic(boolean z) {
        DensityConfigManager.getInstance().setUseStableDensityLogic(z);
    }

    @Deprecated
    public static void setUseDeprecatedDensityLogic(boolean z) {
        DensityConfigManager.getInstance().setUseDeprecatedDensityLogic(z);
    }

    public static AutoDensityConfig init(Application application) {
        if (sInstance == null) {
            sInstance = init(application, true);
        }
        return sInstance;
    }

    public static AutoDensityConfig init(Application application, boolean z) {
        if (sInstance == null) {
            sUpdateSystemResources = z;
            sInstance = new AutoDensityConfig(application);
        }
        return sInstance;
    }

    public static boolean shouldUpdateSystemResource() {
        return sUpdateSystemResources;
    }

    public static void setUpdateSystemRes(boolean z) {
        sUpdateSystemResources = z;
        if (z) {
            if (DensityConfigManager.getInstance().getTargetConfig() == null) {
                return;
            }
            DensityUtil.setSystemResources(DensityConfigManager.getInstance().getTargetConfig());
        } else {
            DensityConfig originConfig = DensityConfigManager.getInstance().getOriginConfig();
            if (originConfig == null) {
                return;
            }
            DensityUtil.setSystemResources(originConfig);
        }
    }

    public static Context createAutoDensityContextWrapper(Context context) {
        return createAutoDensityContextWrapper(context, 0, 0);
    }

    public static Context createAutoDensityContextWrapper(Context context, int i) {
        return createAutoDensityContextWrapper(context, i, 0);
    }

    public static Context createAutoDensityContextWrapperWithBaseDp(Context context, int i) {
        return createAutoDensityContextWrapper(context, 0, i);
    }

    public static Context createAutoDensityContextWrapper(Context context, int i, int i2) {
        Configuration configuration = context.getResources().getConfiguration();
        Configuration configuration2 = new Configuration(configuration);
        Display currentDisplay = DensityUtil.getCurrentDisplay(context);
        if (DensityConfigManager.getInstance().getTargetConfig(currentDisplay) == null) {
            DensityConfigManager.getInstance().init(context);
        }
        AutoDensityContextWrapper autoDensityContextWrapper = new AutoDensityContextWrapper(context, i);
        DensityConfigManager.getInstance().updateConfig(context, configuration, currentDisplay);
        autoDensityContextWrapper.setOriginConfiguration(configuration2);
        DensityUtil.updateCustomDensity(autoDensityContextWrapper, i2, currentDisplay);
        return autoDensityContextWrapper;
    }

    public static Configuration updateDensityOverrideConfiguration(Context context, Configuration configuration) {
        Configuration noDensityOverrideConfiguration = DensityUtil.getNoDensityOverrideConfiguration(context);
        if (noDensityOverrideConfiguration != null) {
            EnvStateManager.markWindowInfoDirty(context);
        } else {
            noDensityOverrideConfiguration = configuration;
        }
        if (!DensityUtil.shouldUpdateDensityForConfig(noDensityOverrideConfiguration, DensityUtil.getCurrentDisplay(context))) {
            return configuration;
        }
        Configuration configuration2 = new Configuration(noDensityOverrideConfiguration);
        DensityUtil.updateDensityForConfig(context, configuration2);
        return configuration2;
    }

    private AutoDensityConfig(final Application application) {
        prepareInApplication(application);
        if (application instanceof miuix.app.Application) {
            miuix.app.Application application2 = (miuix.app.Application) application;
            application2.registerActivityLifecycleSubCallbacks(new DensityProcessor.DensityProcessorLifecycleCallbacks(this));
            application2.registerComponentSubCallbacks(new ComponentCallbacks() { // from class: miuix.autodensity.AutoDensityConfig.1
                @Override // android.content.ComponentCallbacks
                public void onLowMemory() {
                }

                @Override // android.content.ComponentCallbacks
                public void onConfigurationChanged(Configuration configuration) {
                    AutoDensityConfig.this.processOnAppConfigChanged(application, configuration);
                }
            });
        } else {
            application.registerActivityLifecycleCallbacks(new DensityProcessor.DensityProcessorLifecycleCallbacks(this));
            application.registerComponentCallbacks(new ComponentCallbacks() { // from class: miuix.autodensity.AutoDensityConfig.2
                @Override // android.content.ComponentCallbacks
                public void onLowMemory() {
                }

                @Override // android.content.ComponentCallbacks
                public void onConfigurationChanged(Configuration configuration) {
                    AutoDensityConfig.this.processOnAppConfigChanged(application, configuration);
                }
            });
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.autodensity.DensityProcessor
    public boolean isEnableProcessInActivity(Activity activity) {
        boolean zShouldAdaptAutoDensity;
        try {
            if (activity instanceof IDensity) {
                zShouldAdaptAutoDensity = ((IDensity) activity).shouldAdaptAutoDensity();
            } else {
                if (!(activity.getApplication() instanceof IDensity)) {
                    return false;
                }
                zShouldAdaptAutoDensity = ((IDensity) activity.getApplication()).shouldAdaptAutoDensity();
            }
            return zShouldAdaptAutoDensity;
        } catch (Exception unused) {
            return false;
        }
    }

    @Override // miuix.autodensity.DensityProcessor
    public void prepareInApplication(Application application) {
        try {
            this.sCanAccessHiddenAPI = ((Boolean) ReflectionHelper.invokeObject(ApplicationInfo.class, application.getApplicationInfo(), "isAllowedToUseHiddenApis", new Class[0], new Object[0])).booleanValue();
        } catch (Exception unused) {
        }
        DebugUtil.initAutoDensityDebugEnable();
        DensityConfigManager.getInstance().init(application);
        if (isShouldAdaptAutoDensity(application)) {
            DensityUtil.updateCustomDensity(application);
        }
    }

    @Override // miuix.autodensity.DensityProcessor
    public void processOnAppConfigChanged(Application application, Configuration configuration) {
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("->processOnAppConfigChanged");
        }
        DensityConfigManager.getInstance().tryUpdateConfig(application, configuration);
        if (isShouldAdaptAutoDensity(application)) {
            Display currentDisplay = DensityUtil.getCurrentDisplay(application);
            DensityUtil.updateCustomDensity(application);
            onDensityChangedOnAppConfigChanged(application);
            configuration.densityDpi = DensityConfigManager.getInstance().getTargetConfig(currentDisplay).densityDpi;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.autodensity.DensityProcessor
    public void processOnActivityCreated(Activity activity) {
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("->processOnActivityCreated");
        }
        boolean zIsShouldAdaptAutoDensity = isShouldAdaptAutoDensity(activity.getApplication());
        boolean zShouldAdaptAutoDensity = activity instanceof IDensity ? ((IDensity) activity).shouldAdaptAutoDensity() : zIsShouldAdaptAutoDensity;
        if (zShouldAdaptAutoDensity && DeviceHelper.isInRearDisplay(activity) && activity.getResources() != null) {
            DensityConfigManager.getInstance().tryUpdateConfig(activity, activity.getResources().getConfiguration());
        }
        updateApplicationDensity(activity.getApplication());
        if (zShouldAdaptAutoDensity) {
            DensityUtil.updateCustomDensity(activity);
            onDensityChangedOnActivityCreated(activity);
        } else if (zIsShouldAdaptAutoDensity) {
            DensityUtil.restoreDefaultDensity(activity);
            onDensityChangedOnActivityCreated(activity);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.autodensity.DensityProcessor
    protected void processBeforeActivityConfigChanged(Activity activity, Configuration configuration) {
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("->processBeforeActivityConfigChanged");
        }
        boolean zIsShouldAdaptAutoDensity = isShouldAdaptAutoDensity(activity.getApplication());
        if (activity instanceof IDensity ? ((IDensity) activity).shouldAdaptAutoDensity() : zIsShouldAdaptAutoDensity) {
            DensityUtil.updateCustomDensity(activity);
            WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(activity);
            onDensityChangedBeforeActivityConfigChanged(activity, configuration, windowInfo);
            if (!ScreenModeHelper.isInSplitScreenMode(windowInfo.windowMode)) {
                ScreenModeHelper.isInFreeFormMode(windowInfo.windowMode);
            }
            if (Build.VERSION.SDK_INT <= 31) {
                removeCurrentConfig(activity);
                return;
            } else {
                changeCurrentConfig(activity);
                return;
            }
        }
        if (zIsShouldAdaptAutoDensity) {
            boolean zRestoreDefaultDensity = DensityUtil.restoreDefaultDensity(activity);
            WindowBaseInfo windowInfo2 = EnvStateManager.getWindowInfo(activity);
            onDensityChangedBeforeActivityConfigChanged(activity, configuration, windowInfo2);
            if (zRestoreDefaultDensity) {
                if (!ScreenModeHelper.isInSplitScreenMode(windowInfo2.windowMode)) {
                    ScreenModeHelper.isInFreeFormMode(windowInfo2.windowMode);
                }
                if (Build.VERSION.SDK_INT <= 31) {
                    removeCurrentConfig(activity);
                } else {
                    changeCurrentConfig(activity);
                }
            }
        }
    }

    @Override // miuix.autodensity.DensityProcessor
    public void processOnActivityDestroyed(Activity activity) {
        unregisterCallback(activity);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // miuix.autodensity.DensityProcessor
    public void processOnActivityDisplayChanged(int i, Activity activity) {
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("->onDisplayChanged displayId: " + i + " config " + activity.getResources().getConfiguration() + "\n activity: " + activity);
        }
        boolean zIsShouldAdaptAutoDensity = isShouldAdaptAutoDensity(activity.getApplication());
        if (activity instanceof IDensity ? ((IDensity) activity).shouldAdaptAutoDensity() : zIsShouldAdaptAutoDensity) {
            DensityUtil.updateCustomDensity(activity);
            onDensityChangedOnActivityDisplayChanged(i, activity);
        } else if (zIsShouldAdaptAutoDensity) {
            DensityUtil.restoreDefaultDensity(activity);
            onDensityChangedOnActivityDisplayChanged(i, activity);
        }
    }

    @Override // miuix.autodensity.DensityProcessor
    protected void onDensityChangedOnActivityCreated(Activity activity) {
        super.onDensityChangedOnActivityCreated(activity);
        addForOnConfigurationChange(activity);
    }

    @Override // miuix.autodensity.DensityProcessor
    protected void registerCallback(Activity activity) {
        super.registerCallback(activity);
        tryToAddActivityConfigCallback(activity);
    }

    private void tryToAddActivityConfigCallback(final Activity activity) {
        View viewPeekDecorView;
        if (!this.sCanAccessHiddenAPI || (viewPeekDecorView = activity.getWindow().peekDecorView()) == null) {
            return;
        }
        View.OnAttachStateChangeListener onAttachStateChangeListener = new View.OnAttachStateChangeListener() { // from class: miuix.autodensity.AutoDensityConfig.3
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View view) {
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View view) {
                try {
                    Object objInvokeObject = ReflectionHelper.invokeObject(View.class, view, "getViewRootImpl", new Class[0], new Object[0]);
                    final Object fieldValue = ReflectionHelper.getFieldValue(ViewRootImpl.class, objInvokeObject, "mActivityConfigCallback");
                    ReflectionHelper.invokeObject(ViewRootImpl.class, objInvokeObject, "setActivityConfigCallback", new Class[]{ViewRootImpl.ActivityConfigCallback.class}, new ViewRootImpl.ActivityConfigCallback() { // from class: miuix.autodensity.AutoDensityConfig.3.1
                        public void requestCompatCameraControl(boolean z, boolean z2, ICompatCameraControlCallback iCompatCameraControlCallback) {
                        }

                        public void onConfigurationChanged(Configuration configuration, int i) {
                            try {
                                ReflectionHelper.invokeObject(ViewRootImpl.ActivityConfigCallback.class, fieldValue, "onConfigurationChanged", new Class[]{Configuration.class, Integer.TYPE}, configuration, Integer.valueOf(i));
                                boolean zIsShouldAdaptAutoDensity = AutoDensityConfig.isShouldAdaptAutoDensity(activity.getApplication());
                                if (activity instanceof IDensity) {
                                    zIsShouldAdaptAutoDensity = ((IDensity) activity).shouldAdaptAutoDensity();
                                }
                                if (zIsShouldAdaptAutoDensity) {
                                    DensityUtil.updateCustomDensity(activity);
                                }
                            } catch (Exception unused) {
                            }
                        }
                    });
                } catch (Exception unused) {
                }
                view.removeOnAttachStateChangeListener(this);
            }
        };
        viewPeekDecorView.addOnAttachStateChangeListener(onAttachStateChangeListener);
        this.mModifier.get(Integer.valueOf(activity.hashCode())).addOnAttachStateChangeListener(onAttachStateChangeListener);
    }

    private void addForOnConfigurationChange(Activity activity) {
        Fragment configurationChangeFragment = getConfigurationChangeFragment(activity);
        if (configurationChangeFragment == null) {
            ConfigurationChangeFragment configurationChangeFragment2 = new ConfigurationChangeFragment();
            configurationChangeFragment2.setDensityProcessor(this);
            activity.getFragmentManager().beginTransaction().add(configurationChangeFragment2, TAG_CONFIG_CHANGE_FRAGMENT).commitAllowingStateLoss();
        } else {
            ((ConfigurationChangeFragment) configurationChangeFragment).setDensityProcessor(this);
            Log.d("AutoDensity", "ConfigurationChangeFragment has already added");
        }
    }

    private void removeCurrentConfig(Activity activity) {
        try {
            ReflectionHelper.setFieldValue(Activity.class, activity, "mCurrentConfig", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeCurrentConfig(Activity activity) {
        try {
            ((Configuration) ReflectionHelper.getFieldValue(Activity.class, activity, "mCurrentConfig")).densityDpi = DensityConfigManager.getInstance().getTargetConfig(DensityUtil.getCurrentDisplay(activity)).densityDpi;
            ActivityInfo activityInfo = (ActivityInfo) ReflectionHelper.getFieldValue(Activity.class, activity, "mActivityInfo");
            if ((activityInfo.configChanges & 4096) == 0) {
                activityInfo.configChanges |= 4096;
                Fragment configurationChangeFragment = getConfigurationChangeFragment(activity);
                if (configurationChangeFragment != null) {
                    ((ConfigurationChangeFragment) configurationChangeFragment).removeDensityChangeFlag();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Fragment getConfigurationChangeFragment(Activity activity) {
        return activity.getFragmentManager().findFragmentByTag(TAG_CONFIG_CHANGE_FRAGMENT);
    }

    void updateApplicationDensity(Application application) {
        if (Build.VERSION.SDK_INT == 29 && isShouldAdaptAutoDensity(application)) {
            DensityUtil.updateCustomDensity(application);
        }
    }

    public boolean updateDensityOnConfigChanged(Context context, Configuration configuration) {
        boolean zTryUpdateConfig = DensityConfigManager.getInstance().tryUpdateConfig(context, configuration);
        if (context instanceof Activity) {
            Application application = ((Activity) context).getApplication();
            if (isShouldAdaptAutoDensity(application)) {
                updateApplicationDensity(application);
            }
        }
        updateDensity(context);
        return zTryUpdateConfig;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static boolean isShouldAdaptAutoDensity(Application application) {
        if (application instanceof IDensity) {
            return ((IDensity) application).shouldAdaptAutoDensity();
        }
        return true;
    }

    public static boolean updateDensityByConfig(Context context, Configuration configuration) {
        AutoDensityConfig autoDensityConfig = sInstance;
        if (autoDensityConfig == null || context == null) {
            return false;
        }
        return autoDensityConfig.updateDensityOnConfigChanged(context, configuration);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v11 */
    /* JADX WARN: Type inference failed for: r1v2, types: [android.app.Activity] */
    /* JADX WARN: Type inference failed for: r1v9 */
    public static void updateDensity(Context context) {
        Application application;
        boolean z;
        boolean zIsShouldAdaptAutoDensity;
        if (sInstance == null) {
            return;
        }
        ?? r1 = 0;
        r1 = 0;
        r1 = 0;
        if (context instanceof Activity) {
            r1 = (Activity) context;
            application = null;
        } else if (context instanceof Application) {
            application = (Application) context;
        } else {
            if (!(context instanceof ContextWrapper)) {
                application = null;
                break;
            }
            ContextWrapper contextWrapper = (ContextWrapper) context;
            while (true) {
                if (!(contextWrapper.getBaseContext() instanceof ContextWrapper)) {
                    application = null;
                    break;
                }
                contextWrapper = (ContextWrapper) contextWrapper.getBaseContext();
                if (contextWrapper instanceof Activity) {
                    context = (Activity) contextWrapper;
                    application = null;
                    r1 = context;
                    break;
                } else if (contextWrapper instanceof Application) {
                    application = (Application) contextWrapper;
                    break;
                }
            }
        }
        if (r1 != 0) {
            zIsShouldAdaptAutoDensity = isShouldAdaptAutoDensity(r1.getApplication());
            if (r1 instanceof IDensity) {
                boolean zShouldAdaptAutoDensity = ((IDensity) r1).shouldAdaptAutoDensity();
                z = zIsShouldAdaptAutoDensity;
                zIsShouldAdaptAutoDensity = zShouldAdaptAutoDensity;
            } else {
                z = zIsShouldAdaptAutoDensity;
            }
        } else {
            z = false;
            zIsShouldAdaptAutoDensity = application != null ? isShouldAdaptAutoDensity(application) : false;
        }
        if (zIsShouldAdaptAutoDensity) {
            forceUpdateDensity(context);
        } else if (z) {
            DensityUtil.restoreDefaultDensity(context);
        }
    }

    public static void forceUpdateDensity(Context context) {
        if (sInstance != null) {
            DensityUtil.updateCustomDensity(context);
        }
    }

    @Override // miuix.autodensity.DensityProcessor
    protected void onRegisterDensityCallback(Object obj) {
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("registerCallback obj: " + obj);
        }
    }
}
