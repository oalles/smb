package es.neivi.smb.handler;


public abstract class AbstractMessageHandler implements MessageHandler {

	@Override
	public abstract void handleMessage(Object o);

}
