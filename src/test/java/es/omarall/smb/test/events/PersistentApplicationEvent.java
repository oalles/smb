package es.omarall.smb.test.events;

import java.util.Date;

import es.omarall.smb.annotation.RootMessageEntity;

//@Document(collection = "#{environment.getRequiredProperty('collectionname')}")
//@Document(collection = "abc")
@RootMessageEntity
public class PersistentApplicationEvent {

	private final String source;
	private final Date date = new Date();

	public PersistentApplicationEvent(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public Date getDate() {
		return date;
	}

}
