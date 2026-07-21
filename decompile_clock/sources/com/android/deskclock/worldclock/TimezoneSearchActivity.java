package com.android.deskclock.worldclock;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.AdditionUtil;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.base.BaseActivity;
import com.android.deskclock.util.CityZoneHelper;
import com.android.deskclock.util.GestureLineUtil;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.UiUtil;
import com.android.deskclock.util.Util;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.ActionBarTransitionListener;
import miuix.miuixbasewidget.widget.AlphabetIndexer;
import miuix.pinyin.utilities.ChinesePinyinConverter;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes.dex */
public class TimezoneSearchActivity extends BaseActivity implements View.OnClickListener, TimezoneSearchAdapter.AddDataToListener {
    public static final int SET_RESULT_FOR_WIDGET = 3004;
    private static final String TAG = "DC:TimezoneSearchActivity";
    public Rect containerRect;
    public boolean isInExternalScreenOpen;
    public boolean isLargeScreenMode;
    private ActionBar mActionBar;
    private ActionMode mActionMode;
    private AlphabetIndexer mAlphabetIndexer;
    private View mEditActivityContainer;
    private boolean mIsActionBarResizing;
    private boolean mIsFromWidget = false;
    private boolean mIsInSearchMode;
    private LinearLayoutManager mLayoutManager;
    private ChinesePinyinConverter mPinyinConverter;
    private RecyclerView mRecyclerView;
    private View mRootView;
    private SearchCallback mSearchCallback;
    private TextView mSearchTextView;
    private View mSearchView;
    private TimezoneSearchAdapter mTimezoneAdapter;
    private View mViewHolder0;
    private View mViewHolder1;
    private String mWidgetLocalId;
    private RelativeLayout mWidgetModeActionBar;
    private WindowInsetsControllerCompat mWindowInsetController;
    public String openSource;
    public Rect rect;

