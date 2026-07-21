package com.android.deskclock.worldclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.R2;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.util.CityZoneHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.GestureLineUtil;
import com.android.deskclock.util.LocalCityZoneUtil;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.view.list.AlarmRecyclerView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import miuix.appcompat.app.ActionBar;
import miuix.pinyin.utilities.ChinesePinyinConverter;

/* JADX INFO: loaded from: classes.dex */
public class WorldClockEditActivity extends BaseActivity implements TimezoneSearchAdapter.InsertDataListener {
    public static final String CITY_LIST_SIZE = "timezone_list_size";
    public static final int DEFAULT_MAX_SIZE = 4;
    public static final String LOCAL_CITY_ID = "0";
    public static final float NORMAL_DPI = 2.75f;
    public static final int SET_RESULT_FOR_WIDGET = 3004;
    public static final String SHOW_CITIES_IDS = "timezone_show_ids";
    public static final String TAG = "DC:WorldClockEditActivity";
    public Rect containerRect;
    public boolean isInExternalScreenOpen;
    public boolean isLargeScreenMode;
    private View largeMode_actionbar;
    private ActionMode mActionMode;
    private List<CityObj> mAllCityList;
    private View mEditContainer;
    private ItemTouchHelper mItemTouchHelper;
    private LinearLayoutManager mLayoutManager;
    private ChinesePinyinConverter mPinyinConverter;
    private BroadcastReceiver mReceiver;
    private View mRootView;
    private SearchCallback mSearchCallback;
    private TextView mSearchInputView;
    private View mSearchListShowPage;
    private RecyclerView mSearchRecyclerView;
    private View mSearchView;
    private View mSpringBackLayout;
    private TimeZoneShowAdapter mTimeZoneShowAdapter;
    private TimezoneModel mTimezoneModel;
    private TimezoneSearchAdapter mTimezoneSearchAdapter;
    private AlarmRecyclerView mTimezoneShowRv;
    private View mViewHolder0;
    private View mViewHolder1;
    private WindowInsetsControllerCompat mWindowInsetController;
    private View mWorldClockContent;
    private String openSource;
    public Rect rect;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        this.isInExternalScreenOpen = Util.isFoldDevice(this) && !Util.isInInternalScreen(this);
        initWorldClockWidgetArgument(bundle);
        if (!TextUtils.isEmpty(this.openSource) && this.openSource.equals("editPage") && Util.isWideMode(this)) {
            setTheme(R.style.TimezoneSearchEditThemeForWeight);
            if (Util.isDeviceLandScape(this)) {
                setRequestedOrientation(6);
            } else {
                setRequestedOrientation(7);
            }
        } else {
            setTheme(R.style.TimezoneSearchEditTheme);
        }
        super.onCreate(bundle);
        if (Util.isTinyScreen(this)) {
            finish();
        }
        Log.d(TAG, "isInExternalScreenOpen: " + this.isInExternalScreenOpen);
        Log.d(TAG, "isInMultiWindowMode: " + Util.isInInternalScreen(this));
        if (this.openSource != null && this.isInExternalScreenOpen && Util.isInInternalScreen(this)) {
            Log.d(TAG, "finish");
            setResult(3004, null);
            finish();
            return;
        }
        this.mWindowInsetController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        setContentView(R.layout.activity_world_clock_edit);
        inflateActionbar();
        initView();
        initData();
        initReceiver();
        showActivityStyle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("isLargeScreenMode", this.isLargeScreenMode);
        bundle.putParcelable("rect", this.rect);
        bundle.putParcelable("miuiContentContainerScreenBound", this.containerRect);
        bundle.putString("openSource", this.openSource);
        bundle.putBoolean("isInExternalScreenOpen", this.isInExternalScreenOpen);
    }

    private void initWorldClockWidgetArgument(Bundle bundle) {
        if (bundle != null) {
            this.isLargeScreenMode = bundle.getBoolean("isLargeScreenMode", false);
            this.rect = (Rect) bundle.getParcelable("miuiWidgetScreenBound");
            this.containerRect = (Rect) bundle.getParcelable("miuiContentContainerScreenBound");
            this.openSource = bundle.getString("openSource");
            this.isInExternalScreenOpen = bundle.getBoolean("isInExternalScreenOpen");
            Log.d(TAG, "savedInstanceState is not null :   isLargeScreenMode : " + this.isLargeScreenMode);
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                this.isLargeScreenMode = intent.getBooleanExtra("isLargeScreenMode", false);
                this.rect = (Rect) intent.getParcelableExtra("miuiWidgetScreenBound");
                this.containerRect = (Rect) intent.getParcelableExtra("miuiContentContainerScreenBound");
                this.openSource = intent.getStringExtra("openSource");
                Log.d(TAG, "savedInstanceState is null :   isLargeScreenMode : " + this.isLargeScreenMode);
            }
        }
        Log.d(TAG, "openSource: " + this.openSource);
    }

    private void initView() {
        this.mPinyinConverter = ChinesePinyinConverter.getInstance(this);
        ((ClockNestedScrollView) findViewById(R.id.scroll_view)).setActivity(this);
        this.mEditContainer = findViewById(R.id.edit_container);
        this.mWorldClockContent = findViewById(R.id.world_clock_content);
        this.mEditContainer.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), WorldClockEditActivity.this.getResources().getDimensionPixelOffset(R.dimen.widget_edit_mode_radius));
            }
        });
        this.mEditContainer.setClipToOutline(true);
        this.mRootView = findViewById(R.id.root_view_new);
        this.mViewHolder0 = findViewById(R.id.place_holder0);
        this.mViewHolder1 = findViewById(R.id.place_holder1);
        initSearchView();
        initTimezoneShowRv();
    }

    private void showActivityStyle() {
        if (Util.isWideMode(this) && this.isLargeScreenMode && !TextUtils.isEmpty(this.openSource) && this.openSource.equals("editPage")) {
            showSmallFloatActivity();
        } else {
            showNormalActivity();
        }
    }

    private void showNormalActivity() {
        this.mViewHolder0.setVisibility(8);
        this.mViewHolder1.setVisibility(8);
        this.mEditContainer.setClipToOutline(false);
        this.mEditContainer.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        this.mRootView.setPadding(0, 0, 0, Util.getVirtualNavigationBarHeight(this));
    }

    private void showSmallFloatActivity() {
        float f;
        float f2;
        int dimensionPixelOffset;
        Rect rect;
        int i;
        int i2;
        int i3;
        int i4;
        int windowHeightByWm = Util.getWindowHeightByWm();
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.widget_edit_page_width);
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R.dimen.distance_between_widget_and_edit_page);
        int windowWidthByWM = Util.getWindowWidthByWM();
        if (Util.isFoldDevice(this)) {
            dimensionPixelSize = (int) ((dimensionPixelSize / getResources().getDisplayMetrics().density) * 2.75f);
            dimensionPixelSize2 = (int) ((dimensionPixelSize2 / getResources().getDisplayMetrics().density) * 2.75f);
        }
        Rect rect2 = this.rect;
        if (rect2 != null) {
            Rect rect3 = this.containerRect;
            if (rect3 != null) {
                dimensionPixelSize = rect3.width();
                dimensionPixelOffset = this.containerRect.height();
                if (this.rect.left < this.containerRect.left) {
                    i3 = this.rect.right + (this.containerRect.left - this.rect.right);
                    i4 = (windowWidthByWM - i3) - dimensionPixelSize;
                } else {
                    int i5 = (this.rect.left - this.containerRect.right) + (windowWidthByWM - this.rect.left);
                    i3 = (windowWidthByWM - i5) - dimensionPixelSize;
                    i4 = i5;
                }
                float f3 = i3;
                float f4 = i3 + i4;
                f = f3 / f4;
                f2 = i4 / f4;
            } else {
                if (windowWidthByWM - rect2.right > this.rect.left) {
                    i = this.rect.right + dimensionPixelSize2;
                    i2 = (windowWidthByWM - i) - dimensionPixelSize;
                } else {
                    int i6 = dimensionPixelSize2 + (windowWidthByWM - this.rect.left);
                    i = (windowWidthByWM - i6) - dimensionPixelSize;
                    i2 = i6;
                }
                float f5 = i;
                float f6 = i + i2;
                f2 = i2 / f6;
                f = f5 / f6;
                dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.widget_edit_page_height);
            }
        } else {
            f = 1.0f;
            f2 = 1.0f;
            dimensionPixelOffset = 0;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -1, f);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, -1, f2);
        if (!PadAdapterUtil.IS_PAD) {
            dimensionPixelOffset = -1;
        }
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(dimensionPixelSize, dimensionPixelOffset);
        this.mViewHolder0.setVisibility(0);
        this.mViewHolder1.setVisibility(0);
        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1) {
            Log.d(TAG, "is RTL " + (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1));
            this.mViewHolder0.setLayoutParams(layoutParams2);
            this.mViewHolder1.setLayoutParams(layoutParams);
        } else {
            Log.d(TAG, "not is RTL");
            this.mViewHolder0.setLayoutParams(layoutParams);
            this.mViewHolder1.setLayoutParams(layoutParams2);
        }
        this.mEditContainer.setClipToOutline(true);
        this.mEditContainer.setBackgroundResource(R.color.widget_bg);
        if (PadAdapterUtil.IS_PAD && (rect = this.rect) != null) {
            Rect rect4 = this.containerRect;
            if (rect4 != null) {
                layoutParams3.topMargin = rect4.top;
            } else {
                int i7 = windowHeightByWm / 2;
                if (rect.top <= i7 && windowHeightByWm - this.rect.top > getResources().getDimensionPixelOffset(R.dimen.widget_edit_page_height)) {
                    layoutParams3.topMargin = this.rect.top - Util.getStatusBarHeight(this);
                } else if (this.rect.top > i7 && this.rect.bottom - getResources().getDimensionPixelOffset(R.dimen.widget_edit_page_height) > Util.getStatusBarHeight(this)) {
                    layoutParams3.topMargin = (this.rect.bottom - getResources().getDimensionPixelOffset(R.dimen.widget_edit_page_height)) - Util.getStatusBarHeight(this);
                } else {
                    layoutParams3.topMargin = 0;
                }
            }
        } else if (Util.isInFullWindowGestureMode(this)) {
            this.mRootView.setPadding(0, Util.getStatusBarHeights(this), 0, Util.getVirtualNavigationBarHeight(this));
        } else {
            this.mRootView.setPadding(0, Util.getStatusBarHeights(this), 0, 0);
        }
        if (!Util.isInFullWindowGestureMode(this)) {
            this.mWindowInsetController.setAppearanceLightNavigationBars(false);
        }
        this.mEditContainer.setLayoutParams(layoutParams3);
        this.mRootView.setBackgroundResource(android.R.color.transparent);
        if (this.mSearchView == null) {
            this.mSearchView = findViewById(R.id.search_city_view);
        }
        this.mSearchView.setVisibility(8);
    }

    private void initSearchView() {
        this.mSearchView = findViewById(R.id.search_city_view);
        TextView textView = (TextView) findViewById(android.R.id.input);
        this.mSearchInputView = textView;
        textView.setHint(R.string.worldclock_input_hint_text);
        this.mSearchInputView.setText("");
        this.mSearchView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (WorldClockEditActivity.this.mSearchListShowPage == null) {
                    WorldClockEditActivity.this.initSearchListPage();
                }
                WorldClockEditActivity.this.onSearchRequest(view);
            }
        });
    }

    private void initTimezoneShowRv() {
        this.mTimezoneShowRv = (AlarmRecyclerView) findViewById(R.id.city_list_show);
        this.mTimeZoneShowAdapter = new TimeZoneShowAdapter(this, this.mTimezoneShowRv);
        this.mTimezoneShowRv.setLayoutManager(new LinearLayoutManager(this));
        this.mTimezoneShowRv.setSpringEnabled(false);
        this.mTimezoneShowRv.setAdapter(this.mTimeZoneShowAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(new ItemTouchHelperCallback.ItemTouchHelperAdapter() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.3
            @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
            public void onDragEnd(RecyclerView.ViewHolder viewHolder, int i) {
            }

            @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
            public boolean onDragStart(RecyclerView.ViewHolder viewHolder, int i) {
                return false;
            }

            @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
            public int onMovementFlags() {
                return 3;
            }

            @Override // com.android.deskclock.worldclock.ItemTouchHelperCallback.ItemTouchHelperAdapter
            public boolean onItemMove(int i, int i2) {
                if (WorldClockEditActivity.this.mTimeZoneShowAdapter == null) {
                    return true;
                }
                WorldClockEditActivity.this.mTimeZoneShowAdapter.onItemMove(i, i2);
                return true;
            }
        }, this));
        this.mItemTouchHelper = itemTouchHelper;
        itemTouchHelper.attachToRecyclerView(this.mTimezoneShowRv);
        this.mTimeZoneShowAdapter.setOnStateChangeListener(new TimeZoneShowAdapter.OnStateChangeListener() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.4
            @Override // com.android.deskclock.worldclock.TimeZoneShowAdapter.OnStateChangeListener
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                WorldClockEditActivity.this.mItemTouchHelper.startDrag(viewHolder);
            }
        });
    }

    private void initData() {
        TimezoneModel timezoneModel = new TimezoneModel(getApplicationContext(), new TimezoneObserverImp(this));
        this.mTimezoneModel = timezoneModel;
        timezoneModel.startLoad();
    }

    public boolean isInActionMode() {
        return this.mActionMode != null;
    }

    private void initReceiver() {
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.i(WorldClockEditActivity.TAG, "onReceive action=" + intent.getAction());
                if ("android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction()) || "android.intent.action.TIME_SET".equals(intent.getAction()) || "android.intent.action.TIME_TICK".equals(intent.getAction())) {
                    WorldClockEditActivity.this.onTimeChanged();
                } else if (DeskClockTabActivity.ACTION_ALARM_CHANGED.equals(intent.getAction())) {
                    WorldClockEditActivity.this.onDataChanged();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction(DeskClockTabActivity.ACTION_ALARM_CHANGED);
        if (Build.VERSION.SDK_INT >= 34) {
            registerReceiver(this.mReceiver, intentFilter, 4);
        } else {
            registerReceiver(this.mReceiver, intentFilter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged() {
        TimeZoneShowAdapter timeZoneShowAdapter = this.mTimeZoneShowAdapter;
        if (timeZoneShowAdapter != null) {
            timeZoneShowAdapter.setTime(System.currentTimeMillis());
            this.mTimeZoneShowAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataChanged() {
        TimezoneModel timezoneModel = this.mTimezoneModel;
        if (timezoneModel != null) {
            timezoneModel.startLoad();
        }
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        TextView textView = this.mSearchInputView;
        if (textView != null) {
            textView.setText("");
        }
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveDatabase() {
        this.mAllCityList.clear();
        this.mAllCityList.addAll(this.mTimeZoneShowAdapter.getDataList());
        this.mAllCityList.removeIf(new Predicate() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((CityObj) obj).mCityId.equals(WorldClockEditActivity.LOCAL_CITY_ID);
            }
        });
        this.mTimezoneModel.updateDatabase(this.mAllCityList);
    }

    @Override // com.android.deskclock.worldclock.TimezoneSearchAdapter.InsertDataListener
    public void insertData(String str) {
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
        if (!TextUtils.isEmpty(str) && !isCityExists(this, str)) {
            insertCityZone(str);
            this.mSearchListShowPage.setVisibility(8);
            this.mWorldClockContent.setVisibility(0);
        } else {
            this.mSearchListShowPage.setVisibility(8);
            this.mWorldClockContent.setVisibility(0);
            Toast.makeText(DeskClockApp.getAppDEContext(), R.string.timezone_exist_error_message, 0).show();
        }
    }

    public static class TimezoneObserverImp implements TimezoneModel.TimezoneObserver {
        private WeakReference<WorldClockEditActivity> mReference;

        @Override // com.android.deskclock.worldclock.TimezoneModel.TimezoneObserver
        public void onTimezoneChangedForWidget(List<CityObj> list) {
        }

        public TimezoneObserverImp(WorldClockEditActivity worldClockEditActivity) {
            this.mReference = new WeakReference<>(worldClockEditActivity);
        }

        @Override // com.android.deskclock.worldclock.TimezoneModel.TimezoneObserver
        public void onTimezoneLoaded(List<CityObj> list, List<TimezoneModel.TimezoneBean> list2) {
            WorldClockEditActivity worldClockEditActivity = this.mReference.get();
            if (worldClockEditActivity != null) {
                worldClockEditActivity.onTimezoneLoadedForObserver(list, list2);
            }
        }

        @Override // com.android.deskclock.worldclock.TimezoneModel.TimezoneObserver
        public void onTimezoneChanged(List<CityObj> list, List<TimezoneModel.TimezoneBean> list2) {
            WorldClockEditActivity worldClockEditActivity = this.mReference.get();
            if (worldClockEditActivity != null) {
                worldClockEditActivity.onTimezoneChangedForObserver(list, list2);
            }
        }
    }

    public void onTimezoneLoadedForObserver(List<CityObj> list, List<TimezoneModel.TimezoneBean> list2) {
        this.mAllCityList = list;
        if (list.size() == 0) {
            this.mAllCityList.add(LocalCityZoneUtil.getLocalTimeZone());
        }
        TimeZoneShowAdapter timeZoneShowAdapter = this.mTimeZoneShowAdapter;
        if (timeZoneShowAdapter != null) {
            timeZoneShowAdapter.initData(new ArrayList(this.mAllCityList));
            this.mTimeZoneShowAdapter.notifyDataSetChanged();
        }
    }

    public void onTimezoneChangedForObserver(List<CityObj> list, List<TimezoneModel.TimezoneBean> list2) {
        if (this.mAllCityList.size() != 0) {
            this.mAllCityList.removeIf(new Predicate() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((CityObj) obj).mCityId.equals(WorldClockEditActivity.LOCAL_CITY_ID);
                }
            });
        }
        TimeZoneShowAdapter timeZoneShowAdapter = this.mTimeZoneShowAdapter;
        if (timeZoneShowAdapter != null) {
            timeZoneShowAdapter.initData(new ArrayList(this.mAllCityList));
            this.mTimeZoneShowAdapter.notifyDataSetChanged();
        }
    }

    private void initSearchRecyclerView() {
        this.mSearchRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_new);
        this.mSearchRecyclerView.setPadding(0, 0, 0, (int) (GestureLineUtil.getGestureLineHeight(this) + getResources().getDimension(R.dimen.full_list_padding_bottom)));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.mLayoutManager = linearLayoutManager;
        this.mSearchRecyclerView.setLayoutManager(linearLayoutManager);
        TimezoneSearchAdapter timezoneSearchAdapter = new TimezoneSearchAdapter(this);
        this.mTimezoneSearchAdapter = timezoneSearchAdapter;
        this.mSearchRecyclerView.setAdapter(timezoneSearchAdapter);
        this.mSearchRecyclerView.setItemViewCacheSize(R2.attr.actionBarUnfavoriteIcon);
        this.mSearchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.6
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    WorldClockEditActivity worldClockEditActivity = WorldClockEditActivity.this;
                    Util.hideSoftInput(worldClockEditActivity, worldClockEditActivity.mSearchInputView);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initSearchListPage() {
        this.mSearchListShowPage = ((ViewStub) findViewById(R.id.city_search_list_show_stub)).inflate();
        initSearchRecyclerView();
        new SortAsyncTask(this).execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldShowAlphabet() {
        return Locale.getDefault().equals(Locale.SIMPLIFIED_CHINESE) || Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage());
    }

    private static class SortAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<WorldClockEditActivity> mReference;

        public SortAsyncTask(WorldClockEditActivity worldClockEditActivity) {
            this.mReference = new WeakReference<>(worldClockEditActivity);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            WeakReference<WorldClockEditActivity> weakReference = this.mReference;
            WorldClockEditActivity worldClockEditActivity = weakReference != null ? weakReference.get() : null;
            if (worldClockEditActivity == null) {
                return null;
            }
            worldClockEditActivity.sortCity();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute(r1);
            WeakReference<WorldClockEditActivity> weakReference = this.mReference;
            WorldClockEditActivity worldClockEditActivity = weakReference != null ? weakReference.get() : null;
            if (worldClockEditActivity == null) {
                return;
            }
            worldClockEditActivity.handleSortCity();
        }
    }

    private static class QueryAsyncTask extends AsyncTask<Void, Void, ArrayList<CityObj>> {
        private String mQueryInfo;
        private WeakReference<WorldClockEditActivity> mReference;

        public QueryAsyncTask(WorldClockEditActivity worldClockEditActivity, String str) {
            this.mReference = new WeakReference<>(worldClockEditActivity);
            this.mQueryInfo = str;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public ArrayList<CityObj> doInBackground(Void... voidArr) {
            WeakReference<WorldClockEditActivity> weakReference = this.mReference;
            WorldClockEditActivity worldClockEditActivity = weakReference != null ? weakReference.get() : null;
            if (worldClockEditActivity == null) {
                return null;
            }
            if (this.mQueryInfo == null) {
                this.mQueryInfo = "";
            }
            return worldClockEditActivity.notifyTimezonesListOnQueryChange(this.mQueryInfo);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(ArrayList<CityObj> arrayList) {
            WeakReference<WorldClockEditActivity> weakReference = this.mReference;
            WorldClockEditActivity worldClockEditActivity = weakReference != null ? weakReference.get() : null;
            if (worldClockEditActivity == null) {
                return;
            }
            worldClockEditActivity.showQueryResult(arrayList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sortCity() {
        CityZoneHelper.init();
        CityZoneHelper.sort();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSortCity() {
        TimezoneSearchAdapter timezoneSearchAdapter = this.mTimezoneSearchAdapter;
        if (timezoneSearchAdapter == null) {
            return;
        }
        timezoneSearchAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<CityObj> notifyTimezonesListOnQueryChange(String str) {
        CityZoneHelper.init();
        return CityZoneHelper.queryCityTimezoneItems(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showQueryResult(final ArrayList<CityObj> arrayList) {
        final String[] strArr;
        int[] iArr;
        if (this.mTimezoneSearchAdapter == null || this.mSearchRecyclerView == null || arrayList == null) {
            return;
        }
        int i = 0;
        if (arrayList.size() > 0) {
            HashMap map = new HashMap(26);
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                map.put(this.mPinyinConverter.get(arrayList.get(i2).mCityName).get(0).target.substring(0, 1).toUpperCase(), Integer.valueOf(i2));
            }
            int size = map.size();
            strArr = new String[size];
            iArr = new int[size];
            map.keySet().toArray(strArr);
            Arrays.sort(strArr);
            iArr[0] = 0;
            while (i < size - 1) {
                String str = strArr[i];
                i++;
                iArr[i] = ((Integer) map.get(str)).intValue() + 1;
            }
        } else {
            strArr = new String[0];
            iArr = new int[0];
        }
        this.mTimezoneSearchAdapter.setSections(strArr, iArr);
        this.mTimezoneSearchAdapter.setData(arrayList);
        this.mTimezoneSearchAdapter.notifyDataSetChanged();
        this.mSearchRecyclerView.post(new Runnable() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.7
            @Override // java.lang.Runnable
            public void run() {
                WorldClockEditActivity.this.configIndexer(WorldClockEditActivity.this.shouldShowAlphabet() && WorldClockEditActivity.this.mSearchRecyclerView.getChildCount() < arrayList.size() && strArr.length >= 2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void configIndexer(boolean z) {
        if (z) {
            this.mSearchRecyclerView.setVerticalScrollBarEnabled(false);
        } else {
            this.mSearchRecyclerView.setVerticalScrollBarEnabled(true);
        }
    }

    public static int getmShowListSize(Context context) {
        if (context == null) {
            return -1;
        }
        return FBEUtil.getDefaultSharedPreferences(context).getInt(CITY_LIST_SIZE, -1);
    }

    public static void saveShowListSize(Context context, List<CityObj> list, int i) {
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(context).edit();
        editorEdit.putInt(CITY_LIST_SIZE, i).apply();
        if (list == null) {
            editorEdit.putString(SHOW_CITIES_IDS, "").apply();
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < list.size() && i2 < i; i2++) {
            sb.append(list.get(i2).mCityId + "/");
        }
        editorEdit.putString(SHOW_CITIES_IDS, sb.toString()).apply();
    }

    public static String[] getShowCitiesIds(Context context) {
        if (context == null) {
            return null;
        }
        String string = FBEUtil.getDefaultSharedPreferences(context).getString(SHOW_CITIES_IDS, "");
        if ("".equals(string)) {
            return null;
        }
        return string.split("/");
    }

    private boolean isCityExists(Context context, String str) {
        Iterator<CityObj> it = this.mAllCityList.iterator();
        while (it.hasNext()) {
            if (it.next().mCityId.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private void insertCityZone(String str) {
        CityObj cityTimezoneItemById = CityZoneHelper.getCityTimezoneItemById(str);
        this.mTimeZoneShowAdapter.addFirstCity(cityTimezoneItemById);
        this.mAllCityList.add(0, cityTimezoneItemById);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSearchRequest(View view) {
        if (this.mSearchCallback == null) {
            this.mSearchCallback = new SearchCallback(this, new SearchView.OnQueryTextListener() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.8
                @Override // android.widget.SearchView.OnQueryTextListener
                public boolean onQueryTextSubmit(String str) {
                    return false;
                }

                @Override // android.widget.SearchView.OnQueryTextListener
                public boolean onQueryTextChange(String str) {
                    WorldClockEditActivity.this.mTimezoneSearchAdapter.clearData();
                    if (!TextUtils.isEmpty(str)) {
                        new QueryAsyncTask(WorldClockEditActivity.this, str).execute(new Void[0]);
                    }
                    return false;
                }
            }, new SearchCallback.OnSearchListener() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.9
                @Override // com.android.deskclock.worldclock.SearchCallback.OnSearchListener
                public void onPrepareSearchMode(ActionMode actionMode, Menu menu) {
                    WorldClockEditActivity.this.mActionMode = actionMode;
                }

                @Override // com.android.deskclock.worldclock.SearchCallback.OnSearchListener
                public void onCreateSearchMode(ActionMode actionMode, Menu menu) {
                    WorldClockEditActivity.this.mWorldClockContent.setVisibility(8);
                    WorldClockEditActivity.this.mSearchListShowPage.setVisibility(0);
                    WorldClockEditActivity worldClockEditActivity = WorldClockEditActivity.this;
                    worldClockEditActivity.mSpringBackLayout = worldClockEditActivity.findViewById(R.id.spring_back_layout);
                    WorldClockEditActivity.this.mSpringBackLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, 0));
                }

                @Override // com.android.deskclock.worldclock.SearchCallback.OnSearchListener
                public void onDestroySearchMode(ActionMode actionMode) {
                    WorldClockEditActivity.this.mSearchListShowPage.setVisibility(8);
                    WorldClockEditActivity.this.mWorldClockContent.setVisibility(0);
                    WorldClockEditActivity.this.mSpringBackLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                    WorldClockEditActivity.this.mActionMode = null;
                }
            });
        }
        this.mSearchCallback.setup(view, this.mRootView);
        startActionMode(this.mSearchCallback);
    }

    private void inflateActionbar() {
        if (this.isLargeScreenMode && !TextUtils.isEmpty(this.openSource) && this.openSource.equals("editPage") && Util.isWideMode(this)) {
            View viewFindViewById = findViewById(R.id.largeMode_actionbar);
            this.largeMode_actionbar = viewFindViewById;
            viewFindViewById.setVisibility(0);
            ((TextView) findViewById(R.id.widget_mode_subtitle)).setText(String.format(getResources().getString(R.string.world_edit_actionbar_subtitle), 4));
            ((ImageView) findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.10
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    WorldClockEditActivity.this.finish();
                }
            });
            ((ImageView) findViewById(R.id.button_confirm)).setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.11
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    WorldClockEditActivity.this.saveDatabase();
                    WorldClockEditActivity.this.finish();
                }
            });
            return;
        }
        ActionBar appCompatActionBar = getAppCompatActionBar();
        appCompatActionBar.setDisplayHomeAsUpEnabled(false);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.action_mode_title_button_cancel);
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WorldClockEditActivity.this.finish();
            }
        });
        imageView.setContentDescription(getResources().getString(R.string.cancel));
        appCompatActionBar.setStartView(imageView);
        ImageView imageView2 = new ImageView(this);
        imageView2.setImageResource(R.drawable.action_mode_title_button_confirm);
        imageView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.WorldClockEditActivity.13
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WorldClockEditActivity.this.saveDatabase();
                WorldClockEditActivity.this.finish();
            }
        });
        imageView2.setContentDescription(getResources().getString(R.string.confirm));
        appCompatActionBar.setEndView(imageView2);
        appCompatActionBar.setTitle(R.string.world_edit_actionbar_title);
        appCompatActionBar.setSubtitle(String.format(getResources().getString(R.string.world_edit_actionbar_subtitle), 4));
        appCompatActionBar.setDisplayShowCustomEnabled(false);
        appCompatActionBar.setDisplayShowTitleEnabled(true);
        appCompatActionBar.setExpandState(1);
        appCompatActionBar.setResizable(false);
    }

    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        BroadcastReceiver broadcastReceiver = this.mReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        TimezoneModel timezoneModel = this.mTimezoneModel;
        if (timezoneModel != null) {
            timezoneModel.release();
        }
        TextView textView = this.mSearchInputView;
        if (textView != null) {
            textView.setText("");
            this.mSearchInputView.clearFocus();
        }
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (y < this.mEditContainer.getTop() || y > this.mEditContainer.getBottom() || x < this.mEditContainer.getLeft() || x > this.mEditContainer.getRight()) {
                finish();
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }
}
