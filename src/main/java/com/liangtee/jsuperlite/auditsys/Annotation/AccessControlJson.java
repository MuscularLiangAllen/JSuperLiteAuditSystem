package com.liangtee.jsuperlite.auditsys.Annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessControlJson {

	int accessLevel();
	
}
