package com.android.deskclock;

import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.android.deskclock.addition.resource.ExternalResourceUtils;
import com.android.deskclock.addition.resource.MiuiResource;
import com.android.deskclock.addition.resource.ResourceLoadService;
import com.android.deskclock.addition.ringtone.RingtoneUriCompat;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.alarm.AlarmClockFragment;
import com.android.deskclock.alarm.AlarmEditDialogView;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.lifepost.LifePostSettingActivity;
import com.android.deskclock.interfaces.PermissionRequestCallback;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.NetworkUtil;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.fab.FabControllerNew;
import com.android.deskclock.util.permission.KoreaPermissionUtil;
import com.android.deskclock.util.permission.PermissionUtil;
import com.android.deskclock.util.permission.SystemPermissionUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.SimpleDialogFragment;
import com.android.deskclock.view.tab.TabViewModel;
import java.lang.ref.WeakReference;
import java.util.Set;
import miuix.android.content.MiuiIntent;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.Fragment;
import miuix.appcompat.app.LayoutUiModeHelper;
import miuix.navigator.Navigator;
import miuix.navigator.NavigatorStrategy;
import miuix.navigator.app.NavigatorActivity;
import miuix.navigator.navigatorinfo.NavigatorInfo;
import miuix.navigator.navigatorinfo.NavigatorInfoProvider;
import miuix.navigator.navigatorinfo.UpdateFragmentNavInfo;

/* JADX INFO: loaded from: classes.dex */
public class DeskClockTabActivity extends NavigatorActivity {
    public static final String ACTION_ALARM_CHANGED = "miui.intent.action.ALARM_CHANGED";
    private static final String ACTION_REQUEST_NET_PERMISSION = "com.android.deskclock.REQUEST_NET_PERMISSION";
    private static final int HOME_PAGE_MASK_SHOW_MAX_MILLIS = 1500;
    public static final String KEY_GUARD_SHORTCUT_ALARM_PAGE = "com.android.deskclock.ACTION_ALARM";
    public static final String KEY_GUARD_SHORTCUT_STOPWATCH_PAGE = "com.android.deskclock.ACTION_STOPWATCH";
    public static final String KEY_GUARD_SHORTCUT_TIMER_PAGE = "com.android.deskclock.ACTION_TIMER";
    private static final String TAG = "DC:DeskClockTabActivity";
    private AlarmEditDialogView mAlarmEditDialogView;
    private long mCtaTriggerTime;
    private Handler mHandler;
    private View mHomePageMaskView;
    private boolean mIsPause;
    private PermissionRequestCallback mPermissionRequestCallback;
    private ResourceLoadServiceCallback mResourceLoadCallback;
    private ResourceLoadServiceConnection mResourceLoadConnection;
    private ResourceLoadService mResourceLoadService;
    private ActivityResultLauncher<Intent> toCtaLauncher;
    private ActivityResultLauncher<Intent> toKoreaLauncher;
    public static final String mAlarmPageName = DeskClockApp.getAppDEContext().getString(R.string.alarm_list_title);
    public static final String mClockPageName = DeskClockApp.getAppDEContext().getString(R.string.clock_tab_name);
    public static final String mStopwatchPageName = DeskClockApp.getAppDEContext().getString(R.string.stopwatch_title);
    public static final String mTimerPageName = DeskClockApp.getAppDEContext().getString(R.string.timer_title);
    public static boolean NOTIFICATION_PERMISSION_GRANTED = false;
    public static String mKeyguardShortcutIndex = null;
    private boolean mIsPermissionAccepted = false;
    private boolean mShowNetResourceDialog = false;
    private boolean mShowHolidayAlarmDialog = false;
    private SimpleDialogFragment mUserNoticeDialog = null;
    private SimpleDialogFragment mDownloadResourceDialog = null;
    private SimpleDialogFragment mLegalWorkdayDialog = null;
    private AlertDialog mResetDaysOfWeekDialog = null;
    private boolean mLoadAfterServiceBind = false;
    private boolean mDownloadAfterServiceBind = false;
    private boolean hasInited = false;
    private final Runnable mDismissHomePageMaskRunnable = new Runnable() { // from class: com.android.deskclock.DeskClockTabActivity.11
        @Override // java.lang.Runnable
        public void run() {
            DeskClockTabActivity.this.dismissHomePageMask(false, false);
        }
    };

    @Override // miuix.navigator.app.NavigatorBuilder
    public int getNavigationOptionMenu() {
        return 0;
    }

    @Override // miuix.navigator.app.NavigatorBuilder
    public void onCreateOtherNavigation(Navigator navigator, Bundle bundle) {
    }

    public void setNavigationForActionMode(boolean z) {
    }

