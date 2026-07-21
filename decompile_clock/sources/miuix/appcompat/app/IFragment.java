package miuix.appcompat.app;

import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import miuix.container.ExtraPaddingObserver;
import miuix.container.ExtraPaddingProcessor;

/* JADX INFO: loaded from: classes2.dex */
public interface IFragment extends IImmersionMenu, IContentInsetState, ExtraPaddingProcessor, ExtraPaddingObserver {
    boolean acceptExtraPaddingFromParent();

    void checkThemeLegality();

    ActionBar getActionBar();

    Context getThemedContext();

    boolean hasActionBar();

    boolean isInEditActionMode();

    boolean isIsInSearchActionMode();

    boolean isRegisterResponsive();

    void onActionModeFinished(ActionMode actionMode);

    void onActionModeStarted(ActionMode actionMode);

    boolean onCreateOptionsMenu(Menu menu);

    boolean onCreatePanelMenu(int i, Menu menu);

    View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle);

    void onOptionsMenuViewAdded(Menu menu, Menu menu2);

    void onPanelClosed(int i, Menu menu);

    void onPreparePanel(int i, View view, Menu menu);

    void onViewInflated(View view, Bundle bundle);

    void setNestedScrollingParentEnabled(boolean z);

    void setThemeRes(int i);

    ActionMode startActionMode(ActionMode.Callback callback);
}
