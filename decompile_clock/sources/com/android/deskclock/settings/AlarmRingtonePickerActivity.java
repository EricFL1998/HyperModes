package com.android.deskclock.settings;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.GridLayoutManager;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
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
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.settings.pref.AlarmRingtoneAdapter;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.NetworkUtil;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.UiUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.PermissionUtil;
import com.android.deskclock.util.permission.SystemPermissionUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.util.themeringtone.RingtoneHelper;
import com.android.deskclock.util.themeringtone.ThemeProviderHelper;
import com.android.deskclock.view.SimpleDialogFragment;
import com.android.deskclock.view.list.AlarmRecyclerView;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URI;
import miuix.appcompat.app.ActionBar;
import miuix.core.widget.NestedScrollView;
import miuix.responsive.map.ScreenSpec;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes.dex */
public class AlarmRingtonePickerActivity extends BaseActivity {
    public static final String ACTION_ALARM_RINGTONE_PICKER = "miui.intent.action.ALARM_RINGTONE_PICKER";
    public static final String IS_FROM_ALARM = "is_from_alarm";
    public static final String IS_SET_MODE = "is_set_mode";
    public static final String IS_SUPPORT_XIAO_AI_RINGTONE = "support_xiaoai_ringtone";
    public static final String KEY_LAST_OTHER_RINGTONE = "last_other_ringtone";
    public static final int ONE_HOUR_TIME = 3600000;
    public static final int REQUEST_CODE_XIAO_AI_RINGTONE = 112;
    private static final String TAG = "DC:AlarmRingtonePickerActivity";
    public static final String VALUE_NO_RECORD = "noRecord";
    private boolean isFromAlarm;
    private ActionBar mActionBar;
    private Activity mActivity;
    private AlarmRingtoneAdapter mAlarmRingtoneAdapter;
    private AlarmRecyclerView mAlarmRingtoneLv;
    private Uri mAlert;
    private RingtonePlayServiceConnection mConnection;
    private boolean mIsWeatherNeedPermission;
    private boolean mIsWeekNeedPermission;
    private boolean mIsXiaoAiOrTimerNeedPermission;
    private Uri mNeedPermissionUri;
    private Uri mOldAlert;
    private ResourceLoadServiceCallback mResourceLoadCallback;
    private ResourceLoadServiceConnection mResourceLoadConnection;
    private ResourceLoadService mResourceLoadService;
    private RingtonePlayServiceCallback mRingtonePlayCallback;
    private RingtonePlayService mRingtonePlayService;
    private LinearLayout mRootView;
    private NestedScrollView mScrollView;
    private int mSelectedViewId;
    private SharedPreferences mSharedPreferences;
    private SimpleDialogFragment mWeatherRingtoneDownloadDialog;
    private SimpleDialogFragment mWeatherRingtoneIntroduceDialog;
    private SimpleDialogFragment mWeekRingtoneDownloadDialog;
    private SimpleDialogFragment mWeekRingtoneIntroduceDialog;
    private ActivityResultLauncher<Intent> toCtaLauncher;
    private ActivityResultLauncher<Intent> toThemeRingtoneLauncher;
    private Handler mHandler = new Handler();
    private SimpleDialogFragment mUserNoticeDialog = null;
    private boolean mThemeSupportChangeAlertDirectly = false;
    private boolean mShouldPlay = false;
    private boolean mIsSetMode = true;
    private boolean mIsSupportXiaoAiRingtone = false;
    private boolean mIsPermissionGranted = false;
    private boolean mSupportLinearMotorVibrate = false;
    private boolean mIsFromSetting = false;
    private boolean mLoadAfterServiceBind = false;
    private boolean mDownloadAfterServiceBind = false;

