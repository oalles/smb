package es.neivi.smb.test;

import java.util.Arrays;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

import es.neivi.smb.annotation.AbstractSMBConfigurer;
import es.neivi.smb.annotation.EnableSMB;
import es.neivi.smb.handler.MessageHandler;

@Configuration
@ComponentScan
@EnableSMB
@PropertySource("classpath:/META-INF/application.properties")
public class AppConfiguration extends AbstractSMBConfigurer {

	@Override
	public TaskExecutor getExecutor() {

		// TaskExecutor executor = new SyncTaskExecutor();

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(40);
		executor.setKeepAliveSeconds(0);
		executor.setThreadNamePrefix("SMB-");
		executor.initialize();
		
		executor.setAwaitTerminationSeconds(60);
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

	@Override
	public MessageHandler messageHandler() {
		return new SampleMessageHandler();
	}

}