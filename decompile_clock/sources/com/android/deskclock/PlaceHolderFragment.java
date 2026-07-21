package com.android.deskclock;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.list.EditableAdapter;

/* JADX INFO: loaded from: classes.dex */
public class PlaceHolderFragment extends Fragment implements TabNavigatorContentFragment.IClockViews, EditableAdapter.MultiChoiceModeListener, TabNavigatorContentFragment.IFragmentChange, TabNavigatorContentFragment.IFabClick {
    @Override // android.view.ActionMode.Callback
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override // com.android.deskclock.view.list.EditableAdapter.MultiChoiceModeListener
    public void onAllItemCheckedStateChanged(ActionMode actionMode, boolean z) {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onCenterClick(View view) {
    }

    @Override // android.view.ActionMode.Callback
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onDataChanged() {
    }

    @Override // android.view.ActionMode.Callback
    public void onDestroyActionMode(ActionMode actionMode) {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onEndClick(View view) {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onEndClick2(View view) {
    }

    @Override // android.widget.AbsListView.MultiChoiceModeListener
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long j, boolean z) {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IFragmentChange
    public void onLeave() {
    }

    @Override // android.view.ActionMode.Callback
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IFabClick
    public void onStartClick(View view) {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimeChanged() {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimeFormatChanged() {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimeTick() {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public void onTimezoneChanged() {
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IClockViews
    public boolean shouldKeepScreenOn() {
        return false;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.layout_placeholder_fragment, viewGroup, false);
    }

    @Override // com.android.deskclock.TabNavigatorContentFragment.IFragmentChange
    public void onEnter() {
        StatHelper.recordCountEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_ENTER_PLACE_HOLDER_FRAGMENT);
        OneTrackStatHelper.trackTriggerEvent("");
    }
}
