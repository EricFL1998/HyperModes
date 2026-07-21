package miuix.navigator.app;

import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.strategy.ActionBarConfig;
import miuix.appcompat.app.strategy.ActionBarSpec;
import miuix.appcompat.app.strategy.IActionBarStrategy;

/* JADX INFO: loaded from: classes3.dex */
public class SecondaryContentActionBarStrategy implements IActionBarStrategy {
    @Override // miuix.appcompat.app.strategy.IActionBarStrategy
    public ActionBarConfig config(ActionBar actionBar, ActionBarSpec actionBarSpec) {
        if (actionBar == null || actionBarSpec == null) {
            return null;
        }
        ActionBarConfig actionBarConfig = new ActionBarConfig();
        actionBarConfig.expandState = 0;
        actionBarConfig.resizable = false;
        actionBarConfig.endMenuMaxItemCount = 3;
        return actionBarConfig;
    }
}
