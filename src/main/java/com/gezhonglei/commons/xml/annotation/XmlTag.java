package com.gezhonglei.commons.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.FIELD})
public @interface XmlTag {
	String value() default "";
	String name() default "";
	boolean ignoreCase() default false;
	/**
	 * 如果注解的是List.class实现类，那么必须指定subClass
	 * @return
	 */
	Class<?> subClass() default EmptyClass.class;
	/**
	 * 非空时使用subName作为子元素的tag名称；空值时，使用类型上指定的XmlTag，如未指定XmlTag使用Class名称
	 * @return
	 */
	String subName() default "";
}
