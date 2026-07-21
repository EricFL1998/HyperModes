package com.android.deskclock.view;

import android.R;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.stat.StatHelper;
import miuix.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes.dex */
public class SimpleDialogFragment extends DialogFragment {
    public static final String ARG_MESSAGE = "msg_res_id";
    public static final String ARG_TITLE = "title";
    public static final String CHECKBOX_CHECKED = "checkbox_checked";
    public static final String CHECKBOX_KEY = "checkbox_key";
    public static final String CHECKBOX_MSG = "checkbox_msg";
    public static final String ITEMS = "items";
    public static final String NEGATIVE_WORDS = "negative_res";
    public static final String NEUTRAL_WORDS = "neutral_res";
    public static final String POSITIVE_WORDS = "positive_res";
    private static final String SET_CANCELABLE = "set_cancelable";
    private static final String SET_MOVEMENT_METHOD_DIALOG = "set_movement_method";
    private AlertDialog mAlertDialog;
    private boolean mCancelable;
    private String[] mItems;
    private DialogInterface.OnClickListener mItemsClickListener;
    private CharSequence mMessage;
    private DialogInterface.OnClickListener mNegativeButtonClickListener;
    private int mNegativeButtonTextRes;
    private DialogInterface.OnClickListener mNeutralButtonClickListener;
    private int mNeutralButtonTextRes;
    private DialogInterface.OnCancelListener mOnCancelListener;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private DialogInterface.OnClickListener mPositiveButtonClickListener;
    private int mPositiveButtonTextRes;
    private String mTitle;
    private boolean mIsCheckBoxChecked = false;
    private String mCheckBoxMsg = null;
    private String mCheckBoxKey = null;
    private boolean isPositiveClick = false;

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalStateException("no argument");
        }
        this.mMessage = arguments.getCharSequence(ARG_MESSAGE);
        this.mTitle = arguments.getString("title");
        this.mItems = arguments.getStringArray(ITEMS);
        int i = arguments.getInt(NEGATIVE_WORDS);
        this.mNegativeButtonTextRes = i;
        if (i == 0) {
            this.mNegativeButtonTextRes = R.string.cancel;
        }
        this.mPositiveButtonTextRes = arguments.getInt(POSITIVE_WORDS);
        int i2 = arguments.getInt(NEUTRAL_WORDS);
        this.mNeutralButtonTextRes = i2;
        if (i2 > 0) {
            this.mNegativeButtonTextRes = 0;
            this.mPositiveButtonTextRes = 0;
        }
        this.mIsCheckBoxChecked = arguments.getBoolean(CHECKBOX_CHECKED, false);
        this.mCheckBoxMsg = arguments.getString(CHECKBOX_MSG, null);
        this.mCheckBoxKey = arguments.getString(CHECKBOX_KEY, null);
        this.mCancelable = arguments.getBoolean(SET_CANCELABLE, true);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder title;
        String str = this.mTitle;
        if (str != null && !str.isEmpty() && this.mTitle.equals(getString(com.android.deskclock.R.string.notification_permission_dialog_title))) {
            title = new AlertDialog.Builder(getActivity(), com.android.deskclock.R.style.NotificationPermissionDialogStyle).setMessage(this.mMessage).setTitle(this.mTitle);
        } else {
            title = new AlertDialog.Builder(getActivity()).setMessage(this.mMessage).setTitle(this.mTitle);
        }
        String[] strArr = this.mItems;
        if (strArr != null) {
            title.setItems(strArr, this.mItemsClickListener);
        }
        int i = this.mPositiveButtonTextRes;
        if (i > 0) {
            title.setPositiveButton(i, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.view.SimpleDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    SimpleDialogFragment.this.isPositiveClick = true;
                    if (SimpleDialogFragment.this.mPositiveButtonClickListener != null) {
                        SimpleDialogFragment.this.mPositiveButtonClickListener.onClick(dialogInterface, i2);
                    }
                }
            });
        }
        int i2 = this.mNegativeButtonTextRes;
        if (i2 > 0) {
            title.setNegativeButton(i2, this.mNegativeButtonClickListener);
        }
        int i3 = this.mNeutralButtonTextRes;
        if (i3 > 0) {
            title.setNeutralButton(i3, this.mNeutralButtonClickListener);
        }
        if (!TextUtils.isEmpty(this.mCheckBoxMsg)) {
            title.setCheckBox(this.mIsCheckBoxChecked, this.mCheckBoxMsg);
        }
        AlertDialog alertDialogCreate = title.create();
        this.mAlertDialog = alertDialogCreate;
        alertDialogCreate.setCanceledOnTouchOutside(this.mCancelable);
        if (!this.mCancelable) {
            this.mAlertDialog.setCancelable(false);
            this.mAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.android.deskclock.view.SimpleDialogFragment.2
                @Override // android.content.DialogInterface.OnKeyListener
                public boolean onKey(DialogInterface dialogInterface, int i4, KeyEvent keyEvent) {
                    return i4 == 4;
                }
            });
        }
        return this.mAlertDialog;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (getArguments().getBoolean(SET_MOVEMENT_METHOD_DIALOG, false)) {
            ((TextView) getDialog().findViewById(com.android.deskclock.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        AlertDialog alertDialog;
        if (!TextUtils.isEmpty(this.mCheckBoxKey) && (alertDialog = this.mAlertDialog) != null && !this.isPositiveClick) {
            try {
                boolean z = !alertDialog.isChecked();
                FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putBoolean(this.mCheckBoxKey, z).apply();
                StatHelper.updateAlarmProperties(this.mCheckBoxKey, z + "");
            } catch (Exception e) {
                Log.e("SimpleDialogFragment, onDestroyView error: " + e.getMessage());
            }
        }
        AlertDialog alertDialog2 = this.mAlertDialog;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
            this.mAlertDialog = null;
        }
        this.mPositiveButtonClickListener = null;
        this.mNegativeButtonClickListener = null;
        this.mNeutralButtonClickListener = null;
        this.mOnCancelListener = null;
        this.mOnDismissListener = null;
        this.mItems = null;
        this.mItemsClickListener = null;
        super.onDestroyView();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        DialogInterface.OnCancelListener onCancelListener = this.mOnCancelListener;
        if (onCancelListener != null) {
            onCancelListener.onCancel(dialogInterface);
        }
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        DialogInterface.OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialogInterface);
        }
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.mOnCancelListener = onCancelListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public void setNegativeButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.mNegativeButtonTextRes = i;
        this.mNegativeButtonClickListener = onClickListener;
    }

    public void setPositiveButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.mPositiveButtonTextRes = i;
        this.mPositiveButtonClickListener = onClickListener;
    }

    public void setNeutralButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.mNeutralButtonTextRes = i;
        this.mNeutralButtonClickListener = onClickListener;
    }

    public void setItems(DialogInterface.OnClickListener onClickListener) {
        this.mItemsClickListener = onClickListener;
    }

    public static SimpleDialogFragment newInstance(String str, String str2, int i) {
        return newInstance(str, str2, 0, 0, i, null, false, null, null, false, true);
    }

    public static SimpleDialogFragment newInstance(String str, String str2, int i, String[] strArr) {
        return newInstance(str, str2, i, 0, strArr);
    }

    public static SimpleDialogFragment newInstance(String str, String str2, int i, int i2, String[] strArr) {
        return newInstance(str, str2, i, i2, strArr, false, null, null);
    }

    public static SimpleDialogFragment newInstance(String str, CharSequence charSequence, int i, int i2, String[] strArr, boolean z, String str2, String str3) {
        return newInstance(str, charSequence, i, i2, strArr, z, str2, str3, false);
    }

    public static SimpleDialogFragment newInstance(String str, CharSequence charSequence, int i, int i2, String[] strArr, boolean z, String str2, String str3, boolean z2) {
        return newInstance(str, charSequence, i, i2, 0, strArr, z, str2, str3, z2, true);
    }

    public static SimpleDialogFragment newInstance(String str, CharSequence charSequence, int i, int i2, int i3, String[] strArr, boolean z, String str2, String str3, boolean z2, boolean z3) {
        SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        bundle.putCharSequence(ARG_MESSAGE, charSequence);
        bundle.putStringArray(ITEMS, strArr);
        bundle.putInt(POSITIVE_WORDS, i);
        bundle.putInt(NEGATIVE_WORDS, i2);
        bundle.putInt(NEUTRAL_WORDS, i3);
        bundle.putBoolean(CHECKBOX_CHECKED, z);
        bundle.putString(CHECKBOX_MSG, str2);
        bundle.putString(CHECKBOX_KEY, str3);
        bundle.putBoolean(SET_MOVEMENT_METHOD_DIALOG, z2);
        bundle.putBoolean(SET_CANCELABLE, z3);
        simpleDialogFragment.setArguments(bundle);
        return simpleDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public void show(FragmentManager fragmentManager, String str) {
        SimpleDialogFragment simpleDialogFragment = (SimpleDialogFragment) fragmentManager.findFragmentByTag(str);
        FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
        if (simpleDialogFragment != null) {
            simpleDialogFragment.dismissAllowingStateLoss();
        }
        fragmentTransactionBeginTransaction.add(this, str);
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
    }
}