    @Override // miuix.appcompat.app.AppCompatActivity
    protected boolean isResponsiveEnabled() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        if (Util.isTinyScreen(this)) {
            finish();
        }
        Intent intent = getIntent();
        this.isInExternalScreenOpen = Util.isFoldDevice(this) && !Util.isInInternalScreen(this);
        initWidgetArgument(bundle);
        if ((Util.isWideMode(this) || Util.isSmallPad()) && (TextUtils.isEmpty(this.openSource) || (!TextUtils.isEmpty(this.openSource) && this.openSource.equals("detailPage")))) {
            setTheme(R.style.TimezoneSearchThemeForPad);
        } else if (Util.isWideMode(this) && !TextUtils.isEmpty(this.openSource) && this.openSource.equals("editPage")) {
            setTheme(R.style.TimezoneSearchThemeForWidget);
            if (Util.isDeviceLandScape(this)) {
                setRequestedOrientation(6);
            } else {
                setRequestedOrientation(7);
            }
        } else {
            setTheme(R.style.TimezoneSearchTheme);
        }
        super.onCreate(bundle);
        if (this.openSource != null && this.isInExternalScreenOpen && Util.isInInternalScreen(this)) {
            Log.d(TAG, "finish");
            setResult(3004, null);
            finish();
            return;
        }
        setContentView(R.layout.timezone_searchview);
        this.mWindowInsetController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        initActionBar();
        this.mPinyinConverter = ChinesePinyinConverter.getInstance(this);
        View viewFindViewById = findViewById(R.id.search_view);
        this.mSearchView = viewFindViewById;
        viewFindViewById.setOnClickListener(this);
        this.mRootView = findViewById(R.id.root_view);
        this.mViewHolder0 = findViewById(R.id.place_holder0);
        this.mViewHolder1 = findViewById(R.id.place_holder1);
        this.mWidgetModeActionBar = (RelativeLayout) findViewById(R.id.widget_mode_back_action_bar);
        View viewFindViewById2 = findViewById(R.id.search_view_container);
        this.mEditActivityContainer = viewFindViewById2;
        viewFindViewById2.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.deskclock.worldclock.TimezoneSearchActivity.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), TimezoneSearchActivity.this.getResources().getDimensionPixelOffset(R.dimen.widget_edit_mode_radius));
            }
        });
        this.mEditActivityContainer.setClipToOutline(true);
        TextView textView = (TextView) findViewById(android.R.id.input);
        this.mSearchTextView = textView;
        textView.setHint(R.string.worldclock_input_hint_text);
        this.mSearchTextView.setText("");
        this.mAlphabetIndexer = (AlphabetIndexer) findViewById(R.id.alphabet_indexer);
        int notchHeight = Util.getNotchHeight(this, (int) getResources().getDimension(R.dimen.timezone_item_fast_indexer_margin_end));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mAlphabetIndexer.getLayoutParams();
        layoutParams.setMarginEnd(notchHeight);
        this.mAlphabetIndexer.setLayoutParams(layoutParams);
        initRecyclerView();
        initIndexer();
        showSortedCities();
        getWidgetLocalId(intent);
        showActivityStyle();
        Util.cutOut(this);
    }

    private void initWidgetArgument(Bundle bundle) {
        if (bundle != null) {
            this.isLargeScreenMode = bundle.getBoolean("isLargeScreenMode", false);
            this.rect = (Rect) bundle.getParcelable("rect");
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

    private void showActivityStyle() {
        if (Util.isWideMode(this) && this.isLargeScreenMode && !TextUtils.isEmpty(this.openSource) && this.openSource.equals("editPage")) {
            showSmallFloatActivity();
        } else {
            showNormalActivity();
        }
    }

    private void showNormalActivity() {
        this.mSearchView.setVisibility(0);
        this.mViewHolder0.setVisibility(8);
        this.mViewHolder1.setVisibility(8);
        this.mWidgetModeActionBar.setVisibility(8);
        this.mEditActivityContainer.setClipToOutline(false);
        this.mEditActivityContainer.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        this.mRootView.setBackgroundResource(R.color.search_action_bar_bg);
        if (Util.isWideMode(this) || Util.isSmallPad()) {
            this.mRootView.setBackgroundResource(R.color.search_action_bar_bg_float);
        }
        this.mRootView.setPadding(0, 0, 0, 0);
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
        this.mSearchView.setVisibility(8);
        setWidgetActionBar();
        int windowHeightByWm = Util.getWindowHeightByWm();
        int dimensionPixelOffset2 = getResources().getDimensionPixelOffset(R.dimen.widget_edit_page_width);
        int dimensionPixelOffset3 = getResources().getDimensionPixelOffset(R.dimen.distance_between_widget_and_edit_page);
        int windowWidthByWM = Util.getWindowWidthByWM();
        Rect rect2 = this.rect;
        if (rect2 != null) {
            Rect rect3 = this.containerRect;
            if (rect3 != null) {
                dimensionPixelOffset2 = rect3.right - this.containerRect.left;
                dimensionPixelOffset = this.containerRect.bottom - this.containerRect.top;
                if (this.rect.left < this.containerRect.left) {
                    i3 = this.rect.right + (this.containerRect.left - this.rect.right);
                    i4 = (windowWidthByWM - i3) - dimensionPixelOffset2;
                } else {
                    int i5 = (this.rect.left - this.containerRect.right) + (windowWidthByWM - this.rect.left);
                    i3 = (windowWidthByWM - i5) - dimensionPixelOffset2;
                    i4 = i5;
                }
                float f3 = i3;
                float f4 = i3 + i4;
                f = f3 / f4;
                f2 = i4 / f4;
            } else {
                if (windowWidthByWM - rect2.right > this.rect.left) {
                    i = this.rect.right + dimensionPixelOffset3;
                    i2 = (windowWidthByWM - i) - dimensionPixelOffset2;
                } else {
                    int i6 = dimensionPixelOffset3 + (windowWidthByWM - this.rect.left);
                    i = (windowWidthByWM - i6) - dimensionPixelOffset2;
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
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(dimensionPixelOffset2, dimensionPixelOffset);
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
        this.mEditActivityContainer.setClipToOutline(true);
        this.mEditActivityContainer.setBackgroundResource(R.color.widget_bg_color);
        if (PadAdapterUtil.IS_PAD && (rect = this.rect) != null) {
            Rect rect4 = this.containerRect;
            if (rect4 != null) {
                layoutParams3.topMargin = rect4.top;
            } else {
                int i7 = windowHeightByWm / 2;
                if (rect.top <= i7 && windowHeightByWm - this.rect.top > getResources().getDimensionPixelOffset(R.dimen.widget_edit_page_height)) {
                    layoutParams3.topMargin = this.rect.top - Util.getStatusBarHeights(this);
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
        this.mRootView.setBackgroundResource(android.R.color.transparent);
        this.mEditActivityContainer.setLayoutParams(layoutParams3);
    }

    private void setWidgetActionBar() {
        this.mWidgetModeActionBar.setVisibility(0);
        ((TextView) findViewById(R.id.widget_mode_title)).setText(R.string.worldclock_search_city);
        ImageView imageView = (ImageView) this.mRootView.findViewById(16908313);
        if (!Util.isNightMode(this)) {
            imageView.setBackgroundResource(R.drawable.miuix_action_icon_back_light);
        } else {
            imageView.setBackgroundResource(R.drawable.miuix_action_icon_back_dark);
        }
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m104x4461755b(view);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setWidgetActionBar$0$com-android-deskclock-worldclock-TimezoneSearchActivity, reason: not valid java name */
    /* synthetic */ void m104x4461755b(View view) {
        onBackPressed();
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

    private void getWidgetLocalId(Intent intent) {
        if (intent != null) {
            Uri data = intent.getData();
            String action = intent.getAction();
            if (action != null && action.equals("android.intent.action.SEARCH")) {
                this.mIsFromWidget = true;
            } else {
                this.mIsFromWidget = false;
            }
            if (data != null && TextUtils.equals("details", data.getHost())) {
                this.mWidgetLocalId = data.getQueryParameter("localid");
            }
            Log.d(TAG, "getWidgetLocalId: " + this.mWidgetLocalId);
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getWidgetLocalId(intent);
    }

    private void initRecyclerView() {
        this.mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.mRecyclerView.setPadding(0, (int) getResources().getDimension(R.dimen.am_pm_margin_start), 0, (int) (GestureLineUtil.getGestureLineHeight(this) + getResources().getDimension(R.dimen.full_list_padding_bottom)));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.mLayoutManager = linearLayoutManager;
        this.mRecyclerView.setLayoutManager(linearLayoutManager);
        TimezoneSearchAdapter timezoneSearchAdapter = new TimezoneSearchAdapter(this);
        this.mTimezoneAdapter = timezoneSearchAdapter;
        this.mRecyclerView.setAdapter(timezoneSearchAdapter);
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchActivity.2
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    TimezoneSearchActivity timezoneSearchActivity = TimezoneSearchActivity.this;
                    Util.hideSoftInput(timezoneSearchActivity, timezoneSearchActivity.mSearchTextView);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
            }
        });
    }

    private void initIndexer() {
        this.mAlphabetIndexer = (AlphabetIndexer) findViewById(R.id.alphabet_indexer);
        int notchHeight = Util.getNotchHeight(this, (int) getResources().getDimension(R.dimen.timezone_item_fast_indexer_margin_end));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mAlphabetIndexer.getLayoutParams();
        layoutParams.setMarginEnd(notchHeight);
        this.mAlphabetIndexer.setLayoutParams(layoutParams);
        if (shouldShowAlphabet()) {
            this.mAlphabetIndexer.setSectionIndexer(this.mTimezoneAdapter);
            this.mAlphabetIndexer.attach(new AlphabetIndexer.Adapter() { // from class: com.android.deskclock.worldclock.TimezoneSearchActivity.3
                @Override // miuix.miuixbasewidget.widget.AlphabetIndexer.Adapter
                public int getListHeaderCount() {
                    return 0;
                }

                @Override // miuix.miuixbasewidget.widget.AlphabetIndexer.Adapter
                public void stopScroll() {
                }

                @Override // miuix.miuixbasewidget.widget.AlphabetIndexer.Adapter
                public int getFirstVisibleItemPosition() {
                    return TimezoneSearchActivity.this.mLayoutManager.findFirstVisibleItemPosition();
                }

                @Override // miuix.miuixbasewidget.widget.AlphabetIndexer.Adapter
                public int getItemCount() {
                    return TimezoneSearchActivity.this.mLayoutManager.getItemCount();
                }

                @Override // miuix.miuixbasewidget.widget.AlphabetIndexer.Adapter
                public void scrollToPosition(int i) {
                    TimezoneSearchActivity.this.mLayoutManager.scrollToPositionWithOffset(i, 0);
                }
            });
            this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchActivity.4
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    TimezoneSearchActivity.this.mAlphabetIndexer.onScrollStateChanged(i);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    if (TimezoneSearchActivity.this.mTimezoneAdapter.getItemCount() > 1) {
                        TimezoneSearchActivity.this.mAlphabetIndexer.onScrolled(i, i2);
                    }
                }
            });
            return;
        }
        this.mAlphabetIndexer.detach();
    }

    private void initActionBar() {
        if (Util.isWideMode(this) && !TextUtils.isEmpty(this.openSource) && this.openSource.equals("editPage") && this.isLargeScreenMode) {
            return;
        }
        ActionBar appCompatActionBar = getAppCompatActionBar();
        this.mActionBar = appCompatActionBar;
        appCompatActionBar.setDisplayShowCustomEnabled(false);
        this.mActionBar.setDisplayShowTitleEnabled(true);
        this.mActionBar.setDisplayHomeAsUpEnabled(false);
        this.mActionBar.setTitle(R.string.worldclock_search_city);
        this.mActionBar.setSubtitle(R.string.worldclock_world_time);
        ImageView floatPageBackIcon = UiUtil.getFloatPageBackIcon(this);
        floatPageBackIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m103x9ae298b8(view);
            }
        });
        floatPageBackIcon.setContentDescription(getResources().getString(R.string.go_back));
        this.mActionBar.setStartView(floatPageBackIcon);
        this.mActionBar.setResizable(false);
        this.mActionBar.addActionBarTransitionListener(new ActionBarTransitionListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchActivity.5
            @Override // miuix.appcompat.app.ActionBarTransitionListener
            public void onActionBarResizing(int i, float f, int i2) {
                TimezoneSearchActivity.this.mIsActionBarResizing = true;
                if (f == 0.0f || f == 1.0f) {
                    TimezoneSearchActivity.this.mIsActionBarResizing = false;
                }
            }

            @Override // miuix.appcompat.app.ActionBarTransitionListener
            public void onExpandStateChanged(int i) {
                TimezoneSearchActivity.this.mIsActionBarResizing = false;
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initActionBar$1$com-android-deskclock-worldclock-TimezoneSearchActivity, reason: not valid java name */
    /* synthetic */ void m103x9ae298b8(View view) {
        onBackPressed();
    }

    private void showSortedCities() {
        new SortAsyncTask(this).execute(new Void[0]);
    }

    @Override // com.android.deskclock.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        TextView textView;
        super.onResume();
        if (this.mActionMode != null || (textView = this.mSearchTextView) == null) {
            return;
        }
        textView.setText("");
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == 16908332) {
            Util.hideSoftInput(this, this.mSearchTextView);
            finish();
        } else if (id == R.id.search_view && !this.mIsActionBarResizing) {
            onSearchRequest(view);
        }
    }

    private void onSearchRequest(View view) {
        if (this.mSearchCallback == null) {
            this.mSearchCallback = new SearchCallback(this, new SearchView.OnQueryTextListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchActivity.6
                @Override // android.widget.SearchView.OnQueryTextListener
                public boolean onQueryTextSubmit(String str) {
                    return false;
                }

                @Override // android.widget.SearchView.OnQueryTextListener
                public boolean onQueryTextChange(String str) {
                    TimezoneSearchActivity.this.mTimezoneAdapter.clearData();
                    new QueryAsyncTask(TimezoneSearchActivity.this, str).execute(new Void[0]);
                    return false;
                }
            }, new SearchCallback.OnSearchListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchActivity.7
                @Override // com.android.deskclock.worldclock.SearchCallback.OnSearchListener
                public void onPrepareSearchMode(ActionMode actionMode, Menu menu) {
                    TimezoneSearchActivity.this.mActionMode = actionMode;
                    TimezoneSearchActivity.this.mSearchView.setVisibility(8);
                }

                @Override // com.android.deskclock.worldclock.SearchCallback.OnSearchListener
                public void onCreateSearchMode(ActionMode actionMode, Menu menu) {
                    TimezoneSearchActivity.this.mIsInSearchMode = true;
                    TimezoneSearchActivity.this.configIndexer(false);
                    TimezoneSearchActivity.this.mTimezoneAdapter.clearData();
                }

                @Override // com.android.deskclock.worldclock.SearchCallback.OnSearchListener
                public void onDestroySearchMode(ActionMode actionMode) {
                    TimezoneSearchActivity.this.mIsInSearchMode = false;
                    new QueryAsyncTask(TimezoneSearchActivity.this, "").execute(new Void[0]);
                    TimezoneSearchActivity.this.mActionMode = null;
                    TimezoneSearchActivity.this.mSearchView.setVisibility(0);
                }
            });
        }
        this.mSearchCallback.setup(view, this.mRootView);
        startActionMode(this.mSearchCallback);
    }

    @Override // com.android.deskclock.base.BaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        TextView textView = this.mSearchTextView;
        if (textView != null) {
            textView.setText("");
            this.mSearchTextView.clearFocus();
        }
        if (this.mSearchCallback != null) {
            this.mSearchCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sortCity() {
        CityZoneHelper.init();
        CityZoneHelper.sort();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSortCity() {
        if (this.mSearchTextView != null) {
            new QueryAsyncTask(this, this.mSearchTextView.getText().toString()).execute(new Void[0]);
        }
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
        if (this.mTimezoneAdapter == null || this.mRecyclerView == null || arrayList == null) {
            return;
        }
        int i = 0;
        if (arrayList.size() > 0) {
            HashMap map = new HashMap(26);
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                CityObj cityObj = arrayList.get(i2);
                if (cityObj.mCityId.equals("C155")) {
                    arrayList.remove(cityObj);
                }
                map.put(this.mPinyinConverter.get(cityObj.mCityName).get(0).target.substring(0, 1).toUpperCase(), Integer.valueOf(i2));
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
        this.mTimezoneAdapter.setSections(strArr, iArr);
        this.mTimezoneAdapter.setData(arrayList);
        this.mTimezoneAdapter.notifyDataSetChanged();
        this.mRecyclerView.post(new Runnable() { // from class: com.android.deskclock.worldclock.TimezoneSearchActivity.8
            @Override // java.lang.Runnable
            public void run() {
                TimezoneSearchActivity.this.configIndexer(TimezoneSearchActivity.this.shouldShowAlphabet() && TimezoneSearchActivity.this.mRecyclerView.getChildCount() < arrayList.size() && strArr.length >= 2);
            }
        });
    }

    public void configIndexer(boolean z) {
        AlphabetIndexer alphabetIndexer = this.mAlphabetIndexer;
        if (alphabetIndexer == null) {
            return;
        }
        alphabetIndexer.setVisibility(z ? 0 : 8);
        if (z) {
            this.mRecyclerView.setVerticalScrollBarEnabled(false);
        } else {
            this.mRecyclerView.setVerticalScrollBarEnabled(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldShowAlphabet() {
        return Locale.getDefault().equals(Locale.SIMPLIFIED_CHINESE) || Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage());
    }

    @Override // com.android.deskclock.worldclock.TimezoneSearchAdapter.AddDataToListener
    public void addData(String str) {
        if (this.mIsFromWidget) {
            AdditionUtil.putWidgetCityId(DeskClockApp.getAppDEContext(), this.mWidgetLocalId, str);
            String newLocalId = AdditionUtil.getNewLocalId(this.mWidgetLocalId);
            if (!TextUtils.isEmpty(newLocalId)) {
                AdditionUtil.putWidgetCityId(DeskClockApp.getAppDEContext(), newLocalId, str);
            }
            finish();
            return;
        }
        setResult(-1, new Intent().putExtra("android.intent.extra.TEXT", str));
        finish();
    }

    private static class SortAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<TimezoneSearchActivity> mReference;

        public SortAsyncTask(TimezoneSearchActivity timezoneSearchActivity) {
            this.mReference = new WeakReference<>(timezoneSearchActivity);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            WeakReference<TimezoneSearchActivity> weakReference = this.mReference;
            TimezoneSearchActivity timezoneSearchActivity = weakReference != null ? weakReference.get() : null;
            if (timezoneSearchActivity == null) {
                return null;
            }
            timezoneSearchActivity.sortCity();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute(r1);
            WeakReference<TimezoneSearchActivity> weakReference = this.mReference;
            TimezoneSearchActivity timezoneSearchActivity = weakReference != null ? weakReference.get() : null;
            if (timezoneSearchActivity == null) {
                return;
            }
            timezoneSearchActivity.handleSortCity();
        }
    }

    private static class QueryAsyncTask extends AsyncTask<Void, Void, ArrayList<CityObj>> {
        private String mQueryInfo;
        private WeakReference<TimezoneSearchActivity> mReference;

        public QueryAsyncTask(TimezoneSearchActivity timezoneSearchActivity, String str) {
            this.mReference = new WeakReference<>(timezoneSearchActivity);
            this.mQueryInfo = str;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public ArrayList<CityObj> doInBackground(Void... voidArr) {
            WeakReference<TimezoneSearchActivity> weakReference = this.mReference;
            TimezoneSearchActivity timezoneSearchActivity = weakReference != null ? weakReference.get() : null;
            if (timezoneSearchActivity == null) {
                return null;
            }
            if (this.mQueryInfo == null) {
                this.mQueryInfo = "";
            }
            return timezoneSearchActivity.notifyTimezonesListOnQueryChange(this.mQueryInfo);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(ArrayList<CityObj> arrayList) {
            WeakReference<TimezoneSearchActivity> weakReference = this.mReference;
            TimezoneSearchActivity timezoneSearchActivity = weakReference != null ? weakReference.get() : null;
            if (timezoneSearchActivity == null) {
                return;
            }
            if (timezoneSearchActivity.mIsInSearchMode) {
                timezoneSearchActivity.configIndexer(false);
            }
            timezoneSearchActivity.showQueryResult(arrayList);
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        super.onResponsiveLayout(configuration, screenSpec, z);
        if ((PadAdapterUtil.IS_PAD || (Util.isFoldDevice(this) && Util.isInInternalScreen(this))) && this.mActionBar != null) {
            initActionBar();
        }
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (y < this.mEditActivityContainer.getTop() || y > this.mEditActivityContainer.getBottom() || x < this.mEditActivityContainer.getLeft() || x > this.mEditActivityContainer.getRight()) {
                finish();
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }
}
