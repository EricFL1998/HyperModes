package com.android.deskclock.alarm.lifepost.okhttp;

import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class LinkBean implements Serializable {
    private String marketUrl;
    private String pkgName;
    private String url;
    private String urlApp;
    private String urlType;

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

    public String getMarketUrl() {
        return this.marketUrl;
    }

    public void setMarketUrl(String str) {
        this.marketUrl = str;
    }
}
