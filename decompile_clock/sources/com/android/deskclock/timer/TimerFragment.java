package com.android.deskclock.timer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.R2;
import com.android.deskclock.TabNavigatorContentFragment;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.base.BaseClockFragment;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.ResponsiveUtil;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.fab.FabControllerNew;
import com.android.deskclock.util.fab.FabDataHelper;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.util.themeringtone.RingtoneHelper;
import com.android.deskclock.view.drawable.SegmentDialProgressDrawable;
import com.android.deskclock.view.tab.TabViewModel;
import com.android.deskclock.widget.TimePickerForTimer;
import com.android.deskclock.widget.TimerButton;
import java.lang.ref.WeakReference;
import java.util.List;
import miuix.animation.Folme;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.core.util.MiuixUIUtils;
import miuix.recyclerview.widget.RecyclerView;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes.dex */
public class TimerFragment extends BaseClockFragment implements View.OnClickListener, TimePickerForTimer.OnTimeChangedListener, TimePickerForTimer.OnTimerScrollListener, TimerHistoryAdapter.onDataListChangedListener {
    private static final int GUIDE_WINDOW_SHOW_DURATION = 3000;
    private static final String TAG = "DC:TimerFragment";
    private static final String TIMER_TIME_FORMAT = "%02d:%02d";
    private static final String TIMER_TIME_FORMAT_HOUR = "%02d:%02d:%02d";
    private static final int TIME_PICKER_COUNT = 5;
    private static final int TIME_PICKER_COUNT_TINY_SCREEN = 3;
    private float bgRadius;
    private ViewGroup bottomButtonContainer;
    private int highLightSize;
    private int hintSize;
    private TimerButton mCenterBtn;
    private ViewStub mCircleViewStub;
    private TimerProgressBgView mClockCircleBgView;
    private TimerProgressView mClockCircleView;
    private TimerButton mEndBtn;
    private int mFontLevel;
    private Intent mServiceIntent;
    private TimerButton mStartBtn;
    private RelativeLayout mTimerCircleLayout;
    private TextView mTimerDisplay;
    private TextView mTimerDuration;
    private TimerHistoryAdapter mTimerHistoryAdapter;
    private RecyclerView mTimerHistoryView;
    private TextView mTimerLabel;
    private TimerModel mTimerModel;
    private TimePickerForTimer mTimerPicker;
    private FrameLayout mTimerPickerContainer;
    private LinearLayout mTimerScreenOn;
    private ImageView mTimerScreenOnImage;
    private TextView mTimerScreenOnText;
    private TimerService mTimerService;
    private TimerServiceCallback mTimerServiceCallback;
    private TimerServiceConnection mTimerServiceConnection;
    private Toast mToast;
    private VirtualTimerAnimView mVirtualAnimView;
    private View progressContent;
    private float progressTopPadding;
    private int mTimerCircleOffsetTopForMultiWindow = 0;
    private boolean mContinueTimerUpdate = false;
    private int mHour = 0;
    private int mMinute = 5;
    private int mSecond = 0;
    private Timer mTimer = new Timer();
    private boolean mIsCircleLayoutShowing = false;
    private boolean mTimerCircleLayoutIsNullOnChangeScreen = false;
    private boolean mPendingShortCut = false;
    private Handler mHandler = null;
    private boolean mIsEnable = true;
    private Runnable mSetEnableStateRunnable = new Runnable() { // from class: com.android.deskclock.timer.TimerFragment.8
        @Override // java.lang.Runnable
        public void run() {
            boolean z = (TimerFragment.this.mHour == 0 && TimerFragment.this.mMinute == 0 && TimerFragment.this.mSecond == 0) ? false : true;
            if (TimerFragment.this.mIsEnable != z) {
                TimerFragment.this.mIsEnable = z;
                FabDataHelper.getInstance().setEnableState(TabViewModel.TAB_TIMER, TimerFragment.this.mIsEnable);
            }
        }
    };

    @Override // miuix.appcompat.app.Fragment
    protected boolean isResponsiveEnabled() {
        return true;
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mActivity = (DeskClockTabActivity) getActivity();
        this.mRootView = (ViewGroup) layoutInflater.inflate(R.layout.fragment_timer, viewGroup, false);
        this.mHandler = new Handler();
        if (this.mTimerModel == null) {
            this.mTimerModel = new TimerModel(this.mActivity.getApplicationContext(), new TimerObserverImp(this));
        }
        this.mFontLevel = MiuixUIUtils.getFontLevel(DeskClockApp.getAppContext());
        bindTimerService();
        initTimerBtn();
        return this.mRootView;
    }

    private void bindTimerService() {
        this.mTimerServiceCallback = new TimerServiceCallback(this);
        this.mTimerServiceConnection = new TimerServiceConnection();
        this.mActivity.bindService(new Intent(this.mActivity, (Class<?>) TimerService.class), this.mTimerServiceConnection, 1);
    }

    @Override // com.android.deskclock.base.BaseClockFragment
    protected void initView() {
        TextView textView;
        TimerServiceCallback timerServiceCallback;
        super.initView();
        if (this.mInitialized) {
            return;
        }
        this.bottomButtonContainer = (ViewGroup) this.mRootView.findViewById(R.id.bottomButtonContainer);
        this.mTimerPicker = (TimePickerForTimer) this.mRootView.findViewById(R.id.time_picker);
        this.mTimerPickerContainer = (FrameLayout) this.mRootView.findViewById(R.id.time_picker_container);
        setTimerPickerCount();
        this.mTimerPicker.setOnTimeChangedListener(this);
        this.mTimerPicker.setOnTimerScrollListener(this);
        initTimerHistory();
        if (!MiuiSdk.isLiteOrMiddleMode()) {
            VirtualTimerAnimView virtualTimerAnimView = (VirtualTimerAnimView) ((ViewStub) this.mRootView.findViewById(R.id.virtual_anim_holder_stub)).inflate();
            this.mVirtualAnimView = virtualTimerAnimView;
            virtualTimerAnimView.setVisibility(8);
        }
        ViewStub viewStub = (ViewStub) this.mRootView.findViewById(R.id.timer_circle_view_stub);
        this.mCircleViewStub = viewStub;
        viewStub.setVisibility(8);
        this.mIsCircleLayoutShowing = false;
        this.mTimer = TimerDao.getTimer(this.mActivity.getApplicationContext());
        updateView();
        updateTimerPickerValue();
        updateViewWithState(true);
        TimerService timerService = this.mTimerService;
        if (timerService != null && (timerServiceCallback = this.mTimerServiceCallback) != null) {
            timerService.registerCallListener(timerServiceCallback);
        }
        this.mContinueTimerUpdate = true;
        this.mInitialized = true;
        if (this.mPendingShortCut) {
            startFromShortcut();
        }
        if (this.mTimer.getState() == 1 || ((textView = this.mTimerDisplay) != null && textView.getVisibility() == 0)) {
            this.mTimerPicker.setVisibility(8);
        } else {
            this.mTimerPicker.setVisibility(0);
        }
        setItemBackground(0);
    }

