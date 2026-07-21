package miuix.appcompat.app;

import android.content.res.Configuration;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: loaded from: classes2.dex */
public interface ActionBarDelegate extends IContentInsetState {
    ActionBar createActionBar();

    int getBottomMenuMode();

    void invalidateOptionsMenu();

    void onActionModeFinished(ActionMode actionMode);

    void onActionModeStarted(ActionMode actionMode);

    void onConfigurationChanged(Configuration configuration);

    boolean onCreatePanelMenu(int i, Menu menu);

    View onCreatePanelView(int i);

    void onDestroy();

    boolean onMenuItemSelected(int i, MenuItem menuItem);

    void onPanelClosed(int i, Menu menu);

    void onPanelViewAdded(int i, View view, Menu menu, Menu menu2);

    void onPostResume();

    boolean onPreparePanel(int i, View view, Menu menu);

    void onStop();

    ActionMode onWindowStartingActionMode(ActionMode.Callback callback);

    void registerCoordinateScrollView(View view);

    boolean requestWindowFeature(int i);

    default void setBottomExtraInset(int i) {
    }

    default void setBottomMenuMode(int i) {
    }

    @Override // miuix.appcompat.app.IContentInsetState
    void setCorrectNestedScrollMotionEventEnabled(boolean z);

    ActionMode startActionMode(ActionMode.Callback callback);

    void unregisterCoordinateScrollView(View view);
}
