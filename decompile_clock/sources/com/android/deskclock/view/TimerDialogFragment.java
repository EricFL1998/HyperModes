package com.android.deskclock.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.R;
import miuix.appcompat.app.AlertDialog;
import miuix.pickerwidget.widget.TimePicker;

/* JADX INFO: loaded from: classes.dex */
public class TimerDialogFragment extends DialogFragment implements TimePicker.OnTimeChangedListener {
    private AlertDialog mAlertDialog;
    private int mHour = 0;
    private int mMinute = 5;
    private NegativeObserver mNegativeObserver;
    private PositiveObserver mPositiveObserver;
    private LinearLayout mRootView;
    private TimePicker timePickerNew;

    public interface NegativeObserver {
        void onNegativeClick();
    }

    public interface PositiveObserver {
        void onPositiveClick(int i);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.timer_picker_dialog_fragment, (ViewGroup) null);
        this.mRootView = linearLayout;
        TimePicker timePicker = (TimePicker) linearLayout.findViewById(R.id.time_picker_new);
        this.timePickerNew = timePicker;
        timePicker.set24HourView(true);
        this.timePickerNew.setCurrentHour(0);
        this.timePickerNew.setCurrentMinute(5);
        this.timePickerNew.setOnTimeChangedListener(this);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder view = new AlertDialog.Builder(getActivity()).setTitle(R.string.add_timer).setView(this.mRootView);
        view.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.view.TimerDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TimerDialogFragment.this.mPositiveObserver != null) {
                    TimerDialogFragment.this.mPositiveObserver.onPositiveClick((TimerDialogFragment.this.mHour * 60) + TimerDialogFragment.this.mMinute);
                }
                TimerDialogFragment.this.dismissDialog();
            }
        });
        view.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.view.TimerDialogFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TimerDialogFragment.this.mNegativeObserver != null) {
                    TimerDialogFragment.this.mNegativeObserver.onNegativeClick();
                }
                TimerDialogFragment.this.dismissDialog();
            }
        });
        AlertDialog alertDialogCreate = view.create();
        this.mAlertDialog = alertDialogCreate;
        alertDialogCreate.setCanceledOnTouchOutside(true);
        return this.mAlertDialog;
    }

    public void dismissDialog() {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mAlertDialog = null;
        }
        this.mNegativeObserver = null;
        this.mPositiveObserver = null;
        this.timePickerNew = null;
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
    }

    public static TimerDialogFragment newInstance() {
        return new TimerDialogFragment();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void show(FragmentManager fragmentManager, String str) {
        TimerDialogFragment timerDialogFragment = (TimerDialogFragment) fragmentManager.findFragmentByTag(str);
        FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
        if (timerDialogFragment != null) {
            timerDialogFragment.dismissAllowingStateLoss();
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

    @Override // miuix.pickerwidget.widget.TimePicker.OnTimeChangedListener
    public void onTimeChanged(TimePicker timePicker, int i, int i2) {
        AlertDialog alertDialog;
        this.mHour = i;
        this.mMinute = i2;
        if (i == 0 && i2 == 0 && (alertDialog = this.mAlertDialog) != null) {
            alertDialog.getButton(-1).setEnabled(false);
            return;
        }
        AlertDialog alertDialog2 = this.mAlertDialog;
        if (alertDialog2 != null) {
            alertDialog2.getButton(-1).setEnabled(true);
        }
    }
}
