package com.google.errorprone.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: classes2.dex */
@Target({ElementType.ANNOTATION_TYPE})
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface RequiredModifiers {
    Modifier[] modifier() default {};

    @Deprecated
    javax.lang.model.element.Modifier[] value() default {};
}
