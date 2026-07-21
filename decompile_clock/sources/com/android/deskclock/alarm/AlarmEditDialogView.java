package com.android.deskclock.alarm;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.DateFormatUtil;
import com.android.deskclock.util.ScenarioRecognitionUtil;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.widget.TimePicker;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ValueProperty;
import miuix.animation.property.ViewProperty;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MiuiBlurUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.slidingwidget.widget.SlidingButton;
import miuix.smooth.SmoothFrameLayout2;

/* JADX INFO: loaded from: classes.dex */
public class AlarmEditDialogView implements TimePicker.OnTimeChangedListener {
    public static final String ALARM_INTENT_EXTRA_CHANGED = "intent.extra.alarm.changed";
    public static final String EXTRA_ALARM = "alarm";
    public static final String EXTRA_CALENDER = "calender";
    private static final String HANDLER_THREAD_NAME = "AlarmEditDialogView";
    public static final String TAG = "DC:AlarmEditDialogView";
    private static final int TIME_PICKER_COUNT = 5;
    private DeskClockTabActivity mActivity;
    private Alarm mAlarm;
    private AlarmViewGhostHolder mAlarmGhostHolder;
    private int mAlarmId;
    private TextView mAlarmInFutureView;
    private int mAnchorHeight;
    private float mAnchorRadius;
    private int mAnchorTop;
    private View mAnchorView;
    private int mAnchorWidth;
    private Handler mAsyncHandler;
    private View mBackgroundView;
    private LinearLayout mBtnGroup;
    private Calendar mCalender;
    private View mContentView;
    private float mDensity;
    private int mDialogHeight;
    private float mDialogRadius;
    private int mDialogTop;
    private SmoothFrameLayout2 mDialogView;
    private int mDialogWidth;
    private boolean mEnableAlarm;
    private View mExpandView;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private int mHour;
    private int mMinute;
    private LinearLayout mModifyAlarmDesc;
    private Button mMoreBtn;
    private OnMoreClickListener mOnMoreClickListener;
    private OnSaveAlarmListener mOnSaveAlarmListener;
    private Alarm mOriginalAlarm;
    private ViewGroup mParentView;
    private View mRootContentView;
    private Button mSaveBtn;
    private TimePicker mTimePicker;
    private boolean mEnableHyperMaterial = false;
    private boolean isShowAnim = false;
    private boolean isHideAnim = false;
    private OnDismissListener mDismissListener = null;
    private int mRootContentAccessibilityImportant = 0;

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnMoreClickListener {
        void onMoreClick(Alarm alarm, Alarm alarm2);
    }

    public interface OnSaveAlarmListener {
        void onSaveAlarm(Alarm alarm);
    }

    public AlarmEditDialogView(DeskClockTabActivity deskClockTabActivity) {
        this.mActivity = deskClockTabActivity;
        deskClockTabActivity.setEditDialogView(this);
    }

    public void setAlarm(Bundle bundle) {
        Alarm alarm = (Alarm) bundle.getParcelable("alarm");
        this.mOriginalAlarm = alarm;
        if (alarm == null) {
            this.mOriginalAlarm = new Alarm();
        }
        this.mAlarm = this.mOriginalAlarm.m78clone();
        this.mCalender = (Calendar) bundle.getSerializable("calender");
    }

    public int getCurrentId() {
        return this.mAlarmId;
    }

    public void updateAnchorTop(View view) {
        if (view == null) {
            return;
        }
        int[] iArr = new int[2];
        this.mAnchorView = view;
        view.getLocationInWindow(iArr);
        this.mAnchorView.setVisibility(4);
        this.mAnchorTop = iArr[1];
    }

