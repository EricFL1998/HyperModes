package com.android.deskclock;

import com.android.deskclock.util.Log;
import java.util.ArrayList;
import java.util.List;
import miuix.androidbasewidget.adapter.FragmentStateAdapter;
import miuix.appcompat.app.Fragment;

/* JADX INFO: loaded from: classes.dex */
public class ViewPagerAdapter extends FragmentStateAdapter {
    public static final String TAG = "DC:ViewPagerAdapter";
    private final List<Fragment> fragments;
    private final List<Integer> mFragmentIds;

    public ViewPagerAdapter(Fragment fragment) {
        super(fragment);
        this.fragments = new ArrayList();
        this.mFragmentIds = new ArrayList();
    }

    @Override // miuix.androidbasewidget.adapter.FragmentStateAdapter
    public Fragment createFragment(int i) {
        Log.d(TAG, "createFragment: " + this.fragments.get(i));
        return this.fragments.get(i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + this.fragments.size());
        return this.fragments.size();
    }

    @Override // miuix.androidbasewidget.adapter.FragmentStateAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return this.mFragmentIds.get(i).intValue();
    }

    @Override // miuix.androidbasewidget.adapter.FragmentStateAdapter
    public boolean containsItem(long j) {
        return this.mFragmentIds.contains(Integer.valueOf((int) j));
    }

    public void addFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        Log.d(TAG, "addFragment: " + fragment.getClass().getSimpleName());
        if (!this.mFragmentIds.contains(Integer.valueOf(fragment.hashCode()))) {
            this.mFragmentIds.add(Integer.valueOf(fragment.hashCode()));
        }
        if (this.fragments.contains(fragment)) {
            return;
        }
        this.fragments.add(fragment);
        notifyItemInserted(this.fragments.size() - 1);
    }

    public void addFragment(int i, Fragment fragment) {
        if (fragment == null) {
            return;
        }
        Log.d(TAG, "addFragment: " + fragment.getClass().getSimpleName());
        if (!this.mFragmentIds.contains(Integer.valueOf(fragment.hashCode()))) {
            this.mFragmentIds.add(i, Integer.valueOf(fragment.hashCode()));
        }
        if (this.fragments.contains(fragment)) {
            return;
        }
        this.fragments.add(i, fragment);
        notifyItemInserted(i);
    }

    public void removeFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        Log.d(TAG, "removeFragment: " + fragment.getClass().getSimpleName());
        removeFragmentAtIndex(this.fragments.indexOf(fragment));
    }

    public void removeFragmentAtIndex(int i) {
        Log.d(TAG, "remove fragment at index: " + i);
        if (i < 0 || i >= this.fragments.size()) {
            return;
        }
        this.fragments.remove(i);
        this.mFragmentIds.remove(i);
        notifyItemRemoved(i);
    }

    public void removeAllFragment() {
        this.fragments.clear();
        this.mFragmentIds.clear();
        notifyDataSetChanged();
    }

    public Fragment getItem(int i) {
        Log.d(TAG, "getItem: " + i);
        if (i < 0 || i >= this.fragments.size()) {
            return null;
        }
        Log.d(TAG, "getItem: " + this.fragments.get(i));
        return this.fragments.get(i);
    }
}
