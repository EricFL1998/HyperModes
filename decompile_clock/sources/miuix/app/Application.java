package miuix.app;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.core.util.Consumer;
import java.util.ArrayList;
import java.util.List;
import miuix.core.R;
import miuix.core.util.EnvStateManager;
import miuix.core.util.HyperMaterialUtils;

/* JADX INFO: loaded from: classes2.dex */
public class Application extends android.app.Application {
    private ComponentCallbacksWrapper mComponentCallbacksWrapper;
    private LifecycleCallbacksWrapper mLifecycleCallbacksWrapper;
    private Object mLifecycleLock = new Object();
    private Object mComponentLock = new Object();

    @Override // android.app.Application
    public void onCreate() {
        EnvStateManager.init(this);
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 31) {
            Resources resources = getApplicationContext().getResources();
            if (resources.getInteger(R.integer.miuix_theme_use_third_party_theme) == 2) {
                UiModeManager uiModeManager = (UiModeManager) getSystemService("uimode");
                uiModeManager.setApplicationNightMode(0);
                if (uiModeManager.getNightMode() == 1) {
                    if (!resources.getBoolean(R.bool.miuix_theme_use_light_theme_in_light)) {
                        uiModeManager.setApplicationNightMode(2);
                    }
                } else if (resources.getBoolean(R.bool.miuix_theme_use_light_theme_in_dark)) {
                    uiModeManager.setApplicationNightMode(1);
                }
            }
        }
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        HyperMaterialUtils.clearFeatureEnable();
        EnvStateManager.markEnvStateDirty(this);
        super.onConfigurationChanged(configuration);
    }

    public void registerActivityLifecycleSubCallbacks(android.app.Application.ActivityLifecycleCallbacks activityLifecycleCallbacks) {
        synchronized (this.mLifecycleLock) {
            if (this.mLifecycleCallbacksWrapper == null) {
                LifecycleCallbacksWrapper lifecycleCallbacksWrapper = new LifecycleCallbacksWrapper();
                this.mLifecycleCallbacksWrapper = lifecycleCallbacksWrapper;
                registerActivityLifecycleCallbacks(lifecycleCallbacksWrapper);
            }
            this.mLifecycleCallbacksWrapper.add(activityLifecycleCallbacks);
        }
    }

    public void unregisterActivityLifecycleSubCallbacks(android.app.Application.ActivityLifecycleCallbacks activityLifecycleCallbacks) {
        synchronized (this.mLifecycleLock) {
            LifecycleCallbacksWrapper lifecycleCallbacksWrapper = this.mLifecycleCallbacksWrapper;
            if (lifecycleCallbacksWrapper != null) {
                lifecycleCallbacksWrapper.remove(activityLifecycleCallbacks);
                if (this.mLifecycleCallbacksWrapper.getSize() == 0) {
                    unregisterActivityLifecycleCallbacks(this.mLifecycleCallbacksWrapper);
                    this.mLifecycleCallbacksWrapper = null;
                }
            }
        }
    }

    public void registerComponentSubCallbacks(ComponentCallbacks componentCallbacks) {
        synchronized (this.mComponentLock) {
            if (this.mComponentCallbacksWrapper == null) {
                ComponentCallbacksWrapper componentCallbacksWrapper = new ComponentCallbacksWrapper(this);
                this.mComponentCallbacksWrapper = componentCallbacksWrapper;
                registerComponentCallbacks(componentCallbacksWrapper);
            }
            this.mComponentCallbacksWrapper.registerCallBacks(componentCallbacks);
        }
    }

    public void unregisterComponentSubCallbacks(ComponentCallbacks componentCallbacks) {
        synchronized (this.mComponentLock) {
            ComponentCallbacksWrapper componentCallbacksWrapper = this.mComponentCallbacksWrapper;
            if (componentCallbacksWrapper != null) {
                componentCallbacksWrapper.unregisterCallBacks(componentCallbacks);
                if (this.mComponentCallbacksWrapper.getSize() == 0) {
                    unregisterComponentCallbacks(this.mComponentCallbacksWrapper);
                    this.mComponentCallbacksWrapper = null;
                }
            }
        }
    }

    static class LifecycleCallbacksWrapper implements android.app.Application.ActivityLifecycleCallbacks {
        private ArrayList<android.app.Application.ActivityLifecycleCallbacks> mActivitySubLifecycleCallbacks = new ArrayList<>();

        LifecycleCallbacksWrapper() {
        }

        public boolean add(android.app.Application.ActivityLifecycleCallbacks activityLifecycleCallbacks) {
            return this.mActivitySubLifecycleCallbacks.add(activityLifecycleCallbacks);
        }

        public boolean remove(android.app.Application.ActivityLifecycleCallbacks activityLifecycleCallbacks) {
            return this.mActivitySubLifecycleCallbacks.remove(activityLifecycleCallbacks);
        }

        public int getSize() {
            return this.mActivitySubLifecycleCallbacks.size();
        }

        private Object[] collectActivityLifecycleSubCallbacks() {
            Object[] array;
            synchronized (this.mActivitySubLifecycleCallbacks) {
                array = this.mActivitySubLifecycleCallbacks.size() > 0 ? this.mActivitySubLifecycleCallbacks.toArray() : null;
            }
            return array;
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle bundle) {
            Object[] objArrCollectActivityLifecycleSubCallbacks = collectActivityLifecycleSubCallbacks();
            if (objArrCollectActivityLifecycleSubCallbacks != null) {
                for (Object obj : objArrCollectActivityLifecycleSubCallbacks) {
                    ((android.app.Application.ActivityLifecycleCallbacks) obj).onActivityCreated(activity, bundle);
                }
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStarted(Activity activity) {
            Object[] objArrCollectActivityLifecycleSubCallbacks = collectActivityLifecycleSubCallbacks();
            if (objArrCollectActivityLifecycleSubCallbacks != null) {
                for (Object obj : objArrCollectActivityLifecycleSubCallbacks) {
                    ((android.app.Application.ActivityLifecycleCallbacks) obj).onActivityStarted(activity);
                }
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityResumed(Activity activity) {
            Object[] objArrCollectActivityLifecycleSubCallbacks = collectActivityLifecycleSubCallbacks();
            if (objArrCollectActivityLifecycleSubCallbacks != null) {
                for (Object obj : objArrCollectActivityLifecycleSubCallbacks) {
                    ((android.app.Application.ActivityLifecycleCallbacks) obj).onActivityResumed(activity);
                }
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityPaused(Activity activity) {
            Object[] objArrCollectActivityLifecycleSubCallbacks = collectActivityLifecycleSubCallbacks();
            if (objArrCollectActivityLifecycleSubCallbacks != null) {
                for (Object obj : objArrCollectActivityLifecycleSubCallbacks) {
                    ((android.app.Application.ActivityLifecycleCallbacks) obj).onActivityPaused(activity);
                }
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
            Object[] objArrCollectActivityLifecycleSubCallbacks = collectActivityLifecycleSubCallbacks();
            if (objArrCollectActivityLifecycleSubCallbacks != null) {
                for (Object obj : objArrCollectActivityLifecycleSubCallbacks) {
                    ((android.app.Application.ActivityLifecycleCallbacks) obj).onActivityStopped(activity);
                }
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            Object[] objArrCollectActivityLifecycleSubCallbacks = collectActivityLifecycleSubCallbacks();
            if (objArrCollectActivityLifecycleSubCallbacks != null) {
                for (Object obj : objArrCollectActivityLifecycleSubCallbacks) {
                    ((android.app.Application.ActivityLifecycleCallbacks) obj).onActivitySaveInstanceState(activity, bundle);
                }
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
            Object[] objArrCollectActivityLifecycleSubCallbacks = collectActivityLifecycleSubCallbacks();
            if (objArrCollectActivityLifecycleSubCallbacks != null) {
                for (Object obj : objArrCollectActivityLifecycleSubCallbacks) {
                    ((android.app.Application.ActivityLifecycleCallbacks) obj).onActivityDestroyed(activity);
                }
            }
        }
    }

    static class ComponentCallbacksWrapper implements ComponentCallbacks {
        private List<ComponentCallbacks> mComponentSubCallbacks;
        private final Context mContext;

        public ComponentCallbacksWrapper(Context context) {
            this.mContext = context;
        }

        public int getSize() {
            return this.mComponentSubCallbacks.size();
        }

        public void registerCallBacks(ComponentCallbacks componentCallbacks) {
            if (this.mComponentSubCallbacks == null) {
                this.mComponentSubCallbacks = new ArrayList();
            }
            this.mComponentSubCallbacks.add(componentCallbacks);
        }

        public void unregisterCallBacks(ComponentCallbacks componentCallbacks) {
            List<ComponentCallbacks> list = this.mComponentSubCallbacks;
            if (list == null || list.isEmpty()) {
                return;
            }
            this.mComponentSubCallbacks.remove(componentCallbacks);
        }

        @Override // android.content.ComponentCallbacks
        public void onConfigurationChanged(final Configuration configuration) {
            forAllComponentCallbacks(new Consumer() { // from class: miuix.app.Application$ComponentCallbacksWrapper$$ExternalSyntheticLambda1
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((ComponentCallbacks) obj).onConfigurationChanged(configuration);
                }
            });
        }

        @Override // android.content.ComponentCallbacks
        public void onLowMemory() {
            forAllComponentCallbacks(new Consumer() { // from class: miuix.app.Application$ComponentCallbacksWrapper$$ExternalSyntheticLambda0
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((ComponentCallbacks) obj).onLowMemory();
                }
            });
        }

        private void forAllComponentCallbacks(Consumer<ComponentCallbacks> consumer) {
            synchronized (this) {
                List<ComponentCallbacks> list = this.mComponentSubCallbacks;
                if (list != null && !list.isEmpty()) {
                    int size = this.mComponentSubCallbacks.size();
                    ComponentCallbacks[] componentCallbacksArr = new ComponentCallbacks[size];
                    this.mComponentSubCallbacks.toArray(componentCallbacksArr);
                    for (int i = 0; i < size; i++) {
                        consumer.accept(componentCallbacksArr[i]);
                    }
                }
            }
        }
    }
}