    public void show(View view, Rect rect, AlarmModel.AlarmBean alarmBean, int i) {
        if (alarmBean == null) {
            return;
        }
        createHandler();
        this.mAlarmId = alarmBean.id;
        this.mDensity = this.mActivity.getResources().getDisplayMetrics().density;
        this.mEnableHyperMaterial = HyperMaterialUtils.isFeatureEnable(this.mActivity);
        this.mParentView = (ViewGroup) this.mActivity.getWindow().getDecorView();
        View viewInflate = LayoutInflater.from(this.mActivity).inflate(R.layout.dialog_alarm_edit, (ViewGroup) null, false);
        this.mContentView = viewInflate;
        this.mParentView.addView(viewInflate);
        initView();
        initAlarmGhost(alarmBean, i);
        View view2 = this.mContentView;
        if (view2 != null) {
            view2.setVisibility(4);
        }
        View viewFindViewById = this.mActivity.findViewById(android.R.id.content);
        this.mRootContentView = viewFindViewById;
        if (viewFindViewById != null) {
            this.mRootContentAccessibilityImportant = viewFindViewById.getImportantForAccessibility();
            this.mRootContentView.setImportantForAccessibility(4);
        }
        if (this.mEnableHyperMaterial) {
            MiuiBlurUtils.setBackgroundBlur(this.mContentView, 0, false);
        }
        this.mAnchorView = view;
        this.mAnchorWidth = view.getMeasuredWidth();
        this.mAnchorRadius = (int) this.mAnchorView.getResources().getDimension(R.dimen.alarm_item_radius);
        this.mDialogWidth = this.mAnchorWidth - (MiuixUIUtils.dp2px(this.mAnchorView.getContext(), 4.0f) * 2);
        this.mDialogHeight = (int) this.mAnchorView.getResources().getDimension(R.dimen.drop_down_popup_window_height);
        this.mDialogRadius = this.mAnchorView.getResources().getDimension(R.dimen.drop_down_popup_window_corner);
        this.mAnchorHeight = this.mAnchorView.getMeasuredHeight();
        int[] iArr = new int[2];
        this.mAnchorView.getLocationInWindow(iArr);
        this.mAnchorTop = iArr[1];
        this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(this.mDialogWidth, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(this.mDialogHeight, BasicMeasure.EXACTLY));
        ViewGroup.LayoutParams layoutParams = this.mTimePicker.getLayoutParams();
        layoutParams.width = this.mTimePicker.getMeasuredWidth();
        this.mTimePicker.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams2 = this.mBtnGroup.getLayoutParams();
        layoutParams2.width = this.mBtnGroup.getMeasuredWidth();
        this.mBtnGroup.setLayoutParams(layoutParams2);
        this.mDialogHeight = this.mDialogView.getMeasuredHeight();
        this.mDialogTop = (this.mParentView.getHeight() - this.mDialogHeight) / 2;
        this.mAnchorView.setVisibility(4);
        this.mContentView.setVisibility(0);
        ViewGroup.LayoutParams layoutParams3 = this.mExpandView.getLayoutParams();
        layoutParams3.height = this.mExpandView.getMeasuredHeight();
        this.mExpandView.setLayoutParams(layoutParams3);
        MiuiFolme.visibleShowTest(this.mBackgroundView);
        MiuiFolme.showAlarmEditDialog(this.mDialogView, this.mAnchorWidth, this.mAnchorHeight, this.mAnchorTop, this.mDialogWidth, this.mDialogHeight, this.mDialogTop, new ShowClockTransitionListener(this.mActivity));
    }

    public static class ShowClockTransitionListener extends MiuiFolme.ClockTransitionListener {
        private WeakReference<DeskClockTabActivity> mReference;

