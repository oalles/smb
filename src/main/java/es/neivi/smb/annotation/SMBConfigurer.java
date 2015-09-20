package es.neivi.smb.annotation;

import org.springframework.core.task.TaskExecutor;

import com.mongodb.MongoClient;

import es.neivi.smb.handler.MessageHandler;

public interface SMBConfigurer {

	public TaskExecutor getTaskExecutor();

	public MongoClient getMongoClient() throws Exception;

	public String getDatabaseName();

	// public String getCollectionName();

	public MessageHandler messageHandler();

	/**
	 * Persistent tracking will be automatically activated if a consumerId is
	 * provided.
	 * 
	 * @return
	 */
	public long getCursorRegenerationDelay();
}
