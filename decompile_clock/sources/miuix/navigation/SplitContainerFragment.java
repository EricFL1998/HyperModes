package miuix.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

/* JADX INFO: loaded from: classes.dex */
public class SplitContainerFragment extends Fragment {
    public static SplitContainerFragment newInstance() {
        return new SplitContainerFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.miuix_fragment_container, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public void replaceRootFragment(Fragment fragment) {
        if (getActivity() == null || !isAdded()) {
            return;
        }
        clearBackStack();
        getChildFragmentManager().beginTransaction().replace(R.id.miuix_fragment_container, fragment).addToBackStack(null).commitAllowingStateLoss();
    }

    public void replaceChildFragment(Fragment fragment) {
        if (getActivity() == null || !isAdded()) {
            return;
        }
        getChildFragmentManager().beginTransaction().replace(R.id.miuix_fragment_container, fragment).addToBackStack(null).commitAllowingStateLoss();
    }

    public void replaceChildFragment(Fragment fragment, int i, int i2, int i3, int i4) {
        if (getActivity() == null || !isAdded()) {
            return;
        }
        getChildFragmentManager().beginTransaction().setCustomAnimations(i, i2, i3, i4).replace(R.id.miuix_fragment_container, fragment).addToBackStack(null).commitAllowingStateLoss();
    }

    public void addChildFragment(Fragment fragment) {
        if (getActivity() == null || !isAdded()) {
            return;
        }
        getChildFragmentManager().beginTransaction().add(R.id.miuix_fragment_container, fragment).addToBackStack(null).commitAllowingStateLoss();
    }

    public void addChildFragment(Fragment fragment, int i, int i2, int i3, int i4) {
        if (getActivity() == null || !isAdded()) {
            return;
        }
        getChildFragmentManager().beginTransaction().setCustomAnimations(i, i2, i3, i4).add(R.id.miuix_fragment_container, fragment).addToBackStack(null).commitAllowingStateLoss();
    }

    public void removeChildFragment(Fragment fragment) {
        if (getActivity() == null || !isAdded()) {
            return;
        }
        getChildFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
    }

    public void hideChildFragment(Fragment fragment) {
        if (getActivity() == null || !isAdded()) {
            return;
        }
        getChildFragmentManager().beginTransaction().hide(fragment).commitAllowingStateLoss();
    }

    public void showChildFragment(Fragment fragment) {
        if (getActivity() == null || !isAdded()) {
            return;
        }
        getChildFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();
    }

    public boolean clearBackStack() {
        if (getActivity() == null || !isAdded() || getChildFragmentManager().getBackStackEntryCount() <= 0) {
            return false;
        }
        getChildFragmentManager().popBackStackImmediate((String) null, 1);
        return true;
    }

    public boolean popBackStack() {
        if (getActivity() == null || !isAdded() || getChildFragmentManager().getBackStackEntryCount() <= 0) {
            return false;
        }
        getChildFragmentManager().popBackStack();
        return true;
    }
}
