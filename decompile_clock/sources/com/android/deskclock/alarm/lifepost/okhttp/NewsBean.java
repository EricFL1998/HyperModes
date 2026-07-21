package com.android.deskclock.alarm.lifepost.okhttp;

import java.io.Serializable;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class NewsBean implements Serializable {
    private ArrayList<Candidate> candidates;
    private long createTime;
    private String expireMinutes;
    private boolean isShow;

    public boolean isShow() {
        return this.isShow;
    }

    public void setShow(boolean z) {
        this.isShow = z;
    }

    public String getExpireMinutes() {
        return this.expireMinutes;
    }

    public void setExpireMinutes(String str) {
        this.expireMinutes = str;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long j) {
        this.createTime = j;
    }

    public ArrayList<Candidate> getCandidates() {
        return this.candidates;
    }

    public void setCandidates(ArrayList<Candidate> arrayList) {
        this.candidates = arrayList;
    }

    public class Candidate {
        private String desc;
        private ArrayList<String> imgs;
        private LinkBean link;
        private String title;

        public Candidate() {
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

        public ArrayList<String> getImgs() {
            return this.imgs;
        }

        public void setImgs(ArrayList<String> arrayList) {
            this.imgs = arrayList;
        }

        public LinkBean getLink() {
            return this.link;
        }

        public void setLink(LinkBean linkBean) {
            this.link = linkBean;
        }
    }
}
