package com.android.deskclock;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.OriginalViewPager;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.MiuiTheme;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.alarm.AlarmClockFragment;
import com.android.deskclock.alarm.bedtime.BedtimeGuideActivity;
import com.android.deskclock.alarm.bedtime.BedtimeManageActivity;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.lifepost.LifePostSettingActivity;
import com.android.deskclock.alarm.lifepost.LifePostUtils;
import com.android.deskclock.base.BaseClockFragment;
import com.android.deskclock.settings.SettingsActivity;
import com.android.deskclock.stopwatch.StopwatchFragment;
import com.android.deskclock.timer.Timer;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.timer.TimerFragment;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.FastStartUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.fab.FabControllerNew;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.DraggableViewPager;
import com.android.deskclock.view.FabView;
import com.android.deskclock.view.ViewStubFrameLayout;
import com.android.deskclock.view.tab.TabViewModel;
import com.android.deskclock.worldclock.TimezoneSearchBSActivity;
import com.android.deskclock.worldclock.WorldClockFragment;
import java.lang.ref.WeakReference;
import miuix.appcompat.app.Fragment;
import miuix.appcompat.internal.app.widget.ActionBarContainer;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.navigator.Navigator;
import miuix.navigator.NavigatorFragmentListener;
import miuix.navigator.navigatorinfo.UpdateFragmentNavInfo;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes.dex */
public class TabNavigatorContentFragment extends Fragment implements NavigatorFragmentListener, ViewStubFrameLayout.OnChildStubInflatedListener, ActionBar.TabListener {
    public static final String ARG_DATA_ID = "data_id";
    public static final String ARG_PAGE = "page";
    public static final String CURRENT_TAB = "current_tab";
    private static final int DESKCLOCK_DEFAULT_OFFSCREEN_PAGES = 3;
    public static final String EXTRA_DATA_BUNDLE = "data_bundle";
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    public static final String TAG = "DC:TabNavigatorContentFragment";
    private static long lastClickTime = 0;
    public static String mCurrTab = "ALARM";
    public static Alarm mFromCtsAlarm = null;
    public static boolean mIsKeyguardShortcut = false;
    public static String mShortcutType;
    private FabView endView2;
    private miuix.appcompat.app.ActionBar mActionBar;
    private DeskClockTabActivity mActivity;
    private MenuItem mAddClock;
    private UpdateFragmentNavInfo mAlarmNavInfo;
    private UpdateFragmentNavInfo mClockNavInfo;
    private BroadcastReceiver mReceiver;
    private View mRootView;
    private boolean mScreenOn;
    private UpdateFragmentNavInfo mStopWatchNavInfo;
    private UpdateFragmentNavInfo mTimerNavInfo;
    private DraggableViewPager mViewPager;
    private DynamicFragmentPagerAdapter mViewPagerAdapter;
    private ActivityResultLauncher<Intent> toBedtimeLauncher;
    public ActivityResultLauncher<Intent> toTimeZoneSearchLauncher;
    private final String mPage1Name = DeskClockApp.getAppDEContext().getString(R.string.alarm_list_title);
    private final String mPage2Name = DeskClockApp.getAppDEContext().getString(R.string.clock_tab_name);
    private final String mPage3Name = DeskClockApp.getAppDEContext().getString(R.string.stopwatch_title);
    private final String mPage4Name = DeskClockApp.getAppDEContext().getString(R.string.timer_title);
    public String mOldTab = TabViewModel.TAB_ALARM;
    private boolean handleTimezoneResult = false;
    private String timezoneCityId = null;
    private boolean hasInited = false;
    private boolean isInActionMode = false;
    private Handler mHandler = new Handler();
    private final Runnable mChangeFabTaskRunnable = new Runnable() { // from class: com.android.deskclock.TabNavigatorContentFragment.7
        @Override // java.lang.Runnable
        public void run() {
            if (TabNavigatorContentFragment.this.mActivity == null || TabNavigatorContentFragment.this.mActivity.isFinishing()) {
                return;
            }
            FabControllerNew.getInstance().changeFabWithPageChanged(TabNavigatorContentFragment.mCurrTab, 0.0f);
        }
    };

    public interface IClockViews {
        void onDataChanged();

        void onTimeChanged();

        void onTimeFormatChanged();

        void onTimeTick();

        void onTimezoneChanged();

        boolean shouldKeepScreenOn();
    }

    public interface IFabClick {
        void onCenterClick(View view);

        void onEndClick(View view);

        void onEndClick2(View view);

        void onStartClick(View view);
    }

    public interface IFragmentChange {
        void onEnter();

        void onLeave();
    }

    @Override // miuix.appcompat.app.Fragment
    protected boolean isResponsiveEnabled() {
        return true;
    }

    @Override // com.android.deskclock.view.ViewStubFrameLayout.OnChildStubInflatedListener
    public void onChildInflated(View view, int i) {
    }

