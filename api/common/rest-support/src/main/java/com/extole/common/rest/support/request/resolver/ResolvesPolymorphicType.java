package com.extole.common.rest.support.request.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@NameBinding
public @interface ResolvesPolymorphicType {

    Class<? extends PolymorphicRequestTypeResolver> resolver() default PolymorphicRequestTypeResolver.class;
}
