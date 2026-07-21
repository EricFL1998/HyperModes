package miuix.appcompat.app.strategy;

import miuix.appcompat.app.ActionBar;

/* JADX INFO: loaded from: classes2.dex */
public class CollapseActionBarStrategy implements IActionBarStrategy {
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
