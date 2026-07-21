package com.android.deskclock.addition.monitor.data;

/* JADX INFO: loaded from: classes.dex */
public class AlarmAlert {
    private int alarmId;
    private long alarmTime;
    private int id = -1;
    private boolean isShutDown;
    private long notifyTime;
    private long playTime;
    private boolean screenLocked;
    private long showTime;
    private int volume;

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

    public long getAlarmTime() {
        return this.alarmTime;
    }

    public void setAlarmTime(long j) {
        this.alarmTime = j;
    }

    public long getNotifyTime() {
        return this.notifyTime;
    }

    public void setNotifyTime(long j) {
        this.notifyTime = j;
    }

    public long getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(long j) {
        this.playTime = j;
    }

    public long getShowTime() {
        return this.showTime;
    }

    public void setShowTime(long j) {
        this.showTime = j;
    }

    public int getVolume() {
        return this.volume;
    }

    public void setVolume(int i) {
        this.volume = i;
    }

    public boolean isScreenLocked() {
        return this.screenLocked;
    }

    public void setScreenLocked(boolean z) {
        this.screenLocked = z;
    }

    public boolean isShutDown() {
        return this.isShutDown;
    }

    public void setShutDown(boolean z) {
        this.isShutDown = z;
    }
}
