package com.extole.common.rest.authorization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AccessTokenParam {

    boolean required() default false;

    boolean readCookie() default true;

    Scope requiredScope() default Scope.ANY;

}
