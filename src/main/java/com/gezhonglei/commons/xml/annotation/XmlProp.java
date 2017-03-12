package com.gezhonglei.commons.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface XmlProp {
	String value() default "";
	String name() default "";
	boolean ignoreCase() default false;
}
