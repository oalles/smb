package es.neivi.smb.annotation;

import java.util.Arrays;

import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

import es.neivi.smb.handler.MessageHandler;

public class SampleSMBConfigurer implements SMBConfigurer {

	@Override
	public TaskExecutor getTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(40);
		executor.setKeepAliveSeconds(0);
		executor.setThreadNamePrefix("SMB-");
		executor.initialize();
		return executor;
	}

	@Override
	public MongoClient getMongoClient() {
		MongoClientOptions options = MongoClientOptions.builder()
				.readPreference(ReadPreference.primary())
				.writeConcern(WriteConcern.JOURNALED).connectionsPerHost(10)
				.build();
		return new MongoClient(Arrays.asList(new ServerAddress("localhost")),
				options);
	}

	/**
	 * Uses default NAME
	 */
	@Override
	public String getDatabaseName() {
		return null;
	}

	@Override
	public long getCursorRegenerationDelay() {
		return 0;
	}

	@Override
	public MessageHandler messageHandler() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Object o) {
				LoggerFactory.getLogger(this.getClass()).debug(
						"Message Received: {}", o.toString());
			}
		};
	}
}
