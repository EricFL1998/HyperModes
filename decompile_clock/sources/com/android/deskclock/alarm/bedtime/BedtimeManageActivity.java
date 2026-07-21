package com.android.deskclock.alarm.bedtime;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.R2;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.alarm.SetAlarmController;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.UiUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.SimpleDialogFragment;
import com.android.deskclock.view.pref.AlarmCheckboxLayout;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.widget.ActionSheet;
import miuix.popupwidget.widget.GuidePopupWindow;
import miuix.responsive.map.ScreenSpec;
import miuix.springback.view.SpringBackLayout;

/* JADX INFO: loaded from: classes.dex */
public class BedtimeManageActivity extends BaseActivity implements View.OnClickListener, AlarmCheckboxLayout.OnPreferenceChangeListener {
    public static final String KEY_WAKE_UP_ALERT = "key_wake_up_alert";
    public static final String NOTIFICATION_CLICK = "notification_click";
    private static final int SLEEP_ALARM_INDEX = 0;
    private static final String TAG = "DC:BedtimeManageActivity";
    private static final int WAKE_ALARM_INDEX = 1;
    private FormatBedtimeView getUpTimeFormatView;
    private IntentFilter intentFilter;
    int lowLimitTime;
    private Activity mActivity;
    private BedtimeAlarmEditDialog mAlarmEditDialog;
    private BedtimeEditDialogView mAlarmEditDialogView;
    private RelativeLayout mBedtimeManageView;
    private SpringBackLayout mBedtimeScrollView;
    private View mBtnBgView;
    private Button mButton;
    private ActionSheet.IActionSheet mCurrentActionSheet;
    private AlertDialog mHealthAppDialog;
    private InitOriginalAlarmAsyncTask mInitOriginalAlarmAsyncTask;
    private boolean mScreenChanged;
    private Alarm mSleepAlarm;
    private View mTimeDisplay;
    private Alarm mWakeAlarm;
    private AlarmCheckboxLayout mWakeAlertPref;
    private MiHomeHelper miHomeHelper;
    public ImageView moreSettingsIcon;
    private List<SleepRecord> sleepRecordList;
    private SleepRecordObserver sleepRecordObserver;
    private List<SleepSummary> sleepSummaryList;
    private FormatBedtimeView sleepTimeFormatView;
    private View sleepTimeView;
    int upLimitTime;
    private WakeAlarmObserver wakeAlarmObserver;
    private View wakeUpTimeView;
    private SimpleDialogFragment mCloseConfirmDialog = null;
    private boolean showToast = true;
    private boolean editTimeSuccess = false;
    private GuidePopupWindow mBedtimePopupWindow = null;
    private boolean mIsActionSheetShowing = false;

    @Override // miuix.appcompat.app.AppCompatActivity
    protected boolean isResponsiveEnabled() {
        return true;
    }

    private static class QueryHealthDataAsyncTask extends AsyncTask<Void, Void, HealthDataResult> {
        private WeakReference<BedtimeManageActivity> mBedtimeManageActivity;

        public QueryHealthDataAsyncTask(BedtimeManageActivity bedtimeManageActivity) {
            this.mBedtimeManageActivity = new WeakReference<>(bedtimeManageActivity);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public HealthDataResult doInBackground(Void... voidArr) {
            if (this.mBedtimeManageActivity.get() != null) {
                return HealthDataUtil.queryHealthData(DeskClockApp.getAppDEContext());
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(HealthDataResult healthDataResult) {
            BedtimeManageActivity bedtimeManageActivity = this.mBedtimeManageActivity.get();
            if (bedtimeManageActivity == null) {
                return;
            }
            if (healthDataResult == null) {
                bedtimeManageActivity.drawNoRecordChart();
                return;
            }
            bedtimeManageActivity.setSleepRecordList(healthDataResult.sleepRecordList);
            bedtimeManageActivity.setSleepSummaryList(healthDataResult.sleepSummarytList);
            bedtimeManageActivity.setUpLimitTime(healthDataResult.upLimitTime);
            bedtimeManageActivity.setLowLimitTime(healthDataResult.lowLimitTime);
            bedtimeManageActivity.drawRecordChart();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSleepRecordList(List<SleepRecord> list) {
        this.sleepRecordList = list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSleepSummaryList(List<SleepSummary> list) {
        this.sleepSummaryList = list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setUpLimitTime(int i) {
        this.upLimitTime = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLowLimitTime(int i) {
        this.lowLimitTime = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drawNoRecordChart() {
        Log.i(TAG, "drawNoRecordChart ");
        setToOpenText();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drawRecordChart() {
        Log.i(TAG, "drawRecordChart ");
        setAvgSleepDuration();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Resources resources;
        int i;
        super.onCreate(bundle);
        Log.d(TAG, "BedtimeManageActivity onCreate");
        this.mActivity = this;
        setContentView(R.layout.bedtime_manage);
        addMiuiActionBar();
        startOriginalAlarmTask();
        Button button = (Button) findViewById(R.id.look_sleep_data);
        this.mButton = button;
        button.setOnClickListener(this);
        ViewCompat.setOnApplyWindowInsetsListener(this.mButton, new OnApplyWindowInsetsListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.1
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                WindowInsetsCompat rootWindowInsets;
                if (!MiuixUIUtils.isLayoutHideNavigation(view) || (rootWindowInsets = ViewCompat.getRootWindowInsets(view)) == null) {
                    return windowInsetsCompat;
                }
                Insets insets = rootWindowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
                if (BedtimeManageActivity.this.mButton != null) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) BedtimeManageActivity.this.mButton.getLayoutParams();
                    layoutParams.bottomMargin = insets.bottom + BedtimeManageActivity.this.getResources().getDimensionPixelOffset(R.dimen.bedtime_alarm_btn_margin_bottom);
                    BedtimeManageActivity.this.mButton.setLayoutParams(layoutParams);
                }
                return windowInsetsCompat;
            }
        });
        this.mBedtimeManageView = (RelativeLayout) findViewById(R.id.bedtime_manage_view);
        if (isInFloatingWindowMode()) {
            resources = getResources();
            i = R.dimen.bedtime_manage_floating_padding_margin;
        } else {
            resources = getResources();
            i = R.dimen.bedtime_manage_padding_margin;
        }
        int dimension = (int) resources.getDimension(i);
        this.mBedtimeManageView.setPadding(dimension, 0, dimension, 0);
        this.mBedtimeScrollView = (SpringBackLayout) findViewById(R.id.bedtime_scroll_view);
        findViewById(R.id.scroll_view).setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (BedtimeManageActivity.this.mAlarmEditDialogView == null || !BedtimeManageActivity.this.mAlarmEditDialogView.isShowing()) {
                    return false;
                }
                BedtimeManageActivity.this.mAlarmEditDialogView.dismissDirectly();
                Log.d(BedtimeManageActivity.TAG, "Animation interrupted by scroll touch");
                return false;
            }
        });
        AlarmCheckboxLayout alarmCheckboxLayout = (AlarmCheckboxLayout) findViewById(R.id.get_up_alert);
        this.mWakeAlertPref = alarmCheckboxLayout;
        alarmCheckboxLayout.setOnPreferenceChangeListener(this);
        this.mTimeDisplay = findViewById(R.id.bedtime_display);
        this.sleepTimeFormatView = (FormatBedtimeView) findViewById(R.id.sleep_time);
        this.getUpTimeFormatView = (FormatBedtimeView) findViewById(R.id.get_up_time);
        if (Util.isDeviceCetus() && !Util.isInInternalScreen(this)) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.setMarginStart((int) getResources().getDimension(R.dimen.bedtime_alarm_cetus_start));
            this.sleepTimeFormatView.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams2.addRule(21);
            layoutParams2.setMarginEnd((int) getResources().getDimension(R.dimen.bedtime_alarm_cetus_start));
            this.getUpTimeFormatView.setLayoutParams(layoutParams2);
        }
        setupClickListeners();
        setupFonts();
        IntentFilter intentFilter = new IntentFilter();
        this.intentFilter = intentFilter;
        intentFilter.addAction(BedtimeUtil.ACTION_ENABLE_WAKE_ALARM);
        this.wakeAlarmObserver = new WakeAlarmObserver(this);
        getContentResolver().registerContentObserver(SleepAlarmTable.CONTENT_URI, false, this.wakeAlarmObserver);
        this.sleepRecordObserver = new SleepRecordObserver(this);
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.3
            @Override // java.lang.Runnable
            public void run() {
                if (HealthDataUtil.isHealthAppValuable(BedtimeManageActivity.this)) {
                    try {
                        BedtimeManageActivity.this.getContentResolver().registerContentObserver(Uri.parse("content://com.mi.health.provider.main/sleep/record"), false, BedtimeManageActivity.this.sleepRecordObserver);
                    } catch (Exception e) {
                        Log.e(BedtimeManageActivity.TAG, "error " + e.getMessage());
                    }
                }
                XiaoAiRingtoneHelper.resetEnableValue();
            }
        });
        handleIntentExtras();
        if (Util.isInternational()) {
            this.mButton.setVisibility(8);
            this.mBedtimeScrollView.setPadding(0, 0, 0, 0);
        }
    }

