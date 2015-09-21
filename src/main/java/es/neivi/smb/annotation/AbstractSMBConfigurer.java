package es.neivi.smb.annotation;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;

import com.mongodb.MongoClient;

import es.neivi.smb.handler.MessageHandler;

/**
 * Base class for SMB configuration using JavaConfig.

 * @see SMBConfiguration
 * @see EnableSMB
 */
@Configuration
public abstract class AbstractSMBConfigurer {

	@Autowired
	private Environment env;

	/**
	 * The {@link Executor} instance to be used to be used to poll mongodb for
	 * documents from a tailable consumer
	 */
	public abstract Executor getExecutor();

	/**
	 * Remember, for most applications, you should have one MongoClient instance
	 * for the entire JVM.
	 */
	public abstract MongoClient getMongoClient();

	/**
	 * Message processor for the consume messages.
	 * 
	 * @return
	 */
	public abstract MessageHandler messageHandler();

	/**
	 * Default behavoir is to have a consumer_id property
	 * 
	 * @return
	 */
	public String getConsumerId() {
		return env.getRequiredProperty("smb.consumer_id");
	}

	/**
	 * The name of the mongo database where the messages are being published and
	 * consumed.
	 */
	public String getDatabaseName() {
		return env.getRequiredProperty("smb.database");
	}

	/**
	 * The name of the mongo collection where the messages are being published
	 * and consumed.
	 */
	public String getCollectionName() {
		return env.getRequiredProperty("smb.cappedcollection");
	}

	/**
	 * Wait 1 second before regenerating a cursor when last one was closed
	 */
	public long getCursorRegenerationDelay() {
		return 1L;
	}
}
