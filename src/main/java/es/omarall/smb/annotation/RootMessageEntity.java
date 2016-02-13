package es.omarall.smb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Identifies a domain object as a type to be broadcasted.
 */
@Document(collection = "#{smbConfig.collectionname}")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface RootMessageEntity {
}
