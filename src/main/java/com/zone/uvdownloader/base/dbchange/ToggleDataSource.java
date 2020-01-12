package com.zone.uvdownloader.base.dbchange;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Owen Pan on 2017-06-16.
 * @author owen pan
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface ToggleDataSource {
    String value();
}