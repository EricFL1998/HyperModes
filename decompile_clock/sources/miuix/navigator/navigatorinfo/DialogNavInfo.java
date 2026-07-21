package miuix.navigator.navigatorinfo;

import android.app.Dialog;
import miuix.navigator.Navigator;

/* JADX INFO: loaded from: classes3.dex */
public class DialogNavInfo extends NavigatorInfo {
    private final Dialog mDialog;

    public DialogNavInfo(Dialog dialog) {
        this.mDialog = dialog;
    }

    @Override // miuix.navigator.navigatorinfo.NavigatorInfo
    public boolean onNavigate(Navigator navigator) {
        this.mDialog.show();
        return true;
    }
}