    private void updateView() {
        if (!isAdded() || this.mActivity == null) {
            return;
        }
        resetWidth();
        int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_height);
        this.bgRadius = getResources().getDimensionPixelOffset(R.dimen.timer_progress_bg_radius);
        this.progressTopPadding = getResources().getDimensionPixelOffset(R.dimen.timer_progress_topPadding);
        this.highLightSize = (int) getResources().getDimension(R.dimen.timer_number_picker_highlight_size);
        this.hintSize = (int) getResources().getDimension(R.dimen.timer_number_picker_hint_size);
        int dimensionPixelOffset2 = getResources().getDimensionPixelOffset(R.dimen.timer_screen_on_top_margin);
        if (PadAdapterUtil.IS_PAD) {
            if (Util.isSmallPad()) {
                if (!Util.isInMultiWindowMode(this.mActivity)) {
                    if (Util.isPadOrientationLand(DeskClockApp.getAppDEContext())) {
                        this.progressTopPadding = 0.0f;
                        dimensionPixelOffset = MiuixUIUtils.isTallFontLang(getContext()) ? getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_small_pad_land_height_tall) : getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_small_pad_land_height);
                    }
                } else {
                    this.bgRadius = getResources().getDimensionPixelOffset(R.dimen.timer_progress_bg_radius_small_pad_multi);
                    this.progressTopPadding = 0.0f;
                }
            } else if (!Util.isInMultiWindowMode(this.mActivity)) {
                if (Util.isOrientationPortrait(DeskClockApp.getAppDEContext())) {
                    this.progressTopPadding = getResources().getDimensionPixelOffset(R.dimen.timer_progress_topPadding_pad_portrait);
                } else {
                    this.bgRadius = getResources().getDimensionPixelOffset(R.dimen.timer_progress_bg_radius_pad_land);
                }
            }
        } else if (Util.isFoldDevice(getContext()) && Util.isInInternalScreen(this.mActivity) && !Util.isInMultiWindowMode(this.mActivity)) {
            if (Util.isFoldOrientationLand(DeskClockApp.getAppDEContext())) {
                this.progressTopPadding = 0.0f;
                this.bgRadius = getResources().getDimensionPixelOffset(R.dimen.timer_progress_bg_radius_fold_in_land);
                dimensionPixelOffset = MiuixUIUtils.isTallFontLang(getContext()) ? getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_height_fold_in_land_tall) : getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_height_fold_in_land);
            } else {
                dimensionPixelOffset = MiuixUIUtils.isTallFontLang(getContext()) ? getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_height_fold_in_portrait_tall) : getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_height_fold_in_portrait);
            }
        } else if (Util.isTinyScreen(getContext())) {
            dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_tiny_height);
            this.progressTopPadding = 0.0f;
            this.bgRadius = getResources().getDimensionPixelOffset(R.dimen.timer_progress_bg_radius_tiny);
            this.highLightSize = (int) this.mActivity.getResources().getDimension(R.dimen.timer_number_picker_tiny_highlight_size);
            this.hintSize = (int) this.mActivity.getResources().getDimension(R.dimen.timer_number_picker_tiny_hint_size);
        } else if (Util.isInMultiWindowMode(this.mActivity)) {
            if (MiuixUIUtils.isTallFontLang(getContext())) {
                dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_height_multi_tall_font);
                this.bgRadius = getResources().getDimensionPixelOffset(R.dimen.timer_progress_bg_radius_multi_tall_font);
                this.progressTopPadding = 0.0f;
                this.highLightSize = (int) this.mActivity.getResources().getDimension(R.dimen.timer_number_picker_highlight_size_multi_tall_font);
                this.hintSize = (int) this.mActivity.getResources().getDimension(R.dimen.timer_number_picker_hint_size_multi_tall_font);
                dimensionPixelOffset2 = getResources().getDimensionPixelOffset(R.dimen.timer_screen_on_top_margin_multi_tall_font);
            } else {
                dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_height_multi);
                this.bgRadius = getResources().getDimensionPixelOffset(R.dimen.timer_progress_bg_radius_multi);
                this.progressTopPadding = 0.0f;
                this.highLightSize = (int) this.mActivity.getResources().getDimension(R.dimen.timer_number_picker_highlight_size_multi);
                this.hintSize = (int) this.mActivity.getResources().getDimension(R.dimen.timer_number_picker_hint_size_multi);
            }
        } else if (MiuixUIUtils.isTallFontLang(this.mActivity)) {
            dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.timer_number_picker_height_phone_large_font_and_tall);
        }
        TimePickerForTimer timePickerForTimer = this.mTimerPicker;
        if (timePickerForTimer != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) timePickerForTimer.getLayoutParams();
            layoutParams.height = dimensionPixelOffset;
            this.mTimerPicker.setLayoutParams(layoutParams);
            this.mTimerPicker.setTextSize(this.highLightSize, this.hintSize);
        }
        VirtualTimerAnimView virtualTimerAnimView = this.mVirtualAnimView;
        if (virtualTimerAnimView != null) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) virtualTimerAnimView.getLayoutParams();
            layoutParams2.height = dimensionPixelOffset;
            this.mVirtualAnimView.setLayoutParams(layoutParams2);
            this.mVirtualAnimView.setSize(this.highLightSize);
        }
        TimerProgressBgView timerProgressBgView = this.mClockCircleBgView;
        if (timerProgressBgView != null) {
            timerProgressBgView.setPadding(0, (int) this.progressTopPadding, 0, 0);
            this.mClockCircleBgView.setProgressBgRadius(this.bgRadius);
        }
        TextView textView = this.mTimerDisplay;
        if (textView != null) {
            textView.setTextSize(0, this.highLightSize);
        }
        LinearLayout linearLayout = this.mTimerScreenOn;
        if (linearLayout != null) {
            LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams3.topMargin = dimensionPixelOffset2;
            this.mTimerScreenOn.setLayoutParams(layoutParams3);
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IContentInsetState
    public void onContentInsetChanged(Rect rect) {
        int dimensionPixelOffset;
        super.onContentInsetChanged(rect);
        if (!(getActionBar() instanceof ActionBarImpl) || ((ActionBarImpl) getActionBar()).getActionBarContainer() == null) {
            dimensionPixelOffset = 0;
        } else {
            dimensionPixelOffset = ((ActionBarImpl) getActionBar()).getActionBarContainer().getMeasuredHeight();
            Log.d(TAG, "onContentInsetChanged height: " + dimensionPixelOffset);
        }
        if (this.mRootView != null) {
            if (Util.isTinyScreen(this.mActivity)) {
                dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.timer_tiny_top_space);
            }
            this.mRootView.setPadding(this.mRootView.getPaddingStart(), dimensionPixelOffset, this.mRootView.getPaddingEnd(), rect.bottom);
        }
    }

    protected void initTimerBtn() {
        this.mStartBtn = (TimerButton) this.mRootView.findViewById(R.id.start_btn);
        this.mEndBtn = (TimerButton) this.mRootView.findViewById(R.id.end_btn);
        this.mCenterBtn = (TimerButton) this.mRootView.findViewById(R.id.center_btn);
        FabControllerNew.getInstance().initTimerFabViewBtn(this.mStartBtn, this.mEndBtn, this.mCenterBtn);
        FabControllerNew.getInstance().setOnTimerFabClickListener(new TimerFabClickListenerImpl(this));
        FabControllerNew.getInstance().setTimerInitTab(TabViewModel.TAB_TIMER);
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

    private void initTimerHistory() {
        this.mTimerModel.startLoad();
        int size = this.mTimerModel.getTimers().size();
        if (this.mTimerHistoryView == null) {
            this.mTimerHistoryView = (RecyclerView) this.mRootView.findViewById(R.id.timer_history_view);
            TimerHistoryAdapter timerHistoryAdapter = new TimerHistoryAdapter(this.mActivity);
            this.mTimerHistoryAdapter = timerHistoryAdapter;
            timerHistoryAdapter.setOnDataListChangedListener(this);
            setGridLayoutManagerBySize(size);
            this.mTimerHistoryView.setAdapter(this.mTimerHistoryAdapter);
            this.mTimerHistoryAdapter.setOnItemClickListener(new TimerHistoryAdapter.OnItemClickListener() { // from class: com.android.deskclock.timer.TimerFragment.1
                @Override // com.android.deskclock.timer.TimerHistoryAdapter.OnItemClickListener
                public void onTimerHistoryItemClick(int i, int i2) {
                    TimerFragment.this.mTimerPicker.stopScroll();
                    TimerFragment.this.quickSetTimerWithAnim(i2);
                }
            });
        } else {
            setGridLayoutManagerBySize(size);
            if (Util.isFreeFormScreen(DeskClockApp.getAppContext().getResources().getConfiguration())) {
                this.mTimerHistoryView.setVisibility(8);
            }
        }
        if (this.mTimerHistoryView != null && this.mActivity.isInMultiWindowMode() && Util.isTinyScreen(this.mActivity)) {
            this.mTimerHistoryView.setVisibility(8);
        }
        setTimerHistoryLayout();
    }

    private void setGridLayoutManagerBySize(int i) {
        int i2;
        if (i == 1 || i == 2 || !(this.mFontLevel != 2 || Util.isPadOrientationLand(this.mActivity) || isFoldInternalScreen())) {
            i2 = i < 2 ? 1 : 2;
        } else {
            i2 = 3;
        }
        RecyclerView recyclerView = this.mTimerHistoryView;
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new GridLayoutManager(this.mActivity, i2));
        }
    }

    private boolean isFoldInternalScreen() {
        return (!Util.isInInternalScreen(this.mActivity) || PadAdapterUtil.IS_PAD || this.mActivity.isInMultiWindowMode()) ? false : true;
    }

    @Override // com.android.deskclock.timer.TimerHistoryAdapter.onDataListChangedListener
    public void onDataListChanged() {
        int size = this.mTimerModel.getTimers().size();
        setGridLayoutManagerBySize(size);
        if (size == 0) {
            setTimerHistoryLayout();
        }
        Log.d(TAG, "dataChanged: " + size);
    }

    public static class TimerObserverImp implements TimerModel.TimerObserver {
        private WeakReference<TimerFragment> mReference;

        public TimerObserverImp(TimerFragment timerFragment) {
            this.mReference = new WeakReference<>(timerFragment);
        }

        @Override // com.android.deskclock.timer.TimerModel.TimerObserver
        public void onTimersLoaded(List<TimerModel.TimerBean> list) {
            TimerFragment timerFragment = this.mReference.get();
            if (timerFragment != null) {
                timerFragment.onTimersLoaded(list);
            }
        }

        @Override // com.android.deskclock.timer.TimerModel.TimerObserver
        public void onTimersChanged(int i) {
            TimerFragment timerFragment = this.mReference.get();
            if (timerFragment != null) {
                timerFragment.onTimersChanged(i);
            }
        }
    }

    public void onTimersLoaded(List<TimerModel.TimerBean> list) {
        if (this.mTimerHistoryAdapter != null) {
            Log.i(TAG, "onTimersLoaded dataList " + list.size());
            this.mTimerHistoryAdapter.initData(list);
            this.mTimerHistoryAdapter.notifyDataSetChanged();
        }
    }

    public void onTimersChanged(int i) {
        if (this.mTimerHistoryAdapter != null) {
            Log.i(TAG, "onTimersChanged  position" + i);
            if (i != -1) {
                this.mTimerHistoryAdapter.notifyItemInserted(i);
            }
            this.mTimerHistoryAdapter.notifyDataSetChanged();
        }
    }

    private void setTimerPickerCount() {
        if (this.mTimerPicker != null) {
            if (Util.isTinyScreen(this.mActivity) || ((Util.isSmallPad() && Util.isPadOrientationLand(this.mActivity) && !Util.isInMultiWindowMode(this.mActivity)) || (Util.isFoldOrientationLand(DeskClockApp.getAppDEContext()) && !Util.isInMultiWindowMode(this.mActivity)))) {
                this.mTimerPicker.setSelectorIndicesCount(3);
            } else {
                this.mTimerPicker.setSelectorIndicesCount(5);
            }
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Log.d(TAG, "TimerFragment onResume");
        setItemBackground(0);
        FabControllerNew.getInstance().resetAnimChangeFlag();
    }

    @Override // com.android.deskclock.base.BaseClockFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (this.mTimerService != null && TabViewModel.TAB_TIMER.equals(TabNavigatorContentFragment.mCurrTab)) {
            this.mTimerService.setNormalState(true);
        }
        if (this.mInitialized) {
            updateViewWithState(true);
            updateCircleView();
            updateBrightView();
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        TimerProgressView timerProgressView = this.mClockCircleView;
        if (timerProgressView != null) {
            timerProgressView.release();
        }
        super.onDestroyView();
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        Log.d(TAG, "TimerFragment onStop");
        TimerService timerService = this.mTimerService;
        if (timerService != null) {
            timerService.setNormalState(false);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        TimePickerForTimer timePickerForTimer = this.mTimerPicker;
        if (timePickerForTimer != null) {
            timePickerForTimer.stopScroll();
        }
        Log.d(TAG, "TimerFragment onPause");
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFragmentChange
    public void onEnter() {
        super.onEnter();
        Log.d(TAG, "onEnter: ");
        if (this.mHour == 0 && this.mMinute == 0 && this.mSecond == 0) {
            FabDataHelper.getInstance().setEnableState(TabViewModel.TAB_TIMER, false);
        }
        TimerService timerService = this.mTimerService;
        if (timerService != null) {
            timerService.setNormalState(true);
        }
        setItemBackground(0);
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFragmentChange
    public void onLeave() {
        super.onLeave();
        Log.d(TAG, "TimerFragment onLeave: ");
        TimePickerForTimer timePickerForTimer = this.mTimerPicker;
        if (timePickerForTimer != null) {
            timePickerForTimer.stopScroll();
        }
        TimerService timerService = this.mTimerService;
        if (timerService != null) {
            timerService.setNormalState(false);
        }
    }

    private void onAlertMuteStateChanged(boolean z) {
        this.mTimer.setSilent(z);
        TimerDao.updateTimerSilent(this.mActivity.getApplicationContext(), z);
        TimerService timerService = this.mTimerService;
        if (timerService != null) {
            timerService.setSilent(this.mTimer.isSilent());
        }
        if (z) {
            showToast(R.string.timer_alert_mute_on);
        } else {
            showToast(R.string.timer_alert_mute_off);
        }
    }

    private void showToast(int i) {
        Toast toast = this.mToast;
        if (toast != null) {
            toast.cancel();
        }
        Toast toastMakeText = Toast.makeText(DeskClockApp.getAppDEContext(), this.mActivity.getResources().getString(i), 1);
        this.mToast = toastMakeText;
        toastMakeText.show();
    }

    private void onKeepScreenStateChanged(boolean z) {
        this.mTimer.setBright(z);
        TimerService timerService = this.mTimerService;
        if (timerService != null) {
            timerService.setScreenOnState(z);
        }
        TimerDao.updateTimerBright(this.mActivity.getApplicationContext(), z);
        updateBrightView();
        ((TabNavigatorContentFragment) getParentFragment()).refreshKeepScreenOnState();
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onStartClick(View view) {
        super.onStartClick(view);
        Log.d(TAG, "onStartClick: ");
        cancelsTimer();
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onCenterClick(View view) {
        super.onCenterClick(view);
        Log.d(TAG, "timer onCenterClick: ");
        if (this.mTimer.getState() == 3) {
            cancelsTimer();
            AlarmHelper.dismissTimer(this.mActivity.getApplicationContext());
            AlarmHelper.notifyToFinishAlertUI(this.mActivity.getApplicationContext());
            return;
        }
        startTimer();
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onEndClick(View view) {
        super.onEndClick(view);
        Log.d(TAG, "onEndClick: ");
        if (this.mTimer.getState() == 2) {
            continuesTimer();
        } else if (this.mTimer.getState() == 1) {
            pausesTimer();
        }
    }

    private void updateViewWithState(boolean z) {
        int state = this.mTimer.getState();
        TabNavigatorContentFragment tabNavigatorContentFragment = (TabNavigatorContentFragment) getParentFragment();
        if (tabNavigatorContentFragment != null) {
            tabNavigatorContentFragment.refreshKeepScreenOnState();
        }
        if (state == 0) {
            hideCircleView(z);
            setItemBackground(this.mTimerPicker.getCurrentSecond().intValue() + (this.mTimerPicker.getCurrentMinute().intValue() * 60) + (this.mTimerPicker.getCurrentHour().intValue() * R2.color.miuix_appcompat_coloured_btn_fg_color_light));
            if (this.mClockCircleView != null && !MiuiSdk.isSuperLiteMode() && !MiuiSdk.isLiteV1StockMode()) {
                this.mClockCircleView.setState(0, 0L, this.mTimer.getDuration());
            }
            initTimerHistory();
            return;
        }
        if (state == 1) {
            showCircleLayout(z);
            if (this.mClockCircleView == null || MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
                return;
            }
            this.mClockCircleView.setState(1, this.mTimer.getRemain(), this.mTimer.getDuration());
            return;
        }
        if (state == 2) {
            showCircleLayout(z);
            if (this.mClockCircleView == null || MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
                return;
            }
            this.mClockCircleView.setState(2, this.mTimer.getRemain(), this.mTimer.getDuration());
            return;
        }
        if (state != 3) {
            return;
        }
        showCircleLayout(z);
        if (this.mClockCircleView == null || MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
            return;
        }
        this.mClockCircleView.setState(3, 0L, this.mTimer.getDuration());
    }

    private void initCircleView() {
        this.mTimerModel.getTimers().size();
        if (this.mCircleViewStub.getParent() != null) {
            this.mCircleViewStub.inflate();
        }
        this.mTimerCircleLayout = (RelativeLayout) this.mRootView.findViewById(R.id.timer_circle_view_for_bitmap);
        TextView textView = (TextView) this.mRootView.findViewById(R.id.timer_display);
        this.mTimerDisplay = textView;
        textView.setFontFeatureSettings("tnum");
        this.mTimerDisplay.setVisibility(4);
        if (MiuiSdk.isSupportMiUiFont()) {
            MiuiFont.setFont(this.mTimerDisplay, MiuiFont.MI_TYPE_MONO_DEMIBOLD);
        }
        this.mTimerDuration = (TextView) this.mRootView.findViewById(R.id.timer_duration);
        if (MiuiSdk.isSupportMiUiFont()) {
            MiuiFont.setFont(this.mTimerDuration, MiuiFont.MI_PRO_REGULAR);
        }
        this.mTimerLabel = (TextView) this.mRootView.findViewById(R.id.timer_label);
        LinearLayout linearLayout = (LinearLayout) this.mRootView.findViewById(R.id.timer_screen_on);
        this.mTimerScreenOn = linearLayout;
        ViewCompat.setAccessibilityDelegate(linearLayout, new AccessibilityDelegateCompat() { // from class: com.android.deskclock.timer.TimerFragment.2
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setClassName(Button.class.getName());
            }
        });
        this.mTimerScreenOnImage = (ImageView) this.mRootView.findViewById(R.id.timer_screen_on_image);
        this.mTimerScreenOnText = (TextView) this.mRootView.findViewById(R.id.timer_screen_on_text);
        this.mTimerScreenOn.setOnClickListener(this);
        updateBrightView();
        TimerProgressView timerProgressView = (TimerProgressView) this.mRootView.findViewById(R.id.timer_progress);
        this.mClockCircleView = timerProgressView;
        timerProgressView.initContext(this.mActivity);
        TimerProgressBgView timerProgressBgView = (TimerProgressBgView) this.mRootView.findViewById(R.id.timer_progress_bg);
        this.mClockCircleBgView = timerProgressBgView;
        timerProgressBgView.initContext(this.mActivity);
        SegmentDialProgressDrawable segmentDialProgressDrawable = new SegmentDialProgressDrawable(this.mActivity);
        float dimension = this.mActivity.getResources().getDimension(R.dimen.timer_dial_size);
        segmentDialProgressDrawable.setIntrinsicSize(dimension, dimension);
        segmentDialProgressDrawable.setSegmentColor(this.mActivity.getResources().getColor(R.color.dial_segments_color), this.mActivity.getResources().getColor(R.color.dial_segments_color_background), this.mActivity.getResources().getColor(R.color.dial_segments_color_pause));
        this.progressContent = this.mRootView.findViewById(R.id.timer_circle_content);
        this.mClockCircleBgView.setPadding(0, (int) this.progressTopPadding, 0, 0);
        this.mClockCircleBgView.setProgressBgRadius(this.bgRadius);
        this.mTimerDisplay.setTextSize(0, this.highLightSize);
    }

    private void showCircleLayout(boolean z) {
        RelativeLayout relativeLayout = this.mTimerCircleLayout;
        if (relativeLayout == null) {
            initCircleView();
        } else {
            Folme.useAt(relativeLayout).state().cancel();
        }
        RecyclerView recyclerView = this.mTimerHistoryView;
        if (recyclerView != null && recyclerView.getVisibility() != 8) {
            this.mTimerHistoryView.setVisibility(4);
        }
        Folme.useAt(this.mTimerPicker).state().cancel();
        if (this.mIsCircleLayoutShowing || this.mTimerCircleLayout == null) {
            return;
        }
        this.mTimerPicker.setAlpha(1.0f);
        if (z || MiuiSdk.isLiteOrMiddleMode()) {
            this.mTimerPicker.setVisibility(8);
            this.mTimerDisplay.setVisibility(0);
            this.mTimerCircleLayout.setVisibility(0);
        } else {
            this.mTimerPicker.setVisibility(8);
            this.mVirtualAnimView.setVisibility(0);
            this.mVirtualAnimView.setDuration(this.mHour, this.mMinute, this.mSecond);
            this.progressContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.deskclock.timer.TimerFragment.3
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    if (TimerFragment.this.progressContent != null) {
                        TimerFragment.this.progressContent.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    if (TimerFragment.this.mVirtualAnimView == null || TimerFragment.this.progressContent == null || TimerFragment.this.mTimerDisplay == null) {
                        return false;
                    }
                    TimerFragment.this.mVirtualAnimView.compress(TimerFragment.this.progressContent.getY() + TimerFragment.this.mTimerDisplay.getY());
                    return false;
                }
            });
            this.mTimerDisplay.setVisibility(4);
            this.mTimerCircleLayout.setVisibility(0);
            this.mTimerCircleLayout.setAlpha(0.0f);
            MiuiFolme.showTimerCircle(this.mTimerCircleLayout, new MiuiFolme.ClockTransitionListener() { // from class: com.android.deskclock.timer.TimerFragment.4
                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    if (TimerFragment.this.mTimer == null || TimerFragment.this.mTimer.getState() == 0) {
                        return;
                    }
                    TimerFragment.this.mTimerDisplay.setVisibility(0);
                    TimerFragment.this.mVirtualAnimView.setVisibility(8);
                    TimerFragment.this.mTimerPicker.setVisibility(8);
                    TimerFragment.this.mTimerCircleLayout.setScaleX(1.0f);
                    TimerFragment.this.mTimerCircleLayout.setScaleY(1.0f);
                    TimerFragment.this.mTimerCircleLayout.setAlpha(1.0f);
                }

                @Override // miuix.animation.listener.TransitionListener
                public void onCancel(Object obj) {
                    super.onCancel(obj);
                    if (TimerFragment.this.mTimer == null || TimerFragment.this.mTimer.getState() == 0) {
                        return;
                    }
                    TimerFragment.this.mHandler.postDelayed(new Runnable() { // from class: com.android.deskclock.timer.TimerFragment.4.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (TimerFragment.this.mTimerDisplay == null || TimerFragment.this.mVirtualAnimView == null || TimerFragment.this.mTimerPicker == null) {
                                return;
                            }
                            TimerFragment.this.mTimerDisplay.setVisibility(0);
                            TimerFragment.this.mVirtualAnimView.setVisibility(8);
                            MiuiFolme.cancelFolme(TimerFragment.this.mTimerCircleLayout);
                            TimerFragment.this.mTimerPicker.setVisibility(8);
                            MiuiFolme.cancelFolme(TimerFragment.this.mTimerPicker);
                        }
                    }, 300L);
                    TimerFragment.this.mTimerCircleLayout.setScaleX(1.0f);
                    TimerFragment.this.mTimerCircleLayout.setScaleY(1.0f);
                    TimerFragment.this.mTimerCircleLayout.setAlpha(1.0f);
                }
            });
        }
        updateCircleView();
        Log.d(TAG, "trigger full screen mode when start timer");
        this.mIsCircleLayoutShowing = true;
    }

    private void hideCircleView(boolean z) {
        RecyclerView recyclerView = this.mTimerHistoryView;
        if (recyclerView != null) {
            recyclerView.setVisibility(0);
        }
        if (this.mTimerCircleLayout != null) {
            this.mTimerDisplay.setVisibility(4);
            if (z || MiuiSdk.isLiteOrMiddleMode()) {
                this.mTimerCircleLayout.setVisibility(8);
                this.mTimerPicker.setVisibility(0);
                this.mTimerPicker.setAlpha(1.0f);
            } else {
                MiuiFolme.hideTimerCircle(this.mTimerCircleLayout, new MiuiFolme.ClockTransitionListener() { // from class: com.android.deskclock.timer.TimerFragment.5
                    @Override // miuix.animation.listener.TransitionListener
                    public void onComplete(Object obj) {
                        if (TimerFragment.this.mTimer == null || TimerFragment.this.mTimer.getState() != 0) {
                            return;
                        }
                        TimerFragment.this.mTimerCircleLayout.setVisibility(8);
                        TimerFragment.this.mTimerCircleLayout.setScaleX(1.0f);
                        TimerFragment.this.mTimerCircleLayout.setScaleY(1.0f);
                        TimerFragment.this.mTimerCircleLayout.setAlpha(1.0f);
                    }

                    @Override // miuix.animation.listener.TransitionListener
                    public void onCancel(Object obj) {
                        super.onCancel(obj);
                        if (TimerFragment.this.mTimer == null || TimerFragment.this.mTimer.getState() != 0) {
                            return;
                        }
                        TimerFragment.this.mTimerCircleLayout.setVisibility(8);
                        MiuiFolme.cancelFolme(TimerFragment.this.mTimerCircleLayout);
                        TimerFragment.this.mTimerCircleLayout.setScaleX(1.0f);
                        TimerFragment.this.mTimerCircleLayout.setScaleY(1.0f);
                        TimerFragment.this.mTimerCircleLayout.setAlpha(1.0f);
                    }
                });
                this.mTimerPicker.setVisibility(0);
                this.mTimerPicker.setAlpha(0.0f);
                this.mVirtualAnimView.setVisibility(0);
                this.mVirtualAnimView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.deskclock.timer.TimerFragment.6
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        if (TimerFragment.this.mVirtualAnimView == null) {
                            return false;
                        }
                        TimerFragment.this.mVirtualAnimView.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (TimerFragment.this.progressContent == null || TimerFragment.this.mTimerDisplay == null) {
                            return false;
                        }
                        TimerFragment.this.mVirtualAnimView.expand(TimerFragment.this.progressContent.getY() + TimerFragment.this.mTimerDisplay.getY());
                        return false;
                    }
                });
                MiuiFolme.cancelFolme(this.mTimerPicker);
                MiuiFolme.showTimerPicker(this.mTimerPicker, new MiuiFolme.ClockTransitionListener() { // from class: com.android.deskclock.timer.TimerFragment.7
                    @Override // miuix.animation.listener.TransitionListener
                    public void onComplete(Object obj) {
                        if (TimerFragment.this.mTimer == null || TimerFragment.this.mTimer.getState() != 0) {
                            return;
                        }
                        TimerFragment.this.mVirtualAnimView.setVisibility(8);
                        TimerFragment.this.mTimerPicker.setScaleX(1.0f);
                        TimerFragment.this.mTimerPicker.setScaleY(1.0f);
                        TimerFragment.this.mTimerPicker.setAlpha(1.0f);
                    }

                    @Override // miuix.animation.listener.TransitionListener
                    public void onCancel(Object obj) {
                        super.onCancel(obj);
                        if (TimerFragment.this.mTimer == null || TimerFragment.this.mTimer.getState() != 0) {
                            return;
                        }
                        TimerFragment.this.mVirtualAnimView.setVisibility(8);
                        MiuiFolme.cancelAnim(TimerFragment.this.mTimerPicker);
                        TimerFragment.this.mTimerPicker.setScaleX(1.0f);
                        TimerFragment.this.mTimerPicker.setScaleY(1.0f);
                        TimerFragment.this.mTimerPicker.setAlpha(1.0f);
                    }
                });
            }
            this.mIsCircleLayoutShowing = false;
        }
    }

    private void startTimer(Timer timer) {
        Log.d(TAG, "startTimer: " + this.mTimerService);
        if (this.mTimerService != null) {
            if (this.mServiceIntent == null) {
                this.mServiceIntent = new Intent(this.mActivity, (Class<?>) TimerService.class);
            }
            this.mActivity.startService(this.mServiceIntent);
            this.mTimerService.startTimer(timer);
        }
    }

    private int getTimerState() {
        TimerService timerService = this.mTimerService;
        if (timerService != null) {
            return timerService.getTimerState();
        }
        if (this.mActivity != null) {
            return TimerDao.getTimer(this.mActivity).getState();
        }
        return 0;
    }

    private void startTimer() {
        this.mHour = this.mTimerPicker.getCurrentHour().intValue();
        this.mMinute = this.mTimerPicker.getCurrentMinute().intValue();
        this.mSecond = this.mTimerPicker.getCurrentSecond().intValue();
        startNewTimer();
        TimerHistoryAdapter timerHistoryAdapter = this.mTimerHistoryAdapter;
        if (timerHistoryAdapter != null) {
            timerHistoryAdapter.addTimerHistory(this.mSecond + (this.mMinute * 60) + (this.mHour * R2.color.miuix_appcompat_coloured_btn_fg_color_light));
        }
        StatHelper.deskclockEvent(StatHelper.EVENT_CLICK_TIMER_START);
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.TIMER_FAB_START);
    }

    private void cancelsTimer() {
        TimerService timerService = this.mTimerService;
        if (timerService != null) {
            timerService.cancelTimer();
        }
        StatHelper.deskclockEvent(StatHelper.EVENT_CLICK_TIMER_CANCEL);
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.TIMER_FAB_CANCEL);
    }

    private void pausesTimer() {
        TimerService timerService = this.mTimerService;
        if (timerService != null) {
            timerService.pauseTimer();
        }
        StatHelper.deskclockEvent(StatHelper.EVENT_CLICK_TIMER_PAUSE);
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.TIMER_FAB_PAUSE);
    }

    private void continuesTimer() {
        TimerService timerService = this.mTimerService;
        if (timerService != null) {
            timerService.continueTimer();
        }
        StatHelper.deskclockEvent(StatHelper.EVENT_CLICK_TIMER_CONTINUE);
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.TIMER_FAB_CONTINUE);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getVisibility() != 8 && view.getId() == R.id.timer_screen_on) {
            onKeepScreenStateChanged(!this.mTimer.isBright());
        }
    }

    private void startNewTimer() {
        long j = (((long) this.mHour) * AlarmHelper.ARRIVING_ALARM_DURATION) + (((long) this.mMinute) * 60000) + (((long) this.mSecond) * 1000);
        long jCurrentTimeMillis = System.currentTimeMillis() + j;
        this.mTimer.setDuration(j);
        this.mTimer.setRemain(j);
        this.mTimer.setTime(jCurrentTimeMillis);
        this.mTimer.setLabel(null);
        startTimer(this.mTimer);
        RingtoneHelper.handleTimerAlert(TimerDao.getTimerRingtone());
    }

    public void startFromShortcut() {
        TimerService timerService;
        this.mPendingShortCut = true;
        if (this.mTimerService == null || !this.mInitialized) {
            return;
        }
        int timerState = getTimerState();
        if (timerState == 0 || timerState == 3) {
            if (this.mHour == 0 && this.mMinute == 0 && this.mSecond == 0) {
                this.mTimerPicker.setCurrentHour(0);
                this.mTimerPicker.setCurrentMinute(5);
                this.mTimerPicker.setCurrentSecond(0);
                setItemBackground(300);
            }
            this.mTimerService.setNormalState(true);
            startNewTimer();
        } else if ((timerState == 2 || timerState == 1) && (timerService = this.mTimerService) != null) {
            timerService.continueTimer();
            this.mTimerService.setNormalState(true);
        }
        this.mPendingShortCut = false;
        TimerHistoryAdapter timerHistoryAdapter = this.mTimerHistoryAdapter;
        if (timerHistoryAdapter != null) {
            timerHistoryAdapter.addTimerHistory(this.mSecond + (this.mMinute * 60) + (this.mHour * R2.color.miuix_appcompat_coloured_btn_fg_color_light));
        }
    }

    public void handleNotificationTimer() {
        if (this.mTimerService == null || !this.mInitialized) {
            return;
        }
        int timerState = getTimerState();
        if (timerState == 2 || timerState == 1) {
            this.mTimerService.setNormalState(true);
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public boolean shouldKeepScreenOn() {
        return getTimerState() == 1 && this.mTimer.isBright();
    }

    @Override // com.android.deskclock.base.BaseClockFragment
    protected String getTab() {
        return TabViewModel.TAB_TIMER;
    }

    @Override // com.android.deskclock.widget.TimePickerForTimer.OnTimeChangedListener
    public void onTimeChanged(TimePickerForTimer timePickerForTimer, int i, int i2, int i3) {
        this.mHour = i;
        this.mMinute = i2;
        this.mSecond = i3;
        this.mHandler.postDelayed(this.mSetEnableStateRunnable, 20L);
    }

    @Override // com.android.deskclock.widget.TimePickerForTimer.OnTimerScrollListener
    public void onScrollStateChange(int i) {
        if (i == 0) {
            setItemBackground(0);
        }
    }

    private void setItemBackground(int i) {
        TimerModel timerModel;
        TimePickerForTimer timePickerForTimer;
        if (i == 0 && (timePickerForTimer = this.mTimerPicker) != null) {
            i = timePickerForTimer.getCurrentSecond().intValue() + (this.mTimerPicker.getCurrentMinute().intValue() * 60) + (this.mTimerPicker.getCurrentHour().intValue() * R2.color.miuix_appcompat_coloured_btn_fg_color_light);
        }
        Log.d("setItemBackground values :" + i);
        if (this.mTimerHistoryView == null || (timerModel = this.mTimerModel) == null || this.mTimerHistoryAdapter == null) {
            return;
        }
        boolean z = false;
        for (TimerModel.TimerBean timerBean : timerModel.getTimers()) {
            if (i == timerBean.seconds) {
                View viewFindViewByPosition = this.mTimerHistoryView.getLayoutManager().findViewByPosition(timerBean.id - 1);
                TimerHistoryAdapter.mTouchId = timerBean.id - 1;
                Log.d("setItemBackground view :" + viewFindViewByPosition + " history.id  :" + (timerBean.id - 1));
                z = true;
            }
        }
        if (!z) {
            TimerHistoryAdapter.mTouchId = -1;
        }
        this.mTimerHistoryAdapter.notifyDataSetChanged();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "TimerFragment onDestroy");
        TimerModel timerModel = this.mTimerModel;
        if (timerModel != null) {
            timerModel.release();
        }
        TimePickerForTimer timePickerForTimer = this.mTimerPicker;
        if (timePickerForTimer != null) {
            timePickerForTimer.setOnTimeChangedListener(null);
            this.mTimerPicker.setOnTimerScrollListener(null);
        }
        TimerHistoryAdapter timerHistoryAdapter = this.mTimerHistoryAdapter;
        if (timerHistoryAdapter != null) {
            timerHistoryAdapter.setOnDataListChangedListener(null);
            this.mTimerHistoryAdapter.setOnItemClickListener(null);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        unBindService();
    }

    private void unBindService() {
        if (this.mTimerServiceConnection != null) {
            this.mActivity.unbindService(this.mTimerServiceConnection);
            this.mTimerServiceConnection = null;
            TimerService timerService = this.mTimerService;
            if (timerService != null) {
                timerService.unRegisterCallbackListener(this.mTimerServiceCallback);
                this.mTimerServiceCallback = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTimerInfoFromService(Timer timer) {
        boolean z = true;
        boolean z2 = this.mTimer.getState() != timer.getState();
        boolean z3 = this.mTimer.getDuration() != timer.getDuration();
        this.mTimer.getType();
        timer.getType();
        boolean z4 = this.mTimer.getRemain() != timer.getRemain();
        this.mTimer.isSilent();
        timer.isSilent();
        boolean z5 = this.mTimer.isBright() != timer.isBright();
        if ((this.mTimer.getLabel() != null || timer.getLabel() == null) && (this.mTimer.getLabel() == null || this.mTimer.getLabel().equals(timer.getLabel()))) {
            z = false;
        }
        this.mTimer.setState(timer.getState());
        this.mTimer.setTime(timer.getTime());
        this.mTimer.setRemain(timer.getRemain());
        this.mTimer.setType(timer.getType());
        this.mTimer.setDuration(timer.getDuration());
        this.mTimer.setSilent(timer.isSilent());
        this.mTimer.setBright(timer.isBright());
        this.mTimer.setLabel(timer.getLabel());
        if (z2 || this.mTimer.getRemain() == this.mTimer.getDuration()) {
            updateViewWithState(false);
        }
        if (this.mContinueTimerUpdate) {
            updateViewWithState(false);
            this.mContinueTimerUpdate = false;
        }
        if (z3) {
            updateTimerPickerValue();
        }
        if (z4 || z3) {
            updateRemainedTime();
        }
        if (z3 || z || z2) {
            updateLabelAndDuration();
        }
        if (z5) {
            updateBrightView();
        }
    }

    private void updateBrightView() {
        if (this.mTimerScreenOn == null) {
            return;
        }
        if (Util.isTinyScreen(this.mActivity)) {
            this.mTimerScreenOn.setVisibility(8);
            return;
        }
        boolean zIsBright = this.mTimer.isBright();
        this.mTimerScreenOn.setBackgroundResource(zIsBright ? R.drawable.timer_screen_on_background : R.drawable.timer_screen_off_background);
        this.mTimerScreenOnImage.setImageResource(zIsBright ? R.drawable.timer_screen_on_icon : R.drawable.timer_screen_off_icon);
        this.mTimerScreenOnText.setTextColor(this.mActivity.getResources().getColor(zIsBright ? R.color.timer_keep_screen_on_text_color : R.color.timer_keep_screen_off_text_color));
        this.mTimerScreenOn.setSelected(zIsBright);
    }

    private void updateCircleView() {
        if (isAdded()) {
            updateRemainedTime();
            updateLabelAndDuration();
        }
    }

    private void updateRemainedTime() {
        this.mTimer.getDuration();
        long remain = this.mTimer.getRemain();
        if (this.mTimerDisplay != null) {
            if (remain % 1000 > 0) {
                remain = ((remain / 1000) + 1) * 1000;
            }
            String time = Util.formatTime(String.format(getResources().getString(R.string.timer_display), Long.valueOf(remain / AlarmHelper.ARRIVING_ALARM_DURATION), Long.valueOf((remain % AlarmHelper.ARRIVING_ALARM_DURATION) / 60000), Long.valueOf((remain % 60000) / 1000)), new Object[0]);
            if (!time.equals(this.mTimerDisplay.getText())) {
                this.mTimerDisplay.setText(time);
            }
            this.mTimerDisplay.setContentDescription(Util.formatTimerDuration(this.mActivity, remain, R.array.time));
        }
    }

    private void updateLabelAndDuration() {
        long duration = this.mTimer.getDuration();
        String label = this.mTimer.getLabel();
        if (this.mTimerDuration != null) {
            if (this.mTimer.getState() == 3) {
                this.mTimerDuration.setText(R.string.timer_end_island);
                this.mTimerDuration.setTextColor(getResources().getColor(R.color.timer_remain_circle_color));
            } else {
                this.mTimerDuration.setText(Util.formatTimerDuration(this.mActivity, duration, R.array.timer_duration));
                this.mTimerDuration.setTextColor(getResources().getColor(R.color.timer_duration_desc_text_color));
            }
        }
        TextView textView = this.mTimerLabel;
        if (textView != null) {
            if (label != null && !label.equals(textView.getText())) {
                this.mTimerLabel.setVisibility(0);
                this.mTimerLabel.setText(label);
            } else if (label == null) {
                this.mTimerLabel.setText("");
                this.mTimerLabel.setVisibility(8);
            }
        }
    }

    private void updateTimerPickerValue() {
        long duration = this.mTimer.getDuration();
        int hourFromDuration = TimeUtil.getHourFromDuration(duration);
        int minuteFromDuration = TimeUtil.getMinuteFromDuration(duration);
        int secondFromDuration = TimeUtil.getSecondFromDuration(duration);
        TimePickerForTimer timePickerForTimer = this.mTimerPicker;
        if (timePickerForTimer != null) {
            timePickerForTimer.setCurrentHour(Integer.valueOf(hourFromDuration));
            this.mTimerPicker.setCurrentMinute(Integer.valueOf(minuteFromDuration));
            this.mTimerPicker.setCurrentSecond(Integer.valueOf(secondFromDuration));
            if (hourFromDuration == 0 && minuteFromDuration == 0 && secondFromDuration == 0) {
                this.mIsEnable = false;
            }
            setItemBackground((minuteFromDuration * 60) + secondFromDuration + (hourFromDuration * R2.color.miuix_appcompat_coloured_btn_fg_color_light));
        }
        VirtualTimerAnimView virtualTimerAnimView = this.mVirtualAnimView;
        if (virtualTimerAnimView != null) {
            virtualTimerAnimView.setDuration(hourFromDuration, minuteFromDuration, secondFromDuration);
        }
    }

    private static class TimerServiceCallback implements TimerService.CallbackListener {
        private WeakReference<TimerFragment> mReference;

        public TimerServiceCallback(TimerFragment timerFragment) {
            this.mReference = new WeakReference<>(timerFragment);
        }

        @Override // com.android.deskclock.timer.TimerService.CallbackListener
        public void onTimerInfo(Timer timer, boolean z) {
            WeakReference<TimerFragment> weakReference = this.mReference;
            TimerFragment timerFragment = weakReference != null ? weakReference.get() : null;
            if (timerFragment == null) {
                return;
            }
            if (timer.getState() != 1) {
                timerFragment.updateTimerInfoFromService(timer);
            } else {
                if (z) {
                    return;
                }
                timerFragment.updateTimerInfoFromService(timer);
            }
        }
    }

    private class TimerServiceConnection implements ServiceConnection {
        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        private TimerServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TimerFragment.this.mTimerService = ((TimerService.CallbackBinder) iBinder).getService();
            if (TimerFragment.this.mInitialized) {
                TimerFragment.this.mTimerService.registerCallListener(TimerFragment.this.mTimerServiceCallback);
            }
            if (TabViewModel.TAB_TIMER.equals(TabNavigatorContentFragment.mCurrTab)) {
                TimerFragment.this.mTimerService.setNormalState(true);
            }
            if (TimerFragment.this.mPendingShortCut) {
                TimerFragment.this.startFromShortcut();
            }
        }
    }

    public void quickSetTimerWithAnim(int i) {
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.setDuration(((long) i) * 1000);
        }
        updateTimerPickerValueWithAnim();
        setItemBackground(i);
    }

    private void updateTimerPickerValueWithAnim() {
        long duration = this.mTimer.getDuration();
        int hourFromDuration = TimeUtil.getHourFromDuration(duration);
        int minuteFromDuration = TimeUtil.getMinuteFromDuration(duration);
        int secondFromDuration = TimeUtil.getSecondFromDuration(duration);
        TimePickerForTimer timePickerForTimer = this.mTimerPicker;
        if (timePickerForTimer != null) {
            timePickerForTimer.setCurrentHourWithAnim(Integer.valueOf(hourFromDuration));
            this.mTimerPicker.setCurrentMinuteWithAnim(Integer.valueOf(minuteFromDuration));
            this.mTimerPicker.setCurrentSecondWithAnim(Integer.valueOf(secondFromDuration));
        }
        VirtualTimerAnimView virtualTimerAnimView = this.mVirtualAnimView;
        if (virtualTimerAnimView != null) {
            virtualTimerAnimView.setDuration(hourFromDuration, minuteFromDuration, secondFromDuration);
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        super.onResponsiveLayout(configuration, screenSpec, z);
        if (this.mInitialized) {
            setTimerPickerCount();
            resetLayout();
            TimerButton timerButton = this.mStartBtn;
            if (timerButton != null && this.mEndBtn != null && this.mCenterBtn != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) timerButton.getLayoutParams();
                layoutParams.setMarginStart((int) (Util.isTinyScreen(this.mActivity) ? DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.fab_view_btn_margin_start_tiny) : 0.0f));
                this.mStartBtn.setLayoutParams(layoutParams);
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mEndBtn.getLayoutParams();
                layoutParams2.setMarginEnd((int) (Util.isTinyScreen(this.mActivity) ? DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.fab_view_btn_margin_start_tiny) : 0.0f));
                this.mEndBtn.setLayoutParams(layoutParams2);
            }
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

    private void setTimerHistoryLayout() {
        if (this.mTimerHistoryView != null) {
            int size = this.mTimerModel.getTimers().size();
            Timer timer = this.mTimer;
            if ((timer != null && timer.getState() != 0) || this.mActivity.isInMultiWindowMode() || Util.isTinyScreen(this.mActivity) || size == 0) {
                this.mTimerHistoryView.setVisibility(8);
            } else {
                this.mTimerHistoryView.setVisibility(0);
            }
        }
    }

    private void resetLayout() {
        updateView();
        setTimerHistoryLayout();
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0071  */
    /* JADX WARN: Code duplicated, block: B:20:0x0074  */
    private void resetWidth() {
        int i;
        float dimension;
        LinearLayout.LayoutParams layoutParams;
        if (this.mTimerPickerContainer == null) {
            return;
        }
        int i2 = this.mActivity.getResources().getConfiguration().screenWidthDp;
        Log.d(TAG, "reset windowWidth: " + i2);
        if (i2 >= 392 && i2 <= 760) {
            dimension = DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.timer_number_picker_width1);
        } else if (i2 > 760 && i2 <= 1260) {
            dimension = DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.timer_number_picker_width2);
        } else {
            if (i2 > 1260) {
                dimension = DeskClockApp.getAppDEContext().getResources().getDimension(R.dimen.timer_number_picker_width3);
            } else {
                i = 0;
            }
            layoutParams = (LinearLayout.LayoutParams) this.mTimerPickerContainer.getLayoutParams();
            if (i != 0) {
                layoutParams.width = i;
            } else {
                layoutParams.width = -1;
            }
            this.mTimerPickerContainer.setLayoutParams(layoutParams);
        }
        i = (int) dimension;
        layoutParams = (LinearLayout.LayoutParams) this.mTimerPickerContainer.getLayoutParams();
        if (i != 0) {
            layoutParams.width = i;
        } else {
            layoutParams.width = -1;
        }
        this.mTimerPickerContainer.setLayoutParams(layoutParams);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mFontLevel == 2) {
            setGridLayoutManagerBySize(this.mTimerModel.getTimers().size());
        }
        TimerHistoryAdapter timerHistoryAdapter = this.mTimerHistoryAdapter;
        if (timerHistoryAdapter != null) {
            timerHistoryAdapter.dismissPopup();
        }
        if (this.mRootView != null) {
            this.mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.deskclock.timer.TimerFragment.9
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    TimerFragment.this.mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    TimerFragment.this.updateRootViewPadding();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:10:0x0036  */
    public void updateRootViewPadding() {
        int dimensionPixelOffset;
        if (this.mRootView == null) {
            return;
        }
        if (getActionBar() instanceof ActionBarImpl) {
            ActionBarImpl actionBarImpl = (ActionBarImpl) getActionBar();
            if (actionBarImpl.getActionBarContainer() != null) {
                dimensionPixelOffset = actionBarImpl.getActionBarContainer().getHeight();
                Log.d(TAG, "updateRootViewPadding height: " + dimensionPixelOffset);
            } else {
                dimensionPixelOffset = 0;
            }
        } else {
            dimensionPixelOffset = 0;
        }
        if (Util.isTinyScreen(this.mActivity)) {
            dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.timer_tiny_top_space);
        }
        this.mRootView.setPadding(this.mRootView.getPaddingLeft(), dimensionPixelOffset, this.mRootView.getPaddingRight(), this.mRootView.getPaddingBottom());
    }

    static class TimerFabClickListenerImpl implements FabControllerNew.onTimerFabClickListener {
        private WeakReference<TimerFragment> mWeakReference;

        public TimerFabClickListenerImpl(TimerFragment timerFragment) {
            this.mWeakReference = new WeakReference<>(timerFragment);
        }

        @Override // com.android.deskclock.util.fab.FabControllerNew.onTimerFabClickListener
        public void onStartFabClick(View view) {
            WeakReference<TimerFragment> weakReference = this.mWeakReference;
            TimerFragment timerFragment = weakReference == null ? null : weakReference.get();
            if (timerFragment != null) {
                timerFragment.onStartClick(view);
            }
        }

        @Override // com.android.deskclock.util.fab.FabControllerNew.onTimerFabClickListener
        public void onEndFabClick(View view) {
            WeakReference<TimerFragment> weakReference = this.mWeakReference;
            TimerFragment timerFragment = weakReference == null ? null : weakReference.get();
            if (timerFragment != null) {
                timerFragment.onEndClick(view);
            }
        }

        @Override // com.android.deskclock.util.fab.FabControllerNew.onTimerFabClickListener
        public void onCenterFabClick(View view) {
            WeakReference<TimerFragment> weakReference = this.mWeakReference;
            TimerFragment timerFragment = weakReference == null ? null : weakReference.get();
            if (timerFragment != null) {
                timerFragment.onCenterClick(view);
            }
        }
    }
}
