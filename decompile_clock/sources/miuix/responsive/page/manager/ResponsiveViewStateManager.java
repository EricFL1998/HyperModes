package miuix.responsive.page.manager;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import miuix.responsive.interfaces.IResponsive;
import miuix.responsive.interfaces.IViewResponsive;
import miuix.responsive.map.ResponsiveState;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes3.dex */
public class ResponsiveViewStateManager extends BaseStateManager {
    private boolean mHostActivityIsIResponsive;
    private IViewResponsive mIViewResponsive;
    private View mView;

    public ResponsiveViewStateManager(View view, IViewResponsive iViewResponsive) {
        this.mView = view;
        this.mIViewResponsive = iViewResponsive;
        this.mHostActivityIsIResponsive = IResponsive.class.isAssignableFrom(view.getContext().getClass());
    }

    @Override // miuix.responsive.page.manager.BaseStateManager
    public void beforeConfigurationChanged(Configuration configuration) {
        if (!this.mHostActivityIsIResponsive && isSupportResponsive()) {
            this.mOldState.setTo(this.mState);
            ResponsiveState responsiveStateComputeResponsiveState = computeResponsiveState();
            onBeforeConfigurationChanged(configuration);
            this.mState = responsiveStateComputeResponsiveState;
        }
    }

    @Override // miuix.responsive.page.manager.BaseStateManager
    protected Context getContext() {
        return this.mView.getContext();
    }

    @Override // miuix.responsive.page.manager.BaseStateManager
    public void afterConfigurationChanged(Configuration configuration) {
        if (!this.mHostActivityIsIResponsive && isSupportResponsive()) {
            onAfterConfigurationChanged(configuration);
            notifyResponseChange(configuration, this.mState, isStateEquals(this.mState, this.mOldState));
        }
    }

    protected void notifyResponseChange(Configuration configuration, ResponsiveState responsiveState, boolean z) {
        ScreenSpec screenSpec = new ScreenSpec();
        if (responsiveState != null) {
            responsiveState.toScreenSpec(screenSpec);
        }
        IViewResponsive iViewResponsive = this.mIViewResponsive;
        if (iViewResponsive != null) {
            iViewResponsive.onResponsiveLayout(configuration, screenSpec, z);
        }
    }
}
