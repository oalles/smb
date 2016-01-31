package es.neivi.smb.publisher.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mongodb.core.MongoTemplate;

import es.neivi.smb.annotation.RootMessageEntity;
import es.neivi.smb.exceptions.InvalidPayloadException;
import es.neivi.smb.publisher.MessagePublisher;

public class MessagePublisherImpl implements MessagePublisher {

	private static Logger LOG = LoggerFactory.getLogger(MessagePublisherImpl.class);

	@Autowired
	@Qualifier("mbMongoTemplate")
	private transient MongoTemplate mongoTemplate;

	public void publish(Object message) {

		try {

			// type to be broadcasted?
			RootMessageEntity entity = AnnotationUtils.findAnnotation(message.getClass(), RootMessageEntity.class);

			if (entity == null) // Not to be broadcasted
				throw new RuntimeException("Not to be broadcasted");
			mongoTemplate.insert(message);

		} catch (RuntimeException c) {
			throw new InvalidPayloadException();
		}
	}
}