        public ShowClockTransitionListener(DeskClockTabActivity deskClockTabActivity) {
            this.mReference = new WeakReference<>(deskClockTabActivity);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onBegin(Object obj) {
            ScenarioRecognitionUtil.INSTANCE.setScenarioState(334L, true);
            AlarmEditDialogView alarmEditDialogView = this.mReference.get().getAlarmEditDialogView();
            if (alarmEditDialogView != null) {
                alarmEditDialogView.onShowBegin();
            }
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
            super.onUpdate(obj, collection);
            AlarmEditDialogView alarmEditDialogView = this.mReference.get().getAlarmEditDialogView();
            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, ValueProperty.FRACTION);
            if (updateInfoFindBy == null || alarmEditDialogView == null) {
                return;
            }
            alarmEditDialogView.onShowUpdate(updateInfoFindBy.getFloatValue());
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            AlarmEditDialogView alarmEditDialogView = this.mReference.get().getAlarmEditDialogView();
            if (alarmEditDialogView != null) {
                alarmEditDialogView.onShowEnd();
            }
            ScenarioRecognitionUtil.INSTANCE.setScenarioState(334L, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onShowBegin() {
        this.isShowAnim = true;
        LinearLayout linearLayout = this.mModifyAlarmDesc;
        Float fValueOf = Float.valueOf(1.0f);
        if (linearLayout != null) {
            Folme.useAt(linearLayout).to(ViewProperty.ALPHA, fValueOf, new AnimConfig().setEase(FolmeEase.linear(100L)));
        }
        TimePicker timePicker = this.mTimePicker;
        if (timePicker != null) {
            Folme.useAt(timePicker).to(ViewProperty.ALPHA, fValueOf, new AnimConfig().setEase(FolmeEase.linear(100L)));
        }
        LinearLayout linearLayout2 = this.mBtnGroup;
        if (linearLayout2 != null) {
            Folme.useAt(linearLayout2).to(ViewProperty.ALPHA, fValueOf, new AnimConfig().setEase(FolmeEase.linear(100L)));
        }
        AlarmViewGhostHolder alarmViewGhostHolder = this.mAlarmGhostHolder;
        if (alarmViewGhostHolder != null) {
            Folme.useAt(alarmViewGhostHolder.itemView).to(ViewProperty.ALPHA, Float.valueOf(0.0f), new AnimConfig().setEase(FolmeEase.linear(100L)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onShowEnd() {
        this.isShowAnim = false;
        this.mDialogView.requestFocus();
        this.mDialogView.setCornerRadius(this.mDialogRadius);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onShowUpdate(float f) {
        SmoothFrameLayout2 smoothFrameLayout2 = this.mDialogView;
        float f2 = this.mAnchorRadius;
        smoothFrameLayout2.setCornerRadius(f2 + ((this.mDialogRadius - f2) * f));
        if (this.mEnableHyperMaterial) {
            MiuiBlurUtils.setBackgroundBlur(this.mContentView, MiuixUIUtils.dp2px(this.mDensity, 40.0f * f));
            MiuiBlurUtils.setBackgroundBlurScaleRatio(this.mContentView, f * 0.05f);
        }
    }

    public static class HideClockTransitionListener extends MiuiFolme.ClockTransitionListener {
        private WeakReference<DeskClockTabActivity> mReference;

        public HideClockTransitionListener(DeskClockTabActivity deskClockTabActivity) {
            this.mReference = new WeakReference<>(deskClockTabActivity);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onBegin(Object obj) {
            ScenarioRecognitionUtil.INSTANCE.setScenarioState(337L, true);
            AlarmEditDialogView alarmEditDialogView = this.mReference.get().getAlarmEditDialogView();
            if (alarmEditDialogView != null) {
                alarmEditDialogView.onHideBegin();
            }
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
            super.onUpdate(obj, collection);
            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, ValueProperty.FRACTION);
            AlarmEditDialogView alarmEditDialogView = this.mReference.get().getAlarmEditDialogView();
            if (alarmEditDialogView == null || updateInfoFindBy == null) {
                return;
            }
            alarmEditDialogView.onHideUpdate(updateInfoFindBy.getFloatValue());
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            AlarmEditDialogView alarmEditDialogView = this.mReference.get().getAlarmEditDialogView();
            if (alarmEditDialogView != null) {
                alarmEditDialogView.onHideEnd();
            }
            ScenarioRecognitionUtil.INSTANCE.setScenarioState(337L, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onHideBegin() {
        this.isHideAnim = true;
        LinearLayout linearLayout = this.mModifyAlarmDesc;
        Float fValueOf = Float.valueOf(0.0f);
        if (linearLayout != null) {
            Folme.useAt(linearLayout).to(ViewProperty.ALPHA, fValueOf, new AnimConfig().setEase(FolmeEase.linear(100L)));
        }
        TimePicker timePicker = this.mTimePicker;
        if (timePicker != null) {
            Folme.useAt(timePicker).to(ViewProperty.ALPHA, fValueOf, new AnimConfig().setEase(FolmeEase.linear(100L)));
        }
        LinearLayout linearLayout2 = this.mBtnGroup;
        if (linearLayout2 != null) {
            Folme.useAt(linearLayout2).to(ViewProperty.ALPHA, fValueOf, new AnimConfig().setEase(FolmeEase.linear(100L)));
        }
        AlarmViewGhostHolder alarmViewGhostHolder = this.mAlarmGhostHolder;
        if (alarmViewGhostHolder != null) {
            Folme.useAt(alarmViewGhostHolder.itemView).to(ViewProperty.ALPHA, Float.valueOf(1.0f), new AnimConfig().setEase(FolmeEase.linear(100L)));
        }
        OnDismissListener onDismissListener = this.mDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onHideUpdate(float f) {
        SmoothFrameLayout2 smoothFrameLayout2 = this.mDialogView;
        float f2 = this.mAnchorRadius;
        smoothFrameLayout2.setCornerRadius(f2 + ((this.mDialogRadius - f2) * f));
        if (this.mEnableHyperMaterial) {
            MiuiBlurUtils.setBackgroundBlur(this.mContentView, MiuixUIUtils.dp2px(this.mDensity, 30.0f * f));
            MiuiBlurUtils.setBackgroundBlurScaleRatio(this.mContentView, f * 0.1f);
        }
    }

    private void onAnchorViewShow() {
        View view = this.mExpandView;
        if (view != null) {
            Folme.useAt(view).visible().hide(new AnimConfig[0]);
        }
        View view2 = this.mAnchorView;
        if (view2 != null) {
            view2.setVisibility(0);
            this.mAnchorView = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onHideEnd() {
        onAnchorViewShow();
        SmoothFrameLayout2 smoothFrameLayout2 = this.mDialogView;
        if (smoothFrameLayout2 != null) {
            MiuiFolme.cleanFolme(smoothFrameLayout2);
            this.mDialogView.setCornerRadius(this.mAnchorRadius);
        }
        ViewGroup viewGroup = this.mParentView;
        if (viewGroup != null) {
            viewGroup.removeView(this.mContentView);
            this.mParentView = null;
        }
        this.mActivity.setEditDialogView(null);
        this.isHideAnim = false;
    }

    public void dismissDirectly() {
        View view = this.mAnchorView;
        if (view != null) {
            view.setVisibility(0);
            this.mAnchorView = null;
        }
        SmoothFrameLayout2 smoothFrameLayout2 = this.mDialogView;
        if (smoothFrameLayout2 != null) {
            MiuiFolme.cleanFolme(smoothFrameLayout2);
        }
        restoreAccessibilityState();
        ViewGroup viewGroup = this.mParentView;
        if (viewGroup != null) {
            viewGroup.removeView(this.mContentView);
            this.mParentView = null;
        }
        this.mActivity.setEditDialogView(null);
        this.isHideAnim = false;
        View view2 = this.mBackgroundView;
        if (view2 != null) {
            MiuiFolme.cleanFolme(view2);
        }
        View view3 = this.mExpandView;
        if (view3 != null) {
            MiuiFolme.cleanFolme(view3);
        }
        releaseHandler();
    }

    public void dismiss() {
        if (this.isHideAnim) {
            return;
        }
        restoreAccessibilityState();
        MiuiFolme.visibleHideTest(this.mBackgroundView);
        MiuiFolme.hideAlarmEditDialog(this.mDialogView, this.mAnchorWidth, this.mAnchorHeight, this.mAnchorTop, new HideClockTransitionListener(this.mActivity));
        releaseHandler();
    }

    public boolean isShowing() {
        return this.mParentView != null;
    }

    private int validLocationY(int i, int i2, Rect rect) {
        int i3 = rect.top + 80;
        int i4 = rect.bottom - 80;
        if (i <= i3) {
            return i3 - (i / 3);
        }
        return i2 >= i4 ? i4 - (i2 - i) : i;
    }

    private void initView() {
        View viewFindViewById = this.mContentView.findViewById(R.id.background_dialog);
        this.mBackgroundView = viewFindViewById;
        viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmEditDialogView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (AlarmEditDialogView.this.isShowAnim) {
                    return;
                }
                AlarmEditDialogView.this.dismiss();
            }
        });
        this.mDialogView = (SmoothFrameLayout2) this.mContentView.findViewById(R.id.dialog_holder);
        if (Locale.getDefault().getLanguage().contains("bo") && MiuixUIUtils.getFontLevel(DeskClockApp.getAppDEContext()) == 2) {
            ViewGroup.LayoutParams layoutParams = this.mDialogView.getLayoutParams();
            layoutParams.height = (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.drop_down_popup_window_height_large);
            this.mDialogView.setLayoutParams(layoutParams);
        }
        this.mDialogView.setOnClickListener(null);
        this.mExpandView = this.mContentView.findViewById(R.id.expand_dialog);
        this.mBtnGroup = (LinearLayout) this.mContentView.findViewById(R.id.alarm_button_group);
        this.mAlarmInFutureView = (TextView) this.mContentView.findViewById(R.id.alarm_in_future_time);
        this.mModifyAlarmDesc = (LinearLayout) this.mContentView.findViewById(R.id.modify_alarm_desc);
        TimePicker timePicker = (TimePicker) this.mContentView.findViewById(R.id.time_picker);
        this.mTimePicker = timePicker;
        timePicker.setSelectorIndicesCount(5);
        this.mTimePicker.setOnTimeChangedListener(this);
        Button button = (Button) this.mContentView.findViewById(R.id.alarm_more_setting);
        this.mMoreBtn = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmEditDialogView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (AlarmEditDialogView.this.isShowAnim) {
                    return;
                }
                AlarmEditDialogView.this.dismiss();
                Intent intent = new Intent();
                intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, AlarmEditDialogView.this.mOriginalAlarm);
                intent.putExtra("intent.extra.alarm.changed", AlarmEditDialogView.this.mAlarm);
                if (AlarmEditDialogView.this.mOnMoreClickListener != null) {
                    AlarmEditDialogView.this.mOnMoreClickListener.onMoreClick(AlarmEditDialogView.this.mOriginalAlarm, AlarmEditDialogView.this.mAlarm);
                }
                StatHelper.alarmEvent(StatHelper.EVENT_CLICK_TIME_PICKER_MORE_SETTING);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_ITEM_DIALOG_MORE_CLICK);
            }
        });
        handleMoreBtnSize();
        Button button2 = (Button) this.mContentView.findViewById(R.id.set_alarm_done);
        this.mSaveBtn = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmEditDialogView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.d(AlarmEditDialogView.TAG, "mSaveBtn onClick: ");
                if (AlarmEditDialogView.this.isShowAnim) {
                    return;
                }
                if (AlarmEditDialogView.this.mTimePicker != null) {
                    StatHelper.updateAlarmProperties(StatHelper.EVENT_EDIT_ALARM_HOUR_PICKER_SLIDE_TIMES, AlarmEditDialogView.this.mTimePicker.getHourSlideTimes());
                    StatHelper.updateAlarmProperties(StatHelper.EVENT_EDIT_ALARM_MIN_PICKER_SLIDE_TIMES, AlarmEditDialogView.this.mTimePicker.getMinSlideTimes());
                    OneTrackStatHelper.trackNumEvent(AlarmEditDialogView.this.mTimePicker.getHourSlideTimes(), OneTrackStatHelper.EDIT_ALARM_HOUR_PICKER_SLIDE_COUNT);
                    OneTrackStatHelper.trackNumEvent(AlarmEditDialogView.this.mTimePicker.getMinSlideTimes(), OneTrackStatHelper.EDIT_ALARM_MIN_PICKER_SLIDE_COUNT);
                }
                Log.d(AlarmEditDialogView.TAG, "isModified: " + AlarmEditDialogView.this.isModified());
                if (AlarmEditDialogView.this.isModified()) {
                    AlarmEditDialogView.this.mOriginalAlarm.hour = AlarmEditDialogView.this.mHour;
                    AlarmEditDialogView.this.mOriginalAlarm.minutes = AlarmEditDialogView.this.mMinute;
                    AlarmEditDialogView.this.mOriginalAlarm.enabled = AlarmEditDialogView.this.mEnableAlarm;
                    if (AlarmEditDialogView.this.mAlarmGhostHolder != null) {
                        AlarmEditDialogView alarmEditDialogView = AlarmEditDialogView.this;
                        AlarmEditDialogView.this.mAlarmGhostHolder.update(alarmEditDialogView.createNewBean(alarmEditDialogView.mOriginalAlarm), 0);
                    }
                    if (AlarmEditDialogView.this.mOnSaveAlarmListener != null) {
                        com.android.deskclock.util.Log.f(AlarmEditDialogView.TAG, "mOriginalAlarm: " + AlarmEditDialogView.this.mOriginalAlarm);
                        AlarmEditDialogView.this.mOnSaveAlarmListener.onSaveAlarm(AlarmEditDialogView.this.mOriginalAlarm);
                        StatHelper.trackEvent(StatHelper.KEY_SET_ALARM_TIME, TimeUtil.composeTime(AlarmEditDialogView.this.mHour, AlarmEditDialogView.this.mMinute));
                        OneTrackStatHelper.trackNumEvent((AlarmEditDialogView.this.mHour * 60) + AlarmEditDialogView.this.mMinute, "");
                    }
                } else {
                    AlarmEditDialogView.this.dismiss();
                }
                StatHelper.alarmEvent(StatHelper.EVENT_CLICK_TIME_PICKER_FINISH);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_ITEM_DIALOG_DONE_CLICK);
            }
        });
        updateUI();
    }

    private void initAlarmGhost(AlarmModel.AlarmBean alarmBean, int i) {
        if (this.mContentView == null) {
            return;
        }
        if (this.mAlarmGhostHolder == null) {
            this.mAlarmGhostHolder = new AlarmViewGhostHolder(this.mContentView.findViewById(R.id.alarm_time_ghost_root));
        }
        this.mAlarmGhostHolder.update(alarmBean, i);
        this.mAlarmGhostHolder.itemView.setAlpha(1.0f);
    }

    private void handleMoreBtnSize() {
        DeskClockTabActivity deskClockTabActivity = this.mActivity;
        if (Util.getTypefaceTextViewWidth(deskClockTabActivity, deskClockTabActivity.getResources().getString(R.string.alarm_clock_more_setting), (int) this.mActivity.getResources().getDimension(R.dimen.drop_down_popup_window_more_setting_size), null) > this.mActivity.getResources().getDimension(R.dimen.dialog_button_width) - 40.0f) {
            this.mMoreBtn.setTextSize(0, this.mActivity.getResources().getDimension(R.dimen.drop_down_popup_window_more_setting_size_small));
        }
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

    public void onConfigurationChanged(Configuration configuration) {
        this.mDensity = this.mActivity.getResources().getDisplayMetrics().density;
        boolean zIsFeatureEnable = HyperMaterialUtils.isFeatureEnable(this.mActivity);
        this.mEnableHyperMaterial = zIsFeatureEnable;
        if (zIsFeatureEnable) {
            return;
        }
        Folme.useAt(this.mBackgroundView).visible().setShow();
    }

    private void updateFutureView(Alarm alarm) {
        Handler handler = this.mAsyncHandler;
        if (handler != null) {
            handler.removeMessages(0);
            Handler handler2 = this.mAsyncHandler;
            handler2.sendMessage(handler2.obtainMessage(0));
        }
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

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        this.mOnMoreClickListener = onMoreClickListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AlarmModel.AlarmBean createNewBean(Alarm alarm) {
        String skipDateString;
        DeskClockTabActivity deskClockTabActivity = this.mActivity;
        AlarmModel.AlarmBean alarmBean = new AlarmModel.AlarmBean(0, 0, alarm);
        alarmBean.daysOfWeekStr = alarm.daysOfWeek.toString(deskClockTabActivity, !alarm.enabled);
        if (alarm.enabled) {
            skipDateString = SetAlarmController.getAlarmInFuture(this.mCalender, deskClockTabActivity, alarm.hour, alarm.minutes, alarm.daysOfWeek);
        } else if (alarm.skipTime == 0) {
            skipDateString = "";
        } else {
            skipDateString = Util.getSkipDateString(deskClockTabActivity, alarm.skipTime);
            alarmBean.alarmSkipOnceText = skipDateString;
        }
        alarmBean.alarmInFutureText = skipDateString;
        String str = AlarmHelper.get24HourMode() ? AlarmModel.M24 : AlarmModel.M12;
        this.mCalender.set(11, alarm.hour);
        this.mCalender.set(12, alarm.minutes);
        alarmBean.mTimeDisplay = (String) DateFormat.format(str, this.mCalender);
        if (!AlarmHelper.get24HourMode()) {
            String[] amPmStrings = DateFormatUtil.getAmPmStrings();
            alarmBean.mAmPmDisplay = this.mCalender.get(9) == 0 ? amPmStrings[0] : amPmStrings[1];
        }
        return alarmBean;
    }

    private class AlarmViewGhostHolder {
        public TextView alarmInFutureView;
        private TextView amPmDisplay;
        public SlidingButton clockOnOff;
        public TextView daysOfWeekView;
        public TextView daysOfWeekViewRightLine;
        private int fontLevel;
        public View itemView;
        public TextView labelView;
        private TextView timeDisplay;

        public AlarmViewGhostHolder(View view) {
            this.itemView = view;
            this.clockOnOff = (SlidingButton) view.findViewById(R.id.clock_onoff);
            this.daysOfWeekViewRightLine = (TextView) view.findViewById(R.id.days_of_week_right_line);
            this.daysOfWeekView = (TextView) view.findViewById(R.id.days_of_week);
            this.labelView = (TextView) view.findViewById(R.id.label);
            this.alarmInFutureView = (TextView) view.findViewById(R.id.alarm_in_future);
            this.timeDisplay = (TextView) view.findViewById(R.id.time_display);
            this.amPmDisplay = (TextView) view.findViewById(R.id.am_pm);
            this.clockOnOff.setOnCheckedChangeListener(null);
            if (Util.isDeviceCetus()) {
                this.daysOfWeekView.setMaxWidth((int) view.getResources().getDimension(R.dimen.alarm_time_item_label_textview_max_width));
            }
        }

        public void update(AlarmModel.AlarmBean alarmBean, int i) {
            Context context = this.itemView.getContext();
            this.fontLevel = MiuixUIUtils.getFontLevel(context);
            changeToLinearLayout(this);
            setAlarmItemForLinear(this, alarmBean, i);
            if (Util.isTinyScreen(context)) {
                this.daysOfWeekView.setMaxWidth((int) context.getResources().getDimension(R.dimen.alarm_item_days_of_week_width_tiny));
                this.daysOfWeekViewRightLine.setVisibility(8);
                this.labelView.setVisibility(8);
            }
        }

        private void changeToLinearLayout(AlarmViewGhostHolder alarmViewGhostHolder) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) alarmViewGhostHolder.amPmDisplay.getLayoutParams();
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) alarmViewGhostHolder.timeDisplay.getLayoutParams();
            String language = Locale.getDefault().getLanguage();
            if (language.equals("zh") || language.equals("ja") || language.equals("ko") || language.equals("fa") || language.equals("ar")) {
                layoutParams2.addRule(17, R.id.am_pm);
            } else {
                layoutParams.addRule(17, R.id.time_display);
            }
            RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) alarmViewGhostHolder.labelView.getLayoutParams();
            RelativeLayout.LayoutParams layoutParams4 = (RelativeLayout.LayoutParams) alarmViewGhostHolder.alarmInFutureView.getLayoutParams();
            if (this.fontLevel == 2) {
                alarmViewGhostHolder.daysOfWeekViewRightLine.setVisibility(8);
                layoutParams3.addRule(3, R.id.days_of_week);
                layoutParams4.addRule(3, R.id.days_of_week);
                layoutParams3.topMargin = (int) alarmViewGhostHolder.itemView.getResources().getDimension(R.dimen.alarm_time_info_margin_top);
            } else {
                alarmViewGhostHolder.daysOfWeekViewRightLine.setVisibility(0);
                layoutParams3.addRule(17, R.id.days_of_week_right_line);
                layoutParams4.addRule(17, R.id.days_of_week_right_line);
            }
            RelativeLayout.LayoutParams layoutParams5 = (RelativeLayout.LayoutParams) alarmViewGhostHolder.clockOnOff.getLayoutParams();
            layoutParams5.addRule(15, -1);
            layoutParams5.addRule(10, 0);
            layoutParams5.topMargin = 0;
            RelativeLayout.LayoutParams layoutParams6 = (RelativeLayout.LayoutParams) alarmViewGhostHolder.itemView.findViewById(android.R.id.checkbox).getLayoutParams();
            layoutParams6.addRule(15, -1);
            layoutParams6.addRule(10, 0);
            layoutParams6.topMargin = 0;
        }

