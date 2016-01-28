package es.neivi.smb.annotation;

import java.util.concurrent.Executor;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import com.mongodb.MongoClient;

import es.neivi.smb.handler.MessageHandler;

/**
 * IT is notified from the environment, properties and instances needed to provide all
 * SMBConfiguration objects.
 * 
 * @see SMBConfiguration
 * @see EnableSMB
 */
// @Configuration
public abstract class AbstractSMBConfigurer implements EnvironmentAware {

	protected Environment env;

	/**
	 * The {@link Executor} instance to be used to be used to poll mongodb for
	 * documents from a tailable consumer
	 */
	public abstract TaskExecutor getExecutor();

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
