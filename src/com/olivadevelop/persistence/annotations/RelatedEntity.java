package com.olivadevelop.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright OlivaDevelop 2014-2018
 * Created by Oliva on 03/02/2018.
 * RolerMaster
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RelatedEntity {

    String joinColumn();

    boolean preference() default false;
}
