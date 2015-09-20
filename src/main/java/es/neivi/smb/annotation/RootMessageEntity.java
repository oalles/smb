package es.neivi.smb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Identifies a domain object as root message class.
 * 
 * @see RootMessageEntityDescriptor
 */
@Document(collection = "#{environment.getRequiredProperty('collectionname')}")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Import(SMBConfiguration.class)
@Documented
public @interface RootMessageEntity {
}
