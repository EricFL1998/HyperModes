package com.android.deskclock.worldclock;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.HyperGridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.R2;
import com.android.deskclock.TabNavigatorContentFragment;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.base.BaseClockFragment;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.AnimationUtils;
import com.android.deskclock.util.CityZoneHelper;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.UiUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.fab.FabControllerNew;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.FormatClockForLocalTime;
import com.android.deskclock.view.HeaderScrollBehavior;
import com.android.deskclock.view.NestedContentScrollBehavior;
import com.android.deskclock.view.SimpleDialogFragment;
import com.android.deskclock.view.TypefaceTextView;
import com.android.deskclock.view.list.AlarmRecyclerView;
import com.android.deskclock.view.list.EditableAdapter;
import com.android.deskclock.view.tab.TabViewModel;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import miuix.animation.Folme;
import miuix.core.util.MiuixUIUtils;
import miuix.core.widget.NestedScrollView;
import miuix.flexible.template.TemplateFactory;
import miuix.popupwidget.widget.GuidePopupWindow;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes.dex */
public class WorldClockFragment extends BaseClockFragment {
    private static final String KEY_TIMEZONE_ITEM_SHOW = "key_timezone_item_show";
    public static final int REQUEST_CODE_ADD_TIMEZONE = 100;
    public static final long SELECT_ITEM_LOCAL_TIMEZONE = -1;
    private static final String TAG = "DC:WorldClockFragment";
    private static final int TIME_ENTER_DAY = 6;
    private static final int TIME_ENTER_NIGHT = 18;
    public static boolean mClockIsInActionMode;
    public static boolean mIsAnimRunning;
    ItemTouchHelperCallback callback;
    private int endWidth;
    GuidePopupWindow guidePopupWindow;
    private HeaderScrollBehavior headerScrollBehavior;
    private HyperGridLayoutManager layoutManager;
    private LocalClockBgView mClockBgView;
    private View mClockLand;
    private LocalClockView mClockView;
    private int mClockViewHeight;
    private Calendar mContrastCalendar;
    private boolean mContrastMode;
    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout mDateView;
    private String mDefaultTimeZoneId;
    private View mHeadView;
    ItemTouchHelper mItemTouchHelper;
    private CityObj mLocalCity;
    private FormatClockForLocalTime mLocalDigitClock;
    private NestedScrollView mNestedScrollView;
    private int[] mPrepareToDelete;
    private int[] mPrepareToDeletePos;
    private RulerView mRulerView;
    private Handler mSecondTickHandler;
    private Runnable mTicker;
    private AsyncTask<Void, Void, Void> mTimeZoneChangeAsyncTask;
    private TimezoneAdapter mTimezoneAdapter;
    private AlarmRecyclerView mTimezoneLv;
    private TimezoneModel mTimezoneModel;
    private View mWorldClockBlankPage;
    private int startWidth;
    private SimpleDialogFragment mDeleteConfirmDialog = null;
    private boolean mTickerStopped = true;
    private boolean mNeedHandleWidgetData = false;
    private int mCurrSelectedIndex = -1;
    private boolean mUserChangeOrder = false;
    private boolean mIsDeleteData = false;
    private EditableAdapter.MultiChoiceModeListener mMultiChoiceModeListener = new EditableAdapter.MultiChoiceModeListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.13
        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            ((TabNavigatorContentFragment) WorldClockFragment.this.getParentFragment()).onActionModeChanged(true);
            WorldClockFragment.mClockIsInActionMode = true;
            WorldClockFragment.this.setPadLandScreenLayout();
            return false;
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            ((TabNavigatorContentFragment) WorldClockFragment.this.getParentFragment()).onActionModeChanged(false);
            WorldClockFragment.mClockIsInActionMode = false;
            if (WorldClockFragment.this.mClockView != null) {
                WorldClockFragment.this.mClockView.setVisibility(0);
                WorldClockFragment.this.mNestedScrollView.setTranslationY(0.0f);
                if (WorldClockFragment.this.headerScrollBehavior != null) {
                    WorldClockFragment.this.headerScrollBehavior.setMinLocalTimeScale(WorldClockFragment.this.mCoordinatorLayout, WorldClockFragment.this.mHeadView, WorldClockFragment.this.mNestedScrollView);
                }
            }
            if (!WorldClockFragment.this.mUserChangeOrder || WorldClockFragment.this.mIsDeleteData) {
                if (WorldClockFragment.this.mUserChangeOrder || WorldClockFragment.this.mIsDeleteData) {
                    if (WorldClockFragment.this.mUserChangeOrder && WorldClockFragment.this.mIsDeleteData) {
                        WorldClockFragment.this.mNeedHandleWidgetData = true;
                        new Handler().postDelayed(new Runnable() { // from class: com.android.deskclock.worldclock.WorldClockFragment.13.1
                            @Override // java.lang.Runnable
                            public void run() {
                                WorldClockFragment.this.mUserChangeOrder = false;
                                WorldClockFragment.this.mIsDeleteData = false;
                                WorldClockFragment.this.mTimezoneModel.updateDatabase(WorldClockFragment.this.mTimezoneAdapter.getData());
                            }
                        }, 500L);
                    }
                } else {
                    Log.d(WorldClockFragment.TAG, "no need to update the database ");
                }
            } else {
                WorldClockFragment.this.mUserChangeOrder = false;
                WorldClockFragment.this.mNeedHandleWidgetData = true;
                WorldClockFragment.this.mTimezoneModel.updateDatabase(WorldClockFragment.this.mTimezoneAdapter.getData());
            }
            WorldClockFragment.this.setPadLandScreenLayout();
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            WorldClockFragment.this.getActivity().getMenuInflater().inflate(R.menu.delete_item, menu);
            UiUtil.updateActionModeButton1(actionMode);
            UiUtil.updateActionModeButton2(actionMode, WorldClockFragment.this.mTimezoneAdapter.isAllItemsChecked());
            WorldClockFragment.this.setContentDescription();
            try {
                WorldClockFragment.this.mActivity.getWindow().getDecorView().findViewById(16908313).setContentDescription(WorldClockFragment.this.mActivity.getResources().getString(R.string.back));
            } catch (Throwable unused) {
            }
            if (Util.isPadOrientationLand(WorldClockFragment.this.mActivity) && (!Util.isPadOrientationLand(WorldClockFragment.this.mActivity) || !Util.isInMultiWindowMode(WorldClockFragment.this.mActivity))) {
                WorldClockFragment.this.mClockView.setVisibility(0);
                WorldClockFragment.this.mNestedScrollView.setTranslationY(0.0f);
                if (WorldClockFragment.this.headerScrollBehavior != null) {
                    WorldClockFragment.this.headerScrollBehavior.setMinLocalTimeScale(WorldClockFragment.this.mCoordinatorLayout, WorldClockFragment.this.mHeadView, WorldClockFragment.this.mNestedScrollView);
                }
            } else if (WorldClockFragment.this.mNestedScrollView.getTranslationY() == 0.0f) {
                WorldClockFragment.mIsAnimRunning = true;
                AnimationUtils.animateTranslateY(WorldClockFragment.this.mNestedScrollView, 0.0f, (-WorldClockFragment.this.mClockViewHeight) + WorldClockFragment.this.mActivity.getResources().getDimension(R.dimen.clock_view_offset_y), 250L);
                WorldClockFragment.this.mNestedScrollView.postDelayed(new Runnable() { // from class: com.android.deskclock.worldclock.WorldClockFragment.13.2
                    @Override // java.lang.Runnable
                    public void run() {
                        WorldClockFragment.mIsAnimRunning = false;
                    }
                }, 400L);
            }
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == 16908313) {
                actionMode.finish();
            } else if (menuItem.getItemId() == 16908314) {
                WorldClockFragment.this.mTimezoneAdapter.setAllItemsChecked(!WorldClockFragment.this.mTimezoneAdapter.isAllItemsChecked());
            } else if (menuItem.getItemId() == R.id.delete) {
                if (WorldClockFragment.this.mTimezoneAdapter.getCheckedItemCount() > 0) {
                    WorldClockFragment.this.mIsDeleteData = true;
                    WorldClockFragment worldClockFragment = WorldClockFragment.this;
                    worldClockFragment.mPrepareToDelete = worldClockFragment.mTimezoneAdapter.getCheckedItemIds();
                    WorldClockFragment worldClockFragment2 = WorldClockFragment.this;
                    worldClockFragment2.mPrepareToDeletePos = worldClockFragment2.mTimezoneAdapter.getCheckedItemPostions();
                    if (WorldClockFragment.this.mActivity != null) {
                        WorldClockFragment.this.mActivity.setNavigationForActionMode(false);
                    }
                    WorldClockFragment.this.mTimezoneAdapter.finishActionMode();
                }
                return true;
            }
            return false;
        }

        @Override // android.widget.AbsListView.MultiChoiceModeListener
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long j, boolean z) {
            boolean zIsAllItemsChecked = WorldClockFragment.this.mTimezoneAdapter.isAllItemsChecked();
            boolean zIsAllItemsUnChecked = WorldClockFragment.this.mTimezoneAdapter.isAllItemsUnChecked();
            UiUtil.updateActionModeButton2(actionMode, zIsAllItemsChecked);
            UiUtil.updateActionModeDeleteBtn(actionMode, zIsAllItemsUnChecked);
            WorldClockFragment.this.setContentDescription();
        }

        @Override // com.android.deskclock.view.list.EditableAdapter.MultiChoiceModeListener
        public void onAllItemCheckedStateChanged(ActionMode actionMode, boolean z) {
            boolean zIsAllItemsUnChecked = WorldClockFragment.this.mTimezoneAdapter.isAllItemsUnChecked();
            UiUtil.updateActionModeButton2(actionMode, z);
            UiUtil.updateActionModeDeleteBtn(actionMode, zIsAllItemsUnChecked);
            WorldClockFragment.this.setContentDescription();
        }
    };

    @Override // miuix.appcompat.app.Fragment
    protected boolean isResponsiveEnabled() {
        return true;
    }

    static {
        TemplateFactory.registerTemplate("timezone", TimezoneTemplate.class);
        mIsAnimRunning = false;
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mActivity = (DeskClockTabActivity) getActivity();
        this.mRootView = (ViewGroup) layoutInflater.inflate(R.layout.fragment_clock, viewGroup, false);
        this.mRulerView = (RulerView) this.mRootView.findViewById(R.id.ruler_view);
        FormatClockForLocalTime formatClockForLocalTime = (FormatClockForLocalTime) this.mRootView.findViewById(R.id.local_time);
        this.mLocalDigitClock = formatClockForLocalTime;
        this.mDateView = (LinearLayout) formatClockForLocalTime.findViewById(R.id.time_total_desc);
        this.mClockLand = this.mRootView.findViewById(R.id.clock_land);
        this.mCoordinatorLayout = (CoordinatorLayout) this.mRootView.findViewById(R.id.clock_scroll_layout);
        this.mNestedScrollView = (NestedScrollView) this.mRootView.findViewById(R.id.clcok_nested_scroll_view);
        this.mHeadView = this.mRootView.findViewById(R.id.clock_head_view);
        registerCoordinateScrollView(this.mCoordinatorLayout);
        this.startWidth = (int) this.mActivity.getResources().getDimension(R.dimen.ruler_min_width);
        if (Util.isDeviceCetus() && !Util.isInInternalScreen(this.mActivity)) {
            this.endWidth = (int) this.mActivity.getResources().getDimension(R.dimen.ruler_view_width_j18);
        } else {
            this.endWidth = (int) this.mActivity.getResources().getDimension(R.dimen.ruler_view_width);
        }
        this.mRootView.post(new Runnable() { // from class: com.android.deskclock.worldclock.WorldClockFragment.1
            @Override // java.lang.Runnable
            public void run() {
                WorldClockFragment.this.initClockView();
                WorldClockFragment worldClockFragment = WorldClockFragment.this;
                worldClockFragment.updateLayout(worldClockFragment.mRootView);
            }
        });
        this.mRootView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (WorldClockFragment.this.mRulerView == null || WorldClockFragment.this.mRulerView.getVisibility() != 0) {
                    return;
                }
                Folme.clean(WorldClockFragment.this.mRulerView);
                WorldClockFragment.this.mRulerView.setVisibility(8);
                WorldClockFragment.this.mCurrSelectedIndex = -1;
                WorldClockFragment.this.cancleContrastMode();
            }
        });
        setPadLandScreenLayout();
        return this.mRootView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPadLandScreenLayout() {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) this.mHeadView.getLayoutParams();
        CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams) this.mNestedScrollView.getLayoutParams();
        if (Util.isPadOrientationLand(this.mActivity) && !Util.isInMultiWindowMode(this.mActivity)) {
            layoutParams.height = this.mCoordinatorLayout.getTop() + this.mActivity.getResources().getDimensionPixelOffset(R.dimen.world_clock_view_height);
            layoutParams.width = (int) this.mActivity.getResources().getDimension(R.dimen.world_clock_view_width);
            layoutParams2.setMarginStart((int) this.mActivity.getResources().getDimension(R.dimen.world_clock_item_width));
            if (mClockIsInActionMode) {
                layoutParams2.topMargin = 0;
            } else if (Util.isSmallPad()) {
                layoutParams2.topMargin = (int) this.mActivity.getResources().getDimension(R.dimen.world_clock_item_padding_top_small_pad);
            } else {
                layoutParams2.topMargin = (int) this.mActivity.getResources().getDimension(R.dimen.world_clock_item_padding_top);
            }
            layoutParams2.bottomMargin = 0;
            layoutParams.gravity = 16;
            layoutParams.setAnchorId(R.id.clock_land);
            layoutParams2.setAnchorId(R.id.clock_land);
            if (Util.isRtl()) {
                layoutParams.anchorGravity = 21;
                layoutParams2.anchorGravity = 3;
            } else {
                layoutParams.anchorGravity = 19;
                layoutParams2.anchorGravity = 5;
            }
        } else {
            layoutParams.width = -1;
            layoutParams.height = (int) this.mActivity.getResources().getDimension(R.dimen.world_clock_view_height);
            layoutParams.gravity = 1;
            layoutParams2.gravity = 1;
            layoutParams2.setMarginStart(0);
            layoutParams2.topMargin = 0;
            layoutParams2.bottomMargin = (int) this.mActivity.getResources().getDimension(R.dimen.world_clock_item_padding_bottom);
            layoutParams.setAnchorId(-1);
            layoutParams.anchorGravity = 0;
            layoutParams2.setAnchorId(-1);
            layoutParams2.anchorGravity = 0;
        }
        this.mHeadView.setLayoutParams(layoutParams);
        this.mNestedScrollView.setLayoutParams(layoutParams2);
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mSecondTickHandler = new Handler() { // from class: com.android.deskclock.worldclock.WorldClockFragment.3
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                WorldClockFragment.this.updateTimezoneItem(System.currentTimeMillis());
            }
        };
        this.mTicker = new Runnable() { // from class: com.android.deskclock.worldclock.WorldClockFragment.4
            @Override // java.lang.Runnable
            public void run() {
                if (WorldClockFragment.this.mTickerStopped) {
                    return;
                }
                WorldClockFragment.this.onSecondTick();
                long jUptimeMillis = SystemClock.uptimeMillis();
                long jCurrentTimeMillis = 1000 - (System.currentTimeMillis() % 1000);
                long j = jUptimeMillis + jCurrentTimeMillis;
                Log.d(WorldClockFragment.TAG, "next offset: " + jCurrentTimeMillis);
                if (WorldClockFragment.this.mSecondTickHandler != null) {
                    WorldClockFragment.this.mSecondTickHandler.postAtTime(WorldClockFragment.this.mTicker, j + 5);
                }
            }
        };
        this.mTickerStopped = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeRulerValue(CityObj cityObj) {
        if (cityObj != null) {
            int i = Calendar.getInstance(TimeZone.getTimeZone(cityObj.mTimeZone)).get(11);
            RulerView rulerView = this.mRulerView;
            if (rulerView != null) {
                rulerView.setValue(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveRulerValue(CityObj cityObj) {
        this.mRulerView.moveThumb(Calendar.getInstance(TimeZone.getTimeZone(cityObj.mTimeZone)).get(11));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeSeekBarListener(final int i, final CityObj cityObj, final Calendar calendar) {
        this.mRulerView.setOnSeekBarChangeListener(new RulerView.OnValueChangeListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.5
            @Override // com.android.deskclock.worldclock.RulerView.OnValueChangeListener
            public void onChangeStart() {
                WorldClockFragment.this.mTimezoneAdapter.setContrastMode(true);
                WorldClockFragment.this.mContrastMode = true;
                WorldClockFragment.this.mContrastCalendar = Calendar.getInstance();
                WorldClockFragment.this.mContrastCalendar.setTimeInMillis(calendar.getTimeInMillis());
                WorldClockFragment.this.mContrastCalendar.set(12, 0);
                WorldClockFragment.this.mContrastCalendar.set(13, 0);
                WorldClockFragment.this.mContrastCalendar.set(14, 0);
                WorldClockFragment.this.changeRulerValue(cityObj);
            }

            @Override // com.android.deskclock.worldclock.RulerView.OnValueChangeListener
            public void onValueChanged(RulerView rulerView, int i2, int i3) {
                if (WorldClockFragment.this.mContrastCalendar == null) {
                    WorldClockFragment.this.mContrastCalendar = Calendar.getInstance();
                }
                WorldClockFragment.this.mContrastCalendar.add(11, i3 - i2);
                long timeInMillis = WorldClockFragment.this.mContrastCalendar.getTimeInMillis();
                if (WorldClockFragment.this.mTimezoneAdapter != null) {
                    WorldClockFragment worldClockFragment = WorldClockFragment.this;
                    worldClockFragment.startTimeZoneTask(worldClockFragment.mTimezoneAdapter, WorldClockFragment.this.mTimezoneModel, timeInMillis);
                }
                if (WorldClockFragment.this.mLocalDigitClock != null) {
                    WorldClockFragment.this.mLocalDigitClock.updateTime(timeInMillis);
                }
                if (WorldClockFragment.this.mClockBgView != null) {
                    WorldClockFragment.this.mClockBgView.updateClockBgView(WorldClockFragment.this.mContrastCalendar);
                }
                if (WorldClockFragment.this.mClockView != null) {
                    WorldClockFragment.this.mClockView.updateClockView(WorldClockFragment.this.mContrastCalendar);
                }
                WorldClockFragment.this.mRulerView.announceForAccessibility(cityObj.mCityName + WorldClockFragment.this.mTimezoneAdapter.getItemTime(i) + WorldClockFragment.this.getResources().getString(R.string.worldclock_local_time) + WorldClockFragment.this.mLocalDigitClock.getmTimeDisplay().getText().toString());
            }

            @Override // com.android.deskclock.worldclock.RulerView.OnValueChangeListener
            public void onReleaseValueChanged(RulerView rulerView, int i2, int i3) {
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTimeInMillis(System.currentTimeMillis());
                if (WorldClockFragment.this.mContrastCalendar == null) {
                    WorldClockFragment.this.mContrastCalendar = Calendar.getInstance();
                }
                WorldClockFragment.this.mContrastCalendar.set(12, calendar2.get(12));
                WorldClockFragment.this.mContrastCalendar.set(13, calendar2.get(13));
                WorldClockFragment.this.mContrastCalendar.set(14, calendar2.get(14));
                WorldClockFragment.this.mContrastCalendar.add(11, i3 - i2);
                long timeInMillis = WorldClockFragment.this.mContrastCalendar.getTimeInMillis();
                if (WorldClockFragment.this.mLocalDigitClock != null) {
                    WorldClockFragment.this.mLocalDigitClock.updateTime(timeInMillis);
                }
                if (WorldClockFragment.this.mClockBgView != null) {
                    WorldClockFragment.this.mClockBgView.updateClockBgView(WorldClockFragment.this.mContrastCalendar);
                }
                if (WorldClockFragment.this.mClockView != null) {
                    WorldClockFragment.this.mClockView.updateClockView(WorldClockFragment.this.mContrastCalendar);
                }
            }

            @Override // com.android.deskclock.worldclock.RulerView.OnValueChangeListener
            public void onChangeStop() {
                if (WorldClockFragment.this.mTimezoneAdapter != null) {
                    WorldClockFragment worldClockFragment = WorldClockFragment.this;
                    worldClockFragment.startTimeZoneTask(worldClockFragment.mTimezoneAdapter, WorldClockFragment.this.mTimezoneModel, System.currentTimeMillis());
                }
                WorldClockFragment.this.mRulerView.setContentDescription(cityObj.mCityName + WorldClockFragment.this.mTimezoneAdapter.getItemTime(i) + WorldClockFragment.this.getResources().getString(R.string.worldclock_local_time) + WorldClockFragment.this.mLocalDigitClock.getmTimeDisplay().getText().toString());
            }

            @Override // com.android.deskclock.worldclock.RulerView.OnValueChangeListener
            public void onReleaseStop() {
                if (WorldClockFragment.this.mLocalDigitClock != null) {
                    WorldClockFragment.this.mLocalDigitClock.updateTime(System.currentTimeMillis());
                }
                if (WorldClockFragment.this.mClockBgView != null) {
                    WorldClockFragment.this.mClockBgView.updateClockBgView(Calendar.getInstance());
                }
                if (WorldClockFragment.this.mClockView != null) {
                    WorldClockFragment.this.mClockView.updateClockView(WorldClockFragment.this.mContrastCalendar);
                }
                WorldClockFragment.this.mContrastMode = false;
                WorldClockFragment.this.mTimezoneAdapter.setContrastMode(false);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:22:0x00c7  */
    public void updateLayout(View view) {
        float dimension;
        int dimension2 = (int) this.mActivity.getResources().getDimension(R.dimen.world_clock_root_view_start);
        if (!Util.isWideMode(this.mActivity) && Util.isPadOrientationLand(this.mActivity)) {
            dimension = this.mActivity.getResources().getDimension(R.dimen.world_clock_root_view_pad_not_wide_start);
        } else if (Util.isPadOrientationLand(this.mActivity) && !this.mActivity.isInMultiWindowMode()) {
            dimension = this.mActivity.getResources().getDimension(R.dimen.world_clock_root_view_pad_land_start);
        } else {
            if (Util.isInInternalScreen(this.mActivity) && Util.isFoldDevice(this.mActivity) && !this.mActivity.isInMultiWindowMode()) {
                dimension = this.mActivity.getResources().getDimension(R.dimen.alarm_clock_root_view_fold_padding_start);
            }
            this.mRootView.setPadding(dimension2, 0, dimension2, 0);
            AlarmRecyclerView alarmRecyclerView = (AlarmRecyclerView) view.findViewById(android.R.id.list);
            this.mTimezoneLv = alarmRecyclerView;
            alarmRecyclerView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.6
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view2, MotionEvent motionEvent) {
                    if (motionEvent.getAction() != 0 || WorldClockFragment.this.mRulerView == null || WorldClockFragment.this.mRulerView.getVisibility() != 0) {
                        return false;
                    }
                    WorldClockFragment.this.mRulerView.cancelInteraction();
                    WorldClockFragment.this.mRulerView.setOnSeekBarChangeListener(null);
                    WorldClockFragment.this.mRulerView.setVisibility(8);
                    WorldClockFragment.this.mCurrSelectedIndex = -1;
                    WorldClockFragment.this.cancleContrastMode();
                    WorldClockFragment.this.mTimezoneAdapter.setSelected(-1);
                    WorldClockFragment.this.mTimezoneAdapter.notifyDataSetChanged();
                    return false;
                }
            });
            setRvLayoutMode();
            this.mTimezoneLv.setVerticalScrollBarEnabled(true);
            this.mTimezoneLv.setSpringEnabled(false);
            this.mTimezoneAdapter = new TimezoneAdapter(this.mActivity, this.mTimezoneLv);
            this.callback = new ItemTouchHelperCallback(new ItemTouchHelperCallback.ItemTouchHelperAdapter() { // from class: com.android.deskclock.worldclock.WorldClockFragment.7
                @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
                public boolean onDragStart(RecyclerView.ViewHolder viewHolder, int i) {
                    return false;
                }

                @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
                public boolean onItemMove(int i, int i2) {
                    WorldClockFragment.this.mTimezoneAdapter.onItemMove(i, i2);
                    WorldClockFragment.this.mUserChangeOrder = true;
                    return true;
                }

                @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
                public void onDragEnd(RecyclerView.ViewHolder viewHolder, int i) {
                    WorldClockFragment.this.mTimezoneAdapter.notifyDataSetChanged();
                }

                @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
                public int onMovementFlags() {
                    return WorldClockFragment.this.layoutManager.getSpanCount() >= 2 ? 15 : 3;
                }
            }, this.mActivity);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this.callback);
            this.mItemTouchHelper = itemTouchHelper;
            itemTouchHelper.attachToRecyclerView(this.mTimezoneLv);
            if (Util.isFoldDevice(this.mActivity)) {
                this.mTimezoneAdapter.setInternalScreen(Util.isInInternalScreen(this.mActivity));
            }
            this.mTimezoneAdapter.setOnItemClickListener(new TimezoneAdapter.OnItemClickListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.8
                @Override // com.android.deskclock.worldclock.TimezoneAdapter.OnItemClickListener
                public void onTimezoneClick(int i, CityObj cityObj, Calendar calendar) {
                    if (WorldClockFragment.this.mContrastMode) {
                        return;
                    }
                    if (WorldClockFragment.this.mTimezoneAdapter.isInActionMode()) {
                        WorldClockFragment.this.mTimezoneAdapter.toggleItemChecked(i);
                        return;
                    }
                    if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
                        return;
                    }
                    if (WorldClockFragment.this.mCurrSelectedIndex == -1) {
                        WorldClockFragment.this.mCurrSelectedIndex = i;
                        WorldClockFragment.this.mTimezoneAdapter.setSelected(i);
                        MiuiFolme.animateRulerShow(WorldClockFragment.this.mRulerView, WorldClockFragment.this.startWidth, WorldClockFragment.this.endWidth);
                        WorldClockFragment.this.changeRulerValue(cityObj);
                        WorldClockFragment.this.changeSeekBarListener(i, cityObj, calendar);
                    } else if (WorldClockFragment.this.mCurrSelectedIndex == i) {
                        WorldClockFragment.this.mCurrSelectedIndex = -1;
                        MiuiFolme.animateRulerHide(WorldClockFragment.this.mRulerView, WorldClockFragment.this.endWidth, WorldClockFragment.this.startWidth);
                        WorldClockFragment.this.mTimezoneAdapter.setSelected(-1);
                        WorldClockFragment.this.mRulerView.setOnSeekBarChangeListener(null);
                    } else {
                        WorldClockFragment.this.mCurrSelectedIndex = i;
                        WorldClockFragment.this.mTimezoneAdapter.setSelected(i);
                        WorldClockFragment.this.moveRulerValue(cityObj);
                        WorldClockFragment.this.changeRulerValue(cityObj);
                        WorldClockFragment.this.changeSeekBarListener(i, cityObj, calendar);
                    }
                    WorldClockFragment.this.mTimezoneAdapter.notifyDataSetChanged();
                    StatHelper.deskclockEvent(StatHelper.EVENT_CLICK_WORLD_CLOCK_ITEM_COUNT);
                    OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.CLOCK_ITEM_CLICK);
                    WorldClockFragment.this.mRulerView.setContentDescription(cityObj.mCityName + WorldClockFragment.this.mTimezoneAdapter.getItemTime(i) + WorldClockFragment.this.getResources().getString(R.string.worldclock_local_time) + WorldClockFragment.this.mLocalDigitClock.getmTimeDisplay().getText().toString());
                }
            });
            this.mRulerView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.9
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    WorldClockFragment.this.mRulerView.requestFocus();
                    WorldClockFragment.this.mRulerView.sendAccessibilityEvent(8);
                    WorldClockFragment.this.mTimezoneLv.setFocusable(false);
                }
            });
            this.mTimezoneAdapter.setOnLongClickListener(new TimezoneAdapter.OnLongClickListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.10
                @Override // com.android.deskclock.worldclock.TimezoneAdapter.OnLongClickListener
                public boolean onLongClick(int i, RecyclerView.ViewHolder viewHolder) {
                    if (!WorldClockFragment.this.mTimezoneAdapter.isInActionMode() && WorldClockFragment.this.mTimezoneAdapter != null && WorldClockFragment.this.mActivity != null && TabViewModel.TAB_CLOCK.equals(TabNavigatorContentFragment.mCurrTab)) {
                        WorldClockFragment.this.mTimezoneAdapter.setItemChecked(i, true);
                        if (WorldClockFragment.this.mActivity != null) {
                            WorldClockFragment.this.mActivity.setNavigationForActionMode(true);
                        }
                        if (WorldClockFragment.this.mRulerView.getVisibility() == 0) {
                            MiuiFolme.animateRulerHide(WorldClockFragment.this.mRulerView, WorldClockFragment.this.endWidth, WorldClockFragment.this.startWidth);
                        }
                        WorldClockFragment.this.mCurrSelectedIndex = -1;
                        WorldClockFragment.this.mTimezoneAdapter.cancelContrastMode();
                        WorldClockFragment.this.mTimezoneAdapter.startActionMode(WorldClockFragment.this.mMultiChoiceModeListener, (TabNavigatorContentFragment) WorldClockFragment.this.getParentFragment());
                        return true;
                    }
                    if (WorldClockFragment.this.mItemTouchHelper != null) {
                        viewHolder.itemView.setHapticFeedbackEnabled(false);
                        WorldClockFragment.this.mItemTouchHelper.startDrag(viewHolder);
                    }
                    return false;
                }
            });
            this.mTimezoneAdapter.setOnIvTouchListener(new TimezoneAdapter.OnIvTouchListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.11
                @Override // com.android.deskclock.worldclock.TimezoneAdapter.OnIvTouchListener
                public void onCancelTouch(RecyclerView.ViewHolder viewHolder) {
                }

                @Override // com.android.deskclock.worldclock.TimezoneAdapter.OnIvTouchListener
                public void onIvTouch(RecyclerView.ViewHolder viewHolder) {
                    WorldClockFragment.this.mItemTouchHelper.startDrag(viewHolder);
                }
            });
            this.mTimezoneAdapter.setOnActionAnimListener(new EditableAdapter.OnActionAnimListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.12
                @Override // com.android.deskclock.view.list.EditableAdapter.OnActionAnimListener
                public void onAnimStart() {
                }

                @Override // com.android.deskclock.view.list.EditableAdapter.OnActionAnimListener
                public void onAnimStop() {
                    int i = 0;
                    if (!WorldClockFragment.this.mUserChangeOrder || !WorldClockFragment.this.mIsDeleteData) {
                        if (WorldClockFragment.this.mUserChangeOrder || !WorldClockFragment.this.mIsDeleteData) {
                            return;
                        }
                        WorldClockFragment.this.mIsDeleteData = false;
                        if (WorldClockFragment.this.mPrepareToDelete != null) {
                            while (i < WorldClockFragment.this.mPrepareToDelete.length) {
                                WorldClockFragment.this.mNeedHandleWidgetData = true;
                                if (WorldClockFragment.this.mPrepareToDelete[i] < 0) {
                                    return;
                                }
                                WorldClockFragment.this.mActivity.getContentResolver().delete(ContentUris.withAppendedId(WorldClock.CONTENT_URI, WorldClockFragment.this.mPrepareToDelete[i]), null, null);
                                i++;
                            }
                        }
                        WorldClockFragment.this.mPrepareToDelete = null;
                        Util.playDeleteRingtone();
                        return;
                    }
                    if (WorldClockFragment.this.mPrepareToDeletePos != null) {
                        while (i < WorldClockFragment.this.mPrepareToDeletePos.length) {
                            if (WorldClockFragment.this.mPrepareToDeletePos[i] < 0) {
                                return;
                            }
                            int i2 = i + 1;
                            for (int i3 = i2; i3 < WorldClockFragment.this.mPrepareToDeletePos.length; i3++) {
                                if (WorldClockFragment.this.mPrepareToDeletePos[i] > WorldClockFragment.this.mPrepareToDeletePos[i3]) {
                                    int i4 = WorldClockFragment.this.mPrepareToDeletePos[i];
                                    WorldClockFragment.this.mPrepareToDeletePos[i] = WorldClockFragment.this.mPrepareToDeletePos[i3];
                                    WorldClockFragment.this.mPrepareToDeletePos[i3] = i4;
                                }
                            }
                            i = i2;
                        }
                        for (int length = WorldClockFragment.this.mPrepareToDelete.length - 1; length >= 0 && length < WorldClockFragment.this.mPrepareToDeletePos.length; length--) {
                            WorldClockFragment.this.mTimezoneAdapter.getShowDataList().remove(WorldClockFragment.this.mPrepareToDeletePos[length]);
                        }
                        WorldClockFragment.this.mTimezoneAdapter.notifyDataSetChanged();
                    }
                    Util.playDeleteRingtone();
                    WorldClockFragment.this.mPrepareToDeletePos = null;
                }
            });
            this.mTimezoneLv.setPadding(0, 0, 0, 900);
            TimezoneModel timezoneModel = new TimezoneModel(this.mActivity.getApplicationContext(), new TimezoneObserverImp(this));
            this.mTimezoneModel = timezoneModel;
            timezoneModel.startLoad();
        }
        dimension2 = (int) dimension;
        this.mRootView.setPadding(dimension2, 0, dimension2, 0);
        AlarmRecyclerView alarmRecyclerView2 = (AlarmRecyclerView) view.findViewById(android.R.id.list);
        this.mTimezoneLv = alarmRecyclerView2;
        alarmRecyclerView2.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.6
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view2, MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || WorldClockFragment.this.mRulerView == null || WorldClockFragment.this.mRulerView.getVisibility() != 0) {
                    return false;
                }
                WorldClockFragment.this.mRulerView.cancelInteraction();
                WorldClockFragment.this.mRulerView.setOnSeekBarChangeListener(null);
                WorldClockFragment.this.mRulerView.setVisibility(8);
                WorldClockFragment.this.mCurrSelectedIndex = -1;
                WorldClockFragment.this.cancleContrastMode();
                WorldClockFragment.this.mTimezoneAdapter.setSelected(-1);
                WorldClockFragment.this.mTimezoneAdapter.notifyDataSetChanged();
                return false;
            }
        });
        setRvLayoutMode();
        this.mTimezoneLv.setVerticalScrollBarEnabled(true);
        this.mTimezoneLv.setSpringEnabled(false);
        this.mTimezoneAdapter = new TimezoneAdapter(this.mActivity, this.mTimezoneLv);
        this.callback = new ItemTouchHelperCallback(new ItemTouchHelperCallback.ItemTouchHelperAdapter() { // from class: com.android.deskclock.worldclock.WorldClockFragment.7
            @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
            public boolean onDragStart(RecyclerView.ViewHolder viewHolder, int i) {
                return false;
            }

            @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
            public boolean onItemMove(int i, int i2) {
                WorldClockFragment.this.mTimezoneAdapter.onItemMove(i, i2);
                WorldClockFragment.this.mUserChangeOrder = true;
                return true;
            }

            @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
            public void onDragEnd(RecyclerView.ViewHolder viewHolder, int i) {
                WorldClockFragment.this.mTimezoneAdapter.notifyDataSetChanged();
            }

            @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
            public int onMovementFlags() {
                return WorldClockFragment.this.layoutManager.getSpanCount() >= 2 ? 15 : 3;
            }
        }, this.mActivity);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(this.callback);
        this.mItemTouchHelper = itemTouchHelper2;
        itemTouchHelper2.attachToRecyclerView(this.mTimezoneLv);
        if (Util.isFoldDevice(this.mActivity)) {
            this.mTimezoneAdapter.setInternalScreen(Util.isInInternalScreen(this.mActivity));
        }
        this.mTimezoneAdapter.setOnItemClickListener(new TimezoneAdapter.OnItemClickListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.8
            @Override // com.android.deskclock.worldclock.TimezoneAdapter.OnItemClickListener
            public void onTimezoneClick(int i, CityObj cityObj, Calendar calendar) {
                if (WorldClockFragment.this.mContrastMode) {
                    return;
                }
                if (WorldClockFragment.this.mTimezoneAdapter.isInActionMode()) {
                    WorldClockFragment.this.mTimezoneAdapter.toggleItemChecked(i);
                    return;
                }
                if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
                    return;
                }
                if (WorldClockFragment.this.mCurrSelectedIndex == -1) {
                    WorldClockFragment.this.mCurrSelectedIndex = i;
                    WorldClockFragment.this.mTimezoneAdapter.setSelected(i);
                    MiuiFolme.animateRulerShow(WorldClockFragment.this.mRulerView, WorldClockFragment.this.startWidth, WorldClockFragment.this.endWidth);
                    WorldClockFragment.this.changeRulerValue(cityObj);
                    WorldClockFragment.this.changeSeekBarListener(i, cityObj, calendar);
                } else if (WorldClockFragment.this.mCurrSelectedIndex == i) {
                    WorldClockFragment.this.mCurrSelectedIndex = -1;
                    MiuiFolme.animateRulerHide(WorldClockFragment.this.mRulerView, WorldClockFragment.this.endWidth, WorldClockFragment.this.startWidth);
                    WorldClockFragment.this.mTimezoneAdapter.setSelected(-1);
                    WorldClockFragment.this.mRulerView.setOnSeekBarChangeListener(null);
                } else {
                    WorldClockFragment.this.mCurrSelectedIndex = i;
                    WorldClockFragment.this.mTimezoneAdapter.setSelected(i);
                    WorldClockFragment.this.moveRulerValue(cityObj);
                    WorldClockFragment.this.changeRulerValue(cityObj);
                    WorldClockFragment.this.changeSeekBarListener(i, cityObj, calendar);
                }
                WorldClockFragment.this.mTimezoneAdapter.notifyDataSetChanged();
                StatHelper.deskclockEvent(StatHelper.EVENT_CLICK_WORLD_CLOCK_ITEM_COUNT);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.CLOCK_ITEM_CLICK);
                WorldClockFragment.this.mRulerView.setContentDescription(cityObj.mCityName + WorldClockFragment.this.mTimezoneAdapter.getItemTime(i) + WorldClockFragment.this.getResources().getString(R.string.worldclock_local_time) + WorldClockFragment.this.mLocalDigitClock.getmTimeDisplay().getText().toString());
            }
        });
        this.mRulerView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                WorldClockFragment.this.mRulerView.requestFocus();
                WorldClockFragment.this.mRulerView.sendAccessibilityEvent(8);
                WorldClockFragment.this.mTimezoneLv.setFocusable(false);
            }
        });
        this.mTimezoneAdapter.setOnLongClickListener(new TimezoneAdapter.OnLongClickListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.10
            @Override // com.android.deskclock.worldclock.TimezoneAdapter.OnLongClickListener
            public boolean onLongClick(int i, RecyclerView.ViewHolder viewHolder) {
                if (!WorldClockFragment.this.mTimezoneAdapter.isInActionMode() && WorldClockFragment.this.mTimezoneAdapter != null && WorldClockFragment.this.mActivity != null && TabViewModel.TAB_CLOCK.equals(TabNavigatorContentFragment.mCurrTab)) {
                    WorldClockFragment.this.mTimezoneAdapter.setItemChecked(i, true);
                    if (WorldClockFragment.this.mActivity != null) {
                        WorldClockFragment.this.mActivity.setNavigationForActionMode(true);
                    }
                    if (WorldClockFragment.this.mRulerView.getVisibility() == 0) {
                        MiuiFolme.animateRulerHide(WorldClockFragment.this.mRulerView, WorldClockFragment.this.endWidth, WorldClockFragment.this.startWidth);
                    }
                    WorldClockFragment.this.mCurrSelectedIndex = -1;
                    WorldClockFragment.this.mTimezoneAdapter.cancelContrastMode();
                    WorldClockFragment.this.mTimezoneAdapter.startActionMode(WorldClockFragment.this.mMultiChoiceModeListener, (TabNavigatorContentFragment) WorldClockFragment.this.getParentFragment());
                    return true;
                }
                if (WorldClockFragment.this.mItemTouchHelper != null) {
                    viewHolder.itemView.setHapticFeedbackEnabled(false);
                    WorldClockFragment.this.mItemTouchHelper.startDrag(viewHolder);
                }
                return false;
            }
        });
        this.mTimezoneAdapter.setOnIvTouchListener(new TimezoneAdapter.OnIvTouchListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.11
            @Override // com.android.deskclock.worldclock.TimezoneAdapter.OnIvTouchListener
            public void onCancelTouch(RecyclerView.ViewHolder viewHolder) {
            }

            @Override // com.android.deskclock.worldclock.TimezoneAdapter.OnIvTouchListener
            public void onIvTouch(RecyclerView.ViewHolder viewHolder) {
                WorldClockFragment.this.mItemTouchHelper.startDrag(viewHolder);
            }
        });
        this.mTimezoneAdapter.setOnActionAnimListener(new EditableAdapter.OnActionAnimListener() { // from class: com.android.deskclock.worldclock.WorldClockFragment.12
            @Override // com.android.deskclock.view.list.EditableAdapter.OnActionAnimListener
            public void onAnimStart() {
            }

            @Override // com.android.deskclock.view.list.EditableAdapter.OnActionAnimListener
            public void onAnimStop() {
                int i = 0;
                if (!WorldClockFragment.this.mUserChangeOrder || !WorldClockFragment.this.mIsDeleteData) {
                    if (WorldClockFragment.this.mUserChangeOrder || !WorldClockFragment.this.mIsDeleteData) {
                        return;
                    }
                    WorldClockFragment.this.mIsDeleteData = false;
                    if (WorldClockFragment.this.mPrepareToDelete != null) {
                        while (i < WorldClockFragment.this.mPrepareToDelete.length) {
                            WorldClockFragment.this.mNeedHandleWidgetData = true;
                            if (WorldClockFragment.this.mPrepareToDelete[i] < 0) {
                                return;
                            }
                            WorldClockFragment.this.mActivity.getContentResolver().delete(ContentUris.withAppendedId(WorldClock.CONTENT_URI, WorldClockFragment.this.mPrepareToDelete[i]), null, null);
                            i++;
                        }
                    }
                    WorldClockFragment.this.mPrepareToDelete = null;
                    Util.playDeleteRingtone();
                    return;
                }
                if (WorldClockFragment.this.mPrepareToDeletePos != null) {
                    while (i < WorldClockFragment.this.mPrepareToDeletePos.length) {
                        if (WorldClockFragment.this.mPrepareToDeletePos[i] < 0) {
                            return;
                        }
                        int i2 = i + 1;
                        for (int i3 = i2; i3 < WorldClockFragment.this.mPrepareToDeletePos.length; i3++) {
                            if (WorldClockFragment.this.mPrepareToDeletePos[i] > WorldClockFragment.this.mPrepareToDeletePos[i3]) {
                                int i4 = WorldClockFragment.this.mPrepareToDeletePos[i];
                                WorldClockFragment.this.mPrepareToDeletePos[i] = WorldClockFragment.this.mPrepareToDeletePos[i3];
                                WorldClockFragment.this.mPrepareToDeletePos[i3] = i4;
                            }
                        }
                        i = i2;
                    }
                    for (int length = WorldClockFragment.this.mPrepareToDelete.length - 1; length >= 0 && length < WorldClockFragment.this.mPrepareToDeletePos.length; length--) {
                        WorldClockFragment.this.mTimezoneAdapter.getShowDataList().remove(WorldClockFragment.this.mPrepareToDeletePos[length]);
                    }
                    WorldClockFragment.this.mTimezoneAdapter.notifyDataSetChanged();
                }
                Util.playDeleteRingtone();
                WorldClockFragment.this.mPrepareToDeletePos = null;
            }
        });
        this.mTimezoneLv.setPadding(0, 0, 0, 900);
        TimezoneModel timezoneModel2 = new TimezoneModel(this.mActivity.getApplicationContext(), new TimezoneObserverImp(this));
        this.mTimezoneModel = timezoneModel2;
        timezoneModel2.startLoad();
    }

    private void setRvLayoutMode() {
        HyperGridLayoutManager hyperGridLayoutManager = new HyperGridLayoutManager(this.mActivity, 0);
        this.layoutManager = hyperGridLayoutManager;
        hyperGridLayoutManager.setMinCellWidth(MiuixUIUtils.dp2px(this.mActivity, 298.0f));
        this.layoutManager.setColumnSpacing(MiuixUIUtils.dp2px(this.mActivity, 12.0f));
        if (this.layoutManager.getSpanCount() == 1) {
            this.mNestedScrollView.setPadding((int) this.mActivity.getResources().getDimension(R.dimen.world_clock_layout_margin_start), 0, (int) this.mActivity.getResources().getDimension(R.dimen.world_clock_layout_margin_start), 0);
        }
        AlarmRecyclerView alarmRecyclerView = this.mTimezoneLv;
        if (alarmRecyclerView != null) {
            alarmRecyclerView.setLayoutManager(this.layoutManager);
        }
    }

    private void updateBlankPage() {
        TimezoneAdapter timezoneAdapter = this.mTimezoneAdapter;
        if (timezoneAdapter != null) {
            if (timezoneAdapter.getItemCount() == 0) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) this.mHeadView.getLayoutParams();
                if (this.mWorldClockBlankPage == null) {
                    ViewStub viewStub = (ViewStub) this.mRootView.findViewById(R.id.worldclock_blank_page_lite_stub);
                    if (Util.isPadOrientationLand(this.mActivity) && !Util.isInMultiWindowMode(this.mActivity)) {
                        layoutParams.gravity = 17;
                        layoutParams.setAnchorId(R.id.clock_land);
                        layoutParams.anchorGravity = 17;
                        this.mHeadView.setLayoutParams(layoutParams);
                        this.mNestedScrollView.setFocusable(false);
                    } else {
                        this.mWorldClockBlankPage = viewStub.inflate();
                    }
                }
                if (this.mWorldClockBlankPage != null) {
                    if (Util.isPadOrientationLand(this.mActivity) && !Util.isInMultiWindowMode(this.mActivity)) {
                        layoutParams.gravity = 17;
                        layoutParams.setAnchorId(R.id.clock_land);
                        layoutParams.anchorGravity = 17;
                        this.mHeadView.setLayoutParams(layoutParams);
                        this.mWorldClockBlankPage.setVisibility(8);
                        this.mTimezoneLv.setPadding(0, 0, 0, 700);
                        this.mNestedScrollView.setFocusable(false);
                    } else {
                        this.mWorldClockBlankPage.setVisibility(0);
                        this.mTimezoneLv.setPadding(0, 0, 0, 0);
                    }
                }
                this.mTimezoneLv.setVisibility(4);
                return;
            }
            View view = this.mWorldClockBlankPage;
            if (view != null) {
                view.setVisibility(8);
                this.mTimezoneLv.setPadding(0, 0, 0, 700);
            }
            this.mTimezoneLv.setVisibility(0);
            setPadLandScreenLayout();
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        TimezoneAdapter timezoneAdapter;
        super.onResume();
        if (this.mTicker != null && TabViewModel.TAB_CLOCK.equals(TabNavigatorContentFragment.mCurrTab) && this.mTickerStopped) {
            this.mTickerStopped = false;
            this.mTicker.run();
            if (!this.mContrastMode && (timezoneAdapter = this.mTimezoneAdapter) != null) {
                startTimeZoneTask(timezoneAdapter, this.mTimezoneModel, System.currentTimeMillis());
            }
        }
        LocalClockBgView localClockBgView = this.mClockBgView;
        if (localClockBgView != null) {
            localClockBgView.updateClockBgView(Calendar.getInstance());
        }
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) this.mHeadView.getLayoutParams();
        HeaderScrollBehavior headerScrollBehavior = new HeaderScrollBehavior(this.mActivity, null);
        this.headerScrollBehavior = headerScrollBehavior;
        layoutParams.setBehavior(headerScrollBehavior);
        ((NestedContentScrollBehavior) ((CoordinatorLayout.LayoutParams) this.mNestedScrollView.getLayoutParams()).getBehavior()).initContext(this.mActivity);
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IContentInsetState
    public void onContentInsetChanged(Rect rect) {
        super.onContentInsetChanged(rect);
        RulerView rulerView = this.mRulerView;
        if (rulerView != null) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) rulerView.getLayoutParams();
            layoutParams.bottomMargin = rect.bottom;
            this.mRulerView.setLayoutParams(layoutParams);
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        unregisterCoordinateScrollView(this.mNestedScrollView);
        super.onDestroyView();
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFragmentChange
    public void onEnter() {
        super.onEnter();
        Log.d(TAG, "onEnter: ");
        if (!MiuiSdk.isSuperLiteMode() && !MiuiSdk.isLiteV1StockMode()) {
            handleGuideShow();
            if (this.mCurrSelectedIndex != -1) {
                MiuiFolme.animateRulerHide(this.mRulerView, this.endWidth, this.startWidth);
                this.mCurrSelectedIndex = -1;
                cancleContrastMode();
                this.mContrastMode = false;
            }
        }
        Runnable runnable = this.mTicker;
        if (runnable == null || !this.mTickerStopped) {
            return;
        }
        this.mTickerStopped = false;
        runnable.run();
        TimezoneAdapter timezoneAdapter = this.mTimezoneAdapter;
        if (timezoneAdapter != null) {
            startTimeZoneTask(timezoneAdapter, this.mTimezoneModel, System.currentTimeMillis());
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IFragmentChange
    public void onLeave() {
        super.onLeave();
        if (!MiuiSdk.isSuperLiteMode() && !MiuiSdk.isLiteV1StockMode() && this.mCurrSelectedIndex != -1) {
            MiuiFolme.animateRulerHide(this.mRulerView, this.endWidth, this.startWidth);
            this.mCurrSelectedIndex = -1;
            cancleContrastMode();
            this.mContrastMode = false;
        }
        this.mTickerStopped = true;
        TimezoneAdapter timezoneAdapter = this.mTimezoneAdapter;
        if (timezoneAdapter == null || !timezoneAdapter.isInActionMode()) {
            return;
        }
        this.mTimezoneAdapter.finishActionMode();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mTickerStopped = true;
        GuidePopupWindow guidePopupWindow = this.guidePopupWindow;
        if (guidePopupWindow != null) {
            guidePopupWindow.dismiss();
            this.guidePopupWindow = null;
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimeFormatChanged() {
        TimezoneAdapter timezoneAdapter = this.mTimezoneAdapter;
        if (timezoneAdapter != null) {
            startTimeZoneTask(timezoneAdapter, this.mTimezoneModel, System.currentTimeMillis());
        }
        FormatClockForLocalTime formatClockForLocalTime = this.mLocalDigitClock;
        if (formatClockForLocalTime != null) {
            formatClockForLocalTime.resetTimeFormat();
        }
        LocalClockBgView localClockBgView = this.mClockBgView;
        if (localClockBgView != null) {
            localClockBgView.updateClockBgView(Calendar.getInstance());
        }
        if (this.mClockView != null) {
            this.mClockView.updateClockView(Calendar.getInstance());
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        DialogUtil.dismissDialogFragment(this.mDeleteConfirmDialog);
        this.mDeleteConfirmDialog = null;
        if (this.mCurrSelectedIndex != -1) {
            MiuiFolme.animateRulerHide(this.mRulerView, this.endWidth, this.startWidth);
            this.mCurrSelectedIndex = -1;
            cancleContrastMode();
            this.mContrastMode = false;
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        TimezoneModel timezoneModel = this.mTimezoneModel;
        if (timezoneModel != null) {
            timezoneModel.release();
        }
        Handler handler = this.mSecondTickHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.mSecondTickHandler = null;
        }
    }

    public void initClockView() {
        this.mClockView = (LocalClockView) this.mRootView.findViewById(R.id.clock_view);
        this.mClockBgView = (LocalClockBgView) this.mRootView.findViewById(R.id.clock_bg_view);
        this.mClockViewHeight = this.mClockView.getHeight();
        updateLocalTime(true);
        this.mDefaultTimeZoneId = TimeZone.getDefault().getID();
        TypefaceTextView typefaceTextView = (TypefaceTextView) this.mLocalDigitClock.findViewById(R.id.time_display);
        typefaceTextView.setTypefaceType(13);
        if (MiuiSdk.isSupportFontAnim()) {
            MiuiFont.setFont((TextView) this.mLocalDigitClock.findViewById(R.id.time_desc_local_only), MiuiFont.MI_PRO_REGULAR);
            MiuiFont.setFont((TextView) this.mLocalDigitClock.findViewById(R.id.date_display_with_year), MiuiFont.MI_PRO_REGULAR);
            MiuiFont.setFont((TextView) this.mLocalDigitClock.findViewById(R.id.am_pm), MiuiFont.MI_PRO_REGULAR);
        }
        if (Util.isFoldDevice(this.mActivity) && Util.isWideMode(this.mActivity)) {
            typefaceTextView.setTextSize(0, (int) this.mActivity.getResources().getDimension(R.dimen.worldclock_local_time_textSize_flod));
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mLocalDigitClock.getLayoutParams();
            layoutParams.topMargin = (int) this.mActivity.getResources().getDimension(R.dimen.clock_view_local_time_marginTop_fold);
            this.mLocalDigitClock.setLayoutParams(layoutParams);
            CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams) this.mHeadView.getLayoutParams();
            layoutParams2.height = (int) this.mActivity.getResources().getDimension(R.dimen.world_clock_view_height_flod);
            this.mHeadView.setLayoutParams(layoutParams2);
        }
    }

    private void setSingleMode() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mLocalDigitClock.getLayoutParams();
        layoutParams.addRule(20, 0);
        layoutParams.addRule(14, -1);
        layoutParams.setMarginStart(0);
        this.mLocalDigitClock.setLayoutParams(layoutParams);
    }

    public void handleActivityResult(String str) {
        if (!TextUtils.isEmpty(str) && !isCityExists(this.mActivity, str)) {
            insertCityZoneToDatabase(this.mActivity, str);
            StatHelper.deskclockEvent(StatHelper.EVENT_ADD_WORLD_CLOCK_COUNT);
            OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.CLOCK_ADD_TIMEZONE);
            return;
        }
        AlarmThreadPool.runOnUiThread(new Runnable() { // from class: com.android.deskclock.worldclock.WorldClockFragment.14
            @Override // java.lang.Runnable
            public void run() {
                Toast.makeText(DeskClockApp.getAppDEContext(), R.string.timezone_exist_error_message, 0).show();
            }
        });
    }

    private boolean isCityExists(Context context, String str) {
        boolean zMoveToFirst = false;
        Cursor cursorQuery = context.getContentResolver().query(WorldClock.CONTENT_URI, WorldClock.PROJECTION, "cityid_new=?", new String[]{str}, null);
        if (cursorQuery != null) {
            try {
                zMoveToFirst = cursorQuery.moveToFirst();
            } finally {
                cursorQuery.close();
            }
        }
        return zMoveToFirst;
    }

    private void insertCityZoneToDatabase(Context context, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorldClock.Columns.CITY_ID, str);
        context.getContentResolver().insert(WorldClock.CONTENT_URI, contentValues);
    }

    @Override // com.android.deskclock.base.BaseClockFragment
    protected String getTab() {
        return TabViewModel.TAB_CLOCK;
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimezoneChanged() {
        Log.i(TAG, "onTimezoneChanged");
        this.mDefaultTimeZoneId = TimeZone.getDefault().getID();
        updateLocalTime(true);
        if (this.mContrastMode || this.mTimezoneAdapter == null) {
            return;
        }
        updateTimezoneItem(System.currentTimeMillis());
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimeChanged() {
        Log.i(TAG, "onTimeChanged");
        updateLocalTime(true);
        if (this.mContrastMode || this.mTimezoneAdapter == null) {
            return;
        }
        updateTimezoneItem(System.currentTimeMillis());
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimeTick() {
        Log.i(TAG, "onTimeTick");
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.worldclock.WorldClockFragment.15
            @Override // java.lang.Runnable
            public void run() {
                if (!WorldClockFragment.this.mContrastMode && WorldClockFragment.this.mTimezoneAdapter != null && !WorldClockFragment.this.mTickerStopped && TabViewModel.TAB_CLOCK.equals(TabNavigatorContentFragment.mCurrTab) && WorldClockFragment.this.mSecondTickHandler != null) {
                    WorldClockFragment.this.mSecondTickHandler.sendMessage(WorldClockFragment.this.mSecondTickHandler.obtainMessage());
                }
                Calendar calendar = Calendar.getInstance();
                int i = calendar.get(11);
                int i2 = calendar.get(12);
                int i3 = calendar.get(13);
                if (WorldClockFragment.this.mContrastMode || WorldClockFragment.this.mClockBgView == null || i2 != 0 || i3 != 0) {
                    return;
                }
                if (i == 6) {
                    WorldClockFragment.this.mClockBgView.updateClockBgView(calendar);
                } else if (i == 18) {
                    WorldClockFragment.this.mClockBgView.updateClockBgView(calendar);
                }
            }
        });
    }

    private static class TimeZoneChangeAsyncTask extends AsyncTask<Void, Void, Void> {
        private long mTime;
        private TimezoneAdapter mTimezoneAdapter;
        private TimezoneModel mTimezoneModel;

        public TimeZoneChangeAsyncTask(WorldClockFragment worldClockFragment, TimezoneAdapter timezoneAdapter, TimezoneModel timezoneModel, long j) {
            this.mTimezoneAdapter = timezoneAdapter;
            this.mTimezoneModel = timezoneModel;
            this.mTime = j;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            this.mTimezoneAdapter.setTime(this.mTime);
            this.mTimezoneModel.resetShowData(this.mTime);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r2) {
            Log.i(WorldClockFragment.TAG, "updateTimezoneItem");
            this.mTimezoneAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTimeZoneTask(TimezoneAdapter timezoneAdapter, TimezoneModel timezoneModel, long j) {
        cancelTimeZoneTask();
        TimeZoneChangeAsyncTask timeZoneChangeAsyncTask = new TimeZoneChangeAsyncTask(this, timezoneAdapter, timezoneModel, j);
        this.mTimeZoneChangeAsyncTask = timeZoneChangeAsyncTask;
        timeZoneChangeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void cancelTimeZoneTask() {
        AsyncTask<Void, Void, Void> asyncTask = this.mTimeZoneChangeAsyncTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.mTimeZoneChangeAsyncTask = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTimezoneItem(long j) {
        Log.i(TAG, "updateTimezoneItem");
        this.mTimezoneAdapter.setTime(j);
        this.mTimezoneModel.resetShowData(j);
        this.mTimezoneAdapter.notifyDataSetChanged();
    }

    public void onSecondTick() {
        Log.i(TAG, "onSecondTick");
        if (this.mContrastMode) {
            return;
        }
        FormatClockForLocalTime formatClockForLocalTime = this.mLocalDigitClock;
        if (formatClockForLocalTime != null) {
            formatClockForLocalTime.updateTime(System.currentTimeMillis());
        }
        Calendar calendar = Calendar.getInstance();
        LocalClockView localClockView = this.mClockView;
        if (localClockView != null) {
            localClockView.updateClockView(calendar);
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment, com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onDataChanged() {
        TimezoneModel timezoneModel = this.mTimezoneModel;
        if (timezoneModel != null) {
            timezoneModel.startLoad();
        }
    }

    private void updateLocalTime(boolean z) {
        Log.i("updateLocalTime");
        if (z || this.mLocalCity == null) {
            CityZoneHelper.init();
            this.mLocalCity = CityZoneHelper.getCityByTimeZone(TimeZone.getDefault().getID());
        }
        if (this.mLocalDigitClock != null) {
            if (this.mDefaultTimeZoneId == null) {
                this.mDefaultTimeZoneId = TimeZone.getDefault().getID();
            }
            this.mLocalDigitClock.updateTime(TimeZone.getTimeZone(this.mDefaultTimeZoneId), System.currentTimeMillis());
        }
        Calendar calendar = Calendar.getInstance();
        LocalClockBgView localClockBgView = this.mClockBgView;
        if (localClockBgView != null) {
            localClockBgView.updateClockBgView(calendar);
        }
        LocalClockView localClockView = this.mClockView;
        if (localClockView != null) {
            localClockView.updateClockView(calendar);
        }
    }

    @Override // com.android.deskclock.base.BaseClockFragment
    public void destroyActionMode() {
        if (this.mTimezoneAdapter != null) {
            if (this.mActivity != null) {
                this.mActivity.setNavigationForActionMode(false);
            }
            this.mTimezoneAdapter.finishActionMode();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancleContrastMode() {
        TimezoneAdapter timezoneAdapter = this.mTimezoneAdapter;
        if (timezoneAdapter != null) {
            timezoneAdapter.cancelContrastMode();
        }
    }

    public void handleGuideShow() {
        AlarmRecyclerView alarmRecyclerView = this.mTimezoneLv;
        if (alarmRecyclerView != null) {
            alarmRecyclerView.postDelayed(new Runnable() { // from class: com.android.deskclock.worldclock.WorldClockFragment.16
                @Override // java.lang.Runnable
                public void run() {
                    WorldClockFragment.this.showGuideWindow();
                }
            }, 100L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showGuideWindow() {
        TimezoneAdapter timezoneAdapter;
        Log.d("showGuideWindow mCurrTab:" + TabNavigatorContentFragment.mCurrTab);
        if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode() || MiuiSdk.isMiuiMiddle() || TabNavigatorContentFragment.mCurrTab != TabViewModel.TAB_CLOCK || isGuideShowed() || (timezoneAdapter = this.mTimezoneAdapter) == null || timezoneAdapter.getItemCount() == 0 || this.mTimezoneLv == null) {
            return;
        }
        GuidePopupWindow guidePopupWindow = this.guidePopupWindow;
        if (guidePopupWindow != null) {
            guidePopupWindow.dismiss();
        }
        GuidePopupWindow guidePopupWindow2 = new GuidePopupWindow(this.mActivity);
        this.guidePopupWindow = guidePopupWindow2;
        guidePopupWindow2.setArrowMode(18);
        this.guidePopupWindow.setGuideText(R.string.city_time_contrast_desc);
        this.guidePopupWindow.setShowDuration(R2.color.word_photo_color);
        String lastTab = FabControllerNew.getInstance().getLastTab();
        if (Util.isPadOrientationLand(this.mActivity)) {
            if (lastTab != null && lastTab.equals(TabViewModel.TAB_ALARM)) {
                this.guidePopupWindow.show(this.mTimezoneLv, -800, 0, true);
            } else {
                this.guidePopupWindow.show(this.mTimezoneLv, 900, 0, true);
            }
        } else if ((Util.isOrientationPortrait(this.mActivity) || Util.isFoldOrientationPortrait(this.mActivity)) && lastTab != null && !lastTab.equals(TabViewModel.TAB_ALARM)) {
            this.guidePopupWindow.show(this.mTimezoneLv, 100, 0, true);
        } else if (Util.isRtl() && lastTab != null && lastTab.equals(TabViewModel.TAB_ALARM)) {
            this.guidePopupWindow.show(this.mTimezoneLv, 800, 0, true);
        } else {
            this.guidePopupWindow.show(this.mTimezoneLv, 0, 0, true);
        }
        setGuideShowed(true);
    }

    private boolean isGuideShowed() {
        return FBEUtil.getDefaultSharedPreferences(getContext()).getBoolean(KEY_TIMEZONE_ITEM_SHOW, false);
    }

    private void setGuideShowed(boolean z) {
        FBEUtil.getDefaultSharedPreferences(getContext()).edit().putBoolean(KEY_TIMEZONE_ITEM_SHOW, true).apply();
    }

    public static class TimezoneObserverImp implements TimezoneModel.TimezoneObserver {
        private WeakReference<WorldClockFragment> mReference;

        public TimezoneObserverImp(WorldClockFragment worldClockFragment) {
            this.mReference = new WeakReference<>(worldClockFragment);
        }

        @Override // com.android.deskclock.worldclock.TimezoneModel.TimezoneObserver
        public void onTimezoneLoaded(List<CityObj> list, List<TimezoneModel.TimezoneBean> list2) {
            WorldClockFragment worldClockFragment = this.mReference.get();
            if (worldClockFragment != null) {
                worldClockFragment.onTimezoneLoadedForObserver(list, list2);
            }
        }

        @Override // com.android.deskclock.worldclock.TimezoneModel.TimezoneObserver
        public void onTimezoneChanged(List<CityObj> list, List<TimezoneModel.TimezoneBean> list2) {
            WorldClockFragment worldClockFragment = this.mReference.get();
            if (worldClockFragment != null) {
                worldClockFragment.onTimezoneChangedForObserver(list, list2);
            }
        }

        @Override // com.android.deskclock.worldclock.TimezoneModel.TimezoneObserver
        public void onTimezoneChangedForWidget(List<CityObj> list) {
            WorldClockFragment worldClockFragment = this.mReference.get();
            if (worldClockFragment != null) {
                worldClockFragment.onTimezoneChangedForWidget(list);
            }
        }
    }

    public void onTimezoneLoadedForObserver(List<CityObj> list, List<TimezoneModel.TimezoneBean> list2) {
        TimezoneAdapter timezoneAdapter = this.mTimezoneAdapter;
        if (timezoneAdapter != null) {
            timezoneAdapter.initData(list, list2);
            this.mTimezoneAdapter.notifyDataSetChanged();
        }
        updateBlankPage();
    }

    public void onTimezoneChangedForObserver(List<CityObj> list, List<TimezoneModel.TimezoneBean> list2) {
        TimezoneAdapter timezoneAdapter = this.mTimezoneAdapter;
        if (timezoneAdapter != null) {
            timezoneAdapter.initData(list, list2);
            this.mTimezoneAdapter.notifyDataSetChanged();
            handleGuideShow();
        }
        updateBlankPage();
    }

    public void onTimezoneChangedForWidget(List<CityObj> list) {
        if (this.mNeedHandleWidgetData) {
            this.mNeedHandleWidgetData = false;
            int i = WorldClockEditActivity.getmShowListSize(this.mActivity);
            String[] showCitiesIds = WorldClockEditActivity.getShowCitiesIds(this.mActivity);
            if (i == 0 || showCitiesIds == null) {
                return;
            }
            ArrayList arrayList = new ArrayList();
            Iterator<CityObj> it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().mCityId);
            }
            for (String str : showCitiesIds) {
                if (!arrayList.contains(str)) {
                    i--;
                }
            }
            WorldClockEditActivity.saveShowListSize(this.mActivity, list, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setContentDescription() {
        try {
            if (this.mTimezoneAdapter.isAllItemsChecked()) {
                this.mActivity.getWindow().getDecorView().findViewById(16908314).setContentDescription(this.mActivity.getResources().getString(R.string.miuix_appcompat_deselect_all_description));
            } else {
                this.mActivity.getWindow().getDecorView().findViewById(16908314).setContentDescription(this.mActivity.getResources().getString(R.string.miuix_appcompat_select_all_description));
            }
        } catch (Throwable unused) {
        }
    }

    /* JADX WARN: Code duplicated, block: B:29:0x009a  */
    /* JADX WARN: Code duplicated, block: B:32:0x00af  */
    @Override // miuix.appcompat.app.Fragment, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        float dimension;
        GuidePopupWindow guidePopupWindow;
        TimezoneAdapter timezoneAdapter;
        super.onResponsiveLayout(configuration, screenSpec, z);
        LocalClockView localClockView = this.mClockView;
        if (localClockView != null && !mClockIsInActionMode) {
            localClockView.setVisibility(0);
            this.mNestedScrollView.setTranslationY(0.0f);
            HeaderScrollBehavior headerScrollBehavior = this.headerScrollBehavior;
            if (headerScrollBehavior != null) {
                headerScrollBehavior.setMinLocalTimeScale(this.mCoordinatorLayout, this.mHeadView, this.mNestedScrollView);
            }
        }
        setPadLandScreenLayout();
        int dimension2 = (int) this.mActivity.getResources().getDimension(R.dimen.world_clock_root_view_start);
        if (!Util.isWideMode(this.mActivity) && Util.isPadOrientationLand(this.mActivity)) {
            dimension = this.mActivity.getResources().getDimension(R.dimen.world_clock_root_view_pad_not_wide_start);
        } else if (Util.isPadOrientationLand(this.mActivity) && !this.mActivity.isInMultiWindowMode()) {
            dimension = this.mActivity.getResources().getDimension(R.dimen.world_clock_root_view_pad_land_start);
        } else {
            if (Util.isInInternalScreen(this.mActivity) && Util.isFoldDevice(this.mActivity) && !this.mActivity.isInMultiWindowMode()) {
                dimension = this.mActivity.getResources().getDimension(R.dimen.alarm_clock_root_view_fold_padding_start);
            }
            guidePopupWindow = this.guidePopupWindow;
            if (guidePopupWindow != null) {
                guidePopupWindow.dismiss();
                this.guidePopupWindow = null;
            }
            setRootViewPadding(dimension2, 0);
            setRvLayoutMode();
            updateBlankPage();
            if (isInActionMode()) {
                this.mTimezoneAdapter.finishActionMode();
                this.mTimezoneAdapter.notifyDataSetChanged();
            }
            if (Util.isFoldDevice(this.mActivity) || (timezoneAdapter = this.mTimezoneAdapter) == null) {
            }
            timezoneAdapter.setInternalScreen(Util.isInInternalScreen(this.mActivity));
            return;
        }
        dimension2 = (int) dimension;
        guidePopupWindow = this.guidePopupWindow;
        if (guidePopupWindow != null) {
            guidePopupWindow.dismiss();
            this.guidePopupWindow = null;
        }
        setRootViewPadding(dimension2, 0);
        setRvLayoutMode();
        updateBlankPage();
        if (isInActionMode()) {
            this.mTimezoneAdapter.finishActionMode();
            this.mTimezoneAdapter.notifyDataSetChanged();
        }
        if (Util.isFoldDevice(this.mActivity)) {
        }
    }

    public boolean isInActionMode() {
        TimezoneAdapter timezoneAdapter = this.mTimezoneAdapter;
        if (timezoneAdapter != null) {
            return timezoneAdapter.isInActionMode();
        }
        return false;
    }

    @Override // com.android.deskclock.base.BaseClockFragment
    protected void initView() {
        super.initView();
        this.mInitialized = true;
    }
}
