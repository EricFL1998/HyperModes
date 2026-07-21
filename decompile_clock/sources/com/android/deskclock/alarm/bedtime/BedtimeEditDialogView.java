package com.android.deskclock.alarm.bedtime;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.android.deskclock.Alarm;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.SimpleNumberFormatter;
import com.android.deskclock.util.Util;
import com.android.deskclock.widget.SleepTimePicker;
import java.util.Calendar;
import miuix.animation.Folme;
import miuix.animation.controller.AnimState;
import miuix.animation.property.ViewProperty;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;
import miuix.appcompat.internal.app.widget.ActionBarContainer;
import miuix.appcompat.internal.app.widget.ActionBarImpl;

/* JADX INFO: loaded from: classes.dex */
public class BedtimeEditDialogView implements SleepTimePicker.OnTimeChangedListener {
    public static final String EXTRA_ALARM = "alarm";
    public static final String EXTRA_INDEX = "index";
    public static final String EXTRA_INVALID_HOUR = "invalid_hour";
    public static final String EXTRA_INVALID_MIN = "invalid_min";
    private static final int SLEEP_ALARM_INDEX = 0;
    public static final String TAG = "DC:BedtimeEditDialogView";
    private static final int TIME_PICKER_COUNT = 5;
    private View mActionBarContainer;
    private Activity mActivity;
    private Alarm mAlarm;
    private int mAnchorHeight;
    private int mAnchorTop;
    private View mAnchorView;
    private int mAnchorWidth;
    private View mBackgroundView;
    private Button mCancelBtn;
    private View mContentView;
    private int mDialogHeight;
    private int mDialogTop;
    private View mDialogView;
    private int mDialogWidth;
    private boolean mEnableAlarm;
    private int mHour;
    private int mIndex;
    private int mInvalidHour;
    private int mInvalidMin;
    private int mMinute;
    private OnSaveAlarmListener mOnSaveAlarmListener;
    private Alarm mOriginalAlarm;
    private ViewGroup mParentView;
    private View mRootContentView;
    private Button mSaveBtn;
    private int mStartX;
    private TextView mTimeDisplay;
    private float mTimeOffsetY;
    private SleepTimePicker mTimePicker;
    private TextView mTitle;
    private View mVirtualTitle;
    private int temp;
    private boolean isShowAnim = false;
    private boolean isHideAnim = false;
    private int mEndX = 0;
    private OnDismissListener mDismissListener = null;
    private int mRootContentAccessibilityImportant = 0;
    private int mActionBarA11yImportant = 0;

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnSaveAlarmListener {
        void onSaveAlarm(Alarm alarm);
    }

    static /* synthetic */ int access$708(BedtimeEditDialogView bedtimeEditDialogView) {
        int i = bedtimeEditDialogView.mDialogTop;
        bedtimeEditDialogView.mDialogTop = i + 1;
        return i;
    }

    public BedtimeEditDialogView(Activity activity) {
        this.mActivity = activity;
    }

    public void setAlarm(Bundle bundle) {
        if (bundle != null) {
            Alarm alarm = (Alarm) bundle.getParcelable("alarm");
            this.mOriginalAlarm = alarm;
            if (alarm == null) {
                this.mOriginalAlarm = new Alarm();
            }
            this.mAlarm = this.mOriginalAlarm.m78clone();
            this.mIndex = bundle.getInt("index");
            this.mInvalidHour = bundle.getInt("invalid_hour");
            this.mInvalidMin = bundle.getInt("invalid_min");
        }
    }

