package miuix.appcompat.app;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import miuix.appcompat.R;
import miuix.pickerwidget.widget.DateTimePicker;
import miuix.slidingwidget.widget.SlidingButton;

/* JADX INFO: loaded from: classes2.dex */
public class DateTimePickerDialog extends AlertDialog {
    private View mLunarModePanel;
    private SlidingButton mLunarModeState;
    private OnTimeSetListener mTimeListener;
    private DateTimePicker mTimePicker;

    public interface OnTimeSetListener {
        void onTimeSet(DateTimePickerDialog dateTimePickerDialog, long j);
    }

    public DateTimePickerDialog(Context context, OnTimeSetListener onTimeSetListener) {
        this(context, onTimeSetListener, 1);
    }

    public DateTimePickerDialog(Context context, OnTimeSetListener onTimeSetListener, int i) {
        super(context);
        this.mTimeListener = onTimeSetListener;
        init(i);
        setTitle(R.string.date_time_picker_dialog_title);
    }

    private void init(int i) {
        setButton(-1, getContext().getText(android.R.string.ok), new DialogInterface.OnClickListener() { // from class: miuix.appcompat.app.DateTimePickerDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                if (DateTimePickerDialog.this.mTimeListener != null) {
                    OnTimeSetListener onTimeSetListener = DateTimePickerDialog.this.mTimeListener;
                    DateTimePickerDialog dateTimePickerDialog = DateTimePickerDialog.this;
                    onTimeSetListener.onTimeSet(dateTimePickerDialog, dateTimePickerDialog.mTimePicker.getTimeInMillis());
                }
            }
        });
        setButton(-2, getContext().getText(android.R.string.cancel), (DialogInterface.OnClickListener) null);
        View viewInflate = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.miuix_appcompat_datetime_picker_dialog, (ViewGroup) null);
        setView(viewInflate);
        DateTimePicker dateTimePicker = (DateTimePicker) viewInflate.findViewById(R.id.dateTimePicker);
        this.mTimePicker = dateTimePicker;
        dateTimePicker.setMinuteInterval(i);
        View viewFindViewById = viewInflate.findViewById(R.id.lunarModePanel);
        this.mLunarModePanel = viewFindViewById;
        ViewCompat.setAccessibilityDelegate(viewFindViewById, new AccessibilityDelegateCompat() { // from class: miuix.appcompat.app.DateTimePickerDialog.2
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setClickable(true);
                accessibilityNodeInfoCompat.setClassName(Switch.class.getName());
                if (DateTimePickerDialog.this.mLunarModeState != null) {
                    accessibilityNodeInfoCompat.setChecked(DateTimePickerDialog.this.mLunarModeState.isChecked());
                    accessibilityNodeInfoCompat.setContentDescription(DateTimePickerDialog.this.mLunarModeState.getContentDescription());
                }
            }
        });
        ViewCompat.replaceAccessibilityAction(this.mLunarModePanel, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK, "", new AccessibilityViewCommand() { // from class: miuix.appcompat.app.DateTimePickerDialog$$ExternalSyntheticLambda0
            @Override // androidx.core.view.accessibility.AccessibilityViewCommand
            public final boolean perform(View view, AccessibilityViewCommand.CommandArguments commandArguments) {
                return this.f$0.m1810lambda$init$0$miuixappcompatappDateTimePickerDialog(view, commandArguments);
            }
        });
        SlidingButton slidingButton = (SlidingButton) viewInflate.findViewById(R.id.datePickerLunar);
        this.mLunarModeState = slidingButton;
        slidingButton.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: miuix.appcompat.app.DateTimePickerDialog$$ExternalSyntheticLambda1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.m1811lambda$init$1$miuixappcompatappDateTimePickerDialog(compoundButton, z);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$init$0$miuix-appcompat-app-DateTimePickerDialog, reason: not valid java name */
    /* synthetic */ boolean m1810lambda$init$0$miuixappcompatappDateTimePickerDialog(View view, AccessibilityViewCommand.CommandArguments commandArguments) {
        SlidingButton slidingButton = this.mLunarModeState;
        if (slidingButton != null) {
            slidingButton.performClick();
        }
        view.sendAccessibilityEvent(1);
        return true;
    }

    /* JADX INFO: renamed from: lambda$init$1$miuix-appcompat-app-DateTimePickerDialog, reason: not valid java name */
    /* synthetic */ void m1811lambda$init$1$miuixappcompatappDateTimePickerDialog(CompoundButton compoundButton, boolean z) {
        this.mTimePicker.setLunarMode(z);
    }

    public void setLunarMode(boolean z) {
        this.mLunarModePanel.setVisibility(z ? 0 : 8);
    }

    public void switchLunarState(boolean z) {
        this.mLunarModeState.setChecked(z);
        this.mTimePicker.setLunarMode(z);
    }

    public void update(long j) {
        this.mTimePicker.update(j);
    }

    public void setMinDateTime(long j) {
        this.mTimePicker.setMinDateTime(j);
    }

    public void setMaxDateTime(long j) {
        this.mTimePicker.setMaxDateTime(j);
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
    }
}
