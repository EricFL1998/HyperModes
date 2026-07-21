package miuix.appcompat.internal.widget;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import miuix.autodensity.AutoDensityConfig;
import miuix.autodensity.AutoDensityContextWrapper;
import miuix.autodensity.DensityUtil;
import miuix.core.util.EnvStateManager;

/* JADX INFO: loaded from: classes2.dex */
public class DialogRootView extends FrameLayout {
    private ConfigurationChangedCallback mCallback;
    private ComponentCallbacks mComponentCallbacks;
    private boolean mNotifyConfigChanged;
    private RootViewSizeChangedCallback mSizeChangeCallback;
    private boolean mViewConfigChangedDispatched;

    public interface ConfigurationChangedCallback {
        void onConfigurationChanged(Configuration configuration, int i, int i2, int i3, int i4);
    }

    public interface RootViewSizeChangedCallback {
        void onSizeChanged(int i, int i2, int i3, int i4);
    }

    public DialogRootView(Context context) {
        super(context);
        this.mNotifyConfigChanged = false;
        this.mViewConfigChangedDispatched = false;
        this.mComponentCallbacks = new ComponentCallbacks() { // from class: miuix.appcompat.internal.widget.DialogRootView.1
            @Override // android.content.ComponentCallbacks
            public void onLowMemory() {
            }

            @Override // android.content.ComponentCallbacks
            public void onConfigurationChanged(Configuration configuration) {
                DialogRootView.this.mNotifyConfigChanged = true;
                DialogRootView.this.onConfigurationChanged(configuration);
                Handler handler = DialogRootView.this.getHandler();
                if (handler == null) {
                    return;
                }
                if (DialogRootView.this.mViewConfigChangedDispatched && Looper.myLooper() == handler.getLooper()) {
                    DialogRootView.this.requestLayout();
                } else {
                    DialogRootView.this.post(new Runnable() { // from class: miuix.appcompat.internal.widget.DialogRootView.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (DialogRootView.this.mNotifyConfigChanged && DialogRootView.this.isAttachedToWindow()) {
                                DialogRootView.this.requestLayout();
                            }
                        }
                    });
                }
            }
        };
    }

    public DialogRootView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mNotifyConfigChanged = false;
        this.mViewConfigChangedDispatched = false;
        this.mComponentCallbacks = new ComponentCallbacks() { // from class: miuix.appcompat.internal.widget.DialogRootView.1
            @Override // android.content.ComponentCallbacks
            public void onLowMemory() {
            }

            @Override // android.content.ComponentCallbacks
            public void onConfigurationChanged(Configuration configuration) {
                DialogRootView.this.mNotifyConfigChanged = true;
                DialogRootView.this.onConfigurationChanged(configuration);
                Handler handler = DialogRootView.this.getHandler();
                if (handler == null) {
                    return;
                }
                if (DialogRootView.this.mViewConfigChangedDispatched && Looper.myLooper() == handler.getLooper()) {
                    DialogRootView.this.requestLayout();
                } else {
                    DialogRootView.this.post(new Runnable() { // from class: miuix.appcompat.internal.widget.DialogRootView.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (DialogRootView.this.mNotifyConfigChanged && DialogRootView.this.isAttachedToWindow()) {
                                DialogRootView.this.requestLayout();
                            }
                        }
                    });
                }
            }
        };
    }

    public DialogRootView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNotifyConfigChanged = false;
        this.mViewConfigChangedDispatched = false;
        this.mComponentCallbacks = new ComponentCallbacks() { // from class: miuix.appcompat.internal.widget.DialogRootView.1
            @Override // android.content.ComponentCallbacks
            public void onLowMemory() {
            }

            @Override // android.content.ComponentCallbacks
            public void onConfigurationChanged(Configuration configuration) {
                DialogRootView.this.mNotifyConfigChanged = true;
                DialogRootView.this.onConfigurationChanged(configuration);
                Handler handler = DialogRootView.this.getHandler();
                if (handler == null) {
                    return;
                }
                if (DialogRootView.this.mViewConfigChangedDispatched && Looper.myLooper() == handler.getLooper()) {
                    DialogRootView.this.requestLayout();
                } else {
                    DialogRootView.this.post(new Runnable() { // from class: miuix.appcompat.internal.widget.DialogRootView.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (DialogRootView.this.mNotifyConfigChanged && DialogRootView.this.isAttachedToWindow()) {
                                DialogRootView.this.requestLayout();
                            }
                        }
                    });
                }
            }
        };
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().registerComponentCallbacks(this.mComponentCallbacks);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterComponentCallbacks(this.mComponentCallbacks);
        AutoDensityContextWrapper autoDensityContextWrapperFindAutoDensityContextWrapper = DensityUtil.findAutoDensityContextWrapper(getContext());
        if (autoDensityContextWrapperFindAutoDensityContextWrapper != null) {
            autoDensityContextWrapperFindAutoDensityContextWrapper.restoreOriginConfig();
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        AutoDensityContextWrapper autoDensityContextWrapperFindAutoDensityContextWrapper = DensityUtil.findAutoDensityContextWrapper(getContext());
        if (autoDensityContextWrapperFindAutoDensityContextWrapper != null) {
            autoDensityContextWrapperFindAutoDensityContextWrapper.getOriginConfiguration().setTo(configuration);
            if (this.mNotifyConfigChanged) {
                return;
            }
            AutoDensityConfig.updateDensityOverrideConfiguration(getContext(), configuration);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchConfigurationChanged(Configuration configuration) {
        this.mViewConfigChangedDispatched = true;
        super.dispatchConfigurationChanged(configuration);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, final int i, final int i2, final int i3, final int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mNotifyConfigChanged) {
            this.mViewConfigChangedDispatched = false;
            this.mNotifyConfigChanged = false;
            Configuration configuration = getResources().getConfiguration();
            final AutoDensityContextWrapper autoDensityContextWrapperFindAutoDensityContextWrapper = DensityUtil.findAutoDensityContextWrapper(getContext());
            if (autoDensityContextWrapperFindAutoDensityContextWrapper != null) {
                autoDensityContextWrapperFindAutoDensityContextWrapper.getOriginConfiguration().setTo(configuration);
                AutoDensityConfig.updateDensityOverrideConfiguration(getContext(), configuration);
            }
            final int i5 = configuration.screenWidthDp;
            final int i6 = configuration.screenHeightDp;
            ConfigurationChangedCallback configurationChangedCallback = this.mCallback;
            if (configurationChangedCallback != null) {
                configurationChangedCallback.onConfigurationChanged(getResources().getConfiguration(), i, i2, i3, i4);
            }
            post(new Runnable() { // from class: miuix.appcompat.internal.widget.DialogRootView.2
                @Override // java.lang.Runnable
                public void run() {
                    Configuration configuration2 = DialogRootView.this.getResources().getConfiguration();
                    if (autoDensityContextWrapperFindAutoDensityContextWrapper != null) {
                        configuration2 = AutoDensityConfig.updateDensityOverrideConfiguration(DialogRootView.this.getContext(), configuration2);
                    }
                    Configuration configuration3 = configuration2;
                    if (configuration3.screenWidthDp == i5 && configuration3.screenHeightDp == i6) {
                        return;
                    }
                    if (autoDensityContextWrapperFindAutoDensityContextWrapper != null) {
                        EnvStateManager.markWindowInfoDirty(DialogRootView.this.getContext());
                    }
                    if (DialogRootView.this.mCallback != null) {
                        DialogRootView.this.mCallback.onConfigurationChanged(configuration3, i, i2, i3, i4);
                    }
                }
            });
        }
    }

    public void setConfigurationChangedCallback(ConfigurationChangedCallback configurationChangedCallback) {
        this.mCallback = configurationChangedCallback;
    }
}
