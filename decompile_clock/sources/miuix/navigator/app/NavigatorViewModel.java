package miuix.navigator.app;

import android.os.Bundle;
import androidx.lifecycle.ViewModel;
import miuix.navigator.Navigator;

/* JADX INFO: loaded from: classes3.dex */
public class NavigatorViewModel extends ViewModel {
    public void init(NavigatorBuilder navigatorBuilder, Bundle bundle) {
        Navigator navigator = navigatorBuilder.getNavigator();
        navigator.setNavigationMenu(navigatorBuilder.getNavigationOptionMenu());
        navigator.setBottomTabMenu(navigatorBuilder.getBottomTabMenu(), navigatorBuilder.getBottomTabMenuNavInfoProvider());
        navigatorBuilder.onCreatePrimaryNavigation(navigator, bundle);
        navigatorBuilder.onCreateOtherNavigation(navigator, bundle);
    }
}
