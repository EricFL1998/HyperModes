package miuix.appcompat.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
interface ActivityCallback {
    void onConfigurationChanged(Configuration configuration);

    void onCreate(Bundle bundle);

    boolean onCreatePanelMenu(int i, Menu menu);

    View onCreatePanelView(int i);

    boolean onMenuItemSelected(int i, MenuItem menuItem);

    void onPanelClosed(int i, Menu menu);

    void onPanelViewAdded(int i, View view, Menu menu, Menu menu2);

    void onPostResume();

    boolean onPreparePanel(int i, View view, Menu menu);

    void onRestoreInstanceState(Bundle bundle);

    void onSaveInstanceState(Bundle bundle);

    void onStop();
}
