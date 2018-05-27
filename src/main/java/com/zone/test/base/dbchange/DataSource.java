package com.zone.test.base.dbchange;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Owen Pan on 2017-06-16.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface DataSource {
    String value();
}