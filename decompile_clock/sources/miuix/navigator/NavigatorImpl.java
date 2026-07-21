package miuix.navigator;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import androidx.activity.OnBackPressedCallback;
import androidx.collection.ArrayMap;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import miuix.appcompat.app.IFragment;
import miuix.appcompat.app.ShortcutsCallback;
import miuix.navigator.adapter.LabelAdapter;
import miuix.navigator.adapter.NavigationAdapter;
import miuix.navigator.navigation.NavigationBarView;
import miuix.navigator.navigatorinfo.NavigatorInfo;
import miuix.navigator.navigatorinfo.NavigatorInfoManager;
import miuix.navigator.navigatorinfo.NavigatorInfoProvider;
import miuix.responsive.interfaces.IResponsive;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes3.dex */
public class NavigatorImpl extends Navigator implements NavigatorFragmentListener, Navigator.NavigatorStateListener {
    static final boolean DEBUG = false;
    private static final String TAG_MIUIX_NAVIGATION_LAYOUT_STATE = "miuix:navigationLayoutState";
    private static final String TAG_MIUIX_NAVIGATOR_INFO_STATE = "miuix:navigatorInfoState";
    private int mContentVisibility;
    private View mCrossBackground;
    private int mCrossBackgroundRes;
    private boolean mEditing;
    private final NavHostFragment mNavHostFragment;
    private int mNavigationMenuResId;
    private View mNavigationSwitch;
    private MiuixNavigationLayout mNavigationView;
    private int mNavigationVisibility;
    private final NavigatorInfoManager mNavigatorInfoManager;
    private NavigatorStrategy mNavigatorStrategy;
    private MiuixNavigationLayout.WidthConfig mNavigatorWidthConfig;
    private Bundle mSavedState;
    private int mSecondaryContentVisibility;
    private Boolean mSettingNavigationInitOpen;
    private Navigator.Mode mMode = Navigator.Mode.C;
    private final List<NavigatorFragmentListener> mNavigatorFragmentListeners = new CopyOnWriteArrayList();
    private final List<Navigator.NavigatorStateListener> mNavigatorStateListeners = new CopyOnWriteArrayList();

    @Deprecated
    private ActionMode.Callback mContentActionModeWrapper = null;
    private final Map<View, ViewAfterNavigatorSwitchPresenter> mContentSwitch = new ArrayMap();
    private final Map<View, ViewAfterNavigatorSwitchPresenter> mSecondaryContentSwitch = new ArrayMap();
    private final OnBackPressedCallback mCallback = new OnBackPressedCallback(false) { // from class: miuix.navigator.NavigatorImpl.2
        @Override // androidx.activity.OnBackPressedCallback
        public void handleOnBackPressed() {
            if (NavigatorImpl.this.canCloseNavigation()) {
                NavigatorImpl.this.closeNavigation();
                return;
            }
            FragmentManager fragmentManager = NavigatorImpl.this.mSecondaryContentSub.getFragmentManager();
            if (fragmentManager.isStateSaved()) {
                return;
            }
            fragmentManager.popBackStackImmediate();
        }
    };
    final NavigationSubNavigator mNavigationSub = new NavigationSubNavigator(this);
    final ContentSubNavigator mContentSub = new ContentSubNavigator(this);
    final SecondaryContentSubNavigator mSecondaryContentSub = new SecondaryContentSubNavigator(this);

    @Override // miuix.navigator.Navigator
    public void applyBottomNavigationBlur(boolean z) {
    }

    @Override // miuix.navigator.Navigator
    public Menu getBottomTabMenu() {
        return null;
    }

    @Override // miuix.navigator.Navigator
    public NavigationBarView getBottomTabView() {
        return null;
    }

    public void initExtraViews() {
    }

    @Override // miuix.navigator.Navigator
    public boolean isFocused() {
        return false;
    }

    boolean isSecondaryContentUserFocused() {
        return false;
    }

    @Override // miuix.navigator.Navigator
    public void requestFocus(boolean z) {
    }

    @Override // miuix.navigator.Navigator
    public void setBottomNavigationBackgroundVisible(boolean z) {
    }

    @Override // miuix.navigator.Navigator
    public void setBottomTabMenu(int i, NavigatorInfoProvider navigatorInfoProvider) {
    }

    @Override // miuix.navigator.Navigator
    public void setBottomTabStyle(int i) {
    }

    @Override // miuix.navigator.Navigator
    public void setTabSelectListener(BottomNavigation.OnItemSelectedListener onItemSelectedListener) {
    }

    void userFocusSecondaryContent(boolean z) {
    }

