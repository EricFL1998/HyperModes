package com.android.deskclock.alarm.bedtime;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.android.deskclock.Alarm;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.Log;
import com.android.deskclock.widget.SleepTimePicker;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class BedtimeAlarmEditDialog extends DialogFragment implements SleepTimePicker.OnTimeChangedListener {
    private static final float ALPHA_SCREEN = 0.35f;
    public static final String EXTRA_ALARM = "alarm";
    public static final String EXTRA_CALENDER = "calender";
    public static final String EXTRA_INDEX = "index";
    public static final String EXTRA_INVALID_HOUR = "invalid_hour";
    public static final String EXTRA_INVALID_MIN = "invalid_min";
    private static final int SLEEP_ALARM_INDEX = 0;
    public static final String TAG = "DC:BedtimeAlarmEditDialog";
    private static final int TIME_PICKER_COUNT = 5;
    private Activity mActivity;
    private Alarm mAlarm;
    private Button mCancelBtn;
    private View mContentView;
    private OnDismissListener mDismissListener = null;
    private boolean mEnableAlarm;
    private int mHour;
    private int mIndex;
    private int mInvalidHour;
    private int mInvalidMin;
    private int mMinute;
    private OnSaveAlarmListener mOnSaveAlarmListener;
    private Alarm mOriginalAlarm;
    private Button mSaveBtn;
    private SleepTimePicker mTimePicker;
    private TextView mTitle;

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnSaveAlarmListener {
        void onSaveAlarm(Alarm alarm);
    }

    @Override // androidx.fragment.app.DialogFragment
    public void show(FragmentManager fragmentManager, String str) {
        try {
            super.show(fragmentManager, str);
        } catch (IllegalStateException e) {
            Log.e(TAG, "showAllowingStateLoss error: " + e.getMessage());
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(2, R.style.BaseDialog);
        this.mActivity = getActivity();
        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalStateException("no argument");
        }
        Alarm alarm = (Alarm) arguments.getParcelable("alarm");
        this.mOriginalAlarm = alarm;
        if (alarm == null) {
            this.mOriginalAlarm = new Alarm();
        }
        this.mAlarm = this.mOriginalAlarm.m78clone();
        this.mIndex = arguments.getInt("index");
        this.mInvalidHour = arguments.getInt("invalid_hour");
        this.mInvalidMin = arguments.getInt("invalid_min");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Window window = getDialog().getWindow();
        this.mContentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_bedtime_alarm_clock, (ViewGroup) window.findViewById(android.R.id.content), false);
        window.setLayout(getDimenValue(R.dimen.drop_down_popup_window_width), getDimenValue(R.dimen.bedtime_popup_window_height));
        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setWindowAnimations(R.style.dialogWindowAnim);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.dimAmount = 0.35f;
        attributes.gravity = 17;
        window.setAttributes(attributes);
        initView();
        return this.mContentView;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        OnDismissListener onDismissListener = this.mDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    private void initView() {
        TextView textView = (TextView) this.mContentView.findViewById(R.id.bedtime_alarm_title);
        this.mTitle = textView;
        if (this.mIndex == 0) {
            textView.setText(R.string.sleep_time);
        } else {
            textView.setText(R.string.wake_up_time);
        }
        if (MiuiSdk.isSupportFontAnim()) {
            MiuiFont.setFont(this.mTitle, MiuiFont.MI_PRO_MEDIUM);
        }
        SleepTimePicker sleepTimePicker = (SleepTimePicker) this.mContentView.findViewById(R.id.time_picker);
        this.mTimePicker = sleepTimePicker;
        sleepTimePicker.setSelectorIndicesCount(5);
        this.mTimePicker.setOnTimeChangedListener(this);
        Button button = (Button) this.mContentView.findViewById(R.id.cancel_btn);
        this.mCancelBtn = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeAlarmEditDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BedtimeAlarmEditDialog.this.dismissAllowingStateLoss();
            }
        });
        Button button2 = (Button) this.mContentView.findViewById(R.id.done_btn);
        this.mSaveBtn = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeAlarmEditDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BedtimeAlarmEditDialog.this.dismissAllowingStateLoss();
                if (BedtimeAlarmEditDialog.this.isModified()) {
                    BedtimeAlarmEditDialog.this.mOriginalAlarm.hour = BedtimeAlarmEditDialog.this.mHour;
                    BedtimeAlarmEditDialog.this.mOriginalAlarm.minutes = BedtimeAlarmEditDialog.this.mMinute;
                    BedtimeAlarmEditDialog.this.mOriginalAlarm.enabled = BedtimeAlarmEditDialog.this.mEnableAlarm;
                    if (BedtimeAlarmEditDialog.this.mOnSaveAlarmListener != null) {
                        BedtimeAlarmEditDialog.this.mOnSaveAlarmListener.onSaveAlarm(BedtimeAlarmEditDialog.this.mOriginalAlarm);
                    }
                }
            }
        });
        updateUI();
    }

    public void updateUI() {
        showTimePicker();
        this.mHour = this.mOriginalAlarm.hour;
        this.mMinute = this.mOriginalAlarm.minutes;
        this.mEnableAlarm = this.mOriginalAlarm.enabled;
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, this.mOriginalAlarm.hour);
        calendar.set(12, this.mOriginalAlarm.minutes);
    }

    private int getDimenValue(int i) {
        return (int) this.mActivity.getResources().getDimension(i);
    }

    private void showTimePicker() {
        this.mTimePicker.setIs24HourView(true);
        this.mTimePicker.setCurrentHour(Integer.valueOf(this.mOriginalAlarm.hour));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(this.mOriginalAlarm.minutes / 5));
        if (this.mTimePicker.is24HourView()) {
            this.mTimePicker.setPadding((int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_24), 0, (int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_24), 0);
        } else {
            this.mTimePicker.setPadding((int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_12), 0, (int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_12), 0);
        }
    }

    @Override // com.android.deskclock.widget.SleepTimePicker.OnTimeChangedListener
    public void onTimeChanged(SleepTimePicker sleepTimePicker, int i, int i2) {
        this.mHour = i;
        this.mMinute = i2 * 5;
        this.mAlarm.hour = i;
        this.mAlarm.minutes = this.mMinute;
        this.mEnableAlarm = true;
        if (this.mAlarm.hour == this.mInvalidHour && this.mAlarm.minutes == this.mInvalidMin) {
            this.mSaveBtn.setEnabled(false);
            this.mSaveBtn.setAlpha(0.5f);
        } else {
            this.mSaveBtn.setEnabled(true);
            this.mSaveBtn.setAlpha(1.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isModified() {
        return (this.mOriginalAlarm.hour == this.mHour && this.mOriginalAlarm.minutes == this.mMinute && this.mOriginalAlarm.enabled == this.mEnableAlarm) ? false : true;
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mDismissListener = onDismissListener;
    }

    public void setOnSaveAlarmListener(OnSaveAlarmListener onSaveAlarmListener) {
        this.mOnSaveAlarmListener = onSaveAlarmListener;
    }
}