    private void startOriginalAlarmTask() {
        cancelOriginalAlarmTask();
        InitOriginalAlarmAsyncTask initOriginalAlarmAsyncTask = new InitOriginalAlarmAsyncTask(this);
        this.mInitOriginalAlarmAsyncTask = initOriginalAlarmAsyncTask;
        initOriginalAlarmAsyncTask.execute(new Void[0]);
    }

    private void cancelOriginalAlarmTask() {
        InitOriginalAlarmAsyncTask initOriginalAlarmAsyncTask = this.mInitOriginalAlarmAsyncTask;
        if (initOriginalAlarmAsyncTask != null) {
            initOriginalAlarmAsyncTask.cancel(true);
            this.mInitOriginalAlarmAsyncTask = null;
        }
    }

    private static class InitOriginalAlarmAsyncTask extends AsyncTask<Void, Void, Alarm> {
        private WeakReference<BedtimeManageActivity> mReference;

        public InitOriginalAlarmAsyncTask(BedtimeManageActivity bedtimeManageActivity) {
            this.mReference = new WeakReference<>(bedtimeManageActivity);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Alarm doInBackground(Void... voidArr) {
            WeakReference<BedtimeManageActivity> weakReference = this.mReference;
            BedtimeManageActivity bedtimeManageActivity = weakReference == null ? null : weakReference.get();
            if (bedtimeManageActivity == null) {
                return null;
            }
            Alarm wakeAlarm = BedtimeUtil.getWakeAlarm(bedtimeManageActivity);
            if (wakeAlarm != null) {
                return wakeAlarm;
            }
            Alarm alarm = new Alarm();
            alarm.daysOfWeek.setDay(127);
            alarm.hour = 6;
            alarm.minutes = 0;
            return alarm;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Alarm alarm) {
            WeakReference<BedtimeManageActivity> weakReference = this.mReference;
            BedtimeManageActivity bedtimeManageActivity = weakReference == null ? null : weakReference.get();
            if (bedtimeManageActivity != null) {
                bedtimeManageActivity.mWakeAlarm = alarm;
                bedtimeManageActivity.mSleepAlarm = new Alarm();
                bedtimeManageActivity.mSleepAlarm.hour = BedtimeUtil.getSleepAlarmHour(bedtimeManageActivity);
                bedtimeManageActivity.mSleepAlarm.minutes = BedtimeUtil.getSleepAlarmMin(bedtimeManageActivity);
                bedtimeManageActivity.updateSleepAlarmUI();
                bedtimeManageActivity.updateWakeAlarmUI();
            }
        }
    }

