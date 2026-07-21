package com.android.deskclock.alarm.bedtime;

import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SettingGuidePagerAdapter extends PagerAdapter {
    private List<View> viewList;

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public SettingGuidePagerAdapter(List<View> list) {
        this.viewList = list;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        viewGroup.addView(this.viewList.get(i));
        return this.viewList.get(i);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView(this.viewList.get(i));
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.viewList.size();
    }
}
