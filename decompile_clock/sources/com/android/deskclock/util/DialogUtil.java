package com.android.deskclock.util;

import android.R;
import android.app.Dialog;
import android.content.DialogInterface;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.view.SimpleDialogFragment;

/* JADX INFO: loaded from: classes.dex */
public class DialogUtil {
    public static SimpleDialogFragment showAlertDialog(String str, String str2, int i, DialogInterface.OnClickListener onClickListener, int i2, DialogInterface.OnClickListener onClickListener2, FragmentManager fragmentManager) {
        SimpleDialogFragment simpleDialogFragmentNewInstance = SimpleDialogFragment.newInstance(str, str2, i2, i, null);
        simpleDialogFragmentNewInstance.setNegativeButton(i, onClickListener);
        simpleDialogFragmentNewInstance.setPositiveButton(i2, onClickListener2);
        simpleDialogFragmentNewInstance.show(fragmentManager, (String) null);
        return simpleDialogFragmentNewInstance;
    }

    public static SimpleDialogFragment showAlertDialog(String str, String str2, int i, DialogInterface.OnClickListener onClickListener, int i2, DialogInterface.OnClickListener onClickListener2, FragmentManager fragmentManager, boolean z, String str3, String str4) {
        SimpleDialogFragment simpleDialogFragmentNewInstance = SimpleDialogFragment.newInstance(str, str2, i2, i, null, z, str3, str4);
        simpleDialogFragmentNewInstance.setNegativeButton(i, onClickListener);
        simpleDialogFragmentNewInstance.setPositiveButton(i2, onClickListener2);
        simpleDialogFragmentNewInstance.show(fragmentManager, (String) null);
        return simpleDialogFragmentNewInstance;
    }

    public static SimpleDialogFragment showAlertDialog(String str, CharSequence charSequence, int i, DialogInterface.OnClickListener onClickListener, int i2, DialogInterface.OnClickListener onClickListener2, DialogInterface.OnCancelListener onCancelListener, FragmentManager fragmentManager, boolean z, String str2, String str3, boolean z2, boolean z3) {
        SimpleDialogFragment simpleDialogFragmentNewInstance = SimpleDialogFragment.newInstance(str, charSequence, i2, i, 0, null, z, str2, str3, z2, z3);
        simpleDialogFragmentNewInstance.setNegativeButton(i, onClickListener);
        simpleDialogFragmentNewInstance.setPositiveButton(i2, onClickListener2);
        simpleDialogFragmentNewInstance.setOnCancelListener(onCancelListener);
        simpleDialogFragmentNewInstance.show(fragmentManager, UserNoticeUtil.TAG_DIALOG_USER_NOTICE);
        return simpleDialogFragmentNewInstance;
    }

    public static SimpleDialogFragment showAlertDialog(String str, String str2, int i, DialogInterface.OnClickListener onClickListener, FragmentManager fragmentManager) {
        SimpleDialogFragment simpleDialogFragmentNewInstance = SimpleDialogFragment.newInstance(str, str2, i, null);
        simpleDialogFragmentNewInstance.setNegativeButton(R.string.cancel, null);
        simpleDialogFragmentNewInstance.setPositiveButton(i, onClickListener);
        simpleDialogFragmentNewInstance.show(fragmentManager, (String) null);
        return simpleDialogFragmentNewInstance;
    }

    public static SimpleDialogFragment showAlertDialog(String str, String[] strArr, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onClickListener2, DialogInterface.OnCancelListener onCancelListener, DialogInterface.OnDismissListener onDismissListener, FragmentManager fragmentManager) {
        SimpleDialogFragment simpleDialogFragmentNewInstance = SimpleDialogFragment.newInstance(str, null, 0, strArr);
        simpleDialogFragmentNewInstance.setNegativeButton(R.string.cancel, onClickListener2);
        simpleDialogFragmentNewInstance.setItems(onClickListener);
        simpleDialogFragmentNewInstance.setOnCancelListener(onCancelListener);
        simpleDialogFragmentNewInstance.setOnDismissListener(onDismissListener);
        simpleDialogFragmentNewInstance.show(fragmentManager, (String) null);
        return simpleDialogFragmentNewInstance;
    }

    public static SimpleDialogFragment showAlertDialog(String str, int i, DialogInterface.OnClickListener onClickListener, FragmentManager fragmentManager) {
        SimpleDialogFragment simpleDialogFragmentNewInstance = SimpleDialogFragment.newInstance(null, str, i);
        simpleDialogFragmentNewInstance.setNeutralButton(i, onClickListener);
        simpleDialogFragmentNewInstance.show(fragmentManager, (String) null);
        return simpleDialogFragmentNewInstance;
    }

    public static SimpleDialogFragment showNotificationPermissionDialog(String str, String str2, int i, DialogInterface.OnClickListener onClickListener, int i2, DialogInterface.OnClickListener onClickListener2, DialogInterface.OnCancelListener onCancelListener, DialogInterface.OnDismissListener onDismissListener, FragmentManager fragmentManager) {
        SimpleDialogFragment simpleDialogFragmentNewInstance = SimpleDialogFragment.newInstance(str, str2, i2, i, null);
        simpleDialogFragmentNewInstance.setNegativeButton(i, onClickListener);
        simpleDialogFragmentNewInstance.setPositiveButton(i2, onClickListener2);
        simpleDialogFragmentNewInstance.setOnCancelListener(onCancelListener);
        simpleDialogFragmentNewInstance.setOnDismissListener(onDismissListener);
        simpleDialogFragmentNewInstance.show(fragmentManager, (String) null);
        return simpleDialogFragmentNewInstance;
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog == null || !dialog.isShowing()) {
            return;
        }
        dialog.dismiss();
    }

    public static void dismissDialogFragment(DialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss();
        }
    }
}