    public NavigatorImpl(Bundle bundle, NavHostFragment navHostFragment) {
        this.mSettingNavigationInitOpen = null;
        this.mNavHostFragment = navHostFragment;
        if (bundle != null) {
            if (bundle.containsKey(Navigator.KEY_MIUIX_NAVIGATOR_STRATEGY)) {
                if (Build.VERSION.SDK_INT >= 33) {
                    this.mNavigatorStrategy = (NavigatorStrategy) bundle.getParcelable(Navigator.KEY_MIUIX_NAVIGATOR_STRATEGY, NavigatorStrategy.class);
                } else {
                    this.mNavigatorStrategy = (NavigatorStrategy) bundle.getParcelable(Navigator.KEY_MIUIX_NAVIGATOR_STRATEGY);
                }
            }
            if (bundle.containsKey(Navigator.KEY_MIUIX_NAVIGATION_INIT_OPEN)) {
                this.mSettingNavigationInitOpen = Boolean.valueOf(bundle.getBoolean(Navigator.KEY_MIUIX_NAVIGATION_INIT_OPEN));
            }
        }
        if (this.mNavigatorStrategy == null) {
            this.mNavigatorStrategy = new NavigatorStrategy();
        }
        this.mNavigatorInfoManager = new NavigatorInfoManager(this);
    }

    protected final MiuixNavigationLayout getNavigationView() {
        return this.mNavigationView;
    }

    private void forAllSubNavigator(Consumer<SubNavigator> consumer) {
        consumer.accept(this.mNavigationSub);
        consumer.accept(this.mContentSub);
        consumer.accept(this.mSecondaryContentSub);
    }

