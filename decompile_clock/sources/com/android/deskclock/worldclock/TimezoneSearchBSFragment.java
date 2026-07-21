package com.android.deskclock.worldclock;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
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
import miuix.appcompat.app.Fragment;
import miuix.miuixbasewidget.widget.AlphabetIndexer;
import miuix.pinyin.utilities.ChinesePinyinConverter;
import miuix.responsive.map.ScreenSpec;
import miuix.view.SearchActionMode;

/* JADX INFO: loaded from: classes.dex */
public class TimezoneSearchBSFragment extends Fragment implements View.OnClickListener {
    protected ActionBar mActionBar;
    private ActionMode mActionMode;
    protected TimezoneSearchBSActivity mActivity;
    private AlphabetIndexer mAlphabetIndexer;
    private boolean mIsActionBarResizing;
    private boolean mIsInSearchMode;
    private LinearLayoutManager mLayoutManager;
    private ChinesePinyinConverter mPinyinConverter;
    private RecyclerView mRecyclerView;
    protected View mRootView;
    private SearchCallback mSearchCallback;
    private TextView mSearchTextView;
    private View mSearchView;
    private TimezoneSearchAdapter mTimezoneAdapter;

    @Override // miuix.appcompat.app.Fragment
    protected boolean isResponsiveEnabled() {
        return true;
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mActivity = (TimezoneSearchBSActivity) getActivity();
        this.mRootView = (ViewGroup) layoutInflater.inflate(R.layout.timezone_search_bs_view, viewGroup, false);
        initActionBar();
        this.mPinyinConverter = ChinesePinyinConverter.getInstance(this.mActivity);
        View viewFindViewById = this.mRootView.findViewById(R.id.search_view);
        this.mSearchView = viewFindViewById;
        viewFindViewById.setOnClickListener(this);
        TextView textView = (TextView) this.mRootView.findViewById(android.R.id.input);
        this.mSearchTextView = textView;
        textView.setHint(R.string.worldclock_input_hint_text);
        this.mSearchTextView.setText("");
        initRecyclerView();
        initIndexer();
        showSortedCities();
        return this.mRootView;
    }

