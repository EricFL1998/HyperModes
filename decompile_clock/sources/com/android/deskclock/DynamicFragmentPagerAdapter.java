package com.android.deskclock;

import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import com.android.deskclock.alarm.AlarmClockFragment;
import com.android.deskclock.base.BaseClockFragment;
import com.android.deskclock.stopwatch.StopwatchFragment;
import com.android.deskclock.timer.TimerFragment;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import com.android.deskclock.view.tab.TabViewModel;
import com.android.deskclock.worldclock.WorldClockFragment;
import java.util.Map;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.Fragment;

/* JADX INFO: loaded from: classes.dex */
public class DynamicFragmentPagerAdapter extends PagerAdapter {
    private static final String TAG = "DC:DynamicFragmentPagerAdapter";
    private AppCompatActivity mActivity;
    private String mCurrTab;
    private final Map<String, FragmentInfo> mFragmentCache;
    private FragmentManager mFragmentManager;
    private int mFragmentSize;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;
    private long mDelayTime = 0;

    public void onActivityDestroy() {
    }

    public DynamicFragmentPagerAdapter(Fragment fragment, String str) {
        Log.d(TAG, "init, currTab: " + str);
        this.mCurrTab = str;
        this.mActivity = fragment.getAppCompatActivity();
        this.mFragmentManager = fragment.getChildFragmentManager();
        this.mFragmentCache = new ArrayMap(getCount());
        if (Util.isTinyScreen(this.mActivity)) {
            addFragment(TabViewModel.TAB_ALARM, AlarmClockFragment.class);
            addFragment(TabViewModel.TAB_TIMER, TimerFragment.class);
            this.mFragmentSize = 2;
        } else {
            addFragment(TabViewModel.TAB_ALARM, AlarmClockFragment.class);
            addFragment(TabViewModel.TAB_CLOCK, WorldClockFragment.class);
            addFragment(TabViewModel.TAB_STOPWATCH, StopwatchFragment.class);
            addFragment(TabViewModel.TAB_TIMER, TimerFragment.class);
            this.mFragmentSize = 4;
        }
    }

    public void addFragment(String str, Class<? extends Fragment> cls) {
        this.mFragmentCache.put(str, new FragmentInfo(str, cls, false));
    }

    private String getTabAt(int i, boolean z) {
        return TabViewModel.getTabAt(i, z);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        String tabAt = getTabAt(i, this.mFragmentSize == 2);
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        Fragment fragment = getFragment(tabAt, true);
        if (tabAt.equals(this.mCurrTab) && (fragment instanceof BaseClockFragment)) {
            ((BaseClockFragment) fragment).setTime(0L);
        } else {
            long j = this.mDelayTime + 300;
            this.mDelayTime = j;
            ((BaseClockFragment) fragment).setTime(j);
        }
        if (fragment.getFragmentManager() != null) {
            this.mCurTransaction.attach(fragment);
        } else {
            this.mCurTransaction.add(viewGroup.getId(), fragment, tabAt);
        }
        if (fragment != this.mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }
        return fragment;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        this.mCurTransaction.detach((Fragment) obj);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void finishUpdate(ViewGroup viewGroup) {
        FragmentTransaction fragmentTransaction;
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity == null || appCompatActivity.isDestroyed() || (fragmentTransaction = this.mCurTransaction) == null) {
            return;
        }
        fragmentTransaction.commitAllowingStateLoss();
        this.mCurTransaction = null;
        if (this.mFragmentManager.isDestroyed()) {
            return;
        }
        this.mFragmentManager.executePendingTransactions();
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
        Fragment fragment = (Fragment) obj;
        Fragment fragment2 = this.mCurrentPrimaryItem;
        if (fragment != fragment2) {
            if (fragment2 != null) {
                fragment2.setMenuVisibility(false);
                this.mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            this.mCurrentPrimaryItem = fragment;
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return ((Fragment) obj).getView() == view;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.mFragmentSize;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getItemPosition(Object obj) {
        boolean z = this.mFragmentSize == 2;
        for (int i = 0; i < getCount(); i++) {
            if (obj == this.mFragmentCache.get(getTabAt(i, z)).fragment) {
                return i;
            }
        }
        return -2;
    }

    public Fragment getFragment(String str, boolean z) {
        String name;
        FragmentInfo fragmentInfo = this.mFragmentCache.get(str);
        if (fragmentInfo != null) {
            if (fragmentInfo.fragment == null) {
                fragmentInfo.fragment = (Fragment) this.mFragmentManager.findFragmentByTag(fragmentInfo.tag);
            }
            if (z && fragmentInfo.fragment == null) {
                fragmentInfo.fragment = (Fragment) Fragment.instantiate(this.mActivity, fragmentInfo.clazz.getName());
            }
            return fragmentInfo.fragment;
        }
        if (z) {
            if (str.equals(TabViewModel.TAB_ALARM)) {
                name = AlarmClockFragment.class.getName();
            } else if (str.equals(TabViewModel.TAB_CLOCK)) {
                name = WorldClockFragment.class.getName();
            } else if (str.equals(TabViewModel.TAB_STOPWATCH)) {
                name = StopwatchFragment.class.getName();
            } else {
                name = TimerFragment.class.getName();
            }
            return (Fragment) Fragment.instantiate(this.mActivity, name);
        }
        return (Fragment) this.mFragmentManager.findFragmentByTag(str);
    }

    public void reCreateFragment() {
        for (String str : TabViewModel.TABS) {
            Fragment fragment = getFragment(str, false);
            int id = ((View) fragment.getView().getParent()).getId();
            FragmentInfo fragmentInfo = this.mFragmentCache.get(str);
            FragmentTransaction fragmentTransactionBeginTransaction = this.mFragmentManager.beginTransaction();
            fragmentTransactionBeginTransaction.remove(fragment);
            Fragment newFragment = getNewFragment(str);
            fragmentInfo.fragment = newFragment;
            fragmentInfo.lazyInit = false;
            fragmentTransactionBeginTransaction.add(id, newFragment, str);
            fragmentTransactionBeginTransaction.commitAllowingStateLoss();
            notifyDataSetChanged();
        }
    }

    private Fragment getNewFragment(String str) {
        return (Fragment) Fragment.instantiate(this.mActivity, this.mFragmentCache.get(str).clazz.getName());
    }

    private class FragmentInfo {
        Class<? extends Fragment> clazz;
        Fragment fragment = null;
        boolean lazyInit;
        String tag;

        FragmentInfo(String str, Class<? extends Fragment> cls, boolean z) {
            this.tag = str;
            this.clazz = cls;
            this.lazyInit = z;
        }
    }
}
