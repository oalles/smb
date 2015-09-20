package es.neivi.smb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Import(SMBConfiguration.class)
@Documented
public @interface EnableSMB {

	// If set, persistent tracking is enabled
	String consumerId() default "";
}
