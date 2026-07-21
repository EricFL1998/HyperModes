package com.android.deskclock.alarm;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.widget.TimePicker;
import java.util.Calendar;
import java.util.Locale;
import miuix.core.util.MiuixUIUtils;

/* JADX INFO: loaded from: classes.dex */
public class AlarmEditDialog extends DialogFragment implements TimePicker.OnTimeChangedListener {
    public static final String ALARM_INTENT_EXTRA_CHANGED = "intent.extra.alarm.changed";
    private static final float ALPHA_SCREEN = 0.35f;
    public static final String EXTRA_ALARM = "alarm";
    public static final String EXTRA_CALENDER = "calender";
    public static final String TAG = "DC:AlarmEditDialog";
    private static final int TIME_PICKER_COUNT = 5;
    private Activity mActivity;
    private Alarm mAlarm;
    private TextView mAlarmInFutureView;
    private Calendar mCalender;
    private View mContentView;
    private View mDialogView;
    private OnDismissListener mDismissListener = null;
    private boolean mEnableAlarm;
    private int mHour;
    private int mMinute;
    private Button mMoreBtn;
    private OnMoreClickListener mOnMoreClickListener;
    private OnSaveAlarmListener mOnSaveAlarmListener;
    private Alarm mOriginalAlarm;
    private Button mSaveBtn;
    private TimePicker mTimePicker;

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnMoreClickListener {
        void onMoreClick(Alarm alarm, Alarm alarm2);
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
        this.mAlarm = alarm.m78clone();
        this.mCalender = (Calendar) arguments.getSerializable("calender");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Window window = getDialog().getWindow();
        this.mContentView = LayoutInflater.from(getActivity()).inflate(R.layout.drop_down_popup_window_alarm_clock, (ViewGroup) window.findViewById(android.R.id.content), false);
        window.setLayout(getDimenValue(R.dimen.drop_down_popup_window_width), -2);
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
        this.mDialogView = this.mContentView.findViewById(R.id.dialog_holder);
        if (Locale.getDefault().getLanguage().contains("bo") && MiuixUIUtils.getFontLevel(DeskClockApp.getAppDEContext()) == 2) {
            ViewGroup.LayoutParams layoutParams = this.mDialogView.getLayoutParams();
            layoutParams.height = (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.drop_down_popup_window_height_large);
            this.mDialogView.setLayoutParams(layoutParams);
        }
        this.mAlarmInFutureView = (TextView) this.mContentView.findViewById(R.id.alarm_in_future_time);
        TimePicker timePicker = (TimePicker) this.mContentView.findViewById(R.id.time_picker);
        this.mTimePicker = timePicker;
        timePicker.setSelectorIndicesCount(5);
        this.mTimePicker.setOnTimeChangedListener(this);
        Button button = (Button) this.mContentView.findViewById(R.id.alarm_more_setting);
        this.mMoreBtn = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmEditDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AlarmEditDialog.this.dismissAllowingStateLoss();
                if (AlarmEditDialog.this.mOnMoreClickListener != null) {
                    AlarmEditDialog.this.mOnMoreClickListener.onMoreClick(AlarmEditDialog.this.mOriginalAlarm, AlarmEditDialog.this.mAlarm);
                }
                StatHelper.alarmEvent(StatHelper.EVENT_CLICK_TIME_PICKER_MORE_SETTING);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_ITEM_DIALOG_MORE_CLICK);
            }
        });
        handleMoreBtnSize();
        Button button2 = (Button) this.mContentView.findViewById(R.id.set_alarm_done);
        this.mSaveBtn = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmEditDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AlarmEditDialog.this.dismissAllowingStateLoss();
                if (AlarmEditDialog.this.isModified()) {
                    AlarmEditDialog.this.mOriginalAlarm.hour = AlarmEditDialog.this.mHour;
                    AlarmEditDialog.this.mOriginalAlarm.minutes = AlarmEditDialog.this.mMinute;
                    AlarmEditDialog.this.mOriginalAlarm.enabled = AlarmEditDialog.this.mEnableAlarm;
                    if (AlarmEditDialog.this.mOnSaveAlarmListener != null) {
                        AlarmEditDialog.this.mOnSaveAlarmListener.onSaveAlarm(AlarmEditDialog.this.mOriginalAlarm);
                        StatHelper.trackEvent(StatHelper.KEY_SET_ALARM_TIME, TimeUtil.composeTime(AlarmEditDialog.this.mHour, AlarmEditDialog.this.mMinute));
                        OneTrackStatHelper.trackNumEvent((AlarmEditDialog.this.mHour * 60) + AlarmEditDialog.this.mMinute, "");
                    }
                }
                StatHelper.alarmEvent(StatHelper.EVENT_CLICK_TIME_PICKER_FINISH);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_ITEM_DIALOG_DONE_CLICK);
            }
        });
        updateUI();
    }

    public void updateUI() {
        showTimePicker();
        if (!this.mOriginalAlarm.enabled && 0 < this.mOriginalAlarm.skipTime && this.mOriginalAlarm.skipTime < System.currentTimeMillis()) {
            AlarmHelper.enableAlarm(this.mActivity, this.mOriginalAlarm.id, true);
            this.mOriginalAlarm.enabled = true;
            this.mOriginalAlarm.skipTime = 0L;
        }
        this.mHour = this.mOriginalAlarm.hour;
        this.mMinute = this.mOriginalAlarm.minutes;
        this.mEnableAlarm = this.mOriginalAlarm.enabled;
        updateFutureView(this.mOriginalAlarm);
    }

    private int getDimenValue(int i) {
        return (int) this.mActivity.getResources().getDimension(i);
    }

    private void showTimePicker() {
        this.mTimePicker.setIs24HourView(Boolean.valueOf(DateFormat.is24HourFormat(this.mActivity)));
        this.mTimePicker.setCurrentHour(Integer.valueOf(this.mOriginalAlarm.hour));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(this.mOriginalAlarm.minutes));
        if (this.mTimePicker.is24HourView()) {
            this.mTimePicker.setPadding((int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_24), 0, (int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_24), 0);
        } else {
            this.mTimePicker.setPadding((int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_12), 0, (int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_12), 0);
        }
    }

    @Override // com.android.deskclock.widget.TimePicker.OnTimeChangedListener
    public void onTimeChanged(TimePicker timePicker, int i, int i2) {
        this.mHour = i;
        this.mMinute = i2;
        this.mEnableAlarm = true;
        this.mAlarm.hour = i;
        this.mAlarm.minutes = this.mMinute;
        updateFutureView(this.mAlarm);
    }

    private void updateFutureView(Alarm alarm) {
        this.mAlarmInFutureView.setText(SetAlarmController.getAlarmInFuture(this.mCalender, this.mActivity, alarm.hour, alarm.minutes, alarm.daysOfWeek));
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

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        this.mOnMoreClickListener = onMoreClickListener;
    }

    private void handleMoreBtnSize() {
        Activity activity = this.mActivity;
        if (Util.getTypefaceTextViewWidth(activity, activity.getResources().getString(R.string.alarm_clock_more_setting), (int) this.mActivity.getResources().getDimension(R.dimen.drop_down_popup_window_more_setting_size), null) > this.mActivity.getResources().getDimension(R.dimen.dialog_button_width) - 40.0f) {
            this.mMoreBtn.setTextSize(0, this.mActivity.getResources().getDimension(R.dimen.drop_down_popup_window_more_setting_size_small));
        }
    }
}
