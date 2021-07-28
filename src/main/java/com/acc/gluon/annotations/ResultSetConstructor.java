package com.acc.gluon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates constructor for ResultSet
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ResultSetConstructor {

    /**
     * If present, do not include this field in the ResultSet extractor,
     * but provide this field in constructor parameter as is
     */
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Provided {}

    /**
     * Join Entities (with recursive generation of rs constructor).
     * Valid only for Set<Joined> or List<Joined>
     */
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Join {}
}
