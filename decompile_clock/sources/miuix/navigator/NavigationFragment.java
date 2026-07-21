package miuix.navigator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.Fragment;
import miuix.internal.util.ViewUtils;
import miuix.navigator.adapter.NavigationAdapter;

/* JADX INFO: loaded from: classes3.dex */
public class NavigationFragment extends Fragment {
    private RecyclerView mRV;

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public RecyclerView getRecyclerView() {
        return this.mRV;
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.miuix_navigator_navigation_fragment, viewGroup, false);
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public void onViewInflated(View view, Bundle bundle) {
        NavigatorImpl navigatorImpl = (NavigatorImpl) Navigator.get(this).getBaseNavigator();
        getView().setClickable(true);
        view.setLayerType(2, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.navigation_rv);
        this.mRV = recyclerView;
        recyclerView.setAdapter(navigatorImpl.getAdapter());
        this.mRV.setLayoutManager(new NavigatorLayoutManager(getThemedContext()));
        this.mRV.addItemDecoration(new NavigationDividerItemDecoration(getThemedContext()));
        this.mRV.setItemAnimator(navigatorImpl.getAdapter().getItemAnimator());
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }
        ViewUtils.doOnApplyWindowInsets(this.mRV, new ViewUtils.OnApplyWindowInsetsListener() { // from class: miuix.navigator.NavigationFragment$$ExternalSyntheticLambda0
            @Override // miuix.internal.util.ViewUtils.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view2, WindowInsetsCompat windowInsetsCompat, ViewUtils.RelativePadding relativePadding) {
                return NavigationFragment.lambda$onViewInflated$0(view2, windowInsetsCompat, relativePadding);
            }
        });
    }

    static /* synthetic */ WindowInsetsCompat lambda$onViewInflated$0(View view, WindowInsetsCompat windowInsetsCompat, ViewUtils.RelativePadding relativePadding) {
        WindowInsetsCompat rootWindowInsets = ViewCompat.getRootWindowInsets(view);
        if (rootWindowInsets != null) {
            view.setPaddingRelative(relativePadding.start, relativePadding.top, relativePadding.end, relativePadding.bottom + rootWindowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()).bottom);
        }
        return windowInsetsCompat;
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mRV = null;
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public boolean onCreateOptionsMenu(Menu menu) {
        Navigator navigator = Navigator.get(this);
        if (navigator.getNavigationMenu() != 0) {
            getMenuInflater().inflate(navigator.getNavigationMenu(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        ((NavigatorImpl) Navigator.get(this).getBaseNavigator()).notifyNavigationMenuSelected(menuItem);
        return true;
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onContextItemSelected(MenuItem menuItem) {
        ((NavigationAdapter) this.mRV.getAdapter()).onContextEditAction(menuItem.getItemId());
        return true;
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public void onPanelClosed(int i, Menu menu) {
        if (i == 6) {
            ((NavigationAdapter) this.mRV.getAdapter()).setContextHolder(null);
        }
    }
}