        private void setAlarmItemForLinear(AlarmViewGhostHolder alarmViewGhostHolder, AlarmModel.AlarmBean alarmBean, int i) {
            Resources resources = alarmViewGhostHolder.itemView.getResources();
            alarmViewGhostHolder.timeDisplay.setText(alarmBean.mTimeDisplay);
            if (!TextUtils.isEmpty(alarmBean.daysOfWeekStr)) {
                alarmViewGhostHolder.daysOfWeekView.setText(alarmBean.daysOfWeekStr);
                alarmViewGhostHolder.daysOfWeekView.setVisibility(0);
            } else {
                alarmViewGhostHolder.daysOfWeekView.setText("");
                alarmViewGhostHolder.daysOfWeekView.setVisibility(8);
            }
            if (AlarmHelper.get24HourMode()) {
                alarmViewGhostHolder.amPmDisplay.setVisibility(8);
            } else {
                alarmViewGhostHolder.amPmDisplay.setText(alarmBean.mAmPmDisplay);
                alarmViewGhostHolder.amPmDisplay.setVisibility(0);
                if (Util.isTinyScreen(DeskClockApp.getAppContext())) {
                    alarmViewGhostHolder.amPmDisplay.setMaxWidth((int) resources.getDimension(R.dimen.alarm_clock_am_pm_max_width));
                }
            }
            if (alarmBean.alarm.label != null && alarmBean.alarm.label.trim().length() != 0) {
                alarmViewGhostHolder.labelView.setText(alarmBean.alarm.label.trim());
                alarmViewGhostHolder.labelView.setVisibility(0);
            } else {
                alarmViewGhostHolder.labelView.setText("");
                alarmViewGhostHolder.labelView.setVisibility(8);
            }
            if (alarmViewGhostHolder.labelView.getVisibility() == 8 || alarmViewGhostHolder.daysOfWeekView.getVisibility() == 8) {
                alarmViewGhostHolder.daysOfWeekViewRightLine.setVisibility(8);
                alarmViewGhostHolder.daysOfWeekView.setMaxWidth((int) resources.getDimension(R.dimen.alarm_time_days_of_week_max_width_large));
            } else if (this.fontLevel == 2) {
                alarmViewGhostHolder.daysOfWeekViewRightLine.setVisibility(8);
            } else {
                alarmViewGhostHolder.daysOfWeekViewRightLine.setVisibility(0);
                alarmViewGhostHolder.daysOfWeekView.setMaxWidth((int) resources.getDimension(R.dimen.alarm_time_days_of_week_max_width));
            }
            if (!TextUtils.isEmpty(alarmBean.alarmSkipOnceText)) {
                if (alarmViewGhostHolder.labelView.getVisibility() == 0) {
                    alarmViewGhostHolder.labelView.setVisibility(8);
                } else if (this.fontLevel == 2) {
                    alarmViewGhostHolder.daysOfWeekViewRightLine.setVisibility(8);
                } else {
                    alarmViewGhostHolder.daysOfWeekViewRightLine.setVisibility(0);
                }
                alarmViewGhostHolder.alarmInFutureView.setVisibility(0);
                alarmViewGhostHolder.alarmInFutureView.setText(alarmBean.alarmSkipOnceText);
            } else {
                alarmViewGhostHolder.alarmInFutureView.setVisibility(8);
            }
            if (alarmBean.alarm.id != i) {
                alarmViewGhostHolder.clockOnOff.setChecked(alarmBean.alarm.enabled);
            }
            setClockItemTextColor(alarmViewGhostHolder, alarmBean.alarm.enabled);
        }

