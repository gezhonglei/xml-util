package com.gezhonglei.commons.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.FIELD})
//@XmlTag
public @interface XmlMap {
	String name() default "";
	boolean ignoreCase() default false;
	Class<?> keyType() default String.class;
	Class<?> valueType() default String.class;
	
	/**
	 * item标签可见
	 * 
	 * <p>
	 * 		true时, itemTag必须非空，默认值"item"；<br/>
	 * 		false时，key或value必有一个是Tag
	 * </p>
	 * @return
	 */
	boolean isItemVisible() default true;
	/**
	 * item标签名	
	 * <p>仅在isItemVisible=true时有效</p>
	 * @return
	 */
	String itemTag() default "item";
	/**
	 * key以标签显示
	 * <p>
	 * 		<b>仅当keyName非空时有效。</b>
	 * 		true时，keyName表示key标签名；<br/>
	 * 		false时，keyName表示显示key属性名。
	 * </p>
	 * @return
	 */
	boolean isKeyTag() default false;
	/**
	 * keyName空值时，表示以Content展示；否则根据isKeyTag取值以Tag或Attribute显示。
	 * @return
	 */
	String keyName() default "key";
	/**
	 * value以标签显示	
	 * <p>
	 * 		<b>仅当valueName非空时有效。</b>
	 * 		true时，valueName表示value标签名；<br/>
	 * 		false时，valueName表示显示value属性名。
	 * </p>
	 * @return
	 */
	boolean isValueTag() default false;
	/**
	 * valueName空值时，表示以Content展示；否则根据isValueTag取值以Tag或Attribute显示。
	 * @return
	 */
	String valueName();
}
