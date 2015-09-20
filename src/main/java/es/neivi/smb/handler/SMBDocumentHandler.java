package es.neivi.smb.handler;

import org.bson.Document;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import es.neivi.mtc.DocumentHandler;
import es.neivi.smb.annotation.RootMessageEntityDescriptor;

public class SMBDocumentHandler implements DocumentHandler {

	@Autowired
	@Qualifier("mbMongoConverter")
	private MongoConverter mongoConverter;

	private Class<?> rootMessageEntityType;

	@Autowired
	private MessageHandler messageHandler;

	@Override
	public void handleDocument(Document document) {

		// Convert to DBObject
		DBObject dbo = (DBObject) JSON.parse(document.toJson());

		// this is why we need RootEventEntity
		try {
			Object message = mongoConverter.read(rootMessageEntityType, dbo);
			messageHandler.handleMessage(message);
		} catch (RuntimeException e) {
			// Object cannot be converted to a domain model instance.
			LoggerFactory.getLogger(this.getClass()).error(e.toString());
			e.printStackTrace();
		}

	}

	@Autowired
	public void setRootMessageEntityDescriptor(
			RootMessageEntityDescriptor rootMessageEntityDescriptor) {
		this.rootMessageEntityType = rootMessageEntityDescriptor
				.getRootMessageEntityType();
	}

}
