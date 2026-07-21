package miuix.navigator.app;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import java.util.List;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.LayoutUiModeHelper;
import miuix.internal.util.AttributeResolver;
import miuix.navigator.NavHostFragment;
import miuix.navigator.Navigator;
import miuix.navigator.R;
import miuix.navigator.adapter.ListCategoryAdapter;
import miuix.navigator.adapter.MenuCategoryAdapter;
import miuix.navigator.navigatorinfo.NavigatorInfo;

/* JADX INFO: loaded from: classes3.dex */
public abstract class NavigatorActivity extends AppCompatActivity implements NavigatorBuilder {
    private NavHostFragment mNavHostFragment;
    private NavigatorViewModel mNaviViewModel;

    protected boolean onPlaceNavHostFragment() {
        return false;
    }

    @Override // miuix.appcompat.app.AppCompatActivity, miuix.appcompat.app.IActivity
    public void checkThemeLegality() {
        int iResolve = AttributeResolver.resolve(this, R.attr.isNavigatorTheme);
        if (iResolve < 0) {
            throw new IllegalStateException("You need to use a Theme.Light/Dark.Navigator theme (or descendant) with this fragment. attrValue:" + iResolve + " R.attr.isNavigatorTheme:" + R.attr.isNavigatorTheme + " 0x" + Integer.toHexString(R.attr.isNavigatorTheme) + " theme:" + getTheme());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LayoutUiModeHelper.autoSetLayoutUiMode(this);
        if (bundle == null && !onPlaceNavHostFragment()) {
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, MiuixNavHostFragment.class, getNavigatorInitArgs(), NavHostFragment.TAG).commitNow();
        }
        this.mNavHostFragment = onFindNavHostFragment();
        NavigatorViewModel navigatorViewModel = (NavigatorViewModel) new ViewModelProvider(this).get(NavigatorViewModel.class);
        this.mNaviViewModel = navigatorViewModel;
        navigatorViewModel.init(this, bundle);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        LayoutUiModeHelper.autoSetLayoutUiMode(this);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        LayoutUiModeHelper.autoSetLayoutUiMode(this);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }

    protected NavHostFragment onFindNavHostFragment() {
        return (NavHostFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
    }

    @Override // miuix.navigator.app.NavigatorBuilder
    public NavHostFragment getNavHostFragment() {
        return this.mNavHostFragment;
    }

    @Override // miuix.navigator.app.NavigatorBuilder
    public Navigator getNavigator() {
        return Navigator.get(getNavHostFragment());
    }

    @Override // miuix.navigator.app.NavigatorBuilder
    public Navigator.Label newLabel(String str, NavigatorInfo navigatorInfo) {
        return newLabel(str, -1, navigatorInfo);
    }

    @Override // miuix.navigator.app.NavigatorBuilder
    public Navigator.Label newLabel(String str, int i, NavigatorInfo navigatorInfo) {
        Navigator navigator = getNavigator();
        Navigator.Label labelNewLabel = navigator.newLabel(navigatorInfo.getNavigationId());
        labelNewLabel.setTitle(str);
        labelNewLabel.setIcon(i);
        labelNewLabel.setNavigatorInfo(navigatorInfo);
        navigator.addLabel(labelNewLabel);
        return labelNewLabel;
    }

    public MenuCategoryAdapter.Item newMenuItem(List<MenuCategoryAdapter.Item> list, String str, int i) {
        MenuCategoryAdapter.Item item = new MenuCategoryAdapter.Item(str, i);
        list.add(item);
        return item;
    }

    public ListCategoryAdapter.Item newListItem(List<ListCategoryAdapter.Item> list, String str, int i) {
        ListCategoryAdapter.Item item = new ListCategoryAdapter.Item(str, i);
        list.add(item);
        return item;
    }
}
