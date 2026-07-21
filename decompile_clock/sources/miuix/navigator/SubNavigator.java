package miuix.navigator;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import miuix.appcompat.app.DelegateFragmentFactory;
import miuix.appcompat.app.FragmentDelegate;
import miuix.navigator.adapter.LabelAdapter;
import miuix.navigator.navigation.NavigationBarView;
import miuix.navigator.navigatorinfo.NavigatorInfo;
import miuix.navigator.navigatorinfo.NavigatorInfoProvider;

/* JADX INFO: loaded from: classes3.dex */
abstract class SubNavigator extends Navigator {
    private final NavigatorFragmentController mFragmentController = new NavigatorFragmentController(this);
    private final NavigatorImpl mNavigatorImpl;

    void addNavigatorSwitch(View view, ViewAfterNavigatorSwitchPresenter viewAfterNavigatorSwitchPresenter) {
    }

    @Override // miuix.navigator.Navigator
    public abstract String getTag();

    @Override // miuix.navigator.Navigator
    public abstract boolean isFocused();

    public abstract boolean isUserFocused();

    @Override // miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onBottomNavigationVisibilityChanged(boolean z, int i) {
    }

    @Override // miuix.navigator.Navigator
    public void onConfigurationChanged(Configuration configuration) {
    }

    @Override // miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onContentVisibilityChanged(int i) {
    }

    @Override // miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onNavigationVisibilityChanged(int i) {
    }

    @Override // miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onNavigatorModeChanged(Navigator.Mode mode, Navigator.Mode mode2) {
    }

    @Override // miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onSecondaryContentVisibilityChanged(int i) {
    }

    void removeNavigatorSwitch(View view) {
    }

    @Override // miuix.navigator.Navigator
    public abstract void requestFocus(boolean z);

    public abstract boolean requestUserFocus(boolean z);

    SubNavigator(NavigatorImpl navigatorImpl) {
        this.mNavigatorImpl = navigatorImpl;
    }

    NavigatorFragmentController getFragmentController() {
        return this.mFragmentController;
    }

    @Override // miuix.navigator.Navigator
    public NavigatorImpl getBaseNavigator() {
        return this.mNavigatorImpl;
    }

    @Override // miuix.navigator.Navigator
    public void onCreate(Bundle bundle) {
        getFragmentController().setOnAttachListener(new NavigatorFragmentController.OnAttachListener() { // from class: miuix.navigator.SubNavigator$$ExternalSyntheticLambda0
            @Override // miuix.navigator.NavigatorFragmentController.OnAttachListener
            public final void onAttach(NavigatorFragmentController navigatorFragmentController) {
                this.f$0.onControllerAttach(navigatorFragmentController);
            }
        });
        getFragmentController().performRestore(bundle);
        getFragmentController().attachHost();
    }

    void onControllerAttach(NavigatorFragmentController navigatorFragmentController) {
        navigatorFragmentController.getFragmentManager().setFragmentFactory(new DelegateFragmentFactory() { // from class: miuix.navigator.SubNavigator.1
            @Override // miuix.appcompat.app.DelegateFragmentFactory
            public FragmentDelegate createFragmentDelegate(Fragment fragment) {
                SubNavigator subNavigator = SubNavigator.this;
                if (subNavigator instanceof ContentSubNavigator) {
                    return new NavContentFragmentDelegate(SubNavigator.this, fragment);
                }
                if (subNavigator instanceof SecondaryContentSubNavigator) {
                    return new NavSecondaryContentFragmentDelegate(SubNavigator.this, fragment);
                }
                if (subNavigator instanceof NavigationSubNavigator) {
                    return new NavigationFragmentDelegate(SubNavigator.this, fragment);
                }
                return new NavigatorFragmentDelegate(SubNavigator.this, fragment);
            }
        });
    }

    @Override // miuix.navigator.Navigator
    public void onSaveInstanceState(Bundle bundle) {
        getFragmentController().performSaveInstanceState(bundle);
    }

    @Override // miuix.navigator.Navigator
    public Navigator getByTag(String str) {
        return this.mNavigatorImpl.getByTag(str);
    }

    @Override // miuix.navigator.Navigator
    public FragmentManager getFragmentManager() {
        return getFragmentController().getFragmentManager();
    }

    @Override // miuix.navigator.Navigator
    public void setStrategy(NavigatorStrategy navigatorStrategy) {
        this.mNavigatorImpl.setStrategy(navigatorStrategy);
    }

    @Override // miuix.navigator.Navigator
    public NavigatorStrategy getStrategy() {
        return this.mNavigatorImpl.getStrategy();
    }

    @Override // miuix.navigator.Navigator
    public void setNavigationMode(Navigator.Mode mode) {
        this.mNavigatorImpl.setNavigationMode(mode);
    }

    @Override // miuix.navigator.Navigator
    public Navigator.Mode getNavigationMode() {
        return this.mNavigatorImpl.getNavigationMode();
    }

    @Override // miuix.navigator.Navigator
    public void setNavigationWidthConfig(int i, int i2, int i3) {
        this.mNavigatorImpl.setNavigationWidthConfig(i, i2, i3);
    }

    @Override // miuix.navigator.Navigator
    public void navigate(NavigatorInfo navigatorInfo) {
        this.mNavigatorImpl.navigate(navigatorInfo, this);
    }

    @Override // miuix.navigator.Navigator
    public NavigatorInfo getCurrentInfo() {
        return this.mNavigatorImpl.getCurrentInfo();
    }

    @Override // miuix.navigator.Navigator
    public void setNavigationMenu(int i) {
        this.mNavigatorImpl.setNavigationMenu(i);
    }

    @Override // miuix.navigator.Navigator
    public int getNavigationMenu() {
        return this.mNavigatorImpl.getNavigationMenu();
    }

    @Override // miuix.navigator.Navigator
    public Navigator.Label newLabel(int i) {
        return this.mNavigatorImpl.newLabel(i);
    }

    @Override // miuix.navigator.Navigator
    public void addLabel(Navigator.Label label) {
        this.mNavigatorImpl.addLabel(label);
    }

    @Override // miuix.navigator.Navigator
    public void addLabel(Navigator.Label label, int i) {
        this.mNavigatorImpl.addLabel(label, i);
    }

    @Override // miuix.navigator.Navigator
    public void removeLabel(int i) {
        this.mNavigatorImpl.removeLabel(i);
    }

    @Override // miuix.navigator.Navigator
    public void removeLabel(Navigator.Label label) {
        this.mNavigatorImpl.removeLabel(label);
    }

    @Override // miuix.navigator.Navigator
    public LabelAdapter getLabelAdapter() {
        return this.mNavigatorImpl.getLabelAdapter();
    }

    @Override // miuix.navigator.Navigator
    public void setLabelAdapter(LabelAdapter labelAdapter) {
        this.mNavigatorImpl.setLabelAdapter(labelAdapter);
    }

    @Override // miuix.navigator.Navigator
    public Navigator.Category newCategory(int i) {
        return this.mNavigatorImpl.newCategory(i);
    }

    @Override // miuix.navigator.Navigator
    public Navigator.Category newCategory(int i, int i2) {
        return this.mNavigatorImpl.newCategory(i, i2);
    }

    @Override // miuix.navigator.Navigator
    public void addCategory(Navigator.Category category) {
        this.mNavigatorImpl.addCategory(category);
    }

    @Override // miuix.navigator.Navigator
    public void addCategory(Navigator.Category category, int i) {
        this.mNavigatorImpl.addCategory(category, i);
    }

    @Override // miuix.navigator.Navigator
    public void removeCategory(int i) {
        this.mNavigatorImpl.removeCategory(i);
    }

    @Override // miuix.navigator.Navigator
    public void removeCategory(Navigator.Category category) {
        this.mNavigatorImpl.removeCategory(category);
    }

    @Override // miuix.navigator.Navigator
    public NavigationBarView getBottomTabView() {
        return this.mNavigatorImpl.getBottomTabView();
    }

    @Override // miuix.navigator.Navigator
    public void setBottomTabMenu(int i, NavigatorInfoProvider navigatorInfoProvider) {
        this.mNavigatorImpl.setBottomTabMenu(i, navigatorInfoProvider);
    }

    @Override // miuix.navigator.Navigator
    public Menu getBottomTabMenu() {
        return this.mNavigatorImpl.getBottomTabMenu();
    }

    @Override // miuix.navigator.Navigator
    public void setBottomTabStyle(int i) {
        this.mNavigatorImpl.setBottomTabStyle(i);
    }

    @Override // miuix.navigator.Navigator
    public BottomTab newTab() {
        return this.mNavigatorImpl.newTab();
    }

    @Override // miuix.navigator.Navigator
    public void addTab(BottomTab bottomTab) {
        this.mNavigatorImpl.addTab(bottomTab);
    }

    @Override // miuix.navigator.Navigator
    public void addTab(BottomTab bottomTab, int i) {
        this.mNavigatorImpl.addTab(bottomTab, i);
    }

    @Override // miuix.navigator.Navigator
    public void selectTab(int i) {
        this.mNavigatorImpl.selectTab(i);
    }

    @Override // miuix.navigator.Navigator
    public void setTabSelectListener(BottomNavigation.OnItemSelectedListener onItemSelectedListener) {
        this.mNavigatorImpl.setTabSelectListener(onItemSelectedListener);
    }

    @Override // miuix.navigator.Navigator
    public void applyBottomNavigationBlur(boolean z) {
        this.mNavigatorImpl.applyBottomNavigationBlur(z);
    }

    @Override // miuix.navigator.Navigator
    public void setBottomNavigationBackgroundVisible(boolean z) {
        this.mNavigatorImpl.setBottomNavigationBackgroundVisible(z);
    }

    @Override // miuix.navigator.Navigator
    public void openNavigation() {
        openNavigation(true);
    }

    @Override // miuix.navigator.Navigator
    public void openNavigation(boolean z) {
        this.mNavigatorImpl.openNavigation(z);
    }

    @Override // miuix.navigator.Navigator
    public void closeNavigation() {
        closeNavigation(true);
    }

    @Override // miuix.navigator.Navigator
    public void closeNavigation(boolean z) {
        this.mNavigatorImpl.closeNavigation(z);
    }

    @Override // miuix.navigator.Navigator
    public void toggleNavigation() {
        toggleNavigation(true);
    }

    @Override // miuix.navigator.Navigator
    public void toggleNavigation(boolean z) {
        this.mNavigatorImpl.toggleNavigation(z);
    }

    @Override // miuix.navigator.Navigator
    public boolean isNavigationOpen() {
        return this.mNavigatorImpl.isNavigationOpen();
    }

    @Override // miuix.navigator.Navigator
    public boolean isNavigationInitOpen() {
        return this.mNavigatorImpl.isNavigationInitOpen();
    }

    @Override // miuix.navigator.Navigator
    public void openContent() {
        openContent(true);
    }

    @Override // miuix.navigator.Navigator
    public void openContent(boolean z) {
        this.mNavigatorImpl.openContent(z);
    }

    @Override // miuix.navigator.Navigator
    public void closeContent() {
        closeContent(true);
    }

    @Override // miuix.navigator.Navigator
    public void closeContent(boolean z) {
        this.mNavigatorImpl.closeContent(z);
    }

    @Override // miuix.navigator.Navigator
    public void toggleContent() {
        toggleContent(true);
    }

    @Override // miuix.navigator.Navigator
    public void toggleContent(boolean z) {
        this.mNavigatorImpl.toggleContent(z);
    }

    @Override // miuix.navigator.Navigator
    public boolean isContentOpen() {
        return this.mNavigatorImpl.isContentOpen();
    }

    @Override // miuix.navigator.Navigator
    public boolean isNavigationOverlay() {
        return this.mNavigatorImpl.isNavigationOverlay();
    }

    @Override // miuix.navigator.Navigator
    public void immerseSecondaryContent(boolean z) {
        this.mNavigatorImpl.immerseSecondaryContent(z);
    }

    @Override // miuix.navigator.Navigator
    public OnBackPressedCallback getOnBackPressedCallback() {
        return this.mNavigatorImpl.getOnBackPressedCallback();
    }

    @Override // miuix.navigator.Navigator
    public boolean canPopSecondaryContent() {
        return this.mNavigatorImpl.canPopSecondaryContent();
    }

    @Override // miuix.navigator.Navigator
    public void addNavigatorFragmentListener(NavigatorFragmentListener navigatorFragmentListener) {
        this.mNavigatorImpl.addNavigatorFragmentListener(navigatorFragmentListener);
    }

    @Override // miuix.navigator.Navigator
    public void removeNavigatorFragmentListener(NavigatorFragmentListener navigatorFragmentListener) {
        this.mNavigatorImpl.removeNavigatorFragmentListener(navigatorFragmentListener);
    }

    @Override // miuix.navigator.Navigator
    public int getNavigationVisibility() {
        return this.mNavigatorImpl.getNavigationVisibility();
    }

    @Override // miuix.navigator.Navigator
    public int getContentVisibility() {
        return this.mNavigatorImpl.getContentVisibility();
    }

    @Override // miuix.navigator.Navigator
    public int getSecondaryContentVisibility() {
        return this.mNavigatorImpl.getSecondaryContentVisibility();
    }

    @Override // miuix.navigator.Navigator
    public void addNavigatorStateListener(Navigator.NavigatorStateListener navigatorStateListener) {
        this.mNavigatorImpl.addNavigatorStateListener(navigatorStateListener);
    }

    @Override // miuix.navigator.Navigator
    public void removeNavigatorStateListener(Navigator.NavigatorStateListener navigatorStateListener) {
        this.mNavigatorImpl.removeNavigatorStateListener(navigatorStateListener);
    }

    @Override // miuix.navigator.Navigator
    public void setCrossBackground(int i) {
        this.mNavigatorImpl.setCrossBackground(i);
    }

    @Override // miuix.navigator.Navigator
    public void setCrossBackground(View view) {
        this.mNavigatorImpl.setCrossBackground(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHostState(int i) {
        getFragmentController().setHostState(i);
    }

    @Override // miuix.navigator.Navigator
    public void setSplitAnimationStyle(int i) {
        this.mNavigatorImpl.setSplitAnimationStyle(i);
    }

    @Override // miuix.navigator.Navigator
    public void setContentExpandedPaddingWithDp(int i) {
        this.mNavigatorImpl.setContentExpandedPaddingWithDp(i);
    }

    @Override // miuix.navigator.Navigator
    public void setContentExpandedMaxWidthWithDp(int i) {
        this.mNavigatorImpl.setContentExpandedMaxWidthWithDp(i);
    }

    @Override // miuix.navigator.Navigator
    public void setSplitAnimationMaskBlurRadiusWithPx(int i) {
        this.mNavigatorImpl.setSplitAnimationMaskBlurRadiusWithPx(i);
    }
}
