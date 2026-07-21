package com.android.deskclock.alarm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import com.android.deskclock.Alarm;
import com.android.deskclock.AlarmClockExtras;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.MiuiTheme;
import com.android.deskclock.addition.resource.ExternalResourceUtils;
import com.android.deskclock.addition.resource.MiuiResource;
import com.android.deskclock.addition.resource.ResourceLoadService;
import com.android.deskclock.addition.ringtone.RingtoneConstants;
import com.android.deskclock.addition.ringtone.RingtoneUriCompat;
import com.android.deskclock.addition.ringtone.digital.DigitalTimerRingtoneHelper;
import com.android.deskclock.addition.ringtone.star.WYStarRingtoneHelper;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmAlertHelper;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmController;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmDataHelper;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmGroup;
import com.android.deskclock.appaf.AppSearchUtil;
import com.android.deskclock.interfaces.PermissionRequestCallback;
import com.android.deskclock.settings.AlarmRingtonePickerActivity;
import com.android.deskclock.settings.AlarmSettingsFragment;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.SetAlarmUtil;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.KoreaPermissionUtil;
import com.android.deskclock.util.permission.PermissionUtil;
import com.android.deskclock.util.permission.SystemPermissionUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.LabelDialogFragment;
import com.android.deskclock.view.SimpleDialogFragment;
import com.android.deskclock.widget.TimePicker;
import com.miui.support.cardview.CardView;
import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import miuix.animation.Folme;
import miuix.bottomsheet.BottomSheetBehavior;
import miuix.bottomsheet.BottomSheetModal;
import miuix.core.util.MiuixUIUtils;
import miuix.core.widget.NestedScrollView;
import miuix.internal.util.AnimHelper;

/* JADX INFO: loaded from: classes.dex */
public class SetAlarmController extends AlarmController implements ViewTreeObserver.OnGlobalLayoutListener {
    public static final String IS_SHOW_REPEAT_ALARM_DIALOG = "is_show_repeat_alarm_dialog";
    private static final String KEY_CURRENT_ALARM = "currentAlarm";
    private static final String KEY_ORIGINAL_ALARM = "originalAlarm";
    private static final String KEY_ORIGINAL_SHIFT_ALARM_GROUP = "original_shift_alarm_group";
    private static final String KEY_SHIFT_ALARM_GROUP = "shift_alarm_group";
    private static final String KEY_XIAO_AI_ALARM = "xiao_ai_alarm";
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    public static final String TAG = "DC:SetAlarmController";
    private static final int TIME_PICKER_COUNT = 5;
    private static final int TINY_SCREEN_TIME_PICKER_COUNT = 3;
    public static boolean isBackValid = false;
    private static Toast mAlarmToast;
    private static long mLastClickTime;
    private String[] dayList;
    private Button mActionButton1;
    private Button mActionButton2;
    private DeskClockTabActivity mActivity;
    private Alarm mAlarmChanged;
    private TextView mAlarmInFuture;
    private Uri mAlert;
    private Handler mAsyncHandler;
    private Calendar mCalendar;
    private SimpleDialogFragment mDeleteConfirmDialog;
    private boolean mDownloadAfterServiceBind;
    private boolean mEnableAlarm;
    private Handler mHandler;
    private final HandlerThread mHandlerThread;
    private boolean mHasInit;
    private int mHour;
    private int mId;
    private boolean mIsOldXiaoAiAlarm;
    private boolean mIsSetAlarmDialogFloating;
    private boolean mIsXiaoAiAlarm;
    private String mLabel;
    private LabelDialogFragment mLabelDialogFragment;
    private TextView mLabelSummary;
    private boolean mLoadAfterServiceBind;
    private int mMinute;
    private SimpleDialogFragment mNetPermissionDialog;
    private Uri mOldAlert;
    private LinearLayout mOneShotPreferenceView;
    private TextView mOneShotTitleTv;
    private CheckBox mOneShotValueCb;
    private Alarm mOriginalAlarm;
    private ShiftAlarmGroup mOriginalShiftAlarmGroup;
    private PermissionRequestCallback mPermissionRequestCallback;
    private BroadcastReceiver mReceiver;
    public BottomSheetModal mRepeatAlarmBottomSheetModal;
    public RepeatAlarmController mRepeatAlarmController;
    private View mRepeatAlarmView;
    private BottomSheetBehavior mRepeatBottomSheetBehavior;
    private LinearLayout mRepeatPreferenceView;
    private TextView mRepeatTitleTv;
    private TextView mRepeatValueTv;
    private ResourceLoadServiceCallback mResourceLoadCallback;
    private ResourceLoadServiceConnection mResourceLoadConnection;
    private ResourceLoadService mResourceLoadService;
    private LinearLayout mRingtonePreferenceView;
    private TextView mRingtoneTitleTv;
    private TextView mRingtoneValueTv;
    private View mRootView;
    private ConstraintLayout mSetAlarmLabelView;
    private CardView mSetAlarmOtherView;
    private ShiftAlarmController mShiftAlarmController;
    private ShiftAlarmGroup mShiftAlarmGroup;
    private boolean mSupportWeekRingtone;
    private TimePicker mTimePicker;
    private TextView mTitle;
    private SimpleDialogFragment mUserNoticeDialog;
    private LinearLayout mVibratorPreferenceView;
    private TextView mVibratorTitleTv;
    private CheckBox mVibratorValueCb;
    private SimpleDialogFragment mWeatherRingtoneDownloadDialog;
    private SimpleDialogFragment mWeatherRingtoneIntroduceDialog;
    private SimpleDialogFragment mWeekRingtoneDownloadDialog;
    private SimpleDialogFragment mWeekRingtoneIntroduceDialog;
    private NestedScrollView scrollView;
    private String[] weekdayShort;

    public boolean isInMoreShiftAlarmsPage() {
        Button button;
        CardView cardView = this.mSetAlarmOtherView;
        return cardView != null && cardView.getVisibility() == 8 && (button = this.mActionButton2) != null && button.getVisibility() == 4;
    }

