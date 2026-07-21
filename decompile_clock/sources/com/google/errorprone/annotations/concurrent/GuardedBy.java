package com.google.errorprone.annotations.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: classes2.dex */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface GuardedBy {
    String value();
}
