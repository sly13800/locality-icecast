package se.slackers.locality.media.queue;

import java.io.IOException;

import org.apache.log4j.Logger;

import se.slackers.locality.data.FrameStorage;
import se.slackers.locality.data.FrameStorageEntry;
import se.slackers.locality.exception.EncapsuledExceptionRuntimException;
import se.slackers.locality.exception.FrameStorageIsEmptyException;
import se.slackers.locality.media.Frame;
import se.slackers.locality.media.reader.MediaReader;

public class PreloadDataMediaQueueProcessor extends AbstractMediaQueueProcessor {
	private static final Logger log = Logger.getLogger(PreloadDataMediaQueueProcessor.class);
	
	private long maximumPreload = 5000;
	private long maximumHistory = 5000;
	
	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			throw new EncapsuledExceptionRuntimException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void readData(MediaReader reader, Frame frame) {
		FrameStorage storage = getMediaQueue().getFrameStorage();
		
		long currentTime = System.currentTimeMillis();
		long lastFrameTime = currentTime;
		
		// first purge the history
		storage.purgeUntil(currentTime - maximumHistory);
		
		try {
			lastFrameTime = storage.getLastFrameTime();
			
			// Make sure we don't use old time stamps when we store frames
			if (lastFrameTime < currentTime) {
				lastFrameTime = currentTime;
				log.warn("FrameReader is lagging");
			}
			
		} catch (FrameStorageIsEmptyException e) {
			// do nothing
		}
		
		if (lastFrameTime > currentTime + maximumPreload) {
			// the maximum data preload have been reached, stall the thread for a while.	
			sleep(250);			
			return;
		}
		
		//.. read more data from the media
		try {
			reader.readFrame(frame);
			storage.add( new FrameStorageEntry(lastFrameTime, new Frame(frame) ) );			
		} catch (IOException e) {
			throw new EncapsuledExceptionRuntimException(e);
		}		
	}
}
