package es.neivi.smb.publisher.impl;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

import es.neivi.smb.annotation.RootMessageEntityDescriptor;
import es.neivi.smb.publisher.MessagePublisher;

public class MessagePublisherImpl implements MessagePublisher {

	@Autowired
	@Qualifier("mbMongoTemplate")
	private transient MongoTemplate mongoTemplate;

	private transient Class<?> rootMessageEntityType;

	public void publish(Object message) {

		try {

			if (rootMessageEntityType.isAssignableFrom(message.getClass()))
				mongoTemplate.insert(message);
		} catch (RuntimeException c) {
			LoggerFactory.getLogger(this.getClass()).error("EXCP: {}",
					c.toString());
		}
	}

	@Autowired
	public void setRootMessageEntityDescriptor(
			RootMessageEntityDescriptor rootMessageEntityType) {
		this.rootMessageEntityType = rootMessageEntityType
				.getRootMessageEntityType();
	}
}
