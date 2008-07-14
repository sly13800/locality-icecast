package se.slackers.locality.media.queue;

import se.slackers.locality.media.reader.MediaReaderFactory;
import se.slackers.locality.model.Metadata;
import se.slackers.locality.shout.ClientListener;

/**
 * 
 * @author bysse
 *
 */
public interface MediaQueueProcessor extends Runnable, ClientListener {
	public void init();
	public void deinit();
	
	/**
	 * Stop the processor and wait until it really is stopped.
	 */
	public void stopProcessing();
	
	public void setMediaQueue(MediaQueue mediaQueue);
	public MediaQueue getMediaQueue();
	
	public void setMediaReaderFactory(MediaReaderFactory mediaReaderFactory);
	public MediaReaderFactory getMediaReaderFactory();	
		
	public void addMediaQueueProcessorListener(MediaQueueProcessorListener listener);
	public void removeMediaQueueProcessorListener(MediaQueueProcessorListener listener);
	
	public Metadata getCurrentMetadata();	
}
