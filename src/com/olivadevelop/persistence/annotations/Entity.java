package com.olivadevelop.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 26/02/2018.
 */

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    String table() default "";
}
