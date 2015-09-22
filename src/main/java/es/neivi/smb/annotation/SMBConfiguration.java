package es.neivi.smb.annotation;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import es.neivi.mtc.DocumentHandler;
import es.neivi.mtc.TailingTask;
import es.neivi.mtc.configuration.MTCConfiguration;
import es.neivi.mtc.configuration.MTCPersistentTrackingConfiguration;
import es.neivi.smb.handler.MessageHandler;
import es.neivi.smb.handler.SMBDocumentHandler;
import es.neivi.smb.publisher.MessagePublisher;
import es.neivi.smb.publisher.impl.MessagePublisherImpl;

@Configuration("smbConfig")
public class SMBConfiguration extends AbstractMongoConfiguration implements
		ImportAware {

	private AnnotationAttributes enableSMB;

	// Annotation atts
	// private String consumerId;

	// Configurer properties
	private TaskExecutor smbTaskExecutor;
	private MongoClient smbMongoClient;
	private MessageHandler messageHandler;
	private String consumerId;
	private String collectionname;
	private String databaseName;
	private long cursorRegenerationDelay = 0;

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		this.enableSMB = AnnotationAttributes.fromMap(importMetadata
				.getAnnotationAttributes(EnableSMB.class.getName()));

		//
		// this.enableSMB = AnnotationAttributes.fromMap(importMetadata
		// .getAnnotationAttributes(EnableSMB.class.getName(), false));
		// EnableSMB is present
		if (this.enableSMB == null) {
			throw new IllegalArgumentException(
					"@EnableSMB is not present on importing class "
							+ importMetadata.getClassName());
		}

		// Get annotation atts
		// String cId = enableSMB.getString("consumerId");
		// this.consumerId = StringUtils.hasText(cId) ? cId : null;
	}

	/**
	 * Collect any {@link AbstractSMBConfigurer} beans through autowiring.
	 */
	@Autowired(required = false)
	void setConfigurers(Collection<AbstractSMBConfigurer> configurers)
			throws Exception {

		if (configurers.size() != 1) {
			throw new IllegalStateException(
					"Only one AbstractSMBConfigurer may exist");
		}
		// configurer.size() == 1
		AbstractSMBConfigurer configurer = configurers.iterator().next();

		this.smbMongoClient = configurer.getMongoClient();
		this.smbTaskExecutor = configurer.getExecutor();
		this.databaseName = configurer.getDatabaseName();
		this.collectionname = configurer.getCollectionName();
		this.cursorRegenerationDelay = configurer.getCursorRegenerationDelay();
		this.messageHandler = configurer.messageHandler();
		this.consumerId = configurer.getConsumerId();
	}

	@Bean
	public RootMessageEntityDescriptor rootMessageEntityDescriptor()
			throws Exception {
		RootMessageEntityDescriptor rmed = new RootMessageEntityDescriptor();
		return rmed;
	}

	@Bean(name = "mbMongoTemplate")
	public MongoTemplate mongoTemplate() throws Exception {
		return super.mongoTemplate();
	}

	@Bean(name = "mbDbFactory")
	@Override
	public MongoDbFactory mongoDbFactory() throws Exception {
		return super.mongoDbFactory();
	}

	@Bean(name = "mbMongoConverter")
	public MappingMongoConverter mappingMongoConverter() throws Exception {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
		MappingMongoConverter converter = new MappingMongoConverter(
				dbRefResolver, mongoMappingContext());
		converter.setCustomConversions(customConversions());

		return converter;
	}

	@Override
	protected String getDatabaseName() {
		return this.databaseName;
	}

	@Override
	public Mongo mongo() throws Exception {
		return this.smbMongoClient;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		return this.smbTaskExecutor;
	}

	// BEANS
	@Bean
	public MessagePublisher messagePublisher() {
		return new MessagePublisherImpl();
	}

	@Bean
	public MessageHandler messageHandler() {
		return this.messageHandler;
	}

	@Bean
	public DocumentHandler smbDocumentHandler() {
		return new SMBDocumentHandler();
	}

	@Bean
	public TailingTask tailingTask() {

		MTCConfiguration configuration = new MTCConfiguration();
		configuration.setDatabase(this.databaseName);
		configuration.setMongoClient(this.smbMongoClient);
		configuration.setCollection(this.getCollectionname());
		if (this.consumerId != null) {
			// Persistent Tracking ENABLED
			MTCPersistentTrackingConfiguration ptc = new MTCPersistentTrackingConfiguration();
			ptc.setConsumerId(this.consumerId);
			ptc.setCursorRegenerationDelay(this.cursorRegenerationDelay);
			configuration.setPersistentTrackingConfiguration(ptc);
		}

		TailingTask tailingTask = new TailingTask(configuration);
		tailingTask.setDocumentHandler(smbDocumentHandler());
		tailingTask.start();
		this.smbTaskExecutor.execute(tailingTask);
		return tailingTask;
	}

	public String getCollectionname() {
		return collectionname;
	}
}
