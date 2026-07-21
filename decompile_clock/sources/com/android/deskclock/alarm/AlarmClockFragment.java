package com.android.deskclock.alarm;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.TabNavigatorContentFragment;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.alarm.alert.AlarmAlertFullScreenActivity;
import com.android.deskclock.alarm.alert.AlarmService;
import com.android.deskclock.alarm.bedtime.BedtimeManageActivity;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmDataHelper;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmEditEmptyActivity;
import com.android.deskclock.appaf.AppSearchUtil;
import com.android.deskclock.base.BaseClockFragment;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmUtils;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.FastStartUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.ScenarioRecognitionUtil;
import com.android.deskclock.util.UiUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.fab.FabControllerNew;
import com.android.deskclock.util.permission.PermissionUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.FabView;
import com.android.deskclock.view.SimpleDialogFragment;
import com.android.deskclock.view.list.AlarmRecyclerView;
import com.android.deskclock.view.list.EditableAdapter;
import com.android.deskclock.view.tab.TabViewModel;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import miuix.animation.Folme;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.bottomsheet.BottomSheetBehavior;
import miuix.bottomsheet.BottomSheetModal;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.widget.ActionSheet;
import miuix.nestedheader.widget.NestedHeaderLayout;
import miuix.recyclerview.widget.MiuiScaleItemAnimator;
import miuix.responsive.map.ScreenSpec;
import miuix.slidingwidget.widget.SlidingButton;
import miuix.transition.ActivityOptionsCompatImpl;
import miuix.transition.ActivityOptionsHelper;

