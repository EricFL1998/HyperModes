package miuix.navigator.app;

import android.os.Bundle;
import miuix.navigator.NavHostFragment;
import miuix.navigator.NavigatorImpl;

/* JADX INFO: loaded from: classes3.dex */
public class MiuixNavHostFragment extends NavHostFragment {
    @Override // miuix.navigator.NavHostFragment
    protected NavigatorImpl createNavigator(Bundle bundle, NavHostFragment navHostFragment) {
        return new MiuixNavigatorImpl(bundle, navHostFragment);
    }
}
