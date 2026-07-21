package com.android.deskclock.alarm.lifepost.model;

import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class News implements Serializable {
    private String desc;
    private long expire;
    private int id;
    private boolean isShow;
    private String pic;
    private String pkgName;
    private String title;
    private String type;
    private String url;
    private String urlApp;
    private String urlType;

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public boolean isShow() {
        return this.isShow;
    }

    public void setShow(boolean z) {
        this.isShow = z;
    }

    public long getExpire() {
        return this.expire;
    }

    public void setExpire(long j) {
        this.expire = j;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String str) {
        this.desc = str;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String str) {
        this.pic = str;
    }

    public String getUrlType() {
        return this.urlType;
    }

    public void setUrlType(String str) {
        this.urlType = str;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
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
}
