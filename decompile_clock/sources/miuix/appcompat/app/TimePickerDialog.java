package miuix.appcompat.app;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import miuix.appcompat.R;
import miuix.pickerwidget.widget.TimePicker;

/* JADX INFO: loaded from: classes2.dex */
public class TimePickerDialog extends AlertDialog {
    private static final String HOUR = "miuix:hour";
    private static final String IS_24_HOUR = "miuix:is24hour";
    private static final String MINUTE = "miuix:minute";
    private final OnTimeSetListener mCallback;
    int mInitialHourOfDay;
    int mInitialMinute;
    boolean mIs24HourView;
    private final TimePicker mTimePicker;

    public interface OnTimeSetListener {
        void onTimeSet(TimePicker timePicker, int i, int i2);
    }

    public TimePickerDialog(Context context, OnTimeSetListener onTimeSetListener, int i, int i2, boolean z) {
        this(context, 0, onTimeSetListener, i, i2, z);
    }

    public TimePickerDialog(Context context, int i, OnTimeSetListener onTimeSetListener, int i2, int i3, boolean z) {
        super(context, i);
        this.mCallback = onTimeSetListener;
        this.mInitialHourOfDay = i2;
        this.mInitialMinute = i3;
        this.mIs24HourView = z;
        setIcon(0);
        setTitle(R.string.time_picker_dialog_title);
        Context context2 = getContext();
        setButton(-1, context2.getText(android.R.string.ok), new DialogInterface.OnClickListener() { // from class: miuix.appcompat.app.TimePickerDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i4) {
                TimePickerDialog.this.tryNotifyTimeSet();
            }
        });
        setButton(-2, getContext().getText(android.R.string.cancel), (DialogInterface.OnClickListener) null);
        View viewInflate = ((LayoutInflater) context2.getSystemService("layout_inflater")).inflate(R.layout.miuix_appcompat_time_picker_dialog, (ViewGroup) null);
        setView(viewInflate);
        TimePicker timePicker = (TimePicker) viewInflate.findViewById(R.id.timePicker);
        this.mTimePicker = timePicker;
        timePicker.set24HourView(Boolean.valueOf(this.mIs24HourView));
        timePicker.setCurrentHour(Integer.valueOf(this.mInitialHourOfDay));
        timePicker.setCurrentMinute(Integer.valueOf(this.mInitialMinute));
        timePicker.setOnTimeChangedListener(null);
    }

    public void updateTime(int i, int i2) {
        this.mTimePicker.setCurrentHour(Integer.valueOf(i));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(i2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryNotifyTimeSet() {
        if (this.mCallback != null) {
            this.mTimePicker.clearFocus();
            OnTimeSetListener onTimeSetListener = this.mCallback;
            TimePicker timePicker = this.mTimePicker;
            onTimeSetListener.onTimeSet(timePicker, timePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
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
