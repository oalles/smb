package es.neivi.smb.publisher.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mongodb.core.MongoTemplate;

import es.neivi.smb.annotation.RootMessageEntity;
import es.neivi.smb.publisher.MessagePublisher;

public class MessagePublisherImpl implements MessagePublisher {

	private static Logger LOG = LoggerFactory
			.getLogger(MessagePublisherImpl.class);

	@Autowired
	@Qualifier("mbMongoTemplate")
	private transient MongoTemplate mongoTemplate;

	public void publish(Object message) {

		try {

			// type to be broadcasted?
			RootMessageEntity entity = AnnotationUtils.findAnnotation(
					message.getClass(), RootMessageEntity.class);

			if (entity != null) // Yes -> Broadcast usint a Tailable collection
				mongoTemplate.insert(message);
			else
				// No -> Do nothing but notify ...
				LOG.warn("This type: {} is not intended to be broadcasted", message.getClass());

		} catch (RuntimeException c) {
			LOG.error("EXCP: {}", c);
		}
	}
}
