package com.android.deskclock.util.fab;

import android.content.SharedPreferences;
import android.util.Log;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.timer.Timer;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.view.tab.TabViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class FabDataHelper {
    public static final int STATE_PAUSE = 2;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_STOP = 0;
    public static final int STATE_TIME_OFF = 3;
    private static final String TAG = "DC:FabDataHelper";
    private static Map<String, TabInfo> mDataMap;
    private static FabDataHelper mFabDataHelper;
    private SharedPreferences mSharedPref;
    public List<DataChangeListener> mListener = new ArrayList();
    private TabInfo mTempInfo = new TabInfo();

    interface DataChangeListener {
        void onFabDataChanged(TabInfo tabInfo);

        void onFabEnableChange(TabInfo tabInfo, boolean z);
    }

    static {
        HashMap map = new HashMap();
        mDataMap = map;
        map.put(TabViewModel.TAB_ALARM, new TabInfo());
        mDataMap.put(TabViewModel.TAB_CLOCK, new TabInfo());
        mDataMap.put(TabViewModel.TAB_STOPWATCH, new TabInfo());
        mDataMap.put(TabViewModel.TAB_TIMER, new TabInfo());
        mFabDataHelper = new FabDataHelper();
    }

    private FabDataHelper() {
        if (this.mSharedPref == null) {
            this.mSharedPref = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext());
        }
    }

    public static FabDataHelper getInstance() {
        if (mFabDataHelper == null) {
            synchronized (FabDataHelper.class) {
                if (mFabDataHelper == null) {
                    mFabDataHelper = new FabDataHelper();
                }
            }
        }
        return mFabDataHelper;
    }

    public void initAlarmFab() {
        resetAlarmFab();
    }

    public void initStopWatchFab() {
        resetStopWatchFab();
    }

    public void initTimerFab() {
        resetTimerFab();
    }

    private void resetAlarmFab() {
        TabInfo tabInfo = mDataMap.get(TabViewModel.TAB_ALARM);
        tabInfo.tabName = TabViewModel.TAB_ALARM;
        tabInfo.num = 1;
        tabInfo.setVisibleStates(8, 8, 8);
    }

    private void resetStopWatchFab() {
        TabInfo tabInfo = mDataMap.get(TabViewModel.TAB_STOPWATCH);
        if (tabInfo == null) {
            tabInfo = new TabInfo();
            mDataMap.put(TabViewModel.TAB_STOPWATCH, tabInfo);
        }
        tabInfo.tabName = TabViewModel.TAB_STOPWATCH;
        if (this.mSharedPref == null) {
            this.mSharedPref = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext());
        }
        if (this.mSharedPref.getBoolean(Util.STOPWATCH_STATE_RUNNING_PREF, false)) {
            setStopWatchInfo(1);
        } else if (this.mSharedPref.getLong(Util.STOPWATCH_ELAPSED_TIME_PREF, 0L) == 0) {
            setStopWatchInfo(0);
        } else {
            setStopWatchInfo(2);
        }
    }

    private void resetTimerFab() {
        TabInfo tabInfo = mDataMap.get(TabViewModel.TAB_TIMER);
        if (tabInfo == null) {
            tabInfo = new TabInfo();
            mDataMap.put(TabViewModel.TAB_TIMER, tabInfo);
        }
        tabInfo.tabName = TabViewModel.TAB_TIMER;
        Timer timer = TimerDao.getTimer(DeskClockApp.getAppDEContext());
        if (timer != null) {
            setTimerInfo(timer.getState());
        } else if (mDataMap.get(TabViewModel.TAB_TIMER).tabName == null) {
            setTimerInfo(0);
        }
    }

    public void setDataChangeListener(DataChangeListener dataChangeListener) {
        this.mListener.add(dataChangeListener);
    }

    public static TabInfo getTabInfo(String str) {
        return mDataMap.get(str);
    }

    public void changeFabState(String str, int i) {
        if (mDataMap.get(str).state == i) {
            return;
        }
        mDataMap.get(str).copyTo(this.mTempInfo);
        Log.d(TAG, "changeFabState: " + str + "state: " + i);
        if (str.equals(TabViewModel.TAB_STOPWATCH)) {
            setStopWatchInfo(i);
        } else if (str.equals(TabViewModel.TAB_TIMER)) {
            setTimerInfo(i);
        }
        List<DataChangeListener> list = this.mListener;
        if (list != null) {
            Iterator<DataChangeListener> it = list.iterator();
            while (it.hasNext()) {
                it.next().onFabDataChanged(this.mTempInfo);
            }
        }
    }

    private static void setStopWatchInfo(int i) {
        TabInfo tabInfo = mDataMap.get(TabViewModel.TAB_STOPWATCH);
        if (i == 0) {
            tabInfo.num = 1;
            tabInfo.setImageIds(-1, R.drawable.fab_start_icon, -1);
            tabInfo.setVisibleStates(8, 0, 8);
            tabInfo.state = 0;
            return;
        }
        if (i == 1) {
            tabInfo.num = 2;
            tabInfo.setImageIds(R.drawable.fab_lap_icon, -1, R.drawable.fab_pause_icon);
            tabInfo.setVisibleStates(0, 8, 0);
            tabInfo.state = 1;
            return;
        }
        if (i != 2) {
            return;
        }
        tabInfo.num = 2;
        tabInfo.setImageIds(R.drawable.fab_stop_icon, -1, R.drawable.fab_start_icon);
        tabInfo.setVisibleStates(0, 8, 0);
        tabInfo.state = 2;
    }

    private static void setTimerInfo(int i) {
        TabInfo tabInfo = mDataMap.get(TabViewModel.TAB_TIMER);
        if (i == 0) {
            tabInfo.num = 1;
            tabInfo.setImageIds(-1, R.drawable.fab_start_icon, -1);
            tabInfo.setVisibleStates(8, 0, 8);
            tabInfo.state = 0;
            return;
        }
        if (i == 1) {
            tabInfo.num = 2;
            tabInfo.setImageIds(R.drawable.fab_stop_icon, -1, R.drawable.fab_pause_icon);
            tabInfo.setVisibleStates(0, 8, 0);
            tabInfo.state = 1;
            return;
        }
        if (i == 2) {
            tabInfo.num = 2;
            tabInfo.setImageIds(R.drawable.fab_stop_icon, -1, R.drawable.fab_start_icon);
            tabInfo.setVisibleStates(0, 8, 0);
            tabInfo.state = 2;
            return;
        }
        if (i != 3) {
            return;
        }
        tabInfo.num = 1;
        tabInfo.setImageIds(-1, R.drawable.fab_stop_icon, -1);
        tabInfo.setVisibleStates(8, 0, 8);
        tabInfo.state = 3;
    }

    public void setEnableState(String str, boolean z) {
        if (str.equals(TabViewModel.TAB_TIMER)) {
            mDataMap.get(str).setCenterImageId(z ? R.drawable.fab_start_icon : R.drawable.fab_start_disenable_icon);
            Iterator<DataChangeListener> it = this.mListener.iterator();
            while (it.hasNext()) {
                it.next().onFabEnableChange(mDataMap.get(str), z);
            }
        }
    }

    public static class TabInfo {
        int num;
        int state;
        String tabName;
        int[] imageIds = {-1, -1, -1};
        int[] visibleStates = {8, 8, 8};

        public void setImageIds(int i, int i2, int i3) {
            int[] iArr = this.imageIds;
            iArr[0] = i;
            iArr[1] = i2;
            iArr[2] = i3;
        }

        public void setCenterImageId(int i) {
            this.imageIds[1] = i;
        }

        public void setVisibleStates(int i, int i2, int i3) {
            int[] iArr = this.visibleStates;
            iArr[0] = i;
            iArr[1] = i2;
            iArr[2] = i3;
        }

        public int getStartImageId() {
            return this.imageIds[0];
        }

        public int getCenterImageId() {
            return this.imageIds[1];
        }

        public int getEndImageId() {
            return this.imageIds[2];
        }

        public void copyTo(TabInfo tabInfo) {
            if (tabInfo == null) {
                return;
            }
            tabInfo.num = this.num;
            tabInfo.tabName = this.tabName;
            int[] iArr = tabInfo.imageIds;
            int[] iArr2 = this.imageIds;
            iArr[0] = iArr2[0];
            iArr[1] = iArr2[1];
            iArr[2] = iArr2[2];
            int[] iArr3 = tabInfo.visibleStates;
            int[] iArr4 = this.visibleStates;
            iArr3[0] = iArr4[0];
            iArr3[1] = iArr4[1];
            iArr3[2] = iArr4[2];
        }

        public String toString() {
            return "tabName: " + this.tabName + " num: " + this.num + " imageIds: " + this.imageIds[0] + " " + this.imageIds[1] + " " + this.imageIds[2] + " visibleStates: " + this.visibleStates[0] + " " + this.visibleStates[1] + " " + this.visibleStates[2] + " state: " + this.state;
        }
    }
}
