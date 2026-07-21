package miuix.navigator;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.android.deskclock.R2;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.RomUtils;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.LiteUtils;
import miuix.theme.token.hypermaterial.Blur;
import miuix.theme.token.hypermaterial.Mask;
import miuix.view.BlurableWidget;
import miuix.view.EditActionMode;
import miuix.view.MiuiBlurUiHelper;

/* JADX INFO: loaded from: classes3.dex */
class NavigationFragmentDelegate extends NavigatorFragmentDelegate implements BlurableWidget {
    private Drawable mBackgroundInBlur;
    private Drawable mBackgroundWithoutBlur;
    private MiuiBlurUiHelper mBlurUiHelper;
    private MaterialDayNightConfig mMaterial;

    public NavigationFragmentDelegate(SubNavigator subNavigator, Fragment fragment) {
        super(subNavigator, fragment);
        this.mBackgroundInBlur = null;
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setExtraThemeRes(AttributeResolver.resolve(getActivity(), R.attr.navigationFragmentStyle));
    }

    @Override // miuix.navigator.NavigatorFragmentDelegate, miuix.appcompat.app.FragmentDelegate
    public void onViewCreated(View view, Bundle bundle) {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            super.onViewCreated(view, bundle);
            return;
        }
        actionBar.setDisplayOptions(8192, R2.drawable.miuix_appcompat_btn_inline_add_light);
        super.onViewCreated(view, bundle);
        this.mBackgroundWithoutBlur = view.getBackground();
        if (HyperMaterialUtils.isEnable()) {
            this.mMaterial = MaterialDayNightConfig.create(RomUtils.getHyperOsVersion() > 2 ? Mask.Pured_Regular : Blur.ExtraHeavy);
            this.mBlurUiHelper = new MiuiBlurUiHelper(getActivity(), getView(), false, false, new MiuiBlurUiHelper.BlurStateCallback() { // from class: miuix.navigator.NavigationFragmentDelegate.1
                final boolean isDarkThemeOverlay;

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurEnableStateChanged(boolean z) {
                }

                {
                    this.isDarkThemeOverlay = MiuixUIUtils.isDarkThemeOverlay(NavigationFragmentDelegate.this.getActivity(), miuix.appcompat.R.color.miuix_default_color_on_surface_light);
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public boolean isLightTheme() {
                    Integer colorFromDrawable;
                    if (NavigationFragmentDelegate.this.mBackgroundWithoutBlur == null || (colorFromDrawable = MiuixUIUtils.getColorFromDrawable(NavigationFragmentDelegate.this.mBackgroundWithoutBlur)) == null) {
                        return !this.isDarkThemeOverlay && AttributeResolver.resolveBoolean(NavigationFragmentDelegate.this.getThemedContext(), miuix.appcompat.R.attr.isLightTheme, true);
                    }
                    return MiuixUIUtils.isLightColor(colorFromDrawable.intValue()) && !this.isDarkThemeOverlay;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public int getBackgroundColor() {
                    if (this.isDarkThemeOverlay) {
                        return AttributeResolver.resolveColor(NavigationFragmentDelegate.this.getActivity(), miuix.theme.R.attr.colorSurface, 0);
                    }
                    return 0;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public MaterialConfig.BlurConfig getBlurConfig(boolean z) {
                    MaterialDayNightConfig materialDayNightConfig = NavigationFragmentDelegate.this.mMaterial;
                    if (materialDayNightConfig != null) {
                        return materialDayNightConfig.getBlurConfig(z);
                    }
                    return null;
                }

                @Override // miuix.view.MiuiBlurUiHelper.BlurStateCallback
                public void onBlurApplyStateChanged(boolean z) {
                    if (NavigationFragmentDelegate.this.mBlurUiHelper == null) {
                        return;
                    }
                    View viewApplyBlur = NavigationFragmentDelegate.this.mBlurUiHelper.getViewApplyBlur();
                    if (z) {
                        viewApplyBlur.setBackground(NavigationFragmentDelegate.this.mBackgroundInBlur);
                    } else {
                        viewApplyBlur.setBackground(NavigationFragmentDelegate.this.mBackgroundWithoutBlur);
                    }
                    ActionBar actionBar2 = NavigationFragmentDelegate.this.getActionBar();
                    if (actionBar2 != null) {
                        ((ActionBarImpl) actionBar2).updateBackgroundViewBlurState(z);
                    }
                }
            });
            setSupportBlur(!LiteUtils.isCommonLiteStrategy());
        } else {
            this.mBlurUiHelper = null;
        }
        if ((AttributeResolver.resolveInt(getThemedContext(), miuix.appcompat.R.attr.bgBlurOptions, 0) & 4) != 0) {
            setEnableBlur(true);
        }
        updateBgBlur();
    }

    @Override // miuix.navigator.NavigatorFragmentDelegate, miuix.appcompat.app.FragmentDelegate, miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper != null) {
            miuiBlurUiHelper.onConfigChanged();
            updateBgBlur();
        }
    }

