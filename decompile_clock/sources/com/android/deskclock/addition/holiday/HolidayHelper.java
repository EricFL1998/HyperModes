package com.android.deskclock.addition.holiday;

import android.content.Context;
import android.os.AsyncTask;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.Log;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class HolidayHelper {
    private static final int HOLIDAY_YEAR_VALID = 2013;
    private static final long REFRESH_NEXT_ALARM_DELAYED_TIME = 1000;
    public static final String TAG = "DC:Holiday";

    public static void init() {
        new HolidayInitAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static boolean isHolidayDataInvalid(Context context) {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(1) < 2013 || HolidayInstance.getInstance(context).getTheYearHolidayData(calendar.get(1)) < 0;
    }

    public static boolean isHoliday(Context context, Calendar calendar) {
        int i = calendar.get(1);
        int i2 = calendar.get(6);
        if (HolidayInstance.getInstance(context).isWorkday(i, i2)) {
            return false;
        }
        if (HolidayInstance.getInstance(context).isFreeday(i, i2)) {
            return true;
        }
        return isWeekEnd(calendar);
    }

    private static boolean isWeekEnd(Calendar calendar) {
        return calendar.get(7) == 7 || calendar.get(7) == 1;
    }

    private static class HolidayInitAsyncTask extends AsyncTask<Void, Void, Void> {
        private HolidayInitAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            Log.i(HolidayHelper.TAG, "initial holiday data");
            final Context appContext = DeskClockApp.getAppContext();
            HolidayInstance.getInstance(appContext);
            AlarmThreadPool.runOnUiThreadDelayed(new Runnable() { // from class: com.android.deskclock.addition.holiday.HolidayHelper.HolidayInitAsyncTask.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.i(HolidayHelper.TAG, "refresh next alarm after holiday data initialized");
                    AlarmHelper.setNextAlert(appContext);
                }
            }, 1000L);
            return null;
        }
    }
}