    @Override // miuix.navigator.app.NavigatorActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) throws Throwable {
        if (Util.isTinyScreen(this)) {
            setTheme(R.style.MainActivityThemeTiny);
        } else {
            setTheme(R.style.MainActivityTheme);
        }
        super.onCreate(bundle);
        LayoutUiModeHelper.autoSetLayoutUiMode(this);
        getWindow().addFlags(134217728);
        setContentView(R.layout.activity_desk_clock_tab_navigator);
        Log.d(TAG, "DeskClockTabActivity onCreate");
        initKeyguardShortcutRequest(getIntent());
        handleKeyguardShortcutRequest();
        setFlipTinyStatusBarColor();
        initActivityResultLauncher();
        if (!KoreaPermissionUtil.isKoreaRegion() || KoreaPermissionUtil.isKoreaAuthDisplayed()) {
            if (Util.isInternational()) {
                if (KoreaPermissionUtil.isKoreaRegion()) {
                    showCtaDialog();
                }
            } else if (bundle == null) {
                showCtaDialog();
            }
        }
        setE5ShowWhenLocked(getIntent());
        getWindow().getDecorView().post(new Runnable() { // from class: com.android.deskclock.DeskClockTabActivity.1
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                DeskClockTabActivity.this.showHolidayAlarmDialog();
                DeskClockTabActivity.this.loadExternalResource();
                DeskClockTabActivity.this.hasInited = true;
            }
        });
        resolveAiAction(getIntent());
        requestNotificationPermission();
        Util.cutOut(this);
        this.mHandler = new Handler();
    }

    public void handleKeyguardShortcutRequest() {
        if (TextUtils.isEmpty(mKeyguardShortcutIndex)) {
            return;
        }
        Log.d(TAG, "handleKeyguardShortcutRequest: " + mKeyguardShortcutIndex);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService("keyguard");
        String str = mKeyguardShortcutIndex;
        str.hashCode();
        switch (str) {
            case "com.android.deskclock.ACTION_STOPWATCH":
                checkIsNeedJumpWhenLocked(false, keyguardManager);
                TabNavigatorContentFragment.mCurrTab = TabViewModel.TAB_STOPWATCH;
                break;
            case "com.android.deskclock.ACTION_ALARM":
                checkIsNeedJumpWhenLocked(true, keyguardManager);
                TabNavigatorContentFragment.mCurrTab = TabViewModel.TAB_ALARM;
                break;
            case "com.android.deskclock.ACTION_TIMER":
                checkIsNeedJumpWhenLocked(false, keyguardManager);
                TabNavigatorContentFragment.mCurrTab = TabViewModel.TAB_TIMER;
                break;
        }
    }

    public void keyguardShortcutRequest() {
        if (TextUtils.isEmpty(mKeyguardShortcutIndex)) {
            return;
        }
        if ((mKeyguardShortcutIndex.equals(KEY_GUARD_SHORTCUT_STOPWATCH_PAGE) || mKeyguardShortcutIndex.equals(KEY_GUARD_SHORTCUT_TIMER_PAGE)) && isHaveLockShowPermission() && Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true);
        }
    }

    private boolean isHaveLockShowPermission() {
        AppOpsManager appOpsManager = (AppOpsManager) getSystemService("appops");
        try {
            return ((Integer) appOpsManager.getClass().getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class).invoke(appOpsManager, Integer.valueOf(R2.id.top_line), Integer.valueOf(Process.myUid()), getPackageName())).intValue() == 0;
        } catch (Exception unused) {
            Log.e(TAG, "not support");
            return false;
        }
    }

    private void checkIsNeedJumpWhenLocked(boolean z, KeyguardManager keyguardManager) {
        if (keyguardManager.isKeyguardLocked()) {
            if (isHaveLockShowPermission()) {
                if ((!Util.isInternational() && !UserNoticeUtil.isNetPermissionAgreed()) || z || !PermissionUtil.checkPermission(this, "android.permission.POST_NOTIFICATIONS")) {
                    keyguardManager.requestDismissKeyguard(this, null);
                    return;
                } else {
                    keyguardShortcutRequest();
                    return;
                }
            }
            keyguardManager.requestDismissKeyguard(this, null);
        }
    }

    public static void initKeyguardShortcutRequest(Intent intent) {
        if (intent != null) {
            mKeyguardShortcutIndex = intent.getAction();
        }
    }

    private void setFlipTinyStatusBarColor() {
        if (Util.isTinyScreen(this)) {
            Window window = getWindow();
            ViewGroup viewGroup = (ViewGroup) window.getDecorView();
            View view = new View(window.getContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, Util.getStatusBarHeight(this));
            layoutParams.gravity = 48;
            view.setLayoutParams(layoutParams);
            view.setBackgroundColor(getResources().getColor(R.color.main_bg));
            viewGroup.addView(view);
        }
    }

    @Override // miuix.navigator.app.NavigatorActivity, miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        LayoutUiModeHelper.autoSetLayoutUiMode(this);
        getWindow().addFlags(134217728);
    }

    @Override // miuix.navigator.app.NavigatorActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() throws Throwable {
        Log.d(TAG, "onResume");
        super.onResume();
        LayoutUiModeHelper.autoSetLayoutUiMode(this);
        getWindow().addFlags(134217728);
        this.mIsPause = false;
        StatHelper.recordPageStart(this);
        if (Util.isInternational()) {
            KoreaPermissionUtil.showAuthorization(this, this.toKoreaLauncher);
        }
        if (this.hasInited) {
            showNetResourceDialog();
            showHolidayAlarmDialog();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        this.mIsPause = true;
        StatHelper.recordPageEnd(this);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService("keyguard");
        if ((Util.isFreeFormScreen(getResources().getConfiguration()) && keyguardManager.isKeyguardLocked()) || !Util.isScreenOn()) {
            if (!TextUtils.isEmpty(mKeyguardShortcutIndex)) {
                if (mKeyguardShortcutIndex.equals(KEY_GUARD_SHORTCUT_STOPWATCH_PAGE) || mKeyguardShortcutIndex.equals(KEY_GUARD_SHORTCUT_TIMER_PAGE) || Util.isFreeFormScreen(getResources().getConfiguration())) {
                    finish();
                    return;
                }
                return;
            }
            if (Util.isFreeFormScreen(getResources().getConfiguration())) {
                finish();
                return;
            }
            return;
        }
        if (keyguardManager.isKeyguardLocked() || Util.isScreenOn() || Build.VERSION.SDK_INT < 27) {
            return;
        }
        setShowWhenLocked(false);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    private void requestNotificationPermission() {
        this.mIsPermissionAccepted = UserNoticeUtil.isNetPermissionAgreed();
        boolean z = Util.isInternational() && !KoreaPermissionUtil.isKoreaRegion();
        if ((this.mIsPermissionAccepted || z) && !PermissionUtil.isPermissionGranted(this, "android.permission.POST_NOTIFICATIONS")) {
            checkNotificationPermission();
        } else if ((!UserNoticeUtil.canRemindNetPermission() || z) && !PermissionUtil.isPermissionGranted(this, "android.permission.POST_NOTIFICATIONS")) {
            checkNotificationPermission();
        } else {
            NOTIFICATION_PERMISSION_GRANTED = true;
        }
    }

    @Override // miuix.navigator.app.NavigatorBuilder
    public Bundle getNavigatorInitArgs() {
        Log.d(TAG, "getNavigatorInitArgs");
        NavigatorStrategy navigatorStrategy = new NavigatorStrategy();
        navigatorStrategy.setCompactMode(Navigator.Mode.C).setRegularMode(Navigator.Mode.C, Navigator.Mode.C).setLargeMode(Navigator.Mode.C);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Navigator.KEY_MIUIX_NAVIGATOR_STRATEGY, navigatorStrategy);
        return bundle;
    }

    @Override // miuix.navigator.app.NavigatorBuilder
    public Class<? extends Fragment> getDefaultContentFragment() {
        return TabNavigatorContentFragment.class;
    }

    @Override // miuix.navigator.app.NavigatorBuilder
    public int getBottomTabMenu() {
        Log.d(TAG, "getBottomTabMenu  Util.isTinyScreen(this)：" + Util.isTinyScreen(this));
        return Util.isTinyScreen(this) ? R.menu.bottom_nav_tiny_menu : R.menu.bottom_nav_menu;
    }

    @Override // miuix.navigator.app.NavigatorBuilder
    public NavigatorInfoProvider getBottomTabMenuNavInfoProvider() {
        return new NavigatorInfoProvider() { // from class: com.android.deskclock.DeskClockTabActivity.2
            @Override // miuix.navigator.navigatorinfo.NavigatorInfoProvider
            public NavigatorInfo onCreateNavigatorInfo(int i) {
                Bundle bundle = new Bundle();
                switch (i) {
                    case 1000:
                        bundle.putInt(TabNavigatorContentFragment.ARG_PAGE, 0);
                        break;
                    case 1001:
                        bundle.putInt(TabNavigatorContentFragment.ARG_PAGE, 1);
                        break;
                    case 1002:
                        bundle.putInt(TabNavigatorContentFragment.ARG_PAGE, 2);
                        break;
                    case 1003:
                        bundle.putInt(TabNavigatorContentFragment.ARG_PAGE, 3);
                        break;
                    default:
                        return null;
                }
                return new UpdateFragmentNavInfo(i, DeskClockTabActivity.this.getDefaultContentFragment(), bundle);
            }
        };
    }

    @Override // miuix.navigator.app.NavigatorBuilder
    public void onCreatePrimaryNavigation(Navigator navigator, Bundle bundle) {
        Bundle bundle2 = new Bundle();
        bundle2.putInt(TabNavigatorContentFragment.ARG_PAGE, 0);
        UpdateFragmentNavInfo updateFragmentNavInfo = new UpdateFragmentNavInfo(1000, getDefaultContentFragment(), bundle2);
        newLabel(mAlarmPageName, updateFragmentNavInfo);
        navigator.navigate(updateFragmentNavInfo);
        Bundle bundle3 = new Bundle();
        bundle3.putInt(TabNavigatorContentFragment.ARG_PAGE, 1);
        newLabel(mClockPageName, new UpdateFragmentNavInfo(1001, getDefaultContentFragment(), bundle3));
        Bundle bundle4 = new Bundle();
        bundle4.putInt(TabNavigatorContentFragment.ARG_PAGE, 2);
        newLabel(mStopwatchPageName, new UpdateFragmentNavInfo(1002, getDefaultContentFragment(), bundle4));
        Bundle bundle5 = new Bundle();
        bundle5.putInt(TabNavigatorContentFragment.ARG_PAGE, 3);
        newLabel(mTimerPageName, new UpdateFragmentNavInfo(1003, getDefaultContentFragment(), bundle5));
    }

    private void showCtaDialog() throws Throwable {
        dismissHomePageMask(false, true);
        if (System.currentTimeMillis() - this.mCtaTriggerTime > 500) {
            this.mCtaTriggerTime = System.currentTimeMillis();
            this.mIsPermissionAccepted = UserNoticeUtil.isNetPermissionAgreed();
            Log.d(TAG, "mIsPermissionAccepted : " + this.mIsPermissionAccepted + "   UserNoticeUtil.canRemindNetPermission():" + UserNoticeUtil.canRemindNetPermission());
            if (this.mIsPermissionAccepted) {
                return;
            }
            if (UserNoticeUtil.canRemindNetPermission()) {
                if (Util.isKddiCustomized() && Util.isInternational()) {
                    UserNoticeUtil.setAcceptNetPermission(false);
                    this.mIsPermissionAccepted = false;
                    UserNoticeUtil.setRemindNetPermission(false);
                    return;
                } else if (Util.isInternational() || !SystemPermissionUtil.showPermissionDeclare(this, this.toCtaLauncher)) {
                    this.mUserNoticeDialog = UserNoticeUtil.showUserNoticeDialog(this, Util.isInternational() ? R.string.network_privacy_global : R.string.network_privacy, R.string.net_permission_cancel, R.string.net_permission_ok, new UserNoticeUtil.OnNetPermissionListener() { // from class: com.android.deskclock.DeskClockTabActivity.3
                        @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
                        public void onAccept() {
                            DeskClockTabActivity.this.onNetPermissionAccept();
                            DeskClockTabActivity.this.showNetResourceDialog();
                            DeskClockTabActivity.this.checkNotificationPermission();
                        }

                        @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
                        public void onReject() throws Throwable {
                            DeskClockTabActivity.this.onNetPermissionNotAccept();
                            DeskClockTabActivity.this.mShowHolidayAlarmDialog = true;
                            DeskClockTabActivity.this.showHolidayAlarmDialog();
                            DeskClockTabActivity.this.checkNotificationPermission();
                        }
                    }, getString(R.string.dialog_message_not_remind), UserNoticeUtil.KEY_REMIND_INTERNET_PERMISSION, getSupportFragmentManager());
                    return;
                } else {
                    showHomePageMask();
                    return;
                }
            }
            this.mShowHolidayAlarmDialog = true;
            showHolidayAlarmDialog();
        }
    }

    private void showNetPermissionDialogFromAction() {
        boolean zIsNetPermissionAgreed = UserNoticeUtil.isNetPermissionAgreed();
        this.mIsPermissionAccepted = zIsNetPermissionAgreed;
        if (zIsNetPermissionAgreed) {
            Log.i("net permission has accepted, no need to request");
        } else {
            if (SystemPermissionUtil.showPermissionDeclare(this, 2)) {
                return;
            }
            this.mUserNoticeDialog = UserNoticeUtil.showUserNoticeDialog(this, R.string.network_privacy, new UserNoticeUtil.OnNetPermissionListener() { // from class: com.android.deskclock.DeskClockTabActivity.4
                @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
                public void onAccept() {
                    DeskClockTabActivity.this.onNetPermissionAccept();
                }

                @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
                public void onReject() {
                    DeskClockTabActivity.this.onNetPermissionNotAccept();
                }
            }, getSupportFragmentManager());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNetPermissionAccept() {
        this.mIsPermissionAccepted = true;
        LifePostSettingActivity.updateLifePostSwitchState(this, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNetPermissionNotAccept() {
        LifePostSettingActivity.updateLifePostSwitchState(this, false);
        this.mIsPermissionAccepted = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadExternalResource() {
        int miuiResourceVersion = PrefUtil.getMiuiResourceVersion();
        Log.i(ExternalResourceUtils.TAG, "current resource version: " + miuiResourceVersion);
        if (miuiResourceVersion <= 1) {
            MiuiResource.checkLocalResourceVersion();
            miuiResourceVersion = PrefUtil.getMiuiResourceVersion();
            Log.i(ExternalResourceUtils.TAG, "resource version after check: " + miuiResourceVersion);
        }
        if (miuiResourceVersion == 5) {
            Log.i(ExternalResourceUtils.TAG, "newest module has loaded");
            if (!PrefUtil.isWeekRingtoneRecommendInClock() && TabViewModel.TAB_ALARM.equals(TabNavigatorContentFragment.mCurrTab)) {
                if (WeekRingtoneHelper.isDefaultRingtone(this)) {
                    PrefUtil.setWeekRingtoneRecommendInEdit(true);
                } else if (!PrefUtil.hasRingtoneModifiedToWeek() && WeekRingtoneHelper.isRomSupport()) {
                    UserNoticeUtil.showRecommendDialog(getResources().getString(R.string.week_ringtone_recommend_desc), R.string.not_set_temporarily, R.string.set_now, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.DeskClockTabActivity.5
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AlarmRingtoneUtil.setDefaultAlarmRingtone(WeekRingtoneHelper.getWeekRingtoneUri());
                            PrefUtil.setRingtoneModifiedToWeek(true);
                        }
                    }, getSupportFragmentManager());
                }
                PrefUtil.setWeekRingtoneRecommendInClock(true);
            }
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_RESOURCE_STATE, "NEW");
            return;
        }
        if (!ExternalResourceUtils.hasLoadRomResource()) {
            this.mResourceLoadConnection = new ResourceLoadServiceConnection();
            this.mResourceLoadCallback = new ResourceLoadServiceCallback(this);
            this.mLoadAfterServiceBind = true;
            Intent intent = new Intent();
            intent.setClass(DeskClockApp.getAppDEContext(), ResourceLoadService.class);
            intent.putExtra(ResourceLoadService.EXTRA_TYPE, 1);
            startForegroundService(intent);
            bindService(intent, this.mResourceLoadConnection, 1);
            return;
        }
        if (ExternalResourceUtils.canRemindNetResource()) {
            if (ExternalResourceUtils.needUpdateResource()) {
                this.mShowNetResourceDialog = true;
                showNetResourceDialog();
                return;
            } else {
                Log.i(ExternalResourceUtils.TAG, "no need to update net resource");
                return;
            }
        }
        Log.i(ExternalResourceUtils.TAG, "no need to load rom or net resource");
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

    /* JADX INFO: Access modifiers changed from: private */
    public void showNetResourceDialog() {
        String str;
        if (this.mIsPermissionAccepted && this.mShowNetResourceDialog && !this.mIsPause) {
            boolean z = PrefUtil.getMiuiResourceVersion() >= 2;
            if (WeatherRingtoneHelper.isRomSupport()) {
                if (NetworkUtil.isWifiConnected()) {
                    PrefUtil.setUpdateDirectly(true);
                    startLoadNetResource();
                } else if (NetworkUtil.isNetworkConnected()) {
                    PrefUtil.setUpdateDirectly(false);
                    if (WeekRingtoneHelper.isRomSupport()) {
                        if (z) {
                            str = String.format(getResources().getString(R.string.module_week_ringtone_update_data_new), 30);
                        } else {
                            str = String.format(getResources().getString(R.string.module_week_ringtone_download_data_new), 30);
                        }
                    } else if (z) {
                        str = String.format(getResources().getString(R.string.module_weather_ringtone_update_data_new), 30);
                    } else {
                        str = String.format(getResources().getString(R.string.module_weather_ringtone_download_data_new), 30);
                    }
                    this.mDownloadResourceDialog = DialogUtil.showAlertDialog("", str, R.string.module_dialog_negative, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.DeskClockTabActivity.6
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i("showDownloadDialog, negative click");
                            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_RESOURCE_STATE, "NULL");
                        }
                    }, R.string.module_dialog_positive, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.DeskClockTabActivity.7
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i("showDownloadDialog, positive click");
                            DeskClockTabActivity.this.startLoadNetResource();
                            OneTrackStatHelper.trackClickEvent("");
                        }
                    }, getSupportFragmentManager(), false, getString(R.string.dialog_message_not_remind), ExternalResourceUtils.PREF_NET_RESOURCE_REMIND);
                }
            }
            this.mShowNetResourceDialog = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showHolidayAlarmDialog() throws Throwable {
        int i;
        final int i2;
        if (this.mIsPermissionAccepted || !this.mShowHolidayAlarmDialog || this.mIsPause || Build.VERSION.SDK_INT >= 29) {
            return;
        }
        boolean z = false;
        this.mShowHolidayAlarmDialog = false;
        if (Util.isInternational()) {
            return;
        }
        final Set<Integer> setQueryRepeatTypeAlarm = AlarmHelper.queryRepeatTypeAlarm(this, 128);
        final Set<Integer> setQueryRepeatTypeAlarm2 = AlarmHelper.queryRepeatTypeAlarm(this, 256);
        boolean z2 = (setQueryRepeatTypeAlarm == null || setQueryRepeatTypeAlarm.isEmpty()) ? false : true;
        if (setQueryRepeatTypeAlarm2 != null && !setQueryRepeatTypeAlarm2.isEmpty()) {
            z = true;
        }
        if (z2 || z) {
            if (z2 && z) {
                i = R.string.dialog_legal_workday_and_off_day;
                i2 = R.string.dialog_legal_workday_and_off_day_desc;
            } else if (!z2 || z) {
                i = R.string.dialog_legal_off_day;
                i2 = R.string.dialog_legal_off_day_desc;
            } else {
                i = R.string.dialog_legal_workday;
                i2 = R.string.dialog_legal_workday_desc;
            }
            this.mLegalWorkdayDialog = UserNoticeUtil.showUserNoticeDialog(this, i, new UserNoticeUtil.OnNetPermissionListener() { // from class: com.android.deskclock.DeskClockTabActivity.8
                @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
                public void onAccept() {
                    DeskClockTabActivity.this.onNetPermissionAccept();
                }

                @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
                public void onReject() {
                    DeskClockTabActivity.this.onNetPermissionNotAccept();
                    DeskClockTabActivity.this.showResetDaysOfWeekDialog(i2, setQueryRepeatTypeAlarm, setQueryRepeatTypeAlarm2);
                }
            }, getSupportFragmentManager());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showResetDaysOfWeekDialog(int i, final Set<Integer> set, final Set<Integer> set2) {
        AlertDialog alertDialogCreate = new AlertDialog.Builder(this).setMessage(i).setPositiveButton(R.string.module_update_success_control, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.DeskClockTabActivity.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                Context appDEContext = DeskClockApp.getAppDEContext();
                AlarmHelper.resetWorkdayAlarm(appDEContext, set);
                AlarmHelper.resetOffdayAlarm(appDEContext, set2);
                AlarmHelper.setNextAlert(DeskClockTabActivity.this);
            }
        }).setCancelable(false).create();
        this.mResetDaysOfWeekDialog = alertDialogCreate;
        alertDialogCreate.show();
    }

    public void onLoadingComplete(boolean z) {
        if (z) {
            ExternalResourceUtils.toastDownloadSuccess();
        } else {
            ExternalResourceUtils.toastDownloadFail();
        }
    }

    private class ResourceLoadServiceConnection implements ServiceConnection {
        private ResourceLoadServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(ExternalResourceUtils.TAG, "service bind");
            DeskClockTabActivity.this.mResourceLoadService = ((ResourceLoadService.CallbackBinder) iBinder).getService();
            DeskClockTabActivity.this.mResourceLoadService.registerCallbackListener(DeskClockTabActivity.this.mResourceLoadCallback);
            if (DeskClockTabActivity.this.mLoadAfterServiceBind) {
                DeskClockTabActivity.this.mResourceLoadService.loadRomResource(PrefUtil.getMiuiResourceVersion());
                DeskClockTabActivity.this.mLoadAfterServiceBind = false;
            } else if (DeskClockTabActivity.this.mDownloadAfterServiceBind) {
                DeskClockTabActivity.this.mResourceLoadService.loadNetResource();
                DeskClockTabActivity.this.mDownloadAfterServiceBind = false;
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            DeskClockTabActivity.this.mResourceLoadService.unregisterCallbackListener(DeskClockTabActivity.this.mResourceLoadCallback);
            DeskClockTabActivity.this.mResourceLoadService = null;
        }
    }

    private static class ResourceLoadServiceCallback implements ResourceLoadService.CallbackListener {
        private WeakReference<DeskClockTabActivity> mReference;

        public ResourceLoadServiceCallback(DeskClockTabActivity deskClockTabActivity) {
            this.mReference = new WeakReference<>(deskClockTabActivity);
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onRomLoadSuccess(int i) {
            Log.i(ExternalResourceUtils.TAG, "onRomLoadSuccess in DeskClockTabActivity");
            WeakReference<DeskClockTabActivity> weakReference = this.mReference;
            DeskClockTabActivity deskClockTabActivity = weakReference != null ? weakReference.get() : null;
            if (deskClockTabActivity != null && ExternalResourceUtils.needUpdateResource()) {
                deskClockTabActivity.mShowNetResourceDialog = true;
                deskClockTabActivity.showNetResourceDialog();
            }
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onRomLoadFailed() {
            Log.i(ExternalResourceUtils.TAG, "onRomLoadFailed in DeskClockTabActivity");
            WeakReference<DeskClockTabActivity> weakReference = this.mReference;
            DeskClockTabActivity deskClockTabActivity = weakReference != null ? weakReference.get() : null;
            if (deskClockTabActivity == null) {
                return;
            }
            deskClockTabActivity.mShowNetResourceDialog = true;
            deskClockTabActivity.showNetResourceDialog();
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onNetLoadSuccess() {
            Log.i(ExternalResourceUtils.TAG, "onNetLoadSuccess in DeskClockTabActivity");
            WeakReference<DeskClockTabActivity> weakReference = this.mReference;
            if ((weakReference != null ? weakReference.get() : null) == null || PrefUtil.isUpdateDirectly()) {
                return;
            }
            ExternalResourceUtils.toastDownloadSuccess();
        }

        @Override // com.android.deskclock.addition.resource.ResourceLoadService.CallbackListener
        public void onNetLoadFailed() {
            Log.i(ExternalResourceUtils.TAG, "onNetLoadFailed in DeskClockTabActivity");
            WeakReference<DeskClockTabActivity> weakReference = this.mReference;
            if ((weakReference != null ? weakReference.get() : null) == null || PrefUtil.isUpdateDirectly()) {
                return;
            }
            ExternalResourceUtils.toastDownloadFail();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (!PermissionUtil.checkPermission(this, "android.permission.POST_NOTIFICATIONS")) {
                PermissionUtil.requestPermissions(this, "android.permission.POST_NOTIFICATIONS", getResources().getString(R.string.notification_permission_desc), 1000);
                return;
            } else {
                NOTIFICATION_PERMISSION_GRANTED = true;
                return;
            }
        }
        NOTIFICATION_PERMISSION_GRANTED = true;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 1000) {
            if (iArr.length != 0 && iArr[0] == 0) {
                NOTIFICATION_PERMISSION_GRANTED = true;
                return;
            } else {
                NOTIFICATION_PERMISSION_GRANTED = false;
                return;
            }
        }
        if (i == 1) {
            Fragment fragmentFetchAlarmFragment = fetchAlarmFragment();
            if (fragmentFetchAlarmFragment instanceof AlarmClockFragment) {
                ((AlarmClockFragment) fragmentFetchAlarmFragment).handleRequestPermissionsResult(i, strArr, iArr);
                return;
            }
            return;
        }
        if (i == 2000 && iArr.length != 0 && iArr[0] == 0) {
            handlePermissionGranted();
        }
    }

    public Fragment fetchAlarmFragment() {
        Fragment fragment = (Fragment) getSupportFragmentManager().findFragmentByTag(TabViewModel.TAB_ALARM);
        if (fragment instanceof TabNavigatorContentFragment) {
            return ((TabNavigatorContentFragment) fragment).getAlarmClockFragment();
        }
        Log.e(TAG, "Fragment with tag ALARM is not a TabNavigatorContentFragment");
        return null;
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) throws Throwable {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent intent：" + intent);
        resolveAiAction(intent);
        setE5ShowWhenLocked(intent);
        TabNavigatorContentFragment.initShortcutRequest(intent);
        initKeyguardShortcutRequest(intent);
        if (ACTION_REQUEST_NET_PERMISSION.equals(intent.getAction())) {
            showNetPermissionDialogFromAction();
        } else if (!Util.isInternational()) {
            showCtaDialog();
        }
        BedtimeUtil.getGuideSettingsIndex(this);
        if (StatHelper.EVENT_CLICK_ALARM_ARRIVING_NOTIFICATION.equals(intent.getStringExtra(Util.INTENT_FROM))) {
            StatHelper.alarmEvent(StatHelper.EVENT_CLICK_ALARM_ARRIVING_NOTIFICATION);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_ARRIVING_NOTIFICATION_CLICK);
        }
        if (intent.hasExtra(Util.NAVIGATION_TAB) || intent.getData() != null) {
            String primaryTabFromIntent = TabNavigatorContentFragment.getPrimaryTabFromIntent(intent);
            AlarmClockFragment.isFromCtsSetAlarm = intent.getBooleanExtra(Util.IS_FROM_CTS_SET_ALARM, false);
            if (AlarmClockFragment.isFromCtsSetAlarm) {
                TabNavigatorContentFragment.mFromCtsAlarm = (Alarm) intent.getParcelableExtra(AlarmHelper.ALARM_INTENT_EXTRA);
            }
            TabNavigatorContentFragment.mCurrTab = primaryTabFromIntent;
        }
    }

    private void setE5ShowWhenLocked(Intent intent) {
        if (intent.getBooleanExtra(MiuiIntent.EXTRA_START_ACTIVITY_WHEN_LOCKED, false)) {
            getWindow().addFlags(524288);
        }
    }

    public void setEditDialogView(AlarmEditDialogView alarmEditDialogView) {
        this.mAlarmEditDialogView = alarmEditDialogView;
    }

    public AlarmEditDialogView getAlarmEditDialogView() {
        return this.mAlarmEditDialogView;
    }

    private void initActivityResultLauncher() {
        this.toKoreaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.DeskClockTabActivity$$ExternalSyntheticLambda0
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) throws Throwable {
                this.f$0.m79x44fd5410((ActivityResult) obj);
            }
        });
        this.toCtaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.DeskClockTabActivity$$ExternalSyntheticLambda1
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) throws Throwable {
                this.f$0.m80x7e9bd6f((ActivityResult) obj);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initActivityResultLauncher$0$com-android-deskclock-DeskClockTabActivity, reason: not valid java name */
    /* synthetic */ void m79x44fd5410(ActivityResult activityResult) throws Throwable {
        KoreaPermissionUtil.handleKoreaAuthCallback(activityResult.getResultCode());
        if (KoreaPermissionUtil.isKoreaResultCode(activityResult.getResultCode())) {
            showCtaDialog();
        }
    }

    /* JADX INFO: renamed from: lambda$initActivityResultLauncher$1$com-android-deskclock-DeskClockTabActivity, reason: not valid java name */
    /* synthetic */ void m80x7e9bd6f(ActivityResult activityResult) throws Throwable {
        Log.d(TAG, "toCtaLauncher getResultCode : " + activityResult.getResultCode());
        checkNotificationPermission();
        if (activityResult.getResultCode() == -3) {
            showCtaDialog();
        }
        if (activityResult.getResultCode() == 1) {
            dismissHomePageMask(true, true);
            UserNoticeUtil.setAcceptNetPermission(true);
            StatHelper.init(DeskClockApp.getAppContext());
            OneTrackStatHelper.init(DeskClockApp.getAppContext());
            onNetPermissionAccept();
            showNetResourceDialog();
            if (RingtoneUriCompat.atLeastU()) {
                AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.DeskClockTabActivity.10
                    @Override // java.lang.Runnable
                    public void run() {
                        RingtoneUriCompat.updateConvertAllUri();
                        DeskClockTabActivity.this.loadExternalResource();
                    }
                });
                return;
            }
            return;
        }
        if (activityResult.getResultCode() == 666) {
            dismissHomePageMask(true, true);
            UserNoticeUtil.setAcceptNetPermission(false);
            onNetPermissionNotAccept();
            UserNoticeUtil.setRemindNetPermission(false);
            this.mShowHolidayAlarmDialog = true;
            showHolidayAlarmDialog();
            return;
        }
        dismissHomePageMask(false, true);
        Log.e(SystemPermissionUtil.TAG, "lack of important information");
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
    }

    @Override // miuix.navigator.app.NavigatorActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy ");
        ResourceLoadServiceConnection resourceLoadServiceConnection = this.mResourceLoadConnection;
        if (resourceLoadServiceConnection != null) {
            unbindService(resourceLoadServiceConnection);
            this.mResourceLoadService = null;
        }
        DialogUtil.dismissDialogFragment(this.mUserNoticeDialog);
        DialogUtil.dismissDialogFragment(this.mDownloadResourceDialog);
        DialogUtil.dismissDialogFragment(this.mLegalWorkdayDialog);
        DialogUtil.dismissDialog(this.mResetDaysOfWeekDialog);
        this.mUserNoticeDialog = null;
        this.mDownloadResourceDialog = null;
        this.mLegalWorkdayDialog = null;
        this.mResetDaysOfWeekDialog = null;
        FabControllerNew.getInstance().destroy();
        this.mPermissionRequestCallback = null;
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler = null;
    }

    private void resolveAiAction(Intent intent) {
        Bundle bundle;
        Bundle extras = intent.getExtras();
        if (extras == null || !extras.containsKey("foreground_input") || (bundle = extras.getBundle("foreground_input")) == null) {
            return;
        }
        Bundle bundle2 = new Bundle();
        bundle.getString("type");
        if (bundle.containsKey("action_callback_uri")) {
            String string = bundle.getString("action_callback_uri");
            bundle2.putString(MyAiActionProvider.OUT_CODE_NAME, "{\"status\": 0}");
            bundle2.putInt("target_code", 0);
            if (bundle.containsKey("action_request_id")) {
                bundle2.putString("target_response_id", bundle.getString("action_request_id"));
            }
            getContentResolver().call(Uri.parse(string), "action_result", (String) null, bundle2);
        }
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

    private void showHomePageMask() {
        if (this.mHomePageMaskView == null) {
            ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
            Context baseContext = getBaseContext();
            if (viewGroup != null && baseContext != null) {
                View view = new View(baseContext);
                this.mHomePageMaskView = view;
                view.setBackgroundResource(R.color.home_page_mask_bg);
                this.mHomePageMaskView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
                viewGroup.addView(this.mHomePageMaskView);
            }
        }
        if (this.mHomePageMaskView != null) {
            Log.f(TAG, "showHomePageMask");
            this.mHomePageMaskView.bringToFront();
            this.mHomePageMaskView.setVisibility(0);
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.postDelayed(this.mDismissHomePageMaskRunnable, 1500L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissHomePageMask(boolean z, boolean z2) {
        ViewGroup viewGroup;
        View view;
        Handler handler;
        if (this.mHomePageMaskView != null) {
            Log.f(TAG, "dismissHomePageMask");
            this.mHomePageMaskView.setVisibility(8);
            if (z2 && (handler = this.mHandler) != null) {
                handler.removeCallbacks(this.mDismissHomePageMaskRunnable);
            }
        }
        if (!z || (viewGroup = (ViewGroup) findViewById(android.R.id.content)) == null || (view = this.mHomePageMaskView) == null || view.getParent() != viewGroup) {
            return;
        }
        viewGroup.removeView(this.mHomePageMaskView);
        this.mHomePageMaskView = null;
    }
}