        private void setClockItemTextColor(AlarmViewGhostHolder alarmViewGhostHolder, boolean z) {
            Resources resources = alarmViewGhostHolder.itemView.getResources();
            int color = resources.getColor(z ? R.color.alarm_item_time_display_color_enable : R.color.alarm_item_time_display_color_disable);
            alarmViewGhostHolder.timeDisplay.setTextColor(color);
            alarmViewGhostHolder.amPmDisplay.setTextColor(color);
            alarmViewGhostHolder.daysOfWeekViewRightLine.setTextColor(resources.getColor(z ? R.color.alarm_item_line_enable : R.color.alarm_item_line_disable));
            int color2 = resources.getColor(z ? R.color.alarm_item_info_enable : R.color.alarm_item_info_disable);
            alarmViewGhostHolder.labelView.setTextColor(color2);
            alarmViewGhostHolder.daysOfWeekView.setTextColor(color2);
            alarmViewGhostHolder.alarmInFutureView.setTextColor(color2);
        }
    }

    private void createHandler() {
        if (this.mAsyncHandler == null) {
            HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
            this.mHandlerThread = handlerThread;
            handlerThread.start();
            this.mAsyncHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: com.android.deskclock.alarm.AlarmEditDialogView.4
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    super.handleMessage(message);
                    if (AlarmEditDialogView.this.mHandler != null) {
                        String alarmInFuture = SetAlarmController.getAlarmInFuture(AlarmEditDialogView.this.mCalender, AlarmEditDialogView.this.mActivity, AlarmEditDialogView.this.mHour, AlarmEditDialogView.this.mMinute, AlarmEditDialogView.this.mAlarm.daysOfWeek);
                        Message messageObtainMessage = AlarmEditDialogView.this.mHandler.obtainMessage(0);
                        messageObtainMessage.obj = alarmInFuture;
                        AlarmEditDialogView.this.mHandler.removeMessages(0);
                        AlarmEditDialogView.this.mHandler.sendMessage(messageObtainMessage);
                    }
                }
            };
        }
        if (this.mHandler == null) {
            this.mHandler = new Handler() { // from class: com.android.deskclock.alarm.AlarmEditDialogView.5
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    if (AlarmEditDialogView.this.mAlarmInFutureView == null || message.obj == null) {
                        return;
                    }
                    AlarmEditDialogView.this.mAlarmInFutureView.setText(message.obj.toString());
                }
            };
        }
    }

    private void releaseHandler() {
        Handler handler = this.mAsyncHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            HandlerThread handlerThread = this.mHandlerThread;
            if (handlerThread != null) {
                handlerThread.quitSafely();
            }
            this.mAsyncHandler = null;
        }
        Handler handler2 = this.mHandler;
        if (handler2 != null) {
            handler2.removeCallbacksAndMessages(null);
        }
    }

    private void restoreAccessibilityState() {
        View view = this.mRootContentView;
        if (view != null) {
            view.setImportantForAccessibility(this.mRootContentAccessibilityImportant);
            this.mRootContentView = null;
        }
    }
}
