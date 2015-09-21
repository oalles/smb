package es.neivi.smb.test;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import es.neivi.smb.publisher.MessagePublisher;
import es.neivi.smb.test.events.MessageCreatedEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfiguration.class }, loader = AnnotationConfigContextLoader.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SMBTests extends AbstractJUnit4SpringContextTests {

	private static Logger LOG = LoggerFactory.getLogger(SMBTests.class);

	@Autowired
	private MessagePublisher publisher;

	@Before
	public void beforeTest() throws Exception {

	}

	@After
	public void afterTest() {
	}

	@Test
	public void publishAndConsumeSameClient() throws Exception {
		LOG.debug("We are publishing ...");
		publisher.publish(new MessageCreatedEvent("ctx1", "Hola CTX2"));
		LOG.debug("Published");

		// WAIT for completion
		try {

			TimeUnit.SECONDS.sleep(60);
		} catch (InterruptedException e) {
			LOG.error("Thread was interrupted", e);
			Thread.currentThread().interrupt();
		}

	}
}