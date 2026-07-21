package com.android.deskclock.worldclock;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.format.DateFormat;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.CityZoneHelper;
import com.android.deskclock.util.DateFormatUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.ScenarioRecognitionUtil;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.stat.StatHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class TimezoneModel {
    private static final String M12 = "hh:mm";
    private static final String M24 = "kk:mm";
    private static String TAG = "DC:TimezoneModel";
    private final Handler mAsyncHandler;
    private final Context mContext;
    private ContentObserver mDataObserver;
    private final HandlerThread mHandlerThread;
    private final Handler mMainHandler;
    private final TimezoneObserver mTimezoneObserver;
    private List<CityObj> mDataList = new ArrayList();
    private List<TimezoneBean> mDataShowList = new ArrayList();

    public interface TimezoneObserver {
        void onTimezoneChanged(List<CityObj> list, List<TimezoneBean> list2);

        void onTimezoneChangedForWidget(List<CityObj> list);

        void onTimezoneLoaded(List<CityObj> list, List<TimezoneBean> list2);
    }

    public TimezoneModel(final Context context, TimezoneObserver timezoneObserver) {
        HandlerThread handlerThread = new HandlerThread("TimezoneDataThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        this.mAsyncHandler = handler;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mContext = context;
        this.mTimezoneObserver = timezoneObserver;
        this.mDataObserver = new ContentObserver(handler) { // from class: com.android.deskclock.worldclock.TimezoneModel.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                List<WorldClock> timezone = TimezoneModel.this.getTimezone();
                StatHelper.recordNumericPropertyEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_WORLDCLOCK_COUNT, timezone.size());
                final List<CityObj> cityById = TimezoneModel.getCityById(timezone);
                final List<TimezoneBean> timezoneBeanById = TimezoneModel.getTimezoneBeanById(timezone);
                context.sendBroadcast(new Intent("miui.intent.action.TIMEZONE_CHANGED_FOR_WIDGET"));
                if (TimezoneModel.this.mTimezoneObserver != null) {
                    TimezoneModel.this.mTimezoneObserver.onTimezoneChangedForWidget(cityById);
                }
                TimezoneModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.worldclock.TimezoneModel.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        TimezoneModel.this.mDataList.clear();
                        TimezoneModel.this.mDataList.addAll(cityById);
                        TimezoneModel.this.mDataShowList.clear();
                        TimezoneModel.this.mDataShowList.addAll(timezoneBeanById);
                        if (TimezoneModel.this.mTimezoneObserver != null) {
                            TimezoneModel.this.mTimezoneObserver.onTimezoneChanged(TimezoneModel.this.mDataList, TimezoneModel.this.mDataShowList);
                        }
                    }
                });
            }
        };
        context.getContentResolver().registerContentObserver(WorldClock.CONTENT_URI, true, this.mDataObserver);
    }

    public void release() {
        if (this.mDataObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mDataObserver);
            this.mDataObserver = null;
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
    }

    public void startLoad() {
        this.mAsyncHandler.post(new Runnable() { // from class: com.android.deskclock.worldclock.TimezoneModel.2
            @Override // java.lang.Runnable
            public void run() {
                ScenarioRecognitionUtil.INSTANCE.setScenarioState(333L, true);
                List<WorldClock> timezone = TimezoneModel.this.getTimezone();
                StatHelper.recordNumericPropertyEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_WORLDCLOCK_COUNT, timezone.size());
                final List<CityObj> cityById = TimezoneModel.getCityById(timezone);
                final List<TimezoneBean> timezoneBeanById = TimezoneModel.getTimezoneBeanById(timezone);
                TimezoneModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.worldclock.TimezoneModel.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        TimezoneModel.this.mDataList.clear();
                        TimezoneModel.this.mDataList.addAll(cityById);
                        TimezoneModel.this.mDataShowList.clear();
                        TimezoneModel.this.mDataShowList.addAll(timezoneBeanById);
                        if (TimezoneModel.this.mTimezoneObserver != null) {
                            ScenarioRecognitionUtil.INSTANCE.setScenarioState(333L, false);
                            TimezoneModel.this.mTimezoneObserver.onTimezoneLoaded(TimezoneModel.this.mDataList, TimezoneModel.this.mDataShowList);
                        }
                    }
                });
            }
        });
    }

    public static List<CityObj> getCityById(List<WorldClock> list) {
        CityZoneHelper.init();
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                WorldClock worldClock = list.get(i);
                CityObj cityTimezoneItemById = CityZoneHelper.getCityTimezoneItemById(worldClock.cityId);
                if (cityTimezoneItemById != null) {
                    cityTimezoneItemById.id = worldClock.id;
                    arrayList.add(cityTimezoneItemById);
                }
            }
        }
        return arrayList;
    }

    List<WorldClock> getTimezone() {
        Log.i(TAG, "getTimezone running in " + Thread.currentThread().getName());
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(WorldClock.CONTENT_URI, WorldClock.PROJECTION, "cityid_new is not null", null, null);
        try {
            if (cursorQuery.moveToFirst()) {
                do {
                    arrayList.add(new WorldClock(cursorQuery));
                } while (cursorQuery.moveToNext());
            }
            return arrayList;
        } finally {
            cursorQuery.close();
        }
    }

    public static List<TimezoneBean> getTimezoneBeanById(List<WorldClock> list) {
        ArrayList arrayList = new ArrayList();
        CityZoneHelper.init();
        String str = AlarmHelper.get24HourMode() ? "kk:mm" : "hh:mm";
        String[] amPmStrings = DateFormatUtil.getAmPmStrings();
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                WorldClock worldClock = list.get(i);
                CityObj cityTimezoneItemById = CityZoneHelper.getCityTimezoneItemById(worldClock.cityId);
                if (cityTimezoneItemById != null) {
                    cityTimezoneItemById.id = worldClock.id;
                    TimezoneBean timezoneBean = new TimezoneBean(cityTimezoneItemById);
                    TimeZone timeZone = TimeZone.getTimeZone(cityTimezoneItemById.mTimeZone);
                    Calendar calendar = Calendar.getInstance(timeZone);
                    timezoneBean.dateDisplay = TimeUtil.formatDate(DeskClockApp.getAppContext().getString(R.string.worldcolock_timezone_date), calendar.getTime(), timeZone);
                    timezoneBean.timeDisplay = DateFormat.format(str, calendar).toString();
                    timezoneBean.amPmDisplay = calendar.get(9) == 0 ? amPmStrings[0] : amPmStrings[1];
                    timezoneBean.timezoneName = cityTimezoneItemById.mCityName;
                    timezoneBean.timezoneDateGap = getGapString(timeZone);
                    arrayList.add(timezoneBean);
                }
            }
        }
        return arrayList;
    }

    public static String getGapString(TimeZone timeZone) {
        int i;
        String quantityString;
        int offset = timeZone.getOffset(Calendar.getInstance().getTimeInMillis()) - TimeZone.getDefault().getOffset(Calendar.getInstance().getTimeInMillis());
        if (offset < 0) {
            offset = -offset;
            i = R.plurals.worldcolock_timezone_gap_slow;
        } else {
            i = R.plurals.worldcolock_timezone_gap_fast;
        }
        long j = offset;
        if (j % AlarmHelper.ARRIVING_ALARM_DURATION == 0) {
            Resources resources = DeskClockApp.getAppDEContext().getResources();
            int i2 = (int) (j / AlarmHelper.ARRIVING_ALARM_DURATION);
            quantityString = resources.getQuantityString(i, i2, Integer.valueOf(i2));
        } else {
            float f = offset / 3600000.0f;
            quantityString = DeskClockApp.getAppDEContext().getResources().getQuantityString(i, ((int) f) + 1, Float.valueOf(f));
        }
        return offset == 0 ? DeskClockApp.getAppDEContext().getString(R.string.worldcolock_timezone_local) : quantityString;
    }

    public void resetShowData(long j) {
        String[] amPmStrings = DateFormatUtil.getAmPmStrings();
        String str = AlarmHelper.get24HourMode() ? "kk:mm" : "hh:mm";
        for (int i = 0; i < this.mDataShowList.size(); i++) {
            TimezoneBean timezoneBean = this.mDataShowList.get(i);
            TimeZone timeZone = TimeZone.getTimeZone(timezoneBean.cityObj.mTimeZone);
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(j);
            timezoneBean.dateDisplay = TimeUtil.formatDate(DeskClockApp.getAppContext().getString(R.string.worldcolock_timezone_date), calendar.getTime(), timeZone);
            timezoneBean.timeDisplay = DateFormat.format(str, calendar).toString();
            timezoneBean.amPmDisplay = calendar.get(9) == 0 ? amPmStrings[0] : amPmStrings[1];
            timezoneBean.timezoneDateGap = getGapString(timeZone);
        }
    }

    public void updateDatabase(List<CityObj> list) {
        Context appDEContext = DeskClockApp.getAppDEContext();
        appDEContext.getContentResolver().delete(WorldClock.CONTENT_URI, null, null);
        for (int i = 0; i < list.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WorldClock.Columns.CITY_ID, list.get(i).mCityId);
            appDEContext.getContentResolver().insert(WorldClock.CONTENT_URI, contentValues);
        }
    }

    public static class TimezoneBean {
        public String amPmDisplay;
        public CityObj cityObj;
        public String dateDisplay;
        public String timeDisplay;
        public String timezoneDateGap;
        public String timezoneName;

        public TimezoneBean(CityObj cityObj) {
            this.cityObj = cityObj;
        }
    }
}