/* JADX INFO: loaded from: classes.dex */
public class AlarmClockFragment extends BaseClockFragment {
    private static final String ALARM_ALERT_STATUS = "alarm_alert_status";
    public static final int GUIDE_ACTIVITY_REQUEST_CODE = 3;
    private static final String IS_SHOW_SET_ALARM_DIALOG = "is_show_set_alarm_dialog";
    public static final String PREFERENCES = "AlarmClock";
    private static final String TAG = "DC:AlarmClockFragment";
    public static boolean isFromCtsSetAlarm;
    private static boolean mIsAlarmAlertBannerShow;
    public static ActivityResultLauncher<Intent> toBedtimeManagerLauncher;
    public static ActivityResultLauncher<Intent> toCtaLauncher;
    public static ActivityResultLauncher<Intent> toKoreaAuthorizeLauncher;
    public static ActivityResultLauncher<Intent> toNetWorkLauncher;
    public static ActivityResultLauncher<Intent> toThemeOrRingtoneLauncher;
    private AlarmAdapter mAlarmAdapter;
    private View mAlarmAlertBannerView;
    private ImageView mAlarmBannerImage;
    private View mAlarmBlankPage;
    private ImageView mAlarmClose;
    private AlarmEditDialog mAlarmEditDialog;
    private AlarmEditDialogView mAlarmEditDialogView;
    private AlarmRecyclerView mAlarmLv;
    private AlarmModel mAlarmModel;
    private LinearLayout mAlarmScrollView;
    private TextView mAlarmTime;
    private ContentObserver mContentObserver;
    private ActionSheet.IActionSheet mCurrentActionSheet;
    private FabView mEndView2;
    private NestedHeaderLayout mNestedHeaderLayout;
    private TextView mNextAlertText;
    private View mNotificationPermissionView;
    private int[] mPrepareToDeleteAlarms;
    private BroadcastReceiver mReceiver;
    private BottomSheetBehavior mSetAlarmBottomSheetBehavior;
    private BottomSheetModal mSetAlarmBottomSheetModal;
    private View mSetAlarmContentView;
    private SetAlarmController mSetAlarmController;
    private SetNextAlertTimeTask mSetNextAlertTimeTask;
    private TextView mShiftIndexText;
    private AsyncTask<Void, Void, Void> mTimeChangeAsyncTask;
    private ActivityResultLauncher<Intent> toBedtimeLauncher;
    private Calendar mCalender = Calendar.getInstance();
    private SimpleDialogFragment mCloseConfirmDialog = null;
    private int mCloseConfirmPosition = -1;
    private boolean isFromShortCutNewAlarm = false;
    private Handler mHandler = new Handler();
    private boolean mRequestSaveAlarm = false;
    private boolean mIsActionSheetShowing = false;
    private int oldState = 0;
    private boolean isDragging = false;
    private Runnable closeRunnable = new Runnable() { // from class: com.android.deskclock.alarm.AlarmClockFragment.1
        @Override // java.lang.Runnable
        public void run() {
            try {
                ((SlidingButton) AlarmClockFragment.this.mAlarmLv.getLayoutManager().findViewByPosition(AlarmClockFragment.this.mCloseConfirmPosition).findViewById(R.id.clock_onoff)).setChecked(false);
            } catch (Exception unused) {
            }
        }
    };
    private EditableAdapter.MultiChoiceModeListener mMultiChoiceModeListener = new EditableAdapter.MultiChoiceModeListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.26
        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            ((TabNavigatorContentFragment) AlarmClockFragment.this.getParentFragment()).onActionModeChanged(true);
            return false;
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            ((TabNavigatorContentFragment) AlarmClockFragment.this.getParentFragment()).onActionModeChanged(false);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            AlarmClockFragment.this.getActivity().getMenuInflater().inflate(R.menu.delete_item, menu);
            UiUtil.updateActionModeButton1(actionMode);
            UiUtil.updateActionModeButton2(actionMode, AlarmClockFragment.this.mAlarmAdapter.isAllItemsChecked());
            AlarmClockFragment.this.setContentDescription();
            try {
                AlarmClockFragment.this.mActivity.getWindow().getDecorView().findViewById(16908313).setContentDescription(AlarmClockFragment.this.mActivity.getResources().getString(R.string.back));
                return true;
            } catch (Throwable unused) {
                return true;
            }
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == 16908313) {
                actionMode.finish();
            } else if (menuItem.getItemId() == 16908314) {
                AlarmClockFragment.this.mAlarmAdapter.setAllItemsChecked(!AlarmClockFragment.this.mAlarmAdapter.isAllItemsChecked());
            } else if (menuItem.getItemId() == R.id.delete) {
                if (AlarmClockFragment.this.mAlarmAdapter.getCheckedItemCount() > 0) {
                    AlarmClockFragment alarmClockFragment = AlarmClockFragment.this;
                    alarmClockFragment.mPrepareToDeleteAlarms = alarmClockFragment.mAlarmAdapter.getCheckedItemIds();
                    Log.i(AlarmClockFragment.TAG, "mPrepareToDeleteAlarms: " + Arrays.toString(AlarmClockFragment.this.mPrepareToDeleteAlarms));
                    if (AlarmClockFragment.this.mActivity != null) {
                        AlarmClockFragment.this.mActivity.setNavigationForActionMode(false);
                    }
                    AlarmClockFragment.this.mAlarmAdapter.finishActionMode();
                }
                return true;
            }
            return false;
        }

        @Override // android.widget.AbsListView.MultiChoiceModeListener
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long j, boolean z) {
            boolean zIsAllItemsChecked = AlarmClockFragment.this.mAlarmAdapter.isAllItemsChecked();
            boolean zIsAllItemsUnChecked = AlarmClockFragment.this.mAlarmAdapter.isAllItemsUnChecked();
            UiUtil.updateActionModeButton2(actionMode, zIsAllItemsChecked);
            UiUtil.updateActionModeDeleteBtn(actionMode, zIsAllItemsUnChecked);
            AlarmClockFragment.this.setContentDescription();
        }

        @Override // com.android.deskclock.view.list.EditableAdapter.MultiChoiceModeListener
        public void onAllItemCheckedStateChanged(ActionMode actionMode, boolean z) {
            boolean zIsAllItemsUnChecked = AlarmClockFragment.this.mAlarmAdapter.isAllItemsUnChecked();
            UiUtil.updateActionModeButton2(actionMode, z);
            UiUtil.updateActionModeDeleteBtn(actionMode, zIsAllItemsUnChecked);
            AlarmClockFragment.this.setActionModeClickDescription();
            AlarmClockFragment.this.setContentDescription();
        }
    };
    private Runnable mNotifyTaskRunnable = new Runnable() { // from class: com.android.deskclock.alarm.AlarmClockFragment.37
        @Override // java.lang.Runnable
        public void run() {
            if (AlarmClockFragment.this.mActivity == null || AlarmClockFragment.this.mActivity.isFinishing()) {
                return;
            }
            FastStartUtil.notifyTakeSnapshotQs(AlarmClockFragment.this.mActivity);
        }
    };

    @Override // miuix.appcompat.app.Fragment
    protected boolean isResponsiveEnabled() {
        return true;
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mActivity = (DeskClockTabActivity) getActivity();
        this.mRootView = (ViewGroup) layoutInflater.inflate(R.layout.fragment_alarm, viewGroup, false);
        registerAlarmObserver();
        initFragmentResultLauncher();
        initActivityResultLauncher();
        NestedHeaderLayout nestedHeaderLayout = (NestedHeaderLayout) this.mRootView.findViewById(R.id.nested_header_layout);
        this.mNestedHeaderLayout = nestedHeaderLayout;
        registerCoordinateScrollView(nestedHeaderLayout);
        this.mNestedHeaderLayout.setNestedHeaderChangedListener(new NestedHeaderLayout.NestedHeaderChangedListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.2
            @Override // miuix.nestedheader.widget.NestedHeaderLayout.NestedHeaderChangedListener
            public void onOverViewBlurStateChanged(boolean z) {
                ActionBar actionBar = AlarmClockFragment.this.getActionBar();
                if (actionBar instanceof ActionBarImpl) {
                    ((ActionBarImpl) actionBar).updateBackgroundViewBlurState(z);
                }
            }
        });
        setCorrectNestedScrollMotionEventEnabled(true);
        this.mAlarmScrollView = (LinearLayout) this.mRootView.findViewById(R.id.alarm_scroll_layout);
        this.mNextAlertText = (TextView) this.mRootView.findViewById(R.id.next_alert_text);
        this.mShiftIndexText = (TextView) this.mRootView.findViewById(R.id.shift_index_text);
        initAlarmBtn();
        updateLayout(this.mRootView);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.deskclock.alarm.AlarmClockFragment.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                AlarmClockFragment.this.setNextAlertTime();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AlarmHelper.ACTION_ALARM_SNOOZE_FROM_ADDITIONS);
        intentFilter.addAction(AlarmHelper.ACTION_SNOOZE_CANCEL_AGAIN);
        if (Build.VERSION.SDK_INT >= 34) {
            this.mActivity.registerReceiver(this.mReceiver, intentFilter, 2);
        } else {
            this.mActivity.registerReceiver(this.mReceiver, intentFilter);
        }
        Intent intent = this.mActivity.getIntent();
        if (intent != null) {
            boolean booleanExtra = intent.getBooleanExtra(Util.IS_FROM_CTS_SET_ALARM, false);
            isFromCtsSetAlarm = booleanExtra;
            if (booleanExtra) {
                TabNavigatorContentFragment.mFromCtsAlarm = (Alarm) intent.getParcelableExtra(AlarmHelper.ALARM_INTENT_EXTRA);
                showSetAlarmDialog(null, TabNavigatorContentFragment.mFromCtsAlarm, null, true);
            }
        }
        if (bundle != null && bundle.getBoolean(IS_SHOW_SET_ALARM_DIALOG)) {
            showSetAlarmDialog(bundle, null, null, false);
        }
        return this.mRootView;
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IContentInsetState
    public void onContentInsetChanged(Rect rect) {
        super.onContentInsetChanged(rect);
        FabView fabView = this.mEndView2;
        if (fabView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabView.getLayoutParams();
            layoutParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.alarm_clock_add_btn_margin_bottom) + rect.bottom;
            this.mEndView2.setLayoutParams(layoutParams);
        }
        AlarmRecyclerView alarmRecyclerView = this.mAlarmLv;
        if (alarmRecyclerView != null) {
            alarmRecyclerView.setPadding(alarmRecyclerView.getPaddingStart(), this.mAlarmLv.getPaddingTop(), this.mAlarmLv.getPaddingEnd(), rect.bottom + getResources().getDimensionPixelOffset(R.dimen.alarm_clock_padding_bottom));
        }
    }

    protected void initAlarmBtn() {
        this.mEndView2 = (FabView) this.mRootView.findViewById(R.id.end_btn2);
        FabControllerNew.getInstance().initAlarmFabViewBtn(this.mEndView2);
        FabControllerNew.getInstance().setOnAlarmFabClickListener(new AlarmFabClickListenerImpl(this));
        FabControllerNew.getInstance().setAlarmInitTab(TabViewModel.TAB_ALARM);
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mSetAlarmBottomSheetModal != null) {
            bundle.putBoolean(IS_SHOW_SET_ALARM_DIALOG, true);
            SetAlarmController setAlarmController = this.mSetAlarmController;
            if (setAlarmController != null) {
                setAlarmController.onSaveInstance(bundle);
            }
        }
    }

    private void initActivityResultLauncher() {
        toThemeOrRingtoneLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new WeakResultCallback(this) { // from class: com.android.deskclock.alarm.AlarmClockFragment.4
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.deskclock.alarm.AlarmClockFragment.WeakResultCallback, androidx.activity.result.ActivityResultCallback
            public void onActivityResult(ActivityResult activityResult) {
                super.onActivityResult(activityResult);
                AlarmClockFragment alarmClockFragment = this.mFragmentRef.get();
                if (alarmClockFragment == null || alarmClockFragment.mSetAlarmController == null || activityResult == null) {
                    return;
                }
                alarmClockFragment.mSetAlarmController.handleRingtone(activityResult);
            }
        });
        toNetWorkLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new WeakResultCallback(this) { // from class: com.android.deskclock.alarm.AlarmClockFragment.5
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.deskclock.alarm.AlarmClockFragment.WeakResultCallback, androidx.activity.result.ActivityResultCallback
            public void onActivityResult(ActivityResult activityResult) {
                super.onActivityResult(activityResult);
                AlarmClockFragment alarmClockFragment = this.mFragmentRef.get();
                if (alarmClockFragment == null || alarmClockFragment.mSetAlarmController == null || activityResult == null) {
                    return;
                }
                alarmClockFragment.mSetAlarmController.handleSetAlarmNetWork(activityResult);
                alarmClockFragment.mSetAlarmController.handleRepeatAlarmNewWork(activityResult);
            }
        });
        toCtaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new WeakResultCallback(this) { // from class: com.android.deskclock.alarm.AlarmClockFragment.6
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.deskclock.alarm.AlarmClockFragment.WeakResultCallback, androidx.activity.result.ActivityResultCallback
            public void onActivityResult(ActivityResult activityResult) {
                super.onActivityResult(activityResult);
                AlarmClockFragment alarmClockFragment = this.mFragmentRef.get();
                if (alarmClockFragment == null || alarmClockFragment.mSetAlarmController == null || activityResult == null) {
                    return;
                }
                alarmClockFragment.mSetAlarmController.handleCta(activityResult);
            }
        });
        toKoreaAuthorizeLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new WeakResultCallback(this) { // from class: com.android.deskclock.alarm.AlarmClockFragment.7
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.deskclock.alarm.AlarmClockFragment.WeakResultCallback, androidx.activity.result.ActivityResultCallback
            public void onActivityResult(ActivityResult activityResult) {
                super.onActivityResult(activityResult);
                AlarmClockFragment alarmClockFragment = this.mFragmentRef.get();
                if (alarmClockFragment == null || alarmClockFragment.mSetAlarmController == null || activityResult == null) {
                    return;
                }
                alarmClockFragment.mSetAlarmController.handleKoreaAuthorize(activityResult);
            }
        });
        toBedtimeManagerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new WeakResultCallback(this) { // from class: com.android.deskclock.alarm.AlarmClockFragment.8
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.deskclock.alarm.AlarmClockFragment.WeakResultCallback, androidx.activity.result.ActivityResultCallback
            public void onActivityResult(ActivityResult activityResult) {
                super.onActivityResult(activityResult);
                AlarmClockFragment alarmClockFragment = this.mFragmentRef.get();
                if (alarmClockFragment == null || alarmClockFragment.mSetAlarmController == null || alarmClockFragment.mActivity == null || activityResult == null) {
                    return;
                }
                int resultCode = activityResult.getResultCode();
                Intent data = activityResult.getData();
                if (resultCode == -1 && data != null && data.getBooleanExtra(Util.IS_SWITCH_NIGHT_MODE, false)) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Util.ANIM_BACK_KEY, true);
                    ActivityOptionsHelper.updateScaleUpDownData(alarmClockFragment.mActivity, bundle);
                }
            }
        });
    }

    public void setNextAlertTime() {
        SetNextAlertTimeTask setNextAlertTimeTask = this.mSetNextAlertTimeTask;
        if (setNextAlertTimeTask != null && !setNextAlertTimeTask.isCancelled()) {
            this.mSetNextAlertTimeTask.cancel(true);
        }
        SetNextAlertTimeTask setNextAlertTimeTask2 = new SetNextAlertTimeTask(this);
        this.mSetNextAlertTimeTask = setNextAlertTimeTask2;
        setNextAlertTimeTask2.execute(new Void[0]);
    }

    private static class SetNextAlertTimeTask extends AsyncTask<Void, Void, Alarm> {
        private WeakReference<AlarmClockFragment> mFragmentRef;
        private Alarm nextAlarm;

        public SetNextAlertTimeTask(AlarmClockFragment alarmClockFragment) {
            this.mFragmentRef = new WeakReference<>(alarmClockFragment);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Alarm doInBackground(Void... voidArr) {
            if (isCancelled()) {
                return null;
            }
            Alarm alarmCalculateNextAlert = AlarmHelper.calculateNextAlert(DeskClockApp.getAppDEContext());
            this.nextAlarm = alarmCalculateNextAlert;
            return alarmCalculateNextAlert;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Alarm alarm) {
            WeakReference<AlarmClockFragment> weakReference = this.mFragmentRef;
            AlarmClockFragment alarmClockFragment = weakReference != null ? weakReference.get() : null;
            if (alarmClockFragment == null || alarmClockFragment.isDetached() || alarmClockFragment.isRemoving()) {
                return;
            }
            alarmClockFragment.updateNextAlertTime(alarm);
        }
    }

    public void updateNextAlertTime(Alarm alarm) {
        if (alarm == null) {
            AlarmAdapter alarmAdapter = this.mAlarmAdapter;
            if (alarmAdapter != null && alarmAdapter.getItemCount() != 0) {
                this.mNextAlertText.setVisibility(0);
                this.mNextAlertText.setText(DeskClockApp.getAppDEContext().getString(R.string.all_alarm_close));
            } else {
                this.mNextAlertText.setVisibility(8);
            }
            this.mShiftIndexText.setVisibility(8);
            return;
        }
        if (alarm.type == 2) {
            this.mShiftIndexText.setVisibility(0);
            int shiftIndexFromAlarmId = ShiftAlarmDataHelper.getShiftIndexFromAlarmId(alarm.id);
            if (shiftIndexFromAlarmId > 0) {
                this.mShiftIndexText.setText(DeskClockApp.getAppContext().getResources().getQuantityString(R.plurals.index_in_shift_alarms_des, shiftIndexFromAlarmId, Integer.valueOf(shiftIndexFromAlarmId)));
            } else {
                this.mShiftIndexText.setVisibility(8);
            }
        } else {
            this.mShiftIndexText.setVisibility(8);
        }
        String toast = Util.formatToast(DeskClockApp.getAppDEContext(), alarm.time, R.array.alarm_in_furture_new_array);
        this.mNextAlertText.setVisibility(0);
        this.mNextAlertText.setText(toast);
        if (this.mNextAlertText.isShown() && Util.isTinyScreen(DeskClockApp.getAppDEContext())) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mNextAlertText.getLayoutParams();
            layoutParams.height = (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.next_alert_text_height_tiny);
            layoutParams.leftMargin = (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.next_alert_text_margin_h);
            layoutParams.rightMargin = (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.next_alert_text_margin_h);
            this.mNextAlertText.setLayoutParams(layoutParams);
            this.mNextAlertText.setTextSize(0, (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.set_alarm_title_text_size));
            this.mNextAlertText.setPadding(42, 0, 42, 0);
            if (this.mShiftIndexText.isShown()) {
                this.mShiftIndexText.setTextSize(0, (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.set_alarm_title_text_size));
            }
            if (Locale.getDefault().getLanguage().contains("bo") || MiuixUIUtils.getFontLevel(DeskClockApp.getAppDEContext()) == 2) {
                this.mNextAlertText.setTextSize(0, (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.set_alarm_title_text_size_bo));
                if (this.mShiftIndexText.isShown()) {
                    this.mShiftIndexText.setTextSize(0, (int) DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.set_alarm_title_text_size_bo));
                }
            }
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public void onViewInflated(View view, Bundle bundle) {
        super.onViewInflated(view, bundle);
    }

    private void registerAlarmObserver() {
        Log.d(TAG, "registerAlarmObserver");
        this.mContentObserver = new ContentObserver(new Handler()) { // from class: com.android.deskclock.alarm.AlarmClockFragment.9
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                try {
                    int i = Settings.Global.getInt(AlarmClockFragment.this.mActivity.getContentResolver(), "alarm_alert_status");
                    Log.d(AlarmClockFragment.TAG, "onChange status :" + i);
                    if (i != 1) {
                        if (AlarmClockFragment.this.mAlarmAlertBannerView != null) {
                            AlarmClockFragment.this.mAlarmAlertBannerView.setVisibility(8);
                            boolean unused = AlarmClockFragment.mIsAlarmAlertBannerShow = false;
                            AlarmClockFragment.this.setNextAlertTime();
                        }
                    } else {
                        AlarmClockFragment.this.setAlarmAlertBannerLayout(AlarmService.getCurrentAlarm());
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        this.mActivity.getContentResolver().registerContentObserver(Settings.Global.getUriFor("alarm_alert_status"), true, this.mContentObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarm(boolean z, Alarm alarm) {
        if (alarm.type == 2) {
            ShiftAlarmDataHelper.enableShiftAlarm(alarm.id, z, 0L);
            return;
        }
        AlarmHelper.enableAlarm(this.mActivity, alarm.id, z);
        Log.f(TAG, "update Alarm(id=" + alarm.id + ") " + (z ? "ON" : "OFF") + " by User");
        if (z) {
            if (!DeskClockTabActivity.NOTIFICATION_PERMISSION_GRANTED) {
                showNotificationPermissionDialog(this.mActivity, this.mActivity.getSupportFragmentManager());
            }
            SetAlarmController.popAlarmSetToast(alarm.hour, alarm.minutes, alarm.daysOfWeek);
        }
        AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).updateAppSearchAlarmScheduled(alarm, z);
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFragmentChange
    public void onEnter() {
        super.onEnter();
        Log.d(TAG, "onEnter: ");
        if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode() || !BedtimeUtil.showBedTimeBanner(this.mActivity)) {
            return;
        }
        BedtimeUtil.addBannerShowCount(this.mActivity);
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        if (this.mReceiver != null) {
            this.mActivity.unregisterReceiver(this.mReceiver);
        }
        super.onDestroyView();
        Log.d(TAG, "AlarmClockFragment onDestroyView");
        this.mAlarmLv.removeCallbacks(this.closeRunnable);
        this.mHandler.removeCallbacks(this.mNotifyTaskRunnable);
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFragmentChange
    public void onLeave() {
        super.onLeave();
        Log.d(TAG, "AlarmClockFragment onLeave");
        resetDialog();
        AlarmAdapter alarmAdapter = this.mAlarmAdapter;
        if (alarmAdapter == null || !alarmAdapter.isInActionMode()) {
            return;
        }
        this.mAlarmAdapter.finishActionMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showRepeatAlarmTurnOffDialog(final Alarm alarm, CompoundButton compoundButton, final int i) {
        String skipDateString;
        if (isAdded()) {
            this.mAlarmAdapter.setSelectedId(alarm.id);
            this.mCloseConfirmPosition = i;
            if (alarm.type == 2) {
                skipDateString = Util.getSkipDateString(getActivity(), alarm.time);
            } else {
                skipDateString = Util.getSkipDateString(getActivity(), AlarmHelper.calculateAlarmTime(getActivity(), alarm.hour, alarm.minutes, alarm.daysOfWeek).getTimeInMillis());
            }
            String[] strArr = {skipDateString, this.mActivity.getResources().getString(R.string.turn_off_this_repeat_alarm)};
            ActionSheet.Builder builder = new ActionSheet.Builder(requireActivity());
            builder.setActionItems(strArr, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.10
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    AlarmClockFragment.this.mCloseConfirmPosition = -1;
                    if (i2 != 0) {
                        if (i2 != 1) {
                            return;
                        }
                        AlarmClockFragment.this.updateAlarm(false, alarm);
                        StatHelper.alarmEvent(StatHelper.EVENT_CLOSE_REPEAT_ALARM_FOREVER);
                        OneTrackStatHelper.trackStringEvent("forever", OneTrackStatHelper.ALARM_CLOSE_REPEAT);
                        return;
                    }
                    if (alarm.type == 2) {
                        ShiftAlarmDataHelper.enableShiftAlarm(alarm.id, false, alarm.time);
                    } else {
                        AlarmHelper.skipAlarmForOnce(AlarmClockFragment.this.getActivity(), alarm.id);
                        AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).updateAppSearchAlarmScheduled(alarm, true);
                    }
                    StatHelper.alarmEvent(StatHelper.EVENT_CLOSE_REPEAT_ALARM_ONECE);
                    OneTrackStatHelper.trackStringEvent("once", OneTrackStatHelper.ALARM_CLOSE_REPEAT);
                }
            });
            builder.setSeparateText(getResources().getString(R.string.cancel));
            builder.setSeparateClickListener(new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.11
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    AlarmClockFragment.this.mCloseConfirmPosition = -1;
                    AlarmClockFragment.this.mAlarmAdapter.notifyDataSetChanged();
                    OneTrackStatHelper.trackStringEvent("cancel", OneTrackStatHelper.ALARM_CLOSE_REPEAT);
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.12
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface dialogInterface) {
                    AlarmClockFragment.this.mCloseConfirmPosition = -1;
                    AlarmClockFragment.this.mAlarmAdapter.notifyDataSetChanged();
                    OneTrackStatHelper.trackStringEvent("cancel", OneTrackStatHelper.ALARM_CLOSE_REPEAT);
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.13
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    AlarmClockFragment.this.mCloseConfirmPosition = -1;
                    AlarmClockFragment.this.mAlarmAdapter.setSelectedId(-1);
                    AlarmClockFragment.this.mAlarmAdapter.notifyItemChanged(i);
                    AlarmClockFragment.this.setNextAlertTime();
                    AlarmClockFragment.this.mIsActionSheetShowing = false;
                    if (AlarmClockFragment.this.mCurrentActionSheet == null || AlarmClockFragment.this.mCurrentActionSheet.getSeparateView() == null) {
                        return;
                    }
                    Folme.clean(AlarmClockFragment.this.mCurrentActionSheet.getSeparateView());
                    if (AlarmClockFragment.this.mCurrentActionSheet.getListView() != null) {
                        Folme.clean(AlarmClockFragment.this.mCurrentActionSheet.getListView());
                    }
                    AlarmClockFragment.this.mCurrentActionSheet = null;
                }
            });
            ActionSheet.IActionSheet iActionSheetCreate = builder.create();
            this.mCurrentActionSheet = iActionSheetCreate;
            iActionSheetCreate.show();
            this.mIsActionSheetShowing = true;
            StatHelper.alarmEvent(StatHelper.EVENT_CLICK_CLOSE_REPEAT_ALARM);
        }
    }

    private void showNotificationPermissionDialog(final Context context, FragmentManager fragmentManager) {
        DialogUtil.showNotificationPermissionDialog(context.getResources().getString(R.string.notification_permission_dialog_title), context.getResources().getString(R.string.notification_permission_dialog_content), R.string.notification_permission_dialog_not_open, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.14
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }, R.string.notification_permission_dialog_open, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.15
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                UserNoticeUtil.gotoNotificationSettingPage(context);
            }
        }, new DialogInterface.OnCancelListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.16
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
            }
        }, new DialogInterface.OnDismissListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.17
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
            }
        }, fragmentManager);
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimeTick() {
        AlarmModel alarmModel;
        super.onTimeTick();
        AlarmAdapter alarmAdapter = this.mAlarmAdapter;
        if (alarmAdapter == null || alarmAdapter.isDoingActionAnim() || (alarmModel = this.mAlarmModel) == null) {
            return;
        }
        startTimeTask(this.mAlarmAdapter, alarmModel);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        AlarmModel alarmModel = this.mAlarmModel;
        if (alarmModel != null) {
            alarmModel.release();
        }
        if (this.mContentObserver != null) {
            this.mActivity.getContentResolver().unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
        }
        mIsAlarmAlertBannerShow = false;
        SetAlarmController setAlarmController = this.mSetAlarmController;
        if (setAlarmController != null) {
            setAlarmController.onDestroy();
        }
        if (toThemeOrRingtoneLauncher != null) {
            toThemeOrRingtoneLauncher = null;
        }
        if (this.mSetAlarmController != null) {
            this.mSetAlarmController = null;
        }
        SetNextAlertTimeTask setNextAlertTimeTask = this.mSetNextAlertTimeTask;
        if (setNextAlertTimeTask != null && !setNextAlertTimeTask.isCancelled()) {
            this.mSetNextAlertTimeTask.cancel(true);
            this.mSetNextAlertTimeTask = null;
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        AlarmAdapter alarmAdapter;
        super.onResponsiveLayout(configuration, screenSpec, z);
        SetAlarmController.cancleToast();
        setAlarmLayoutPadding();
        setRvLayoutMode();
        if (this.mCloseConfirmPosition != -1) {
            this.mAlarmLv.post(this.closeRunnable);
        }
        if (Util.isFoldDevice(this.mActivity) && (alarmAdapter = this.mAlarmAdapter) != null) {
            alarmAdapter.setInternalScreen(Util.isInInternalScreen(this.mActivity));
        }
        AlarmEditDialogView alarmEditDialogView = this.mAlarmEditDialogView;
        if (alarmEditDialogView != null && alarmEditDialogView.isShowing()) {
            this.mAlarmEditDialogView.dismissDirectly();
        }
        updateBlankPage();
    }

    private void updateLayout(View view) {
        this.mAlarmLv = (AlarmRecyclerView) view.findViewById(R.id.alarm_list);
        setAlarmLayoutPadding();
        this.mAlarmLv.setVerticalScrollBarEnabled(true);
        this.mAlarmLv.setSpringEnabled(false);
        this.mAlarmLv.setItemAnimator(new MiuiScaleItemAnimator());
        this.mAlarmLv.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.18
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view2, MotionEvent motionEvent) {
                if (AlarmClockFragment.this.mAlarmEditDialogView == null || !AlarmClockFragment.this.mAlarmEditDialogView.isShowing()) {
                    return false;
                }
                AlarmClockFragment.this.mAlarmEditDialogView.dismissDirectly();
                return false;
            }
        });
        this.mAlarmLv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.19
            private boolean isScrolling = false;
            private float lastY;

            @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
            public void onRequestDisallowInterceptTouchEvent(boolean z) {
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.isScrolling = false;
                    this.lastY = motionEvent.getY();
                } else if (action != 2) {
                    if (action == 3) {
                        this.isScrolling = false;
                    }
                } else if (!this.isScrolling && Math.abs(motionEvent.getY() - this.lastY) > ViewConfiguration.get(recyclerView.getContext()).getScaledTouchSlop()) {
                    this.isScrolling = true;
                    ScenarioRecognitionUtil.INSTANCE.setScenarioState(335L, true, AlarmClockFragment.this.getContext() != null ? AlarmClockFragment.this.getContext().getPackageName() : null);
                }
                return false;
            }
        });
        this.mAlarmLv.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.20
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && !AlarmClockFragment.this.isDragging) {
                    ScenarioRecognitionUtil.INSTANCE.setScenarioState(335L, false, AlarmClockFragment.this.getContext() != null ? AlarmClockFragment.this.getContext().getPackageName() : null);
                    AlarmClockFragment.this.isDragging = true;
                } else if (i == 0) {
                    AlarmClockFragment.this.isDragging = false;
                }
                if (AlarmClockFragment.this.oldState == 1 || i == 1) {
                    ScenarioRecognitionUtil.INSTANCE.setScenarioState(336L, i == 1);
                }
                AlarmClockFragment.this.oldState = i;
            }
        });
        this.mAlarmAdapter = new AlarmAdapter(this.mActivity, this.mAlarmLv);
        if (Util.isFoldDevice(this.mActivity)) {
            this.mAlarmAdapter.setInternalScreen(Util.isInInternalScreen(this.mActivity));
        }
        this.mAlarmAdapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.21
            @Override // com.android.deskclock.alarm.AlarmAdapter.OnItemClickListener
            public void onAlarmClick(View view2, int i, Alarm alarm) {
                if (AlarmClockFragment.this.mAlarmAdapter.isInActionMode()) {
                    AlarmClockFragment.this.mAlarmAdapter.toggleItemChecked(i);
                    return;
                }
                if (TabNavigatorContentFragment.mCurrTab.equals(TabViewModel.TAB_ALARM)) {
                    if (alarm.type != 2 || !Util.isTinyScreen(AlarmClockFragment.this.mActivity)) {
                        if (Util.isWideMode(AlarmClockFragment.this.mActivity) || MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode() || Util.isFreeFormScreen(AlarmClockFragment.this.mActivity.getResources().getConfiguration()) || Util.inExternalSplitScreen(AlarmClockFragment.this.mActivity) || Util.isTinyScreen(AlarmClockFragment.this.mActivity) || alarm.type == 2) {
                            AlarmClockFragment.this.showSetAlarmDialog(null, alarm, alarm, true);
                            StatHelper.alarmEvent(StatHelper.EVENT_TIME_PICKER_OPEN_COUNT);
                            return;
                        }
                        if (AlarmClockFragment.this.mAlarmEditDialogView != null && AlarmClockFragment.this.mAlarmEditDialogView.isShowing()) {
                            AlarmClockFragment.this.mAlarmEditDialogView.dismissDirectly();
                        }
                        if (AlarmClockFragment.this.mAlarmEditDialog == null || !AlarmClockFragment.this.mAlarmEditDialog.isShowing()) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("alarm", alarm);
                            bundle.putSerializable("calender", AlarmClockFragment.this.mCalender);
                            if (!MiuiSdk.isLiteOrMiddleMode()) {
                                AlarmClockFragment.this.mAlarmEditDialogView = new AlarmEditDialogView(AlarmClockFragment.this.mActivity);
                                AlarmClockFragment.this.mAlarmEditDialogView.setAlarm(bundle);
                                AlarmClockFragment.this.mAlarmEditDialogView.setOnSaveAlarmListener(new AlarmEditDialogView.OnSaveAlarmListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.21.1
                                    @Override // com.android.deskclock.alarm.AlarmEditDialogView.OnSaveAlarmListener
                                    public void onSaveAlarm(Alarm alarm2) {
                                        Log.v("AlarmClockFragment saveAlarm: uri:" + alarm2.alert);
                                        AlarmClockFragment.this.mAlarmLv.setItemAnimator(null);
                                        AlarmHelper.setAlarm(AlarmClockFragment.this.mActivity, alarm2);
                                        if (alarm2.enabled) {
                                            SetAlarmController unused = AlarmClockFragment.this.mSetAlarmController;
                                            SetAlarmController.popAlarmSetToast(alarm2.hour, alarm2.minutes, alarm2.daysOfWeek);
                                        }
                                        AlarmClockFragment.this.mRequestSaveAlarm = true;
                                    }
                                });
                                AlarmClockFragment.this.mAlarmEditDialogView.setOnMoreClickListener(new AlarmEditDialogView.OnMoreClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.21.2
                                    @Override // com.android.deskclock.alarm.AlarmEditDialogView.OnMoreClickListener
                                    public void onMoreClick(Alarm alarm2, Alarm alarm3) {
                                        AlarmClockFragment.this.showSetAlarmDialog(null, alarm2, alarm3, true);
                                    }
                                });
                                AlarmClockFragment.this.mAlarmEditDialogView.setOnDismissListener(new AlarmEditDialogView.OnDismissListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.21.3
                                    @Override // com.android.deskclock.alarm.AlarmEditDialogView.OnDismissListener
                                    public void onDismiss() {
                                        AlarmClockFragment.this.setNextAlertTime();
                                    }
                                });
                                Rect rect = new Rect();
                                AlarmClockFragment.this.mAlarmLv.getGlobalVisibleRect(rect);
                                AlarmClockFragment.this.mAlarmEditDialogView.show(view2, rect, AlarmClockFragment.this.mAlarmAdapter.getBean(i), AlarmClockFragment.this.mAlarmAdapter.getSelectedId());
                            } else {
                                AlarmClockFragment.this.mAlarmEditDialog = new AlarmEditDialog();
                                AlarmClockFragment.this.mAlarmEditDialog.setArguments(bundle);
                                AlarmClockFragment.this.mAlarmEditDialog.setOnSaveAlarmListener(new AlarmEditDialog.OnSaveAlarmListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.21.4
                                    @Override // com.android.deskclock.alarm.AlarmEditDialog.OnSaveAlarmListener
                                    public void onSaveAlarm(Alarm alarm2) {
                                        Log.v("AlarmClockFragment saveAlarm: uri:" + alarm2.alert);
                                        AlarmHelper.setAlarm(AlarmClockFragment.this.mActivity, alarm2);
                                        if (alarm2.enabled) {
                                            SetAlarmController unused = AlarmClockFragment.this.mSetAlarmController;
                                            SetAlarmController.popAlarmSetToast(alarm2.hour, alarm2.minutes, alarm2.daysOfWeek);
                                        }
                                    }
                                });
                                AlarmClockFragment.this.mAlarmEditDialog.setOnMoreClickListener(new AlarmEditDialog.OnMoreClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.21.5
                                    @Override // com.android.deskclock.alarm.AlarmEditDialog.OnMoreClickListener
                                    public void onMoreClick(Alarm alarm2, Alarm alarm3) {
                                        AlarmClockFragment.this.showSetAlarmDialog(null, alarm2, alarm3, true);
                                    }
                                });
                                AlarmClockFragment.this.mAlarmEditDialog.show(AlarmClockFragment.this.getChildFragmentManager(), AlarmEditDialog.TAG);
                            }
                            StatHelper.alarmEvent(StatHelper.EVENT_TIME_PICKER_OPEN_COUNT);
                            return;
                        }
                        return;
                    }
                    AlarmClockFragment.this.startActivity(new Intent(AlarmClockFragment.this.mActivity, (Class<?>) ShiftAlarmEditEmptyActivity.class));
                }
            }

            @Override // com.android.deskclock.alarm.AlarmAdapter.OnItemClickListener
            public void onWakeAlarmClick(View view2) {
                int color;
                if (AlarmClockFragment.this.mAlarmAdapter.isInActionMode()) {
                    return;
                }
                Intent intent = new Intent(AlarmClockFragment.this.mActivity, (Class<?>) BedtimeManageActivity.class);
                AlarmClockFragment alarmClockFragment = AlarmClockFragment.this;
                if (alarmClockFragment.canUseMiuiSdkAnim(view2, alarmClockFragment.getActivity())) {
                    Rect rect = new Rect(0, 0, view2.getWidth(), view2.getHeight());
                    int dimensionPixelSize = AlarmClockFragment.this.getContext().getResources().getDimensionPixelSize(R.dimen.bedtime_alarm_enter_ic_start);
                    Drawable background = view2.getBackground();
                    if (background instanceof ColorDrawable) {
                        color = ((ColorDrawable) background).getColor();
                    } else {
                        color = AlarmClockFragment.this.getContext().getResources().getColor(R.color.bedtime_manager_page_color);
                    }
                    intent.putExtra(Util.IS_NIGHT_MODE_KEY, Util.isNightMode(DeskClockApp.getAppDEContext()));
                    intent.putExtra(Util.IS_ANIM_BACK_KEY, true);
                    ActivityOptions activityOptionsMakeScaleUpAnim = ActivityOptionsHelper.makeScaleUpAnim(view2, rect, dimensionPixelSize, color, 103);
                    if (AlarmClockFragment.toBedtimeManagerLauncher != null) {
                        AlarmClockFragment.toBedtimeManagerLauncher.launch(intent, new ActivityOptionsCompatImpl(activityOptionsMakeScaleUpAnim));
                    }
                } else {
                    AlarmClockFragment.this.startActivity(intent);
                }
                StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_CLICK);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.BEDTIME_ITEM_CLICK);
            }
        });
        this.mAlarmAdapter.setOnLongClickListener(new AlarmAdapter.OnLongClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.22
            @Override // com.android.deskclock.alarm.AlarmAdapter.OnLongClickListener
            public boolean onLongClick(int i) {
                ((TabNavigatorContentFragment) AlarmClockFragment.this.getParentFragment()).onActionModeChanged(true);
                if (AlarmClockFragment.this.mAlarmAdapter.isInActionMode() || AlarmClockFragment.this.mAlarmAdapter == null || !TabViewModel.TAB_ALARM.equals(TabNavigatorContentFragment.mCurrTab)) {
                    return false;
                }
                AlarmClockFragment.this.mAlarmAdapter.setItemChecked(i, true);
                if (AlarmClockFragment.this.mActivity != null) {
                    AlarmClockFragment.this.mActivity.setNavigationForActionMode(true);
                }
                AlarmClockFragment.this.mAlarmAdapter.startActionMode(AlarmClockFragment.this.mMultiChoiceModeListener, (TabNavigatorContentFragment) AlarmClockFragment.this.getParentFragment());
                return true;
            }
        });
        this.mAlarmAdapter.setOnAlarmCheckedChangeListener(new AlarmAdapter.OnAlarmCheckedChangedListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.23
            @Override // com.android.deskclock.alarm.AlarmAdapter.OnAlarmCheckedChangedListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z, Alarm alarm, int i) {
                if (z || (!alarm.daysOfWeek.isRepeatSet() && alarm.type != 2)) {
                    AlarmClockFragment.this.updateAlarm(z, alarm);
                } else if (!AlarmClockFragment.this.mStopped) {
                    AlarmClockFragment.this.showRepeatAlarmTurnOffDialog(alarm, compoundButton, i);
                }
                AlarmClockFragment.this.setNextAlertTime();
                if (z) {
                    StatHelper.alarmEvent(StatHelper.EVENT_SWITCH_ON_ALARM);
                } else {
                    StatHelper.alarmEvent(StatHelper.EVENT_SWITCH_OFF_ALARM);
                }
                OneTrackStatHelper.trackBoolEvent(z, OneTrackStatHelper.ALARM_ITEM_BUTTON_VALUE);
            }
        });
        this.mAlarmAdapter.setOnActionAnimListener(new EditableAdapter.OnActionAnimListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.24
            @Override // com.android.deskclock.view.list.EditableAdapter.OnActionAnimListener
            public void onAnimStart() {
            }

            @Override // com.android.deskclock.view.list.EditableAdapter.OnActionAnimListener
            public void onAnimStop() {
                if (AlarmClockFragment.this.mPrepareToDeleteAlarms != null) {
                    AlarmHelper.deleteAlarmsAsync(DeskClockApp.getAppDEContext(), AlarmClockFragment.this.mPrepareToDeleteAlarms, AlarmClockFragment.this.getFragmentManager());
                    AlarmHelper.deleteAlarmsFromAppSearch(AlarmClockFragment.this.mPrepareToDeleteAlarms);
                    Util.playDeleteRingtone();
                    StatHelper.alarmEvent(AlarmClockFragment.this.mPrepareToDeleteAlarms.length > 1 ? StatHelper.EVENT_DELETE_MULTIPLE_ALARMS : StatHelper.EVENT_DELETE_ONE_ALARM);
                    OneTrackStatHelper.trackNumEvent(AlarmClockFragment.this.mPrepareToDeleteAlarms.length, OneTrackStatHelper.ALARM_ITEM_DELETE_COUNT);
                }
                AlarmClockFragment.this.mPrepareToDeleteAlarms = null;
            }
        });
        setRvLayoutMode();
        cancelDialogView();
        AlarmModel alarmModel = new AlarmModel(this.mActivity.getApplicationContext(), new AlarmObserverImp(this));
        this.mAlarmModel = alarmModel;
        alarmModel.showData();
    }

    private void setAlarmLayoutPadding() {
        Resources resources;
        int i;
        int paddingStart = getPaddingStart();
        AlarmRecyclerView alarmRecyclerView = this.mAlarmLv;
        if (alarmRecyclerView != null) {
            alarmRecyclerView.setPadding(paddingStart, alarmRecyclerView.getPaddingTop(), paddingStart, this.mAlarmLv.getPaddingBottom());
        }
        LinearLayout linearLayout = this.mAlarmScrollView;
        if (linearLayout != null) {
            linearLayout.setPadding(paddingStart, linearLayout.getPaddingTop(), paddingStart, this.mAlarmScrollView.getPaddingBottom());
        }
        FabView fabView = this.mEndView2;
        if (fabView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabView.getLayoutParams();
            if (Util.isLargeScreenPad()) {
                resources = DeskClockApp.getAppDEContext().getResources();
                i = R.dimen.fab_view_btn_pad_margin_end;
            } else {
                resources = DeskClockApp.getAppDEContext().getResources();
                i = R.dimen.fab_view_btn_margin_end;
            }
            layoutParams.setMarginEnd((int) (resources.getDimension(i) + paddingStart));
            this.mEndView2.setLayoutParams(layoutParams);
        }
    }

    private int getPaddingStart() {
        float dimension;
        int dimension2 = (int) this.mActivity.getResources().getDimension(R.dimen.alarm_clock_root_view_padding_start);
        if (!Util.isWideMode(this.mActivity) && Util.isPadOrientationLand(this.mActivity)) {
            dimension = this.mActivity.getResources().getDimension(R.dimen.alarm_clock_root_view_pad_not_wide_padding_start);
        } else if (Util.isPadOrientationLand(this.mActivity) && !this.mActivity.isInMultiWindowMode()) {
            dimension = this.mActivity.getResources().getDimension(R.dimen.alarm_clock_root_view_pad_land_padding_start);
        } else {
            if (!Util.isInInternalScreen(this.mActivity) || !Util.isFoldDevice(this.mActivity) || this.mActivity.isInMultiWindowMode()) {
                return dimension2;
            }
            dimension = this.mActivity.getResources().getDimension(R.dimen.alarm_clock_root_view_fold_padding_start);
        }
        return (int) dimension;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canUseMiuiSdkAnim(View view, Activity activity) {
        if (MiuiSdk.isLiteOrMiddleMode()) {
            return false;
        }
        if (Util.isFoldDevice(DeskClockApp.getAppDEContext()) && !Util.isFlipType(DeskClockApp.getAppDEContext())) {
            return false;
        }
        String str = Build.DEVICE;
        return (TextUtils.isEmpty(str) || !(str.equals("vangogh") || str.equals("lime"))) && view != null && ActivityOptionsHelper.isSupportScaleUpDown() && !Util.isInMultiWindowMode(this.mActivity);
    }

    private void setRvLayoutMode() {
        if (this.mAlarmLv == null) {
            return;
        }
        if (!Util.isWideMode(this.mActivity)) {
            this.mAlarmLv.setLayoutManager(new LinearLayoutManager(this.mActivity));
            return;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.mActivity, (!Util.isPadOrientationLand(this.mActivity) || this.mActivity.isInMultiWindowMode()) ? 2 : 3);
        this.mAlarmLv.setLayoutManager(gridLayoutManager);
        if (BedtimeUtil.isWakeAlarmSupport(this.mActivity.getApplicationContext())) {
            Alarm alarmQueryWakeAlarm = DataPrepareUtil.queryWakeAlarm();
            this.mAlarmAdapter.hasWakeAlarm(alarmQueryWakeAlarm != null);
            if (alarmQueryWakeAlarm != null) {
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: com.android.deskclock.alarm.AlarmClockFragment.25
                    @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                    public int getSpanSize(int i) {
                        return i < 3 ? 2 : 1;
                    }
                });
            }
        }
    }

    private void updateBlankPage() {
        AlarmAdapter alarmAdapter = this.mAlarmAdapter;
        if (alarmAdapter != null) {
            if (alarmAdapter.getItemCount() == 0) {
                if (this.mAlarmBlankPage == null) {
                    this.mAlarmBlankPage = ((ViewStub) this.mRootView.findViewById(R.id.alarm_blank_page_lite_stub)).inflate();
                }
                this.mAlarmLv.setVisibility(4);
                View view = this.mAlarmBlankPage;
                if (view != null) {
                    view.setVisibility(0);
                    return;
                }
                return;
            }
            View view2 = this.mAlarmBlankPage;
            if (view2 != null) {
                view2.setVisibility(8);
            }
            this.mAlarmLv.setVisibility(0);
        }
    }

    public void startFromShortcut() {
        this.isFromShortCutNewAlarm = true;
        new Handler().postDelayed(new Runnable() { // from class: com.android.deskclock.alarm.AlarmClockFragment.27
            @Override // java.lang.Runnable
            public void run() {
                if (AlarmClockFragment.this.mSetAlarmController != null && AlarmClockFragment.this.mSetAlarmBottomSheetModal != null && AlarmClockFragment.this.mSetAlarmController.isRepeatAlarmDialogShow()) {
                    AlarmClockFragment.this.mSetAlarmController.resetRepeatAlarmDialog();
                }
                AlarmClockFragment.this.showSetAlarmDialog(null, null, null, false);
            }
        }, 200L);
    }

    @Override // com.android.deskclock.base.BaseClockFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
    }

    private void setNotificationPermission() {
        LinearLayout linearLayout;
        Log.d(TAG, "setNotificationPermission");
        if (Build.VERSION.SDK_INT >= 33) {
            if (!PermissionUtil.checkPermission(this.mActivity, "android.permission.POST_NOTIFICATIONS")) {
                DeskClockTabActivity.NOTIFICATION_PERMISSION_GRANTED = false;
                showNotificationPermission();
                return;
            }
            View view = this.mNotificationPermissionView;
            if (view == null || (linearLayout = this.mAlarmScrollView) == null) {
                return;
            }
            linearLayout.removeView(view);
            this.mNotificationPermissionView.setVisibility(8);
            DeskClockTabActivity.NOTIFICATION_PERMISSION_GRANTED = true;
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimeFormatChanged() {
        AlarmModel alarmModel;
        AlarmAdapter alarmAdapter = this.mAlarmAdapter;
        if (alarmAdapter == null || (alarmModel = this.mAlarmModel) == null) {
            return;
        }
        startTimeTask(alarmAdapter, alarmModel);
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimeChanged() {
        AlarmModel alarmModel;
        AlarmAdapter alarmAdapter = this.mAlarmAdapter;
        if (alarmAdapter == null || (alarmModel = this.mAlarmModel) == null) {
            return;
        }
        startTimeTask(alarmAdapter, alarmModel);
    }

    @Override // com.android.deskclock.base.BaseClockFragment
    protected String getTab() {
        return TabViewModel.TAB_ALARM;
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimezoneChanged() {
        if (this.mAlarmAdapter != null) {
            this.mCalender.setTimeZone(TimeZone.getDefault());
            this.mAlarmModel.resetCalender();
            startTimeTask(this.mAlarmAdapter, this.mAlarmModel);
        }
    }

    public void dismissSetAlarmDialog() {
        BottomSheetModal bottomSheetModal = this.mSetAlarmBottomSheetModal;
        if (bottomSheetModal == null || this.mSetAlarmBottomSheetBehavior == null) {
            return;
        }
        bottomSheetModal.dismiss();
    }

    public void handleRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        this.mSetAlarmController.handleRequestPermissionResult(i, strArr, iArr);
    }

    private static class TimeChangeAsyncTask extends AsyncTask<Void, Void, Void> {
        private AlarmAdapter mAlarmAdapter;
        private AlarmModel mAlarmModel;
        private WeakReference<AlarmClockFragment> mReference;

        public TimeChangeAsyncTask(AlarmClockFragment alarmClockFragment, AlarmAdapter alarmAdapter, AlarmModel alarmModel) {
            this.mAlarmAdapter = alarmAdapter;
            this.mAlarmModel = alarmModel;
            this.mReference = new WeakReference<>(alarmClockFragment);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            this.mAlarmModel.updateData();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            this.mAlarmAdapter.notifyDataSetChanged();
            AlarmClockFragment alarmClockFragment = this.mReference.get();
            if (alarmClockFragment != null) {
                alarmClockFragment.setNextAlertTime();
            }
        }
    }

    private void startTimeTask(AlarmAdapter alarmAdapter, AlarmModel alarmModel) {
        cancelTimeTask();
        TimeChangeAsyncTask timeChangeAsyncTask = new TimeChangeAsyncTask(this, alarmAdapter, alarmModel);
        this.mTimeChangeAsyncTask = timeChangeAsyncTask;
        timeChangeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void cancelTimeTask() {
        AsyncTask<Void, Void, Void> asyncTask = this.mTimeChangeAsyncTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.mTimeChangeAsyncTask = null;
        }
    }

    public void setAlarmAlertBannerLayout(final Alarm alarm) {
        Log.d(TAG, "setAlarmAlertBannerLayout mIsAlarmAlertBannerShow :" + mIsAlarmAlertBannerShow + "   " + alarm);
        if (mIsAlarmAlertBannerShow || alarm == null) {
            return;
        }
        mIsAlarmAlertBannerShow = true;
        View viewInflate = LayoutInflater.from(this.mActivity).inflate(R.layout.alarm_alert_ringing_banner, (ViewGroup) this.mAlarmScrollView, false);
        this.mAlarmAlertBannerView = viewInflate;
        TextView textView = (TextView) viewInflate.findViewById(R.id.alarm_time);
        this.mAlarmTime = textView;
        textView.setText(AlarmHelper.formatAlarmTime(getContext(), alarm));
        this.mAlarmClose = (ImageView) this.mAlarmAlertBannerView.findViewById(R.id.alarm_alert_close_item);
        this.mAlarmBannerImage = (ImageView) this.mAlarmAlertBannerView.findViewById(R.id.alarm_banner_image);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        int dimension = (int) this.mActivity.getResources().getDimension(R.dimen.alarm_alert_banner_margin_top);
        layoutParams.bottomMargin = (int) this.mActivity.getResources().getDimension(R.dimen.alarm_alert_banner_margin_bottom);
        layoutParams.topMargin = dimension;
        layoutParams.setMarginStart((int) this.mActivity.getResources().getDimension(R.dimen.alarm_alert_banner_margin_start));
        layoutParams.setMarginEnd((int) this.mActivity.getResources().getDimension(R.dimen.alarm_alert_banner_margin_start));
        this.mAlarmAlertBannerView.setPadding((int) this.mActivity.getResources().getDimension(R.dimen.alarm_time_layout_margin_start), 0, (int) this.mActivity.getResources().getDimension(R.dimen.alarm_time_layout_margin_start), 0);
        this.mAlarmScrollView.addView(this.mAlarmAlertBannerView, 0, layoutParams);
        this.mAlarmAlertBannerView.setVisibility(0);
        if (Util.isTinyScreen(this.mActivity)) {
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.mAlarmClose.getLayoutParams();
            layoutParams2.setMarginEnd((int) this.mActivity.getResources().getDimension(R.dimen.alarm_alert_close_margin_end_tiny));
            this.mAlarmClose.setLayoutParams(layoutParams2);
            RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) this.mAlarmBannerImage.getLayoutParams();
            layoutParams3.setMarginStart((int) this.mActivity.getResources().getDimension(R.dimen.alarm_alert_close_margin_end_tiny));
            this.mAlarmBannerImage.setLayoutParams(layoutParams3);
        }
        if (MiuiSdk.isSupportFolmeAnim() && !MiuiSdk.isLiteOrMiddleMode()) {
            MiuiFolme.registerPressAnim(this.mAlarmAlertBannerView);
        }
        this.mAlarmAlertBannerView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.28
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PadAdapterUtil.IS_PAD && AlarmClockFragment.this.mActivity.isInMultiWindowMode()) {
                    return;
                }
                Log.i(AlarmClockFragment.TAG, "start AlarmAlertFullScreenActivity from alarmAlertBannerView click");
                Intent intent = new Intent(AlarmClockFragment.this.getContext(), (Class<?>) AlarmAlertFullScreenActivity.class);
                intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
                intent.setFlags(268697600);
                AlarmClockFragment.this.getContext().startActivity(intent);
                AlarmClockFragment.this.mAlarmAlertBannerView.setVisibility(8);
                boolean unused = AlarmClockFragment.mIsAlarmAlertBannerShow = false;
            }
        });
        this.mAlarmClose.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.29
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.i(AlarmClockFragment.TAG, "dismiss alarm from alarmAlertBannerView");
                Intent intent = new Intent(AlarmHelper.ACTION_ALARM_DISMISS);
                intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
                intent.setPackage(AlarmClockFragment.this.getContext().getPackageName());
                AlarmClockFragment.this.getContext().sendBroadcast(intent);
                AlarmClockFragment.this.mAlarmAlertBannerView.setVisibility(8);
                boolean unused = AlarmClockFragment.mIsAlarmAlertBannerShow = false;
            }
        });
    }

    public void showNotificationPermission() {
        View view = this.mNotificationPermissionView;
        if (view != null) {
            view.setVisibility(0);
            return;
        }
        this.mNotificationPermissionView = LayoutInflater.from(this.mActivity).inflate(R.layout.notification_permission_open_remind, (ViewGroup) this.mAlarmScrollView, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        int dimension = (int) this.mActivity.getResources().getDimension(R.dimen.notification_permission_margin_top);
        int dimension2 = (int) this.mActivity.getResources().getDimension(R.dimen.notification_permission_margin_bottom);
        int dimension3 = (int) this.mActivity.getResources().getDimension(R.dimen.notification_permission_margin_left);
        layoutParams.setMarginStart(dimension3);
        layoutParams.setMarginEnd(dimension3);
        if (PadAdapterUtil.IS_PAD || MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode() || !MiuiSdk.isSupportSleep() || !BedtimeUtil.showBedTimeBanner(getContext())) {
            layoutParams.bottomMargin = dimension2;
            layoutParams.topMargin = dimension;
        } else {
            layoutParams.topMargin = dimension2;
            layoutParams.bottomMargin = dimension2;
        }
        this.mAlarmScrollView.addView(this.mNotificationPermissionView, 1, layoutParams);
        this.mNotificationPermissionView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.30
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                UserNoticeUtil.gotoNotificationSettingPage(AlarmClockFragment.this.mActivity);
            }
        });
    }

    public void resetNotificationPermissionLayout() {
        View view = this.mNotificationPermissionView;
        if (view != null) {
            view.setVisibility(0);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mNotificationPermissionView.getLayoutParams();
            layoutParams.bottomMargin = (int) this.mActivity.getResources().getDimension(R.dimen.notification_permission_margin_bottom);
            layoutParams.topMargin = (int) this.mActivity.getResources().getDimension(R.dimen.notification_permission_margin_bottom);
        }
    }

    private void initFragmentResultLauncher() {
        this.toBedtimeLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.alarm.AlarmClockFragment$$ExternalSyntheticLambda0
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m81x555b3549((ActivityResult) obj);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initFragmentResultLauncher$0$com-android-deskclock-alarm-AlarmClockFragment, reason: not valid java name */
    /* synthetic */ void m81x555b3549(ActivityResult activityResult) {
        toBedtimeActivityResult(activityResult.getResultCode());
    }

    public void toBedtimeActivityResult(int i) {
        if (i != -1 || this.mActivity == null) {
            return;
        }
        resetNotificationPermissionLayout();
    }

    @Override // com.android.deskclock.base.BaseClockFragment
    public void destroyActionMode() {
        if (this.mAlarmAdapter != null) {
            if (this.mActivity != null) {
                this.mActivity.setNavigationForActionMode(false);
            }
            this.mAlarmAdapter.finishActionMode();
        }
    }

    public boolean cancelDialogView() {
        AlarmEditDialogView alarmEditDialogView = this.mAlarmEditDialogView;
        if (alarmEditDialogView != null && alarmEditDialogView.isShowing()) {
            this.mAlarmEditDialogView.dismiss();
            return true;
        }
        if (this.mSetAlarmController == null || this.mSetAlarmBottomSheetModal == null) {
            return false;
        }
        SetAlarmController.isBackValid = true;
        if (!this.mSetAlarmController.isRepeatAlarmDialogShow()) {
            return false;
        }
        this.mSetAlarmController.handleAlarmRepeatResult();
        return false;
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onDataChanged() {
        AlarmModel alarmModel = this.mAlarmModel;
        if (alarmModel != null) {
            alarmModel.startLoad();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        ActionSheet.IActionSheet iActionSheet;
        DialogUtil.dismissDialogFragment(this.mCloseConfirmDialog);
        this.mCloseConfirmDialog = null;
        resetDialog();
        if (this.mIsActionSheetShowing && (iActionSheet = this.mCurrentActionSheet) != null) {
            iActionSheet.dismiss();
        }
        super.onPause();
    }

    private void resetDialog() {
        if (!MiuiSdk.isSuperLiteMode() && !MiuiSdk.isLiteV1StockMode()) {
            AlarmEditDialogView alarmEditDialogView = this.mAlarmEditDialogView;
            if (alarmEditDialogView != null) {
                alarmEditDialogView.setOnDismissListener(null);
                this.mAlarmEditDialogView.setOnSaveAlarmListener(null);
                this.mAlarmEditDialogView.dismissDirectly();
                this.mAlarmEditDialogView = null;
            }
            AlarmEditDialog alarmEditDialog = this.mAlarmEditDialog;
            if (alarmEditDialog != null) {
                alarmEditDialog.setOnDismissListener(null);
                this.mAlarmEditDialog.setOnSaveAlarmListener(null);
                DialogUtil.dismissDialogFragment(this.mAlarmEditDialog);
                this.mAlarmEditDialog = null;
            }
        }
        DialogUtil.dismissDialogFragment(this.mCloseConfirmDialog);
        this.mCloseConfirmPosition = -1;
        this.mCloseConfirmDialog = null;
    }

    public static class AlarmObserverImp implements AlarmModel.AlarmObserver {
        private WeakReference<AlarmClockFragment> mReference;

        public AlarmObserverImp(AlarmClockFragment alarmClockFragment) {
            this.mReference = new WeakReference<>(alarmClockFragment);
        }

        @Override // com.android.deskclock.alarm.AlarmModel.AlarmObserver
        public void onAlarmLoaded(List<AlarmModel.AlarmBean> list) {
            AlarmClockFragment alarmClockFragment = this.mReference.get();
            if (alarmClockFragment != null) {
                alarmClockFragment.onAlarmLoaded(list);
            }
        }

        @Override // com.android.deskclock.alarm.AlarmModel.AlarmObserver
        public void onAlarmChanged(Boolean bool) {
            AlarmClockFragment alarmClockFragment = this.mReference.get();
            if (alarmClockFragment != null) {
                alarmClockFragment.onAlarmChanged(bool);
                alarmClockFragment.setNextAlertTime();
            }
        }
    }

    public void onAlarmLoaded(List<AlarmModel.AlarmBean> list) {
        AlarmAdapter alarmAdapter = this.mAlarmAdapter;
        if (alarmAdapter != null) {
            alarmAdapter.initData(list);
            this.mAlarmAdapter.notifyDataSetChanged();
            setNextAlertTime();
        }
        updateBlankPage();
        this.mHandler.postDelayed(this.mNotifyTaskRunnable, 600L);
    }

    public void onAlarmChanged(Boolean bool) {
        if (bool.booleanValue() && Util.isWideMode(this.mActivity)) {
            setRvLayoutMode();
            cancelDialogView();
        }
        AlarmAdapter alarmAdapter = this.mAlarmAdapter;
        if (alarmAdapter != null) {
            alarmAdapter.notifyDataSetChanged();
            doPendingDismissEditDialog();
        }
        updateBlankPage();
        this.mHandler.postDelayed(this.mNotifyTaskRunnable, 600L);
    }

    private void doPendingDismissEditDialog() {
        if (this.mRequestSaveAlarm) {
            this.mRequestSaveAlarm = false;
            AlarmEditDialogView alarmEditDialogView = this.mAlarmEditDialogView;
            if (alarmEditDialogView == null || !alarmEditDialogView.isShowing()) {
                return;
            }
            final int positionById = this.mAlarmAdapter.getPositionById(this.mAlarmEditDialogView.getCurrentId());
            View viewFindViewByPosition = this.mAlarmLv.getLayoutManager().findViewByPosition(positionById);
            if (viewFindViewByPosition != null) {
                restoreListItemAnimator();
                this.mAlarmEditDialogView.updateAnchorTop(viewFindViewByPosition);
                this.mAlarmEditDialogView.dismiss();
            } else {
                this.mAlarmLv.getLayoutManager().scrollToPosition(positionById);
                this.mAlarmLv.post(new Runnable() { // from class: com.android.deskclock.alarm.AlarmClockFragment.31
                    @Override // java.lang.Runnable
                    public void run() {
                        AlarmClockFragment.this.mAlarmEditDialogView.updateAnchorTop(AlarmClockFragment.this.mAlarmLv.getLayoutManager().findViewByPosition(positionById));
                        AlarmClockFragment.this.mAlarmEditDialogView.dismiss();
                        AlarmClockFragment.this.restoreListItemAnimator();
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreListItemAnimator() {
        AlarmRecyclerView alarmRecyclerView = this.mAlarmLv;
        if (alarmRecyclerView == null || alarmRecyclerView.getItemAnimator() != null) {
            return;
        }
        this.mAlarmLv.setItemAnimator(new MiuiScaleItemAnimator());
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onEndClick2(View view) {
        super.onEndClick2(view);
        Log.d(TAG, "onEndClick2: ");
        showSetAlarmDialog(null, null, null, false);
        StatHelper.alarmEvent(StatHelper.EVENT_CLICK_ALARM_ADD);
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_FAB_ADD_CLICK);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSetAlarmDialog(Bundle bundle, Alarm alarm, Alarm alarm2, Boolean bool) {
        SetAlarmController setAlarmController = this.mSetAlarmController;
        if (setAlarmController != null && this.mSetAlarmBottomSheetModal != null) {
            if (setAlarmController.isRepeatAlarmDialogShow()) {
                this.mSetAlarmController.dismissRepeatAlarmDialog();
                return;
            }
            return;
        }
        this.mSetAlarmBottomSheetModal = new BottomSheetModal(this.mActivity);
        View viewInflate = LayoutInflater.from(this.mActivity).inflate(R.layout.new_alarm_bottom_sheet_modal, (ViewGroup) null);
        this.mSetAlarmContentView = viewInflate;
        this.mSetAlarmBottomSheetModal.setContentView(viewInflate);
        this.mSetAlarmBottomSheetModal.setDragHandleViewEnabled(true);
        this.mSetAlarmBottomSheetModal.setOnDismissListener(new BottomSheetModal.OnDismissListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.32
            @Override // miuix.bottomsheet.BottomSheetModal.OnDismissListener
            public void onDismiss() {
                if (AlarmClockFragment.this.mSetAlarmController != null) {
                    AlarmClockFragment.this.mSetAlarmController.removeGlobalLayoutListener();
                    AlarmClockFragment.this.mSetAlarmController.onDestroy();
                }
                if (AlarmClockFragment.this.mSetAlarmContentView != null) {
                    Folme.getTarget(AlarmClockFragment.this.mSetAlarmContentView).clean();
                    Folme.clean(AlarmClockFragment.this.mSetAlarmContentView);
                }
                if (AlarmClockFragment.this.mSetAlarmBottomSheetModal != null) {
                    Folme.getTarget((View) AlarmClockFragment.this.mSetAlarmBottomSheetModal.getBottomSheetView()).clean();
                    Folme.clean(AlarmClockFragment.this.mSetAlarmBottomSheetModal.getBottomSheetView());
                    AlarmClockFragment.this.mSetAlarmBottomSheetModal.release();
                    AlarmClockFragment.this.mSetAlarmBottomSheetModal = null;
                }
                AlarmClockFragment.this.mSetAlarmBottomSheetBehavior = null;
            }
        });
        SetAlarmController setAlarmController2 = new SetAlarmController(this.mActivity, this.mSetAlarmContentView, bundle);
        this.mSetAlarmController = setAlarmController2;
        setAlarmController2.initData(alarm, alarm2, bool.booleanValue(), false);
        this.mSetAlarmController.initBundleData(bundle);
        this.mSetAlarmController.initOtherData();
        this.mSetAlarmController.setBackButtonClickListener(new AlarmController.BackButtonClickListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.33
            @Override // com.android.deskclock.alarm.AlarmController.BackButtonClickListener
            public void onButtonClick() {
                AlarmClockFragment.this.dismissSetAlarmDialog();
            }
        });
        this.mSetAlarmBottomSheetModal.setOnBackListener(new BottomSheetModal.OnBackListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.34
            @Override // miuix.bottomsheet.BottomSheetModal.OnBackListener
            public boolean onBack() {
                View viewFindViewById;
                if (AlarmClockFragment.this.mSetAlarmController == null || !AlarmClockFragment.this.mSetAlarmController.isInMoreShiftAlarmsPage() || (viewFindViewById = AlarmClockFragment.this.mSetAlarmContentView.findViewById(16908313)) == null || viewFindViewById.getVisibility() != 0) {
                    return false;
                }
                viewFindViewById.performClick();
                return true;
            }
        });
        BottomSheetBehavior<FrameLayout> behavior = this.mSetAlarmBottomSheetModal.getBehavior();
        this.mSetAlarmBottomSheetBehavior = behavior;
        behavior.setOnModeChangeListener(new BottomSheetBehavior.OnModeChangeListener() { // from class: com.android.deskclock.alarm.AlarmClockFragment.35
            @Override // miuix.bottomsheet.BottomSheetBehavior.OnModeChangeListener
            public void onModeChange(int i, View view) {
                if (i == 1) {
                    AlarmClockFragment.this.mSetAlarmController.setViewLayout(true);
                } else {
                    AlarmClockFragment.this.mSetAlarmController.setViewLayout(false);
                }
            }
        });
        this.mSetAlarmBottomSheetBehavior.setDraggable(true);
        this.mSetAlarmBottomSheetBehavior.setSkipHalfExpanded(true);
        this.mSetAlarmBottomSheetBehavior.setSkipCollapsed(true);
        this.mSetAlarmBottomSheetBehavior.setForceFullHeight(true);
        this.mSetAlarmBottomSheetBehavior.setState(3);
        this.mSetAlarmBottomSheetModal.show();
        if (bundle == null || !bundle.getBoolean(SetAlarmController.IS_SHOW_REPEAT_ALARM_DIALOG)) {
            return;
        }
        this.mSetAlarmController.showRepeatAlarmDialog(bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setContentDescription() {
        try {
            if (this.mAlarmAdapter.isAllItemsChecked()) {
                this.mActivity.getWindow().getDecorView().findViewById(16908314).setContentDescription(this.mActivity.getResources().getString(R.string.miuix_appcompat_deselect_all_description));
            } else {
                this.mActivity.getWindow().getDecorView().findViewById(16908314).setContentDescription(this.mActivity.getResources().getString(R.string.miuix_appcompat_select_all_description));
            }
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setActionModeClickDescription() {
        try {
            View viewFindViewById = this.mActivity.getWindow().getDecorView().findViewById(16908314);
            if (viewFindViewById != null) {
                viewFindViewById.sendAccessibilityEvent(4);
            }
        } catch (Exception e) {
            Log.e(TAG, "setActionModeClickDescription error is " + e);
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.isFromShortCutNewAlarm || isFromCtsSetAlarm) {
            this.isFromShortCutNewAlarm = false;
            SetAlarmController setAlarmController = this.mSetAlarmController;
            if (setAlarmController != null && this.mSetAlarmBottomSheetModal != null && setAlarmController.isRepeatAlarmDialogShow()) {
                this.mSetAlarmController.resetRepeatAlarmDialog();
            }
            if (isFromCtsSetAlarm) {
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.deskclock.alarm.AlarmClockFragment.36
                    @Override // java.lang.Runnable
                    public void run() {
                        AlarmClockFragment.this.showSetAlarmDialog(null, TabNavigatorContentFragment.mFromCtsAlarm, null, true);
                    }
                }, 200L);
            }
        }
        setNextAlertTime();
        AlarmModel alarmModel = this.mAlarmModel;
        if (alarmModel != null) {
            alarmModel.startLoad();
        }
        if (AlarmUtils.alarmAlertStatus) {
            setAlarmAlertBannerLayout(AlarmService.getCurrentAlarm());
        } else {
            View view = this.mAlarmAlertBannerView;
            if (view != null) {
                view.setVisibility(8);
                mIsAlarmAlertBannerShow = false;
            }
        }
        setNotificationPermission();
    }

    @Override // com.android.deskclock.base.BaseClockFragment
    protected void initView() {
        super.initView();
        this.mInitialized = true;
    }

    static class AlarmFabClickListenerImpl implements FabControllerNew.onAlarmFabClickListener {
        private WeakReference<AlarmClockFragment> mWeakReference;

        public AlarmFabClickListenerImpl(AlarmClockFragment alarmClockFragment) {
            this.mWeakReference = new WeakReference<>(alarmClockFragment);
        }

        @Override // com.android.deskclock.util.fab.FabControllerNew.onAlarmFabClickListener
        public void onEndFabClick2(View view) {
            WeakReference<AlarmClockFragment> weakReference = this.mWeakReference;
            AlarmClockFragment alarmClockFragment = weakReference == null ? null : weakReference.get();
            if (alarmClockFragment != null) {
                alarmClockFragment.onEndClick2(view);
            }
        }
    }

    private static class WeakResultCallback implements ActivityResultCallback<ActivityResult> {
        protected final WeakReference<AlarmClockFragment> mFragmentRef;

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // androidx.activity.result.ActivityResultCallback
        public void onActivityResult(ActivityResult activityResult) {
        }

        WeakResultCallback(AlarmClockFragment alarmClockFragment) {
            this.mFragmentRef = new WeakReference<>(alarmClockFragment);
        }
    }
}
