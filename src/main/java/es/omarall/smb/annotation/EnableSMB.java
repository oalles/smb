package es.omarall.smb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Enables Simple Mongo Broadcaster capability, to be used on @
 * {@link Configuration} classes as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableSMB
 * public class AppConfig {
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Import(SMBConfiguration.class)
@Documented
public @interface EnableSMB {

	/**
	 * Alias for the {@link #mappingBasePackageClass()} attribute.
	 */
	String value() default "";

	/**
	 * Base package to scan for annotated components.
	 * <p>
	 */
	String mappingBasePackage() default "";
}
