package com.android.deskclock.stopwatch;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.TabNavigatorContentFragment;
import com.android.deskclock.base.BaseClockFragment;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.ResponsiveUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.fab.FabControllerNew;
import com.android.deskclock.util.fab.FabDataHelper;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.view.StopwatchChronometer;
import com.android.deskclock.view.list.AlarmRecyclerView;
import com.android.deskclock.view.tab.TabViewModel;
import com.android.deskclock.widget.TimerButton;
import java.lang.ref.WeakReference;
import java.util.List;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes.dex */
public class StopwatchFragment extends BaseClockFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "DC:StopwatchFragment";
    private static final long TIME_ANIM_DELAY = 100;
    private ViewGroup bottomButtonContainer;
    private LapModel.LapObserver fragmentObserver;
    private View llContent;
    private TimerButton mCenterBtn;
    private int mCurrentLapCount;
    private SharedPreferences.Editor mEditor;
    private long mElapsedTime;
    private StopwatchChronometer mElapsedTimeView;
    private TimerButton mEndBtn;
    private boolean mIsRunning;
    private LapAdapter mLapAdapter;
    private AlarmRecyclerView mLapLv;
    private LapModel mLapModel;
    private View mLapViewGroup;
    private long mLastElapsedTime;
    private SharedPreferences mSharedPref;
    private TimerButton mStartBtn;
    private StopWatchAnimHelper mStopWatchAnimHelper;
    private StopWatchService mStopWatchService;
    private StopWatchServiceCallback mStopWatchServiceCallbakc;
    private boolean mPendingShortCut = false;
    private final ServiceConnection connection = new ServiceConnection() { // from class: com.android.deskclock.stopwatch.StopwatchFragment.1
        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            StopwatchFragment.this.mStopWatchService = ((StopWatchService.StopWatchBinder) iBinder).getService();
            StopwatchFragment.this.mStopWatchService.registerListener(StopwatchFragment.this.mStopWatchServiceCallbakc);
            if (StopwatchFragment.this.mPendingShortCut) {
                StopwatchFragment.this.startFromShortcut();
            }
        }
    };

    @Override // miuix.appcompat.app.Fragment
    protected boolean isResponsiveEnabled() {
        return true;
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.d(TAG, "fragment onInflateView");
        this.mActivity = (DeskClockTabActivity) getActivity();
        this.mRootView = (ViewGroup) layoutInflater.inflate(R.layout.fragment_stopwatch, viewGroup, false);
        this.llContent = this.mRootView.findViewById(R.id.ll_content);
        this.bottomButtonContainer = (ViewGroup) this.mRootView.findViewById(R.id.bottomButtonContainer);
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext());
        this.mSharedPref = defaultSharedPreferences;
        this.mEditor = defaultSharedPreferences.edit();
        initStopWatchBtn();
        bindStopWatchService();
        return this.mRootView;
    }

    private void bindStopWatchService() {
        this.mStopWatchServiceCallbakc = new StopWatchServiceCallback(this);
        this.mActivity.bindService(new Intent(this.mActivity, (Class<?>) StopWatchService.class), this.connection, 1);
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IContentInsetState
    public void onContentInsetChanged(final Rect rect) {
        super.onContentInsetChanged(rect);
        if (this.mRootView != null) {
            this.mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.deskclock.stopwatch.StopwatchFragment.2
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    StopwatchFragment.this.mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    StopwatchFragment.this.updateRootViewPadding(rect.bottom);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRootViewPadding(int i) {
        if (this.mRootView == null) {
            return;
        }
        this.mRootView.setPadding(this.mRootView.getPaddingStart(), (!(getActionBar() instanceof ActionBarImpl) || ((ActionBarImpl) getActionBar()).getActionBarContainer() == null) ? 0 : ((ActionBarImpl) getActionBar()).getActionBarContainer().getMeasuredHeight(), this.mRootView.getPaddingEnd(), i);
    }

    @Override // com.android.deskclock.base.BaseClockFragment
    protected void initView() {
        float dimension;
        StopWatchServiceCallback stopWatchServiceCallback;
        super.initView();
        Log.d(TAG, "fragment initView");
        if (this.mInitialized) {
            return;
        }
        this.mCurrentLapCount = this.mSharedPref.getInt(LapModel.KEY_LAP_ITEM_COUNT, 0);
        this.mElapsedTimeView = (StopwatchChronometer) this.mRootView.findViewById(R.id.elapsed_time);
        this.mIsRunning = Util.getRunningState(this.mSharedPref);
        StopWatchAnimHelper stopWatchAnimHelper = new StopWatchAnimHelper(this.mRootView.findViewById(R.id.place_holder), this.mElapsedTimeView.getTimeView(), this.mElapsedTimeView.getTimeHourView());
        this.mStopWatchAnimHelper = stopWatchAnimHelper;
        SharedPreferences sharedPreferences = this.mSharedPref;
        stopWatchAnimHelper.resetUI(sharedPreferences != null && sharedPreferences.getInt(LapModel.KEY_LAP_ITEM_COUNT, 0) > 0);
        showElapsedTime();
        if (this.mIsRunning || this.mElapsedTime > 0) {
            initMarkList();
            setFreeLapListView();
        } else {
            this.mStopWatchAnimHelper.resetUI(false);
        }
        StopWatchService stopWatchService = this.mStopWatchService;
        if (stopWatchService != null && (stopWatchServiceCallback = this.mStopWatchServiceCallbakc) != null) {
            stopWatchService.registerListener(stopWatchServiceCallback);
        }
        if (this.mPendingShortCut) {
            startFromShortcut();
        }
        this.mInitialized = true;
        if (!TabViewModel.TAB_STOPWATCH.equals(TabNavigatorContentFragment.mCurrTab)) {
            this.mElapsedTimeView.stop();
        }
        if (PadAdapterUtil.IS_PAD && !Util.isFreeFormScreen(this.mActivity.getResources().getConfiguration()) && (!Util.isPadOrientationLand(this.mActivity) || !this.mActivity.isInMultiWindowMode())) {
            int dimension2 = (int) this.mActivity.getResources().getDimension(R.dimen.stopwatch_root_view_pad_margin_start);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mElapsedTimeView.getLayoutParams();
            layoutParams.setMarginStart(dimension2);
            layoutParams.setMarginEnd(dimension2);
            this.mElapsedTimeView.setLayoutParams(layoutParams);
            int dimension3 = (int) this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_holder_pad_height_large);
            if (Util.isTopBottomSplitScreen(this.mActivity)) {
                dimension3 = (int) this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_bs_split_screen_holder_height);
            }
            this.mStopWatchAnimHelper.resetOnConfigChanged(dimension3);
        } else if (Util.inExternalSplitScreen(this.mActivity) || Util.isFreeFormScreen(this.mActivity.getResources().getConfiguration())) {
            int dimension4 = (int) this.mActivity.getResources().getDimension(R.dimen.stopwatch_root_view_pad_margin_start);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mElapsedTimeView.getLayoutParams();
            layoutParams2.setMarginStart(dimension4);
            layoutParams2.setMarginEnd(dimension4);
            this.mElapsedTimeView.setLayoutParams(layoutParams2);
            if (Util.isTopBottomSplitScreen(this.mActivity)) {
                dimension = this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_holder_height_large_half);
            } else {
                dimension = this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_holder_split_screen_height);
            }
            this.mStopWatchAnimHelper.resetOnConfigChanged((int) dimension);
        } else if (Util.isInInternalScreen(this.mActivity) && !PadAdapterUtil.IS_PAD && !this.mActivity.isInMultiWindowMode()) {
            this.mStopWatchAnimHelper.resetOnConfigChanged((int) this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_holder_isInInternalScreen_height_large));
        }
        setButtonState();
    }

    protected void initStopWatchBtn() {
        this.mStartBtn = (TimerButton) this.mRootView.findViewById(R.id.start_btn);
        this.mEndBtn = (TimerButton) this.mRootView.findViewById(R.id.end_btn);
        this.mCenterBtn = (TimerButton) this.mRootView.findViewById(R.id.center_btn);
        FabControllerNew.getInstance().initStopWatchFabViewBtn(this.mStartBtn, this.mEndBtn, this.mCenterBtn);
        FabControllerNew.getInstance().setOnStopWatchFabClickListener(new StopWatchFabClickListenerImpl(this));
        FabControllerNew.getInstance().setStopWatchInitTab(TabViewModel.TAB_STOPWATCH);
        TimerButton timerButton = this.mStartBtn;
        if (timerButton == null || this.mEndBtn == null || this.mCenterBtn == null) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) timerButton.getLayoutParams();
        layoutParams.setMarginStart((int) (Util.isTinyScreen(this.mActivity) ? DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.fab_view_btn_margin_start_tiny) : 0.0f));
        this.mStartBtn.setLayoutParams(layoutParams);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mEndBtn.getLayoutParams();
        layoutParams2.setMarginEnd((int) (Util.isTinyScreen(this.mActivity) ? DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.fab_view_btn_margin_start_tiny) : 0.0f));
        this.mEndBtn.setLayoutParams(layoutParams2);
    }

    private void initMarkList() {
        Log.d(TAG, "fragment initMarkList");
        if (this.mLapViewGroup == null) {
            this.mLapViewGroup = ((ViewStub) this.mRootView.findViewById(R.id.list_stub)).inflate();
            int dimension = (int) ((!PadAdapterUtil.IS_PAD || this.mActivity.isInMultiWindowMode()) ? this.mActivity.getResources().getDimension(R.dimen.stopwatch_root_view_margin_start) : this.mActivity.getResources().getDimension(R.dimen.stopwatch_root_view_pad_margin_start));
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mLapViewGroup.getLayoutParams();
            layoutParams.setMarginStart(dimension);
            layoutParams.setMarginEnd(dimension);
            this.mLapViewGroup.setLayoutParams(layoutParams);
        }
        AlarmRecyclerView alarmRecyclerView = (AlarmRecyclerView) this.mLapViewGroup.findViewById(android.R.id.list);
        this.mLapLv = alarmRecyclerView;
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mLapLv.setVerticalScrollBarEnabled(true);
        LapAdapter lapAdapter = new LapAdapter(this.mActivity);
        this.mLapAdapter = lapAdapter;
        this.mLapLv.setAdapter(lapAdapter);
        this.mLapLv.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.android.deskclock.stopwatch.StopwatchFragment.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i != 1 || StopwatchFragment.this.mLapAdapter == null) {
                    return;
                }
                StopwatchFragment.this.mLapAdapter.setNeedAnimate(false);
            }
        });
        if (this.mLapModel == null) {
            this.mLapModel = LapModel.getInstance();
            LapModel.LapObserver lapObserver = new LapModel.LapObserver() { // from class: com.android.deskclock.stopwatch.StopwatchFragment.4
                @Override // com.android.deskclock.stopwatch.LapModel.LapObserver
                public void onLapLoaded(List<LapModel.LapBean> list) {
                    if (StopwatchFragment.this.mLapAdapter != null) {
                        StopwatchFragment.this.mLapAdapter.initData(list);
                        StopwatchFragment.this.notifyAdapterDataSetChanged();
                    }
                    StopwatchFragment.this.mStopWatchAnimHelper.resetUI(list != null && list.size() > 0);
                }

                @Override // com.android.deskclock.stopwatch.LapModel.LapObserver
                public void onLapChanged() {
                    if (StopwatchFragment.this.mLapAdapter != null) {
                        StopwatchFragment.this.notifyAdapterDataSetChanged();
                        StopwatchFragment.this.mStopWatchAnimHelper.anim(StopwatchFragment.this.mLapAdapter.getItemCount() > 0);
                    }
                }

                @Override // com.android.deskclock.stopwatch.LapModel.LapObserver
                public void onLastElapsedTimeGet(long j) {
                    StopwatchFragment.this.mLastElapsedTime = j;
                }
            };
            this.fragmentObserver = lapObserver;
            this.mLapModel.registerObserver(lapObserver);
        }
        this.mLapModel.startLoad();
    }

    private void showElapsedTime() {
        long baseTime = Util.getBaseTime(this.mSharedPref);
        if (this.mIsRunning && baseTime > System.currentTimeMillis()) {
            this.mIsRunning = false;
            Util.setRunningState(this.mEditor, false);
            this.mElapsedTime = 0L;
            resetTimer();
        }
        if (this.mIsRunning) {
            this.mElapsedTimeView.setBase(baseTime);
            this.mElapsedTimeView.start();
            this.mElapsedTime = System.currentTimeMillis() - baseTime;
        } else {
            this.mElapsedTime = Util.getElapsedTime(this.mSharedPref);
            this.mElapsedTimeView.setBase(System.currentTimeMillis() - this.mElapsedTime);
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mInitialized) {
            Util.setElapsedTime(this.mEditor, this.mElapsedTime);
        }
        StopwatchChronometer stopwatchChronometer = this.mElapsedTimeView;
        if (stopwatchChronometer != null) {
            stopwatchChronometer.stop();
        }
        this.mSharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override // com.android.deskclock.base.BaseClockFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mSharedPref.registerOnSharedPreferenceChangeListener(this);
        if (this.mInitialized) {
            showElapsedTime();
            if (TabViewModel.TAB_STOPWATCH.equals(TabNavigatorContentFragment.mCurrTab)) {
                return;
            }
            this.mElapsedTimeView.stop();
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        LapModel lapModel = this.mLapModel;
        if (lapModel != null) {
            lapModel.unregisterObserver(this.fragmentObserver);
            this.mLapModel = null;
        }
        StopWatchAnimHelper stopWatchAnimHelper = this.mStopWatchAnimHelper;
        if (stopWatchAnimHelper != null) {
            stopWatchAnimHelper.releaseAnim();
        }
        unBindService();
    }

    private void unBindService() {
        if (this.mStopWatchService != null) {
            this.mActivity.unbindService(this.connection);
            StopWatchService stopWatchService = this.mStopWatchService;
            if (stopWatchService != null) {
                stopWatchService.unregisterListener(this.mStopWatchServiceCallbakc);
                this.mStopWatchServiceCallbakc = null;
            }
            this.mStopWatchService = null;
        }
    }

    private void startTimer() {
        if (!this.mInitialized) {
            initView();
        }
        initMarkList();
        setFreeLapListView();
        if (this.mStopWatchService != null) {
            this.mActivity.startService(new Intent(this.mActivity, (Class<?>) StopWatchService.class));
            this.mStopWatchService.startTimer();
        }
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.STOPWATCH_FAB_START);
    }

    private void pauseTimer() {
        LapAdapter lapAdapter = this.mLapAdapter;
        if (lapAdapter != null) {
            lapAdapter.setNeedAnimate(false);
        }
        StopWatchService stopWatchService = this.mStopWatchService;
        if (stopWatchService != null) {
            stopWatchService.pauseTimer();
        }
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.STOPWATCH_FAB_PAUSE);
    }

    private void continueTimer() {
        LapAdapter lapAdapter = this.mLapAdapter;
        if (lapAdapter != null) {
            lapAdapter.setNeedAnimate(false);
        }
        StopWatchService stopWatchService = this.mStopWatchService;
        if (stopWatchService != null) {
            stopWatchService.continueTimer();
        }
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.STOPWATCH_FAB_CONTINUE);
    }

    private void resetTimer() {
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.STOPWATCH_FAB_RESET);
        cancelListAnim();
        StopWatchService stopWatchService = this.mStopWatchService;
        if (stopWatchService != null) {
            stopWatchService.resetTimer();
        }
    }

    private void cancelListAnim() {
        LapAdapter lapAdapter;
        AlarmRecyclerView alarmRecyclerView = this.mLapLv;
        if (alarmRecyclerView == null || alarmRecyclerView.getChildAt(0) == null || (lapAdapter = this.mLapAdapter) == null) {
            return;
        }
        lapAdapter.cancelListAnim(this.mLapLv.getChildAt(0));
    }

    private void lapTimer() {
        LapAdapter lapAdapter = this.mLapAdapter;
        if (lapAdapter != null) {
            lapAdapter.setNeedAnimate(true);
        }
        StopWatchService stopWatchService = this.mStopWatchService;
        if (stopWatchService != null) {
            stopWatchService.lapTimer();
        }
        AlarmRecyclerView alarmRecyclerView = this.mLapLv;
        if (alarmRecyclerView != null && alarmRecyclerView.getLayoutManager() != null) {
            this.mLapLv.getLayoutManager().scrollToPosition(0);
        }
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.STOPWATCH_FAB_LAP);
    }

    private void startOrContinueTimer() {
        if (!this.mInitialized) {
            initView();
        }
        initMarkList();
        setFreeLapListView();
        this.mIsRunning = true;
        this.mElapsedTimeView.setBase(System.currentTimeMillis() - this.mElapsedTime);
        this.mElapsedTimeView.start();
        Util.setBaseTime(this.mEditor, this.mElapsedTimeView.getBase());
        Util.setRunningState(this.mEditor, true);
        ((TabNavigatorContentFragment) getParentFragment()).refreshKeepScreenOnState();
    }

    public void startFromShortcut() {
        this.mPendingShortCut = true;
        if (this.mStopWatchService == null || !this.mInitialized) {
            return;
        }
        if (!this.mIsRunning) {
            if (this.mElapsedTime == 0) {
                startTimer();
            } else {
                continueTimer();
            }
        }
        this.mPendingShortCut = false;
    }

    private void setButtonState() {
        if (this.mIsRunning) {
            FabDataHelper.getInstance().changeFabState(TabViewModel.TAB_STOPWATCH, 1);
        } else if (this.mElapsedTime == 0) {
            FabDataHelper.getInstance().changeFabState(TabViewModel.TAB_STOPWATCH, 0);
        } else {
            FabDataHelper.getInstance().changeFabState(TabViewModel.TAB_STOPWATCH, 2);
        }
    }

    private int getLapSize() {
        LapAdapter lapAdapter = this.mLapAdapter;
        if (lapAdapter != null) {
            return lapAdapter.getItemCount();
        }
        return 0;
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        FabControllerNew.getInstance().resetAnimChangeFlag();
        if (this.mInitialized && this.mIsRunning) {
            showElapsedTime();
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFragmentChange
    public void onEnter() {
        super.onEnter();
        Log.d(TAG, "onEnter: ");
        LapAdapter lapAdapter = this.mLapAdapter;
        if (lapAdapter != null) {
            lapAdapter.setNeedAnimate(false);
        }
        if (this.mInitialized && this.mIsRunning) {
            showElapsedTime();
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFragmentChange
    public void onLeave() {
        super.onLeave();
        Log.d(TAG, "StopwatchFragment onLeave: ");
        LapAdapter lapAdapter = this.mLapAdapter;
        if (lapAdapter != null) {
            lapAdapter.setNeedAnimate(false);
        }
        if (this.mInitialized && this.mIsRunning) {
            this.mElapsedTimeView.stop();
        }
    }

    public void notifyAdapterDataSetChanged() {
        LapAdapter lapAdapter = this.mLapAdapter;
        if (lapAdapter == null) {
            return;
        }
        lapAdapter.notifyDataSetChanged();
        this.mLapViewGroup.setVisibility(getLapSize() == 0 ? 8 : 0);
    }

    @Override // miuix.appcompat.app.Fragment, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        float dimension;
        super.onResponsiveLayout(configuration, screenSpec, z);
        if (this.mInitialized) {
            TimerButton timerButton = this.mStartBtn;
            if (timerButton != null && this.mEndBtn != null && this.mCenterBtn != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) timerButton.getLayoutParams();
                layoutParams.setMarginStart((int) (Util.isTinyScreen(this.mActivity) ? DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.fab_view_btn_margin_start_tiny) : 0.0f));
                this.mStartBtn.setLayoutParams(layoutParams);
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mEndBtn.getLayoutParams();
                layoutParams2.setMarginEnd((int) (Util.isTinyScreen(this.mActivity) ? DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.fab_view_btn_margin_start_tiny) : 0.0f));
                this.mEndBtn.setLayoutParams(layoutParams2);
            }
            int dimension2 = (int) ((!PadAdapterUtil.IS_PAD || this.mActivity.isInMultiWindowMode()) ? this.mActivity.getResources().getDimension(R.dimen.stopwatch_root_view_margin_start) : this.mActivity.getResources().getDimension(R.dimen.stopwatch_root_view_pad_margin_start));
            LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.mElapsedTimeView.getLayoutParams();
            layoutParams3.setMarginStart(dimension2);
            layoutParams3.setMarginEnd(dimension2);
            this.mElapsedTimeView.setLayoutParams(layoutParams3);
            View view = this.mLapViewGroup;
            if (view != null) {
                LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) view.getLayoutParams();
                layoutParams4.setMarginStart(dimension2);
                layoutParams4.setMarginEnd(dimension2);
                this.mLapViewGroup.setLayoutParams(layoutParams4);
            }
            if (PadAdapterUtil.IS_PAD && Util.isTopBottomSplitScreen(this.mActivity)) {
                dimension = this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_bs_split_screen_holder_height);
            } else if (PadAdapterUtil.IS_PAD && !ResponsiveUtil.inFreeFormWindow(screenSpec) && (!Util.isPadOrientationLand(this.mActivity) || !this.mActivity.isInMultiWindowMode())) {
                dimension = this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_holder_pad_height_large);
            } else if ((ResponsiveUtil.inOneThird(screenSpec) || (ResponsiveUtil.inHalfMode(screenSpec) && !Util.isInInternalScreen(this.mActivity))) && !PadAdapterUtil.IS_PAD) {
                dimension = this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_holder_height_large_half);
            } else if ((ResponsiveUtil.inFreeFormWindow(screenSpec) || ResponsiveUtil.inExternalSplitScreen(screenSpec, DeskClockApp.getAppContext())) && !PadAdapterUtil.IS_PAD) {
                dimension = this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_holder_height_large_two_third);
            } else if (Util.isInInternalScreen(this.mActivity) && !PadAdapterUtil.IS_PAD && !this.mActivity.isInMultiWindowMode()) {
                dimension = this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_holder_isInInternalScreen_height_large);
            } else {
                dimension = this.mActivity.getResources().getDimension(R.dimen.stopwatch_view_holder_height_large);
            }
            int i = (int) dimension;
            initMarkList();
            resetLapListView(screenSpec);
            this.mStopWatchAnimHelper.resetOnConfigChanged(i);
            if (((Util.isFoldDevice(this.mActivity) && !Util.isInInternalScreen(this.mActivity)) || Util.isPhoneDevice(this.mActivity)) && ResponsiveUtil.inOneThird(screenSpec)) {
                ViewGroup viewGroup = this.bottomButtonContainer;
                if (viewGroup != null) {
                    viewGroup.setVisibility(8);
                    return;
                }
                return;
            }
            ViewGroup viewGroup2 = this.bottomButtonContainer;
            if (viewGroup2 != null) {
                viewGroup2.setVisibility(0);
            }
        }
    }

    private void resetLapListView(ScreenSpec screenSpec) {
        int dimension = (int) this.mActivity.getResources().getDimension(R.dimen.stopwatch_lap_list_margin_top);
        if (this.mLapLv == null || this.mLapViewGroup == null) {
            return;
        }
        if (!Util.isInInternalScreen(this.mActivity) && (ResponsiveUtil.inHalfMode(screenSpec) || ResponsiveUtil.inTwoThird(screenSpec))) {
            float dimension2 = this.mActivity.getResources().getDimension(R.dimen.stopwatch_list_view_margin_top);
            dimension = (int) dimension2;
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mLapViewGroup.getLayoutParams();
        layoutParams.topMargin = dimension;
        this.mLapViewGroup.setLayoutParams(layoutParams);
    }

    private void setFreeLapListView() {
        int dimension = (int) this.mActivity.getResources().getDimension(R.dimen.stopwatch_lap_list_margin_top);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mLapViewGroup.getLayoutParams();
        layoutParams.topMargin = dimension;
        this.mLapViewGroup.setLayoutParams(layoutParams);
    }

    @Override // com.android.deskclock.base.BaseClockFragment
    protected String getTab() {
        return TabViewModel.TAB_STOPWATCH;
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (Util.CURRENT_TIME_MILLS_OFFSET.equals(str) && this.mInitialized) {
            showElapsedTime();
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public boolean shouldKeepScreenOn() {
        return this.mIsRunning;
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onStartClick(View view) {
        super.onStartClick(view);
        if (!this.mInitialized) {
            initView();
        }
        if (this.mIsRunning) {
            lapTimer();
        } else if (this.mElapsedTime != 0) {
            resetTimer();
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onCenterClick(View view) {
        super.onCenterClick(view);
        if (!this.mInitialized) {
            initView();
        }
        startTimer();
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onEndClick(View view) {
        super.onEndClick(view);
        if (!this.mInitialized) {
            initView();
        }
        if (this.mIsRunning) {
            pauseTimer();
        } else {
            continueTimer();
        }
    }

    static class StopWatchFabClickListenerImpl implements FabControllerNew.onStopWatchFabClickListener {
        private WeakReference<StopwatchFragment> mWeakReference;

        public StopWatchFabClickListenerImpl(StopwatchFragment stopwatchFragment) {
            this.mWeakReference = new WeakReference<>(stopwatchFragment);
        }

        @Override // com.android.deskclock.util.fab.FabControllerNew.onStopWatchFabClickListener
        public void onStartFabClick(View view) {
            WeakReference<StopwatchFragment> weakReference = this.mWeakReference;
            StopwatchFragment stopwatchFragment = weakReference == null ? null : weakReference.get();
            if (stopwatchFragment != null) {
                stopwatchFragment.onStartClick(view);
            }
        }

        @Override // com.android.deskclock.util.fab.FabControllerNew.onStopWatchFabClickListener
        public void onEndFabClick(View view) {
            WeakReference<StopwatchFragment> weakReference = this.mWeakReference;
            StopwatchFragment stopwatchFragment = weakReference == null ? null : weakReference.get();
            if (stopwatchFragment != null) {
                stopwatchFragment.onEndClick(view);
            }
        }

        @Override // com.android.deskclock.util.fab.FabControllerNew.onStopWatchFabClickListener
        public void onCenterFabClick(View view) {
            WeakReference<StopwatchFragment> weakReference = this.mWeakReference;
            StopwatchFragment stopwatchFragment = weakReference == null ? null : weakReference.get();
            if (stopwatchFragment != null) {
                stopwatchFragment.onCenterClick(view);
            }
        }
    }

    public void onStopWatchUpdate(long j, boolean z, boolean z2, long j2) {
        StopwatchChronometer stopwatchChronometer = this.mElapsedTimeView;
        if (stopwatchChronometer == null) {
            return;
        }
        if (z2) {
            this.mIsRunning = z;
            stopwatchChronometer.stop();
            this.mElapsedTimeView.resetTimeViewContentDescription();
            this.mElapsedTimeView.setBase(System.currentTimeMillis());
            Util.setElapsedTime(this.mEditor, 0L);
            Util.setElapsedTime(this.mEditor, j);
        } else {
            this.mIsRunning = z;
            if (z && this.mElapsedTime > 0) {
                initMarkList();
                setFreeLapListView();
            }
            this.mElapsedTimeView.setBase(System.currentTimeMillis() - j);
            if (z) {
                this.mElapsedTimeView.start();
                Util.setBaseTime(this.mEditor, this.mElapsedTimeView.getBase());
            } else {
                this.mElapsedTimeView.stop();
            }
            Util.setElapsedTime(this.mEditor, j);
        }
        this.mElapsedTime = j;
        this.mLastElapsedTime = j2;
        Util.setRunningState(this.mEditor, z);
        updateFabStat(z, j);
        if (getParentFragment() != null) {
            ((TabNavigatorContentFragment) getParentFragment()).refreshKeepScreenOnState();
        }
    }

    private void updateFabStat(boolean z, long j) {
        if (z) {
            FabDataHelper.getInstance().changeFabState(TabViewModel.TAB_STOPWATCH, 1);
        } else {
            FabDataHelper.getInstance().changeFabState(TabViewModel.TAB_STOPWATCH, j > 0 ? 2 : 0);
        }
    }

    private static class StopWatchServiceCallback implements StopWatchService.StopWatchListener {
        private WeakReference<StopwatchFragment> mReference;

        public StopWatchServiceCallback(StopwatchFragment stopwatchFragment) {
            this.mReference = new WeakReference<>(stopwatchFragment);
        }

        @Override // com.android.deskclock.stopwatch.StopWatchService.StopWatchListener
        public void onStopWatchUpdate(StopwatchModel stopwatchModel) {
            WeakReference<StopwatchFragment> weakReference = this.mReference;
            StopwatchFragment stopwatchFragment = weakReference != null ? weakReference.get() : null;
            if (stopwatchFragment == null) {
                return;
            }
            stopwatchFragment.onStopWatchUpdate(stopwatchModel.getElapsedTime(), stopwatchModel.isRunning(), stopwatchModel.isRest(), stopwatchModel.getLastElapsedTime());
        }
    }
}
