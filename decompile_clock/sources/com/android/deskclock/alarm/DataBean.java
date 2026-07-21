package com.android.deskclock.alarm;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public class DataBean {
    private static AtomicInteger nextHashCode = new AtomicInteger();
    int color;
    int groupId;
    int id = nextHashCode();
    int repeatType;

    private static int nextHashCode() {
        return nextHashCode.getAndAdd(1);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.id == ((DataBean) obj).id;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.id));
    }
}
