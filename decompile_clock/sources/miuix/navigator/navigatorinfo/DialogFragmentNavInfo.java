package miuix.navigator.navigatorinfo;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import miuix.navigator.Navigator;
import miuix.navigator.NavigatorImpl;

/* JADX INFO: loaded from: classes3.dex */
public class DialogFragmentNavInfo extends NavigatorInfo {
    private final DialogFragment mDialog;
    private final String mDialogTag;

    public DialogFragmentNavInfo(DialogFragment dialogFragment) {
        this(dialogFragment, null);
    }

    public DialogFragmentNavInfo(DialogFragment dialogFragment, String str) {
        this.mDialog = dialogFragment;
        this.mDialogTag = str;
    }

    @Override // miuix.navigator.navigatorinfo.NavigatorInfo
    public boolean onNavigate(Navigator navigator) {
        FragmentManager fragmentManager = ((NavigatorImpl) navigator).getBaseNavigator().getFragmentManager();
        DialogFragment dialogFragment = this.mDialog;
        String str = this.mDialogTag;
        if (str == null) {
            str = "miuix:navigatorDialogFragment";
        }
        dialogFragment.show(fragmentManager, str);
        return false;
    }
}
