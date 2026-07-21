package com.android.deskclock.alarm.lifepost.okhttp;

import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class GalleryBean implements Serializable {
    private long date;
    private String image;
    private TextInfo textInfo;

    public long getDate() {
        return this.date;
    }

    public void setDate(long j) {
        this.date = j;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String str) {
        this.image = str;
    }

    public TextInfo getTextInfo() {
        return this.textInfo;
    }

    public void setTextInfo(TextInfo textInfo) {
        this.textInfo = textInfo;
    }

    public class TextInfo {
        private String color;
        private LinkBean link;
        private String text;

        public TextInfo() {
        }

        public String getText() {
            return this.text;
        }

        public void setText(String str) {
            this.text = str;
        }

        public String getColor() {
            return this.color;
        }

        public void setColor(String str) {
            this.color = str;
        }

        public LinkBean getLink() {
            return this.link;
        }

        public void setLink(LinkBean linkBean) {
            this.link = linkBean;
        }
    }
}