    private void handleIntentExtras() {
        Intent intent = getIntent();
        if (intent.hasExtra("notification_click") && intent.getBooleanExtra("notification_click", false)) {
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, "notification_click");
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.BEDTIME_NOTIFICATION_CLICK);
        }
    }

    private void setupFonts() {
        TextView textView = (TextView) this.getUpTimeFormatView.findViewById(R.id.bedtime_desc);
        TextView textView2 = (TextView) this.sleepTimeFormatView.findViewById(R.id.bedtime_desc);
        textView.setText(R.string.wake_up_time);
        textView.setSelected(true);
        textView2.setSelected(true);
        if (MiuiSdk.isSupportFontAnim()) {
            MiuiFont.setFont(textView, MiuiFont.MI_PRO_REGULAR);
            MiuiFont.setFont(textView2, MiuiFont.MI_PRO_REGULAR);
        }
        ((ImageView) this.getUpTimeFormatView.findViewById(R.id.time_display_icon)).setImageResource(R.drawable.ic_get_up_time);
    }

    private void setupClickListeners() {
        View viewFindViewById = this.sleepTimeFormatView.findViewById(R.id.touch_region);
        this.sleepTimeView = viewFindViewById;
        viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_TIME_EDIT_CLICK);
                BedtimeManageActivity.this.ensureWakeAlarmExists();
                BedtimeManageActivity bedtimeManageActivity = BedtimeManageActivity.this;
                bedtimeManageActivity.showAlarmEditDialog(bedtimeManageActivity.mSleepAlarm, 0, BedtimeManageActivity.this.mWakeAlarm);
            }
        });
        View viewFindViewById2 = this.getUpTimeFormatView.findViewById(R.id.touch_region);
        this.wakeUpTimeView = viewFindViewById2;
        viewFindViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.WAKE_TIME_EDIT_CLICK);
                BedtimeManageActivity.this.ensureSleepAlarmExists();
                BedtimeManageActivity bedtimeManageActivity = BedtimeManageActivity.this;
                bedtimeManageActivity.showAlarmEditDialog(bedtimeManageActivity.mWakeAlarm, 1, BedtimeManageActivity.this.mSleepAlarm);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureSleepAlarmExists() {
        if (this.mSleepAlarm == null) {
            Alarm alarm = new Alarm();
            this.mSleepAlarm = alarm;
            alarm.hour = BedtimeUtil.getSleepAlarmHour(this);
            this.mSleepAlarm.minutes = BedtimeUtil.getSleepAlarmMin(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureWakeAlarmExists() {
        if (this.mWakeAlarm == null) {
            Alarm alarm = new Alarm();
            this.mWakeAlarm = alarm;
            alarm.daysOfWeek.setDay(127);
            this.mWakeAlarm.hour = 6;
            this.mWakeAlarm.minutes = 0;
        }
    }

    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BedtimeManageActivity  onDestroy");
        if (this.wakeAlarmObserver != null) {
            getContentResolver().unregisterContentObserver(this.wakeAlarmObserver);
        }
        if (this.sleepRecordObserver != null) {
            getContentResolver().unregisterContentObserver(this.sleepRecordObserver);
        }
        MiHomeHelper miHomeHelper = this.miHomeHelper;
        if (miHomeHelper != null) {
            miHomeHelper.release();
            this.miHomeHelper = null;
        }
        GuidePopupWindow guidePopupWindow = this.mBedtimePopupWindow;
        if (guidePopupWindow != null) {
            guidePopupWindow.dismiss();
        }
    }

    private void setToOpenText() {
        StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_CHART_HIDE);
        OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.BEDTIME_SLEEP_CHART_HIDE);
        if (!BedtimeUtil.isBedtimeOpen(this)) {
            this.mButton.setText(R.string.open_sleep_record);
        } else {
            this.mButton.setText(R.string.look_sleep_data);
        }
    }

    private void setAvgSleepDuration() {
        int i;
        int i2;
        int i3;
        this.mButton.setText(R.string.look_sleep_data);
        long j = 0;
        if (this.sleepSummaryList != null) {
            i = 0;
            long j2 = 0;
            for (int i4 = 0; i4 < 7; i4++) {
                long j3 = this.sleepSummaryList.get(i4).duration;
                if (j3 > 0) {
                    j2 += j3;
                    i++;
                }
            }
            j = j2;
        } else {
            i = 0;
        }
        if (i != 0) {
            int i5 = (int) (j / ((long) (i * 60000)));
            i3 = i5 / 60;
            i2 = i5 % 60;
        } else {
            i2 = 0;
            i3 = 0;
        }
        if (i3 == 0 && i2 == 0) {
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_CHART_SHOW_NO_DATA);
            OneTrackStatHelper.trackBoolEvent(false, OneTrackStatHelper.BEDTIME_SLEEP_CHART_SHOW_WITH_DATA);
        } else {
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_CHART_SHOW);
            OneTrackStatHelper.trackBoolEvent(true, OneTrackStatHelper.BEDTIME_SLEEP_CHART_SHOW_WITH_DATA);
        }
        Calendar.getInstance().add(5, -6);
        StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_HEALTH_SHOW);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSleepAlarmUI() {
        if (this.sleepTimeFormatView == null || this.mSleepAlarm == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, this.mSleepAlarm.hour);
        calendar.set(12, this.mSleepAlarm.minutes);
        this.sleepTimeFormatView.updateTime(calendar);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWakeAlarmUI() {
        if (this.getUpTimeFormatView == null || this.mWakeAlarm == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, this.mWakeAlarm.hour);
        calendar.set(12, this.mWakeAlarm.minutes);
        this.getUpTimeFormatView.updateTime(calendar);
    }

    private void setStatus() {
        if (this.mWakeAlertPref == null || this.mTimeDisplay == null) {
            return;
        }
        if (!BedtimeUtil.isBedtimeOpen(this)) {
            this.mWakeAlertPref.setChecked(false);
            this.mWakeAlertPref.setAvailable(false);
            this.sleepTimeView.setClickable(false);
            this.wakeUpTimeView.setClickable(false);
            this.mTimeDisplay.setAlpha(0.2f);
            GuidePopupWindow guidePopupWindow = this.mBedtimePopupWindow;
            if (guidePopupWindow != null) {
                guidePopupWindow.dismiss();
            }
            if (BedtimeUtil.getManageGuideShowed(this)) {
                return;
            }
            GuidePopupWindow guidePopupWindow2 = new GuidePopupWindow(this);
            this.mBedtimePopupWindow = guidePopupWindow2;
            guidePopupWindow2.setBackgroundDrawable(new ColorDrawable(0));
            this.mBedtimePopupWindow.setGuideText(R.string.bedtime_manage_open);
            this.mBedtimePopupWindow.setShowDuration(R2.color.word_photo_color);
            this.moreSettingsIcon.postDelayed(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.6
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        if (!Util.isInInternalScreen(BedtimeManageActivity.this) || BedtimeManageActivity.this.mActivity.isInMultiWindowMode()) {
                            BedtimeManageActivity.this.mBedtimePopupWindow.setArrowMode(10);
                            BedtimeManageActivity.this.mBedtimePopupWindow.show(BedtimeManageActivity.this.moreSettingsIcon, -(BedtimeManageActivity.this.moreSettingsIcon.getWidth() / 2), 0, true);
                        } else {
                            BedtimeManageActivity.this.mBedtimePopupWindow.show(BedtimeManageActivity.this.moreSettingsIcon, 0, 0, true);
                        }
                    } catch (Exception e) {
                        Log.e(BedtimeManageActivity.TAG, "GuidePopupWindow show error: " + e);
                    }
                }
            }, 300L);
            BedtimeUtil.setManageGuideShowed(this, true);
            return;
        }
        resetWakeAlertPref();
        this.mTimeDisplay.setAlpha(1.0f);
        this.mWakeAlertPref.setAvailable(true);
        this.sleepTimeView.setClickable(true);
        this.wakeUpTimeView.setClickable(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetWakeAlertPref() {
        Alarm wakeAlarm = BedtimeUtil.getWakeAlarm(this);
        this.mWakeAlarm = wakeAlarm;
        AlarmCheckboxLayout alarmCheckboxLayout = this.mWakeAlertPref;
        if (alarmCheckboxLayout == null || wakeAlarm == null) {
            return;
        }
        alarmCheckboxLayout.setChecked(wakeAlarm.enabled);
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        ViewGroup viewGroup;
        super.onResume();
        if (!isInFloatingWindowMode() && (viewGroup = (ViewGroup) findViewById(R.id.action_bar_overlay_layout)) != null) {
            viewGroup.setBackground(new ColorDrawable(getResources().getColor(R.color.main_bg)));
        }
        setStatus();
        doDrawChartTask();
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        Log.d(TAG, "BedtimeManageActivity  onStop");
        if (Util.isDeviceRuyiOrBixi() && Util.getCurrentFoldState() == 0) {
            finish();
        }
    }

    private void addMiuiActionBar() {
        ActionBar appCompatActionBar = getAppCompatActionBar();
        appCompatActionBar.setTitle(R.string.bedtime);
        appCompatActionBar.setDisplayHomeAsUpEnabled(false);
        ImageView floatPageBackIcon = UiUtil.getFloatPageBackIcon(this);
        floatPageBackIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m83x119c3ff6(view);
            }
        });
        floatPageBackIcon.setContentDescription(getResources().getString(R.string.go_back));
        appCompatActionBar.setStartView(floatPageBackIcon);
        ImageView imageView = new ImageView(this);
        this.moreSettingsIcon = imageView;
        imageView.setImageResource(R.drawable.icon_settings);
        this.moreSettingsIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BedtimeManageActivity.this.startActivity(new Intent(BedtimeManageActivity.this, (Class<?>) BedtimeSettingsActivity.class));
            }
        });
        this.moreSettingsIcon.setContentDescription(getResources().getString(R.string.more_setting));
        appCompatActionBar.setEndView(this.moreSettingsIcon);
        appCompatActionBar.setDisplayShowCustomEnabled(false);
        appCompatActionBar.setDisplayShowTitleEnabled(true);
    }

    /* JADX INFO: renamed from: lambda$addMiuiActionBar$0$com-android-deskclock-alarm-bedtime-BedtimeManageActivity, reason: not valid java name */
    /* synthetic */ void m83x119c3ff6(View view) {
        onBackPressed();
    }

    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        BedtimeEditDialogView bedtimeEditDialogView = this.mAlarmEditDialogView;
        if (bedtimeEditDialogView == null || !bedtimeEditDialogView.isShowing()) {
            return;
        }
        this.sleepTimeView.postDelayed(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.8
            @Override // java.lang.Runnable
            public void run() {
                int[] iArr = new int[2];
                BedtimeManageActivity.this.sleepTimeView.getLocationInWindow(iArr);
                int i = iArr[0];
                if (BedtimeManageActivity.this.mAlarmEditDialogView.isSleepTimeShowing()) {
                    BedtimeManageActivity.this.mAlarmEditDialogView.showDirectly(BedtimeManageActivity.this.sleepTimeView, i);
                } else {
                    BedtimeManageActivity.this.mAlarmEditDialogView.showDirectly(BedtimeManageActivity.this.wakeUpTimeView, -i);
                }
            }
        }, 100L);
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        ActionSheet.IActionSheet iActionSheet;
        super.onPause();
        Log.d(TAG, "onPause");
        BedtimeAlarmEditDialog bedtimeAlarmEditDialog = this.mAlarmEditDialog;
        if (bedtimeAlarmEditDialog != null) {
            bedtimeAlarmEditDialog.setOnDismissListener(null);
            this.mAlarmEditDialog.setOnSaveAlarmListener(null);
            DialogUtil.dismissDialogFragment(this.mAlarmEditDialog);
            this.mAlarmEditDialog = null;
        }
        BedtimeEditDialogView bedtimeEditDialogView = this.mAlarmEditDialogView;
        if (bedtimeEditDialogView != null && bedtimeEditDialogView.isShowing()) {
            this.mAlarmEditDialogView.dismissDirectly();
        }
        SimpleDialogFragment simpleDialogFragment = this.mCloseConfirmDialog;
        if (simpleDialogFragment != null && simpleDialogFragment.getDialog() != null && this.mCloseConfirmDialog.getDialog().isShowing()) {
            this.showToast = false;
            this.mWakeAlertPref.setChecked(true);
        }
        if (this.mIsActionSheetShowing && (iActionSheet = this.mCurrentActionSheet) != null) {
            iActionSheet.dismiss();
            this.mWakeAlertPref.setChecked(true);
        }
        DialogUtil.dismissDialogFragment(this.mCloseConfirmDialog);
        this.mCloseConfirmDialog = null;
        DialogUtil.dismissDialog(this.mHealthAppDialog);
        this.mHealthAppDialog = null;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != R.id.look_sleep_data) {
            return;
        }
        if (!BedtimeUtil.isBedtimeOpen(this)) {
            startActivity(new Intent(this, (Class<?>) BedtimeSettingsActivity.class));
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_SLEEP_GO);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.BEDTIME_SLEEP_TO_OPEN_CLICK);
            return;
        }
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.9
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                if (!HealthDataUtil.queryRecordState(BedtimeManageActivity.this)) {
                    StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_RECORD_GO);
                    OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.BEDTIME_RECORD_TO_OPEN_CLICK);
                    if (HealthDataUtil.isSupportSettingsIntent(BedtimeManageActivity.this)) {
                        HealthDataUtil.jumpToHealthSettings(BedtimeManageActivity.this);
                        return;
                    } else {
                        AlarmThreadPool.runOnUiThread(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.9.1
                            @Override // java.lang.Runnable
                            public void run() {
                                BedtimeManageActivity.this.showDownloadHealthDialog();
                            }
                        });
                        return;
                    }
                }
                HealthDataUtil.jumpToHealthData(BedtimeManageActivity.this);
                StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_HEALTH_CLICK);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.BEDTIME_SLEEP_HEALTH_CLICK);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDownloadHealthDialog() {
        AlertDialog alertDialog = this.mHealthAppDialog;
        if (alertDialog != null) {
            DialogUtil.dismissDialog(alertDialog);
            this.mHealthAppDialog = null;
        }
        AlertDialog alertDialogCreate = new AlertDialog.Builder(this).setTitle(R.string.update_health_app_new).setMessage(R.string.update_health_app_desc_new).setNegativeButton(R.string.not_update, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.update_it, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.10
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                HealthDataUtil.downloadHealthApp(BedtimeManageActivity.this);
            }
        }).create();
        this.mHealthAppDialog = alertDialogCreate;
        alertDialogCreate.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAlarmEditDialog(Alarm alarm, final int i, Alarm alarm2) {
        Log.i(TAG, "show alarm edit dialog : " + i);
        BedtimeAlarmEditDialog bedtimeAlarmEditDialog = this.mAlarmEditDialog;
        if (bedtimeAlarmEditDialog == null || !bedtimeAlarmEditDialog.isShowing()) {
            BedtimeEditDialogView bedtimeEditDialogView = this.mAlarmEditDialogView;
            if (bedtimeEditDialogView == null || !bedtimeEditDialogView.isShowing()) {
                OneTrackStatHelper.trackStringEvent(i == 0 ? "sleep" : "wake", OneTrackStatHelper.BEDTIME_TIME_EDIT_DIALOG);
                if (!MiuiSdk.isLiteOrMiddleMode()) {
                    BedtimeEditDialogView bedtimeEditDialogView2 = this.mAlarmEditDialogView;
                    if (bedtimeEditDialogView2 != null) {
                        bedtimeEditDialogView2.dismissDirectly();
                    }
                    this.mAlarmEditDialogView = new BedtimeEditDialogView(this);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("alarm", alarm);
                    bundle.putInt("index", i);
                    bundle.putInt("invalid_hour", alarm2.hour);
                    bundle.putInt("invalid_min", alarm2.minutes);
                    bundle.putSerializable("calender", Calendar.getInstance());
                    this.mAlarmEditDialogView.setAlarm(bundle);
                    this.mAlarmEditDialogView.setOnDismissListener(new BedtimeEditDialogView.OnDismissListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.11
                        @Override // com.android.deskclock.alarm.bedtime.BedtimeEditDialogView.OnDismissListener
                        public void onDismiss() {
                            if (BedtimeManageActivity.this.editTimeSuccess) {
                                BedtimeManageActivity.this.editTimeSuccess = false;
                            } else if (i == 0) {
                                StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_TIME_EDIT_CANCEL);
                            } else {
                                StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.WAKE_TIME_EDIT_CANCEL);
                            }
                        }
                    });
                    this.mAlarmEditDialogView.setOnSaveAlarmListener(new BedtimeEditDialogView.OnSaveAlarmListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.12
                        /* JADX WARN: Type inference fix 'apply assigned field type' failed
                        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
                        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
                        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
                        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
                        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
                        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
                        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
                        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
                         */
                        @Override // com.android.deskclock.alarm.bedtime.BedtimeEditDialogView.OnSaveAlarmListener
                        public void onSaveAlarm(Alarm alarm3) {
                            BedtimeManageActivity.this.editTimeSuccess = true;
                            if (i == 0) {
                                if (BedtimeManageActivity.this.mSleepAlarm == null || alarm3 == null) {
                                    return;
                                }
                                BedtimeManageActivity.this.mSleepAlarm.hour = alarm3.hour;
                                BedtimeManageActivity.this.mSleepAlarm.minutes = alarm3.minutes;
                                BedtimeManageActivity.this.updateSleepAlarmUI();
                                Log.i(BedtimeManageActivity.TAG, "reset sleep alarm, hour: " + BedtimeManageActivity.this.mSleepAlarm.hour + " minutes: " + BedtimeManageActivity.this.mSleepAlarm.minutes);
                                BedtimeManageActivity bedtimeManageActivity = BedtimeManageActivity.this;
                                BedtimeUtil.saveSleepAlarm(bedtimeManageActivity, bedtimeManageActivity.mSleepAlarm);
                                BedtimeManageActivity bedtimeManageActivity2 = BedtimeManageActivity.this;
                                bedtimeManageActivity2.notifyBedtimeChanged(bedtimeManageActivity2);
                                StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_TIME_EDIT_SUCCESS);
                                OneTrackStatHelper.trackStringEvent("sleep", OneTrackStatHelper.BEDTIME_TIME_EDIT_SUCCESS);
                                AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.12.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        try {
                                            if (HealthDataUtil.isHealthAppValuable(BedtimeManageActivity.this)) {
                                                HealthDataUtil.updateSleepSchedule(BedtimeManageActivity.this, BedtimeManageActivity.this.mSleepAlarm.hour, BedtimeManageActivity.this.mSleepAlarm.minutes);
                                                Log.i(BedtimeManageActivity.TAG, "reset sleep time to health app, hour: " + BedtimeManageActivity.this.mSleepAlarm.hour + " minutes: " + BedtimeManageActivity.this.mSleepAlarm.minutes);
                                            }
                                        } catch (Exception e) {
                                            Log.e(BedtimeManageActivity.TAG, "onSaveAlarm runnable error: " + e);
                                        }
                                    }
                                });
                                BedtimeManageActivity.this.doDrawChartTask();
                            } else {
                                if (BedtimeManageActivity.this.mWakeAlarm == null || alarm3 == null) {
                                    return;
                                }
                                BedtimeManageActivity.this.mWakeAlarm.hour = alarm3.hour;
                                BedtimeManageActivity.this.mWakeAlarm.minutes = alarm3.minutes;
                                BedtimeManageActivity.this.mWakeAlarm.enabled = true;
                                BedtimeManageActivity.this.mWakeAlarm.skipTime = 0L;
                                BedtimeManageActivity.this.mWakeAlertPref.setChecked(true);
                                BedtimeManageActivity.this.updateWakeAlarmUI();
                                Log.i(BedtimeManageActivity.TAG, "reset wake alarm, hour: " + BedtimeManageActivity.this.mWakeAlarm.hour + " minutes: " + BedtimeManageActivity.this.mWakeAlarm.minutes);
                                BedtimeManageActivity bedtimeManageActivity3 = BedtimeManageActivity.this;
                                AlarmHelper.setWakeAlarm(bedtimeManageActivity3, bedtimeManageActivity3.mWakeAlarm);
                                BedtimeManageActivity bedtimeManageActivity4 = BedtimeManageActivity.this;
                                bedtimeManageActivity4.notifyBedtimeChanged(bedtimeManageActivity4);
                                SetAlarmController.popAlarmSetToast(BedtimeManageActivity.this.mWakeAlarm.hour, BedtimeManageActivity.this.mWakeAlarm.minutes, BedtimeManageActivity.this.mWakeAlarm.daysOfWeek);
                                StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.WAKE_TIME_EDIT_SUCCESS);
                                OneTrackStatHelper.trackStringEvent("wake", OneTrackStatHelper.BEDTIME_TIME_EDIT_SUCCESS);
                                AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.12.2
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        try {
                                            if (HealthDataUtil.isHealthAppValuable(BedtimeManageActivity.this)) {
                                                HealthDataUtil.updateWakeSchedule(BedtimeManageActivity.this, BedtimeManageActivity.this.mWakeAlarm.hour, BedtimeManageActivity.this.mWakeAlarm.minutes);
                                                Log.i(BedtimeManageActivity.TAG, "reset wake time to health app, hour: " + BedtimeManageActivity.this.mWakeAlarm.hour + " minutes: " + BedtimeManageActivity.this.mWakeAlarm.minutes);
                                            }
                                        } catch (Exception unused) {
                                        }
                                    }
                                });
                                BedtimeManageActivity.this.doDrawChartTask();
                            }
                            AlarmHelper.setSleepNotification(BedtimeManageActivity.this);
                            BedtimeManageActivity bedtimeManageActivity5 = BedtimeManageActivity.this;
                            bedtimeManageActivity5.updateDNDSetting(bedtimeManageActivity5);
                        }
                    });
                    int[] iArr = new int[2];
                    this.sleepTimeView.getLocationInWindow(iArr);
                    int i2 = iArr[0];
                    int[] iArr2 = new int[2];
                    this.wakeUpTimeView.getLocationInWindow(iArr2);
                    int i3 = iArr2[0];
                    Util.resetRtl();
                    if (i == 0) {
                        if (Util.isRtl()) {
                            this.mAlarmEditDialogView.show(this.sleepTimeView, -i3);
                        } else {
                            this.mAlarmEditDialogView.show(this.sleepTimeView, i2);
                        }
                    } else if (Util.isRtl()) {
                        this.mAlarmEditDialogView.show(this.wakeUpTimeView, i3);
                    } else {
                        this.mAlarmEditDialogView.show(this.wakeUpTimeView, -i2);
                    }
                    StatHelper.alarmEvent(StatHelper.EVENT_TIME_PICKER_OPEN_COUNT);
                    return;
                }
                this.mAlarmEditDialog = new BedtimeAlarmEditDialog();
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("alarm", alarm);
                bundle2.putInt("index", i);
                bundle2.putInt("invalid_hour", alarm2.hour);
                bundle2.putInt("invalid_min", alarm2.minutes);
                bundle2.putSerializable("calender", Calendar.getInstance());
                this.mAlarmEditDialog.setArguments(bundle2);
                this.mAlarmEditDialog.setOnDismissListener(new BedtimeAlarmEditDialog.OnDismissListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.13
                    @Override // com.android.deskclock.alarm.bedtime.BedtimeAlarmEditDialog.OnDismissListener
                    public void onDismiss() {
                        if (BedtimeManageActivity.this.editTimeSuccess) {
                            BedtimeManageActivity.this.editTimeSuccess = false;
                        } else if (i == 0) {
                            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_TIME_EDIT_CANCEL);
                        } else {
                            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.WAKE_TIME_EDIT_CANCEL);
                        }
                    }
                });
                this.mAlarmEditDialog.setOnSaveAlarmListener(new BedtimeAlarmEditDialog.OnSaveAlarmListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.14
                    /* JADX WARN: Type inference fix 'apply assigned field type' failed
                    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
                    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
                    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
                    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
                     */
                    @Override // com.android.deskclock.alarm.bedtime.BedtimeAlarmEditDialog.OnSaveAlarmListener
                    public void onSaveAlarm(Alarm alarm3) {
                        BedtimeManageActivity.this.editTimeSuccess = true;
                        if (i == 0) {
                            BedtimeManageActivity.this.mSleepAlarm.hour = alarm3.hour;
                            BedtimeManageActivity.this.mSleepAlarm.minutes = alarm3.minutes;
                            BedtimeManageActivity.this.updateSleepAlarmUI();
                            Log.i(BedtimeManageActivity.TAG, "reset sleep alarm, hour: " + BedtimeManageActivity.this.mSleepAlarm.hour + " minutes: " + BedtimeManageActivity.this.mSleepAlarm.minutes);
                            BedtimeManageActivity bedtimeManageActivity = BedtimeManageActivity.this;
                            BedtimeUtil.saveSleepAlarm(bedtimeManageActivity, bedtimeManageActivity.mSleepAlarm);
                            BedtimeManageActivity bedtimeManageActivity2 = BedtimeManageActivity.this;
                            bedtimeManageActivity2.notifyBedtimeChanged(bedtimeManageActivity2);
                            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_TIME_EDIT_SUCCESS);
                            OneTrackStatHelper.trackStringEvent("sleep", OneTrackStatHelper.BEDTIME_TIME_EDIT_SUCCESS);
                            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.14.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    try {
                                        if (HealthDataUtil.isHealthAppValuable(BedtimeManageActivity.this)) {
                                            HealthDataUtil.updateSleepSchedule(BedtimeManageActivity.this, BedtimeManageActivity.this.mSleepAlarm.hour, BedtimeManageActivity.this.mSleepAlarm.minutes);
                                            Log.i(BedtimeManageActivity.TAG, "reset sleep time to health app, hour: " + BedtimeManageActivity.this.mSleepAlarm.hour + " minutes: " + BedtimeManageActivity.this.mSleepAlarm.minutes);
                                        }
                                    } catch (Exception e) {
                                        Log.e(BedtimeManageActivity.TAG, "onSaveAlarm runnable error: " + e);
                                    }
                                }
                            });
                            BedtimeManageActivity.this.doDrawChartTask();
                        } else {
                            BedtimeManageActivity.this.mWakeAlarm.hour = alarm3.hour;
                            BedtimeManageActivity.this.mWakeAlarm.minutes = alarm3.minutes;
                            BedtimeManageActivity.this.mWakeAlarm.enabled = true;
                            BedtimeManageActivity.this.mWakeAlarm.skipTime = 0L;
                            BedtimeManageActivity.this.mWakeAlertPref.setChecked(true);
                            BedtimeManageActivity.this.updateWakeAlarmUI();
                            Log.i(BedtimeManageActivity.TAG, "reset wake alarm, hour: " + BedtimeManageActivity.this.mWakeAlarm.hour + " minutes: " + BedtimeManageActivity.this.mWakeAlarm.minutes);
                            BedtimeManageActivity bedtimeManageActivity3 = BedtimeManageActivity.this;
                            AlarmHelper.setWakeAlarm(bedtimeManageActivity3, bedtimeManageActivity3.mWakeAlarm);
                            BedtimeManageActivity bedtimeManageActivity4 = BedtimeManageActivity.this;
                            bedtimeManageActivity4.notifyBedtimeChanged(bedtimeManageActivity4);
                            SetAlarmController.popAlarmSetToast(BedtimeManageActivity.this.mWakeAlarm.hour, BedtimeManageActivity.this.mWakeAlarm.minutes, BedtimeManageActivity.this.mWakeAlarm.daysOfWeek);
                            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.WAKE_TIME_EDIT_SUCCESS);
                            OneTrackStatHelper.trackStringEvent("wake", OneTrackStatHelper.BEDTIME_TIME_EDIT_SUCCESS);
                            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.14.2
                                @Override // java.lang.Runnable
                                public void run() {
                                    try {
                                        if (HealthDataUtil.isHealthAppValuable(BedtimeManageActivity.this)) {
                                            HealthDataUtil.updateWakeSchedule(BedtimeManageActivity.this, BedtimeManageActivity.this.mWakeAlarm.hour, BedtimeManageActivity.this.mWakeAlarm.minutes);
                                            Log.i(BedtimeManageActivity.TAG, "reset wake time to health app, hour: " + BedtimeManageActivity.this.mWakeAlarm.hour + " minutes: " + BedtimeManageActivity.this.mWakeAlarm.minutes);
                                        }
                                    } catch (Exception unused) {
                                    }
                                }
                            });
                            BedtimeManageActivity.this.doDrawChartTask();
                        }
                        AlarmHelper.setSleepNotification(BedtimeManageActivity.this);
                        BedtimeManageActivity bedtimeManageActivity5 = BedtimeManageActivity.this;
                        bedtimeManageActivity5.updateDNDSetting(bedtimeManageActivity5);
                    }
                });
                this.mAlarmEditDialog.show(getSupportFragmentManager(), BedtimeAlarmEditDialog.TAG);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDrawChartTask() {
        if (Util.isInternational()) {
            return;
        }
        new QueryHealthDataAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    @Override // com.android.deskclock.view.pref.AlarmCheckboxLayout.OnPreferenceChangeListener
    public void onPreferenceChange(String str, boolean z) {
        if (!TextUtils.isEmpty(str) && str.equals(KEY_WAKE_UP_ALERT)) {
            Log.i(TAG, "reset wake alarm enable: " + Boolean.valueOf(z));
            if (BedtimeUtil.isBedtimeOpen(this)) {
                Alarm alarm = this.mWakeAlarm;
                if (alarm == null || z || alarm.skipTime == 0) {
                    if (!z) {
                        Log.i(TAG, "onPreferenceChange and showRepeatAlarmTurnOffDialog");
                        showRepeatAlarmTurnOffDialog(this.mWakeAlarm, this.mWakeAlertPref);
                        return;
                    }
                    Alarm alarm2 = this.mWakeAlarm;
                    if (alarm2 == null || alarm2.enabled == z) {
                        return;
                    }
                    this.mWakeAlarm.enabled = Boolean.valueOf(z).booleanValue();
                    Log.i(TAG, "onPreferenceChange, enable alarm newValue : " + Boolean.valueOf(z));
                    AlarmHelper.enableAlarm(this, this.mWakeAlarm.id, z);
                    if (this.showToast) {
                        SetAlarmController.popAlarmSetToast(this.mWakeAlarm.hour, this.mWakeAlarm.minutes, this.mWakeAlarm.daysOfWeek);
                    }
                    this.showToast = true;
                }
            }
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        float dimension;
        super.onResponsiveLayout(configuration, screenSpec, z);
        Log.d(TAG, "onResponsiveLayout");
        if (Util.isDeviceCetus() && (Util.isGridSplitScreen(this) || !Util.isInInternalScreen(this))) {
            dimension = getResources().getDimension(R.dimen.bedtime_alarm_cetus_start);
        } else {
            dimension = getResources().getDimension(R.dimen.bedtime_alarm_start);
        }
        int i = (int) dimension;
        BedtimeEditDialogView bedtimeEditDialogView = this.mAlarmEditDialogView;
        if (bedtimeEditDialogView != null) {
            bedtimeEditDialogView.dismissDirectly();
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.setMarginStart(i);
        this.sleepTimeFormatView.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.addRule(21);
        layoutParams2.setMarginEnd(i);
        this.getUpTimeFormatView.setLayoutParams(layoutParams2);
        RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) this.mButton.getLayoutParams();
        if (Util.isFoldDevice(this) && (Util.isGridSplitScreen(this) || Util.isFreeFormScreen(configuration))) {
            layoutParams3.setMarginStart((int) getResources().getDimension(R.dimen.bedtime_alarm_btn_split_margin_start));
            layoutParams3.setMarginEnd((int) getResources().getDimension(R.dimen.bedtime_alarm_btn_split_margin_start));
        } else if (Util.isPadOrFoldDeviceFullScreen(this) && !PadAdapterUtil.IS_PAD) {
            layoutParams3.setMarginStart((int) getResources().getDimension(R.dimen.bedtime_alarm_btn_margin_start));
            layoutParams3.setMarginEnd((int) getResources().getDimension(R.dimen.bedtime_alarm_btn_margin_start));
        }
        this.mButton.setLayoutParams(layoutParams3);
        if (this.mScreenChanged) {
            return;
        }
        this.mScreenChanged = z;
    }

    private static class WakeAlarmObserver extends ContentObserver {
        private WeakReference<BedtimeManageActivity> mBedtimeManageActivity;

        public WakeAlarmObserver(BedtimeManageActivity bedtimeManageActivity) {
            super(new Handler());
            this.mBedtimeManageActivity = new WeakReference<>(bedtimeManageActivity);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            BedtimeManageActivity bedtimeManageActivity = this.mBedtimeManageActivity.get();
            if (bedtimeManageActivity != null) {
                bedtimeManageActivity.resetWakeAlertPref();
            }
        }
    }

    private static class SleepRecordObserver extends ContentObserver {
        private WeakReference<BedtimeManageActivity> mBedtimeManageActivity;

        public SleepRecordObserver(BedtimeManageActivity bedtimeManageActivity) {
            super(new Handler());
            this.mBedtimeManageActivity = new WeakReference<>(bedtimeManageActivity);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            Log.i(BedtimeManageActivity.TAG, "SleepRecordObserver onChange ");
            BedtimeManageActivity bedtimeManageActivity = this.mBedtimeManageActivity.get();
            if (bedtimeManageActivity != null) {
                bedtimeManageActivity.doDrawChartTask();
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void showRepeatAlarmTurnOffDialog(final Alarm alarm, final AlarmCheckboxLayout alarmCheckboxLayout) {
        String[] strArr = {Util.getSkipDateString(this, AlarmHelper.calculateAlarmTime(this, alarm.hour, alarm.minutes, alarm.daysOfWeek).getTimeInMillis()), getResources().getString(R.string.turn_off_this_repeat_alarm)};
        ActionSheet.Builder builder = new ActionSheet.Builder(this);
        builder.setActionItems(strArr, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.15
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Log.i(BedtimeManageActivity.TAG, "skip wake alarm for once");
                    AlarmHelper.skipAlarmForOnce(BedtimeManageActivity.this, alarm.id);
                    AlarmHelper.registerWakeAlarm(BedtimeManageActivity.this);
                    BedtimeManageActivity.this.mIsActionSheetShowing = false;
                    BedtimeManageActivity.this.mCurrentActionSheet = null;
                    return;
                }
                if (i != 1) {
                    return;
                }
                Log.i(BedtimeManageActivity.TAG, "enableAlarm wake alarm false");
                AlarmHelper.enableAlarm(BedtimeManageActivity.this, alarm.id, false);
                AlarmHelper.registerWakeAlarm(BedtimeManageActivity.this);
                BedtimeManageActivity.this.mIsActionSheetShowing = false;
                BedtimeManageActivity.this.mCurrentActionSheet = null;
            }
        });
        builder.setSeparateText(getResources().getString(R.string.cancel));
        builder.setSeparateClickListener(new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.16
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                BedtimeManageActivity.this.showToast = false;
                alarmCheckboxLayout.setChecked(true);
                BedtimeManageActivity.this.mIsActionSheetShowing = false;
                BedtimeManageActivity.this.mCurrentActionSheet = null;
            }
        });
        ActionSheet.IActionSheet iActionSheetCreate = builder.create();
        this.mCurrentActionSheet = iActionSheetCreate;
        if (iActionSheetCreate instanceof AlertDialog) {
            ((AlertDialog) iActionSheetCreate).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.deskclock.alarm.bedtime.BedtimeManageActivity.17
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface dialogInterface) {
                    BedtimeManageActivity.this.showToast = false;
                    alarmCheckboxLayout.setChecked(true);
                    BedtimeManageActivity.this.mIsActionSheetShowing = false;
                    BedtimeManageActivity.this.mCurrentActionSheet = null;
                }
            });
        }
        this.mCurrentActionSheet.show();
        this.mIsActionSheetShowing = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDNDSetting(Context context) {
        if (BedtimeUtil.getDisturbanceState(context)) {
            Log.i(TAG, "updateDNDSetting");
            AlarmHelper.setZenMode(context);
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("notification_click") && intent.getBooleanExtra("notification_click", false)) {
            StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, "notification_click");
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.BEDTIME_NOTIFICATION_CLICK);
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        bedtimeMangerPageAnimBack();
        BedtimeEditDialogView bedtimeEditDialogView = this.mAlarmEditDialogView;
        if (bedtimeEditDialogView != null && bedtimeEditDialogView.isShowing()) {
            this.mAlarmEditDialogView.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    private void bedtimeMangerPageAnimBack() {
        boolean booleanExtra = getIntent().getBooleanExtra(Util.IS_NIGHT_MODE_KEY, false);
        if (getIntent().getBooleanExtra(Util.IS_ANIM_BACK_KEY, false)) {
            if (booleanExtra != Util.isNightMode(DeskClockApp.getAppDEContext()) || this.mScreenChanged) {
                setResult(-1, new Intent().putExtra(Util.IS_SWITCH_NIGHT_MODE, true));
            }
        }
    }

    public void notifyBedtimeChanged(Context context) {
        if (this.miHomeHelper == null) {
            this.miHomeHelper = new MiHomeHelper(context);
        }
        this.miHomeHelper.notifyBedtimeChanged();
    }
}