    private void showSortedCities() {
        new SortAsyncTask(this).execute(new Void[0]);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == 16908332) {
            Util.hideSoftInput(this.mActivity, this.mSearchTextView);
            this.mActivity.finish();
        } else if (id == R.id.search_view && !this.mIsActionBarResizing) {
            onSearchRequest(view);
        }
    }

    private void onSearchRequest(View view) {
        if (this.mSearchCallback == null) {
            this.mSearchCallback = new SearchCallback(this.mActivity, new SearchView.OnQueryTextListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSFragment.1
                @Override // android.widget.SearchView.OnQueryTextListener
                public boolean onQueryTextSubmit(String str) {
                    return false;
                }

                @Override // android.widget.SearchView.OnQueryTextListener
                public boolean onQueryTextChange(String str) {
                    TimezoneSearchBSFragment.this.mTimezoneAdapter.clearData();
                    new QueryAsyncTask(TimezoneSearchBSFragment.this, str).execute(new Void[0]);
                    return false;
                }
            }, new SearchCallback.OnSearchListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSFragment.2
                @Override // com.android.deskclock.worldclock.SearchCallback.OnSearchListener
                public void onPrepareSearchMode(ActionMode actionMode, Menu menu) {
                    TimezoneSearchBSFragment.this.mActionMode = actionMode;
                    TimezoneSearchBSFragment.this.mSearchView.setVisibility(8);
                }

                /* JADX WARN: Multi-variable type inference failed */
                @Override // com.android.deskclock.worldclock.SearchCallback.OnSearchListener
                public void onCreateSearchMode(ActionMode actionMode, Menu menu) {
                    TimezoneSearchBSFragment.this.mIsInSearchMode = true;
                    TimezoneSearchBSFragment.this.configIndexer(false);
                    TimezoneSearchBSFragment.this.mTimezoneAdapter.clearData();
                    if (actionMode instanceof SearchActionMode) {
                        ((SearchActionMode) actionMode).getSearchInput().setHint(R.string.worldclock_input_hint_text);
                    }
                }

                @Override // com.android.deskclock.worldclock.SearchCallback.OnSearchListener
                public void onDestroySearchMode(ActionMode actionMode) {
                    TimezoneSearchBSFragment.this.mIsInSearchMode = false;
                    new QueryAsyncTask(TimezoneSearchBSFragment.this, "").execute(new Void[0]);
                    TimezoneSearchBSFragment.this.mActionMode = null;
                    TimezoneSearchBSFragment.this.mSearchView.setVisibility(0);
                }
            });
        }
        this.mSearchCallback.setup(view, this.mRootView);
        startActionMode(this.mSearchCallback);
    }

    private static class SortAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<TimezoneSearchBSFragment> mReference;

        public SortAsyncTask(TimezoneSearchBSFragment timezoneSearchBSFragment) {
            this.mReference = new WeakReference<>(timezoneSearchBSFragment);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            WeakReference<TimezoneSearchBSFragment> weakReference = this.mReference;
            TimezoneSearchBSFragment timezoneSearchBSFragment = weakReference != null ? weakReference.get() : null;
            if (timezoneSearchBSFragment == null) {
                return null;
            }
            timezoneSearchBSFragment.sortCity();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute(r1);
            WeakReference<TimezoneSearchBSFragment> weakReference = this.mReference;
            TimezoneSearchBSFragment timezoneSearchBSFragment = weakReference != null ? weakReference.get() : null;
            if (timezoneSearchBSFragment == null) {
                return;
            }
            timezoneSearchBSFragment.handleSortCity();
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

    private static class QueryAsyncTask extends AsyncTask<Void, Void, ArrayList<CityObj>> {
        private String mQueryInfo;
        private WeakReference<TimezoneSearchBSFragment> mReference;

        public QueryAsyncTask(TimezoneSearchBSFragment timezoneSearchBSFragment, String str) {
            this.mReference = new WeakReference<>(timezoneSearchBSFragment);
            this.mQueryInfo = str;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public ArrayList<CityObj> doInBackground(Void... voidArr) {
            WeakReference<TimezoneSearchBSFragment> weakReference = this.mReference;
            TimezoneSearchBSFragment timezoneSearchBSFragment = weakReference != null ? weakReference.get() : null;
            if (timezoneSearchBSFragment == null) {
                return null;
            }
            if (this.mQueryInfo == null) {
                this.mQueryInfo = "";
            }
            return timezoneSearchBSFragment.notifyTimezonesListOnQueryChange(this.mQueryInfo);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(ArrayList<CityObj> arrayList) {
            WeakReference<TimezoneSearchBSFragment> weakReference = this.mReference;
            TimezoneSearchBSFragment timezoneSearchBSFragment = weakReference != null ? weakReference.get() : null;
            if (timezoneSearchBSFragment == null) {
                return;
            }
            if (timezoneSearchBSFragment.mIsInSearchMode) {
                timezoneSearchBSFragment.configIndexer(false);
            }
            timezoneSearchBSFragment.showQueryResult(arrayList);
        }
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
        this.mRecyclerView.post(new Runnable() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSFragment.3
            @Override // java.lang.Runnable
            public void run() {
                TimezoneSearchBSFragment.this.configIndexer(TimezoneSearchBSFragment.this.shouldShowAlphabet() && TimezoneSearchBSFragment.this.mRecyclerView.getChildCount() < arrayList.size() && strArr.length >= 2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<CityObj> notifyTimezonesListOnQueryChange(String str) {
        CityZoneHelper.init();
        return CityZoneHelper.queryCityTimezoneItems(str);
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

    private void initIndexer() {
        this.mAlphabetIndexer = (AlphabetIndexer) this.mRootView.findViewById(R.id.alphabet_indexer);
        int notchHeight = Util.getNotchHeight(this.mActivity, (int) getResources().getDimension(R.dimen.timezone_item_fast_indexer_margin_end));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mAlphabetIndexer.getLayoutParams();
        layoutParams.setMarginEnd(notchHeight);
        this.mAlphabetIndexer.setLayoutParams(layoutParams);
        if (shouldShowAlphabet()) {
            this.mAlphabetIndexer.setSectionIndexer(this.mTimezoneAdapter);
            this.mAlphabetIndexer.attach(new AlphabetIndexer.Adapter() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSFragment.4
                @Override // miuix.miuixbasewidget.widget.AlphabetIndexer.Adapter
                public int getListHeaderCount() {
                    return 0;
                }

                @Override // miuix.miuixbasewidget.widget.AlphabetIndexer.Adapter
                public void stopScroll() {
                }

                @Override // miuix.miuixbasewidget.widget.AlphabetIndexer.Adapter
                public int getFirstVisibleItemPosition() {
                    return TimezoneSearchBSFragment.this.mLayoutManager.findFirstVisibleItemPosition();
                }

                @Override // miuix.miuixbasewidget.widget.AlphabetIndexer.Adapter
                public int getItemCount() {
                    return TimezoneSearchBSFragment.this.mLayoutManager.getItemCount();
                }

                @Override // miuix.miuixbasewidget.widget.AlphabetIndexer.Adapter
                public void scrollToPosition(int i) {
                    TimezoneSearchBSFragment.this.mLayoutManager.scrollToPositionWithOffset(i, 0);
                }
            });
            this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSFragment.5
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    TimezoneSearchBSFragment.this.mAlphabetIndexer.onScrollStateChanged(i);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    if (TimezoneSearchBSFragment.this.mTimezoneAdapter.getItemCount() > 1) {
                        TimezoneSearchBSFragment.this.mAlphabetIndexer.onScrolled(i, i2);
                    }
                }
            });
            return;
        }
        this.mAlphabetIndexer.detach();
    }

    private void initRecyclerView() {
        this.mRecyclerView = (RecyclerView) this.mRootView.findViewById(R.id.recycler_view);
        this.mRecyclerView.setPadding(0, (int) getResources().getDimension(R.dimen.am_pm_margin_start), 0, (int) (GestureLineUtil.getGestureLineHeight(this.mActivity) + getResources().getDimension(R.dimen.full_list_padding_bottom)));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mActivity);
        this.mLayoutManager = linearLayoutManager;
        this.mRecyclerView.setLayoutManager(linearLayoutManager);
        TimezoneSearchAdapter timezoneSearchAdapter = new TimezoneSearchAdapter(this.mActivity);
        this.mTimezoneAdapter = timezoneSearchAdapter;
        this.mRecyclerView.setAdapter(timezoneSearchAdapter);
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSFragment.6
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    Util.hideSoftInput(TimezoneSearchBSFragment.this.mActivity, TimezoneSearchBSFragment.this.mSearchTextView);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        this.mActionBar = actionBar;
        actionBar.setDisplayShowCustomEnabled(false);
        this.mActionBar.setDisplayShowTitleEnabled(true);
        this.mActionBar.setDisplayHomeAsUpEnabled(false);
        this.mActionBar.setTitle(R.string.worldclock_search_city);
        this.mActionBar.setSubtitle(R.string.worldclock_world_time);
        ImageView timezoneSearchBSBackIcon = UiUtil.getTimezoneSearchBSBackIcon(this.mActivity);
        timezoneSearchBSBackIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m105x1998a449(view);
            }
        });
        timezoneSearchBSBackIcon.setContentDescription(getResources().getString(R.string.go_back));
        this.mActionBar.setStartView(timezoneSearchBSBackIcon);
        this.mActionBar.setExpandState(0);
        this.mActionBar.setResizable(false);
        this.mActionBar.addActionBarTransitionListener(new ActionBarTransitionListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSFragment.7
            @Override // miuix.appcompat.app.ActionBarTransitionListener
            public void onActionBarResizing(int i, float f, int i2) {
                TimezoneSearchBSFragment.this.mIsActionBarResizing = true;
                if (f == 0.0f || f == 1.0f) {
                    TimezoneSearchBSFragment.this.mIsActionBarResizing = false;
                }
            }

            @Override // miuix.appcompat.app.ActionBarTransitionListener
            public void onExpandStateChanged(int i) {
                TimezoneSearchBSFragment.this.mIsActionBarResizing = false;
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initActionBar$0$com-android-deskclock-worldclock-TimezoneSearchBSFragment, reason: not valid java name */
    /* synthetic */ void m105x1998a449(View view) {
        this.mActivity.onBackPressed();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        TextView textView;
        super.onResume();
        if (this.mActionMode != null || (textView = this.mSearchTextView) == null) {
            return;
        }
        textView.setText("");
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IContentInsetState
    public void onContentInsetChanged(Rect rect) {
        super.onContentInsetChanged(rect);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        TextView textView = this.mSearchTextView;
        if (textView != null) {
            textView.setText("");
            this.mSearchTextView.clearFocus();
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.responsive.interfaces.IResponsive
    public void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
        super.onResponsiveLayout(configuration, screenSpec, z);
        if ((PadAdapterUtil.IS_PAD || (Util.isFoldDevice(DeskClockApp.getAppDEContext()) && Util.isInInternalScreen(DeskClockApp.getAppDEContext()))) && this.mActionBar != null) {
            initActionBar();
        }
    }
}
