package miuix.navigator.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import miuix.core.util.MiuiBlurUtils;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.LiteUtils;
import miuix.navigator.BottomNavigation;
import miuix.navigator.BottomTab;
import miuix.navigator.NavHostFragment;
import miuix.navigator.NavigatorImpl;
import miuix.navigator.R;
import miuix.navigator.bottomnavigation.BottomNavigationView;
import miuix.navigator.navigation.NavigationBarMenu;
import miuix.navigator.navigation.NavigationBarView;
import miuix.navigator.navigatorinfo.NavigatorInfo;
import miuix.navigator.navigatorinfo.NavigatorInfoProvider;

/* JADX INFO: loaded from: classes3.dex */
public class MiuixNavigatorImpl extends NavigatorImpl {
    private BottomNavigationView mBottomNavigationView;
    private int mBottomTabStyle;
    private NavigationBarView.OnItemSelectedListener mDefaultNavTabSelectedListener;
    private NavigatorInfoProvider mInfoProvider;
    private int mInitTabMenuId;

    /* JADX INFO: renamed from: lambda$new$0$miuix-navigator-app-MiuixNavigatorImpl, reason: not valid java name */
    /* synthetic */ boolean m1909lambda$new$0$miuixnavigatorappMiuixNavigatorImpl(MenuItem menuItem) {
        Intent intent = menuItem.getIntent();
        if (intent == null) {
            return false;
        }
        BottomTab bottomTabFindTab = findTab(intent.getIntExtra(NavigationBarMenu.EXTRA_BOTTOM_TAB_ID, -1));
        if (bottomTabFindTab == null) {
            return true;
        }
        navigate(bottomTabFindTab.getNavigatorInfo());
        return true;
    }

    public MiuixNavigatorImpl(Bundle bundle, NavHostFragment navHostFragment) {
        super(bundle, navHostFragment);
        this.mInitTabMenuId = 0;
        this.mInfoProvider = null;
        this.mBottomTabStyle = 0;
        this.mBottomNavigationView = null;
        this.mDefaultNavTabSelectedListener = new NavigationBarView.OnItemSelectedListener() { // from class: miuix.navigator.app.MiuixNavigatorImpl$$ExternalSyntheticLambda0
            @Override // miuix.navigator.navigation.NavigationBarView.OnItemSelectedListener
            public final boolean onNavigationItemSelected(MenuItem menuItem) {
                return this.f$0.m1909lambda$new$0$miuixnavigatorappMiuixNavigatorImpl(menuItem);
            }
        };
    }

    @Override // miuix.navigator.NavigatorImpl, miuix.navigator.Navigator
    public NavigationBarView getBottomTabView() {
        return this.mBottomNavigationView;
    }

    @Override // miuix.navigator.NavigatorImpl, miuix.navigator.Navigator
    public void setBottomTabStyle(int i) {
        this.mBottomTabStyle = i;
        BottomNavigationView bottomNavigationView = this.mBottomNavigationView;
        if (bottomNavigationView != null) {
            bottomNavigationView.setLayoutStyle(i);
        }
    }

    @Override // miuix.navigator.NavigatorImpl
    public void initExtraViews() {
        int i = this.mInitTabMenuId;
        if (i != 0) {
            this.mBottomNavigationView.inflateMenu(i);
            if (this.mInfoProvider != null) {
                NavigationBarMenu navigationBarMenu = (NavigationBarMenu) this.mBottomNavigationView.getMenu();
                for (int i2 = 0; i2 < navigationBarMenu.size(); i2++) {
                    Intent intent = navigationBarMenu.getItem(i2).getIntent();
                    int intExtra = -1;
                    if (intent != null) {
                        intExtra = intent.getIntExtra(NavigationBarMenu.EXTRA_BOTTOM_TAB_ID, -1);
                    }
                    NavigatorInfo navigatorInfoOnCreateNavigatorInfo = this.mInfoProvider.onCreateNavigatorInfo(intExtra);
                    BottomTab bottomTabNewTab = newTab();
                    bottomTabNewTab.setNavigatorInfo(navigatorInfoOnCreateNavigatorInfo);
                    super.addTab(bottomTabNewTab);
                }
            }
        }
        if (getNavigationView().getBottomNavigation() != this.mBottomNavigationView) {
            getNavigationView().setBottomNavigation(this.mBottomNavigationView);
        }
    }

    @Override // miuix.navigator.NavigatorImpl
    public void showBottomTab() {
        BottomNavigationView bottomNavigationView = this.mBottomNavigationView;
        if (bottomNavigationView != null) {
            bottomNavigationView.show(bottomNavigationView.getParent() != null);
        }
    }

