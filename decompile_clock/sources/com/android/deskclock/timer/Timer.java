package com.android.deskclock.timer;

import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class Timer {
    public static final int STATE_PAUSE = 2;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_STOP = 0;
    public static final int STATE_TIME_OFF = 3;
    private long duration;
    private String label;
    private long remain;
    private long time;
    private int id = -2;
    private int type = 0;
    private int state = 0;
    private boolean silent = false;
    private boolean bright = true;

    public boolean isSilent() {
        return false;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String str) {
        this.label = str;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long j) {
        this.duration = j;
    }

    public long getRemain() {
        return this.remain;
    }

    public void setRemain(long j) {
        this.remain = j;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long j) {
        this.time = j;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int i) {
        this.state = i;
    }

    public void setSilent(boolean z) {
        this.silent = z;
    }

    public boolean isBright() {
        return this.bright;
    }

    public void setBright(boolean z) {
        this.bright = z;
    }

    public void cloneFrom(Timer timer) {
        setId(timer.getId());
        setTime(timer.getTime());
        setDuration(timer.getDuration());
        setRemain(timer.getRemain());
        setType(timer.getType());
        setState(timer.getState());
        setSilent(timer.isSilent());
        setBright(timer.isBright());
        setLabel(timer.getLabel());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Timer:(id:");
        StringBuilder sbAppend = sb.append(this.id).append(", duration:").append(this.duration).append(", remain:").append(this.remain).append(", time:");
        long j = this.time;
        sbAppend.append(j == 0 ? 0 : Util.formatTimeForLog(j)).append(", type:").append(this.type).append(", state:").append(this.state).append(", silent:").append(this.silent).append(", bright:").append(this.bright).append(", label:").append(this.label).append(")");
        return sb.toString();
    }
}