    public SetAlarmController(DeskClockTabActivity deskClockTabActivity, View view, Bundle bundle) {
        super(deskClockTabActivity, view);
        this.mNetPermissionDialog = null;
        this.mCalendar = Calendar.getInstance();
        this.mSupportWeekRingtone = false;
        this.mIsXiaoAiAlarm = false;
        this.mIsOldXiaoAiAlarm = false;
        this.mHasInit = false;
        this.mIsSetAlarmDialogFloating = false;
        this.mUserNoticeDialog = null;
        this.mDeleteConfirmDialog = null;
        this.mLoadAfterServiceBind = false;
        this.mDownloadAfterServiceBind = false;
        this.mActivity = deskClockTabActivity;
        this.mRootView = view;
        HandlerThread handlerThread = new HandlerThread("SetAlarmThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mAsyncHandler = new Handler(handlerThread.getLooper()) { // from class: com.android.deskclock.alarm.SetAlarmController.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                String string;
                super.handleMessage(message);
                if (SetAlarmController.this.mEnableAlarm) {
                    if (!RepeatAlarmController.mDaysOfWeek.isShiftAlarm() || SetAlarmController.this.mShiftAlarmGroup == null) {
                        Calendar calendar = SetAlarmController.this.mCalendar;
                        Context appDEContext = DeskClockApp.getAppDEContext();
                        int i = SetAlarmController.this.mHour;
                        int i2 = SetAlarmController.this.mMinute;
                        RepeatAlarmController repeatAlarmController = SetAlarmController.this.mRepeatAlarmController;
                        string = SetAlarmController.getAlarmInFuture(calendar, appDEContext, i, i2, RepeatAlarmController.mDaysOfWeek);
                    } else {
                        string = SetAlarmController.getAlarmInFuture(ShiftAlarmAlertHelper.getAlertTime(SetAlarmController.this.mShiftAlarmGroup));
                    }
                } else {
                    string = DeskClockApp.getAppDEContext().getString(R.string.alarm_disabled);
                }
                Message messageObtainMessage = SetAlarmController.this.mHandler.obtainMessage(0);
                messageObtainMessage.obj = string;
                SetAlarmController.this.mHandler.removeMessages(0);
                SetAlarmController.this.mHandler.sendMessage(messageObtainMessage);
            }
        };
        this.mHandler = new Handler() { // from class: com.android.deskclock.alarm.SetAlarmController.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (SetAlarmController.this.mAlarmInFuture == null || message.obj == null) {
                    return;
                }
                SetAlarmController.this.mAlarmInFuture.setText(message.obj.toString());
            }
        };
        this.mSetAlarmOtherView = (CardView) this.mRootView.findViewById(R.id.set_alarm_other);
        this.mSetAlarmLabelView = (ConstraintLayout) this.mRootView.findViewById(R.id.edit_label);
        this.mTimePicker = (TimePicker) this.mRootView.findViewById(R.id.time_picker);
        this.scrollView = (NestedScrollView) this.mRootView.findViewById(R.id.scroll_view);
        this.mTimePicker.requestFocus();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mTimePicker.getLayoutParams();
        if (Util.isTinyScreen(this.mActivity)) {
            layoutParams.height = (int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_time_picker_width_tiny_screen);
            layoutParams.width = (int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_time_picker_height_tiny_screen);
            layoutParams.bottomMargin = (int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_time_picker_tiny_margin_bottom);
            layoutParams.topMargin = 0;
            this.mTimePicker.setLayoutParams(layoutParams);
        } else if (MiuixUIUtils.getFontLevel(DeskClockApp.getAppContext()) == 2 && Util.isWideMode(this.mActivity) && !Util.isInMultiWindowMode(this.mActivity)) {
            layoutParams.height = this.mActivity.getResources().getDimensionPixelSize(R.dimen.set_alarm_time_picker_height_large);
        }
        this.mTimePicker.setSelectorIndicesCount(Util.isTinyScreen(this.mActivity) ? 3 : 5);
        String[] shortWeekdays = new DateFormatSymbols().getShortWeekdays();
        this.dayList = shortWeekdays;
        this.weekdayShort = new String[]{shortWeekdays[2], shortWeekdays[3], shortWeekdays[4], shortWeekdays[5], shortWeekdays[6], shortWeekdays[7], shortWeekdays[1]};
    }

    /* JADX WARN: Code duplicated, block: B:24:0x005e  */
    @Override // com.android.deskclock.alarm.AlarmController
    public void initBundleData(Bundle bundle) {
        boolean z;
        if (bundle != null && bundle.containsKey(KEY_ORIGINAL_ALARM) && bundle.containsKey(KEY_CURRENT_ALARM)) {
            Alarm alarm = (Alarm) bundle.getParcelable(KEY_ORIGINAL_ALARM);
            if (alarm != null) {
                this.mOriginalAlarm = alarm;
            }
            Alarm alarm2 = (Alarm) bundle.getParcelable(KEY_CURRENT_ALARM);
            if (alarm2 != null) {
                this.mAlarmChanged = alarm2;
                this.mAlert = alarm2.alert;
            }
            if (bundle.getBoolean(KEY_XIAO_AI_ALARM)) {
                this.mAlert = XiaoAiRingtoneHelper.getRingtoneUri();
            }
            if (bundle.containsKey(KEY_SHIFT_ALARM_GROUP) && bundle.containsKey(KEY_ORIGINAL_SHIFT_ALARM_GROUP)) {
                ShiftAlarmGroup shiftAlarmGroup = (ShiftAlarmGroup) bundle.getParcelable(KEY_SHIFT_ALARM_GROUP);
                ShiftAlarmGroup shiftAlarmGroup2 = (ShiftAlarmGroup) bundle.getParcelable(KEY_ORIGINAL_SHIFT_ALARM_GROUP);
                if (shiftAlarmGroup == null || shiftAlarmGroup2 == null) {
                    z = false;
                } else {
                    this.mShiftAlarmGroup = shiftAlarmGroup;
                    this.mOriginalShiftAlarmGroup = shiftAlarmGroup2;
                    z = true;
                }
            } else {
                z = false;
            }
        } else {
            z = false;
        }
        if (this.mOriginalAlarm.type == 2) {
            this.mTimePicker.setVisibility(8);
            if (z && this.mShiftAlarmGroup != null && this.mOriginalShiftAlarmGroup != null) {
                initShiftAlarms();
            } else {
                ShiftAlarmGroup shiftGroupFromAlarmId = ShiftAlarmDataHelper.getShiftGroupFromAlarmId(this.mOriginalAlarm.id);
                this.mShiftAlarmGroup = shiftGroupFromAlarmId;
                this.mOriginalShiftAlarmGroup = shiftGroupFromAlarmId;
                initShiftAlarms();
            }
            RepeatAlarmController.mDaysOfWeek.set(new Alarm.DaysOfWeek(512));
            return;
        }
        ShiftAlarmController shiftAlarmController = this.mShiftAlarmController;
        if (shiftAlarmController != null) {
            shiftAlarmController.setShiftAlarmsVisibility(8);
        }
    }

    private void showCtaPermissionDialog(ActivityResultLauncher<Intent> activityResultLauncher) {
        SystemPermissionUtil.showPermissionDeclare(this.mActivity, activityResultLauncher);
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void initData(Alarm alarm, Alarm alarm2, boolean z, boolean z2) {
        Intent intent = this.mActivity.getIntent();
        if (z) {
            this.mAlarmChanged = alarm2;
        } else {
            alarm = (Alarm) intent.getParcelableExtra(AlarmHelper.ALARM_INTENT_EXTRA);
            this.mAlarmChanged = (Alarm) intent.getParcelableExtra("intent.extra.alarm.changed");
        }
        if (alarm == null) {
            alarm = new Alarm();
            alarm.vibrate = AlarmSettingsFragment.getVibrateState();
        }
        if (Util.uriHasUserId(alarm.alert) && Util.getUserIdFromUri(alarm.alert) != Util.getCurrentUser()) {
            Log.d(TAG, "initData getUserIdFromUri: " + Util.getUserIdFromUri(alarm.alert) + ", getUserId: " + Util.getCurrentUser());
            alarm.alert = AlarmRingtoneUtil.getDefaultAlarmRingtone();
        }
        SetAlarmUtil.handleRequestSetAlarm(intent, alarm);
        this.mOriginalAlarm = alarm;
        this.mIsXiaoAiAlarm = XiaoAiRingtoneHelper.isXiaoAiAlarm(this.mActivity, alarm.id);
        Log.d(XiaoAiRingtoneHelper.TAG, "set alarm, id: " + alarm.id + ", isXiaoAiRt " + this.mIsXiaoAiAlarm);
        Uri uri = this.mOriginalAlarm.alert;
        this.mAlert = uri;
        if (WYStarRingtoneHelper.isWYStarAlert(uri) && !WYStarRingtoneHelper.isSupport()) {
            this.mAlert = WeatherRingtoneHelper.getWeatherRingtoneUri();
        }
        this.mOldAlert = this.mAlert;
        this.mIsOldXiaoAiAlarm = this.mIsXiaoAiAlarm;
    }

    private void initShiftAlarms() {
        ShiftAlarmController shiftAlarmController = new ShiftAlarmController(this.mActivity, this.mRootView, this.mShiftAlarmGroup, false);
        this.mShiftAlarmController = shiftAlarmController;
        shiftAlarmController.setShiftAlarmsVisibility(0);
        this.mShiftAlarmController.setOnDateChangedListener(new ShiftAlarmController.OnDateChangedListener() { // from class: com.android.deskclock.alarm.SetAlarmController.3
            @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.OnDateChangedListener
            public void onDurationChanged(int i, int i2) {
            }

            @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.OnDateChangedListener
            public void onStartTimeChanged(long j, int i) {
            }

            @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.OnDateChangedListener
            public void onAlarmChanged(int i, int i2, boolean z, int i3, int i4) {
                SetAlarmController.this.updateAlarmInFuture();
            }
        });
        this.mShiftAlarmController.setOnMoreButtonClickListener(new AnonymousClass4());
        this.mShiftAlarmController.setViewLayout(this.mIsSetAlarmDialogFloating);
    }

    /* JADX INFO: renamed from: com.android.deskclock.alarm.SetAlarmController$4, reason: invalid class name */
    class AnonymousClass4 implements ShiftAlarmController.OnMoreButtonClickListener {
        AnonymousClass4() {
        }

        @Override // com.android.deskclock.alarm.shiftalarm.ShiftAlarmController.OnMoreButtonClickListener
        public void onMoreButtonClick() {
            SetAlarmController.this.mSetAlarmOtherView.setVisibility(8);
            SetAlarmController.this.mSetAlarmLabelView.setVisibility(8);
            SetAlarmController.this.mAlarmInFuture.setVisibility(8);
            SetAlarmController.this.mTitle.setText(R.string.shift_information);
            SetAlarmController.this.mActionButton2.setVisibility(4);
            SetAlarmController.this.mActionButton1.setBackgroundResource(R.drawable.action_icon_back);
            SetAlarmController.this.mActionButton1.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.SetAlarmController.4.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SetAlarmController.this.mSetAlarmOtherView.setVisibility(0);
                    SetAlarmController.this.mSetAlarmLabelView.setVisibility(0);
                    SetAlarmController.this.mAlarmInFuture.setVisibility(0);
                    SetAlarmController.this.mTitle.setText(SetAlarmController.this.mId == -1 ? R.string.add_alarm : R.string.edit_alarm);
                    SetAlarmController.this.mActionButton2.setVisibility(0);
                    SetAlarmController.this.mShiftAlarmController.showLimitedData();
                    SetAlarmController.this.mActionButton1.setBackgroundResource(R.drawable.edit_mode_title_button_close);
                    SetAlarmController.this.mActionButton1.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.SetAlarmController.4.1.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view2) {
                            SetAlarmController.this.clearLabelSummaryFocus();
                            SetAlarmController.this.sendAckBroadcast(false);
                            SetAlarmController.this.dismissDialog();
                        }
                    });
                }
            });
        }
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void initOtherData() {
        XiaoAiRingtoneHelper.handleNotSureAlarm();
        initActionbarView();
        initOtherSettings();
        setRingtoneValue();
        this.mActivity.setVolumeControlStream(4);
        StatHelper.alarmEvent(StatHelper.EVENT_SET_ALARM_ACTIVE_COUNT);
        AlarmThreadPool.runOnUiThread(new Runnable() { // from class: com.android.deskclock.alarm.SetAlarmController.5
            @Override // java.lang.Runnable
            public void run() {
                if (SetAlarmController.this.mAlarmChanged != null) {
                    SetAlarmController setAlarmController = SetAlarmController.this;
                    setAlarmController.updateSettings(setAlarmController.mAlarmChanged);
                } else {
                    SetAlarmController setAlarmController2 = SetAlarmController.this;
                    setAlarmController2.updateSettings(setAlarmController2.mOriginalAlarm);
                }
                SetAlarmController.this.mTitle.setText(SetAlarmController.this.mId == -1 ? R.string.add_alarm : R.string.edit_alarm);
                SetAlarmController.this.mTitle.setTypeface(MiuiFont.MI_PRO_MEDIUM);
                SetAlarmController.this.updateAlarmInFuture();
                SetAlarmController.this.hideTitleAndOtherSetting();
            }
        });
        loadExternalResource();
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.deskclock.alarm.SetAlarmController.6
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                SetAlarmController.this.updateAlarmInFuture();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIME_TICK");
        if (Build.VERSION.SDK_INT >= 34) {
            this.mActivity.registerReceiver(this.mReceiver, intentFilter, 4);
        } else {
            this.mActivity.registerReceiver(this.mReceiver, intentFilter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideTitleAndOtherSetting() {
        if (Util.isTinyScreen(this.mActivity)) {
            this.mTitle.setVisibility(8);
            this.mAlarmInFuture.setVisibility(8);
            this.mRingtonePreferenceView.setVisibility(8);
            this.mVibratorPreferenceView.setVisibility(8);
            this.mLabelSummary.setVisibility(8);
            return;
        }
        this.mTitle.setVisibility(0);
        this.mAlarmInFuture.setVisibility(0);
        this.mRingtonePreferenceView.setVisibility(0);
        if (!Util.isSupportVibrator(this.mActivity)) {
            this.mVibratorPreferenceView.setVisibility(8);
        } else {
            this.mVibratorPreferenceView.setVisibility(0);
        }
        this.mLabelSummary.setVisibility(0);
    }

    private void isShowOneShotPreferenceView(Boolean bool) {
        if (bool.booleanValue() || Util.isTinyScreen(this.mActivity)) {
            this.mOneShotPreferenceView.setVisibility(8);
        } else {
            this.mOneShotPreferenceView.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarmInFuture() {
        Handler handler = this.mAsyncHandler;
        if (handler != null) {
            handler.removeMessages(0);
            Handler handler2 = this.mAsyncHandler;
            handler2.sendMessage(handler2.obtainMessage(0));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSettings(Alarm alarm) {
        this.mId = alarm.id;
        this.mEnableAlarm = alarm.enabled;
        if (!TextUtils.isEmpty(alarm.label) && this.mLabelSummary != null) {
            this.mLabel = alarm.label;
            this.mLabelSummary.setText(alarm.label);
        }
        this.mHour = alarm.hour;
        this.mMinute = alarm.minutes;
        setDaysOfWeek(alarm.daysOfWeek);
        this.mVibratorValueCb.setChecked(alarm.vibrate);
        showTimePicker();
        this.mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() { // from class: com.android.deskclock.alarm.SetAlarmController.7
            @Override // com.android.deskclock.widget.TimePicker.OnTimeChangedListener
            public void onTimeChanged(TimePicker timePicker, int i, int i2) {
                SetAlarmController.this.clearLabelSummaryFocus();
                SetAlarmController.this.mHour = i;
                SetAlarmController.this.mMinute = i2;
                SetAlarmController.this.mEnableAlarm = true;
                SetAlarmController.this.updateAlarmInFuture();
                StatHelper.alarmEvent(StatHelper.EVENT_CLICK_SET_ALARM_PICKER_COUNT);
            }
        });
        isShowOneShotPreferenceView(Boolean.valueOf(alarm.daysOfWeek.isRepeatSet() || alarm.type == 2));
        this.mOneShotValueCb.setChecked(alarm.deleteAfterUse);
        if (Util.isSupportVibrator(this.mActivity)) {
            return;
        }
        this.mVibratorPreferenceView.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearLabelSummaryFocus() {
        TextView textView = this.mLabelSummary;
        if (textView == null || !textView.isFocused()) {
            return;
        }
        this.mLabelSummary.clearFocus();
    }

    public void setDaysOfWeek(Alarm.DaysOfWeek daysOfWeek) {
        Log.d(TAG, "setDaysOfWeek: " + daysOfWeek.toString(this.mActivity, true));
        RepeatAlarmController.setRepeatDaysOfWeek(daysOfWeek);
        this.mRepeatValueTv.setText(daysOfWeek.toString(DeskClockApp.getAppDEContext(), true));
    }

    private void showTimePicker() {
        this.mTimePicker.setIs24HourView(Boolean.valueOf(DateFormat.is24HourFormat(this.mActivity)));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(this.mMinute));
        this.mTimePicker.setCurrentHour(Integer.valueOf(this.mHour));
        if (this.mTimePicker.is24HourView()) {
            this.mTimePicker.setPadding((int) this.mActivity.getResources().getDimension(R.dimen.time_picker_layout_padding_start_24), 0, (int) this.mActivity.getResources().getDimension(R.dimen.time_picker_layout_padding_start_24), 0);
        } else {
            this.mTimePicker.setPadding((int) this.mActivity.getResources().getDimension(R.dimen.time_picker_layout_padding_start_12), 0, (int) this.mActivity.getResources().getDimension(R.dimen.time_picker_layout_padding_start_12), 0);
        }
    }

    private void setRingtoneValue() {
        String string;
        Log.d(TAG + RingtoneManager.getDefaultUri(4) + ", mAlert=" + this.mAlert + " mIsXiaoAiAlarm:" + this.mIsXiaoAiAlarm);
        if (this.mIsXiaoAiAlarm) {
            string = this.mActivity.getResources().getString(R.string.xiaoai_ringtone_title);
        } else {
            Uri uri = this.mAlert;
            if (uri == null || uri.toString().isEmpty()) {
                string = this.mActivity.getResources().getString(R.string.silent_alarm_summary);
            } else {
                string = AlarmRingtoneUtil.getAlarmRingtoneTitle(this.mActivity, this.mAlert);
                if (string.equals(this.mActivity.getResources().getString(R.string.xiaoai_ringtone_title))) {
                    this.mIsXiaoAiAlarm = true;
                }
            }
        }
        TextView textView = this.mRingtoneValueTv;
        if (textView != null) {
            textView.setText(string);
        }
    }

    private void initOtherSettings() {
        this.mHasInit = true;
        this.mSupportWeekRingtone = WeekRingtoneHelper.isRomSupport() && WeatherRingtoneHelper.isRomSupport();
        LinearLayout linearLayout = (LinearLayout) this.mRootView.findViewById(R.id.ringtone);
        this.mRingtonePreferenceView = linearLayout;
        AnimHelper.addPressAnimWithBg(linearLayout);
        TextView textView = (TextView) this.mRingtonePreferenceView.findViewById(R.id.title);
        this.mRingtoneTitleTv = textView;
        textView.setText(R.string.alert);
        this.mRingtoneValueTv = (TextView) this.mRingtonePreferenceView.findViewById(R.id.summary);
        if (MiuixUIUtils.getFontLevel(DeskClockApp.getAppContext()) == 2) {
            RelativeLayout relativeLayout = (RelativeLayout) this.mRingtonePreferenceView.findViewById(R.id.titleVIew);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.addRule(3, this.mRingtoneTitleTv.getId());
            this.mRingtoneValueTv.setLayoutParams(layoutParams);
            this.mRingtoneValueTv.setMaxWidth(Integer.MAX_VALUE);
            this.mRingtoneValueTv.setGravity(GravityCompat.START);
            if (this.mRingtoneValueTv.getParent() != null) {
                ((ViewGroup) this.mRingtoneValueTv.getParent()).removeView(this.mRingtoneValueTv);
            }
            relativeLayout.addView(this.mRingtoneValueTv);
        }
        this.mRingtonePreferenceView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.SetAlarmController.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SetAlarmController.this.clearLabelSummaryFocus();
                if (SetAlarmController.this.mSupportWeekRingtone && Util.isInternational() && !MiuiSdk.isLiteOrMiddleMode()) {
                    SetAlarmController.this.jumpToRingtonePicker();
                } else {
                    AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.SetAlarmController.8.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (RingtoneUriCompat.atLeastU() && AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), SetAlarmController.this.mAlert).contains(SetAlarmController.this.mActivity.getResources().getString(R.string.ringtone_digital_timer))) {
                                SetAlarmController.this.mAlert = DigitalTimerRingtoneHelper.getRingtoneUri();
                            }
                            Intent intentCreateRingtonePickerIntent = MiuiTheme.createRingtonePickerIntent(SetAlarmController.this.mIsXiaoAiAlarm ? XiaoAiRingtoneHelper.getRingtoneUri() : SetAlarmController.this.mAlert, MiuiTheme.createAlarmRingtoneExtrasWithXiaoAi(), "SetAlarmActivity");
                            if (AlarmClockFragment.toThemeOrRingtoneLauncher != null) {
                                AlarmClockFragment.toThemeOrRingtoneLauncher.launch(intentCreateRingtonePickerIntent);
                            }
                        }
                    });
                }
                StatHelper.alarmEvent(StatHelper.EVENT_CLICK_SET_ALARM_RINGTONE);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_EDIT_RINGTONE_CLICK);
            }
        });
        LinearLayout linearLayout2 = (LinearLayout) this.mRootView.findViewById(R.id.repeat);
        this.mRepeatPreferenceView = linearLayout2;
        AnimHelper.addPressAnimWithBg(linearLayout2);
        TextView textView2 = (TextView) this.mRepeatPreferenceView.findViewById(R.id.title);
        this.mRepeatTitleTv = textView2;
        textView2.setText(R.string.alarm_repeat);
        this.mRepeatValueTv = (TextView) this.mRepeatPreferenceView.findViewById(R.id.summary);
        if (MiuixUIUtils.getFontLevel(DeskClockApp.getAppContext()) == 2) {
            RelativeLayout relativeLayout2 = (RelativeLayout) this.mRepeatPreferenceView.findViewById(R.id.titleVIew);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams2.addRule(3, this.mRepeatTitleTv.getId());
            this.mRepeatValueTv.setLayoutParams(layoutParams2);
            this.mRepeatValueTv.setMaxWidth(Integer.MAX_VALUE);
            this.mRepeatValueTv.setGravity(GravityCompat.START);
            if (this.mRepeatValueTv.getParent() != null) {
                ((ViewGroup) this.mRepeatValueTv.getParent()).removeView(this.mRepeatValueTv);
            }
            relativeLayout2.addView(this.mRepeatValueTv);
        }
        this.mRepeatPreferenceView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.SetAlarmController.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SetAlarmController.this.clearLabelSummaryFocus();
                SetAlarmController.this.showRepeatAlarmDialog(null);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_EDIT_REPEAT_CLICK);
            }
        });
        LinearLayout linearLayout3 = (LinearLayout) this.mRootView.findViewById(R.id.vibrator);
        this.mVibratorPreferenceView = linearLayout3;
        AnimHelper.addPressAnimWithBg(linearLayout3);
        TextView textView3 = (TextView) this.mVibratorPreferenceView.findViewById(R.id.title);
        this.mVibratorTitleTv = textView3;
        textView3.setText(R.string.vibrate_pref_summary);
        this.mVibratorValueCb = (CheckBox) this.mVibratorPreferenceView.findViewById(R.id.checkbox);
        this.mVibratorPreferenceView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.SetAlarmController.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SetAlarmController.this.clearLabelSummaryFocus();
                SetAlarmController.this.mVibratorValueCb.setChecked(!SetAlarmController.this.mVibratorValueCb.isChecked());
                StatHelper.alarmEvent(StatHelper.EVENT_CLICK_SET_ALARM_VIBRATE_COUNT);
            }
        });
        LinearLayout linearLayout4 = (LinearLayout) this.mRootView.findViewById(R.id.one_shot);
        this.mOneShotPreferenceView = linearLayout4;
        AnimHelper.addPressAnimWithBg(linearLayout4);
        TextView textView4 = (TextView) this.mOneShotPreferenceView.findViewById(R.id.title);
        this.mOneShotTitleTv = textView4;
        textView4.setText(R.string.oneshot_alarm);
        if ((Util.isDeviceCetus() && !Util.isInInternalScreen(this.mActivity)) || (Util.isSmallPad() && Util.isOrientationPortrait(this.mActivity) && Util.isInMultiWindowMode(this.mActivity))) {
            this.mOneShotTitleTv.setMaxWidth((int) this.mActivity.getResources().getDimension(R.dimen.checkbox_preference_cetus_title_width));
        }
        this.mOneShotValueCb = (CheckBox) this.mOneShotPreferenceView.findViewById(R.id.checkbox);
        this.mOneShotPreferenceView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.SetAlarmController.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SetAlarmController.this.clearLabelSummaryFocus();
                SetAlarmController.this.mOneShotValueCb.setChecked(!SetAlarmController.this.mOneShotValueCb.isChecked());
                if (SetAlarmController.this.mOneShotValueCb.isChecked()) {
                    StatHelper.alarmEvent(StatHelper.EVENT_SWITCH_ON_ALARM_ONESHOT_PREF);
                } else {
                    StatHelper.alarmEvent(StatHelper.EVENT_SWITCH_OFF_ALARM_ONESHOT_PREF);
                }
            }
        });
        this.mLabelSummary = (TextView) this.mRootView.findViewById(R.id.edit_summary);
        String strTrim = replaceNull(this.mOriginalAlarm.label).trim();
        this.mLabel = strTrim;
        this.mLabelSummary.setText(strTrim);
        this.mActivity.getWindow().getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(this);
        this.mLabelSummary.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.android.deskclock.alarm.SetAlarmController.12
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    return;
                }
                SetAlarmController.this.hideSoftInput();
            }
        });
        this.mVibratorValueCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.deskclock.alarm.SetAlarmController.13
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SetAlarmController.this.clearLabelSummaryFocus();
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_EDIT_VIBRATE_CLICK);
            }
        });
        this.mOneShotValueCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.deskclock.alarm.SetAlarmController.14
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SetAlarmController.this.clearLabelSummaryFocus();
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_EDIT_ONESHOT_CLICK);
            }
        });
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        Log.d(TAG, "onGlobalLayout");
        int height = this.mActivity.getWindow().getDecorView().getHeight();
        Rect rect = new Rect();
        this.mSetAlarmLabelView.getWindowVisibleDisplayFrame(rect);
        if (height - rect.bottom > ((double) height) * 0.15d) {
            this.scrollView.scrollBy(0, (int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_edit_text_margin_bottom));
        }
    }

    public void removeGlobalLayoutListener() {
        try {
            this.mActivity.getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } catch (Exception e) {
            Log.e(TAG, "Error removing global layout listener: " + e.getClass().getSimpleName() + " - " + e.getMessage(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSoftInput() {
        TextView textView = this.mLabelSummary;
        if (textView == null || !Util.isShowKeyboard(this.mActivity, textView)) {
            return;
        }
        Util.hideSoftInput(this.mActivity, this.mLabelSummary);
    }

    public void showRepeatAlarmDialog(Bundle bundle) {
        this.mRepeatAlarmBottomSheetModal = new BottomSheetModal(this.mActivity);
        View viewInflate = LayoutInflater.from(this.mActivity).inflate(R.layout.activity_alarm_repeat, (ViewGroup) null);
        this.mRepeatAlarmView = viewInflate;
        this.mRepeatAlarmBottomSheetModal.setContentView(viewInflate);
        this.mRepeatAlarmBottomSheetModal.setDragHandleViewEnabled(true);
        RepeatAlarmController repeatAlarmController = new RepeatAlarmController(this.mActivity, this.mRepeatAlarmView, true);
        this.mRepeatAlarmController = repeatAlarmController;
        repeatAlarmController.initShiftAlarm(this.mShiftAlarmGroup);
        this.mRepeatAlarmController.initData(null, null, false, false);
        this.mRepeatAlarmController.setLastCheckedItem((String) this.mRepeatValueTv.getText());
        this.mRepeatAlarmController.initBundleData(bundle);
        this.mRepeatAlarmController.initOtherData();
        this.mRepeatAlarmController.setOnShiftAlarmSelectedListener(new RepeatAlarmController.onShiftAlarmSelectedListener() { // from class: com.android.deskclock.alarm.SetAlarmController.15
            @Override // com.android.deskclock.alarm.RepeatAlarmController.onShiftAlarmSelectedListener
            public void onBack(ShiftAlarmGroup shiftAlarmGroup) {
                SetAlarmController.this.mShiftAlarmGroup = shiftAlarmGroup;
            }
        });
        this.mRepeatAlarmController.setBackButtonClickListener(new AlarmController.BackButtonClickListener() { // from class: com.android.deskclock.alarm.SetAlarmController.16
            @Override // com.android.deskclock.alarm.AlarmController.BackButtonClickListener
            public void onButtonClick() {
                SetAlarmController.isBackValid = true;
                SetAlarmController.this.resetShiftAlarms();
                SetAlarmController.this.dismissRepeatAlarmDialog();
            }
        });
        this.mRepeatAlarmBottomSheetModal.setOnBackListener(new BottomSheetModal.OnBackListener() { // from class: com.android.deskclock.alarm.SetAlarmController.17
            @Override // miuix.bottomsheet.BottomSheetModal.OnBackListener
            public boolean onBack() {
                SetAlarmController.isBackValid = true;
                SetAlarmController.this.resetShiftAlarms();
                return false;
            }
        });
        this.mRepeatAlarmBottomSheetModal.setOnDismissListener(new BottomSheetModal.OnDismissListener() { // from class: com.android.deskclock.alarm.SetAlarmController.18
            @Override // miuix.bottomsheet.BottomSheetModal.OnDismissListener
            public void onDismiss() {
                if (SetAlarmController.this.mRepeatAlarmBottomSheetModal != null) {
                    Folme.getTarget((View) SetAlarmController.this.mRepeatAlarmBottomSheetModal.getBottomSheetView()).clean();
                    Folme.clean(SetAlarmController.this.mRepeatAlarmBottomSheetModal.getBottomSheetView());
                    if (SetAlarmController.this.mRepeatAlarmView != null) {
                        Folme.getTarget(SetAlarmController.this.mRepeatAlarmView).clean();
                        Folme.clean(SetAlarmController.this.mRepeatAlarmView);
                    }
                }
                SetAlarmController.this.mRepeatAlarmBottomSheetModal = null;
            }
        });
        BottomSheetBehavior<FrameLayout> behavior = this.mRepeatAlarmBottomSheetModal.getBehavior();
        this.mRepeatBottomSheetBehavior = behavior;
        behavior.setOnModeChangeListener(new BottomSheetBehavior.OnModeChangeListener() { // from class: com.android.deskclock.alarm.SetAlarmController.19
            @Override // miuix.bottomsheet.BottomSheetBehavior.OnModeChangeListener
            public void onModeChange(int i, View view) {
                SetAlarmController.this.mIsSetAlarmDialogFloating = i == 1;
                if (SetAlarmController.this.mRepeatAlarmController != null) {
                    if (i == 1) {
                        SetAlarmController.this.mRepeatAlarmController.setViewLayout(true);
                    } else {
                        SetAlarmController.this.mRepeatAlarmController.setViewLayout(false);
                    }
                }
                if (SetAlarmController.this.mShiftAlarmController != null) {
                    SetAlarmController.this.mShiftAlarmController.setViewLayout(i == 1);
                }
            }
        });
        this.mRepeatBottomSheetBehavior.setDraggable(true);
        this.mRepeatBottomSheetBehavior.setSkipHalfExpanded(true);
        this.mRepeatBottomSheetBehavior.setSkipCollapsed(true);
        this.mRepeatBottomSheetBehavior.setForceFullHeight(true);
        this.mRepeatBottomSheetBehavior.setState(3);
        this.mRepeatAlarmBottomSheetModal.show();
        if (bundle == null || !bundle.getBoolean(RepeatAlarmController.IS_SHOW_SELF_DEFINE_DIALOG)) {
            return;
        }
        this.mRepeatAlarmController.showSelfDefDialog(RepeatAlarmController.mNewDaysOfWeekSwitchScreen.getBooleanArray(), bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetShiftAlarms() {
        if (!RepeatAlarmController.mDaysOfWeek.isShiftAlarm()) {
            this.mTimePicker.setVisibility(0);
            ShiftAlarmController shiftAlarmController = this.mShiftAlarmController;
            if (shiftAlarmController != null) {
                shiftAlarmController.setShiftAlarmsVisibility(8);
                return;
            }
            return;
        }
        if (this.mShiftAlarmGroup == null) {
            this.mShiftAlarmGroup = this.mRepeatAlarmController.getShiftAlarmGroup();
        }
        ShiftAlarmController shiftAlarmController2 = this.mShiftAlarmController;
        if (shiftAlarmController2 != null) {
            shiftAlarmController2.setShiftAlarmsVisibility(0);
            this.mShiftAlarmController.setData(this.mShiftAlarmGroup);
        } else {
            initShiftAlarms();
        }
        this.mTimePicker.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void jumpToRingtonePicker() {
        Intent intent = new Intent(this.mActivity, (Class<?>) AlarmRingtonePickerActivity.class);
        intent.putExtra(AlarmRingtonePickerActivity.IS_SET_MODE, false);
        intent.putExtra("android.intent.extra.ringtone.PICKED_URI", this.mAlert);
        intent.putExtra(AlarmRingtonePickerActivity.IS_FROM_ALARM, true);
        if (AlarmClockFragment.toThemeOrRingtoneLauncher != null) {
            AlarmClockFragment.toThemeOrRingtoneLauncher.launch(intent);
        }
    }

    private String replaceNull(String str) {
        return str == null ? "" : str;
    }

    private boolean isAlertEqual(Uri uri, Uri uri2) {
        if (uri == null && uri2 == null) {
            return true;
        }
        if (uri != null) {
            return uri.equals(uri2);
        }
        return false;
    }

    public void handleRequestPermissionResult(int i, String[] strArr, int[] iArr) {
        if (i != 1) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            this.mAlert = this.mOldAlert;
            this.mIsXiaoAiAlarm = this.mIsOldXiaoAiAlarm;
        }
        setRingtoneValue();
    }

    private void initActionbarView() {
        View viewFindViewById = this.mRootView.findViewById(R.id.set_alarm_actionbar);
        ViewGroup.LayoutParams layoutParams = viewFindViewById.getLayoutParams();
        if (MiuixUIUtils.getFontLevel(this.mActivity) == 2) {
            layoutParams.height = (int) this.mActivity.getResources().getDimension(R.dimen.bottom_sheet_dialog_title_large_font_height);
        } else {
            layoutParams.height = (int) this.mActivity.getResources().getDimension(R.dimen.bottom_sheet_dialog_title_height);
        }
        viewFindViewById.setLayoutParams(layoutParams);
        this.mAlarmInFuture = (TextView) viewFindViewById.findViewById(R.id.alarm_in_future);
        Button button = (Button) viewFindViewById.findViewById(16908314);
        this.mActionButton2 = button;
        button.setOnClickListener(new AnonymousClass20());
        Button button2 = (Button) viewFindViewById.findViewById(16908313);
        this.mActionButton1 = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.SetAlarmController.21
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SetAlarmController.this.clearLabelSummaryFocus();
                StatHelper.alarmEvent(StatHelper.EVENT_CLICK_SET_ALARM_CANCEL);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_EDIT_CANCEL_CLICK);
                SetAlarmController.this.sendAckBroadcast(false);
                SetAlarmController.this.dismissDialog();
            }
        });
        this.mTitle = (TextView) viewFindViewById.findViewById(android.R.id.title);
        if (Locale.getDefault().getLanguage().contains("bo")) {
            this.mTitle.setPadding(0, 0, 0, (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.set_alarm_actionbar_padding_bottom_bo));
            this.mAlarmInFuture.setPadding(0, 0, 0, (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.set_alarm_actionbar_padding_bottom_bo));
        }
    }

    /* JADX INFO: renamed from: com.android.deskclock.alarm.SetAlarmController$20, reason: invalid class name */
    class AnonymousClass20 implements View.OnClickListener {
        AnonymousClass20() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            SetAlarmController.this.clearLabelSummaryFocus();
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.SetAlarmController.20.1
                @Override // java.lang.Runnable
                public void run() {
                    if (SetAlarmController.this.mHasInit && Math.abs(System.currentTimeMillis() - SetAlarmController.mLastClickTime) > 1000) {
                        long unused = SetAlarmController.mLastClickTime = System.currentTimeMillis();
                        if (SetAlarmController.this.mLabelSummary != null) {
                            SetAlarmController.this.mLabel = SetAlarmController.this.mLabelSummary.getText().toString();
                        }
                        final long jSaveAlarm = SetAlarmController.this.saveAlarm(null);
                        StatHelper.alarmEvent(StatHelper.EVENT_CLICK_SET_ALARM_SAVE);
                        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_EDIT_SAVE_CLICK);
                        AlarmThreadPool.runOnUiThread(new Runnable() { // from class: com.android.deskclock.alarm.SetAlarmController.20.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                SetAlarmController.this.sendAckBroadcast(true);
                                if (!DeskClockTabActivity.NOTIFICATION_PERMISSION_GRANTED) {
                                    SetAlarmController.this.showNotificationPermissionDialog(SetAlarmController.this.mActivity, SetAlarmController.this.mActivity.getSupportFragmentManager(), jSaveAlarm);
                                } else {
                                    SetAlarmController.popAlarmSetToast(jSaveAlarm);
                                    SetAlarmController.this.dismissDialog();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public static void popAlarmSetToast(int i, int i2, Alarm.DaysOfWeek daysOfWeek) {
        popAlarmSetToast(AlarmHelper.calculateAlarmTime(DeskClockApp.getAppDEContext(), i, i2, daysOfWeek).getTimeInMillis());
    }

    public static void popAlarmSetToast(long j) {
        String toastNew;
        cancleToast();
        Context appDEContext = DeskClockApp.getAppDEContext();
        if (j - System.currentTimeMillis() > 60000) {
            toastNew = Util.formatToast(appDEContext, j, R.array.alarm_set_new_array);
        } else {
            toastNew = Util.formatToastNew(appDEContext);
        }
        if (TextUtils.isEmpty(toastNew)) {
            return;
        }
        Toast toastMakeText = Toast.makeText(DeskClockApp.getAppDEContext(), toastNew, 1);
        mAlarmToast = toastMakeText;
        toastMakeText.show();
    }

    public static void cancleToast() {
        Log.d(TAG, "cancleToast mAlarmToast :" + mAlarmToast);
        Toast toast = mAlarmToast;
        if (toast != null) {
            toast.cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showNotificationPermissionDialog(final Context context, FragmentManager fragmentManager, final long j) {
        DialogUtil.showNotificationPermissionDialog(context.getResources().getString(R.string.notification_permission_dialog_title), context.getResources().getString(R.string.notification_permission_dialog_content), R.string.notification_permission_dialog_not_open, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.SetAlarmController.22
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }, R.string.notification_permission_dialog_open, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.SetAlarmController.23
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                UserNoticeUtil.gotoNotificationSettingPage(context);
            }
        }, new DialogInterface.OnCancelListener() { // from class: com.android.deskclock.alarm.SetAlarmController.24
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
            }
        }, new DialogInterface.OnDismissListener() { // from class: com.android.deskclock.alarm.SetAlarmController.25
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                Log.d(SetAlarmController.TAG, "showNotificationPermissionDialog onDismiss");
                SetAlarmController.popAlarmSetToast(j);
                SetAlarmController.this.dismissDialog();
            }
        }, fragmentManager);
    }

    public void sendAckBroadcast(boolean z) {
        Intent ackIntent = SetAlarmUtil.getAckIntent(this.mActivity.getIntent(), z);
        if (ackIntent != null) {
            Log.i(SetAlarmUtil.TAG, "sendAckBroadcast");
            this.mActivity.sendBroadcast(ackIntent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long saveAlarm(Alarm alarm) {
        long jAddAlarm;
        if (this.mShiftAlarmGroup != null && RepeatAlarmController.mDaysOfWeek.isShiftAlarm()) {
            ShiftAlarmGroup shiftAlarmGroup = this.mShiftAlarmGroup;
            CheckBox checkBox = this.mVibratorValueCb;
            shiftAlarmGroup.vibrate = checkBox == null || checkBox.isChecked();
            this.mShiftAlarmGroup.label = replaceNull(this.mLabel).trim();
            this.mShiftAlarmGroup.alert = this.mAlert;
            this.mShiftAlarmGroup.silent = this.mAlert == null || AlarmClockExtras.NO_RINGTONE.equals(this.mAlert.toString());
            this.mShiftAlarmGroup.enable = true;
            this.mShiftAlarmGroup.skipTime = 0L;
            this.mShiftAlarmGroup.skipIndex = -1;
            ShiftAlarmDataHelper.disableSnooze(this.mShiftAlarmGroup);
            ShiftAlarmDataHelper.handlerXiaoAiRingtoneForShiftAlarm(this.mShiftAlarmGroup, this.mIsXiaoAiAlarm || isDefaultXiaoAiByTitle());
            ShiftAlarmDataHelper.saveShiftAlarmGroup(this.mShiftAlarmGroup);
            Alarm alarm2 = this.mOriginalAlarm;
            if (alarm2 != null && alarm2.id != -1 && this.mOriginalAlarm.type == 0) {
                AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).deleteAppSearchAlarm(this.mOriginalAlarm.id);
                AlarmHelper.deleteAlarm(this.mActivity, Integer.valueOf(this.mOriginalAlarm.id));
            }
            return ShiftAlarmAlertHelper.getAlertTime(this.mShiftAlarmGroup);
        }
        boolean z = alarm == null;
        if (alarm == null) {
            alarm = buildAlarmFromUi();
            alarm.skipTime = 0L;
            alarm.enabled = true;
        }
        ShiftAlarmGroup shiftAlarmGroup2 = this.mOriginalShiftAlarmGroup;
        if (shiftAlarmGroup2 != null) {
            ShiftAlarmDataHelper.deleteShiftAlarmGroup(shiftAlarmGroup2);
            alarm.id = -1;
        }
        if (alarm.id == -1) {
            jAddAlarm = AlarmHelper.addAlarm(this.mActivity, alarm);
            int i = alarm.id;
            this.mId = i;
            handlerXiaoAiRingtone(i);
            AlarmHelper.setNextAlert(this.mActivity);
            if (RingtoneManager.getDefaultUri(4).equals(alarm.alert)) {
                StatHelper.deskclockEvent(StatHelper.EVENT_NEW_ALARM_DEFAULT_RINGTONE);
                OneTrackStatHelper.trackBoolEvent(false, OneTrackStatHelper.ALARM_NEW_EDIT_RINGTONE);
            } else {
                StatHelper.deskclockEvent(StatHelper.EVENT_NEW_ALARM_EDIT_RINGTONE);
                OneTrackStatHelper.trackBoolEvent(true, OneTrackStatHelper.ALARM_NEW_EDIT_RINGTONE);
            }
            StatHelper.recordAlarmAction(this.mActivity, StatHelper.EVENT_ALARM_ADD, alarm);
            OneTrackStatHelper.recordAlarmAction(this.mActivity, alarm);
            TimePicker timePicker = this.mTimePicker;
            if (timePicker != null) {
                StatHelper.updateAlarmProperties(StatHelper.EVENT_NEW_ALARM_HOUR_PICKER_SLIDE_TIMES, timePicker.getHourSlideTimes());
                StatHelper.updateAlarmProperties(StatHelper.EVENT_NEW_ALARM_MIN_PICKER_SLIDE_TIMES, this.mTimePicker.getMinSlideTimes());
                OneTrackStatHelper.trackNumEvent(this.mTimePicker.getHourSlideTimes(), OneTrackStatHelper.NEW_ALARM_HOUR_PICKER_SLIDE_COUNT);
                OneTrackStatHelper.trackNumEvent(this.mTimePicker.getMinSlideTimes(), OneTrackStatHelper.NEW_ALARM_MIN_PICKER_SLIDE_COUNT);
            }
        } else {
            handlerXiaoAiRingtone(alarm.id);
            long alarm3 = AlarmHelper.setAlarm(this.mActivity, alarm);
            AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).updateAppSearchAlarmScheduled(alarm, true);
            if (z && isModified()) {
                if (this.mOriginalAlarm.alert == null) {
                    if (alarm.alert == null) {
                        StatHelper.deskclockEvent(StatHelper.EVENT_EDIT_ALARM_NOT_CHANGE_RINGTONE);
                        OneTrackStatHelper.trackBoolEvent(false, OneTrackStatHelper.ALARM_EDIT_CHANGE_RINGTONE);
                    } else {
                        StatHelper.deskclockEvent(StatHelper.EVENT_EDIT_ALARM_CHANGE_RINGTONE);
                        OneTrackStatHelper.trackBoolEvent(true, OneTrackStatHelper.ALARM_EDIT_CHANGE_RINGTONE);
                    }
                } else if (this.mOriginalAlarm.alert.equals(alarm.alert)) {
                    StatHelper.deskclockEvent(StatHelper.EVENT_EDIT_ALARM_NOT_CHANGE_RINGTONE);
                    OneTrackStatHelper.trackBoolEvent(false, OneTrackStatHelper.ALARM_EDIT_CHANGE_RINGTONE);
                } else {
                    StatHelper.deskclockEvent(StatHelper.EVENT_EDIT_ALARM_CHANGE_RINGTONE);
                    OneTrackStatHelper.trackBoolEvent(true, OneTrackStatHelper.ALARM_EDIT_CHANGE_RINGTONE);
                }
            }
            StatHelper.recordAlarmAction(this.mActivity, StatHelper.EVENT_ALARM_EDIT, alarm);
            OneTrackStatHelper.recordAlarmAction(this.mActivity, alarm);
            TimePicker timePicker2 = this.mTimePicker;
            if (timePicker2 != null) {
                StatHelper.updateAlarmProperties(StatHelper.EVENT_EDIT_ALARM_HOUR_PICKER_SLIDE_TIMES, timePicker2.getHourSlideTimes());
                StatHelper.updateAlarmProperties(StatHelper.EVENT_EDIT_ALARM_MIN_PICKER_SLIDE_TIMES, this.mTimePicker.getMinSlideTimes());
                OneTrackStatHelper.trackNumEvent(this.mTimePicker.getHourSlideTimes(), OneTrackStatHelper.EDIT_ALARM_HOUR_PICKER_SLIDE_COUNT);
                OneTrackStatHelper.trackNumEvent(this.mTimePicker.getMinSlideTimes(), OneTrackStatHelper.EDIT_ALARM_MIN_PICKER_SLIDE_COUNT);
            }
            jAddAlarm = alarm3;
        }
        if (WeatherRingtoneHelper.isWeatherRingtone(this.mAlert)) {
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_SET_ALARM_RINGTONE, "WEATHER");
        } else if (WeekRingtoneHelper.isWeekRingtone(this.mAlert)) {
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_SET_ALARM_RINGTONE, "WEEK");
        } else {
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_SET_ALARM_RINGTONE, "OTHER");
        }
        StatHelper.trackEvent(StatHelper.KEY_SET_ALARM_TIME, TimeUtil.composeTime(alarm.hour, alarm.minutes));
        OneTrackStatHelper.trackNumEvent((this.mHour * 60) + this.mMinute, "");
        return jAddAlarm;
    }

    private void handlerXiaoAiRingtone(int i) {
        if (this.mIsXiaoAiAlarm || isDefaultXiaoAiByTitle()) {
            XiaoAiRingtoneHelper.addXiaoAiRingtoneIds(this.mActivity, i);
        } else {
            XiaoAiRingtoneHelper.removeXiaoAiRingtoneIds(this.mActivity, i);
        }
    }

    private boolean isDefaultXiaoAiByTitle() {
        boolean zEquals = AlarmRingtoneUtil.getAlarmRingtoneTitle(this.mActivity, this.mAlert).equals(this.mActivity.getResources().getString(R.string.xiaoai_ringtone_title));
        Log.d("DC:SetAlarmRingtone isDefaultXiaoAiByTitle :" + zEquals);
        return zEquals;
    }

    private Alarm buildAlarmFromUi() {
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.android.deskclock.alarm.SetAlarmController.26
            @Override // java.lang.Runnable
            public void run() {
                SetAlarmController.this.mTimePicker.requestFocus();
            }
        });
        Alarm alarm = new Alarm();
        alarm.id = this.mId;
        alarm.enabled = this.mEnableAlarm;
        alarm.hour = this.mHour;
        alarm.minutes = this.mMinute;
        if (!isBackValid) {
            if (this.mRepeatValueTv.getText() != null) {
                if (this.mRepeatValueTv.getText().toString().equals(this.mActivity.getResources().getString(R.string.every_day))) {
                    RepeatAlarmController.mDaysOfWeek.set(new Alarm.DaysOfWeek(127));
                } else if (this.mRepeatValueTv.getText().toString().equals(this.mActivity.getResources().getString(R.string.monday_to_friday))) {
                    RepeatAlarmController.mDaysOfWeek.set(new Alarm.DaysOfWeek(31));
                } else if (this.mRepeatValueTv.getText().toString().equals(this.mActivity.getResources().getString(R.string.legal_workday))) {
                    RepeatAlarmController.mDaysOfWeek.set(new Alarm.DaysOfWeek(128));
                } else if (this.mRepeatValueTv.getText().toString().equals(this.mActivity.getResources().getString(R.string.legal_off_day))) {
                    RepeatAlarmController.mDaysOfWeek.set(new Alarm.DaysOfWeek(256));
                } else if (this.mRepeatValueTv.getText().toString().equals(this.mActivity.getResources().getString(R.string.never))) {
                    RepeatAlarmController.mDaysOfWeek.set(new Alarm.DaysOfWeek(0));
                } else if (this.mRepeatValueTv.getText().toString().equals(this.mActivity.getResources().getString(R.string.shift_alarm))) {
                    RepeatAlarmController.mDaysOfWeek.set(new Alarm.DaysOfWeek(512));
                } else {
                    RepeatAlarmController.mDaysOfWeek.set(new Alarm.DaysOfWeek(0));
                    setDaysOfWeek(this.mRepeatValueTv, RepeatAlarmController.mDaysOfWeek);
                }
            } else {
                Log.e(TAG, "repeatValueTv is null");
                RepeatAlarmController.mDaysOfWeek.set(new Alarm.DaysOfWeek(0));
            }
        }
        alarm.daysOfWeek = RepeatAlarmController.mDaysOfWeek;
        isBackValid = false;
        if (!alarm.daysOfWeek.isRepeatSet()) {
            alarm.deleteAfterUse = this.mOneShotValueCb.isChecked();
        } else {
            alarm.deleteAfterUse = false;
        }
        CheckBox checkBox = this.mVibratorValueCb;
        alarm.vibrate = checkBox != null ? checkBox.isChecked() : true;
        alarm.label = replaceNull(this.mLabel).trim();
        alarm.alert = this.mAlert;
        return alarm;
    }

    private void setDaysOfWeek(TextView textView, Alarm.DaysOfWeek daysOfWeek) {
        String string = textView.getText().toString();
        int i = 0;
        while (true) {
            String[] strArr = this.weekdayShort;
            if (i >= strArr.length) {
                return;
            }
            if (string.contains(strArr[i])) {
                daysOfWeek.set(i, true);
            }
            i++;
        }
    }

    private boolean isModified() {
        return (this.mOriginalAlarm.hour == this.mHour && this.mOriginalAlarm.minutes == this.mMinute && this.mOriginalAlarm.daysOfWeek.toString(this.mActivity, true).equals(RepeatAlarmController.mDaysOfWeek.toString(this.mActivity, true)) && this.mOriginalAlarm.vibrate == this.mVibratorValueCb.isChecked() && replaceNull(this.mLabel).trim().equals(replaceNull(this.mOriginalAlarm.label).trim()) && isAlertEqual(this.mOriginalAlarm.alert, this.mAlert)) ? false : true;
    }

    private void loadExternalResource() {
        int miuiResourceVersion = PrefUtil.getMiuiResourceVersion();
        Log.i(ExternalResourceUtils.TAG, "current resource version: " + miuiResourceVersion);
        if (miuiResourceVersion <= 1) {
            MiuiResource.checkLocalResourceVersion();
            miuiResourceVersion = PrefUtil.getMiuiResourceVersion();
            Log.i(ExternalResourceUtils.TAG, "resource version after check: " + miuiResourceVersion);
        }
        if (miuiResourceVersion == 5) {
            Log.i(ExternalResourceUtils.TAG, "newest module has loaded");
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_RESOURCE_STATE, "NEW");
        } else {
            if (ExternalResourceUtils.hasLoadRomResource()) {
                return;
            }
            this.mResourceLoadConnection = new ResourceLoadServiceConnection();
            this.mResourceLoadCallback = new ResourceLoadServiceCallback(this.mActivity);
            this.mLoadAfterServiceBind = true;
            Intent intent = new Intent();
            intent.setClass(DeskClockApp.getAppDEContext(), ResourceLoadService.class);
            intent.putExtra(ResourceLoadService.EXTRA_TYPE, 1);
            this.mActivity.bindService(intent, this.mResourceLoadConnection, 1);
        }
    }

    public void handleRingtone(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        final Intent data = activityResult.getData();
        Log.i("DC:SetAlarmRingtone", "toThemeOrRingtoneLauncher resultCode: " + resultCode);
        if ((resultCode == -1 || resultCode == 112) && data != null) {
            Uri uri = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            Log.d("DC:SetAlarmRingtone", "initActivityResultLauncher: " + uri);
            if (XiaoAiRingtoneHelper.isXiaoAiRingtone(uri) && !Util.isInternational()) {
                this.mIsXiaoAiAlarm = true;
                Log.d("DC:SetAlarmRingtone", "isXiaoAiRingtone: " + this.mAlert);
                this.mOldAlert = this.mAlert;
                this.mAlert = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
                AlarmRingtoneUtil.takePersistableUriPermission(data, uri, this.mActivity);
                setRingtoneValue();
                return;
            }
            if (Util.isRingtoneInternal(uri) || PermissionUtil.isCTAAndReadPermissionGranted(this.mActivity) || (RingtoneUriCompat.atLeastU() && (uri.equals(RingtoneConstants.RINGTONE_URI_WEATHER_NEW) || uri.equals(RingtoneConstants.RINGTONE_URI_WEEK_NEW) || uri.equals(RingtoneConstants.RINGTONE_URI_DEW_NEW) || uri.equals(RingtoneConstants.RINGTONE_URI_FIREFLY_NEW) || uri.equals(RingtoneConstants.RINGTONE_URI_DREAM_NEW)))) {
                handleRingtoneChange(data);
                return;
            }
            if (PermissionUtil.askReadPermissionByRingtoneChange(this.mActivity)) {
                DeskClockTabActivity deskClockTabActivity = this.mActivity;
                if (deskClockTabActivity != null) {
                    deskClockTabActivity.setPermissionRequestCallback(new PermissionRequestCallback() { // from class: com.android.deskclock.alarm.SetAlarmController.27
                        @Override // com.android.deskclock.interfaces.PermissionRequestCallback
                        public void onPermissionDenied() {
                        }

                        @Override // com.android.deskclock.interfaces.PermissionRequestCallback
                        public void onPermissionGranted() {
                            SetAlarmController.this.handleRingtoneChange(data);
                        }
                    });
                    return;
                }
                return;
            }
            if (PermissionUtil.shouldShowCtaPermission(this.mActivity)) {
                showCtaPermissionDialog(AlarmClockFragment.toCtaLauncher);
                setPermissionRequestCallback(new PermissionRequestCallback() { // from class: com.android.deskclock.alarm.SetAlarmController.28
                    @Override // com.android.deskclock.interfaces.PermissionRequestCallback
                    public void onPermissionDenied() {
                    }

                    @Override // com.android.deskclock.interfaces.PermissionRequestCallback
                    public void onPermissionGranted() {
                        if (SetAlarmController.this.mActivity != null) {
                            if (PermissionUtil.askReadPermissionByRingtoneChange(SetAlarmController.this.mActivity)) {
                                SetAlarmController.this.mActivity.setPermissionRequestCallback(new PermissionRequestCallback() { // from class: com.android.deskclock.alarm.SetAlarmController.28.1
                                    @Override // com.android.deskclock.interfaces.PermissionRequestCallback
                                    public void onPermissionDenied() {
                                    }

                                    @Override // com.android.deskclock.interfaces.PermissionRequestCallback
                                    public void onPermissionGranted() {
                                        SetAlarmController.this.handleRingtoneChange(data);
                                    }
                                });
                            } else {
                                SetAlarmController.this.handleRingtoneChange(data);
                            }
                        }
                    }
                });
            } else {
                Log.i(TAG, "setRingtone fail!");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRingtoneChange(Intent intent) {
        try {
            this.mIsOldXiaoAiAlarm = this.mIsXiaoAiAlarm;
            this.mIsXiaoAiAlarm = false;
            this.mOldAlert = this.mAlert;
            this.mAlert = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            Log.d("DC:SetAlarmRingtone", "new alert: " + this.mAlert);
            AlarmRingtoneUtil.takePersistableUriPermission(intent, this.mAlert, this.mActivity);
            setRingtoneValue();
        } catch (Exception e) {
            Log.e(TAG, "handleRingtoneChange error", e);
        }
    }

    public void handleSetAlarmNetWork(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        Log.i(TAG, "toNetWorkLauncher resultCode: " + resultCode);
        if (resultCode == 1) {
            UserNoticeUtil.setAcceptNetPermission(true);
            StatHelper.init(DeskClockApp.getAppContext());
            OneTrackStatHelper.init(DeskClockApp.getAppContext());
            if (PermissionUtil.shouldNotAskPermission(this.mActivity)) {
                this.mAlert = this.mOldAlert;
                this.mIsXiaoAiAlarm = this.mIsOldXiaoAiAlarm;
                setRingtoneValue();
                return;
            }
            return;
        }
        this.mAlert = this.mOldAlert;
        this.mIsXiaoAiAlarm = this.mIsOldXiaoAiAlarm;
        setRingtoneValue();
    }

    public void handleCta(ActivityResult activityResult) {
        if (activityResult.getResultCode() == 1) {
            UserNoticeUtil.setAcceptNetPermission(true);
            StatHelper.init(DeskClockApp.getAppContext());
            OneTrackStatHelper.init(DeskClockApp.getAppContext());
            handlePermissionGranted();
        }
    }

    public void setAlarmRepeatValue() {
        TextView textView = this.mRepeatValueTv;
        if (textView == null) {
            Log.d(TAG, "mRepeatValueTv");
            return;
        }
        textView.setText(RepeatAlarmController.mDaysOfWeek.toString(this.mActivity, true));
        isShowOneShotPreferenceView(Boolean.valueOf(RepeatAlarmController.mDaysOfWeek.isRepeatSet()));
        this.mEnableAlarm = true;
        updateAlarmInFuture();
    }

    public void handleKoreaAuthorize(ActivityResult activityResult) {
        KoreaPermissionUtil.handleKoreaAuthCallback(activityResult.getResultCode());
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void onSaveInstance(Bundle bundle) {
        ShiftAlarmGroup shiftAlarmGroup;
        super.onSaveInstance(bundle);
        bundle.putParcelable(KEY_ORIGINAL_ALARM, this.mOriginalAlarm);
        bundle.putParcelable(KEY_CURRENT_ALARM, buildAlarmFromUi());
        bundle.putBoolean(KEY_XIAO_AI_ALARM, this.mIsXiaoAiAlarm);
        if (this.mOriginalAlarm.type == 2 && (shiftAlarmGroup = this.mShiftAlarmGroup) != null && this.mOriginalShiftAlarmGroup != null) {
            bundle.putParcelable(KEY_SHIFT_ALARM_GROUP, shiftAlarmGroup);
            bundle.putParcelable(KEY_ORIGINAL_SHIFT_ALARM_GROUP, this.mOriginalShiftAlarmGroup);
        }
        if (this.mRepeatAlarmBottomSheetModal != null) {
            bundle.putBoolean(IS_SHOW_REPEAT_ALARM_DIALOG, true);
            RepeatAlarmController repeatAlarmController = this.mRepeatAlarmController;
            if (repeatAlarmController != null) {
                repeatAlarmController.onSaveInstance(bundle);
            }
        }
    }

    public void handleRepeatAlarmNewWork(ActivityResult activityResult) {
        RepeatAlarmController repeatAlarmController = this.mRepeatAlarmController;
        if (repeatAlarmController != null) {
            repeatAlarmController.handleRepeatAlarmNewWork(activityResult);
        }
    }

    public void dismissRepeatAlarmDialog() {
        handleAlarmRepeatResult();
        Log.i(TAG, "mRepeatAlarmBottomSheetModal: " + this.mRepeatAlarmBottomSheetModal);
        BottomSheetModal bottomSheetModal = this.mRepeatAlarmBottomSheetModal;
        if (bottomSheetModal != null) {
            bottomSheetModal.dismiss();
        }
    }

    public boolean isRepeatAlarmDialogShow() {
        return (this.mRepeatAlarmController == null || this.mRepeatAlarmBottomSheetModal == null) ? false : true;
    }

    public void handleAlarmRepeatResult() {
        if (this.mRepeatAlarmController.isSelfDefineDialogShow()) {
            this.mRepeatAlarmController.handleSelfDefineResult();
        } else {
            setAlarmRepeatValue();
        }
    }

    public void resetRepeatAlarmDialog() {
        RepeatAlarmController repeatAlarmController = this.mRepeatAlarmController;
        if (repeatAlarmController == null || this.mRepeatAlarmBottomSheetModal == null) {
            return;
        }
        if (repeatAlarmController.isSelfDefineDialogShow()) {
            this.mRepeatAlarmController.resetSelfDefineDialog();
        } else {
            this.mRepeatAlarmBottomSheetModal.dismiss();
            this.mRepeatAlarmController = null;
        }
    }

    public void setViewLayout(boolean z) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mSetAlarmOtherView.getLayoutParams();
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mSetAlarmLabelView.getLayoutParams();
        if (z) {
            layoutParams.setMarginStart((int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_floating_margin_start));
            layoutParams.setMarginEnd((int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_floating_margin_start));
            layoutParams2.setMarginStart((int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_floating_margin_start));
            layoutParams2.setMarginEnd((int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_floating_margin_start));
        } else {
            layoutParams.setMarginStart((int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_other_margin_start));
            layoutParams.setMarginEnd((int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_other_margin_start));
            layoutParams2.setMarginStart((int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_other_margin_start));
            layoutParams2.setMarginEnd((int) this.mActivity.getResources().getDimension(R.dimen.set_alarm_other_margin_start));
        }
        this.mSetAlarmOtherView.setLayoutParams(layoutParams);
        this.mSetAlarmLabelView.setLayoutParams(layoutParams2);
        ShiftAlarmController shiftAlarmController = this.mShiftAlarmController;
        if (shiftAlarmController != null) {
            shiftAlarmController.setViewLayout(z);
        }
    }

    private class ResourceLoadServiceConnection implements ServiceConnection {
        private ResourceLoadServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(ExternalResourceUtils.TAG, "service bind");
            SetAlarmController.this.mResourceLoadService = ((ResourceLoadService.CallbackBinder) iBinder).getService();
            SetAlarmController.this.mResourceLoadService.registerCallbackListener(SetAlarmController.this.mResourceLoadCallback);
            if (SetAlarmController.this.mLoadAfterServiceBind) {
                SetAlarmController.this.mResourceLoadService.loadRomResource(PrefUtil.getMiuiResourceVersion());
                SetAlarmController.this.mLoadAfterServiceBind = false;
            } else if (SetAlarmController.this.mDownloadAfterServiceBind) {
                SetAlarmController.this.mResourceLoadService.loadNetResource();
                SetAlarmController.this.mDownloadAfterServiceBind = false;
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            SetAlarmController.this.mResourceLoadService.unregisterCallbackListener(SetAlarmController.this.mResourceLoadCallback);
            SetAlarmController.this.mResourceLoadService = null;
        }
    }

    private static class ResourceLoadServiceCallback implements ResourceLoadService.CallbackListener {
        private WeakReference<DeskClockTabActivity> mReference;

        public ResourceLoadServiceCallback(DeskClockTabActivity deskClockTabActivity) {
            this.mReference = new WeakReference<>(deskClockTabActivity);
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onRomLoadSuccess(int i) {
            Log.i(ExternalResourceUtils.TAG, "onRomLoadSuccess in SetAlarmController");
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onRomLoadFailed() {
            Log.i(ExternalResourceUtils.TAG, "onRomLoadFailed in SetAlarmController");
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onNetLoadSuccess() {
            Log.i(ExternalResourceUtils.TAG, "onNetLoadSuccess in SetAlarmController");
            WeakReference<DeskClockTabActivity> weakReference = this.mReference;
            DeskClockTabActivity deskClockTabActivity = weakReference != null ? weakReference.get() : null;
            if (deskClockTabActivity == null) {
                return;
            }
            deskClockTabActivity.onLoadingComplete(true);
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onNetLoadFailed() {
            Log.i(ExternalResourceUtils.TAG, "onNetLoadFailed in SetAlarmController");
            WeakReference<DeskClockTabActivity> weakReference = this.mReference;
            DeskClockTabActivity deskClockTabActivity = weakReference != null ? weakReference.get() : null;
            if (deskClockTabActivity == null) {
                return;
            }
            deskClockTabActivity.onLoadingComplete(false);
        }
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void onDestroy() {
        ResourceLoadServiceConnection resourceLoadServiceConnection;
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
        RepeatAlarmController repeatAlarmController = this.mRepeatAlarmController;
        if (repeatAlarmController != null) {
            repeatAlarmController.onDestroy();
        }
        if (this.mResourceLoadService != null && (resourceLoadServiceConnection = this.mResourceLoadConnection) != null) {
            this.mActivity.unbindService(resourceLoadServiceConnection);
            this.mResourceLoadService = null;
        }
        this.mTimePicker.setOnTimeChangedListener(null);
        BroadcastReceiver broadcastReceiver = this.mReceiver;
        if (broadcastReceiver != null) {
            this.mActivity.unregisterReceiver(broadcastReceiver);
            this.mReceiver = null;
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        Handler handler2 = this.mAsyncHandler;
        if (handler2 != null) {
            handler2.removeCallbacksAndMessages(null);
        }
        HandlerThread handlerThread2 = this.mHandlerThread;
        if (handlerThread2 != null) {
            handlerThread2.quitSafely();
        }
        this.mPermissionRequestCallback = null;
        LinearLayout linearLayout = this.mRingtonePreferenceView;
        if (linearLayout != null) {
            Folme.getTarget((View) linearLayout).clean();
            Folme.clean(this.mRingtonePreferenceView);
        }
        LinearLayout linearLayout2 = this.mRepeatPreferenceView;
        if (linearLayout2 != null) {
            Folme.getTarget((View) linearLayout2).clean();
            Folme.clean(this.mRepeatPreferenceView);
        }
        LinearLayout linearLayout3 = this.mOneShotPreferenceView;
        if (linearLayout3 != null) {
            Folme.getTarget((View) linearLayout3).clean();
            Folme.clean(this.mOneShotPreferenceView);
        }
        LinearLayout linearLayout4 = this.mVibratorPreferenceView;
        if (linearLayout4 != null) {
            Folme.getTarget((View) linearLayout4).clean();
            Folme.clean(this.mVibratorPreferenceView);
        }
        TextView textView = this.mLabelSummary;
        if (textView != null) {
            textView.setOnFocusChangeListener(null);
        }
        super.onDestroy();
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void onResume() {
        super.onResume();
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.SetAlarmController.29
            @Override // java.lang.Runnable
            public void run() {
                KoreaPermissionUtil.showAuthorization(SetAlarmController.this.mActivity, AlarmClockFragment.toKoreaAuthorizeLauncher);
            }
        });
        TimePicker timePicker = this.mTimePicker;
        if (timePicker != null) {
            timePicker.requestFocus();
        }
    }

    @Override // com.android.deskclock.alarm.AlarmController
    public void onPause() {
        super.onPause();
        this.mTimePicker.stopScroll();
        DialogUtil.dismissDialogFragment(this.mDeleteConfirmDialog);
        this.mDeleteConfirmDialog = null;
        DialogUtil.dismissDialogFragment(this.mUserNoticeDialog);
        this.mUserNoticeDialog = null;
        DialogUtil.dismissDialogFragment(this.mWeatherRingtoneIntroduceDialog);
        this.mWeatherRingtoneIntroduceDialog = null;
        DialogUtil.dismissDialogFragment(this.mWeatherRingtoneDownloadDialog);
        this.mWeatherRingtoneDownloadDialog = null;
        DialogUtil.dismissDialogFragment(this.mWeekRingtoneIntroduceDialog);
        this.mWeekRingtoneIntroduceDialog = null;
        DialogUtil.dismissDialogFragment(this.mWeekRingtoneDownloadDialog);
        this.mWeekRingtoneDownloadDialog = null;
        DialogUtil.dismissDialogFragment(this.mNetPermissionDialog);
        this.mNetPermissionDialog = null;
        LabelDialogFragment labelDialogFragment = this.mLabelDialogFragment;
        if (labelDialogFragment != null) {
            labelDialogFragment.dismissDialog();
            DialogUtil.dismissDialogFragment(this.mLabelDialogFragment);
            this.mLabelDialogFragment = null;
        }
    }

    public static String getAlarmInFuture(Calendar calendar, Context context, int i, int i2, Alarm.DaysOfWeek daysOfWeek) {
        return getAlarmInFuture(AlarmHelper.calculateAlarmTime(calendar, context, i, i2, daysOfWeek).getTimeInMillis());
    }

    public static String getAlarmInFuture(long j) {
        if (j - System.currentTimeMillis() > 60000) {
            return Util.formatToast(DeskClockApp.getAppContext(), j, R.array.alarm_in_furture_new_array);
        }
        return Util.formatToastInFutureNew(DeskClockApp.getAppContext());
    }

    public void setPermissionRequestCallback(PermissionRequestCallback permissionRequestCallback) {
        if (this.mPermissionRequestCallback != null) {
            Log.i(TAG, "repeat set PermissionRequestCallback : " + android.util.Log.getStackTraceString(new Throwable()));
        }
        this.mPermissionRequestCallback = permissionRequestCallback;
    }

    private void handlePermissionGranted() {
        PermissionRequestCallback permissionRequestCallback = this.mPermissionRequestCallback;
        if (permissionRequestCallback != null) {
            permissionRequestCallback.onPermissionGranted();
            this.mPermissionRequestCallback = null;
        }
    }

    private void handlePermissionDenied() {
        PermissionRequestCallback permissionRequestCallback = this.mPermissionRequestCallback;
        if (permissionRequestCallback != null) {
            permissionRequestCallback.onPermissionDenied();
            this.mPermissionRequestCallback = null;
        }
    }
}