    @Override // miuix.navigator.NavigatorImpl
    public void hideBottomTab(boolean z) {
        BottomNavigationView bottomNavigationView = this.mBottomNavigationView;
        if (bottomNavigationView != null) {
            bottomNavigationView.hide(z, bottomNavigationView.getParent() != null);
        }
    }

    @Override // miuix.navigator.NavigatorImpl, miuix.navigator.Navigator
    public void setBottomTabMenu(int i, NavigatorInfoProvider navigatorInfoProvider) {
        this.mInitTabMenuId = i;
        this.mInfoProvider = navigatorInfoProvider;
        if (i != 0) {
            ensureBottomNavigationExist();
        }
    }

    @Override // miuix.navigator.NavigatorImpl, miuix.navigator.Navigator
    public Menu getBottomTabMenu() {
        ensureBottomNavigationExist();
        return this.mBottomNavigationView.getMenu();
    }

    @Override // miuix.navigator.NavigatorImpl, miuix.navigator.Navigator
    public void addTab(BottomTab bottomTab) {
        super.addTab(bottomTab);
        ensureBottomNavigationExist();
        ((NavigationBarMenu) getBottomTabMenu()).add(bottomTab);
    }

    @Override // miuix.navigator.NavigatorImpl, miuix.navigator.Navigator
    public void addTab(BottomTab bottomTab, int i) {
        super.addTab(bottomTab, i);
        ensureBottomNavigationExist();
        ((NavigationBarMenu) getBottomTabMenu()).add(bottomTab);
    }

    @Override // miuix.navigator.NavigatorImpl, miuix.navigator.Navigator
    public void selectTab(int i) {
        super.selectTab(i);
        this.mBottomNavigationView.getPresenter().updateMenuView(false);
    }

    @Override // miuix.navigator.NavigatorImpl, miuix.navigator.Navigator
    public void setTabSelectListener(final BottomNavigation.OnItemSelectedListener onItemSelectedListener) {
        if (onItemSelectedListener == null) {
            this.mBottomNavigationView.setOnItemSelectedListener(this.mDefaultNavTabSelectedListener);
        } else {
            this.mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() { // from class: miuix.navigator.app.MiuixNavigatorImpl.1
                @Override // miuix.navigator.navigation.NavigationBarView.OnItemSelectedListener
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    Intent intent = menuItem.getIntent();
                    if (intent == null) {
                        return false;
                    }
                    return onItemSelectedListener.onNavigationItemSelected(menuItem, MiuixNavigatorImpl.this.findTab(intent.getIntExtra(NavigationBarMenu.EXTRA_BOTTOM_TAB_ID, -1)).getNavigatorInfo());
                }
            });
        }
    }

    @Override // miuix.navigator.NavigatorImpl, miuix.navigator.Navigator
    public void applyBottomNavigationBlur(boolean z) {
        ensureBottomNavigationExist();
        BottomNavigationView bottomNavigationView = this.mBottomNavigationView;
        if (bottomNavigationView != null) {
            bottomNavigationView.applyBlur(z);
        }
    }

    @Override // miuix.navigator.NavigatorImpl, miuix.navigator.Navigator
    public void setBottomNavigationBackgroundVisible(boolean z) {
        ensureBottomNavigationExist();
        BottomNavigationView bottomNavigationView = this.mBottomNavigationView;
        if (bottomNavigationView != null) {
            bottomNavigationView.setBackgroundVisible(z);
        }
    }

    private void ensureBottomNavigationExist() {
        if (this.mBottomNavigationView == null) {
            int i = R.layout.miuix_bottom_navigation;
            int i2 = this.mBottomTabStyle;
            if (i2 == 1) {
                i = R.layout.miuix_bottom_navigation_wide_port;
            } else if (i2 == 3) {
                i = R.layout.miuix_bottom_navigation_wide_land;
            }
            BottomNavigationView bottomNavigationView = (BottomNavigationView) LayoutInflater.from(getContext()).inflate(i, (ViewGroup) null);
            this.mBottomNavigationView = bottomNavigationView;
            bottomNavigationView.setOnItemSelectedListener(this.mDefaultNavTabSelectedListener);
            if (MiuiBlurUtils.isEnable() && !LiteUtils.isCommonLiteStrategy()) {
                this.mBottomNavigationView.setSupportBlur(true);
                if ((AttributeResolver.resolveInt(getContext(), miuix.appcompat.R.attr.bgBlurOptions, 0) & 2) != 0) {
                    this.mBottomNavigationView.setEnableBlur(true);
                    return;
                } else {
                    this.mBottomNavigationView.setEnableBlur(false);
                    return;
                }
            }
            this.mBottomNavigationView.setSupportBlur(false);
        }
    }
}
