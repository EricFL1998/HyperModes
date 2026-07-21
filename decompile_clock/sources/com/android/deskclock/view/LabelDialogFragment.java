package com.android.deskclock.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.R;
import com.android.deskclock.util.Log;
import miuix.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes.dex */
public class LabelDialogFragment extends DialogFragment {
    private AlertDialog mAlertDialog;
    private EditText mEditText;
    private NegativeObserver mNegativeObserver;
    private String mOldLabel;
    private PositiveObserver mPositiveObserver;
    private LinearLayout mRootView;

    public interface NegativeObserver {
        void onNegativeClick();
    }

    public interface PositiveObserver {
        void onPositiveClick(String str);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.label_dialog_fragment, (ViewGroup) null);
        this.mRootView = linearLayout;
        this.mEditText = (EditText) linearLayout.findViewById(R.id.alarm_label);
    }

    public void setOldLabel(String str) {
        this.mOldLabel = str;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder view = new AlertDialog.Builder(getActivity(), R.style.inputDialog).setTitle(R.string.new_label).setView(this.mRootView);
        if (!TextUtils.isEmpty(this.mOldLabel)) {
            view.setTitle(R.string.edit_label);
            this.mEditText.setText(this.mOldLabel);
            if (this.mEditText.getText() != null) {
                try {
                    EditText editText = this.mEditText;
                    editText.setSelection(editText.getText().length());
                } catch (IndexOutOfBoundsException e) {
                    Log.e(Log.TAG, "索引越界：", e);
                }
            }
        }
        view.setPositiveButton(R.string.set_now, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.view.LabelDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (LabelDialogFragment.this.mPositiveObserver != null && LabelDialogFragment.this.mEditText != null) {
                    LabelDialogFragment labelDialogFragment = LabelDialogFragment.this;
                    LabelDialogFragment.this.mPositiveObserver.onPositiveClick(labelDialogFragment.replaceNull(labelDialogFragment.mEditText.getText().toString()).trim());
                }
                LabelDialogFragment.this.dismissDialog();
            }
        });
        view.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.view.LabelDialogFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (LabelDialogFragment.this.mNegativeObserver != null) {
                    LabelDialogFragment.this.mNegativeObserver.onNegativeClick();
                }
                LabelDialogFragment.this.dismissDialog();
            }
        });
        view.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.android.deskclock.view.LabelDialogFragment.3
            @Override // android.content.DialogInterface.OnShowListener
            public void onShow(DialogInterface dialogInterface) {
                LabelDialogFragment labelDialogFragment = LabelDialogFragment.this;
                labelDialogFragment.showSoftInput(labelDialogFragment.mEditText);
            }
        });
        AlertDialog alertDialogCreate = view.create();
        this.mAlertDialog = alertDialogCreate;
        alertDialogCreate.setCanceledOnTouchOutside(true);
        return this.mAlertDialog;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSoftInput(final View view) {
        view.postDelayed(new Runnable() { // from class: com.android.deskclock.view.LabelDialogFragment$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LabelDialogFragment.lambda$showSoftInput$0(view);
            }
        }, 100L);
    }

    static /* synthetic */ void lambda$showSoftInput$0(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getApplicationContext().getSystemService("input_method");
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    public void dismissDialog() {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mAlertDialog = null;
        }
        this.mNegativeObserver = null;
        this.mPositiveObserver = null;
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
    }

    public static LabelDialogFragment newInstance() {
        return new LabelDialogFragment();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void show(FragmentManager fragmentManager, String str) {
        LabelDialogFragment labelDialogFragment = (LabelDialogFragment) fragmentManager.findFragmentByTag(str);
        FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
        if (labelDialogFragment != null) {
            labelDialogFragment.dismissAllowingStateLoss();
        }
        fragmentTransactionBeginTransaction.add(this, str);
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
    }

    public void setPositiveObserver(PositiveObserver positiveObserver) {
        this.mPositiveObserver = positiveObserver;
    }

    public void setNegativeObserver(NegativeObserver negativeObserver) {
        this.mNegativeObserver = negativeObserver;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String replaceNull(String str) {
        return str == null ? "" : str;
    }
}
