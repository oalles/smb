package es.omarall.smb.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import es.omarall.smb.handler.MessageHandler;
import es.omarall.smb.test.events.MessageCreatedEvent;
import es.omarall.smb.test.events.PersistentApplicationEvent;

public class SampleMessageHandler implements MessageHandler {

	private static Logger LOG = LoggerFactory
			.getLogger(SampleMessageHandler.class);

	@Override
	public void handleMessage(Object o) {
		LOG.debug("Message consumed: o=[{}]", o.toString());
		Assert.isTrue(o instanceof PersistentApplicationEvent);
		PersistentApplicationEvent pae = (PersistentApplicationEvent) o;
		if (pae instanceof MessageCreatedEvent) {
			MessageCreatedEvent mce = (MessageCreatedEvent) pae;
			LOG.debug(
					"Source: {}, Date: {}, Message: {}", pae.getSource(),
					pae.getDate(), mce.getMessage());
		}
	}

}
