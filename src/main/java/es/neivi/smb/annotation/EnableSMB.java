package es.neivi.smb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Async;

/**
 * Enables Simple Mongo Broadcaster capability, to be used on @
 * {@link Configuration} classes as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableSMB(consumerId=#{env.getRequiredProperty("smb.consumer_id")})
 * public class AppConfig {
 * 	&#064;Bean
 * 	public MyAsyncBean asyncBean() {
 * 		return new MyAsyncBean();
 * 	}
 * }
 * </pre>
 *
 * where {@code MyAsyncBean} is a user-defined type with one or methods
 * annotated with @{@link Async} (or any custom annotation specified by the
 * {@link #annotation()} attribute).
 *
 * @see RootMessageEntity
 * @see SMBConfigurer
 * @see SMBConfiguration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Import(SMBConfiguration.class)
@Documented
public @interface EnableSMB {
}