    @Override // miuix.navigator.NavigatorFragmentDelegate
    void updateViewAfterNavigatorSwitchPresenter(ActionBar actionBar) {
        super.updateViewAfterNavigatorSwitchPresenter(actionBar);
        SubNavigator navigator = getNavigator();
        if (actionBar == null || navigator == null) {
            return;
        }
        navigator.addNavigatorSwitch(getView().findViewById(R.id.navigator_switch), null);
    }

    private void updateBgBlur() {
        SubNavigator navigator = getNavigator();
        Navigator.Mode navigationMode = navigator.getNavigationMode();
        if (navigationMode == Navigator.Mode.NC || navigationMode == Navigator.Mode.NLC) {
            applyBlur(navigator.getBaseNavigator().isNavigationOverlay());
        }
    }

    @Override // miuix.view.HyperMaterialWidget
    public void setMaterial(MaterialDayNightConfig materialDayNightConfig) {
        boolean z = this.mMaterial == null && materialDayNightConfig != null;
        if (materialDayNightConfig == null) {
            this.mMaterial = null;
            applyBlur(false);
            return;
        }
        this.mMaterial = materialDayNightConfig;
        if (this.mBlurUiHelper != null) {
            if (!isApplyBlur() && z) {
                applyBlur(true);
            }
            this.mBlurUiHelper.onConfigChanged();
        }
    }

    @Override // miuix.view.HyperMaterialWidget
    public MaterialDayNightConfig getMaterial() {
        return this.mMaterial;
    }

    @Override // miuix.view.BlurableWidget
    public void setSupportBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.setSupportBlur(z);
        if (z) {
            this.mBackgroundInBlur = new ColorDrawable(0);
        }
    }

    @Override // miuix.view.BlurableWidget
    public boolean isSupportBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isSupportBlur();
    }

    @Override // miuix.view.BlurableWidget
    public void setEnableBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.setEnableBlur(z);
    }

    @Override // miuix.view.BlurableWidget
    public boolean isEnableBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isEnableBlur();
    }

    @Override // miuix.view.BlurableWidget
    public void applyBlur(boolean z) {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return;
        }
        miuiBlurUiHelper.applyBlur(z);
    }

    @Override // miuix.view.BlurableWidget
    public boolean isApplyBlur() {
        MiuiBlurUiHelper miuiBlurUiHelper = this.mBlurUiHelper;
        if (miuiBlurUiHelper == null) {
            return false;
        }
        return miuiBlurUiHelper.isApplyBlur();
    }

    @Override // miuix.appcompat.app.ActionBarDelegateImpl, miuix.appcompat.app.ActionBarDelegate
    public void onActionModeStarted(ActionMode actionMode) {
        super.onActionModeStarted(actionMode);
        Fragment fragmentFindFragmentByTag = getNavigator().getBaseNavigator().mContentSub.getFragmentManager().findFragmentByTag(Navigator.TAG_CONTENT);
        if (fragmentFindFragmentByTag instanceof miuix.appcompat.app.Fragment) {
            miuix.appcompat.app.Fragment fragment = (miuix.appcompat.app.Fragment) fragmentFindFragmentByTag;
            if (fragment.getDelegate().getActionMode() instanceof EditActionMode) {
                fragment.getDelegate().getActionMode().finish();
            }
        }
        Fragment fragmentFindFragmentByTag2 = getNavigator().getBaseNavigator().mSecondaryContentSub.getFragmentManager().findFragmentByTag(Navigator.TAG_SECONDARY_CONTENT);
        if (fragmentFindFragmentByTag2 instanceof miuix.appcompat.app.Fragment) {
            miuix.appcompat.app.Fragment fragment2 = (miuix.appcompat.app.Fragment) fragmentFindFragmentByTag2;
            if (fragment2.getDelegate().getActionMode() instanceof EditActionMode) {
                fragment2.getDelegate().getActionMode().finish();
            }
        }
    }
}