    @Override // miuix.appcompat.app.AppCompatActivity
    protected boolean isResponsiveEnabled() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        if ((getIntent() != null && ACTION_ALARM_RINGTONE_PICKER.equals(getIntent().getAction())) || Util.isTinyScreen(this)) {
            setTheme(R.style.BaseTheme);
            this.mIsFromSetting = true;
        } else {
            setTheme(R.style.BaseThemeForPad);
        }
        super.onCreate(bundle);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.action_bar_overlay_layout);
        if (!isInFloatingWindowMode()) {
            viewGroup.setBackground(new ColorDrawable(getResources().getColor(R.color.main_bg)));
        }
        this.mActivity = this;
        setContentView(R.layout.activity_alarm_ringtone_picker);
        this.mIsSetMode = getIntent().getBooleanExtra(IS_SET_MODE, true);
        this.mIsSupportXiaoAiRingtone = getIntent().getBooleanExtra(IS_SUPPORT_XIAO_AI_RINGTONE, false) && XiaoAiRingtoneHelper.isAvailable();
        if (Build.VERSION.SDK_INT >= 33) {
            this.mIsPermissionGranted = PermissionUtil.isPermissionGranted(this, "android.permission.READ_MEDIA_AUDIO");
        } else {
            this.mIsPermissionGranted = PermissionUtil.isPermissionGranted(this, "android.permission.READ_EXTERNAL_STORAGE");
        }
        this.mScrollView = (NestedScrollView) findViewById(R.id.scroll_holder);
        this.mRootView = (LinearLayout) findViewById(R.id.ringtoneLayout);
        this.mSupportLinearMotorVibrate = Util.isSupportLinearMotorVibrate();
        this.isFromAlarm = getIntent().getBooleanExtra(IS_FROM_ALARM, false);
        if (getIntent() != null && !this.isFromAlarm && PadAdapterUtil.IS_PAD) {
            getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.ringtone_picker_background_color));
            initActionBar();
        }
        this.mSharedPreferences = FBEUtil.getDefaultSharedPreferences(this);
        this.mThemeSupportChangeAlertDirectly = MiuiTheme.supportChangeAlertDirectly();
        this.mConnection = new RingtonePlayServiceConnection();
        this.mRingtonePlayCallback = new RingtonePlayServiceCallback(this);
        if (this.mIsSetMode) {
            getDefaultRingtone();
        } else if (RingtoneUriCompat.atLeastU()) {
            String string = Settings.Global.getString(getContentResolver(), "android.intent.extra.ringtone.PICKED_URI");
            if (string != null && !this.isFromAlarm) {
                this.mAlert = Uri.parse(string);
            } else if (bundle != null) {
                this.mAlert = (Uri) bundle.getParcelable("android.intent.extra.ringtone.PICKED_URI");
            } else {
                this.mAlert = (Uri) getIntent().getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            }
        } else {
            this.mAlert = (Uri) getIntent().getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
        }
        Intent intent = new Intent();
        intent.putExtra("android.intent.extra.ringtone.PICKED_URI", this.mAlert);
        setResult(-1, intent);
        initAlarmRingtonePicker();
        Log.d(TAG, "init mAlert: " + this.mAlert + ", mIsSetMode: " + this.mIsSetMode);
        showDefaultRingtone();
        setOtherRingtone();
        if (RingtoneUriCompat.atLeastU() && !Util.isInternational() && UserNoticeUtil.isNetPermissionAgreed() && !this.mIsFromSetting) {
            RingtoneUriCompat.updateConvertAllUri();
        }
        loadExternalResource();
        this.mScrollView.post(new Runnable() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.1
            @Override // java.lang.Runnable
            public void run() {
                AlarmRingtonePickerActivity.this.mScrollView.fullScroll(33);
            }
        });
        setVolumeControlStream(4);
        if (this.mIsSetMode && this.mIsPermissionGranted) {
            RingtoneHelper.handleRingtonePickerAlert(this);
        }
        initActivityResultLauncher();
        if (isInFloatingWindowMode()) {
            this.mRootView.setPadding((int) getResources().getDimension(R.dimen.ringtone_picker_item_floating_margin_start), (int) getResources().getDimension(R.dimen.ringtone_picker_padding_top), (int) getResources().getDimension(R.dimen.ringtone_picker_item_floating_margin_start), (int) getResources().getDimension(R.dimen.ringtone_picker_padding_top));
        } else {
            this.mRootView.setPadding((int) getResources().getDimension(R.dimen.ringtone_picker_item_margin_start), (int) getResources().getDimension(R.dimen.ringtone_picker_padding_top), (int) getResources().getDimension(R.dimen.ringtone_picker_item_margin_start), (int) getResources().getDimension(R.dimen.ringtone_picker_padding_top));
        }
        Util.cutOut(this);
    }

    private void initAlarmRingtonePicker() {
        this.mAlarmRingtoneLv = (AlarmRecyclerView) findViewById(android.R.id.list);
        AlarmRingtoneAdapter alarmRingtoneAdapter = new AlarmRingtoneAdapter(this);
        this.mAlarmRingtoneAdapter = alarmRingtoneAdapter;
        this.mAlarmRingtoneLv.setAdapter(alarmRingtoneAdapter);
        this.mAlarmRingtoneAdapter.setOnItemClickListener(new AlarmRingtoneAdapter.OnItemClickListener() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.2
            @Override // com.android.deskclock.settings.pref.AlarmRingtoneAdapter.OnItemClickListener
            public void onRingtoneItemClick(View view, int i) {
                if (AlarmRingtonePickerActivity.this.mSelectedViewId != i) {
                    AlarmRingtonePickerActivity.this.doVibrate(view);
                }
                if (i == 0) {
                    if (WeatherRingtoneHelper.isSupport()) {
                        AlarmRingtonePickerActivity.this.setWeatherRingtoneSelected();
                        return;
                    } else {
                        AlarmRingtonePickerActivity.this.showWeatherRingtoneDownloadDialog();
                        return;
                    }
                }
                if (i == 1) {
                    if (WeekRingtoneHelper.isSupport()) {
                        AlarmRingtonePickerActivity.this.setWeekRingtoneSelected();
                        return;
                    } else {
                        AlarmRingtonePickerActivity.this.showWeekRingtoneDownloadDialog();
                        return;
                    }
                }
                if (i == 2) {
                    AlarmRingtonePickerActivity.this.setDewRingtoneSelected();
                } else if (i == 3) {
                    AlarmRingtonePickerActivity.this.setFireflyRingtoneSelected();
                } else if (i == 4) {
                    AlarmRingtonePickerActivity.this.setDreamRingtoneSelected();
                }
            }
        });
        this.mAlarmRingtoneAdapter.setOnMoreClickListener(new AlarmRingtoneAdapter.OnMoreClickListener() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.3
            @Override // com.android.deskclock.settings.pref.AlarmRingtoneAdapter.OnMoreClickListener
            public void onMoreItemClick(View view, int i) {
                if (AlarmRingtonePickerActivity.this.mSelectedViewId != i) {
                    AlarmRingtonePickerActivity.this.doVibrate(view);
                }
                if (!AlarmRingtonePickerActivity.this.mIsSetMode || !AlarmRingtonePickerActivity.this.mIsPermissionGranted) {
                    AlarmRingtonePickerActivity.this.openRingtonePicker();
                } else if (AlarmRingtonePickerActivity.this.mSharedPreferences.getString(AlarmRingtonePickerActivity.KEY_LAST_OTHER_RINGTONE, AlarmRingtonePickerActivity.VALUE_NO_RECORD).equals(AlarmRingtonePickerActivity.VALUE_NO_RECORD)) {
                    AlarmRingtonePickerActivity.this.openRingtonePicker();
                } else {
                    AlarmRingtonePickerActivity.this.setLastOtherRingtoneSelected();
                }
            }
        });
        this.mAlarmRingtoneAdapter.setListener(new AlarmRingtoneAdapter.OnAllClickListener() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.4
            @Override // com.android.deskclock.settings.pref.AlarmRingtoneAdapter.OnAllClickListener
            public void onClick() {
                AlarmRingtonePickerActivity.this.openRingtonePicker();
            }
        });
        setRingtonePickerViewLayout();
    }

    /* JADX WARN: Code duplicated, block: B:11:0x0041  */
    /* JADX WARN: Code duplicated, block: B:15:0x0048  */
    private void setRingtonePickerViewLayout() {
        if (this.mAlarmRingtoneLv == null) {
            return;
        }
        int i = getResources().getConfiguration().screenWidthDp;
        Log.d(TAG, "setRingtonePickerViewLayout: " + i);
        int i2 = 3;
        if (!isInFloatingWindowMode()) {
            if (Util.isFreeFormScreen(getResources().getConfiguration())) {
                if (i < 440) {
                    i2 = 2;
                } else if (i <= 440 || i >= 1000) {
                    i2 = 4;
                }
            } else if (this.isFromAlarm) {
                if (i < 440 || i <= 440 || i >= 1000) {
                    i2 = 2;
                }
            } else if (i < 730) {
                i2 = 2;
            } else if (i <= 730 || i >= 1200) {
                i2 = 4;
            }
        }
        this.mAlarmRingtoneLv.setLayoutManager(new GridLayoutManager(this, i2));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("android.intent.extra.ringtone.PICKED_URI", this.mAlert);
    }

    private void initActionBar() {
        ActionBar appCompatActionBar = getAppCompatActionBar();
        this.mActionBar = appCompatActionBar;
        appCompatActionBar.setDisplayShowCustomEnabled(false);
        this.mActionBar.setDisplayShowTitleEnabled(true);
        this.mActionBar.setTitle(R.string.alarm_ringtone_picker);
        this.mActionBar.setExpandState(0);
        this.mActionBar.setResizable(false);
        ImageView floatPageBackIcon = UiUtil.getFloatPageBackIcon(this);
        floatPageBackIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m85x952361f0(view);
            }
        });
        floatPageBackIcon.setContentDescription(getResources().getString(R.string.go_back));
    }

    /* JADX INFO: renamed from: lambda$initActionBar$0$com-android-deskclock-settings-AlarmRingtonePickerActivity, reason: not valid java name */
    /* synthetic */ void m85x952361f0(View view) {
        onBackPressed();
    }

    private void setOtherRingtone() {
        if (this.mIsSetMode && this.mIsPermissionGranted) {
            String string = this.mSharedPreferences.getString(KEY_LAST_OTHER_RINGTONE, VALUE_NO_RECORD);
            Log.d(TAG, "setOtherRingtone: " + string);
            if (VALUE_NO_RECORD.equals(string)) {
                if (this.mSelectedViewId == 5) {
                    AlarmThreadPool.poolExecute(new SaveRtToSp(AlarmRingtoneUtil.getDefaultAlarmRingtone()));
                    return;
                }
                return;
            }
            if (this.mSelectedViewId == 5) {
                Uri defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
                String alarmRingtoneTitle = AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), defaultAlarmRingtone);
                String string2 = this.mSharedPreferences.getString(KEY_LAST_OTHER_RINGTONE, VALUE_NO_RECORD);
                if (alarmRingtoneTitle.equals(AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), "".equals(string2) ? null : Uri.parse(string2)))) {
                    return;
                }
                Log.d(TAG, "reset ringtone in SP");
                AlarmThreadPool.poolExecute(new SaveRtToSp(defaultAlarmRingtone));
                return;
            }
            this.mAlarmRingtoneAdapter.setOtherItemText(AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), "".equals(string) ? null : Uri.parse(string)));
        }
    }

    static class SaveRtToSp implements Runnable {
        Uri ringtone;

        public SaveRtToSp(Uri uri) {
            this.ringtone = uri;
        }

        @Override // java.lang.Runnable
        public void run() throws Throwable {
            AlarmRingtonePickerActivity.saveRingtoneToSp(this.ringtone);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void saveRingtoneToSp(Uri uri) throws Throwable {
        Log.d(TAG, "saveRingtoneToSp : " + uri);
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppContext());
        if (uri == null) {
            defaultSharedPreferences.edit().putString(KEY_LAST_OTHER_RINGTONE, "").apply();
            return;
        }
        if (!uri.toString().startsWith(RingtoneHelper.NEW_THEME_RINGTONE_URI)) {
            defaultSharedPreferences.edit().putString(KEY_LAST_OTHER_RINGTONE, uri.toString()).apply();
            return;
        }
        try {
            ThemeProviderHelper.GrantThemeResult grantThemeResultRequestGrantThemeFiles = ThemeProviderHelper.requestGrantThemeFiles(DeskClockApp.getAppDEContext(), ".ringtone/" + new File(new URI(uri.toString())).getName(), null);
            Log.d(TAG, "result: " + grantThemeResultRequestGrantThemeFiles);
            if (grantThemeResultRequestGrantThemeFiles != null && grantThemeResultRequestGrantThemeFiles.uri != null) {
                defaultSharedPreferences.edit().putString(KEY_LAST_OTHER_RINGTONE, grantThemeResultRequestGrantThemeFiles.uri.toString()).apply();
            } else if (grantThemeResultRequestGrantThemeFiles != null && grantThemeResultRequestGrantThemeFiles.resultCode == 3) {
                Log.e(TAG, "resultCode error");
            }
        } catch (Exception e) {
            Log.e(TAG, "saveRingtoneToSp error, " + e);
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.mConnection != null) {
            bindService(new Intent(DeskClockApp.getAppDEContext(), (Class<?>) RingtonePlayService.class), this.mConnection, 1);
        }
        this.mShouldPlay = true;
    }

    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    public void finish() {
        if (!this.mIsSetMode) {
            Intent intent = new Intent();
            intent.putExtra("android.intent.extra.ringtone.PICKED_URI", this.mAlert);
            setResult(-1, intent);
            Log.d(TAG, "finish setResult: " + this.mAlert);
        }
        super.finish();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        finish();
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        this.mAlarmRingtoneAdapter.setPlayView(false);
        this.mAlarmRingtoneAdapter.notifyDataSetChanged();
        RingtonePlayServiceConnection ringtonePlayServiceConnection = this.mConnection;
        if (ringtonePlayServiceConnection != null) {
            unbindService(ringtonePlayServiceConnection);
            this.mRingtonePlayService = null;
        }
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
    }

    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        ResourceLoadServiceConnection resourceLoadServiceConnection = this.mResourceLoadConnection;
        if (resourceLoadServiceConnection != null) {
            unbindService(resourceLoadServiceConnection);
            this.mResourceLoadService = null;
        }
        if (this.mIsSetMode && this.mIsPermissionGranted) {
            RingtoneHelper.handleRingtonePickerAlert(this);
        }
        this.toThemeRingtoneLauncher = null;
        DialogUtil.dismissDialogFragment(this.mUserNoticeDialog);
        DialogUtil.dismissDialogFragment(this.mWeatherRingtoneIntroduceDialog);
        DialogUtil.dismissDialogFragment(this.mWeatherRingtoneDownloadDialog);
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 1) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            Log.d("AlarmRingtonePickerActivity onRequestPermissionsResult not allow");
            this.mAlert = this.mOldAlert;
            showDefaultRingtone();
            return;
        }
        Log.d("AlarmRingtonePickerActivity onRequestPermissionsResult allow");
        if (Build.VERSION.SDK_INT >= 29 && this.mIsXiaoAiOrTimerNeedPermission) {
            this.mAlert = RingtoneUriCompat.saveMediaStore(this, this.mNeedPermissionUri);
            this.mIsXiaoAiOrTimerNeedPermission = false;
        }
        if (this.mIsWeatherNeedPermission) {
            setWeatherRingtoneSelected();
            this.mIsWeatherNeedPermission = false;
        }
        if (this.mIsWeekNeedPermission) {
            setWeekRingtoneSelected();
            this.mIsWeekNeedPermission = false;
        }
        showDefaultRingtone();
    }

    private void initActivityResultLauncher() {
        this.toThemeRingtoneLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity$$ExternalSyntheticLambda0
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) throws Throwable {
                this.f$0.m86xb37f56a6((ActivityResult) obj);
            }
        });
        this.toCtaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity$$ExternalSyntheticLambda1
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m87xb9832205((ActivityResult) obj);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initActivityResultLauncher$1$com-android-deskclock-settings-AlarmRingtonePickerActivity, reason: not valid java name */
    /* synthetic */ void m86xb37f56a6(ActivityResult activityResult) throws Throwable {
        int resultCode = activityResult.getResultCode();
        Intent data = activityResult.getData();
        Log.i(TAG, "toThemeRingtoneLauncher resultCode: " + resultCode);
        if ((resultCode == -1 || resultCode == 112) && data != null) {
            if (!this.mIsSetMode) {
                Uri uri = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
                Log.d(TAG, "onActivityResult, newAlert: " + uri);
                if (Util.isRingtoneInternal(uri) || PermissionUtil.shouldAskReadPermission(this)) {
                    this.mOldAlert = this.mAlert;
                    Uri uri2 = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
                    this.mAlert = uri2;
                    AlarmRingtoneUtil.takePersistableUriPermission(data, uri2, this);
                    showDefaultRingtone();
                } else if (PermissionUtil.shouldShowCtaPermission(this)) {
                    showCtaPermissionDialog();
                    this.mOldAlert = this.mAlert;
                    Uri uri3 = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
                    this.mAlert = uri3;
                    AlarmRingtoneUtil.takePersistableUriPermission(data, uri3, this);
                    showDefaultRingtone();
                }
                Intent intent = new Intent();
                intent.putExtra("android.intent.extra.ringtone.PICKED_URI", this.mAlert);
                setResult(-1, intent);
                Log.d(TAG, "setResult: " + this.mAlert);
            } else if (!this.mThemeSupportChangeAlertDirectly) {
                Uri uri4 = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
                this.mAlert = uri4;
                AlarmRingtoneUtil.takePersistableUriPermission(data, uri4, this);
                AlarmRingtoneUtil.setDefaultAlarmRingtone(this.mAlert);
                showDefaultRingtone();
            } else {
                Uri uriSaveMediaStore = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
                if (uriSaveMediaStore == null) {
                    Log.d(TAG, "initActivityResultLauncher: " + uriSaveMediaStore);
                    uriSaveMediaStore = AlarmRingtoneUtil.getDefaultAlarmRingtone();
                }
                Log.d(TAG, "onActivityResult, defaultAlert: " + uriSaveMediaStore);
                if (this.mIsPermissionGranted && this.mIsSetMode && uriSaveMediaStore == null) {
                    this.mSharedPreferences.edit().putString(KEY_LAST_OTHER_RINGTONE, "").apply();
                } else if (uriSaveMediaStore != null && (uriSaveMediaStore.equals(DigitalTimerRingtoneHelper.getRingtoneUri()) || uriSaveMediaStore.equals(XiaoAiRingtoneHelper.getRingtoneUri()))) {
                    this.mOldAlert = this.mAlert;
                    this.mIsXiaoAiOrTimerNeedPermission = true;
                    this.mNeedPermissionUri = uriSaveMediaStore;
                    if (Build.VERSION.SDK_INT >= 29) {
                        uriSaveMediaStore = RingtoneUriCompat.saveMediaStore(this, uriSaveMediaStore);
                        Log.d(TAG, "onActivityResult, saveMediaStore: " + uriSaveMediaStore);
                    }
                    this.mAlert = uriSaveMediaStore;
                }
                if (uriSaveMediaStore != null && this.mIsPermissionGranted && this.mIsSetMode && !uriSaveMediaStore.equals(RingtoneConstants.RINGTONE_URI_WEATHER) && !uriSaveMediaStore.equals(RingtoneConstants.RINGTONE_URI_WEEK) && !uriSaveMediaStore.equals(RingtoneConstants.RINGTONE_URI_DEW) && !uriSaveMediaStore.equals(RingtoneConstants.RINGTONE_URI_FIREFLY) && !uriSaveMediaStore.equals(RingtoneConstants.RINGTONE_URI_DREAM)) {
                    if (RingtoneUriCompat.atLeastU()) {
                        uriSaveMediaStore = AlarmRingtoneUtil.getDefaultAlarmRingtone();
                    }
                    if (uriSaveMediaStore != null) {
                        this.mSharedPreferences.edit().putString(KEY_LAST_OTHER_RINGTONE, RingtoneHelper.transToFileProviderUri(uriSaveMediaStore)).apply();
                    }
                }
                getDefaultRingtone();
                showDefaultRingtone();
            }
            if (resultCode == 112) {
                try {
                    Long lValueOf = Long.valueOf(Settings.Global.getLong(this.mActivity.getContentResolver(), AlarmHelper.NEXT_ALARM_LONG_TIME));
                    Log.d(TAG, "initActivityResultLauncher: " + lValueOf);
                    if (lValueOf.longValue() <= 0 || lValueOf.longValue() - System.currentTimeMillis() >= AlarmHelper.ARRIVING_ALARM_DURATION) {
                        return;
                    }
                    XiaoAiRingtoneHelper.addXiaoAiRingtoneIds(this, Settings.Global.getInt(this.mActivity.getContentResolver(), AlarmHelper.NEXT_ALARM_LONG_ID));
                    return;
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }
            XiaoAiRingtoneHelper.clearXiaoAiRingtoneIds(this);
        }
    }

    /* JADX INFO: renamed from: lambda$initActivityResultLauncher$2$com-android-deskclock-settings-AlarmRingtonePickerActivity, reason: not valid java name */
    /* synthetic */ void m87xb9832205(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        Log.i(TAG, "toCtaLauncher resultCode: " + resultCode);
        if (resultCode == 1) {
            UserNoticeUtil.setAcceptNetPermission(true);
            StatHelper.init(DeskClockApp.getAppContext());
            OneTrackStatHelper.init(DeskClockApp.getAppContext());
            if (PermissionUtil.shouldNotAskPermission(this)) {
                this.mAlert = this.mOldAlert;
                showDefaultRingtone();
                return;
            }
            return;
        }
        this.mAlert = this.mOldAlert;
        showDefaultRingtone();
    }

    private void showCtaPermissionDialog() {
        SystemPermissionUtil.showPermissionDeclare(this, this.toCtaLauncher);
    }

    private void getDefaultRingtone() {
        Uri defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
        this.mAlert = defaultAlarmRingtone;
        if (!WYStarRingtoneHelper.isWYStarAlert(defaultAlarmRingtone) || WYStarRingtoneHelper.isSupport()) {
            return;
        }
        Uri weatherRingtoneUri = WeatherRingtoneHelper.getWeatherRingtoneUri();
        this.mAlert = weatherRingtoneUri;
        AlarmRingtoneUtil.setDefaultAlarmRingtone(weatherRingtoneUri);
    }

    private void playRingtone(int i, String str) {
        if (this.mSelectedViewId == i) {
            RingtonePlayService ringtonePlayService = this.mRingtonePlayService;
            if (ringtonePlayService != null) {
                if (this.mShouldPlay) {
                    ringtonePlayService.playRingtone(str);
                    this.mAlarmRingtoneAdapter.setPlayView(true);
                    this.mShouldPlay = false;
                    return;
                } else {
                    this.mShouldPlay = true;
                    this.mAlarmRingtoneAdapter.setPlayView(false);
                    this.mRingtonePlayService.stopRingtone();
                    return;
                }
            }
            return;
        }
        boolean z = this.mIsSetMode;
        if (!z || (z && !this.mIsPermissionGranted)) {
            this.mAlarmRingtoneAdapter.setOtherItemText("");
        }
        RingtonePlayService ringtonePlayService2 = this.mRingtonePlayService;
        if (ringtonePlayService2 != null) {
            ringtonePlayService2.playRingtone(str);
            this.mAlarmRingtoneAdapter.setPlayView(true);
            this.mShouldPlay = false;
        }
        this.mSelectedViewId = i;
    }

    private void showDefaultRingtone() {
        Uri defaultAlarmRingtone = this.mAlert;
        Log.d(TAG, "showDefaultRingtone mAlert: " + this.mAlert);
        if (RingtoneManager.getDefaultUri(4).equals(defaultAlarmRingtone)) {
            defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
        }
        if (WeatherRingtoneHelper.getWeatherRingtoneUri().equals(defaultAlarmRingtone) || WeatherRingtoneHelper.isWeatherRingtone(defaultAlarmRingtone)) {
            this.mAlarmRingtoneAdapter.setRingtoneItemChecked(0);
            playRingtone(0, RingtoneConstants.RINGTONE_WEATHER);
            return;
        }
        if (WeekRingtoneHelper.getWeekRingtoneUri().equals(defaultAlarmRingtone) || WeekRingtoneHelper.isWeekRingtone(defaultAlarmRingtone)) {
            this.mAlarmRingtoneAdapter.setRingtoneItemChecked(1);
            playRingtone(1, RingtoneConstants.RINGTONE_WEEK);
            return;
        }
        if (RingtoneHelper.getDewRingtoneUri().equals(defaultAlarmRingtone) || (RingtoneUriCompat.atLeastU() && RingtoneConstants.RINGTONE_URI_DEW.equals(defaultAlarmRingtone))) {
            this.mAlarmRingtoneAdapter.setRingtoneItemChecked(2);
            playRingtone(2, RingtoneConstants.RINGTONE_DEW);
            return;
        }
        if (RingtoneHelper.getFireflyRingtoneUri().equals(defaultAlarmRingtone) || AlarmRingtoneUtil.isFireFliesRingtone(defaultAlarmRingtone) || (RingtoneUriCompat.atLeastU() && RingtoneConstants.RINGTONE_URI_FIREFLY.equals(defaultAlarmRingtone))) {
            this.mAlarmRingtoneAdapter.setRingtoneItemChecked(3);
            playRingtone(3, RingtoneConstants.RINGTONE_FIREFLY);
            return;
        }
        if (RingtoneHelper.getDreamRingtoneUri().equals(defaultAlarmRingtone) || (RingtoneUriCompat.atLeastU() && RingtoneConstants.RINGTONE_URI_DREAM.equals(defaultAlarmRingtone))) {
            this.mAlarmRingtoneAdapter.setRingtoneItemChecked(4);
            playRingtone(4, RingtoneConstants.RINGTONE_DREAM);
            return;
        }
        this.mAlarmRingtoneAdapter.setRingtoneItemChecked(5);
        if (defaultAlarmRingtone != null) {
            playRingtone(5, defaultAlarmRingtone.toString());
        }
        if (defaultAlarmRingtone != null && "".equals(defaultAlarmRingtone.toString())) {
            defaultAlarmRingtone = null;
        }
        this.mAlarmRingtoneAdapter.setOtherItemText(AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), defaultAlarmRingtone));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openRingtonePicker() {
        if (this.mAlert == null && this.mIsSetMode) {
            this.mAlert = AlarmRingtoneUtil.getDefaultAlarmRingtone();
        }
        final boolean z = this.mIsSetMode && this.mThemeSupportChangeAlertDirectly;
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.5
            @Override // java.lang.Runnable
            public void run() {
                AlarmRingtonePickerActivity.this.mAlert = AlarmRingtoneUtil.getXiaoAiOrDigitalTimerAlertUri(DeskClockApp.getAppDEContext(), AlarmRingtonePickerActivity.this.mAlert);
                final Intent intentCreateRingtonePickerIntent = (AlarmRingtonePickerActivity.this.mIsSetMode || !AlarmRingtonePickerActivity.this.mIsSupportXiaoAiRingtone) ? MiuiTheme.createRingtonePickerIntent(AlarmRingtonePickerActivity.this.mAlert, z, "AlarmRingtonePickerActivity") : MiuiTheme.createRingtonePickerIntent(AlarmRingtonePickerActivity.this.mAlert, MiuiTheme.createXiaoAiRingtoneExtra(), z, "AlarmRingtonePickerActivity");
                AlarmRingtonePickerActivity.this.runOnUiThread(new Runnable() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.5.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (AlarmRingtonePickerActivity.this.toThemeRingtoneLauncher != null) {
                            AlarmRingtonePickerActivity.this.toThemeRingtoneLauncher.launch(intentCreateRingtonePickerIntent);
                            Log.i(AlarmRingtonePickerActivity.TAG, "jump to theme ringtone");
                        }
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWeatherRingtoneSelected() {
        if (Util.isInternational() && RingtoneUriCompat.atLeastU() && checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != 0) {
            Log.i(TAG, "setWeatherRingtoneSelected not has permission");
            PermissionUtil.shouldNotAskPermission(this);
            this.mIsWeatherNeedPermission = true;
        }
        if (!PrefUtil.isWeatherRingtoneIntroduce()) {
            showWeatherRingtoneIntroduceDialog();
            PrefUtil.setWeatherRingtoneIntroduce(true);
        }
        this.mOldAlert = this.mAlert;
        Uri weatherRingtoneUri = WeatherRingtoneHelper.getWeatherRingtoneUri();
        this.mAlert = weatherRingtoneUri;
        if (this.mIsSetMode) {
            AlarmRingtoneUtil.setDefaultAlarmRingtone(weatherRingtoneUri);
        }
        showDefaultRingtone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWeekRingtoneSelected() {
        if (Util.isInternational() && RingtoneUriCompat.atLeastU() && checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != 0) {
            Log.i(TAG, "setWeekRingtoneSelected not has permission");
            PermissionUtil.shouldNotAskPermission(this);
            this.mIsWeekNeedPermission = true;
        }
        if (!PrefUtil.isWeekRingtoneIntroduce()) {
            showWeekRingtoneIntroduceDialog();
            PrefUtil.setWeekRingtoneIntroduce(true);
        }
        this.mOldAlert = this.mAlert;
        Uri weekRingtoneUri = WeekRingtoneHelper.getWeekRingtoneUri();
        this.mAlert = weekRingtoneUri;
        if (this.mIsSetMode) {
            AlarmRingtoneUtil.setDefaultAlarmRingtone(weekRingtoneUri);
        }
        showDefaultRingtone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDewRingtoneSelected() {
        Uri dewRingtoneUri = RingtoneHelper.getDewRingtoneUri();
        this.mAlert = dewRingtoneUri;
        if (this.mIsSetMode) {
            AlarmRingtoneUtil.setDefaultAlarmRingtone(dewRingtoneUri);
        }
        showDefaultRingtone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFireflyRingtoneSelected() {
        Uri fireflyRingtoneUri = RingtoneHelper.getFireflyRingtoneUri();
        this.mAlert = fireflyRingtoneUri;
        if (this.mIsSetMode) {
            AlarmRingtoneUtil.setDefaultAlarmRingtone(fireflyRingtoneUri);
        }
        showDefaultRingtone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDreamRingtoneSelected() {
        Uri dreamRingtoneUri = RingtoneHelper.getDreamRingtoneUri();
        this.mAlert = dreamRingtoneUri;
        if (this.mIsSetMode) {
            AlarmRingtoneUtil.setDefaultAlarmRingtone(dreamRingtoneUri);
        }
        showDefaultRingtone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLastOtherRingtoneSelected() {
        String string = this.mSharedPreferences.getString(KEY_LAST_OTHER_RINGTONE, VALUE_NO_RECORD);
        Uri uri = "".equals(string) ? null : Uri.parse(string);
        this.mAlert = uri;
        if (this.mIsSetMode && this.mSelectedViewId != 5) {
            AlarmRingtoneUtil.setDefaultAlarmRingtone(uri);
        }
        showDefaultRingtone();
    }

    private void showWeatherRingtoneIntroduceDialog() {
        String string;
        if (Util.isInternational()) {
            string = getString(R.string.module_weather_ringtone_introduce_global);
        } else {
            string = getString(R.string.module_weather_ringtone_introduce);
        }
        this.mWeatherRingtoneIntroduceDialog = DialogUtil.showAlertDialog(string, R.string.module_update_success_control, null, getSupportFragmentManager());
    }

    private void showWeekRingtoneIntroduceDialog() {
        this.mWeekRingtoneIntroduceDialog = DialogUtil.showAlertDialog(getString(R.string.module_week_ringtone_introduce), R.string.module_update_success_control, null, getSupportFragmentManager());
    }

    private boolean checkBeforeDownload(boolean z) {
        int i;
        ResourceLoadService resourceLoadService = this.mResourceLoadService;
        if (resourceLoadService != null) {
            if (resourceLoadService.isLoading()) {
                ExternalResourceUtils.toastResourceUnzipping();
                return false;
            }
            if (this.mResourceLoadService.isDownloading()) {
                ExternalResourceUtils.toastResourceDownloading();
                return false;
            }
        }
        if (!NetworkUtil.isNetworkConnected()) {
            Toast.makeText(DeskClockApp.getAppDEContext(), R.string.module_download_no_network, 0).show();
            return false;
        }
        if (UserNoticeUtil.isNetPermissionAgreed()) {
            return true;
        }
        if (z) {
            i = NetworkUtil.isWifiConnected() ? R.string.module_week_ringtone_download_wifi_privacy : R.string.module_week_ringtone_download_data_privacy_new;
        } else {
            i = NetworkUtil.isWifiConnected() ? R.string.module_weather_ringtone_download_wifi_privacy : R.string.module_weather_ringtone_download_data_privacy_new;
        }
        this.mUserNoticeDialog = UserNoticeUtil.showUserNoticeDialog(DeskClockApp.getAppDEContext(), i, new UserNoticeUtil.OnNetPermissionListener() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.6
            @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
            public void onReject() {
            }

            @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
            public void onAccept() {
                AlarmRingtonePickerActivity.this.startLoadNetResource();
            }
        }, getSupportFragmentManager());
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showWeatherRingtoneDownloadDialog() {
        String string;
        if (checkBeforeDownload(false)) {
            if (NetworkUtil.isWifiConnected()) {
                string = getString(R.string.module_weather_ringtone_download_wifi);
            } else {
                string = String.format(getResources().getString(R.string.module_weather_ringtone_download_data_new), 30);
            }
            this.mWeatherRingtoneDownloadDialog = DialogUtil.showAlertDialog("", string, R.string.module_dialog_negative, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.7
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }, R.string.module_dialog_positive, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.8
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    AlarmRingtonePickerActivity.this.startLoadNetResource();
                }
            }, getSupportFragmentManager());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showWeekRingtoneDownloadDialog() {
        String string;
        if (checkBeforeDownload(true)) {
            if (NetworkUtil.isWifiConnected()) {
                string = getString(R.string.module_week_ringtone_download_wifi);
            } else {
                string = String.format(getResources().getString(R.string.module_week_ringtone_download_data_new), 30);
            }
            this.mWeekRingtoneDownloadDialog = DialogUtil.showAlertDialog("", string, R.string.module_dialog_negative, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.9
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }, R.string.module_dialog_positive, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.10
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    AlarmRingtonePickerActivity.this.startLoadNetResource();
                }
            }, getSupportFragmentManager());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPlayComplete(String str) {
        this.mHandler.post(new Runnable() { // from class: com.android.deskclock.settings.AlarmRingtonePickerActivity.11
            @Override // java.lang.Runnable
            public void run() {
                AlarmRingtonePickerActivity.this.mAlarmRingtoneAdapter.setPlayView(false);
                AlarmRingtonePickerActivity.this.mAlarmRingtoneAdapter.notifyDataSetChanged();
                AlarmRingtonePickerActivity.this.mShouldPlay = true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLoadingComplete(boolean z) {
        if (z) {
            ExternalResourceUtils.toastDownloadSuccess();
        } else {
            ExternalResourceUtils.toastDownloadFail();
        }
    }

    private class RingtonePlayServiceConnection implements ServiceConnection {
        private RingtonePlayServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AlarmRingtonePickerActivity.this.mRingtonePlayService = ((RingtonePlayService.CallbackBinder) iBinder).getService();
            AlarmRingtonePickerActivity.this.mRingtonePlayService.registerCallbackListener(AlarmRingtonePickerActivity.this.mRingtonePlayCallback);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            AlarmRingtonePickerActivity.this.mRingtonePlayService.registerCallbackListener(null);
            AlarmRingtonePickerActivity.this.mRingtonePlayService = null;
        }
    }

    private static class RingtonePlayServiceCallback implements RingtonePlayService.CallbackListener {
        private WeakReference<AlarmRingtonePickerActivity> mReference;

        public RingtonePlayServiceCallback(AlarmRingtonePickerActivity alarmRingtonePickerActivity) {
            this.mReference = new WeakReference<>(alarmRingtonePickerActivity);
        }

        @Override // com.android.deskclock.settings.RingtonePlayService.CallbackListener
        public void onPlayComplete(String str) {
            AlarmRingtonePickerActivity alarmRingtonePickerActivity = this.mReference.get();
            if (alarmRingtonePickerActivity != null) {
                alarmRingtonePickerActivity.onPlayComplete(str);
            }
        }
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
            this.mResourceLoadCallback = new ResourceLoadServiceCallback(this);
            this.mLoadAfterServiceBind = true;
            Intent intent = new Intent();
            intent.setClass(DeskClockApp.getAppDEContext(), ResourceLoadService.class);
            intent.putExtra(ResourceLoadService.EXTRA_TYPE, 1);
            bindService(intent, this.mResourceLoadConnection, 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startLoadNetResource() {
        ResourceLoadService resourceLoadService = this.mResourceLoadService;
        if (resourceLoadService == null) {
            this.mResourceLoadConnection = new ResourceLoadServiceConnection();
            this.mResourceLoadCallback = new ResourceLoadServiceCallback(this);
            this.mDownloadAfterServiceBind = true;
            Intent intent = new Intent();
            intent.setClass(DeskClockApp.getAppDEContext(), ResourceLoadService.class);
            intent.putExtra(ResourceLoadService.EXTRA_TYPE, 3);
            startService(intent);
            bindService(intent, this.mResourceLoadConnection, 1);
            return;
        }
        resourceLoadService.loadNetResource();
    }

    private class ResourceLoadServiceConnection implements ServiceConnection {
        private ResourceLoadServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(ExternalResourceUtils.TAG, "service bind");
            AlarmRingtonePickerActivity.this.mResourceLoadService = ((ResourceLoadService.CallbackBinder) iBinder).getService();
            AlarmRingtonePickerActivity.this.mResourceLoadService.registerCallbackListener(AlarmRingtonePickerActivity.this.mResourceLoadCallback);
            if (AlarmRingtonePickerActivity.this.mLoadAfterServiceBind) {
                AlarmRingtonePickerActivity.this.mResourceLoadService.loadRomResource(PrefUtil.getMiuiResourceVersion());
                AlarmRingtonePickerActivity.this.mLoadAfterServiceBind = false;
            } else if (AlarmRingtonePickerActivity.this.mDownloadAfterServiceBind) {
                AlarmRingtonePickerActivity.this.mResourceLoadService.loadNetResource();
                AlarmRingtonePickerActivity.this.mDownloadAfterServiceBind = false;
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            AlarmRingtonePickerActivity.this.mResourceLoadService.unregisterCallbackListener(AlarmRingtonePickerActivity.this.mResourceLoadCallback);
            AlarmRingtonePickerActivity.this.mResourceLoadService = null;
        }
    }

    private static class ResourceLoadServiceCallback implements ResourceLoadService.CallbackListener {
        private WeakReference<AlarmRingtonePickerActivity> mReference;

        public ResourceLoadServiceCallback(AlarmRingtonePickerActivity alarmRingtonePickerActivity) {
            this.mReference = new WeakReference<>(alarmRingtonePickerActivity);
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onRomLoadSuccess(int i) {
            Log.i(ExternalResourceUtils.TAG, "onRomLoadSuccess in AlarmRingtonePickerActivity");
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onRomLoadFailed() {
            Log.i(ExternalResourceUtils.TAG, "onRomLoadFailed in AlarmRingtonePickerActivity");
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onNetLoadSuccess() {
            Log.i(ExternalResourceUtils.TAG, "onNetLoadSuccess in AlarmRingtonePickerActivity");
            WeakReference<AlarmRingtonePickerActivity> weakReference = this.mReference;
            AlarmRingtonePickerActivity alarmRingtonePickerActivity = weakReference != null ? weakReference.get() : null;
            if (alarmRingtonePickerActivity == null) {
                return;
            }
            alarmRingtonePickerActivity.onLoadingComplete(true);
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onNetLoadFailed() {
            Log.i(ExternalResourceUtils.TAG, "onNetLoadFailed in AlarmRingtonePickerActivity");
            WeakReference<AlarmRingtonePickerActivity> weakReference = this.mReference;
            AlarmRingtonePickerActivity alarmRingtonePickerActivity = weakReference != null ? weakReference.get() : null;
            if (alarmRingtonePickerActivity == null) {
                return;
            }
            alarmRingtonePickerActivity.onLoadingComplete(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doVibrate(View view) {
        if (this.mSupportLinearMotorVibrate) {
            try {
                HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_BUTTON_LARGE, HapticFeedbackConstants.MIUI_MESH_NORMAL);
            } catch (Exception e) {
                Log.e(TAG, "doVibrate error: " + e.getMessage());
            }
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        super.onResponsiveLayout(configuration, screenSpec, z);
        setRingtonePickerViewLayout();
        if (getIntent() == null || getIntent().getBooleanExtra(IS_FROM_ALARM, false) || !PadAdapterUtil.IS_PAD) {
            return;
        }
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.ringtone_picker_background_color));
        initActionBar();
    }
}
