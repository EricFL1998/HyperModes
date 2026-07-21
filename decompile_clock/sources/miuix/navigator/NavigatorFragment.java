package miuix.navigator;

import miuix.appcompat.app.Fragment;

/* JADX INFO: loaded from: classes3.dex */
public abstract class NavigatorFragment extends Fragment {
    public Navigator getNavigator() {
        return Navigator.get(this);
    }
}
