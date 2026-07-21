package com.android.deskclock.alarm;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.deskclock.R;
import miuix.appcompat.app.AlertDialog;
import miuix.pickerwidget.widget.NumberPicker;

/* JADX INFO: loaded from: classes.dex */
public class DurationPickerDialog extends AlertDialog {
    private final NumberPicker.OnValueChangeListener mCallback;
    private int mCurrentDuration;
    private final NumberPicker mNumberPicker;
    private int mOriginalDuration;

    public DurationPickerDialog(Context context, NumberPicker.OnValueChangeListener onValueChangeListener) {
        this(context, 0, onValueChangeListener);
    }

    private DurationPickerDialog(Context context, int i, NumberPicker.OnValueChangeListener onValueChangeListener) {
        super(context, i);
        this.mCallback = onValueChangeListener;
        setIcon(0);
        setTitle(context.getString(R.string.shift_alarm_duration));
        Context context2 = getContext();
        setButton(-1, context2.getText(android.R.string.ok), new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.DurationPickerDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                if (DurationPickerDialog.this.mCallback != null) {
                    DurationPickerDialog.this.mCallback.onValueChange(DurationPickerDialog.this.mNumberPicker, DurationPickerDialog.this.mOriginalDuration, DurationPickerDialog.this.mCurrentDuration);
                }
            }
        });
        setButton(-2, getContext().getText(android.R.string.cancel), (DialogInterface.OnClickListener) null);
        View viewInflate = ((LayoutInflater) context2.getSystemService("layout_inflater")).inflate(R.layout.duration_picker_dialog, (ViewGroup) null);
        setView(viewInflate);
        NumberPicker numberPicker = (NumberPicker) viewInflate.findViewById(R.id.number_picker);
        this.mNumberPicker = numberPicker;
        numberPicker.setMinValue(2);
        numberPicker.setMaxValue(62);
        numberPicker.setOnLongPressUpdateInterval(100L);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.alarm.DurationPickerDialog.2
            @Override // miuix.pickerwidget.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker2, int i2, int i3) {
                DurationPickerDialog.this.mCurrentDuration = i3;
            }
        });
    }

    public void setDurationValue(int i) {
        this.mNumberPicker.setValue(i);
        this.mOriginalDuration = i;
        this.mCurrentDuration = i;
    }

    @Override // androidx.activity.ComponentDialog, android.app.Dialog
    public Bundle onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
    }
}
