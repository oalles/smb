package es.neivi.smb.annotation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import es.neivi.mtc.DocumentHandler;
import es.neivi.mtc.TailingTask;
import es.neivi.mtc.configuration.MTCConfiguration;
import es.neivi.mtc.configuration.MTCPersistentTrackingConfiguration;
import es.neivi.smb.exceptions.RootMessageEntityRequired;
import es.neivi.smb.exceptions.UniqueRootMessageEntityRequired;
import es.neivi.smb.handler.MessageHandler;
import es.neivi.smb.handler.SMBDocumentHandler;
import es.neivi.smb.publisher.MessagePublisher;
import es.neivi.smb.publisher.impl.MessagePublisherImpl;

/**
 * instantiates, configures and initializes all objects required to have a
 * working simple message broadcaster.
 * 
 * 
 */
@Configuration("smbConfig")
public class SMBConfiguration extends AbstractMongoConfiguration implements
		ImportAware {

	private Logger LOG = LoggerFactory.getLogger(SMBConfiguration.class);

	private AnnotationAttributes enableSMB;
	private String mappingBasePackage;

	// Configurer properties
	private TaskExecutor smbTaskExecutor;
	private MongoClient smbMongoClient;
	private MongoMappingContext smbMongoMappingContext;
	private MessageHandler messageHandler;
	private String consumerId;
	private String collectionname;
	private String databaseName;
	private long cursorRegenerationDelay = 0;

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		this.enableSMB = AnnotationAttributes.fromMap(importMetadata
				.getAnnotationAttributes(EnableSMB.class.getName()));

		if (this.enableSMB == null) {
			throw new IllegalArgumentException(
					"@EnableSMB is not present on importing class: "
							+ importMetadata.getClassName());
		}

		// Base package?
		mappingBasePackage = enableSMB.getString("value");
		if (StringUtils.isEmpty(mappingBasePackage))
			mappingBasePackage = enableSMB.getString("mappingBasePackage");
		if (StringUtils.isEmpty(mappingBasePackage))
			mappingBasePackage = "/*";
	}

	/**
	 * Collect any {@link AbstractSMBConfigurer} beans through autowiring.
	 */
	@Autowired(required = true)
	void setConfigurers(Collection<AbstractSMBConfigurer> configurers)
			throws Exception {

		if (configurers.size() != 1) {
			throw new IllegalStateException(
					"One and only one AbstractSMBConfigurer may exist. You have to provide a concrete implementation. You could Make your ");
		}
		// configurer.size() == 1
		AbstractSMBConfigurer configurer = configurers.iterator().next();

		this.smbMongoClient = configurer.getMongoClient();
		if (smbMongoClient == null) {
			throw new IllegalStateException(
					"You have provided a concrete inmplementation for AbstractSMBConfigurer. But the MongoClient instance you provided is null ");
		}
		this.smbTaskExecutor = configurer.getExecutor();
		if (smbTaskExecutor == null) {
			throw new IllegalStateException(
					"You have provided a concrete inmplementation for AbstractSMBConfigurer. But the TaskExecutor instance you provided is null ");
		}
		this.messageHandler = configurer.messageHandler();
		if (messageHandler == null) {
			throw new IllegalStateException(
					"You have provided a concrete inmplementation for AbstractSMBConfigurer. But the MessageHandler instance you provided is null ");
		}

		// Properties required
		this.databaseName = configurer.getDatabaseName();
		this.collectionname = configurer.getCollectionName();
		this.consumerId = configurer.getConsumerId();
		this.cursorRegenerationDelay = configurer.getCursorRegenerationDelay();
	}

	@Bean
	@Description("The document processor when the document is read from a mongo tailable collection is going to call a read on a mongoconverter instance, so the document is converted to a given type")
	public DocumentHandler smbDocumentHandler() {
		return new SMBDocumentHandler(this.getRootEntityType());
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

	@Bean(name = "mbMongoMappingContext")
	public MongoMappingContext mongoMappingContext()
			throws ClassNotFoundException {

		if (smbMongoMappingContext == null) {
			smbMongoMappingContext = super.mongoMappingContext();
		}

		return smbMongoMappingContext;
	}

	@Bean(name = "mbMongoConverter")
	public MappingMongoConverter mappingMongoConverter() throws Exception {

		// DbRefResolver dbRefResolver = new
		// DefaultDbRefResolver(mongoDbFactory());
		// MappingMongoConverter converter = new MappingMongoConverter(
		// dbRefResolver, mongoMappingContext());
		// converter.setCustomConversions(customConversions());
		//
		// return converter;

		return super.mappingMongoConverter();
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

	protected String getMappingBasePackage() {
		return mappingBasePackage;
	}

	/**
	 * Scans the mapping base package for classes annotated with
	 * {@link RootMessageEntity}.
	 * 
	 * @see #getMappingBasePackage()
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {

		String basePackage = getMappingBasePackage();
		Set<Class<?>> initialEntitySet = new HashSet<Class<?>>();

		if (StringUtils.hasText(basePackage)) {
			ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
					false);
			componentProvider.addIncludeFilter(new AnnotationTypeFilter(
					RootMessageEntity.class));

			for (BeanDefinition candidate : componentProvider
					.findCandidateComponents(basePackage)) {
				initialEntitySet.add(ClassUtils.forName(
						candidate.getBeanClassName(),
						SMBConfiguration.class.getClassLoader()));
			}
		}

		// TO be stored in MongoMappingContext ...
		return initialEntitySet;
	}

	public Class<?> getRootEntityType() {

		Collection<BasicMongoPersistentEntity<?>> pes = null;

		try {

			pes = mongoMappingContext().getPersistentEntities();

		} catch (ClassNotFoundException e) {
			throw new RootMessageEntityRequired();
		}

		int size = pes.size();

		if (size == 0)
			throw new RootMessageEntityRequired();
		else if (size > 1) {
			throw new UniqueRootMessageEntityRequired();
		}

		// size == 1
		BasicMongoPersistentEntity<?> pe = pes.iterator().next();

		return pe.getType();
	}
}
