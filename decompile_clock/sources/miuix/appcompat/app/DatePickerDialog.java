package miuix.appcompat.app;

import android.R;
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
import com.android.deskclock.R2;
import miuix.pickerwidget.date.Calendar;
import miuix.pickerwidget.date.DateUtils;
import miuix.pickerwidget.widget.DatePicker;
import miuix.slidingwidget.widget.SlidingButton;

/* JADX INFO: loaded from: classes2.dex */
public class DatePickerDialog extends AlertDialog {
    private final Calendar mCalendar;
    private final OnDateSetListener mCallBack;
    private final DatePicker mDatePicker;
    private View mLunarModePanel;
    private SlidingButton mLunarModeState;
    private DatePicker.OnDateChangedListener mOnDateChangedListener;
    private boolean mTitleNeedsUpdate;

    public interface OnDateSetListener {
        void onDateSet(DatePicker datePicker, int i, int i2, int i3);
    }

    public DatePickerDialog(Context context, OnDateSetListener onDateSetListener, int i, int i2, int i3) {
        this(context, 0, onDateSetListener, i, i2, i3);
    }

    public DatePickerDialog(Context context, int i, OnDateSetListener onDateSetListener, int i2, int i3, int i4) {
        super(context, i);
        this.mTitleNeedsUpdate = true;
        this.mOnDateChangedListener = new DatePicker.OnDateChangedListener() { // from class: miuix.appcompat.app.DatePickerDialog.1
            @Override // miuix.pickerwidget.widget.DatePicker.OnDateChangedListener
            public void onDateChanged(DatePicker datePicker, int i5, int i6, int i7, boolean z) {
                if (DatePickerDialog.this.mTitleNeedsUpdate) {
                    DatePickerDialog.this.updateTitle(i5, i6, i7);
                }
            }
        };
        this.mCallBack = onDateSetListener;
        this.mCalendar = new Calendar();
        Context context2 = getContext();
        setButton(-1, context2.getText(R.string.ok), new DialogInterface.OnClickListener() { // from class: miuix.appcompat.app.DatePickerDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i5) {
                DatePickerDialog.this.tryNotifyDateSet();
            }
        });
        setButton(-2, getContext().getText(R.string.cancel), (DialogInterface.OnClickListener) null);
        setIcon(0);
        View viewInflate = ((LayoutInflater) context2.getSystemService("layout_inflater")).inflate(miuix.appcompat.R.layout.miuix_appcompat_date_picker_dialog, (ViewGroup) null);
        setView(viewInflate);
        DatePicker datePicker = (DatePicker) viewInflate.findViewById(miuix.appcompat.R.id.datePicker);
        this.mDatePicker = datePicker;
        datePicker.init(i2, i3, i4, this.mOnDateChangedListener);
        updateTitle(i2, i3, i4);
        this.mLunarModePanel = viewInflate.findViewById(miuix.appcompat.R.id.lunarModePanel);
        SlidingButton slidingButton = (SlidingButton) viewInflate.findViewById(miuix.appcompat.R.id.datePickerLunar);
        this.mLunarModeState = slidingButton;
        slidingButton.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: miuix.appcompat.app.DatePickerDialog$$ExternalSyntheticLambda0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.m1808lambda$new$0$miuixappcompatappDatePickerDialog(compoundButton, z);
            }
        });
        ViewCompat.setAccessibilityDelegate(this.mLunarModePanel, new AccessibilityDelegateCompat() { // from class: miuix.appcompat.app.DatePickerDialog.3
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setClickable(true);
                accessibilityNodeInfoCompat.setClassName(Switch.class.getName());
                if (DatePickerDialog.this.mLunarModeState != null) {
                    accessibilityNodeInfoCompat.setChecked(DatePickerDialog.this.mLunarModeState.isChecked());
                    accessibilityNodeInfoCompat.setContentDescription(DatePickerDialog.this.mLunarModeState.getContentDescription());
                }
            }
        });
        ViewCompat.replaceAccessibilityAction(this.mLunarModePanel, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK, "", new AccessibilityViewCommand() { // from class: miuix.appcompat.app.DatePickerDialog$$ExternalSyntheticLambda1
            @Override // androidx.core.view.accessibility.AccessibilityViewCommand
            public final boolean perform(View view, AccessibilityViewCommand.CommandArguments commandArguments) {
                return this.f$0.m1809lambda$new$1$miuixappcompatappDatePickerDialog(view, commandArguments);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$new$0$miuix-appcompat-app-DatePickerDialog, reason: not valid java name */
    /* synthetic */ void m1808lambda$new$0$miuixappcompatappDatePickerDialog(CompoundButton compoundButton, boolean z) {
        this.mDatePicker.setLunarMode(z);
    }

    /* JADX INFO: renamed from: lambda$new$1$miuix-appcompat-app-DatePickerDialog, reason: not valid java name */
    /* synthetic */ boolean m1809lambda$new$1$miuixappcompatappDatePickerDialog(View view, AccessibilityViewCommand.CommandArguments commandArguments) {
        SlidingButton slidingButton = this.mLunarModeState;
        if (slidingButton != null) {
            slidingButton.performClick();
        }
        view.sendAccessibilityEvent(1);
        return true;
    }

    public void setLunarMode(boolean z) {
        this.mLunarModePanel.setVisibility(z ? 0 : 8);
    }

    public void switchLunarState(boolean z) {
        this.mLunarModeState.setChecked(z);
        this.mDatePicker.setLunarMode(z);
    }

    public DatePicker getDatePicker() {
        return this.mDatePicker;
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void setTitle(CharSequence charSequence) {
        super.setTitle(charSequence);
        this.mTitleNeedsUpdate = false;
    }

    @Override // androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void setTitle(int i) {
        super.setTitle(i);
        this.mTitleNeedsUpdate = false;
    }

    public void updateDate(int i, int i2, int i3) {
        this.mDatePicker.updateDate(i, i2, i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryNotifyDateSet() {
        if (this.mCallBack != null) {
            this.mDatePicker.clearFocus();
            OnDateSetListener onDateSetListener = this.mCallBack;
            DatePicker datePicker = this.mDatePicker;
            onDateSetListener.onDateSet(datePicker, datePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTitle(int i, int i2, int i3) {
        this.mCalendar.set(1, i);
        this.mCalendar.set(5, i2);
        this.mCalendar.set(9, i3);
        super.setTitle(DateUtils.formatDateTime(getContext(), this.mCalendar.getTimeInMillis(), R2.styleable.ChipGroup_chipSpacingHorizontal));
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        DatePicker datePicker = this.mDatePicker;
        if (datePicker != null) {
            updateTitle(datePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
        }
    }
}
