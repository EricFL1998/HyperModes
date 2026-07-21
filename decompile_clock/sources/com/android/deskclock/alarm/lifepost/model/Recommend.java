package com.android.deskclock.alarm.lifepost.model;

/* JADX INFO: loaded from: classes.dex */
public class Recommend {
    public static final int TYPE_GALLERY = 1;
    private int content;
    private long expire;
    private int id;
    private int type;

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public long getExpire() {
        return this.expire;
    }

    public void setExpire(long j) {
        this.expire = j;
    }

    public int getContent() {
        return this.content;
    }

    public void setContent(int i) {
        this.content = i;
    }
}