    @Override // miuix.navigator.Navigator
    public void onCreate(Bundle bundle) {
        NavigatorStrategy navigatorStrategy;
        if (bundle == null) {
            forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda3
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((SubNavigator) obj).onCreate(null);
                }
            });
            return;
        }
        final Bundle bundle2 = bundle.getBundle(Navigator.KEY_MIUIX_NAVIGATOR_STATE);
        forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda4
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                Bundle bundle3 = bundle2;
                SubNavigator subNavigator = (SubNavigator) obj;
                subNavigator.onCreate(bundle3 == null ? null : bundle3.getBundle(subNavigator.getTag()));
            }
        });
        if (bundle.containsKey(Navigator.KEY_MIUIX_NAVIGATOR_STRATEGY)) {
            if (Build.VERSION.SDK_INT >= 33) {
                navigatorStrategy = (NavigatorStrategy) bundle.getParcelable(Navigator.KEY_MIUIX_NAVIGATOR_STRATEGY, NavigatorStrategy.class);
            } else {
                navigatorStrategy = (NavigatorStrategy) bundle.getParcelable(Navigator.KEY_MIUIX_NAVIGATOR_STRATEGY);
            }
            setStrategy(navigatorStrategy);
            if (!navigatorStrategy.isIgnoreSaveInstance() && bundle.containsKey(Navigator.KEY_MIUIX_NAVIGATOR_MODE)) {
                setNavigationMode(Navigator.Mode.valueOf(bundle.getString(Navigator.KEY_MIUIX_NAVIGATOR_MODE)));
            }
        }
        if (bundle.containsKey(TAG_MIUIX_NAVIGATION_LAYOUT_STATE)) {
            this.mSavedState = bundle.getBundle(TAG_MIUIX_NAVIGATION_LAYOUT_STATE);
        }
        if (bundle.containsKey(TAG_MIUIX_NAVIGATOR_INFO_STATE)) {
            this.mNavigatorInfoManager.onRestoreState(bundle.getBundle(TAG_MIUIX_NAVIGATOR_INFO_STATE));
        }
    }

    @Override // miuix.navigator.Navigator
    public void onSaveInstanceState(Bundle bundle) {
        final Bundle bundle2 = new Bundle();
        forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda6
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                NavigatorImpl.lambda$onSaveInstanceState$2(bundle2, (SubNavigator) obj);
            }
        });
        bundle.putBundle(Navigator.KEY_MIUIX_NAVIGATOR_STATE, bundle2);
        bundle.putString(Navigator.KEY_MIUIX_NAVIGATOR_MODE, getNavigationMode().toString());
        bundle.putParcelable(Navigator.KEY_MIUIX_NAVIGATOR_STRATEGY, getStrategy());
        Bundle bundle3 = new Bundle();
        this.mNavigationView.onSaveState(bundle3);
        bundle.putBundle(TAG_MIUIX_NAVIGATION_LAYOUT_STATE, bundle3);
        Bundle bundle4 = new Bundle();
        this.mNavigatorInfoManager.onSaveState(bundle4);
        bundle.putBundle(TAG_MIUIX_NAVIGATOR_INFO_STATE, bundle4);
    }

    static /* synthetic */ void lambda$onSaveInstanceState$2(Bundle bundle, SubNavigator subNavigator) {
        Bundle bundle2 = new Bundle();
        subNavigator.onSaveInstanceState(bundle2);
        bundle.putBundle(subNavigator.getTag(), bundle2);
    }

    @Override // miuix.navigator.Navigator
    public void onConfigurationChanged(Configuration configuration) {
        this.mNavigatorInfoManager.onConfigurationChanged(configuration);
    }

    @Override // miuix.navigator.Navigator
    public String getTag() {
        return Navigator.TAG_ROOT;
    }

    @Override // miuix.navigator.Navigator
    public Navigator getByTag(String str) {
        if (Navigator.TAG_ROOT.equals(str)) {
            return this;
        }
        if ("miuix.navigation".equals(str)) {
            return this.mNavigationSub;
        }
        if (Navigator.TAG_CONTENT.equals(str)) {
            return this.mContentSub;
        }
        return Navigator.TAG_SECONDARY_CONTENT.equals(str) ? this.mSecondaryContentSub : this;
    }

    @Override // miuix.navigator.Navigator
    public FragmentManager getFragmentManager() {
        return this.mNavHostFragment.getChildFragmentManager();
    }

    @Override // miuix.navigator.Navigator
    public void setStrategy(NavigatorStrategy navigatorStrategy) {
        this.mNavigatorStrategy = navigatorStrategy;
        updateNavigationMode();
    }

    @Override // miuix.navigator.Navigator
    public NavigatorStrategy getStrategy() {
        return this.mNavigatorStrategy;
    }

    void updateNavigationMode() {
        Navigator.Mode currentMode = this.mNavigatorStrategy.getCurrentMode(this.mNavHostFragment.getResponsiveState(), this.mNavHostFragment.getDeviceType());
        if (getNavigationMode() != currentMode) {
            setInnerNavigationMode(currentMode);
        }
    }

    void dispatchResponsiveLayout(int i, Configuration configuration, ScreenSpec screenSpec, boolean z) {
        updateNavigationMode();
        dispatchResponsiveLayout(this.mContentSub.getFragmentManager(), configuration, screenSpec, z);
        dispatchResponsiveLayout(this.mSecondaryContentSub.getFragmentManager(), configuration, screenSpec, z);
        dispatchResponsiveLayout(this.mNavigationSub.getFragmentManager(), configuration, screenSpec, z);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void dispatchResponsiveLayout(FragmentManager fragmentManager, Configuration configuration, ScreenSpec screenSpec, boolean z) {
        List<Fragment> fragments = fragmentManager.getFragments();
        int size = fragments.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Fragment fragment = fragments.get(i);
                if (fragment.isAdded() && (fragment instanceof IFragment) && (fragment instanceof IResponsive) && !((IFragment) fragment).isRegisterResponsive()) {
                    ((IResponsive) fragment).dispatchResponsiveLayout(configuration, screenSpec, z);
                }
            }
        }
    }

    void setInnerNavigationMode(Navigator.Mode mode) {
        Navigator.Mode mode2 = this.mMode;
        this.mMode = mode;
        onNavigatorModeChanged(mode2, mode);
    }

    @Override // miuix.navigator.Navigator
    public void setNavigationMode(Navigator.Mode mode) {
        if (getNavigationMode() != mode) {
            this.mNavigatorStrategy.updateStrategyOnNavigationModeChanged(this.mNavHostFragment.getResponsiveState(), this.mNavHostFragment.getDeviceType(), mode);
        }
        setInnerNavigationMode(mode);
    }

    @Override // miuix.navigator.Navigator
    public Navigator.Mode getNavigationMode() {
        return this.mMode;
    }

    @Override // miuix.navigator.Navigator
    public void setNavigationWidthConfig(int i, int i2, int i3) {
        MiuixNavigationLayout.WidthConfig widthConfig = new MiuixNavigationLayout.WidthConfig();
        widthConfig.contentWidthMode = i;
        widthConfig.contentMaxWidth = i2;
        widthConfig.secondaryContentMaxWidth = i3;
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.setWidthConfig(widthConfig);
            this.mNavigationView.requestLayout();
        } else {
            this.mNavigatorWidthConfig = widthConfig;
        }
    }

    @Override // miuix.navigator.Navigator
    public void navigate(NavigatorInfo navigatorInfo) {
        navigate(navigatorInfo, this);
    }

    void navigate(NavigatorInfo navigatorInfo, Navigator navigator) {
        if (navigatorInfo == null) {
            return;
        }
        this.mNavigatorInfoManager.navigate(navigatorInfo, navigator);
        if (navigatorInfo.shouldCloseOverlay() && this.mNavigationView != null && canCloseNavigation()) {
            closeNavigation();
        }
    }

    @Override // miuix.navigator.Navigator
    public NavigatorInfo getCurrentInfo() {
        return this.mNavigatorInfoManager.getSelectedInfo();
    }

    @Override // miuix.navigator.Navigator
    public void setNavigationMenu(int i) {
        if (this.mNavigationMenuResId != i) {
            this.mNavigationMenuResId = i;
            Fragment fragmentFindFragmentByTag = getByTag("miuix.navigation").getFragmentManager().findFragmentByTag("miuix.navigation");
            if (fragmentFindFragmentByTag instanceof miuix.appcompat.app.Fragment) {
                ((miuix.appcompat.app.Fragment) fragmentFindFragmentByTag).invalidateOptionsMenu();
            }
        }
    }

    @Override // miuix.navigator.Navigator
    public int getNavigationMenu() {
        return this.mNavigationMenuResId;
    }

    @Override // miuix.navigator.Navigator
    public Navigator.Label newLabel(int i) {
        return this.mNavigatorInfoManager.newLabel(i);
    }

    @Override // miuix.navigator.Navigator
    public void addLabel(Navigator.Label label) {
        this.mNavigatorInfoManager.addLabel(label);
    }

    @Override // miuix.navigator.Navigator
    public void addLabel(Navigator.Label label, int i) {
        this.mNavigatorInfoManager.addLabel(label, i);
    }

    @Override // miuix.navigator.Navigator
    public void removeLabel(int i) {
        this.mNavigatorInfoManager.removeLabel(i);
    }

    @Override // miuix.navigator.Navigator
    public void removeLabel(Navigator.Label label) {
        this.mNavigatorInfoManager.removeLabel(label);
    }

    public Navigator.Label findLabel(int i) {
        return this.mNavigatorInfoManager.findLabel(i);
    }

    public Navigator.Label findLabel(NavigatorInfo navigatorInfo) {
        return this.mNavigatorInfoManager.findLabel(navigatorInfo);
    }

    @Override // miuix.navigator.Navigator
    public LabelAdapter getLabelAdapter() {
        return this.mNavigatorInfoManager.getLabelAdapter();
    }

    @Override // miuix.navigator.Navigator
    public void setLabelAdapter(LabelAdapter labelAdapter) {
        this.mNavigatorInfoManager.setLabelAdapter(labelAdapter);
    }

    @Override // miuix.navigator.Navigator
    public Navigator.Category newCategory(int i) {
        return this.mNavigatorInfoManager.newCategory(i);
    }

    @Override // miuix.navigator.Navigator
    public Navigator.Category newCategory(int i, int i2) {
        return this.mNavigatorInfoManager.newCategory(i, i2);
    }

    @Override // miuix.navigator.Navigator
    public void addCategory(Navigator.Category category) {
        this.mNavigatorInfoManager.addCategory(category);
    }

    @Override // miuix.navigator.Navigator
    public void addCategory(Navigator.Category category, int i) {
        this.mNavigatorInfoManager.addCategory(category, i);
    }

    @Override // miuix.navigator.Navigator
    public void removeCategory(int i) {
        this.mNavigatorInfoManager.removeCategory(i);
    }

    @Override // miuix.navigator.Navigator
    public void removeCategory(Navigator.Category category) {
        this.mNavigatorInfoManager.removeCategory(category);
    }

    public Navigator.Category findCategory(int i) {
        return this.mNavigatorInfoManager.findCategory(i);
    }

    public void showBottomTab() {
        throw new UnsupportedOperationException("not implemented. Subclass must override this");
    }

    public void hideBottomTab(boolean z) {
        throw new UnsupportedOperationException("not implemented. Subclass must override this");
    }

    @Override // miuix.navigator.Navigator
    public BottomTab newTab() {
        return this.mNavigatorInfoManager.newTab();
    }

    @Override // miuix.navigator.Navigator
    public void addTab(BottomTab bottomTab) {
        this.mNavigatorInfoManager.addTab(bottomTab);
    }

    @Override // miuix.navigator.Navigator
    public void addTab(BottomTab bottomTab, int i) {
        this.mNavigatorInfoManager.addTab(bottomTab, i);
    }

    @Override // miuix.navigator.Navigator
    public void selectTab(int i) {
        MenuItem item;
        Menu bottomTabMenu = getBottomTabMenu();
        if (bottomTabMenu == null || (item = bottomTabMenu.getItem(i)) == null) {
            return;
        }
        item.setChecked(true);
    }

    public BottomTab findTab(int i) {
        return this.mNavigatorInfoManager.findTab(i);
    }

    public BottomTab findTab(NavigatorInfo navigatorInfo) {
        return this.mNavigatorInfoManager.findTab(navigatorInfo);
    }

    @Override // miuix.navigator.Navigator
    public boolean isNavigationOverlay() {
        return this.mNavigationView.isOverlay();
    }

    @Override // miuix.navigator.Navigator
    public boolean isNavigationInitOpen() {
        return this.mNavigationView.isNavigationInitOpen();
    }

    @Override // miuix.navigator.Navigator
    public void openNavigation() {
        openNavigation(true);
    }

    @Override // miuix.navigator.Navigator
    public void openNavigation(boolean z) {
        this.mNavigationView.openNavigation(z);
        updateOnBackPressedCallbackEnabled();
    }

    @Override // miuix.navigator.Navigator
    public void closeNavigation() {
        closeNavigation(true);
    }

    @Override // miuix.navigator.Navigator
    public void closeNavigation(boolean z) {
        this.mNavigationView.closeNavigation(z);
        updateOnBackPressedCallbackEnabled();
    }

    @Override // miuix.navigator.Navigator
    public void toggleNavigation() {
        toggleNavigation(true);
    }

    @Override // miuix.navigator.Navigator
    public void toggleNavigation(boolean z) {
        this.mNavigationView.toggleNavigation(z);
        updateOnBackPressedCallbackEnabled();
    }

    @Override // miuix.navigator.Navigator
    public boolean isNavigationOpen() {
        return this.mNavigationView.isNavigationOpen();
    }

    @Override // miuix.navigator.Navigator
    public void openContent() {
        openContent(true);
    }

    @Override // miuix.navigator.Navigator
    public void openContent(boolean z) {
        this.mNavigationView.openContent(z);
    }

    @Override // miuix.navigator.Navigator
    public void closeContent() {
        closeContent(true);
    }

    @Override // miuix.navigator.Navigator
    public void closeContent(boolean z) {
        this.mNavigationView.closeContent(z);
    }

    @Override // miuix.navigator.Navigator
    public void toggleContent() {
        toggleContent(true);
    }

    @Override // miuix.navigator.Navigator
    public void toggleContent(boolean z) {
        this.mNavigationView.toggleContent(z);
    }

    @Override // miuix.navigator.Navigator
    public boolean isContentOpen() {
        return this.mNavigationView.isContentOpen();
    }

    void userFocusContent(boolean z) {
        this.mNavigationView.maskSecondaryContent(z);
        this.mNavigationView.setOverlaySwitchEnabled(!z);
    }

    boolean isContentUserFocused() {
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            return miuixNavigationLayout.isSecondaryContentMasked();
        }
        return false;
    }

    @Override // miuix.navigator.Navigator
    public void immerseSecondaryContent(boolean z) {
        this.mSecondaryContentSub.requestFocus(true);
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.maskNavigation(z);
            this.mNavigationView.maskContent(z);
        }
    }

    @Override // miuix.navigator.Navigator
    public void setCrossBackground(int i) {
        this.mCrossBackgroundRes = i;
        this.mCrossBackground = null;
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.setCrossBackground(i);
        }
    }

    @Override // miuix.navigator.Navigator
    public void setCrossBackground(View view) {
        this.mCrossBackgroundRes = 0;
        this.mCrossBackground = view;
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.setCrossBackground(view);
        }
    }

    @Override // miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onNavigatorModeChanged(Navigator.Mode mode, Navigator.Mode mode2) {
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.notifyModeChanged(mode2);
            updateOnBackPressedCallbackEnabled();
        }
        Iterator<NavigatorFragmentListener> it = this.mNavigatorFragmentListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigatorModeChanged(mode, mode2);
        }
    }

    @Override // miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onNavigationVisibilityChanged(int i) {
        if (this.mNavigationVisibility == i) {
            return;
        }
        this.mNavigationVisibility = i;
        Iterator<NavigatorFragmentListener> it = this.mNavigatorFragmentListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigationVisibilityChanged(i);
        }
        updateOnBackPressedCallbackEnabled();
    }

    @Override // miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onContentVisibilityChanged(int i) {
        if (this.mContentVisibility == i) {
            return;
        }
        this.mContentVisibility = i;
        Iterator<NavigatorFragmentListener> it = this.mNavigatorFragmentListeners.iterator();
        while (it.hasNext()) {
            it.next().onContentVisibilityChanged(i);
        }
        updateOnBackPressedCallbackEnabled();
    }

    @Override // miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onSecondaryContentVisibilityChanged(int i) {
        if (this.mSecondaryContentVisibility == i) {
            return;
        }
        this.mSecondaryContentVisibility = i;
        Iterator<NavigatorFragmentListener> it = this.mNavigatorFragmentListeners.iterator();
        while (it.hasNext()) {
            it.next().onSecondaryContentVisibilityChanged(i);
        }
        updateOnBackPressedCallbackEnabled();
    }

    @Override // miuix.navigator.NavigatorFragmentListener
    public void onBottomNavigationPrepared() {
        Iterator<NavigatorFragmentListener> it = this.mNavigatorFragmentListeners.iterator();
        while (it.hasNext()) {
            it.next().onBottomNavigationPrepared();
        }
    }

    @Override // miuix.navigator.NavigatorFragmentListener
    public void onBottomNavigationDestroyed() {
        Iterator<NavigatorFragmentListener> it = this.mNavigatorFragmentListeners.iterator();
        while (it.hasNext()) {
            it.next().onBottomNavigationDestroyed();
        }
    }

    @Override // miuix.navigator.Navigator, miuix.navigator.NavigatorFragmentListener
    public void onBottomNavigationVisibilityChanged(boolean z, int i) {
        Iterator<NavigatorFragmentListener> it = this.mNavigatorFragmentListeners.iterator();
        while (it.hasNext()) {
            it.next().onBottomNavigationVisibilityChanged(z, i);
        }
    }

    public Context getContext() {
        return this.mNavHostFragment.getContext();
    }

    public NavigationAdapter getAdapter() {
        return this.mNavigatorInfoManager.getAdapter();
    }

    void setNavigationView(View view) {
        MiuixNavigationLayout miuixNavigationLayout = (MiuixNavigationLayout) view;
        this.mNavigationView = miuixNavigationLayout;
        Boolean bool = this.mSettingNavigationInitOpen;
        if (bool != null) {
            miuixNavigationLayout.setNavigationInitOpen(bool.booleanValue());
            this.mSettingNavigationInitOpen = null;
        }
        initExtraViews();
        Bundle bundle = this.mSavedState;
        if (bundle != null) {
            this.mNavigationView.onRestoreState(bundle);
            this.mSavedState = null;
        }
        MiuixNavigationLayout.WidthConfig widthConfig = this.mNavigatorWidthConfig;
        if (widthConfig != null) {
            this.mNavigationView.setWidthConfig(widthConfig);
            this.mNavigatorWidthConfig = null;
        }
        this.mNavigationView.initWithMode(getNavigationMode());
        this.mNavigationView.setNavigatorFragmentListener(this);
        View view2 = this.mNavigationSwitch;
        if (view2 != null) {
            this.mNavigationView.setNavigationSwitch(view2);
        }
        for (Map.Entry<View, ViewAfterNavigatorSwitchPresenter> entry : this.mContentSwitch.entrySet()) {
            this.mNavigationView.addContentSwitch(entry.getKey(), entry.getValue());
        }
        this.mContentSwitch.clear();
        for (Map.Entry<View, ViewAfterNavigatorSwitchPresenter> entry2 : this.mSecondaryContentSwitch.entrySet()) {
            this.mNavigationView.addSecondaryContentSwitch(entry2.getKey(), entry2.getValue());
        }
        this.mSecondaryContentSwitch.clear();
        View view3 = this.mCrossBackground;
        if (view3 != null) {
            this.mNavigationView.setCrossBackground(view3);
        } else {
            int i = this.mCrossBackgroundRes;
            if (i != 0) {
                this.mNavigationView.setCrossBackground(i);
            }
        }
        if (!this.mNavigatorStateListeners.isEmpty()) {
            this.mNavigationView.setNavigatorStateListener(this);
        }
        updateOnBackPressedCallbackEnabled();
    }

    @Deprecated
    public void startContentActionMode(miuix.appcompat.app.Fragment fragment, final ActionMode.Callback callback) {
        if (this.mContentActionModeWrapper != null || callback == null) {
            return;
        }
        ActionMode.Callback callback2 = new ActionMode.Callback() { // from class: miuix.navigator.NavigatorImpl.1
            @Override // android.view.ActionMode.Callback
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return callback.onCreateActionMode(actionMode, menu);
            }

            @Override // android.view.ActionMode.Callback
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return callback.onPrepareActionMode(actionMode, menu);
            }

            @Override // android.view.ActionMode.Callback
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return callback.onActionItemClicked(actionMode, menuItem);
            }

            @Override // android.view.ActionMode.Callback
            public void onDestroyActionMode(ActionMode actionMode) {
                NavigatorImpl.this.mContentActionModeWrapper = null;
                NavigatorImpl.this.showBottomTab();
                callback.onDestroyActionMode(actionMode);
            }
        };
        this.mContentActionModeWrapper = callback2;
        fragment.startActionMode(callback2);
        hideBottomTab(false);
    }

    @Override // miuix.navigator.Navigator
    public OnBackPressedCallback getOnBackPressedCallback() {
        return this.mCallback;
    }

    public boolean canCloseNavigation() {
        return !this.mEditing && this.mNavigationView.isOverlay() && this.mNavigationView.isNavigationOpen();
    }

    @Override // miuix.navigator.Navigator
    public boolean canPopSecondaryContent() {
        int backStackEntryCount = this.mSecondaryContentSub.getFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            return true;
        }
        if (backStackEntryCount == 0) {
            return false;
        }
        return this.mNavigationView.isSecondaryOnTopNow();
    }

    void updateOnBackPressedCallbackEnabled() {
        this.mCallback.setEnabled(this.mNavigationView != null && (canCloseNavigation() || canPopSecondaryContent()));
    }

    public void onEditingChanged(boolean z) {
        this.mEditing = z;
        this.mNavigationView.setEditingMode(z);
        updateOnBackPressedCallbackEnabled();
    }

    @Override // miuix.navigator.Navigator
    public void addNavigatorFragmentListener(NavigatorFragmentListener navigatorFragmentListener) {
        if (navigatorFragmentListener != null) {
            this.mNavigatorFragmentListeners.add(navigatorFragmentListener);
            navigatorFragmentListener.onNavigationVisibilityChanged(this.mNavigationVisibility);
            navigatorFragmentListener.onContentVisibilityChanged(this.mContentVisibility);
            navigatorFragmentListener.onSecondaryContentVisibilityChanged(this.mSecondaryContentVisibility);
        }
    }

    @Override // miuix.navigator.Navigator
    public void removeNavigatorFragmentListener(NavigatorFragmentListener navigatorFragmentListener) {
        this.mNavigatorFragmentListeners.remove(navigatorFragmentListener);
    }

    @Override // miuix.navigator.Navigator
    public int getNavigationVisibility() {
        return this.mNavigationVisibility;
    }

    @Override // miuix.navigator.Navigator
    public int getContentVisibility() {
        return this.mContentVisibility;
    }

    @Override // miuix.navigator.Navigator
    public int getSecondaryContentVisibility() {
        return this.mSecondaryContentVisibility;
    }

    @Override // miuix.navigator.Navigator
    public void addNavigatorStateListener(Navigator.NavigatorStateListener navigatorStateListener) {
        if (navigatorStateListener == null) {
            return;
        }
        if (this.mNavigationView != null && this.mNavigatorStateListeners.isEmpty()) {
            this.mNavigationView.setNavigatorStateListener(this);
        }
        this.mNavigatorStateListeners.add(navigatorStateListener);
    }

    @Override // miuix.navigator.Navigator
    public void removeNavigatorStateListener(Navigator.NavigatorStateListener navigatorStateListener) {
        this.mNavigatorStateListeners.remove(navigatorStateListener);
        if (this.mNavigationView == null || !this.mNavigatorStateListeners.isEmpty()) {
            return;
        }
        this.mNavigationView.setNavigatorStateListener(null);
    }

    public void setSecondaryOnTop(boolean z, boolean z2) {
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.setSecondaryOnTop(z, z2);
            return;
        }
        if (this.mSavedState == null) {
            this.mSavedState = new Bundle();
        }
        this.mSavedState.putBoolean("secondaryOnTop", z);
    }

    public boolean isSecondaryOnTop() {
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            return miuixNavigationLayout.isSecondaryOnTop();
        }
        Bundle bundle = this.mSavedState;
        if (bundle != null) {
            return bundle.getBoolean("secondaryOnTop");
        }
        return false;
    }

    public void notifyNavigationMenuSelected(MenuItem menuItem) {
        Iterator<NavigatorFragmentListener> it = this.mNavigatorFragmentListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigationMenuSelected(menuItem);
        }
    }

    void setNavigationSwitch(View view) {
        this.mNavigationSwitch = view;
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.setNavigationSwitch(view);
        }
    }

    void addContentSwitch(View view, ViewAfterNavigatorSwitchPresenter viewAfterNavigatorSwitchPresenter) {
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.addContentSwitch(view, viewAfterNavigatorSwitchPresenter);
        } else {
            this.mContentSwitch.put(view, viewAfterNavigatorSwitchPresenter);
        }
    }

    void removeContentSwitch(View view) {
        if (view == null) {
            return;
        }
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.removeContentSwitch(view);
        } else {
            this.mContentSwitch.remove(view);
        }
    }

    void addSecondaryContentSwitch(View view, ViewAfterNavigatorSwitchPresenter viewAfterNavigatorSwitchPresenter) {
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.addSecondaryContentSwitch(view, viewAfterNavigatorSwitchPresenter);
        } else {
            this.mSecondaryContentSwitch.put(view, viewAfterNavigatorSwitchPresenter);
        }
    }

    void removeSecondaryContentSwitch(View view) {
        if (view == null) {
            return;
        }
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.removeSecondaryContentSwitch(view);
        } else {
            this.mSecondaryContentSwitch.remove(view);
        }
    }

    void releaseView() {
        this.mNavigationSwitch = null;
        this.mContentSwitch.clear();
        this.mSecondaryContentSwitch.clear();
        this.mNavigationView.setNavigatorFragmentListener(null);
        this.mNavigationView.setNavigatorStateListener(null);
        this.mNavigationView = null;
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onNavigationOpenBegin() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigationOpenBegin();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onNavigationOpenFinish() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigationOpenFinish();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onNavigationOpenCancel() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigationOpenCancel();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onNavigationCloseBegin() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigationCloseBegin();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onNavigationCloseFinish() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigationCloseFinish();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onNavigationCloseCancel() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigationCloseCancel();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onNavigationRatioChanged(float f) {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigationRatioChanged(f);
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onContentOpenBegin() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onContentOpenBegin();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onContentOpenFinish() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onContentOpenFinish();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onContentOpenCancel() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onContentOpenCancel();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onContentCloseBegin() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onContentCloseBegin();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onContentCloseFinish() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onContentCloseFinish();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onContentCloseCancel() {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onContentCloseCancel();
        }
    }

    @Override // miuix.navigator.Navigator.NavigatorStateListener
    public void onContentRatioChanged(float f) {
        Iterator<Navigator.NavigatorStateListener> it = this.mNavigatorStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onContentRatioChanged(f);
        }
    }

    void dispatchCreate() {
        forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda1
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                ((SubNavigator) obj).setHostState(1);
            }
        });
    }

    void dispatchActivityCreated() {
        forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda10
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                ((SubNavigator) obj).setHostState(2);
            }
        });
    }

    void dispatchStart() {
        forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda0
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                ((SubNavigator) obj).setHostState(3);
            }
        });
    }

    void dispatchResume() {
        forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda2
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                ((SubNavigator) obj).setHostState(4);
            }
        });
    }

    void dispatchPause() {
        forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda7
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                ((SubNavigator) obj).setHostState(3);
            }
        });
    }

    void dispatchStop() {
        forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda5
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                ((SubNavigator) obj).setHostState(2);
            }
        });
    }

    void dispatchDestroyView() {
        forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda9
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                ((SubNavigator) obj).setHostState(1);
            }
        });
    }

    void dispatchDestroy() {
        forAllSubNavigator(new Consumer() { // from class: miuix.navigator.NavigatorImpl$$ExternalSyntheticLambda8
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                ((SubNavigator) obj).setHostState(0);
            }
        });
    }

    Navigator findNavigator(Fragment fragment) {
        if (fragment == null || fragment == this.mNavHostFragment) {
            return this;
        }
        FragmentManager parentFragmentManager = fragment.getParentFragmentManager();
        if (parentFragmentManager == this.mSecondaryContentSub.getFragmentManager()) {
            return this.mSecondaryContentSub;
        }
        if (parentFragmentManager == this.mContentSub.getFragmentManager()) {
            return this.mContentSub;
        }
        if (parentFragmentManager == this.mNavigationSub.getFragmentManager()) {
            return this.mNavigationSub;
        }
        return findNavigator(fragment.getParentFragment());
    }

    NavHostFragment getNavHostFragment() {
        return this.mNavHostFragment;
    }

    @Override // miuix.navigator.Navigator
    public void setSplitAnimationStyle(int i) {
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.setSplitAnimationStyle(i);
        }
    }

    @Override // miuix.navigator.Navigator
    public void setContentExpandedPaddingWithDp(int i) {
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.setContentExpandedPaddingWithDp(i);
        }
    }

    @Override // miuix.navigator.Navigator
    public void setContentExpandedMaxWidthWithDp(int i) {
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.setContentExpandedMaxWidthWithDp(i);
        }
    }

    @Override // miuix.navigator.Navigator
    public void setSplitAnimationMaskBlurRadiusWithPx(int i) {
        MiuixNavigationLayout miuixNavigationLayout = this.mNavigationView;
        if (miuixNavigationLayout != null) {
            miuixNavigationLayout.setSplitAnimationMaskBlurRadius(i);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return ShortcutsCallback.dispatchKeyEvent(this.mNavigationSub.getFragmentManager(), keyEvent) || ShortcutsCallback.dispatchKeyEvent(this.mContentSub.getFragmentManager(), keyEvent) || ShortcutsCallback.dispatchKeyEvent(this.mSecondaryContentSub.getFragmentManager(), keyEvent);
    }

    public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
        return ShortcutsCallback.dispatchKeyShortcutEvent(this.mNavigationSub.getFragmentManager(), keyEvent) || ShortcutsCallback.dispatchKeyShortcutEvent(this.mContentSub.getFragmentManager(), keyEvent) || ShortcutsCallback.dispatchKeyShortcutEvent(this.mSecondaryContentSub.getFragmentManager(), keyEvent);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return ShortcutsCallback.dispatchTouchEvent(this.mNavigationSub.getFragmentManager(), motionEvent) || ShortcutsCallback.dispatchTouchEvent(this.mContentSub.getFragmentManager(), motionEvent) || ShortcutsCallback.dispatchTouchEvent(this.mSecondaryContentSub.getFragmentManager(), motionEvent);
    }

    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        return ShortcutsCallback.dispatchTrackballEvent(this.mNavigationSub.getFragmentManager(), motionEvent) || ShortcutsCallback.dispatchTrackballEvent(this.mContentSub.getFragmentManager(), motionEvent) || ShortcutsCallback.dispatchTrackballEvent(this.mSecondaryContentSub.getFragmentManager(), motionEvent);
    }

    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        return ShortcutsCallback.dispatchGenericMotionEvent(this.mNavigationSub.getFragmentManager(), motionEvent) || ShortcutsCallback.dispatchGenericMotionEvent(this.mContentSub.getFragmentManager(), motionEvent) || ShortcutsCallback.dispatchGenericMotionEvent(this.mSecondaryContentSub.getFragmentManager(), motionEvent);
    }

    public void dispatchProvideKeyboardShortcuts(List<KeyboardShortcutGroup> list, Menu menu, int i) {
        ShortcutsCallback.dispatchProvideKeyboardShortcuts(this.mNavigationSub.getFragmentManager(), list, menu, i);
        ShortcutsCallback.dispatchProvideKeyboardShortcuts(this.mContentSub.getFragmentManager(), list, menu, i);
        ShortcutsCallback.dispatchProvideKeyboardShortcuts(this.mSecondaryContentSub.getFragmentManager(), list, menu, i);
    }

    public boolean dispatchKeyDown(int i, KeyEvent keyEvent) {
        return ShortcutsCallback.dispatchKeyDown(this.mNavigationSub.getFragmentManager(), i, keyEvent) || ShortcutsCallback.dispatchKeyDown(this.mContentSub.getFragmentManager(), i, keyEvent) || ShortcutsCallback.dispatchKeyDown(this.mSecondaryContentSub.getFragmentManager(), i, keyEvent);
    }

    public boolean dispatchKeyLongPress(int i, KeyEvent keyEvent) {
        return ShortcutsCallback.dispatchKeyLongPress(this.mNavigationSub.getFragmentManager(), i, keyEvent) || ShortcutsCallback.dispatchKeyLongPress(this.mContentSub.getFragmentManager(), i, keyEvent) || ShortcutsCallback.dispatchKeyLongPress(this.mSecondaryContentSub.getFragmentManager(), i, keyEvent);
    }

    public boolean dispatchKeyUp(int i, KeyEvent keyEvent) {
        return ShortcutsCallback.dispatchKeyUp(this.mNavigationSub.getFragmentManager(), i, keyEvent) || ShortcutsCallback.dispatchKeyUp(this.mContentSub.getFragmentManager(), i, keyEvent) || ShortcutsCallback.dispatchKeyUp(this.mSecondaryContentSub.getFragmentManager(), i, keyEvent);
    }

    public boolean dispatchKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return ShortcutsCallback.dispatchKeyMultiple(this.mNavigationSub.getFragmentManager(), i, i2, keyEvent) || ShortcutsCallback.dispatchKeyMultiple(this.mContentSub.getFragmentManager(), i, i2, keyEvent) || ShortcutsCallback.dispatchKeyMultiple(this.mSecondaryContentSub.getFragmentManager(), i, i2, keyEvent);
    }
}
