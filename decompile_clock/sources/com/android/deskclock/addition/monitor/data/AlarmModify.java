package com.android.deskclock.addition.monitor.data;

import com.android.deskclock.addition.monitor.MonitorImpl;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class AlarmModify {
    public static final int MODIFY_BOOT = 2;
    public static final int MODIFY_DELETE = 6;
    public static final int MODIFY_END = 4;
    public static final int MODIFY_INIT = 1;
    public static final int MODIFY_INSERT = 5;
    public static final int MODIFY_SHUTDOWN = 3;
    public static final int MODIFY_SHUTDOWN_ALARM_STATE = 10;
    public static final int MODIFY_TIME_SET = 9;
    public static final int MODIFY_TIME_SET_PRE = 8;
    public static final int MODIFY_UPDATE = 7;
    private int alarmId;
    private String content;
    private int id = -1;
    private long time;
    private int type;

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public int getAlarmId() {
        return this.alarmId;
    }

    public void setAlarmId(int i) {
        this.alarmId = i;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long j) {
        this.time = j;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String str) {
        this.content = str;
    }

    public static String getType(int i) {
        switch (i) {
            case 1:
                return "init";
            case 2:
                return "boot";
            case 3:
                return "shutdown";
            case 4:
                return "end";
            case 5:
                return "insert";
            case 6:
                return "delete";
            case 7:
                return "update";
            case 8:
                return "time_set_pre";
            case 9:
                return "time_set";
            case 10:
                return "shutdown_alarm_state";
            default:
                return "";
        }
    }

    public String toString() {
        return "AlarmModify, id: " + this.id + " alarmId: " + this.alarmId + " type: " + getType(this.type) + " time: " + Util.formatTimeForLog(this.time) + " content: " + MonitorImpl.getContentValuesFromString(this.content);
    }
}