    public void show(View view, int i) {
        this.mParentView = (ViewGroup) this.mActivity.getWindow().getDecorView();
        View viewInflate = LayoutInflater.from(this.mActivity).inflate(R.layout.dialog_sleep_alarm_edit, (ViewGroup) null, false);
        this.mContentView = viewInflate;
        this.mParentView.addView(viewInflate);
        this.mStartX = i;
        this.mContentView.setVisibility(4);
        View viewFindViewById = this.mActivity.findViewById(android.R.id.content);
        this.mRootContentView = viewFindViewById;
        if (viewFindViewById != null) {
            this.mRootContentAccessibilityImportant = viewFindViewById.getImportantForAccessibility();
            this.mRootContentView.setImportantForAccessibility(4);
        }
        try {
            Activity activity = this.mActivity;
            if (activity instanceof AppCompatActivity) {
                ActionBar appCompatActionBar = ((AppCompatActivity) activity).getAppCompatActionBar();
                if (appCompatActionBar instanceof ActionBarImpl) {
                    ActionBarContainer actionBarContainer = ((ActionBarImpl) appCompatActionBar).getActionBarContainer();
                    this.mActionBarContainer = actionBarContainer;
                    if (actionBarContainer != null) {
                        this.mActionBarA11yImportant = actionBarContainer.getImportantForAccessibility();
                        this.mActionBarContainer.setImportantForAccessibility(4);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "屏蔽 ActionBar 无障碍失败", e);
        }
        initView();
        if (Util.isFoldDevice(this.mActivity) && Util.isInInternalScreen(this.mActivity) && !this.mActivity.isInMultiWindowMode()) {
            this.mDialogView.setBackground(ContextCompat.getDrawable(this.mActivity, R.drawable.bedtime_edit_dialog_fold_bg));
        }
        this.mAnchorView = view;
        this.mAnchorWidth = view.getMeasuredWidth();
        this.mAnchorHeight = this.mAnchorView.getMeasuredHeight();
        int[] iArr = new int[2];
        this.mAnchorView.getLocationInWindow(iArr);
        this.mAnchorTop = iArr[1];
        this.mContentView.post(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeEditDialogView.1
            @Override // java.lang.Runnable
            public void run() {
                if (BedtimeEditDialogView.this.mAnchorView == null) {
                    return;
                }
                if (Util.isWideMode(BedtimeEditDialogView.this.mActivity)) {
                    BedtimeEditDialogView bedtimeEditDialogView = BedtimeEditDialogView.this;
                    bedtimeEditDialogView.mDialogWidth = (int) bedtimeEditDialogView.mActivity.getResources().getDimension(R.dimen.drop_down_popup_window_width);
                    BedtimeEditDialogView bedtimeEditDialogView2 = BedtimeEditDialogView.this;
                    bedtimeEditDialogView2.mEndX = (int) ((bedtimeEditDialogView2.mParentView.getMeasuredWidth() - BedtimeEditDialogView.this.mActivity.getResources().getDimension(R.dimen.drop_down_popup_window_width)) / 2.0f);
                } else {
                    BedtimeEditDialogView bedtimeEditDialogView3 = BedtimeEditDialogView.this;
                    bedtimeEditDialogView3.mEndX = (int) bedtimeEditDialogView3.mActivity.getResources().getDimension(R.dimen.drop_down_popup_window_margin_start);
                    BedtimeEditDialogView bedtimeEditDialogView4 = BedtimeEditDialogView.this;
                    bedtimeEditDialogView4.mDialogWidth = bedtimeEditDialogView4.mDialogView.getMeasuredWidth() - (BedtimeEditDialogView.this.mEndX * 2);
                }
                BedtimeEditDialogView bedtimeEditDialogView5 = BedtimeEditDialogView.this;
                bedtimeEditDialogView5.mDialogHeight = bedtimeEditDialogView5.mDialogView.getMeasuredHeight();
                BedtimeEditDialogView bedtimeEditDialogView6 = BedtimeEditDialogView.this;
                bedtimeEditDialogView6.mDialogTop = ((bedtimeEditDialogView6.mParentView.getMeasuredHeight() / 2) - (BedtimeEditDialogView.this.mDialogHeight / 2)) - 100;
                if (BedtimeEditDialogView.this.mDialogTop == BedtimeEditDialogView.this.mAnchorTop) {
                    BedtimeEditDialogView.access$708(BedtimeEditDialogView.this);
                }
                BedtimeEditDialogView.this.mAnchorView.setVisibility(4);
                BedtimeEditDialogView.this.mContentView.setVisibility(0);
                MiuiFolme.visibleShowTest(BedtimeEditDialogView.this.mBackgroundView);
                int i2 = BedtimeEditDialogView.this.mEndX;
                View view2 = BedtimeEditDialogView.this.mDialogView;
                int i3 = BedtimeEditDialogView.this.mAnchorWidth;
                int i4 = BedtimeEditDialogView.this.temp + BedtimeEditDialogView.this.mAnchorHeight;
                int i5 = BedtimeEditDialogView.this.mAnchorTop - BedtimeEditDialogView.this.temp;
                int i6 = BedtimeEditDialogView.this.mDialogWidth;
                int i7 = BedtimeEditDialogView.this.mDialogHeight;
                int i8 = BedtimeEditDialogView.this.mDialogTop;
                int i9 = BedtimeEditDialogView.this.mStartX;
                if (BedtimeEditDialogView.this.mStartX <= 0) {
                    i2 = -i2;
                }
                MiuiFolme.showBedtimeEditDialog(view2, i3, i4, i5, i6, i7, i8, i9, i2, new MiuiFolme.ClockTransitionListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeEditDialogView.1.1
                    @Override // miuix.animation.listener.TransitionListener
                    public void onBegin(Object obj) {
                        BedtimeEditDialogView.this.isShowAnim = true;
                    }

                    @Override // miuix.animation.listener.TransitionListener
                    public void onComplete(Object obj) {
                        BedtimeEditDialogView.this.isShowAnim = false;
                    }
                });
                MiuiFolme.animTimeDisplay(BedtimeEditDialogView.this.mTimeDisplay, 0, (int) BedtimeEditDialogView.this.mTimeOffsetY);
                MiuiFolme.visibleShow1(BedtimeEditDialogView.this.mTitle, null);
                MiuiFolme.visibleShow1(BedtimeEditDialogView.this.mTimePicker, null);
                MiuiFolme.visibleShow1(BedtimeEditDialogView.this.mCancelBtn, null);
                MiuiFolme.visibleShow1(BedtimeEditDialogView.this.mSaveBtn, null);
                MiuiFolme.visibleHide1(BedtimeEditDialogView.this.mTimeDisplay, null);
                MiuiFolme.visibleHide1(BedtimeEditDialogView.this.mVirtualTitle, null);
            }
        });
        this.mContentView.postDelayed(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeEditDialogView.2
            @Override // java.lang.Runnable
            public void run() {
                BedtimeEditDialogView.this.mContentView.setFocusable(true);
                BedtimeEditDialogView.this.mContentView.sendAccessibilityEvent(8);
                if (BedtimeEditDialogView.this.mIndex == 0) {
                    BedtimeEditDialogView.this.mContentView.announceForAccessibility(BedtimeEditDialogView.this.mActivity.getResources().getString(R.string.sleep_time));
                } else {
                    BedtimeEditDialogView.this.mContentView.announceForAccessibility(BedtimeEditDialogView.this.mActivity.getResources().getString(R.string.wake_up_time));
                }
            }
        }, 300L);
    }

    public void showDirectly(View view, int i) {
        this.temp = (int) this.mActivity.getResources().getDimension(R.dimen.bedtime_alarm_p_top);
        this.mStartX = i;
        this.mContentView.setVisibility(4);
        this.mTimeOffsetY = this.mActivity.getResources().getDimension(R.dimen.bedtime_popup_window_time_offsetY);
        this.mAnchorView = view;
        this.mAnchorWidth = view.getMeasuredWidth();
        this.mAnchorHeight = this.mAnchorView.getMeasuredHeight();
        int[] iArr = new int[2];
        this.mAnchorView.getLocationInWindow(iArr);
        this.mAnchorTop = iArr[1];
        this.mContentView.post(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeEditDialogView.3
            @Override // java.lang.Runnable
            public void run() {
                if (BedtimeEditDialogView.this.mAnchorView == null) {
                    return;
                }
                if (Util.isWideMode(BedtimeEditDialogView.this.mActivity)) {
                    BedtimeEditDialogView bedtimeEditDialogView = BedtimeEditDialogView.this;
                    bedtimeEditDialogView.mDialogWidth = (int) bedtimeEditDialogView.mActivity.getResources().getDimension(R.dimen.drop_down_popup_window_width);
                    BedtimeEditDialogView bedtimeEditDialogView2 = BedtimeEditDialogView.this;
                    bedtimeEditDialogView2.mEndX = (int) ((bedtimeEditDialogView2.mParentView.getMeasuredWidth() - BedtimeEditDialogView.this.mActivity.getResources().getDimension(R.dimen.drop_down_popup_window_width)) / 2.0f);
                } else {
                    BedtimeEditDialogView bedtimeEditDialogView3 = BedtimeEditDialogView.this;
                    bedtimeEditDialogView3.mEndX = (int) bedtimeEditDialogView3.mActivity.getResources().getDimension(R.dimen.drop_down_popup_window_margin_start);
                    BedtimeEditDialogView bedtimeEditDialogView4 = BedtimeEditDialogView.this;
                    bedtimeEditDialogView4.mDialogWidth = bedtimeEditDialogView4.mDialogView.getMeasuredWidth() - (BedtimeEditDialogView.this.mEndX * 2);
                }
                BedtimeEditDialogView bedtimeEditDialogView5 = BedtimeEditDialogView.this;
                bedtimeEditDialogView5.mDialogHeight = bedtimeEditDialogView5.mDialogView.getMeasuredHeight();
                BedtimeEditDialogView bedtimeEditDialogView6 = BedtimeEditDialogView.this;
                bedtimeEditDialogView6.mDialogTop = ((bedtimeEditDialogView6.mParentView.getMeasuredHeight() / 2) - (BedtimeEditDialogView.this.mDialogHeight / 2)) - 100;
                if (BedtimeEditDialogView.this.mDialogTop == BedtimeEditDialogView.this.mAnchorTop) {
                    BedtimeEditDialogView.access$708(BedtimeEditDialogView.this);
                }
                BedtimeEditDialogView.this.mAnchorView.setVisibility(4);
                BedtimeEditDialogView.this.mContentView.setVisibility(0);
                Folme.useAt(BedtimeEditDialogView.this.mDialogView).state().setTo(new AnimState("showDialogEnd").add(ViewProperty.WIDTH, BedtimeEditDialogView.this.mDialogWidth).add(ViewProperty.HEIGHT, BedtimeEditDialogView.this.mDialogHeight).add(ViewProperty.Y, BedtimeEditDialogView.this.mDialogTop).add(ViewProperty.TRANSLATION_X, BedtimeEditDialogView.this.mStartX > 0 ? BedtimeEditDialogView.this.mEndX : -BedtimeEditDialogView.this.mEndX));
            }
        });
    }

    public void dismissDirectly() {
        View view = this.mAnchorView;
        if (view != null) {
            view.setVisibility(0);
            this.mAnchorView = null;
        }
        View view2 = this.mDialogView;
        if (view2 != null) {
            MiuiFolme.cleanAnimArgs(view2);
        }
        restoreAccessibilityState();
        ViewGroup viewGroup = this.mParentView;
        if (viewGroup != null) {
            viewGroup.removeView(this.mContentView);
            this.mParentView = null;
        }
        this.isHideAnim = false;
    }

    public void dismiss() {
        if (this.isHideAnim) {
            return;
        }
        MiuiFolme.visibleHideTest(this.mBackgroundView);
        View view = this.mDialogView;
        int i = this.mDialogWidth;
        int i2 = this.mDialogHeight;
        int i3 = this.mDialogTop;
        int i4 = this.mAnchorWidth;
        int i5 = this.mAnchorHeight;
        int i6 = this.temp;
        int i7 = i5 + i6;
        int i8 = this.mAnchorTop - i6;
        int i9 = this.mStartX;
        int i10 = this.mEndX;
        if (i9 <= 0) {
            i10 = -i10;
        }
        MiuiFolme.hideBedtimeEditDialog(view, i, i2, i3, i4, i7, i8, i10, i9, new MiuiFolme.ClockTransitionListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeEditDialogView.4
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                BedtimeEditDialogView.this.isHideAnim = true;
                if (BedtimeEditDialogView.this.mTimePicker != null) {
                    BedtimeEditDialogView.this.mTimePicker.setVisibility(8);
                }
                if (BedtimeEditDialogView.this.mDismissListener != null) {
                    BedtimeEditDialogView.this.mDismissListener.onDismiss();
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                if (BedtimeEditDialogView.this.mAnchorView != null) {
                    BedtimeEditDialogView.this.mAnchorView.setVisibility(0);
                    BedtimeEditDialogView.this.mAnchorView = null;
                }
                if (BedtimeEditDialogView.this.mDialogView != null) {
                    MiuiFolme.cleanAnimArgs(BedtimeEditDialogView.this.mDialogView);
                }
                BedtimeEditDialogView.this.restoreAccessibilityState();
                if (BedtimeEditDialogView.this.mParentView != null) {
                    BedtimeEditDialogView.this.mParentView.removeView(BedtimeEditDialogView.this.mContentView);
                    BedtimeEditDialogView.this.mParentView = null;
                }
                BedtimeEditDialogView.this.isHideAnim = false;
            }
        });
        MiuiFolme.animTimeDisplay(this.mTimeDisplay, (int) this.mTimeOffsetY, 0);
        MiuiFolme.visibleHide1(this.mTitle, null);
        MiuiFolme.visibleHide1(this.mTimePicker, null);
        MiuiFolme.visibleHide1(this.mCancelBtn, null);
        MiuiFolme.visibleHide1(this.mSaveBtn, null);
        MiuiFolme.visibleShow1(this.mTimeDisplay, null);
        MiuiFolme.visibleShow1(this.mVirtualTitle, null);
    }

    public boolean isShowing() {
        return this.mParentView != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreAccessibilityState() {
        View view = this.mRootContentView;
        if (view != null) {
            view.setImportantForAccessibility(this.mRootContentAccessibilityImportant);
            this.mRootContentView = null;
        }
        View view2 = this.mActionBarContainer;
        if (view2 != null) {
            view2.setImportantForAccessibility(this.mActionBarA11yImportant);
            this.mActionBarContainer = null;
        }
    }

    public boolean isSleepTimeShowing() {
        return this.mIndex == 0;
    }

    private void initView() {
        View viewFindViewById = this.mContentView.findViewById(R.id.background_dialog);
        this.mBackgroundView = viewFindViewById;
        viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeEditDialogView.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (BedtimeEditDialogView.this.isShowAnim) {
                    return;
                }
                BedtimeEditDialogView.this.dismiss();
            }
        });
        this.mVirtualTitle = this.mContentView.findViewById(R.id.time_display_desc);
        this.mTimeOffsetY = this.mActivity.getResources().getDimension(R.dimen.bedtime_popup_window_time_offsetY);
        View viewFindViewById2 = this.mContentView.findViewById(R.id.time_dialog);
        this.mDialogView = viewFindViewById2;
        viewFindViewById2.setOnClickListener(null);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mDialogView.getLayoutParams();
        this.temp = (int) this.mActivity.getResources().getDimension(R.dimen.bedtime_alarm_p_top);
        TextView textView = (TextView) this.mContentView.findViewById(R.id.bedtime_alarm_title);
        this.mTitle = textView;
        if (this.mIndex == 0) {
            textView.setText(R.string.sleep_time);
            ((TextView) this.mVirtualTitle.findViewById(R.id.bedtime_desc)).setText(R.string.sleep_time);
            this.mDialogView.setLayoutParams(layoutParams);
        } else {
            layoutParams.addRule(21);
            this.mDialogView.setLayoutParams(layoutParams);
            this.mTitle.setText(R.string.wake_up_time);
            ((TextView) this.mVirtualTitle.findViewById(R.id.bedtime_desc)).setText(R.string.wake_up_time);
            ((ImageView) this.mVirtualTitle.findViewById(R.id.time_display_icon)).setImageResource(R.drawable.ic_get_up_time);
        }
        if (MiuiSdk.isSupportFontAnim()) {
            MiuiFont.setFont(this.mTitle, MiuiFont.MI_PRO_MEDIUM);
        }
        this.mDialogView.setContentDescription(this.mTitle.getText().toString());
        SleepTimePicker sleepTimePicker = (SleepTimePicker) this.mContentView.findViewById(R.id.time_picker);
        this.mTimePicker = sleepTimePicker;
        sleepTimePicker.setSelectorIndicesCount(5);
        this.mTimePicker.setOnTimeChangedListener(this);
        Button button = (Button) this.mContentView.findViewById(R.id.cancel_btn);
        this.mCancelBtn = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeEditDialogView.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BedtimeEditDialogView.this.dismiss();
            }
        });
        Button button2 = (Button) this.mContentView.findViewById(R.id.done_btn);
        this.mSaveBtn = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeEditDialogView.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BedtimeEditDialogView.this.dismiss();
                if (BedtimeEditDialogView.this.isModified()) {
                    BedtimeEditDialogView.this.mOriginalAlarm.hour = BedtimeEditDialogView.this.mHour;
                    BedtimeEditDialogView.this.mOriginalAlarm.minutes = BedtimeEditDialogView.this.mMinute;
                    BedtimeEditDialogView.this.mOriginalAlarm.enabled = BedtimeEditDialogView.this.mEnableAlarm;
                    if (BedtimeEditDialogView.this.mOnSaveAlarmListener != null) {
                        BedtimeEditDialogView.this.mOnSaveAlarmListener.onSaveAlarm(BedtimeEditDialogView.this.mOriginalAlarm);
                    }
                    BedtimeEditDialogView.this.mTimeDisplay.setText(SimpleNumberFormatter.format(2, BedtimeEditDialogView.this.mHour) + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + SimpleNumberFormatter.format(2, BedtimeEditDialogView.this.mMinute));
                }
            }
        });
        this.mTimeDisplay = (TextView) this.mContentView.findViewById(R.id.time_display);
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

    private void showTimePicker() {
        this.mTimePicker.setIs24HourView(true);
        this.mTimePicker.setCurrentHour(Integer.valueOf(this.mOriginalAlarm.hour));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(this.mOriginalAlarm.minutes / 5));
        if (this.mTimePicker.is24HourView()) {
            this.mTimePicker.setPadding((int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_24), 0, (int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_24), 0);
        } else {
            this.mTimePicker.setPadding((int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_12), 0, (int) this.mActivity.getResources().getDimension(R.dimen.dialog_time_picker_layout_padding_start_12), 0);
        }
        this.mTimeDisplay.setText(SimpleNumberFormatter.format(2, this.mOriginalAlarm.hour) + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + SimpleNumberFormatter.format(2, this.mOriginalAlarm.minutes));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isModified() {
        return (this.mOriginalAlarm.hour == this.mHour && this.mOriginalAlarm.minutes == this.mMinute && this.mOriginalAlarm.enabled == this.mEnableAlarm) ? false : true;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mDismissListener = onDismissListener;
    }

    public void setOnSaveAlarmListener(OnSaveAlarmListener onSaveAlarmListener) {
        this.mOnSaveAlarmListener = onSaveAlarmListener;
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
}
