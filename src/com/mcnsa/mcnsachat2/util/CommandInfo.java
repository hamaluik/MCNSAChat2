package com.mcnsa.mcnsachat2.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
	public String alias();
	public String permission() default "";
	public String usage() default "";
	public String description() default "";
	public boolean visible() default true;
}
