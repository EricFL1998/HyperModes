package miuix.navigator.app;

import android.os.Bundle;
import miuix.navigator.NavigatorFragment;
import miuix.navigator.NavigatorFragmentListener;
import miuix.navigator.R;

/* JADX INFO: loaded from: classes3.dex */
public class NavigatorContentChildFragment extends NavigatorFragment implements NavigatorFragmentListener {
    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setThemeRes(R.style.Theme_DayNight_ContentChild);
    }
}
