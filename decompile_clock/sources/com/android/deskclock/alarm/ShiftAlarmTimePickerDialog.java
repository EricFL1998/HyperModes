package com.android.deskclock.alarm;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.deskclock.R;
import miuix.appcompat.app.AlertDialog;
import miuix.pickerwidget.widget.TimePicker;
import miuix.slidingwidget.widget.SlidingButton;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarmTimePickerDialog extends AlertDialog {
    private static final String HOUR = "miuix:hour";
    private static final String IS_24_HOUR = "miuix:is24hour";
    private static final String MINUTE = "miuix:minute";
    private boolean mCanClose;
    boolean mEnable;
    SlidingButton mEnableSlidingButton;
    int mInitialHourOfDay;
    int mInitialMinute;
    boolean mIs24HourView;
    private OnTimeChanged mOnTimeChangedCallback;
    private final TimePicker mTimePicker;

    public interface OnTimeChanged {
        void onTimeChanged(TimePicker timePicker, int i, int i2, boolean z);
    }

    public interface OnTimeSetListener {
        void onTimeSet(TimePicker timePicker, int i, int i2);
    }

    public ShiftAlarmTimePickerDialog(Context context, OnTimeChanged onTimeChanged, int i, int i2, boolean z) {
        this(context, 0, onTimeChanged, i, i2, z);
    }

    public ShiftAlarmTimePickerDialog(Context context, int i, OnTimeChanged onTimeChanged, int i2, int i3, boolean z) {
        super(context, i);
        this.mEnable = false;
        this.mCanClose = true;
        this.mOnTimeChangedCallback = onTimeChanged;
        this.mInitialHourOfDay = i2;
        this.mInitialMinute = i3;
        this.mIs24HourView = z;
        setIcon(0);
        setTitle(R.string.time_picker_dialog_title);
        Context context2 = getContext();
        setButton(-1, context2.getText(android.R.string.ok), new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.ShiftAlarmTimePickerDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i4) {
                ShiftAlarmTimePickerDialog.this.tryNotifyTimeSet();
            }
        });
        setButton(-2, getContext().getText(android.R.string.cancel), (DialogInterface.OnClickListener) null);
        View viewInflate = ((LayoutInflater) context2.getSystemService("layout_inflater")).inflate(R.layout.shift_alarm_time_picker_dialog, (ViewGroup) null);
        setView(viewInflate);
        TimePicker timePicker = (TimePicker) viewInflate.findViewById(R.id.timePicker);
        this.mTimePicker = timePicker;
        timePicker.set24HourView(Boolean.valueOf(this.mIs24HourView));
        timePicker.setCurrentHour(Integer.valueOf(this.mInitialHourOfDay));
        timePicker.setCurrentMinute(Integer.valueOf(this.mInitialMinute));
        timePicker.setOnTimeChangedListener(null);
        ((TextView) viewInflate.findViewById(R.id.pref_title)).setText(R.string.rest_without_ringing);
        SlidingButton slidingButton = (SlidingButton) viewInflate.findViewById(R.id.pref_checkbox);
        this.mEnableSlidingButton = slidingButton;
        slidingButton.setChecked(true ^ this.mEnable);
        this.mEnableSlidingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.deskclock.alarm.ShiftAlarmTimePickerDialog.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                if (!z2 || ShiftAlarmTimePickerDialog.this.mCanClose) {
                    return;
                }
                ShiftAlarmTimePickerDialog.this.mEnableSlidingButton.setChecked(false);
                Toast.makeText(ShiftAlarmTimePickerDialog.this.getContext(), R.string.set_at_least_one_shift_reminder, 0).show();
            }
        });
    }

    public void setCanClose(boolean z) {
        this.mCanClose = z;
    }

    public void setEnable(boolean z) {
        this.mEnable = z;
        this.mEnableSlidingButton.setChecked(!z);
    }

    public void updateTime(int i, int i2) {
        this.mTimePicker.setCurrentHour(Integer.valueOf(i));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(i2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryNotifyTimeSet() {
        if (this.mOnTimeChangedCallback != null) {
            this.mTimePicker.clearFocus();
            OnTimeChanged onTimeChanged = this.mOnTimeChangedCallback;
            TimePicker timePicker = this.mTimePicker;
            onTimeChanged.onTimeChanged(timePicker, timePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue(), !this.mEnableSlidingButton.isChecked());
        }
    }

    @Override // androidx.activity.ComponentDialog, android.app.Dialog
    public Bundle onSaveInstanceState() {
        Bundle bundleOnSaveInstanceState = super.onSaveInstanceState();
        bundleOnSaveInstanceState.putInt(HOUR, this.mTimePicker.getCurrentHour().intValue());
        bundleOnSaveInstanceState.putInt(MINUTE, this.mTimePicker.getCurrentMinute().intValue());
        bundleOnSaveInstanceState.putBoolean(IS_24_HOUR, this.mTimePicker.is24HourView());
        return bundleOnSaveInstanceState;
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        int i = bundle.getInt(HOUR);
        int i2 = bundle.getInt(MINUTE);
        this.mTimePicker.set24HourView(Boolean.valueOf(bundle.getBoolean(IS_24_HOUR)));
        this.mTimePicker.setCurrentHour(Integer.valueOf(i));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(i2));
    }
}
