package com.android.deskclock.alarm.shiftalarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.deskclock.R;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.Fragment;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarmRepeatEditFragment extends Fragment {
    protected ViewGroup mRootView;

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setThemeRes(R.style.EditBottomSheetTheme);
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = (ViewGroup) layoutInflater.inflate(R.layout.fragment_shift_repeat_edit, viewGroup, false);
        initActionBar();
        return this.mRootView;
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.shift_alarm);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setExpandState(0);
            actionBar.setResizable(false);
        }
    }
}
