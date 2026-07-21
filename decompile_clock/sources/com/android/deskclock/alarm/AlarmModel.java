package com.android.deskclock.alarm;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.format.DateFormat;
import com.android.deskclock.Alarm;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.bedtime.SleepAlarmTable;
import com.android.deskclock.alarm.bedtime.ZenModeUtil;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarm;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmDataHelper;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.DateFormatUtil;
import com.android.deskclock.util.ScenarioRecognitionUtil;
import com.android.deskclock.util.Util;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class AlarmModel {
    public static final String M12 = "hh:mm";
    public static final String M12_WITH_A = "ahh:mm";
    public static final String M24 = "kk:mm";
    public static final int TYPE_ALARM = 3;
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_TITLE_1 = -1;
    public static final int TYPE_WAKE = 2;
    public static final int TYPE_WAKE_1 = -2;
    private ContentObserver mAlarmDataObserver;
    private final AlarmObserver mAlarmObserver;
    private final Handler mAsyncHandler;
    private final Context mContext;
    private HandlerThread mHandlerThread;
    private final Handler mMainHandler;
    private ContentObserver mSleepDataObserver;
    private Alarm mWakeAlarm;
    private List<Alarm> mAlarms = new ArrayList();
    private Calendar mCalender = Calendar.getInstance();
    private List<AlarmBean> mViewModelList = Collections.synchronizedList(new ArrayList());

    public interface AlarmObserver {
        void onAlarmChanged(Boolean bool);

        void onAlarmLoaded(List<AlarmBean> list);
    }

    public AlarmModel(Context context, AlarmObserver alarmObserver) {
        HandlerThread handlerThread = new HandlerThread("AlarmDataThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        Handler handler = new Handler(this.mHandlerThread.getLooper());
        this.mAsyncHandler = handler;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mContext = context;
        this.mAlarmObserver = alarmObserver;
        this.mAlarmDataObserver = new ContentObserver(handler) { // from class: com.android.deskclock.alarm.AlarmModel.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                List<Alarm> listQueryNormalAlarms = DataPrepareUtil.queryNormalAlarms();
                AlarmModel.this.mAlarms.clear();
                AlarmModel.this.mAlarms.addAll(listQueryNormalAlarms);
                AlarmModel alarmModel = AlarmModel.this;
                final List listTransferToViewModel = alarmModel.transferToViewModel(alarmModel.mAlarms, AlarmModel.this.mWakeAlarm);
                AlarmModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.AlarmModel.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AlarmModel.this.mViewModelList.clear();
                        AlarmModel.this.mViewModelList.addAll(listTransferToViewModel);
                        if (AlarmModel.this.mAlarmObserver != null) {
                            AlarmModel.this.mAlarmObserver.onAlarmChanged(false);
                        }
                    }
                });
            }
        };
        this.mSleepDataObserver = new ContentObserver(handler) { // from class: com.android.deskclock.alarm.AlarmModel.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                AlarmModel.this.mWakeAlarm = BedtimeUtil.isWakeAlarmSupport(AlarmModel.this.mContext) ? DataPrepareUtil.queryWakeAlarm() : null;
                AlarmModel alarmModel = AlarmModel.this;
                final List listTransferToViewModel = alarmModel.transferToViewModel(alarmModel.mAlarms, AlarmModel.this.mWakeAlarm);
                AlarmModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.AlarmModel.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AlarmModel.this.mViewModelList.clear();
                        AlarmModel.this.mViewModelList.addAll(listTransferToViewModel);
                        if (AlarmModel.this.mAlarmObserver != null) {
                            AlarmModel.this.mAlarmObserver.onAlarmChanged(true);
                        }
                    }
                });
            }
        };
        context.getContentResolver().registerContentObserver(Alarm.Columns.CONTENT_URI, true, this.mAlarmDataObserver);
        context.getContentResolver().registerContentObserver(ShiftAlarm.Columns.CONTENT_URI, true, this.mAlarmDataObserver);
        context.getContentResolver().registerContentObserver(SleepAlarmTable.CONTENT_URI, true, this.mSleepDataObserver);
    }

    public void release() {
        if (this.mAlarmDataObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mAlarmDataObserver);
            this.mAlarmDataObserver = null;
        }
        if (this.mSleepDataObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mSleepDataObserver);
            this.mSleepDataObserver = null;
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
    }

    public void startLoad() {
        this.mAsyncHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.AlarmModel.3
            @Override // java.lang.Runnable
            public void run() {
                final Alarm alarmQueryWakeAlarm;
                ScenarioRecognitionUtil.INSTANCE.setScenarioState(332L, true);
                final List<Alarm> listQueryNormalAlarms = DataPrepareUtil.queryNormalAlarms();
                if (BedtimeUtil.isWakeAlarmSupport(AlarmModel.this.mContext)) {
                    alarmQueryWakeAlarm = DataPrepareUtil.queryWakeAlarm();
                    if (alarmQueryWakeAlarm == null) {
                        BedtimeUtil.setBedTimeCompleted(AlarmModel.this.mContext, false);
                        BedtimeUtil.setNotificationAdvTime(AlarmModel.this.mContext, 15);
                        BedtimeUtil.setBedtimeOpenState(AlarmModel.this.mContext, false);
                        BedtimeUtil.setDisturbanceState(AlarmModel.this.mContext, true);
                        ZenModeUtil.resetZenRule(AlarmModel.this.mContext);
                    }
                } else {
                    alarmQueryWakeAlarm = null;
                }
                final List listTransferToViewModel = AlarmModel.this.transferToViewModel(listQueryNormalAlarms, alarmQueryWakeAlarm);
                AlarmModel.this.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.AlarmModel.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AlarmModel.this.mAlarms = listQueryNormalAlarms;
                        AlarmModel.this.mWakeAlarm = alarmQueryWakeAlarm;
                        AlarmModel.this.mViewModelList.clear();
                        AlarmModel.this.mViewModelList.addAll(listTransferToViewModel);
                        if (AlarmModel.this.mAlarmObserver != null) {
                            ScenarioRecognitionUtil.INSTANCE.setScenarioState(332L, false);
                            AlarmModel.this.mAlarmObserver.onAlarmLoaded(AlarmModel.this.mViewModelList);
                        }
                    }
                });
            }
        });
    }

    public void showData() {
        if (DataPrepareUtil.isQueryDone() && DataPrepareUtil.isValid()) {
            this.mAlarms = DataPrepareUtil.getAlarms();
            this.mWakeAlarm = DataPrepareUtil.getWakeAlarm(this.mContext);
            this.mViewModelList.clear();
            this.mViewModelList.addAll(transferToViewModel(DataPrepareUtil.getAlarms(), DataPrepareUtil.getWakeAlarm(this.mContext)));
            AlarmObserver alarmObserver = this.mAlarmObserver;
            if (alarmObserver != null) {
                alarmObserver.onAlarmLoaded(this.mViewModelList);
            }
            if (BedtimeUtil.isWakeAlarmSupport(this.mContext) && this.mWakeAlarm == null) {
                BedtimeUtil.setBedTimeCompleted(this.mContext, false);
                BedtimeUtil.setNotificationAdvTime(this.mContext, 15);
                BedtimeUtil.setBedtimeOpenState(this.mContext, false);
                BedtimeUtil.setDisturbanceState(this.mContext, true);
                ZenModeUtil.resetZenRule(this.mContext);
                return;
            }
            return;
        }
        startLoad();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<AlarmBean> transferToViewModel(List<Alarm> list, Alarm alarm) {
        long j;
        String skipDateString;
        String alarmInFuture;
        DateFormatUtil.reset();
        ArrayList arrayList = new ArrayList();
        long j2 = 0;
        if (alarm != null) {
            arrayList.add(new AlarmBean(-1, 1, null));
            AlarmBean alarmBean = new AlarmBean(alarm.id, 2, alarm);
            alarmBean.daysOfWeekStr = alarm.daysOfWeek.toString(this.mContext, !alarm.enabled);
            if (alarm.enabled) {
                alarmInFuture = SetAlarmController.getAlarmInFuture(this.mCalender, this.mContext, alarm.hour, alarm.minutes, alarm.daysOfWeek);
            } else {
                if (alarm.skipTime != 0) {
                    alarmBean.alarmSkipOnceText = Util.getSkipDateString(this.mContext, alarm.skipTime);
                }
                alarmInFuture = "";
            }
            alarmBean.alarmInFutureText = alarmInFuture;
            this.mCalender.set(11, alarm.hour);
            this.mCalender.set(12, alarm.minutes);
            alarmBean.mTimeDisplay = (String) DateFormat.format(AlarmHelper.get24HourMode() ? M24 : M12, this.mCalender);
            if (!AlarmHelper.get24HourMode()) {
                String[] amPmStrings = DateFormatUtil.getAmPmStrings();
                alarmBean.mAmPmDisplay = this.mCalender.get(9) == 0 ? amPmStrings[0] : amPmStrings[1];
            }
            arrayList.add(alarmBean);
        }
        if (list != null && list.size() > 0) {
            if (alarm != null) {
                arrayList.add(new AlarmBean(-2, 1, null));
            }
            int size = list.size();
            int i = 0;
            while (i < size) {
                Alarm alarm2 = list.get(i);
                AlarmBean alarmBean2 = new AlarmBean(alarm2.id, 3, alarm2);
                if (alarm2.type == 2) {
                    alarmBean2.daysOfWeekStr = ShiftAlarmDataHelper.getShiftDurationFromAlarmId(alarm2.id);
                    if (alarm2.skipTime != j2) {
                        alarmBean2.alarmSkipOnceText = Util.getSkipDateString(this.mContext, alarm2.skipTime);
                    } else {
                        alarmBean2.alarmSkipOnceText = null;
                    }
                } else {
                    alarmBean2.daysOfWeekStr = alarm2.daysOfWeek.toString(this.mContext, !alarm2.enabled);
                }
                if (alarm2.enabled) {
                    skipDateString = SetAlarmController.getAlarmInFuture(this.mCalender, this.mContext, alarm2.hour, alarm2.minutes, alarm2.daysOfWeek);
                    j = 0;
                } else {
                    j = 0;
                    if (alarm2.skipTime == 0) {
                        skipDateString = "";
                    } else {
                        skipDateString = Util.getSkipDateString(this.mContext, alarm2.skipTime);
                        alarmBean2.alarmSkipOnceText = skipDateString;
                    }
                }
                alarmBean2.alarmInFutureText = skipDateString;
                CharSequence charSequence = AlarmHelper.get24HourMode() ? M24 : M12;
                this.mCalender.set(11, alarm2.hour);
                this.mCalender.set(12, alarm2.minutes);
                alarmBean2.mTimeDisplay = (String) DateFormat.format(charSequence, this.mCalender);
                if (!AlarmHelper.get24HourMode()) {
                    String[] amPmStrings2 = DateFormatUtil.getAmPmStrings();
                    alarmBean2.mAmPmDisplay = this.mCalender.get(9) == 0 ? amPmStrings2[0] : amPmStrings2[1];
                }
                arrayList.add(alarmBean2);
                i++;
                j2 = j;
            }
        }
        return arrayList;
    }

    public void resetCalender() {
        this.mCalender.setTimeZone(TimeZone.getDefault());
    }

    public void updateData() {
        DateFormatUtil.reset();
        List<AlarmBean> list = this.mViewModelList;
        if (list == null || list.isEmpty()) {
            return;
        }
        synchronized (this.mViewModelList) {
            for (int i = 0; i < this.mViewModelList.size(); i++) {
                AlarmBean alarmBean = this.mViewModelList.get(i);
                if (alarmBean.type != 1) {
                    Alarm alarm = alarmBean.alarm;
                    String skipDateString = "";
                    if (alarm.enabled) {
                        skipDateString = SetAlarmController.getAlarmInFuture(this.mCalender, this.mContext, alarm.hour, alarm.minutes, alarm.daysOfWeek);
                    } else if (alarm.skipTime != 0) {
                        skipDateString = Util.getSkipDateString(this.mContext, alarm.skipTime);
                        alarmBean.alarmSkipOnceText = skipDateString;
                    }
                    alarmBean.alarmInFutureText = skipDateString;
                    this.mCalender.set(11, alarm.hour);
                    this.mCalender.set(12, alarm.minutes);
                    if (!AlarmHelper.get24HourMode()) {
                        String[] amPmStrings = DateFormatUtil.getAmPmStrings();
                        alarmBean.mAmPmDisplay = this.mCalender.get(9) == 0 ? amPmStrings[0] : amPmStrings[1];
                    }
                    alarmBean.mTimeDisplay = (String) DateFormat.format(AlarmHelper.get24HourMode() ? M24 : M12, this.mCalender);
                }
            }
        }
    }

    public static class AlarmBean {
        public Alarm alarm;
        public String alarmInFutureText;
        public String alarmSkipOnceText;
        public String daysOfWeekStr;
        public int id;
        public String mAmPmDisplay;
        public String mTimeDisplay;
        public int type;

        public AlarmBean(int i, int i2, Alarm alarm) {
            this.id = i;
            this.type = i2;
            this.alarm = alarm;
        }
    }
}
