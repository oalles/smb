package es.omarall.smb.handler;

/**
 * What to do when the domain entity is read from Mongodb
 * 
 * @author Omar
 *
 */
public interface MessageHandler {
	/**
	 * Entity processor sets what to do once the entity is read from a mongodb
	 * tailable collection.
	 * 
	 * @param o
	 */
	public void handleMessage(Object message);
}
