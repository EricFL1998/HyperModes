package com.android.deskclock;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import com.android.deskclock.util.Log;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class JobUtil {
    public static int JOB_SCHEDULE_ALERT_ID = 1001;
    public static int JOB_SCHEDULE_UPDATE_HOLIDAY_ID = 1002;
    public static int JOB_SCHEDULE_UPDATE_RINGTONE_ID = 1003;

    private JobUtil() {
    }

    public static void startJobScheduler(Context context, int i, long j) {
        if (!isJobSchedulerExist(context, i)) {
            Log.i("startJobScheduler and the jobId is " + i);
            JobInfo.Builder builder = new JobInfo.Builder(i, new ComponentName(context, (Class<?>) JobSchedulerService.class));
            builder.setPeriodic(j);
            builder.setPersisted(true);
            builder.setRequiredNetworkType(0);
            ((JobScheduler) context.getSystemService("jobscheduler")).schedule(builder.build());
            return;
        }
        Log.i("JobScheduler is exist");
    }

    public static boolean isJobSchedulerExist(Context context, int i) {
        List<JobInfo> allPendingJobs = ((JobScheduler) context.getSystemService("jobscheduler")).getAllPendingJobs();
        if (allPendingJobs == null) {
            Log.d("Util.created(), the jobs is null");
            return false;
        }
        Iterator<JobInfo> it = allPendingJobs.iterator();
        while (it.hasNext()) {
            if (it.next().getId() == i) {
                return true;
            }
        }
        return false;
    }
}
