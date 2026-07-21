package miuix.appcompat.app;

import androidx.fragment.app.FragmentFactory;

/* JADX INFO: loaded from: classes2.dex */
public class DelegateFragmentFactory extends FragmentFactory {
    public FragmentDelegate createFragmentDelegate(androidx.fragment.app.Fragment fragment) {
        return new FragmentDelegate(fragment);
    }
}
