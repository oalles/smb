package es.neivi.smb.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import es.neivi.smb.handler.MessageHandler;
import es.neivi.smb.test.events.MessageCreatedEvent;
import es.neivi.smb.test.events.PersistentApplicationEvent;

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
