package miuix.navigator.app;

import android.os.Bundle;
import miuix.appcompat.app.Fragment;
import miuix.navigator.NavHostFragment;
import miuix.navigator.Navigator;
import miuix.navigator.navigatorinfo.NavigatorInfo;
import miuix.navigator.navigatorinfo.NavigatorInfoProvider;

/* JADX INFO: loaded from: classes3.dex */
public interface NavigatorBuilder {
    int getBottomTabMenu();

    NavigatorInfoProvider getBottomTabMenuNavInfoProvider();

    Class<? extends Fragment> getDefaultContentFragment();

    NavHostFragment getNavHostFragment();

    int getNavigationOptionMenu();

    Navigator getNavigator();

    Bundle getNavigatorInitArgs();

    Navigator.Label newLabel(String str, int i, NavigatorInfo navigatorInfo);

    Navigator.Label newLabel(String str, NavigatorInfo navigatorInfo);

    void onCreateOtherNavigation(Navigator navigator, Bundle bundle);

    void onCreatePrimaryNavigation(Navigator navigator, Bundle bundle);
}
