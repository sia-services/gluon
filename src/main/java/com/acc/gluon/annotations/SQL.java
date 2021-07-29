package com.acc.gluon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface SQL {
    String value();

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Prepare {}
}