    @Override // androidx.appcompat.app.ActionBar.TabListener
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override // androidx.appcompat.app.ActionBar.TabListener
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DeskClockTabActivity deskClockTabActivity = (DeskClockTabActivity) getActivity();
        this.mActivity = deskClockTabActivity;
        if (Util.isTinyScreen(deskClockTabActivity)) {
            setThemeRes(R.style.TabNavigatorContentFragmentThemeTiny);
        } else {
            setThemeRes(R.style.TabNavigatorContentFragmentTheme);
        }
        Log.d(TAG, "onCreate");
        if (bundle != null) {
            mCurrTab = bundle.getString(CURRENT_TAB);
        }
        Util.resetRtl();
        mCurrTab = getPrimaryTabFromIntent(this.mActivity.getIntent());
        initShortcutRequest(getActivity().getIntent());
        this.mActivity.getWindow().getDecorView().post(new Runnable() { // from class: com.android.deskclock.TabNavigatorContentFragment.1
            @Override // java.lang.Runnable
            public void run() {
                if (TabNavigatorContentFragment.this.handleTimezoneResult && TabNavigatorContentFragment.this.timezoneCityId != null) {
                    Fragment fragment = TabNavigatorContentFragment.this.mViewPagerAdapter.getFragment(TabViewModel.TAB_CLOCK, false);
                    if (fragment instanceof WorldClockFragment) {
                        ((WorldClockFragment) fragment).handleActivityResult(TabNavigatorContentFragment.this.timezoneCityId);
                    }
                    TabNavigatorContentFragment.this.handleTimezoneResult = false;
                    TabNavigatorContentFragment.this.timezoneCityId = null;
                }
                TabNavigatorContentFragment.this.refreshKeepScreenOnState();
                TabNavigatorContentFragment.this.handleShortcutRequest();
                TabNavigatorContentFragment.this.hasInited = true;
                MiuiTheme.recordVersion();
            }
        });
        initActivityResultLauncher();
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.fragment_tab_content, viewGroup, false);
        this.mRootView = viewInflate;
        AlarmThreadPool.poolExecute(new InitRunnable(this, this.mActivity));
        return viewInflate;
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public void onViewInflated(View view, Bundle bundle) {
        super.onViewInflated(view, bundle);
        setCorrectNestedScrollMotionEventEnabled(true);
        this.mViewPager = (DraggableViewPager) this.mRootView.findViewById(R.id.viewpager);
        Log.d(TAG, "onViewInflated: ");
        setupViewPager();
    }

    @Override // androidx.appcompat.app.ActionBar.TabListener
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        DraggableViewPager draggableViewPager = this.mViewPager;
        if (draggableViewPager == null || this.isInActionMode) {
            return;
        }
        draggableViewPager.setCurrentItem(tab.getPosition());
        OneTrackStatHelper.recordTabClick(tab.getPosition());
    }

    private static class InitRunnable implements Runnable {
        private WeakReference<TabNavigatorContentFragment> mReference;
        private WeakReference<DeskClockTabActivity> mWeakActivity;

        public InitRunnable(TabNavigatorContentFragment tabNavigatorContentFragment, DeskClockTabActivity deskClockTabActivity) {
            this.mReference = new WeakReference<>(tabNavigatorContentFragment);
            this.mWeakActivity = new WeakReference<>(deskClockTabActivity);
        }

        @Override // java.lang.Runnable
        public void run() {
            TabNavigatorContentFragment tabNavigatorContentFragment = this.mReference.get();
            DeskClockTabActivity deskClockTabActivity = this.mWeakActivity.get();
            if (tabNavigatorContentFragment != null) {
                tabNavigatorContentFragment.initAsync(deskClockTabActivity, tabNavigatorContentFragment);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initAsync(DeskClockTabActivity deskClockTabActivity, TabNavigatorContentFragment tabNavigatorContentFragment) {
        if (StatHelper.EVENT_CLICK_ALARM_ARRIVING_NOTIFICATION.equals(deskClockTabActivity.getIntent().getStringExtra(Util.INTENT_FROM))) {
            StatHelper.alarmEvent(StatHelper.EVENT_CLICK_ALARM_ARRIVING_NOTIFICATION);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_ARRIVING_NOTIFICATION_CLICK);
        }
        deskClockTabActivity.runOnUiThread(new Runnable() { // from class: com.android.deskclock.TabNavigatorContentFragment.2
            @Override // java.lang.Runnable
            public void run() {
                TabNavigatorContentFragment.this.handleShortcutRequest();
            }
        });
        initReceiver();
        initNavigationInfos();
        boolean zIsTinyScreen = Util.isTinyScreen(this.mActivity);
        StatHelper.recordTabSelected(TabViewModel.getTabPosition(mCurrTab, zIsTinyScreen));
        OneTrackStatHelper.recordTabView(TabViewModel.getTabPosition(mCurrTab, zIsTinyScreen));
    }

    private void initNavigationInfos() {
        this.mAlarmNavInfo = getUpdateFragmentNavInfoToAlarm();
        this.mClockNavInfo = getUpdateFragmentNavInfoToClock();
        this.mStopWatchNavInfo = getUpdateFragmentNavInfoToStopWatch();
        this.mTimerNavInfo = getUpdateFragmentNavInfoToTimer();
    }

    private UpdateFragmentNavInfo getUpdateFragmentNavInfoToAlarm() {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE, 0);
        Log.d("getUpdateFragmentNavInfoToAlarm ：" + bundle);
        return new UpdateFragmentNavInfo(1000, getClass(), bundle);
    }

    private UpdateFragmentNavInfo getUpdateFragmentNavInfoToClock() {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE, 1);
        Log.d("getUpdateFragmentNavInfoToClock ：" + bundle);
        return new UpdateFragmentNavInfo(1001, getClass(), bundle);
    }

    private UpdateFragmentNavInfo getUpdateFragmentNavInfoToStopWatch() {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE, 2);
        Log.d("getUpdateFragmentNavInfoToStopWatch ：" + bundle);
        return new UpdateFragmentNavInfo(1002, getClass(), bundle);
    }

    private UpdateFragmentNavInfo getUpdateFragmentNavInfoToTimer() {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE, 3);
        Log.d("getUpdateFragmentNavInfoToTimer ：" + bundle);
        return new UpdateFragmentNavInfo(1003, getClass(), bundle);
    }

    public void onActionModeChanged(boolean z) {
        this.isInActionMode = z;
        DraggableViewPager draggableViewPager = this.mViewPager;
        if (draggableViewPager != null) {
            draggableViewPager.setDraggable(!z);
        }
        if (mCurrTab.equals(TabViewModel.TAB_CLOCK)) {
            return;
        }
        if (mCurrTab.equals(TabViewModel.TAB_STOPWATCH) || mCurrTab.equals(TabViewModel.TAB_TIMER)) {
            FabControllerNew.getInstance().changeBtnAlpha(false);
        } else {
            FabControllerNew.getInstance().changeBtnAlpha(!z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectNavigationItem(boolean z) {
        if (this.isInActionMode) {
            destroyActionMode();
            if (mCurrTab.equals(TabViewModel.TAB_STOPWATCH) || mCurrTab.equals(TabViewModel.TAB_TIMER) || mCurrTab.equals(TabViewModel.TAB_CLOCK)) {
                FabControllerNew.getInstance().changeBtnAlpha(false);
            }
        }
        Log.d(TAG, "selectNavigationItem: " + mCurrTab);
        if (z && !DeskClockTabActivity.KEY_GUARD_SHORTCUT_ALARM_PAGE.equals(DeskClockTabActivity.mKeyguardShortcutIndex) && (TabViewModel.TAB_ALARM.equals(mCurrTab) || TabViewModel.TAB_CLOCK.equals(mCurrTab))) {
            jumpToLockScreen();
        }
        String str = mCurrTab;
        str.hashCode();
        switch (str) {
            case "ALARM":
                navigateToAlarm(this);
                break;
            case "CLOCK":
                navigateToClock(this);
                break;
            case "TIMER":
                DynamicFragmentPagerAdapter dynamicFragmentPagerAdapter = this.mViewPagerAdapter;
                if (dynamicFragmentPagerAdapter != null) {
                    Fragment fragment = dynamicFragmentPagerAdapter.getFragment(TabViewModel.TAB_TIMER, false);
                    if (fragment instanceof TimerFragment) {
                        ((TimerFragment) fragment).handleNotificationTimer();
                    }
                }
                navigateToTimer(this);
                break;
            case "STOPWATCH":
                navigateToStopWatch(this);
                break;
        }
    }

    private void jumpToLockScreen() {
        KeyguardManager keyguardManager = (KeyguardManager) this.mActivity.getSystemService("keyguard");
        if (keyguardManager.isKeyguardLocked()) {
            keyguardManager.requestDismissKeyguard(this.mActivity, new LockedKeyguardDismissCallback());
        }
    }

    public class LockedKeyguardDismissCallback extends KeyguardManager.KeyguardDismissCallback {
        public LockedKeyguardDismissCallback() {
        }

        @Override // android.app.KeyguardManager.KeyguardDismissCallback
        public void onDismissSucceeded() {
            Log.d(TabNavigatorContentFragment.TAG, "succeeded to dismiss keyguard: ");
            TabNavigatorContentFragment.mIsKeyguardShortcut = false;
            DeskClockTabActivity.mKeyguardShortcutIndex = null;
        }

        @Override // android.app.KeyguardManager.KeyguardDismissCallback
        public void onDismissError() {
            Log.e(TabNavigatorContentFragment.TAG, "Failed to dismiss keyguard");
        }

        @Override // android.app.KeyguardManager.KeyguardDismissCallback
        public void onDismissCancelled() {
            ActivityResultCaller fragment;
            Log.e(TabNavigatorContentFragment.TAG, "Cancel to dismiss keyguard");
            if ((TabNavigatorContentFragment.mCurrTab.equals(TabViewModel.TAB_ALARM) || TabNavigatorContentFragment.mCurrTab.equals(TabViewModel.TAB_CLOCK)) && !TextUtils.isEmpty(DeskClockTabActivity.mKeyguardShortcutIndex)) {
                if (DeskClockTabActivity.mKeyguardShortcutIndex.equals(DeskClockTabActivity.KEY_GUARD_SHORTCUT_STOPWATCH_PAGE) || DeskClockTabActivity.mKeyguardShortcutIndex.equals(DeskClockTabActivity.KEY_GUARD_SHORTCUT_TIMER_PAGE)) {
                    if (DeskClockTabActivity.mKeyguardShortcutIndex.equals(DeskClockTabActivity.KEY_GUARD_SHORTCUT_STOPWATCH_PAGE)) {
                        TabNavigatorContentFragment.mCurrTab = TabViewModel.TAB_STOPWATCH;
                    } else if (DeskClockTabActivity.mKeyguardShortcutIndex.equals(DeskClockTabActivity.KEY_GUARD_SHORTCUT_TIMER_PAGE)) {
                        TabNavigatorContentFragment.mCurrTab = TabViewModel.TAB_TIMER;
                    }
                    TabNavigatorContentFragment.this.selectNavigationItem(false);
                    if (!TabNavigatorContentFragment.mCurrTab.equals(TabViewModel.TAB_STOPWATCH) || (fragment = TabNavigatorContentFragment.this.mViewPagerAdapter.getFragment(TabNavigatorContentFragment.mCurrTab, false)) == null) {
                        return;
                    }
                    ((IFragmentChange) fragment).onEnter();
                }
            }
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (this.hasInited) {
            refreshKeepScreenOnState();
            handleShortcutRequest();
        }
        selectNavigationItem(false);
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.TabNavigatorContentFragment.3
            @Override // java.lang.Runnable
            public void run() {
                StatHelper.recordNumericPropertyEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_OPEN_DESKCLOCK, TimeUtil.getHour());
                OneTrackStatHelper.trackNumEvent(TimeUtil.getHour(), "");
            }
        });
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        boolean z = AlarmHelper.get24HourMode();
        AlarmHelper.reset24HourMode(this.mActivity);
        if (z != AlarmHelper.get24HourMode()) {
            onTimeFormatChanged();
        }
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.TabNavigatorContentFragment.4
            @Override // java.lang.Runnable
            public void run() {
                XiaoAiRingtoneHelper.resetEnableValue();
            }
        });
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mActivity.getWindow().clearFlags(128);
        if (TabViewModel.TAB_ALARM.equals(mCurrTab)) {
            FastStartUtil.notifyTakeSnapshotQs(this.mActivity);
        }
        mShortcutType = null;
    }

    private void onTimeFormatChanged() {
        if (this.mViewPagerAdapter == null) {
            return;
        }
        for (int i = 0; i < this.mViewPagerAdapter.getCount(); i++) {
            ActivityResultCaller fragment = this.mViewPagerAdapter.getFragment(TabViewModel.getTabAt(i, this.mViewPagerAdapter.getCount() == 2), false);
            if (fragment instanceof IClockViews) {
                ((IClockViews) fragment).onTimeFormatChanged();
            }
        }
    }

    public void refreshKeepScreenOnState() {
        DynamicFragmentPagerAdapter dynamicFragmentPagerAdapter = this.mViewPagerAdapter;
        if (dynamicFragmentPagerAdapter == null) {
            return;
        }
        boolean z = false;
        ActivityResultCaller fragment = dynamicFragmentPagerAdapter.getFragment(mCurrTab, false);
        if (fragment instanceof IClockViews) {
            this.mScreenOn = ((IClockViews) fragment).shouldKeepScreenOn();
            Timer timer = TimerDao.getTimer(this.mActivity);
            if (timer.getState() == 1 && timer.isBright()) {
                z = true;
            }
            if (TabViewModel.TAB_STOPWATCH.equals(mCurrTab)) {
                if (this.mScreenOn || z) {
                    this.mActivity.getWindow().addFlags(128);
                    return;
                } else {
                    this.mActivity.getWindow().clearFlags(128);
                    return;
                }
            }
            if (TabViewModel.TAB_ALARM.equals(mCurrTab) || TabViewModel.TAB_CLOCK.equals(mCurrTab)) {
                if (z) {
                    this.mActivity.getWindow().addFlags(128);
                    return;
                } else {
                    this.mActivity.getWindow().clearFlags(128);
                    return;
                }
            }
            if (this.mScreenOn) {
                this.mActivity.getWindow().addFlags(128);
            } else {
                this.mActivity.getWindow().clearFlags(128);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0022  */
    private int getIndexByTab(String str) {
        int i = 1;
        if (Util.isTinyScreen(this.mActivity)) {
            if (TabViewModel.TAB_ALARM.equals(str) || !TabViewModel.TAB_TIMER.equals(str)) {
                i = 0;
            }
        } else if (TabViewModel.TAB_ALARM.equals(str)) {
            i = 0;
        } else if (!TabViewModel.TAB_CLOCK.equals(str)) {
            if (TabViewModel.TAB_STOPWATCH.equals(str)) {
                i = 2;
            } else if (TabViewModel.TAB_TIMER.equals(str)) {
                i = 3;
            } else {
                i = 0;
            }
        }
        Log.d(TAG, "getIndexByTab: " + i);
        return i;
    }

    private void initReceiver() {
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.deskclock.TabNavigatorContentFragment.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                boolean z;
                String action = intent.getAction();
                Log.i(TabNavigatorContentFragment.TAG, "onReceive action=" + action);
                if (TextUtils.isEmpty(action) || TabNavigatorContentFragment.this.mViewPagerAdapter == null) {
                    return;
                }
                if ("android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction())) {
                    z = TabNavigatorContentFragment.this.mViewPagerAdapter.getCount() == 2;
                    for (int i = 0; i < TabNavigatorContentFragment.this.mViewPagerAdapter.getCount(); i++) {
                        ActivityResultCaller fragment = TabNavigatorContentFragment.this.mViewPagerAdapter.getFragment(TabViewModel.getTabAt(i, z), false);
                        if (fragment instanceof IClockViews) {
                            ((IClockViews) fragment).onTimezoneChanged();
                        }
                    }
                    return;
                }
                if ("android.intent.action.TIME_SET".equals(intent.getAction())) {
                    z = TabNavigatorContentFragment.this.mViewPagerAdapter.getCount() == 2;
                    for (int i2 = 0; i2 < TabNavigatorContentFragment.this.mViewPagerAdapter.getCount(); i2++) {
                        ActivityResultCaller fragment2 = TabNavigatorContentFragment.this.mViewPagerAdapter.getFragment(TabViewModel.getTabAt(i2, z), false);
                        if (fragment2 instanceof IClockViews) {
                            ((IClockViews) fragment2).onTimeChanged();
                        }
                    }
                    return;
                }
                if ("android.intent.action.TIME_TICK".equals(intent.getAction())) {
                    Log.d(TabNavigatorContentFragment.TAG, "onReceive getCount : " + TabNavigatorContentFragment.this.mViewPagerAdapter.getCount());
                    if (TabNavigatorContentFragment.this.mViewPagerAdapter.getCount() != 2) {
                        ActivityResultCaller fragment3 = TabNavigatorContentFragment.this.mViewPagerAdapter.getFragment(TabViewModel.TAB_CLOCK, false);
                        if (fragment3 instanceof IClockViews) {
                            ((IClockViews) fragment3).onTimeTick();
                        }
                    }
                    ActivityResultCaller fragment4 = TabNavigatorContentFragment.this.mViewPagerAdapter.getFragment(TabViewModel.TAB_ALARM, false);
                    if (fragment4 instanceof IClockViews) {
                        ((IClockViews) fragment4).onTimeTick();
                        return;
                    }
                    return;
                }
                if (DeskClockTabActivity.ACTION_ALARM_CHANGED.equals(intent.getAction())) {
                    z = TabNavigatorContentFragment.this.mViewPagerAdapter.getCount() == 2;
                    for (int i3 = 0; i3 < TabNavigatorContentFragment.this.mViewPagerAdapter.getCount(); i3++) {
                        ActivityResultCaller fragment5 = TabNavigatorContentFragment.this.mViewPagerAdapter.getFragment(TabViewModel.getTabAt(i3, z), false);
                        if (fragment5 instanceof IClockViews) {
                            ((IClockViews) fragment5).onDataChanged();
                        }
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction(DeskClockTabActivity.ACTION_ALARM_CHANGED);
        Log.d(TAG, "  Build :" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 34) {
            this.mActivity.registerReceiver(this.mReceiver, intentFilter, 2);
        } else {
            this.mActivity.registerReceiver(this.mReceiver, intentFilter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFragmentChange(String str, String str2) {
        Log.d(TAG, "handleFragmentChange: oldTab: " + str + "newTab: " + str2);
        if ((Util.isTinyScreen(this.mActivity) && (str.equals(TabViewModel.TAB_CLOCK) || str.equals(TabViewModel.TAB_STOPWATCH))) || str2 == null || str == null || str2.equals(str)) {
            return;
        }
        Object fragment = this.mViewPagerAdapter.getFragment(str, false);
        Log.d(TAG, "oldFragment: " + fragment);
        if (fragment != null) {
            ((IFragmentChange) fragment).onLeave();
        }
        Object fragment2 = this.mViewPagerAdapter.getFragment(str2, false);
        Log.d(TAG, "newFragment: " + fragment2);
        if (fragment2 != null) {
            ((IFragmentChange) fragment2).onEnter();
        }
    }

    public static void initShortcutRequest(Intent intent) {
        if (!Util.isInternational() && UserNoticeUtil.canRemindNetPermission() && !UserNoticeUtil.isNetPermissionAgreed()) {
            if (intent != null) {
                intent.removeExtra(ShortcutTrampolineActivity.EXTRA_SHORTCUT);
            }
        } else {
            mShortcutType = null;
            if (intent != null) {
                mShortcutType = intent.getStringExtra(ShortcutTrampolineActivity.EXTRA_SHORTCUT);
                intent.removeExtra(ShortcutTrampolineActivity.EXTRA_SHORTCUT);
            }
        }
    }

    public void handleShortcutRequest() {
        if (Util.isInternational() || !UserNoticeUtil.canRemindNetPermission() || UserNoticeUtil.isNetPermissionAgreed()) {
            Log.d("handleShortcutRequest ShortcutType: " + mShortcutType);
            if (mShortcutType == null || this.mViewPagerAdapter == null) {
                return;
            }
            try {
                if (this.isInActionMode) {
                    destroyActionMode();
                }
                if (ShortcutTrampolineActivity.SHORTCUT_NEW_ALARM.equals(mShortcutType)) {
                    Fragment fragment = this.mViewPagerAdapter.getFragment(TabViewModel.TAB_ALARM, false);
                    if (fragment instanceof AlarmClockFragment) {
                        ((AlarmClockFragment) fragment).startFromShortcut();
                        return;
                    }
                    return;
                }
                if (ShortcutTrampolineActivity.SHORTCUT_STOP_WATCH.equals(mShortcutType)) {
                    Fragment fragment2 = this.mViewPagerAdapter.getFragment(TabViewModel.TAB_STOPWATCH, false);
                    if (fragment2 instanceof StopwatchFragment) {
                        ((StopwatchFragment) fragment2).startFromShortcut();
                        navigateToStopWatch(this);
                    }
                    Fragment fragment3 = this.mViewPagerAdapter.getFragment(TabViewModel.TAB_ALARM, false);
                    if (fragment3 instanceof AlarmClockFragment) {
                        ((AlarmClockFragment) fragment3).dismissSetAlarmDialog();
                        return;
                    }
                    return;
                }
                if (ShortcutTrampolineActivity.SHORTCUT_START_TIMER.equals(mShortcutType)) {
                    Fragment fragment4 = this.mViewPagerAdapter.getFragment(TabViewModel.TAB_TIMER, false);
                    if (fragment4 instanceof TimerFragment) {
                        ((TimerFragment) fragment4).startFromShortcut();
                        navigateToTimer(this);
                    }
                    Fragment fragment5 = this.mViewPagerAdapter.getFragment(TabViewModel.TAB_ALARM, false);
                    if (fragment5 instanceof AlarmClockFragment) {
                        ((AlarmClockFragment) fragment5).dismissSetAlarmDialog();
                    }
                }
            } catch (Exception e) {
                Log.e("shortcut error: " + e.getMessage());
            }
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Util.isTinyScreen(this.mActivity)) {
            return false;
        }
        if (this.mViewPager != null) {
            getMenuInflater().inflate(R.menu.immersion_menu, menu);
        }
        this.mAddClock = menu.findItem(R.id.add_clock);
        if (mCurrTab.equals(TabViewModel.TAB_CLOCK)) {
            this.mAddClock.setVisible(true);
        } else {
            this.mAddClock.setVisible(false);
        }
        if (MiuiSdk.isSupportSleep() && !PadAdapterUtil.IS_PAD) {
            menu.findItem(R.id.bedtime).setVisible(true);
        } else {
            menu.findItem(R.id.bedtime).setVisible(false);
        }
        if (LifePostUtils.isLifePostEnabled()) {
            menu.findItem(R.id.life_post).setVisible(true);
        } else {
            menu.findItem(R.id.life_post).setVisible(false);
        }
        if (PadAdapterUtil.IS_PAD && Build.VERSION.SDK_INT > 33) {
            menu.findItem(R.id.settings).setShowAsAction(2);
            menu.findItem(R.id.settings).setIcon(R.drawable.icon_settings);
        }
        menu.findItem(R.id.settings).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.add_clock /* 2131361894 */:
                if (this.mViewPagerAdapter.getFragment(mCurrTab, false) instanceof WorldClockFragment) {
                    Log.d(TAG, " onAddClockClick: ");
                    this.toTimeZoneSearchLauncher.launch(new Intent(this.mActivity, (Class<?>) TimezoneSearchBSActivity.class));
                    StatHelper.deskclockEvent(StatHelper.EVENT_CLICK_ADD_WORLD_CLOCK);
                    OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.CLOCK_FAB_ADD_CLICK);
                }
                break;
            case R.id.bedtime /* 2131361976 */:
                jumpToLockScreen();
                StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_CLICK2);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.MENU_BEDTIME_CLICK);
                if (!BedtimeUtil.bedTimeAlarmCompleted(DeskClockApp.getAppContext())) {
                    this.toBedtimeLauncher.launch(new Intent(this.mActivity, (Class<?>) BedtimeGuideActivity.class));
                } else {
                    startActivity(new Intent(this.mActivity, (Class<?>) BedtimeManageActivity.class));
                }
                break;
            case R.id.life_post /* 2131362327 */:
                startActivity(new Intent(this.mActivity, (Class<?>) LifePostSettingActivity.class));
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.MENU_LIFE_POST_CLICK);
                break;
            case R.id.settings /* 2131362733 */:
                jumpToLockScreen();
                Intent intent = new Intent(this.mActivity, (Class<?>) SettingsActivity.class);
                if (Math.abs(System.currentTimeMillis() - lastClickTime) > 1000 && PadAdapterUtil.IS_PAD) {
                    lastClickTime = System.currentTimeMillis();
                    startActivity(intent);
                    OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.MENU_SETTINGS_CLICK);
                } else if (!PadAdapterUtil.IS_PAD) {
                    startActivity(intent);
                    OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.MENU_SETTINGS_CLICK);
                }
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public Fragment getAlarmClockFragment() {
        return this.mViewPagerAdapter.getFragment(TabViewModel.TAB_ALARM, true);
    }

    public static String getPrimaryTabFromIntent(Intent intent) {
        String str = mCurrTab;
        if (intent == null) {
            return str;
        }
        int intExtra = intent.hasExtra(Util.NAVIGATION_TAB) ? intent.getIntExtra(Util.NAVIGATION_TAB, -1) : -1;
        Uri data = intent.getData();
        if (data != null) {
            String queryParameter = data.getQueryParameter("index");
            if ("alarmclock".equals(queryParameter)) {
                intExtra = 0;
            } else if ("worldclock".equals(queryParameter)) {
                intExtra = 1;
            } else if (NotificationCompat.CATEGORY_STOPWATCH.equals(queryParameter)) {
                intExtra = 2;
            } else if ("timer".equals(queryParameter)) {
                intExtra = 3;
            }
        }
        return intExtra != -1 ? TabViewModel.getTab(intExtra) : str;
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(CURRENT_TAB, mCurrTab);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        BroadcastReceiver broadcastReceiver = this.mReceiver;
        if (broadcastReceiver != null) {
            this.mActivity.unregisterReceiver(broadcastReceiver);
        }
        if (this.mViewPagerAdapter != null) {
            this.mViewPagerAdapter = null;
        }
        FabControllerNew.getInstance().destroy();
        DeskClockTabActivity.mKeyguardShortcutIndex = null;
        this.mHandler.removeCallbacks(this.mChangeFabTaskRunnable);
        super.onDestroyView();
    }

    @Override // miuix.appcompat.app.Fragment
    public void onUpdateArguments(Bundle bundle) {
        super.onUpdateArguments(bundle);
        DraggableViewPager draggableViewPager = this.mViewPager;
        if (draggableViewPager != null) {
            int i = bundle.getInt(ARG_PAGE, draggableViewPager.getCurrentItem());
            if (Util.isTinyScreen(this.mActivity)) {
                if (i == 3) {
                    i = 1;
                } else if (i == 1 || i == 2) {
                    i = 0;
                }
            }
            Log.d(TAG, "onUpdateArguments mViewPager.getCurrentItem() ：" + this.mViewPager.getCurrentItem() + "  page : " + i);
            Log.d(TAG, "onUpdateArguments ARG_PAGE ：" + bundle.getInt(ARG_PAGE, this.mViewPager.getCurrentItem()));
            DraggableViewPager draggableViewPager2 = this.mViewPager;
            draggableViewPager2.setCurrentItem(i, draggableViewPager2.isDraggable());
        }
    }

    @Override // miuix.navigator.NavigatorFragmentListener
    public void onNavigatorModeChanged(Navigator.Mode mode, Navigator.Mode mode2) {
        if (getView() != null) {
            Navigator.get(this);
            this.mViewPager.setDraggable(true);
            invalidateOptionsMenu();
        }
    }

    private void setupViewPager() {
        ActionBarContainer actionBarContainer;
        this.mActionBar = getActionBar();
        if (Util.isTinyScreen(this.mActivity)) {
            this.mActionBar.hide();
        } else {
            this.mActionBar.show();
        }
        this.mActionBar.setDisplayShowCustomEnabled(false);
        this.mActionBar.setDisplayShowTitleEnabled(true);
        this.mActionBar.setDisplayHomeAsUpEnabled(false);
        miuix.appcompat.app.ActionBar actionBar = this.mActionBar;
        if ((actionBar instanceof ActionBarImpl) && (actionBarContainer = ((ActionBarImpl) actionBar).getActionBarContainer()) != null) {
            actionBarContainer.setClickable(false);
        }
        final Navigator navigator = Navigator.get(this);
        this.mViewPager.setDraggable(true);
        invalidateOptionsMenu();
        this.mViewPagerAdapter = new DynamicFragmentPagerAdapter(this, mCurrTab);
        this.mViewPager.setOffscreenPageLimit(3);
        this.mViewPager.setAdapter(this.mViewPagerAdapter);
        if (getArguments() != null && getArguments().containsKey(ARG_PAGE)) {
            int i = getArguments().getInt(ARG_PAGE);
            this.mViewPager.setCurrentItem(i);
            miuix.appcompat.app.ActionBar actionBar2 = this.mActionBar;
            if (actionBar2 != null) {
                if (i == 0) {
                    actionBar2.setTitle(this.mPage1Name);
                    if (PadAdapterUtil.IS_PAD) {
                        this.mActionBar.setExpandState(0);
                        this.mActionBar.setResizable(false);
                    } else {
                        this.mActionBar.setExpandState(1);
                        this.mActionBar.setResizable(true);
                    }
                } else if (i == 1) {
                    actionBar2.setTitle(this.mPage2Name);
                    if (PadAdapterUtil.IS_PAD) {
                        this.mActionBar.setExpandState(0);
                        this.mActionBar.setResizable(false);
                    } else {
                        this.mActionBar.setExpandState(1);
                        this.mActionBar.setResizable(false);
                    }
                } else if (i == 2) {
                    actionBar2.setTitle(this.mPage3Name);
                    if (PadAdapterUtil.IS_PAD) {
                        this.mActionBar.setExpandState(0);
                        this.mActionBar.setResizable(false);
                    } else {
                        this.mActionBar.setExpandState(1);
                        this.mActionBar.setResizable(false);
                    }
                } else if (i == 3) {
                    actionBar2.setTitle(this.mPage4Name);
                    if (PadAdapterUtil.IS_PAD) {
                        this.mActionBar.setExpandState(0);
                        this.mActionBar.setResizable(false);
                    } else {
                        this.mActionBar.setExpandState(1);
                        this.mActionBar.setResizable(false);
                    }
                }
            }
        }
        this.mViewPager.setOnPageChangeListener(new OriginalViewPager.OnPageChangeListener() { // from class: com.android.deskclock.TabNavigatorContentFragment.6
            boolean isPageChanged;
            String leftTab;
            float offSet = 1.0f;
            boolean isHandScroll = false;
            boolean isHandUp = false;

            @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
            public void onPageScrolled(int i2, float f, int i3) {
                if (MiuiSdk.isLiteOrMiddleMode()) {
                    return;
                }
                if (this.isHandScroll || (this.isHandUp && !this.isPageChanged)) {
                    boolean z = TabNavigatorContentFragment.this.mViewPagerAdapter.getCount() == 2;
                    String tabAt = TabViewModel.getTabAt(i2, z);
                    this.leftTab = tabAt;
                    if (tabAt.equals(TabNavigatorContentFragment.mCurrTab)) {
                        this.offSet = 1.0f - f;
                    } else if (i2 == TabViewModel.getTabPosition(TabNavigatorContentFragment.mCurrTab, z) - 1) {
                        this.offSet = f;
                    } else {
                        this.offSet = 0.5f;
                    }
                    if (this.offSet < 0.5f) {
                        this.offSet = 0.5f;
                    }
                }
            }

            @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
            public void onPageSelected(int i2) {
                Log.d(TabNavigatorContentFragment.TAG, "position: " + i2);
                int i3 = (TabNavigatorContentFragment.this.getArguments() == null || !TabNavigatorContentFragment.this.getArguments().containsKey(TabNavigatorContentFragment.ARG_PAGE)) ? i2 : TabNavigatorContentFragment.this.getArguments().getInt(TabNavigatorContentFragment.ARG_PAGE);
                if (Util.isDeviceRuyiOrBixi()) {
                    if (TabNavigatorContentFragment.this.mViewPagerAdapter.getCount() == 4) {
                        if (i3 == 3 && i2 == 1) {
                            i2 = 3;
                        }
                    } else if (i3 == 1 || i3 == 2) {
                        i2 = 0;
                    }
                }
                Log.d(TabNavigatorContentFragment.TAG, "position: " + i2 + " page: " + i3 + "  mViewPagerAdapter.getCount(): " + TabNavigatorContentFragment.this.mViewPagerAdapter.getCount());
                String tabAt = TabViewModel.getTabAt(i2, TabNavigatorContentFragment.this.mViewPagerAdapter.getCount() == 2);
                FabControllerNew.getInstance().setCurrTab(tabAt);
                navigator.selectTab(i2);
                if (TabNavigatorContentFragment.this.mActionBar != null) {
                    if (i2 == 0) {
                        TabNavigatorContentFragment.this.mActionBar.setTitle(TabNavigatorContentFragment.this.mPage1Name);
                        if (TabNavigatorContentFragment.this.mAddClock != null) {
                            TabNavigatorContentFragment.this.mAddClock.setVisible(false);
                        }
                        if (TabNavigatorContentFragment.this.endView2 != null) {
                            TabNavigatorContentFragment.this.endView2.setVisibility(0);
                        }
                        if (!PadAdapterUtil.IS_PAD) {
                            TabNavigatorContentFragment.this.mActionBar.setResizable(true);
                        }
                    } else if (i2 == 1) {
                        TabNavigatorContentFragment.this.mActionBar.setTitle(TabNavigatorContentFragment.this.mPage2Name);
                        if (TabNavigatorContentFragment.this.mAddClock != null) {
                            TabNavigatorContentFragment.this.mAddClock.setVisible(true);
                        }
                        if (TabNavigatorContentFragment.this.endView2 != null) {
                            TabNavigatorContentFragment.this.endView2.setVisibility(8);
                        }
                        if (!PadAdapterUtil.IS_PAD) {
                            TabNavigatorContentFragment.this.mActionBar.setExpandState(1);
                            TabNavigatorContentFragment.this.mActionBar.setResizable(false);
                        }
                    } else if (i2 == 2) {
                        TabNavigatorContentFragment.this.mActionBar.setTitle(TabNavigatorContentFragment.this.mPage3Name);
                        if (TabNavigatorContentFragment.this.mAddClock != null) {
                            TabNavigatorContentFragment.this.mAddClock.setVisible(false);
                        }
                        if (!PadAdapterUtil.IS_PAD) {
                            TabNavigatorContentFragment.this.mActionBar.setExpandState(1);
                            TabNavigatorContentFragment.this.mActionBar.setResizable(false);
                        }
                    } else if (i2 == 3) {
                        TabNavigatorContentFragment.this.mActionBar.setTitle(TabNavigatorContentFragment.this.mPage4Name);
                        if (TabNavigatorContentFragment.this.mAddClock != null) {
                            TabNavigatorContentFragment.this.mAddClock.setVisible(false);
                        }
                        if (!PadAdapterUtil.IS_PAD) {
                            TabNavigatorContentFragment.this.mActionBar.setExpandState(1);
                            TabNavigatorContentFragment.this.mActionBar.setResizable(false);
                        }
                    }
                }
                TabNavigatorContentFragment.this.handleFragmentChange(TabNavigatorContentFragment.mCurrTab, tabAt);
                this.offSet = 1.0f;
                TabNavigatorContentFragment.this.mOldTab = TabNavigatorContentFragment.mCurrTab;
                TabNavigatorContentFragment.mCurrTab = tabAt;
                TabNavigatorContentFragment.this.mHandler.postDelayed(TabNavigatorContentFragment.this.mChangeFabTaskRunnable, 300L);
                TabNavigatorContentFragment.this.selectNavigationItem(true);
                TabNavigatorContentFragment.this.refreshKeepScreenOnState();
                StatHelper.recordTabSelected(i2);
                OneTrackStatHelper.recordTabView(i2);
            }

            @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i2) {
                if (i2 == 1) {
                    this.isPageChanged = false;
                    this.isHandScroll = true;
                    this.isHandUp = false;
                } else if (i2 == 2) {
                    this.isHandScroll = false;
                    this.isHandUp = true;
                } else {
                    this.isHandScroll = false;
                    this.isHandUp = false;
                }
            }
        });
        selectNavigationItem(true);
    }

    private void destroyActionMode() {
        if (this.mViewPagerAdapter == null) {
            return;
        }
        for (String str : Util.isTinyScreen(this.mActivity) ? TabViewModel.TINY_SCREEN_TABS : TabViewModel.TABS) {
            Fragment fragment = this.mViewPagerAdapter.getFragment(str, false);
            if (fragment instanceof BaseClockFragment) {
                ((BaseClockFragment) fragment).destroyActionMode();
            }
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        super.onResponsiveLayout(configuration, screenSpec, z);
        Log.d(TAG, "onResponsiveLayout--------------------------------------------------------------");
        resetActionBar();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        boolean z = AlarmHelper.get24HourMode();
        AlarmHelper.reset24HourMode(this.mActivity);
        if (z != AlarmHelper.get24HourMode()) {
            onTimeFormatChanged();
        }
    }

    public void resetActionBar() {
        if (this.mActionBar != null && PadAdapterUtil.IS_PAD) {
            this.mActionBar.setExpandState(0);
            this.mActionBar.setResizable(false);
        }
    }

    private void initActivityResultLauncher() {
        this.toTimeZoneSearchLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() { // from class: com.android.deskclock.TabNavigatorContentFragment.8
            @Override // androidx.activity.result.ActivityResultCallback
            public void onActivityResult(ActivityResult activityResult) {
                if (activityResult.getResultCode() == -1) {
                    String stringExtra = activityResult.getData().getStringExtra("android.intent.extra.TEXT");
                    Log.d(TabNavigatorContentFragment.TAG, "onActivityResult: " + stringExtra);
                    if (TabNavigatorContentFragment.this.mViewPagerAdapter == null) {
                        TabNavigatorContentFragment.this.handleTimezoneResult = true;
                        TabNavigatorContentFragment.this.timezoneCityId = stringExtra;
                    } else {
                        AlarmThreadPool.poolExecute(new TimeZoneSearchRunnable(TabNavigatorContentFragment.this.mViewPagerAdapter.getFragment(TabViewModel.TAB_CLOCK, false), stringExtra));
                    }
                }
            }
        });
        this.toBedtimeLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() { // from class: com.android.deskclock.TabNavigatorContentFragment.9
            @Override // androidx.activity.result.ActivityResultCallback
            public void onActivityResult(ActivityResult activityResult) {
                if (activityResult.getResultCode() != -1 || TabNavigatorContentFragment.this.mViewPagerAdapter == null) {
                    return;
                }
                Fragment fragment = TabNavigatorContentFragment.this.mViewPagerAdapter.getFragment(TabViewModel.TAB_ALARM, false);
                if (fragment instanceof AlarmClockFragment) {
                    ((AlarmClockFragment) fragment).toBedtimeActivityResult(activityResult.getResultCode());
                }
            }
        });
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.ShortcutsCallback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        DynamicFragmentPagerAdapter dynamicFragmentPagerAdapter;
        if (i == 4 && mCurrTab.equals(TabViewModel.TAB_ALARM) && (dynamicFragmentPagerAdapter = this.mViewPagerAdapter) != null) {
            Fragment fragment = dynamicFragmentPagerAdapter.getFragment(TabViewModel.TAB_ALARM, false);
            if ((fragment instanceof AlarmClockFragment) && ((AlarmClockFragment) fragment).cancelDialogView()) {
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void navigateToAlarm(TabNavigatorContentFragment tabNavigatorContentFragment) {
        if (this.mAlarmNavInfo == null) {
            this.mAlarmNavInfo = getUpdateFragmentNavInfoToAlarm();
        }
        Navigator.get(tabNavigatorContentFragment).navigate(this.mAlarmNavInfo);
    }

    public void navigateToClock(TabNavigatorContentFragment tabNavigatorContentFragment) {
        if (this.mClockNavInfo == null) {
            this.mClockNavInfo = getUpdateFragmentNavInfoToClock();
        }
        Navigator.get(tabNavigatorContentFragment).navigate(this.mClockNavInfo);
    }

    public void navigateToStopWatch(TabNavigatorContentFragment tabNavigatorContentFragment) {
        if (this.mStopWatchNavInfo == null) {
            this.mStopWatchNavInfo = getUpdateFragmentNavInfoToStopWatch();
        }
        Navigator.get(tabNavigatorContentFragment).navigate(this.mStopWatchNavInfo);
    }

    public void navigateToTimer(TabNavigatorContentFragment tabNavigatorContentFragment) {
        if (this.mTimerNavInfo == null) {
            this.mTimerNavInfo = getUpdateFragmentNavInfoToTimer();
        }
        Navigator.get(tabNavigatorContentFragment).navigate(this.mTimerNavInfo);
    }

    static class TimeZoneSearchRunnable implements Runnable {
        private String mCityId;
        private WeakReference<Fragment> mFragmentWeakRef;

        public TimeZoneSearchRunnable(Fragment fragment, String str) {
            this.mFragmentWeakRef = new WeakReference<>(fragment);
            this.mCityId = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            WeakReference<Fragment> weakReference = this.mFragmentWeakRef;
            Fragment fragment = weakReference == null ? null : weakReference.get();
            if (fragment == null || !(fragment instanceof WorldClockFragment)) {
                return;
            }
            ((WorldClockFragment) fragment).handleActivityResult(this.mCityId);
        }
    }
}
