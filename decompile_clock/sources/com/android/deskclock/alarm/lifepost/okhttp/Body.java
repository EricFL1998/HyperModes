package com.android.deskclock.alarm.lifepost.okhttp;

import java.io.Serializable;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Body<T> implements Serializable {
    private Body<T>.Data<T> data;
    private ArrayList<Body<T>.Data<T>> datas;
    private String msg;
    private int status;

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String str) {
        this.msg = str;
    }

    public ArrayList<Body<T>.Data<T>> getDatas() {
        return this.datas;
    }

    public void setDatas(ArrayList<Body<T>.Data<T>> arrayList) {
        this.datas = arrayList;
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data<T> {
        private T data;
        private String itemId;
        private String itemType;
        private String template;

        public Data() {
        }

        public String getItemId() {
            return this.itemId;
        }

        public void setItemId(String str) {
            this.itemId = str;
        }

        public String getItemType() {
            return this.itemType;
        }

        public void setItemType(String str) {
            this.itemType = str;
        }

        public String getTemplate() {
            return this.template;
        }

        public void setTemplate(String str) {
            this.template = str;
        }

        public T getData() {
            return this.data;
        }

        public void setData(T t) {
            this.data = t;
        }
    }
}
