package com.android.deskclock.alarm.lifepost.model;

import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class Gallery implements Serializable {
    public static final int TYPE_BACKUP = 1;
    public static final int TYPE_CURR = 0;
    private String color;
    private long expire;
    private int id;
    private String marketUrl;
    private String pic;
    private String pkgName;
    private String text;
    private int type;
    private String urlApp;
    private String urlType;

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

    public String getText() {
        return this.text;
    }

    public void setText(String str) {
        this.text = str;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String str) {
        this.pic = str;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String str) {
        this.color = str;
    }

    public long getExpire() {
        return this.expire;
    }

    public void setExpire(long j) {
        this.expire = j;
    }

    public String getUrlType() {
        return this.urlType;
    }

    public void setUrlType(String str) {
        this.urlType = str;
    }

    public String getUrlApp() {
        return this.urlApp;
    }

    public void setUrlApp(String str) {
        this.urlApp = str;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String str) {
        this.pkgName = str;
    }

    public String getMarketUrl() {
        return this.marketUrl;
    }

    public void setMarketUrl(String str) {
        this.marketUrl = str;
    }
}
